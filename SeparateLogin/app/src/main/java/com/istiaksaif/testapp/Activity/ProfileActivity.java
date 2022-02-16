package com.istiaksaif.testapp.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.istiaksaif.testapp.R;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {

    private TextView userName,email,companyName,editProfile,division,mobileNo,headQuarter,designation;
    private ImageView imageView,verifyimage;
    private Toolbar toolbar;
    private DatabaseReference databaseReference;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.leftarrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        userName = findViewById(R.id.userName);
        email = findViewById(R.id.textEmail);
        designation = findViewById(R.id.textDesignation);
        imageView = findViewById(R.id.profileimage);
        companyName = findViewById(R.id.textCompany);
        division = findViewById(R.id.textDivision);
        mobileNo = findViewById(R.id.textMobile);
        headQuarter = findViewById(R.id.textHeadQuarter);
        verifyimage = findViewById(R.id.verifyimage);
        editProfile = findViewById(R.id.editProfile);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        Query query = databaseReference.child("SeparateLogin").child("usersData").orderByChild("userId").equalTo(uid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren()) {
                    String name = " "+dataSnapshot.child("name").getValue();
                    String Company = " "+dataSnapshot.child("company").getValue();
                    String Division = " Division: "+dataSnapshot.child("division").getValue();
                    String retriveEmail = " "+dataSnapshot.child("email").getValue();
                    String img = ""+dataSnapshot.child("image").getValue();
                    String receivephone = " Phone Number: "+dataSnapshot.child("mobile").getValue();
                    String HeadQ = " "+dataSnapshot.child("headQuarter").getValue();
                    String Designation = " Designation: "+dataSnapshot.child("designation").getValue();
                    String Status = dataSnapshot.child("status").getValue().toString();

                    userName.setText(name);
                    companyName.setText(Company);
                    division.setText(Division);
                    email.setText(retriveEmail);
                    mobileNo.setText(receivephone);
                    headQuarter.setText(HeadQ);
                    designation.setText(Designation);

                    if (Status.equals("")){
                        verifyimage.setVisibility(View.GONE);
                    }else if(Status.equals("approve")){
                        verifyimage.setVisibility(View.VISIBLE);
                    }else if(Status.equals("pending")){
                        verifyimage.setVisibility(View.GONE);
                    }
                    try {
                        if (img.equals("")){
                            imageView.setImageResource(R.drawable.ic_logo);
                        }else {
                            Picasso.get().load(img).resize(320,320).into(imageView);
                        }
                    }catch (Exception e){
                        Picasso.get().load(R.drawable.dropdown).into(imageView);
                    }
                    editProfile.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
                            intent.putExtra("status",Status);
                            startActivity(intent);
                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(),"Some Thing Wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }
}