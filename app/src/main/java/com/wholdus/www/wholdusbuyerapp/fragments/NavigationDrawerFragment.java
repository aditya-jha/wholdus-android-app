package com.wholdus.www.wholdusbuyerapp.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.wholdus.www.wholdusbuyerapp.activities.LoginSignupActivity;
import com.wholdus.www.wholdusbuyerapp.aynctasks.LoginHelperAsyncTask;
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

        initNavigationDrawer(rootView);

        return rootView;
    }

    private void initNavigationDrawer(ViewGroup rootView) {
        mExpandableListView = (ExpandableListView) rootView.findViewById(R.id.expandable_list_view);
        mNavigationDrawerData = NavigationDrawerData.getData();
        mNavigationDrawerDataTitles = new ArrayList<>(mNavigationDrawerData.keySet());

        mExpandableListViewAdapter = new ExpandableListViewAdapter(getContext(),
                mNavigationDrawerDataTitles, mNavigationDrawerData,
                R.layout.navigation_drawer_list_group, R.layout.navigation_drawer_list_item);
        mExpandableListView.setAdapter(mExpandableListViewAdapter);

        mExpandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                switch (groupPosition) {
                    case 0:
                        return true;
                    case 4:
                        logout();
                        return true;
                    default:
                        return false;
                }
            }
        });

        mExpandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                switch (groupPosition) {
                    case 1:
                        return handleStoreCase(groupPosition, childPosition);
                    case 2:
                        return handleAccountCase(groupPosition, childPosition);
                    case 3:
                        return handleHelpSupportCase(groupPosition, childPosition);
                    default:
                        return false;
                }
            }
        });
    }

    private boolean handleStoreCase(int groupPosition, int childPosition) {
        switch (childPosition) {
            case 4:
                return true;
            default:
                return false;
        }
    }

    private boolean handleAccountCase(int groupPosition, int childPosition) {
        switch (childPosition) {
            case 4:
                logout();
                return true;
            default:
                return false;
        }
    }

    private boolean handleHelpSupportCase(int groupPosition, int childPosition) {
        switch (childPosition) {
            case 1:
                return true;
            default:
                return false;
        }
    }

    private void logout() {
        LoginHelperAsyncTask loginHelperAsyncTask = new LoginHelperAsyncTask(getContext(),
                new LoginHelperAsyncTask.AsyncResponse() {
                    @Override
                    public void processFinish(Boolean output) {
                        if (output) {
                            startActivity(new Intent(getContext(), LoginSignupActivity.class));
                            getActivity().finish();
                        }
                    }
                });
        loginHelperAsyncTask.setUpProgressDialog(true, getString(R.string.logout_loader_message));
        loginHelperAsyncTask.execute("logout");
    }
}
