package com.wholdus.www.wholdusbuyerapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.fragments.NavigationDrawerFragment;
import com.wholdus.www.wholdusbuyerapp.helperClasses.Constants;
import com.wholdus.www.wholdusbuyerapp.helperClasses.ShareIntentClass;

public class StoreActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        initNavigationDrawer();
        initToolbar();

        Button shareStore = (Button) findViewById(R.id.share_store);
        shareStore.setOnClickListener(this);

        Button storeProducts = (Button) findViewById(R.id.store_products);
        storeProducts.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.checkout_action_button, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int ID = item.getItemId();
        if (ID == R.id.action_bar_checkout) {
            startActivity(new Intent(this, CheckoutActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        final int ID = view.getId();
        switch (ID) {
            case R.id.share_store:
                shareStore();
                break;
            case R.id.store_products:
                Toast.makeText(this, "go to store products", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void initToolbar() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        // set default toolbar as the action bar for this activity
        mToolbar = (Toolbar) findViewById(R.id.default_toolbar);
        mToolbar.setTitle(getString(R.string.store_title));
        setSupportActionBar(mToolbar);

        mToolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp);
        mToolbar.setNavigationContentDescription("default");
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    private void initNavigationDrawer() {
        Fragment navDrawerFragment = new NavigationDrawerFragment();
        Bundle args = new Bundle();
        args.putSerializable(Constants.OPEN_ACTIVITY_KEY, this.getClass().getSimpleName());
        navDrawerFragment.setArguments(args);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.navigation_drawer_fragment, navDrawerFragment).commit();
    }

    private void shareStore() {
        String shareText = String.format(getString(R.string.store_share_text), "Aditya Jha's", "Jha Company", "http://www.wholdus.com/store/jha-company-1");
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, shareText);
        intent.setType("text/plain");

        startActivity(Intent.createChooser(intent, getString(R.string.share_store)));
    }
}
