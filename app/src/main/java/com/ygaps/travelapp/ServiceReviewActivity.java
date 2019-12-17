package com.ygaps.travelapp;

import androidx.appcompat.app.AppCompatActivity;

import com.ygaps.travelapp.ApiService.APIRetrofitCreator;
import com.ygaps.travelapp.ApiService.APITour;
import com.ygaps.travelapp.AppHelper.TokenStorage;
import com.ygaps.travelapp.Model.ListReviewPoint;
import com.ygaps.travelapp.Model.MessageResponse;
import com.ygaps.travelapp.Model.ReviewPoint;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.ygaps.travelapp.Adapter.ListStopPointAdapter;
import com.ygaps.travelapp.ApiService.APITour;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ServiceReviewActivity extends AppCompatActivity {
    private Integer stopPointID;

    RatingBar serviceRating;
    TextView tvShowFeedbackDialog;
    ListView lvFeedbackList;

    private APITour apiTour;

    private Dialog dialogAddReview;
    private RatingBar userRating;
    private EditText userReview;
    private Button btnSendReview;
//    private Toolbar toolbarReviewDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_review);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorCustomPrimary)));
        setTitle("Đánh giá điểm dừng");
        init();
        getServiceReviewPoint();
    }



    private void init(){
        Intent intent = getIntent();
        if(intent != null){
            stopPointID = (int)intent.getIntExtra(ListStopPointAdapter.STOPPOINT_ID, 0);
        }
        serviceRating = (RatingBar) findViewById(R.id.service_rating);
        tvShowFeedbackDialog = (TextView) findViewById(R.id.show_dialog_rate_service);
        lvFeedbackList = (ListView) findViewById(R.id.list_view_service_review);

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
                    int totalPoint =0,count=0,stat=0;
                    List<ReviewPoint> reviewPointList = response.body().getPointStats();
                    for (ReviewPoint reviewPoint : reviewPointList){
                        totalPoint += reviewPoint.getPoint()*reviewPoint.getTotal();
                        count += reviewPoint.getTotal();
                    }
                    if(count!=0){
                        stat = totalPoint/count;
                    }
                    serviceRating.setRating(stat);
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
