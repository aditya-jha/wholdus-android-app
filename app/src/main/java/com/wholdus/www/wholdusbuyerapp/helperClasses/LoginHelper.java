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
    private static final String MOBILE_NUMBER_KEY = UserProfileContract.UserTable.COLUMN_MOBILE_NUMBER;

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
            final String mobileNumber = sp.getString(MOBILE_NUMBER_KEY, null);

            if (aToken == null || rToken == null || buyerID == -1 || mobileNumber == null) {
                return false;
            } else {
                setTokens(aToken, rToken, buyerID, mobileNumber);
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
            JSONObject buyer = apiResponse.getJSONObject("buyer");
            final int buyerID = buyer.getInt(BUYER_ID_KEY);
            final String mobileNUmber = buyer.getString(MOBILE_NUMBER_KEY);

            editor.putString(ACCESS_TOKEN_KEY, aToken);
            editor.putString(REFRESH_TOKEN_KEY, rToken);
            editor.putInt(BUYER_ID_KEY, buyerID);
            editor.putString(MOBILE_NUMBER_KEY, mobileNUmber);
            editor.apply();

            setTokens(aToken, rToken, buyerID, mobileNUmber);
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
            editor.remove(MOBILE_NUMBER_KEY);
            editor.apply();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public boolean saveRegistrationToken(String token) {
        SharedPreferences.Editor editor = getSharedPreference().edit();
        try {
            editor.putString(APIConstants.REGISTRATION_TOKEN_KEY, token);
            editor.apply();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private SharedPreferences getSharedPreference() {
        return mContext.getSharedPreferences("LoginHelperSharedPreference", Context.MODE_PRIVATE);
    }

    private void setTokens(String aToken, String rToken, int buyerID, String mobileNumber) {
        try {
            WholdusApplication wholdusApplication = ((WholdusApplication) ((Activity) mContext).getApplication());
            wholdusApplication.setTokens(aToken, rToken, buyerID, mobileNumber);
        } catch (Exception e) {
        }
    }
}
