package com.wholdus.www.wholdusbuyerapp;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
/**
 * Created by aditya on 20/11/16.
 */

public class WholdusApplication extends MultiDexApplication {

    private String mAccessToken;
    private String mRefreshToken;
    private String mBuyerID;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(WholdusApplication.this);
    }

    public void setTokens(String aToken, String rToken, String buyerID) {
        mAccessToken = aToken;
        mRefreshToken = rToken;
        mBuyerID = buyerID;
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
