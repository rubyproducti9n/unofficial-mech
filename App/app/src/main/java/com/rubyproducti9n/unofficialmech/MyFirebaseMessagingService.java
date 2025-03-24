package com.rubyproducti9n.unofficialmech;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.squareup.picasso.Picasso;

import java.io.IOException;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final int REQUEST_NOTIFICATION_PERMISSION = 1;
    Context context;
    long[] vibrate = {200, 500,200};

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
        context = getApplicationContext();

        startForegroundService(new Intent(this, ForegroundService.class));

        if(message.getNotification() != null){

//            if (isAdmin(context)){
//                try {
//                    triggerNotification( "title1", "msg1", null, "Service");
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
//            }else{
//            }

            String title = message.getNotification().getTitle();
            String msg = message.getNotification().getBody();
            String img = String.valueOf(message.getNotification().getImageUrl());
            String channel = message.getNotification().getChannelId();
            String sound = message.getNotification().getSound();
            String priority = String.valueOf(message.getNotification().getNotificationPriority());

            try {
                triggerNotification(title, msg, img, channel);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }



    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
    }

    private void triggerNotification(String title, String msg, String img, String ChannelId) throws IOException {
        
        long[] vibrate = {200, 500,200};

        NotificationChannel channel = new NotificationChannel("alert", "Alert", NotificationManager.IMPORTANCE_HIGH);
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel);

//            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECEIVE_BOOT_COMPLETED) != PackageManager.PERMISSION_GRANTED ||
//                    ContextCompat.checkSelfPermission(this, android.Manifest.permission.VIBRATE) != PackageManager.PERMISSION_GRANTED ||
//                    ContextCompat.checkSelfPermission(this, android.Manifest.permission.FOREGROUND_SERVICE) != PackageManager.PERMISSION_GRANTED ||
//                    ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_NOTIFICATION_POLICY) != PackageManager.PERMISSION_GRANTED){
//                ActivityCompat.requestPermissions(context, new String[]{
//                        android.Manifest.permission.RECEIVE_BOOT_COMPLETED,
//                        android.Manifest.permission.VIBRATE,
//                        android.Manifest.permission.FOREGROUND_SERVICE,
//                        Manifest.permission.ACCESS_NOTIFICATION_POLICY},REQUEST_NOTIFICATION_PERMISSION);
//            }


        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, ChannelId)
                .setSmallIcon(R.drawable.notification_img)
                .setContentTitle(title)
                .setContentText(msg)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(msg))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setVibrate(vibrate)
                .setFullScreenIntent(pendingIntent, true)
                .setContentIntent(pendingIntent)
                .setOngoing(true);
        
        if (img!=null){
            Bitmap bitmap = Picasso.get().load(img).get();
            builder.setLargeIcon(bitmap);
        }

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

}
