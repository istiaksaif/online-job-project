package com.bayzid.qrbarcodescanner2022.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bayzid.qrbarcodescanner2022.Fragment.GenerateFragment;
import com.bayzid.qrbarcodescanner2022.Fragment.HistoryFragment;
import com.bayzid.qrbarcodescanner2022.Fragment.ScanFragment;
import com.bayzid.qrbarcodescanner2022.Fragment.SettingsFragment;
import com.bayzid.qrbarcodescanner2022.R;
import com.bayzid.qrbarcodescanner2022.databinding.ActivityHomeBinding;
import com.bayzid.qrbarcodescanner2022.utils.CheckInternet;
import com.bayzid.qrbarcodescanner2022.utils.PermissionUtil;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;


public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityHomeBinding mBinding;
    private long backPressedTime;
    private CardView notNow,rateUs;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    public static Boolean checkFirstTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_home);

        getWindow().setBackgroundDrawable(null);

        setListeners();
        initializeToolbar();
        initializeBottomBar();
        playAd();
    }


    private void playAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        mBinding.adView.loadAd(adRequest);
        mBinding.adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
            }

            public void onAdFailedToLoad(int errorCode) {
                mBinding.adView.setVisibility(View.GONE);
            }

            @Override
            public void onAdOpened() {
            }

            public void onAdLeftApplication() {
            }

            @Override
            public void onAdClosed() {
            }
        });
    }

    private void initializeToolbar() {
        setSupportActionBar(mBinding.toolbar);
    }

    private void setListeners() {
        mBinding.textViewGenerate.setOnClickListener(this);
        mBinding.textViewScan.setOnClickListener(this);
        mBinding.textViewHistory.setOnClickListener(this);
        mBinding.textViewSettings.setOnClickListener(this);

        mBinding.imageViewGenerate.setOnClickListener(this);
        mBinding.imageViewScan.setOnClickListener(this);
        mBinding.imageViewHistory.setOnClickListener(this);
        mBinding.imageViewSettings.setOnClickListener(this);

        mBinding.constraintLayoutGenerateContainer.setOnClickListener(this);
        mBinding.constraintLayoutScanContainer.setOnClickListener(this);
        mBinding.constraintLayoutHistoryContainer.setOnClickListener(this);
        mBinding.constraintLayoutSettingsContainer.setOnClickListener(this);
    }

    private void initializeBottomBar() {
        clickOnScan();
    }

    private void clickOnGenerate() {
        mBinding.textViewGenerate.setTextColor(
                ContextCompat.getColor(this, R.color.dark_blue));

        mBinding.textViewScan.setTextColor(
                ContextCompat.getColor(this, R.color.transparent));

        mBinding.textViewHistory.setTextColor(
                ContextCompat.getColor(this, R.color.transparent));

        mBinding.textViewSettings.setTextColor(
                ContextCompat.getColor(this, R.color.transparent));

        mBinding.imageViewGenerate.setVisibility(View.INVISIBLE);
        mBinding.imageViewGenerateActive.setVisibility(View.VISIBLE);

        mBinding.imageViewScan.setVisibility(View.VISIBLE);
        mBinding.imageViewScanActive.setVisibility(View.INVISIBLE);

        mBinding.imageViewHistory.setVisibility(View.VISIBLE);
        mBinding.imageViewHistoryActive.setVisibility(View.INVISIBLE);

        mBinding.imageViewSettings.setVisibility(View.VISIBLE);
        mBinding.imageViewSettingsActive.setVisibility(View.INVISIBLE);

        setToolbarTitle(getString(R.string.toolbar_title_generate));
        mBinding.toolbar.setVisibility(View.VISIBLE);
        showFragment(GenerateFragment.newInstance());
    }

    private void clickOnScan() {
        if (PermissionUtil.on().requestPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)) {


            mBinding.textViewGenerate.setTextColor(
                    ContextCompat.getColor(this, R.color.transparent));

            mBinding.textViewScan.setTextColor(
                    ContextCompat.getColor(this, R.color.dark_blue));

            mBinding.textViewHistory.setTextColor(
                    ContextCompat.getColor(this, R.color.transparent));

            mBinding.textViewSettings.setTextColor(
                    ContextCompat.getColor(this, R.color.transparent));

            mBinding.imageViewGenerate.setVisibility(View.VISIBLE);
            mBinding.imageViewGenerateActive.setVisibility(View.INVISIBLE);

            mBinding.imageViewScan.setVisibility(View.INVISIBLE);
            mBinding.imageViewScanActive.setVisibility(View.VISIBLE);

            mBinding.imageViewHistory.setVisibility(View.VISIBLE);
            mBinding.imageViewHistoryActive.setVisibility(View.INVISIBLE);

            mBinding.imageViewSettings.setVisibility(View.VISIBLE);
            mBinding.imageViewSettingsActive.setVisibility(View.INVISIBLE);

            mBinding.toolbar.setVisibility(View.GONE);
            showFragment(ScanFragment.newInstance());
        }
    }

    private void clickOnHistory() {
        mBinding.textViewGenerate.setTextColor(
                ContextCompat.getColor(this, R.color.transparent));

        mBinding.textViewScan.setTextColor(
                ContextCompat.getColor(this, R.color.transparent));

        mBinding.textViewHistory.setTextColor(
                ContextCompat.getColor(this, R.color.dark_blue));

        mBinding.textViewSettings.setTextColor(
                ContextCompat.getColor(this, R.color.transparent));

        mBinding.imageViewGenerate.setVisibility(View.VISIBLE);
        mBinding.imageViewGenerateActive.setVisibility(View.INVISIBLE);

        mBinding.imageViewScan.setVisibility(View.VISIBLE);
        mBinding.imageViewScanActive.setVisibility(View.INVISIBLE);

        mBinding.imageViewHistory.setVisibility(View.INVISIBLE);
        mBinding.imageViewHistoryActive.setVisibility(View.VISIBLE);

        mBinding.imageViewSettings.setVisibility(View.VISIBLE);
        mBinding.imageViewSettingsActive.setVisibility(View.INVISIBLE);

        setToolbarTitle(getString(R.string.toolbar_title_history));
        mBinding.toolbar.setVisibility(View.VISIBLE);
        showFragment(HistoryFragment.newInstance());
    }
    private void clickOnSettings() {
        mBinding.textViewGenerate.setTextColor(
                ContextCompat.getColor(this, R.color.transparent));

        mBinding.textViewScan.setTextColor(
                ContextCompat.getColor(this, R.color.transparent));

        mBinding.textViewHistory.setTextColor(
                ContextCompat.getColor(this, R.color.transparent));

        mBinding.textViewSettings.setTextColor(
                ContextCompat.getColor(this, R.color.dark_blue));

        mBinding.imageViewGenerate.setVisibility(View.VISIBLE);
        mBinding.imageViewGenerateActive.setVisibility(View.INVISIBLE);

        mBinding.imageViewScan.setVisibility(View.VISIBLE);
        mBinding.imageViewScanActive.setVisibility(View.INVISIBLE);

        mBinding.imageViewHistory.setVisibility(View.VISIBLE);
        mBinding.imageViewHistoryActive.setVisibility(View.INVISIBLE);

        mBinding.imageViewSettings.setVisibility(View.INVISIBLE);
        mBinding.imageViewSettingsActive.setVisibility(View.VISIBLE);

        setToolbarTitle("Settings");
        mBinding.toolbar.setVisibility(View.VISIBLE);
        showFragment(SettingsFragment.newInstance());
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_view_generate:
            case R.id.text_view_generate:
            case R.id.constraint_layout_generate_container:
                clickOnGenerate();
                break;

            case R.id.image_view_scan:
            case R.id.text_view_scan:
            case R.id.constraint_layout_scan_container:
                clickOnScan();
                break;

            case R.id.image_view_history:
            case R.id.text_view_history:
            case R.id.constraint_layout_history_container:
                clickOnHistory();
                break;
            case R.id.image_view_settings:
            case R.id.text_view_settings:
            case R.id.constraint_layout_settings_container:
                clickOnSettings();
                break;
        }
    }

    private void rateUsPopUp() {
        dialogBuilder = new AlertDialog.Builder(this);
        final View contactPopupView = getLayoutInflater().inflate(R.layout.rateuscard,null);

        dialogBuilder.setView(contactPopupView);
        dialog = dialogBuilder.create();
        dialog.show();

        notNow = contactPopupView.findViewById(R.id.card_not_now);
        rateUs = contactPopupView.findViewById(R.id.card_rate_us);
        notNow.setOnClickListener(view -> {
            saveData(HomeActivity.this,"",10);
            super.onBackPressed();
        });
        rateUs.setOnClickListener(view -> {
            saveData(HomeActivity.this,"rateUs",0);
            try{
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=com.bayzid.qrbarcodescanner2022")));
            }catch (ActivityNotFoundException e){
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=com.simplemobiletools.gallery.bayzid")));
//                              https://play.google.com/store/apps/dev?id=6496719373857236664
            }
        });
    }

    private void setToolbarTitle(String title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
    }

    private void showFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.coordinator_layout_fragment_container, fragment,
                fragment.getClass().getSimpleName());
        transaction.commit();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PermissionUtil.REQUEST_CODE_PERMISSION_DEFAULT) {
            boolean isAllowed = true;

            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    isAllowed = false;
                }
            }

            if (isAllowed) {
                clickOnScan();
            }
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        int s = 0;
        int oneClickSaveData = loadDataOneClickPerOpening(HomeActivity.this);
        s = oneClickSaveData-1;
        if (s>=0) {
            saveDataOneClick(HomeActivity.this, s);
            Log.d("CheckSN", String.valueOf(s));
        }
        int n = 0;
        int notNowSaveData = loadDataNotNow(HomeActivity.this);
        n = notNowSaveData-1;
        if (n>=0) {
            saveData(HomeActivity.this, "",n);
            Log.d("CheckSN", String.valueOf(n));
        }
        if(!CheckInternet.isConnectedToInternet(HomeActivity.this)){
            mBinding.networkCheck.setVisibility(View.VISIBLE);
        }else {
            mBinding.networkCheck.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(!CheckInternet.isConnectedToInternet(HomeActivity.this)){
            mBinding.networkCheck.setVisibility(View.VISIBLE);
        }else {
            mBinding.networkCheck.setVisibility(View.GONE);
        }
    }
    public void onBackPressed(){
//        String rateUsSaveData = loadData(HomeActivity.this);
//        int notNowSaveData = loadDataNotNow(HomeActivity.this);
//        int oneClickSaveData = loadDataOneClickPerOpening(HomeActivity.this);
//        if(notNowSaveData>0||rateUsSaveData.equals("rateUs")){
//            if(backPressedTime + 2000>System.currentTimeMillis()){
//                super.onBackPressed();
//                return;
//            }else{
//                Toast.makeText(getBaseContext(),"Press Back Again to Exit",Toast.LENGTH_SHORT).show();
//            }
//            backPressedTime = System.currentTimeMillis();
//        }else if(oneClickSaveData==0){
//            saveDataOneClick(HomeActivity.this,3);
//            rateUsPopUp();
//        }else {
            if(backPressedTime + 2000>System.currentTimeMillis()){
                super.onBackPressed();
                return;
            }else{
                Toast.makeText(getBaseContext(),"Press Back Again to Exit",Toast.LENGTH_SHORT).show();
            }
            backPressedTime = System.currentTimeMillis();
//        }
    }
    public static void saveData(Activity activity, String rateUs, int notNow){
        SharedPreferences sharedPreferences = activity.getSharedPreferences("CheckFirstTime", MODE_PRIVATE);
        checkFirstTime = Boolean.valueOf(sharedPreferences.getBoolean("CheckFirstTime", true));
        if (checkFirstTime.booleanValue()) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
//            editor.putBoolean("CheckFirstTime", false);
            editor.putString("rateUs", rateUs);
            editor.putInt("notNow", notNow);
            editor.apply();
        }
    }
    public static void saveDataOneClick(Activity activity, int oneClickPerOpening){
        SharedPreferences sharedPreferences = activity.getSharedPreferences("CheckFirstTimeOneClick", MODE_PRIVATE);
        checkFirstTime = Boolean.valueOf(sharedPreferences.getBoolean("CheckFirstTime", true));
        if (checkFirstTime.booleanValue()) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("oneClickPerOpening", oneClickPerOpening);
            editor.apply();
        }
    }
    public static String loadData(Activity activity){
        SharedPreferences sharedPreferences = activity.getSharedPreferences("CheckFirstTime", MODE_PRIVATE);
        String key = sharedPreferences.getString("rateUs","");
        return key;
    }
    public static int loadDataNotNow(Activity activity){
        SharedPreferences sharedPreferences = activity.getSharedPreferences("CheckFirstTime", MODE_PRIVATE);
        int key = sharedPreferences.getInt("notNow",0);
        return key;
    }
    public static int loadDataOneClickPerOpening(Activity activity){
        SharedPreferences sharedPreferences = activity.getSharedPreferences("CheckFirstTimeOneClick", MODE_PRIVATE);
        int key = sharedPreferences.getInt("oneClickPerOpening",0);
        return key;
    }
}
