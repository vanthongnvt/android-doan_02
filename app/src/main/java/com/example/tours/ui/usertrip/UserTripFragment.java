package com.example.tours.ui.usertrip;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;

import com.example.tours.Adapter.ListTourAdapter;
import com.example.tours.Adapter.UserListTourAdapter;
import com.example.tours.ApiService.APIRetrofitCreator;
import com.example.tours.ApiService.APITour;
import com.example.tours.AppHelper.TokenStorage;
import com.example.tours.Model.ListTour;
import com.example.tours.Model.Tour;
import com.example.tours.Model.TourInfo;
import com.example.tours.Model.UserListTour;
import com.example.tours.Model.UserTour;
import com.example.tours.R;
import com.example.tours.TourInfoActivity;
import com.example.tours.ui.usersettings.UserSettingsViewModel;

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
    private int numTotalTours;

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

        // goi api lan dau de lay total tours :
        apiTour.userListTour(TokenStorage.getInstance().getAccessToken(),1,1).enqueue(new Callback<UserListTour>() {
            @Override
            public void onResponse(Call<UserListTour> call, Response<UserListTour> response) {
                userlistTourResponse = response.body();
                numTotalTours = userlistTourResponse.getTotal();

                //goi api lan tiep theo de load danh sach:
                getUserTourList(container);
            }


            @Override
            public void onFailure(Call<UserListTour> call, Throwable t) {
                Toast.makeText(getActivity(), "Lỗi get total tours", Toast.LENGTH_SHORT).show();
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
                            userListTourAdapter.filter(s.toString());
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
            }

            @Override
            public void onFailure(Call<UserListTour> call, Throwable t) {
                Toast.makeText(getActivity(), R.string.failed_fetch_api, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
