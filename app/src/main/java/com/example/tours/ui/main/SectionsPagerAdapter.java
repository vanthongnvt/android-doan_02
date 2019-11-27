package com.example.tours.ui.main;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.tours.Model.TourComment;
import com.example.tours.Model.TourInfo;
import com.example.tours.R;
import com.example.tours.TourInfoActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    private final Context mContext;
    private TourInfo mtourInfo=null;
    private boolean isHostUser;

    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
        mtourInfo= ((TourInfoActivity)mContext).getTourInfo();
        isHostUser=((TourInfoActivity)mContext).isHostUser();
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a TourInfoFragment (defined as a static inner class below).
        switch (position){
            case 0: return TourInfoFragment.newInstance(mtourInfo,isHostUser);
            case 1: return TourMemberFragment.newInstance(mtourInfo,isHostUser);
            case 2: return TourCommentFragment.newInstance(mtourInfo,isHostUser);
        }
        return null;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
//        return mContext.getResources().getString(TAB_TITLES[position]);
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }
}