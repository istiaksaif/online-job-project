package com.rtapps.moc;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.WanderingCubes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.rtapps.moc.Model.LeumiGoodysScraper;
import com.rtapps.moc.Model.OrderInfo;
import com.rtapps.moc.Utils.NotificationUtils;
import com.skydoves.powerspinner.OnSpinnerItemSelectedListener;
import com.skydoves.powerspinner.PowerSpinnerView;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class UploadMenuItemsActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int GalleryPick = 1;
    RelativeLayout backButton;
    RelativeLayout imageBackground;
    String name, catlognumber, expirydate, price;
    String userId;
    String userImage;
    File imageFile;
    ImageView uploadImage, failImage;
    CardView uploadImageBtn;
    CircleImageView profileImage;
    Button uploadDataBtn;
    Calendar calendar;
    PowerSpinnerView spinnerView;
    EditText itemNameET, itemPriceET, itemCatalogNumberET,itemProviderET;
    String selectedTime = "";
    EditText itemExpiryExpiryDateET;
    ProgressBar progressBar, imageLoader;
    RelativeLayout progressBarLayout;
    Button test;
    boolean notificationClicked = false;
    String catalogNumber = "";
    String senderName = "";
    CountDownTimer countDownTimer;
    String SpinnerValue, providerValue;
    private String productRandomKey, downloadImageUrl;
    private StorageReference ProductImagesRef;
    private ImageView InputProductImage;
    private DatabaseReference ProductsRef;
    private Uri ImageUri;


    static Calendar notifyCalendar, notifyCalendar7;

    public static String getFormatedDate(Date date) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
        return dateFormat.format(date);
    }

    @Override
    protected void onStart() {
        super.onStart();
        LanguageSet.setLocale(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_menu_items);

//        getWindow().addFlags(SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setStatusBarColor(UploadMenuItemsActivity.this.getResources().getColor(R.color.background));


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        init();
        String code = null;
        notificationClicked = getIntent().getBooleanExtra("notificationClicked", false);
        if (notificationClicked) {
            catalogNumber = getIntent().getStringExtra("catalogNumber");
            senderName = getIntent().getStringExtra("name");
            code = getIntent().getStringExtra("code");
            String date = getIntent().getStringExtra("date");
            String provider = getIntent().getStringExtra("provider");

            Log.v("TEXTMSG",senderName+"\n"+code+"\n"+date+"\n"+provider);

            SimpleDateFormat paisPlusDF =  new SimpleDateFormat("dd-MM-yyyy");
            SimpleDateFormat sdf =  new SimpleDateFormat("dd MMM yyyy", Locale.forLanguageTag("iw"));

            itemExpiryExpiryDateET.setText(date);
            itemNameET.setText(senderName);
            itemCatalogNumberET.setText(code);
            itemProviderET.setText(provider);

            Date paisPlusDate = null;
            try {
                paisPlusDate = paisPlusDF.parse(date);
                String paisPlusString=sdf.format(paisPlusDate);
                itemExpiryExpiryDateET.setText(paisPlusString);

            } catch (ParseException e) {
                e.printStackTrace();

            }

            if(date==null||date.isEmpty()) {
                try {
                    setDate(catalogNumber.split("-")[1]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if(code==null||code.isEmpty()) {
                try {
                    itemCatalogNumberET.setText(catalogNumber.split("-")[0]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }

        calendar = Calendar.getInstance();
        userId = getIntent().getStringExtra("UserId");
        if(userId==null){userId= FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
        ProductsRef = FirebaseDatabase.getInstance().getReference();
        ProductImagesRef = FirebaseStorage.getInstance().getReference().child("Product Images");
        progressBar = findViewById(R.id.spin_kit);
        progressBarLayout = findViewById(R.id.progress_bar_layout);
        progressBarLayout.setOnClickListener(this);
        Sprite rotatingCircle = new WanderingCubes();


        if(code==null||code.isEmpty()) {
            Intent intent = getIntent();
            String action = intent.getAction();
            String type = intent.getType();

            if (Intent.ACTION_SEND.equals(action) && type != null) {
                if ("text/plain".equals(type)) {
                    handleSendText(intent); // Handle text being sent
                } else if (type.startsWith("image/")) {
                    handleSendImage(intent); // Handle single image being sent
                }
            } else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
                if (type.startsWith("image/")) {
                    handleSendMultipleImages(intent); // Handle multiple images being sent
                }
            } else {
                // Handle other intents, such as being started from the home screen
            }
        }


        countDownTimer = new CountDownTimer(1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                getImage();
            }
        };


        itemNameET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

//visible
                if (!s.toString().equals("")) {
                    imageLoader.setVisibility(View.VISIBLE);
                    countDownTimer.cancel();
                    countDownTimer.start();
                } else {
                    uploadImage.setVisibility(View.GONE);
                    countDownTimer.cancel();
                    imageLoader.setVisibility(View.GONE);

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }

    void handleSendText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);


        if (sharedText != null) {
            // Update UI to reflect text being shared
//            String catalogNumber = getCatalogNumber(sharedText);
//            itemCatlogNumberET.setText(catalogNumber);

            if (sharedText.contains("https://Leumigoodys.co.il/OrderSummary?k=")) {
                LeumiGoodysScraper scraper = new LeumiGoodysScraper();
                OrderInfo info = null;
                try {
                    info = scraper.scrape(getOrderIdFromSms(sharedText));
                    itemNameET.setText(info.title + " " + info.message);
                    itemCatalogNumberET.setText(info.code);
                    setDate(info.date);
//                    itemExpiryExpiryDateET.setText(info.date);


                } catch (IOException e) {
                    // Catch network exceptions
                    System.err.println("Exception: " + e);
                    e.printStackTrace();
                }
            }


        }
    }

    String getOrderIdFromSms(String smsMessages) {
        String orderId = "";
        if (!smsMessages.equals("")) {
            try {
                orderId = smsMessages.split("=")[1];
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return orderId;
    }

    void handleSendImage(Intent intent) {
        Uri imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri != null) {
            // Update UI to reflect image being shared
        }
    }

    void handleSendMultipleImages(Intent intent) {
        ArrayList<Uri> imageUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
        if (imageUris != null) {
            // Update UI to reflect multiple images being shared
        }
    }

    public void init() {
        itemExpiryExpiryDateET = findViewById(R.id.edt_expiry_date_layout);
        itemExpiryExpiryDateET.setOnClickListener(this);
        imageBackground = findViewById(R.id.image_background);
        backButton = findViewById(R.id.back_button_layout);
        backButton.setOnClickListener(this);
        itemPriceET = findViewById(R.id.edt_price);
        itemCatalogNumberET = findViewById(R.id.edt_catlognumber);
//        itemExpiryExpiryDateET=findViewById(R.id.edt_expiry_date);
        itemNameET = findViewById(R.id.edt_name);
        itemProviderET = findViewById(R.id.edt_provider);
        uploadDataBtn = findViewById(R.id.upload_data_btn);
        uploadDataBtn.setOnClickListener(this);

//
        uploadImageBtn = findViewById(R.id.upload_image_card);
//        uploadImageBtn.setOnClickListener(this);
        uploadImage = findViewById(R.id.image_success);
        failImage = findViewById(R.id.image_fail);

        test = findViewById(R.id.test);
        test.setOnClickListener(this);

        imageLoader = findViewById(R.id.image_loader);


        spinnerView = findViewById(R.id.powerspinner);
        spinnerView.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener<String>() {
            @Override
            public void onItemSelected(int i, @Nullable String s, int i1, String t1) {
                Log.i("TAG", "onItemSelected: " + t1);
                SpinnerValue = t1;
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_button_layout:
                onBackPressed();
                break;

            case R.id.upload_data_btn:
                upLoadData();
                break;

            case R.id.test:

                break;

            case R.id.edt_expiry_date_layout:
                DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                        Calendar calendar = Calendar.getInstance();

                        calendar.set(year, month, dayOfMonth);

                        itemExpiryExpiryDateET.setError(null);
                        selectedTime = String.valueOf(calendar.getTimeInMillis());
                        itemExpiryExpiryDateET.setText(getFormatedDate(calendar.getTime()));

                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
                        Date date1 = calendar.getTime();
                        date1.setHours(10);
                        date1.setMinutes(00);
                        date1.setSeconds(00);

                        dateFormat.format(date1);
                        notifyCalendar = Calendar.getInstance();
                        notifyCalendar7 = Calendar.getInstance();

                        notifyCalendar.setTime(date1);
                        notifyCalendar7.setTime(date1);

                        notifyCalendar.add(Calendar.DATE, -1);
                        notifyCalendar7.add(Calendar.DATE, -7);

                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();

                break;
//            case R.id.upload_image_card:
//                ImagePicker.Companion.with(this)
//                        .crop()                    //Crop image(Optional), Check Customization for more option
//                        .compress(512)            //Final image size will be less than 1 MB(Optional)
//                        .maxResultSize(512, 512)    //Final image resolution will be less than 1080 x 1080(Optional)
//                        .start();
//
////               OpenGallery();
//
//                break;

        }
    }

    private void setDate(String date) {
        int year = -1;
        int month = -1;
        int dayOfMonth = -1;
        try {
            dayOfMonth = Integer.parseInt(date.split("\\.")[0]);
            month = Integer.parseInt(date.split("\\.")[1]);
            year = Integer.parseInt(date.split("\\.")[2]);

        } catch (Exception e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.set(2000 + year, month, dayOfMonth);

//        notifyCalendar  = Calendar.getInstance();
//        notifyCalendar.set(2000 + year, month, dayOfMonth,18,50,00);

        itemExpiryExpiryDateET.setError(null);
        selectedTime = String.valueOf(calendar.getTimeInMillis());
        itemExpiryExpiryDateET.setText(getFormatedDate(calendar.getTime()));
    }

    private void getImage() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference listRef = storage.getReference().child("Product Images");
        listRef.listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {

                        for (StorageReference item : listResult.getItems()) {
                            // All the items under listRef.
                            String itemName = "";
                            if (item.getName().toLowerCase().endsWith(".png") || item.getName().toLowerCase().endsWith(".jpg") || item.getName().toLowerCase().endsWith(".jpeg"))
                                itemName = item.getName().substring(0, item.getName().lastIndexOf("."));
                            else
                                itemName = item.getName();

                            String name = itemNameET.getText().toString().trim();
                            String[] str  = name.split(" ", 3);
                            String s;

                            if (str.length>1){
                                s = str[0];
                            } else {
                                s=name;
                            }
                           // if (itemName.equalsIgnoreCase(itemNameET.getText().toString().trim())) {
                            if (itemName.equalsIgnoreCase(name) || itemName.toLowerCase().contains(s.toLowerCase()) || name.toLowerCase().equals(itemName.toLowerCase())) {

                                    Log.d("", "");
                                item.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        //invisible
                                        imageLoader.setVisibility(View.INVISIBLE);

                                        uploadImage.setVisibility(View.VISIBLE);
                                        downloadImageUrl = uri.toString();
                                        Picasso.get().load(uri).into(uploadImage);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        //invisible
                                        imageLoader.setVisibility(View.INVISIBLE);
                                    }
                                });

                                break;
                            }
                            else if (!itemName.toLowerCase().contains(s.toLowerCase()) && itemName.equalsIgnoreCase("default")) {
                                item.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        //invisible
                                        imageLoader.setVisibility(View.INVISIBLE);

                                        uploadImage.setVisibility(View.VISIBLE);
                                        downloadImageUrl = uri.toString();
                                        Picasso.get().load(uri).into(uploadImage);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        //invisible
                                        // imageLoader.setVisibility(View.INVISIBLE);
                                    }
                                });
                            }
                        }

//                   if(name.equals(listResult.getItems().get(1).getName()));
//
//
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Uh-oh, an error occurred!
                        //imageLoader.setVisibility(View.INVISIBLE);
                        imageLoader.setVisibility(View.INVISIBLE);

                        uploadImage.setVisibility(View.VISIBLE);
                        Picasso.get().load(R.drawable.default1).into(uploadImage);

                    }
                });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        imageFile= ImagePicker.Companion.getFile(data);

        if (resultCode == RESULT_OK) {
            ImageUri = data.getData();
            uploadImage.setImageURI(ImageUri);
            uploadImage.setVisibility(View.VISIBLE);
            failImage.setVisibility(View.GONE);
            imageFile = new File(data.getData().getPath());
            imageBackground.setBackgroundResource(R.drawable.white_bg);
        }

    }

    private void OpenGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GalleryPick);
    }


