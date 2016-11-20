package com.wholdus.www.wholdusbuyerapp;

import android.app.Application;

/**
 * Created by aditya on 20/11/16.
 */

public class WholdusApplication extends Application {

    private String mAccessToken;
    private String mRefreshToken;

    public void setTokens(String aToken, String rToken) {
        mAccessToken = aToken;
        mRefreshToken = rToken;
    }

    public String getAccessToken() {
        return mAccessToken;
    }

    public String getRefreshToken() {
        return mRefreshToken;
    }
}
