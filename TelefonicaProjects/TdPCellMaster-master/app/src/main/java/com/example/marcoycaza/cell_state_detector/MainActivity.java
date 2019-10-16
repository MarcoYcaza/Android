package com.example.marcoycaza.cell_state_detector;


import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;


import static com.example.marcoycaza.cell_state_detector.R.*;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,LocationListener {

    private final static long REPETITIVE_TASK_DELAY_IN_MILLIS = 10;


    //______________Variables_________________

    private RepetitiveTask cellRepetitiveTask;
    private TextView network_details;
    private String btsNameFetched;

    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private TextView mLatitude;
    private TextView mLongitude;
    private TextView mTextoResultado;

    private BTS_ViewModel bts_viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_main);

        final Button mButtonSearch = findViewById(id.buttonSearch);

        bts_viewModel = ViewModelProviders.of(this).get(BTS_ViewModel.class);

        mTextoResultado = findViewById(id.textRes);

        network_details =    findViewById(id.detailsNetTx);
        cellRepetitiveTask =    createCellRepetitiveTask();

        mLatitude  =  findViewById(id.latitud);
        mLongitude = findViewById(id.longitud);

        buildGoogleApiClient();
        createLocationRequest();

        mButtonSearch.setOnClickListener(
                this::onClickSearch);

    }

    public void WorkingCellTasks() {

        final CellParameterGetter cellMonitor = new CellParameterGetter(getApplication());
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

        final CellParameterGetter cellMonitor = new CellParameterGetter(getApplication());
        final CellRegistered celdax = cellMonitor.action_monitor();
        final Integer cellid = celdax.getCid();
        final Handler handler = new Handler();
        final String string;

        cellRepetitiveTask.start(true);
        startLocationUpdates();

        string = bts_viewModel.FindThing(cellid,handler);


        mTextoResultado.setText(string);

    }

}
