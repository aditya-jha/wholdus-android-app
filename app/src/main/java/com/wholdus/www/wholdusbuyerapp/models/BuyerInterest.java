package com.wholdus.www.wholdusbuyerapp.models;

import android.database.Cursor;

import com.wholdus.www.wholdusbuyerapp.databaseContracts.UserProfileContract;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by aditya on 7/12/16.
 */

public class BuyerInterest {
    private int m_ID;
    private String mBuyerID;
    private String mBuyerInterestID;
    private String mCategoryID;
    private String mCategoryName;
    private String mFabricFilter;
    private Boolean mPriceFilterApplied;
    private double mMinPricePerUnit;
    private double mMaxPricePerUnit;

    public BuyerInterest() {}

    public BuyerInterest(JSONObject json) throws JSONException {}

    public BuyerInterest(Cursor cursor) {
        m_ID = cursor.getInt(cursor.getColumnIndexOrThrow(UserProfileContract.UserInterestsTable._ID));
        mBuyerID = cursor.getString(cursor.getColumnIndexOrThrow(UserProfileContract.UserInterestsTable.COLUMN_BUYER_ID));
        mBuyerInterestID = cursor.getString(cursor.getColumnIndexOrThrow(UserProfileContract.UserInterestsTable.COLUMN_BUYER_INTEREST_ID));
        mCategoryID = cursor.getString(cursor.getColumnIndexOrThrow(UserProfileContract.UserInterestsTable.COLUMN_CATEGORY_ID));
        mCategoryName = cursor.getString(cursor.getColumnIndexOrThrow(UserProfileContract.UserInterestsTable.COLUMN_CATEGORY_NAME));
        mFabricFilter = cursor.getString(cursor.getColumnIndexOrThrow(UserProfileContract.UserInterestsTable.COLUMN_FABRIC_FILTER_TEXT));
        mPriceFilterApplied = cursor.getInt(cursor.getColumnIndexOrThrow(UserProfileContract.UserInterestsTable.COLUMN_PRICE_FILTER_APPLIED)) == 1;
        mMinPricePerUnit = cursor.getDouble(cursor.getColumnIndexOrThrow(UserProfileContract.UserInterestsTable.COLUMN_MIN_PRICE_PER_UNIT));
        mMaxPricePerUnit = cursor.getDouble(cursor.getColumnIndexOrThrow(UserProfileContract.UserInterestsTable.COLUMN_MAX_PRICE_PER_UNIT));
    }

    public int get_ID() {
        return m_ID;
    }

    public String getBuyerID() {
        return mBuyerID;
    }

    public String getBuyerInterestID() {
        return mBuyerInterestID;
    }

    public String getCategoryID() {
        return mCategoryID;
    }

    public String getCategoryName() {
        return mCategoryName;
    }

    public String getFabricFilter() {
        return mFabricFilter;
    }

    public Boolean getPriceFilterApplied() {
        return mPriceFilterApplied;
    }

    public double getMinPricePerUnit() {
        return mMinPricePerUnit;
    }

    public double getMaxPricePerUnit() {
        return mMaxPricePerUnit;
    }
}
