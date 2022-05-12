package com.rtapps.moc;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SellItemsActivity extends AppCompatActivity implements View.OnClickListener {
    String itemName;
    String itemCatalogNumber;
    String itemImage;
    String sellerName;
    String sellerPhone;
    String itemID;
    Button sellBtn;
    TextView iName;
    TextView iCatalogNumber;
    RelativeLayout backBtn;
    EditText sName;
    EditText sPhone;
    String itemPrice;
    String userId;
    TextView iprice;
    RelativeLayout progressBarLayout;
    private DatabaseReference UpdateRef;

    @Override
    protected void onStart() {
        super.onStart();
        LanguageSet.setLocale(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell_items);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setStatusBarColor(SellItemsActivity.this.getResources().getColor(R.color.background));


        itemName = getIntent().getStringExtra("ItemName");
        itemCatalogNumber = getIntent().getStringExtra("CatalogNumber");
        itemImage = getIntent().getStringExtra("ItemImage");
        itemID = getIntent().getStringExtra("ID");
        itemPrice = getIntent().getStringExtra("ItemPrice");

        userId = getIntent().getStringExtra("UserId");

        init();


        iCatalogNumber.setText(itemCatalogNumber);
        iName.setText(itemName);
        iprice.setText(itemPrice);
    }

    private void init() {
        iName = findViewById(R.id.name_textview);
        iCatalogNumber = findViewById(R.id.catalog_textview);
        iprice = findViewById(R.id.price_textview);


        backBtn = findViewById(R.id.back_button_layout_sell);
        backBtn.setOnClickListener(this);
        sName = findViewById(R.id.edt_seller_name);
        sPhone = findViewById(R.id.edt_seller_price);
        progressBarLayout = findViewById(R.id.progress_bar_layout);
        progressBarLayout.setOnClickListener(this);


        sellBtn = findViewById(R.id.upload_data_btn_sell);
        sellBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_button_layout_sell:
                onBackPressed();
                finish();
                break;

            case R.id.upload_data_btn_sell:
                upLoadData();
                break;
        }
    }

    private void upLoadData() {

        sellerName = sName.getText().toString().trim();
        sellerPhone = sPhone.getText().toString().trim();
        boolean isError = false;

        if (TextUtils.isEmpty(sellerName)) {
            isError = true;
            sName.setError("Seller Name");
        }
        if (TextUtils.isEmpty(sellerPhone)) {
            isError = true;
            sPhone.setError("Seller Phone");
        }

        if (isError) {

            Toast.makeText(this, "Kindly fill all data", Toast.LENGTH_LONG).show();

        } else {

            progressBarLayout.setVisibility(View.VISIBLE);

            UpdateRef = FirebaseDatabase.getInstance().getReference().child("Food").child(userId).child(itemID).child("sellerName");
            UpdateRef.setValue(sellerName);
            UpdateRef = FirebaseDatabase.getInstance().getReference().child("Food").child(userId).child(itemID).child("sellerPhone");
            UpdateRef.setValue(sellerPhone);
            UpdateRef = FirebaseDatabase.getInstance().getReference().child("Food").child(userId).child(itemID).child("ForSale");
            UpdateRef.setValue("true").addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    progressBarLayout.setVisibility(View.INVISIBLE);
                    finish();

                }
            });


        }


    }


}