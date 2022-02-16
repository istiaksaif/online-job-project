package com.istiaksaif.testapp.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.istiaksaif.testapp.Adapter.PhoneRetriveAdapter;
import com.istiaksaif.testapp.Model.Phone;
import com.istiaksaif.testapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ProductDetailsActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private DatabaseReference databaseReference;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String uid = user.getUid();
    private TextView productName,company,price,sideEffects,substitute,editDealerButton,composition,pack;
    private ImageView productimage;
    private Intent intent;
    private String productId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        intent = getIntent();
        productId = intent.getStringExtra("productid");
        databaseReference = FirebaseDatabase.getInstance().getReference();

        toolbar = findViewById(R.id.drtoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.leftarrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        productName = findViewById(R.id.productName);
        price = findViewById(R.id.textPrice);
        company = findViewById(R.id.textCompany);
        sideEffects = findViewById(R.id.textSideEffects);
        substitute = findViewById(R.id.textSubstitute);
        productimage = findViewById(R.id.productimage);
        composition = findViewById(R.id.textcomposition);
        pack = findViewById(R.id.textPack);
        editDealerButton = findViewById(R.id.editDealerBtn);
        editDealerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProductDetailsActivity.this, AddProductActivity.class);
                intent.putExtra("productId",productId);
                startActivity(intent);
            }
        });
        GetDataFromFirebase();
    }
    private void GetDataFromFirebase() {

        Query query = databaseReference.child("SeparateLogin").child("Product").child(productId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    productName.setText(snapshot.child("productName").getValue().toString());
                    company.setText(snapshot.child("company").getValue().toString());
                    price.setText(snapshot.child("price").getValue().toString()+" Rupee");
                    substitute.setText(snapshot.child("substitute").getValue().toString());
                    sideEffects.setText(snapshot.child("sideEffects").getValue().toString());
                    composition.setText(snapshot.child("composition").getValue().toString());
                    pack.setText(snapshot.child("package").getValue().toString());
                    String img = snapshot.child("image").getValue().toString();

                    try {
                        if (img.equals("")){
                            productimage.setImageResource(R.drawable.ic_logo);
                        }else {
                            Picasso.get().load(img).resize(480,480).into(productimage);
                        }
                    }catch (Exception e){
                        Picasso.get().load(R.drawable.dropdown).into(productimage);
                    }
                }catch (Exception e) {
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProductDetailsActivity.this,"Some Thing Wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }
}