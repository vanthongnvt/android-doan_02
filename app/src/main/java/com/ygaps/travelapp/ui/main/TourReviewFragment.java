package com.ygaps.travelapp.ui.main;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.taufiqrahman.reviewratings.BarLabels;
import com.taufiqrahman.reviewratings.RatingReviews;
import com.ygaps.travelapp.Adapter.ListTourReviewAdapter;
import com.ygaps.travelapp.ApiService.APIRetrofitCreator;
import com.ygaps.travelapp.ApiService.APITour;
import com.ygaps.travelapp.AppHelper.TokenStorage;
import com.ygaps.travelapp.Model.ListReviewPoint;
import com.ygaps.travelapp.Model.MessageResponse;
import com.ygaps.travelapp.Model.ReviewPoint;
import com.ygaps.travelapp.Model.TotalTourReview;
import com.ygaps.travelapp.Model.TourInfo;
import com.ygaps.travelapp.Model.TourReview;
import com.ygaps.travelapp.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TourReviewFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TourReviewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TourReviewFragment extends Fragment {
    private static final String ARG_PARAM1 = "tour";

    private TourInfo tourInfo;
    private boolean isHostUser;

    private OnFragmentInteractionListener mListener;

    private APITour apiTour;
    private ListTourReviewAdapter listReviewApdater;
    private List<TourReview> listTourReview;

    private RatingBar tourRatingBar;
    private TextView ratingPoint;
    private TextView totalRatings;
    private TextView tvShowDialogRating;
    private ListView listViewRating;
    private RatingReviews ratingReviews;

    private Dialog dialogAddReview;
    private RatingBar userRating;
    private EditText userReview;
    private Button btnSendReview;
    private View root;

    private static int maxPageSize = 1000;

    public TourReviewFragment() {
        // Required empty public constructor
    }

    public static TourReviewFragment newInstance(TourInfo tourInfo, boolean isHostUser) {
        TourReviewFragment fragment = new TourReviewFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, tourInfo);
        args.putBoolean("isHostUser",isHostUser);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            tourInfo = (TourInfo) getArguments().getSerializable(ARG_PARAM1);
            isHostUser=getArguments().getBoolean("isHostUser");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_tour_review, container, false);
        this.root= root;
        listViewRating = root.findViewById(R.id.list_view_tour_review);
        View headerReviewPoint = inflater.inflate(R.layout.header_for_list_view_tour_review, listViewRating, false);
        listViewRating.addHeaderView(headerReviewPoint);
        init(headerReviewPoint);
        return root;
    }

    private void init(View headerReviewPoint) {


        tourRatingBar = headerReviewPoint.findViewById(R.id.ratingBar);
        tvShowDialogRating = headerReviewPoint.findViewById(R.id.show_dialog_rate_tour);

        ratingPoint = headerReviewPoint.findViewById(R.id.rating_point);
        totalRatings = headerReviewPoint.findViewById(R.id.total_ratings);

        ratingReviews = (RatingReviews) headerReviewPoint.findViewById(R.id.rating_reviews);


        apiTour = new APIRetrofitCreator().getAPIService();

        getReviewPoint();
        getReviewList();

        initDialogAddReview(root);

        tvShowDialogRating.setOnClickListener(v -> {
            dialogAddReview.show();
        });



    }

    private void initDialogAddReview(View root){
        dialogAddReview = new Dialog(root.getContext(),R.style.DialogSlideAnimation);
        dialogAddReview.setContentView(R.layout.dialog_review_tour);

        userRating=dialogAddReview.findViewById(R.id.user_rating);
        userReview = dialogAddReview.findViewById(R.id.user_review);
        btnSendReview = dialogAddReview.findViewById(R.id.send_review);

        btnSendReview.setOnClickListener(v -> {
            int rating = (int)userRating.getRating();
            String review = userReview.getText().toString();
            if(rating==0){
                Toast.makeText(getContext(), R.string.empty_point_review, Toast.LENGTH_SHORT).show();
                return;
            }
            if(review.isEmpty()){
                Toast.makeText(getContext(), R.string.empty_review, Toast.LENGTH_SHORT).show();
                return;
            }

            apiTour.addReview(TokenStorage.getInstance().getAccessToken(),tourInfo.getId(),rating,review).enqueue(new Callback<MessageResponse>() {
                @Override
                public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                    if(response.isSuccessful()){
                        userRating.setRating(0);
                        userReview.setText(null);
                        TourReview review1 = new TourReview();
                        review1.setAvatar(TokenStorage.getInstance().getAvatar());
                        review1.setName(TokenStorage.getInstance().getName());
                        review1.setPoint(rating);
                        review1.setReview(review);
                        review1.setCreatedOn(String.valueOf(System.currentTimeMillis()));
                        listTourReview.add(0,review1);
                        listReviewApdater.notifyDataSetChanged();
                        Toast.makeText(getContext(), R.string.add_review_successfully, Toast.LENGTH_SHORT).show();
                        dialogAddReview.dismiss();
                    }
                    else{
                        Toast.makeText(getContext(), R.string.server_err, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<MessageResponse> call, Throwable t) {
                    Toast.makeText(getContext(), R.string.failed_fetch_api, Toast.LENGTH_SHORT).show();
                }
            });
        });
    }


    private void getReviewPoint(){
        apiTour.reviewPoint(TokenStorage.getInstance().getAccessToken(), tourInfo.getId()).enqueue(new Callback<ListReviewPoint>() {
            @Override
            public void onResponse(Call<ListReviewPoint> call, Response<ListReviewPoint> response) {

                if(response.isSuccessful()){
                    int totalPoint =0,count=0;
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
                    totalRatings.setText(String.valueOf(count));
                    ratingPoint.setText(String.valueOf(stat));
                    tourRatingBar.setRating((int)stat);
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
                    Toast.makeText(getContext(), R.string.server_err, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ListReviewPoint> call, Throwable t) {
                Toast.makeText(getContext(), R.string.failed_fetch_api, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getReviewList() {
        apiTour.getTotalTourReview(TokenStorage.getInstance().getAccessToken(), tourInfo.getId(), 1, 1000).enqueue(new Callback<TotalTourReview>() {
            @Override
            public void onResponse(Call<TotalTourReview> call, Response<TotalTourReview> response) {
                if (response.isSuccessful()) {

                    listTourReview = response.body().getReviewList();
                    listReviewApdater = new ListTourReviewAdapter(getContext(), R.layout.list_view_tour_review_item, listTourReview);
                    listViewRating.setAdapter(listReviewApdater);
                    listReviewApdater.notifyDataSetChanged();
//                    setListViewHeightBasedOnChildren(listViewRating);

                } else if (response.code() == 500) {
                    Toast.makeText(getContext(), "Lỗi server, không thể hiển thị danh sách tour review", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TotalTourReview> call, Throwable t) {
                Toast.makeText(getContext(), R.string.failed_fetch_api, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
