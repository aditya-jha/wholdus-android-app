package com.wholdus.www.wholdusbuyerapp.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.firebase.crash.FirebaseCrash;
import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.helperClasses.ShareIntentClass;
import com.wholdus.www.wholdusbuyerapp.helperClasses.TouchImageView;
import com.wholdus.www.wholdusbuyerapp.interfaces.ItemClickListener;

/**
 * Created by aditya on 21/12/16.
 */

public class GalleryImageFragment extends Fragment {

    private String mImageUrl, mName, mUrl;
    private ItemClickListener mListener;
    private TouchImageView mImageView;

    public GalleryImageFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (ItemClickListener) context;
        } catch (ClassCastException cee) {
            FirebaseCrash.report(cee);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.layout_product_gallery_image_view, container, false);

        mImageView = (TouchImageView) rootView.findViewById(R.id.gallery_image);

        Glide.with(getContext())
                .load(mImageUrl)
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        mImageView.setImageBitmap(resource);
                    }
                });

        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.itemClicked(view, -1, -1);
            }
        });

        return rootView;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.share_action_button, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_bar_share:
                shareProduct();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setData(String url, String name, String productUrl) {
        mImageUrl = url;
        mName = name;
        mUrl = productUrl;
    }

    private void shareProduct() {
        TouchImageView imageView = (TouchImageView) getView();
        String title = String.format(getString(R.string.product_share_text), mName, mUrl);
        ShareIntentClass.shareImage(getContext(), imageView, title);
    }
}
