package com.wholdus.www.wholdusbuyerapp.aynctasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.text.TextUtils;

/**
 * Created by aditya on 2/11/16.
 * AsyncTask class handle SharedPreferences usage in login functionality
 */

public class LoginHelperAsyncTask extends AsyncTask<String, Void, Boolean> {

    public interface AsyncResponse {
        void processFinish(Boolean output);
    }

    private final String ACCESS_TOKEN_KEY = "com.wholdus.www.wholdusbuyerapp.ACCESS_TOKEN";
    private Context mContext;
    private AsyncResponse mAsyncResponse;

    public LoginHelperAsyncTask(Context context, AsyncResponse asyncResponse) {
        mContext = context;
        mAsyncResponse = asyncResponse;
    }

    @Override
    protected Boolean doInBackground(String... params) {
        final String LoginHelperSharedPreference = "LoginHelperSharedPreference";

        SharedPreferences loginHelperSharedPreference;
        loginHelperSharedPreference = mContext.getSharedPreferences(LoginHelperSharedPreference, Context.MODE_PRIVATE);

        if(params[0].equals("checkIfLoggedIn")) {
            return checkIfLoggedIn(loginHelperSharedPreference);
        } else if(params[0].equals("logIn") && !TextUtils.isEmpty(params[1])){
            return logIn(loginHelperSharedPreference, params[1]);
        }

        return false;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        mAsyncResponse.processFinish(result);
    }

    private Boolean checkIfLoggedIn(SharedPreferences loginHelperSharedPreference) {
        String accessToken;

        try {
            accessToken = loginHelperSharedPreference.getString(ACCESS_TOKEN_KEY, "");
        } catch (ClassCastException e) {
            e.printStackTrace();
            return false;
        }

        // check access_token validity
        return !accessToken.equals("");
    }

    private Boolean logIn(SharedPreferences loginHelperSharedPreference, String accessToken) {
        SharedPreferences.Editor editor = loginHelperSharedPreference.edit();

        editor.putString(ACCESS_TOKEN_KEY, accessToken);

        try {
            editor.apply();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
