package com.istiaksaif.highlymotavated.Activity;

import static com.istiaksaif.highlymotavated.Activity.SplashScreenActivity.loadData;
import static com.istiaksaif.highlymotavated.Activity.SplashScreenActivity.loadDataReferUserId;
import static com.istiaksaif.highlymotavated.Receiver.NotificationHelper.getToken;
import static com.istiaksaif.highlymotavated.Utils.GetServerTimeContext.getCurrentDate;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.istiaksaif.highlymotavated.Fragment.HomeFragment;
import com.istiaksaif.highlymotavated.R;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class UserHomeActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private long backPressedTime;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private TextView appVersion,storekey,storeimg,n,t,balancestore,name,address,phone;
    private ImageView search,i;

    private GoogleSignInClient googleSignInClient;
    private DatabaseReference databaseReference;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String uid = user.getUid();
    private View hView;

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private LottieAnimationView coinDrop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportFragmentManager().beginTransaction().add(R.id.container,new HomeFragment()).commit();

        databaseReference = FirebaseDatabase.getInstance().getReference();

        storekey = findViewById(R.id.keystore);
        storeimg = findViewById(R.id.imgstore);
        balancestore = findViewById(R.id.balancestore);
        search = findViewById(R.id.search);
        phone = findViewById(R.id.phonestore);
        address = findViewById(R.id.addressstore);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(UserHomeActivity.this, SearchActivity.class);
                startActivity(intent1);
            }
        });
        //appDrawer

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.drawer_menu);
        hView = navigationView.getHeaderView(0);


        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        actionBarDrawerToggle.setDrawerIndicatorEnabled(false);
        actionBarDrawerToggle.setHomeAsUpIndicator(R.drawable.ic_menu);

        actionBarDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        i = hView.findViewById(R.id.profileimage);
        n = hView.findViewById(R.id.name);
        t = hView.findViewById(R.id.title);

        hView.findViewById(R.id.cross).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });
        GetDataFromFirebase();
        checkReward();
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.profile) {
                    Intent i = new Intent(UserHomeActivity.this, ProfileActivity.class);
                    i.putExtra("key",storekey.getText().toString());
                    i.putExtra("imageUrl",storeimg.getText().toString());
                    i.putExtra("balance",balancestore.getText().toString());
                    startActivity(i);
                }
                if (id == R.id.post){
                    Intent i = new Intent(UserHomeActivity.this, MyPostActivity.class);
                    startActivity(i);
                }
                if (id == R.id.addproduct) {
                    Intent a = new Intent(UserHomeActivity.this, AddProductActivity.class);
                    startActivity(a);
                }
                if (id == R.id.bid) {
                    Intent a = new Intent(UserHomeActivity.this, BidHistoryActivity.class);
                    startActivity(a);
                }
                if (id == R.id.cart) {
                    Intent aa = new Intent(UserHomeActivity.this, CartActivity.class);
                    aa.putExtra("name",n.getText().toString());
                    aa.putExtra("phone",phone.getText().toString());
                    aa.putExtra("address",address.getText().toString());
                    startActivity(aa);
                }
                if (id == R.id.notification) {
                    Intent as = new Intent(UserHomeActivity.this, NotificationActivity.class);
                    startActivity(as);
                }
                if (id == R.id.refer) {
                    Intent i = new Intent(UserHomeActivity.this, ReferActivity.class);
                    i.putExtra("key",storekey.getText().toString());
                    startActivity(i);
                }
                if (id == R.id.logout) {
                    signOut();
                }

                return false;
            }
        });

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail().build();
        googleSignInClient = GoogleSignIn.getClient(this,googleSignInOptions);

        //version on menu
        appVersion = findViewById(R.id.app_version);
        PackageManager manager = getApplication().getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(
                    getApplication().getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String version = info.versionName;
        appVersion.setText("Version "+version);
    }
    public void setItemVisible(boolean visible,boolean cartVisible){
        NavigationView navigationView = (NavigationView) findViewById(R.id.drawer_menu);
        toggleVisibility(navigationView.getMenu(), R.id.post, R.id.addproduct, R.id.cart, visible, cartVisible);
    }

    private void toggleVisibility(Menu menu, @IdRes int id, @IdRes int id1, @IdRes int id2, boolean visible, boolean cartVisible){
        menu.findItem(id).setVisible(visible);
        menu.findItem(id1).setVisible(visible);
        menu.findItem(id2).setVisible(cartVisible);
    }
    @Override
    protected void onResume() {
        super.onResume();
        checkStatus("online");
    }
    @Override
    protected void onPause() {
        super.onPause();
        checkStatus("offline");
    }

    private void checkStatus(String status){
        HashMap<String, Object> result = new HashMap<>();
        result.put("status",status);

        databaseReference.child("users").child(uid).updateChildren(result);
    }
    private void giveReward(String token,String key,String referUserId){
        try {
            HashMap<String, Object> result = new HashMap<>();
            result.put("userId",uid);
            result.put("phoneToken",token);

            databaseReference.child("Reward").child(token).updateChildren(result);
            databaseReference.child("Reward").child(uid).updateChildren(result);
            databaseReference.child("users").child(uid)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String k = snapshot.child("key").getValue().toString();
                            databaseReference.child("usersData").child(k).addListenerForSingleValueEvent(
                                    new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            try {
                                                int balance = 0;
                                                String s = snapshot.child("balanceTk").getValue().toString();
                                                balance = (Integer.parseInt(s.trim()))+(1);

                                                HashMap<String, Object> result1 = new HashMap<>();
                                                result1.put("balanceTk", balance);
                                                databaseReference.child("usersData").
                                                        child(k).updateChildren(result1);

                                            }catch (Exception e){

                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Toast.makeText(UserHomeActivity.this,
                                                    "Something Wrong ", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
            databaseReference.child("usersData").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try {
                        int balance = 0, referCount=0;
                        String s = snapshot.child("balanceTk").getValue().toString();
//                                        String ss = snapshot.child("referCount").getValue(String.class);
                        balance = (Integer.parseInt(s.trim())) + (1);
//                                        referCount = (Integer.parseInt(ss.trim())) + (1);

                        HashMap<String, Object> result1 = new HashMap<>();
                        result1.put("balanceTk", balance);
//                                        result1.put("referCount", referCount);
                        databaseReference.child("usersData").
                                child(key).updateChildren(result1);
                        String message = "You rewarded $1 for join "+n.getText().toString()+" using your referral link";
                        String stitle = "Get Reward form Referral";
                        String notifyId = databaseReference.child("Notification").push().getKey();
                        HashMap<String, Object> resultNotify = new HashMap<>();
                        resultNotify.put("message",message);
                        resultNotify.put("title",stitle);
                        resultNotify.put("timestamp",getCurrentDate());
                        resultNotify.put("notifyId",notifyId);
                        resultNotify.put("userId",referUserId);
                        databaseReference.child("Notification").child(notifyId).updateChildren(resultNotify);
                        getToken(message,stitle,referUserId,getApplicationContext());
                    }catch (Exception e) {

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    dialog.dismiss();
                }
            }, 5000);
        }catch (Exception e){

        }
    }

    private void checkReward(){
        String token = Settings.Secure.getString(UserHomeActivity.this.getContentResolver(),Settings.Secure.ANDROID_ID);
        String key = loadData(UserHomeActivity.this);
        String referUserId = loadDataReferUserId(UserHomeActivity.this);
        if(key.equals("")){
            HashMap<String, Object> result = new HashMap<>();
            result.put("userId",uid);
            result.put("phoneToken",token);
            databaseReference.child("Reward").child(token).updateChildren(result);
            databaseReference.child("Reward").child(uid).updateChildren(result);
        }else if(!referUserId.equals(uid)){
            databaseReference.child("Reward").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(!dataSnapshot.child(token).exists()){
                        if(!dataSnapshot.child(uid).exists()){
                            try {
                                dialogBuilder = new AlertDialog.Builder(UserHomeActivity.this);
                                final View contactPopupView = getLayoutInflater().inflate(R.layout.reward,null);
                                coinDrop = (LottieAnimationView)contactPopupView. findViewById(R.id.coindrop);

                                coinDrop.setVisibility(View.VISIBLE);
                                coinDrop.setAnimation(R.raw.coinsdrop);
                                coinDrop.loop(false);
                                coinDrop.playAnimation();

                                dialogBuilder.setView(contactPopupView);
                                dialog = dialogBuilder.create();
                                dialog.show();
                                giveReward(token,key,referUserId);
                            }catch (Exception e){

                            }
                        }else if(dataSnapshot.child(uid).exists()){
                            try {
                                HashMap<String, Object> result = new HashMap<>();
                                result.put("userId",uid);
                                result.put("phoneToken",token);
                                databaseReference.child("Reward").child(token).updateChildren(result);
                            }catch (Exception e){

                            }
                        }
                    }else if(dataSnapshot.child(token).exists()){
                        try {
                            HashMap<String, Object> result = new HashMap<>();
                            result.put("userId",uid);
                            result.put("phoneToken",token);
                            databaseReference.child("Reward").child(uid).updateChildren(result);
                        }catch (Exception e){

                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }else if(referUserId.equals(uid)){
            HashMap<String, Object> result = new HashMap<>();
            result.put("userId",uid);
            result.put("phoneToken",token);
            databaseReference.child("Reward").child(token).updateChildren(result);
            databaseReference.child("Reward").child(uid).updateChildren(result);
        }
    }

    private void GetDataFromFirebase() {
        Query query = databaseReference.child("users").orderByChild("userId").equalTo(uid);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren()) {
                    String key = dataSnapshot.child("key").getValue().toString();
                    storekey.setText(key);
                    databaseReference.child("usersData").child(key)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {
                                    String retrivename = ""+dataSnapshot2.child("name").getValue();
                                    String retrivetitle = ""+dataSnapshot2.child("title").getValue();
                                    String img = dataSnapshot2.child("imageUrl").getValue().toString();
                                    String balance = dataSnapshot2.child("balanceTk").getValue().toString();
                                    storeimg.setText(img);
                                    balancestore.setText(balance);
                                    phone.setText(dataSnapshot2.child("phone").getValue().toString());
                                    address.setText(dataSnapshot2.child("address").getValue().toString());
                                    String storeUser = dataSnapshot2.child("isUser").getValue().toString();
                                    if (storeUser.equals("User")) {
                                        setItemVisible(false,true);
                                    }else if (storeUser.equals("Admin")){
                                        setItemVisible(true,false);
                                    }
                                    n.setText(retrivename);
                                    t.setText(retrivetitle);
//                                    try {
//                                        Picasso.get().load(img).resize(320,320).into(profileimage);
//                                    }catch (Exception e){
//                                        Picasso.get().load(R.drawable.dropdown).into(profileimage);
//                                    }
                                    try {
                                        Picasso.get().load(img).resize(480,480).into(i);
                                    }catch (Exception e){
                                        Picasso.get().load(R.drawable.dropdown).into(i);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case android.R.id.home:
                finish();
        }return true;
    }

    private void signOut(){
        FirebaseAuth.getInstance().signOut();
        googleSignInClient.signOut();
        Intent intent1 = new Intent(this, LogInActivity.class);
        intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent1);
        finish();
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