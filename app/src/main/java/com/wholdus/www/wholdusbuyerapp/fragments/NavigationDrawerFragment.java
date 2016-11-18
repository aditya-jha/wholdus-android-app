package com.wholdus.www.wholdusbuyerapp.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.wholdus.www.wholdusbuyerapp.dataSource.NavigationDrawerData;
import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.adapters.ExpandableListViewAdapter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class NavigationDrawerFragment extends Fragment {

    private LinkedHashMap<String, List<String>> mNavigationDrawerData;
    private ArrayList<String> mNavigationDrawerDataTitles;
    private ExpandableListView mExpandableListView;
    private ExpandableListViewAdapter mExpandableListViewAdapter;

    public NavigationDrawerFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_navigation_drawer, container, false);

        mExpandableListView = (ExpandableListView) rootView.findViewById(R.id.expandable_list_view);

        initNavigationDrawer();

        return rootView;
    }

    private void initNavigationDrawer() {
        mNavigationDrawerData = NavigationDrawerData.getData();
        mNavigationDrawerDataTitles = new ArrayList<String>(mNavigationDrawerData.keySet());

        mExpandableListViewAdapter = new ExpandableListViewAdapter(getContext(),
                mNavigationDrawerDataTitles, mNavigationDrawerData,
                R.layout.navigation_drawer_list_group, R.layout.navigation_drawer_list_item);
        mExpandableListView.setAdapter(mExpandableListViewAdapter);
    }
}
