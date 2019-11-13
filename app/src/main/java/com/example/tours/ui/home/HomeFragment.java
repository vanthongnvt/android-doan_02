package com.example.tours.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.example.tours.AppHelper.TokenStorage;
import com.example.tours.CreateTourActivity;
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
    private List<Tour> tours;
    private ListTourAdapter listTourAdapter;
    private APITour apiTour;
    private ListTour listTourResponse;
    private TextView totalTours;
    private EditText edtHomeSearch;
    private ImageView btnAddTour;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             final ViewGroup container, Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        apiTour =  new APIRetrofitCreator().getAPIService();
        listViewTour = view.findViewById(R.id.listview_tour);
        totalTours = view.findViewById(R.id.edt_totalTour);
        edtHomeSearch = view.findViewById(R.id.home_search);
        btnAddTour = view.findViewById(R.id.btn_add_tour);

//        Intent itent=getActivity().getIntent();
//        final Auth auth= (Auth) itent.getSerializableExtra("Auth");

        String strtotalTours = totalTours.getText().toString();
        Integer numTotalTours = Integer.parseInt(strtotalTours);
        apiTour.listTour(TokenStorage.getInstance().getAccessToken(),numTotalTours,1,null,null).enqueue(new Callback<ListTour>() {
            @Override
            public void onResponse(Call<ListTour> call, Response<ListTour> response) {
                if(response.isSuccessful()){
                    listTourResponse= response.body();

                    totalTours.setText(listTourResponse.getTotal().toString());

                    tours = listTourResponse.getTours();


                    listTourAdapter = new ListTourAdapter(container.getContext(),R.layout.listview_tour_item,tours);
                    listTourAdapter.notifyDataSetChanged();

                    listViewTour.setAdapter(listTourAdapter);

                    // search:

                    edtHomeSearch.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            listTourAdapter.filter(s.toString());
                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    });
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

        btnAddTour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CreateTourActivity.class);
//                intent.putExtra("Auth", auth);
                startActivity(intent);
            }
        });

        return view;
    }



}
