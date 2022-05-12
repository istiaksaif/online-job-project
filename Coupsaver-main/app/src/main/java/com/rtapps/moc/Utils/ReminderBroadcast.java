package com.rtapps.moc.Utils;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.rtapps.moc.R;
import com.rtapps.moc.SplashActivity;

import java.util.Random;

public class ReminderBroadcast extends BroadcastReceiver
{

    int rCode = -1;
    static String itemName="";

    @Override
    public void onReceive(Context context, Intent intent)
    {


        NotificationUtils _notificationUtils = new NotificationUtils(context);
        NotificationCompat.Builder _builder = _notificationUtils.setNotification(context.getResources().getString(R.string.app_name),
                 itemName);


        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, SplashActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        _builder.setContentIntent(contentIntent);

        _notificationUtils.getManager().notify(new Random().nextInt(9999 - 1000) + 1000, _builder.build());
    }
    public static void setData(String itemNam){
        itemName=itemNam;
    }



}