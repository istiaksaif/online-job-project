package com.bayzid.qrbarcodescanner2022.Model;

import androidx.room.Dao;
import androidx.room.Query;

import com.bayzid.qrbarcodescanner2022.constant.TableNames;
import com.bayzid.qrbarcodescanner2022.database.BaseDao;

import java.util.List;

import io.reactivex.Flowable;

@Dao

public interface CodeDao extends BaseDao<Code> {
    @Query("SELECT * FROM " + TableNames.CODES)
    Flowable<List<Code>> getAllFlowableCodes();
}
