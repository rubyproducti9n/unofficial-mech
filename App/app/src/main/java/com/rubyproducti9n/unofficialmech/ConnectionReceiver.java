package com.rubyproducti9n.unofficialmech;

import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Objects;

public class ConnectionReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "Receiver_Channel";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

//        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "default")
//                .setSmallIcon(R.drawable.notify)
//                .setContentTitle("5 Minute Reminder")
//                .setContentText("Hello! It's been 5 minutes.")
//                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
//
//        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
//        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        notificationManager.notify(1, builder.build());

        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
            boolean isConnected = isConnectedToNetwork(context);

            if (!isServiceRunning(context)) {
                startForegroundService(context);
            }

            if (!isConnected) {
                showNetworkNotification(context);
            }
        }else if (Intent.ACTION_BOOT_COMPLETED.equals(action)) {
            startForegroundService(context);
        } else if (isServiceKilled(intent)) {
            startForegroundService(context);
        }
    }

    private boolean isConnectedToNetwork(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    private boolean isServiceRunning(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (ForegroundService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void startForegroundService(Context context) {
        Intent serviceIntent = new Intent(context, ForegroundService.class);
        context.startForegroundService(serviceIntent);
    }

    private void showNetworkNotification(Context context) {
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID, "No Network", NotificationManager.IMPORTANCE_HIGH);
        context.getSystemService(NotificationManager.class).createNotificationChannel(channel);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_unofficial)
                .setContentTitle("No Network Connection")
                .setContentText("It looks like you're offline. We're standing by and will reconnect as soon as you're back online!")
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManager.notify(1, builder.build());
    }

    private boolean isServiceKilled(Intent intent) {
        return "android.app.action.SERVICE_DESTROYED".equals(intent.getAction())
                && ForegroundService.class.getName().equals(Objects.requireNonNull(intent.getComponent()).getClassName());
    }
}
