package com.wholdus.www.wholdusbuyerapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.fragments.BuyerAddressFragment;
import com.wholdus.www.wholdusbuyerapp.fragments.BuyerInterestFragment;
import com.wholdus.www.wholdusbuyerapp.fragments.EditAddressFragment;
import com.wholdus.www.wholdusbuyerapp.fragments.EditProfileDetailsFragment;
import com.wholdus.www.wholdusbuyerapp.fragments.NavigationDrawerFragment;
import com.wholdus.www.wholdusbuyerapp.fragments.OrderDetailsFragment;
import com.wholdus.www.wholdusbuyerapp.fragments.OrdersFragment;
import com.wholdus.www.wholdusbuyerapp.fragments.ProductGridCategoryTabFragment;
import com.wholdus.www.wholdusbuyerapp.fragments.ProfileFragment;
import com.wholdus.www.wholdusbuyerapp.helperClasses.Constants;
import com.wholdus.www.wholdusbuyerapp.interfaces.OrderDetailsListenerInterface;
import com.wholdus.www.wholdusbuyerapp.interfaces.ProfileListenerInterface;
import com.wholdus.www.wholdusbuyerapp.interfaces.UserAddressInterface;

public class AccountActivity extends AppCompatActivity implements ProfileListenerInterface,
        UserAddressInterface, OrderDetailsListenerInterface {

    private DrawerLayout mDrawerLayout;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        initNavigationDrawer(savedInstanceState);
        initToolbar();
        Bundle extras = getIntent().getExtras();

        openToFragment(getFragmentToOpenName(savedInstanceState), extras);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_bar_checkout:
                startActivity(new Intent(this, CartActivity.class));
                break;
            case R.id.action_bar_shortlist:
                Intent shortlistIntent = new Intent(this, CategoryProductActivity.class);
                shortlistIntent.putExtra(Constants.TYPE, Constants.FAV_PRODUCTS);
                shortlistIntent.getIntExtra(getString(R.string.selected_category_id), 1);
                startActivity(shortlistIntent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
            finish();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void editPersonalDetails() {
        // load edit profile details fragment
        // don't add it to backstack
        openToFragment("editPersonalDetails", null);
    }

    @Override
    public void openOrderDetails(int orderID) {
        Bundle bundle = new Bundle();
        bundle.putInt("orderID", orderID);
        openToFragment("orderDetails", bundle);
    }

    @Override
    public void fragmentCreated(String fragmentName, boolean backEnabled) {
        modifyToolbar(fragmentName, backEnabled);
    }

    @Override
    public void openProfileFragment() {
        onBackPressed();
    }

    @Override
    public void editAddress(int addressID, int _ID) {
        Bundle bundle = new Bundle();
        bundle.putInt("addressID", addressID);
        bundle.putInt("_ID", _ID);
        openToFragment("editAddress", bundle);
    }

    @Override
    public void addressClicked(int addressID, int _ID) {
        //editAddress(addressID, _ID);
    }

    @Override
    public void addressSaved() {
        openToFragment("buyerAddresses",null);
    }

    private void initToolbar() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        // set default toolbar as the action bar for this activity
        mToolbar = (Toolbar) findViewById(R.id.default_toolbar);
        setSupportActionBar(mToolbar);
    }

    private void modifyToolbar(String title, boolean backEnabled) {
        mToolbar.setTitle(title);
        if (backEnabled && mToolbar.getNavigationContentDescription() != "backEnabled") {
            mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
            mToolbar.setNavigationContentDescription("backEnabled");
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
        } else if (!backEnabled && mToolbar.getNavigationContentDescription() != "default") {
            mToolbar.setNavigationIcon(R.drawable.ic_menu_black_24dp);
            mToolbar.setNavigationContentDescription("default");
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDrawerLayout.openDrawer(GravityCompat.START);
                }
            });
        }
    }

    private void initNavigationDrawer(Bundle savedInstanceState) {
        Fragment navDrawerFragment = new NavigationDrawerFragment();

        Bundle args = new Bundle();
        //TODO : Check method for getting current fragment name, this has bugs
        args.putSerializable(Constants.OPEN_FRAGMENT_KEY, getFragmentToOpenName(savedInstanceState));
        args.putSerializable(getString(R.string.open_activity_key), this.getClass().getSimpleName());
        navDrawerFragment.setArguments(args);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.navigation_drawer_fragment, navDrawerFragment).commit();
    }

    private String getFragmentToOpenName(Bundle savedInstanceState) {
        String openFragment;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            openFragment = extras.getString(Constants.OPEN_FRAGMENT_KEY);
            if (TextUtils.isEmpty(openFragment)) {
                openFragment = "profile";
            }
        } else {
            openFragment = (String) savedInstanceState.getSerializable(Constants.OPEN_FRAGMENT_KEY);
        }
        return openFragment;
    }

    private void openToFragment(String fragmentName, @Nullable Bundle bundle) {
        Fragment fragment;

        switch (fragmentName) {
            case "profile":
                fragment = new ProfileFragment();
                break;
            case "orders":
                fragment = new OrdersFragment();
                break;
            case "rejectedProducts":
                fragment = new ProductGridCategoryTabFragment();
                break;
            case "editPersonalDetails":
                fragment = new EditProfileDetailsFragment();
                break;
            case "buyerAddresses":
                fragment = new BuyerAddressFragment();
                break;
            case "editAddress":
                fragment = new EditAddressFragment();
                break;
            case "buyerInterests":
                fragment = new BuyerInterestFragment();
                break;
            case "orderDetails":
                fragment = new OrderDetailsFragment();
                break;
            default:
                fragment = new ProfileFragment();
        }

        fragment.setArguments(bundle);
        String backStateName = fragment.getClass().getName();
        FragmentManager fm = getSupportFragmentManager();

        boolean fragmentPopped = fm.popBackStackImmediate(backStateName, 0);

        if (!fragmentPopped) { // fragment not in backstack create it
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.fragment_container, fragment, fragment.getClass().getSimpleName());
            ft.addToBackStack(backStateName);
            ft.commit();
        }

    }
}
