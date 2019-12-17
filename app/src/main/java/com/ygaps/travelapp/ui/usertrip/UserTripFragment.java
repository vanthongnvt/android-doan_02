package com.ygaps.travelapp.ui.usertrip;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.ygaps.travelapp.Adapter.UserListTourAdapter;
import com.ygaps.travelapp.ApiService.APIRetrofitCreator;
import com.ygaps.travelapp.ApiService.APITour;
import com.ygaps.travelapp.AppHelper.TokenStorage;
import com.ygaps.travelapp.Model.GetStatusTours;
import com.ygaps.travelapp.Model.TotalToursGroupedBystatus;
import com.ygaps.travelapp.Model.UserListTour;
import com.ygaps.travelapp.Model.UserTour;
import com.ygaps.travelapp.R;
import com.ygaps.travelapp.TourInfoActivity;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserTripFragment extends Fragment {
    private UserTripViewModel userTripViewModel;
    private ListView listViewUserTour;
    private List<UserTour> tours;
    private UserListTourAdapter userListTourAdapter;
    private APITour apiTour;
    private UserListTour userlistTourResponse;
    private TextView totalTours;
    private EditText edtHomeSearch;
    private  TextView tvTotalCancelTours;
    private  TextView tvTotalOpenTours;
    private  TextView tvTotalStartedTours;
    private  TextView tvTotalClosedTours;
    private int numTotalTours;
    private TextView tvTourStatus;
    private ProgressBar progressBar;

    public static String EDIT_ID_TOUR = "EDIT ID TOUR";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        userTripViewModel =
                ViewModelProviders.of(this).get(UserTripViewModel.class);
        View view = inflater.inflate(R.layout.fragment_user_trip, container, false);
        apiTour =  new APIRetrofitCreator().getAPIService();
        listViewUserTour = view.findViewById(R.id.usertrip_listview_tour);
        totalTours = view.findViewById(R.id.usertrip_edt_totalTour);
        edtHomeSearch = view.findViewById(R.id.usertrip_home_search);
        tvTotalCancelTours = view.findViewById(R.id.tv_total_canceled_tour);
        tvTotalOpenTours = view.findViewById(R.id.tv_total_open_tour);
        tvTotalStartedTours = view.findViewById(R.id.tv_total_started_tour);
        tvTotalClosedTours = view.findViewById(R.id.tv_total_closed_tour);

        // goi api de lay cac status cua tours:
        setStatusTourText();

        // goi api lan dau de lay total tours :
        progressBar = view.findViewById(R.id.progressbar_loading);

        progressBar.setVisibility(View.VISIBLE);
        apiTour.userListTour(TokenStorage.getInstance().getAccessToken(),1,1).enqueue(new Callback<UserListTour>() {
            @Override
            public void onResponse(Call<UserListTour> call, Response<UserListTour> response) {
                userlistTourResponse = response.body();
                numTotalTours = userlistTourResponse.getTotal().intValue();

                //goi api lan tiep theo de load danh sach:
                getUserTourList(container);
            }


            @Override
            public void onFailure(Call<UserListTour> call, Throwable t) {
                Toast.makeText(getActivity(), "Lỗi get total tours", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
        return view;
    }

    private void getUserTourList(ViewGroup container) {
        apiTour.userListTour(TokenStorage.getInstance().getAccessToken(), 1,numTotalTours).enqueue(new Callback<UserListTour>() {
            @Override
            public void onResponse(Call<UserListTour> call, Response<UserListTour> response) {
                if(response.isSuccessful()){
                    userlistTourResponse= response.body();
                    totalTours.setText(numTotalTours + "");

                    tours = userlistTourResponse.getTours();
                    userListTourAdapter = new UserListTourAdapter(container.getContext(),R.layout.listview_usertrip_item,tours);
                    userListTourAdapter.notifyDataSetChanged();

                    listViewUserTour.setAdapter(userListTourAdapter);

                    // search:

                    edtHomeSearch.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            int totalSearch = userListTourAdapter.filter(s.toString());
                            totalTours.setText(totalSearch+"");
                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    });

                    listViewUserTour.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            UserTour tour = tours.get(position);
                            Intent intent = new Intent(view.getContext(), TourInfoActivity.class);
                            intent.putExtra("tourId",tour.getId());
                            startActivity(intent);
                        }
                    });
                }
                else{
                    Toast.makeText(getActivity(), R.string.failed_fetch_api, Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<UserListTour> call, Throwable t) {
                Toast.makeText(getActivity(), R.string.failed_fetch_api, Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    void setStatusTourText(){
        apiTour.getStatusTours(TokenStorage.getInstance().getAccessToken()).enqueue(new Callback<GetStatusTours>() {
            @Override
            public void onResponse(Call<GetStatusTours> call, Response<GetStatusTours> response) {
                if(response.isSuccessful()){
                    List<TotalToursGroupedBystatus> arr = response.body().getTotalToursGroupedByStatus();
                    Number canceled = arr.get(0).getTotal();
                    Number open = arr.get(1).getTotal();
                    Number started = arr.get(2).getTotal();
                    Number closed = arr.get(3).getTotal();

                    tvTotalCancelTours.setText(canceled +"");
                    tvTotalOpenTours.setText(open +"");
                    tvTotalStartedTours.setText(started +"");
                    tvTotalClosedTours.setText(closed +"");
                }
            }

            @Override
            public void onFailure(Call<GetStatusTours> call, Throwable t) {

            }
        });
    }
}
