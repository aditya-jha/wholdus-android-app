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
import android.widget.Toast;

import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.databaseContracts.UserProfileContract;
import com.wholdus.www.wholdusbuyerapp.helperClasses.APIConstants;
import com.wholdus.www.wholdusbuyerapp.helperClasses.HelperFunctions;
import com.wholdus.www.wholdusbuyerapp.helperClasses.InputValidationHelper;
import com.wholdus.www.wholdusbuyerapp.helperClasses.IntentFilters;
import com.wholdus.www.wholdusbuyerapp.helperClasses.TODO;
import com.wholdus.www.wholdusbuyerapp.interfaces.LoginSignupListenerInterface;
import com.wholdus.www.wholdusbuyerapp.services.LoginAPIService;

/**
 * Created by aditya on 2/1/17.
 */

public class ForgotPasswordFragment extends Fragment implements View.OnClickListener, View.OnFocusChangeListener {

    private LoginSignupListenerInterface mListener;
    private TextInputLayout mMobileNumberWrapper;
    private TextInputEditText mMobileNumberEditText;
    private BroadcastReceiver mReceiver;
    private ProgressBar mProgressBar;

    private static final int RECEIVE_SMS_REQUEST = 1;

    public ForgotPasswordFragment() {}

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
        return inflater.inflate(R.layout.fragment_forgot_password, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMobileNumberEditText = (TextInputEditText) view.findViewById(R.id.mobile_number_edit_text);
        mMobileNumberWrapper = (TextInputLayout) view.findViewById(R.id.mobile_number_wrapper);

        mProgressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        mProgressBar.setVisibility(View.INVISIBLE);

        Button back = (Button) view.findViewById(R.id.back_button);
        back.setOnClickListener(this);

        Button requestOTPButton = (Button) view.findViewById(R.id.request_otp_button);
        requestOTPButton.setOnClickListener(this);

        Bundle bundle = getArguments();
        if (bundle != null) {
            mMobileNumberEditText.setText(bundle.getString(UserProfileContract.UserTable.COLUMN_MOBILE_NUMBER));
        }

        mMobileNumberEditText.setOnFocusChangeListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
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
        if (mProgressBar.getVisibility() == View.VISIBLE) return;
        final int ID = view.getId();
        switch (ID) {
            case R.id.request_otp_button:
                requestOTP(true);
                break;
            case R.id.back_button:
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
                requestOTP(false);
                break;
        }
    }

    @Nullable
    private String getValueFromEditText(TextInputEditText editText) {
        try {
            return editText.getText().toString();
        } catch (Exception e) {
            return null;
        }
    }

    private void handleAPIResponse(final Intent intent) {
        mProgressBar.setVisibility(View.INVISIBLE);
        if (intent.getIntExtra("TODO", -1) != TODO.FORGOT_PASSWORD) return;

        final int responseCode = intent.getIntExtra(APIConstants.RESPONSE_CODE, -1);

        switch (responseCode) {
            case 200:
                final String forgotPasswordToken = intent.getStringExtra(APIConstants.LOGIN_API_DATA);
                if (forgotPasswordToken != null) {
                    Bundle bundle = new Bundle();
                    bundle.putString(APIConstants.FORGOT_PASSWORD_TOKEN, forgotPasswordToken);
                    bundle.putString(UserProfileContract.UserTable.COLUMN_MOBILE_NUMBER, getValueFromEditText(mMobileNumberEditText));
                    mListener.resetPassword(bundle);
                }
                break;
            case 400:
                mMobileNumberWrapper.setError(intent.getStringExtra(APIConstants.LOGIN_API_DATA));
                break;
            default:
                if (HelperFunctions.isNetworkAvailable(getContext())) {
                    Toast.makeText(getContext(), getString(R.string.api_error_message), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), getString(R.string.no_internet_access), Toast.LENGTH_SHORT).show();
                }
        }
    }

    private void requestOTP(boolean requestPermission) {
        String mobileNumber = getValueFromEditText(mMobileNumberEditText);

        if (InputValidationHelper.isValidMobileNumber(mMobileNumberWrapper, mobileNumber)) {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED || !requestPermission) {
                mProgressBar.setVisibility(View.VISIBLE);
                requestOTPAPICall();
            } else {
                requestPermissions(new String[]{Manifest.permission.RECEIVE_SMS}, RECEIVE_SMS_REQUEST);
            }
        }
    }

    private void requestOTPAPICall() {
        String mobileNumber = getValueFromEditText(mMobileNumberEditText);

        if (InputValidationHelper.isValidMobileNumber(mMobileNumberWrapper, mobileNumber)) {
            Intent intent = new Intent(getContext(), LoginAPIService.class);
            intent.putExtra("TODO", TODO.FORGOT_PASSWORD);
            intent.putExtra(UserProfileContract.UserTable.COLUMN_MOBILE_NUMBER, mobileNumber);

            getActivity().startService(intent);
        }
    }
}
