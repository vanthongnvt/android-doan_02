package com.ygaps.travelapp.ui.main;

import androidx.appcompat.app.AppCompatActivity;

import com.taufiqrahman.reviewratings.BarLabels;
import com.taufiqrahman.reviewratings.RatingReviews;
import com.ygaps.travelapp.Adapter.ListServiceFeedbackAdapter;
import com.ygaps.travelapp.ApiService.APIRetrofitCreator;
import com.ygaps.travelapp.ApiService.APITour;
import com.ygaps.travelapp.AppHelper.TokenStorage;
import com.ygaps.travelapp.Model.FeedbackService;
import com.ygaps.travelapp.Model.ListFeedbackService;
import com.ygaps.travelapp.Model.ListReviewPoint;
import com.ygaps.travelapp.Model.MessageResponse;
import com.ygaps.travelapp.Model.ReviewPoint;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ygaps.travelapp.Adapter.ListStopPointAdapter;
import com.ygaps.travelapp.Model.TotalTourReview;
import com.ygaps.travelapp.R;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ServiceReviewActivity extends AppCompatActivity {
    private Integer stopPointID;

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
        init();
        getFeedbackServiceList();
        getServiceReviewPoint();
    }

    private void getFeedbackServiceList() {
        apiTour.getListFeedbackService(TokenStorage.getInstance().getAccessToken(), stopPointID, 1, maxPageSize).enqueue(new Callback<ListFeedbackService>() {
            @Override
            public void onResponse(Call<ListFeedbackService> call, Response<ListFeedbackService> response) {
                if(response.isSuccessful()){
                    feedbackList = response.body().getFeedbackList();
                    listFeedbackAdapter = new ListServiceFeedbackAdapter(ServiceReviewActivity.this, R.layout.list_view_feedback_service_item, feedbackList);
                    listFeedbackAdapter.notifyDataSetChanged();
                    lvFeedbackList.setAdapter(listFeedbackAdapter);
                }
                else if(response.code() == 500){
                    Toast.makeText(ServiceReviewActivity.this, "Lỗi server, không thể hiển thị feedback list", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ListFeedbackService> call, Throwable t) {
                Toast.makeText(ServiceReviewActivity.this, R.string.failed_fetch_api, Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void init(){
        Intent intent = getIntent();
        if(intent != null){
            stopPointID = (int)intent.getIntExtra(ListStopPointAdapter.STOPPOINT_ID, 0);
        }
        serviceRating = (RatingBar) findViewById(R.id.service_ratingBar);
        tvShowFeedbackDialog = (TextView) findViewById(R.id.show_dialog_rate_service);
        lvFeedbackList = (ListView) findViewById(R.id.list_view_service_review);
        ratingPoint = findViewById(R.id.service_rating_point);
        totalRating = findViewById(R.id.service_total_ratings);
        ratingReviews = (RatingReviews) findViewById(R.id.rating_reviews);

        apiTour = new APIRetrofitCreator().getAPIService();

        initDialogAddReview();

        tvShowFeedbackDialog.setOnClickListener(v -> {
            dialogAddReview.show();
        });
    }

    private void initDialogAddReview() {
        dialogAddReview = new Dialog(ServiceReviewActivity.this,R.style.DialogSlideAnimation);
        dialogAddReview.setContentView(R.layout.dialog_review_tour);

        userRating=dialogAddReview.findViewById(R.id.user_rating);
        userReview = dialogAddReview.findViewById(R.id.user_review);
        btnSendReview = dialogAddReview.findViewById(R.id.send_review);

//        toolbarReviewDialog = dialogAddReview.findViewById(R.id.toolbar_review);
//        toolbarReviewDialog.setTitle(R.string.tool_bar_review_stop_point_title);

        btnSendReview.setOnClickListener(v -> {
            int rating = (int)userRating.getRating();
            String review = userReview.getText().toString();
            if(rating==0){
                Toast.makeText(ServiceReviewActivity.this, R.string.empty_point_review, Toast.LENGTH_SHORT).show();
                return;
            }
            if(review.isEmpty()){
                Toast.makeText(ServiceReviewActivity.this, R.string.empty_review, Toast.LENGTH_SHORT).show();
                return;
            }

            apiTour.addServiceReview(TokenStorage.getInstance().getAccessToken(),stopPointID,review, rating).enqueue(new Callback<MessageResponse>() {
                @Override
                public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                    if(response.isSuccessful()){
                        userRating.setRating(0);
                        userReview.setText(null);
                        Toast.makeText(ServiceReviewActivity.this, R.string.add_review_successfully, Toast.LENGTH_SHORT).show();
                        dialogAddReview.dismiss();
                    }
                    else{
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

    private void getServiceReviewPoint(){
        apiTour.serviceReviewPoint(TokenStorage.getInstance().getAccessToken(),stopPointID).enqueue(new Callback<ListReviewPoint>() {
            @Override
            public void onResponse(Call<ListReviewPoint> call, Response<ListReviewPoint> response) {

                if(response.isSuccessful()){
                    int totalPoint =0;
                    int count=0;
                    double stat=0;
                    List<ReviewPoint> reviewPointList = response.body().getPointStats();
                    for (ReviewPoint reviewPoint : reviewPointList){
                        totalPoint += reviewPoint.getPoint()*reviewPoint.getTotal();
                        count += reviewPoint.getTotal();
                    }
                    if(count!=0){
                        stat = totalPoint/(count*1.0);
                        stat = Math.round(stat*10)/10.0;
                    }
                    totalRating.setText(String.valueOf(count));
                    ratingPoint.setText(String.valueOf(stat));
                    serviceRating.setRating((int)stat);
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
                }
                else{
                    Toast.makeText(ServiceReviewActivity.this, R.string.server_err, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ListReviewPoint> call, Throwable t) {
                Toast.makeText(ServiceReviewActivity.this, R.string.failed_fetch_api, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
