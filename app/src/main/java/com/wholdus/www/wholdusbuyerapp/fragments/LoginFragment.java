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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.aynctasks.LoginHelperAsyncTask;
import com.wholdus.www.wholdusbuyerapp.helperClasses.InputValidationHelper;
import com.wholdus.www.wholdusbuyerapp.interfaces.LoginSignupListenerInterface;
import com.wholdus.www.wholdusbuyerapp.services.UserAPIService;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by aditya on 16/11/16.
 */

public class LoginFragment extends Fragment {

    private LoginSignupListenerInterface listener;
    private final String LOG_TAG = LoginFragment.class.getSimpleName();
    private TextInputLayout mMobileNumberWrapper;
    private TextInputLayout mPasswordWrapper;
    private TextInputEditText mMobileNumberEditText;
    private TextInputEditText mPasswordEditText;
    private UserAPIService mUserAPIService;

    private BroadcastReceiver mUserAPIServiceResponseReceiver;

    public LoginFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (LoginSignupListenerInterface) context;
        } catch (ClassCastException cce) {
            Log.d(LOG_TAG, LOG_TAG + " must implement listener interface");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_login, container, false);

        mUserAPIServiceResponseReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String data = intent.getStringExtra(getString(R.string.api_response_data_key));
                handleLoginAPIResponse(data);
            }
        };

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                initFragment(rootView);
            }
        };
        new Thread(runnable).start();

        return rootView;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public void onStart() {
        super.onStart();

        IntentFilter intentFilter = new IntentFilter(getString(R.string.api_response));
        getContext().registerReceiver(mUserAPIServiceResponseReceiver, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        getContext().unregisterReceiver(mUserAPIServiceResponseReceiver);
    }

    private void initFragment(ViewGroup rootView) {
        Button loginSubmitButton = (Button) rootView.findViewById(R.id.login_submit_button);
        loginSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleLoginSubmitButtonClick();
            }
        });

        TextView signupText = (TextView) rootView.findViewById(R.id.signup_text);
        signupText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.singupClicked();
            }
        });

        TextView forgotPasswordTextView = (TextView) rootView.findViewById(R.id.forgot_password_textView);
        forgotPasswordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.forgotPasswordClicked(getMobileNumber());
            }
        });

        mMobileNumberWrapper = (TextInputLayout) rootView.findViewById(R.id.mobile_number_wrapper);
        mPasswordWrapper = (TextInputLayout) rootView.findViewById(R.id.password_wrapper);

        mMobileNumberEditText = (TextInputEditText) rootView.findViewById(R.id.mobile_number_editText);
        mPasswordEditText = (TextInputEditText) rootView.findViewById(R.id.password_editText);

        mMobileNumberEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                boolean emptyText = getMobileNumber().isEmpty();
                if ((!hasFocus && emptyText) || (hasFocus && emptyText)) {
                    mMobileNumberWrapper.setError(null);
                }
            }
        });

        mPasswordEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                boolean emptyText = getPassword().isEmpty();
                if ((!hasFocus && emptyText) || (hasFocus && emptyText)) {
                    mPasswordWrapper.setError(null);
                }
            }
        });
    }

    private String getMobileNumber() {
        return mMobileNumberEditText.getText().toString();
    }

    private String getPassword() {
        return mPasswordEditText.getText().toString();
    }

    private void handleLoginSubmitButtonClick() {
        listener.hideSoftKeyboard();
        String password = getPassword();
        String mobileNumber = getMobileNumber();

        if (InputValidationHelper.isValidMobileNumber(mMobileNumberWrapper, mobileNumber)
                && InputValidationHelper.isValidPassword(mPasswordWrapper, password)) {
            mUserAPIService = new UserAPIService(getContext());
            mUserAPIService.login(mobileNumber, password);
        }
    }

    private void handleLoginAPIResponse(String response) {

        JSONObject data = mUserAPIService.parseLoginResponseData(response);
        try {
            if (data != null) {
                JSONObject buyerLogin = data.getJSONObject("buyer_login");

                LoginHelperAsyncTask loginHelperAsyncTask = new LoginHelperAsyncTask(getContext(),
                        new LoginHelperAsyncTask.AsyncResponse() {
                            @Override
                            public void processFinish(Boolean output) {
                                if (output) {
                                    listener.loginSuccess();
                                } else {
                                    Toast.makeText(getContext(), "Something went wrong. Please try again", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                loginHelperAsyncTask.setUpProgressDialog(true, getString(R.string.login_progress_message));
                loginHelperAsyncTask.execute("logIn", buyerLogin.toString());
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
