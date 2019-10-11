package com.example.marcoycaza.cell_state_detector;

import android.Manifest;
import android.annotation.SuppressLint;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.marcoycaza.cell_state_detector.Data.BtsDatabase;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;


import java.util.ArrayList;

public class MainActivity extends AppCompatActivity  implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,LocationListener {

    private final static long REPETITIVE_TASK_DELAY_IN_MILLIS = 10;
    private static final String SAVED_INSTANCE_STATE_KEY_IS_RUNNING = "isRunning";
    private static final String SAVED_INSTANCE_STATE_KEY_LAST_TEXT = "lastDataText";

    private static final String DATABASE_NAME = "bts_db";
    private BtsDatabase btsDatabase;
    private boolean workingFlag;


    Resources res;

    //___________________________Variables_______________________________________
    private Menu menu;
    private boolean isPaused;
    private RepetitiveTask cellRepetitiveTask;
    private TextView detailsNetTx;
    private RelativeLayout relativeLayout;

    private String btsNameFinded;
    private LineChart mChart;

    private View view;
    //___________________________Location Variabless_______________________________________
    private static int UPDATE_INTERVAL = 5000; // SEC
    private static int FASTEST_INTERVAL = 3000;  //SEC
    private static int DISPLACEMENT = 10; // METERS


    private static final int MY_PERMISSION_REQUEST_CODE= 7171;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 7172;


