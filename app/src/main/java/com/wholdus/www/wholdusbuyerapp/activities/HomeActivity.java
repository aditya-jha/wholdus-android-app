package com.wholdus.www.wholdusbuyerapp.activities;

import android.Manifest;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.databaseContracts.CatalogContract;
import com.wholdus.www.wholdusbuyerapp.fragments.CategoryGridFragment;
import com.wholdus.www.wholdusbuyerapp.fragments.ContactUsFragment;
import com.wholdus.www.wholdusbuyerapp.fragments.HomeFragment;
import com.wholdus.www.wholdusbuyerapp.fragments.NavigationDrawerFragment;
import com.wholdus.www.wholdusbuyerapp.fragments.OrderDetailsFragment;
import com.wholdus.www.wholdusbuyerapp.helperClasses.CartMenuItemHelper;
import com.wholdus.www.wholdusbuyerapp.helperClasses.Constants;
import com.wholdus.www.wholdusbuyerapp.helperClasses.ContactsHelperClass;
import com.wholdus.www.wholdusbuyerapp.helperClasses.FilterClass;
import com.wholdus.www.wholdusbuyerapp.helperClasses.HelperFunctions;
import com.wholdus.www.wholdusbuyerapp.helperClasses.NavDrawerHelper;
import com.wholdus.www.wholdusbuyerapp.interfaces.HomeListenerInterface;
import com.wholdus.www.wholdusbuyerapp.loaders.CartLoader;
import com.wholdus.www.wholdusbuyerapp.models.Cart;

import java.util.ArrayList;
import java.util.Arrays;

public class HomeActivity extends AppCompatActivity implements HomeListenerInterface {

    private DrawerLayout mDrawerLayout;
    private boolean mDoublePressToExit;
    private Toolbar mToolbar;
    private FirebaseAnalytics mFirebaseAnalytics;
    private static final int CONTACTS_PERMISSION = 0;

    private CartMenuItemHelper mCartMenuItemHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        routeToActivity();

        // check for google play api
        HelperFunctions.checkGooglePlay(this);

        // initialize the toolbar
        initToolbar();

        // initialize the navigation drawer
        initNavigationDrawer(getIntent().getExtras());

