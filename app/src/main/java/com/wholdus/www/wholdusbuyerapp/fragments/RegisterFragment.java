package com.wholdus.www.wholdusbuyerapp.fragments;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.databaseContracts.UserProfileContract;
import com.wholdus.www.wholdusbuyerapp.helperClasses.APIConstants;
import com.wholdus.www.wholdusbuyerapp.helperClasses.Constants;
import com.wholdus.www.wholdusbuyerapp.helperClasses.InputValidationHelper;
import com.wholdus.www.wholdusbuyerapp.helperClasses.IntentFilters;
import com.wholdus.www.wholdusbuyerapp.helperClasses.TODO;
import com.wholdus.www.wholdusbuyerapp.interfaces.LoginSignupListenerInterface;
import com.wholdus.www.wholdusbuyerapp.services.LoginAPIService;

/**
 * Created by aditya on 30/12/16.
 */

public class RegisterFragment extends Fragment implements View.OnClickListener, View.OnFocusChangeListener {

    private LoginSignupListenerInterface mListener;
    private TextInputEditText mPasswordEditText, mMobileNumberEditText, mNameEditText;
    private TextInputLayout mMobileNumberWrapper, mPasswordWrapper, mNameWrapper;
    private ProgressBar mProgressBar;
    private BroadcastReceiver mReceiver;

    private static final int RECEIVE_SMS_REQUEST = 1;

    public RegisterFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mListener = (LoginSignupListenerInterface) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                handleAPIResponse(intent);
            }
        };
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mProgressBar = (ProgressBar) view.findViewById(R.id.progress_bar);

        Button registerButton = (Button) view.findViewById(R.id.register_submit_button);
        registerButton.setOnClickListener(this);

        TextView loginText = (TextView) view.findViewById(R.id.login_text);
        loginText.setOnClickListener(this);

        mMobileNumberEditText = (TextInputEditText) view.findViewById(R.id.mobile_number_edit_text);
        mMobileNumberEditText.setOnFocusChangeListener(this);

        mNameEditText = (TextInputEditText) view.findViewById(R.id.name_edit_text);
        mNameEditText.setOnFocusChangeListener(this);

        mPasswordEditText = (TextInputEditText) view.findViewById(R.id.password_edit_text);
        mPasswordEditText.setOnFocusChangeListener(this);

        mMobileNumberWrapper = (TextInputLayout) view.findViewById(R.id.mobile_number_wrapper);
        mPasswordWrapper = (TextInputLayout) view.findViewById(R.id.password_wrapper);
        mNameWrapper = (TextInputLayout) view.findViewById(R.id.name_wrapper);

        Bundle args = getArguments();
        if (args != null) {
            mMobileNumberEditText.setText(args.getString(UserProfileContract.UserTable.COLUMN_MOBILE_NUMBER, ""));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mProgressBar.setVisibility(View.INVISIBLE);
        IntentFilter intentFilter = new IntentFilter(IntentFilters.LOGIN_SIGNUP_DATA);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mReceiver, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mReceiver);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View view) {
        final int ID = view.getId();
        switch (ID) {
            case R.id.register_submit_button:
                mListener.hideSoftKeyboard(view);
                registerUser(true);
                break;
            case R.id.login_text:
                mListener.loginClicked(getValueFromEditText(mMobileNumberEditText));
                break;
        }
    }

    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        final String emptyText = getValueFromEditText((TextInputEditText) view);

        if (emptyText != null && ((!hasFocus && emptyText.isEmpty()) || (hasFocus && emptyText.isEmpty()))) {
            ((TextInputEditText) view).setError(null);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case RECEIVE_SMS_REQUEST:
                registerUser(false);
                break;
        }
    }

    private String getValueFromEditText(TextInputEditText editText) {
        try {
            return editText.getText().toString();
        } catch (Exception e) {
            return null;
        }
    }

    private void handleAPIResponse(final Intent intent) {
        mProgressBar.setVisibility(View.INVISIBLE);
        Bundle extras = intent.getExtras();

        if (extras.getInt("TODO", -1) != TODO.REGISTER) {
            return;
        }

        int responseCode = extras.getInt(APIConstants.RESPONSE_CODE);
        String data = extras.getString(APIConstants.LOGIN_API_DATA, null);

        switch (responseCode) {
            case 200:
                Bundle bundle = new Bundle();
                bundle.putString(APIConstants.REGISTRATION_TOKEN_KEY, data);
                bundle.putString(UserProfileContract.UserTable.COLUMN_MOBILE_NUMBER, getValueFromEditText(mMobileNumberEditText));
                bundle.putString(Constants.BACK_FRAGMENT, this.getClass().getSimpleName());
                mListener.openOTPFragment(bundle);
                break;
            case 400:
                mMobileNumberWrapper.setError(data);
                break;
        }
    }

    private void registerUser(boolean requestPermission) {
        if (mProgressBar.getVisibility() == View.VISIBLE) {
            return;
        }

        String mobileNumber = getValueFromEditText(mMobileNumberEditText);
        String password = getValueFromEditText(mPasswordEditText);
        String name = getValueFromEditText(mNameEditText);

        if (InputValidationHelper.isNameValid(mNameWrapper, name)
                && InputValidationHelper.isValidMobileNumber(mMobileNumberWrapper, mobileNumber)
                && InputValidationHelper.isValidPassword(mPasswordWrapper, password)) {

            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED || !requestPermission) {
                registerUserAPICall();
            } else {
                requestPermissions(new String[]{Manifest.permission.RECEIVE_SMS}, RECEIVE_SMS_REQUEST);
            }
        }
    }

    private void registerUserAPICall() {
        String mobileNumber = getValueFromEditText(mMobileNumberEditText);
        String password = getValueFromEditText(mPasswordEditText);
        String name = getValueFromEditText(mNameEditText);

        mProgressBar.setVisibility(View.VISIBLE);

        Intent intent = new Intent(getContext(), LoginAPIService.class);
        intent.putExtra("TODO", TODO.REGISTER);
        intent.putExtra(UserProfileContract.UserTable.COLUMN_MOBILE_NUMBER, mobileNumber);
        intent.putExtra(UserProfileContract.UserTable.COLUMN_NAME, name);
        intent.putExtra(UserProfileContract.UserTable.COLUMN_PASSWORD, password);
        getActivity().startService(intent);
    }
}
