package com.wholdus.www.wholdusbuyerapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.fragments.CategoryGridFragment;
import com.wholdus.www.wholdusbuyerapp.fragments.GetBusinessTypeFragment;
import com.wholdus.www.wholdusbuyerapp.fragments.GetWhatsappNumberFragment;
import com.wholdus.www.wholdusbuyerapp.fragments.HomeFragment;
import com.wholdus.www.wholdusbuyerapp.helperClasses.Constants;
import com.wholdus.www.wholdusbuyerapp.interfaces.HomeListenerInterface;
import com.wholdus.www.wholdusbuyerapp.interfaces.OnBoardingListenerInterface;

/**
 * Created by kaustubh on 16/1/17.
 */

public class OnBoardingActivity extends AppCompatActivity implements OnBoardingListenerInterface, HomeListenerInterface {

    private Toolbar mToolbar;
    private Button mSubmitButton;
    private ProgressBar mProgressBar;
    private TextView mStepCount;
    private TextView mStatement;
    private boolean mDoublePressToExit;
    private int mSelectedPosition = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);
        initToolbar();
        initReferences();
        openToFragment(getFragmentToOpenName(savedInstanceState), null);
    }

    private void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.default_toolbar);
        setSupportActionBar(mToolbar);
    }

    @Override
    public void changeProgressBarState(boolean state) {
        if (state) {
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mProgressBar.setVisibility(View.GONE);
        }
    }

    private void initReferences() {
        mProgressBar = (ProgressBar) findViewById(R.id.loading_indicator);
        mProgressBar.setVisibility(View.VISIBLE);
        mSubmitButton = (Button) findViewById(R.id.onboarding_submit_button);
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitButtonClicked();
            }
        });
        mSubmitButton.setEnabled(false);

        mStepCount = (TextView) findViewById(R.id.onboarding_step_number);
        mStatement = (TextView) findViewById(R.id.onboarding_statement);

    }

    @Override
    public void fragmentCreated(String title, boolean backEnabled) {
        mToolbar.setTitle(title);
        if (backEnabled) {
            mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
            mToolbar.setNavigationContentDescription("backEnabled");
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
        } else {
            mToolbar.setNavigationIcon(null);
            mToolbar.setNavigationContentDescription(null);
        }
        mProgressBar.setVisibility(View.GONE);
        mSubmitButton.setEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.skip_action_button, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_bar_skip:
                startHomeActivity();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void startHomeActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra(Constants.OPEN_FRAGMENT_KEY, HomeFragment.class.getSimpleName());
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);

        if (fragment instanceof CategoryGridFragment) {
            getSupportFragmentManager().popBackStack();
            openToFragment(GetWhatsappNumberFragment.class.getSimpleName(), new Bundle());
            return;
        }

        if (fragment instanceof GetWhatsappNumberFragment) {
            getSupportFragmentManager().popBackStack();
            openToFragment(GetBusinessTypeFragment.class.getSimpleName(), new Bundle());
            return;
        }

        if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
            if (!mDoublePressToExit) {
                mDoublePressToExit = true;

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mDoublePressToExit = false;
                    }
                }, 3000);
            } else {
                startHomeActivity();
            }
        } else {
            super.onBackPressed();
        }
    }

    private void openToFragment(String fragmentName, @Nullable Bundle bundle) {
        Fragment fragment;

        if (fragmentName.equals(GetWhatsappNumberFragment.class.getSimpleName())) {
            fragment = new GetWhatsappNumberFragment();
            mStepCount.setText("Step 2 of 3");
            mStatement.setText("Please let us know your whatsapp number so that we can update you");
        } else if (fragmentName.equals(GetBusinessTypeFragment.class.getSimpleName())) {
            if (bundle == null) {
                bundle = new Bundle();
            }
            bundle.putInt("businessTypeSelectedPosition", mSelectedPosition);
            fragment = new GetBusinessTypeFragment();
            mStepCount.setText("Step 1 of 3");
            mStatement.setText("Please let us know what business you run");
        } else if (fragmentName.equals(CategoryGridFragment.class.getSimpleName())) {
            fragment = new CategoryGridFragment();
            mStatement.setText("Tell us your favourite categories so we can provide you products to your liking");
            mStepCount.setText("Step 3 of 3");
        } else {
            if (bundle == null) {
                bundle = new Bundle();
            }
            bundle.putInt("businessTypeSelectedPosition", mSelectedPosition);
            fragment = new GetBusinessTypeFragment();
            mStepCount.setText("Step 1 of 3");
            mStatement.setText("Please let us know what business you run");
        }

        fragment.setArguments(bundle);
        String backStateName = fragment.getClass().getName();
        FragmentManager fm = getSupportFragmentManager();

        boolean fragmentPopped = fm.popBackStackImmediate(backStateName, 0);

        if (!fragmentPopped) { // fragment not in backstack create it
            mProgressBar.setVisibility(View.VISIBLE);
            mSubmitButton.setEnabled(false);
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.fragment_container, fragment, fragment.getClass().getSimpleName());
            ft.addToBackStack(backStateName);
            ft.commit();
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

    private void submitButtonClicked() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);

        if (fragment instanceof GetWhatsappNumberFragment) {
            GetWhatsappNumberFragment activeFragment = (GetWhatsappNumberFragment) fragment;
            activeFragment.updateWhatsappNumber();
        } else if (fragment instanceof GetBusinessTypeFragment) {
            GetBusinessTypeFragment activeFragment = (GetBusinessTypeFragment) fragment;
            activeFragment.updateBusinessType();
        } else {
            startHomeActivity();
        }
    }

    @Override
    public void businessTypeSaved(int position) {
        mSelectedPosition = position;
        openToFragment(GetWhatsappNumberFragment.class.getSimpleName(), null);
    }

    @Override
    public void whatsappNumberSaved() {
        openToFragment(CategoryGridFragment.class.getSimpleName(), null);
    }

    @Override
    public void helpButtonClicked() {

    }

    @Override
    public void openCategory(int categoryID) {

    }
}
