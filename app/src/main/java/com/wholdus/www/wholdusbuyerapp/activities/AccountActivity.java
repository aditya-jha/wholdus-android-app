package com.wholdus.www.wholdusbuyerapp.activities;

import android.os.Bundle;
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
import com.wholdus.www.wholdusbuyerapp.fragments.NavigationDrawerFragment;
import com.wholdus.www.wholdusbuyerapp.fragments.OrdersFragment;
import com.wholdus.www.wholdusbuyerapp.fragments.ProductsGridFragment;
import com.wholdus.www.wholdusbuyerapp.fragments.ProfileFragment;

public class AccountActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private String mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        initNavigationDrawer();
        fragmentToOpen(savedInstanceState);
        initToolbar();

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

    private void initToolbar() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        // set default toolbar as the action bar for this activity
        Toolbar toolbar = (Toolbar) findViewById(R.id.default_toolbar);
        toolbar.setTitle(mTitle);
        toolbar.setNavigationIcon(R.drawable.ic_menu_black_24dp);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    private void initNavigationDrawer() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.navigation_drawer_fragment, new NavigationDrawerFragment()).commit();
    }

    private void fragmentToOpen(Bundle savedInstanceState) {
        String openFragment;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            openFragment = extras.getString("openFragment");
            if(TextUtils.isEmpty(openFragment)) {
                openFragment = "profile";
            }
            Log.d("openfragment", openFragment);
        } else {
            openFragment = (String) savedInstanceState.getSerializable("openFragment");
        }
        openToFragment(openFragment);
    }

    private void openToFragment(String fragmentName) {
        Fragment fragment;

        switch (fragmentName) {
            case "profile":
                mTitle = "My Profile";
                fragment = new ProfileFragment();
                break;
            case "orders":
                mTitle = "My Orders";
                fragment = new OrdersFragment();
                break;
            case "rejectedProducts":
                mTitle = "Rejected Products";
                fragment = new ProductsGridFragment();
                break;
            default:
                mTitle = "My Profile";
                fragment = new ProfileFragment();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment, fragment.getClass().getSimpleName());
        fragmentTransaction.commit();
    }
}
