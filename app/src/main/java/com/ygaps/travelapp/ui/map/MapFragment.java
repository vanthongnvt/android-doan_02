package com.ygaps.travelapp.ui.map;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.ygaps.travelapp.Adapter.ListMapDestinationAdapter;
import com.ygaps.travelapp.ApiService.APIRetrofitCreator;
import com.ygaps.travelapp.ApiService.APITour;
import com.ygaps.travelapp.AppHelper.TokenStorage;
import com.ygaps.travelapp.CreateStopPointActivity;
import com.ygaps.travelapp.HomeActivity;
import com.ygaps.travelapp.Model.StopPoint;
import com.ygaps.travelapp.Model.TourInfo;
import com.ygaps.travelapp.R;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapFragment extends Fragment implements OnMapReadyCallback {
    private MapViewModel mapViewModel;

    private static final int ERROR_DIALOG_REQUEST = 9001;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUESET_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;
    private static final String TAG = "SHOW_MAP";

    private boolean mLocationPermisstionsGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private MarkerOptions markerOptions = null;
    private Marker marker;
    List<Marker> markers = new ArrayList<>();

    private Context context;
    private View mRoot;
    private ImageView btnCurLocation;
    private ImageView btnShowListDestination;

    private Dialog dialogListDestination;
    private ListMapDestinationAdapter mapDestinationAdapter;
    private ListView listViewDestination;

    private BottomSheetDialog dialogStopPointInfo;
    private TextView nameStopPoint;
    private TextView typeStopPoint;
    private TextView addressStopPoint;
    private TextView provinceCityStopPoint;
    private TextView minCostStopPoint;
    private TextView maxCostStopPoint;
    private TextView leaveStopPoint;
    private TextView arriveStopPoint;
    String ServiceArr[]={"Restaurant", "Hotel", "Rest Station", "Other"};

    private Integer tourId = 227;
    private TourInfo tourInfo;
    private APITour apiTour;
    private StopPoint targetStopPoint = null;

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
        if (getArguments()==null || getArguments().getInt("directionTourId",-1) != -1) {
//            tourId = getArguments().getInt("directionTourId");
            int isOK = isServiceAvailable();

            if (isOK == 1) {
                getLocationPermission();
//                Intent serviceIntent = new Intent(context, BackgroundLocationService.class);
//                serviceIntent.putExtra("tourId", tourId);
//                context.startService(serviceIntent);
                init();

//                FirebaseMessaging.getInstance().subscribeToTopic("/topics/tour-id-"+tourId);


            }
        } else {
            showDialogSelectTour();
        }
    }

    private void showDialogSelectTour() {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle("Không có chuyến đi nào");
        alert.setMessage("Vui lòng chọn chuyến đi để theo dõi");
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

//                UserTripFragment nextFrag= new UserTripFragment();
//                getActivity().getSupportFragmentManager().beginTransaction()
//                        .replace(R.id.nav_host_fragment, nextFrag)
//                        .addToBackStack(null)
//                        .commit();
                ((HomeActivity) getActivity()).getNavigation().setSelectedItemId(R.id.navigation_history);
            }
        });

        alert.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alert.show();
    }

    private void init() {
        btnCurLocation = mRoot.findViewById(R.id.map_btn_cur_location);
        btnShowListDestination = mRoot.findViewById(R.id.map_btn_direction);
        dialogListDestination = new Dialog(getContext(), R.style.DialogSlideAnimation);
        dialogListDestination.setContentView(R.layout.dialog_list_destination_map);
        initDialogListDestination();

        dialogStopPointInfo =  new BottomSheetDialog(getContext());
        dialogStopPointInfo.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogStopPointInfo.setContentView(R.layout.dialog_stop_point_info);
        initDialogStopPointInfo();

        apiTour = new APIRetrofitCreator().getAPIService();

        btnCurLocation.setOnClickListener(v -> getDeviceLocation());

        apiTour.getTourInfo(TokenStorage.getInstance().getAccessToken(), tourId).enqueue(new Callback<TourInfo>() {
            @Override
            public void onResponse(Call<TourInfo> call, Response<TourInfo> response) {
                if (response.isSuccessful()) {
                    tourInfo = response.body();
                    if (tourInfo.getStopPoints().size() > 0) {
                        targetStopPoint = tourInfo.getStopPoints().get(0);
                        for (StopPoint stopPoint : tourInfo.getStopPoints()) {
                            addStopPointMarker(stopPoint);
                        }
                    }
                    mapDestinationAdapter = new ListMapDestinationAdapter(getContext(), R.layout.list_view_destination_item, tourInfo.getStopPoints(), MapFragment.this);
                    listViewDestination.setAdapter(mapDestinationAdapter);
                } else {
                    Toast.makeText(context, R.string.server_err, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TourInfo> call, Throwable t) {
                Toast.makeText(context, R.string.failed_fetch_api, Toast.LENGTH_SHORT).show();
            }
        });

        btnShowListDestination.setOnClickListener(v -> {
            dialogListDestination.show();
        });

    }

    private void initDialogListDestination() {
        listViewDestination = dialogListDestination.findViewById(R.id.list_view_destination);

    }

    private void initDialogStopPointInfo(){
        nameStopPoint = dialogStopPointInfo.findViewById(R.id.stop_point_info_name);
        typeStopPoint = dialogStopPointInfo.findViewById(R.id.stop_point_info_type);
        addressStopPoint = dialogStopPointInfo.findViewById(R.id.stop_point_info_address);
        provinceCityStopPoint = dialogStopPointInfo.findViewById(R.id.stop_point_info_provice_city);
        minCostStopPoint = dialogStopPointInfo.findViewById(R.id.stop_point_info_min_cost);
        maxCostStopPoint = dialogStopPointInfo.findViewById(R.id.stop_point_info_max_cost);
        leaveStopPoint = dialogStopPointInfo.findViewById(R.id.stop_point_info_leave);
        arriveStopPoint = dialogStopPointInfo.findViewById(R.id.stop_point_info_arrive);
    }


    public void showStopPointInfo(StopPoint stopPoint) {

        nameStopPoint.setText(stopPoint.getName());
        int numServiceID = stopPoint.getServiceTypeId();
        typeStopPoint.setText(ServiceArr[numServiceID - 1]);
        addressStopPoint.setText(stopPoint.getAddress());
        int numMinCost = stopPoint.getMinCost();
        int numMaxCost = stopPoint.getMaxCost();
        minCostStopPoint.setText(Integer.toString(numMinCost));
        maxCostStopPoint.setText(Integer.toString(numMaxCost));
        long numLeave = stopPoint.getLeaveAt();
        long numArrive = stopPoint.getArrivalAt();
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(numLeave);
        Date date = cal.getTime();
        DateFormat dateFormat = new SimpleDateFormat("HH:mm dd/MM/yyyy");
        leaveStopPoint.setText(dateFormat.format(date));
        cal.setTimeInMillis(numArrive);
        date = cal.getTime();
        arriveStopPoint.setText(dateFormat.format(date));

        dialogStopPointInfo.show();

        //show dialog at bottom
        Window window = dialogStopPointInfo.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.BOTTOM;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);
    }

    public void drawRouteToStopPoint(StopPoint stopPoint){

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (mLocationPermisstionsGranted) {
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
                        } else {
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

    private void moveCamera(LatLng latLng, float zoom, String title) {

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        if (marker == null) {
            markerOptions = new MarkerOptions();
        } else {
            marker.remove();
        }
        if (title != null) {
            if (!title.equals(getString(R.string.map_my_location))) {
                markerOptions.position(latLng).title(title);
                marker = mMap.addMarker(markerOptions);
            }
        } else {
            markerOptions.position(latLng).title(null);
            marker = mMap.addMarker(markerOptions);

        }
    }

    private int isServiceAvailable() {
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getContext());
        if (available == ConnectionResult.SUCCESS) {
            return 1;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(getActivity(), available, ERROR_DIALOG_REQUEST);
            dialog.show();
            return 0;
        }
        return -1;
    }

    private void getLocationPermission() {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};
        if (ContextCompat.checkSelfPermission(context.getApplicationContext(), FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(context.getApplicationContext(), COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermisstionsGranted = true;
                if (getActivity() != null) {
                    SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_follow_tour);
                    if (supportMapFragment != null) {
                        supportMapFragment.getMapAsync(this);
                    } else {
                        Log.d(TAG, "getLocationPermission: 2");
                    }
                }
            } else {
                ActivityCompat.requestPermissions((HomeActivity) context, permissions, LOCATION_PERMISSION_REQUESET_CODE);
            }
        } else {
            ActivityCompat.requestPermissions((HomeActivity) context, permissions, LOCATION_PERMISSION_REQUESET_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mLocationPermisstionsGranted = true;
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUESET_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermisstionsGranted = false;
                            return;
                        }
                    }
                    mLocationPermisstionsGranted = true;
                    SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_follow_tour);
                    supportMapFragment.getMapAsync(this);
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void addStopPointMarker(StopPoint stopPoint) {
        if (marker != null) {
            marker.remove();
        }
        MarkerOptions stopPointMarkerOptions = new MarkerOptions();
        stopPointMarkerOptions.position(new LatLng(stopPoint.getLatitude(), stopPoint.getLongitude()))
                .icon(bitmapDescriptorFromVector(getContext(), R.drawable.ic_pin))
                .title(stopPoint.getName());
        Marker stopPointMarker = mMap.addMarker(stopPointMarkerOptions);
        stopPointMarker.setTag(stopPoint);
        markers.add(stopPointMarker);
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
