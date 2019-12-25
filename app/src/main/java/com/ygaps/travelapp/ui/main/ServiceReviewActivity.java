package com.ygaps.travelapp.ui.main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.taufiqrahman.reviewratings.BarLabels;
import com.taufiqrahman.reviewratings.RatingReviews;
import com.ygaps.travelapp.Adapter.ListServiceFeedbackAdapter;
import com.ygaps.travelapp.ApiService.APIRetrofitCreator;
import com.ygaps.travelapp.ApiService.APITour;
import com.ygaps.travelapp.AppHelper.TokenStorage;
import com.ygaps.travelapp.CreateStopPointActivity;
import com.ygaps.travelapp.Model.FeedbackService;
import com.ygaps.travelapp.Model.ListFeedbackService;
import com.ygaps.travelapp.Model.ListReviewPoint;
import com.ygaps.travelapp.Model.MessageResponse;
import com.ygaps.travelapp.Model.ReviewPoint;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ygaps.travelapp.Adapter.ListStopPointAdapter;
import com.ygaps.travelapp.Model.StopPoint;
import com.ygaps.travelapp.Model.TotalTourReview;
import com.ygaps.travelapp.R;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ServiceReviewActivity extends AppCompatActivity implements OnMapReadyCallback {
    private Integer stopPointID;
    private StopPoint stopPoint;

    private static final int ERROR_DIALOG_REQUEST = 9001;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUESET_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;
    private GoogleMap mMap;
    private Boolean mLocationPermisstionsGranted = false;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private MarkerOptions markerOptions = null;

    private TextView totalRating;
    private TextView ratingPoint;
    private RatingBar serviceRating;
    private TextView tvShowFeedbackDialog;
    private ListView lvFeedbackList;
    private RatingReviews ratingReviews;

    private APITour apiTour;
    private ListServiceFeedbackAdapter listFeedbackAdapter;
    private List<FeedbackService> feedbackList;

    private Dialog dialogAddReview;
    private RatingBar userRating;
    private EditText userReview;
    private Button btnSendReview;

    private static int maxPageSize = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_review);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorCustomPrimary)));
        setTitle("Đánh giá điểm dừng");
        int isOK = isServiceAvailable();

        if (isOK == 1) {
            getLocationPermission();
        }
        init();
        getFeedbackServiceList();
        getServiceReviewPoint();
    }

    private void getFeedbackServiceList() {
        apiTour.getListFeedbackService(TokenStorage.getInstance().getAccessToken(), stopPointID, 1, maxPageSize).enqueue(new Callback<ListFeedbackService>() {
            @Override
            public void onResponse(Call<ListFeedbackService> call, Response<ListFeedbackService> response) {
                if (response.isSuccessful()) {
                    feedbackList = response.body().getFeedbackList();
                    listFeedbackAdapter = new ListServiceFeedbackAdapter(ServiceReviewActivity.this, R.layout.list_view_feedback_service_item, feedbackList);
                    listFeedbackAdapter.notifyDataSetChanged();
                    lvFeedbackList.setAdapter(listFeedbackAdapter);
                } else if (response.code() == 500) {
                    Toast.makeText(ServiceReviewActivity.this, "Lỗi server, không thể hiển thị feedback list", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ListFeedbackService> call, Throwable t) {
                Toast.makeText(ServiceReviewActivity.this, R.string.failed_fetch_api, Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void init() {
        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            stopPointID = (int) bundle.getInt(ListStopPointAdapter.STOPPOINT_ID, 0);
            stopPoint = (StopPoint) bundle.getSerializable("STOP_POINT");
        }
        lvFeedbackList = (ListView) findViewById(R.id.list_view_service_review);
        View headerReviewPoint = getLayoutInflater().inflate(R.layout.header_for_list_view_feedback_service, lvFeedbackList, false);
        lvFeedbackList.addHeaderView(headerReviewPoint);
        serviceRating = (RatingBar) headerReviewPoint.findViewById(R.id.service_ratingBar);
        tvShowFeedbackDialog = (TextView) headerReviewPoint.findViewById(R.id.show_dialog_rate_service);

        ratingPoint = headerReviewPoint.findViewById(R.id.service_rating_point);
        totalRating = headerReviewPoint.findViewById(R.id.service_total_ratings);
        ratingReviews = (RatingReviews) headerReviewPoint.findViewById(R.id.rating_reviews);

        apiTour = new APIRetrofitCreator().getAPIService();

        initDialogAddReview();

        tvShowFeedbackDialog.setOnClickListener(v -> {
            dialogAddReview.show();
        });
    }

    private void initDialogAddReview() {
        dialogAddReview = new Dialog(ServiceReviewActivity.this, R.style.DialogSlideAnimation);
        dialogAddReview.setContentView(R.layout.dialog_review_tour);

        userRating = dialogAddReview.findViewById(R.id.user_rating);
        userReview = dialogAddReview.findViewById(R.id.user_review);
        btnSendReview = dialogAddReview.findViewById(R.id.send_review);

//        toolbarReviewDialog = dialogAddReview.findViewById(R.id.toolbar_review);
//        toolbarReviewDialog.setTitle(R.string.tool_bar_review_stop_point_title);

        btnSendReview.setOnClickListener(v -> {
            int rating = (int) userRating.getRating();
            String review = userReview.getText().toString();
            if (rating == 0) {
                Toast.makeText(ServiceReviewActivity.this, R.string.empty_point_review, Toast.LENGTH_SHORT).show();
                return;
            }
            if (review.isEmpty()) {
                Toast.makeText(ServiceReviewActivity.this, R.string.empty_review, Toast.LENGTH_SHORT).show();
                return;
            }

            apiTour.addServiceReview(TokenStorage.getInstance().getAccessToken(), stopPointID, review, rating).enqueue(new Callback<MessageResponse>() {
                @Override
                public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                    if (response.isSuccessful()) {
                        userRating.setRating(0);
                        userReview.setText(null);
                        FeedbackService feedbackService = new FeedbackService();
                        feedbackService.setAvatar(TokenStorage.getInstance().getAvatar());
                        feedbackService.setName(TokenStorage.getInstance().getName());
                        feedbackService.setPoint(rating);
                        feedbackService.setFeedback(review);
                        feedbackService.setCreatedOn(String.valueOf(System.currentTimeMillis()));
                        feedbackList.add(0, feedbackService);
                        listFeedbackAdapter.notifyDataSetChanged();
                        Toast.makeText(ServiceReviewActivity.this, R.string.add_review_successfully, Toast.LENGTH_SHORT).show();
                        dialogAddReview.dismiss();
                    } else {
                        Toast.makeText(ServiceReviewActivity.this, R.string.server_err, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<MessageResponse> call, Throwable t) {
                    Toast.makeText(ServiceReviewActivity.this, R.string.failed_fetch_api, Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void getServiceReviewPoint() {
        apiTour.serviceReviewPoint(TokenStorage.getInstance().getAccessToken(), stopPointID).enqueue(new Callback<ListReviewPoint>() {
            @Override
            public void onResponse(Call<ListReviewPoint> call, Response<ListReviewPoint> response) {

                if (response.isSuccessful()) {
                    int totalPoint = 0;
                    int count = 0;
                    double stat = 0;
                    List<ReviewPoint> reviewPointList = response.body().getPointStats();
                    for (ReviewPoint reviewPoint : reviewPointList) {
                        totalPoint += reviewPoint.getPoint() * reviewPoint.getTotal();
                        count += reviewPoint.getTotal();
                    }
                    if (count != 0) {
                        stat = totalPoint / (count * 1.0);
                        stat = Math.round(stat * 10) / 10.0;
                    }
                    totalRating.setText(String.valueOf(count));
                    ratingPoint.setText(String.valueOf(stat));
                    serviceRating.setRating((int) stat);
                    int colors[] = new int[]{
                            Color.parseColor("#0e9d58"),
                            Color.parseColor("#bfd047"),
                            Color.parseColor("#ffc105"),
                            Color.parseColor("#ef7e14"),
                            Color.parseColor("#d36259")};

                    int raters[] = new int[]{
                            reviewPointList.get(4).getTotal(),
                            reviewPointList.get(3).getTotal(),
                            reviewPointList.get(2).getTotal(),
                            reviewPointList.get(1).getTotal(),
                            reviewPointList.get(0).getTotal()
                    };

                    ratingReviews.createRatingBars(100, BarLabels.STYPE1, colors, raters);
                } else {
                    Toast.makeText(ServiceReviewActivity.this, R.string.server_err, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ListReviewPoint> call, Throwable t) {
                Toast.makeText(ServiceReviewActivity.this, R.string.failed_fetch_api, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        markerOptions = new MarkerOptions();
        if (mLocationPermisstionsGranted) {
            mMap.setMyLocationEnabled(true);
//            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            LatLng latLng = new LatLng(stopPoint.getLatitude(), stopPoint.getLongitude());
            markerOptions.position(latLng)
                    .icon(bitmapDescriptorFromVector(ServiceReviewActivity.this, R.drawable.ic_pin))
                    .title(stopPoint.getName());
            Marker stopPointMarker = mMap.addMarker(markerOptions);
            stopPointMarker.setTag(stopPoint);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));

        }
    }

    private int isServiceAvailable() {
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(ServiceReviewActivity.this);
        if (available == ConnectionResult.SUCCESS) {
            return 1;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(ServiceReviewActivity.this, available, ERROR_DIALOG_REQUEST);
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
                supportMapFragment.getMapAsync(ServiceReviewActivity.this);
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
                    supportMapFragment.getMapAsync(ServiceReviewActivity.this);
                }
            }
        }
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}
