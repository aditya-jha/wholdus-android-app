package com.wholdus.www.wholdusbuyerapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.inputmethod.InputMethodManager;

import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.fragments.LoginFragment;
import com.wholdus.www.wholdusbuyerapp.interfaces.LoginSignupListenerInterface;

public class LoginSignupActivity extends AppCompatActivity implements LoginSignupListenerInterface {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_signup);

        LoginFragment loginFragment = new LoginFragment();
        openFragment(loginFragment);
    }

    private void openFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment, fragment.getClass().getSimpleName());
        fragmentTransaction.commit();
    }

    @Override
    public void singupClicked() {

    }

    @Override
    public void loginClicked() {

    }

    @Override
    public void forgotPasswordClicked(String mobileNumber) {
        Bundle bundle = new Bundle();
        bundle.putString("moibleNumber", mobileNumber);
    }

    @Override
    public void permissionsBottomSheet(boolean show) {

    }

    @Override
    public void requestReceiveSMSPermission() {

    }

    @Override
    public void loginSuccess() {
        // start HomeActivity and finish this activity
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }

    @Override
    public void hideSoftKeyboard() {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
