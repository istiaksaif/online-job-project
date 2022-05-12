package com.rtapps.moc;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.WanderingCubes;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.rtapps.moc.Adapter.FoodItemAdapter;
import com.rtapps.moc.Model.Item;

import java.io.File;
import java.util.ArrayList;

public class DashboardActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher {
    RelativeLayout uploadItemViewBtn;
    RecyclerView foodItemRecyclerView;
    ImageView logoutBtn;
    String userId;
    String shareName, shareExpDate, shareCatlogNumber;
    EditText searchItemsEdittext;
    ArrayList<Item> items;
    RecyclerView.LayoutManager layoutManager;
    FoodItemAdapter foodItemAdapter;
    FirebaseDatabase firebaseDatabase;
    ProgressBar progressBar;
    File localFile;
    ImageView archiveBtn;
    RelativeLayout progressBarLayout;
    ImageView sellItemBtn;
    TextView vegetable, fruit, milk, fast, meat, clearfilter;
    TextView movieShow, shopping, restaruantsFastFood, other;
    DrawerLayout drawerLayout;
    ImageButton menubtn;
    NavigationView navigationView;
    String vegCheck, fruitCheck, milkCheck, fastCheck, meatCheck = "true";
    // creating a variable for our
    // Database Reference for Firebase.
    private DatabaseReference ProductsRef;
    private DatabaseReference UpdateRef;

    @Override
    protected void onStart() {
        super.onStart();
        LanguageSet.setLocale(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_dashboard);
        drawerLayout = findViewById(R.id.drawerlayout);
        menubtn = findViewById(R.id.menubtn);
        navigationView = findViewById(R.id.drawer_menu);

        menubtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.upload_screen_btn) {
                    Intent i = new Intent(DashboardActivity.this, UploadMenuItemsActivity.class);
                    i.putExtra("UserId", userId);
                    startActivity(i);
                }
                if (id == R.id.sale_items_btn) {
                    Intent a = new Intent(DashboardActivity.this, DisplaySellItemsActivity.class);
                    a.putExtra("UserId", userId);
                    startActivity(a);

                }
                if (id == R.id.archive_items_btn) {
                    Intent as = new Intent(DashboardActivity.this, MainActivity.class);
                    as.putExtra("UserId", userId);
                    startActivity(as);
                }
                if (id == R.id.language) {
                    //TODO Dialog
                    dialogLanguage(DashboardActivity.this);

                }
                if (id == R.id.logout) {
                    FirebaseAuth.getInstance().signOut();
                    Intent a = new Intent(DashboardActivity.this, EmailLoginActivity.class);
                    startActivity(a);
                    finish();
                }

                return false;
            }
        });
        View hView = navigationView.getHeaderView(0);
        hView.findViewById(R.id.closedrawer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });


        vegetable = findViewById(R.id.vegetable);
        fruit = findViewById(R.id.fruit);
        milk = findViewById(R.id.milk);
        fast = findViewById(R.id.fast);
        meat = findViewById(R.id.meat);

        movieShow = findViewById(R.id.movies);
        shopping = findViewById(R.id.shoping);
        restaruantsFastFood = findViewById(R.id.restuarent);
        other = findViewById(R.id.other);


//        getWindow().addFlags(SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
//        FOR WHITE bELOW
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setStatusBarColor(DashboardActivity.this.getResources().getColor(R.color.background));
        init();
//        populateFoodItems();

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        firebaseDatabase = FirebaseDatabase.getInstance();

        // below line is used to get
        // reference for our database.

        foodItemRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(DashboardActivity.this);
        foodItemRecyclerView.setLayoutManager(layoutManager);
        ProductsRef = FirebaseDatabase.getInstance().getReference().child("Food").child(userId);
        progressBar = findViewById(R.id.spin_kit);
        Sprite rotatingCircle = new WanderingCubes();


        initRecyclerView();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS}, 44);
        }

