package com.istiaksaif.testapp.Fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
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
import com.istiaksaif.testapp.Activity.AddNewDealerActivity;
import com.istiaksaif.testapp.Activity.HomeActivity;
import com.istiaksaif.testapp.Adapter.DealerAdapter;
import com.istiaksaif.testapp.Model.DealerItem;
import com.istiaksaif.testapp.R;

import java.util.ArrayList;
import java.util.HashMap;

public class HomeFragment extends Fragment {

    private RecyclerView dealerRecycler;
    private DealerAdapter dealerAdapter;
    private ArrayList<DealerItem> dealerItems;
    private DatabaseReference dealerDatabaseRef;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String uid = user.getUid();


    private TextInputLayout companyLayout;
    private TextInputEditText division;
    private MaterialAutoCompleteTextView companyName;
    private MaterialButton submitButton,addButton;
    RelativeLayout buttonLayout;
    private ExtendedFloatingActionButton fab;
    private TextView newCompany;

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private EditText newcompanyname;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        companyName = view.findViewById(R.id.companyName);
        division = view.findViewById(R.id.division);
        companyLayout = view.findViewById(R.id.companyLayout);
        submitButton = view.findViewById(R.id.submit);
        buttonLayout = view.findViewById(R.id.buttonLayout);
        newCompany = view.findViewById(R.id.newCompany);
//        newCompany.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                createpopupDiaglog();
//            }
//        });

        fab = (ExtendedFloatingActionButton) view.findViewById(R.id.floatingButtonAdd);
        fab.bringToFront();

        dealerDatabaseRef = FirebaseDatabase.getInstance().getReference();
        dealerItems = new ArrayList<>();

        Query query = dealerDatabaseRef.child("SeparateLogin").child("New-Companies").orderByChild("userId").equalTo(uid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    try{
                        String companyname = dataSnapshot.child("companyName").getValue().toString();
                        String dv = dataSnapshot.child("division").getValue().toString();
                        String NewCompaniesId = dataSnapshot.child("NewCompaniesId").getValue().toString();
                        String limit = dataSnapshot.child("limit").getValue().toString();

                        companyName.setText(companyname);
                        division.setText(dv);
                        GetDataFromFirebase(NewCompaniesId,limit);
                        if(companyName.getText().toString().equals("") && division.getText().toString().equals("")){
                            buttonLayout.setVisibility(View.VISIBLE);
                        }else {
                            buttonLayout.setVisibility(View.GONE);
                            newCompany.setVisibility(View.GONE);
                            companyLayout.setEnabled(false);
                            companyLayout.setEndIconVisible(false);
                            companyName.setFocusable(false);
                            division.setClickable(false);
                            division.setFocusable(false);
                            division.setFocusableInTouchMode(false);
                        }
                        if(limit.equals("0")){
                            fab.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Toast.makeText(getActivity(), "No limit ", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }else{
                            fab.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(getActivity(), AddNewDealerActivity.class);
                                    intent.putExtra("dealerId","Add");
                                    intent.putExtra("companyName",companyName.getText().toString());
                                    intent.putExtra("NewCompaniesId",NewCompaniesId);
                                    intent.putExtra("limit",limit);
                                    startActivity(intent);
                                }
                            });
                        }
                    }catch (Exception e){

                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(),"Some Thing Wrong", Toast.LENGTH_SHORT).show();
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Info();
            }
        });
//        Query query1 = dealerDatabaseRef.child("Companies");
//        query1.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                ArrayList<String> companyList = new ArrayList<>();
//                for(DataSnapshot dataSnapshot:snapshot.getChildren()) {
//                    try {
//                        String provinceName = dataSnapshot.child("name").getValue(String.class);
//
//                        companyList.add(provinceName);
//                    }catch (Exception e){
//
//                    }
//                }
//                ArrayAdapter<String> arrayAdapterState = new ArrayAdapter<>(getActivity(),R.layout.usertype_item,companyList);
//                ((MaterialAutoCompleteTextView) companyLayout.getEditText()).setAdapter(arrayAdapterState);
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(getActivity(),"Some Thing Wrong", Toast.LENGTH_SHORT).show();
//            }
//        });

        dealerRecycler = view.findViewById(R.id.dealerRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        dealerRecycler.setLayoutManager(layoutManager);
        dealerRecycler.setHasFixedSize(true);
    }

    private void GetDataFromFirebase(String NewCompaniesId,String limit) {
        Query query = dealerDatabaseRef.child("SeparateLogin").child("Dealers").orderByChild("NewCompaniesId").equalTo(NewCompaniesId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ClearAll();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    try {
                        DealerItem dealerItem = new DealerItem();
                        dealerItem.setDealerName(snapshot.child("dealerName").getValue().toString());
                        dealerItem.setStatus(snapshot.child("status").getValue().toString());
                        dealerItem.setDealerId(snapshot.child("dealerId").getValue(String.class));
                        dealerItem.setNewCompaniesId(NewCompaniesId);
                        dealerItem.setLimit(limit);

                        dealerItems.add(dealerItem);
                    } catch (Exception e) {

                    }
                }
                dealerAdapter = new DealerAdapter(getActivity(), dealerItems);
                dealerRecycler.setAdapter(dealerAdapter);
                dealerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void ClearAll(){
        if (dealerItems !=null){
            dealerItems.clear();
            if (dealerAdapter !=null){
                dealerAdapter.notifyDataSetChanged();
            }
        }
        dealerItems = new ArrayList<>();
    }

    private void Info() {
        String CompanyName = companyName.getText().toString();
        String Division = division.getText().toString();

        if (TextUtils.isEmpty(CompanyName)){
            Toast.makeText(getActivity(), "please enter your Designation", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (TextUtils.isEmpty(Division)){
            Toast.makeText(getActivity(), "please enter workingIn", Toast.LENGTH_SHORT).show();
            return;
        }

        String key = dealerDatabaseRef.child("SeparateLogin").child("New-Companies").push().getKey();
        HashMap<String, Object> result = new HashMap<>();
        result.put("companyName", companyName.getText().toString());
        result.put("division", division.getText().toString());
        result.put("userId", uid);
        result.put("NewCompaniesId", key);
        result.put("limit","2");

        dealerDatabaseRef.child("SeparateLogin").child("New-Companies").child(key).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        dealerDatabaseRef.child("SeparateLogin").child("New-Companies").child(key).updateChildren(result);
                        Toast.makeText(getActivity(),"Submitted Successfully!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

//    public void createpopupDiaglog(){
//        dialogBuilder = new AlertDialog.Builder(getActivity());
//        final View contactPopupView = getLayoutInflater().inflate(R.layout.addcompany,null);
//        newcompanyname = (EditText)contactPopupView.findViewById(R.id.newcompanyname);
//        addButton = (MaterialButton) contactPopupView.findViewById(R.id.addbtn);
//
//        dialogBuilder.setView(contactPopupView);
//        dialog = dialogBuilder.create();
//        dialog.show();
//
//        addButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                HashMap<String, Object> result = new HashMap<>();
//                result.put("name", newcompanyname.getText().toString());
//
//                dealerDatabaseRef.child("Companies").addListenerForSingleValueEvent(
//                        new ValueEventListener() {
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                String key = dealerDatabaseRef.push().getKey();
//                                dealerDatabaseRef.child("Companies").child(key).updateChildren(result);
//                                Toast.makeText(getActivity(),"Added Company Successfully!", Toast.LENGTH_SHORT).show();
//                                dialog.dismiss();
//                            }
//
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError error) {
//
//                            }
//                        });
//            }
//        });
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        return view;
    }
}