package com.istiaksaif.testapp.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.istiaksaif.testapp.Activity.ProductDetailsActivity;
import com.istiaksaif.testapp.Model.ProductItemList;
import com.istiaksaif.testapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ProductItemAdapter extends RecyclerView.Adapter<ProductItemAdapter.ViewHolder> {
    private static final String Tag = "RecyclerView";
    private Context context;
    private ArrayList<ProductItemList> mdata;

    public ProductItemAdapter(Context context, ArrayList<ProductItemList> mdata) {
        this.context = context;
        this.mdata = mdata;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.card_product,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.itemName.setText(mdata.get(position).getProductName());
        holder.itemPrice.setText("  "+mdata.get(position).getProductPrice()+" Rupee");
        String status = mdata.get(position).getStatus();
        if (status.equals("pending")){
            holder.approve_status.setText("Approval Pending");
        }else {
            holder.approve_status.setText("Approved");
            holder.approve_status.setTextColor(ContextCompat.getColor(context,R.color.dark_blue));
            holder.cardView.setBackground(ContextCompat.getDrawable(context, R.drawable.buttonbackcardgreen));
        }

        String img = mdata.get(position).getProductImage();
        try {
            if (img.equals("")){
                holder.itemImage.setImageResource(R.drawable.ic_logo);
            }else {
                Picasso.get().load(img).resize(480,480).into(holder.itemImage);
            }
        }catch (Exception e){
            Picasso.get().load(R.drawable.dropdown).into(holder.itemImage);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProductDetailsActivity.class);
                intent.putExtra("productid",mdata.get(position).getProductId());
                Bundle b = ActivityOptions.makeSceneTransitionAnimation((Activity) context,holder.itemImage,"proImg").toBundle();
                context.startActivity(intent,b);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mdata.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView itemImage;
        TextView itemName, itemPrice,approve_status;
        LinearLayout cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = (ImageView) itemView.findViewById(R.id.pimage);
            itemName = (TextView) itemView.findViewById(R.id.pname);
            itemPrice = (TextView) itemView.findViewById(R.id.pprice);
            approve_status = (TextView) itemView.findViewById(R.id.approve_status);
            cardView = (LinearLayout) itemView.findViewById(R.id.productcard);
        }
    }
}
