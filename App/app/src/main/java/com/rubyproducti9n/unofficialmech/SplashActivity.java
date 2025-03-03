package com.rubyproducti9n.unofficialmech;


import static com.rubyproducti9n.unofficialmech.Algorithms.paymentServiceCheck;
import static com.rubyproducti9n.unofficialmech.ProjectToolkit.disableStatusBar;
import static com.rubyproducti9n.unofficialmech.ProjectToolkit.fServer;
import static com.rubyproducti9n.unofficialmech.ProjectToolkit.fadeIn;
import static com.rubyproducti9n.unofficialmech.ProjectToolkit.fadeOut;
import static com.rubyproducti9n.unofficialmech.ProjectToolkit.initiateAds;
import static com.rubyproducti9n.unofficialmech.ProjectToolkit.prefetchAds;
import static com.rubyproducti9n.unofficialmech.ProjectToolkit.register;
import static com.rubyproducti9n.unofficialmech.ProjectToolkit.serviceCheck;
import static com.rubyproducti9n.unofficialmech.ProjectToolkit.unregister;

import static java.lang.Math.log;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.ActivityManager;
import android.app.ActivityOptions;
import android.app.DownloadManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.job.JobInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat;
import androidx.preference.PreferenceManager;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.appupdate.AppUpdateOptions;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class SplashActivity extends AppCompatActivity {


    ActivityResultLauncher<IntentSenderRequest> activityResultLauncher;
    private static final String TAG = "MainActivity";
    private static final int IMMEDIATE_UPDATE_REQUEST_CODE = 123;

    private AppUpdateManager appUpdateManager;
    private InstallStateUpdatedListener installStateUpdatedListener;

    private LinearProgressIndicator progressInd;
    private int progressStatus = 0;
    private Handler handler = new Handler(Looper.getMainLooper());

    //public static final long EXPIRY = 1688371312000L;
    String versionValue;
    String getRole;
    Boolean isSuspended;
    SharedPreferences pref;
    MaterialCardView logo;
    public static int TIMEOUT = 1000;
    ExecutorService exeSingle = Executors.newSingleThreadExecutor();
    ExecutorService exeFixed = Executors.newFixedThreadPool(9);
    ExecutorService exeCached = Executors.newCachedThreadPool();
    private static final int REQUEST_NOTIFICATION_PERMISSION = 1;
    private ConnectionReceiver receiver;
    //public String expiryDate = "Expires: 03-07-2023";
    //Date:- 2023/03/20 00:00:00 using Epoch Converter
    //https://epochconverter.com

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        EdgeToEdge.enable(this);

        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(this.getResources().getColor(R.color.DynamicWhite));

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        disableStatusBar(SplashActivity.this);

        Looper.getMainLooper();

//        register(SplashActivity.this);
//        load(DashboardActivity.class);
        optimized();

        MaterialCardView img0 = findViewById(R.id.img_logo0);
        animateImageView(img0, 120, 850);

        progressInd = findViewById(R.id.progress);


        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartIntentSenderForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(androidx.activity.result.ActivityResult o) {
                        if (o.getResultCode() != RESULT_OK) {
                            Log.d("Google Play Store Auto-Update", "Update flow failed! Result code: " + o.getResultCode());
                            // If the update is canceled or fails,
                            // you can request to start the update again.
                        }
                    }
                }
        );
        appUpdateManager = AppUpdateManagerFactory.create(this);


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    public void animateImageView(MaterialCardView imageView, float targetX, int duration) {
        float currentX = imageView.getX();
        float currentScale = imageView.getScaleX();

        //Translation
        ObjectAnimator translateAnim = ObjectAnimator.ofFloat(imageView, "translationX", currentX, targetX);
        translateAnim.setDuration(duration);
        translateAnim.setInterpolator(new AccelerateInterpolator());

        //Rotation
        float currentRotation = imageView.getRotation();
        ObjectAnimator rotateAnim = ObjectAnimator.ofFloat(imageView, "rotation", currentRotation, 0f);
        rotateAnim.setDuration(duration);
//        rotateAnim.setInterpolator(new AccelerateDecelerateInterpolator());

        //Scale
        float targetScale = 0.9f; // Adjust this value to control the final scale (e.g., 0.8f for 20% smaller)
        ObjectAnimator scaleAnim = ObjectAnimator.ofFloat(imageView, "scaleX", currentScale, targetScale);
        scaleAnim.setDuration(duration);

        AnimatorSet animationSet = new AnimatorSet();
        animationSet.playTogether(translateAnim, rotateAnim, scaleAnim);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                animationSet.start();
            }
        }, 2000);
    }

    private void initializeServer() {
        String role = pref.getString("auth_userole", null);
        boolean serverStat = pref.getBoolean("serverStat", false);
        serviceCheck(this, new ProjectToolkit.ServiceCheckCallBack() {
            @Override
            public void onResult(Boolean result) {
                SharedPreferences.Editor editor = pref.edit();
                if (role != null) {
                    editor.putBoolean("serverStat", result);
                    editor.apply();
                }
            }
        });
    }

    public boolean foregroundServiceRunning() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE)) {
            if (ForegroundService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private void createChannel() {

        long[] v = {0, 100, 100};

        NotificationChannel channel = new NotificationChannel("alert", "Alert", NotificationManager.IMPORTANCE_HIGH);
        NotificationManager manager0 = getSystemService(NotificationManager.class);
        manager0.createNotificationChannel(channel);

        NotificationChannel defaultChannel = new NotificationChannel("default_channel", "Unofficial Mech", NotificationManager.IMPORTANCE_DEFAULT);
        defaultChannel.setDescription("Default Channel");
        defaultChannel.enableLights(true);
        defaultChannel.setLightColor(Color.BLUE);
        defaultChannel.enableVibration(true);
        defaultChannel.setVibrationPattern(v);

        NotificationChannel newPostChannel = new NotificationChannel("new_post_channel", "New Post", NotificationManager.IMPORTANCE_HIGH);defaultChannel.setDescription("Default Channel");
        newPostChannel.enableLights(true);
        newPostChannel.setLightColor(Color.BLUE);
        newPostChannel.enableVibration(true);
        newPostChannel.setVibrationPattern(v);

        NotificationChannel newNoticeChannel = new NotificationChannel("new_notice_channel", "New Notice", NotificationManager.IMPORTANCE_HIGH);defaultChannel.setDescription("Default Channel");
        newNoticeChannel.enableLights(true);
        newNoticeChannel.setLightColor(Color.BLUE);
        newNoticeChannel.enableVibration(true);
        newNoticeChannel.setVibrationPattern(v);

        NotificationChannel postUploadChannel = new NotificationChannel("post_upload_channel", "Post Upload", NotificationManager.IMPORTANCE_MIN);defaultChannel.setDescription("Default Channel");
        postUploadChannel.enableLights(true);
        postUploadChannel.setLightColor(Color.BLUE);
        postUploadChannel.enableVibration(true);
        postUploadChannel.setVibrationPattern(v);

        NotificationChannel noticeUploadChannel = new NotificationChannel("notice_upload_channel", "Notice Upload", NotificationManager.IMPORTANCE_LOW);defaultChannel.setDescription("Default Channel");
        noticeUploadChannel.enableLights(true);
        noticeUploadChannel.setLightColor(Color.BLUE);
        noticeUploadChannel.enableVibration(true);
        noticeUploadChannel.setVibrationPattern(v);

        NotificationChannel fChannel = new NotificationChannel("facultyChannel", "Faculty Channel", NotificationManager.IMPORTANCE_DEFAULT);defaultChannel.setDescription("Default Channel");
        fChannel.enableLights(true);
        fChannel.setLightColor(Color.BLUE);
        fChannel.enableVibration(true);
        fChannel.setVibrationPattern(v);

        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(defaultChannel);
        manager.createNotificationChannel(newPostChannel);
        manager.createNotificationChannel(newNoticeChannel);
        manager.createNotificationChannel(postUploadChannel);
        manager.createNotificationChannel(noticeUploadChannel);
        manager.createNotificationChannel(fChannel);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQUEST_NOTIFICATION_PERMISSION);
            return;
        }
        showNotification();

    }

    private void showNotification() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECEIVE_BOOT_COMPLETED) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.VIBRATE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.FOREGROUND_SERVICE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_NOTIFICATION_POLICY) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    android.Manifest.permission.RECEIVE_BOOT_COMPLETED,
                    android.Manifest.permission.VIBRATE,
                    android.Manifest.permission.FOREGROUND_SERVICE,
                    Manifest.permission.ACCESS_NOTIFICATION_POLICY}, REQUEST_NOTIFICATION_PERMISSION);
        }

        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(SplashActivity.this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default")
                .setSmallIcon(R.drawable.notification_img)
                .setContentTitle("Optimizing..")
                .setContentText("Checking for new posts")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setFullScreenIntent(pendingIntent, true)
                .setTimeoutAfter(2000);

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
        notificationManagerCompat.notify(0, builder.build());
    }

    private void initiateActivity() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            createChannel();
        }
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String userId = sharedPreferences.getString("auth_userId", null);
        boolean dash = sharedPreferences.getBoolean("dashboard", false);
        if (dash) {
            load(QuickMenuActivity.class);
        } else {
            if (userId != null) {
                MainActivity();
            } else {
                PasswordActivity();
            }
        }
    }

    public void PasswordActivity() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                toPassword();
            }
        }, TIMEOUT);
    }

    public void MainActivity() {
        SharedPreferences rolePreference = PreferenceManager.getDefaultSharedPreferences(this);
        getRole = rolePreference.getString("auth_userole", null);

        DatabaseReference lockdownReference = FirebaseDatabase.getInstance().getReference("app-configuration/lockdown");
        lockdownReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean lockdownValue = snapshot.getValue(Boolean.class);

                if (lockdownValue != null) {
                    if (isRoleNull(getRole)) {
                        toPassword();
                    } else {
                        if (Boolean.FALSE.equals(lockdownValue)) {
                            load(MainActivity.class);
                        } else {
                            load(AccessDeniedActivity.class);
                        }
                    }
                } else {
                    progressInd.setVisibility(View.GONE);
                    Intent intent = new Intent(SplashActivity.this, AccessDeniedActivity.class);
                    intent.putExtra("err_anim", 1);
                    intent.putExtra("err_title", "Error 505");
                    intent.putExtra("err_msg", "Oops! We're unable to fetch data");
                    startActivity(intent);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SplashActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isRoleNull(String role) {
        return role == null || "null".equals(role);
    }

    private void toMainActivity() {
        load(MainActivity.class);
    }

    private void toPassword() {
        load(PasswordActivity.class);
    }

    private void fetch() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                fServer(SplashActivity.this);
            }
        });
        initiateAds(SplashActivity.this, SplashActivity.this);

        Intent serviceIntent = new Intent(SplashActivity.this, ForegroundService.class);
        startForegroundService(serviceIntent);
        foregroundServiceRunning();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            register(SplashActivity.this);
        }

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        pref = PreferenceManager.getDefaultSharedPreferences(this);
        initializeServer();


        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String mail = pref.getString("auth_email", null);
        new Thread(new Runnable() {
            @Override
            public void run() {
                paymentServiceCheck(SplashActivity.this, new Algorithms.PaymentServiceCheckCallBack() {
                    @Override
                    public void onResult(Boolean result) {
                        if (result) {
                            if (mail != null) {
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");
                                ref.orderByChild("personalEmail").equalTo(mail).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                                String id = dataSnapshot.child("userId").getValue(String.class);
                                                String date = dataSnapshot.child("lastPaymentDate").getValue(String.class);
                                                if (date != null) {
                                                    Date lastPyamentDate = null;
                                                    try {
                                                        lastPyamentDate = dateFormat.parse(date);
                                                    } catch (ParseException e) {
                                                        throw new RuntimeException(e);
                                                    }
                                                    Date currentDate = new Date();
                                                    long diffInMs = currentDate.getTime() - lastPyamentDate.getTime();
                                                    long diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMs);

                                                    //Working Code but not as wished
//                                            if (lastPyamentDate.before(currentDate) || lastPyamentDate.equals(currentDate)) {
//                                                Intent intent = new Intent(SplashActivity.this, PaymentActivity.class);
//                                                intent.putExtra("userId", id);
//                                                startActivity(new Intent(SplashActivity.this, PaymentActivity.class));
//                                                overridePendingTransition(R.anim.slide_right, R.anim.slide_left);
//                                                finish();
//                                            }
                                                    if (lastPyamentDate.before(currentDate) || lastPyamentDate.equals(currentDate)) {
                                                        runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                Intent intent = new Intent(SplashActivity.this, PaymentActivity.class);
                                                                intent.putExtra("userId", id);
                                                                load(PaymentActivity.class);
                                                            }
                                                        });
                                                    } else {
                                                        new Handler().postDelayed(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                initiateActivity();
                                                            }
                                                        }, TIMEOUT);
                                                    }
                                                } else {
                                                    ref.orderByChild("personalEmail").equalTo(mail).addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            Date newDate = new Date();
                                                            String dateString = dateFormat.format(newDate);
                                                            //Date newPaymentDate = dateFormat.parse(dateString);
                                                            String newPaymentDate = dateFormat.format(newDate);
                                                            snapshot.getRef().child(id).child("lastPaymentDate").setValue(newPaymentDate);
                                                            //Uncomment
                                                            toMainActivity();
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {
                                                            runOnUiThread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    toPassword();
                                                                    Toast.makeText(SplashActivity.this, "Access Denied!", Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                        }
                                                    });
                                                }
                                            }
                                        } else {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    toPassword();
                                                    Toast.makeText(SplashActivity.this, "Sorry, your account was not found.", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                toPassword();
                                                Toast.makeText(SplashActivity.this, "Access Denied!", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                });
                            } else {
                                runOnUiThread(SplashActivity.this::toPassword);
                            }
                        } else {
                            if (mail != null) {
                                boolean dashboard = pref.getBoolean("dashboard", false);
                                if (dashboard) {
                                    runOnUiThread(() -> load(QuickMenuActivity.class));
                                } else {
                                    runOnUiThread(SplashActivity.this::toMainActivity);
                                }
                            } else {
                                runOnUiThread(SplashActivity.this::toPassword);
                            }
                        }
                    }
                });
            }
        });
    }

    private void optimized() {
        exeCached.execute(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
            @Override
            public void run() {
                fServer(SplashActivity.this);
//                checkSystemSettings();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        initiateAds(SplashActivity.this, SplashActivity.this);
                    }
                });
                Intent serviceIntent = new Intent(SplashActivity.this, ForegroundService.class);
                startForegroundService(serviceIntent);
                foregroundServiceRunning();

                register(SplashActivity.this);
