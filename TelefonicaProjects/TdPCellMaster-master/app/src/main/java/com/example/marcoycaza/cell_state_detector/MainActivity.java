package com.example.marcoycaza.cell_state_detector;

import android.Manifest;
import android.arch.persistence.room.Room;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.marcoycaza.cell_state_detector.Data.BtsDatabase;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;


import static com.example.marcoycaza.cell_state_detector.R.*;

public class MainActivity extends AppCompatActivity  implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,LocationListener {

    private final static long REPETITIVE_TASK_DELAY_IN_MILLIS = 10;
    private static final String SAVED_INSTANCE_STATE_KEY_IS_RUNNING = "isRunning";
    private static final String SAVED_INSTANCE_STATE_KEY_LAST_TEXT = "lastDataText";
    private static final String DATABASE_NAME = "bts_db";
    private BtsDatabase btsDatabase;



    //______________Variables_________________

    private boolean isPaused;
    private RepetitiveTask cellRepetitiveTask;
    private TextView network_details;
    private String btsNameFetched;


    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private TextView mLatitude;
    private TextView mLongitude;
    private TextView mTextoResultado;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_main);

        final Button mButtonSearch = findViewById(id.buttonSearch);
        final Button mButtonStop = findViewById(id.buttonStop);

        mTextoResultado = findViewById(id.textRes);


        isPaused = false;

        btsDatabase = Room.databaseBuilder(getApplicationContext(),
                BtsDatabase.class, DATABASE_NAME).fallbackToDestructiveMigration().build();

        Thread thread = new Thread();

        btsDatabase.PopulationExecution(thread,getApplicationContext());

        buildGoogleApiClient();
        createLocationRequest();

        network_details = findViewById(id.detailsNetTx);
        cellRepetitiveTask = createCellRepetitiveTask();

        mLatitude = findViewById(id.latitud);
        mLongitude = findViewById(id.longitud);


        /* Acá vamos a incluir los botones*/
        mButtonStop.setOnClickListener(//// borrar
                this::onClickStop);

        mButtonSearch.setOnClickListener(// Lo que sucede cuando buscamos
                this::onClickSearch);

    }

    /*  Methods override form Activity , can be replaced with ViewModel*/
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        final boolean isRunning = savedInstanceState.getBoolean(SAVED_INSTANCE_STATE_KEY_IS_RUNNING, false);
        if (isRunning) {
            cellRepetitiveTask.start(true);
        } else {
            final String lastTimeText = savedInstanceState.getString(SAVED_INSTANCE_STATE_KEY_LAST_TEXT, "");
            network_details.setText(lastTimeText);
        }
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(SAVED_INSTANCE_STATE_KEY_IS_RUNNING, isPaused || cellRepetitiveTask.isRunning());
        outState.putString(SAVED_INSTANCE_STATE_KEY_LAST_TEXT, network_details.getText().toString());
        super.onSaveInstanceState(outState);
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        if (isPaused) {
            cellRepetitiveTask.start(true);
            isPaused = false;
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        if (cellRepetitiveTask.isRunning()) {
            cellRepetitiveTask.stop();
            isPaused = true;
        }
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,this);
        if(mGoogleApiClient != null)
            mGoogleApiClient.disconnect();
    }


    public void WorkingCellTasks() {

        final CellParameteGetter cellMonitor = new CellParameteGetter(getApplication());
        final CellRegistered cell = cellMonitor.action_monitor();
        final TextView netText = findViewById(id.netTypeTx);

        final Integer cellId = cell.getCid();
        final Integer area_code = cell.getLac();
        final Integer pci = cell.getPci();
        final Integer psc = cell.getPsc();

        switch (cell.getType())
        {
            case "GSM":
                netText.setText(getString(string.GSM));
                final String fill_text_G = "Cell ID: " + cellId +
                      System.getProperty ("line.separator")+
                      "LAC: " + area_code ;
                network_details.setText(fill_text_G);

                break;
            case "WCDMA":
                netText.setText(getString(string.WCDMA));
                final String fill_text_W = "Cell ID: " + cellId +
                        System.getProperty ("line.separator")+
                        "LAC: " + area_code+
                        System.getProperty ("line.separator")+
                        "PSC: " + psc ;

                network_details.setText(fill_text_W);

                break;
            case "LTE":
                netText.setText(string.LTE);

                final String fill_text_L = "enodeB ID: " + cellId +
                        System.getProperty ("line.separator")+
                        "TAC: " + area_code+
                        System.getProperty ("line.separator")+
                        "PCI: " + pci ;

                network_details.setText(fill_text_L);

                break;
            case "UNKNOWN":
                network_details.setText(string.NONE);
                break;
            default:
                break;
        }

    }

    private RepetitiveTask createCellRepetitiveTask() {
        return new RepetitiveTask(this::WorkingCellTasks, REPETITIVE_TASK_DELAY_IN_MILLIS);
    }

    /*  Methods for locations*/
    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }

    }

    private void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,this);
    }

    private void displayLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            double latitude = mLastLocation.getLatitude();
            double longitude = mLastLocation.getLongitude();

            mLongitude.setText(""+longitude);

            mLatitude.setText(""+latitude);



        } else {
            mLatitude.setText("null");
            mLongitude.setText("null");

        }

    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        //___________________________Location Variabless______
        // SEC
        int UPDATE_INTERVAL = 5000;
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        //SEC
        int FASTEST_INTERVAL = 3000;
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        // METERS
        int DISPLACEMENT = 10;
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        // Fixed in this part
        mGoogleApiClient.connect();
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest,this);
    }


    /*  methods implemented for ConnectionCallbacks*/

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        displayLocation();

         startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        displayLocation();
    }


    /*  methods referenced*/

    private void onClickSearch(View view) {

        final CellParameteGetter cellMonitor = new CellParameteGetter(getApplication());
        final CellRegistered celdax = cellMonitor.action_monitor();
        final Integer cellid = celdax.getCid();
        final Handler handler = new Handler();

        cellRepetitiveTask.start(true);
        startLocationUpdates();


        new Thread(() -> {
            try {
                final String texto = btsDatabase.daoAccess().fetchOneBtsbyId(cellid)
                        .getBtsName();

                handler.post(() -> btsNameFetched = texto);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        mTextoResultado.setText(btsNameFetched);

    }

    private void onClickStop(View view) {

        if (!cellRepetitiveTask.isRunning()) {
            return;
        }

        cellRepetitiveTask.stop();
        stopLocationUpdates();

    }
}
