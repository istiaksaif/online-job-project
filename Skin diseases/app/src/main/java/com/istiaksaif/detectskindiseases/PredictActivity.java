package com.istiaksaif.detectskindiseases;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.airbnb.lottie.LottieAnimationView;
import com.istiaksaif.detectskindiseases.ml.SkinDiseases;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class PredictActivity extends AppCompatActivity {

    Toolbar toolbar;

    private TextView predictResult,predictResultPercent,confidence,confidencePercent,confidence1,confidencePercent1,
            confidence2,confidencePercent2,savedOnSql,description;
    private Bitmap img;
    private ImageView predictImg;

    private static final float IMAGE_STD= 1.0f;
    private static final float PROBABILITY_STD = 255.0f;
    private Intent intent;
    private String intentImage;
    private LottieAnimationView loading;
    private LinearLayout l0,l1,l2,buttonLayout;

    int delay = 1000;
    private ProgressDialog progressDialog;

    DatabaseHelper factory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_predict);
        intent = getIntent();
        intentImage = intent.getStringExtra("image");

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_transparent);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        predictResult = findViewById(R.id.predictResult);
        predictResultPercent = findViewById(R.id.predictResultPercent);
        predictImg = findViewById(R.id.predictimg);
        confidence = findViewById(R.id.confidence);
        confidencePercent = findViewById(R.id.percent);
        confidence1 = findViewById(R.id.confidence1);
        confidencePercent1 = findViewById(R.id.percent1);
        confidence2 = findViewById(R.id.confidence2);
        confidencePercent2 = findViewById(R.id.percent2);
        l0 = findViewById(R.id.l1);
        l1 = findViewById(R.id.l2);
        l2 = findViewById(R.id.l3);
        buttonLayout = findViewById(R.id.button_layout);

        Uri image = Uri.parse(intentImage);
        InputStream stream = null;
        try {
            stream = getContentResolver().openInputStream(image);
            Bitmap bitmap = BitmapFactory.decodeStream(stream);
            predictImg.setImageBitmap(bitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        loading = (LottieAnimationView) findViewById(R.id.loading);
        loading.setVisibility(View.VISIBLE);
        loading.setAnimation(R.raw.loading);
        loading.loop(true);
        loading.playAnimation();
        description = findViewById(R.id.disease_desc);

        factory = new DatabaseHelper(PredictActivity.this);
        savedOnSql = findViewById(R.id.save);
        savedOnSql.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String PredictCase = predictResult.getText().toString().trim();
                String PredictPercent = predictResultPercent.getText().toString().trim();
                if(factory.save(convertImageViewToByte(predictImg),PredictCase,PredictPercent)){
                    Toast.makeText(PredictActivity.this, "Saved Your Detection Result", Toast.LENGTH_SHORT).show();
                    finish();
                }else {
                    Toast.makeText(PredictActivity.this, "Not Found", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }

    private byte[] convertImageViewToByte(ImageView predictImg) {
        Bitmap bitmap = ((BitmapDrawable)predictImg.getDrawable()).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    public void classifyImage(Bitmap image){
        try {
            SkinDiseases model = SkinDiseases.newInstance(this);

            // Creates inputs for reference.
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * 224 * 224 * 3);
            byteBuffer.order(ByteOrder.nativeOrder());

            // get 1D array of 224 * 224 pixels in image
            int [] intValues = new int[224 * 224];
            image.getPixels(intValues, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());

            // iterate over pixels and extract R, G, and B values. Add to bytebuffer.
            int pixel = 0;
            for(int i = 0; i < 224; i++){
                for(int j = 0; j < 224; j++){
                    int val = intValues[pixel++]; // RGB
                    byteBuffer.putFloat(((val >> 16) & 0xFF) * (IMAGE_STD / PROBABILITY_STD));
                    byteBuffer.putFloat(((val >> 8) & 0xFF) * (IMAGE_STD / PROBABILITY_STD));
                    byteBuffer.putFloat((val & 0xFF) * (IMAGE_STD / PROBABILITY_STD));
                }
            }

            inputFeature0.loadBuffer(byteBuffer);

            // Runs model inference and gets result.
            SkinDiseases.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            float[] confidences = outputFeature0.getFloatArray();
            // find the index of the class with the biggest confidence.
            int maxPos = 0;
            float maxConfidence = 0;
            for(int i = 0; i < confidences.length; i++){
                if(confidences[i] > maxConfidence){
                    maxConfidence = confidences[i];
                    maxPos = i;
                }
            }
            String[] classes = {"Acne & Rosacea", "Eczema", "Herpes HPV", "Melanoma Skin Cancer"
                    , "Normal Skin", "Actinic Keratosis", "Basal Cell Carcinoma","Bengin"
                    ,"Dermatofibroma","Mallgnant","Nevus"};
            predictResult.setVisibility(View.VISIBLE);
            predictResult.setText(classes[maxPos]);
            predictResultPercent.setText(String.format("%.1f%%", confidences[maxPos] * 100));

            description.setVisibility(View.VISIBLE);
            if(maxPos==0){
                description.setText(R.string.s0);
            }else if(maxPos==1){
                description.setText(R.string.s1);
            }else if(maxPos==2){
                description.setText(R.string.s2);
            }else if(maxPos==3){
                description.setText(R.string.s3);
            }else if(maxPos==5){
                description.setText(R.string.s5);
            }else if(maxPos==6){
                description.setText(R.string.s6);
            }else if(maxPos==7){
                description.setText(R.string.s7);
            }else if(maxPos==9){
                description.setText(R.string.s9);
            }else if(maxPos==10){
                description.setText(R.string.s10);
            }
//            predictResultPercent.setText(String.format("%.1f%%", confidences[3] * 100));
//            predictResultPercent.setText(String.format("%.1f%%", confidences[4] * 100));
//            predictResultPercent.setText(String.format("%.1f%%", confidences[5] * 100));
//            predictResultPercent.setText(String.format("%.1f%%", confidences[6] * 100));
//            predictResultPercent.setText(String.format("%.1f%%", confidences[7] * 100));
//            predictResultPercent.setText(String.format("%.1f%%", confidences[8] * 100));
//            predictResultPercent.setText(String.format("%.1f%%", confidences[9] * 100));
//            predictResultPercent.setText(String.format("%.1f%%", confidences[10] * 100));
//            predictResultPercent.setText(String.format("%.1f%%", confidences[11] * 100));
//            predictResultPercent.setText(String.format("%.1f%%", confidences[12] * 100));



            predictResultPercent.setVisibility(View.VISIBLE);
//            predictResultPercent.setText(confidences[]);

//            l0.setVisibility(View.VISIBLE);
//            l1.setVisibility(View.VISIBLE);
//            l2.setVisibility(View.VISIBLE);
            buttonLayout.setVisibility(View.VISIBLE);

            String s = "l"+maxPos;
            if (s.equals("l0")) {
                l0.setBackground(getDrawable(R.color.pink));
                confidence.setTextColor(getResources().getColor(R.color.white));
                confidencePercent.setTextColor(getResources().getColor(R.color.white));
            }else if (s.equals("l1")) {
                l1.setBackground(getDrawable(R.color.green));
                confidence1.setTextColor(getResources().getColor(R.color.white));
                confidencePercent1.setTextColor(getResources().getColor(R.color.white));
            }else if (s.equals("l2")) {
                l2.setBackground(getDrawable(R.color.pink));
                confidence2.setTextColor(getResources().getColor(R.color.white));
                confidencePercent2.setTextColor(getResources().getColor(R.color.white));
            }

            confidence.setText(classes[0]);
            confidencePercent.setText(String.format("%.1f%%", confidences[0] * 100));
            confidence1.setText(classes[1]);
            confidencePercent1.setText(String.format("%.1f%%", confidences[1] * 100));
            confidence2.setText(classes[2]);
            confidencePercent2.setText(String.format("%.1f%%", confidences[2] * 100));
            loading.setVisibility(View.GONE);

            model.close();
        } catch (IOException e) {

        }
    }

    @Override
    protected void onStart(){
        super.onStart();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Uri image = Uri.parse(intentImage);
                try {
                    img = MediaStore.Images.Media.getBitmap(getContentResolver(),image);
                    img = Bitmap.createScaledBitmap(img, 224, 224, false);
                    classifyImage(img);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, delay);
    }

    public void onBackPressed(){
        finish();
    }
}