    private boolean mRequestingLocationUpdates = false;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private TextView latitud;
    private TextView longitud;
    //___________________________Location Variables_______________________________________

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_CODE:
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if(checkPlayServices()) buildGoogleApiClient();
                }
                break;
        }
    }
    //___________________________On Create Method____________________________________________
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        isPaused = false;


        //__________________Texto de Room y esa vaina_________ Borrar
        btsDatabase = Room.databaseBuilder(getApplicationContext(),
                BtsDatabase.class, DATABASE_NAME).fallbackToDestructiveMigration().build();

        Thread thread = new Thread();
        Handler handler = new Handler();


        btsDatabase.PopulationExecution(thread,getApplicationContext());


      ///////////////////

        //__________________Texto temporal_________ Borrar
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Run-Time request permission
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            }, MY_PERMISSION_REQUEST_CODE);
        } else {
            if (checkPlayServices()) {

                buildGoogleApiClient();
                createLocationRequest();
            }
        }//__________________Texto temporal_________ Borrar


        detailsNetTx = findViewById(R.id.detailsNetTx);
        cellRepetitiveTask = createCellRepetitiveTask();

        latitud = findViewById(R.id.latitud);
        longitud = findViewById(R.id.longitud);



    }

    //___________________________This to make data Persistent_______________________________________
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        final boolean isRunning = savedInstanceState.getBoolean(SAVED_INSTANCE_STATE_KEY_IS_RUNNING, false);
        if (isRunning) {
            cellRepetitiveTask.start(true);
        } else {
            final String lastTimeText = savedInstanceState.getString(SAVED_INSTANCE_STATE_KEY_LAST_TEXT, "");
            detailsNetTx.setText(lastTimeText);
        }
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(SAVED_INSTANCE_STATE_KEY_IS_RUNNING, isPaused || cellRepetitiveTask.isRunning());
        outState.putString(SAVED_INSTANCE_STATE_KEY_LAST_TEXT, detailsNetTx.getText().toString());
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
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,this); //borrar
        if(mGoogleApiClient != null)//borrar
            mGoogleApiClient.disconnect();//borrar
    }

    //___________________________Network Cell Affairs and other Methods_____________________________
    public void WorkingCellTasks() {
        String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
        Permissions.check(getApplicationContext(), permissions, "This permissions are required",
                null/*options*/, new PermissionHandler() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onGranted() {

                            final CellParameteGetter cellMonitor = new CellParameteGetter(getApplication());
                            final CellRegistered celda = cellMonitor.action_monitor();
                            final TextView netText = findViewById(R.id.netTypeTx);

                            final Integer cellid = celda.getCid();
                            final Integer area_code = celda.getLac();
                            final Integer pci = celda.getPci();
                            final Integer psc = celda.getPsc();



                            switch (celda.getType()) {
                                    case "GSM":
                                        netText.setText("GSM");
                                        final String filltextG = "Cell ID: " + cellid +
                                              System.getProperty ("line.separator")+
                                              "LAC: " + area_code ;
                                        detailsNetTx.setText(filltextG);



                                        break;
                                    case "WCDMA":
                                        netText.setText("WCDMA");
                                        final String filltextW = "Cell ID: " + cellid +
                                                System.getProperty ("line.separator")+
                                                "LAC: " + area_code+
                                                System.getProperty ("line.separator")+
                                                "PSC: " + psc ;

                                        detailsNetTx.setText(filltextW);




                                        break;
                                    case "LTE":
                                        netText.setText("LTE");

                                        final String filltextL = "enodeB ID: " + cellid +
                                                System.getProperty ("line.separator")+
                                                "TAC: " + area_code+
                                                System.getProperty ("line.separator")+
                                                "PCI: " + pci ;

                                        detailsNetTx.setText(filltextL);


                                        break;
                                    case "UNKNOWN":
                                        detailsNetTx.setText("null");
                                        break;
                                    default:
                                        break;
                                }



                        //___________________________________________Some beautiful info__________________________
                    }
                    @Override
                    public void onDenied(Context context, ArrayList<String> deniedPermissions) {
                        super.onDenied(context, deniedPermissions);
                        WorkingCellTasks();
                    }
                });

    }


    private RepetitiveTask createCellRepetitiveTask() {
        return new RepetitiveTask(new Runnable() {
            @Override
            public void run() {
                WorkingCellTasks();
            }
        }, REPETITIVE_TASK_DELAY_IN_MILLIS);
    }

    //___________________________About Menu On Click________________________________________________
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_button, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
                case R.id.action_switch_timer:
                    toggleCellTask(menu);
                    return true;
                case R.id.search:
                    if (!workingFlag) {
                        Toast.makeText(this, "Presionar Stop", Toast.LENGTH_SHORT).show();
                    } else {
                        final CellParameteGetter cellMonitor = new CellParameteGetter(getApplication());
                        final CellRegistered celdax = cellMonitor.action_monitor();
                        final Integer cellid = celdax.getCid();
                        final Handler handler = new Handler();


                        final Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    final String texto = btsDatabase.daoAccess().fetchOneBtsbyId(cellid)
                                            .getBtsName();

                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {

                                            btsNameFinded = texto;

                                        }
                                    });
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                        });

                        thread.start();


                        AlertDialog.Builder dialog = new AlertDialog.Builder(this)
                                .setTitle("Información detectada:")
                                .setMessage("La torre se llama: " + btsNameFinded )
                                .setPositiveButton("Cerrar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Log.d("MainActivity", "Sending atomic bombs to Jupiter");
                                    }
                                });

                        if (btsNameFinded == null) {

                            Toast.makeText(this, "buscando...", Toast.LENGTH_SHORT).show();
                        } else {
                            dialog.show();
                        }

                    }


            return true;
            default:
            return false;
        }
    }





    private void toggleCellTask(Menu menu) {
        this.menu = menu;
        if (cellRepetitiveTask.isRunning()) {
            menu.getItem(0).setIcon(getDrawable(R.drawable.play_action));
            stopLocationUpdates(); //// borrar

            workingFlag = true;
            cellRepetitiveTask.stop();
        } else {
            menu.getItem(0).setIcon(getDrawable(R.drawable.stop_action));
            startLocationUpdates();//// borrar
            workingFlag = false;

            cellRepetitiveTask.start(true);
        }
    }

    /*
    Methods for locations
     */
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

            longitud.setText(""+longitude);

            latitud.setText(""+latitude);



        } else {
            latitud.setText("null");
            longitud.setText("null");

        }

    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
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

    private boolean checkPlayServices(){
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(),"This device is not supported",Toast.LENGTH_SHORT).show();
                finish();
            }
            return false;
        }

        return true;
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest,this);
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        displayLocation();
        if(mRequestingLocationUpdates)
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


}
