package com.example.tours;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import com.example.tours.Adapter.ListTourCommentAdapter;
import com.example.tours.ApiService.APIRetrofitCreator;
import com.example.tours.ApiService.APITour;
import com.example.tours.AppHelper.TokenStorage;
import com.example.tours.Model.CommentList;
import com.example.tours.Model.MessageResponse;
import com.example.tours.Model.TourComment;
import com.example.tours.Model.TourInfo;
import com.example.tours.Model.TourMember;
import com.example.tours.ui.main.TourReviewFragment;
import com.example.tours.ui.main.TourInfoFragment;
import com.example.tours.ui.main.TourMemberFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.example.tours.ui.main.SectionsPagerAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TourInfoActivity extends AppCompatActivity implements TourInfoFragment.OnFragmentInteractionListener,
        TourMemberFragment.OnFragmentInteractionListener,
        TourReviewFragment.OnFragmentInteractionListener, AbsListView.OnScrollListener {

    private APITour apiTour;
    private Integer tourId = 227;
    private static TourInfo tourInfo = null;
    private Integer userId;
    private static boolean isHostUser = false;
    private ViewPager viewPager;
    private TabLayout tabs;
    private SectionsPagerAdapter sectionsPagerAdapter;
    private FloatingActionButton btnShowComment;
    private boolean firstClick = true;

    private Dialog dialogComment;
    private EditText inputComment;
    private ImageView btnSendComment;
    private ListView listViewComment;
    private ListTourCommentAdapter tourCommentAdapter;
    private List<TourComment> tourCommentList = new ArrayList<>();
    private Toolbar toolbar;
    private ProgressBar progressBar;

    private Integer pageIndex = 1;
    private boolean loading = false;
    private Integer count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour_info);
        tourId = getIntent().getExtras().getInt("tourId");
        userId = TokenStorage.getInstance().getUserId();
//        Log.d("TOUR_INFO", "onCreate: tourId"+tourId);
        apiTour = new APIRetrofitCreator().getAPIService();
        apiTour.getTourInfo(TokenStorage.getInstance().getAccessToken(), tourId).enqueue(new Callback<TourInfo>() {
            @Override
            public void onResponse(Call<TourInfo> call, Response<TourInfo> response) {
                if (response.isSuccessful()) {
                    tourInfo = response.body();
                    if (userId == Integer.parseInt(tourInfo.getHostId())) {
                        isHostUser = true;
                    } else {
                        isHostUser = false;
                    }
                    init();
                } else {
                    Toast.makeText(TourInfoActivity.this, R.string.tour_not_exists, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TourInfo> call, Throwable t) {
                Toast.makeText(TourInfoActivity.this, R.string.failed_fetch_api, Toast.LENGTH_SHORT).show();
            }
        });


    }

    @SuppressLint("RestrictedApi")
    private void init() {
        sectionsPagerAdapter = new SectionsPagerAdapter(TourInfoActivity.this, getSupportFragmentManager());
        viewPager = findViewById(R.id.view_pager);
        tabs = findViewById(R.id.tabs);
        viewPager.setAdapter(sectionsPagerAdapter);
        tabs.setupWithViewPager(viewPager);
        setupTabIcons();

        btnShowComment = findViewById(R.id.fab_comment);

        if(isMemberOfTour()) {
            btnShowComment.setVisibility(View.VISIBLE);
            btnShowComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (firstClick) {
                        initDialogComment();
                        firstClick = false;
                        getComment();
                    }
                    dialogComment.show();
                }
            });
        }
    }

    private void initDialogComment() {
        dialogComment = new Dialog(TourInfoActivity.this, R.style.PlacesAutocompleteThemeFullscreen);
        dialogComment.setContentView(R.layout.dialog_comment_tour_info);

        btnSendComment = dialogComment.findViewById(R.id.send_comment);
        inputComment = dialogComment.findViewById(R.id.input_comment);
        listViewComment = dialogComment.findViewById(R.id.list_view_comment);
        tourCommentAdapter = new ListTourCommentAdapter(TourInfoActivity.this, R.layout.list_view_tour_comment_item, tourCommentList);
        listViewComment.setAdapter(tourCommentAdapter);
        listViewComment.setOnScrollListener(this);

        toolbar = dialogComment.findViewById(R.id.toolbar_comment);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        progressBar = dialogComment.findViewById(R.id.progressbar_loading);

        btnSendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment = inputComment.getText().toString();
                if (!comment.isEmpty()) {
                    inputComment.setText("");
                    sendComment(comment);
                }
            }
        });
    }

    private void getComment() {
        apiTour.getComment(TokenStorage.getInstance().getAccessToken(), tourInfo.getId(), pageIndex, 10).enqueue(new Callback<CommentList>() {
            @Override
            public void onResponse(Call<CommentList> call, Response<CommentList> response) {
                if (response.isSuccessful()) {
                    count = response.body().getCommentList().size();
                    tourCommentList.addAll(response.body().getCommentList());
                    loading = false;
                    pageIndex++;
                    progressBar.setVisibility(View.GONE);
                } else {
                    Toast.makeText(TourInfoActivity.this, R.string.failed_fetch_api, Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<CommentList> call, Throwable t) {
                Toast.makeText(TourInfoActivity.this, R.string.failed_fetch_api, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendComment(String comment) {
        apiTour.sendComment(TokenStorage.getInstance().getAccessToken(), tourInfo.getId(), userId, comment).enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                if (response.isSuccessful()) {
                    TourComment tourComment = new TourComment(userId, null, comment, null, String.valueOf((new Date()).getTime()));

                    tourCommentList.add(tourComment);
                    tourCommentAdapter.notifyDataSetChanged();
//                    listViewComment.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            listViewComment.smoothScrollToPosition(0);
//                        }
//                    });

                } else {
                    Toast.makeText(TourInfoActivity.this, R.string.failed_fetch_api, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {
                Toast.makeText(TourInfoActivity.this, R.string.failed_fetch_api, Toast.LENGTH_SHORT).show();
            }
        });
    }


    public TourInfo getTourInfo() {
        return tourInfo;
    }

    public boolean isHostUser() {
        return isHostUser;
    }

    private void setupTabIcons() {
        tabs.getTabAt(0).setIcon(R.drawable.ic_info_black_24dp);
        tabs.getTabAt(1).setIcon(R.drawable.ic_member_white_24dp);
        tabs.getTabAt(2).setIcon(R.drawable.ic_stars_black_24dp);
        tabs.setSelectedTabIndicatorColor(Color.parseColor("#FFFFFF"));
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private boolean isMemberOfTour() {

        for (TourMember member : tourInfo.getMembers()) {
            if (member.getId().equals(userId)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        switch (view.getId()) {
            case R.id.list_view_comment:
                int lastItem = firstVisibleItem + visibleItemCount;
                if (lastItem == totalItemCount) {
                    if (count == 10 && !loading) {
                        loading = true;
                        progressBar.setVisibility(View.VISIBLE);
                        getComment();
                    }
                }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        dialogComment.dismiss();
        return true;
    }
}