package com.istiaksaif.highlymotavated.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.istiaksaif.highlymotavated.R;
import com.istiaksaif.highlymotavated.Receiver.NetworkChangeListener;
import com.istiaksaif.highlymotavated.Utils.CheckInternet;

public class SplashScreenActivity extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 2000;
    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private NetworkChangeListener networkChangeListener = new NetworkChangeListener();
    public static Boolean checkFirstTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("users");
        getDynamicLink();
    }

    private void checkUserInfo() {
        FirebaseUser user = mAuth.getCurrentUser();
        Query query = databaseReference.child(user.getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        Intent intent = new Intent(SplashScreenActivity.this, UserHomeActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }, SPLASH_TIME_OUT);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeListener, filter);
        if(CheckInternet.isConnectedToInternet(this)){
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                checkUserInfo();
            } else {
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        Intent intent = new Intent(SplashScreenActivity.this, LogInActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }, SPLASH_TIME_OUT);
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(networkChangeListener);
    }
    private void getDynamicLink() {
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        Uri deepLink = null;
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();
                            String reflink = deepLink.toString();
                            try {
                                reflink = reflink.substring(reflink.lastIndexOf("=")+1);
                                String referUserKey = reflink.substring(0,reflink.indexOf("_"));
                                String referUserId = reflink.substring(reflink.indexOf("_")+1);
                                saveData(SplashScreenActivity.this,referUserKey,referUserId);
                            }catch (Exception e){
                                Log.e("Receivelink", "error: "+e);
                            }
                        }
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Receivelink", "getDynamicLink:onFailure", e);
                    }
                });
    }
    public static void saveData(Activity activity,String referUserKey,String referUserId){
        SharedPreferences sharedPreferences = activity.getSharedPreferences("CheckFirstTime", MODE_PRIVATE);
        checkFirstTime = Boolean.valueOf(sharedPreferences.getBoolean("CheckFirstTime", true));
        if (checkFirstTime.booleanValue()) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("CheckFirstTime", false);
            editor.putString("referUserKey", referUserKey);
            editor.putString("referUserId", referUserId);
            editor.apply();
        }
    }
    public static String loadData(Activity activity){
        SharedPreferences sharedPreferences = activity.getSharedPreferences("CheckFirstTime", MODE_PRIVATE);
        String key = sharedPreferences.getString("referUserKey","");
        return key;
    }
    public static String loadDataReferUserId(Activity activity){
        SharedPreferences sharedPreferences = activity.getSharedPreferences("CheckFirstTime", MODE_PRIVATE);
        String key = sharedPreferences.getString("referUserId","");
        return key;
    }
}