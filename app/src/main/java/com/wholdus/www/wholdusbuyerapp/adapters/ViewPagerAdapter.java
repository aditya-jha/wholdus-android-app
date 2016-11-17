package com.wholdus.www.wholdusbuyerapp.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.View;

import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.fragments.IntroSlideFragment;

/**
 * Created by aditya on 6/11/16.
 */

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    private Context mContext;
    private int mTotalPages;

    public ViewPagerAdapter(FragmentManager fm, int totalPages, Context context) {
        super(fm);
        mTotalPages = totalPages;
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        String callersClassName = mContext.getClass().getSimpleName();

        if(callersClassName.equals("IntroActivity")) {
            return handleIntroActivityCase(position);
        }

        return null;
    }

    @Override
    public int getCount() {
        return mTotalPages;
    }

    private Fragment handleIntroActivityCase(int position) {
        IntroSlideFragment introSlideFragment = new IntroSlideFragment();

        switch (position) {
            case 0:
                introSlideFragment.setData(R.drawable.slide_1, mContext.getString(R.string.intro_page_1), View.GONE);
                break;
            case 1:
                introSlideFragment.setData(R.drawable.slide_2, mContext.getString(R.string.intro_page_2), View.GONE);
                break;
            case 2:
                introSlideFragment.setData(R.drawable.slide_3, mContext.getString(R.string.intro_page_3), View.VISIBLE);
                break;
        }
        return introSlideFragment;
    }
}
