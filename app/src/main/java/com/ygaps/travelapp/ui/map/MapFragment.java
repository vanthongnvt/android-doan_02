package com.ygaps.travelapp.ui.map;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.messaging.FirebaseMessaging;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.ygaps.travelapp.Adapter.ListMapDestinationAdapter;
import com.ygaps.travelapp.Adapter.ListNotificationTourAdapter;
import com.ygaps.travelapp.Adapter.ListTourCommentAdapter;
import com.ygaps.travelapp.Adapter.ListTourMemberAdapter;
import com.ygaps.travelapp.ApiService.APIRetrofitCreator;
import com.ygaps.travelapp.ApiService.APITour;
import com.ygaps.travelapp.AppHelper.DialogProgressBar;
import com.ygaps.travelapp.AppHelper.PicassoMarker;
import com.ygaps.travelapp.AppHelper.TokenStorage;
import com.ygaps.travelapp.HomeActivity;
import com.ygaps.travelapp.Model.FirebaseNotifyLocation;
import com.ygaps.travelapp.Model.MemberLocation;
import com.ygaps.travelapp.Model.MessageResponse;
import com.ygaps.travelapp.Model.Notification;
import com.ygaps.travelapp.Model.NotificationList;
import com.ygaps.travelapp.Model.NotificationOnRoad;
import com.ygaps.travelapp.Model.NotificationOnRoadList;
import com.ygaps.travelapp.Model.StopPoint;
import com.ygaps.travelapp.Model.TourInfo;
import com.ygaps.travelapp.Model.TourMember;
import com.ygaps.travelapp.Model.TourNotificationLimitSpeed;
import com.ygaps.travelapp.Model.TourNotificationText;
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
import com.ygaps.travelapp.Service.BackgroundLocationService;
import com.ygaps.travelapp.Service.BroadcastLocationReceiver;