//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data)
//    {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode==GalleryPick  &&  resultCode==RESULT_OK  &&  data!=null)
//        {
//            ImageUri = data.getData();
////            InputProductImage.setImageURI(ImageUri);
//        }
//    }

    public void upLoadData() {


        name = itemNameET.getText().toString().trim();
        expirydate = itemExpiryExpiryDateET.getText().toString().trim();
        catlognumber = itemCatalogNumberET.getText().toString().trim();
        providerValue = itemProviderET.getText().toString().trim();
        //price = itemPriceET.getText().toString().trim();

        boolean isError = false;

        if (TextUtils.isEmpty(name)) {
            isError = true;
            itemNameET.setError("Name");
        }
        if (TextUtils.isEmpty(selectedTime)) {
            isError = true;
            itemExpiryExpiryDateET.setError("Expiry Date");
        }
        if (TextUtils.isEmpty(catlognumber)) {
            isError = true;
            itemCatalogNumberET.setError("Catalog Number");
        }
//        if (TextUtils.isEmpty(price)) {
//            isError = true;
//            itemPriceET.setError("Price");
//        }
        if (TextUtils.isEmpty(providerValue)) {
            isError = true;
            itemProviderET.setError("Provider");
        }
        if (TextUtils.isEmpty(SpinnerValue)) {
            Toast.makeText(UploadMenuItemsActivity.this, R.string.select_category, Toast.LENGTH_SHORT).show();
        } else {
            progressBarLayout.setVisibility(View.VISIBLE);
            addProductToFirebase();
        }

        if (downloadImageUrl == null) {
            isError = true;
            imageBackground.setBackgroundResource(R.drawable.error_bg);

        }

//
//
//        if (TextUtils.isEmpty(name)) {
//            itemNameET.setError("Enter Name");
//
//
//
//            }
//            else if (TextUtils.isEmpty(price)) {
//                itemExpiryExpiryDateET.setText(getFormatedDate(calendar.getTime()));
//
//                itemPriceET.setError("Price");
//
//            }
//
//            else if (ImageUri==null) {
//
//                Toast.makeText(this, "Please Upload your Image", Toast.LENGTH_SHORT).show();
//            }
//
//
//        }
//
//
//
//        else if (TextUtils.isEmpty(catlognumber)){
//            itemCatlogNumberET.setError("CatlogNumber");
//
//
//        }
//
//
//
//        else if (TextUtils.isEmpty(selectedTime)) {
//            itemExpiryExpiryDateET.setError("Exp-Date");
//            itemPriceET.setError("Enter Price");
//
//
//
//
//        }
//
//
//
//
//
//        else if (TextUtils.isEmpty(price)) {
////            itemExpiryExpiryDateET.setText(getFormatedDate(calendar.getTime()));
//
//            itemPriceET.setError("Price");
//
//            if(TextUtils.isEmpty(name)){
//
//                itemNameET.setError("Name");
//
//            }
//           else if(TextUtils.isEmpty(catlognumber)){
//
//                itemCatlogNumberET.setError("CatalogNumber");
//
//            }
//            else if(TextUtils.isEmpty(expirydate)){
//
//                itemExpiryExpiryDateET.setError("Date");
//
//            }
//
//
//
//        }
//
//        else if (ImageUri==null) {
//
//            Toast.makeText(this, "Please Upload your Image", Toast.LENGTH_SHORT).show();
//        }


//        if (isError) {
//
//            Toast.makeText(this, "Kindly fill all data", Toast.LENGTH_LONG).show();
//
//        } else {

//            progressBarLayout.setVisibility(View.VISIBLE);
//
//            addProductToFirebase();
        // }
//            final StorageReference filePath = ProductImagesRef.child(currentRecordRef.getKey());
//
//            final UploadTask uploadTask = filePath.putFile(ImageUri);
//
//
//            uploadTask.addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//                    String message = e.toString();
//                    Toast.makeText(UploadMenuItemsActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
//                }
//            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//
//                    Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
//                        @Override
//                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
//                            if (!task.isSuccessful()) {
//                                throw task.getException();
//                            }
////                                failImage.setVisibility(View.GONE);
////                                uploadImage.setVisibility(View.VISIBLE);
//                            downloadImageUrl = filePath.getDownloadUrl().toString();
//                            return filePath.getDownloadUrl();
//                        }
//                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Uri> task) {
//                            if (task.isSuccessful()) {
//
//
//                                downloadImageUrl = task.getResult().toString();
//
//                                addProductToFirebase();
//                            }
//                        }
//                    });
//                }
//            });
//

        //  }


    }

    private void addProductToFirebase() {
        DatabaseReference currentRecordRef = ProductsRef.child("Food").child(userId).push();

        HashMap<String, Object> productMap = new HashMap<>();
        productMap.put("Name", name);
        productMap.put("ExpDate", expirydate);
        productMap.put("CatalogNumber", catlognumber);
        //productMap.put("Price", price);
        productMap.put("Provider", providerValue);
        productMap.put("image", downloadImageUrl);
        productMap.put("TEST", "TEST");
        productMap.put("isArchive", "false");
        productMap.put("Category", SpinnerValue);


        currentRecordRef.setValue(productMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            finish();
                            progressBarLayout.setVisibility(View.GONE);


                            String messsage = " יפוג בעוד יום. אם כבר מימשת נא להתעלם :) "+ name +" שובר ";
                            reminderNotification(notifyCalendar, messsage);
                             messsage = " יפוג בעוד שבוע. אם כבר מימשת נא להתעלם ;) "+ name +" שובר ";
                            reminderNotification(notifyCalendar7,messsage);
                            Toast.makeText(UploadMenuItemsActivity.this, "Product is added successfully..", Toast.LENGTH_SHORT).show();
                        } else {
                            String message = task.getException().toString();
                            Toast.makeText(UploadMenuItemsActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });


    }


    String getCatalogNumber(String s) {
        String catalogNumber = "";
        Pattern p = Pattern.compile("\\d+");
        Matcher m = p.matcher(s);
        while (m.find()) {
            catalogNumber = m.group();
            break;
        }
        return catalogNumber;
    }


    public void reminderNotification(Calendar notyfycalendar, String messageBody)
    {

        NotificationUtils _notificationUtils = new NotificationUtils(this);
        long _currentTime =System.currentTimeMillis();
        long tenSeconds = 1000 * 30;
        long _triggerReminder = _currentTime + tenSeconds; //triggers a reminder after 10 seconds.
        //36000000
        _notificationUtils.setReminder(System.currentTimeMillis(), messageBody);
    }

}