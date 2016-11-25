package com.wholdus.www.wholdusbuyerapp;

import android.app.Application;

/**
 * Created by aditya on 20/11/16.
 */

public class WholdusApplication extends Application {

    private String mAccessToken;
    private String mRefreshToken;
    private String mBuyerID;

    public void setTokens(String aToken, String rToken) {
        mAccessToken = aToken;
        mRefreshToken = rToken;
        mBuyerID = "1";
    }

    public String getAccessToken() {
        return mAccessToken;
    }

    public String getRefreshToken() {
        return mRefreshToken;
    }

    public String getBuyerID() {
        return mBuyerID;
    }
}
