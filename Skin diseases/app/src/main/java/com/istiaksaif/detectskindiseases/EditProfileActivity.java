package com.istiaksaif.detectskindiseases;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class EditProfileActivity extends AppCompatActivity {
    private TextInputEditText userName,address,email,mobileNo,blood;
    private ImageView editProfileImage;
    private Button nextButton;
    private Toolbar toolBar;

    private ProgressDialog progressDialog,pro;

    private ImageGetHelper getImageFunction;
    private Uri image;

//    ProfileDatabaseHelper databaseHelper;

    public SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        checkPermission();
        SharedPreferences sharedPreferences = getSharedPreferences("ProfileData", MODE_PRIVATE);
        this.sharedPreferences = sharedPreferences;

        getImageFunction = new ImageGetHelper(null,this);
        pro = new ProgressDialog(this);

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

        userName = findViewById(R.id.userName);
        email = findViewById(R.id.textEmail);
        address = findViewById(R.id.textAddress);
        blood = findViewById(R.id.textBlood);
        mobileNo = findViewById(R.id.textMobile);
        editProfileImage = findViewById(R.id.profileimage);

        editProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getImageFunction.pickFromGallery();
            }
        });

//        databaseHelper = new ProfileDatabaseHelper(EditProfileActivity.this);
        nextButton = findViewById(R.id.next_button);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadToFirebase();
                pro.show();
                pro.setContentView(R.layout.progress_dialog);
                pro.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            }
        });
    }

    private void uploadToFirebase() {
        String name = userName.getText().toString().trim();
        String Email = email.getText().toString();
        String Blood = blood.getText().toString();
        String Number = mobileNo.getText().toString();
        String Address = address.getText().toString();

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("name",name);
        editor.putString("email",Email);
        editor.putString("blood",Blood);
        editor.putString("number",Number);
        editor.putString("address",Address);
        editor.apply();
        finish();
//        if(databaseHelper.saveProfile(1,name,Email,Blood,Number,Address)){
//            Toast.makeText(EditProfileActivity.this, "Saved Your Profile", Toast.LENGTH_SHORT).show();
//            finish();
//        }else {
//            Toast.makeText(EditProfileActivity.this, "SomeThing wrong", Toast.LENGTH_SHORT).show();
//            finish();
//        }
    }
    private byte[] convertImageViewToByte(ImageView imageUri) {
        Bitmap bitmap = ((BitmapDrawable)imageUri.getDrawable()).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == getImageFunction.IMAGE_PICK_GALLERY_CODE && resultCode == RESULT_OK && data != null) {
            image = data.getData();
            editProfileImage.setImageURI(image);
            InputStream stream = null;
            try {
                stream = getContentResolver().openInputStream(image);
                Bitmap bitmap = BitmapFactory.decodeStream(stream);
                editProfileImage.setImageBitmap(bitmap);
                editProfileImage.setScaleType(ImageView.ScaleType.CENTER_CROP);

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("image", Arrays.toString(convertImageViewToByte(editProfileImage)));
                editor.apply();
//                if(databaseHelper.saveProfileImg(1,convertImageViewToByte(editProfileImage))){
//                    Toast.makeText(EditProfileActivity.this, "Saved Your Profile pic", Toast.LENGTH_SHORT).show();
////                    finish();
//                }else {
//                    Toast.makeText(EditProfileActivity.this, "SomeThing wrong", Toast.LENGTH_SHORT).show();
//                    finish();
//                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
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