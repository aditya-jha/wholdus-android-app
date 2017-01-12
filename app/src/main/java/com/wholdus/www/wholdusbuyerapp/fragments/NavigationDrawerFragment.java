package com.wholdus.www.wholdusbuyerapp.fragments;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.activities.AccountActivity;
import com.wholdus.www.wholdusbuyerapp.activities.CategoryProductActivity;
import com.wholdus.www.wholdusbuyerapp.activities.HandPickedActivity;
import com.wholdus.www.wholdusbuyerapp.activities.HelpSupportActivity;
import com.wholdus.www.wholdusbuyerapp.activities.HomeActivity;
import com.wholdus.www.wholdusbuyerapp.activities.LoginSignupActivity;
import com.wholdus.www.wholdusbuyerapp.activities.NotificationActivity;
import com.wholdus.www.wholdusbuyerapp.activities.StoreActivity;
import com.wholdus.www.wholdusbuyerapp.adapters.NavigationDrawerAdapter;
import com.wholdus.www.wholdusbuyerapp.dataSource.NavigationDrawerData;
import com.wholdus.www.wholdusbuyerapp.helperClasses.APIConstants;
import com.wholdus.www.wholdusbuyerapp.helperClasses.Constants;
import com.wholdus.www.wholdusbuyerapp.helperClasses.LoginHelper;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

/**
 * A simple {@link Fragment} subclass.
 */
public class NavigationDrawerFragment extends Fragment implements ExpandableListView.OnGroupClickListener, ExpandableListView.OnChildClickListener {

    private Bundle mBundle;
    private DrawerLayout mDrawerLayout;

