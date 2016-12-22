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

    private List<String> mImages;
    private String mProductName, mProductUrl;

    public ProductGalleryViewPagerAdapter(
            FragmentManager fm,
            ArrayList<String> images,
            String productName,
            String productUrl) {
        super(fm);
        mImages = images;
        mProductName = productName;
        mProductUrl = productUrl;
    }

    @Override
    public Fragment getItem(int position) {
        GalleryImageFragment fragment = new GalleryImageFragment();
        fragment.setData(mImages.get(position), mProductName, mProductUrl);
        return fragment;
    }

    @Override
    public int getCount() {
        return mImages.size();
    }
}
