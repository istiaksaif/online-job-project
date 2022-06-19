package com.istiaksaif.highlymotavated.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DatabaseReference;
import com.istiaksaif.highlymotavated.Model.NotifyItem;
import com.istiaksaif.highlymotavated.R;
import com.istiaksaif.highlymotavated.Utils.GetServerTimeContext;
import com.istiaksaif.highlymotavated.Utils.ScreenSizeGetHelper;

import java.util.ArrayList;

public class NotifyAdapter extends RecyclerView.Adapter<NotifyAdapter.ViewHolder> {
    private Context context;
    private ArrayList<NotifyItem> mdata;

    public NotifyAdapter(Context context, ArrayList<NotifyItem> mdata) {
        this.context = context;
        this.mdata = mdata;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.notifycard,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.title.setText(mdata.get(position).getTitle());
        holder.message.setText(mdata.get(position).getMessage());
        holder.date.setText(mdata.get(position).getDatetime());
    }

    @Override
    public int getItemCount() {
        return mdata.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView title,message,date;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            message = (TextView) itemView.findViewById(R.id.message);
            date = (TextView) itemView.findViewById(R.id.date);
        }
    }
}
