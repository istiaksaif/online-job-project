package com.istiaksaif.testapp.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.istiaksaif.testapp.Model.ImageGetHelper;
import com.istiaksaif.testapp.R;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class EditProfileActivity extends AppCompatActivity {
    private TextInputEditText userName,companyName,division,mobileNo,headQuarter,designation;
    private ImageView editProfileImage,adarimg;
    private Button nextButton;
    private Toolbar toolBar;

    private DatabaseReference databaseReference;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String uid = user.getUid();
    private ProgressDialog progressDialog,pro;

    private ImageGetHelper getImageFunction;
    private StorageReference storageReference;
    private Uri image;

    private Intent intent;
    private String status;
    private RelativeLayout adarlayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        intent = getIntent();
        status = intent.getStringExtra("status");

        checkPermission();

        getImageFunction = new ImageGetHelper(null,this);
        pro = new ProgressDialog(this);
        storageReference = FirebaseStorage.getInstance().getReference();

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

        progressDialog = new ProgressDialog(this);
        databaseReference = FirebaseDatabase.getInstance().getReference();

        userName = findViewById(R.id.userName);
        companyName = findViewById(R.id.company);
        division = findViewById(R.id.division);
        companyName = findViewById(R.id.company);
        mobileNo = findViewById(R.id.mobilenum);
        headQuarter = findViewById(R.id.headQuarter);
        designation = findViewById(R.id.designation);
        editProfileImage = findViewById(R.id.profileimage);
        adarimg = findViewById(R.id.adarimg);
        adarlayout = findViewById(R.id.adarlayout);
        if (status.equals("")){
            adarlayout.setVisibility(View.VISIBLE);
        }else {
            adarlayout.setVisibility(View.GONE);
        }

        editProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getImageFunction.pickFromGallery();
            }
        });
        adarimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getImageFunction.pickFromGallery();
            }
        });

        nextButton = findViewById(R.id.next_button);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadToFirebase(image);
                pro.show();
                pro.setContentView(R.layout.progress_dialog);
                pro.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            }
        });
        Query query = databaseReference.child("SeparateLogin").child("usersData").orderByChild("userId").equalTo(uid);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren()) {
                    String name = ""+dataSnapshot.child("name").getValue();
                    String Company = ""+dataSnapshot.child("company").getValue();
                    String Division = ""+dataSnapshot.child("division").getValue();
                    String img = dataSnapshot.child("image").getValue().toString();
                    String receivephone = ""+dataSnapshot.child("mobile").getValue();
                    String HeadQ = ""+dataSnapshot.child("headQuarter").getValue();
                    String Designation = ""+dataSnapshot.child("designation").getValue();

                    userName.setText(name);
                    companyName.setText(Company);
                    division.setText(Division);
                    mobileNo.setText(receivephone);
                    headQuarter.setText(HeadQ);
                    designation.setText(Designation);
                    try {
                        if (img.equals("")){
                            editProfileImage.setImageResource(R.drawable.edit_profile);
                        }else {
                            Picasso.get().load(img).resize(320,320).into(editProfileImage);
                        }
                    }catch (Exception e){
                        Picasso.get().load(R.drawable.dropdown).into(editProfileImage);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(),"Some Thing Wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadToFirebase(Uri uri) {

        HashMap<String, Object> result = new HashMap<>();
        result.put("designation", designation.getText().toString());
        result.put("name", userName.getText().toString());
        result.put("company", companyName.getText().toString());
        result.put("division", division.getText().toString());
        result.put("mobile", mobileNo.getText().toString());
        result.put("headQuarter", headQuarter.getText().toString());
        final StorageReference adarfileRef = storageReference.child("adar_card"+uid + "." + getContentResolver());
        final StorageReference fileRef = storageReference.child(System.currentTimeMillis() + "." + getContentResolver());
        databaseReference.child("SeparateLogin").child("usersData").child(uid).updateChildren(result).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                if(uri == null){
                    Uri imguri = Uri.parse("");
                    HashMap<String, Object> resultimg = new HashMap<>();
                    resultimg.put("image", imguri.toString());
                    databaseReference.child("SeparateLogin").child("usersData").child(uid).updateChildren(resultimg).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(EditProfileActivity.this, "Successful Edited", Toast.LENGTH_SHORT).show();
                            pro.dismiss();
                            finish();
                        }
                    });
                }else
                if(uri != null){
                    fileRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    HashMap<String, Object> resultimg = new HashMap<>();
                                    resultimg.put("image", uri.toString());
                                    databaseReference.child("SeparateLogin").child("usersData").child(uid).updateChildren(resultimg).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(EditProfileActivity.this, "Successful Edited", Toast.LENGTH_SHORT).show();
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
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditProfileActivity.this, "Uploading Failed !!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == getImageFunction.IMAGE_PICK_GALLERY_CODE && resultCode == RESULT_OK && data != null) {
            image = data.getData();
            editProfileImage.setImageURI(image);
            editProfileImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= 23 && checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") != (PackageManager.PERMISSION_GRANTED)
                && ContextCompat.checkSelfPermission(getApplicationContext(), "android.permission.CAMERA") != 0) {
            requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.CAMERA"}, 1);
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        EditProfileActivity.super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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