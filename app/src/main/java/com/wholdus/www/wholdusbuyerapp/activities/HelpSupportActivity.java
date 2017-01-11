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
import android.view.View;

import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.fragments.ContactUsFragment;
import com.wholdus.www.wholdusbuyerapp.fragments.FAQFragment;
import com.wholdus.www.wholdusbuyerapp.fragments.HelpSupportFragment;
import com.wholdus.www.wholdusbuyerapp.fragments.NavigationDrawerFragment;
import com.wholdus.www.wholdusbuyerapp.helperClasses.Constants;
import com.wholdus.www.wholdusbuyerapp.interfaces.HelpSupportListenerInterface;

public class HelpSupportActivity extends AppCompatActivity implements HelpSupportListenerInterface {

    private DrawerLayout mDrawerLayout;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_support);

        Bundle extras = getIntent().getExtras();
        initNavigationDrawer(extras);
        initToolbar();

        openToFragment(extras.getString(Constants.OPEN_FRAGMENT_KEY), extras);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
            finish();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void fragmentCreated(String title) {
        modifyToolbar(title);
    }

    private void initToolbar() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        // set default toolbar as the action bar for this activity
        mToolbar = (Toolbar) findViewById(R.id.default_toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        mToolbar.setNavigationContentDescription("backEnabled");
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void modifyToolbar(String title) {
        mToolbar.setTitle(title);
    }

    private void initNavigationDrawer(@Nullable Bundle args) {
        Fragment navDrawerFragment = new NavigationDrawerFragment();
        navDrawerFragment.setArguments(args);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.navigation_drawer_fragment, navDrawerFragment).commit();
    }

    private void openToFragment(String fragmentName, @Nullable Bundle bundle) {
        Fragment fragment;

        if (fragmentName.equals(FAQFragment.class.getSimpleName())) {
            fragment = new FAQFragment();
        } else if (fragmentName.equals(HelpSupportFragment.class.getSimpleName())) {
            fragment = new HelpSupportFragment();
        } else if (fragmentName.equals(ContactUsFragment.class.getSimpleName())) {
            fragment = new ContactUsFragment();
        } else {
            fragment = new ContactUsFragment();
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
