package com.wholdus.www.wholdusbuyerapp.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.helperClasses.TouchImageView;
import com.wholdus.www.wholdusbuyerapp.interfaces.ItemClickListener;

/**
 * Created by aditya on 21/12/16.
 */

public class GalleryImageFragment extends Fragment {

    private String mImageUrl;
    private ItemClickListener mListener;

    public GalleryImageFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (ItemClickListener) context;
        } catch (ClassCastException cee) {
            cee.printStackTrace();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final TouchImageView imageView = (TouchImageView) inflater.inflate(R.layout.layout_product_gallery_image_view, container, false);
        Glide.with(this).load(mImageUrl).asBitmap().thumbnail(0.05f).diskCacheStrategy(DiskCacheStrategy.ALL).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                imageView.setImageBitmap(resource);
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.itemClicked(-1, 0);
            }
        });
        return imageView;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void setImageUrl(String url) {
        mImageUrl = url;
    }
}
