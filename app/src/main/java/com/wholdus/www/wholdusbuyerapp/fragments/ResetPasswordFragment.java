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
import android.widget.Toast;

import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.databaseContracts.UserProfileContract;
import com.wholdus.www.wholdusbuyerapp.helperClasses.APIConstants;
import com.wholdus.www.wholdusbuyerapp.helperClasses.HelperFunctions;
import com.wholdus.www.wholdusbuyerapp.helperClasses.InputValidationHelper;
import com.wholdus.www.wholdusbuyerapp.helperClasses.IntentFilters;
import com.wholdus.www.wholdusbuyerapp.helperClasses.LoginHelper;
import com.wholdus.www.wholdusbuyerapp.helperClasses.TODO;
import com.wholdus.www.wholdusbuyerapp.interfaces.LoginSignupListenerInterface;
import com.wholdus.www.wholdusbuyerapp.services.LoginAPIService;

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

    public ResetPasswordFragment() {
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
                String intentAction = intent.getAction();
                switch (intentAction) {
                    case IntentFilters.LOGIN_SIGNUP_DATA:
                        handleAPIResponse(intent);
                        break;
                    case IntentFilters.SMS_DATA:
                        handleSMSReceived(intent);
                }
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

        Button resendOTP = (Button) view.findViewById(R.id.resend_otp);
        resendOTP.setOnClickListener(this);

        mForgotPasswordToken = getArguments().getString(APIConstants.FORGOT_PASSWORD_TOKEN, null);
        if (mForgotPasswordToken == null) {
            mListener.forgotPasswordClicked(getArguments().getString(UserProfileContract.UserTable.COLUMN_MOBILE_NUMBER));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter apiFilter = new IntentFilter(IntentFilters.LOGIN_SIGNUP_DATA);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mReceiver, apiFilter);

        IntentFilter smsFilter = new IntentFilter(IntentFilters.SMS_DATA);
        getActivity().registerReceiver(mReceiver, smsFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(mReceiver);
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mReceiver);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mListener = null;
    }

    @Override
    public void onClick(View view) {
        mListener.hideSoftKeyboard(view);
        if (mProgressBar.getVisibility() == View.VISIBLE) return;

        final int ID = view.getId();
        switch (ID) {
            case R.id.reset_password:
                resetPassword();
                break;
            case R.id.back_button:
                getFragmentManager().popBackStack();
                break;
            case R.id.resend_otp:
                resendOTP();
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

    private void handleAPIResponse(final Intent intent) {
        final int responseCode = intent.getIntExtra(APIConstants.RESPONSE_CODE, 500);

        switch (responseCode) {
            case 200:
                if (intent.getIntExtra("TODO", -1) == TODO.RESEND_OTP) {
                    mProgressBar.setVisibility(View.INVISIBLE);
                    return;
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        LoginHelper loginHelper = new LoginHelper(getContext());
                        if (loginHelper.checkIfLoggedIn()) {
                            if (getActivity() != null) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mProgressBar.setVisibility(View.INVISIBLE);
                                        Toast.makeText(getContext(), getString(R.string.password_reset_success), Toast.LENGTH_SHORT).show();
                                        mListener.loginSuccess(false);
                                    }
                                });
                            }
                        } else {
                            if (getActivity() != null) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mProgressBar.setVisibility(View.INVISIBLE);
                                        mListener.loginClicked(null);
                                    }
                                });
                            }
                        }
                    }
                }).start();
                break;
            case 400:
                mProgressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(getContext(), getString(R.string.invalid_otp_error), Toast.LENGTH_SHORT).show();
                break;
            case 403:
                mProgressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(getContext(), getString(R.string.token_expired), Toast.LENGTH_SHORT).show();
                break;
            default:
                mProgressBar.setVisibility(View.INVISIBLE);
                if (HelperFunctions.isNetworkAvailable(getActivity().getApplicationContext())) {
                    Toast.makeText(getContext(), getString(R.string.api_error_message), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), getString(R.string.no_internet_access), Toast.LENGTH_SHORT).show();
                }
        }
    }

    private void handleSMSReceived(final Intent intent) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String otp = HelperFunctions.getOTPFromSMS(intent);
                if (otp != null && getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mOTPEditText.setText(otp);
                        }
                    });
                }
            }
        }).start();
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

    private void resendOTP() {
        mProgressBar.setVisibility(View.VISIBLE);

        Intent intent = new Intent(getContext(), LoginAPIService.class);
        intent.putExtra("TODO", TODO.RESEND_OTP);
        intent.putExtra(APIConstants.FORGOT_PASSWORD_TOKEN, mForgotPasswordToken);

        getActivity().startService(intent);
    }
}
