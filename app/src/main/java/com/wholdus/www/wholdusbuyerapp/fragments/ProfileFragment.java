package com.wholdus.www.wholdusbuyerapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.wholdus.www.wholdusbuyerapp.R;

/**
 * Created by aditya on 19/11/16.
 */

public class ProfileFragment extends Fragment {

    private ListView mPersonalDetailsListView;
    private ListView mAddressListView;

    public ProfileFragment() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_profile, container, false);

        mPersonalDetailsListView = (ListView) rootView.findViewById(R.id.personal_details_list_view);

        return  rootView;
    }
}
