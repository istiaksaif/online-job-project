package com.istiaksaif.detectskindiseases;

import static android.app.Activity.RESULT_OK;
import static android.os.Environment.getExternalStoragePublicDirectory;


import static com.istiaksaif.detectskindiseases.DatabaseHelper.TABLE_NAME;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;

import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.sql.Blob;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class HomeFragment extends Fragment {

    private LinearLayout takeImageCard;

    private TextView detect_his;

    ActivityResultLauncher<Intent>activityResultLauncher;
    public static final int CAMERA_REQUEST_CODE=100;
    public static final int STORAGE_REQUEST_CODE=200;
    public static final int IMAGE_PICK_GALLERY_CODE=300;
    public static final int IMAGE_PICK_CAMERA_CODE=400;
    public Uri imageUri;
    public String pathFile;

    DatabaseHelper myDb;
    RecyclerView recyclerView;
    CustomAdapter customAdapter;
    SQLiteDatabase sqLiteDatabase;

    String cameraPermission[] = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    String storagePermission[] = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        takeImageCard = view.findViewById(R.id.takeimgcard);
        detect_his = view.findViewById(R.id.detect_his);
        takeImageCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePicDialog();
            }
        });

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                try {
                    Bundle extras = result.getData().getExtras();
                    Uri imageUri;
                    Bitmap imageBitmap=(Bitmap) extras.get("data");

                    WeakReference<Bitmap> result1 = new WeakReference<>(Bitmap.createScaledBitmap(imageBitmap
                            ,imageBitmap.getHeight(),imageBitmap.getWidth(),false).copy(
                            Bitmap.Config.RGB_565,true
                    ));
                    Bitmap bm = result1.get();
                    imageUri = saveImage(bm,getActivity());
                    Intent intent = new Intent(getActivity(), PredictActivity.class);
                    intent.putExtra("image",imageUri.toString());
                    getActivity().startActivity(intent);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        myDb = new DatabaseHelper(getActivity());

        recyclerView = view.findViewById(R.id.detectRecycler);
        displayData();
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),RecyclerView.HORIZONTAL,false));
        customAdapter.notifyDataSetChanged();
    }

    private void displayData(){
        sqLiteDatabase=myDb.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select *from "+TABLE_NAME+"",null);
        ArrayList<Model>modelArrayList = new ArrayList<>();
        detect_his.setVisibility(View.VISIBLE);
        while (cursor.moveToNext()){
            int id=cursor.getInt(0);
            byte[] d_image = cursor.getBlob(1);
            String disease_name = cursor.getString(2);
            modelArrayList.add(new Model(id,d_image,disease_name));
        }
        cursor.close();
        customAdapter = new CustomAdapter(getActivity(),R.layout.detection_card,modelArrayList,sqLiteDatabase);
        recyclerView.setAdapter(customAdapter);
        customAdapter.notifyDataSetChanged();
    }
    private Uri saveImage(Bitmap bm, FragmentActivity activity) {
        File imagesFolder = new File(activity.getCacheDir(),"images");
        Uri uri = null;
        try {
            imagesFolder.mkdir();
            File file = new File(imagesFolder,"captured_image.jpg");
            FileOutputStream fileOutputStream = new FileOutputStream(file);

            bm.compress(Bitmap.CompressFormat.PNG,100,fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
            uri = FileProvider.getUriForFile(activity.getApplicationContext(),"com.istiaksaif.detectskindiseases"+".provider",file);

        }catch (FileNotFoundException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }
        return uri;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == IMAGE_PICK_GALLERY_CODE && resultCode == RESULT_OK && data != null) {
            Uri image = data.getData();
            Intent intent = new Intent(getActivity(), PredictActivity.class);
            intent.putExtra("image",image.toString());
            getActivity().startActivity(intent);
        } else if (requestCode == IMAGE_PICK_CAMERA_CODE && resultCode == RESULT_OK && data != null) {
            Uri image = data.getData();
            Intent intent = new Intent(getActivity(), PredictActivity.class);
            intent.putExtra("image",image.toString());
            getActivity().startActivity(intent);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_home, container, false);
        return view;
    }

    private boolean checkStoragePermission(){
        boolean result = ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestStoragePermission(){
        requestPermissions(storagePermission, STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermission(){
        boolean result = ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);

        boolean result1 = ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }
    public void requestCameraPermission(){
        requestPermissions(cameraPermission, CAMERA_REQUEST_CODE);
    }
    private void checkPermission(){
//        if(Build.VERSION.SDK_INT>=23){
//            if (ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.CAMERA)!=
//                    PackageManager.PERMISSION_GRANTED){
//                requestPermissions(new String[]{Manifest.permission.CAMERA},1);
//            }
            if (Build.VERSION.SDK_INT >= 23 || ContextCompat.checkSelfPermission(getActivity(),"android.permission.WRITE_EXTERNAL_STORAGE") != (PackageManager.PERMISSION_GRANTED)
                    && ContextCompat.checkSelfPermission(getActivity(), "android.permission.CAMERA") != 0) {
                requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.CAMERA"}, 1);
            }
//        }
    }

    public void showImagePicDialog() {
        String options[] = {"Camera","Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Pick Image");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which ==0){
                    if(!checkCameraPermission() || !checkStoragePermission()){
                        checkPermission();
                        requestStoragePermission();
                    }else {
//                        pickFromCamera();
                        newPickFromCamera();
                    }
                }
                else if (which == 1){
                    if(!checkStoragePermission()){
                        requestStoragePermission();
                    }
                    else{
                        pickFromGallery();
                    }
                }
            }
        });
        builder.create().show();
    }

    private void newPickFromCamera() {
        Intent takepicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        activityResultLauncher.launch(takepicture);
    }

    public void pickFromCamera(){
        Intent takePic = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if(takePic.resolveActivity(getActivity().getPackageManager())!=null){
                File picFile = null;
                picFile= createPhotoFile();
                if (picFile !=null){
                    pathFile = picFile.getAbsolutePath();
                    imageUri = FileProvider.getUriForFile(getActivity(),"com.istiaksaif.detectskindiseases.fileprovider",picFile);
                    takePic.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                    getActivity().startActivityForResult(takePic,IMAGE_PICK_CAMERA_CODE);
                }
            }
    }

    private File createPhotoFile() {
        String name = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File storageDir = getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = null;
        try {
            image = File.createTempFile(name,".jpg",storageDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    public void pickFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, IMAGE_PICK_GALLERY_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case CAMERA_REQUEST_CODE:{
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    boolean cameraAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if(cameraAccepted && writeStorageAccepted){
                        pickFromCamera();
                    }
                    else{
                        Toast.makeText(getActivity(),"Please camera enable", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
            case STORAGE_REQUEST_CODE:{
                if(grantResults.length>0){
//                    boolean writeStorageAcceptedd = grantResults[1] == PackageManager.PERMISSION_GRANTED;
//                    if(writeStorageAccepted){
//                        pickFromGallery();
//                    }
//                    else{
//                        Toast.makeText(getActivity(),"Please enable", Toast.LENGTH_SHORT).show();
//                    }
                }
            }
            break;
        }
    }

}