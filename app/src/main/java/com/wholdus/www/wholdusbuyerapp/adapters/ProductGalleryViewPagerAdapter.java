package com.wholdus.www.wholdusbuyerapp.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.wholdus.www.wholdusbuyerapp.fragments.GalleryImageFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aditya on 21/12/16.
 */

public class ProductGalleryViewPagerAdapter extends FragmentStatePagerAdapter {

    private Context mContext;
    private List<String> mImages;

    public ProductGalleryViewPagerAdapter(FragmentManager fm, Context context, ArrayList<String> images) {
        super(fm);
        mContext = context;
        mImages = images;
    }

    @Override
    public Fragment getItem(int position) {
        GalleryImageFragment fragment = new GalleryImageFragment();
        fragment.setImageUrl(mImages.get(position));
        return fragment;
    }

    @Override
    public int getCount() {
        return mImages.size();
    }
}
