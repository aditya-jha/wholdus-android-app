package com.wholdus.www.wholdusbuyerapp.singletons;

/**
 * Created by aditya on 20/11/16.
 */

public class TokenSingleton {

    private static TokenSingleton mInstance;

    private String mAccessToken;
    private String mRefreshToken;

    private TokenSingleton() {}

    public void setData(String aToken, String rToken) {
        mAccessToken = aToken;
        mRefreshToken = rToken;
    }

    public String getAccessToken() {
        return mAccessToken;
    }

    public String getRefreshToken() {
        return mRefreshToken;
    }

    public static synchronized TokenSingleton getInstance(){
        if(mInstance==null){
            mInstance=new TokenSingleton();
        }
        return mInstance;
    }
}