        openToFragment(getFragmentToOpenName(savedInstanceState), null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mDoublePressToExit = false;
        FilterClass.resetFilter();
        FilterClass.resetCategoryFilter();
        if (mCartMenuItemHelper != null) {
            mCartMenuItemHelper.restartLoader();
        }
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (fragment instanceof CategoryGridFragment) {
            getSupportFragmentManager().popBackStack();
            openToFragment(HomeFragment.class.getSimpleName(), new Bundle());
            return;
        }

        if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
            if (!mDoublePressToExit) {
                mDoublePressToExit = true;
                Toast.makeText(this, getString(R.string.back_press_message), Toast.LENGTH_SHORT).show();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mDoublePressToExit = false;
                    }
                }, 2000);
            } else {
                finish();
            }
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.default_action_buttons, menu);
        mCartMenuItemHelper = new CartMenuItemHelper(this, menu.findItem(R.id.action_bar_checkout), getSupportLoaderManager());
        mCartMenuItemHelper.restartLoader();
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
    public void onOptionsMenuClosed(Menu menu) {
        super.onOptionsMenuClosed(menu);
    }

    @Override
    public void openCategory(int categoryID) {
        if (categoryID != -1) {
            Intent intent = new Intent(this, CategoryProductActivity.class);
            intent.putExtra(getString(R.string.selected_category_id), categoryID);
            startActivity(intent);
        } else {
            openToFragment(CategoryGridFragment.class.getSimpleName(), null);
        }
    }

    @Override
    public void fragmentCreated(String title, boolean backEnabled) {
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

    @Override
    public void helpButtonClicked() {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "help");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "help on home screen clicked");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    ContactsHelperClass contactsHelperClass = new ContactsHelperClass(getApplicationContext());
                    String savedNumber = contactsHelperClass.getSavedNumber();
                    if (savedNumber != null) {
                        openWhatsapp(savedNumber);
                    } else {
                        contactsHelperClass.saveWholdusContacts();
                        savedNumber = contactsHelperClass.getSavedNumber();
                        if (savedNumber != null) openWhatsapp(savedNumber);
                    }
                }
            }).start();
        } else {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_CONTACTS}, CONTACTS_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CONTACTS_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    helpButtonClicked();
                } else {
                    Toast.makeText(this, "Permission needed to chat with us", Toast.LENGTH_SHORT).show();
                }
        }
    }

    private void initToolbar() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        // set default toolbar as the action bar for this activity
        mToolbar = (Toolbar) findViewById(R.id.default_toolbar);
        setSupportActionBar(mToolbar);
    }

    private void initNavigationDrawer(Bundle bundle) {
        if (bundle == null) {
            bundle = new Bundle();
            bundle.putString(Constants.OPEN_ACTIVITY_KEY, this.getClass().getSimpleName());
            bundle.putString(Constants.OPEN_FRAGMENT_KEY, getFragmentToOpenName(null));
        }
        bundle.putString(Constants.OPEN_ACTIVITY_KEY, HomeActivity.class.getSimpleName());

        Fragment navDrawerFragment = new NavigationDrawerFragment();
        navDrawerFragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.navigation_drawer_fragment, navDrawerFragment).commit();
    }

    private String getFragmentToOpenName(Bundle savedInstanceState) {
        String openFragment;
        if (savedInstanceState == null) {
            openFragment = getIntent().getStringExtra(Constants.OPEN_FRAGMENT_KEY);
        } else {
            openFragment = (String) savedInstanceState.getSerializable(Constants.OPEN_FRAGMENT_KEY);
        }
        if (openFragment == null) {
            openFragment = "";
        }
        return openFragment;
    }

    private void openToFragment(String fragmentName, @Nullable Bundle bundle) {
        Fragment fragment;

        if (fragmentName.equals(HomeFragment.class.getSimpleName())) {
            fragment = new HomeFragment();
        } else if (fragmentName.equals(CategoryGridFragment.class.getSimpleName())) {
            fragment = new CategoryGridFragment();
        } else {
            fragment = new HomeFragment();
        }

        fragment.setArguments(bundle);
        String backStateName = fragment.getClass().getName();
        FragmentManager fm = getSupportFragmentManager();

        sendFragmentOpenBroadcast(fragmentName);

        boolean fragmentPopped = fm.popBackStackImmediate(backStateName, 0);

        if (!fragmentPopped) { // fragment not in backstack create it
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.fragment_container, fragment, fragment.getClass().getSimpleName());
            ft.addToBackStack(backStateName);
            ft.commit();
        }
    }

    private void openWhatsapp(final String number) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    final Uri uri = Uri.parse("smsto:" + number);
                    Intent i = new Intent(Intent.ACTION_SENDTO, uri);
                    i.putExtra("sms_body", "I need some help");
                    i.setPackage("com.whatsapp");
                    startActivity(i);
                } catch (Exception e) {
                    Intent intent = new Intent(getApplicationContext(), HelpSupportActivity.class);
                    intent.putExtra(Constants.OPEN_FRAGMENT_KEY, ContactUsFragment.class.getSimpleName());
                    intent.putExtra(Constants.OPEN_ACTIVITY_KEY, HelpSupportActivity.class.getSimpleName());
                    startActivity(intent);
                }
            }
        });
    }

    public void routeToActivity(){
        Intent receivedIntent = getIntent();
        if (getIntent() == null){
            return;
        }
        Bundle bundle = receivedIntent.getBundleExtra("router");
        if (bundle == null){
            return;
        }
        Class activityClass;
        Intent intent = new Intent();

        String activityToStart = bundle.getString("activity", "");

        switch (activityToStart) {
            case "Handpicked":
                activityClass = HandPickedActivity.class;
                String productIDs = bundle.getString("productIDs", "");
                if (!productIDs.equals("")){
                    try {
                        ArrayList<String> productIDsStringArray = new ArrayList<>(Arrays.asList(TextUtils.split(productIDs, ",")));
                        ArrayList<Integer> productIDsArray = new ArrayList<>();
                        for (String productID:productIDsStringArray){
                            productIDsArray.add(Integer.parseInt(productID.trim()));
                        }
                        intent.putExtra(CatalogContract.ProductsTable.TABLE_NAME,productIDsArray);
                    }catch (Exception e){

                    }

                }
                break;
            case "OrderDetails":
                activityClass = AccountActivity.class;
                intent.putExtra(Constants.OPEN_FRAGMENT_KEY, OrderDetailsFragment.class.getSimpleName());
                String orderID = bundle.getString("orderID", "");
                if (orderID.equals("")){
                    return;
                }
                Bundle args = new Bundle();
                try {
                    args.putInt("orderID", Integer.parseInt(orderID.trim()));
                }catch (Exception e){
                    return;
                }
                intent.putExtras(args);
                break;
            case "Help":
                helpButtonClicked();
                return;
            default:
                return;
        }

        intent.setClass(getApplicationContext(), activityClass);
        startActivity(intent);
    }

    public void sendFragmentOpenBroadcast(String fragmentName){
        NavDrawerHelper.getInstance().setOpenActivity(this.getClass().getSimpleName());
        NavDrawerHelper.getInstance().setOpenFragment(fragmentName);
    }


}
