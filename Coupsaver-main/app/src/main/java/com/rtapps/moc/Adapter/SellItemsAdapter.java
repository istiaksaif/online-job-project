package com.rtapps.moc.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.rtapps.moc.DisplaySellItemsActivity;
import com.rtapps.moc.Interface.ItemClickListner;
import com.rtapps.moc.Model.Item;
import com.rtapps.moc.R;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SellItemsAdapter extends RecyclerView.Adapter<SellItemsAdapter.ResturantViewHolder> {
    public SellItemsAdapter.ResturantViewHolder resturantViewHolder;
    ArrayList<Item> items;
    Calendar calendar;
    Context context;
    Date todayDate;
    SimpleDateFormat sdf;
    String expiryDate;

    public SellItemsAdapter(Context context, ArrayList<Item> items) {
        this.context = context;
        this.items = items;
        todayDate = Calendar.getInstance().getTime();
        sdf = new SimpleDateFormat("dd MMM yyyy", Locale.forLanguageTag("iw"));

    }

    @Override
    public ResturantViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.child_sell_items, parent, false);


        resturantViewHolder = new SellItemsAdapter.ResturantViewHolder(itemView);
        return resturantViewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull ResturantViewHolder holder,  int i) {

        int position=holder.getAbsoluteAdapterPosition();

        expiryDate=items.get(position).getExpDate();


        try {
            expiryDate=getDate(Long.parseLong(expiryDate));
        }catch (Exception e){

        }

        holder.price.setText("₪ " + items.get(position).getPrice());

        holder.name.setText(items.get(position).getName());
        holder.expiry.setText(expiryDate);
        holder.catalog.setText(items.get(position).getCatalogNumber());
        if (!items.get(position).getImage().equals("")) {
            Picasso.get().load(items.get(position).getImage()).into(holder.imagess);
        }


        Date date = null;
        try {
            date = sdf.parse(expiryDate);


            assert date != null;
            long daysLeft = getDifference(todayDate, date);

            Log.v("FoodApp",String.valueOf(daysLeft));

            if (daysLeft <= 7) {

//            holder.expiryDaysTextView.setVisibility(View.VISIBLE);
                holder.expiry.setTextColor(ContextCompat.getColor(context,R.color.red));

            }
        } catch (ParseException e) {
            e.printStackTrace();
            Log.v("FoodApp",e.getMessage());
        }

        TextView sellerName;
        TextView sellerPhone;


        holder.shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 ((DisplaySellItemsActivity) context).dailogShare(expiryDate, items.get(position).getName(), items.get(position).getCatalogNumber(), items.get(position).getImage(), items.get(position).getID(), items.get(position).getSellerName(), items.get(position).getSellerPhone());


            }
        });
//        holder.archiveBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ((ArchiveItemsActivity) context).archiveItem(items.get(position).getName(), items.get(position).getCatalogNumber(), items.get(position).getImage(), items.get(position).getID());
//            }
//        });


    }

    private String getDate(long milliSeconds)
    {
        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return sdf.format(calendar.getTime());
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
        TextView sellerName;
        TextView sellerPhone;
        TextView price;

        public ResturantViewHolder(View itemView) {
            super(itemView);
            archiveBtn = itemView.findViewById(R.id.archive_btn);
            imagess = itemView.findViewById(R.id.image);
            name = itemView.findViewById(R.id.name);
            catalog = itemView.findViewById(R.id.catalog_number_textview);
            expiry = itemView.findViewById(R.id.expiry_date_textview);
            expiryDaysTextView = itemView.findViewById(R.id.expiry_days_textview);
            shareBtn = itemView.findViewById(R.id.share_btn);
            expiryDaysTextView = itemView.findViewById(R.id.expiry_days_textview);
            expiryDaysTextView = itemView.findViewById(R.id.expiry_days_textview);
            price = itemView.findViewById(R.id.price_textview);

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