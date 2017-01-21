package com.wholdus.www.wholdusbuyerapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.adapters.CategorySpinnerAdapter;
import com.wholdus.www.wholdusbuyerapp.fragments.FilterFragment;
import com.wholdus.www.wholdusbuyerapp.fragments.NavigationDrawerFragment;
import com.wholdus.www.wholdusbuyerapp.fragments.ProductsGridFragment;
import com.wholdus.www.wholdusbuyerapp.helperClasses.CartMenuItemHelper;
import com.wholdus.www.wholdusbuyerapp.helperClasses.Constants;
import com.wholdus.www.wholdusbuyerapp.helperClasses.FilterClass;
import com.wholdus.www.wholdusbuyerapp.helperClasses.NavDrawerHelper;
import com.wholdus.www.wholdusbuyerapp.interfaces.CategoryProductListenerInterface;
import com.wholdus.www.wholdusbuyerapp.loaders.CategoriesGridLoader;
import com.wholdus.www.wholdusbuyerapp.models.Category;

import java.util.ArrayList;

public class CategoryProductActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<ArrayList<Category>>, CategoryProductListenerInterface {

    private DrawerLayout mDrawerLayout;
    private Toolbar mToolbar;
    private static final int CATEGORY_LOADER = 0;
    private Spinner mCategorySpinner;
    private boolean mFilterFragmentActive;
    private int mType;

    private CartMenuItemHelper mCartMenuItemHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_product);

        Intent intent = getIntent();
        FilterClass.setCategoryID(intent.getIntExtra(getString(R.string.selected_category_id), -1));
        mType = intent.getIntExtra(Constants.TYPE, Constants.ALL_PRODUCTS);
        sendFragmentOpenBroadcast(ProductsGridFragment.class.getSimpleName());

        mFilterFragmentActive = false;

        // initialize the toolbar
        initToolbar();

        // initialize the navigation drawer
        initNavigationDrawer();

        mCategorySpinner = (Spinner) findViewById(R.id.category_spinner);

        if (mType == Constants.ALL_PRODUCTS) {
            mCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    int oldCategoryID = FilterClass.getCategoryID();
                    FilterClass.setCategoryID((int) mCategorySpinner.getSelectedItemId());
                    if (!mFilterFragmentActive) {
                        if (oldCategoryID != FilterClass.getCategoryID()) {
                            FilterClass.resetFilter();
                            updateProducts();
                        }
                    } else {
                        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                        if (fragment instanceof FilterFragment) {
                            ((FilterFragment) fragment).categoryIDChanged(oldCategoryID);
                        }
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
            getSupportLoaderManager().initLoader(CATEGORY_LOADER, null, this);
        } else {
            mCategorySpinner.setVisibility(View.INVISIBLE);
        }

        updateProducts();
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
                shortlistIntent.getIntExtra(getString(R.string.selected_category_id), 1);
                startActivity(shortlistIntent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mCartMenuItemHelper != null) {
            mCartMenuItemHelper.restartLoader();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FilterClass.resetFilter();
        FilterClass.resetCategoryFilter();
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
            NavDrawerHelper.getInstance().setType(Constants.ALL_PRODUCTS);
            finish();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public Loader<ArrayList<Category>> onCreateLoader(int id, Bundle args) {
        return new CategoriesGridLoader(this, false);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Category>> loader, ArrayList<Category> data) {
        if (data != null && !data.isEmpty()) {
            updateToolbarSpinner(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Category>> loader) {

    }

    @Override
    public void openFilter(boolean open) {
        if (open) {
            openToFragment(FilterFragment.class.getSimpleName(), new Bundle());
        } else {
            onBackPressed();
        }
    }

    @Override
    public void sortClicked() {
        updateProducts();
    }

    @Override
    public void applyFilter() {
        //onBackPressed();
        openToFragment("", new Bundle());
    }

    @Override
    public void filterFragmentActive(boolean isActive) {
        mFilterFragmentActive = isActive;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (mType == Constants.FAV_PRODUCTS) {
            MenuItem menuItem = menu.findItem(R.id.action_bar_shortlist);
            if (menuItem != null) {
                menuItem.setVisible(false);
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }

    private void initToolbar() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        // set default toolbar as the action bar for this activity
        mToolbar = (Toolbar) findViewById(R.id.spinner_toolbar);
        setSupportActionBar(mToolbar);
        try {
            if (mType == Constants.FAV_PRODUCTS) {
                getSupportActionBar().setTitle(getString(R.string.shortlist_title));
            } else if (mType == Constants.REJECTED_PRODUCTS) {
                getSupportActionBar().setTitle(getString(R.string.rejected_title));
            } else {
                getSupportActionBar().setTitle(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showBackButtonInToolbar() {
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                showMenuButtonInToolbar();
            }
        });
    }

    @Override
    public void showMenuButtonInToolbar() {
        mToolbar.setNavigationIcon(R.drawable.ic_menu_black_24dp);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
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

    private void updateProducts() {
        ProductsGridFragment fragment = (ProductsGridFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (fragment != null) {
            fragment.loadData();
        } else {
            // fragment is not added yet
            openToFragment("", new Bundle());
        }
    }

    private void openToFragment(String fragmentName, @NonNull Bundle bundle) {
        Fragment fragment;

        if (fragmentName.equals(FilterFragment.class.getSimpleName())) {
            mFilterFragmentActive = true;
            showBackButtonInToolbar();
            if (mType != Constants.ALL_PRODUCTS) {
                bundle.putBoolean("CategoryDisplayed", true);
            }
            fragment = new FilterFragment();
        } else {
            mFilterFragmentActive = false;
            bundle.putInt(Constants.TYPE, mType);
            showMenuButtonInToolbar();
            fragment = new ProductsGridFragment();
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

    public void sendFragmentOpenBroadcast(String fragmentName){
        NavDrawerHelper.getInstance().setOpenActivity(this.getClass().getSimpleName());
        NavDrawerHelper.getInstance().setOpenFragment(fragmentName);
        NavDrawerHelper.getInstance().setType(mType);
    }

    private void updateToolbarSpinner(ArrayList<Category> data) {
        CategorySpinnerAdapter adapter = new CategorySpinnerAdapter(this, data);
        mCategorySpinner.setAdapter(adapter);
        mCategorySpinner.setSelection(adapter.getPositionFromID(FilterClass.getCategoryID()));
    }
}
