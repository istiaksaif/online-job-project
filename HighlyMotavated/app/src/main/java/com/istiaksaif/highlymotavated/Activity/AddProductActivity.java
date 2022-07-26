package com.istiaksaif.highlymotavated.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
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
import com.istiaksaif.highlymotavated.Adapter.RecyclerImageAdapter;
import com.istiaksaif.highlymotavated.Model.ProductItem;
import com.istiaksaif.highlymotavated.R;
import com.istiaksaif.highlymotavated.Utils.GetServerTime;
import com.istiaksaif.highlymotavated.Utils.ImageGetHelper;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class AddProductActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageView productImage,addproductImage;
    private TextInputEditText productName,productDescription,productPrice;
    private MaterialAutoCompleteTextView days,category,type;
    private TextInputLayout categoryLayout,daysLayout,typelayout;
    private CardView button,backToHome,addAnother;
    private ImageGetHelper getImageFunction;
    private GetServerTime getServerTime;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String uid = user.getUid();
    private ProgressDialog pro;
    private Uri image;
    private ArrayList<Uri> imageUriList;
    private ArrayList<String>imageKey,sendProductId;
    private RecyclerView imageRecycler;
    private RecyclerImageAdapter recyclerImageAdapter;
    private RelativeLayout afterAddedLayout;
    private TextView addproductName,addproductPrice;
    private int count = 0;
    private ProductItem productItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        getImageFunction = new ImageGetHelper(null,this);
        getServerTime = new GetServerTime(this);
        pro = new ProgressDialog(this);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();

        imageUriList = new ArrayList<>();
        imageKey = new ArrayList<>();
        sendProductId = new ArrayList<>();
        imageRecycler = findViewById(R.id.recycler_image);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        imageRecycler.setLayoutManager(layoutManager);
        imageRecycler.setHasFixedSize(true);
        recyclerImageAdapter = new RecyclerImageAdapter(imageUriList,imageKey,sendProductId,this);
        imageRecycler.setAdapter(recyclerImageAdapter);

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
        productImage = findViewById(R.id.productimage);
        productName = findViewById(R.id.productname);
        productDescription = findViewById(R.id.productdescription);
        productPrice = findViewById(R.id.productprice);
        days = findViewById(R.id.productdays);
        button = findViewById(R.id.addbutton);
        category = findViewById(R.id.category);
        type = findViewById(R.id.type);
        daysLayout = findViewById(R.id.dayslayout);
        categoryLayout = findViewById(R.id.categorylayout);
        typelayout = findViewById(R.id.typelayout);
        Query query = databaseReference.child("Category");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> list = new ArrayList<>();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()) {
                    String categoryName = dataSnapshot.child("name").getValue(String.class);

                    list.add(categoryName);
                }
                ArrayAdapter<String> arrayAdapterState = new ArrayAdapter<>(getApplicationContext(),R.layout.listtype_item,list);
                ((MaterialAutoCompleteTextView) categoryLayout.getEditText()).setAdapter(arrayAdapterState);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AddProductActivity.this,"Some Thing Wrong", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        String []optionUniName = {"1 day","2 days","3 days","7 days","10 days","14 days"
                ,"21 days","30 days"};
        ArrayAdapter<String> arrayAdapterUni = new ArrayAdapter<>(this,R.layout.listtype_item,optionUniName);
        ((MaterialAutoCompleteTextView) daysLayout.getEditText()).setAdapter(arrayAdapterUni);

        String []optionType = {"Buy Now","Place Bid"};
        ArrayAdapter<String> arrayAdapterType= new ArrayAdapter<>(this,R.layout.listtype_item,optionType);
        ((MaterialAutoCompleteTextView) typelayout.getEditText()).setAdapter(arrayAdapterType);
        checkPermission();

        if(getIntent().getExtras() != null){
            if (getIntent().getStringExtra("intentType").equals("edit")) {
                productItem = (ProductItem) getIntent().getSerializableExtra("productObjects");
                GetDataFromFirebase(productItem.getProductId(),"null");
                productName.setText(productItem.getProductName());
                productDescription.setText(productItem.getProductDescription());
                productPrice.setText(productItem.getProductPrice());
//                days.setText(productItem.getProductDays());
                category.setText(productItem.getCategory());
                type.setText(productItem.getSellType());
            }
        }
        productImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getImageFunction.pickImg();
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getIntent().getExtras() != null && getIntent().getStringExtra("intentType").equals("edit")){
                    GetDataFromFirebase(productItem.getProductId(),"edit");
                }else {
                    uploadToFirebase(null,"new");
                }
            }
        });
        afterAddedLayout = findViewById(R.id.addedlayout);
        addproductName = findViewById(R.id.added_productname);
        addproductPrice = findViewById(R.id.added_productprice);
        addproductImage = findViewById(R.id.added_productimage);
        backToHome = findViewById(R.id.back_home);
        addAnother = findViewById(R.id.add_new);
    }

    private void GetDataFromFirebase(String productId,String intentType) {
        List<String> imageArray = new ArrayList<>();
        List<String> keyArray = new ArrayList<>();
        Query query = databaseReference.child("Products").child(productId).child("Images");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    String images = dataSnapshot.child("productImage").getValue(String.class);
                    imageArray.add(images);
                    String imageskey = dataSnapshot.getKey();
                    keyArray.add(imageskey);
                }
                if (intentType.equals("edit")){
                    try {
                        uploadToFirebase(imageArray, "edit");
                    }catch (Exception e){

                    }
                }else {
                    for(int i=0; i<imageArray.size(); i++){
                        imageUriList.add(Uri.parse(imageArray.get(i)));
                        imageKey.add(keyArray.get(i));
                        if(imageUriList.size()==5)
                            productImage.setVisibility(View.GONE);
                    }
                    sendProductId.add(productId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void uploadToFirebase(List<String> countnum, String intentType) {
        String productId = databaseReference.child(uid).push().getKey();
        String ProName = productName.getText().toString().trim();
        String ProDes = productDescription.getText().toString().trim();
        String ProPrice = productPrice.getText().toString().trim();
        String ProDays = days.getText().toString().trim();
        String Category = category.getText().toString().trim();
        String Type = type.getText().toString().trim();
        if(imageUriList.size()==0){
            Toast.makeText(this, "Please select one or more!", Toast.LENGTH_SHORT).show();
            return;
        }else if (TextUtils.isEmpty(Category)){
            category.setError("please select Product Category");
            return;
        }else if (TextUtils.isEmpty(Type)){
            type.setError("please select Product sellType");
            return;
        }else if (TextUtils.isEmpty(ProName)){
            productName.setError("please enter Product Name");
            return;
        }else if (TextUtils.isEmpty(ProDes)){
            productDescription.setError("please write Description about Product");
            return;
        }else if (TextUtils.isEmpty(ProPrice)){
            productPrice.setError("please enter Product Price");
            return;
        }else if (TextUtils.isEmpty(ProDays)){
            days.setError("please select Product bid length duration");
            return;
        }
        pro.show();
        pro.setContentView(R.layout.progress_dialog);
        pro.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        getServerTime.getDateTime(new GetServerTime.VolleyCallBack() {
            @Override
            public void onGetDateTime(String dateTime) {
                String splitTime[] = dateTime.split("T");
                String time = splitTime[0]+" "+splitTime[1];
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = null;
                try {
                    date = sdf.parse(time);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                long timestamp = date.getTime();
                String splitDays[] = ProDays.split(" ");
                String endTimestamp = String.valueOf((Long.valueOf(Integer.parseInt(splitDays[0])*86400000))+timestamp);
                HashMap<String, Object> result = new HashMap<>();
                result.put("productName", ProName);
                result.put("category", Category);
                result.put("sellType", Type);
                result.put("productDescription", ProDes);
                result.put("productPrice", ProPrice);
                result.put("dateTime",time);
                result.put("timestamp",timestamp);
                result.put("endTimestamp",endTimestamp);
                result.put("userId", uid);
                if(intentType.equals("edit")){
                    editItemSaved(result,countnum,imageUriList.size());
                }else {
                    result.put("productId",productId);
                    for(count = 0; count<imageUriList.size(); count++){
                        Uri imgUri = imageUriList.get(count);
//                    final StorageReference fileRef = storageReference.child("ProductImageFolder").child(productId).child(imageUriList.get(count).getLastPathSegment()+"."+System.currentTimeMillis() + "." + getContentResolver());
                        final StorageReference fileRef = storageReference.child("ProductImageFolder").child(productId).child("image"+count);
                        databaseReference.child("Products").child(productId).updateChildren(result).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                fileRef.putFile(imgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                HashMap<String, Object> resultimg = new HashMap<>();
                                                resultimg.put("productImage", uri.toString());
                                                databaseReference.child("Products").child(productId).child("Images").push().setValue(resultimg);
                                                successfulIntent(uri,ProName,ProPrice);
                                                pro.dismiss();
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
//                        }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(AddProductActivity.this, "Uploading Failed !!", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });
                    }
                }
            }
        });
    }

    private void editItemSaved(HashMap<String, Object> result,List<String> countnum,int imagecount) {
        result.put("productId",productItem.getProductId());
        a: for(count = countnum.size(); count<imagecount; count++) {
            String imgname[] = imageUriList.get(count).toString().split("://");
            boolean b = imgname[0].equals("content");
            if (b) {
                Uri imgUri = imageUriList.get(count);
                final StorageReference fileRef = storageReference.child("ProductImageFolder").child(productItem.getProductId()).child("image" + count);
                databaseReference.child("Products").child(productItem.getProductId()).updateChildren(result).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        fileRef.putFile(imgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        HashMap<String, Object> resultimg = new HashMap<>();
                                        resultimg.put("productImage", uri.toString());
                                        databaseReference.child("Products").child(productItem.getProductId()).child("Images").push().setValue(resultimg);
                                        Toast.makeText(AddProductActivity.this, "Product Updated", Toast.LENGTH_SHORT).show();
                                        pro.dismiss();
                                        finish();
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
                                Toast.makeText(AddProductActivity.this, "updated Failed !!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddProductActivity.this, "updated Uploading Failed !!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }else {
                break a;
            }
        }
        pro.dismiss();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == getImageFunction.IMAGE_PICK_GALLERY_CODE && resultCode == RESULT_OK) {
            if(data.getClipData()!=null){
                int count = 5-(imageUriList.size());
                if(data.getClipData().getItemCount()>count) {
                    Toast.makeText(this, "You have selected more items than the maximum limit! you can select "+count+" more images!", Toast.LENGTH_LONG).show();
                }else {
                for(int i=0; i<data.getClipData().getItemCount(); i++){
                    image = data.getClipData().getItemAt(i).getUri();
                    imageUriList.add(image);
                    addproductImage.setImageURI(data.getClipData().getItemAt(0).getUri());
                    if(imageUriList.size()==5)
                        productImage.setVisibility(View.GONE);
                }
                recyclerImageAdapter.notifyDataSetChanged();
                }
            }else if(data.getData() != null){
                image = data.getData();
                imageUriList.add(image);
                addproductImage.setImageURI(image);
                if(imageUriList.size()==5)
                    productImage.setVisibility(View.GONE);
                recyclerImageAdapter.notifyDataSetChanged();
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

    private void successfulIntent(Uri uri,String ProName,String ProPrice){
        afterAddedLayout.setVisibility(View.VISIBLE);
        addproductName.setText(ProName);
        addproductPrice.setText(ProPrice);

        backToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        addAnother.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddProductActivity.this,AddProductActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}