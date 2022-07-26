package com.bayzid.qrbarcodescanner2022;

import android.content.Context;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.bayzid.qrbarcodescanner2022.database.DatabaseUtil;
import com.bayzid.qrbarcodescanner2022.utils.SharedPrefUtil;
import com.google.android.gms.ads.MobileAds;

public class QRApplication extends MultiDexApplication {

    private static QRApplication sInstance;

    public static Context getContext() {
        return sInstance.getApplicationContext();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        SharedPrefUtil.init(getApplicationContext());
        DatabaseUtil.init(getApplicationContext());
//        MobileAds.initialize(this, getString(R.string.admob_app_id));
    }
}
