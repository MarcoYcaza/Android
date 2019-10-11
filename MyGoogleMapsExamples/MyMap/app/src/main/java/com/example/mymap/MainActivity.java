package com.example.mymap;

import android.content.Context;
import android.location.Criteria;
import android.location.LocationManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;

import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;



public class MainActivity extends AppCompatActivity
        implements GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback {


    public static final int LOCATION_PERMISSION_REQUEST_CODE=1;
    private GoogleMap mMap;
    private boolean mPermissionDenied = false;
    private Button mButton;


    private LocationManager locationManager;
    private String provider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_main);

        mButton =  (Button) findViewById(R.id.button);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync( this);

        // Get the location manager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Define the criteria how to select the locatioin provider -> use
        // default
        Criteria criteria = new Criteria();

        provider = locationManager.getBestProvider(criteria, false);




        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            //Permission is missing
            PermissionUtils.requestPermission(this,LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION,true);
        }else{
            //dfgdfgdf
           final Location location = locationManager.getLastKnownLocation(provider);


        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(-12.46,-77.9094))
                        .title("Facebook")
                        .snippet("Facebook HQ: Menlo Park"));

                /******/


                Toast.makeText(MainActivity.this, location.getLatitude()+"", Toast.LENGTH_SHORT).show();
            }
        });

        }

    }



    /*      Métodos que se han añadido            */
    @Override
    public boolean onMyLocationButtonClick() {

        Toast.makeText(this, "My location button clicked", Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Your location is "+ location.getLatitude()
                +" "+location.getLongitude(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);



        enableMyLocation();
    }

    private void enableMyLocation() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        != PackageManager.PERMISSION_GRANTED){
            //Permission is missing
        PermissionUtils.requestPermission(this,LOCATION_PERMISSION_REQUEST_CODE,
                Manifest.permission.ACCESS_FINE_LOCATION,true);

        }else if(mMap != null){
            //Access to location has been granted to the app
            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode!=LOCATION_PERMISSION_REQUEST_CODE){
            return;
        }

        if(PermissionUtils.isPermissionGranted(permissions,
                grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)){
            enableMyLocation();
        }else {
            mPermissionDenied = true;
        }


    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();

        if(mPermissionDenied){
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }

    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog.newInstance(true);
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(),"dialog");
    }


}
