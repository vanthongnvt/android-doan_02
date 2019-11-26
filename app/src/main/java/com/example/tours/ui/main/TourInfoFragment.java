package com.example.tours.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.tours.Model.TourInfo;
import com.example.tours.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class TourInfoFragment extends Fragment {

    private static final String ARG_PARAM1 = "tour";

    private PageViewModel pageViewModel;
    private TourInfo tourInfo;

    public static TourInfoFragment newInstance(TourInfo tourInfo) {
        TourInfoFragment fragment = new TourInfoFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_PARAM1, tourInfo);
        fragment.setArguments(bundle);
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



        return root;
    }
}