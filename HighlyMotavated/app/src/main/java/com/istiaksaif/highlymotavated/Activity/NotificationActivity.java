package com.istiaksaif.highlymotavated.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.istiaksaif.highlymotavated.Adapter.NotifyAdapter;
import com.istiaksaif.highlymotavated.Adapter.PostProductListAdapter;
import com.istiaksaif.highlymotavated.Model.NotifyItem;
import com.istiaksaif.highlymotavated.Model.ProductItem;
import com.istiaksaif.highlymotavated.R;
import com.istiaksaif.highlymotavated.Utils.CheckInternet;

import java.util.ArrayList;

public class NotificationActivity extends AppCompatActivity {

    private RecyclerView notifyRecycler;
    private Toolbar toolbar;
    private NotifyAdapter notifyAdapter;
    private ArrayList<NotifyItem> notifyArrayList;
    private DatabaseReference databaseReference;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String uid = user.getUid();
    private TextView checkInternet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
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
        notifyArrayList = new ArrayList<>();

        notifyRecycler = findViewById(R.id.notifyRecycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        notifyRecycler.setLayoutManager(layoutManager);
        notifyRecycler.setHasFixedSize(true);
        GetData();
    }

    private void GetData(){
        Query query = databaseReference.child("Notification").orderByChild("userId").equalTo(uid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ClearAll();
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    try {
                        NotifyItem item = new NotifyItem();
//                        item.setProductId(snapshot.child("productId").getValue().toString());
                        item.setTitle(snapshot.child("title").getValue().toString());
                        item.setMessage(snapshot.child("message").getValue().toString());
                        item.setDatetime(snapshot.child("timestamp").getValue().toString());
                        String userid = snapshot.child("userId").getValue().toString();
                        item.setUserId(userid);
                        notifyArrayList.add(item);

                        notifyAdapter = new NotifyAdapter(getApplicationContext(), notifyArrayList);
                        notifyRecycler.setAdapter(notifyAdapter);
                        notifyAdapter.notifyDataSetChanged();
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
        if (notifyArrayList !=null){
            notifyArrayList.clear();
            if (notifyAdapter !=null){
                notifyAdapter.notifyDataSetChanged();
            }
        }
        notifyArrayList = new ArrayList<>();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!CheckInternet.isConnectedToInternet(NotificationActivity.this)){
            checkInternet.setVisibility(View.VISIBLE);
        }else {
            checkInternet.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(!CheckInternet.isConnectedToInternet(NotificationActivity.this)){
            checkInternet.setVisibility(View.VISIBLE);
        }else {
            checkInternet.setVisibility(View.GONE);
        }
    }
}