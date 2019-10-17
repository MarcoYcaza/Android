package com.example.tdp_cell_master2;

import android.app.Application;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.room.Room;

import com.example.tdp_cell_master2.Data.bts_database;

public class bts_view_model extends AndroidViewModel {

    private bts_database btsDatabase;
    private String btsFetched;

    public bts_view_model(@NonNull Application application) {
        super(application);

        String DATABASE_NAME = "bts_db";
        bts_database btsDatabase;


        btsDatabase = Room.databaseBuilder(getApplication(),
                bts_database.class, DATABASE_NAME).fallbackToDestructiveMigration().build();

        btsDatabase.PopulationExecution(getApplication());

        this.btsDatabase = btsDatabase;


    }


    public String FindThing(int cellid , Handler handler){

        new Thread(() -> {
            try {
                final String texto = btsDatabase.daoAccess().fetchOneBtsbyId(cellid)
                        .getBtsName();

                handler.post(() -> btsFetched = texto);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }).start();

        return btsFetched;
    }
}
