package com.rtapps.moc;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.WanderingCubes;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rtapps.moc.Adapter.SellItemsAdapter;
import com.rtapps.moc.Model.Item;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class DisplaySellItemsActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher {
    RecyclerView sellItemRecyclerView;
    RelativeLayout backBtn;
    String phone;
    String userId;
    String shareName, shareExpDate, shareCatlogNumber;
    EditText searchItemsEdittext;
    ArrayList<Item> items;
    RecyclerView.LayoutManager layoutManager;
    SellItemsAdapter sellItemsAdapter;
    FirebaseDatabase firebaseDatabase;
    RelativeLayout progressBarLayout;
    LinearLayout expirelay, pricelay, categorylay;
    Button expirebtn, pricebtn, pricefilterbtn, day, week, month, categorybtn, clearsellfilter;
    EditText fromedit, toedit;
    Date today;
    TextView vegetable, fruit, milk, fast, meat;
    private DatabaseReference ProductsRef;
    private DatabaseReference UpdateRef;

    TextView movieShow, shopping, restaruantsFastFood, other;


    @Override
    protected void onStart() {
        super.onStart();
        LanguageSet.setLocale(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_sell_items);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setStatusBarColor(DisplaySellItemsActivity.this.getResources().getColor(R.color.background));
        init();
        phone = getIntent().getStringExtra("phone");
        userId = getIntent().getStringExtra("UserId");

        firebaseDatabase = FirebaseDatabase.getInstance();

        // below line is used to get
        // reference for our database.

        sellItemRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(DisplaySellItemsActivity.this);
        sellItemRecyclerView.setLayoutManager(layoutManager);
        ProductsRef = FirebaseDatabase.getInstance().getReference().child("Food");
        progressBarLayout = findViewById(R.id.progress_bar_layout_archived);
        Sprite rotatingCircle = new WanderingCubes();


        initRecyclerView();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS}, 44);
        }

