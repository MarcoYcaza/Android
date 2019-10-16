package com.example.marcoycaza.cell_state_detector;

import android.app.Application;
import android.arch.persistence.room.Room;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;


import com.example.marcoycaza.cell_state_detector.Data.BtsDatabase;

public class BTS_ViewModel extends AndroidViewModel {

    private BtsDatabase btsDatabase;
    private String btsFetched;

    public BTS_ViewModel(@NonNull Application application) {
        super(application);

         String DATABASE_NAME = "bts_db";
         BtsDatabase btsDatabase;


        btsDatabase = Room.databaseBuilder(getApplication(),
                BtsDatabase .class, DATABASE_NAME).fallbackToDestructiveMigration().build();

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
