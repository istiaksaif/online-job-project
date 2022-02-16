package com.istiaksaif.testapp.Adapter;

import android.content.Context;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.istiaksaif.testapp.Model.FeedComment;
import com.istiaksaif.testapp.R;

import java.util.List;

public class CommentRecyclerViewAdapter extends RecyclerView.Adapter<CommentRecyclerViewAdapter.MyViewHolder> {

    Context context;
    List<FeedComment> commentList;
    LayoutInflater inflater;

    public CommentRecyclerViewAdapter(Context context, List<FeedComment> commentList) {

        this.context = context;
        this.commentList = commentList;
        inflater = LayoutInflater.from(context);
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = inflater.inflate(R.layout.comment_custom_layout,viewGroup,false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {

        FeedComment comment = commentList.get(i);

        Log.d("commentText",comment.getCommentText()+"\n"+comment.getCommentTime());
//        myViewHolder.tvCommentTime.setText(DateUtils.formatTime(comment.getCommentTime()));
        myViewHolder.tvCommentPerson.setText(comment.getCommentPersonName());
        myViewHolder.tvCommentText.setText(comment.getCommentText());

    }

    @Override
    public int getItemCount() {
        if (commentList.size() > 0)
            return commentList.size();
        else
            return 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvCommentPerson,tvCommentTime,tvCommentText;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvCommentPerson = itemView.findViewById(R.id.tvCommentPerson);
            tvCommentTime = itemView.findViewById(R.id.tvCommentTime);
            tvCommentText = itemView.findViewById(R.id.tvCommentText);
        }
    }
}