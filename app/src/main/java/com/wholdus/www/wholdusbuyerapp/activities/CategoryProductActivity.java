package com.wholdus.www.wholdusbuyerapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.adapters.CategorySpinnerAdapter;
import com.wholdus.www.wholdusbuyerapp.fragments.NavigationDrawerFragment;
import com.wholdus.www.wholdusbuyerapp.fragments.ProductsGridFragment;
import com.wholdus.www.wholdusbuyerapp.loaders.CategoriesGridLoader;
import com.wholdus.www.wholdusbuyerapp.loaders.ProfileLoader;
import com.wholdus.www.wholdusbuyerapp.models.Buyer;
import com.wholdus.www.wholdusbuyerapp.models.Category;
import com.wholdus.www.wholdusbuyerapp.models.Product;

import java.util.ArrayList;

public class CategoryProductActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<Category>> {

    private DrawerLayout mDrawerLayout;
    private Toolbar mToolbar;
    private int mSelectedCategoryID;
    private static final int CATEGORY_LOADER = 0;
    private Spinner mCategorySpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_product);

        Intent intent = getIntent();
        mSelectedCategoryID = intent.getIntExtra(getString(R.string.selected_category_id), 0);

        // initialize the toolbar
        initToolbar();

        // initialize the navigation drawer
        initNavigationDrawer();

        mCategorySpinner = (Spinner) findViewById(R.id.category_spinner);
        mCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                /* TODO: Implement what happens when category is changed from toolbar dropdown */
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        getSupportLoaderManager().initLoader(CATEGORY_LOADER, null, this);

        openToFragment("", null);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
    public void onBackPressed() {
        if(getSupportFragmentManager().getBackStackEntryCount() == 1) {
            finish();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public Loader<ArrayList<Category>> onCreateLoader(int id, Bundle args) {
        return new CategoriesGridLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Category>> loader, ArrayList<Category> data) {
        updateToolbarSpinner(data);
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Category>> loader) {

    }

    private void initToolbar() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        // set default toolbar as the action bar for this activity
        mToolbar = (Toolbar) findViewById(R.id.default_toolbar);
        setSupportActionBar(mToolbar);
        try {
            getSupportActionBar().setTitle(null);
        } catch (Exception e) {
            e.printStackTrace();
        }

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

    private void openToFragment(String fragmentName, @Nullable Bundle bundle) {
        Fragment fragment;

        switch (fragmentName) {
            case "home":
                fragment = new ProductsGridFragment();
                break;
            default:
                fragment = new ProductsGridFragment();
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

    private void updateToolbarSpinner(ArrayList<Category> data) {
        CategorySpinnerAdapter adapter = new CategorySpinnerAdapter(this, data);
        mCategorySpinner.setAdapter(adapter);
        mCategorySpinner.setSelection(adapter.getPositionFromID(mSelectedCategoryID));
    }
}
