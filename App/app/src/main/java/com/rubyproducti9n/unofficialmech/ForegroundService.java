package com.rubyproducti9n.unofficialmech;

import static androidx.activity.result.ActivityResultCallerKt.registerForActivityResult;
import static androidx.core.app.ActivityCompat.requestPermissions;

import static com.rubyproducti9n.unofficialmech.ProjectToolkit.context;
import static com.rubyproducti9n.unofficialmech.ProjectToolkit.register;
import static com.rubyproducti9n.unofficialmech.ProjectToolkit.unregister;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.preference.PreferenceManager;
import androidx.work.NetworkType;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.rubyproducti9n.smartmech.AlgorithmEngine;

import java.io.File;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class ForegroundService extends Service {

    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "UploadChannel";
    private Set<String> processedChildKeys = new HashSet<>();
    private static final long REPEAT_INTERVAL = 5 * 60 * 1000;
    private Handler handler = new Handler();
    private long[] vibrate = {200, 500, 200};

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        new Thread(() -> {
            while (true) {
                Log.e("Service", "Service is running");
                if (!isNotificationActive(ForegroundService.this)) {
                    startForegroundService();
                }
                try {
                    Thread.sleep(60000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();

        Intent restartIntent = new Intent(this, MainActivity.class);
        restartIntent.setAction(Intent.ACTION_MAIN);
        restartIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, restartIntent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Keeping you updated!")
                .setContentText("We're working to ensure you never miss a beat. Everything's running smoothly!")
                .setSmallIcon(R.drawable.ic_unofficial)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setFullScreenIntent(pendingIntent, false)
                .setContentIntent(pendingIntent)
                .setOngoing(true);

        startForeground(NOTIFICATION_ID, builder.build());

        return START_STICKY;
    }

    private void startForegroundService() {
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Service", NotificationManager.IMPORTANCE_DEFAULT);
        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Keeping You Updated")
                .setSmallIcon(R.drawable.ic_unofficial)
                .build();

        startForeground(NOTIFICATION_ID, notification);
    }

    private boolean isNotificationActive(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        StatusBarNotification[] notifications = notificationManager.getActiveNotifications();
        for (StatusBarNotification notification : notifications) {
            if (notification.getId() == NOTIFICATION_ID) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        startService(new Intent(this, ForegroundService.class));
    }

    @Override
    public void onCreate() {
        super.onCreate();
        startForegroundService();
        createNotificationChannel();
        getNotificationValue();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        stopForeground(true);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Post Upload Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    private void getNotificationValue() {
        DatabaseReference notificationRef = FirebaseDatabase.getInstance().getReference("app-configuration/notifications");
        notificationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean value = snapshot.getValue(Boolean.class);
                if (Boolean.TRUE.equals(value)) {
//                    getNewPost();
//                    getNewNotice();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("FirebaseRequestDeclined", "Request was declined");
            }
        });
    }

    private void getNewNotice() {
        processedChildKeys = getProcessedChildKeys();

        FirebaseApp.initializeApp(this);
        DatabaseReference postReference = FirebaseDatabase.getInstance().getReference("notice");
        postReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    String childKey = childSnapshot.getKey();
                    String username = childSnapshot.child("authName").getValue(String.class);
                    Boolean important = childSnapshot.child("impNotice").getValue(Boolean.class);
                    String uploadTime = childSnapshot.child("uploadTime").getValue(String.class);
                    int status = DayAgoSystem.getThreshold(uploadTime);
                    if (status == 1) {
                        assert username != null;
                        if (!username.equals("Unofficial Mech")) {
                            if (!processedChildKeys.contains(childKey)) {
                                processedChildKeys.add(childKey);
                                saveProcessedChildKey(processedChildKeys);
                                if (important != null && important) {
                                    triggerImportantNotification();
                                } else {
                                    triggerNotification();
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("FirebaseRequestDeclined", "Request was declined");
            }
        });
    }

    private void getNewPost() {
        processedChildKeys = getProcessedChildKeys();

        FirebaseApp.initializeApp(this);
        DatabaseReference postReference = FirebaseDatabase.getInstance().getReference("posts");
        postReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    String childKey = childSnapshot.getKey();
                    String username = childSnapshot.child("userName").getValue(String.class);
                    String uploadTime = childSnapshot.child("uploadTime").getValue(String.class);
                    int status = DayAgoSystem.getThreshold(uploadTime);
                    if (status == 1) {
                        assert username != null;
                        if (!username.equals("Unofficial Mech")) {
                            if (!processedChildKeys.contains(childKey)) {
                                processedChildKeys.add(childKey);
                                saveProcessedChildKey(processedChildKeys);
                                triggerPostNotification(username);
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("FirebaseRequestDeclined", "Request was declined");
            }
        });
    }

    private void triggerNotification() {
        Intent intent = new Intent(ForegroundService.this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(ForegroundService.this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(ForegroundService.this, "new_notice_channel")
                .setSmallIcon(R.drawable.ic_unofficial)
                .setContentTitle("📋New notice!")
                .setVibrate(vibrate)
                .setContentText("A new notice was published on the notice board, tap to view")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManagerCompat.notify(2, builder.build());
    }

    private void triggerImportantNotification() {
        Intent intent = new Intent(ForegroundService.this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(ForegroundService.this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(ForegroundService.this, "new_notice_channel")
                .setSmallIcon(R.drawable.ic_unofficial)
                .setContentTitle("📢Important notice! Tap for details")
                .setContentText("📌An important notice was published on the notice board, tap to view")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setVibrate(vibrate)
                .setFullScreenIntent(pendingIntent, true)
                .setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManagerCompat.notify(3, builder.build());
    }

    private void triggerPostNotification(String username) {
        Intent intent = new Intent(ForegroundService.this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(ForegroundService.this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(ForegroundService.this, "new_post_channel")
                .setSmallIcon(R.drawable.ic_unofficial)
                .setContentTitle("📅New post!")
                .setContentText(username + " just posted")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_SOCIAL)
                .setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManagerCompat.notify(4, builder.build());
    }

    private Set<String> getProcessedChildKeys() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ForegroundService.this);
        return preferences.getStringSet("keys", new HashSet<>());
    }

    private void saveProcessedChildKey(Set<String> keys) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ForegroundService.this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putStringSet("keys", keys);
        editor.apply();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
