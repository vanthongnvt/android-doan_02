package com.example.tours;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.EditText;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

public class CreateStopPoint extends AppCompatActivity implements OnMapReadyCallback{

    private static final int ERROR_DIALOG_REQUEST=9001;
    private  static final String FINE_LOCATION=Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION=Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUESET_CODE=1234;

    private EditText edtSearchAddr;
    private  GoogleMap mMap;
    private  Boolean mLocationPermisstionsGranted=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_stop_point);
        int isOK=isServiceAvailable();

        if(isOK==1){
            getLocationPermission();

        }
        else if(isOK==-1){
            Intent intent = new Intent(CreateStopPoint.this,HomeActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap=googleMap;
    }

    private int isServiceAvailable(){
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(CreateStopPoint.this);
        if(available== ConnectionResult.SUCCESS){
            return  1;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            Dialog dialog=GoogleApiAvailability.getInstance().getErrorDialog(CreateStopPoint.this,available,ERROR_DIALOG_REQUEST);
            dialog.show();
            return 0;
        }
        return -1;
    }
    private void getLocationPermission(){
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION};
        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),FINE_LOCATION)==
        PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED) {
                mLocationPermisstionsGranted = true;
            }
            else{
                ActivityCompat.requestPermissions(this,permissions,LOCATION_PERMISSION_REQUESET_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mLocationPermisstionsGranted=true;
        switch (requestCode){
            case LOCATION_PERMISSION_REQUESET_CODE:{
                if(grantResults.length>0){
                    for(int i=0;i<grantResults.length;i++){
                        if(grantResults[i]!=PackageManager.PERMISSION_GRANTED){
                            mLocationPermisstionsGranted=false;
                            return;
                        }
                    }
                    mLocationPermisstionsGranted=true;
                    init();
                }
            }
        }
    }

    private void init(){
        edtSearchAddr=findViewById(R.id.edt_search_addr);
        SupportMapFragment supportMapFragment= (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(CreateStopPoint.this);
    }
}
