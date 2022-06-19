package com.istiaksaif.highlymotavated.Activity;

import static com.istiaksaif.highlymotavated.Receiver.Constants.PUBLISHING_KEY;
import static com.istiaksaif.highlymotavated.Receiver.Constants.Paypal_CLIENT_ID;
import static com.istiaksaif.highlymotavated.Receiver.Constants.Paypal_SECRECT_KEY;
import static com.istiaksaif.highlymotavated.Receiver.Constants.SECRET_KEY;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import com.google.firebase.database.ValueEventListener;
import com.istiaksaif.highlymotavated.R;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class WalletActivity extends AppCompatActivity {

    private ImageView topup,withdraw;
    private TextView name,fullname,balance;
    private CardView stripeButton,payPalButton;
    private TextInputEditText topUpAmount;
    private RelativeLayout paymentpopup,bottomlayout,headerlayout;

    private Intent intent;
    private String sName,sBalance;
    private Toolbar toolbar;
    private DatabaseReference databaseReference;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String uid = user.getUid();

    //stripe
    private PaymentSheet paymentSheet;
    private String customerId,clientSecret,ephemeralKey,accessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);

        intent = getIntent();
        sName = intent.getStringExtra("name");
        sBalance = intent.getStringExtra("balance");
        databaseReference = FirebaseDatabase.getInstance().getReference();

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

        name = findViewById(R.id.name);
        fullname = findViewById(R.id.fullname);
        balance = findViewById(R.id.balance);
        topup = findViewById(R.id.topup);
        withdraw = findViewById(R.id.withdraw);
        String splitName[] = sName.split(" ");
        name.setText(splitName[0]);
        fullname.setText(sName);
        balance.setText("$ "+sBalance);
        topUpAmount = findViewById(R.id.topupamount);
        stripeButton = findViewById(R.id.stripebutton);
        payPalButton = findViewById(R.id.paypalbutton);
        paymentpopup = findViewById(R.id.paymentpopup);
        headerlayout = findViewById(R.id.header);
        bottomlayout = findViewById(R.id.bottomlayout);
        Animation animation = AnimationUtils.loadAnimation(this,R.anim.fade_in);
        topup.setOnClickListener(view -> {
            paymentpopup.setVisibility(View.VISIBLE);
            headerlayout.setVisibility(View.GONE);
            bottomlayout.setVisibility(View.GONE);
            paymentpopup.setAnimation(animation);
            stripeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    stripeButton.setClickable(false);
                    payment();
                }
            });
        });
        payPalButton.setOnClickListener(view -> {
            Toast.makeText(this, "clicked", Toast.LENGTH_SHORT).show();
            getPaypalAccessToken("10");
        });
        withdraw.setOnClickListener(view ->
                Toast.makeText(this, "Service is not available in this version", Toast.LENGTH_LONG).show()
        );
        PaymentConfiguration.init(this,PUBLISHING_KEY);
        paymentSheet = new PaymentSheet(this,paymentSheetResult -> {
            onPaymentResult(paymentSheetResult);
        });
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
            public Map<String, String> getHeaders()throws AuthFailureError{
                Map<String, String> header=new HashMap<>();
                header.put("Authorization","Bearer "+SECRET_KEY);
                return header;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(WalletActivity.this);
        requestQueue.add(stringRequest);
    }
    private void onPaymentResult(PaymentSheetResult paymentSheetResult) {
        if(paymentSheetResult instanceof PaymentSheetResult.Completed){
            stripeButton.setClickable(true);
            Toast.makeText(this, "payment done and balance will be update within few minutes", Toast.LENGTH_SHORT).show();
            databaseReference.child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String k = snapshot.child("key").getValue().toString();
                    databaseReference.child("usersData").child(k).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String b = snapshot.child("balanceTk").getValue().toString();
                            HashMap<String, Object> returnBalance = new HashMap<>();
                            returnBalance.put("balanceTk", Integer.parseInt(b)+Integer.parseInt(topUpAmount.getText().toString().trim()));
                            databaseReference.child("usersData").child(k).updateChildren(returnBalance);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            paymentpopup.setVisibility(View.GONE);
            headerlayout.setVisibility(View.VISIBLE);
        }
        stripeButton.setClickable(true);
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
        RequestQueue requestQueue = Volley.newRequestQueue(WalletActivity.this);
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
        RequestQueue requestQueue = Volley.newRequestQueue(WalletActivity.this);
        requestQueue.add(stringRequest);
    }

    private void PaymentFlow() {
        paymentSheet.presentWithPaymentIntent(clientSecret,
                new PaymentSheet.Configuration("Highly Motavated",
                        new PaymentSheet.CustomerConfiguration(customerId,ephemeralKey)));
    }

    private void payment() {
        String Amount = topUpAmount.getText().toString().trim();
        if (TextUtils.isEmpty(Amount)){
            Toast.makeText(WalletActivity.this, "please enter your amount", Toast.LENGTH_SHORT).show();
            return;
        }else if (Integer.parseInt(Amount)<10){
            Toast.makeText(WalletActivity.this, "minimum top up $10", Toast.LENGTH_SHORT).show();
            return;
        }
        getCustomerId(Amount);
    }

    private void getPaypalAccessToken(String amount){
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://api-m.sandbox.paypal.com/v1/oauth2/token",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            accessToken=jsonObject.getString("access_token");

                            Toast.makeText(WalletActivity.this, "null"+accessToken, Toast.LENGTH_LONG).show();
//                            getPaypalEphemeralKey(accessToken,amount);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(WalletActivity.this, "try to send report"+e, Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(WalletActivity.this, "error response"+error, Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders()throws AuthFailureError{
                Map<String, String> header=new HashMap<>();
                header.put("Authorization","Basic "+Paypal_CLIENT_ID+Paypal_SECRECT_KEY);
//                header.put("Content-Type","application/x-www-form-urlencoded");
                return header;
            }

            @Override
            protected  Map<String, String>getParams() throws AuthFailureError{
                Map<String, String> params = new HashMap<>();
                params.put("grant_type","client_credentials");
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(WalletActivity.this);
        requestQueue.add(stringRequest);
    }
    private void getPaypalEphemeralKey(String customerID,String amount) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://api.stripe.com/v1/ephemeral_keys",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            ephemeralKey=jsonObject.getString("id");

                            getPaypalClientSecret(customerID,amount);
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
        RequestQueue requestQueue = Volley.newRequestQueue(WalletActivity.this);
        requestQueue.add(stringRequest);
    }

    private void getPaypalClientSecret(String sCustomerId,String amount) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://api.stripe.com/v1/payment_intents",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            clientSecret=jsonObject.getString("client_secret");
//                            Toast.makeText(WalletActivity.this, ""+clientSecret, Toast.LENGTH_SHORT).show();
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
        RequestQueue requestQueue = Volley.newRequestQueue(WalletActivity.this);
        requestQueue.add(stringRequest);
    }
}