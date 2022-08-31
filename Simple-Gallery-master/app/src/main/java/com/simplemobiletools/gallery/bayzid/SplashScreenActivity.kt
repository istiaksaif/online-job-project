package com.simplemobiletools.gallery.bayzid

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.simplemobiletools.gallery.bayzid.activities.MainActivity
import com.simplemobiletools.gallery.bayzid.activities.SplashActivity

class SplashScreenActivity : AppCompatActivity() {

    private var mInterstitialAd: InterstitialAd? = null
//    private var appOpenManager: AppOpenManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
//        MobileAds.initialize(
//            this
//        ) { }
//        appOpenManager = AppOpenManager(this@App)
        loadInterAd()
        Handler().postDelayed({
            showInterAd()
        }, 3000)

    }
    private fun showInterAd() {

        if (mInterstitialAd!=null){
            mInterstitialAd?.show(this)
            mInterstitialAd?.fullScreenContentCallback = object: FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    val intent = Intent(applicationContext, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError?) {

                }

                override fun onAdShowedFullScreenContent() {

                    mInterstitialAd = null
                }
            }

        }
        else{
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    private fun loadInterAd() {

        var adRequest = AdRequest.Builder().build()

        InterstitialAd.load(this,getString(R.string.admob_interstitial_ad_unit_id), adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                mInterstitialAd = null
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                mInterstitialAd = interstitialAd
//                showInterAd()
            }

        })
    }
}
