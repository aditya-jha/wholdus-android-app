package com.wholdus.www.wholdusbuyerapp.activities;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.fragments.CartSummaryFragment;
import com.wholdus.www.wholdusbuyerapp.interfaces.CartListenerInterface;
import com.wholdus.www.wholdusbuyerapp.interfaces.UserAddressInterface;

public class CartActivity extends AppCompatActivity implements CartListenerInterface, UserAddressInterface {

    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        initToolbar();
        openToFragment(getFragmentToOpenName(savedInstanceState), null);
    }

    private void initToolbar(){
        mToolbar = (Toolbar) findViewById(R.id.default_toolbar);
        setSupportActionBar(mToolbar);
    }

    @Override
    public void fragmentCreated(String title, boolean backEnabled) {
        mToolbar.setTitle(title);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
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
    public void editAddress(int addressID, int _ID) {
        Bundle bundle = new Bundle();
        bundle.putInt("addressID", addressID);
        bundle.putInt("_ID", _ID);
        openToFragment("editAddress", bundle);
    }

    @Override
    public void addressSaved() {
        //TODO Open address select fragment
    }

    @Override
    public void addressClicked(int addressID, int _ID) {
        //TODO : SELECT THIS ADDRESS
    }

    private String getFragmentToOpenName(Bundle savedInstanceState) {
        String openFragment;
        if (savedInstanceState == null) {
            openFragment = getIntent().getStringExtra(getString(R.string.open_fragment_key));
        } else {
            openFragment = (String) savedInstanceState.getSerializable(getString(R.string.open_fragment_key));
        }
        if (openFragment == null) {
            openFragment = "";
        }
        return openFragment;
    }

    private void openToFragment(String fragmentName, @Nullable Bundle bundle) {
        Fragment fragment;

        switch (fragmentName) {
            case "cart_summary":
                fragment = new CartSummaryFragment();
                break;
            default:
                fragment = new CartSummaryFragment();
        }

        fragment.setArguments(bundle);
        String backStateName = fragment.getClass().getName();
        FragmentManager fm = getSupportFragmentManager();

        boolean fragmentPopped = fm.popBackStackImmediate(backStateName, 0);

        if (!fragmentPopped) { // fragment not in backstack create it
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.cart_fragment_container, fragment, fragment.getClass().getSimpleName());
            ft.addToBackStack(backStateName);
            ft.commit();
        }

    }

}
