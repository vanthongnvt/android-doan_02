package com.example.tours;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PointOfInterest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class CreateStopPointActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnPoiClickListener{

    private static final int ERROR_DIALOG_REQUEST=9001;
    private  static final String FINE_LOCATION=Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION=Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUESET_CODE=1234;
    private static final float DEFAULT_ZOOM = 15f;
    private static final String TAG="SHOW_MAP";

    private EditText edtSearchAddr;
    private ImageView btnCurLocation;
    private  GoogleMap mMap;
    private  Boolean mLocationPermisstionsGranted=false;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Button btnShowDialog;
    private Dialog dialogCreateStopPoint;
    private Button btnCloseDialog;
    private EditText edtStopPointName;
    private EditText edtStopPointAddress;
    private EditText edtStopPointMaxCost;
    private EditText edtStopPointMinCost;
    private EditText edtStopPointTimeArrive;
    private EditText edtStopPointDateArrive;
    private EditText edtStopPointTimeLeave;
    private EditText edtStopPointDateLeave;
    private int tourId;
    private double mlat;
    private double mlong;
    private String mAddress;
    private MarkerOptions markerOptions=null;
    private Marker marker;
    private boolean isPOIclick=false;
//    private AutocompleteSupportFragment autocompleteFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_create_stop_point);
        int isOK=isServiceAvailable();

        if(isOK==1){
            getLocationPermission();
            init();
//            edtSearchAddr.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//                @Override
//                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                    if(actionId == EditorInfo.IME_ACTION_SEARCH||actionId==EditorInfo.IME_ACTION_DONE
//                            ||event.getAction()==KeyEvent.ACTION_DOWN|| event.getAction()==KeyEvent.KEYCODE_ENTER)
//
//                    return false;
//                }
//            });

            btnCurLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getDeviceLocation();
                }
            });

