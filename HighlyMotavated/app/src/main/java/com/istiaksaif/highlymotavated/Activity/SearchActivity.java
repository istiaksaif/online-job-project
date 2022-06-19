package com.istiaksaif.highlymotavated.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.istiaksaif.highlymotavated.Adapter.PostProductListAdapter;
import com.istiaksaif.highlymotavated.Model.ProductItem;
import com.istiaksaif.highlymotavated.R;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    private RecyclerView productRecyclerView;
    private PostProductListAdapter itemAdapter;
    private ArrayList<ProductItem> productItemLists=new ArrayList<>();
    private DatabaseReference searchDatabaseRef;
    private SearchView searchView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
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

        searchView = findViewById(R.id.searchview);
        searchDatabaseRef = FirebaseDatabase.getInstance().getReference();
        productRecyclerView = findViewById(R.id.searchproductitemrecycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        productRecyclerView.setLayoutManager(layoutManager);
    }

    @Override
    public void onStart() {
        super.onStart();
        Query query = searchDatabaseRef.child("Products");
        if(query!= null){
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        for (DataSnapshot snapshot:dataSnapshot.getChildren()){
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
                            productItemLists.add(item);

                        }
                        itemAdapter = new PostProductListAdapter(SearchActivity.this,productItemLists,"BidHistory");
                        productRecyclerView.setAdapter(itemAdapter);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(SearchActivity.this,"something wrong",Toast.LENGTH_SHORT).show();
                }
            });
        }
        if(searchView !=null){
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    search(newText);
                    return true;
                }
            });
        }
    }

    private void search(String str) {
        ArrayList<ProductItem> searchLists = new ArrayList<>();
        for(ProductItem obj:productItemLists){
            if(obj.getProductName().toLowerCase().contains(str.toLowerCase())){
                searchLists.add(obj);
            }
        }
        itemAdapter = new PostProductListAdapter(SearchActivity.this,searchLists,"BidHistory");
        productRecyclerView.setAdapter(itemAdapter);
    }
}