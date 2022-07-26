package com.bayzid.qrbarcodescanner2022.database;

import android.content.Context;

import com.bayzid.qrbarcodescanner2022.Model.Code;
import com.bayzid.qrbarcodescanner2022.Model.CodeDao;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;

public class DatabaseUtil {
    /**
     * Fields
     */
    private static DatabaseUtil sInstance;
    private CodeDao mCodeDao;

    private DatabaseUtil() {
        setCodeDao(QrCobaDatabase.on().codeDao());
    }

    /**
     * This method builds an instance
     */
    public static void init(Context context) {
        QrCobaDatabase.init(context);

        if (sInstance == null) {
            sInstance = new DatabaseUtil();
        }
    }

    public static DatabaseUtil on() {
        if (sInstance == null) {
            sInstance = new DatabaseUtil();
        }

        return sInstance;
    }

    private CodeDao getCodeDao() {
        return mCodeDao;
    }

    private void setCodeDao(CodeDao codeDao) {
        mCodeDao = codeDao;
    }

    public Completable insertCode(Code code) {
        return getCodeDao().insert(code);
    }

    public Flowable<List<Code>> getAllCodes() {
        return getCodeDao().getAllFlowableCodes();
    }
    public int deleteEntity(Code code) {
        return getCodeDao().delete(code);
    }
}
