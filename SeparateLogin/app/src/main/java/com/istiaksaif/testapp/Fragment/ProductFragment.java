package com.istiaksaif.testapp.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.istiaksaif.testapp.Activity.AddNewDealerActivity;
import com.istiaksaif.testapp.Activity.AddProductActivity;
import com.istiaksaif.testapp.Adapter.ProductItemAdapter;
import com.istiaksaif.testapp.Model.DealerItem;
import com.istiaksaif.testapp.Model.ProductItemList;
import com.istiaksaif.testapp.R;

import java.util.ArrayList;

public class ProductFragment extends Fragment {

    private ExtendedFloatingActionButton fab;

    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String uid = user.getUid();
    private RecyclerView productRecyclerView;
    private ProductItemAdapter itemAdapter;
    private ArrayList<ProductItemList> productItemLists;
    private DatabaseReference productDatabaseRef;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fab = (ExtendedFloatingActionButton) view.findViewById(R.id.floatingButtonAdd);
        fab.bringToFront();

        productRecyclerView = view.findViewById(R.id.productRecyclerView);
        productRecyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        productRecyclerView.setHasFixedSize(true);

        productDatabaseRef = FirebaseDatabase.getInstance().getReference();
        productItemLists = new ArrayList<>();

        GetDataFromFirebase();

        Query query = productDatabaseRef.child("SeparateLogin").child("New-Companies").orderByChild("userId").equalTo(uid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    try{
                        String companyname = dataSnapshot.child("companyName").getValue().toString();
                        String NewCompaniesId = dataSnapshot.child("NewCompaniesId").getValue().toString();
                        String limit = dataSnapshot.child("limitp").getValue().toString();

                        if(companyname.equals("")){
                            fab.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Toast.makeText(getActivity(), "Add company first", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }else if(limit.equals("0")){
                            fab.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Toast.makeText(getActivity(), "No limit ", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }else {
                            fab.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(getActivity(), AddProductActivity.class);
                                    intent.putExtra("productId","Add");
                                    intent.putExtra("companyName",companyname);
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
    }

    private void GetDataFromFirebase() {
        Query query = productDatabaseRef.child("SeparateLogin").child("Product").orderByChild("userId").equalTo(uid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ClearAll();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    try {
                        ProductItemList productItem = new ProductItemList();
                        productItem.setProductImage(snapshot.child("image").getValue().toString());
                        productItem.setProductName(snapshot.child("productName").getValue().toString());
                        productItem.setProductPrice(snapshot.child("price").getValue().toString());
                        productItem.setProductId(snapshot.child("productId").getValue().toString());
                        productItem.setStatus(snapshot.child("status").getValue().toString());
                        productItemLists.add(productItem);
                    }catch (Exception e){

                    }
                }
                itemAdapter = new ProductItemAdapter(getContext(), productItemLists);
                productRecyclerView.setAdapter(itemAdapter);
                itemAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void ClearAll(){
        if (productItemLists!=null){
            productItemLists.clear();
            if (itemAdapter!=null){
                itemAdapter.notifyDataSetChanged();
            }
        }
        productItemLists = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_product, container, false);
        return view;
    }
}