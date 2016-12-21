package com.wholdus.www.wholdusbuyerapp.activities;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.wholdus.www.wholdusbuyerapp.R;

public class ProductGalleryActivity extends AppCompatActivity
    implements View.OnClickListener {

    private RecyclerView mThumbImagesView;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_gallery);

        mViewPager = (ViewPager) findViewById(R.id.gallery_view_pager);
        mViewPager.setOnClickListener(this);
        mViewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (mThumbImagesView.getVisibility() == View.VISIBLE) {
                    mThumbImagesView.setVisibility(View.GONE);
                } else {
                    mThumbImagesView.setVisibility(View.VISIBLE);
                }
                return false;
            }
        });

        mThumbImagesView = (RecyclerView) findViewById(R.id.thumb_images_recycler_view);
    }

    @Override
    public void onClick(View view) {
        final int ID = view.getId();
        switch (ID) {
            case R.id.gallery_view_pager:

        }
    }
}
