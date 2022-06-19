package com.istiaksaif.highlymotavated.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import androidx.core.util.Pair;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.istiaksaif.highlymotavated.Activity.ProductDetailsActivity;
import com.istiaksaif.highlymotavated.Model.ProductItem;
import com.istiaksaif.highlymotavated.R;
import com.istiaksaif.highlymotavated.Utils.GetServerTimeContext;
import com.istiaksaif.highlymotavated.Utils.ScreenSizeGetHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.ViewHolder> {
    private Context context;
    private List<ProductItem> mdata;
    private GetServerTimeContext getServerTime;
    private ScreenSizeGetHelper screenSizeGetHelper;
    private DatabaseReference databaseReference;


    public ProductListAdapter(Context context) {
        this.mdata=new ArrayList<>();
        this.context = context;
        getServerTime = new GetServerTimeContext(context);
        screenSizeGetHelper = new ScreenSizeGetHelper(null,null,context);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.productcard,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final ProductItem model = mdata.get(position);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        Glide.with(context).load(model.getProductImage()).placeholder(R.drawable.dropdown).into(holder.Image);
        Query query = FirebaseDatabase.getInstance().getReference("users").child(model.getUserId());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot1) {
                String k = snapshot1.child("key").getValue().toString();
                databaseReference.child("usersData").child(k).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                        try {
                            String uImg = dataSnapshot1.child("imageUrl").getValue().toString();
                            String uName = dataSnapshot1.child("name").getValue().toString();
                            model.setUserName(uName);
                            model.setUserimage(uImg);
                            Glide.with(context).load(uImg).placeholder(R.drawable.dropdown).into(holder.userImg);
                        }catch (Exception e){

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "Some Thing Wrong", Toast.LENGTH_SHORT).show();
            }
        });

        holder.productName.setText(model.getProductName());
        holder.productPrice.setText("$ "+model.getProductPrice());
        String sellType = model.getSellType();
        holder.buttontxt.setText(sellType);
        String timer = model.getEndTimestamp();

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
            long endTimestamp = (Long.parseLong(timer))-timestamp;
            new CountDownTimer(endTimestamp, 1000) {
                @Override
                public void onTick(long l) {
                    String sduration = String.format(Locale.ENGLISH,"%02d : %02d : %02d",
                            TimeUnit.MILLISECONDS.toHours(l),
                            TimeUnit.MILLISECONDS.toMinutes(l) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(l)),
                            TimeUnit.MILLISECONDS.toSeconds(l) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(l)));
                    holder.counttimertext.setText(sduration);
                }

                @Override
                public void onFinish() {
                    holder.counttimertext.setText("00 : 00 : 00");
                }
            }.start();
        });
        if(sellType.equals("Place Bid")){
            holder.biders.setText(model.getBidders()+" bids");
            holder.priceText.setText("Highest Bid");
        }else if(sellType.equals("Buy Now")){
            holder.priceText.setText("Price");
            GetItemSell(holder,model.getProductId());
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ProductDetailsActivity.class);
                intent.putExtra("productObjects", model);
                intent.putExtra("intent","Home");
                Pair<View,String> p1 = Pair.create((View)holder.Image,"image");
                Pair<View,String> p2 = Pair.create((View)holder.userImg,"userImage");
                Pair<View,String> p3 = Pair.create((View)holder.productName,"productName");
                Pair<View,String> p4 = Pair.create((View)holder.productPrice,"bidprice");
                Pair<View,String> p5 = Pair.create((View)holder.biders,"biders");
                Pair<View,String> p6 = Pair.create((View)holder.counttimertext,"counttimertext");
                Pair<View,String> p7 = Pair.create((View)holder.button,"bidbutton");


                ActivityOptionsCompat optionsCompat =
                        ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context,p1,p2,p3,p4,p5,p6,p7);
                context.startActivity(intent,optionsCompat.toBundle());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mdata.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView Image,userImg;
        TextView productName,productPrice,counttimertext,biders,buttontxt,priceText;
        CardView button;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Image = (ImageView) itemView.findViewById(R.id.productimage);
            userImg = (ImageView) itemView.findViewById(R.id.userimage);
            productName = (TextView) itemView.findViewById(R.id.productname);
            productPrice = (TextView) itemView.findViewById(R.id.bidprice);
            biders = (TextView) itemView.findViewById(R.id.biders);
            button = (CardView) itemView.findViewById(R.id.bidbutton);
            buttontxt = (TextView) itemView.findViewById(R.id.buttontxt);
            counttimertext = (TextView) itemView.findViewById(R.id.counttimertext);
            priceText = (TextView) itemView.findViewById(R.id.pricetext);

            Image.getLayoutParams().height = (screenSizeGetHelper.ScreenHeightSize() /11)*6;
        }
    }
    public void addAll(List<ProductItem> newUsers) {
        int initialSize = mdata.size();
        mdata.addAll(newUsers);
        notifyItemRangeInserted(initialSize, newUsers.size());
        notifyDataSetChanged();
    }

    public String getLastItemId() {
        return mdata.get(mdata.size() - 1).getProductId();
//        return super.getItem(getCount() - (position + 1));
    }
    private void GetItemSell(@NonNull ViewHolder holder, String productId) {
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
                                    if (dataSnapshot1.child("productId").getValue().toString().equals(productId)) {
                                        String cartId = dataSnapshot1.child("cartId").getValue().toString();
                                        intArray.add(cartId);
                                        holder.biders.setText(intArray.size()+" sells");
                                    }
                                }
                            }
                        }catch (Exception e){
                            holder.biders.setText(intArray.size()+" sells");
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
