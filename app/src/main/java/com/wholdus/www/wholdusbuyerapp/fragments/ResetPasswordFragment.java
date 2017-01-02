package com.wholdus.www.wholdusbuyerapp.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.databaseContracts.UserProfileContract;
import com.wholdus.www.wholdusbuyerapp.helperClasses.APIConstants;
import com.wholdus.www.wholdusbuyerapp.helperClasses.HelperFunctions;
import com.wholdus.www.wholdusbuyerapp.helperClasses.InputValidationHelper;
import com.wholdus.www.wholdusbuyerapp.helperClasses.IntentFilters;
import com.wholdus.www.wholdusbuyerapp.helperClasses.TODO;
import com.wholdus.www.wholdusbuyerapp.interfaces.LoginSignupListenerInterface;
import com.wholdus.www.wholdusbuyerapp.services.LoginAPIService;

import static android.os.Build.ID;

/**
 * Created by aditya on 2/1/17.
 */

public class ResetPasswordFragment extends Fragment implements View.OnClickListener, View.OnFocusChangeListener {

    private LoginSignupListenerInterface mListener;
    private BroadcastReceiver mReceiver;

    private TextInputLayout mOTPWrapper, mPasswordWrapper;
    private TextInputEditText mOTPEditText, mPasswordEditText;
    private ProgressBar mProgressBar;
    private String mForgotPasswordToken;

    public ResetPasswordFragment() {}

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (LoginSignupListenerInterface) context;
        } catch (ClassCastException cee) {
            cee.printStackTrace();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mForgotPasswordToken = getArguments().getString(APIConstants.FORGOT_PASSWORD_TOKEN, null);
        if (mForgotPasswordToken == null) {
            mListener.forgotPasswordClicked(getArguments().getString(UserProfileContract.UserTable.COLUMN_MOBILE_NUMBER));
            return;
        }
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, final Intent intent) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String intentAction = intent.getAction();
                        switch (intentAction) {
                            case IntentFilters.LOGIN_SIGNUP_DATA:
                                handleAPIResponse(intent);
                                break;
                            case IntentFilters.SMS_DATA:
                                handleSMSReceived(intent);
                        }
                    }
                }).start();
            }
        };
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reset_password, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mOTPEditText = (TextInputEditText) view.findViewById(R.id.otp_edit_text);
        mOTPWrapper = (TextInputLayout) view.findViewById(R.id.otp_wrapper);

        mPasswordEditText = (TextInputEditText) view.findViewById(R.id.password_edit_text);
        mPasswordWrapper = (TextInputLayout) view.findViewById(R.id.password_wrapper);

        mOTPEditText.setOnFocusChangeListener(this);
        mPasswordEditText.setOnFocusChangeListener(this);

        mProgressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        mProgressBar.setVisibility(View.INVISIBLE);

        Button resetPassword = (Button) view.findViewById(R.id.reset_password);
        resetPassword.setOnClickListener(this);

        Button backButton = (Button) view.findViewById(R.id.back_button);
        backButton.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(IntentFilters.LOGIN_SIGNUP_DATA);
        intentFilter.addAction(IntentFilters.SMS_DATA);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mReceiver, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mReceiver);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mListener = null;
    }

    @Override
    public void onClick(View view) {
        if (mProgressBar.getVisibility() == View.VISIBLE) return;

        final int ID = view.getId();
        switch (ID) {
            case R.id.reset_password:
                resetPassword();
                break;
            case R.id.back_button:
                getFragmentManager().popBackStack();
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

    @Nullable
    private String getValueFromEditText(TextInputEditText editText) {
        try {
            return editText.getText().toString();
        } catch (Exception e) {
            return null;
        }
    }

    private void handleAPIResponse(Intent intent) {
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    private void handleSMSReceived(Intent intent) {
        final String otp = HelperFunctions.getOTPFromSMS(intent);
        if (otp != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mOTPEditText.setText(otp);
                }
            });
        }
    }

    private void resetPassword() {
        String OTP = getValueFromEditText(mOTPEditText);
        String newPassword = getValueFromEditText(mPasswordEditText);

        if (InputValidationHelper.isValidOTP(mOTPWrapper, OTP) &&
                InputValidationHelper.isValidPassword(mPasswordWrapper, newPassword)) {
            mProgressBar.setVisibility(View.VISIBLE);
            Intent intent = new Intent(getContext(), LoginAPIService.class);
            intent.putExtra("TODO", TODO.FORGOT_PASSWORD_VERIFY);
            intent.putExtra(UserProfileContract.UserTable.COLUMN_PASSWORD, newPassword);
            intent.putExtra(APIConstants.OTP_NUMBER_KEY, OTP);
            intent.putExtra(APIConstants.FORGOT_PASSWORD_TOKEN, mForgotPasswordToken);

            getActivity().startService(intent);
        }
    }
}
