package com.istiaksaif.detectskindiseases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DatabaseVersion = 7;
    private static final String DatabaseName = "SkinDiseases";
    public static final String TABLE_NAME = "DetectionOutput";
    public static final String KEY_ROWID = "id";

    public DatabaseHelper(Context context){
        super(context,DatabaseName,null,DatabaseVersion);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "create table "+TABLE_NAME+"(id integer primary key, img blob, disease text, percent text)";
        db.execSQL(query);
//        db.execSQL("CREATE TABLE IF NOT EXISTS DetectionOutput(id INTEGER PRIMARY KEY AUTOINCREMENT, img BLOB, disease TEXT, percent TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String query = "drop table if exists " + TABLE_NAME + "";
        db.execSQL(query);
    }

    public boolean save(byte[] img,String disease,String percent){
        try {
            ContentValues cv = new ContentValues();
            cv.put("img",img);
            cv.put("disease",disease);
            cv.put("percent",percent);
            SQLiteDatabase db = this.getWritableDatabase();
            db.insert(TABLE_NAME,null,cv);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

//    Cursor readAllData(){
//        Cursor cursor = null;
//        try {
//            String query = "SELECT * FROM " + TABLE_NAME;
//            SQLiteDatabase db = this.getReadableDatabase();
//
//            if(db != null){
//                cursor = db.rawQuery(query, null);
//            }
//            return cursor;
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//        return cursor;
//    }
}
