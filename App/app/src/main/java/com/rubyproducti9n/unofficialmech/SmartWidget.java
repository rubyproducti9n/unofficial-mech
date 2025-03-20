package com.rubyproducti9n.unofficialmech;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

public class SmartWidget extends AppWidgetProvider {
    private static final String LIVE_CHANNEL_ID = "live_event_channel";
    private static final int LIVE_NOTIFICATION = 1;
    CountDownTimer countDownTimer;
    private static final Handler handler = new Handler();
    private static Runnable runnable;
    private static final long EVENT_TIME = System.currentTimeMillis() + 3600000; // Event 1 hour from now

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateWidget(context, appWidgetManager, appWidgetId);
        }
        startUpdating(context, appWidgetManager);

        startCountDown(context, "Live", "2025-03-08 18:00", "https://www.google.com/images/branding/googlelogo/2x/googlelogo_light_color_272x92dp.png", appWidgetManager, Integer.parseInt(Arrays.toString(appWidgetIds)));
        //startCountdown(context, appWidgetManager, Integer.parseInt(Arrays.toString(appWidgetIds)));
    }
    private void startCountDown(Context context, String eventTitle, String eventDateTime, String imageUrl) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            Date eventDate = sdf.parse(eventDateTime);
            if (eventDate == null) return;

            long eventTimeMillis = eventDate.getTime();
            long currentTimeMillis = System.currentTimeMillis();
            long countdownTimeMillis = eventTimeMillis - currentTimeMillis;

            if (countdownTimeMillis <= 0) {
                Log.d("LiveActivity", "Event time has already passed.");
                return;
            }

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            String channelId = "event_countdown_channel";

            NotificationChannel channel = new NotificationChannel(
                    LIVE_CHANNEL_ID,
                    "Event Countdown",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            channel.setDescription("Shows countdown for the upcoming event.");
            notificationManager.createNotificationChannel(channel);

            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            // Start Foreground Service for Countdown
            Intent serviceIntent = new Intent(context, ForegroundService.class);
            serviceIntent.putExtra("eventTitle", eventTitle);
            serviceIntent.putExtra("eventTimeMillis", eventTimeMillis);
            serviceIntent.putExtra("imageUrl", imageUrl);
            ContextCompat.startForegroundService(context, serviceIntent);

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    private void startCountDown(Context context, String eventTitle, String eventDateTime, String imageUrl, AppWidgetManager appWidgetManager, int appWidgetIds) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            Date eventDate = sdf.parse(eventDateTime);
            if (eventDate == null) return;

            long eventTimeMillis = eventDate.getTime();
            long currentTimeMillis = System.currentTimeMillis();
            long countdownTimeMillis = eventTimeMillis - currentTimeMillis;

            if (countdownTimeMillis <= 0) {
                Log.d("LiveActivity", "Event time has already passed.");
                return;
            }

            // Set up a persistent notification
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            String channelId = "event_countdown_channel";

            NotificationChannel channel = new NotificationChannel(
                    LIVE_CHANNEL_ID,
                    "Event Countdown",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            channel.setDescription("Shows countdown for the upcoming event.");
            notificationManager.createNotificationChannel(channel);

            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            // Load image from URL
            Bitmap eventImage = getBitmapFromURL(imageUrl);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                    .setSmallIcon(R.drawable.round_calendar_today_24)
                    .setContentTitle(eventTitle)
                    .setContentText("Event starts in...")
                    .setLargeIcon(eventImage)
                    .setOngoing(true)  // **Makes the notification persistent**
                    .setContentIntent(pendingIntent)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(false);

            Notification notification = builder.build();
            notificationManager.notify(1001, notification);

            // Start the countdown using a Handler
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                notificationManager.notify(1001, builder.setContentText("Event is Live Now!").build());
            }, countdownTimeMillis);

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    // Function to fetch Bitmap from an image URL
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
    //Old working function
//    private void startCountdown(Context context, AppWidgetManager appWidgetManager, int appWidgetIds) {
//
//
//        countDownTimer = new CountDownTimer(3600000, 1000) { // 1-hour countdown
//            @Override
//            public void onTick(long millisUntilFinished) {
//                updateWidget(context, appWidgetManager, appWidgetIds);
//            }
//
//            @Override
//            public void onFinish() {
//                updateNotification(context, "Event Started!");
//            }
//        }.start();
//    }
    private void startUpdating(Context context, AppWidgetManager appWidgetManager) {
        if (runnable == null) {
            runnable = new Runnable() {
                @Override
                public void run() {
                    ComponentName componentName = new ComponentName(context, SmartWidget.class);
                    int[] widgetIds = appWidgetManager.getAppWidgetIds(componentName);
                    for (int appWidgetId : widgetIds) {
                        updateWidget(context, appWidgetManager, appWidgetId);
                    }
                    handler.postDelayed(this, 1000); // Update every second
                }
            };
        }
        handler.post(runnable);
    }
    private void updateNotification(Context context,String countdownText) {
        NotificationCompat.Builder notification = new NotificationCompat.Builder(context, LIVE_CHANNEL_ID)
                .setContentTitle("Event Countdown")
                .setContentText(countdownText)
                .setSmallIcon(R.drawable.round_calendar_today_24)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setOngoing(true);

        NotificationManager manager = context.getSystemService(NotificationManager.class);
        manager.notify(LIVE_NOTIFICATION, notification.build());
    }
    private void updateWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

        // Intent to Open MainActivity
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.widgetImg, pendingIntent);

        // Refresh Widget on Click
        Intent refreshIntent = new Intent(context, SmartWidget.class);
        refreshIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        refreshIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, new int[]{appWidgetId});
        PendingIntent refreshPendingIntent = PendingIntent.getBroadcast(context, 0, refreshIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.widget_refresh_button, refreshPendingIntent);

        // Update the Widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

}


