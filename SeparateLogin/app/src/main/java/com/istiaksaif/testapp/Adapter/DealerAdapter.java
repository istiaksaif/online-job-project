package com.istiaksaif.testapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.istiaksaif.testapp.Activity.AddNewDealerActivity;
import com.istiaksaif.testapp.Activity.DealerDetailsActivity;
import com.istiaksaif.testapp.Model.DealerItem;
import com.istiaksaif.testapp.R;

import java.util.ArrayList;

public class DealerAdapter extends RecyclerView.Adapter<DealerAdapter.ViewHolder> {
    private Context context;
    private ArrayList<DealerItem> mdata;

    public DealerAdapter(Context context, ArrayList<DealerItem> mdata) {
        this.context = context;
        this.mdata = mdata;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.dealerlistcard,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.dealerName.setText(mdata.get(position).getDealerName());
        String s = mdata.get(position).getDealerId();
        String companyInfoId = mdata.get(position).getNewCompaniesId();
        String limit = mdata.get(position).getLimit();

        String status = mdata.get(position).getStatus();
        if (status.equals("pending")){
            holder.approve_status.setText("Approval Pending");
        }else if(status.equals("deletePending")){
            holder.approve_status.setText("Remove Pending");
        } else if(status.equals("approve")){
            holder.approve_status.setVisibility(View.GONE);
            holder.editDealerButton.setVisibility(View.VISIBLE);
        }

        holder.dealerCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DealerDetailsActivity.class);
                intent.putExtra("dealerId",s);
                intent.putExtra("NewCompaniesId",companyInfoId);
                intent.putExtra("limit",limit);
                context.startActivity(intent);
            }
        });
        holder.editDealerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, AddNewDealerActivity.class);
                intent.putExtra("dealerId",s);
                intent.putExtra("NewCompaniesId",companyInfoId);
                intent.putExtra("limit",limit);
                context.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return mdata.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView dealerName,editDealerButton,approve_status;
                //company,state,city,address;
        CardView dealerCard;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dealerName = (TextView) itemView.findViewById(R.id.textDealerName);
//            company = (TextView) itemView.findViewById(R.id.textCompany);
//            phoneRetriveRecycler = itemView.findViewById(R.id.phoneRecycler);
//            LinearLayoutManager layoutManager1 = new LinearLayoutManager(context);
//            layoutManager1.setOrientation(LinearLayoutManager.HORIZONTAL);
//            phoneRetriveRecycler.setLayoutManager(layoutManager1);
//            phoneRetriveRecycler.setHasFixedSize(true);
//            state = (TextView) itemView.findViewById(R.id.textState);
//            address = (TextView) itemView.findViewById(R.id.textAddress);
//            city = (TextView) itemView.findViewById(R.id.textCity);
            editDealerButton = (TextView) itemView.findViewById(R.id.editDealerBtn);
            dealerCard = (CardView) itemView.findViewById(R.id.dealerCard);
            approve_status = (TextView) itemView.findViewById(R.id.approve_status);
        }
    }
}
