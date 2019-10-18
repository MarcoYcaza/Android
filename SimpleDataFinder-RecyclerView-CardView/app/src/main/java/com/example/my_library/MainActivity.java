package com.example.my_library;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;

    private ArrayList<btsItem> mbtsList ;

    private projectAdapter mAdapter;
    private btsViewModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Nuestro VM
        model = ViewModelProviders.of(this).get(btsViewModel.class);


        mbtsList =  model.getmbtsList(getApplicationContext());

        mAdapter = new projectAdapter(mbtsList);

        mAdapter.setCopyOfList(mbtsList);

        buildRecyclerView(mAdapter);


    }


    public void buildRecyclerView(final projectAdapter mAdapter){

        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);


        mRecyclerView.setLayoutManager(mLayoutManager);


        mRecyclerView.setAdapter(mAdapter);


        mAdapter.setOnItemClickListener(new projectAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                /* Implementación del OnItemClic  */


                String t1 = mbtsList.get(position).getBtsName();
                String t2 = mbtsList.get(position).getUniqueCode();
                String t3 = mbtsList.get(position).getCoordenadas();
                String t4 = mbtsList.get(position).getDepartamento();
                String t5 = mbtsList.get(position).getDireccion();
                String t6 = mbtsList.get(position).getControlador();

                String infopack = t1+"\n"+t2+"\n"+t3+"\n"+t4+"\n"+t5+"\n"+t6;


                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Se compartirá:");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, infopack);
                startActivity(Intent.createChooser(sharingIntent, "Escoge la app para compartir!"));
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(final String newText) {

                mAdapter.getFilter().filter(newText);

                return false;
            }

        });

        return true;
    }

}
