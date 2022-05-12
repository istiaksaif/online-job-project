package com.rtapps.moc.Adapter;

import static android.content.Context.CLIPBOARD_SERVICE;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.rtapps.moc.DashboardActivity;
import com.rtapps.moc.Interface.ItemClickListner;
import com.rtapps.moc.Model.Item;
import com.rtapps.moc.R;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class FoodItemAdapter extends RecyclerView.Adapter<FoodItemAdapter.ResturantViewHolder> {
    public FoodItemAdapter.ResturantViewHolder resturantViewHolder;
    ArrayList<Item> items;
    Calendar calendar;
    Context context;
    Date todayDate;
    SimpleDateFormat sdf;
    String expiryDate;

    public FoodItemAdapter(Context context, ArrayList<Item> items) {
        this.context = context;
        this.items = items;
        todayDate = Calendar.getInstance().getTime();

        sdf = new SimpleDateFormat("dd MMM yyyy", Locale.forLanguageTag("iw"));

    }



    @Override
    public FoodItemAdapter.ResturantViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.child_food, parent, false);


        resturantViewHolder = new FoodItemAdapter.ResturantViewHolder(itemView);
        return resturantViewHolder;

    }



    @Override
    public void onBindViewHolder(FoodItemAdapter.ResturantViewHolder holder,  int i) {

        int position=holder.getAbsoluteAdapterPosition();

        expiryDate=items.get(position).getExpDate();


        try {
            expiryDate=getDate(Long.parseLong(expiryDate));
        }catch (Exception e){

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


        holder.name.setText(items.get(position).getName());
        holder.expiry.setText(expiryDate);
        holder.catalogNumber.setText(items.get(position).getCatalogNumber());
        holder.catalog.setText(items.get(position).getCategory());
        if (!items.get(position).getImage().equals("")) {
            Picasso.get().load(items.get(position).getImage()).into(holder.imagess);
        }



        holder.shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                  ((DashboardActivity) context).dialogShare(expiryDate, items.get(position).getName(), items.get(position).getCatalogNumber(), items.get(position).getImage(), items.get(position).getID());


            }
        });
        holder.archiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((DashboardActivity) context).archiveItem(items.get(position).getName(), items.get(position).getCatalogNumber(), items.get(position).getImage(), items.get(position).getID());
            }
        });

        holder.sellBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((DashboardActivity) context).sellItem(items.get(position).getName(), items.get(position).getCatalogNumber(), items.get(position).getImage(), items.get(position).getID(), items.get(position).getPrice());


            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyCatalogNumber(items.get(position).getCatalogNumber());
            }
        });

    }

    private void copyCatalogNumber(String catalogNumber){

        ClipboardManager myClipboard = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);

        ClipData myClip = ClipData.newPlainText("CatalogNumber", catalogNumber);
        myClipboard.setPrimaryClip(myClip);

        Toast.makeText(context, context.getString(R.string.catalogCopied)+"\n"+catalogNumber, Toast.LENGTH_SHORT).show();

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
        public TextView catalog,catalogNumber, expiry, name;
        public CircleImageView imagess;
        public ItemClickListner listner;
        TextView expiryDaysTextView, category;
        ImageView shareBtn;
        ImageView archiveBtn;
        ImageView sellBtn;

        public ResturantViewHolder(View itemView) {
            super(itemView);
            sellBtn = itemView.findViewById(R.id.sell_btn);

            archiveBtn = itemView.findViewById(R.id.archive_btn);
            imagess = itemView.findViewById(R.id.image);
            name = itemView.findViewById(R.id.name);
            catalogNumber = itemView.findViewById(R.id.catalog_number_textview);
            expiry = itemView.findViewById(R.id.expiry_date_textview);
            expiryDaysTextView = itemView.findViewById(R.id.expiry_days_textview);
            shareBtn = itemView.findViewById(R.id.share_btn);
            catalog = itemView.findViewById(R.id.category);


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