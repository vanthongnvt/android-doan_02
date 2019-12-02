package com.example.tours.ui.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.tours.Adapter.ListInvitationAdapter;
import com.example.tours.ApiService.APIRetrofitCreator;
import com.example.tours.ApiService.APITour;
import com.example.tours.AppHelper.TokenStorage;
import com.example.tours.HomeActivity;
import com.example.tours.Model.ListTourInvitation;
import com.example.tours.Model.TourInvitation;
import com.example.tours.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationsFragment extends Fragment implements AbsListView.OnScrollListener {

    private NotificationsViewModel notificationsViewModel;

    private APITour apiTour;

    private Integer pageIndex=1;
    private ListTourInvitation listTourInvitation;
    private ListInvitationAdapter invitationAdapter;
    private Integer totalInvitation=-1;
    private List<TourInvitation> tourInvitations = new ArrayList<>();
    private ListView listView;
    private Boolean loading=false;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);
//        notificationsViewModel =
//                ViewModelProviders.of(this).get(NotificationsViewModel.class);
//
//        final TextView textView = root.findViewById(R.id.text_notifications);
//        notificationsViewModel.getText().observe(this, new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });

        init(root);

        return root;
    }

    private void init(View root) {
        apiTour = new APIRetrofitCreator().getAPIService();
        listView =root.findViewById(R.id.list_view_invitation);
        invitationAdapter = new ListInvitationAdapter(root.getContext(),R.layout.list_view_tour_invitation_item,tourInvitations,apiTour);
        listView.setAdapter(invitationAdapter);
        listView.setOnScrollListener(this);

        getInvitation();

    }

    private void getInvitation(){
        apiTour.userInvitation(TokenStorage.getInstance().getAccessToken(),pageIndex,10).enqueue(new Callback<ListTourInvitation>() {
            @Override
            public void onResponse(Call<ListTourInvitation> call, Response<ListTourInvitation> response) {
                if(response.isSuccessful()){
                    listTourInvitation = response.body();
                    if(pageIndex==1) {
                        totalInvitation = listTourInvitation.getTotal();
                        ((HomeActivity)getContext()).setTitleBar("Lời mời ("+totalInvitation+")");
                    }
                    tourInvitations.addAll(listTourInvitation.getTours());
                    invitationAdapter.notifyDataSetChanged();
                    pageIndex++;
                    loading=false;
                }
            }

            @Override
            public void onFailure(Call<ListTourInvitation> call, Throwable t) {
                Toast.makeText(getContext(),R.string.failed_fetch_api, Toast.LENGTH_SHORT).show();
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
            case R.id.list_view_invitation:

                int lastItem = firstVisibleItem + visibleItemCount;
                if(lastItem == totalItemCount)
                {
                    if(totalInvitation!=-1&&pageIndex*10 - totalInvitation < 10&&!loading) {
                        loading=true;
                        getInvitation();
                    }
                }
        }
    }
}