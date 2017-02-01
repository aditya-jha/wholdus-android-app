package com.wholdus.www.wholdusbuyerapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.fragments.NotificationFragment;
import com.wholdus.www.wholdusbuyerapp.helperClasses.Constants;
import com.wholdus.www.wholdusbuyerapp.interfaces.NotificationListenerInterface;

/**
 * Created by kaustubh on 11/1/17.
 */

public class NotificationActivity extends AppCompatActivity implements NotificationListenerInterface {
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        initToolbar();
        Intent intent = getIntent();
        Bundle extras;
        if (intent != null) {
            extras = intent.getExtras();
        } else {
            extras = null;
        }

        openToFragment(getFragmentToOpenName(savedInstanceState), extras);
    }

    private void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.default_toolbar);
        setSupportActionBar(mToolbar);
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

        if (fragmentName == null){
            fragmentName = "notification";
        }

        switch (fragmentName) {
            case "notification":
                fragment = new NotificationFragment();
                break;
            default:
                fragment = new NotificationFragment();
        }

        fragment.setArguments(bundle);
        String backStateName = fragment.getClass().getSimpleName();
        FragmentManager fm = getSupportFragmentManager();
        boolean fragmentPopped = fm.popBackStackImmediate(backStateName, 0);

        if (!fragmentPopped) {
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.fragment_container, fragment, fragment.getClass().getSimpleName());
            ft.addToBackStack(backStateName);
            ft.commit();
        }
    }
}
