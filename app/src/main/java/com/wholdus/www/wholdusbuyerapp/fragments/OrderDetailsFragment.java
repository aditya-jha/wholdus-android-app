package com.wholdus.www.wholdusbuyerapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.wholdus.www.wholdusbuyerapp.interfaces.ProfileListenerInterface;

/**
 * Created by kaustubh on 13/12/16.
 */

public class OrderDetailsFragment extends Fragment {

    private ProfileListenerInterface mListener;
    private int mOrderID;

    public OrderDetailsFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (ProfileListenerInterface) context;
        } catch (ClassCastException cee) {
            cee.printStackTrace();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        mOrderID = arguments.getInt("orderID", -1);
    }
}
