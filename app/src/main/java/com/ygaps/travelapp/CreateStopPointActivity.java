package com.ygaps.travelapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.mancj.materialsearchbar.adapter.SuggestionsAdapter;
import com.ygaps.travelapp.Adapter.ListStopPointTemporaryAdapter;
import com.ygaps.travelapp.Adapter.ListSuggestionStopPointAdapter;
import com.ygaps.travelapp.ApiService.APIRetrofitCreator;
import com.ygaps.travelapp.ApiService.APITour;
import com.ygaps.travelapp.AppHelper.DialogProgressBar;
import com.ygaps.travelapp.AppHelper.TokenStorage;
import com.ygaps.travelapp.Model.Coordinate;
import com.ygaps.travelapp.Model.ListSuggestedStopPoint;
import com.ygaps.travelapp.Model.MessageResponse;
import com.ygaps.travelapp.Model.RequestCoordinateList;
import com.ygaps.travelapp.Model.StopPoint;
import com.ygaps.travelapp.Model.TourInfo;
import com.ygaps.travelapp.Model.UpdateStopPointsOfTour;
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
import com.google.android.gms.maps.model.PointOfInterest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.ygaps.travelapp.ui.main.ServiceReviewActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateStopPointActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnPoiClickListener {

    private static final int ERROR_DIALOG_REQUEST = 9001;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUESET_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;
    private static final String TAG = "SHOW_MAP";

    private boolean canNotEdit = false;
//    private EditText edtSearchAddr;
    private ImageView btnCurLocation;
    private GoogleMap mMap;
    private Boolean mLocationPermisstionsGranted = false;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private ImageView btnShowDialogStopPointInfo;
    private Dialog dialogCreateStopPoint;
    private Dialog dialogListStopPoint;
    private ImageView btnCloseDialogStopPointInfo;
    private EditText edtStopPointName;
    private EditText edtStopPointAddress;
    private EditText edtStopPointMaxCost;
    private EditText edtStopPointMinCost;
    private EditText edtStopPointTimeArrive;
    private EditText edtStopPointDateArrive;
    private EditText edtStopPointTimeLeave;
    private EditText edtStopPointDateLeave;
    private Button btnCreateStopPoint;
    private Spinner spnService;
    private Spinner spnProvince;
    private TextView btnCancelEditAction;

    private View viewUnderSearchBar;
    private ImageView btnShowSuggestDestination;
    private ImageView btnShowDialogListStopPoint;
    private Button btnCloseDialogListStopPoint;
    private Button btnCompleteUpdateStopPoint;

    private int tourId = 227;
    private int editStopPointPosition = -1;
    private Double mlat;
    private Double mlong;
    private Integer mServiceId;
    private Integer mServiceTypeId;
    private Integer mProvinceId;
    private String mAddress;
    private MarkerOptions markerOptions = null;
    private Marker marker;
    private boolean isPOIclick = false;
    private APITour apiTour;
    private Integer existedBeforeSize = 0;
    private List<StopPoint> currentList = new ArrayList<>();
    private List<StopPoint> addList = new ArrayList<>();
    private List<Integer> deleteList = new ArrayList<>();
    private List<Integer> positionMarkUpdateList = new ArrayList<>();
    private ListView listViewStopPoint;
    private ListStopPointTemporaryAdapter listStopPointTemporaryAdapter;
    List<Marker> markers = new ArrayList<>();
    String ServiceArr[] = {"Nhà Hàng", "Khách Sạn", "Trạm nghỉ", "Khác"};
    String ProvinceArr[] = {"Hồ Chí Minh", "Hà Nội", "Đà Nẵng", "Bình Dương", "Đồng Nai", "Khánh Hòa", "Hải Phòng", "Long An", "Quảng Nam", "Bà Rịa Vũng Tàu", "Đắk Lắk", "Cần Thơ", "Bình Thuận  ", "Lâm Đồng", "Thừa Thiên Huế", "Kiên Giang", "Bắc Ninh", "Quảng Ninh", "Thanh Hóa", "Nghệ An", "Hải Dương", "Gia Lai", "Bình Phước", "Hưng Yên", "Bình Định", "Tiền Giang", "Thái Bình", "Bắc Giang", "Hòa Bình", "An Giang", "Vĩnh Phúc", "Tây Ninh", "Thái Nguyên", "Lào Cai", "Nam Định", "Quảng Ngãi", "Bến Tre", "Đắk Nông", "Cà Mau", "Vĩnh Long", "Ninh Bình", "Phú Thọ", "Ninh Thuận", "Phú Yên", "Hà Nam", "Hà Tĩnh", "Đồng Tháp", "Sóc Trăng", "Kon Tum", "Quảng Bình", "Quảng Trị", "Trà Vinh", "Hậu Giang", "Sơn La", "Bạc Liêu", "Yên Bái", "Tuyên Quang", "Điện Biên", "Lai Châu", "Lạng Sơn", "Hà Giang", "Bắc Kạn", "Cao Bằng"};
    private StopPoint selectedSuggestedStopPoint;
    private MaterialSearchBar searchBar;
//    private List<StopPoint> searchSuggestStopPoint;
    private ListSuggestionStopPointAdapter suggestionStopPointAdapter;

//    private AutocompleteSupportFragment autocompleteFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true); // to fix xml drawable error
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_create_stop_point);

        int isOK = isServiceAvailable();

        if (isOK == 1) {
            getLocationPermission();
            init();

            searchBar.addTextChangeListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String searchKey = searchBar.getText().trim();
                    if(!searchKey.isEmpty()) {
                        searchStopPoint(searchKey);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            btnCurLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getDeviceLocation();
                }
            });

            btnShowDialogStopPointInfo.setClickable(true);
            btnShowDialogStopPointInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogCreateStopPoint.show();
                    mServiceId =null;
                }
            });

            btnCloseDialogStopPointInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogCreateStopPoint.hide();
                }
            });

            btnCreateStopPoint.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    StopPoint newStopPoint = checkValidInput();
                    if (newStopPoint != null) {
                        if (editStopPointPosition >= 0) {
                            positionMarkUpdateList.set(editStopPointPosition,1);

                            Integer id = currentList.get(editStopPointPosition).getId();
                            if(id!=null){
                                //edit existed stop point
                                newStopPoint.setId(id);
                            }
                            currentList.set(editStopPointPosition, newStopPoint);
                            editStopPointPosition = -1;
                        } else {
                            Log.d(TAG, "onClick: "+mServiceId);
                            if(mServiceId!=null){
                                newStopPoint.setServiceId(mServiceId);
                            }
                            currentList.add(newStopPoint);
                            positionMarkUpdateList.add(1);
                        }
                        listStopPointTemporaryAdapter.notifyDataSetChanged();
                        dialogListStopPoint.show();
                        dialogCreateStopPoint.hide();
                        addStopPointMarker(newStopPoint,false);
                    }

                }
            });
            btnCancelEditAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogCreateStopPoint.hide();
                    btnCancelEditAction.setVisibility(View.GONE);
                    editStopPointPosition = -1;
                }
            });
            btnShowDialogListStopPoint.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogListStopPoint.show();
                }
            });
            btnCloseDialogListStopPoint.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogListStopPoint.hide();
                }
            });

            btnCompleteUpdateStopPoint.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean canUpdate=false;
                    int size = positionMarkUpdateList.size();
                    for(int i = 0; i<size; i++){
                        if(positionMarkUpdateList.get(i)==1){
                            addList.add(currentList.get(i));
                            canUpdate=true;
                        }
                    }
                    if (!canUpdate && deleteList.size()==0) {

//                        Toast.makeText(CreateStopPointActivity.this,R.string.no_stop_point_selected , Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(CreateStopPointActivity.this, TourInfoActivity.class);
                        intent.putExtra("tourId", tourId);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    } else {

                        addListStopPoint();
                    }
                }
            });

            btnShowSuggestDestination.setOnClickListener(v -> {
                removeOldSuggest();
                getSuggestedDestination();
            });

        } else if (isOK == -1) {
            Intent intent2 = new Intent(CreateStopPointActivity.this, HomeActivity.class);
            startActivity(intent2);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (mLocationPermisstionsGranted) {
            getDeviceLocation();
            for (StopPoint stopPoint : currentList) {
                addStopPointMarker(stopPoint,false);
            }

            mMap.setOnPoiClickListener(this);

            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);

            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    if (!isPOIclick) {
                        if (marker == null) {
                            markerOptions = new MarkerOptions();
                        } else {
                            marker.remove();
                        }
                        markerOptions.position(latLng);
                        marker = mMap.addMarker(markerOptions);
                        mlat = latLng.latitude;
                        mlong = latLng.longitude;
                    } else {
                        isPOIclick = false;
                    }
                }
            });

            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    if (marker.getTag() != null) {
                        StopPoint stopPoint = (StopPoint) marker.getTag();
                        selectedSuggestedStopPoint = stopPoint;
                        showStopPointInfo(stopPoint);
                    }
                    return false;
                }
            });
        }
    }

    private void geoLocateToDialog(LatLng latLng) {

        mlat = latLng.latitude;
        mlong = latLng.longitude;

        Geocoder geocoder = new Geocoder(CreateStopPointActivity.this);
        List<Address> list;
        try {
            list = geocoder.getFromLocation(mlat, mlong, 1);
            if (list.size() > 0) {
                Address address = list.get(0);
                mAddress = address.getAddressLine(0);

                edtStopPointAddress.setText(mAddress);

            } else {
                Log.d(TAG, "geoLocate: EMPTY ");
            }
        } catch (IOException e) {
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
                        } else {
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
                edtStopPointName.setText(title);
            }
        } else {
            markerOptions.position(latLng).title(null);
            marker = mMap.addMarker(markerOptions);
        }

        geoLocateToDialog(latLng);
        hideKeyboard();
    }

    private void searchStopPoint(String searchKey){
        apiTour.searchService(TokenStorage.getInstance().getAccessToken(),searchKey,10,1).enqueue(new Callback<ListSuggestedStopPoint>() {
            @Override
            public void onResponse(Call<ListSuggestedStopPoint> call, Response<ListSuggestedStopPoint> response) {
                if(response.isSuccessful()){
                    suggestionStopPointAdapter.setSuggestions(response.body().getStopPoints());
                    searchBar.updateLastSuggestions(response.body().getStopPoints());
                }
                else{
                    Toast.makeText(CreateStopPointActivity.this, R.string.server_err, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ListSuggestedStopPoint> call, Throwable t) {
//                Toast.makeText(CreateStopPointActivity.this, R.string.failed_fetch_api, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showStopPointInfo(StopPoint stopPoint) {
        BottomSheetDialog dialog = new BottomSheetDialog(CreateStopPointActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_stop_point_info);

        TextView name = (TextView) dialog.findViewById(R.id.stop_point_info_name);
        TextView type = (TextView) dialog.findViewById(R.id.stop_point_info_type);
        TextView address = (TextView) dialog.findViewById(R.id.stop_point_info_address);
        TextView provinceCity = (TextView) dialog.findViewById(R.id.stop_point_info_province_city);
        TextView minCost = (TextView) dialog.findViewById(R.id.stop_point_info_min_cost);
        TextView maxCost = (TextView) dialog.findViewById(R.id.stop_point_info_max_cost);
        TextView leave = (TextView) dialog.findViewById(R.id.stop_point_info_leave);
        TextView arrive = (TextView) dialog.findViewById(R.id.stop_point_info_arrive);
        ImageView add = dialog.findViewById(R.id.add_suggested_stop_point);
        TextView showReview = dialog.findViewById(R.id.btn_stop_point_review);

        showReview.setOnClickListener(v ->{
            Bundle bundle = new Bundle();
            Intent intent = new Intent(this, ServiceReviewActivity.class);
            bundle.putInt("STOPPOINT_ID", stopPoint.getId());
            bundle.putSerializable("STOP_POINT",stopPoint);
            intent.putExtras(bundle);
            startActivity(intent);
        });

        name.setText(stopPoint.getName());
        int numServiceID = stopPoint.getServiceTypeId();
        if(numServiceID>=1&&numServiceID<=4) {
            type.setText(ServiceArr[numServiceID - 1]);
        }
        address.setText(stopPoint.getAddress());
        int numProvinceID = stopPoint.getProvinceId();
        if(numProvinceID>=1&&numProvinceID<=64) {
            provinceCity.setText(ProvinceArr[numProvinceID - 1]);
        }
        int numMinCost = stopPoint.getMinCost();
        int numMaxCost = stopPoint.getMaxCost();
        minCost.setText(Integer.toString(numMinCost));
        maxCost.setText(Integer.toString(numMaxCost));
        if(stopPoint.getArrivalAt()!=null) {
            long numLeave = stopPoint.getLeaveAt();
            long numArrive = stopPoint.getArrivalAt();
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(numLeave);
            Date date = cal.getTime();
            DateFormat dateFormat = new SimpleDateFormat("HH:mm dd/MM/yyyy");
            leave.setText(dateFormat.format(date));
            cal.setTimeInMillis(numArrive);
            date = cal.getTime();
            arrive.setText(dateFormat.format(date));
        }
        dialog.show();

        add.setOnClickListener( v ->{
            mlong = selectedSuggestedStopPoint.getLongitude();
            mlat = selectedSuggestedStopPoint.getLatitude();
            mServiceId = selectedSuggestedStopPoint.getId();
            mServiceTypeId = selectedSuggestedStopPoint.getServiceTypeId();
            mProvinceId = selectedSuggestedStopPoint.getProvinceId();
            edtStopPointAddress.setText(selectedSuggestedStopPoint.getAddress());
            edtStopPointName.setText(selectedSuggestedStopPoint.getName());
            edtStopPointMinCost.setText(String.valueOf(selectedSuggestedStopPoint.getMinCost()));
            edtStopPointMaxCost.setText(String.valueOf(selectedSuggestedStopPoint.getMaxCost()));
            spnService.setSelection(mServiceTypeId - 1);
            spnProvince.setSelection(mProvinceId - 1);
            dialog.dismiss();
            dialogCreateStopPoint.show();
        });


    }

    private int isServiceAvailable() {
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(CreateStopPointActivity.this);
        if (available == ConnectionResult.SUCCESS) {
            return 1;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(CreateStopPointActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
            return 0;
        }
        return -1;
    }

    private void getLocationPermission() {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermisstionsGranted = true;
                SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                supportMapFragment.getMapAsync(CreateStopPointActivity.this);
            } else {
                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUESET_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUESET_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
                    SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                    supportMapFragment.getMapAsync(CreateStopPointActivity.this);
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void init() {

        // nhan tour id:
        Intent intentTourID = getIntent();
        if (intentTourID.hasExtra(CreateTourActivity.INTENT_TOUR_ID)) {
            String strID = intentTourID.getStringExtra(CreateTourActivity.INTENT_TOUR_ID);
            tourId = Integer.parseInt(strID);
        } else {
            TourInfo tourInfo = (TourInfo) intentTourID.getSerializableExtra("tourInfo");
            tourId = tourInfo.getId();
            currentList.addAll(tourInfo.getStopPoints());
            existedBeforeSize = currentList.size();
            for (int i = 0; i< existedBeforeSize; i++){
                positionMarkUpdateList.add(0);
            }
            if (intentTourID.hasExtra("canNotEdit")) {
                canNotEdit = true;
            }
        }

        apiTour = new APIRetrofitCreator().getAPIService();
        viewUnderSearchBar=findViewById(R.id.map_layout1);

//        edtSearchAddr = findViewById(R.id.edt_search_addr);
        searchBar = findViewById(R.id.search_bar_stop_point);
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        suggestionStopPointAdapter = new ListSuggestionStopPointAdapter(inflater);
        searchBar.setCustomSuggestionAdapter(suggestionStopPointAdapter);

        SuggestionsAdapter.OnItemViewClickListener listener = new SuggestionsAdapter.OnItemViewClickListener() {
            @Override
            public void OnItemClickListener(int position, View v) {
                selectedSuggestedStopPoint = suggestionStopPointAdapter.getSuggestions().get(position);
                LatLng latLng = new LatLng(selectedSuggestedStopPoint.getLatitude(),selectedSuggestedStopPoint.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,DEFAULT_ZOOM));
                if (marker == null) {
                    markerOptions = new MarkerOptions();
                } else {
                    marker.remove();
                }
                markerOptions.position(latLng).title(selectedSuggestedStopPoint.getName());
                marker = mMap.addMarker(markerOptions);
                showStopPointInfo(selectedSuggestedStopPoint);
            }

            @Override
            public void OnItemDeleteListener(int position, View v) {

            }
        };
        suggestionStopPointAdapter.setListener(listener);

        btnCurLocation = findViewById(R.id.map_btn_cur_location);
        btnShowDialogStopPointInfo = findViewById(R.id.map_btn_show_dialog);
        btnShowSuggestDestination = findViewById(R.id.map_btn_suggest_destination);
        if (canNotEdit) {
            btnShowDialogStopPointInfo.setVisibility(View.GONE);
            btnShowSuggestDestination.setVisibility(View.GONE);
            searchBar.setVisibility(View.GONE);
            viewUnderSearchBar.setVisibility(View.GONE);
        }

        dialogCreateStopPoint = new Dialog(CreateStopPointActivity.this, R.style.DialogSlideAnimation);
        dialogCreateStopPoint.setContentView(R.layout.dialog_create_stop_point);
        btnCloseDialogStopPointInfo = dialogCreateStopPoint.findViewById(R.id.map_btn_close_dialog);
        edtStopPointAddress = dialogCreateStopPoint.findViewById(R.id.create_stop_point_address);
        edtStopPointName = dialogCreateStopPoint.findViewById(R.id.create_stop_point_name);
        edtStopPointMinCost = dialogCreateStopPoint.findViewById(R.id.create_stop_point_min_cost);
        edtStopPointMaxCost = dialogCreateStopPoint.findViewById(R.id.create_stop_point_max_cost);
        edtStopPointTimeArrive = dialogCreateStopPoint.findViewById(R.id.create_stop_point_arrive_time);
        edtStopPointDateArrive = dialogCreateStopPoint.findViewById(R.id.create_stop_point_arrive_date);
        edtStopPointTimeLeave = dialogCreateStopPoint.findViewById(R.id.create_stop_point_leave_time);
        edtStopPointDateLeave = dialogCreateStopPoint.findViewById(R.id.create_stop_point_leave_date);
        btnCreateStopPoint = dialogCreateStopPoint.findViewById(R.id.btn_create_stop_point);
        btnCancelEditAction = dialogCreateStopPoint.findViewById(R.id.map_btn_cancel_edit_action);

        btnShowDialogListStopPoint = findViewById(R.id.map_btn_show_list);
        dialogListStopPoint = new Dialog(CreateStopPointActivity.this, R.style.DialogSlideAnimation);
        dialogListStopPoint.setContentView(R.layout.dialog_list_temporary_stop_point);
        btnCloseDialogListStopPoint = dialogListStopPoint.findViewById(R.id.map_btn_close_dialog_list);
        btnCompleteUpdateStopPoint = dialogListStopPoint.findViewById(R.id.btn_update_list_stop_point);
        if (canNotEdit) {
            btnCloseDialogListStopPoint.setVisibility(View.GONE);
            btnCompleteUpdateStopPoint.setVisibility(View.GONE);
        }

        listViewStopPoint = dialogListStopPoint.findViewById(R.id.map_list_view_temporary_stop_point);
        listStopPointTemporaryAdapter = new ListStopPointTemporaryAdapter(CreateStopPointActivity.this, R.layout.listview_temporary_stop_point_item, currentList, canNotEdit);
        listStopPointTemporaryAdapter.notifyDataSetChanged();
        listViewStopPoint.setAdapter(listStopPointTemporaryAdapter);


        spnService = (Spinner) dialogCreateStopPoint.findViewById(R.id.spn_create_stop_point_service);
        spnProvince = (Spinner) dialogCreateStopPoint.findViewById(R.id.spn_create_stop_point_province);

        ArrayAdapter<String> serviceAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, ServiceArr){
            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                tv.setTextColor(Color.BLACK);
                return view;
            }
        };
        ArrayAdapter<String> provinceAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, ProvinceArr){
            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                tv.setTextColor(Color.BLACK);
                return view;
            }
        };

        serviceAdapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        provinceAdapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);

        spnService.setAdapter(serviceAdapter);
        spnProvince.setAdapter(provinceAdapter);

        spnService.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mServiceTypeId = position + 1;
//               Log.d(TAG, "onItemSelected: "+mServiceTypeId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // idService = -1
            }
        });

        spnProvince.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mProvinceId = position + 1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // idProvince = -1
            }
        });


        Calendar myCalendar = Calendar.getInstance();
        int hour = myCalendar.get(Calendar.HOUR_OF_DAY);
        int minute = myCalendar.get(Calendar.MINUTE);
        TimePickerDialog.OnTimeSetListener myTimeArriveListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String h = (hourOfDay < 10) ? "0" + hourOfDay : hourOfDay + "";
                String m = (minute < 10) ? "0" + minute : minute + "";
                edtStopPointTimeArrive.setText(h + ":" + m);
            }
        };
        TimePickerDialog timeArrivePickerDialog = new TimePickerDialog(CreateStopPointActivity.this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar, myTimeArriveListener, hour, minute, true);
        timeArrivePickerDialog.setTitle(getString(R.string.stop_point_select_time));
        timeArrivePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        edtStopPointTimeArrive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeArrivePickerDialog.show();
            }
        });

        DatePickerDialog.OnDateSetListener dateArrive = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                String myFormat = "dd/MM/yyyy"; //
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                edtStopPointDateArrive.setText(sdf.format(myCalendar.getTime()));
            }

        };
        DatePickerDialog dateArrivePicker = new DatePickerDialog(CreateStopPointActivity.this, dateArrive, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH));
        edtStopPointDateArrive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateArrivePicker.show();
            }
        });


        TimePickerDialog.OnTimeSetListener myTimeLeaveListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String h = (hourOfDay < 10) ? "0" + hourOfDay : hourOfDay + "";
                String m = (minute < 10) ? "0" + minute : minute + "";
                edtStopPointTimeLeave.setText(h + ":" + m);
            }
        };
        TimePickerDialog timeLeavePickerDialog = new TimePickerDialog(CreateStopPointActivity.this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar, myTimeLeaveListener, hour, minute, true);
        timeLeavePickerDialog.setTitle(getString(R.string.stop_point_select_time));
        timeLeavePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        edtStopPointTimeLeave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeLeavePickerDialog.show();
            }
        });

        DatePickerDialog.OnDateSetListener dateLeave = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                String myFormat = "dd/MM/yyyy"; //
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                edtStopPointDateLeave.setText(sdf.format(myCalendar.getTime()));
            }

        };
        DatePickerDialog dateLeavePicker = new DatePickerDialog(CreateStopPointActivity.this, dateLeave, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH));
        edtStopPointDateLeave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateLeavePicker.show();
            }
        });


