package com.wholdus.www.wholdusbuyerapp.fragments;

import android.content.Context;
import android.support.v4.app.Fragment;

import com.wholdus.www.wholdusbuyerapp.interfaces.HandPickedListenerInterface;

/**
 * Created by kaustubh on 17/12/16.
 */

public class HandPickedFragment extends Fragment {

    private HandPickedListenerInterface mListener;

    public HandPickedFragment(){
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (HandPickedListenerInterface) context;
        } catch (ClassCastException cee) {
            cee.printStackTrace();
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        mListener.fragmentCreated("Hand Picked Fragment");
    }
}
