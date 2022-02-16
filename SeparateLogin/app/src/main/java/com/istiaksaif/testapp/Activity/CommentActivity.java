package com.istiaksaif.testapp.Activity;

import android.app.Activity;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.istiaksaif.testapp.Adapter.CommentRecyclerViewAdapter;
import com.istiaksaif.testapp.Model.FeedComment;
import com.istiaksaif.testapp.Model.FeedModel;
import com.istiaksaif.testapp.R;

import java.util.ArrayList;
import java.util.List;

public class CommentActivity extends Activity {

    FeedModel feed;
    DatabaseReference databaseReference;

    RecyclerView rvComment;
    EditText etComment;
//    CircleImageView ivSendComment;

    CommentRecyclerViewAdapter adapter;
    List<FeedComment> commentList;

//    SharedPreference preference;
//    int numberOfComments = -1;
//    Utility utility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

//        if (getIntent().getExtras() != null) {
//            feed = (FeedModel) getIntent().getSerializableExtra("feedObject");
//            numberOfComments = feed.getNumberofComments();
//        }
//
//        rvComment = findViewById(R.id.rvComment);
//        etComment = findViewById(R.id.etComment);
//        ivSendComment = findViewById(R.id.ivSendComment);
//
//        commentList = new ArrayList<>();
//        utility = new Utility();
//        preference = new SharedPreference(this);
//        adapter = new CommentRecyclerViewAdapter(this, commentList);
//        rvComment.setAdapter(adapter);
//        rvComment.setLayoutManager(new LinearLayoutManager(this));
//
//        getPostComments();
//
//        ivSendComment.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                String text = etComment.getText().toString();
//                if (text.length() > 0) {
//                    setComment(text);
//                } else {
//                    Toast.makeText(CommentActivity.this, "enter comment first", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });

    }

//    private void getPostComments() {
//
//        databaseReference = FirebaseDatabase.getInstance().getReference();
//        databaseReference.child("Feed-Comment").child(feed.getFeedId()).addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//                try {
//                    FeedComment comment = dataSnapshot.getValue(FeedComment.class);
//                    if (comment != null) {
//                        commentList.add(comment);
//                        adapter.notifyDataSetChanged();
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//            }
//
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                FeedComment feed = null;
//                if (dataSnapshot.getKey() != null)
//                    for (int i = commentList.size() - 1; i >= 0; i--){
//                        feed = dataSnapshot.getValue(FeedComment.class);
//                        commentList.set(i, feed);
//                        adapter.notifyDataSetChanged();
//                        break;
//                    }
//
//            }
//
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//            }
//
//            @Override
//            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//            }
//        });
//
//
//    }
//
//    private void setComment(String text) {
//
//        final FeedComment comment = new FeedComment();
//        comment.setCommentText(text);
//        comment.setCommentPersonName(preference.getName());
//        comment.setCommentTime(System.currentTimeMillis());
//
//
//        //todo update comment count first
//
//        if(numberOfComments > -1) {
//            numberOfComments = numberOfComments + 1;
//            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Feed").child(preference.getCity()).child(feed.getFeedId());
//            reference.child("numberofComments").setValue(numberOfComments).addOnCompleteListener(new OnCompleteListener<Void>() {
//                @Override
//                public void onComplete(@NonNull Task<Void> task) {
//
//                    if(task.isSuccessful()){
//                        databaseReference = FirebaseDatabase.getInstance().getReference().child("Feed-Comment").child(feed.getFeedId()).push();
//                        databaseReference.setValue(comment).addOnCompleteListener(new OnCompleteListener<Void>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Void> task) {
//                                if (!task.isSuccessful()) {
//                                    utility.HideKeyBoard(getWindow().getDecorView(),CommentActivity.this);
//                                    Toast.makeText(CommentActivity.this, "failed to send comment", Toast.LENGTH_SHORT).show();
//                                } else{
//                                    etComment.setText("");
//                                    utility.HideKeyBoard(getWindow().getDecorView(),CommentActivity.this);
//                                }
//                            }
//                        });
//                    } else {
//                        Toast.makeText(CommentActivity.this, "error in posting comment", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            });
//        } else {
//
//            Toast.makeText(this, "Error occurred", Toast.LENGTH_SHORT).show();
//        }
//
//
//    }

}