//        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_SMS},44);


    }

    private void initRecyclerView() {

        sellItemsAdapter = new SellItemsAdapter(this, new ArrayList<>());
        sellItemRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        sellItemRecyclerView.setAdapter(sellItemsAdapter);

    }

    @Override
    protected void onResume() {
        super.onResume();

        getdata();

    }

    private void getdata() {
//        ProductsRef.orderByChild("ForSale").equalTo("true").addListenerForSingleValueEvent(new ValueEventListener() {
        ProductsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                items = new ArrayList<>();
                progressBarLayout.setVisibility(View.GONE);

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    for (DataSnapshot nodeSnapshot : dataSnapshot.getChildren()) {
                        if (nodeSnapshot.child("ForSale").getValue() != null) {

                            Log.d("", "");

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
                            item.setImage(nodeSnapshot.child("image").getValue() == null ? ""
                                    : nodeSnapshot.child("image").getValue().toString());
                            item.setSellerName(nodeSnapshot.child("sellerName").getValue().toString());
                            item.setSellerPhone(nodeSnapshot.child("sellerPhone").getValue().toString());
                            //item.setPrice(nodeSnapshot.child("Price").getValue().toString());

                            items.add(item);
                            shareExpDate = item.getExpDate();
                            shareName = item.getName();
                            shareCatlogNumber = item.getCatalogNumber();
                        }

                    }
                }

                Log.d("tag", "populating data");
                if (sellItemsAdapter != null)
                    sellItemsAdapter.updateItems(items);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void init() {
        sellItemRecyclerView = findViewById(R.id.sell_item_recycler);

        expirelay = findViewById(R.id.expirelay);
        pricelay = findViewById(R.id.pricelay);
        categorylay = findViewById(R.id.categorylay);
        expirebtn = findViewById(R.id.expirebutton);
        pricebtn = findViewById(R.id.pricebutton);
        categorybtn = findViewById(R.id.categorybutton);
        backBtn = findViewById(R.id.back_button_layout_sell);
        backBtn.setOnClickListener(this);
        searchItemsEdittext = findViewById(R.id.search_items_textview);
        fromedit = findViewById(R.id.fromedit);
        toedit = findViewById(R.id.toedit);
        pricefilterbtn = findViewById(R.id.pricefilterbtn);
        day = findViewById(R.id.day);
        week = findViewById(R.id.week);
        month = findViewById(R.id.month);
        vegetable = findViewById(R.id.vegetable);
        fruit = findViewById(R.id.fruit);
        milk = findViewById(R.id.milk);
        fast = findViewById(R.id.fast);
        meat = findViewById(R.id.meat);

        movieShow = findViewById(R.id.movies);
        shopping = findViewById(R.id.shoping);
        restaruantsFastFood = findViewById(R.id.restuarent);
        other = findViewById(R.id.other);

        vegetable.setOnClickListener(this);
        fruit.setOnClickListener(this);
        meat.setOnClickListener(this);
        fast.setOnClickListener(this);
        milk.setOnClickListener(this);

        movieShow.setOnClickListener(this);
        restaruantsFastFood.setOnClickListener(this);
        shopping.setOnClickListener(this);
        other.setOnClickListener(this);


        searchItemsEdittext.addTextChangedListener(this);
        expirebtn.setOnClickListener(this);
        pricebtn.setOnClickListener(this);
        categorybtn.setOnClickListener(this);
        pricefilterbtn.setOnClickListener(this);
        day.setOnClickListener(this);
        week.setOnClickListener(this);
        month.setOnClickListener(this);
        fromedit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if (s.length() != 0) {

                    if (TextUtils.isEmpty(fromedit.getText().toString())) {

                    } else if (TextUtils.isEmpty(toedit.getText().toString())) {

                    } else {
                        filterdata();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        toedit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if (s.length() != 0) {

                    if (TextUtils.isEmpty(fromedit.getText().toString())) {

                    } else if (TextUtils.isEmpty(toedit.getText().toString())) {

                    } else {
                        filterdata();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {


            case R.id.back_button_layout_sell:
                onBackPressed();
                finish();
                break;
            case R.id.categorybutton:

                Log.i("categorycolor", "onClick: " + categorybtn.getTextColors().getDefaultColor());

                if (categorybtn.getTextColors().getDefaultColor() == -16777216) {
                    expirelay.setVisibility(View.GONE);
                    pricelay.setVisibility(View.GONE);
                    categorylay.setVisibility(View.VISIBLE);
                    DrawableCompat.setTint(categorybtn.getBackground(), ContextCompat.getColor(DisplaySellItemsActivity.this, R.color.blueshape));
                    categorybtn.setTextColor(getColor(R.color.white));
                    DrawableCompat.setTint(expirebtn.getBackground(), ContextCompat.getColor(DisplaySellItemsActivity.this, R.color.textback));
                    expirebtn.setTextColor(getColor(R.color.black));
                    DrawableCompat.setTint(pricebtn.getBackground(), ContextCompat.getColor(DisplaySellItemsActivity.this, R.color.textback));
                    pricebtn.setTextColor(getColor(R.color.black));
                } else {
                    clearfilter();
                }

                break;
            case R.id.expirebutton:
                if (expirebtn.getTextColors().getDefaultColor() == -16777216) {
                    expirelay.setVisibility(View.VISIBLE);
                    pricelay.setVisibility(View.GONE);
                    categorylay.setVisibility(View.GONE);
                    DrawableCompat.setTint(expirebtn.getBackground(), ContextCompat.getColor(DisplaySellItemsActivity.this, R.color.blueshape));
                    expirebtn.setTextColor(getColor(R.color.white));
                    DrawableCompat.setTint(pricebtn.getBackground(), ContextCompat.getColor(DisplaySellItemsActivity.this, R.color.textback));
                    pricebtn.setTextColor(getColor(R.color.black));
                    DrawableCompat.setTint(categorybtn.getBackground(), ContextCompat.getColor(DisplaySellItemsActivity.this, R.color.textback));
                    categorybtn.setTextColor(getColor(R.color.black));
                } else {
                    clearfilter();
                }

                break;
            case R.id.pricebutton:

                if (pricebtn.getTextColors().getDefaultColor() == -16777216) {
                    expirelay.setVisibility(View.GONE);
                    pricelay.setVisibility(View.VISIBLE);
                    categorylay.setVisibility(View.GONE);

                    DrawableCompat.setTint(pricebtn.getBackground(), ContextCompat.getColor(DisplaySellItemsActivity.this, R.color.blueshape));
                    pricebtn.setTextColor(getColor(R.color.white));
                    DrawableCompat.setTint(expirebtn.getBackground(), ContextCompat.getColor(DisplaySellItemsActivity.this, R.color.textback));
                    expirebtn.setTextColor(getColor(R.color.black));
                    DrawableCompat.setTint(categorybtn.getBackground(), ContextCompat.getColor(DisplaySellItemsActivity.this, R.color.textback));
                    categorybtn.setTextColor(getColor(R.color.black));
                } else {
                    clearfilter();
                }

                break;
            case R.id.pricefilterbtn:
                if (TextUtils.isEmpty(fromedit.getText().toString())) {

                } else if (TextUtils.isEmpty(toedit.getText().toString())) {

                } else {
                    filterdata();
                }
                break;
            case R.id.day:
                DrawableCompat.setTint(day.getBackground(), ContextCompat.getColor(DisplaySellItemsActivity.this, R.color.blueshape));
                day.setTextColor(getColor(R.color.white));
                DrawableCompat.setTint(week.getBackground(), ContextCompat.getColor(DisplaySellItemsActivity.this, R.color.textback));
                week.setTextColor(getColor(R.color.black));
                DrawableCompat.setTint(month.getBackground(), ContextCompat.getColor(DisplaySellItemsActivity.this, R.color.textback));
                month.setTextColor(getColor(R.color.black));

                filterdata(1);

                break;
            case R.id.week:
                DrawableCompat.setTint(week.getBackground(), ContextCompat.getColor(DisplaySellItemsActivity.this, R.color.blueshape));
                week.setTextColor(getColor(R.color.white));
                DrawableCompat.setTint(day.getBackground(), ContextCompat.getColor(DisplaySellItemsActivity.this, R.color.textback));
                day.setTextColor(getColor(R.color.black));
                DrawableCompat.setTint(month.getBackground(), ContextCompat.getColor(DisplaySellItemsActivity.this, R.color.textback));
                month.setTextColor(getColor(R.color.black));
                filterdata(7);
                break;
            case R.id.month:
                DrawableCompat.setTint(month.getBackground(), ContextCompat.getColor(DisplaySellItemsActivity.this, R.color.blueshape));
                month.setTextColor(getColor(R.color.white));
                DrawableCompat.setTint(week.getBackground(), ContextCompat.getColor(DisplaySellItemsActivity.this, R.color.textback));
                week.setTextColor(getColor(R.color.black));
                DrawableCompat.setTint(day.getBackground(), ContextCompat.getColor(DisplaySellItemsActivity.this, R.color.textback));
                day.setTextColor(getColor(R.color.black));
                filterdata(28);
                break;
            case R.id.vegetable:
                filterfood("vegetables","");
                DrawableCompat.setTint(vegetable.getBackground(), ContextCompat.getColor(DisplaySellItemsActivity.this, R.color.blueshape));
                vegetable.setTextColor(getColor(R.color.white));
                DrawableCompat.setTint(fruit.getBackground(), ContextCompat.getColor(DisplaySellItemsActivity.this, R.color.textback));
                fruit.setTextColor(getColor(R.color.black));
                DrawableCompat.setTint(meat.getBackground(), ContextCompat.getColor(DisplaySellItemsActivity.this, R.color.textback));
                meat.setTextColor(getColor(R.color.black));
                DrawableCompat.setTint(milk.getBackground(), ContextCompat.getColor(DisplaySellItemsActivity.this, R.color.textback));
                milk.setTextColor(getColor(R.color.black));
                DrawableCompat.setTint(fast.getBackground(), ContextCompat.getColor(DisplaySellItemsActivity.this, R.color.textback));
                fast.setTextColor(getColor(R.color.black));
                break;
            case R.id.fruit:
                filterfood("fruit","");
                DrawableCompat.setTint(vegetable.getBackground(), ContextCompat.getColor(DisplaySellItemsActivity.this, R.color.textback));
                vegetable.setTextColor(getColor(R.color.black));
                DrawableCompat.setTint(fruit.getBackground(), ContextCompat.getColor(DisplaySellItemsActivity.this, R.color.blueshape));
                fruit.setTextColor(getColor(R.color.white));
                DrawableCompat.setTint(meat.getBackground(), ContextCompat.getColor(DisplaySellItemsActivity.this, R.color.textback));
                meat.setTextColor(getColor(R.color.black));
                DrawableCompat.setTint(milk.getBackground(), ContextCompat.getColor(DisplaySellItemsActivity.this, R.color.textback));
                milk.setTextColor(getColor(R.color.black));
                DrawableCompat.setTint(fast.getBackground(), ContextCompat.getColor(DisplaySellItemsActivity.this, R.color.textback));
                fast.setTextColor(getColor(R.color.black));
                break;
            case R.id.meat:
                filterfood("meat","");
                DrawableCompat.setTint(vegetable.getBackground(), ContextCompat.getColor(DisplaySellItemsActivity.this, R.color.textback));
                vegetable.setTextColor(getColor(R.color.black));
                DrawableCompat.setTint(fruit.getBackground(), ContextCompat.getColor(DisplaySellItemsActivity.this, R.color.textback));
                fruit.setTextColor(getColor(R.color.black));
                DrawableCompat.setTint(meat.getBackground(), ContextCompat.getColor(DisplaySellItemsActivity.this, R.color.blueshape));
                meat.setTextColor(getColor(R.color.white));
                DrawableCompat.setTint(milk.getBackground(), ContextCompat.getColor(DisplaySellItemsActivity.this, R.color.textback));
                milk.setTextColor(getColor(R.color.black));
                DrawableCompat.setTint(fast.getBackground(), ContextCompat.getColor(DisplaySellItemsActivity.this, R.color.textback));
                fast.setTextColor(getColor(R.color.black));
                break;
            case R.id.fast:
                filterfood("milk","");
                DrawableCompat.setTint(vegetable.getBackground(), ContextCompat.getColor(DisplaySellItemsActivity.this, R.color.textback));
                vegetable.setTextColor(getColor(R.color.black));
                DrawableCompat.setTint(fruit.getBackground(), ContextCompat.getColor(DisplaySellItemsActivity.this, R.color.textback));
                fruit.setTextColor(getColor(R.color.black));
                DrawableCompat.setTint(meat.getBackground(), ContextCompat.getColor(DisplaySellItemsActivity.this, R.color.textback));
                meat.setTextColor(getColor(R.color.black));
                DrawableCompat.setTint(milk.getBackground(), ContextCompat.getColor(DisplaySellItemsActivity.this, R.color.textback));
                milk.setTextColor(getColor(R.color.black));
                DrawableCompat.setTint(fast.getBackground(), ContextCompat.getColor(DisplaySellItemsActivity.this, R.color.blueshape));
                fast.setTextColor(getColor(R.color.white));
                break;
            case R.id.milk:
                filterfood("fast food","");
                DrawableCompat.setTint(vegetable.getBackground(), ContextCompat.getColor(DisplaySellItemsActivity.this, R.color.textback));
                vegetable.setTextColor(getColor(R.color.black));
                DrawableCompat.setTint(fruit.getBackground(), ContextCompat.getColor(DisplaySellItemsActivity.this, R.color.textback));
                fruit.setTextColor(getColor(R.color.black));
                DrawableCompat.setTint(meat.getBackground(), ContextCompat.getColor(DisplaySellItemsActivity.this, R.color.textback));
                meat.setTextColor(getColor(R.color.black));
                DrawableCompat.setTint(milk.getBackground(), ContextCompat.getColor(DisplaySellItemsActivity.this, R.color.blueshape));
                milk.setTextColor(getColor(R.color.white));
                DrawableCompat.setTint(fast.getBackground(), ContextCompat.getColor(DisplaySellItemsActivity.this, R.color.textback));
                fast.setTextColor(getColor(R.color.black));
                break;





                case R.id.movies:

                    filterfood("Movies & Shows", "סרטים ומופעים");
                    DrawableCompat.setTint(movieShow.getBackground(), ContextCompat.getColor(DisplaySellItemsActivity.this, R.color.blueshape));
                    movieShow.setTextColor(getColor(R.color.white));
                    DrawableCompat.setTint(shopping.getBackground(), ContextCompat.getColor(DisplaySellItemsActivity.this, R.color.textback));
                    shopping.setTextColor(getColor(R.color.black));
                    DrawableCompat.setTint(restaruantsFastFood.getBackground(), ContextCompat.getColor(DisplaySellItemsActivity.this, R.color.textback));
                    restaruantsFastFood.setTextColor(getColor(R.color.black));
                    DrawableCompat.setTint(other.getBackground(), ContextCompat.getColor(DisplaySellItemsActivity.this, R.color.textback));
                    other.setTextColor(getColor(R.color.black));

                break;
            case R.id.shoping:
                    filterfood("Shopping", "קניות");

                    DrawableCompat.setTint(movieShow.getBackground(), ContextCompat.getColor(DisplaySellItemsActivity.this, R.color.textback));
                    movieShow.setTextColor(getColor(R.color.black));
                    DrawableCompat.setTint(shopping.getBackground(), ContextCompat.getColor(DisplaySellItemsActivity.this, R.color.blueshape));
                    shopping.setTextColor(getColor(R.color.white));
                    DrawableCompat.setTint(restaruantsFastFood.getBackground(), ContextCompat.getColor(DisplaySellItemsActivity.this, R.color.textback));
                    restaruantsFastFood.setTextColor(getColor(R.color.black));
                    DrawableCompat.setTint(other.getBackground(), ContextCompat.getColor(DisplaySellItemsActivity.this, R.color.textback));
                    other.setTextColor(getColor(R.color.black));
                break;
            case R.id.restuarent:
                    filterfood("Restaurants & FastFood", "מסעדות ומזון מהיר");

                    DrawableCompat.setTint(movieShow.getBackground(), ContextCompat.getColor(DisplaySellItemsActivity.this, R.color.textback));
                    movieShow.setTextColor(getColor(R.color.black));
                    DrawableCompat.setTint(shopping.getBackground(), ContextCompat.getColor(DisplaySellItemsActivity.this, R.color.textback));
                    shopping.setTextColor(getColor(R.color.black));
                    DrawableCompat.setTint(restaruantsFastFood.getBackground(), ContextCompat.getColor(DisplaySellItemsActivity.this, R.color.blueshape));
                    restaruantsFastFood.setTextColor(getColor(R.color.white));
                    DrawableCompat.setTint(other.getBackground(), ContextCompat.getColor(DisplaySellItemsActivity.this, R.color.textback));
                    other.setTextColor(getColor(R.color.black));

                break;
            case R.id.other:
                    filterfood("Other", "אחר");

                    DrawableCompat.setTint(movieShow.getBackground(), ContextCompat.getColor(DisplaySellItemsActivity.this, R.color.textback));
                    movieShow.setTextColor(getColor(R.color.black));
                    DrawableCompat.setTint(shopping.getBackground(), ContextCompat.getColor(DisplaySellItemsActivity.this, R.color.textback));
                    shopping.setTextColor(getColor(R.color.black));
                    DrawableCompat.setTint(restaruantsFastFood.getBackground(), ContextCompat.getColor(DisplaySellItemsActivity.this, R.color.textback));
                    restaruantsFastFood.setTextColor(getColor(R.color.black));
                    DrawableCompat.setTint(other.getBackground(), ContextCompat.getColor(DisplaySellItemsActivity.this, R.color.blueshape));
                    other.setTextColor(getColor(R.color.white));

                break;


        }
    }

    private void clearfilter() {
        expirelay.setVisibility(View.GONE);
        pricelay.setVisibility(View.GONE);
        categorylay.setVisibility(View.GONE);
        DrawableCompat.setTint(categorybtn.getBackground(), ContextCompat.getColor(DisplaySellItemsActivity.this, R.color.textback));
        categorybtn.setTextColor(getColor(R.color.black));
        DrawableCompat.setTint(expirebtn.getBackground(), ContextCompat.getColor(DisplaySellItemsActivity.this, R.color.textback));
        expirebtn.setTextColor(getColor(R.color.black));
        DrawableCompat.setTint(pricebtn.getBackground(), ContextCompat.getColor(DisplaySellItemsActivity.this, R.color.textback));
        pricebtn.setTextColor(getColor(R.color.black));

        DrawableCompat.setTint(vegetable.getBackground(), ContextCompat.getColor(DisplaySellItemsActivity.this, R.color.textback));
        vegetable.setTextColor(getColor(R.color.black));
        DrawableCompat.setTint(fruit.getBackground(), ContextCompat.getColor(DisplaySellItemsActivity.this, R.color.textback));
        fruit.setTextColor(getColor(R.color.black));
        DrawableCompat.setTint(meat.getBackground(), ContextCompat.getColor(DisplaySellItemsActivity.this, R.color.textback));
        meat.setTextColor(getColor(R.color.black));
        DrawableCompat.setTint(milk.getBackground(), ContextCompat.getColor(DisplaySellItemsActivity.this, R.color.textback));
        milk.setTextColor(getColor(R.color.black));
        DrawableCompat.setTint(fast.getBackground(), ContextCompat.getColor(DisplaySellItemsActivity.this, R.color.textback));
        fast.setTextColor(getColor(R.color.black));

        DrawableCompat.setTint(week.getBackground(), ContextCompat.getColor(DisplaySellItemsActivity.this, R.color.textback));
        week.setTextColor(getColor(R.color.black));
        DrawableCompat.setTint(day.getBackground(), ContextCompat.getColor(DisplaySellItemsActivity.this, R.color.textback));
        day.setTextColor(getColor(R.color.black));
        DrawableCompat.setTint(month.getBackground(), ContextCompat.getColor(DisplaySellItemsActivity.this, R.color.textback));
        month.setTextColor(getColor(R.color.black));

        fromedit.getText().clear();
        toedit.getText().clear();
        getdata();
    }

    private void filterdata() {
        ProductsRef.orderByChild("ForSale").equalTo("true").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot != null) {

                    items = new ArrayList<>();
                    progressBarLayout.setVisibility(View.GONE);

//                    double fromprice = Double.parseDouble(fromedit.getText().toString());
//                    double toprice = Double.parseDouble(toedit.getText().toString());

                    if (TextUtils.isEmpty(fromedit.getText().toString())) {

                    } else if (TextUtils.isEmpty(toedit.getText().toString())) {

                    } else {
                        for (DataSnapshot nodeSnapshot : snapshot.getChildren()) {
                            Log.d("", "");

                            double price = Double.parseDouble(nodeSnapshot.child("Price").getValue().toString());

                            if (price >= Double.parseDouble(fromedit.getText().toString()) && price <= Double.parseDouble(toedit.getText().toString())) {
                                Item item = new Item();

                                item.setID(nodeSnapshot.getKey());
                                item.setName(nodeSnapshot.child("Name").getValue().toString());
                                item.setExpDate(nodeSnapshot.child("ExpDate").getValue().toString());
                                item.setCatalogNumber(nodeSnapshot.child("CatalogNumber").getValue().toString());
                               // item.setPrice(nodeSnapshot.child("Price").getValue().toString());
                                if (nodeSnapshot.child("Price").exists()){
                                    item.setPrice(nodeSnapshot.child("Price").getValue().toString());
                                }
                                if (nodeSnapshot.child("Provider").exists()){
                                    item.setPrice(nodeSnapshot.child("Provider").getValue().toString());
                                }
                                item.setImage(nodeSnapshot.child("image").getValue() == null ? ""
                                        : nodeSnapshot.child("image").getValue().toString());
                                item.setSellerName(nodeSnapshot.child("sellerName").getValue().toString());
                                item.setSellerPhone(nodeSnapshot.child("sellerPhone").getValue().toString());
                                //item.setPrice(nodeSnapshot.child("Price").getValue().toString());

                                items.add(item);
                                shareExpDate = item.getExpDate();
                                shareName = item.getName();
                                shareCatlogNumber = item.getCatalogNumber();
                            }


                        }

                        Log.d("tag", "populating data");
                        if (sellItemsAdapter != null)
                            sellItemsAdapter.updateItems(items);
                    }


                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void filterdata(long date) {
        ProductsRef.orderByChild("ForSale").equalTo("true").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot != null) {
                    Calendar calendar = Calendar.getInstance();
                    today = Calendar.getInstance().getTime();

                    items = new ArrayList<>();
                    progressBarLayout.setVisibility(View.GONE);


                    for (DataSnapshot nodeSnapshot : snapshot.getChildren()) {
                        Log.d("", "");

                        calendar.setTimeInMillis(Long.parseLong(nodeSnapshot.child("ExpDate").getValue().toString()));

                        long dayleft = getDifference(today, calendar.getTime());

                        Log.i("dayleft", "onDataChange: " + dayleft);
                        Log.i("today", "onDataChange: " + date);
                        if (dayleft == date) {

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
                            item.setImage(nodeSnapshot.child("image").getValue() == null ? ""
                                    : nodeSnapshot.child("image").getValue().toString());
                            item.setSellerName(nodeSnapshot.child("sellerName").getValue().toString());
                            item.setSellerPhone(nodeSnapshot.child("sellerPhone").getValue().toString());
                            //item.setPrice(nodeSnapshot.child("Price").getValue().toString());

                            items.add(item);
                            shareExpDate = item.getExpDate();
                            shareName = item.getName();
                            shareCatlogNumber = item.getCatalogNumber();
                        }


                    }

                    Log.d("tag", "populating data");
                    if (sellItemsAdapter != null)
                        sellItemsAdapter.updateItems(items);

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    public void dailogShare(String formatedDate, String name, String catalogNumber, String image, String id, String sellerName, String sellerPhone) {


        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(DisplaySellItemsActivity.this);

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dailog_sell_screen, null);
        dialogBuilder.setView(dialogView);
        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.setCancelable(true);

        TextView sName = dialogView.findViewById(R.id.sellername_textview);
        TextView sPhone = dialogView.findViewById(R.id.seller_phone_textview);

        ImageView whatsappBtn = dialogView.findViewById(R.id.whatsapp_btn);
        ImageView message = dialogView.findViewById(R.id.gmail_btn);

        sName.setText(sellerName);
        sPhone.setText(sellerPhone);

        whatsappBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send?phone=" + sellerPhone + "&text=" + "Hi, I would like to buy your food " + name)));
            }


        });


        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // The number on which you want to send SMS
                Uri uri = Uri.parse("smsto:" + sellerPhone);
                Intent it = new Intent(Intent.ACTION_SENDTO, uri);
                it.putExtra("sms_body", "Hi, I would like to buy your food " + name);
                startActivity(it);
            }


        });


        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();
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

            sellItemsAdapter.updateItems(filteredItems);


        }


    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    public String getFormatedDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");

        return dateFormat.format(date);
    }

    public long getDifference(Date startDate, Date endDate) {
        //milliseconds
        long different = endDate.getTime() - startDate.getTime();

        System.out.println("startDate : " + startDate);
        System.out.println("endDate : " + endDate);
        System.out.println("different : " + different);

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        return elapsedDays;
    }

    private void filterfood(String text, String text2) {
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
                        } else if (text2.equals(nodeSnapshot.child("Category").getValue().toString())) {
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
                        }


                    }

                    Log.d("tag", "populating data");
                    if (sellItemsAdapter != null)
                        sellItemsAdapter.updateItems(items);

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
