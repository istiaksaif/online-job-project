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
import android.widget.Toast;

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

public class BidHistoryActivity extends AppCompatActivity {
    private Toolbar toolbar;
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
        setContentView(R.layout.activity_bid_history);

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

        databaseReference = FirebaseDatabase.getInstance().getReference();
        productItemArrayList = new ArrayList<>();

        productrecycler = findViewById(R.id.myPostRecycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        productrecycler.setLayoutManager(layoutManager);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        productrecycler.setHasFixedSize(true);
        GetData();
        ClearAll();
    }
    private void checkBiddingList(String getProductId) {
        Query query1 = databaseReference.child("Products").child(getProductId).child("Bidders").orderByChild("userId").equalTo(uid);
        query1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot!= null && snapshot.exists()){
                    LoadData(getProductId);
                }else {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void LoadData(String getProductId) {
        Query query = databaseReference.child("Products").child(getProductId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    ProductItem item = new ProductItem();
                    item.setProductName(snapshot.child("productName").getValue().toString());
                    item.setProductPrice(snapshot.child("productPrice").getValue().toString());
                    item.setProductId(snapshot.child("productId").getValue().toString());
                    item.setEndTimestamp(snapshot.child("endTimestamp").getValue().toString());
                    item.setBidders(Long.toString(snapshot.child("Bidders").getChildrenCount()));
                    String userid = snapshot.child("userId").getValue(String.class);
                    item.setSellType(snapshot.child("sellType").getValue().toString());
                    List<String> imageArray = new ArrayList<>();
                    for (DataSnapshot snapshot1: snapshot.child("Images").getChildren()){
                        try {
                            String images = snapshot1.child("productImage").getValue(String.class);
                            imageArray.add(images);
                            item.setImageCount(imageArray.size());
                            item.setProductImage(imageArray.get(0));
                        }catch (Exception e){

                        }
                    }
                    item.setUserId(userid);
                    productItemArrayList.add(item);

                    postProductListAdapter = new PostProductListAdapter(BidHistoryActivity.this, productItemArrayList,"BidHistory");
                    productrecycler.setAdapter(postProductListAdapter);
                    postProductListAdapter.notifyDataSetChanged();
                }catch (Exception e){

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void GetData(){
        Query query = databaseReference.child("Products");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    checkBiddingList(snapshot.child("productId").getValue().toString());
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
        if(!CheckInternet.isConnectedToInternet(BidHistoryActivity.this)){
            checkInternet.setVisibility(View.VISIBLE);
        }else {
            checkInternet.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(!CheckInternet.isConnectedToInternet(BidHistoryActivity.this)){
            checkInternet.setVisibility(View.VISIBLE);
        }else {
            checkInternet.setVisibility(View.GONE);
        }
    }
}