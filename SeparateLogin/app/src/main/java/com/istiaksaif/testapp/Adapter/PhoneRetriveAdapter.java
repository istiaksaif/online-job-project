package com.istiaksaif.testapp.Adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.istiaksaif.testapp.Model.Phone;
import com.istiaksaif.testapp.R;

import java.util.ArrayList;


public class PhoneRetriveAdapter extends RecyclerView.Adapter<PhoneRetriveAdapter.ViewHolder> {
    private Context context;
    public static ArrayList<Phone> mdata;

    public PhoneRetriveAdapter(Context context, ArrayList<Phone> mdata) {
        this.context = context;
        this.mdata = mdata;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.detailscard,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.PhoneNumber.setText(mdata.get(position).getPhoneNumber());
    }

    @Override
    public int getItemCount() {
        return mdata.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView PhoneNumber,type;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            PhoneNumber = itemView.findViewById(R.id.textTypeinfo);
            type = itemView.findViewById(R.id.textType);
            type.setText("Phone");
        }
    }
}
