package com.example.tours.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.tours.Adapter.ListTourAdapter;
import com.example.tours.ApiService.APIRetrofitCreator;
import com.example.tours.ApiService.APITour;
import com.example.tours.HomeActivity;
import com.example.tours.MainActivity;
import com.example.tours.Model.Auth;
import com.example.tours.Model.ListTour;
import com.example.tours.Model.Tour;
import com.example.tours.R;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private ListView listViewTour;
    private APITour apiTour;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             final ViewGroup container, Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        apiTour =  new APIRetrofitCreator().getAPIService();
        Intent itent=getActivity().getIntent();
        Auth auth= (Auth) itent.getSerializableExtra("Auth");
        apiTour.listTour(auth.getToken(),10,1,null,null).enqueue(new Callback<ListTour>() {
            @Override
            public void onResponse(Call<ListTour> call, Response<ListTour> response) {
                if(response.isSuccessful()){
                    ListTour listTourResponse= response.body();
                    TextView totalTours = getView().findViewById(R.id.edt_totalTour);
                    totalTours.setText(listTourResponse.getTotal() + " trips");
                    List<Tour> tours = listTourResponse.getTours();
                    listViewTour = getView().findViewById(R.id.listview_tour);

                    ListTourAdapter listTourAdapter = new ListTourAdapter(container.getContext(),R.layout.listview_tour_item
                            ,tours);
                    listTourAdapter.notifyDataSetChanged();

                    listViewTour.setAdapter(listTourAdapter);

                }
                else{
                    Toast.makeText(getActivity(), R.string.failed_fetch_api, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ListTour> call, Throwable t) {
                Toast.makeText(getActivity(), R.string.failed_fetch_api, Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}