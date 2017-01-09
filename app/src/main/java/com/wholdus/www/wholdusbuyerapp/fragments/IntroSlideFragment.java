package com.wholdus.www.wholdusbuyerapp.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wholdus.www.wholdusbuyerapp.R;

/**
 * Created by aditya on 6/11/16.
 */

public class IntroSlideFragment extends Fragment {

    private int mImageResourceID;
    private String mDisplayText;

    public IntroSlideFragment() {
    }

    public void setData(int imageResource, String displayText) {
        mImageResourceID = imageResource;
        mDisplayText = displayText;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_intro_slide, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
        imageView.setImageResource(mImageResourceID);

        TextView textView = (TextView) view.findViewById(R.id.textView);
        textView.setText(mDisplayText);
    }
}
