package com.istiaksaif.highlymotavated.Activity;

import static com.istiaksaif.highlymotavated.Receiver.Constants.PUBLISHING_KEY;
import static com.istiaksaif.highlymotavated.Receiver.Constants.SECRET_KEY;
import static com.istiaksaif.highlymotavated.Receiver.Constants.clientSecret;
import static com.istiaksaif.highlymotavated.Receiver.Constants.customerId;
import static com.istiaksaif.highlymotavated.Receiver.Constants.ephemeralKey;
import static com.istiaksaif.highlymotavated.Receiver.Constants.paymentSheet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.istiaksaif.highlymotavated.Adapter.CartItemAdapter;
import com.istiaksaif.highlymotavated.Model.CartItemModel;
import com.istiaksaif.highlymotavated.R;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import in.dd4you.appsconfig.DD4YouConfig;

public class CartActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private CardView checkOutButton,cashOnDelivery,stripeButton,paypalButton,addresscard,saveButton,
            continueShopButton,trackButton,walletButton;
    private TextInputEditText name,phone,address;
    private CartItemAdapter itemAdapter;
    private ArrayList<CartItemModel> cartItemList;

    private FirebaseAuth firebaseAuth ;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseUser user;
    private String uid;
    private TextView subtotal,deliveryCharge,totalPrice,totalitemprice,receiverAddress,receiverName,
            toolbartitle,receiverPhone,trackIdText;
    private int extraCharge=1,finalTotalPrice=0;
    private ImageView editaddress;
    private LinearLayout cartLayout,confirmDetailsLayout,confirmDetails,confirmOrder,editaddresslayout;
    private Toolbar toolbar;
    private DD4YouConfig trackId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        trackId = new DD4YouConfig(this);

        toolbartitle = findViewById(R.id.toolbartitle);
        toolbartitle.setText("My Cart");
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

        recyclerView = findViewById(R.id.cartrecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        user = firebaseAuth.getCurrentUser();
        uid = user.getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        cartItemList = new ArrayList<>();

        GetDataFromFirebase();
        cartLayout = findViewById(R.id.cartlayout);
        confirmDetailsLayout = findViewById(R.id.confirmdetailslayout);
        confirmDetails = findViewById(R.id.confirmdetails);
        confirmOrder = findViewById(R.id.confirmorder);
        cashOnDelivery = findViewById(R.id.cashonbutton);
        paypalButton = findViewById(R.id.paypalbutton);
        stripeButton = findViewById(R.id.stripebutton);
        editaddress = findViewById(R.id.editaddress);
        totalitemprice = findViewById(R.id.totalitemprice);

        checkOutButton = findViewById(R.id.checkoutbutton);

        subtotal = findViewById(R.id.subtotalprice);
        deliveryCharge = findViewById(R.id.deliveryprice);
        totalPrice = findViewById(R.id.producttotalprice);
        receiverName = findViewById(R.id.recevername);
        receiverPhone = findViewById(R.id.receverphone);
        receiverAddress = findViewById(R.id.receveraddress);
        addresscard = findViewById(R.id.addresscard);
        editaddresslayout = findViewById(R.id.editaddresslayout);
        name = findViewById(R.id.name);
        phone = findViewById(R.id.phone);
        address = findViewById(R.id.address);
        saveButton = findViewById(R.id.savebutton);
        trackIdText =findViewById(R.id.trackidtext);
        continueShopButton = findViewById(R.id.continue_shop_btn);
        trackButton = findViewById(R.id.track_btn);
        walletButton = findViewById(R.id.walletbutton);

        caculatateprice();
        Animation animation = AnimationUtils.loadAnimation(this,R.anim.fade_in);
        Animation animationout = AnimationUtils.loadAnimation(this,R.anim.fade_out);
        checkOutButton.setOnClickListener(view -> {
            if (totalPrice.getText().toString().equals("$ 0")) {
                Toast.makeText(CartActivity.this, "No item found", Toast.LENGTH_SHORT).show();
            }else {
                toolbartitle.setText("Place Order");
                cartLayout.setVisibility(View.GONE);
                confirmDetailsLayout.setVisibility(View.VISIBLE);
                confirmDetailsLayout.setAnimation(animation);
                totalitemprice.setText("  " + totalitemprice.getText().toString()+" items: " + totalPrice.getText().toString());
                receiverName.setText(getIntent().getStringExtra("name"));
                receiverPhone.setText(getIntent().getStringExtra("phone"));
                receiverAddress.setText(getIntent().getStringExtra("address"));
            }
        });
        stripeButton.setOnClickListener(View -> {
            stripeButton.setClickable(false);
            payment();
        });
        editaddress.setOnClickListener(View -> {
            addresscard.setAnimation(animationout);
            addresscard.setVisibility(android.view.View.GONE);
            name.setText(receiverName.getText().toString());
            phone.setText(receiverPhone.getText().toString());
            address.setText(receiverAddress.getText().toString());
            editaddresslayout.setAnimation(animation);
            editaddresslayout.setVisibility(android.view.View.VISIBLE);
            name.setSelection(name.getText().length());
            phone.setSelection(phone.getText().length());
            address.setSelection(address.getText().length());
        });
        saveButton.setOnClickListener(View -> {
            editaddresslayout.setAnimation(animationout);
            editaddresslayout.setVisibility(android.view.View.GONE);
            receiverName.setText(name.getText().toString());
            receiverPhone.setText(phone.getText().toString());
            receiverAddress.setText(address.getText().toString());
            addresscard.setAnimation(animation);
            addresscard.setVisibility(android.view.View.VISIBLE);
        });
        paypalButton.setOnClickListener(View -> {
            paypalButton.setClickable(false);
        });
        cashOnDelivery.setOnClickListener(View -> {
            cashOnDelivery.setClickable(false);
            orderConfirm("cash on delivery",0,0);
        });
        walletButton.setOnClickListener(View -> {
            GetRealtimeBalance();
        });
        PaymentConfiguration.init(this,PUBLISHING_KEY);
        paymentSheet = new PaymentSheet(this,paymentSheetResult -> {
            onPaymentResult(paymentSheetResult);
        });
        continueShopButton.setOnClickListener(view -> {
            Intent intent = new Intent(CartActivity.this,UserHomeActivity.class);
            startActivity(intent);
            finish();
        });
    }
    private void GetRealtimeBalance() {
        Query query = databaseReference.child("usersData").orderByChild("userId").equalTo(uid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    try {
                        int currentBalance = Integer.parseInt(snapshot.child("balanceTk").getValue().toString());
                        String Amount = totalPrice.getText().toString().trim();
                        String splitAmount[] = Amount.split(" ");
                        int payBal = Integer.parseInt(splitAmount[1]);
                        if(currentBalance<payBal){
                            Toast.makeText(CartActivity.this, "don't have sufficient balance in your account", Toast.LENGTH_LONG).show();
                        }else {
                            walletButton.setClickable(false);
                            orderConfirm("wallet payment",currentBalance,payBal);
                        }
                    } catch (Exception e) {

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void caculatateprice(){
        databaseReference.child("carts").child(uid).orderByChild("status").equalTo("pending")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int itemTotalPrice = 0;
                        int subTotal=0;
                        if(snapshot.exists()) {
                            for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                String i = snapshot1.child("id").getValue().toString();
                                for (DataSnapshot dataSnapshot: snapshot1.child("cartItem").getChildren()){
                                    String price = dataSnapshot.child("itemtotalprice").getValue().toString();
                                    itemTotalPrice = Integer.parseInt(price);

                                    subTotal += itemTotalPrice;
                                    finalTotalPrice = subTotal + extraCharge;

                                    subtotal.setText("$ " + String.valueOf(subTotal));
                                    deliveryCharge.setText("$ " + String.valueOf(extraCharge));
                                    totalPrice.setText("$ " + String.valueOf(finalTotalPrice));
                                }
                            }
                        }else {
                            subtotal.setText("$ " + String.valueOf(0));
                            deliveryCharge.setText("$ " + String.valueOf(0));
                            totalPrice.setText("$ " + String.valueOf(0));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    private void GetDataFromFirebase() {

        Query query = databaseReference.child("carts").child(uid).orderByChild("status").equalTo("pending");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot  snapshot1: dataSnapshot.getChildren()){
                    String id = snapshot1.child("id").getValue().toString();
                    String count = Long.toString(snapshot1.child("cartItem").getChildrenCount());
                    totalitemprice.setText(count);
                    databaseReference.child("carts").child(uid).child(id).child("cartItem")
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot2) {
                                    ClearAll();
                                    for(DataSnapshot  snapshot: snapshot2.getChildren()){
                                        CartItemModel productItem = new CartItemModel();
                                        productItem.setCartId(snapshot.child("cartId").getValue().toString());
                                        productItem.setImage(snapshot.child("image").getValue().toString());
                                        productItem.setName(snapshot.child("name").getValue().toString());
                                        productItem.setPrice(snapshot.child("price").getValue().toString());
                                        productItem.setProductId(snapshot.child("productId").getValue().toString());
                                        productItem.setQuantity(snapshot.child("quantity").getValue().toString());
                                        productItem.setItemtotalprice(snapshot.child("itemtotalprice").getValue().toString());
                                        productItem.setId(id);
                                        cartItemList.add(productItem);
                                    }
                                    itemAdapter = new CartItemAdapter(CartActivity.this,cartItemList);
                                    recyclerView.setAdapter(itemAdapter);
                                    itemAdapter.notifyDataSetChanged();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
    }

    private void ClearAll(){
        if (cartItemList!=null){
            cartItemList.clear();
            if (itemAdapter!=null){
                itemAdapter.notifyDataSetChanged();
            }
        }
        cartItemList = new ArrayList<>();
    }

    private void getCustomerId(String amount){
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://api.stripe.com/v1/customers",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            customerId=jsonObject.getString("id");

                            getEphemeralKey(customerId,amount);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            public Map<String, String> getHeaders()throws AuthFailureError {
                Map<String, String> header=new HashMap<>();
                header.put("Authorization","Bearer "+SECRET_KEY);
                return header;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(CartActivity.this);
        requestQueue.add(stringRequest);
    }
    private void getEphemeralKey(String customerID,String amount) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://api.stripe.com/v1/ephemeral_keys",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            ephemeralKey=jsonObject.getString("id");

                            getClientSecret(customerID,amount);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            public Map<String, String> getHeaders()throws AuthFailureError{
                Map<String, String> header=new HashMap<>();
                header.put("Authorization","Bearer "+SECRET_KEY);
                header.put("Stripe-Version","2020-08-27");
                return header;
            }

            @Override
            protected  Map<String, String>getParams() throws AuthFailureError{
                Map<String, String> params = new HashMap<>();
                params.put("customer",customerID);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(CartActivity.this);
        requestQueue.add(stringRequest);
    }
    private void getClientSecret(String sCustomerId,String amount) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://api.stripe.com/v1/payment_intents",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            clientSecret=jsonObject.getString("client_secret");
                            PaymentFlow();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            public Map<String, String> getHeaders()throws AuthFailureError{
                Map<String, String> header=new HashMap<>();
                header.put("Authorization","Bearer "+SECRET_KEY);
                return header;
            }

            @Override
            protected  Map<String, String>getParams() throws AuthFailureError{
                Map<String, String> params = new HashMap<>();
                params.put("customer",sCustomerId);
                params.put("amount",amount+"00");
                params.put("currency","usd");
                params.put("automatic_payment_methods[enabled]","true");
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(CartActivity.this);
        requestQueue.add(stringRequest);
    }
    private void onPaymentResult(PaymentSheetResult paymentSheetResult) {
        stripeButton.setClickable(true);
        if(paymentSheetResult instanceof PaymentSheetResult.Completed){
            orderConfirm("done via stripe",0,0);
        }
    }

    private void orderConfirm(String payStatus,int currentBalance,int payAmount) {
        String s = trackId.generateUniqueID(10);
        databaseReference.child("carts").child(uid).orderByChild("status").equalTo("pending")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                            try {
                                String id = dataSnapshot.child("id").getValue().toString();

                                HashMap<String, Object> result11 = new HashMap<>();
                                result11.put("status", "orderConfirm");
                                result11.put("payStatus", payStatus);
                                result11.put("trackId", "#"+s);
                                result11.put("name", receiverName.getText().toString());
                                result11.put("phone", receiverPhone.getText().toString());
                                result11.put("address", receiverAddress.getText().toString());
                                databaseReference.child("carts").child(uid).child(id).updateChildren(result11);
                                trackIdText.setText("#"+s);
                            }catch (Exception e){

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        if(payStatus.equals("wallet payment")){
            UpdateBalance(currentBalance,payAmount);
        }
        confirmDetails.setVisibility(View.GONE);
        confirmOrder.setVisibility(View.VISIBLE);
    }

    private void PaymentFlow() {
        paymentSheet.presentWithPaymentIntent(clientSecret,
                new PaymentSheet.Configuration("Highly Motavated",
                        new PaymentSheet.CustomerConfiguration(customerId,ephemeralKey)));
    }
    private void payment() {
        String Amount = totalPrice.getText().toString().trim();
        String splitAmount[] = Amount.split(" ");
        getCustomerId(splitAmount[1]);
    }
    private void UpdateBalance(int currentBalance, int payAmount) {
        HashMap<String, Object> updateBalance = new HashMap<>();
        updateBalance.put("balanceTk", currentBalance - payAmount);
        databaseReference.child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String k = snapshot.child("key").getValue().toString();
                databaseReference.child("usersData").child(k).updateChildren(updateBalance);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}