//        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_SMS},44);


        loadBanner();
    }


    private void loadBanner(){

        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    private void initRecyclerView() {

        foodItemAdapter = new FoodItemAdapter(this, new ArrayList<>());
        foodItemRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        foodItemRecyclerView.setAdapter(foodItemAdapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        getdata();
    }


    private void getdata() {
        ProductsRef.orderByChild("isArchive").equalTo("false").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot != null) {

                    items = new ArrayList<>();
                    progressBarLayout.setVisibility(View.GONE);

                    for (DataSnapshot nodeSnapshot : snapshot.getChildren()) {
                        Log.d("", "");

                        Item item = new Item();

                        item.setID(nodeSnapshot.getKey());
                        item.setName(nodeSnapshot.child("Name").getValue().toString());
                        item.setExpDate(nodeSnapshot.child("ExpDate").getValue().toString());
                        item.setCatalogNumber(nodeSnapshot.child("CatalogNumber").getValue().toString());
//                        item.setPrice(nodeSnapshot.child("Price").getValue().toString());
                         if (nodeSnapshot.child("Price").exists()){
                            item.setPrice(nodeSnapshot.child("Price").getValue().toString());
                        }
                        if (nodeSnapshot.child("Provider").exists()){
                            item.setPrice(nodeSnapshot.child("Provider").getValue().toString());
                        }
                        item.setCategory(nodeSnapshot.child("Category").getValue().toString());
                        item.setImage(nodeSnapshot.child("image").getValue() == null ? ""
                                : nodeSnapshot.child("image").getValue().toString());

                        items.add(item);
                        shareExpDate = item.getExpDate();
                        shareName = item.getName();
                        shareCatlogNumber = item.getCatalogNumber();

                    }

                    Log.d("tag", "populating data");
                    if (foodItemAdapter != null)
                        foodItemAdapter.updateItems(items);

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void init() {
        foodItemRecyclerView = findViewById(R.id.food_item_recycler);
//        uploadItemViewBtn = findViewById(R.id.upload_screen_btn);
//        uploadItemViewBtn.setOnClickListener(this);
//        logoutBtn = findViewById(R.id.logout_btn);
//        logoutBtn.setOnClickListener(this);
        searchItemsEdittext = findViewById(R.id.search_items_textview);

        searchItemsEdittext.addTextChangedListener(this);
//        archiveBtn=findViewById(R.id.archive_items_btn);
//        archiveBtn.setOnClickListener(this);
//        sellItemBtn = findViewById(R.id.sale_items_btn);
//        sellItemBtn.setOnClickListener(this);

        progressBarLayout = findViewById(R.id.progress_bar_layout_dashboard);
        progressBarLayout.setOnClickListener(this);

        vegetable.setOnClickListener(this);
        fruit.setOnClickListener(this);
        meat.setOnClickListener(this);
        fast.setOnClickListener(this);
        milk.setOnClickListener(this);

        movieShow.setOnClickListener(this);
        restaruantsFastFood.setOnClickListener(this);
        shopping.setOnClickListener(this);
        other.setOnClickListener(this);
    }

    private void populateFoodItems(ArrayList<Item> items) {


    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.vegetable:

                Log.i("textcolor", "onClick: " + vegetable.getCurrentTextColor());

                if (vegetable.getCurrentTextColor() == -16777216) {
                    filterfood("vegetables", "");
                    DrawableCompat.setTint(vegetable.getBackground(), ContextCompat.getColor(DashboardActivity.this, R.color.blueshape));
                    vegetable.setTextColor(getColor(R.color.white));
                    DrawableCompat.setTint(fruit.getBackground(), ContextCompat.getColor(DashboardActivity.this, R.color.textback));
                    fruit.setTextColor(getColor(R.color.black));
                    DrawableCompat.setTint(meat.getBackground(), ContextCompat.getColor(DashboardActivity.this, R.color.textback));
                    meat.setTextColor(getColor(R.color.black));
                    DrawableCompat.setTint(milk.getBackground(), ContextCompat.getColor(DashboardActivity.this, R.color.textback));
                    milk.setTextColor(getColor(R.color.black));
                    DrawableCompat.setTint(fast.getBackground(), ContextCompat.getColor(DashboardActivity.this, R.color.textback));
                    fast.setTextColor(getColor(R.color.black));
                } else {
                    getdata();
                    DrawableCompat.setTint(vegetable.getBackground(), ContextCompat.getColor(DashboardActivity.this, R.color.textback));
                    vegetable.setTextColor(getColor(R.color.black));
                    DrawableCompat.setTint(fruit.getBackground(), ContextCompat.getColor(DashboardActivity.this, R.color.textback));
                    fruit.setTextColor(getColor(R.color.black));
                    DrawableCompat.setTint(meat.getBackground(), ContextCompat.getColor(DashboardActivity.this, R.color.textback));
                    meat.setTextColor(getColor(R.color.black));
                    DrawableCompat.setTint(milk.getBackground(), ContextCompat.getColor(DashboardActivity.this, R.color.textback));
                    milk.setTextColor(getColor(R.color.black));
                    DrawableCompat.setTint(fast.getBackground(), ContextCompat.getColor(DashboardActivity.this, R.color.textback));
                    fast.setTextColor(getColor(R.color.black));

                }
                break;
            case R.id.fruit:
                if (fruit.getCurrentTextColor() == -16777216) {
                    filterfood("fruit", "");
                    DrawableCompat.setTint(vegetable.getBackground(), ContextCompat.getColor(DashboardActivity.this, R.color.textback));
                    vegetable.setTextColor(getColor(R.color.black));
                    DrawableCompat.setTint(fruit.getBackground(), ContextCompat.getColor(DashboardActivity.this, R.color.blueshape));
                    fruit.setTextColor(getColor(R.color.white));
                    DrawableCompat.setTint(meat.getBackground(), ContextCompat.getColor(DashboardActivity.this, R.color.textback));
                    meat.setTextColor(getColor(R.color.black));
                    DrawableCompat.setTint(milk.getBackground(), ContextCompat.getColor(DashboardActivity.this, R.color.textback));
                    milk.setTextColor(getColor(R.color.black));
                    DrawableCompat.setTint(fast.getBackground(), ContextCompat.getColor(DashboardActivity.this, R.color.textback));
                    fast.setTextColor(getColor(R.color.black));
                } else {

                    getdata();
                    DrawableCompat.setTint(vegetable.getBackground(), ContextCompat.getColor(DashboardActivity.this, R.color.textback));
                    vegetable.setTextColor(getColor(R.color.black));
                    DrawableCompat.setTint(fruit.getBackground(), ContextCompat.getColor(DashboardActivity.this, R.color.textback));
                    fruit.setTextColor(getColor(R.color.black));
                    DrawableCompat.setTint(meat.getBackground(), ContextCompat.getColor(DashboardActivity.this, R.color.textback));
                    meat.setTextColor(getColor(R.color.black));
                    DrawableCompat.setTint(milk.getBackground(), ContextCompat.getColor(DashboardActivity.this, R.color.textback));
                    milk.setTextColor(getColor(R.color.black));
                    DrawableCompat.setTint(fast.getBackground(), ContextCompat.getColor(DashboardActivity.this, R.color.textback));
                    fast.setTextColor(getColor(R.color.black));
                }


                break;
            case R.id.meat:
                if (meat.getCurrentTextColor() == -16777216) {
                    filterfood("meat", "");
                    DrawableCompat.setTint(vegetable.getBackground(), ContextCompat.getColor(DashboardActivity.this, R.color.textback));
                    vegetable.setTextColor(getColor(R.color.black));
                    DrawableCompat.setTint(fruit.getBackground(), ContextCompat.getColor(DashboardActivity.this, R.color.textback));
                    fruit.setTextColor(getColor(R.color.black));
                    DrawableCompat.setTint(meat.getBackground(), ContextCompat.getColor(DashboardActivity.this, R.color.blueshape));
                    meat.setTextColor(getColor(R.color.white));
                    DrawableCompat.setTint(milk.getBackground(), ContextCompat.getColor(DashboardActivity.this, R.color.textback));
                    milk.setTextColor(getColor(R.color.black));
                    DrawableCompat.setTint(fast.getBackground(), ContextCompat.getColor(DashboardActivity.this, R.color.textback));
                    fast.setTextColor(getColor(R.color.black));
                } else {
                    getdata();
                    DrawableCompat.setTint(vegetable.getBackground(), ContextCompat.getColor(DashboardActivity.this, R.color.textback));
                    vegetable.setTextColor(getColor(R.color.black));
                    DrawableCompat.setTint(fruit.getBackground(), ContextCompat.getColor(DashboardActivity.this, R.color.textback));
                    fruit.setTextColor(getColor(R.color.black));
                    DrawableCompat.setTint(meat.getBackground(), ContextCompat.getColor(DashboardActivity.this, R.color.textback));
                    meat.setTextColor(getColor(R.color.black));
                    DrawableCompat.setTint(milk.getBackground(), ContextCompat.getColor(DashboardActivity.this, R.color.textback));
                    milk.setTextColor(getColor(R.color.black));
                    DrawableCompat.setTint(fast.getBackground(), ContextCompat.getColor(DashboardActivity.this, R.color.textback));
                    fast.setTextColor(getColor(R.color.black));
                }
                break;
            case R.id.fast:
                if (fast.getCurrentTextColor() == -16777216) {
                    filterfood("milk", "");
                    DrawableCompat.setTint(vegetable.getBackground(), ContextCompat.getColor(DashboardActivity.this, R.color.textback));
                    vegetable.setTextColor(getColor(R.color.black));
                    DrawableCompat.setTint(fruit.getBackground(), ContextCompat.getColor(DashboardActivity.this, R.color.textback));
                    fruit.setTextColor(getColor(R.color.black));
                    DrawableCompat.setTint(meat.getBackground(), ContextCompat.getColor(DashboardActivity.this, R.color.textback));
                    meat.setTextColor(getColor(R.color.black));
                    DrawableCompat.setTint(milk.getBackground(), ContextCompat.getColor(DashboardActivity.this, R.color.textback));
                    milk.setTextColor(getColor(R.color.black));
                    DrawableCompat.setTint(fast.getBackground(), ContextCompat.getColor(DashboardActivity.this, R.color.blueshape));
                    fast.setTextColor(getColor(R.color.white));
                } else {
                    getdata();
                    DrawableCompat.setTint(vegetable.getBackground(), ContextCompat.getColor(DashboardActivity.this, R.color.textback));
                    vegetable.setTextColor(getColor(R.color.black));
                    DrawableCompat.setTint(fruit.getBackground(), ContextCompat.getColor(DashboardActivity.this, R.color.textback));
                    fruit.setTextColor(getColor(R.color.black));
                    DrawableCompat.setTint(meat.getBackground(), ContextCompat.getColor(DashboardActivity.this, R.color.textback));
                    meat.setTextColor(getColor(R.color.black));
                    DrawableCompat.setTint(milk.getBackground(), ContextCompat.getColor(DashboardActivity.this, R.color.textback));
                    milk.setTextColor(getColor(R.color.black));
                    DrawableCompat.setTint(fast.getBackground(), ContextCompat.getColor(DashboardActivity.this, R.color.textback));
                    fast.setTextColor(getColor(R.color.black));
                }
                break;
            case R.id.milk:
                if (milk.getCurrentTextColor() == -16777216) {
                    filterfood("fast food", "");
                    DrawableCompat.setTint(vegetable.getBackground(), ContextCompat.getColor(DashboardActivity.this, R.color.textback));
                    vegetable.setTextColor(getColor(R.color.black));
                    DrawableCompat.setTint(fruit.getBackground(), ContextCompat.getColor(DashboardActivity.this, R.color.textback));
                    fruit.setTextColor(getColor(R.color.black));
                    DrawableCompat.setTint(meat.getBackground(), ContextCompat.getColor(DashboardActivity.this, R.color.textback));
                    meat.setTextColor(getColor(R.color.black));
                    DrawableCompat.setTint(milk.getBackground(), ContextCompat.getColor(DashboardActivity.this, R.color.blueshape));
                    milk.setTextColor(getColor(R.color.white));
                    DrawableCompat.setTint(fast.getBackground(), ContextCompat.getColor(DashboardActivity.this, R.color.textback));
                    fast.setTextColor(getColor(R.color.black));
                } else {
                    getdata();
                    DrawableCompat.setTint(vegetable.getBackground(), ContextCompat.getColor(DashboardActivity.this, R.color.textback));
                    vegetable.setTextColor(getColor(R.color.black));
                    DrawableCompat.setTint(fruit.getBackground(), ContextCompat.getColor(DashboardActivity.this, R.color.textback));
                    fruit.setTextColor(getColor(R.color.black));
                    DrawableCompat.setTint(meat.getBackground(), ContextCompat.getColor(DashboardActivity.this, R.color.textback));
                    meat.setTextColor(getColor(R.color.black));
                    DrawableCompat.setTint(milk.getBackground(), ContextCompat.getColor(DashboardActivity.this, R.color.textback));
                    milk.setTextColor(getColor(R.color.black));
                    DrawableCompat.setTint(fast.getBackground(), ContextCompat.getColor(DashboardActivity.this, R.color.textback));
                    fast.setTextColor(getColor(R.color.black));
                }
                break;
            case R.id.movies:
                if (movieShow.getCurrentTextColor() == -16777216) {
                    filterfood("Movies & Shows", "סרטים ומופעים");
                    DrawableCompat.setTint(movieShow.getBackground(), ContextCompat.getColor(DashboardActivity.this, R.color.blueshape));
                    movieShow.setTextColor(getColor(R.color.white));
                    DrawableCompat.setTint(shopping.getBackground(), ContextCompat.getColor(DashboardActivity.this, R.color.textback));
                    shopping.setTextColor(getColor(R.color.black));
                    DrawableCompat.setTint(restaruantsFastFood.getBackground(), ContextCompat.getColor(DashboardActivity.this, R.color.textback));
                    restaruantsFastFood.setTextColor(getColor(R.color.black));
                    DrawableCompat.setTint(other.getBackground(), ContextCompat.getColor(DashboardActivity.this, R.color.textback));
                    other.setTextColor(getColor(R.color.black));
                } else {
                    getdata();
                    DrawableCompat.setTint(movieShow.getBackground(), ContextCompat.getColor(DashboardActivity.this, R.color.textback));
                    movieShow.setTextColor(getColor(R.color.black));
                    DrawableCompat.setTint(shopping.getBackground(), ContextCompat.getColor(DashboardActivity.this, R.color.textback));
                    shopping.setTextColor(getColor(R.color.black));
                    DrawableCompat.setTint(restaruantsFastFood.getBackground(), ContextCompat.getColor(DashboardActivity.this, R.color.textback));
                    restaruantsFastFood.setTextColor(getColor(R.color.black));
                    DrawableCompat.setTint(other.getBackground(), ContextCompat.getColor(DashboardActivity.this, R.color.textback));
                    other.setTextColor(getColor(R.color.black));
                }
                break;
            case R.id.shoping:
                if (shopping.getCurrentTextColor() == -16777216) {
                    filterfood("Shopping", "קניות");

                    DrawableCompat.setTint(movieShow.getBackground(), ContextCompat.getColor(DashboardActivity.this, R.color.textback));
                    movieShow.setTextColor(getColor(R.color.black));
                    DrawableCompat.setTint(shopping.getBackground(), ContextCompat.getColor(DashboardActivity.this, R.color.blueshape));
                    shopping.setTextColor(getColor(R.color.white));
                    DrawableCompat.setTint(restaruantsFastFood.getBackground(), ContextCompat.getColor(DashboardActivity.this, R.color.textback));
                    restaruantsFastFood.setTextColor(getColor(R.color.black));
                    DrawableCompat.setTint(other.getBackground(), ContextCompat.getColor(DashboardActivity.this, R.color.textback));
                    other.setTextColor(getColor(R.color.black));
                } else {
                    getdata();
                    DrawableCompat.setTint(movieShow.getBackground(), ContextCompat.getColor(DashboardActivity.this, R.color.textback));
                    movieShow.setTextColor(getColor(R.color.black));
                    DrawableCompat.setTint(shopping.getBackground(), ContextCompat.getColor(DashboardActivity.this, R.color.textback));
                    shopping.setTextColor(getColor(R.color.black));
                    DrawableCompat.setTint(restaruantsFastFood.getBackground(), ContextCompat.getColor(DashboardActivity.this, R.color.textback));
                    restaruantsFastFood.setTextColor(getColor(R.color.black));
                    DrawableCompat.setTint(other.getBackground(), ContextCompat.getColor(DashboardActivity.this, R.color.textback));
                    other.setTextColor(getColor(R.color.black));
                }
                break;
            case R.id.restuarent:
                if (restaruantsFastFood.getCurrentTextColor() == -16777216) {
                    filterfood("Restaurants & FastFood", "מסעדות ומזון מהיר");

                    DrawableCompat.setTint(movieShow.getBackground(), ContextCompat.getColor(DashboardActivity.this, R.color.textback));
                    movieShow.setTextColor(getColor(R.color.black));
                    DrawableCompat.setTint(shopping.getBackground(), ContextCompat.getColor(DashboardActivity.this, R.color.textback));
                    shopping.setTextColor(getColor(R.color.black));
                    DrawableCompat.setTint(restaruantsFastFood.getBackground(), ContextCompat.getColor(DashboardActivity.this, R.color.blueshape));
                    restaruantsFastFood.setTextColor(getColor(R.color.white));
                    DrawableCompat.setTint(other.getBackground(), ContextCompat.getColor(DashboardActivity.this, R.color.textback));
                    other.setTextColor(getColor(R.color.black));
                } else {
                    getdata();
                    DrawableCompat.setTint(movieShow.getBackground(), ContextCompat.getColor(DashboardActivity.this, R.color.textback));
                    movieShow.setTextColor(getColor(R.color.black));
                    DrawableCompat.setTint(shopping.getBackground(), ContextCompat.getColor(DashboardActivity.this, R.color.textback));
                    shopping.setTextColor(getColor(R.color.black));
                    DrawableCompat.setTint(restaruantsFastFood.getBackground(), ContextCompat.getColor(DashboardActivity.this, R.color.textback));
                    restaruantsFastFood.setTextColor(getColor(R.color.black));
                    DrawableCompat.setTint(other.getBackground(), ContextCompat.getColor(DashboardActivity.this, R.color.textback));
                    other.setTextColor(getColor(R.color.black));
                }
                break;
            case R.id.other:
                if (other.getCurrentTextColor() == -16777216) {
                    filterfood("Other", "אחר");

                    DrawableCompat.setTint(movieShow.getBackground(), ContextCompat.getColor(DashboardActivity.this, R.color.textback));
                    movieShow.setTextColor(getColor(R.color.black));
                    DrawableCompat.setTint(shopping.getBackground(), ContextCompat.getColor(DashboardActivity.this, R.color.textback));
                    shopping.setTextColor(getColor(R.color.black));
                    DrawableCompat.setTint(restaruantsFastFood.getBackground(), ContextCompat.getColor(DashboardActivity.this, R.color.textback));
                    restaruantsFastFood.setTextColor(getColor(R.color.black));
                    DrawableCompat.setTint(other.getBackground(), ContextCompat.getColor(DashboardActivity.this, R.color.blueshape));
                    other.setTextColor(getColor(R.color.white));
                } else {
                    getdata();
                    DrawableCompat.setTint(movieShow.getBackground(), ContextCompat.getColor(DashboardActivity.this, R.color.textback));
                    movieShow.setTextColor(getColor(R.color.black));
                    DrawableCompat.setTint(shopping.getBackground(), ContextCompat.getColor(DashboardActivity.this, R.color.textback));
                    shopping.setTextColor(getColor(R.color.black));
                    DrawableCompat.setTint(restaruantsFastFood.getBackground(), ContextCompat.getColor(DashboardActivity.this, R.color.textback));
                    restaruantsFastFood.setTextColor(getColor(R.color.black));
                    DrawableCompat.setTint(other.getBackground(), ContextCompat.getColor(DashboardActivity.this, R.color.textback));
                    other.setTextColor(getColor(R.color.black));
                }
                break;
//            case R.id.clearfilter:
//                DrawableCompat.setTint(vegetable.getBackground(), ContextCompat.getColor(DashboardActivity.this, R.color.textback));
//                vegetable.setTextColor(getColor(R.color.black));
//                DrawableCompat.setTint(fruit.getBackground(), ContextCompat.getColor(DashboardActivity.this, R.color.textback));
//                fruit.setTextColor(getColor(R.color.black));
//                DrawableCompat.setTint(meat.getBackground(), ContextCompat.getColor(DashboardActivity.this, R.color.textback));
//                meat.setTextColor(getColor(R.color.black));
//                DrawableCompat.setTint(milk.getBackground(), ContextCompat.getColor(DashboardActivity.this, R.color.textback));
//                milk.setTextColor(getColor(R.color.black));
//                DrawableCompat.setTint(fast.getBackground(), ContextCompat.getColor(DashboardActivity.this, R.color.textback));
//                fast.setTextColor(getColor(R.color.black));
//                getdata();
//                break;

//            case R.id.upload_screen_btn:
//
//                Intent i = new Intent(DashboardActivity.this, UploadMenuItemsActivity.class);
//                i.putExtra("phone", phone);
//                startActivity(i);
//
//                break;
//            case R.id.logout_btn:

//                FirebaseAuth.getInstance().signOut();
//                Intent a = new Intent(DashboardActivity.this, LoginActivity.class);
//                startActivity(a);
//                finish();
//                break;
//            case R.id.sale_items_btn:
//
////                FirebaseAuth.getInstance().signOut();
//                Intent a = new Intent(DashboardActivity.this, DisplaySellItemsActivity.class);
//                startActivity(a);
//                break;
//
//            case R.id.archive_items_btn:
//
//                Intent as = new Intent(DashboardActivity.this, ArchiveItemsActivity.class);
//                startActivity(as);
//
//                break;
        }
    }

    private void filterfood(String text,String text2) {
        ProductsRef.orderByChild("isArchive").equalTo("false").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot != null) {

                    items = new ArrayList<>();
                    progressBarLayout.setVisibility(View.GONE);

                    for (DataSnapshot nodeSnapshot : snapshot.getChildren()) {
                        Log.d("", "");

                        if (text.equals(nodeSnapshot.child("Category").getValue().toString())) {
                            Item item = new Item();

                            item.setID(nodeSnapshot.getKey());
                            item.setName(nodeSnapshot.child("Name").getValue().toString());
                            item.setExpDate(nodeSnapshot.child("ExpDate").getValue().toString());
                            item.setCatalogNumber(nodeSnapshot.child("CatalogNumber").getValue().toString());
                            //item.setPrice(nodeSnapshot.child("Price").getValue().toString());
                            if (nodeSnapshot.child("Price").exists()){
                                item.setPrice(nodeSnapshot.child("Price").getValue().toString());
                            }
                            if (nodeSnapshot.child("Provider").exists()){
                                item.setPrice(nodeSnapshot.child("Provider").getValue().toString());
                            }
                            item.setCategory(nodeSnapshot.child("Category").getValue().toString());
                            item.setImage(nodeSnapshot.child("image").getValue() == null ? ""
                                    : nodeSnapshot.child("image").getValue().toString());

                            items.add(item);
                            shareExpDate = item.getExpDate();
                            shareName = item.getName();
                            shareCatlogNumber = item.getCatalogNumber();
                        }else if (text2.equals(nodeSnapshot.child("Category").getValue().toString())) {
                            Item item = new Item();

                            item.setID(nodeSnapshot.getKey());
                            item.setName(nodeSnapshot.child("Name").getValue().toString());
                            item.setExpDate(nodeSnapshot.child("ExpDate").getValue().toString());
                            item.setCatalogNumber(nodeSnapshot.child("CatalogNumber").getValue().toString());
                            //item.setPrice(nodeSnapshot.child("Price").getValue().toString());
                            if (nodeSnapshot.child("Price").exists()){
                                item.setPrice(nodeSnapshot.child("Price").getValue().toString());
                            }
                            if (nodeSnapshot.child("Provider").exists()){
                                item.setPrice(nodeSnapshot.child("Provider").getValue().toString());
                            }
                            //item.setPrice(nodeSnapshot.child("Price").getValue().toString());
                            item.setCategory(nodeSnapshot.child("Category").getValue().toString());
                            item.setImage(nodeSnapshot.child("image").getValue() == null ? ""
                                    : nodeSnapshot.child("image").getValue().toString());

                            items.add(item);
                            shareExpDate = item.getExpDate();
                            shareName = item.getName();
                            shareCatlogNumber = item.getCatalogNumber();
                        }


                    }

                    Log.d("tag", "populating data");
                    if (foodItemAdapter != null)
                        foodItemAdapter.updateItems(items);

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void archiveItem(String name, String catalogNumber, String image, String id) {


        dailogArchive(name, catalogNumber, image, id);


    }

    private void dailogArchive(String name, String catalogNumber, String image, String id) {


        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(DashboardActivity.this);

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dailog_archive, null);
        dialogBuilder.setView(dialogView);
        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.setCancelable(true);

        TextView yesBtn = dialogView.findViewById(R.id.dailog_yes_btn);
        TextView noBtn = dialogView.findViewById(R.id.dailog_no_btn);


        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                progressBarLayout.setVisibility(View.VISIBLE);

                UpdateRef = FirebaseDatabase.getInstance().getReference().child("Food").child(userId).child(id).child("isArchive");
                UpdateRef.setValue("true").addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressBarLayout.setVisibility(View.INVISIBLE);

                    }
                });
                onResume();

                alertDialog.dismiss();


            }


        });


        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                alertDialog.dismiss();

            }


        });

        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();


    }

    public void sellItem(String name, String catalogNumber, String image, String id, String price) {

        Intent i = new Intent(this, SellItemsActivity.class);
        i.putExtra("ItemName", name);
        i.putExtra("CatalogNumber", catalogNumber);
        i.putExtra("ID", id);
        i.putExtra("ItemPrice", price);

        i.putExtra("UserId", userId);

        startActivity(i);

    }


    public void dialogShare(String formatedDate, String name, String catalogNumber, String image, String ID) {

        StorageReference productImagesRef = FirebaseStorage.getInstance().getReference().child("Product Images");


        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(DashboardActivity.this);

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dailog_share, null);
        dialogBuilder.setView(dialogView);
        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.setCancelable(true);

        ImageView whatsappBtn = dialogView.findViewById(R.id.whatsapp_btn);
        ImageView gmailBtn = dialogView.findViewById(R.id.gmail_btn);


        whatsappBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                Uri uri = FileProvider.getUriForFile(DashboardActivity.this, fileProvider, localFile);
                Intent intent = new Intent(Intent.ACTION_VIEW);
