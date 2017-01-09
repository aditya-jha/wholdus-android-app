package com.wholdus.www.wholdusbuyerapp.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.databaseContracts.UserProfileContract;
import com.wholdus.www.wholdusbuyerapp.fragments.ForgotPasswordFragment;
import com.wholdus.www.wholdusbuyerapp.fragments.LoginFragment;
import com.wholdus.www.wholdusbuyerapp.fragments.OTPFragment;
import com.wholdus.www.wholdusbuyerapp.fragments.RegisterFragment;
import com.wholdus.www.wholdusbuyerapp.fragments.ResetPasswordFragment;
import com.wholdus.www.wholdusbuyerapp.helperClasses.APIConstants;
import com.wholdus.www.wholdusbuyerapp.helperClasses.Constants;
import com.wholdus.www.wholdusbuyerapp.helperClasses.TODO;
import com.wholdus.www.wholdusbuyerapp.interfaces.LoginSignupListenerInterface;
import com.wholdus.www.wholdusbuyerapp.services.BuyerProductService;
import com.wholdus.www.wholdusbuyerapp.services.CatalogService;
import com.wholdus.www.wholdusbuyerapp.services.OrderService;
import com.wholdus.www.wholdusbuyerapp.services.UserService;

import static java.security.AccessController.getContext;

public class LoginSignupActivity extends AppCompatActivity implements LoginSignupListenerInterface {

    private boolean mDoublePressToExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_signup);

        openFragment(new LoginFragment());
    }

    @Override
    protected void onResume() {
        super.onResume();
        mDoublePressToExit = false;
    }

    @Override
    public void onBackPressed() {
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

    private void openFragment(Fragment fragment) {
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

    @Override
    public void singupClicked(@Nullable String mobileNumber) {
        RegisterFragment fragment = new RegisterFragment();
        if (mobileNumber != null) {
            Bundle bundle = new Bundle();
            bundle.putString(UserProfileContract.UserTable.COLUMN_MOBILE_NUMBER, mobileNumber);
            fragment.setArguments(bundle);
        }
        openFragment(fragment);
    }

    @Override
    public void loginClicked(@Nullable String mobileNumber) {
        LoginFragment fragment = new LoginFragment();
        if (mobileNumber != null) {
            Bundle bundle = new Bundle();
            bundle.putString(UserProfileContract.UserTable.COLUMN_MOBILE_NUMBER, mobileNumber);
            fragment.setArguments(bundle);
        }
        openFragment(fragment);
    }

    @Override
    public void forgotPasswordClicked(@Nullable String mobileNumber) {
        ForgotPasswordFragment fragment = new ForgotPasswordFragment();
        if (mobileNumber != null) {
            Bundle bundle = new Bundle();
            bundle.putString(UserProfileContract.UserTable.COLUMN_MOBILE_NUMBER, mobileNumber);
            fragment.setArguments(bundle);
        }
        openFragment(fragment);
    }

    @Override
    public void resetPassword(@NonNull Bundle args) {
        ResetPasswordFragment fragment = new ResetPasswordFragment();
        fragment.setArguments(args);
        openFragment(fragment);
    }

    @Override
    public void loginSuccess() {
        // start HomeActivity and finish this activity

//        Intent categoryIntent = new Intent(this, CatalogService.class);
//        categoryIntent.putExtra("TODO", R.integer.fetch_categories);
//        categoryIntent.putExtra(getString(R.string.seller_category_details), true);
//        startService(categoryIntent);

        Intent userIntent = new Intent(this, UserService.class);
        userIntent.putExtra("TODO", TODO.FETCH_USER_PROFILE);
        startService(userIntent);

        Intent buyerProductIntent = new Intent(this, BuyerProductService.class);
        buyerProductIntent.putExtra("TODO", TODO.FETCH_BUYER_PRODUCTS);
        startService(buyerProductIntent);

        Intent orderIntent = new Intent(this, OrderService.class);
        orderIntent.putExtra("TODO", R.string.fetch_orders);
        startService(orderIntent);

        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra(Constants.OPEN_FRAGMENT_KEY, "home");
        startActivity(intent);
        finish();
    }

    @Override
    public void hideSoftKeyboard(View view) {
        try {
            if (view != null) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void openOTPFragment(@NonNull Bundle bundle) {
        OTPFragment fragment = new OTPFragment();
        fragment.setArguments(bundle);
        openFragment(fragment);
    }
}
