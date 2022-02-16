package com.istiaksaif.testapp.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.istiaksaif.testapp.Model.ImageGetHelper;
import com.istiaksaif.testapp.Model.User;
import com.istiaksaif.testapp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RegistrationActivity extends AppCompatActivity {

    private TextView signin;
    private TextInputLayout textInputLayoutState,textInputLayoutCity;
    private TextInputEditText fullName,email,designation,company,division,mobile,password,passwordRepeat;
    private MaterialAutoCompleteTextView state,city;
    private Button registrationButton;
    private FirebaseAuth firebaseAuth;
    private String PROVINCE;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private ProgressDialog progressDialog;
    private StorageReference storageReference;
    private Uri image;
    private ProgressDialog pro;
    private ImageGetHelper getImageFunction;
    private ImageView adarImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        getImageFunction = new ImageGetHelper(null,this);
        pro = new ProgressDialog(this);
        storageReference = FirebaseStorage.getInstance().getReference();

        fullName = findViewById(R.id.name);
        email = findViewById(R.id.eamil);
        designation = findViewById(R.id.designation);
        company = findViewById(R.id.company);
        division = findViewById(R.id.division);
        mobile = findViewById(R.id.mobilenum);
        state = findViewById(R.id.state);
        city = findViewById(R.id.city);
        password = findViewById(R.id.pass);
        passwordRepeat = findViewById(R.id.passretype);
        textInputLayoutState = findViewById(R.id.stateLayout);
        textInputLayoutCity = findViewById(R.id.cityLayout);
        adarImage = findViewById(R.id.adarimg);
        checkPermission();
        adarImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getImageFunction.pickFromGallery();
            }
        });
        databaseReference = FirebaseDatabase.getInstance().getReference();
        Query query = databaseReference.child("Province");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> provinceList = new ArrayList<>();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()) {
                    String provinceName = dataSnapshot.child("name").getValue(String.class);

                    provinceList.add(provinceName);
                }
                ArrayAdapter<String> arrayAdapterState = new ArrayAdapter<>(getApplicationContext(),R.layout.usertype_item,provinceList);
                ((MaterialAutoCompleteTextView) textInputLayoutState.getEditText()).setAdapter(arrayAdapterState);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(RegistrationActivity.this,"Some Thing Wrong", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        if(state.getText().toString().equals("")){
            textInputLayoutCity.setVisibility(View.GONE);
        }else {
            textInputLayoutCity.setVisibility(View.VISIBLE);
        }
        state.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                if(state.getText().toString().equals("")){
                    textInputLayoutCity.setVisibility(View.GONE);
                }else {
                    textInputLayoutCity.setVisibility(View.VISIBLE);
                }
                PROVINCE = state.getText().toString();
                Query query1 = databaseReference.child("City").child(PROVINCE);
                query1.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ArrayList<String> cityList = new ArrayList<>();
                        for(DataSnapshot dataSnapshot:snapshot.getChildren()) {
                            String cityName = dataSnapshot.child("name").getValue(String.class);

                            cityList.add(cityName);
                        }
                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getApplicationContext(),R.layout.usertype_item,cityList);
                        ((MaterialAutoCompleteTextView) textInputLayoutCity.getEditText()).setAdapter(arrayAdapter);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(RegistrationActivity.this,"Some Thing Wrong", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }
        });

        registrationButton = findViewById(R.id.reg_button);
        registrationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Register();
            }
        });

        //intent Login page
        signin = findViewById(R.id.signinactivity);
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegistrationActivity.this, LogInActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private  void collectToken(){
        FirebaseUser user = firebaseAuth.getCurrentUser();
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if(!task.isSuccessful()){
                    return;
                }
                String token = task.getResult();
                databaseReference.child("SeparateLogin").child("usersData").orderByChild("userId").equalTo(user.getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                                    databaseReference.child("SeparateLogin").child("usersData").child(dataSnapshot.getKey())
                                            .child("token")
                                            .setValue(token);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
            }
        });
    }

    private void uploadToFirebase(Uri uri,String key) {
        final StorageReference fileRef = storageReference.child("adar_card"+key + "." + getContentResolver());
        if(uri == null){
            Uri imguri = Uri.parse("");
            HashMap<String, Object> resultimg = new HashMap<>();
            resultimg.put("adarcard", imguri.toString());
            databaseReference.child("SeparateLogin").child("usersData").child(key).updateChildren(resultimg).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    pro.dismiss();
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
                            resultimg.put("adarcard", uri.toString());
                            resultimg.put("status", "approve");
                            databaseReference.child("SeparateLogin").child("usersData").child(key).updateChildren(resultimg).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    pro.dismiss();
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
                    Toast.makeText(RegistrationActivity.this, "Uploading Failed !!", Toast.LENGTH_SHORT).show();
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
            adarImage.setImageURI(image);
            adarImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= 23 && checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") != (PackageManager.PERMISSION_GRANTED)
                && ContextCompat.checkSelfPermission(getApplicationContext(), "android.permission.CAMERA") != 0) {
            requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.CAMERA"}, 1);
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        RegistrationActivity.super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != 1) {
            return;
        }
        if (grantResults.length <= 0 || grantResults[0] != 0) {
            Toast.makeText(getApplicationContext(), "Please enable to access all feature", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Granted", Toast.LENGTH_SHORT).show();
        }
    }

    private void Register() {
        String FullName = fullName.getText().toString();
        String Email = email.getText().toString();
        String Designation = designation.getText().toString();
        String Company = company.getText().toString();
        String Division = division.getText().toString();
        String MobileNo = mobile.getText().toString();
        String State = state.getText().toString();
        String City = city.getText().toString();


        String Password = password.getText().toString();
        String Password_re = passwordRepeat.getText().toString();

        if (TextUtils.isEmpty(FullName)){
            Toast.makeText(RegistrationActivity.this, "please enter your Name", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(Designation)){
            Toast.makeText(RegistrationActivity.this, "please enter your Designation", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(Company)){
            Toast.makeText(RegistrationActivity.this, "please enter your Company", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(Division)){
            Toast.makeText(RegistrationActivity.this, "please enter Division", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(MobileNo)){
            Toast.makeText(RegistrationActivity.this, "please enter your MobileNo", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(State)){
            Toast.makeText(RegistrationActivity.this, "please enter State", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(City)){
            Toast.makeText(RegistrationActivity.this, "please enter City", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (TextUtils.isEmpty(Email)){
            Toast.makeText(RegistrationActivity.this, "please enter Email", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (TextUtils.isEmpty(Password)){
            Toast.makeText(RegistrationActivity.this, "please enter password",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        else if (TextUtils.isEmpty(Password_re)){
            Toast.makeText(RegistrationActivity.this, "please enter password",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        else if (!Password.equals(Password_re)){
            passwordRepeat.setError("password not match");
            return;
        }
        else if (Password.length()<8){
            Toast.makeText(RegistrationActivity.this, "password week & password length " +
                    "at least 8 character", Toast.LENGTH_SHORT).show();
            return;
        }
        else if(!isValidEmail(Email)){
            email.setError("Invalid email");
            return;
        }
        progressDialog = new ProgressDialog(RegistrationActivity.this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        firebaseAuth.createUserWithEmailAndPassword(Email,Password).addOnCompleteListener(
                RegistrationActivity.this, new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String uid = firebaseAuth.getCurrentUser().getUid();
                            User userhelp = new User(FullName,Email,Designation,Company,Division,MobileNo
                                    ,State,City,"",uid,"","User","","unverified");
                            databaseReference.child("SeparateLogin").child("usersData").child(uid).setValue(userhelp);
                            collectToken();
                            uploadToFirebase(image,uid);
                            pro.show();
                            pro.setContentView(R.layout.progress_dialog);
                            pro.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                            Toast.makeText(RegistrationActivity.this, "Registration " +
                                    "Successful", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(RegistrationActivity.this,
                                    HomeActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(RegistrationActivity.this, "Authentication " +
                                    "Failed "+task.getException().toString(), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(RegistrationActivity.this,
                                    RegistrationActivity.class);
                            startActivity(intent);
                            progressDialog.dismiss();
                            finish();
                        }
                    }
                });
    }
    private Boolean isValidEmail(CharSequence target){
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
}