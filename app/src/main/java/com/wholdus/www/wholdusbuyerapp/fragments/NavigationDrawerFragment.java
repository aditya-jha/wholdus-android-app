package com.wholdus.www.wholdusbuyerapp.fragments;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
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
import com.wholdus.www.wholdusbuyerapp.helperClasses.FilterClass;
import com.wholdus.www.wholdusbuyerapp.helperClasses.LoginHelper;
import com.wholdus.www.wholdusbuyerapp.helperClasses.NavDrawerHelper;
import com.wholdus.www.wholdusbuyerapp.helperClasses.TrackingHelper;
import com.wholdus.www.wholdusbuyerapp.models.NavDrawerData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

/**
 * A simple {@link Fragment} subclass.
 */
public class NavigationDrawerFragment extends Fragment implements ExpandableListView.OnGroupClickListener, ExpandableListView.OnChildClickListener {

    private DrawerLayout mDrawerLayout;

    public NavigationDrawerFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<NavDrawerData> mNavigationDrawerData = NavigationDrawerData.getData();
                final NavigationDrawerAdapter mNavigationDrawerAdapter = new NavigationDrawerAdapter(getContext(), mNavigationDrawerData);

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mDrawerLayout = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
                        ExpandableListView expandableListView = (ExpandableListView) view.findViewById(R.id.expandable_list_view);
                        expandableListView.setAdapter(mNavigationDrawerAdapter);

                        expandableListView.setOnGroupClickListener(NavigationDrawerFragment.this);
                        expandableListView.setOnChildClickListener(NavigationDrawerFragment.this);
                    }
                });
            }
        }).start();
    }

    @Override
    public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
        switch (groupPosition) {
            case 0:
                Intent intent = new Intent(getContext(), HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                if (!NavDrawerHelper.getInstance().getOpenActivity().equals(HomeActivity.class.getSimpleName()) ||
                        !NavDrawerHelper.getInstance().getOpenFragment().equals(HomeFragment.class.getSimpleName())) {
                    intent.putExtra(Constants.OPEN_FRAGMENT_KEY, HomeFragment.class.getSimpleName());
                    startActivity(intent);
                }
                break;
            case 1:
                FilterClass.resetFilter();
                FilterClass.resetCategoryFilter();
                startActivity(new Intent(getContext(), HandPickedActivity.class));
                break;
            case 2:
                if (!NavDrawerHelper.getInstance().getOpenFragment().equals(CategoryGridFragment.class.getSimpleName())){
                    Intent categories = new Intent(getContext(), HomeActivity.class);
                    categories.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    categories.putExtra(Constants.OPEN_FRAGMENT_KEY, CategoryGridFragment.class.getSimpleName());
                    getContext().startActivity(categories);
                }
                break;
            case 3:
                if (NavDrawerHelper.getInstance().getType() != Constants.FAV_PRODUCTS) {
                    Intent shortlistIntent = new Intent(getContext(), CategoryProductActivity.class);
                    shortlistIntent.putExtra(Constants.TYPE, Constants.FAV_PRODUCTS);
                    startActivity(shortlistIntent);
                }
                break;
            case 4:
                getContext().startActivity(new Intent(getContext(), NotificationActivity.class));
                break;
            case 7:
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
            case 5:
                handleAccountCase(childPosition);
                break;
            case 6:
                handleHelpSupportCase(childPosition);
                break;
            default:
                return false;
        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private boolean handleAccountCase(int childPosition) {
        Intent intent = new Intent(getContext(), AccountActivity.class);

        switch (childPosition) {
            case 0:
                // open profile fragment
                intent.putExtra(Constants.OPEN_FRAGMENT_KEY, ProfileFragment.class.getSimpleName());
                break;
            case 1:
                // open profile fragment
                intent.putExtra(Constants.OPEN_FRAGMENT_KEY, BuyerAddressFragment.class.getSimpleName());
                break;
            case 2:
                // open orders fragment
                intent.putExtra(Constants.OPEN_FRAGMENT_KEY, OrdersFragment.class.getSimpleName());
                break;
//            case 3:
//                intent.putExtra(Constants.OPEN_FRAGMENT_KEY, "buyerInterests");
//                break;
            case 3:
                // open rejected products
                if (NavDrawerHelper.getInstance().getType() != Constants.REJECTED_PRODUCTS) {
                    Intent rejectedProductsIntent = new Intent(getContext(), CategoryProductActivity.class);
                    rejectedProductsIntent.putExtra(Constants.TYPE, Constants.REJECTED_PRODUCTS);
                    startActivity(rejectedProductsIntent);
                }
                return true;
            default:
                return false;
        }

        if (intent.getExtras().getString(Constants.OPEN_FRAGMENT_KEY, "none").equals(NavDrawerHelper.getInstance().getOpenFragment())) {
            return false;
        }

        startActivity(intent);

        return true;
    }

    private boolean handleHelpSupportCase(int childPosition) {
        Intent intent = new Intent(getContext(), HelpSupportActivity.class);

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

        if (intent.getExtras().getString(Constants.OPEN_FRAGMENT_KEY, "none").equals(NavDrawerHelper.getInstance().getOpenFragment())) {
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

        TrackingHelper.getInstance(getContext())
                .logEvent(
                        FirebaseAnalytics.Event.SELECT_CONTENT,
                        "logout",
                        "");

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
                }
            }
        }).start();
    }

}
