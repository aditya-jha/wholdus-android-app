package com.wholdus.www.wholdusbuyerapp.activities;

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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.fragments.EditAddressFragment;
import com.wholdus.www.wholdusbuyerapp.fragments.EditProfileDetailsFragment;
import com.wholdus.www.wholdusbuyerapp.fragments.NavigationDrawerFragment;
import com.wholdus.www.wholdusbuyerapp.fragments.OrdersFragment;
import com.wholdus.www.wholdusbuyerapp.fragments.ProductsGridFragment;
import com.wholdus.www.wholdusbuyerapp.fragments.ProfileFragment;
import com.wholdus.www.wholdusbuyerapp.interfaces.ProfileListenerInterface;

public class AccountActivity extends AppCompatActivity implements ProfileListenerInterface {

    private DrawerLayout mDrawerLayout;
    private Toolbar mToolbar;

    private String OPEN_FRAGMENT_KEY = "openFragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        initNavigationDrawer(savedInstanceState);
        initToolbar();

        fragmentToOpen(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_bar_checkout:
                break;
            case R.id.action_bar_store_home:
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
    public void fragmentCreated(String fragmentName, boolean backEnabled) {
        modifyToolbar(fragmentName, backEnabled);
    }

    @Override
    public void openProfileFragment() {
        onBackPressed();
    }

    @Override
    public void editAddress(@Nullable String addressID) {
        Bundle bundle = new Bundle();
        bundle.putString("addressID", addressID);
        openToFragment("editAddress", bundle);
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
            mToolbar.setNavigationIcon(R.drawable.ic_keyboard_arrow_left_black_24dp);
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
        args.putSerializable(OPEN_FRAGMENT_KEY, getFragmenttoOpenName(savedInstanceState));
        navDrawerFragment.setArguments(args);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.navigation_drawer_fragment, navDrawerFragment).commit();
    }

    private String getFragmenttoOpenName(Bundle savedInstanceState){
        String openFragment;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            openFragment = extras.getString(OPEN_FRAGMENT_KEY);
            if (TextUtils.isEmpty(openFragment)) {
                openFragment = "profile";
            }
        } else {
            openFragment = (String) savedInstanceState.getSerializable(OPEN_FRAGMENT_KEY);
        }
        return openFragment;
    }

    private void fragmentToOpen(Bundle savedInstanceState) {
        String openFragment = getFragmenttoOpenName(savedInstanceState);

        openToFragment(openFragment, null);
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
                fragment = new ProductsGridFragment();
                break;
            case "editPersonalDetails":
                fragment = new EditProfileDetailsFragment();
                break;
            case "editAddress":
                fragment = new EditAddressFragment();
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
