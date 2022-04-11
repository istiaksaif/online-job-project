package com.istiaksaif.detectskindiseases;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {

    private Context context;
    private ArrayList <Model> modelArrayList;
    SQLiteDatabase sqLiteDatabase;
    int singledata;
    DatabaseAdapter databaseAdapter;

    public CustomAdapter(Context context, int singledata, ArrayList<Model> modelArrayList, SQLiteDatabase sqLiteDatabase) {
        this.context = context;
        this.modelArrayList = modelArrayList;
        this.sqLiteDatabase = sqLiteDatabase;
        this.singledata = singledata;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.detection_card, parent, false);
        return new MyViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        final Model model = modelArrayList.get(position);
        databaseAdapter = new DatabaseAdapter(context);
        byte[]image=model.getImage();
        Bitmap bitmap = BitmapFactory.decodeByteArray(image,0,image.length);
        holder.Image.setImageBitmap(bitmap);
        holder.DiseaseName.setText(model.getDisease());
        holder.DeleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int id = model.getId();
                databaseAdapter.deleteDataNew(id);
                context.startActivity(new Intent(context, MainActivity.class));
                ((Activity)context).finish();
            }
        });
    }

    @Override
    public int getItemCount() {
        return modelArrayList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView DiseaseName, Percent;
        ImageView Image,DeleteItem;
        LinearLayout mainLayout;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            DiseaseName = itemView.findViewById(R.id.diseasename);
            Image = itemView.findViewById(R.id.diseaseimage);
            DeleteItem = itemView.findViewById(R.id.deleteicon);
        }

    }

}
