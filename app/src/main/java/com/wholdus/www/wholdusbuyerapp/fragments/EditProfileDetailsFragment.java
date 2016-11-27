package com.wholdus.www.wholdusbuyerapp.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wholdus.www.wholdusbuyerapp.R;

/**
 * Created by aditya on 28/11/16.
 */

public class EditProfileDetailsFragment extends Fragment {

    public EditProfileDetailsFragment() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_edit_profile_details, container, false);

        return rootView;
    }
}
