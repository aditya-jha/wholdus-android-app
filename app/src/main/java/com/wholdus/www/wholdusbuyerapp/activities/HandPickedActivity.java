package com.wholdus.www.wholdusbuyerapp.activities;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.databaseContracts.CatalogContract;
import com.wholdus.www.wholdusbuyerapp.fragments.FilterFragment;
import com.wholdus.www.wholdusbuyerapp.fragments.HandPickedFragment;
import com.wholdus.www.wholdusbuyerapp.helperClasses.CartMenuItemHelper;
import com.wholdus.www.wholdusbuyerapp.helperClasses.Constants;
import com.wholdus.www.wholdusbuyerapp.helperClasses.FilterClass;
import com.wholdus.www.wholdusbuyerapp.interfaces.CartDialogListener;
import com.wholdus.www.wholdusbuyerapp.interfaces.CategoryProductListenerInterface;
import com.wholdus.www.wholdusbuyerapp.interfaces.HandPickedListenerInterface;

import static android.support.v4.app.FragmentManager.POP_BACK_STACK_INCLUSIVE;

public class HandPickedActivity extends AppCompatActivity implements HandPickedListenerInterface,
        CategoryProductListenerInterface, CartDialogListener {

    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hand_picked);

        initToolbar();
        Intent intent = getIntent();
        Bundle extras;
        if (intent != null){
            extras = intent.getExtras();
        } else {
            extras = null;
        }

        //FilterClass.resetCategoryFilter();
        //FilterClass.resetFilter();

        openToFragment(getFragmentToOpenName(savedInstanceState), extras);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        FilterClass.resetFilter();
        FilterClass.resetCategoryFilter();
        super.onStop();
    }

    private void initToolbar(){
        mToolbar = (Toolbar) findViewById(R.id.default_toolbar);
        setSupportActionBar(mToolbar);
    }

    @Override
    public void fragmentCreated(String title) {
        mToolbar.setTitle(title);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
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
    public void showMenuButtonInToolbar() {

    }

    @Override
    public void filterFragmentActive(boolean isActive) {

    }

    @Override
    public void dismissDialog() {
        HandPickedFragment fragment = (HandPickedFragment) getSupportFragmentManager().findFragmentById(R.id.handpicked_fragment_container);
        if (fragment != null) {
            fragment.dismissDialog();
        }
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

        switch (fragmentName) {
            case "handpicked":
                fragment = new HandPickedFragment();
                break;
            case "filter":
                if (bundle == null){
                    bundle = new Bundle();
                }
                bundle.putBoolean("CategoryDisplayed", true);
                fragment = new FilterFragment();
                break;
            default:
                fragment = new HandPickedFragment();
        }

        fragment.setArguments(bundle);
        String backStateName = fragment.getClass().getSimpleName();
        FragmentManager fm = getSupportFragmentManager();
        boolean fragmentPopped =  fm.popBackStackImmediate(backStateName, 0);

        if(!fragmentPopped) {
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.handpicked_fragment_container, fragment, fragment.getClass().getSimpleName());
            ft.addToBackStack(backStateName);
            ft.commit();
        }

    }

    public void openProductDetails(int productID){
        Intent intent = new Intent(this, ProductDetailActivity.class);
        intent.putExtra(CatalogContract.ProductsTable.TABLE_NAME, productID);
        startActivity(intent);
    }

    @Override
    public void openFilter(boolean open) {
        if (open) {
            openToFragment("filter", null);
        } else {
            onBackPressed();
        }
    }

    @Override
    public void sortClicked() {
    }

    @Override
    public void applyFilter() {
        //onBackPressed();
        //TODO if open directly or clear from back stack first
        openToFragment("handpicked", null);
        /*
        HandPickedFragment fragment =(HandPickedFragment) getSupportFragmentManager().findFragmentByTag(HandPickedFragment.class.getSimpleName());
        if (fragment != null){
            fragment.resetProducts();
            fragment.updateProducts();
        }*/
    }

}
