package com.rtapps.moc;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends AppCompatActivity {
    InterstitialAd mInterstitialAd;

    boolean notificationClicked = false;


    @Override
    protected void onStart() {
        super.onStart();
        LanguageSet.setLocale(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        getWindow().addFlags(SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_splash);
//
//        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        FOR WHITE bELOW
//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        getWindow().setStatusBarColor(SplashActivity.this.getResources().getColor(R.color.background));
//        notificationClicked = getIntent().getBooleanExtra("notificationClicked", false);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(SplashActivity.this, EmailLoginActivity.class));
            finish();
            return;
        }

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                createCustominterstitial();

            }
        });
    }

    private void createCustominterstitial() {
        AdRequest adRequest = new AdRequest.Builder().build();
        createintersitial(adRequest);

    }

    private void createintersitial(AdRequest adRequest) {

        InterstitialAd.load(this, getString(R.string.interstitialAd), adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                // The mInterstitialAd reference will be null until
                // an ad is loaded.
                mInterstitialAd = interstitialAd;
                Log.e("TAG", "onAdLoaded");
                if (mInterstitialAd != null) {
                    mInterstitialAd.show(SplashActivity.this);
                } else {
                    Log.d("TAG", "The interstitial ad wasn't ready yet.");
                }
                mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        goToHome();
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                        // Called when fullscreen content failed to show.
                        Log.d("TAG", "The ad failed to show.");
                        goToHome();
                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                        // Called when fullscreen content is shown.
                        // Make sure to set your reference to null so you don't
                        // show it a second time.
                        mInterstitialAd = null;
                        Log.d("TAG", "The ad was shown.");
                    }
                });
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                // Handle the error
                Log.e("TAG", loadAdError.getMessage());
                mInterstitialAd = null;
                goToHome();
            }
        });
    }


    private void goToHome(){

        // Called when fullscreen content is dismissed.
        Log.d("TAG", "The ad was dismissed.");

            notificationClicked = getIntent().getBooleanExtra("notificationClicked", false);
            if (notificationClicked) {
//                                boolean isNotificationClicked = getIntent().getBooleanExtra("notificationClicked", false);
                String name = getIntent().getStringExtra("name");
                String code = getIntent().getStringExtra("code");
                String date = getIntent().getStringExtra("date");
                String provider = getIntent().getStringExtra("provider");
                String catalogNumber=getIntent().getStringExtra("catalogNumber");

                Intent uploadIntent = new Intent(SplashActivity.this, UploadMenuItemsActivity.class);
                uploadIntent.putExtra("notificationClicked", notificationClicked);
                uploadIntent.putExtra("catalogNumber", catalogNumber);
                uploadIntent.putExtra("name", name);
                uploadIntent.putExtra("code", code);
                uploadIntent.putExtra("date", date);
                uploadIntent.putExtra("provider", provider);
                startActivity(uploadIntent);

//                        startActivity(new Intent(SplashActivity.this, UploadMenuItemsActivity.class));


            } else {
                Intent intent = new Intent(SplashActivity.this, DashboardActivity.class);
                intent.putExtra("UserId", FirebaseAuth.getInstance().getCurrentUser().getUid());
                startActivity(intent);


            }
            finish();


    }

}