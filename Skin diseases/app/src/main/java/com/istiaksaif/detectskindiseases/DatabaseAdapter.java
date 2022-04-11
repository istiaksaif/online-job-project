package com.istiaksaif.detectskindiseases;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;


public class DatabaseAdapter {
    DatabaseHelper helper;
    SQLiteDatabase db;
    Context context;

    public DatabaseAdapter(Context context) {
        helper = new DatabaseHelper(context);
        db = helper.getWritableDatabase();
        this.context = context;
    }

    public int deleteDataNew(long id) {
        String whereArgs[] = {""+id};
        int count = db.delete(DatabaseHelper.TABLE_NAME, DatabaseHelper.KEY_ROWID + "=?", whereArgs);
        return count;
    }

}
