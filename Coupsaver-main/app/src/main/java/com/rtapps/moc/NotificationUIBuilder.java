package com.rtapps.moc;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;

/**
 * Created by Emp on 12/27/2016.
 */
public class NotificationUIBuilder {

    public static final String NOTIFICATION_CHANNEL_ID = "361671";
    public static final int ACCEPTED_LOCATION_TRACKING = 1;
    public static final int REJECTED_LOCATION_TRACKING = 2;
    private static NotificationUIBuilder ourInstance;
    private final String TAG = "tagNotificationBuilder";
    private final int notificationCounter = 0;
    private final Context context;
    private NotificationCompat.Builder notificationBuilder;
    private NotificationManager notificationManager;


    private NotificationUIBuilder(Context context) {
        this.context = context;
    }

    public static NotificationUIBuilder getInstance(Context context) {
        if (ourInstance == null) {
            ourInstance = new NotificationUIBuilder(context);
        }
        return ourInstance;
    }

    private boolean isSDKGreaterThanMarshmallow() {
        return Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1;
    }


    public void RequestNotification(Bundle bundle) {

    }


    public void generateAlertNotification(String message,String catalogNumber, String name, String code,String date,String provider) {
        /**Creates an explicit intent for an Activity in your app**/
        Intent resultIntent = new Intent(context, SplashActivity.class);
        resultIntent.putExtra("notificationClicked", true);
        resultIntent.putExtra("catalogNumber", catalogNumber);
        resultIntent.putExtra("name", name);
        resultIntent.putExtra("code", code);
        resultIntent.putExtra("date", date);
        resultIntent.putExtra("provider", provider);


        resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent resultPendingIntent = PendingIntent.getActivity(context,
                0 /* Request code */, resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        mBuilder.setContentTitle("Food App")
                .setContentText(message)
                .setContentIntent(resultPendingIntent)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setAutoCancel(true);

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Cangoo Notifications", importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            assert mNotificationManager != null;
            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
        assert mNotificationManager != null;
        mNotificationManager.notify(0 /* Request Code */, mBuilder.build());
//        mNotificationManager.notify(notificationCounter++ /* Request Code */, mBuilder.build());
    }


}
