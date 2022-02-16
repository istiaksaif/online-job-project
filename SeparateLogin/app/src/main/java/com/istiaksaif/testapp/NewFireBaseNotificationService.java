package com.istiaksaif.testapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.istiaksaif.testapp.Activity.HomeActivity;

import java.util.Random;

public class NewFireBaseNotificationService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Intent intent = new Intent(this, HomeActivity.class);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        int notificationId = new Random().nextInt();
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent intent1 = PendingIntent.getActivities(this,0,new Intent[]{intent},PendingIntent.FLAG_ONE_SHOT);
        Notification notification;
        notification = new NotificationCompat.Builder(this,"100")
                .setContentTitle(remoteMessage.getData().get("title"))
                .setContentText(remoteMessage.getData().get("message"))
                .setSmallIcon(R.drawable.ic_logo)
                .setAutoCancel(true).setContentIntent(intent1).build();

        notificationManager.notify(notificationId,notification);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNormalNotification(notificationManager);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNormalNotification(NotificationManager notificationManager) {
        NotificationChannel notificationChannel = new NotificationChannel("100","channelName",NotificationManager.IMPORTANCE_HIGH);
        notificationChannel.setDescription("My Description");
        notificationChannel.enableLights(true);
        notificationChannel.setLightColor(Color.WHITE);
        notificationManager.createNotificationChannel(notificationChannel);
    }
}
