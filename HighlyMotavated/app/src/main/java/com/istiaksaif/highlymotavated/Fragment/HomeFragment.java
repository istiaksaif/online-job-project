package com.istiaksaif.highlymotavated.Fragment;

import static com.istiaksaif.highlymotavated.Utils.Utils.DataCache;
import static com.istiaksaif.highlymotavated.Utils.Utils.show;

import static java.util.Objects.requireNonNull;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
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
import com.istiaksaif.highlymotavated.Adapter.ProductListAdapter;
import com.istiaksaif.highlymotavated.Model.ProductItem;
import com.istiaksaif.highlymotavated.R;
import com.istiaksaif.highlymotavated.Utils.CheckInternet;
import com.istiaksaif.highlymotavated.Utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView productrecycler;
    private ProductListAdapter productListAdapter;
    private ArrayList<ProductItem> productItemArrayList;
    private DatabaseReference databaseReference;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String uid = user.getUid();
    private TextView checkInternet;
    private SwipeRefreshLayout swipeRefreshLayout;

    private boolean mIsLoading = false;
    private int mPostsPerPage = 6;
    private Boolean isScrolling = false;
    private int currentProducts, totalProducts, scrolledOutProducts;
    private List<ProductItem> currentPageProducts;
    private Boolean reachedEnd = false;

    private static final String COMMON_TAG = "AssociatedLifeCycle";
    private static final String Fragment = HomeFragment.class.getSimpleName();
    private static final String TAG = COMMON_TAG;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        productItemArrayList = new ArrayList<>();
        currentPageProducts = new ArrayList<>();
        checkInternet = view.findViewById(R.id.networkcheck);
        swipeRefreshLayout = view.findViewById(R.id.swip);

        productrecycler = view.findViewById(R.id.myPostRecycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        productrecycler.setLayoutManager(layoutManager);
//        layoutManager.setReverseLayout(true);
//        layoutManager.setStackFromEnd(true);
        productrecycler.setHasFixedSize(true);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(productrecycler.getContext(),
                layoutManager.getOrientation());
        productrecycler.addItemDecoration(dividerItemDecoration);
        productListAdapter = new ProductListAdapter(getContext());
        productrecycler.setAdapter(productListAdapter);

        if(DataCache.size() > 0){
            productListAdapter.addAll(DataCache);
        }else{
            GetProductsData(null);
        }

        swipeRefreshLayout.setOnRefreshListener(() -> {
            GetProductsData(null);
            swipeRefreshLayout.setRefreshing(false);
        });
        productrecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                //check for scroll state
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                currentProducts = layoutManager.getChildCount();
                totalProducts = layoutManager.getItemCount();
                scrolledOutProducts = ((LinearLayoutManager) recyclerView.getLayoutManager()).
                        findFirstVisibleItemPosition();

                if (isScrolling && (currentProducts + scrolledOutProducts ==
                        totalProducts)) {
                    isScrolling = false;

                    if (dy > 0) {
                        // Scrolling up
                        if(!reachedEnd){
                            GetProductsData(productListAdapter.getLastItemId());
                            swipeRefreshLayout.setRefreshing(true);
                        }else{
                            show(getActivity(),"No More Item Found");
                        }

                    } else {
                        // Scrolling down
                    }
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if(!CheckInternet.isConnectedToInternet(getActivity())){
            checkInternet.setVisibility(View.VISIBLE);
        }else {
            checkInternet.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!CheckInternet.isConnectedToInternet(getActivity())){
            checkInternet.setVisibility(View.VISIBLE);
        }else {
            checkInternet.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(!CheckInternet.isConnectedToInternet(getActivity())){
            checkInternet.setVisibility(View.VISIBLE);
        }else {
            checkInternet.setVisibility(View.GONE);
        }
    }

    private void GetProductsData(String nodeId) {
        mIsLoading=true;
        swipeRefreshLayout.setRefreshing(true);

        Query query;
        if (nodeId == null) {
            query = databaseReference.child("Products").orderByChild("productId").limitToFirst(mPostsPerPage);
        }
        else {
            query = databaseReference.child("Products").orderByChild("productId").startAt(nodeId).limitToFirst(mPostsPerPage);
        }
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<ProductItem> productModels = new ArrayList<>();
                if(snapshot!= null && snapshot.exists()){
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        if(dataSnapshot.getChildrenCount() > 0){
                            String userid = dataSnapshot.child("userId").getValue(String.class);
                            String category = dataSnapshot.child("category").getValue(String.class);
                            if (!userid.equals(uid)){
                                ProductItem item=new ProductItem();
                                item.setProductName(dataSnapshot.child("productName").getValue().toString());
                                item.setProductPrice(dataSnapshot.child("productPrice").getValue().toString());
                                item.setProductId(dataSnapshot.child("productId").getValue().toString());
                                item.setTimestamp(dataSnapshot.child("timestamp").getValue().toString());
                                item.setEndTimestamp(dataSnapshot.child("endTimestamp").getValue().toString());
                                item.setSellType(dataSnapshot.child("sellType").getValue().toString());
                                item.setProductDescription(dataSnapshot.child("productDescription").getValue().toString());
                                item.setBidders(Long.toString(dataSnapshot.child("Bidders").getChildrenCount()));
                                List<String> imageArray = new ArrayList<>();
                                for (DataSnapshot snapshot1: dataSnapshot.child("Images").getChildren()){
                                    try {
                                        String images = snapshot1.child("productImage").getValue(String.class);
                                        imageArray.add(images);
                                        item.setImageCount(imageArray.size());
                                        item.setProductImage(imageArray.get(0));
                                    }catch (Exception e){

                                    }
                                }
                                item.setUserId(userid);
                                requireNonNull(item).setProductId(dataSnapshot.getKey());
                                if(Utils.productsExists(dataSnapshot.getKey())){
                                    reachedEnd = true;
                                }else{
                                    reachedEnd=false;
                                    DataCache.add(item);
                                    productModels.add(item);
                                    currentPageProducts = productModels;
                                    currentProducts=productModels.size();
                                }
                            }
                        }else{
                            Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT).show();
                        }
                    }
                }else{
                    show(getActivity(), "Data Doesn't Exists or is Null");
                }
                if(!reachedEnd){
                    productListAdapter.addAll(productModels);
                }
                mIsLoading = false;
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                mIsLoading = false;
                swipeRefreshLayout.setRefreshing(false);
                show(getActivity(),error.getMessage());
            }
        });
    }

    @Override
    public void onStop() {
        DataCache.add(null);
        super.onStop();
        DataCache.clear();
    }
}