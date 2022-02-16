package com.istiaksaif.testapp.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.istiaksaif.testapp.R;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class FeedBackActivity extends AppCompatActivity {

    private Toolbar toolBar;
    Button btnFeedback;
    EditText etFeedback;
    ImageView ivBackground;

    DatabaseReference database;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String uid = user.getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_back);

        toolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.leftarrow);
        toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        database = FirebaseDatabase.getInstance().getReference();

        ivBackground = findViewById(R.id.ivBackground);
        btnFeedback = findViewById(R.id.btnFeedback);
        etFeedback = findViewById(R.id.etFeedback);

        btnFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (!etFeedback.getText().toString().trim().equals("")) {

                    HashMap<String, String> feedbackMap = new HashMap<>();
                    feedbackMap.put("id", uid);
                    feedbackMap.put("message", etFeedback.getText().toString());
                    Query query = database.child("SeparateLogin").child("usersData").orderByChild("userId").equalTo(uid);
                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot dataSnapshot:snapshot.getChildren()) {
                                String name = ""+dataSnapshot.child("name").getValue();

                                feedbackMap.put("name", name);
                                database = FirebaseDatabase.getInstance().getReference().child("Feedback").push();
                                database.setValue(feedbackMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            etFeedback.setText("");
                                            Toast.makeText(FeedBackActivity.this, "Thanks for your feedback", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(FeedBackActivity.this, "Error occurred... try again later", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(getApplicationContext(),"Some Thing Wrong", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(FeedBackActivity.this, "EEnter message first", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}