//        autocompleteFragment = (AutocompleteSupportFragment)
//                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
//        if (!Places.isInitialized()) {
//            Places.initialize(getApplicationContext(), getString(R.string.gg_map_API),Locale.CHINESE);
//        }
//      Specify the types of place data to return.
//        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));
    }

    private StopPoint checkValidInput() {
        String name = edtStopPointName.getText().toString();
        if (name.isEmpty()) {
            Toast.makeText(CreateStopPointActivity.this, getString(R.string.stop_point_empty_name), Toast.LENGTH_SHORT).show();
            return null;
        }
        String address = edtStopPointAddress.getText().toString();
        if (address.isEmpty()) {
            Toast.makeText(CreateStopPointActivity.this, getString(R.string.stop_point_empty_address), Toast.LENGTH_SHORT).show();
            return null;
        }
//        int provinceId=1;
//        if(provinceId==0){
//            Toast.makeText(CreateStopPointActivity.this, getString(R.string.stop_point_empty_province), Toast.LENGTH_SHORT).show();
//            return null;
//        }
//        int serviceType=2;
//        if(serviceType==0){
//            Toast.makeText(CreateStopPointActivity.this, getString(R.string.stop_point_empty_service_type), Toast.LENGTH_SHORT).show();
//            return null;
//        }
        String arriveTime = edtStopPointTimeArrive.getText().toString();
        if (arriveTime.isEmpty()) {
            Toast.makeText(CreateStopPointActivity.this, getString(R.string.stop_point_empty_arrive_time), Toast.LENGTH_SHORT).show();
            return null;
        }
        String arriveDate = edtStopPointDateArrive.getText().toString();
        if (arriveDate.isEmpty()) {
            Toast.makeText(CreateStopPointActivity.this, getString(R.string.stop_point_empty_arrive_date), Toast.LENGTH_SHORT).show();
            return null;
        }
        String leaveTime = edtStopPointTimeLeave.getText().toString();
        if (leaveTime.isEmpty()) {
            Toast.makeText(CreateStopPointActivity.this, getString(R.string.stop_point_empty_leave_time), Toast.LENGTH_SHORT).show();
            return null;
        }
        String leaveDate = edtStopPointDateLeave.getText().toString();
        if (leaveDate.isEmpty()) {
            Toast.makeText(CreateStopPointActivity.this, getString(R.string.stop_point_empty_leave_date), Toast.LENGTH_SHORT).show();
            return null;
        }
        long utimeArr = 0;
        long utimeLev = 0;


        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        try {
            Date date1 = (Date) formatter.parse(arriveDate + " " + arriveTime);
            utimeArr = date1.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            Date date2 = (Date) formatter.parse(leaveDate + " " + leaveTime);
            utimeLev = date2.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
//        Log.d(TAG, "checkValidInput: SERVICE "+mServiceTypeId);

        if (utimeArr != 0 && utimeLev != 0) {
            String minCost = edtStopPointMinCost.getText().toString();
            String maxCost = edtStopPointMaxCost.getText().toString();

            StopPoint stopPoint = new StopPoint(null, name, address, mProvinceId, mlong, mlat, null, Integer.parseInt(minCost), Integer.parseInt(maxCost), utimeArr, utimeLev, mServiceTypeId);
//            Toast.makeText(CreateStopPointActivity.this,R.string.stop_point_add_successfully, Toast.LENGTH_SHORT).show();
            return stopPoint;
        } else {
            Toast.makeText(CreateStopPointActivity.this, R.string.failed_fetch_api, Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    private void addListStopPoint() {
        //goi api
        DialogProgressBar.showProgress(this);
        UpdateStopPointsOfTour updateStopPointsOfTour = new UpdateStopPointsOfTour(tourId, addList, deleteList);
        apiTour.addStopPointToTour(TokenStorage.getInstance().getAccessToken(), updateStopPointsOfTour).enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                if (response.isSuccessful()) {
//                    MessageResponse message = response.body();
                    Toast.makeText(CreateStopPointActivity.this, R.string.stop_point_add_successfully, Toast.LENGTH_SHORT).show();
//                    dialogCreateStopPoint.hide();
                    Intent intent = new Intent(CreateStopPointActivity.this, TourInfoActivity.class);
                    intent.putExtra("tourId", tourId);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(response.errorBody().string());
                        Toast.makeText(CreateStopPointActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                DialogProgressBar.closeProgress();
            }

            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {
                Toast.makeText(CreateStopPointActivity.this, R.string.failed_fetch_api, Toast.LENGTH_SHORT).show();
                DialogProgressBar.closeProgress();
            }
        });
    }

    private void hideKeyboard() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    public void onPoiClick(PointOfInterest pointOfInterest) {
//        Log.d(TAG, "onPoiClick: "+pointOfInterest.name);
        moveCamera(pointOfInterest.latLng, DEFAULT_ZOOM, pointOfInterest.name);
        isPOIclick = true;
    }

    public void moveCameraWhenSelectStopPoint(StopPoint stopPoint) {
        dialogListStopPoint.hide();
        LatLng latLng = new LatLng(stopPoint.getLatitude(), stopPoint.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));
        if (marker == null) {
            markerOptions = new MarkerOptions();
        } else {
            marker.remove();
        }

        markerOptions.position(latLng).title(stopPoint.getName());
        marker = mMap.addMarker(markerOptions);
        hideKeyboard();
    }

    public void addToDeleteList(int position){
        Integer id = currentList.get(position).getId();
        currentList.remove(position);
        positionMarkUpdateList.remove(position);
        Log.d(TAG, "addToDeleteList: "+currentList.size()+":"+positionMarkUpdateList.size());
        listStopPointTemporaryAdapter.notifyDataSetChanged();
        deleteList.add(id);
    }

    public void removeTemporaryStopPoint(int position) {
        currentList.remove(position);
        positionMarkUpdateList.remove(position);
        Log.d(TAG, "removeTemporaryStopPoint: "+currentList.size()+":"+positionMarkUpdateList.size());
        listStopPointTemporaryAdapter.notifyDataSetChanged();
    }

    public void showEditStopPointDialog(int positionInCurrent, StopPoint stopPoint, String arriveAt, String leaveAt) {
        dialogListStopPoint.hide();
        btnCancelEditAction.setVisibility(View.VISIBLE);
        dialogCreateStopPoint.show();
        editStopPointPosition = positionInCurrent;
        mlong = stopPoint.getLongitude();
        mlat = stopPoint.getLatitude();
        mServiceTypeId = stopPoint.getServiceTypeId();
        mProvinceId = stopPoint.getProvinceId();
        edtStopPointAddress.setText(stopPoint.getAddress());
        edtStopPointName.setText(stopPoint.getName());
        edtStopPointMinCost.setText(String.valueOf(stopPoint.getMinCost()));
        edtStopPointMaxCost.setText(String.valueOf(stopPoint.getMaxCost()));
        edtStopPointTimeArrive.setText(arriveAt.substring(0, 5));
        edtStopPointDateArrive.setText(arriveAt.substring(6));
        edtStopPointTimeLeave.setText(leaveAt.substring(0, 5));
        edtStopPointDateLeave.setText(leaveAt.substring(6));
        spnService.setSelection(mServiceTypeId - 1);
        spnProvince.setSelection(mProvinceId - 1);
    }

    @Override
    public void onBackPressed() {
        boolean canUpdate=false;
        for(Integer check : positionMarkUpdateList){
            if(check==1){
                canUpdate=true;
                break;
            }
        }
        if (canUpdate || deleteList.size() > 0) {
            new AlertDialog.Builder(this)
                    .setMessage("Thoát sẽ mất những thay đổi. Vẫn thoát?")
                    .setCancelable(false)
                    .setPositiveButton("Thoát và bỏ lưu", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            CreateStopPointActivity.super.onBackPressed();
                        }
                    })
                    .setNegativeButton("Hủy", (dialog, which) -> dialogListStopPoint.show())
                    .show();
        } else {
            super.onBackPressed();
        }
    }

    private void addStopPointMarker(StopPoint stopPoint, boolean isSuggestion) {
        if (marker != null) {
            marker.remove();
        }
        MarkerOptions stopPointMarkerOptions = new MarkerOptions();
        if(isSuggestion) {
            stopPointMarkerOptions.position(new LatLng(stopPoint.getLatitude(), stopPoint.getLongitude()))
                    .icon(bitmapDescriptorFromVector(CreateStopPointActivity.this, R.drawable.ic_suggest_marker))
                    .title(stopPoint.getName());
            Marker stopPointMarker = mMap.addMarker(stopPointMarkerOptions);
            stopPointMarker.setTag(stopPoint);
        }
        else{
            stopPointMarkerOptions.position(new LatLng(stopPoint.getLatitude(), stopPoint.getLongitude()))
                    .icon(bitmapDescriptorFromVector(CreateStopPointActivity.this, R.drawable.ic_pin))
                    .title(stopPoint.getName());
            Marker stopPointMarker = mMap.addMarker(stopPointMarkerOptions);
            stopPointMarker.setTag(stopPoint);
            markers.add(stopPointMarker);
        }
    }

    public void removeStopPointMarker(StopPoint stopPoint) {
        for (Iterator<Marker> i = markers.iterator(); i.hasNext(); ) {
            Marker _marker = i.next();
            if (_marker.getTag().equals(stopPoint)) {
                _marker.remove();
                i.remove();
            }
        }
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

    private Coordinate getDeviceCoordiate() {
        LatLng latLng = mMap.getCameraPosition().target;
        return new Coordinate(latLng.latitude,latLng.longitude);
    }

    private void removeOldSuggest(){

    }

    private void getSuggestedDestination(){
        DialogProgressBar.showProgress(this);
        RequestCoordinateList requestCoordinateList = new RequestCoordinateList(true,getDeviceCoordiate());
        apiTour.getSuggesteDestination(TokenStorage.getInstance().getAccessToken(),requestCoordinateList).enqueue(new Callback<ListSuggestedStopPoint>() {
            @Override
            public void onResponse(Call<ListSuggestedStopPoint> call, Response<ListSuggestedStopPoint> response) {
                if(response.isSuccessful()){
                    for (StopPoint stopPoint: response.body().getStopPoints()){
                        addStopPointMarker(stopPoint,true);
                    }
                }
                else{
                    Toast.makeText(CreateStopPointActivity.this, R.string.server_err, Toast.LENGTH_SHORT).show();
                }
                DialogProgressBar.closeProgress();
            }

            @Override
            public void onFailure(Call<ListSuggestedStopPoint> call, Throwable t) {
                Toast.makeText(CreateStopPointActivity.this, R.string.failed_fetch_api, Toast.LENGTH_SHORT).show();
                DialogProgressBar.closeProgress();
            }
        });
    }

}
