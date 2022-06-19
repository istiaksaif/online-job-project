package com.istiaksaif.highlymotavated.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.istiaksaif.highlymotavated.Model.User;
import com.istiaksaif.highlymotavated.R;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {

    private ImageView profileImage,editButton;
    private TextView name,profession,email,phone,address,joineddate;
    private RelativeLayout wallet,privacy,logout;
    private CardView status1,status2,status3,status4;

    private Toolbar toolbar;
    private DatabaseReference databaseReference,dataRef;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String uid = user.getUid();
    private GoogleSignInClient googleSignInClient;
    private Intent intent;
    private String key,imageUrl,balancestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        intent = getIntent();
        key = intent.getStringExtra("key");
        imageUrl = intent.getStringExtra("imageUrl");
        balancestore = intent.getStringExtra("balance");

        databaseReference = FirebaseDatabase.getInstance().getReference();
        dataRef = FirebaseDatabase.getInstance().getReference();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("");
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.leftarrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        profileImage = findViewById(R.id.profileimage);
        editButton = findViewById(R.id.editprofile);
        name = findViewById(R.id.name);
        profession = findViewById(R.id.title);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);
        address = findViewById(R.id.address);
        wallet = findViewById(R.id.walletcard);
        privacy = findViewById(R.id.privacycard);
        logout = findViewById(R.id.logout);
        status1 = findViewById(R.id.status1);
        status2 = findViewById(R.id.status2);
        status3 = findViewById(R.id.status3);
        status4 = findViewById(R.id.status4);
        joineddate = findViewById(R.id.joineddate);

        Query query = databaseReference.child("users").orderByChild("userId").equalTo(uid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren()) {
                    String key = dataSnapshot.child("key").getValue().toString();
                    dataRef.child("usersData").child(key)
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {
                                    try {
                                        String retrivename = ""+dataSnapshot2.child("name").getValue();
                                        String retriveEmail = ""+dataSnapshot2.child("email").getValue();
                                        String img = dataSnapshot2.child("imageUrl").getValue().toString();
                                        String receivephone = ""+dataSnapshot2.child("phone").getValue();
                                        String retriveaddress = ""+dataSnapshot2.child("address").getValue();
                                        String retrivetitle = ""+dataSnapshot2.child("title").getValue();
                                        User userd= new User();
                                        userd.setImageUrl(img);
                                        userd.setKey(key);
                                        name.setText(retrivename);
                                        email.setText(retriveEmail);
                                        phone.setText(receivephone);
                                        address.setText(retriveaddress);
                                        profession.setText(retrivetitle);

                                        try {
                                            Picasso.get().load(img).resize(480,480).into(profileImage);
                                        }catch (Exception e){
                                            Picasso.get().load(R.drawable.dropdown).into(profileImage);
                                        }
                                        joineddate.setText("Joined "+dataSnapshot2.child("signupdate").getValue().toString());
                                    }catch (Exception e){

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this,"Some Thing Wrong", Toast.LENGTH_SHORT).show();
            }
        });
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this,EditProfileActivity.class);
                intent.putExtra("imageUrl",imageUrl);
                intent.putExtra("key",key);
                startActivity(intent);
            }
        });
        wallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this,WalletActivity.class);
                intent.putExtra("name",name.getText().toString());
                intent.putExtra("balance",balancestore);
                startActivity(intent);
            }
        });
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail().build();
        googleSignInClient = GoogleSignIn.getClient(this,googleSignInOptions);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();
            }
        });
    }

    private void signOut(){
        FirebaseAuth.getInstance().signOut();
        googleSignInClient.signOut();
        Intent intent1 = new Intent(this, LogInActivity.class);
        intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent1);
    }
}