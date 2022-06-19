package com.istiaksaif.highlymotavated.Activity;

import static com.istiaksaif.highlymotavated.Receiver.Constants.Content_Type;
import static com.istiaksaif.highlymotavated.Receiver.Constants.NOTIFICATION_URL;
import static com.istiaksaif.highlymotavated.Receiver.Constants.SERVER_KEY;
import static com.istiaksaif.highlymotavated.Receiver.NotificationHelper.getToken;
import static com.istiaksaif.highlymotavated.Utils.GetServerTimeContext.getCurrentDateForNotification;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.istiaksaif.highlymotavated.Adapter.BidderListAdapter;
import com.istiaksaif.highlymotavated.Adapter.SliderAdapterimage;
import com.istiaksaif.highlymotavated.Model.BiddingItem;
import com.istiaksaif.highlymotavated.Model.CartItemModel;
import com.istiaksaif.highlymotavated.Model.ProductItem;
import com.istiaksaif.highlymotavated.Model.SliderImageModel;
import com.istiaksaif.highlymotavated.R;
import com.istiaksaif.highlymotavated.Utils.DepthPageTransformer;
import com.istiaksaif.highlymotavated.Utils.GetServerTimeContext;
import com.istiaksaif.highlymotavated.Utils.ScreenSizeGetHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ProductDetailsActivity extends AppCompatActivity {
    private ImageView userImg,popupProductImage,popupUserImg,price_decrease,price_increase;
    private TextView productName,userName,productPrice,counttimertext,biders,productDes,priceText,text2;
    private TextView popupProductName,popupUserName,popupProductPrice,popupCounttimertext,
            popupProductDes,bid_warning,popupBalance;
    private CardView button,popupSubmit,addToCart;
    private RelativeLayout placebiddingpopup,lay1;
    private ScrollView popupbelow;
    private RecyclerView bidderRecycler;
    private BidderListAdapter bidderListAdapter;
    private ArrayList<BiddingItem> bidderItemArrayList;
    private ProductItem productItem;
    private Toolbar toolbar;
    private GetServerTimeContext getServerTime;
    private ScreenSizeGetHelper screenSizeGetHelper;
    private DatabaseReference databaseReference;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String uid = user.getUid();
    private ViewPager sliderViewPager;
    private LinearLayout sliderDots;
    private int dotsCount;
    private ImageView[] dots;
    private int currentPosition=0;
    private SliderAdapterimage sliderAdapterimage;
    private List<SliderImageModel> sliderList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);
        if (getIntent().getExtras() != null) {
            productItem = (ProductItem) getIntent().getSerializableExtra("productObjects");
        }

        getServerTime = new GetServerTimeContext(this);
        screenSizeGetHelper = new ScreenSizeGetHelper(null,this,null);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        loadSlider();
        sliderViewPager = findViewById(R.id.sliderviewpager);
        sliderViewPager.setPageTransformer(true,new DepthPageTransformer());
        sliderViewPager.getLayoutParams().height = (screenSizeGetHelper.ScreenHeightSize() /11)*5;

        sliderDots = findViewById(R.id.slide_dot);
        dotsCount = productItem.getImageCount();
        dots = new ImageView[dotsCount];
        for(int i=0; i<dotsCount;i++){
            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),
                    R.drawable.tab_indicator_default));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams
                    (LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);

            params.setMargins(8,0,8,0);
            sliderDots.addView(dots[i],params);
        }
        dots[0].setImageDrawable(ContextCompat.getDrawable(getApplicationContext()
                ,R.drawable.tab_indicator_selected));

        sliderViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                for(int i=0; i<dotsCount;i++){
                    dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),
                            R.drawable.tab_indicator_default));
                }
                dots[position].setImageDrawable(ContextCompat.getDrawable(getApplicationContext()
                        ,R.drawable.tab_indicator_selected));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

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

        userImg = findViewById(R.id.sellerimage);
        productName = findViewById(R.id.productname);
        productPrice = findViewById(R.id.bidprice);
        biders = findViewById(R.id.biders);
        button = findViewById(R.id.bidbutton);
        addToCart = findViewById(R.id.addtocart);
        counttimertext = findViewById(R.id.counttimertext);
        userName = findViewById(R.id.sellername);
        productDes = findViewById(R.id.productdescription);
        priceText = findViewById(R.id.pricetext);
        text2 = findViewById(R.id.text2);

        productName.setText(productItem.getProductName());
        productDes.setText(productItem.getProductDescription());
        userName.setText(productItem.getUserName());
        Glide.with(this).load(productItem.getUserimage()).placeholder(R.drawable.dropdown).into(userImg);

        popupProductImage = findViewById(R.id.popupproductimage);
        popupUserImg = findViewById(R.id.popupsellerimage);
        popupProductName = findViewById(R.id.popupproductname);
        popupProductPrice = findViewById(R.id.popupprice);
        popupSubmit = findViewById(R.id.popupbidbutton);
        popupCounttimertext = findViewById(R.id.popupcounttimertext);
        popupBalance = findViewById(R.id.balance);
        popupUserName = findViewById(R.id.popupsellername);
        popupProductDes = findViewById(R.id.popupproductdescription);
        bid_warning = findViewById(R.id.bid_warning);
        placebiddingpopup = findViewById(R.id.placebiddingpopup);
        popupbelow = findViewById(R.id.popupbelow);
        price_decrease = findViewById(R.id.price_decrease);
        price_increase = findViewById(R.id.price_increase);
        lay1 = findViewById(R.id.lay1);

        bidderRecycler = findViewById(R.id.bidderRecycler);
        bidderItemArrayList = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        bidderRecycler.setLayoutManager(layoutManager);
        bidderRecycler.setHasFixedSize(true);
        popupProductImage.getLayoutParams().height = (screenSizeGetHelper.ScreenHeightSize() /11)*2;
        popupProductImage.getLayoutParams().width = (screenSizeGetHelper.ScreenHeightSize() /13)*2;

        getServerTime.getDateTime(dateTime -> {
            String splitTime[] = dateTime.split("T");
            String time = splitTime[0]+" "+splitTime[1];
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = null;
            try {
                date = sdf.parse(time);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            long timestamp = date.getTime();
            long endTimestamp = (Long.parseLong(productItem.getEndTimestamp()))-timestamp;
            new CountDownTimer(endTimestamp, 1000) {
                @Override
                public void onTick(long l) {
                    String sduration = String.format(Locale.ENGLISH,"%02d : %02d : %02d",
                            TimeUnit.MILLISECONDS.toHours(l),
                            TimeUnit.MILLISECONDS.toMinutes(l) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(l)),
                            TimeUnit.MILLISECONDS.toSeconds(l) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(l)));
                    counttimertext.setText(sduration);
                    popupCounttimertext.setText(sduration);
                }

                @Override
                public void onFinish() {
                    counttimertext.setText("00 : 00 : 00");
                    popupCounttimertext.setText("00 : 00 : 00");
                    popupSubmit.setVisibility(View.GONE);
                    lay1.setVisibility(View.GONE);
                }
            }.start();
        });
        Animation animation = AnimationUtils.loadAnimation(this,R.anim.fade_in);
        if(productItem.getSellType().equals("Place Bid")){
            button.setVisibility(View.VISIBLE);
            priceText.setText("Highest Bid");
            GetRealtimePrice();
            GetDataFromFirebase();
            GetRealtimeBalance();
            if(getIntent().getStringExtra("intent").equals("BidHistory")){
                bidding(animation);
            }
            button.setOnClickListener(view -> {
                bidding(animation);
            });
        }else if(productItem.getSellType().equals("Buy Now")){
            addToCart.setVisibility(View.VISIBLE);
            priceText.setText("Price");
            counttimertext.setVisibility(View.GONE);
            text2.setVisibility(View.GONE);
            GetRealtimePriceOnBuySection();
            GetItemSell();
            addToCart.setOnClickListener(view -> pushProductToCart());
        }
    }

    private void GetItemSell() {
        Query query = databaseReference.child("carts");
        List<String> intArray = new ArrayList<>();
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    for (DataSnapshot snapshot1:dataSnapshot.getChildren()){
                        try {
                            if (snapshot1.child("status").getValue().toString().equals("orderConfirm")) {
                                for (DataSnapshot dataSnapshot1 : snapshot1.child("cartItem").getChildren()) {
                                    if (dataSnapshot1.child("productId").getValue().toString().equals(productItem.getProductId())) {
                                        String cartId = dataSnapshot1.child("cartId").getValue().toString();
                                        intArray.add(cartId);
                                        biders.setText(intArray.size()+" sells");
                                    }
                                }
                            }
                        }catch (Exception e){
                            biders.setText(intArray.size()+" sell");
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void bidding(Animation animation) {
        placebiddingpopup.setVisibility(View.VISIBLE);
        popupbelow.setVisibility(View.GONE);
        button.setVisibility(View.GONE);
        placebiddingpopup.setAnimation(animation);
        popupProductName.setText(productItem.getProductName());
        popupProductDes.setText(productItem.getProductDescription());
        popupUserName.setText(productItem.getUserName());
        Glide.with(getApplicationContext()).load(productItem.getProductImage()).placeholder(R.drawable.dropdown).into(popupProductImage);
        Glide.with(getApplicationContext()).load(productItem.getUserimage()).placeholder(R.drawable.dropdown).into(popupUserImg);

        bid_warning.setText("You must bid at least " + productItem.getProductPrice() + " ETH \uD83D\uDD25");
        price_decrease.setOnClickListener(view13 -> {
            String currentprice = popupProductPrice.getText().toString();
            int price = Integer.parseInt(currentprice);
            int result = price - 1;
            popupProductPrice.setText(result + "");
        });
        price_increase.setOnClickListener(view12 -> {
            String currentprice = popupProductPrice.getText().toString();
            int price = Integer.parseInt(currentprice);
            int result = price + 1;
            popupProductPrice.setText(result + "");
        });
        popupSubmit.setOnClickListener(view1 -> {
            String splitPrice[] = productPrice.getText().toString().split(" ");
            int isplitPrice = Integer.parseInt(splitPrice[1]);
            String currentprice = popupProductPrice.getText().toString();
            int currentBalance = Integer.parseInt(popupBalance.getText().toString());
            checkBiddingList(currentBalance, currentprice, isplitPrice);
        });
    }

    private void checkBiddingList(int currentBalance,String currentprice,int splitPrice) {
        Query query1 = databaseReference.child("Products").child(productItem.getProductId()).child("Bidders").orderByChild("userId").equalTo(uid);
        query1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot!= null && snapshot.exists()){
                    for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                        try {
                            String key = dataSnapshot.child("biddingId").getValue().toString();
                            String prevBidPrice = dataSnapshot.child("productPrice").getValue().toString();
                            int upBal = currentBalance+Integer.parseInt(prevBidPrice);
                            if(upBal<Integer.parseInt(currentprice)){
                                Toast.makeText(ProductDetailsActivity.this, "don't have sufficient balance in your account", Toast.LENGTH_LONG).show();
                            }else {
                                popupSubmit.setClickable(false);
                                if (splitPrice < Integer.parseInt(currentprice)) {
                                    databaseReference.child("Products").child(productItem.getProductId()).child("Bidders").child(key).removeValue();
                                    submitBid(currentprice,upBal);
                                }else {
                                    Toast.makeText(ProductDetailsActivity.this, "increase your price", Toast.LENGTH_SHORT).show();
                                    popupSubmit.setClickable(true);
                                }
                            }
                        }catch (Exception e){

                        }
                    }
                }else {
                    sendNewSubmit(currentBalance,currentprice,splitPrice);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendNewSubmit(int currentBalance,String currentprice,int splitPrice){
        if(currentBalance<Integer.parseInt(currentprice)){
            Toast.makeText(ProductDetailsActivity.this, "don't have sufficient ", Toast.LENGTH_LONG).show();
        }else {
            popupSubmit.setClickable(false);
            if (splitPrice < Integer.parseInt(currentprice)) {
                submitBid(currentprice,currentBalance);
            }else {
                Toast.makeText(ProductDetailsActivity.this, "increase your price", Toast.LENGTH_SHORT).show();
                popupSubmit.setClickable(true);
            }
        }
    }
    private void GetRealtimePrice() {
        Query query = databaseReference.child("Products").child(productItem.getProductId());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {
                        String p = dataSnapshot.child("productPrice").getValue().toString();
                        String b = Long.toString(dataSnapshot.child("Bidders").getChildrenCount());
                        productPrice.setText("$ "+p);
                        popupProductPrice.setText(p);
                        biders.setText(b+" bids");
                    } catch (Exception e) {

                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void GetRealtimePriceOnBuySection() {
        Query query = databaseReference.child("Products").child(productItem.getProductId());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    String p = dataSnapshot.child("productPrice").getValue().toString();
                    String b = Long.toString(dataSnapshot.child("Bidders").getChildrenCount());
                    productPrice.setText("$ "+p);
                } catch (Exception e) {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void GetRealtimeBalance() {
        Query query = databaseReference.child("usersData").orderByChild("userId").equalTo(uid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    try {
                        String currentBalance = snapshot.child("balanceTk").getValue().toString();
                        popupBalance.setText(currentBalance);
                    } catch (Exception e) {

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadSlider() {
        Query query = databaseReference.child("Products").child(productItem.getProductId()).child("Images");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot  snapshot: dataSnapshot.getChildren()){
                    SliderImageModel productItem = new SliderImageModel();
                    productItem.setImageUrl(snapshot.child("productImage").getValue().toString());

                    sliderList.add(productItem);
                }
                sliderAdapterimage = new SliderAdapterimage(getApplicationContext(),sliderList);
                sliderViewPager.setAdapter(sliderAdapterimage);
                sliderAdapterimage.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void submitBid(String currentprice,int currentBalance) {
        getServerTime.getDateTime(new GetServerTimeContext.VolleyCallBack() {
            @Override
            public void onGetDateTime(String dateTime) {
                String splitTime[] = dateTime.split("T");
                String time = splitTime[0] + " " + splitTime[1];
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = null;
                try {
                    date = sdf.parse(time);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                long timestamp = date.getTime();
                bidNewBidder(currentprice,timestamp,currentBalance);
            }
        });
    }
    private void bidNewBidder(String currentprice, long timestamp,int currentBalance) {
        String biddingId = databaseReference.child("Products").child(productItem.getProductId()).child("Bidders").push().getKey();
        HashMap<String, Object> result = new HashMap<>();
        result.put("productPrice", currentprice);
        result.put("timestamp",timestamp);
        result.put("userId", uid);
        result.put("status", "win");
        result.put("biddingId",biddingId);
        databaseReference.child("Products").child(productItem.getProductId()).child("Bidders").child(biddingId).setValue(result);
        HashMap<String, Object> updatePrice = new HashMap<>();
        updatePrice.put("productPrice", currentprice);
        databaseReference.child("Products").child(productItem.getProductId()).updateChildren(updatePrice);
        UpdateBalance(currentBalance,currentprice,timestamp);

        popupSubmit.setClickable(true);
    }

    private void UpdateBalance(int currentBalance, String currentprice,long timestamp) {
        HashMap<String, Object> updateBalance = new HashMap<>();
        updateBalance.put("balanceTk", currentBalance-Integer.parseInt(currentprice));
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

        Query query1 = databaseReference.child("Products").child(productItem.getProductId()).child("Bidders").orderByChild("status").equalTo("win");
        query1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    try {
                        String biddingId = dataSnapshot.child("biddingId").getValue().toString();
                        String prevBidPrice = dataSnapshot.child("productPrice").getValue().toString();
                        String userid = dataSnapshot.child("userId").getValue().toString();
                        String message = "You lost the auction for "+productItem.getProductName()+" go to try for other product";
                        String stitle = "Auction lost";
                        if(!userid.equals(uid)){
                            HashMap<String, Object> result11 = new HashMap<>();
                            result11.put("status", "loose");
                            databaseReference.child("Products").child(productItem.getProductId()).child("Bidders").child(biddingId).updateChildren(result11);
                            String notifyId = databaseReference.child("Notification").push().getKey();
                            HashMap<String, Object> resultNotify = new HashMap<>();
                            resultNotify.put("status", "loose");
                            resultNotify.put("message",message);
                            resultNotify.put("title",stitle);
                            resultNotify.put("timestamp",getCurrentDateForNotification());
                            resultNotify.put("productId", productItem.getProductId());
                            resultNotify.put("notifyId",notifyId);
                            resultNotify.put("userId",userid);
                            databaseReference.child("Notification").child(notifyId).updateChildren(resultNotify);

                            databaseReference.child("users").child(userid).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    String k = snapshot.child("key").getValue().toString();
                                    databaseReference.child("usersData").child(k).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            String b = snapshot.child("balanceTk").getValue().toString();
                                            HashMap<String, Object> returnBalance = new HashMap<>();
                                            returnBalance.put("balanceTk", Integer.parseInt(b)+Integer.parseInt(prevBidPrice));
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
                            getToken(message,stitle,userid,getApplicationContext());
                        }
                    }catch (Exception e){

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void GetDataFromFirebase() {
        Query query = databaseReference.child("Products").child(productItem.getProductId()).child("Bidders");
        String splitBid[] = biders.getText().toString().split(" ");
        query.orderByChild("productPrice").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ClearAll();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    try {
                        BiddingItem item = new BiddingItem();
                        String p = snapshot.child("productPrice").getValue().toString();
                        item.setProductprice(p);
                        item.setBiddingId(snapshot.child("biddingId").getValue().toString());
                        item.setDatetime(snapshot.child("timestamp").getValue().toString());
                        String userid = snapshot.child("userId").getValue().toString();
                        item.setUserId(userid);
                        bidderItemArrayList.add(item);

                    } catch (Exception e) {

                    }
                    bidderListAdapter = new BidderListAdapter(ProductDetailsActivity.this, bidderItemArrayList);
                    bidderRecycler.setAdapter(bidderListAdapter);
                    bidderListAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void ClearAll(){
        if (bidderItemArrayList !=null){
            bidderItemArrayList.clear();
            if (bidderListAdapter !=null){
                bidderListAdapter.notifyDataSetChanged();
            }
        }
        bidderItemArrayList = new ArrayList<>();
    }

    private void pushProductToCart() {
        databaseReference.child("carts").child(uid).orderByChild("status").equalTo("pending")
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot!= null && snapshot.exists()){
                    for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                        String id = dataSnapshot.child("id").getValue().toString();
                        String cartId = databaseReference.child("carts").child(uid).push().getKey();
                        CartItemModel cartItemPush = new CartItemModel(cartId,productItem.getProductId(),productItem.getProductName()
                                ,productItem.getProductPrice(),productItem.getProductPrice(),"1",productItem.getProductImage(),"","");
                        databaseReference.child("carts").child(uid).child(id).child("cartItem").child(cartId).setValue(cartItemPush);
                        Toast.makeText(getApplicationContext(),"Item Add to Cart Successfully",Toast.LENGTH_LONG).show();
                        finish();
                    }
                }else {
                    String id = databaseReference.child("carts").child(uid).push().getKey();
                    String cartId = databaseReference.child("carts").child(uid).push().getKey();
                    CartItemModel cartItemPush = new CartItemModel(cartId,productItem.getProductId(),productItem.getProductName()
                            ,productItem.getProductPrice(),productItem.getProductPrice(),"1",productItem.getProductImage(),"","");
                    databaseReference.child("carts").child(uid).child(id).child("cartItem").child(cartId).setValue(cartItemPush);
                    HashMap<String, Object> result11 = new HashMap<>();
                    result11.put("status", "pending");
                    result11.put("id", id);
                    databaseReference.child("carts").child(uid).child(id).updateChildren(result11);
                    Toast.makeText(getApplicationContext(),"Item Add to Cart Successfully",Toast.LENGTH_LONG).show();
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}