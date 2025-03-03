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
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Icon;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
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
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ForegroundService extends Service {
    private Handler liveHandler = new Handler(Looper.getMainLooper());
    private NotificationManager notificationManager;
    private String eventTitle;
    private long eventTimeMillis;
    private Bitmap eventImage;
    private static final String LIVE_CHANNEL_ID = "live_event_channel";
    private static final int LIVE_NOTIFICATION = 1;
    private CountDownTimer countDownTimer;
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

        String msg = "We're working to ensure you never miss a beat. Everything's running smoothly!";
        Intent restartIntent = new Intent(this, MainActivity.class);
        restartIntent.setAction(Intent.ACTION_MAIN);
        restartIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, restartIntent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Keeping you updated!")
                .setContentText(msg)
                .setStyle(new NotificationCompat.BigTextStyle())
                .setSmallIcon(R.drawable.ic_unofficial)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setFullScreenIntent(pendingIntent, false)
                .setContentIntent(pendingIntent)
                .setOngoing(true);

        startForeground(NOTIFICATION_ID, builder.build());


        eventTitle = intent.getStringExtra("eventTitle");
        eventTimeMillis = intent.getLongExtra("eventTimeMillis", 0);
        String imageUrl = intent.getStringExtra("imageUrl");

        new Thread(() -> eventImage = getBitmapFromURL(imageUrl)).start();

        startCountdown();
        return START_STICKY;
    }
    private Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
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

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        createNotificationChannelLive();
        //startCountDown("Live", "2025-03-08 18:00", "https://www.google.com/images/branding/googlelogo/2x/googlelogo_light_color_272x92dp.png");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        stopForeground(true);
    }


    private void startCountdown() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                long currentTimeMillis = System.currentTimeMillis();
                long remainingTimeMillis = eventTimeMillis - currentTimeMillis;

                if (remainingTimeMillis > 0) {
                    String formattedTime = formatCountdownTime(remainingTimeMillis);
                    updateNotification(formattedTime);
                    handler.postDelayed(this, 1000); // Update every second
                } else {
                    updateNotification("🚀 Event is Live Now!");
                    stopSelf();
                }
            }
        }, 1000);
    }

    private void updateNotification(String countdownText) {
        Intent intent = new Intent(this, EventScheduleActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, LIVE_CHANNEL_ID)
                .setSmallIcon(R.drawable.round_calendar_today_24)
                .setContentTitle(eventTitle)
                .setContentText(countdownText)
                .setLargeIcon(eventImage)
                .setOngoing(true)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(false)
                .setStyle(new NotificationCompat.BigPictureStyle()
                        .bigPicture(eventImage)
                        .bigLargeIcon((Icon) null)
                        .setSummaryText(countdownText));

        startForeground(1001, builder.build());
    }

    private String formatCountdownTime(long millis) {
        long days = TimeUnit.MILLISECONDS.toDays(millis);
        long hours = TimeUnit.MILLISECONDS.toHours(millis) % 24;
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60;

        if (days > 0) {
            return String.format(Locale.getDefault(), "%d days, %d hours" + " to go", days, hours);
        } else {
            return String.format(Locale.getDefault(), "%02d:%02d:%02d" + " remaining", hours, minutes, seconds);
        }
    }


    //Old working Function
//    private void startCountdown() {
//
//        countDownTimer = new CountDownTimer(3600000, 1000) { // 1-hour countdown
//            @Override
//            public void onTick(long millisUntilFinished) {
//                updateNotification(formatTime(millisUntilFinished));
//            }
//
//            @Override
//            public void onFinish() {
//                updateNotification("Event Started!");
//            }
//        }.start();
//    }

//    private void updateNotification(String countdownText) {
//        NotificationCompat.Builder notification = new NotificationCompat.Builder(this, CHANNEL_ID)
//                .setContentTitle("Event Countdown")
//                .setContentText(countdownText)
//                .setSmallIcon(R.drawable.round_calendar_today_24)
//                .setPriority(NotificationCompat.PRIORITY_HIGH)
//                .setOngoing(true);
//
//        NotificationManager manager = getSystemService(NotificationManager.class);
//        manager.notify(NOTIFICATION_ID, notification.build());
//    }

    private void createNotificationChannelLive() {
        NotificationChannel channel = new NotificationChannel(
                LIVE_CHANNEL_ID, "Live Event Countdown", NotificationManager.IMPORTANCE_HIGH);
        getSystemService(NotificationManager.class).createNotificationChannel(channel);
    }

    private String formatTime(long millis) {
        long seconds = (millis / 1000) % 60;
        long minutes = (millis / (1000 * 60)) % 60;
        long hours = (millis / (1000 * 60 * 60)) % 24;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
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
