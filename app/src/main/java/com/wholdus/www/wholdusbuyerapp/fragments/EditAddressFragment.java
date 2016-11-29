package com.wholdus.www.wholdusbuyerapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.interfaces.ProfileListenerInterface;

/**
 * Created by aditya on 29/11/16.
 */

public class EditAddressFragment extends Fragment {

    private ProfileListenerInterface mListener;
    private String mAddressID;

    public EditAddressFragment() {}

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (ProfileListenerInterface) context;
        } catch (ClassCastException cee) {
            Log.d(EditAddressFragment.class.getSimpleName(), " must implement " + ProfileListenerInterface.class.getSimpleName());
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        mAddressID = arguments.getString("addressID", null);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_edit_address, container, false);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mAddressID != null) {
            mListener.fragmentCreated("Edit Address", true);
        } else {
            mListener.fragmentCreated("Add New Address", true);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
