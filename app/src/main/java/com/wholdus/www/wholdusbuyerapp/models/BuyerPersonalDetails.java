package com.wholdus.www.wholdusbuyerapp.models;

/**
 * Created by aditya on 19/11/16.
 */

public class BuyerPersonalDetails {

    private String mKey;
    private String mValue;
    private int mIconResource;

    public BuyerPersonalDetails(String key, String value, int iconResource) {
        mKey = key;
        mValue = value;
        mIconResource = iconResource;
    }

    public String getKey() {
        return mKey;
    }

    public String getValue() {
        return mValue;
    }

    public int getIconResource() {
        return mIconResource;
    }
}
