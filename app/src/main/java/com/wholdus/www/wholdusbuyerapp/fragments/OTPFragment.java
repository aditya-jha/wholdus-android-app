package com.wholdus.www.wholdusbuyerapp.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.SmsMessage;
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
import com.wholdus.www.wholdusbuyerapp.helperClasses.LoginHelper;
import com.wholdus.www.wholdusbuyerapp.helperClasses.TODO;
import com.wholdus.www.wholdusbuyerapp.interfaces.LoginSignupListenerInterface;
import com.wholdus.www.wholdusbuyerapp.services.FirebaseNotificationService;
import com.wholdus.www.wholdusbuyerapp.services.LoginAPIService;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by aditya on 30/12/16.
 */

public class OTPFragment extends Fragment implements View.OnClickListener {

    private LoginSignupListenerInterface mListener;
    private TextInputEditText mOTPEditText;
    private TextInputLayout mOTPWrapper;
    private String mRegistrationToken, mMobileNumber;
    private BroadcastReceiver mSMSReceiver, mReceiver;
    private ProgressBar mProgressBar;

    public OTPFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (LoginSignupListenerInterface) context;
        } catch (ClassCastException cee) {
            Log.e(this.getClass().getSimpleName(), " must implement " + LoginSignupListenerInterface.class.getSimpleName());
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSMSReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                handleSMSReceived(intent);
            }
        };

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
        Bundle args = getArguments();
        if (args != null) {
            mRegistrationToken = args.getString(APIConstants.REGISTRATION_TOKEN_KEY);
            mMobileNumber = args.getString(UserProfileContract.UserTable.COLUMN_MOBILE_NUMBER);
        } else {
            mListener.singupClicked(null);
        }
        return inflater.inflate(R.layout.fragment_otp, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mOTPEditText = (TextInputEditText) view.findViewById(R.id.otp_edit_text);
        mOTPWrapper = (TextInputLayout) view.findViewById(R.id.otp_wrapper);

        Button submitOTP = (Button) view.findViewById(R.id.submit_otp);
        submitOTP.setOnClickListener(this);

        Button resendOTP = (Button) view.findViewById(R.id.resend_otp);
        resendOTP.setOnClickListener(this);

        mProgressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter smsIntentFilter = new IntentFilter(IntentFilters.SMS_DATA);
        smsIntentFilter.setPriority(1000);
        getActivity().registerReceiver(mSMSReceiver, smsIntentFilter);

        IntentFilter APIListenerIF = new IntentFilter(IntentFilters.LOGIN_SIGNUP_DATA);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mReceiver, APIListenerIF);
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(mSMSReceiver);
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mReceiver);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View view) {
        mListener.hideSoftKeyboard(view);
        if (mProgressBar.getVisibility() == View.VISIBLE) {
            return;
        }
        final int ID = view.getId();
        switch (ID) {
            case R.id.resend_otp:
                resendOTP();
                break;
            case R.id.submit_otp:
                verifyOTP();
                break;
        }
    }

    private void resendOTP() {
        mProgressBar.setVisibility(View.VISIBLE);

        Intent intent = new Intent(getContext(), LoginAPIService.class);
        intent.putExtra("TODO", TODO.RESEND_OTP);
        intent.putExtra(APIConstants.REGISTRATION_TOKEN_KEY, mRegistrationToken);

        getActivity().startService(intent);
    }

    private void verifyOTP() {
        mProgressBar.setVisibility(View.VISIBLE);

        String OTP = mOTPEditText.getText().toString();

        if (InputValidationHelper.isValidOTP(mOTPWrapper, OTP)) {
            Intent intent = new Intent(getContext(), LoginAPIService.class);
            intent.putExtra("TODO", TODO.VERIFY_OTP);
            intent.putExtra(APIConstants.REGISTRATION_TOKEN_KEY, mRegistrationToken);
            intent.putExtra(APIConstants.OTP_NUMBER_KEY, OTP);

            getActivity().startService(intent);
        }
    }

    private void handleAPIResponse(final Intent intent) {
        mProgressBar.setVisibility(View.INVISIBLE);
        final int todo = intent.getIntExtra("TODO", -1);

        int responseCode = intent.getIntExtra(APIConstants.RESPONSE_CODE, 500);
        switch (responseCode) {
            case 200:
                if (TODO.VERIFY_OTP == todo) {
                    final LoginHelper loginHelper = new LoginHelper(getContext());
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (!loginHelper.checkIfLoggedIn()) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mListener.loginSuccess();
                                    }
                                });
                            } else {
                                getActivity().startService(new Intent(getActivity().getApplicationContext(), FirebaseNotificationService.class));
                                mListener.loginSuccess();
                            }
                        }
                    }).start();
                }
                break;
            case 403:
            case 400: {
                Toast.makeText(getContext(), getString(R.string.token_expired), Toast.LENGTH_SHORT).show();
                mListener.singupClicked(mMobileNumber);
                break;
            }
            default:
                if (HelperFunctions.isNetworkAvailable(getContext())) {
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
                if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION.equals(intent.getAction())) {
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
            }
        }).start();
    }
}
