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
            String coordenadas;
            String departamento;
            String direccion;
            String controlador;

            String[] sites = res.getStringArray(R.array.ListaSites);


                for (String site : sites){

                    String[] data = site.split(";");

                    btsName = data[0];
                    unique_code = data[1];
                    coordenadas = data[2];
                    departamento = data[3];
                    direccion = data[4];
                    controlador = data[5];

                    mbtsList.add(new btsItem(btsName,unique_code,coordenadas,departamento,direccion,controlador));

                    i=i+1;
                }



    }


    @Override
    protected void onCleared() {
        super.onCleared();
    }
}