//                receiver = new ConnectionReceiver();
//                IntentFilter filter = new IntentFilter();
//                filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
//                filter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
//                filter.addAction(Intent.ACTION_BOOT_COMPLETED);
//                filter.addAction("android.app.action.SERVICE_DESTROYED");
//                registerReceiver(receiver, filter, Context.RECEIVER_NOT_EXPORTED);
            }
        });
        exeCached.shutdown();

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        pref = PreferenceManager.getDefaultSharedPreferences(this);
        initializeServer();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String mail = pref.getString("auth_email", null);

        exeFixed.execute(new Runnable() {
            @Override
            public void run() {
                paymentServiceCheck(SplashActivity.this, new Algorithms.PaymentServiceCheckCallBack() {
                    @Override
                    public void onResult(Boolean result) {
                        if (result) {
                            if (mail != null) {
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");
                                ref.orderByChild("personalEmail").equalTo(mail).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                                String id = dataSnapshot.child("userId").getValue(String.class);
                                                String date = dataSnapshot.child("lastPaymentDate").getValue(String.class);
                                                if (date != null) {
                                                    Date lastPyamentDate = null;
                                                    try {
                                                        lastPyamentDate = dateFormat.parse(date);
                                                    } catch (ParseException e) {
                                                        throw new RuntimeException(e);
                                                    }
                                                    Date currentDate = new Date();
                                                    long diffInMs = currentDate.getTime() - lastPyamentDate.getTime();
                                                    long diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMs);

                                                    //Working Code but not as wished
//                                            if (lastPyamentDate.before(currentDate) || lastPyamentDate.equals(currentDate)) {
//                                                Intent intent = new Intent(SplashActivity.this, PaymentActivity.class);
//                                                intent.putExtra("userId", id);
//                                                startActivity(new Intent(SplashActivity.this, PaymentActivity.class));
//                                                overridePendingTransition(R.anim.slide_right, R.anim.slide_left);
//                                                finish();
//                                            }
                                                    if (lastPyamentDate.before(currentDate) || lastPyamentDate.equals(currentDate)) {
                                                        Intent intent = new Intent(SplashActivity.this, PaymentActivity.class);
                                                        intent.putExtra("userId", id);
                                                        load(PaymentActivity.class);
                                                    } else {
                                                        new Handler().postDelayed(new Runnable() {
                                                            @Override
                                                            public void run() {
//                                        startActivity(new Intent(SplashActivity.this, PaymentActivity.class));
//                                        overridePendingTransition(R.anim.slide_right, R.anim.slide_left);
                                                                initiateActivity();
                                                            }
                                                        }, TIMEOUT);
                                                    }
                                                } else {
                                                    ref.orderByChild("personalEmail").equalTo(mail).addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            Date newDate = new Date();
                                                            String dateString = dateFormat.format(newDate);
                                                            //Date newPaymentDate = dateFormat.parse(dateString);
                                                            String newPaymentDate = dateFormat.format(newDate);
                                                            snapshot.getRef().child(id).child("lastPaymentDate").setValue(newPaymentDate);
                                                            //Uncomment
                                                            toMainActivity();
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {
                                                            toPassword();
                                                            Toast.makeText(SplashActivity.this, "Access Denied!", Toast.LENGTH_SHORT).show();

                                                        }
                                                    });
                                                }
                                            }
                                        } else {
                                            toPassword();
                                            Toast.makeText(SplashActivity.this, "Sorry, your account was not found.", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        toPassword();
                                        Toast.makeText(SplashActivity.this, "Access Denied!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                toPassword();
                            }
                        } else {
                            if (mail != null) {
                                boolean dashboard = pref.getBoolean("dashboard", false);
                                if (dashboard) {
                                    load(QuickMenuActivity.class);
                                } else {
                                    toMainActivity();
                                }
                            } else {
                                toPassword();
                            }
                        }
                    }
                });

            }
        });

    }

    private void load(Class c) {
        Log.d("Background Task", "Fetching server...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(() -> fadeIn(progressInd));
                while (progressStatus < 100) {
                    progressStatus += 1;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressInd.setProgress(progressStatus, true);
                        }
                    });
                    try {
                        Thread.sleep(12);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        checkForUpdate(c);
                    }
                }, 0);
            }
        }).start();
    }

    private void loadActivity(Class c) {
        View view = findViewById(R.id.img_logo);
        Log.d("Activity Load", "Initiating Activity...");
        Intent i = new Intent(this, c);
        startActivity(i, ActivityOptions.makeSceneTransitionAnimation(this, view, "sharedElement").toBundle());
        //startActivity(new Intent(SplashActivity.this, c));
        //overridePendingTransition(R.anim.slide_right, R.anim.slide_left);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 2000);
    }

    private final ConnectionReceiver broadcastReceiver = new ConnectionReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            super.onReceive(context, intent);
            startForegroundService(new Intent(context, ForegroundService.class));
        }
    };

    private void checkForUpdate(Class c) {
        Log.d("InAppUpdate", "Checking for updates....");
        AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(SplashActivity.this);
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

        appUpdateInfoTask.addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(AppUpdateInfo appUpdateInfo) {
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                    if (appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE) || appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                        Log.d("InAppUpdate", "Immediate Update: " + appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE));
                        Log.d("InAppUpdate", "Flexible Update: " + appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE));
                        appUpdateManager.startUpdateFlowForResult(
                                appUpdateInfo,
                                activityResultLauncher,
                                AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build()
                        );
                    }
                } else {
                    loadActivity(c);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("InAppUpdate", "Update check failed", e);
                loadActivity(c);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        progressInd.setVisibility(View.GONE);
//        Toast.makeText(this, "Update to proceed", Toast.LENGTH_SHORT).show();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        overridePendingTransition(R.anim.slide_right, R.anim.slide_left);
//        unregisterReceiver(receiver);
        unregister(SplashActivity.this);
    }
}