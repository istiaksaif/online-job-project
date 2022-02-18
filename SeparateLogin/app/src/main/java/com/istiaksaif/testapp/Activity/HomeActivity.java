package com.istiaksaif.testapp.Activity;

import static com.istiaksaif.testapp.Constants.TYPE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.istiaksaif.testapp.Adapter.DealerAdapter;
import com.istiaksaif.testapp.Adapter.TabViewPagerAdapter;
import com.istiaksaif.testapp.Apiutil;
import com.istiaksaif.testapp.Fragment.HomeFragment;
import com.istiaksaif.testapp.Fragment.ProductFragment;
import com.istiaksaif.testapp.Model.DealerItem;
import com.istiaksaif.testapp.NotificationData;
import com.istiaksaif.testapp.PushNotification;
import com.istiaksaif.testapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private long backPressedTime;
    private FirebaseAuth firebaseAuth;

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private NavigationView navigationView;
    
    private TabLayout tabLayout;
    private ViewPager tabviewPager;

    private TextInputLayout companyLayout;
    private TextInputEditText division;
    private MaterialAutoCompleteTextView companyName;
    private MaterialButton submitButton,addButton;
    RelativeLayout buttonLayout;
    private TextView newCompany;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private EditText newcompanyname;
    private DatabaseReference dealerDatabaseRef,dd;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String uid = user.getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        FirebaseMessaging.getInstance().subscribeToTopic(TYPE);
        firebaseAuth = FirebaseAuth.getInstance();
        dealerDatabaseRef = FirebaseDatabase.getInstance().getReference();
        dd = FirebaseDatabase.getInstance().getReference();

        toolbar = findViewById(R.id.drawertoolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.drawernavview);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open,R.string.close);
        actionBarDrawerToggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.dark));
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        companyName = findViewById(R.id.companyName);
        division = findViewById(R.id.division);
        companyLayout = findViewById(R.id.companyLayout);
        submitButton = findViewById(R.id.submit);
        buttonLayout = findViewById(R.id.buttonLayout);
        newCompany = findViewById(R.id.newCompany);
        newCompany.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createpopupDiaglog();
            }
        });
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Info();
                String c = companyName.getText().toString();
                String d = division.getText().toString();
                if(!c.isEmpty()){
                    DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("Notification");
                    database.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                                String city = dataSnapshot.child("city").getValue(String.class);
                                String usersid = dataSnapshot.child("uid").getValue(String.class);
                                getToken(c,d,usersid);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                //getToken(companyName.getText().toString(), uid);
            }
        });
        Query query = dealerDatabaseRef.child("SeparateLogin").child("New-Companies").orderByChild("userId").equalTo(uid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    try{
                        String companyname = dataSnapshot.child("companyName").getValue().toString();
                        String dv = dataSnapshot.child("division").getValue().toString();
                        String NewCompaniesId = dataSnapshot.child("NewCompaniesId").getValue().toString();
                        String limit = dataSnapshot.child("limit").getValue().toString();

                        companyName.setText(companyname);
                        division.setText(dv);

                        if(companyName.getText().toString().equals("") && division.getText().toString().equals("")){
                            buttonLayout.setVisibility(View.VISIBLE);
                        }else {
                            buttonLayout.setVisibility(View.GONE);
                            newCompany.setVisibility(View.GONE);
                            companyLayout.setEnabled(false);
                            companyLayout.setEndIconVisible(false);
                            companyName.setFocusable(false);
                            division.setClickable(false);
                            division.setFocusable(false);
                            division.setFocusableInTouchMode(false);
                        }
                    }catch (Exception e){

                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HomeActivity.this,"Some Thing Wrong", Toast.LENGTH_SHORT).show();
            }
        });

        Query query1 = dealerDatabaseRef.child("Companies");
        query1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> companyList = new ArrayList<>();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()) {
                    try {
                        String provinceName = dataSnapshot.child("name").getValue(String.class);

                        companyList.add(provinceName);
                    }catch (Exception e){

                    }
                }
                ArrayAdapter<String> arrayAdapterState = new ArrayAdapter<>(HomeActivity.this,R.layout.usertype_item,companyList);
                ((MaterialAutoCompleteTextView) companyLayout.getEditText()).setAdapter(arrayAdapterState);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HomeActivity.this,"Some Thing Wrong", Toast.LENGTH_SHORT).show();
            }
        });

        tabLayout = (TabLayout)findViewById(R.id.tab);
        tabviewPager = (ViewPager)findViewById(R.id.tabviewpager);
        TabViewPagerAdapter tabViewPagerAdapter = new TabViewPagerAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        tabViewPagerAdapter.AddFragment(new HomeFragment(),"My Dealer");
        tabViewPagerAdapter.AddFragment(new ProductFragment(),"Product");
        tabviewPager.setAdapter(tabViewPagerAdapter);
        tabviewPager.setCurrentItem(0);
        tabLayout.setupWithViewPager(tabviewPager);

    }

//    private void sendNotification(PushNotification notification) {
//        Apiutil.getClient().sendNotification(notification).enqueue(new Callback<PushNotification>() {
//            @Override
//            public void onResponse(Call<PushNotification> call, Response<PushNotification> response) {
//                if(response.isSuccessful()){
//                    Toast.makeText(HomeActivity.this, "success", Toast.LENGTH_SHORT).show();
//                }
//                else {
//                    Toast.makeText(HomeActivity.this, "error", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<PushNotification> call, Throwable t) {
//                Toast.makeText(HomeActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

