package com.istiaksaif.highlymotavated.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.istiaksaif.highlymotavated.R;
import com.istiaksaif.highlymotavated.Utils.GetServerTime;
import com.istiaksaif.highlymotavated.Utils.ImageGetHelper;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class EditProfileActivity extends AppCompatActivity {
    private TextInputEditText name,title,email,phone,address;
    private ImageView profileImg;
    private CardView saveButton;
    private Toolbar toolbar;
    private TextView joineddate;
    private DatabaseReference databaseReference;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String uid = user.getUid();
    private GoogleSignInClient googleSignInClient;
    private ImageGetHelper getImageFunction;
    private StorageReference storageReference;
    private ProgressDialog progressDialog,pro;
    private Uri image;
    private String profilePhoto,key,imageUrl;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_proffile);
        intent = getIntent();
        key = intent.getStringExtra("key");
        imageUrl = intent.getStringExtra("imageUrl");

        pro = new ProgressDialog(this);
        getImageFunction = new ImageGetHelper(null,this);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();

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

        name = findViewById(R.id.name);
        title = findViewById(R.id.title);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);
        address = findViewById(R.id.address);
        saveButton = findViewById(R.id.savebutton);
        profileImg = findViewById(R.id.profileimage);
        joineddate = findViewById(R.id.joineddate);
        checkPermission();
        readProfileData();
        progressDialog = new ProgressDialog(this);
        profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.setMessage("Update Profile Image");
                profilePhoto = "imageUrl";
                getImageFunction.pickFromGallery();
            }
        });
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadToFirebase(image);
                pro.show();
                pro.setContentView(R.layout.progress_dialog);
                pro.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            }
        });
    }

    private void uploadToFirebase(Uri uri) {
        String Name = name.getText().toString().trim();
        String Title = title.getText().toString().trim();
        String Address = address.getText().toString().trim();
        String Phone = phone.getText().toString().trim();

        HashMap<String, Object> result = new HashMap<>();
        result.put("name", Name);
        result.put("title", Title);
        result.put("address", Address);
        result.put("phone", Phone);
        final StorageReference fileRef = storageReference.child(profilePhoto + "_" + uid);
        if(uri == null){
            databaseReference.child("usersData").child(key).updateChildren(result).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    pro.dismiss();
                    finish();
                }
            });
        }else if(uri != null){
            fileRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            result.put(profilePhoto, uri.toString());
                            databaseReference.child("usersData").child(key).updateChildren(result).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    pro.dismiss();
                                    finish();
                                }
                            });
                        }
                    });
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(EditProfileActivity.this, "Uploading Failed !!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == getImageFunction.IMAGE_PICK_GALLERY_CODE && resultCode == RESULT_OK && data != null) {
            image = data.getData();
            profileImg.setImageURI(image);
            profileImg.setVisibility(View.VISIBLE);
            profileImg.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
    }
    private void readProfileData(){
        Query query = databaseReference.child("usersData").child(key);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String retrivename = ""+snapshot.child("name").getValue();
                String retriveEmail = ""+snapshot.child("email").getValue();
                String receivephone = ""+snapshot.child("phone").getValue();
                String retriveaddress = ""+snapshot.child("address").getValue();
                String retrivetitle = ""+snapshot.child("title").getValue();

                name.setText(retrivename);
                email.setText(retriveEmail);
                phone.setText(receivephone);
                address.setText(retriveaddress);
                if (retrivetitle.equals("")){
                    title.setText("your title");
                }else {
                    title.setText(retrivetitle);
                }
                name.setSelection(name.getText().length());
                title.setSelection(title.getText().length());
                phone.setSelection(phone.getText().length());
                address.setSelection(address.getText().length());

                try {
                    Picasso.get().load(imageUrl).resize(320,320).into(profileImg);
                }catch (Exception e){
                    Picasso.get().load(R.drawable.dropdown).into(profileImg);
                }
                joineddate.setText("Joined "+snapshot.child("signupdate").getValue().toString());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EditProfileActivity.this,"Some Thing Wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= 23 && checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") != (PackageManager.PERMISSION_GRANTED)
                && ContextCompat.checkSelfPermission(getApplicationContext(), "android.permission.CAMERA") != 0) {
            requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.CAMERA"}, 1);
        }
    }
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != 1) {
            return;
        }
        if (grantResults.length <= 0 || grantResults[0] != 0) {
            Toast.makeText(getApplicationContext(), "Please enable to access all feature", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Granted", Toast.LENGTH_SHORT).show();
        }
    }
}