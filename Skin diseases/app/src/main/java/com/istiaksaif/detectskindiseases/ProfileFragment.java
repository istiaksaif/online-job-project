package com.istiaksaif.detectskindiseases;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


public class ProfileFragment extends Fragment {

    private TextView userName,email,address,editProfile,mobileNo,blood;
    private ImageView imageView;

//    ProfileDatabaseHelper myDb;
//    SQLiteDatabase sqLiteDatabase;
    public SharedPreferences sharedPreferences;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("ProfileData", MODE_PRIVATE);
        this.sharedPreferences = sharedPreferences;

        userName = view.findViewById(R.id.userName);
        email = view.findViewById(R.id.textEmail);
        imageView = view.findViewById(R.id.profileimage);
        address = view.findViewById(R.id.textAddress);
        blood = view.findViewById(R.id.textBlood);
        mobileNo = view.findViewById(R.id.textMobile);
        editProfile = view.findViewById(R.id.editProfile);
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                startActivity(intent);
            }
        });

//        myDb = new ProfileDatabaseHelper(getActivity());

        displayData();
    }

    private void displayData() {
        String name = sharedPreferences.getString("name",null);
        String emailDb = sharedPreferences.getString("email",null);
        String bloodDb = sharedPreferences.getString("blood",null);
        String numberDb = sharedPreferences.getString("number",null);
        String addressDb = sharedPreferences.getString("address",null);
        String imageDb = sharedPreferences.getString("image",null);

        if(name != null){
            editProfile.setVisibility(View.GONE);
        }
        try {
            userName.setText(name);
            email.setText(emailDb);
            blood.setText(bloodDb);
            mobileNo.setText(numberDb);
            address.setText(addressDb);
            if (imageDb != null) {
                String[] split = imageDb.substring(1, imageDb.length()-1).split(", ");
                byte[] array = new byte[split.length];
                for (int i = 0; i < split.length; i++) {
                    array[i] = Byte.parseByte(split[i]);
                }
                Bitmap bitmap = BitmapFactory.decodeByteArray(array, 0, array.length);
                imageView.setImageBitmap(bitmap);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

//    private void displayData(){
//        sqLiteDatabase=myDb.getReadableDatabase();
//        Cursor cursor = sqLiteDatabase.rawQuery("select *from "+TABLE_NAME+"",null);
//        if (cursor.moveToNext()){
//            do{
//                int id=cursor.getInt(0);
//                byte[] d_image = cursor.getBlob(1);
//                String nameDb = cursor.getString(2);
//                String emailDb = cursor.getString(3);
//                String bloodDb = cursor.getString(4);
//                String numberDb = cursor.getString(5);
//                String addressDb = cursor.getString(6);
//
//                try {
//                    userName.setText(nameDb);
//                    email.setText(emailDb);
//                    blood.setText(bloodDb);
//                    mobileNo.setText(numberDb);
//                    address.setText(addressDb);
//                    Bitmap bitmap = BitmapFactory.decodeByteArray(d_image, 0, d_image.length);
//                    imageView.setImageBitmap(bitmap);
//                }catch (Exception e){
//                    e.printStackTrace();
//                }
//            }while (cursor.moveToNext());
//        }
//        cursor.close();
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        return view;
    }
}