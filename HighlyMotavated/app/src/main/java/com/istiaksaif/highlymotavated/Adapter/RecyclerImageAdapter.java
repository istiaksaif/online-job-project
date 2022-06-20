package com.istiaksaif.highlymotavated.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.istiaksaif.highlymotavated.R;

import java.util.ArrayList;

public class RecyclerImageAdapter extends RecyclerView.Adapter<RecyclerImageAdapter.ViewHolder> {

    private ArrayList<Uri> uriArrayList;
    private ArrayList<String>imageKey,productId;
    private Context context;
    private DatabaseReference databaseReference;

    public RecyclerImageAdapter(ArrayList<Uri> uriArrayList,ArrayList<String>imageKey,ArrayList<String>productId,Context context) {
        this.uriArrayList = uriArrayList;
        this.context =context;
        this.imageKey = imageKey;
        this.productId =productId;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerImageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.takeimagecard,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerImageAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        databaseReference = FirebaseDatabase.getInstance().getReference();
        holder.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Glide.with(context).load(uriArrayList.get(position)).placeholder(R.drawable.dropdown).into(holder.imageView);
//        holder.delete.setVisibility(View.VISIBLE);
//        holder.delete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                databaseReference.child("Products").child(productId.get(0)).child("Images").child(imageKey.get(position)).removeValue();
//                uriArrayList.clear();
//                notifyDataSetChanged();
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return uriArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView,delete;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.productimage);
            delete = itemView.findViewById(R.id.deletebutton);
            delete.setVisibility(View.GONE);
        }
    }
}
