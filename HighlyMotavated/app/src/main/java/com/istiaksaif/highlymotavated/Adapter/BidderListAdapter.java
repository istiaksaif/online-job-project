package com.istiaksaif.highlymotavated.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.istiaksaif.highlymotavated.Activity.AddProductActivity;
import com.istiaksaif.highlymotavated.Model.BiddingItem;
import com.istiaksaif.highlymotavated.Model.ProductItem;
import com.istiaksaif.highlymotavated.R;
import com.istiaksaif.highlymotavated.Utils.GetServerTimeContext;
import com.istiaksaif.highlymotavated.Utils.ScreenSizeGetHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class BidderListAdapter extends RecyclerView.Adapter<BidderListAdapter.ViewHolder> {
    private Context context;
    private ArrayList<BiddingItem> mdata;
    private GetServerTimeContext getServerTime;
    private ScreenSizeGetHelper screenSizeGetHelper;
    private DatabaseReference databaseReference;

    public BidderListAdapter(Context context, ArrayList<BiddingItem> mdata) {
        this.context = context;
        this.mdata = mdata;
        getServerTime = new GetServerTimeContext(context);
        screenSizeGetHelper = new ScreenSizeGetHelper(null,null,context);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.bidderlistcard,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        databaseReference = FirebaseDatabase.getInstance().getReference();
        Query query = FirebaseDatabase.getInstance().getReference("users").child(mdata.get(position).getUserId());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot1) {
                String k = snapshot1.child("key").getValue().toString();
                databaseReference.child("usersData").child(k).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                        try {
                            String uImg = dataSnapshot1.child("imageUrl").getValue().toString();
                            String uName = dataSnapshot1.child("name").getValue().toString();
                            holder.bidderName.setText(uName);
                            Glide.with(context).load(uImg).placeholder(R.drawable.dropdown).into(holder.bidderImage);
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
        holder.productPrice.setText(" "+mdata.get(position).getProductprice()+" ETH ");
        holder.bidDateTime.setText(mdata.get(position).getDatetime());
//        String timer = mdata.get(position).getEnddate();

//        getServerTime.getDateTime(new GetServerTimeContext.VolleyCallBack(){
//            @Override
//            public void onGetDateTime(String dateTime) {
//                String splitTime[] = dateTime.split("T");
//                String time = splitTime[0]+" "+splitTime[1];
//                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                Date date = null;
//                try {
//                    date = sdf.parse(time);
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
//                long timestamp = date.getTime();
//                long endTimestamp = (Long.parseLong(timer))-timestamp;
//                new CountDownTimer(endTimestamp, 1000) {
//                    @Override
//                    public void onTick(long l) {
//                        String sduration = String.format(Locale.ENGLISH,"%02d : %02d : %02d",
//                                TimeUnit.MILLISECONDS.toHours(l),
//                                TimeUnit.MILLISECONDS.toMinutes(l) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(l)),
//                                TimeUnit.MILLISECONDS.toSeconds(l) -
//                                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(l)));
//                        holder.counttimertext.setText(sduration);
//                    }
//
//                    @Override
//                    public void onFinish() {
//                        holder.counttimertext.setText("00 : 00 : 00");
//                    }
//                }.start();
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return mdata.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView bidderImage;
        TextView bidderName,productPrice,bidDateTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            bidderImage = (ImageView) itemView.findViewById(R.id.bidderimage);
            productPrice = (TextView) itemView.findViewById(R.id.bidprice);
            bidderName = (TextView) itemView.findViewById(R.id.bidderrname);
            bidDateTime = (TextView) itemView.findViewById(R.id.bidder_biding_date);
//            Image.getLayoutParams().height = (screenSizeGetHelper.ScreenHeightSize() /11)*6;
        }
    }
}
