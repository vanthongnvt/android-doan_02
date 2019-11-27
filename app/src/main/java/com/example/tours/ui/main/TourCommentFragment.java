package com.example.tours.ui.main;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tours.Model.TourInfo;
import com.example.tours.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TourCommentFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TourCommentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TourCommentFragment extends Fragment {
    private static final String ARG_PARAM1 = "tour";

    private TourInfo tourInfo;
    private boolean isHostUser;

    private OnFragmentInteractionListener mListener;

    public TourCommentFragment() {
        // Required empty public constructor
    }

    public static TourCommentFragment newInstance(TourInfo tourInfo,boolean isHostUser) {
        TourCommentFragment fragment = new TourCommentFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, tourInfo);
        args.putBoolean("isHostUser",isHostUser);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            tourInfo = (TourInfo) getArguments().getSerializable(ARG_PARAM1);
            isHostUser=getArguments().getBoolean("isHostUser");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tour_comment, container, false);
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
