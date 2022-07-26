package com.bayzid.qrbarcodescanner2022.database;

import android.content.Context;

import androidx.room.Database;

import com.bayzid.qrbarcodescanner2022.Model.Code;
import com.bayzid.qrbarcodescanner2022.Model.CodeDao;
import com.bayzid.qrbarcodescanner2022.R;

@Database(entities = {Code.class},
        version = 1, exportSchema = false)
public abstract class QrCobaDatabase extends com.bayzid.qrbarcodescanner2022.database.AppDatabase {

    private static volatile QrCobaDatabase sInstance;

    // Get a database instance
    public static synchronized QrCobaDatabase on() {
        return sInstance;
    }

    public static synchronized void init(Context context) {

        if (sInstance == null) {
            synchronized (QrCobaDatabase.class) {
                sInstance = createDb(context, context.getString(R.string.app_name), QrCobaDatabase.class);
            }
        }
    }

    public abstract CodeDao codeDao();
}
