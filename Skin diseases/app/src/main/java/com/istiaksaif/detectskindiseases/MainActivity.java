package com.istiaksaif.detectskindiseases;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager tabviewPager;
    private Toolbar toolbar;

    private long backPressedTime;
    private DrawerLayout drawerLayout;
//    private LottieAnimationView cross;
    private CardView cardView;
    private LinearLayout logoutButton;

    private TextView appVersion;

//    private GoogleSignInClient googleSignInClient;
//    private DatabaseReference databaseReference;
//    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//    private String uid = user.getUid();

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
//    private LottieAnimationView coinDrop,rewardOpen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tabLayout = (TabLayout)findViewById(R.id.tab);
        tabviewPager = (ViewPager)findViewById(R.id.tabviewpager);
        TabViewPagerAdapter tabViewPagerAdapter = new TabViewPagerAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
//        tabViewPagerAdapter.AddFragment(new HomeFragment(),null);
//        tabViewPagerAdapter.AddFragment(new ProfileFragment(),"Profile");
        tabViewPagerAdapter.AddFragment(new HomeFragment(),null);
        tabviewPager.setAdapter(tabViewPagerAdapter);
        tabviewPager.setCurrentItem(3);
        tabLayout.setupWithViewPager(tabviewPager);


//        tabLayout.getTabAt(0).setIcon(R.drawable.profile);
        tabLayout.getTabAt(0).setIcon(R.drawable.home);
//        tabLayout.getTabAt(1).setIcon(R.drawable.community_chat);
//        tabLayout.getTabAt(0).setIcon(R.drawable.menu);

        //appDrawer

//        drawerLayout = findViewById(R.id.drawer_layout);
//        cardView = findViewById(R.id.drawercard);
//        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open,R.string.close);
//        drawerLayout.addDrawerListener(actionBarDrawerToggle);
//        actionBarDrawerToggle.syncState();
//        actionBarDrawerToggle.setDrawerIndicatorEnabled(false);
//        actionBarDrawerToggle.setHomeAsUpIndicator(R.drawable.menu);

//        actionBarDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (drawerLayout.isDrawerVisible(GravityCompat.END)) {
//                    cross = (LottieAnimationView)findViewById(R.id.cross);
//                    cross.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            drawerLayout.closeDrawer(GravityCompat.END);
//                        }
//                    });
//                } else {
//                    drawerLayout.openDrawer(GravityCompat.END);
//                }
//            }
//        });

        //drawerMenus

//        logoutButton = findViewById(R.id.logout);
//        logoutButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                switch (v.getId()){
//                    case R.id.logout:
//                        signOut();
//                        break;
//                }
//            }
//        });

        //version on menu
//        appVersion = findViewById(R.id.app_version);
//        PackageManager manager = getApplication().getPackageManager();
//        PackageInfo info = null;
//        try {
//            info = manager.getPackageInfo(
//                    getApplication().getPackageName(), 0);
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//        }
//        String version = info.versionName;
//        appVersion.setText("Version "+version);
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        checkStatus("online");
//    }
//    @Override
//    protected void onPause() {
//        super.onPause();
//        checkStatus("offline");
//    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case android.R.id.home:
                finish();
        }return true;
    }

    public void onBackPressed(){
        if(backPressedTime + 2000>System.currentTimeMillis()){
            super.onBackPressed();
            return;
        }else{
            Toast.makeText(getBaseContext(),"Press Back Again to Exit",Toast.LENGTH_SHORT).show();
        }
        backPressedTime = System.currentTimeMillis();
    }
}