import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapFragment extends Fragment implements OnMapReadyCallback, AbsListView.OnScrollListener, LocationListener {
    private MapViewModel mapViewModel;

    private static final int ERROR_DIALOG_REQUEST = 9001;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUESET_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;
    private static final String TAG = "MAP_DIRECTION";

    private boolean mLocationPermisstionsGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private MarkerOptions markerOptions = null;
    private LocationManager locationManager;
    private Marker marker;
    @SuppressLint("UseSparseArrays")
    private HashMap markerMemberHashMap = new HashMap<Integer, Marker>();
    private PicassoMarker picassoMarker;
    private Transformation transformation;
    private SharedPreferences sharedPreferences;

    private Context context;
    private View mRoot;
    private ImageView btnCurLocation;
    private ImageView btnShowListDestination;
    private ImageView btnShowWarningSpeedNotificationDialog;
    private ImageView btnShowListMember;
    private ImageView btnShowNotificationList;
    private ImageView btnSetting;

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
    private ImageView addStopPoint;
    String ServiceArr[] = {"Restaurant", "Hotel", "Rest Station", "Other"};
    String ProvinceArr[] = {"Hồ Chí Minh", "Hà Nội", "Đà Nẵng", "Bình Dương", "Đồng Nai", "Khánh Hòa", "Hải Phòng", "Long An", "Quảng Nam", "Bà Rịa Vũng Tàu", "Đắk Lắk", "Cần Thơ", "Bình Thuận  ", "Lâm Đồng", "Thừa Thiên Huế", "Kiên Giang", "Bắc Ninh", "Quảng Ninh", "Thanh Hóa", "Nghệ An", "Hải Dương", "Gia Lai", "Bình Phước", "Hưng Yên", "Bình Định", "Tiền Giang", "Thái Bình", "Bắc Giang", "Hòa Bình", "An Giang", "Vĩnh Phúc", "Tây Ninh", "Thái Nguyên", "Lào Cai", "Nam Định", "Quảng Ngãi", "Bến Tre", "Đắk Nông", "Cà Mau", "Vĩnh Long", "Ninh Bình", "Phú Thọ", "Ninh Thuận", "Phú Yên", "Hà Nam", "Hà Tĩnh", "Đồng Tháp", "Sóc Trăng", "Kon Tum", "Quảng Bình", "Quảng Trị", "Trà Vinh", "Hậu Giang", "Sơn La", "Bạc Liêu", "Yên Bái", "Tuyên Quang", "Điện Biên", "Lai Châu", "Lạng Sơn", "Hà Giang", "Bắc Kạn", "Cao Bằng"};


    private BottomSheetDialog dialogCreateNotification;
    private RadioGroup rdWarnings;
    private RadioButton rdWarning50;
    private RadioButton rdWarning60;
    private RadioButton rdWarning70;
    private EditText textNotification;
    private Button btnSendNotification;

    private BottomSheetDialog dialogListMember;
    private ListView listViewMember;
    private ListTourMemberAdapter listTourMemberAdapter;

    private BottomSheetDialog dialogMemberInfoMarker;
    private TextView markerMemberName;
    private ImageView markerMemberAvatar;
    private TextView markerMemberPhone;
    private TextView markerHost;

    private Dialog dialogNotificationList;
    private EditText inputTextNotify;
    private ImageView btnSendTextNotify;
    private ListView listViewNotification;
    private ListNotificationTourAdapter tourNotificationAdapter;
    private List<Notification> notificationList = new ArrayList<>();
    private ProgressBar progressBar;
    private Integer pageIndex = 1;
    private Integer pageSize = 20;
    private boolean loading = false;
    private Integer count = 0;
    private boolean firstClick = true;

    private BottomSheetDialog dialogTourSetting;
    private Button btnExit;
    private Button btnFinishTour;

    private Integer tourId = null;
    private Integer userId;
    private TourInfo tourInfo;
    private APITour apiTour;
    private StopPoint targetStopPoint = null;
    private Location myLocation = null;
    private boolean getlocationFail = false;
    private Polyline polyline;
    private boolean elementIsHiding = false;

    private BroadcastLocationReceiver broadcastLocationReceiver;
    private IntentFilter mIntentFilter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        View root = inflater.inflate(R.layout.fragment_map, container, false);
        context = root.getContext();
        transformation = new RoundedTransformationBuilder()
                .borderColor(Color.RED)
                .borderWidthDp(1)
                .cornerRadiusDp(30)
                .oval(false)
                .build();
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
        userId = TokenStorage.getInstance().getUserId();
        sharedPreferences = getContext().getSharedPreferences("FOLLOW_TOUR", Context.MODE_PRIVATE);
        tourId = sharedPreferences.getInt("tourId", -1);
        boolean fIntent=false;
        if(getArguments() != null){
            tourId = getArguments().getInt("directionTourId",-2);
            if(tourId!=-1 && tourId!=-2) {
                fIntent = true;
            }
        }
        if (tourId > 0) {
            if(fIntent) {
                sharedPreferences.edit().putInt("tourId", tourId).apply();
            }
            int isOK = isServiceAvailable();

            if (isOK == 1) {
                getLocationPermission();
                init();

                FirebaseMessaging.getInstance().subscribeToTopic("tour-id-" + tourId);
                Intent serviceIntent = new Intent(context, BackgroundLocationService.class);
                serviceIntent.putExtra("tourId", tourId);
                context.startService(serviceIntent);

                broadcastLocationReceiver = new BroadcastLocationReceiver();
                mIntentFilter = new IntentFilter(getString(R.string.receiver_action_send_coordinate));
                mIntentFilter.addAction(getString(R.string.receiver_action_send_notification_on_road));
                mIntentFilter.addAction(getString(R.string.receiver_action_noti_text));
                mIntentFilter.addAction(getString(R.string.receiver_action_noti_limit_speed));
                context.registerReceiver(broadcastLocationReceiver, mIntentFilter);

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
        btnShowWarningSpeedNotificationDialog = mRoot.findViewById(R.id.show_warning_notification);

        dialogListDestination = new Dialog(getContext(), R.style.DialogSlideAnimation);
        dialogListDestination.setContentView(R.layout.dialog_list_destination_map);
        initDialogListDestination();

        dialogStopPointInfo = new BottomSheetDialog(getContext());
        dialogStopPointInfo.setContentView(R.layout.dialog_stop_point_info);
        initDialogStopPointInfo();

        dialogCreateNotification = new BottomSheetDialog(getContext());
        dialogCreateNotification.setContentView(R.layout.dialog_create_notification_on_road);
        initDialogCreateNotification();

        btnShowListMember = mRoot.findViewById(R.id.btn_show_member_list);
        dialogListMember = new BottomSheetDialog(getContext());
        dialogListMember.setContentView(R.layout.dialog_list_member_map);
        initDialogListMember();

        dialogMemberInfoMarker = new BottomSheetDialog(getContext());
        dialogMemberInfoMarker.setContentView(R.layout.dialog_member_info_marker);
        initDialogMemberInfoMarker();

        btnSetting = mRoot.findViewById(R.id.tour_setting);
        dialogTourSetting = new BottomSheetDialog(getContext());
        dialogTourSetting.setContentView(R.layout.dialog_tour_setting);
        initDialogTourSetting();

        apiTour = new APIRetrofitCreator().getAPIService();

        btnCurLocation.setOnClickListener(v -> getDeviceLocation(true));

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
                        if (myLocation != null && targetStopPoint != null) {
                            drawPolylineBetweenTwoLocation(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()), new LatLng(targetStopPoint.getLatitude(), targetStopPoint.getLongitude()));
                        } else {
                            Log.d(TAG, "init: NULL");
                        }
                    }
                    mapDestinationAdapter = new ListMapDestinationAdapter(getContext(), R.layout.list_view_destination_item, tourInfo.getStopPoints(), MapFragment.this);
                    listViewDestination.setAdapter(mapDestinationAdapter);

                    listTourMemberAdapter = new ListTourMemberAdapter(getContext(), R.layout.list_view_tour_member_item, tourInfo.getMembers());
                    listViewMember.setAdapter(listTourMemberAdapter);

                } else {
                    Toast.makeText(context, R.string.server_err, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TourInfo> call, Throwable t) {
                Toast.makeText(context, R.string.failed_fetch_api, Toast.LENGTH_SHORT).show();
            }
        });


        btnShowNotificationList = mRoot.findViewById(R.id.show_notification_list);
        initDialogNotification();
        btnShowNotificationList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogNotificationList.show();
                if (firstClick) {
                    getTextNotifation();
                    firstClick = false;
                }
            }
        });

        btnShowListDestination.setOnClickListener(v -> dialogListDestination.show());

        btnShowWarningSpeedNotificationDialog.setOnClickListener(v -> {
            dialogCreateNotification.show();
            rdWarnings.clearCheck();
            textNotification.setText(null);
        });

        btnShowListMember.setOnClickListener(v -> dialogListMember.show());

        btnSetting.setOnClickListener(v -> dialogTourSetting.show());

    }

    private void initDialogListMember() {
        listViewMember = dialogListMember.findViewById(R.id.list_view_tour_member);

        listViewMember.setOnItemClickListener((parent, view, position, id) -> {
            TourMember member = tourInfo.getMembers().get(position);
            if (markerMemberHashMap.containsKey(member.getId())) {
                Marker markerMember = (Marker) markerMemberHashMap.get(member.getId());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerMember.getPosition(), DEFAULT_ZOOM));
            }
        });
    }

    private void initDialogMemberInfoMarker() {
        markerMemberName = dialogMemberInfoMarker.findViewById(R.id.marker_tv_member_name);
        markerMemberAvatar = dialogMemberInfoMarker.findViewById(R.id.marker_member_avatar);
        markerMemberPhone = dialogMemberInfoMarker.findViewById(R.id.marker_tv_member_phone);
        markerHost = dialogMemberInfoMarker.findViewById(R.id.marker_tv_host);
    }

    private void initDialogListDestination() {
        listViewDestination = dialogListDestination.findViewById(R.id.list_view_destination);
        listViewDestination.setOnItemClickListener(((parent, view, position, id) -> {
            StopPoint stopPoint = tourInfo.getStopPoints().get(position);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(stopPoint.getLatitude(), stopPoint.getLongitude()), DEFAULT_ZOOM));
            dialogListDestination.dismiss();
        }));

    }

    private void initDialogStopPointInfo() {
        nameStopPoint = dialogStopPointInfo.findViewById(R.id.stop_point_info_name);
        typeStopPoint = dialogStopPointInfo.findViewById(R.id.stop_point_info_type);
        addressStopPoint = dialogStopPointInfo.findViewById(R.id.stop_point_info_address);
        provinceCityStopPoint = dialogStopPointInfo.findViewById(R.id.stop_point_info_provice_city);
        minCostStopPoint = dialogStopPointInfo.findViewById(R.id.stop_point_info_min_cost);
        maxCostStopPoint = dialogStopPointInfo.findViewById(R.id.stop_point_info_max_cost);
        leaveStopPoint = dialogStopPointInfo.findViewById(R.id.stop_point_info_leave);
        arriveStopPoint = dialogStopPointInfo.findViewById(R.id.stop_point_info_arrive);
        addStopPoint = dialogStopPointInfo.findViewById(R.id.add_suggested_stop_point);
        addStopPoint.setVisibility(View.VISIBLE);
    }

    private void initDialogCreateNotification() {
        rdWarnings = dialogCreateNotification.findViewById(R.id.warning_speed);
        rdWarning50 = dialogCreateNotification.findViewById(R.id.warning_speed_50);
        rdWarning60 = dialogCreateNotification.findViewById(R.id.warning_speed_60);
        rdWarning70 = dialogCreateNotification.findViewById(R.id.warning_speed_70);
        textNotification = dialogCreateNotification.findViewById(R.id.text_notification);
        btnSendNotification = dialogCreateNotification.findViewById(R.id.send_notification);

        btnSendNotification.setOnClickListener(v -> sendWarningSpeedNotification());
    }

    private void initDialogNotification() {
        dialogNotificationList = new Dialog(getContext(), R.style.DialogSlideAnimation);
        dialogNotificationList.setContentView(R.layout.dialog_list_notification);

        btnSendTextNotify = dialogNotificationList.findViewById(R.id.send_comment);
        inputTextNotify = dialogNotificationList.findViewById(R.id.input_comment);
        listViewNotification = dialogNotificationList.findViewById(R.id.list_view_comment);
        tourNotificationAdapter = new ListNotificationTourAdapter(getContext(), R.layout.list_view_tour_comment_item, notificationList);
        listViewNotification.setAdapter(tourNotificationAdapter);
        listViewNotification.setOnScrollListener(this);

        progressBar = dialogNotificationList.findViewById(R.id.progressbar_loading);

        btnSendTextNotify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = inputTextNotify.getText().toString();
                if (!text.isEmpty()) {
                    inputTextNotify.setText("");
                    sendTextNotification(text);
                }
            }
        });
    }

    private void initDialogTourSetting() {
        btnExit = dialogTourSetting.findViewById(R.id.btn_exit);

        btnExit.setOnClickListener(v -> {
            dialogTourSetting.dismiss();
            sharedPreferences.edit().remove("tourId").apply();
            ((HomeActivity) getActivity()).getNavigation().setSelectedItemId(R.id.navigation_history);
        });
        btnFinishTour = dialogTourSetting.findViewById(R.id.btn_finish_tour);
        if(Integer.parseInt(tourInfo.getHostId())==userId) {

            btnFinishTour.setOnClickListener(v -> {
                dialogTourSetting.dismiss();
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setTitle("Kết thúc chuyến đi");
                alert.setMessage("Bạn sẽ không thể theo dõi chuyến đi này nữa");
                alert.setPositiveButton("Ok", (dialog, which) -> {
                    dialog.dismiss();
                    DialogProgressBar.showProgress(getContext());
                    apiTour.finishTour(TokenStorage.getInstance().getAccessToken(), tourId).enqueue(new Callback<MessageResponse>() {
                        @Override
                        public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                            if (response.isSuccessful()) {
                                ((HomeActivity) getActivity()).getNavigation().setSelectedItemId(R.id.navigation_history);
                            } else {
                                Toast.makeText(context, R.string.server_err, Toast.LENGTH_SHORT).show();
                            }
                            DialogProgressBar.closeProgress();
                        }

                        @Override
                        public void onFailure(Call<MessageResponse> call, Throwable t) {
                            Toast.makeText(context, R.string.failed_fetch_api, Toast.LENGTH_SHORT).show();
                            DialogProgressBar.closeProgress();
                        }
                    });

                });

                alert.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());

                alert.show();
            });
        }
        else{
            btnFinishTour.setVisibility(View.GONE);
        }
    }

    private void sendWarningSpeedNotification() {
//        getDeviceLocation(false);
//        if (getlocationFail) {
//            return;
//        }
        int speed = 0;
        String text = textNotification.getText().toString().trim();
        switch (rdWarnings.getCheckedRadioButtonId()) {
            case R.id.warning_speed_50:
                speed = 50;
                break;
            case R.id.warning_speed_60:
                speed = 60;
                break;
            case R.id.warning_speed_70:
                speed = 70;
                break;
        }
        if (speed != 0) {
            apiTour.createNotificationOnRoad(TokenStorage.getInstance().getAccessToken(), myLocation.getLatitude(), myLocation.getLongitude(), tourId, TokenStorage.getInstance().getUserId(), 3, speed, text).enqueue(new Callback<MessageResponse>() {
                @Override
                public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(context, R.string.notify_successfully, Toast.LENGTH_SHORT).show();
                        dialogCreateNotification.dismiss();
                    } else {
                        Toast.makeText(context, R.string.server_err, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<MessageResponse> call, Throwable t) {
                    Toast.makeText(context, R.string.failed_fetch_api, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(context, R.string.empty_warning_speed, Toast.LENGTH_SHORT).show();
        }
    }

    private void sendTextNotification(String text) {

        apiTour.sendNotification(TokenStorage.getInstance().getAccessToken(), TokenStorage.getInstance().getUserId(), tourId, text).enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(context, R.string.notify_successfully, Toast.LENGTH_SHORT).show();
                    Integer userId = TokenStorage.getInstance().getUserId();
                    Notification notification = new Notification(userId.toString(), "<ID :"+userId+" >", null, text);
                    notificationList.add(notification);
                    tourNotificationAdapter.notifyDataSetChanged();

                } else {
                    Toast.makeText(context, R.string.server_err, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {
                Toast.makeText(context, R.string.failed_fetch_api, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getTextNotifation() {
        loading = true;
        progressBar.setVisibility(View.VISIBLE);
        apiTour.getNotificationTour(TokenStorage.getInstance().getAccessToken(), tourInfo.getId(), pageIndex, pageSize).enqueue(new Callback<NotificationList>() {
            @Override
            public void onResponse(Call<NotificationList> call, Response<NotificationList> response) {
                if (response.isSuccessful()) {
                    count = response.body().getNotiList().size();
                    notificationList.addAll(response.body().getNotiList());
                    tourNotificationAdapter.notifyDataSetChanged();
                    pageIndex++;
                    progressBar.setVisibility(View.GONE);
                } else {
                    Toast.makeText(getContext(), R.string.failed_fetch_api, Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
                loading = false;
            }

            @Override
            public void onFailure(Call<NotificationList> call, Throwable t) {
                Toast.makeText(getContext(), R.string.failed_fetch_api, Toast.LENGTH_SHORT).show();
                loading = false;
            }
        });
    }


    public void showStopPointInfo(StopPoint stopPoint) {

        nameStopPoint.setText(stopPoint.getName());
        int numServiceID = stopPoint.getServiceTypeId();
        if (numServiceID >= 1 && numServiceID <= 4) {
            typeStopPoint.setText(ServiceArr[numServiceID - 1]);
        }
        int numProvinceID = stopPoint.getProvinceId();
        if (numProvinceID >= 1 && numProvinceID <= 64) {
            provinceCityStopPoint.setText(ProvinceArr[numProvinceID - 1]);
        }

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
    }

    public void showMemberInfoMarker(TourMember tourMember) {
        if (tourMember.getAvatar() != null && !tourMember.getAvatar().isEmpty()) {
            Picasso.get().load(tourMember.getAvatar()).into(markerMemberAvatar);
        } else {
            markerMemberAvatar.setImageResource(R.drawable.unknown_user);
        }

        markerMemberName.setText(tourMember.getName());
        markerMemberPhone.setText(tourMember.getPhone());
        if (tourMember.getIsHost()) {
            markerHost.setText(R.string.host);
        } else {
            markerHost.setText(null);
        }

        dialogMemberInfoMarker.show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (mLocationPermisstionsGranted) {
            getDeviceLocation(true);

            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);

            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
//                    moveCamera(latLng, DEFAULT_ZOOM, null);
                    if (marker == null) {
                        markerOptions = new MarkerOptions();
                    } else {
                        marker.remove();
                    }
                    markerOptions.position(latLng);
                    marker = mMap.addMarker(markerOptions);

                    toggleEletemt();
                }
            });

            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    if (marker.getTag() != null) {
                        Log.d(TAG, "onMarkerClick: " + marker.getTag().getClass().getName());
                        if (marker.getTag().getClass().equals(TourMember.class)) {
                            TourMember tourMember = (TourMember) marker.getTag();
                            showMemberInfoMarker(tourMember);
                        } else {
                            StopPoint stopPoint = (StopPoint) marker.getTag();
                            showStopPointInfo(stopPoint);
                        }
                    }
                    return false;
                }
            });
//
//            if(myLocation!=null&&targetStopPoint!=null){
//                drawPolylineBetweenTwoLocation(new LatLng(myLocation.getLatitude(),myLocation.getLongitude()), new LatLng(targetStopPoint.getLatitude(),targetStopPoint.getLongitude()));
//            }
//            else{
//                Log.d(TAG, "init: NULL");
//            }
        }
    }


    private void toggleEletemt() {
        if (elementIsHiding) {
            btnShowListDestination.setVisibility(View.VISIBLE);
            btnShowNotificationList.setVisibility(View.VISIBLE);
            btnShowWarningSpeedNotificationDialog.setVisibility(View.VISIBLE);
            btnShowListMember.setVisibility(View.VISIBLE);
            elementIsHiding = false;
        } else {
            btnShowListDestination.setVisibility(View.GONE);
            btnShowNotificationList.setVisibility(View.GONE);
            btnShowWarningSpeedNotificationDialog.setVisibility(View.GONE);
            btnShowListMember.setVisibility(View.GONE);
            elementIsHiding = true;
        }
    }

    private void getDeviceLocation(boolean toMoveCamera) {
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
                                if (toMoveCamera) {
                                    moveCamera(new LatLng(curLocation.getLatitude(), curLocation.getLongitude()), DEFAULT_ZOOM, getString(R.string.map_my_location));
                                    myLocation = curLocation;
                                    getlocationFail = false;
                                }
                            } else {
                                Toast.makeText(context, R.string.map_failed_to_get_device_location, Toast.LENGTH_SHORT).show();
                                getlocationFail = true;
                            }
                        } else {
                            //khong the lay vi tri
                            Toast.makeText(context, R.string.map_failed_to_get_device_location, Toast.LENGTH_SHORT).show();
                            getlocationFail = true;
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
                locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000, 0, this);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 0, this);
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
//        markers.add(stopPointMarker);
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

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        switch (view.getId()) {
            case R.id.list_view_comment:
                int lastItem = firstVisibleItem + visibleItemCount;
                if (lastItem == totalItemCount) {
                    if (count.equals(pageSize) && !loading) {
                        loading = true;
                        progressBar.setVisibility(View.VISIBLE);
                        getTextNotifation();
                    }
                }
        }
    }

    private void drawPolylineBetweenTwoLocation(LatLng begin, LatLng end) {

        // drawing a straight line between the two points
        polyline = mMap.addPolyline(new PolylineOptions()
                .add(begin, end)
                .width(2)
                .color(Color.BLUE));
        // this point is halfway between Cleveland and San Jose
//        LatLng halfWay = new LatLng( (begin.latitude + end.latitude)/2,
//                (begin.longitude + end.longitude)/2 );
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(begin, DEFAULT_ZOOM));
    }

    public void drawRouteToStopPoint(StopPoint stopPoint) {
        if (polyline != null) {
            polyline.remove();
        }
        drawPolylineBetweenTwoLocation(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()), new LatLng(stopPoint.getLatitude(), stopPoint.getLongitude()));
    }

    private void addMarkerMember(TourMember tourMember, LatLng latLng) {
        if (marker != null) {
            marker.remove();
        }
        MarkerOptions memberMarkerOptions = new MarkerOptions();
        Marker memberMarker;
        if (tourMember.getAvatar() == null) {
            memberMarkerOptions.position(new LatLng(latLng.latitude, latLng.longitude))
                    .icon(bitmapDescriptorFromVector(getContext(), R.drawable.ic_none_avatar_user_marker))
                    .title(tourMember.getName());
            memberMarker = mMap.addMarker(memberMarkerOptions);
        } else {
            memberMarkerOptions.position(new LatLng(latLng.latitude, latLng.longitude))
                    .title(tourMember.getName());
            memberMarker = mMap.addMarker(memberMarkerOptions);
            picassoMarker = new PicassoMarker(memberMarker);

            Picasso.get().load(tourMember.getAvatar()).transform(transformation).into(picassoMarker);
        }
        memberMarker.setTag(tourMember);
        markerMemberHashMap.put(tourMember.getId(), memberMarker);
    }

    public void updateMemberLocation(FirebaseNotifyLocation firebaseNotifyLocation) {
        for (MemberLocation memberLocation : firebaseNotifyLocation.getMemPos()) {
            if (memberLocation.getId().equals(userId)) {
                continue;
            }
            if (markerMemberHashMap.containsKey(memberLocation.getId())) {
                ((Marker) markerMemberHashMap.get(memberLocation.getId())).setPosition(new LatLng(memberLocation.getLat(), memberLocation.getLong()));
            } else {

                for (TourMember member : tourInfo.getMembers()) {
                    if (member.getId().equals(memberLocation.getId())) {
                        addMarkerMember(member, new LatLng(memberLocation.getLat(), memberLocation.getLong()));
                        return;
                    }
                }
            }
        }
    }

    public void addMarkerSpeedLimit(NotificationOnRoad notification) {
        int drawableId = -1;
        if (notification.getSpeed() == 50) {
            drawableId = R.drawable.ic_speed_limit_50;
        } else if (notification.getSpeed() == 60) {
            drawableId = R.drawable.ic_speed_limit_60;
        } else if (notification.getSpeed() == 70) {
            drawableId = R.drawable.ic_speed_limit_70;
        }
        if (drawableId != -1) {
            MarkerOptions memberMarkerOptions = new MarkerOptions();
            memberMarkerOptions.position(new LatLng(notification.getLat(), notification.getLong()))
                    .icon(bitmapDescriptorFromVector(getContext(), drawableId))
                    .title("Limit " + notification.getSpeed());
            Marker memberMarker = mMap.addMarker(memberMarkerOptions);
        }
    }

    public void updateNotificationOnRoad(NotificationOnRoadList notificationOnRoadList) {
        if (notificationOnRoadList == null) {
            return;
        }
        for (NotificationOnRoad notification : notificationOnRoadList.getNotiList()) {
            if (notification.getNotificationType() == 3) {
                addMarkerSpeedLimit(notification);
            }
        }
    }

    public void updateNotificationText(TourNotificationText notiText){
        Notification notification = new Notification(notiText.getUserId(),"<ID :"+notiText.getUserId()+" >",null,notiText.getNotification());
        notificationList.add(notification);
        tourNotificationAdapter.notifyDataSetChanged();
    }

    public void updateNotificationLimitSpeed(TourNotificationLimitSpeed notificationLimitSpeed){
        NotificationOnRoad notification = new NotificationOnRoad(notificationLimitSpeed.getLat(),notificationLimitSpeed.getLong(),notificationLimitSpeed.getNote(),notificationLimitSpeed.getSpeed(),notificationLimitSpeed.getType());
        addMarkerSpeedLimit(notification);
    }

    @Override
    public void onLocationChanged(Location location) {
        myLocation = location;
//        Log.d(TAG, "onLocationChanged: "+location.getLatitude()+" , " + location.getLongitude());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onDestroy() {
        FirebaseMessaging.getInstance().unsubscribeFromTopic("tour-id-" + tourId);
        Intent serviceIntent = new Intent(context, BackgroundLocationService.class);
        context.stopService(serviceIntent);
        if (broadcastLocationReceiver != null) {
            context.unregisterReceiver(broadcastLocationReceiver);
            broadcastLocationReceiver = null;
        }
        super.onDestroy();
    }
}
