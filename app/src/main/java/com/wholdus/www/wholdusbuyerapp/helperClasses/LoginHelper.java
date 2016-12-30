package com.wholdus.www.wholdusbuyerapp.helperClasses;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;

import com.google.firebase.crash.FirebaseCrash;
import com.wholdus.www.wholdusbuyerapp.WholdusApplication;
import com.wholdus.www.wholdusbuyerapp.databaseContracts.UserProfileContract;
import com.wholdus.www.wholdusbuyerapp.databaseHelpers.DatabaseHelper;

import org.json.JSONObject;

/**
 * Created by aditya on 29/12/16.
 */

public class LoginHelper {

    private static final String ACCESS_TOKEN_KEY = "access_token";
    private static final String REFRESH_TOKEN_KEY = "refresh_token";
    private static final String BUYER_ID_KEY = UserProfileContract.UserTable.COLUMN_BUYER_ID;

    private Context mContext;

    public LoginHelper(Context context) {
        mContext = context;
    }

    public boolean checkIfLoggedIn() {
        SharedPreferences sp = getSharedPreference();

        try {
            final String aToken = sp.getString(ACCESS_TOKEN_KEY, null);
            final String rToken = sp.getString(REFRESH_TOKEN_KEY, null);
            final int buyerID = sp.getInt(BUYER_ID_KEY, -1);

            if (aToken == null || rToken == null || buyerID == -1) {
                return false;
            } else {
                setTokens(aToken, rToken, buyerID);
                return true;
            }
        } catch (ClassCastException e) {
            FirebaseCrash.report(e);
            e.printStackTrace();
            return false;
        }
    }

    public boolean login(JSONObject apiResponse) {
        SharedPreferences.Editor editor = getSharedPreference().edit();

        try {
            final String aToken = apiResponse.getString(ACCESS_TOKEN_KEY);
            final String rToken = apiResponse.getString(REFRESH_TOKEN_KEY);
            final int buyerID = apiResponse.getJSONObject("buyer").getInt(BUYER_ID_KEY);

            editor.putString(ACCESS_TOKEN_KEY, aToken);
            editor.putString(REFRESH_TOKEN_KEY, rToken);
            editor.putInt(BUYER_ID_KEY, buyerID);
            editor.apply();

            setTokens(aToken, rToken, buyerID);
        } catch (Exception e) {
            FirebaseCrash.report(e);
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public boolean logout() {
        SharedPreferences.Editor editor = getSharedPreference().edit();

        try {
            DatabaseHelper databaseHelper = DatabaseHelper.getInstance(mContext);
            SQLiteDatabase db = databaseHelper.openDatabase();
            databaseHelper.onUpgrade(db, 1, 1);
            databaseHelper.closeDatabase();

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

    private SharedPreferences getSharedPreference() {
        return mContext.getSharedPreferences("LoginHelperSharedPreference", Context.MODE_PRIVATE);
    }

    private void setTokens(String aToken, String rToken, int buyerID) {
        WholdusApplication wholdusApplication = ((WholdusApplication) ((Activity) mContext).getApplication());
        wholdusApplication.setTokens(aToken, rToken, buyerID);
    }
}
