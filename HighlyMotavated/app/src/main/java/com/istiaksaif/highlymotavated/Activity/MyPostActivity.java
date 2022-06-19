package com.istiaksaif.highlymotavated.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.istiaksaif.highlymotavated.Adapter.PostProductListAdapter;
import com.istiaksaif.highlymotavated.Model.ProductItem;
import com.istiaksaif.highlymotavated.R;
import com.istiaksaif.highlymotavated.Utils.CheckInternet;

import java.util.ArrayList;
import java.util.List;

public class MyPostActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private ImageView addProduct;
    private RecyclerView productrecycler;
    private PostProductListAdapter postProductListAdapter;
    private ArrayList<ProductItem> productItemArrayList;
    private DatabaseReference databaseReference;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String uid = user.getUid();
    private TextView checkInternet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_post);

        checkInternet = findViewById(R.id.networkcheck);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("");
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.leftarrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        addProduct = findViewById(R.id.addproduct);
        addProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyPostActivity.this, AddProductActivity.class);
                startActivity(intent);
            }
        });
        databaseReference = FirebaseDatabase.getInstance().getReference();
        productItemArrayList = new ArrayList<>();

        productrecycler = findViewById(R.id.myPostRecycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        productrecycler.setLayoutManager(layoutManager);
        productrecycler.setHasFixedSize(true);
        GetData();
    }

    private void GetData(){
        Query query = databaseReference.child("Products").orderByChild("userId").equalTo(uid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ClearAll();
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    try {
                        ProductItem item = new ProductItem();
                        item.setProductName(snapshot.child("productName").getValue().toString());
                        item.setProductPrice(snapshot.child("productPrice").getValue().toString());
                        item.setProductId(snapshot.child("productId").getValue().toString());
                        item.setEndTimestamp(snapshot.child("endTimestamp").getValue().toString());
                        item.setBidders(Long.toString(snapshot.child("Bidders").getChildrenCount()));
                        String userid = snapshot.child("userId").getValue(String.class);
                        List<String> imageArray = new ArrayList<>();
                        for (DataSnapshot snapshot2: snapshot.child("Images").getChildren()){
                            String images = snapshot2.child("productImage").getValue(String.class);
                            imageArray.add(images);
                            item.setProductImage(imageArray.get(0));
                        }
                        item.setProductDescription(snapshot.child("productDescription").getValue().toString());
                        item.setCategory(snapshot.child("category").getValue().toString());
                        item.setSellType(snapshot.child("sellType").getValue().toString());
                        item.setUserId(userid);
                        productItemArrayList.add(item);

                        postProductListAdapter = new PostProductListAdapter(getApplicationContext(), productItemArrayList,"MyPost");
                        productrecycler.setAdapter(postProductListAdapter);
                        postProductListAdapter.notifyDataSetChanged();
                    }catch (Exception e){

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void ClearAll(){
        if (productItemArrayList !=null){
            productItemArrayList.clear();
            if (postProductListAdapter !=null){
                postProductListAdapter.notifyDataSetChanged();
            }
        }
        productItemArrayList = new ArrayList<>();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!CheckInternet.isConnectedToInternet(MyPostActivity.this)){
            checkInternet.setVisibility(View.VISIBLE);
        }else {
            checkInternet.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(!CheckInternet.isConnectedToInternet(MyPostActivity.this)){
            checkInternet.setVisibility(View.VISIBLE);
        }else {
            checkInternet.setVisibility(View.GONE);
        }
    }
}