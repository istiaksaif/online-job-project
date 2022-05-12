package com.rtapps.moc;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.WanderingCubes;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.rtapps.moc.Adapter.ArchiveItemsAdapter;
import com.rtapps.moc.Model.Item;

import java.util.ArrayList;
import java.util.Objects;

public class ArchiveItemsActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher {
    RecyclerView foodItemRecyclerView;
    RelativeLayout backBtn;
    String phone;
    String userId;
    String shareName, shareExpDate, shareCatlogNumber;
    EditText searchItemsEdittext;
    ArrayList<Item> items;
    RecyclerView.LayoutManager layoutManager;
    ArchiveItemsAdapter archiveItemsAdapter;
    FirebaseDatabase firebaseDatabase;
    RelativeLayout progressBarLayout;
    private DatabaseReference ProductsRef;
    private DatabaseReference UpdateRef;

    @Override
    protected void onStart() {
        super.onStart();
        LanguageSet.setLocale(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archive_items);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setStatusBarColor(ArchiveItemsActivity.this.getResources().getColor(R.color.background));
        init();
//        populateFoodItems();
        phone = getIntent().getStringExtra("phone");
        userId = getIntent().getStringExtra("UserId");
        firebaseDatabase = FirebaseDatabase.getInstance();

        // below line is used to get
        // reference for our database.

        foodItemRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(ArchiveItemsActivity.this);
        foodItemRecyclerView.setLayoutManager(layoutManager);
        ProductsRef = FirebaseDatabase.getInstance().getReference().child("Food").child(userId);
        progressBarLayout = findViewById(R.id.progress_bar_layout_archived);
        Sprite rotatingCircle = new WanderingCubes();


        initRecyclerView();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS}, 44);
        }

//        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_SMS},44);


    }

    private void initRecyclerView() {

        archiveItemsAdapter = new ArchiveItemsAdapter(this, new ArrayList<>());
        foodItemRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        foodItemRecyclerView.setAdapter(archiveItemsAdapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        ProductsRef.orderByChild("isArchive").equalTo("true").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot != null) {

                    items = new ArrayList<>();
                    progressBarLayout.setVisibility(View.GONE);

                    for (DataSnapshot nodeSnapshot : snapshot.getChildren()) {
                        Log.d("", "");

                        Item item = new Item();

                        item.setID(nodeSnapshot.getKey());
                        item.setName(Objects.requireNonNull(nodeSnapshot.child("Name").getValue()).toString());
                        item.setExpDate(Objects.requireNonNull(nodeSnapshot.child("ExpDate").getValue()).toString());

                        item.setCatalogNumber(Objects.requireNonNull(nodeSnapshot.child("CatalogNumber").getValue()).toString());
                        item.setPrice(Objects.requireNonNull(nodeSnapshot.child("Price").getValue()).toString());
                        item.setImage(nodeSnapshot.child("image").getValue() == null ? ""
                                : Objects.requireNonNull(nodeSnapshot.child("image").getValue()).toString());

                        items.add(item);
                        shareExpDate = item.getExpDate();
                        shareName = item.getName();
                        shareCatlogNumber = item.getCatalogNumber();

                    }

                    Log.d("tag", "populating data");
                    if (archiveItemsAdapter != null)
                        archiveItemsAdapter.updateItems(items);

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void init() {
        foodItemRecyclerView = findViewById(R.id.archive_item_recycler);

        backBtn = findViewById(R.id.back_button_layout);
        backBtn.setOnClickListener(this);
        searchItemsEdittext = findViewById(R.id.search_items_textview);


        searchItemsEdittext.addTextChangedListener(this);

    }

    private void populateFoodItems(ArrayList<Item> items) {


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.upload_screen_btn:

                Intent i = new Intent(ArchiveItemsActivity.this, UploadMenuItemsActivity.class);
                i.putExtra("phone", phone);
                startActivity(i);

                break;
            case R.id.back_button_layout:

                onBackPressed();
                finish();
                break;


        }
    }

    public void archiveItem(String name, String catalogNumber, String image, String id) {
        progressBarLayout.setVisibility(View.VISIBLE);

        UpdateRef = FirebaseDatabase.getInstance().getReference().child("Food").child(userId).child(id).child("isArchive");
        UpdateRef.setValue("false").addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                progressBarLayout.setVisibility(View.INVISIBLE);
            }
        });
        onResume();
    }


    public void dailogShare(String formatedDate, String name, String catalogNumber, String image, String ID) {

        StorageReference productImagesRef = FirebaseStorage.getInstance().getReference().child("Product Images");
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(ArchiveItemsActivity.this);

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dailog_share, null);
        dialogBuilder.setView(dialogView);
        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.setCancelable(true);

        ImageView whatsappBtn = dialogView.findViewById(R.id.whatsapp_btn);
        ImageView gmailBtn = dialogView.findViewById(R.id.gmail_btn);


        whatsappBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_VIEW);
