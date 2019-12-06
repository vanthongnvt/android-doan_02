package com.example.tours.ui.main;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tours.ApiService.APIRetrofitCreator;
import com.example.tours.ApiService.APITour;
import com.example.tours.AppHelper.TokenStorage;
import com.example.tours.Model.ListReviewPoint;
import com.example.tours.Model.MessageResponse;
import com.example.tours.Model.ReviewPoint;
import com.example.tours.Model.TourInfo;
import com.example.tours.R;

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

    private RatingBar tourRatingBar;
    private TextView tvShowDialogRating;
    private ListView listViewRating;

    private Dialog dialogAddReview;
    private RatingBar userRating;
    private EditText userReview;
    private Button btnSendReview;

    private boolean hasLoadedReview=false;

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

        init(root);

        return root;
    }

    private void init(View root) {
        tourRatingBar = root.findViewById(R.id.tour_rating);
        tvShowDialogRating = root.findViewById(R.id.show_dialog_rate_tour);
        listViewRating = root.findViewById(R.id.list_view_tour_review);

        apiTour = new APIRetrofitCreator().getAPIService();


        initDialogAddReview(root);

        tvShowDialogRating.setOnClickListener(v -> {
            dialogAddReview.show();
        });

    }

    private void initDialogAddReview(View root){
        dialogAddReview = new Dialog(root.getContext(),R.style.PlacesAutocompleteThemeFullscreen);
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

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser&&!hasLoadedReview){
           getReviewPoint();
           getReviewList();
           hasLoadedReview=true;
        }
    }

    private void getReviewPoint(){
        apiTour.reviewPoint(TokenStorage.getInstance().getAccessToken(),tourInfo.getId()).enqueue(new Callback<ListReviewPoint>() {
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
                    tourRatingBar.setRating(stat);
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

    private void getReviewList(){

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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
