package com.example.tours;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import com.example.tours.ApiService.APIRetrofitCreator;
import com.example.tours.ApiService.APITour;
import com.example.tours.AppHelper.TokenStorage;
import com.example.tours.Model.TourInfo;
import com.example.tours.ui.main.TourCommentFragment;
import com.example.tours.ui.main.TourInfoFragment;
import com.example.tours.ui.main.TourMemberFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.widget.Toast;

import com.example.tours.ui.main.SectionsPagerAdapter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TourInfoActivity extends AppCompatActivity implements TourInfoFragment.OnFragmentInteractionListener,
                                                                    TourMemberFragment.OnFragmentInteractionListener ,
                                                                    TourCommentFragment.OnFragmentInteractionListener {

    private APITour apiTour;
    private Integer tourId=227;
    private static TourInfo tourInfo=null;
    private static boolean isHostUser=false;
    private ViewPager viewPager;
    private TabLayout tabs;
    private SectionsPagerAdapter sectionsPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour_info);
        apiTour = new APIRetrofitCreator().getAPIService();
        apiTour.getTourInfo(TokenStorage.getInstance().getAccessToken(), tourId).enqueue(new Callback<TourInfo>() {
            @Override
            public void onResponse(Call<TourInfo> call, Response<TourInfo> response) {
                if(response.isSuccessful()){
                    tourInfo=response.body();
                    if(TokenStorage.getInstance().getUserId()==Integer.parseInt(tourInfo.getHostId())){
                        isHostUser=true;
                    }
                    sectionsPagerAdapter = new SectionsPagerAdapter(TourInfoActivity.this, getSupportFragmentManager());
                    viewPager = findViewById(R.id.view_pager);
                    tabs = findViewById(R.id.tabs);
                    viewPager.setAdapter(sectionsPagerAdapter);
                    tabs.setupWithViewPager(viewPager);
                    setupTabIcons();
                }
                else{
                    Toast.makeText(TourInfoActivity.this, R.string.tour_not_exists, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TourInfo> call, Throwable t) {
                Toast.makeText(TourInfoActivity.this, R.string.failed_fetch_api, Toast.LENGTH_SHORT).show();
            }
        });


    }
    public TourInfo getTourInfo(){
        return tourInfo;
    }
    public boolean isHostUser(){
        return isHostUser;
    }
    private void setupTabIcons() {
        tabs.getTabAt(0).setIcon(R.drawable.ic_info_black_24dp);
        tabs.getTabAt(1).setIcon(R.drawable.ic_member_white_24dp);
        tabs.getTabAt(2).setIcon(R.drawable.ic_comment_black_24dp);
        tabs.setSelectedTabIndicatorColor(Color.parseColor("#FFFFFF"));
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}