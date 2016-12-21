package com.wholdus.www.wholdusbuyerapp.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.wholdus.www.wholdusbuyerapp.R;

/**
 * Created by aditya on 21/12/16.
 */

public class GalleryImageFragment extends Fragment {

    private String mImageUrl;

    public GalleryImageFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ImageView imageView = (ImageView) inflater.inflate(R.layout.layout_product_gallery_image_view, container, false);
        Glide.with(this).load(mImageUrl).thumbnail(0.05f).diskCacheStrategy(DiskCacheStrategy.RESULT).into(imageView);
        return imageView;
    }

    public void setImageUrl(String url) {
        mImageUrl = url;
    }
}
