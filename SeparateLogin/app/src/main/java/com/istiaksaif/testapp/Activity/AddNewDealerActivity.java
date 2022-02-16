package com.istiaksaif.testapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.istiaksaif.testapp.Adapter.PhoneAdapter;
import com.istiaksaif.testapp.Model.Phone;
import com.istiaksaif.testapp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AddNewDealerActivity extends AppCompatActivity {

    public static Button saveButton;
    private Toolbar toolBar;

    private RecyclerView recyclerView;
    private PhoneAdapter phoneAdapter;
    private ArrayList<Phone> itemList;

    private TextInputLayout textInputLayoutState,textInputLayoutCity,companyLayout;
    private TextInputEditText dealerName,company,fullAddress;
    private MaterialAutoCompleteTextView state,city;
    private TextView addPhone,title;
    private String PROVINCE;

    private DatabaseReference databaseReference;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String uid = user.getUid();

    private Intent intent;
    private String dealerId,companyName,companyInfoId,limit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_dealer);
        intent = getIntent();
        dealerId = intent.getStringExtra("dealerId");
        companyName = intent.getStringExtra("companyName");
        companyInfoId = intent.getStringExtra("NewCompaniesId");
        limit = intent.getStringExtra("limit");

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

        databaseReference = FirebaseDatabase.getInstance().getReference();

        dealerName = findViewById(R.id.dealerName);
        company = findViewById(R.id.company);
        state = findViewById(R.id.state);
        city = findViewById(R.id.city);
        fullAddress = findViewById(R.id.fullAddress);
        addPhone = findViewById(R.id.addphone);
        title = findViewById(R.id.titleText);
        textInputLayoutState = findViewById(R.id.stateLayout);
        textInputLayoutCity = findViewById(R.id.cityLayout);
        companyLayout = findViewById(R.id.companyLayout);


        recyclerView = findViewById(R.id.phoneRecycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        itemList = new ArrayList<>();
        Phone ListItem = new Phone();
        ListItem.setLopen("increase");
        itemList.add(ListItem);

        addPhone = findViewById(R.id.addphone);
        addPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Phone ListItem = new Phone();
                ListItem.setLopen("increase");
                itemList.add(ListItem);
                phoneAdapter.notifyDataSetChanged();
            }
        });

        phoneAdapter = new PhoneAdapter(this,itemList);
        recyclerView.setAdapter(phoneAdapter);
        phoneAdapter.notifyDataSetChanged();

        if (!dealerId.equals("Add")){
            title.setText("Edit Dealer Info");
            companyLayout.setFocusable(false);
            company.setClickable(false);
            company.setFocusable(false);
            company.setFocusableInTouchMode(false);
            Query query2 = databaseReference.child("SeparateLogin").child("Dealers").child(dealerId);
            query2.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try {
                        dealerName.setText(snapshot.child("dealerName").getValue().toString());
                        company.setText(snapshot.child("company").getValue().toString());
                        fullAddress.setText(snapshot.child("fullAddress").getValue().toString());
                        city.setText(snapshot.child("city").getValue().toString());
                        state.setText(snapshot.child("state").getValue().toString());
                        Query query1 = databaseReference.child("SeparateLogin").child("Dealers")
                                .child(dealerId).child("phoneNumbers");
                        query1.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot2) {
                                itemList.clear();
                                for (DataSnapshot snapshot1:snapshot2.getChildren()){
                                    Phone ListItem = new Phone();
                                    try {
                                        String s = snapshot1.child("phoneNumber").getValue(String.class);
                                        List<String> intArray = new ArrayList<>();
                                        intArray.add(s);
                                        int count = 0;
                                        for (int i=0; i<intArray.size(); i++) {
                                            if (s.equals(intArray.get(i))) {
                                                count += 1;
                                            }
                                        }Toast.makeText(AddNewDealerActivity.this, count+"", Toast.LENGTH_SHORT).show();

                                        if(count==2){
                                            addPhone.setVisibility(View.GONE);
                                        }else {
                                            addPhone.setVisibility(View.VISIBLE);
                                        }
                                        ListItem.setPhoneNumber(s);
                                        itemList.add(ListItem);
                                    }catch (Exception e){

                                    }
                                }
                                phoneAdapter = new PhoneAdapter(AddNewDealerActivity.this, itemList);
                                recyclerView.setAdapter(phoneAdapter);
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
                    Toast.makeText(AddNewDealerActivity.this,"Some Thing Wrong", Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            title.setText("Add New Dealer");
            company.setText(companyName);
            companyLayout.setFocusable(false);
            company.setClickable(false);
            company.setFocusable(false);
            company.setFocusableInTouchMode(false);
            Query query3 = databaseReference.child("SeparateLogin").child("usersData").child(uid);
            query3.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try {
                        city.setText(snapshot.child("city").getValue().toString());
                        state.setText(snapshot.child("state").getValue().toString());
                        if(city.getText().toString().equals("") && state.getText().toString().equals("")){
                            Query query = databaseReference.child("Province");
                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    ArrayList<String> provinceList = new ArrayList<>();
                                    for(DataSnapshot dataSnapshot:snapshot.getChildren()) {
                                        try {
                                            String provinceName = dataSnapshot.child("name").getValue(String.class);

                                            provinceList.add(provinceName);
                                        }catch (Exception e){

                                        }
                                    }
                                    ArrayAdapter<String> arrayAdapterState = new ArrayAdapter<>(getApplicationContext(),R.layout.usertype_item,provinceList);
                                    ((MaterialAutoCompleteTextView) textInputLayoutState.getEditText()).setAdapter(arrayAdapterState);
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(AddNewDealerActivity.this,"Some Thing Wrong", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            });
                            if (dealerId.equals("Add")){
                                if(state.getText().toString().equals("")){
                                    textInputLayoutCity.setVisibility(View.GONE);
                                }else {
                                    textInputLayoutCity.setVisibility(View.VISIBLE);
                                }
                            }else {
                                textInputLayoutCity.setVisibility(View.VISIBLE);
                            }
                            state.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                                    if(state.getText().toString().equals("")){
                                        textInputLayoutCity.setVisibility(View.GONE);
                                    }else {
                                        textInputLayoutCity.setVisibility(View.VISIBLE);
                                    }
                                    PROVINCE = state.getText().toString();
                                    Query query11 = databaseReference.child("City").child(PROVINCE);
                                    query11.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            ArrayList<String> cityList = new ArrayList<>();
                                            for(DataSnapshot dataSnapshot:snapshot.getChildren()) {
                                                try {
                                                    String cityName = dataSnapshot.child("name").getValue(String.class);

                                                    cityList.add(cityName);
                                                }catch (Exception e){

                                                }
                                            }
                                            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getApplicationContext(),R.layout.usertype_item,cityList);
                                            ((MaterialAutoCompleteTextView) textInputLayoutCity.getEditText()).setAdapter(arrayAdapter);
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Toast.makeText(AddNewDealerActivity.this,"Some Thing Wrong", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }
                                    });
                                }
                            });
                        }else {
                            textInputLayoutCity.setEnabled(false);
                            textInputLayoutState.setEndIconVisible(false);
                            city.setClickable(false);
                            city.setFocusable(false);
                            city.setFocusableInTouchMode(false);
                            state.setClickable(false);
                            state.setFocusable(false);
                            state.setFocusableInTouchMode(false);
                        }
                    }catch (Exception e) {
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(AddNewDealerActivity.this,"Some Thing Wrong", Toast.LENGTH_SHORT).show();
                }
            });

        }

        saveButton = findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dealerId.equals("Add")) {
                    int leftlimit=Integer.parseInt(limit)-1;
                    databaseReference.child("SeparateLogin").child("Dealers").child(dealerId).removeValue();
                    String key = databaseReference.child("SeparateLogin").child("usersData").child(uid).push().getKey();
                    HashMap<String, Object> result1 = new HashMap<>();
                    result1.put("dealerName", dealerName.getText().toString());
                    result1.put("company", company.getText().toString());
                    result1.put("state", state.getText().toString());
                    result1.put("city", city.getText().toString());
                    result1.put("fullAddress", fullAddress.getText().toString());
                    result1.put("dealerId",key);
                    result1.put("NewCompaniesId",companyInfoId);
                    result1.put("status","pending");
                    databaseReference.child("SeparateLogin").child("Dealers").child(key).updateChildren(result1);
                    HashMap<String, Object> result2 = new HashMap<>();
                    result2.put("limit",String.valueOf(leftlimit));
                    databaseReference.child("SeparateLogin").child("New-Companies").child(companyInfoId).updateChildren(result2);
                    for (int i = 0; i < phoneAdapter.mdata.size(); i++) {
                        String unique = databaseReference.child(uid).push().getKey();
                        String PhoneNumber = phoneAdapter.mdata.get(i).getPhoneNumber();
                        HashMap<String, Object> result = new HashMap<>();
                        result.put("phoneNumber", PhoneNumber);
                        databaseReference.child("SeparateLogin").child("Dealers").child(key).
                                child("phoneNumbers").child(unique).updateChildren(result);
                        Toast.makeText(AddNewDealerActivity.this, "Dealer Added!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }else {
                    databaseReference.child("SeparateLogin").child("Dealers").child(dealerId).removeValue();
                    String key = databaseReference.child("SeparateLogin").child("usersData").child(uid).push().getKey();
                    HashMap<String, Object> result1 = new HashMap<>();
                    result1.put("dealerName", dealerName.getText().toString());
                    result1.put("company", company.getText().toString());
                    result1.put("state", state.getText().toString());
                    result1.put("city", city.getText().toString());
                    result1.put("fullAddress", fullAddress.getText().toString());
                    result1.put("dealerId",key);
                    result1.put("NewCompaniesId",companyInfoId);
                    result1.put("status","pending");
                    databaseReference.child("SeparateLogin").child("Dealers").child(key).updateChildren(result1);
                    for (int i = 0; i < phoneAdapter.mdata.size(); i++) {
                        String unique = databaseReference.child(uid).push().getKey();
                        String PhoneNumber = phoneAdapter.mdata.get(i).getPhoneNumber();
                        HashMap<String, Object> result = new HashMap<>();
                        result.put("phoneNumber", PhoneNumber);
                        databaseReference.child("SeparateLogin").child("Dealers").child(key).
                                child("phoneNumbers").child(unique).updateChildren(result);
                        Toast.makeText(AddNewDealerActivity.this, "Dealer Added!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }
        });
    }
}