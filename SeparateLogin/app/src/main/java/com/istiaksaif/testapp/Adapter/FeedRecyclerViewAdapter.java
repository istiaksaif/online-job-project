package com.istiaksaif.testapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.istiaksaif.testapp.Activity.CommentActivity;
import com.istiaksaif.testapp.Model.FeedModel;
import com.istiaksaif.testapp.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FeedRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    static int OFFSET = 4;
    private static final int TYPE_AD = 0;
    private static final int TYPE_NORMAL = 1;
    private ArrayList<Object> data = new ArrayList<>();
//    private ArrayList<UnifiedNativeAd> ads = new ArrayList<>();

    Context context;
    //    List<FeedModel> feedModelList;
    LayoutInflater inflater;

    public FeedRecyclerViewAdapter(Context context, List<FeedModel> feedModelList) {

        this.context = context;
//        this.feedModelList = feedModelList;
        inflater = LayoutInflater.from(context);
        Log.d("viewNotWorking", "FeedRecyclerViewAdapter: "+feedModelList.size());
    }

    public void setData(int i, List<FeedModel> feedModels) {
        this.data.addAll(i,feedModels);
    }

//    public void MixData() {
//        if (data.size() == 0) return;
//        if (ads.size() != 0) {
//            List<Object> o = new ArrayList<>();
//            int num = 0;
//            for (int i = 0; i < data.size(); i++) {
//                if (num + OFFSET == i) {
//                    //REMOVE +1
//                    num += OFFSET;
//                    int x = new Random().nextInt(ads.size());
//                    o.add(ads.get(x));
//                    //REMOVE THIS CONTINUE NOT TO SKIP INSERT DATA
//                    //continue;
//                }
//                o.add(data.get(i));
//            }
//            data.clear();
//            data.addAll(o);
//        }
//        notifyDataSetChanged();
//    }
//
//    public void setAds(List<UnifiedNativeAd> ads) {
//        this.ads.addAll(ads);
//    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (i == TYPE_AD) {
            View view = LayoutInflater.from(this.context).inflate(R.layout.ad_template_small, viewGroup, false);
//            return new AdTemplateViewHolder(view);
        }
        View view = LayoutInflater.from(this.context).inflate(R.layout.feed_custom_layout, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder myViewHolder, int i) {

        int viewtype = getItemViewType(i);
        if (viewtype == TYPE_AD) {
//            AdTemplateViewHolder vh = (AdTemplateViewHolder) myViewHolder;
//            vh.setUnifiedNativeAd((UnifiedNativeAd) data.get(i));
//            return;
        }

        menuItem(myViewHolder, i);
    }

    private void menuItem(RecyclerView.ViewHolder myHolder, int i) {
        final FeedModel model = (FeedModel) data.get(i);
        MyViewHolder myViewHolder = (MyViewHolder) myHolder;
        myViewHolder.tvFeedPerson.setText(model.getFeedPerson());
        try {
//            String time = DateUtils.formatDate(model.getFeedDate()) + "\t " + DateUtils.formatTime(model.getFeedDate());
//            myViewHolder.tvFeedTime.setText(
//                    time);
        } catch (NullPointerException e) {
            myViewHolder.tvFeedTime.setText(
                    "");
        }

        myViewHolder.tvFeedText.setText(model.getFeedText());
        if (model.getFeedType().equals("img") || model.getFeedType().equals("both")) {
            myViewHolder.ivFeed.setVisibility(View.VISIBLE);
//            Glide.with(context).load(model.getFeedImg()).into(myViewHolder.ivFeed);

        } else {
            myViewHolder.ivFeed.setVisibility(View.GONE);
        }

        myViewHolder.tvFeedComment.setText(model.getNumberofComments() + " Comments");

        myViewHolder.linearFeedComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, CommentActivity.class);
                intent.putExtra("feedObject", model);
                context.startActivity(intent);
            }
        });


        myViewHolder.ivFeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                new Utility().fullImageDialog(null, model.getFeedImg(), context, false);
            }
        });

    }

    @Override
    public int getItemCount() {
        if (data.size() > 0) {
            Log.d("viewNotWorking", "getItemCount: " + data.size());
            return data.size();
        }
        else
            return 0;
    }


//    @Override
//    public int getItemViewType(int position) {
//        return data.get(position) instanceof UnifiedNativeAd ? TYPE_AD : TYPE_NORMAL;
//    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvFeedPerson, tvFeedTime, tvFeedText, tvFeedComment;
        ImageView ivFeed;
        LinearLayout linearFeedComment;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvFeedPerson = itemView.findViewById(R.id.tvFeedPerson);
            tvFeedTime = itemView.findViewById(R.id.tvFeedTime);
            tvFeedText = itemView.findViewById(R.id.tvFeedText);
            tvFeedComment = itemView.findViewById(R.id.tvFeedComment);
            ivFeed = itemView.findViewById(R.id.ivFeed);
            linearFeedComment = itemView.findViewById(R.id.linearFeedComment);
        }
    }


}
