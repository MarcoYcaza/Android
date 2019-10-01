package com.example.my_library;

import android.content.Context;
import android.content.res.Resources;

import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

public class btsViewModel extends ViewModel {

    private ArrayList<btsItem> mbtsList;

    /*   Definimos la función que crea la lista    */

    public ArrayList<btsItem> getmbtsList(final Context context){
        if(mbtsList == null){

            mbtsList = new ArrayList<>();

            createBTSList(context);
        }
        return mbtsList;
    }



    /*   Definición de la función createBTSList*/
    public void createBTSList(final Context context){

            /*Poblamos la data con un archivo String*/

            final Resources res = context.getResources();

            int i=0;
            String btsName;
            String unique_code;
            String Description;

            String[] sites = res.getStringArray(R.array.ListaSites);


                for (String site : sites){

                    String[] data = site.split(";");

                    btsName = data[0];

                    unique_code = data[1];

                    Description = data[2];

                    mbtsList.add(new btsItem(btsName,unique_code,Description));

                    i=i+1;
                }



    }


    @Override
    protected void onCleared() {
        super.onCleared();
    }
}
