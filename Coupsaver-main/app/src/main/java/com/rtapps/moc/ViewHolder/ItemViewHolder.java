package com.rtapps.moc.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.rtapps.moc.Interface.ItemClickListner;
import com.rtapps.moc.R;


public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView catalog, expiry, name;
    public ImageView imagess;
    public ItemClickListner listner;

    public ItemViewHolder(View itemView) {
        super(itemView);

        imagess = itemView.findViewById(R.id.image);
        name = itemView.findViewById(R.id.name);
        catalog = itemView.findViewById(R.id.catalog_number_textview);
        expiry = itemView.findViewById(R.id.expiry_date_textview);


    }

    public void setItemClickListner(ItemClickListner listner) {
        this.listner = listner;
    }

    @Override
    public void onClick(View view) {
        listner.onClick(view, getAdapterPosition(), false);


    }
}
