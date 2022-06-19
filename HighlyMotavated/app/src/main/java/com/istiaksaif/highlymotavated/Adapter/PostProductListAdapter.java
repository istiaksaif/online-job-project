package com.istiaksaif.highlymotavated.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.istiaksaif.highlymotavated.Activity.AddProductActivity;
import com.istiaksaif.highlymotavated.Activity.MyPostActivity;
import com.istiaksaif.highlymotavated.Activity.ProductDetailsActivity;
import com.istiaksaif.highlymotavated.Activity.UserHomeActivity;
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

public class PostProductListAdapter extends RecyclerView.Adapter<PostProductListAdapter.ViewHolder> {
    private Context context;
    private ArrayList<ProductItem> mdata;
    private GetServerTimeContext getServerTime;
    private ScreenSizeGetHelper screenSizeGetHelper;
    private DatabaseReference databaseReference;
    private String string;

    public PostProductListAdapter(Context context,ArrayList<ProductItem>mdata,String string) {
        this.context = context;
        this.mdata = mdata;
        this.string = string;
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
//        holder.userName.setText(model.getUserName());
        holder.productName.setText(model.getProductName());
        holder.productPrice.setText("$ "+model.getProductPrice());
        holder.biders.setText(model.getBidders()+" bids");
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

        if(string.equals("BidHistory")){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ProductDetailsActivity.class);
                    intent.putExtra("productObjects", model);
                    intent.putExtra("intent","BidHistory");
                    Pair<View,String> p1 = Pair.create((View)holder.Image,"image");
                    Pair<View,String> p2 = Pair.create((View)holder.userImg,"userImage");
                    Pair<View,String> p3 = Pair.create((View)holder.productName,"productName");
                    Pair<View,String> p4 = Pair.create((View)holder.productPrice,"bidprice");
                    Pair<View,String> p5 = Pair.create((View)holder.biders,"biders");
                    Pair<View,String> p6 = Pair.create((View)holder.counttimertext,"counttimertext");

                    ActivityOptionsCompat optionsCompat =
                            ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context,p1,p2,p3,p4,p5,p6);
                    context.startActivity(intent,optionsCompat.toBundle());
                }
            });
        }else if(string.equals("MyPost")){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.popupoption.setVisibility(View.VISIBLE);
                    holder.edit.setOnClickListener(View ->{
                        Intent intent = new Intent(context, AddProductActivity.class);
                        intent.putExtra("productObjects", model);
                        intent.putExtra("intentType","edit");
                        context.startActivity(intent);
                    });
                    holder.delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            databaseReference.child("Products").child(model.getProductId()).removeValue();
                        }
                    });
                }
            });
            holder.popupoption.setOnClickListener(view ->
                    holder.popupoption.setVisibility(View.GONE)
            );
        }
    }

    @Override
    public int getItemCount() {
        return mdata.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView Image,userImg;
        TextView productName,productPrice,counttimertext,biders;
        CardView button,edit,delete;
        RelativeLayout popupoption;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Image = (ImageView) itemView.findViewById(R.id.productimage);
            userImg = (ImageView) itemView.findViewById(R.id.userimage);
            productName = (TextView) itemView.findViewById(R.id.productname);
            productPrice = (TextView) itemView.findViewById(R.id.bidprice);
            biders = (TextView) itemView.findViewById(R.id.biders);
            button = (CardView) itemView.findViewById(R.id.bidbutton);
            counttimertext = (TextView) itemView.findViewById(R.id.counttimertext);
            popupoption = (RelativeLayout) itemView.findViewById(R.id.popupoption);
            edit = (CardView) itemView.findViewById(R.id.editbutton);
            delete = (CardView) itemView.findViewById(R.id.deletebutton);
            button.setVisibility(View.GONE);
            Image.getLayoutParams().height = (screenSizeGetHelper.ScreenHeightSize() /11)*6;
        }
    }
}
