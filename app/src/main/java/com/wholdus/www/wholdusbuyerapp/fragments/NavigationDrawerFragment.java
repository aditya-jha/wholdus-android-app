package com.wholdus.www.wholdusbuyerapp.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.activities.AccountActivity;
import com.wholdus.www.wholdusbuyerapp.activities.LoginSignupActivity;
import com.wholdus.www.wholdusbuyerapp.adapters.ExpandableListViewAdapter;
import com.wholdus.www.wholdusbuyerapp.asynctasks.LoginHelperAsyncTask;
import com.wholdus.www.wholdusbuyerapp.dataSource.NavigationDrawerData;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class NavigationDrawerFragment extends Fragment {

    public NavigationDrawerFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_navigation_drawer, container, false);

        initNavigationDrawer(rootView);

        return rootView;
    }

    private void initNavigationDrawer(ViewGroup rootView) {
        final DrawerLayout mDrawerLayout = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
        ExpandableListView mExpandableListView = (ExpandableListView) rootView.findViewById(R.id.expandable_list_view);
        LinkedHashMap<String, List<String>> mNavigationDrawerData;
        mNavigationDrawerData = NavigationDrawerData.getData();
        ArrayList<String> mNavigationDrawerDataTitles = new ArrayList<>(mNavigationDrawerData.keySet());

        ExpandableListViewAdapter mExpandableListViewAdapter = new ExpandableListViewAdapter(getContext(),
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
                        handleStoreCase(childPosition);
                        break;
                    case 2:
                        handleAccountCase(childPosition);
                        break;
                    case 3:
                        handleHelpSupportCase(childPosition);
                        break;
                    default:
                        return false;
                }
                mDrawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    private boolean handleStoreCase(int childPosition) {
        switch (childPosition) {
            case 4:
                return true;
            default:
                return false;
        }
    }

    private boolean handleAccountCase(int childPosition) {
        Intent intent = new Intent(getContext(), AccountActivity.class);

        Bundle bundle = getArguments();
        String openFragmentName = "";
        if (!(bundle == null)) {
            openFragmentName = bundle.getString("openFragment", "none");
        }

        switch (childPosition) {
            case 0:
                // open profile fragment
                intent.putExtra("openFragment", "profile");
                break;
            case 1:
                // open orders fragment
                intent.putExtra("openFragment", "orders");
                break;
            case 2:
                // TODO: what happens here
                return false;
            case 3:
                // open rejected products
                intent.putExtra("openFragment", "rejectProducts");
                break;
            default:
                return false;
        }

        if (intent.getExtras().getString("openFragment", "no").equals(openFragmentName)){
            return false;
        }

        startActivity(intent);

        return true;
    }

    private boolean handleHelpSupportCase(int childPosition) {
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
