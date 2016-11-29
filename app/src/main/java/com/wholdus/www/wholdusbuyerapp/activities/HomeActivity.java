package com.wholdus.www.wholdusbuyerapp.activities;

import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.fragments.HomeFragment;
import com.wholdus.www.wholdusbuyerapp.fragments.NavigationDrawerFragment;

public class HomeActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private int backpress = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // initialize the toolbar
        initToolbar();

        // initialize the navigation drawer
        initNavigationDrawer();

        // initialize the activity elements
        initActivityElements();
    }

    @Override
    public void onResume(){
        super.onResume();
        backpress = 0;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.default_action_bar, menu);
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
    public void onOptionsMenuClosed(Menu menu) {
        super.onOptionsMenuClosed(menu);
    }

    private void initToolbar() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        // set default toolbar as the action bar for this activity
        Toolbar toolbar = (Toolbar) findViewById(R.id.default_toolbar);
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

    private void initActivityElements() {
        // add fragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new HomeFragment()).commit();
    }

    @Override
    public void onBackPressed(){
        if (backpress == 0) {
            backpress = (backpress + 1);
            Toast.makeText(getApplicationContext(), " Press Back again to Exit ", Toast.LENGTH_SHORT).show();
            return;
        }
        if (backpress >= 1) {
            super.onBackPressed();
            return;
        }
    }
}
