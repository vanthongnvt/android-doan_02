package com.ygaps.travelapp.ui.explore;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.ygaps.travelapp.Adapter.ListStopPointAdapter;
import com.ygaps.travelapp.ApiService.APIRetrofitCreator;
import com.ygaps.travelapp.ApiService.APITour;
import com.ygaps.travelapp.AppHelper.TokenStorage;
import com.ygaps.travelapp.Model.ListSuggestedStopPoint;
import com.ygaps.travelapp.Model.StopPoint;
import com.ygaps.travelapp.R;
import com.ygaps.travelapp.TourInfoActivity;
import com.ygaps.travelapp.ui.home.HomeViewModel;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExploreFragment extends Fragment implements AbsListView.OnScrollListener{

    private HomeViewModel homeViewModel;
    private ListView listViewStopPoint;
    private List<StopPoint> stopPointList = new ArrayList<>();
    private ListStopPointAdapter stopPointAdapter;
    private APITour apiTour;
    private TextView totalStopPoint;
    private EditText edtHomeSearch;
    private ProgressBar progressBar;
    private Integer pageSize=10;
    private Integer pageIndex =1;
    private Boolean loading=false;
    private Integer total=-1;
    private String keySearch = null;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             final ViewGroup container, Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_explore_stop_point, container, false);
        apiTour =  new APIRetrofitCreator().getAPIService();
        listViewStopPoint = view.findViewById(R.id.list_view_stop_point);
        totalStopPoint = view.findViewById(R.id.edt_totalTour);
        edtHomeSearch = view.findViewById(R.id.home_search);
        progressBar = view.findViewById(R.id.progressbar_loading);
        stopPointAdapter = new ListStopPointAdapter(container.getContext(),R.layout.list_view_tour_stop_point_item,stopPointList);
        listViewStopPoint.setAdapter(stopPointAdapter);
        listViewStopPoint.setOnScrollListener(this);
        // search:

        edtHomeSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String key = edtHomeSearch.getText().toString().trim();
                if(!key.isEmpty()){
                    keySearch= key;
                    search(key,true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        listViewStopPoint.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                StopPoint stopPoint = stopPointList.get(position);
                Intent intent = new Intent(view.getContext(), TourInfoActivity.class);
                intent.putExtra("STOPPOINT_ID",stopPoint.getServiceId());
                startActivity(intent);
            }
        });

        return view;
    }

//    private void getList(ViewGroup container) {
//        apiTour.listTour(TokenStorage.getInstance().getAccessToken(), numTotalTours,1,null,null).enqueue(new Callback<ListTour>() {
//            @Override
//            public void onResponse(Call<ListTour> call, Response<ListTour> response) {
//                if(response.isSuccessful()){
////                    listTourResponse= response.body();
//                    totalTours.setText(numTotalTours + "");
//
////                    tours = listTourResponse.getTours();
//                    listTourAdapter = new ListTourAdapter(container.getContext(),R.layout.listview_tour_item,tours);
//                    listTourAdapter.notifyDataSetChanged();
//
//                    listViewTour.setAdapter(listTourAdapter);
//                    listViewTour.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                        @Override
//                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                            Tour tour = tours.get(position);
//                            Intent intent = new Intent(view.getContext(), TourInfoActivity.class);
//                            intent.putExtra("tourId",tour.getId());
//                            startActivity(intent);
//                        }
//                    });
//
//
//                    progressBar.setVisibility(View.GONE);
//                }
//                else{
//                    Toast.makeText(getActivity(), R.string.failed_fetch_api, Toast.LENGTH_SHORT).show();
//                    progressBar.setVisibility(View.GONE);
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ListTour> call, Throwable t) {
//                Toast.makeText(getActivity(), R.string.failed_fetch_api, Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

    private void search(String key,boolean isNewKey) {
        loading=true;
        progressBar.setVisibility(View.VISIBLE);
        apiTour.searchService(TokenStorage.getInstance().getAccessToken(),key,pageSize,pageIndex).enqueue(new Callback<ListSuggestedStopPoint>() {
            @Override
            public void onResponse(Call<ListSuggestedStopPoint> call, Response<ListSuggestedStopPoint> response) {
                if(response.isSuccessful()){
                    if(isNewKey) {
                        stopPointList.clear();
                        stopPointList.addAll(response.body().getStopPoints());
                    }
                    else {
                        stopPointList.addAll(response.body().getStopPoints());
                        pageIndex++;
                    }
                    if(pageIndex==1) {
                        total = response.body().getTotal();
                        totalStopPoint.setText(String.valueOf(total));
                    }
                    stopPointAdapter.notifyDataSetChanged();
                    loading=false;
                    progressBar.setVisibility(View.GONE);
                }
                else{
                    Toast.makeText(getActivity(), R.string.failed_fetch_api, Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<ListSuggestedStopPoint> call, Throwable t) {
                Toast.makeText(getActivity(), R.string.failed_fetch_api, Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        switch(view.getId())
        {
            case R.id.list_view_stop_point:

                int lastItem = firstVisibleItem + visibleItemCount;
                if(lastItem == totalItemCount)
                {
                    if(total!= -1 && pageIndex*pageSize - total < pageSize&&!loading) {
                        loading=true;

                        search(keySearch,false);
                    }
                }
        }
    }
}
