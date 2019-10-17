package com.example.tdp_cell_master2.Data;

import android.content.Context;
import android.content.res.Resources;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.tdp_cell_master2.R;

import java.util.ArrayList;

@Database(entities = {bts_item.class}, version = 1, exportSchema = false)
public abstract class bts_database extends RoomDatabase {

    public abstract DaoAccess daoAccess() ;


    public void PopulationExecution(final Context context) {

        final ArrayList<bts_item> btsList = new ArrayList<>();
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

                    btsList.add(i, new bts_item(String.valueOf(cId), Description));

                    i=i++;
                }

                daoAccess().insertMultipleBts(btsList);
            }

        });

        thread.start();

    }


}
