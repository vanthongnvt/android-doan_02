package com.ygaps.travelapp.ui.main;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.ygaps.travelapp.Adapter.ListSearchMemberAdapter;
import com.ygaps.travelapp.Adapter.ListTourMemberAdapter;
import com.ygaps.travelapp.ApiService.APIRetrofitCreator;
import com.ygaps.travelapp.ApiService.APITour;
import com.ygaps.travelapp.AppHelper.DialogProgressBar;
import com.ygaps.travelapp.AppHelper.TokenStorage;
import com.ygaps.travelapp.Model.ListUserSearch;
import com.ygaps.travelapp.Model.MessageResponse;
import com.ygaps.travelapp.Model.TourInfo;
import com.ygaps.travelapp.Model.TourMember;
import com.ygaps.travelapp.Model.User;
import com.ygaps.travelapp.R;
import com.ygaps.travelapp.TourInfoActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TourMemberFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TourMemberFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TourMemberFragment extends Fragment {
    private static final String ARG_PARAM1 = "tour";

    private TourInfo tourInfo;
    private boolean isHostUser;

    private OnFragmentInteractionListener mListener;

    private APITour apiTour;
    private ListView listViewMember;
    private ListTourMemberAdapter tourMemberAdapter;
    private ImageView btnShowDialogInviteMember;
    private Dialog dialogInviteMember;
    private EditText edtInviteMember;
    private ImageView btnSearchMember;
    private ListView listViewSearchMember;
    private ListSearchMemberAdapter searchMemberAdapter;
    private List<User> listUser;
    private Integer currPage = 1;

    public TourMemberFragment() {
        // Required empty public constructor
    }

    public static TourMemberFragment newInstance(TourInfo tourInfo, boolean isHostUser) {
        TourMemberFragment fragment = new TourMemberFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, tourInfo);
        args.putBoolean("isHostUser", isHostUser);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
//        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            tourInfo = (TourInfo) getArguments().getSerializable(ARG_PARAM1);
            isHostUser = getArguments().getBoolean("isHostUser");
        }
    }

    private boolean youAreMemberOfTour() {

        for (TourMember member : tourInfo.getMembers()) {
            if (member.getId().equals(TokenStorage.getInstance().getUserId())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_tour_member, container, false);
        listViewMember = root.findViewById(R.id.list_view_tour_member);

        tourMemberAdapter = new ListTourMemberAdapter(root.getContext(), R.layout.list_view_tour_member_item, tourInfo.getMembers());
        listViewMember.setAdapter(tourMemberAdapter);

        btnShowDialogInviteMember = root.findViewById(R.id.btn_dialog_invite_member);
        if (isHostUser) {

            initDialogAddMember(root);
            btnShowDialogInviteMember.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogInviteMember.show();
                    Window window = dialogInviteMember.getWindow();
                    window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                }
            });
        } else {
            if (!youAreMemberOfTour()) {
                btnShowDialogInviteMember.setImageResource(R.drawable.ic_sign_in_alt_solid);
                btnShowDialogInviteMember.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // xin vao tour:
                        requestJoinTour();
                    }
                });
            } else {
                btnShowDialogInviteMember.setVisibility(View.GONE);
            }
        }
        return root;
    }

    private void initDialogAddMember(View root) {
        dialogInviteMember = new Dialog(root.getContext());
        dialogInviteMember.setContentView(R.layout.dialog_invite_member);
        edtInviteMember = dialogInviteMember.findViewById(R.id.edt_invite_member);
        btnSearchMember = dialogInviteMember.findViewById(R.id.btn_seacrh_member);

        listViewSearchMember = dialogInviteMember.findViewById(R.id.list_view_search_member);
        listUser = new ArrayList<>();
        searchMemberAdapter = new ListSearchMemberAdapter(root.getContext(), R.layout.list_view_user_search_item, listUser, TourMemberFragment.this);
        listViewSearchMember.setAdapter(searchMemberAdapter);
        apiTour = new APIRetrofitCreator().getAPIService();

        btnSearchMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchKey = edtInviteMember.getText().toString();
                if (!searchKey.isEmpty()) {
                    currPage = 1;
                    ((TourInfoActivity) root.getContext()).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                    searchMember(searchKey);
                }
            }
        });

    }

    public void inviteMember(User user) {
        DialogProgressBar.showProgress(getContext());
        apiTour.inviteMember(TokenStorage.getInstance().getAccessToken(), tourInfo.getId().toString(), user.getId().toString(), true).enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().getResCode() == null) {
                        Toast.makeText(getContext(), getString(R.string.invite_success) + " " + user.getFullName(), Toast.LENGTH_SHORT).show();
                    } else if (response.body().getResCode() == -1) {
                        Toast.makeText(getContext(), user.getFullName() + " " + getString(R.string.existed_member), Toast.LENGTH_SHORT).show();
                    } else if (response.body().getResCode() == 0) {
                        Toast.makeText(getContext(), user.getFullName() + " " + getString(R.string.already_invited), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), R.string.server_err, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), R.string.server_err, Toast.LENGTH_SHORT).show();
                }
                DialogProgressBar.closeProgress();
            }

            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {
                Toast.makeText(getContext(), R.string.failed_fetch_api, Toast.LENGTH_SHORT).show();
                DialogProgressBar.closeProgress();
            }

        });
    }

    public void requestJoinTour() {
        String yourID = TokenStorage.getInstance().getUserId().toString();
        String tourID = tourInfo.getId().toString();
        apiTour.inviteMember(TokenStorage.getInstance().getAccessToken(), tourID, yourID, false).enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                String msg = response.body().toString();
                Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                if (response.body().getMessage() == "Not valid") {
                    Toast.makeText(getContext(), "Lỗi Not valid", Toast.LENGTH_SHORT).show();
                } else if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Yêu cầu tham gia tour thành công", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), R.string.server_err, Toast.LENGTH_SHORT).show();
                }
                DialogProgressBar.closeProgress();
            }

            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {
                Toast.makeText(getContext(), R.string.failed_fetch_api, Toast.LENGTH_SHORT).show();
                DialogProgressBar.closeProgress();
            }

        });
    }

    public void searchMember(String key) {
        apiTour.searchUser(TokenStorage.getInstance().getAccessToken(), key, currPage, 10).enqueue(new Callback<ListUserSearch>() {
            @Override
            public void onResponse(Call<ListUserSearch> call, Response<ListUserSearch> response) {
                ListUserSearch listUserSearch = response.body();
//                searchMemberAdapter.clear();
                listUser.clear();
                listUser.addAll(listUserSearch.getUsers());
//                Toast.makeText(getContext(), listUser.size()+"", Toast.LENGTH_SHORT).show();
                searchMemberAdapter.notifyDataSetChanged();
                currPage++;
            }

            @Override
            public void onFailure(Call<ListUserSearch> call, Throwable t) {
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

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


}