//                intent.setData(Uri.parse("http://api.whatsapp.com/send?phone=" + "&text=" + "Name: " + name + "\n" + "Catalog Number: " + catalogNumber + "\n" + "Date: " + formatedDate + "\nIamge: " + uri.toString()));
                intent.setData(Uri.parse("http://api.whatsapp.com/send?phone=" + "&text=" + getString(R.string.name)+": " + name + "\n" + getString(R.string.catalogNumber) +": "+ catalogNumber + "\n" + getString(R.string.ExpiryDate)+": " + formatedDate+"\n"+getString(R.string.shareMsg)));
                startActivity(intent);


                       }


        });


        gmailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:")); // only email apps should handle this
//                intent.putExtra(Intent.EXTRA_EMAIL, "moazzamsaleem66@gmail.com");
                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.product_details));
                intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.name)+": " + name + "\n" + getString(R.string.catalogNumber) +": "+ catalogNumber + "\n" + getString(R.string.ExpiryDate)+": " + formatedDate+"\n"+getString(R.string.shareMsg));

                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }


        });

        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();
    }

    public void dialogLanguage(final Activity activity) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(DashboardActivity.this);

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dailog_language, null);

        dialogBuilder.setView(dialogView);
        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.setCancelable(true);
        RadioGroup languageRadio = (RadioGroup) dialogView.findViewById(R.id.languageRadioGroup);

        RadioButton english = (RadioButton) dialogView.findViewById(R.id.english);
        RadioButton hebrew = (RadioButton) dialogView.findViewById(R.id.hebrew);


        String languageCode = LanguageSet.getLanguage(getApplicationContext());
        if (languageCode.equals("en")) {
            english.setChecked(true);
        } else {
            hebrew.setChecked(true);
        }


        languageRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.english:
                        if (!languageCode.equals("en")) {
                            LanguageSet.persist(activity, "en");
                            LanguageSet.setLocale(activity);
                            navigateScreen();
                        }

                        break;
                    case R.id.hebrew:
                        if (!languageCode.equals("iw")) {
                            LanguageSet.persist(activity, "iw");
                            LanguageSet.setLocale(activity);
                            navigateScreen();
                        }
                        break;
                }
            }
        });


        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();
    }

    public void navigateScreen() {
        recreate();
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

        ArrayList<Item> filteredItems = new ArrayList<>();
        if (items != null) {
            for (int i = 0; i < items.size(); i++) {

                if (items.get(i).getName().toLowerCase().contains(s.toString().toLowerCase())) {

                    filteredItems.add(items.get(i));
                }
            }
            foodItemAdapter.updateItems(filteredItems);

        }

    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}



