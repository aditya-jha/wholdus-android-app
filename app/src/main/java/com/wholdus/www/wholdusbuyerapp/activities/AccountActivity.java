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
import com.wholdus.www.wholdusbuyerapp.fragments.EditAddressFragment;
import com.wholdus.www.wholdusbuyerapp.fragments.EditProfileDetailsFragment;
import com.wholdus.www.wholdusbuyerapp.fragments.NavigationDrawerFragment;
import com.wholdus.www.wholdusbuyerapp.fragments.OrderDetailsFragment;
import com.wholdus.www.wholdusbuyerapp.fragments.OrdersFragment;
import com.wholdus.www.wholdusbuyerapp.fragments.ProfileFragment;
import com.wholdus.www.wholdusbuyerapp.helperClasses.Constants;
import com.wholdus.www.wholdusbuyerapp.helperClasses.NavDrawerHelper;
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

        initNavigationDrawer();
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == EditAddressFragment.REQUEST_CHECK_SETTINGS) {
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if (fragment != null && fragment instanceof EditAddressFragment) {
                EditAddressFragment activeFragment = (EditAddressFragment) fragment;
                activeFragment.onActivityResult(requestCode, resultCode, data);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void editPersonalDetails() {
        // load edit profile details fragment
        // don't add it to backstack
        openToFragment(EditProfileDetailsFragment.class.getSimpleName(), null);
    }

    @Override
    public void openOrderDetails(int orderID) {
        Bundle bundle = new Bundle();
        bundle.putInt("orderID", orderID);
        openToFragment(OrderDetailsFragment.class.getSimpleName(), bundle);
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
        openToFragment(EditAddressFragment.class.getSimpleName(), bundle);
    }

    @Override
    public void addressClicked(int addressID, int _ID) {
        editAddress(addressID, _ID);
    }

    @Override
    public void addressSaved(int addressID) {
        openToFragment(BuyerAddressFragment.class.getSimpleName(), null);
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

    private void initNavigationDrawer() {
        Fragment navDrawerFragment = new NavigationDrawerFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.navigation_drawer_fragment, navDrawerFragment).commit();
    }

    private String getFragmentToOpenName(Bundle savedInstanceState) {
        String openFragment;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            openFragment = extras.getString(Constants.OPEN_FRAGMENT_KEY);
            if (TextUtils.isEmpty(openFragment)) {
                openFragment = ProfileFragment.class.getSimpleName();
            }
        } else {
            openFragment = (String) savedInstanceState.getSerializable(Constants.OPEN_FRAGMENT_KEY);
        }
        return openFragment;
    }

    private void openToFragment(String fragmentName, @Nullable Bundle bundle) {
        Fragment fragment;

        if (fragmentName.equals(ProfileFragment.class.getSimpleName())) {
            fragment = new ProfileFragment();
        } else if (fragmentName.equals(OrdersFragment.class.getSimpleName())) {
            fragment = new OrdersFragment();
        } else if (fragmentName.equals(EditProfileDetailsFragment.class.getSimpleName())) {
            fragment = new EditProfileDetailsFragment();
        } else if (fragmentName.equals(BuyerAddressFragment.class.getSimpleName())) {
            fragment = new BuyerAddressFragment();
        } else if (fragmentName.equals(EditAddressFragment.class.getSimpleName())) {
            fragment = new EditAddressFragment();
        } else if (fragmentName.equals(OrderDetailsFragment.class.getSimpleName())) {
            fragment = new OrderDetailsFragment();
        } else {
            fragment = new ProfileFragment();
        }

        fragment.setArguments(bundle);
        String backStateName = fragment.getClass().getName();
        FragmentManager fm = getSupportFragmentManager();

        boolean fragmentPopped = fm.popBackStackImmediate(backStateName, 0);

        sendFragmentOpenBroadcast(fragmentName);

        if (!fragmentPopped) { // fragment not in backstack create it
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.fragment_container, fragment, fragment.getClass().getSimpleName());
            ft.addToBackStack(backStateName);
            ft.commit();
        }

    }

    public void sendFragmentOpenBroadcast(String fragmentName) {
        NavDrawerHelper.getInstance().setOpenActivity(this.getClass().getSimpleName());
        NavDrawerHelper.getInstance().setOpenFragment(fragmentName);
    }
}
