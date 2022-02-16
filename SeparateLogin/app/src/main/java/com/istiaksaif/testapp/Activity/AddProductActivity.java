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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
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

import java.util.ArrayList;
import java.util.HashMap;

public class AddProductActivity extends AppCompatActivity {

    private TextInputEditText productName,sideEffects,price,composition,pack,substitute;
    private Button submit;
    private Toolbar toolBar;
    private MaterialAutoCompleteTextView company;
    private TextInputLayout companyLayout;
    private ImageView addImage;

    private DatabaseReference databaseReference;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String uid = user.getUid();
    private ImageGetHelper getImageFunction;
    private StorageReference storageReference;
    private Uri image;
    private ProgressDialog pro;

    private Intent intent;
    private String productId,companyName,companyInfoId,limit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        intent = getIntent();
        productId = intent.getStringExtra("productId");
        companyName = intent.getStringExtra("companyName");
        companyInfoId = intent.getStringExtra("NewCompaniesId");
        limit = intent.getStringExtra("limit");

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
        checkPermission();

        databaseReference = FirebaseDatabase.getInstance().getReference();

        productName = findViewById(R.id.productName);
        sideEffects = findViewById(R.id.side_effect);
        price = findViewById(R.id.price);
        composition = findViewById(R.id.composition);
        pack = findViewById(R.id.pack);
//        substitute = findViewById(R.id.subtitle);
        company = findViewById(R.id.company);
        submit = findViewById(R.id.save_button);
        companyLayout = findViewById(R.id.companyLayout);
        addImage = findViewById(R.id.addimage);
        company.setText(companyName);
        companyLayout.setEnabled(false);
        companyLayout.setEndIconVisible(false);
        company.setClickable(false);
        company.setFocusable(false);
        company.setFocusableInTouchMode(false);

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getImageFunction.pickFromGallery();
            }
        });

        Query query = databaseReference.child("Companies");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> provinceList = new ArrayList<>();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()) {
                    try {
                        String provinceName = dataSnapshot.child("name").getValue(String.class);
                        provinceList.add(provinceName);
                    }catch (Exception e){

                    }
                }
                ArrayAdapter<String> arrayAdapterState = new ArrayAdapter<>(getApplicationContext(),R.layout.usertype_item,provinceList);
                ((MaterialAutoCompleteTextView) companyLayout.getEditText()).setAdapter(arrayAdapterState);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AddProductActivity.this,"Some Thing Wrong", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        Query query1 = databaseReference.child("SeparateLogin").child("New-Companies").orderByChild("userId").equalTo(uid);
        query1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    try{
                        String NewCompaniesId = dataSnapshot.child("NewCompaniesId").getValue().toString();
                        submit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                uploadToFirebase(image,NewCompaniesId);
                                pro.show();
                                pro.setContentView(R.layout.progress_dialog);
                                pro.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                            }
                        });

                    }catch (Exception e){

                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AddProductActivity.this,"Some Thing Wrong", Toast.LENGTH_SHORT).show();
            }
        });

        if (!productId.equals("Add")) {
            GetDataFromFirebase();
        }
    }

    private void GetDataFromFirebase() {

        Query query = databaseReference.child("SeparateLogin").child("Product").child(productId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    productName.setText(snapshot.child("productName").getValue().toString());
                    company.setText(snapshot.child("company").getValue().toString());
                    price.setText(snapshot.child("price").getValue().toString()+" Rupee");
                    sideEffects.setText(snapshot.child("sideEffects").getValue().toString());
                    String img = snapshot.child("image").getValue().toString();
                    composition.setText(snapshot.child("composition").getValue().toString());
                    pack.setText(snapshot.child("package").getValue().toString());

                    try {
                        if (img.equals("")){
                            addImage.setImageResource(R.drawable.ic_logo);
                        }else {
                            Picasso.get().load(img).resize(480,480).into(addImage);
                        }
                    }catch (Exception e){
                        Picasso.get().load(R.drawable.dropdown).into(addImage);
                    }
                }catch (Exception e) {
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AddProductActivity.this,"Some Thing Wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadToFirebase(Uri uri,String NewCompaniesId) {
        if (productId.equals("Add")) {
            int leftlimit = Integer.parseInt(limit) - 1;
            HashMap<String, Object> result2 = new HashMap<>();
            result2.put("limitp",String.valueOf(leftlimit));
            databaseReference.child("SeparateLogin").child("New-Companies").child(companyInfoId).updateChildren(result2);
            String key = databaseReference.child("SeparateLogin").child("usersData").child(uid).push().getKey();
            HashMap<String, Object> result1 = new HashMap<>();
            result1.put("productName", productName.getText().toString());
            result1.put("company", company.getText().toString());
            result1.put("sideEffects", sideEffects.getText().toString());
            result1.put("price", price.getText().toString());
            result1.put("substitute", "nil");
            result1.put("composition", composition.getText().toString());
            result1.put("package", pack.getText().toString());
            result1.put("productId",key);
            result1.put("userId",uid);
            result1.put("NewCompaniesId",NewCompaniesId);
            result1.put("status","pending");
            final StorageReference fileRef = storageReference.child(System.currentTimeMillis() + "." + getContentResolver());
            databaseReference.child("SeparateLogin").child("Product").child(key).updateChildren(result1).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    if(uri == null){
                        Uri imguri = Uri.parse("https://firebasestorage.googleapis.com/v0/b/test-project-7da9e.appspot.com/o/medicine.png?alt=media&token=35d90ee7-7c41-4da3-9bc0-71a5441ef579");
                        HashMap<String, Object> resultimg = new HashMap<>();
                        resultimg.put("image", imguri.toString());
                        databaseReference.child("SeparateLogin").child("Product").child(key).updateChildren(resultimg).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(AddProductActivity.this, "Product Added", Toast.LENGTH_SHORT).show();
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
                                        databaseReference.child("SeparateLogin").child("Product").child(key).updateChildren(resultimg).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(AddProductActivity.this, "Product Added", Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(AddProductActivity.this, "Uploading Failed !!", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AddProductActivity.this, "Uploading Failed !!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }
        else {
            String key = productId;
            HashMap<String, Object> result1 = new HashMap<>();
            result1.put("productName", productName.getText().toString());
            result1.put("company", company.getText().toString());
            result1.put("sideEffects", sideEffects.getText().toString());
            result1.put("price", price.getText().toString());
            result1.put("substitute", "nil");
            result1.put("composition", composition.getText().toString());
            result1.put("package", pack.getText().toString());
            result1.put("productId",key);
            result1.put("userId",uid);
            result1.put("NewCompaniesId",NewCompaniesId);
            result1.put("status","pending");
            final StorageReference fileRef = storageReference.child(System.currentTimeMillis() + "." + getContentResolver());
            databaseReference.child("SeparateLogin").child("Product").child(key).updateChildren(result1).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    if(uri == null){
                        Uri imguri = Uri.parse("https://firebasestorage.googleapis.com/v0/b/test-project-7da9e.appspot.com/o/medicine.png?alt=media&token=35d90ee7-7c41-4da3-9bc0-71a5441ef579");
                        HashMap<String, Object> resultimg = new HashMap<>();
                        resultimg.put("image", imguri.toString());
                        databaseReference.child("SeparateLogin").child("Product").child(key).updateChildren(resultimg).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(AddProductActivity.this, "Product Added", Toast.LENGTH_SHORT).show();
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
                                        databaseReference.child("SeparateLogin").child("Product").child(key).updateChildren(resultimg).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(AddProductActivity.this, "Product Added", Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(AddProductActivity.this, "Uploading Failed !!", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AddProductActivity.this, "Uploading Failed !!", Toast.LENGTH_SHORT).show();
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
            addImage.setImageURI(image);
            addImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= 23 && checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") != (PackageManager.PERMISSION_GRANTED)
                && ContextCompat.checkSelfPermission(getApplicationContext(), "android.permission.CAMERA") != 0) {
            requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.CAMERA"}, 1);
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        AddProductActivity.super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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

