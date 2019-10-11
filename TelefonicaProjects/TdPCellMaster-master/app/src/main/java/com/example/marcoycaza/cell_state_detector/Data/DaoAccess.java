package com.example.marcoycaza.cell_state_detector.Data;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface DaoAccess {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOnlySingleBts(Bts bts);

    @Insert(onConflict = OnConflictStrategy.REPLACE)

    void insertMultipleBts(List<Bts> btsList);

    @Query("SELECT * FROM Bts WHERE BtsId = :BtsId")
    Bts fetchOneBtsbyId(int BtsId);

    @Query("DELETE FROM bts")
    void deleteAll();

    @Update
    void updateBts(Bts bts);

    @Delete
    void deleteBts(Bts bts);
}