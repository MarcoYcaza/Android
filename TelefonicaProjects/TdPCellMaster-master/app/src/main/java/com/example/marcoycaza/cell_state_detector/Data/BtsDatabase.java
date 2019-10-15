package com.example.marcoycaza.cell_state_detector.Data;

import android.app.Application;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;

import com.example.marcoycaza.cell_state_detector.R;

import java.util.ArrayList;
import java.util.List;

@Database(entities = {Bts.class}, version = 1, exportSchema = false)
public abstract class BtsDatabase extends RoomDatabase {
    public abstract DaoAccess daoAccess() ;

        public void PopulationExecution(final Context context) {

            final ArrayList<Bts> btsList = new ArrayList<>();
            final Thread thread = new Thread(new Runnable() {
            final Resources res = context.getResources();

            int i=0;
            String cId;
            String Description;
            String[] sites = res.getStringArray(R.array.ListaSites);

            @Override
            public void run() {

                for (String site : sites) {

                    String[] data = site.split(";");

                    cId = data[0];

                    Description = data[1];

                    btsList.add(i, new Bts(String.valueOf(cId), Description));

                    i=i++;
                }

                daoAccess().insertMultipleBts(btsList);

            }
        });

        thread.start();
    }

}

