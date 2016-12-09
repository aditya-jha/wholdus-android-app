package com.wholdus.www.wholdusbuyerapp.asynctasks;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.WholdusApplication;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by aditya on 2/11/16.
 * AsyncTask class handle SharedPreferences usage in login functionality
 */

public class LoginHelperAsyncTask extends AsyncTask<String, Void, Boolean> {

    public interface AsyncResponse {
        void processFinish(Boolean output);
    }

    private static final String ACCESS_TOKEN_KEY = "com.wholdus.www.wholdusbuyerapp.ACCESS_TOKEN";
    private static final String REFRESH_TOKEN_KEY = "com.wholdus.www.wholdusbuyerapp.REFRESH_TOKEN";
    private static final String BUYER_ID_KEY = "com.wholdus.www.wholdusbuyerapp.BUYER_ID";
    private Context mContext;
    private AsyncResponse mAsyncResponse;
    private boolean mShowProgressDialog;
    private String mProgressDialogMessage;
    private ProgressDialog mProgressDialog;

    public LoginHelperAsyncTask(Context context, AsyncResponse asyncResponse) {
        mContext = context;
        mAsyncResponse = asyncResponse;
    }

    public void setUpProgressDialog(boolean showProgress, String message) {
        mShowProgressDialog = showProgress;
        mProgressDialogMessage = message;
    }

    @Override
    protected Boolean doInBackground(String... params) {
        final String LoginHelperSharedPreference = mContext.getString(R.string.login_helper_shared_preference);

        SharedPreferences loginHelperSharedPreference;
        loginHelperSharedPreference = mContext.getSharedPreferences(LoginHelperSharedPreference, Context.MODE_PRIVATE);

        switch (params[0]) {
            case "checkIfLoggedIn":
                return checkIfLoggedIn(loginHelperSharedPreference);
            case "logIn":
                try {
                    JSONObject data = new JSONObject(params[1]);
                    return logIn(loginHelperSharedPreference, data);
                } catch (JSONException e) {
                    e.printStackTrace();
                    return false;
                }
            case "logout":
                return logout(loginHelperSharedPreference);
            default:
                return false;
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (mShowProgressDialog) {
            mProgressDialog = new ProgressDialog(mContext);
            mProgressDialog.setMessage(mProgressDialogMessage);
            mProgressDialog.show();
        }
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (mShowProgressDialog) {
            mProgressDialog.dismiss();
        }
        mAsyncResponse.processFinish(result);
    }

    private boolean logout(SharedPreferences loginHelperSharedPreference) {
        SharedPreferences.Editor editor = loginHelperSharedPreference.edit();
        try {
            editor.remove(ACCESS_TOKEN_KEY);
            editor.remove(REFRESH_TOKEN_KEY);
            editor.remove(BUYER_ID_KEY);
            editor.apply();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @NonNull
    private Boolean checkIfLoggedIn(SharedPreferences loginHelperSharedPreference) {
        String accessToken, refreshToken, buyerID;

        try {
            accessToken = loginHelperSharedPreference.getString(ACCESS_TOKEN_KEY, "");
            refreshToken = loginHelperSharedPreference.getString(REFRESH_TOKEN_KEY, "");
            buyerID = loginHelperSharedPreference.getString(BUYER_ID_KEY, "");
        } catch (ClassCastException e) {
            e.printStackTrace();
            return false;
        }

        if(!accessToken.equals("") && !refreshToken.equals("") && !buyerID.equals("")) {
            setTokens(accessToken, refreshToken, buyerID);
            return true;
        }

        return false;
    }

    @NonNull
    private Boolean logIn(SharedPreferences loginHelperSharedPreference, JSONObject apiResponse) {
        SharedPreferences.Editor editor = loginHelperSharedPreference.edit();

        try {
            final String ACCESS_TOKEN = apiResponse.getString("access_token");
            final String REFRESH_TOKEN = apiResponse.getString("refresh_token");
            final String BUYER_ID = apiResponse.getJSONObject("buyer").getString("buyerID");
            editor.putString(ACCESS_TOKEN_KEY, ACCESS_TOKEN);
            editor.putString(REFRESH_TOKEN_KEY, REFRESH_TOKEN);
            editor.putString(BUYER_ID_KEY, BUYER_ID);

            setTokens(ACCESS_TOKEN, REFRESH_TOKEN, BUYER_ID);
        } catch (JSONException e) {
            return false;
        }

        try {
            editor.apply();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private void setTokens(String aToken, String rToken, String buyerID) {
         WholdusApplication wholdusApplication = (WholdusApplication)((Activity) mContext).getApplication();
         wholdusApplication.setTokens(aToken, rToken, buyerID);
    }
}
