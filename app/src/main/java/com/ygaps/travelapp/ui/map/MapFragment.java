package com.ygaps.travelapp.ui.map;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.ygaps.travelapp.HomeActivity;
import com.ygaps.travelapp.R;
import com.ygaps.travelapp.Service.BackgroundLocationService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class MapFragment extends Fragment implements OnMapReadyCallback {
    private MapViewModel mapViewModel;

    private static final int ERROR_DIALOG_REQUEST=9001;
    private  static final String FINE_LOCATION=Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION=Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUESET_CODE=1234;
    private static final float DEFAULT_ZOOM = 15f;
    private static final String TAG="SHOW_MAP";

    private boolean mLocationPermisstionsGranted=false;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private MarkerOptions markerOptions=null;
    private Marker marker;
    List<Marker> markers = new ArrayList<>();

    private Context context;
    private View mRoot;
    private ImageView btnCurLocation;

    private Integer tourId;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_map, container, false);
        context = root.getContext();
//        mapViewModel =
//                ViewModelProviders.of(this).get(MapViewModel.class);
//        final TextView textView = root.findViewById(R.id.text_map);
//        mapViewModel.getText().observe(this, new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });
        mRoot = root;

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getArguments() != null) {
            tourId = getArguments().getInt("tourId");
            int isOK=isServiceAvailable();

            if(isOK==1) {
                getLocationPermission();
                Intent serviceIntent = new Intent(context, BackgroundLocationService.class);
                serviceIntent.putExtra("tourId", tourId);
                context.startService(serviceIntent);
                init();
            }
        }
        else {
            showDialogSelectTour();
        }
    }

    private void showDialogSelectTour() {

    }

    private void init() {
        btnCurLocation= mRoot.findViewById(R.id.map_btn_cur_location);

        btnCurLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDeviceLocation();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if(mLocationPermisstionsGranted){
            getDeviceLocation();

            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);

            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    moveCamera(latLng, DEFAULT_ZOOM, null);
                }
            });

            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {

                    return false;
                }
            });
        }
    }
    private void getDeviceLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
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
                                Toast.makeText(context, R.string.map_failed_to_get_device_location, Toast.LENGTH_SHORT).show();

                            }
                        }
                        else{
                            //khong the lay vi tri
                            Toast.makeText(context, R.string.map_failed_to_get_device_location, Toast.LENGTH_SHORT).show();
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
                markerOptions.position(latLng).title(title);
                marker = mMap.addMarker(markerOptions);
            }
        }
        else{
            markerOptions.position(latLng).title(null);
            marker = mMap.addMarker(markerOptions);

        }
    }

    private int isServiceAvailable(){
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getContext());
        if(available== ConnectionResult.SUCCESS){
            return  1;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            Dialog dialog=GoogleApiAvailability.getInstance().getErrorDialog(getActivity(),available,ERROR_DIALOG_REQUEST);
            dialog.show();
            return 0;
        }
        return -1;
    }
    private void getLocationPermission(){
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};
        if(ContextCompat.checkSelfPermission(context.getApplicationContext(),FINE_LOCATION)==
                PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(context.getApplicationContext(),COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED) {
                mLocationPermisstionsGranted = true;
                if(getActivity()!=null) {
                    SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_follow_tour);
                    if (supportMapFragment != null) {
                        supportMapFragment.getMapAsync(this);
                    }
                    else{
                        Log.d(TAG, "getLocationPermission: 2");
                    }
                }
            }
            else{
                ActivityCompat.requestPermissions((HomeActivity)context,permissions,LOCATION_PERMISSION_REQUESET_CODE);
            }
        }
        else {
            ActivityCompat.requestPermissions((HomeActivity)context, permissions, LOCATION_PERMISSION_REQUESET_CODE);
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
                    SupportMapFragment supportMapFragment= (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_follow_tour);
                    supportMapFragment.getMapAsync(this);
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
//        Bitmap.createScaledBitmap(bitmap, 20, 20, false);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}