//            autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
//                @Override
//                public void onPlaceSelected(Place place) {
//                    // TODO: Get info about the selected place.
//                    Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
//                }
//
//                @Override
//                public void onError(Status status) {
//                    // TODO: Handle the error.
//                    Log.i(TAG, "An error occurred: " + status);
//                }
//            });

            btnShowDialog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogCreateStopPoint.show();
                }
            });

            btnCloseDialog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogCreateStopPoint.hide();
                }
            });

        }
        else if(isOK==-1){
            Intent intent = new Intent(CreateStopPointActivity.this,HomeActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if(mLocationPermisstionsGranted){
            getDeviceLocation();

            mMap.setOnPoiClickListener(this);

            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);

            mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                @Override
                public void onMarkerDragStart(Marker marker) {

                }

                @Override
                public void onMarkerDrag(Marker marker) {

                }

                @Override
                public void onMarkerDragEnd(Marker marker) {
                }
            });

            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    if(!isPOIclick) {
                        moveCamera(latLng, DEFAULT_ZOOM, null);
                    }
                    else{
                        isPOIclick=false;
                    }
                }
            });
        }
    }
    private void  geoLocateToDialog(LatLng latLng){

        mlat=latLng.latitude;
        mlong=latLng.longitude;

        Geocoder geocoder =new Geocoder(CreateStopPointActivity.this);
        List<Address> list;
        try {
            list=geocoder.getFromLocation(mlat,mlong,1);
            if(list.size()>0){
                Address address= list.get(0);
                mAddress=address.getAddressLine(0);

                edtStopPointAddress.setText(mAddress);

            }
            else{
                Log.d(TAG, "geoLocate: EMPTY ");
            }
        }catch (IOException e){
            Log.d(TAG, "geoLocate: Exception 1");
        }
    }
    private void getDeviceLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            if (mLocationPermisstionsGranted) {
                Task<Location> location = fusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {

                            Location curLocation = task.getResult();
                            if (curLocation != null) {
                                moveCamera(new LatLng(curLocation.getLatitude(), curLocation.getLongitude()), DEFAULT_ZOOM, getString(R.string.map_my_location));

                            } else {
                                Toast.makeText(CreateStopPointActivity.this, getString(R.string.map_failed_to_get_device_location), Toast.LENGTH_SHORT).show();

                            }
                        }
                        else{
                            //khong the lay vi tri
                            Toast.makeText(CreateStopPointActivity.this, getString(R.string.map_failed_to_get_device_location), Toast.LENGTH_SHORT).show();
                        }

                    }
                });

            }
        } catch (SecurityException e) {
            Log.d(TAG, "getDeviceLocation: Exception 2");
        }
    }

    private void moveCamera(LatLng latLng, float zoom, String title){

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom));
        if(marker==null){
            markerOptions=new MarkerOptions();
        }
        else{
            marker.remove();
        }
        if(title!=null) {
            if(!title.equals(getString(R.string.map_my_location))) {
                markerOptions.position(latLng).title(title).draggable(true);
                marker = mMap.addMarker(markerOptions);
                edtStopPointName.setText(title);
            }
        }
        else{
            markerOptions.position(latLng).title(null).draggable(true);
            marker = mMap.addMarker(markerOptions);
            marker.showInfoWindow();
        }

        geoLocateToDialog(latLng);
        hideKeyboard();
    }


    private int isServiceAvailable(){
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(CreateStopPointActivity.this);
        if(available== ConnectionResult.SUCCESS){
            return  1;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            Dialog dialog=GoogleApiAvailability.getInstance().getErrorDialog(CreateStopPointActivity.this,available,ERROR_DIALOG_REQUEST);
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
                SupportMapFragment supportMapFragment= (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                supportMapFragment.getMapAsync(CreateStopPointActivity.this);
            }
            else{
                ActivityCompat.requestPermissions(this,permissions,LOCATION_PERMISSION_REQUESET_CODE);
            }
        }
        else {
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUESET_CODE);
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
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void init(){
        edtSearchAddr=findViewById(R.id.edt_search_addr);

        btnCurLocation=findViewById(R.id.map_btn_cur_location);
        btnShowDialog=findViewById(R.id.map_btn_show_dialog);
        dialogCreateStopPoint= new Dialog(CreateStopPointActivity.this,R.style.PlacesAutocompleteThemeFullscreen);
        dialogCreateStopPoint.setContentView(R.layout.dialog_create_stop_point);
        btnCloseDialog=dialogCreateStopPoint.findViewById(R.id.map_btn_close_dialog);
        edtStopPointAddress=dialogCreateStopPoint.findViewById(R.id.create_stop_point_address);
        edtStopPointName=dialogCreateStopPoint.findViewById(R.id.create_stop_point_name);
        edtStopPointMinCost=dialogCreateStopPoint.findViewById(R.id.create_stop_point_min_cost);
        edtStopPointMaxCost=dialogCreateStopPoint.findViewById(R.id.create_stop_point_max_cost);
        edtStopPointTimeArrive=dialogCreateStopPoint.findViewById(R.id.create_stop_point_arrive_time);
        edtStopPointDateArrive=dialogCreateStopPoint.findViewById(R.id.create_stop_point_arrive_date);
        edtStopPointTimeLeave=dialogCreateStopPoint.findViewById(R.id.create_stop_point_leave_time);
        edtStopPointDateLeave=dialogCreateStopPoint.findViewById(R.id.create_stop_point_leave_date);


//        autocompleteFragment = (AutocompleteSupportFragment)
//                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
//        if (!Places.isInitialized()) {
//            Places.initialize(getApplicationContext(), getString(R.string.gg_map_API),Locale.CHINESE);
//        }
//      Specify the types of place data to return.
//        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));
    }
    private void hideKeyboard(){
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    public void onPoiClick(PointOfInterest pointOfInterest) {
        Log.d(TAG, "onPoiClick: "+pointOfInterest.name);
        moveCamera(pointOfInterest.latLng,DEFAULT_ZOOM,pointOfInterest.name);
        isPOIclick=true;
    }
}