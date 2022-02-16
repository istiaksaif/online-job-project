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


public class PhoneAdapter extends RecyclerView.Adapter<PhoneAdapter.ViewHolder> {
    private Context context;
    public static ArrayList<Phone> mdata;

    public PhoneAdapter(Context context, ArrayList<Phone> mdata) {
        this.context = context;
        this.mdata = mdata;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.phonecard,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.itemopen.setText(mdata.get(position).getLopen());

        holder.PhoneNumber.setText(mdata.get(position).getPhoneNumber());
    }

    @Override
    public int getItemCount() {
        return mdata.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView itemopen;
        private Button nextButton;
        private TextInputEditText PhoneNumber;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemopen = itemView.findViewById(R.id.layoutopen);
            PhoneNumber = itemView.findViewById(R.id.mobilenum);
            PhoneNumber.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    mdata.get(getAdapterPosition()).setPhoneNumber(PhoneNumber.getText().toString());
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    mdata.get(getAdapterPosition()).setPhoneNumber(PhoneNumber.getText().toString());
                }
            });

        }
    }
}
