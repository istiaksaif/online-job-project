package com.rtapps.moc.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.rtapps.moc.ArchiveItemsActivity;
import com.rtapps.moc.Interface.ItemClickListner;
import com.rtapps.moc.Model.Item;
import com.rtapps.moc.R;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ArchiveItemsAdapter extends RecyclerView.Adapter<ArchiveItemsAdapter.ResturantViewHolder> {
    public ArchiveItemsAdapter.ResturantViewHolder resturantViewHolder;
    ArrayList<Item> items;
    Calendar calendar;
    Context context;
    Date todayDate;

    public ArchiveItemsAdapter(Context context, ArrayList<Item> items) {
        this.context = context;
        this.items = items;
        todayDate = Calendar.getInstance().getTime();

    }

    public static String getFormatedDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");

        return dateFormat.format(date);
    }

    @Override
    public ArchiveItemsAdapter.ResturantViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.child_archived_items, parent, false);


        resturantViewHolder = new ArchiveItemsAdapter.ResturantViewHolder(itemView);
        return resturantViewHolder;

    }

    @Override
    public void onBindViewHolder(final ArchiveItemsAdapter.ResturantViewHolder holder, int position) {

        calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(items.get(position).getExpDate()));


        holder.name.setText(items.get(position).getName());
        holder.expiry.setText(getFormatedDate(calendar.getTime()));
        holder.catalog.setText(items.get(position).getCatalogNumber());
        if (!items.get(position).getImage().equals("")) {
            Picasso.get().load(items.get(position).getImage()).into(holder.imagess);
        }

        long daysLeft = getDifference(todayDate, calendar.getTime());

        if (daysLeft <= 7) {

//            holder.expiryDaysTextView.setVisibility(View.VISIBLE);
            holder.expiry.setTextColor(context.getResources().getColor(R.color.red));

        }

        holder.archiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ArchiveItemsActivity) context).archiveItem(items.get(position).getName(), items.get(position).getCatalogNumber(), items.get(position).getImage(), items.get(position).getID());
            }
        });


    }

    public long getDifference(Date startDate, Date endDate) {
        //milliseconds
        long different = endDate.getTime() - startDate.getTime();

        System.out.println("startDate : " + startDate);
        System.out.println("endDate : " + endDate);
        System.out.println("different : " + different);

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        return elapsedDays;
    }

    public void updateItems(ArrayList<Item> items) {
        this.items = items;
        notifyDataSetChanged();

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ResturantViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView catalog, expiry, name;
        public ImageView imagess;
        public ItemClickListner listner;
        TextView expiryDaysTextView;
        ImageView shareBtn;
        ImageView archiveBtn;
        ImageView sellBtn;

        public ResturantViewHolder(View itemView) {
            super(itemView);
            archiveBtn = itemView.findViewById(R.id.archive_btn);
            imagess = itemView.findViewById(R.id.image);
            name = itemView.findViewById(R.id.name);
            catalog = itemView.findViewById(R.id.catalog_number_textview);
            expiry = itemView.findViewById(R.id.expiry_date_textview);
            expiryDaysTextView = itemView.findViewById(R.id.expiry_days_textview);
            shareBtn = itemView.findViewById(R.id.share_btn);


        }

        public void setItemClickListner(ItemClickListner listner) {
            this.listner = listner;
        }

        @Override
        public void onClick(View view) {
            listner.onClick(view, getAdapterPosition(), false);

        }
    }

}