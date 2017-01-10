package com.wholdus.www.wholdusbuyerapp.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.fragments.IntroSlideFragment;

/**
 * Created by aditya on 6/11/16.
 */

public class IntroViewPagerAdapter extends FragmentStatePagerAdapter {

    private Context mContext;
    private int mTotalPages;

    public IntroViewPagerAdapter(FragmentManager fm, int totalPages, Context context) {
        super(fm);
        mTotalPages = totalPages;
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        IntroSlideFragment introSlideFragment = new IntroSlideFragment();

        switch (position) {
            case 0:
                introSlideFragment.setData(R.drawable.slide_1, mContext.getString(R.string.intro_page_1));
                break;
            case 1:
                introSlideFragment.setData(R.drawable.slide_2, mContext.getString(R.string.intro_page_2));
                break;
            case 2:
                introSlideFragment.setData(R.drawable.slide_3, mContext.getString(R.string.intro_page_3));
                break;
        }
        return introSlideFragment;
    }

    @Override
    public int getCount() {
        return mTotalPages;
    }
}
