package com.istiaksaif.detectskindiseases;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ProfileDatabaseHelper  {
//extends SQLiteOpenHelper
//    private static final int DatabaseVersion = 6;
//    private static final String DatabaseName = "SkinDiseases";
//    public static final String TABLE_NAME = "ProfileDB";
//
//    public ProfileDatabaseHelper(Context context){
//        super(context,DatabaseName,null,DatabaseVersion);
//    }
//    @Override
//    public void onCreate(SQLiteDatabase db) {
//        String query = "create table "+TABLE_NAME+"(id integer primary key, img blob, name text, email text, blood text, mobile text, address text)";
//        db.execSQL(query);
//    }
//
//    @Override
//    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        String query = "drop table if exists " + TABLE_NAME + "";
//        db.execSQL(query);
//    }
//
//    public boolean saveProfile(int id,String name,String email,String blood,String mobile,String address){
//        try {
//            ContentValues cv = new ContentValues();
//            cv.put("id",id);
//            cv.put("name",name);
//            cv.put("email",email);
//            cv.put("blood",blood);
//            cv.put("mobile",mobile);
//            cv.put("address",address);
//            SQLiteDatabase db = this.getWritableDatabase();
//            db.insert(TABLE_NAME,null,cv);
//            return true;
//        }catch (Exception e){
//            e.printStackTrace();
//            return false;
//        }
//    }
//    public boolean saveProfileImg(int id, byte[] img){
//        try {
//            ContentValues cv = new ContentValues();
//            cv.put("id",id);
//            cv.put("img",img);
//            SQLiteDatabase db = this.getWritableDatabase();
//            db.insert(TABLE_NAME,null,cv);
//            return true;
//        }catch (Exception e){
//            e.printStackTrace();
//            return false;
//        }
//    }

}