//    private void getToken(String message,String title, String hisID) {
//        DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("SeparateLogin").child("usersData").child(hisID);
//        database.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if(snapshot.exists()) {
//                    String token = snapshot.child("token").getValue().toString();
//                    newpart(token,message,title,hisID);
//                }else {
//                    DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("SeparateLogin").child("userdata").child(hisID);
//                    database.addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot snapshot) {
//                            if(snapshot.exists()) {
//                                String token = snapshot.child("token").getValue().toString();
//                                newpart(token,message,title,hisID);
//                            }
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError error) {
//
//                        }
//                    });
//                }
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }
private void getToken(String message,String title, String hisID) {
    DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("SeparateLogin").child("usersData").child(hisID);
    database.addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            if(snapshot.exists()) {
                String token = snapshot.child("token").getValue().toString();
                newpart(token,message,title,hisID);
            }else {
                DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("Users").child("normal").child(hisID);
                database.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()) {
                            String token = snapshot.child("token").getValue().toString();
                            newpart(token,message,title,hisID);
                        }else {
                            DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("SeparateLogin").child("userdata").child(hisID);
                            database.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.exists()) {
                                        String token = snapshot.child("token").getValue().toString();
                                        newpart(token,message,title,hisID);
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

        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    });
}
    private void newpart(String token, String message, String title, String hisID) {
        JSONObject to = new JSONObject();
        JSONObject data = new JSONObject();
        try {
            data.put("title", title);
            data.put("message", message);
            data.put("hisID", hisID);

            to.put("to", token);
            to.put("data", data);

            sendNotification(to);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void sendNotification(JSONObject to) {

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, "https://fcm.googleapis.com/fcm/send", to, response -> {
            Log.d("notification", "sendNotification: " + response);
        }, error -> {
            Log.d("notification", "sendNotification: " + error);
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String> map = new HashMap<>();
                map.put("Authorization", "key=" + "AAAA6a5D-3A:APA91bFrJghKny1lk4qgLFl9hRLTpDh6-YL48QK8OiIQA4W3VJn2ySok_pVjt_JpIbdrkV7mEbsFE99Sk_ztAIxVu7rHhW96ivmQZE0x5v5MjodD5334pwStuPZpo-xZmUeXkI6aby-7");
                map.put("Content-Type", "application/json");
                return map;
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        request.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(request);
    }

    public void createpopupDiaglog(){
        dialogBuilder = new AlertDialog.Builder(this);
        final View contactPopupView = getLayoutInflater().inflate(R.layout.addcompany,null);
        newcompanyname = (EditText)contactPopupView.findViewById(R.id.newcompanyname);
        addButton = (MaterialButton) contactPopupView.findViewById(R.id.addbtn);

        dialogBuilder.setView(contactPopupView);
        dialog = dialogBuilder.create();
        dialog.show();

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, Object> result = new HashMap<>();
                result.put("name", newcompanyname.getText().toString());

                dealerDatabaseRef.child("Companies").addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String key = dealerDatabaseRef.push().getKey();
                                dealerDatabaseRef.child("Companies").child(key).updateChildren(result);
                                Toast.makeText(HomeActivity.this,"Added Company Successfully!", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
            }
        });
    }
    private void Info() {
        String CompanyName = companyName.getText().toString();
        String Division = division.getText().toString();

        if (TextUtils.isEmpty(CompanyName)){
            Toast.makeText(HomeActivity.this, "please enter your Designation", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (TextUtils.isEmpty(Division)){
            Toast.makeText(HomeActivity.this, "please enter workingIn", Toast.LENGTH_SHORT).show();
            return;
        }

        String key = dealerDatabaseRef.child("SeparateLogin").child("New-Companies").push().getKey();
        HashMap<String, Object> result = new HashMap<>();
        result.put("companyName", companyName.getText().toString());
        result.put("division", division.getText().toString());
        result.put("userId", uid);
        result.put("NewCompaniesId", key);
        result.put("limit","2");
        result.put("limitp","2");

        dealerDatabaseRef.child("SeparateLogin").child("New-Companies").child(key).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        dealerDatabaseRef.child("SeparateLogin").child("New-Companies").child(key).updateChildren(result);
                        Toast.makeText(HomeActivity.this,"Submitted Successfully!", Toast.LENGTH_SHORT).show();
                        String key1 = dd.child("Notification").push().getKey();
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("text", "feed message");
                        map.put("uid", uid);
                        map.put("id",key1);
                        dd.child("Notification").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot1) {
                                if(snapshot1.exists()) {
                                    for(DataSnapshot dataSnapshot:snapshot1.getChildren()){
                                        String match = dataSnapshot.child("uid").getValue(String.class);
                                        String id = dataSnapshot.child("id").getValue(String.class);
                                        if(match.equals(uid)){
                                            dd.child("Notification").child(id).removeValue();
                                            dd.child("Notification").child(key1).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    dd.child("Notification").child(key1).updateChildren(map);
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                        }else {
                                            dd.child("Notification").child(key1).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    dd.child("Notification").child(key1).updateChildren(map);
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                        }
                                    }
                                }else {
                                    dd.child("Notification").child(key1).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            dd.child("Notification").child(key1).updateChildren(map);
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

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        }
    public void onBackPressed(){
        if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        if(backPressedTime + 2000>System.currentTimeMillis()){
            super.onBackPressed();
            return;
        }else{
            Toast.makeText(getBaseContext(),"Press Back Again to Exit",Toast.LENGTH_SHORT).show();
        }
        backPressedTime = System.currentTimeMillis();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.icProfile:
                Intent intent = new Intent(this,ProfileActivity.class);
                startActivity(intent);
                break;
            case R.id.icFeedback:
                Intent intent_feed = new Intent(this,FeedBackActivity.class);
                startActivity(intent_feed);
                break;
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                Intent intent1 = new Intent(this,LogInActivity.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent1);
                finish();
                break;
        }
        return false;
    }
}