//                intent.setData(Uri.parse("http://api.whatsapp.com/send?phone=" + "&text=" + "Name: " + name + "\n" + "Catalog Number: " + catalogNumber + "\n" + "Date: " + formatedDate + "\nIamge: " + uri.toString()));
                intent.setData(Uri.parse("http://api.whatsapp.com/send?phone=" + "&text=" + "Name: " + name + "\n" + "Catalog Number: " + catalogNumber + "\n" + "Date: " + formatedDate));
                startActivity(intent);

//                StorageReference fileRef = productImagesRef.child(ID);
//
//                try {
//                    localFile = File.createTempFile("images", ".jpg");
//
//                    fileRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
//                        @Override
//                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
//                            // Local temp file has been created
//                            Uri uri = FileProvider.getUriForFile(ArchiveItemsActivity.this, fileProvider, localFile);
//                            Intent intent = new Intent(Intent.ACTION_SEND);
//                            intent.setData(Uri.parse("http://api.whatsapp.com/send?phone=" + "&text=" + "Name: " + name + "\n" + "Catalog Number: " + catalogNumber + "\n" + "Date: " + formatedDate + "\nIamge: " + uri.toString()));
////                        intent.putExtra(Intent.EXTRA_STREAM, uri);
////                        intent.setType("*/*");
////                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                            startActivity(intent);
//
//                        }
//                    }).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception exception) {
//                            // Handle any errors
//                        }
//                    });
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//
//                productImagesRef.child(ID).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                    @Override
//                    public void onSuccess(Uri uri) {
//                        // Got the download URL for 'users/me/profile.png'
//
//
//                        Intent intent = new Intent();
//                        intent.setAction(Intent.ACTION_SEND);
//                        intent.putExtra(Intent.EXTRA_TEXT, "sdfsdfs");
//                        intent.setType("text/plain");
//                        intent.putExtra(Intent.EXTRA_STREAM,uri);
//                        intent.setType("*/*");
//                        intent.setPackage("com.whatsapp");
//                        startActivity(intent);
////
////                        Intent intent = new Intent(Intent.ACTION_SEND);
////                        intent.setData(Uri.parse("http://api.whatsapp.com/send?phone=" + "&text=" + "Name: " + name + "\n" + "Catalog Number: " + catalogNumber + "\n" + "Date: " + formatedDate + "\nIamge: "+uri.toString()));
////                        intent.putExtra(Intent.EXTRA_STREAM, uri);
////                        intent.setType("image/png");
////                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
////                        startActivity(intent);
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception exception) {
//                        // Handle any errors
//                    }
//                });


            }


        });


        gmailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:")); // only email apps should handle this
//                intent.putExtra(Intent.EXTRA_EMAIL, "moazzamsaleem66@gmail.com");
                intent.putExtra(Intent.EXTRA_SUBJECT, "Product Details");
                intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.name)+": " + name  + getString(R.string.catalogNumber) +": "+ catalogNumber + getString(R.string.ExpiryDate)+": " + formatedDate);

                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }


        });

        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

        ArrayList<Item> filteredItems = new ArrayList<>();
        if (items != null) {
            for (int i = 0; i < items.size(); i++) {

                if (items.get(i).getName().toLowerCase().contains(s.toString().toLowerCase())) {

                    filteredItems.add(items.get(i));

                }

            }

            archiveItemsAdapter.updateItems(filteredItems);


        }


    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}



