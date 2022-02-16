package com.istiaksaif.testapp.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
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

import java.util.ArrayList;
import java.util.HashMap;

public class DealerDetailsActivity extends AppCompatActivity {

    private Toolbar toolBar;

    private DatabaseReference databaseReference;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String uid = user.getUid();
    private TextView dealerName,company,city,state,address;
    private RecyclerView phoneRecycler;
    private PhoneRetriveAdapter phoneAdapter;
    private ArrayList<Phone> phonelist;
    private Intent intent;
    private String dealerId,NewCompaniesId,limit;
    private CardView delete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dealer_details);
        intent = getIntent();
        dealerId = intent.getStringExtra("dealerId");
        NewCompaniesId = intent.getStringExtra("NewCompaniesId");
        limit = intent.getStringExtra("limit");

        databaseReference = FirebaseDatabase.getInstance().getReference();

        toolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.leftarrow);
        toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        dealerName = findViewById(R.id.l1);
        company = findViewById(R.id.textCompany);
        city = findViewById(R.id.textCity);
        state = findViewById(R.id.textState);
        address = findViewById(R.id.textAddress);
        delete = findViewById(R.id.delete);

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dealerDelete(limit);
            }
        });
        phonelist = new ArrayList<>();
        phoneRecycler = findViewById(R.id.phoneRecycler);
        phoneRecycler.setLayoutManager(new GridLayoutManager(DealerDetailsActivity.this,2));
        phoneRecycler.setHasFixedSize(true);

        GetDataFromFirebase();
    }

    private void dealerDelete(String limit) {
        String key = databaseReference.child("SeparateLogin").child("DeleteRequest").child(uid).push().getKey();
        HashMap<String, Object> result = new HashMap<>();
        result.put("status","deletePending");
        databaseReference.child("SeparateLogin").child("Dealers").child(dealerId).updateChildren(result);
        HashMap<String, Object> result1 = new HashMap<>();
        result1.put("dealerId",dealerId);
        result1.put("limit",limit);
        result1.put("deleteId",key);
        result1.put("NewCompaniesId",NewCompaniesId);
        databaseReference.child("SeparateLogin").child("DeleteRequest").child(key).updateChildren(result1);

//        int leftlimit=Integer.parseInt(limit)+1;
//        databaseReference.child("SeparateLogin").child("Dealers").child(dealerId).removeValue();
//        HashMap<String, Object> result2 = new HashMap<>();
//        result2.put("limit",String.valueOf(leftlimit));
//        databaseReference.child("SeparateLogin").child("New-Companies").child(NewCompaniesId).updateChildren(result2);
        Toast.makeText(DealerDetailsActivity.this, "Request remove dealer successfully", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void GetDataFromFirebase() {
        Query query = databaseReference.child("SeparateLogin").child("Dealers").child(dealerId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    dealerName.setText(snapshot.child("dealerName").getValue().toString());
                    company.setText(snapshot.child("company").getValue().toString());
                    address.setText(snapshot.child("fullAddress").getValue().toString());
                    city.setText(snapshot.child("city").getValue().toString());
                    state.setText(snapshot.child("state").getValue().toString());
                    String s = snapshot.child("status").getValue().toString();
                    if (s.equals("deletePending")){
                        delete.setVisibility(View.GONE);
                    }
                    Query query1 = databaseReference.child("SeparateLogin").child("Dealers")
                            .child(dealerId).child("phoneNumbers");
                    query1.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot2) {
                            ClearAll();
                            for (DataSnapshot snapshot1:snapshot2.getChildren()){
                                Phone ListItem = new Phone();
                                try {
                                    ListItem.setPhoneNumber(snapshot1.child("phoneNumber").getValue().toString());
                                    phonelist.add(ListItem);
                                }catch (Exception e){

                                }
                            }
                            phoneAdapter = new PhoneRetriveAdapter(DealerDetailsActivity.this, phonelist);
                            phoneRecycler.setAdapter(phoneAdapter);
                            phoneAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }catch (Exception e) {
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DealerDetailsActivity.this,"Some Thing Wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void ClearAll(){
        if (phonelist !=null){
            phonelist.clear();
            if (phoneAdapter !=null){
                phoneAdapter.notifyDataSetChanged();
            }
        }
        phonelist = new ArrayList<>();
    }
}