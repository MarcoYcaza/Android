package com.example.tdp_cell_master2.Data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface DaoAccess {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMultipleBts(List<bts_item> btsList);

    @Query("SELECT * FROM bts_item WHERE BtsId = :BtsId")

    bts_item fetchOneBtsbyId(int BtsId);

}
