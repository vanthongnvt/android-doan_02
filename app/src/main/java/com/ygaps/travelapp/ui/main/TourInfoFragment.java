package com.ygaps.travelapp.ui.main;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.ygaps.travelapp.Adapter.ListStopPointAdapter;
import com.ygaps.travelapp.CreateStopPointActivity;
import com.ygaps.travelapp.HomeActivity;
import com.ygaps.travelapp.Model.TourInfo;
import com.ygaps.travelapp.R;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * A placeholder fragment containing a simple view.
 */
public class TourInfoFragment extends Fragment {

    private static final String ARG_PARAM1 = "tour";
    private TourMemberFragment.OnFragmentInteractionListener mListener;

    private PageViewModel pageViewModel;
    private TourInfo tourInfo;
    private boolean isHostUser=false;
    private ImageView btnCloneTour;
    private ImageView imageAvatar;
    private TextView tvTourName;
    private TextView tvAdults;
    private TextView tvChilds;
    private TextView tvMinCost;
    private TextView tvMaxCost;
    private TextView tvStartDate;
    private TextView tvEndDate;
    private TextView tvStatus;
    private ImageView btnEditStopPoints;
    private ListView listView;
    private ListStopPointAdapter stopPointAdapter;
    private Button btnToMap;

    public static TourInfoFragment newInstance(TourInfo tourInfo,boolean isHostUser) {
        TourInfoFragment fragment = new TourInfoFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_PARAM1, tourInfo);
        bundle.putBoolean("isHostUser",isHostUser);
        fragment.setArguments(bundle);
//        Log.d("CK_HOST", "newInstance: "+isHostUser);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        pageViewModel = ViewModelProviders.of(this).get(PageViewModel.class);
//        int index = 1;
//        if (getArguments() != null) {
//            index = getArguments().getInt(ARG_SECTION_NUMBER);
//        }
//        pageViewModel.setIndex(index);
        if (getArguments() != null) {
            tourInfo = (TourInfo) getArguments().getSerializable(ARG_PARAM1);
            isHostUser=getArguments().getBoolean("isHostUser");
        }
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_tour_info, container, false);
//        final TextView textView = root.findViewById(R.id.section_label);
//        pageViewModel.getText().observe(this, new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });

        init(root);

        btnEditStopPoints.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(root.getContext(), CreateStopPointActivity.class);
                intent.putExtra("tourInfo",tourInfo);
                if(!isHostUser){
                    intent.putExtra("canNotEdit",true);
                }
                startActivity(intent);
            }
        });

        return root;
    }

    private void init(View root){
        btnCloneTour=root.findViewById(R.id.btn_clone_tour);
        imageAvatar=root.findViewById(R.id.img_avatar);
        tvTourName=root.findViewById(R.id.tv_tourName);
        tvAdults=root.findViewById(R.id.tv_adults);
        tvChilds=root.findViewById(R.id.tv_childs);
        tvMinCost=root.findViewById(R.id.tv_minCost);
        tvMaxCost=root.findViewById(R.id.tv_maxCost);
        tvStartDate=root.findViewById(R.id.tv_startDate);
        tvEndDate=root.findViewById(R.id.tv_endDate);
        tvStatus=root.findViewById(R.id.tour_status);
        btnToMap = root.findViewById(R.id.btn_to_map);
        btnEditStopPoints=root.findViewById(R.id.btn_edit_stop_points);
        if(tourInfo.getStatus()==-1||tourInfo.getStatus()==2||tourInfo.getStopPoints().size()==0){
            btnToMap.setVisibility(View.GONE);
        }
        else{
            btnToMap.setOnClickListener(v->{
                Intent home = new Intent(getContext(), HomeActivity.class);
                home.putExtra("directionTourId",tourInfo.getId());
                startActivity(home);
            });
        }
        listView=root.findViewById(R.id.list_view_stop_point);
        stopPointAdapter=new ListStopPointAdapter(root.getContext(),R.layout.list_view_tour_stop_point_item,tourInfo.getStopPoints());
        if(isHostUser){
            btnCloneTour.setVisibility(View.GONE);
        }
        if (tourInfo.getAvatar() != null){
            Picasso.get().load(tourInfo.getAvatar()).into(imageAvatar);
        }else{
            imageAvatar.setImageResource(R.drawable.empty_image);
        }
        tvTourName.setText(tourInfo.getName());

        Calendar cal = Calendar.getInstance();
        long time = 0;
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        if(tourInfo.getStartDate() == null){
            tvStartDate.setText("00/00/0000");
        }
        else{
            time = Long.parseLong(tourInfo.getStartDate());
            cal.setTimeInMillis(time);
            Date timeStartDate = cal.getTime();
            tvStartDate.setText(dateFormat.format(timeStartDate));
        }

        if(tourInfo.getEndDate() == null){
            tvEndDate.setText("00/00/0000");
        }
        else{
            time = Long.parseLong(tourInfo.getEndDate());
            cal.setTimeInMillis(time);
            Date timeEndDate = cal.getTime();
            tvEndDate.setText(dateFormat.format(timeEndDate));
        }
        if(tourInfo.getAdults() == null){
            tvAdults.setText("0 người lớn ");
        }
        else{
            tvAdults.setText(tourInfo.getAdults().toString() + " người lớn ");
        }
        if(tourInfo.getChilds() == null){
            tvChilds.setText("0 trẻ em");
        }
        else{
            tvChilds.setText(tourInfo.getChilds().toString() + " trẻ em");
        }
        if(tourInfo.getMinCost() == null){
            tvMinCost.setText("0");
        }
        else{
            tvMinCost.setText(tourInfo.getMinCost());
        }
        if(tourInfo.getMaxCost() == null){
            tvMaxCost.setText("0");
        }
        else{
            tvMaxCost.setText(tourInfo.getMaxCost());
        }
        if(tourInfo.getStatus()==-1){
            tvStatus.setText(R.string.tour_cancled);
            tvStatus.setTextColor(getResources().getColor(R.color.colorCustomPrimary));
        }
        else if(tourInfo.getStatus()==0){
            tvStatus.setText(R.string.tour_open);
        }
        else if(tourInfo.getStatus()==1){
            tvStatus.setText(R.string.tour_started);
        }
        else if(tourInfo.getStatus()==2){
            tvStatus.setText(R.string.tour_closed);
        }

        listView.setAdapter(stopPointAdapter);

        setListViewHeightBasedOnChildren(listView);

    }

    public static void setListViewHeightBasedOnChildren(ListView listView){
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight=0;
        View view = null;

        for (int i = 0; i < listAdapter.getCount(); i++)
        {
            view = listAdapter.getView(i, view, listView);

            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth,
                        ViewGroup.LayoutParams.MATCH_PARENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();

        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + ((listView.getDividerHeight()) * (listAdapter.getCount()));

        listView.setLayoutParams(params);
        listView.requestLayout();

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof TourMemberFragment.OnFragmentInteractionListener) {
            mListener = (TourMemberFragment.OnFragmentInteractionListener) context;
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
//         TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}