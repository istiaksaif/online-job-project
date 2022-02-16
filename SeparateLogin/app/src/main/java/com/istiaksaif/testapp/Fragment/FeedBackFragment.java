package com.istiaksaif.testapp.Fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.istiaksaif.testapp.R;

import java.util.HashMap;

public class FeedBackFragment extends Fragment {

    Button btnFeedback;
    EditText etFeedback;
    ImageView ivBackground;

    DatabaseReference database;

    String userId;

    public FeedBackFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (getActivity() != null) {
            getActivity().setTitle("Feedback");
        }


        View view = inflater.inflate(R.layout.fragment_feed_back, container, false);
        init(view);
        return view;
    }

    private void init(View view) {

        if(getActivity() != null) {
            ivBackground = view.findViewById(R.id.ivBackground);
            btnFeedback = view.findViewById(R.id.btnFeedback);
            etFeedback = view.findViewById(R.id.etFeedback);


            btnFeedback.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {

                    if (!etFeedback.getText().toString().trim().equals("")) {

                        HashMap<String, String> feedbackMap = new HashMap<>();
                        if (userId != null) {
                            feedbackMap.put("id", userId);
                            feedbackMap.put("message", etFeedback.getText().toString());
//                            feedbackMap.put("name", preferences.getName());
                                database = FirebaseDatabase.getInstance().getReference().child("Feedback").push();
                                database.setValue(feedbackMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            etFeedback.setText("");
                                            Toast.makeText(getActivity(), "Thanks for your feedback", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getActivity(), "Error occurred... try again later", Toast.LENGTH_SHORT).show();
                                    }
                                });

                        } else {
                            Toast.makeText(getActivity(), "Invalid user", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getActivity(), "EEnter message first", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }

}

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_feed_back, container, false);
//    }
//}