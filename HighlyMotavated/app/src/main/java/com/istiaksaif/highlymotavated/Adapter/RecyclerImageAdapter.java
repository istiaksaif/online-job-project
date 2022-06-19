package com.istiaksaif.highlymotavated.Adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.istiaksaif.highlymotavated.R;

import java.util.ArrayList;

public class RecyclerImageAdapter extends RecyclerView.Adapter<RecyclerImageAdapter.ViewHolder> {

    private ArrayList<Uri> uriArrayList;
    private Context context;

    public RecyclerImageAdapter(ArrayList<Uri> uriArrayList,Context context) {
        this.uriArrayList = uriArrayList;
        this.context =context;
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
    public void onBindViewHolder(@NonNull RecyclerImageAdapter.ViewHolder holder, int position) {
//        holder.imageView.setImageURI(uriArrayList.get(position));
        holder.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Glide.with(context).load(uriArrayList.get(position)).placeholder(R.drawable.dropdown).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return uriArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.productimage);
        }
    }
}
