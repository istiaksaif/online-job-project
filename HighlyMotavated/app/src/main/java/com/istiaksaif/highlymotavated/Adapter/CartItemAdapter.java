package com.istiaksaif.highlymotavated.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.istiaksaif.highlymotavated.Model.CartItemModel;
import com.istiaksaif.highlymotavated.R;

import java.util.ArrayList;
import java.util.HashMap;

public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.CartItemViewHolder> {

    private int intquantity,itemTotalPrice,itemprice;
    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("carts").child(userId);
    private Context context;
    private ArrayList<CartItemModel> cartItem;
    public CartItemAdapter(Context context, ArrayList<CartItemModel> cartItem) {
        this.context = context;
        this.cartItem = cartItem;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CartItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cart_item_card, parent, false);
        return new CartItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartItemViewHolder holder, @SuppressLint("RecyclerView") int position) {
        String ccc= cartItem.get(position).getCartId();
        String firstId = cartItem.get(position).getId();

        holder.itemname.setText(cartItem.get(position).getName());
        holder.itemprice.setText("$"+cartItem.get(position).getPrice());
        Glide.with(context).load(cartItem.get(position).getImage()).into(holder.itemimage);
        holder.quantityitem.setText(cartItem.get(position).getQuantity());
        holder.itemtotalprice.setText("$"+cartItem.get(position).getItemtotalprice());

        holder.minus.setOnClickListener(v -> {
            String quantity = cartItem.get(position).getQuantity();
            intquantity =Integer.parseInt(quantity.toString());

            String itemPrice = cartItem.get(position).getPrice();
            itemprice =Integer.parseInt(itemPrice.toString());

            if(intquantity>0){
                intquantity--;
            }
            itemTotalPrice = intquantity*itemprice;
            HashMap<String, Object> result = new HashMap<>();
            result.put("quantity", intquantity);
            result.put("itemtotalprice",itemTotalPrice);

            databaseReference.child(firstId).child("cartItem").child(ccc).updateChildren(result);
        });
        holder.plus.setOnClickListener(v -> {
            String quantity = cartItem.get(position).getQuantity();
            intquantity =Integer.parseInt(quantity.toString());

            String itemPrice = cartItem.get(position).getPrice();
            itemprice =Integer.parseInt(itemPrice.toString());

            intquantity++;
            itemTotalPrice = intquantity*itemprice;
            HashMap<String, Object> result = new HashMap<>();
            result.put("quantity", intquantity);
            result.put("itemtotalprice",itemTotalPrice);

            databaseReference.child(firstId).child("cartItem").child(ccc).updateChildren(result);
        });
        holder.cross.setOnClickListener(v -> {
            databaseReference.child(firstId).child("cartItem").child(ccc).removeValue();
            Toast.makeText(context,"Item Removed from Cart",Toast.LENGTH_SHORT).show();
            cartItem.remove(position);
            notifyItemChanged(position);
            notifyDataSetChanged();
        });

    }


    @Override
    public int getItemCount() {
        return cartItem.size();
    }

    public class CartItemViewHolder extends RecyclerView.ViewHolder {
        private TextView itemname, itemprice, quantityitem, itemtotalprice;
        private ImageView itemimage, minus, plus, cross;

        public CartItemViewHolder(@NonNull View itemView) {
            super(itemView);
            itemname = itemView.findViewById(R.id.product_name);
            itemprice = itemView.findViewById(R.id.product_price);
            itemimage = itemView.findViewById(R.id.product_image);
            quantityitem = itemView.findViewById(R.id.itemcount);
            itemtotalprice = itemView.findViewById(R.id.itemtotalprice);
            minus = itemView.findViewById(R.id.itemminus);
            plus = itemView.findViewById(R.id.itemplus);
            cross = itemView.findViewById(R.id.itemdelete);
        }
    }
}
