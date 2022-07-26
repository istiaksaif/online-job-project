package com.bayzid.qrbarcodescanner2022.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.bayzid.qrbarcodescanner2022.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

public class SplashActivity extends AppCompatActivity {


    private final int SPLASH_DELAY = 3000;
    private ImageView mImageViewLogo;
    private InterstitialAd mInterstitialAd = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        getWindow().setBackgroundDrawable(null);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {

            }
        });

        loadInterAd();
        initializeViews();
        animateLogo();
        new Handler().postDelayed(()->{
            showInterAd();
        }, SPLASH_DELAY);
    }

    private void initializeViews() {
        mImageViewLogo = findViewById(R.id.image_view_logo);
    }
    public void loadInterAd(){
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(this,getString(R.string.admob_interstitial_ad_unit_id), adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        mInterstitialAd = interstitialAd;
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        mInterstitialAd = null;
                    }
                });
    }
    private void showInterAd(){
        if(mInterstitialAd!=null){
            mInterstitialAd.show(this);
            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent();
                    startActivity(new Intent(SplashActivity.this,HomeActivity.class));
                    finish();
                }
            });
        }else {
            startActivity(new Intent(SplashActivity.this, HomeActivity.class));
            finish();
        }
    }

    private void animateLogo() {
        Animation fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in_without_duration);
        fadeInAnimation.setDuration(SPLASH_DELAY);

        mImageViewLogo.startAnimation(fadeInAnimation);
    }
}