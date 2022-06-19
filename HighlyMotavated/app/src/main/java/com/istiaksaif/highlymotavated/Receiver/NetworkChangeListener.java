package com.istiaksaif.highlymotavated.Receiver;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;

import com.istiaksaif.highlymotavated.Activity.LogInActivity;
import com.istiaksaif.highlymotavated.R;
import com.istiaksaif.highlymotavated.Utils.CheckInternet;

public class NetworkChangeListener extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(!CheckInternet.isConnectedToInternet(context)){
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            View view = LayoutInflater.from(context).inflate(R.layout.check_internet_dialog,null);
            builder.setView(view);

            builder.setCancelable(false).setPositiveButton("Connect", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    context.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                }
            }).setNegativeButton("Retry", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
//                    dialog.dismiss();
                    onReceive(context,intent);
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
            dialog.setCancelable(false);
            dialog.getWindow().setGravity(Gravity.CENTER);
//            TextView button = view.findViewById(R.id.retrybutton);
//            button.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    dialog.dismiss();
//                    onReceive(context,intent);
//                }
//            });
        }
    }
}