    public NavigationDrawerFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_navigation_drawer, container, false);

        mBundle = getArguments();
        if (mBundle == null) {
            mBundle = new Bundle();
        }
        if (mBundle.getString(Constants.OPEN_FRAGMENT_KEY, "none").equals("none")) {
            mBundle.putString(Constants.OPEN_FRAGMENT_KEY, HomeFragment.class.getSimpleName());
        }
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mDrawerLayout = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
        ExpandableListView expandableListView = (ExpandableListView) view.findViewById(R.id.expandable_list_view);

        LinkedHashMap<String, List<String>> mNavigationDrawerData = NavigationDrawerData.getData();
        NavigationDrawerAdapter mNavigationDrawerAdapter = new NavigationDrawerAdapter(getContext(),
                new ArrayList<>(mNavigationDrawerData.keySet()), mNavigationDrawerData,
                R.layout.navigation_drawer_list_group, R.layout.navigation_drawer_list_item);
        expandableListView.setAdapter(mNavigationDrawerAdapter);

        expandableListView.setOnGroupClickListener(this);
        expandableListView.setOnChildClickListener(this);
    }

    @Override
    public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
        switch (groupPosition) {
            case 0:
                Intent intent = new Intent(getContext(), HomeActivity.class);
                intent.setFlags(FLAG_ACTIVITY_CLEAR_TOP);
                if (!mBundle.getString(Constants.OPEN_ACTIVITY_KEY, "none").equals(HomeActivity.class.getSimpleName())) {
                    mBundle.putString(Constants.OPEN_ACTIVITY_KEY, HomeActivity.class.getSimpleName());
                    startActivity(intent);
                } else if (!mBundle.getString(Constants.OPEN_FRAGMENT_KEY, "none").equals(HomeFragment.class.getSimpleName())){
                    mBundle.putString(Constants.OPEN_ACTIVITY_KEY, HomeActivity.class.getSimpleName());
                    mBundle.putString(Constants.OPEN_FRAGMENT_KEY, HomeFragment.class.getSimpleName());
                    intent.putExtra(Constants.OPEN_FRAGMENT_KEY, HomeFragment.class.getSimpleName());
                    startActivity(intent);
                }
                break;
            case 1:
                startActivity(new Intent(getContext(), HandPickedActivity.class));
                break;
            case 5:
                startActivity(new Intent(getContext(), NotificationActivity.class));
                break;
            case 6:
                logout();
                break;
            default:
                return false;
        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        switch (groupPosition) {
            case 2:
                handleStoreCase(childPosition);
                break;
            case 3:
                handleAccountCase(childPosition);
                break;
            case 4:
                handleHelpSupportCase(childPosition);
                break;
            default:
                return false;
        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private boolean handleStoreCase(int childPosition) {
        switch (childPosition) {
            case 0:
                if (!mBundle.getString(Constants.OPEN_ACTIVITY_KEY, "none").equals(StoreActivity.class.getSimpleName())) {
                    Intent intent = new Intent(getContext(), StoreActivity.class);
                    startActivity(intent);
                }
                break;
            case 4:
                return true;
            default:
                return false;
        }
        return true;
    }

    private boolean handleAccountCase(int childPosition) {
        Intent intent = new Intent(getContext(), AccountActivity.class);

        String openFragmentName = "";
        if (mBundle != null) {
            openFragmentName = mBundle.getString(Constants.OPEN_FRAGMENT_KEY, "none");
        }

        switch (childPosition) {
            case 0:
                // open profile fragment
                intent.putExtra(Constants.OPEN_FRAGMENT_KEY, "profile");
                break;
            case 1:
                // open profile fragment
                intent.putExtra(Constants.OPEN_FRAGMENT_KEY, "buyerAddresses");
                break;
            case 2:
                // open orders fragment
                intent.putExtra(Constants.OPEN_FRAGMENT_KEY, "orders");
                break;
            case 3:
                intent.putExtra(Constants.OPEN_FRAGMENT_KEY, "buyerInterests");
                break;
            case 4:
                // open rejected products
                Intent rejectedProductsIntent = new Intent(getContext(), CategoryProductActivity.class);
                rejectedProductsIntent.putExtra(Constants.TYPE, Constants.REJECTED_PRODUCTS);
                startActivity(rejectedProductsIntent);
                return true;
            default:
                return false;
        }

        if (intent.getExtras().getString(Constants.OPEN_FRAGMENT_KEY, "none").equals(openFragmentName)) {
            return false;
        }

        startActivity(intent);

        return true;
    }

    private boolean handleHelpSupportCase(int childPosition) {
        Intent intent = new Intent(getContext(), HelpSupportActivity.class);

        String openFragmentName = "";
        if (mBundle != null) {
            openFragmentName = mBundle.getString(Constants.OPEN_FRAGMENT_KEY, "none");
        }
        switch (childPosition) {
            case 0: // contact us
                intent.putExtra(Constants.OPEN_FRAGMENT_KEY, ContactUsFragment.class.getSimpleName());
                break;
            case 1:
                intent.putExtra("TODO", APIConstants.ABOUT_US_URL);
                intent.putExtra(Constants.OPEN_FRAGMENT_KEY, HelpSupportFragment.class.getSimpleName());
                break;
            case 2:
                intent.putExtra(Constants.OPEN_FRAGMENT_KEY, FAQFragment.class.getSimpleName());
                break;
            case 3:
                intent.putExtra("TODO", APIConstants.RETURN_REFUND_POLICY_URL);
                intent.putExtra(Constants.OPEN_FRAGMENT_KEY, HelpSupportFragment.class.getSimpleName());
                break;
            case 4:
                intent.putExtra("TODO", APIConstants.PRIVACY_POLICY_URL);
                intent.putExtra(Constants.OPEN_FRAGMENT_KEY, HelpSupportFragment.class.getSimpleName());
                break;
            default: // about us, privacy, return
                intent.putExtra(Constants.OPEN_FRAGMENT_KEY, ContactUsFragment.class.getSimpleName());
                break;
        }

        if (intent.getExtras().getString(Constants.OPEN_FRAGMENT_KEY, "none").equals(openFragmentName)) {
            return false;
        }

        intent.putExtra(Constants.OPEN_ACTIVITY_KEY, HelpSupportActivity.class.getSimpleName());
        startActivity(intent);
        return true;
    }

    private void logout() {

        final ProgressDialog progressDialog = new ProgressDialog(getContext());

        progressDialog.setTitle(getString(R.string.logout_loader_title));
        progressDialog.setMessage(getString(R.string.logout_loader_message));
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);

        new Thread(new Runnable() {
            @Override
            public void run() {
                LoginHelper loginHelper = new LoginHelper(getActivity().getApplicationContext());
                if (loginHelper.logout()) {
                    progressDialog.dismiss();
                    Intent intent = new Intent(getContext(), LoginSignupActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    getActivity().startActivity(intent);
                    getActivity().finish();
                } else {
                    progressDialog.dismiss();
                    Log.e(this.getClass().getSimpleName(), "error logging out");
                }
            }
        }).start();
    }
}
