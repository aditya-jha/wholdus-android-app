package com.wholdus.www.wholdusbuyerapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.wholdus.www.wholdusbuyerapp.R;

/**
 * Created by aditya on 6/11/16.
 */

public class IntroSlideFragment extends Fragment {

    private int mImageResourceID;
    private String mDisplayText;
    private int mStartButtonState;
    private OnIntroSlideListener mIntroSlideListener;

    public interface OnIntroSlideListener {
        void onStartButtonClicked();
    }

    public IntroSlideFragment() {}

    public void setData(int imageResource, String displayText, int buttonState) {
        mImageResourceID = imageResource;
        mDisplayText = displayText;
        mStartButtonState = buttonState;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_intro_slide, container, false);

        ImageView imageView = (ImageView) rootView.findViewById(R.id.imageView);
        imageView.setImageResource(mImageResourceID);

        TextView textView = (TextView) rootView.findViewById(R.id.textView);
        textView.setText(mDisplayText);

        Button startButton = (Button) rootView.findViewById(R.id.startButton);
        startButton.setVisibility(mStartButtonState);

        if(mStartButtonState == View.VISIBLE) {
            startButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mIntroSlideListener.onStartButtonClicked();
                }
            });
        }

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mIntroSlideListener = (OnIntroSlideListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnIntroSlideListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mIntroSlideListener = null;
    }
}
