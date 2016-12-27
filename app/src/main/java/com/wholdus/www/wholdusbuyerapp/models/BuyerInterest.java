package com.wholdus.www.wholdusbuyerapp.models;

import android.database.Cursor;

import com.wholdus.www.wholdusbuyerapp.databaseContracts.UserProfileContract.UserInterestsTable;

import java.util.ArrayList;

/**
 * Created by aditya on 7/12/16.
 */

public class BuyerInterest {
    private int m_ID;
    private int mBuyerInterestID;
    private int mCategoryID;
    private String mCategoryName;
    private String mFabricFilter;
    private boolean mPriceFilterApplied;
    private double mMinPricePerUnit;
    private double mMaxPricePerUnit;

    public BuyerInterest() {
    }

    public BuyerInterest(Cursor cursor) {
        m_ID = cursor.getInt(cursor.getColumnIndexOrThrow(UserInterestsTable._ID));
        mBuyerInterestID = cursor.getInt(cursor.getColumnIndexOrThrow(UserInterestsTable.COLUMN_BUYER_INTEREST_ID));
        mCategoryID = cursor.getInt(cursor.getColumnIndexOrThrow(UserInterestsTable.COLUMN_CATEGORY_ID));
        mCategoryName = cursor.getString(cursor.getColumnIndexOrThrow(UserInterestsTable.COLUMN_CATEGORY_NAME));
        mFabricFilter = cursor.getString(cursor.getColumnIndexOrThrow(UserInterestsTable.COLUMN_FABRIC_FILTER_TEXT));
        mPriceFilterApplied = cursor.getInt(cursor.getColumnIndexOrThrow(UserInterestsTable.COLUMN_PRICE_FILTER_APPLIED)) == 1;
        mMinPricePerUnit = cursor.getDouble(cursor.getColumnIndexOrThrow(UserInterestsTable.COLUMN_MIN_PRICE_PER_UNIT));
        mMaxPricePerUnit = cursor.getDouble(cursor.getColumnIndexOrThrow(UserInterestsTable.COLUMN_MAX_PRICE_PER_UNIT));
    }

    public static ArrayList<BuyerInterest> getBuyerIntersetList(Cursor cursor) {
        ArrayList<BuyerInterest> data = new ArrayList<>();
        while (cursor.moveToNext()) {
            data.add(new BuyerInterest(cursor));
        }
        return data;
    }

    public int get_ID() {
        return m_ID;
    }

    public int getBuyerInterestID() {
        return mBuyerInterestID;
    }

    public int getCategoryID() {
        return mCategoryID;
    }

    public String getCategoryName() {
        return mCategoryName;
    }

    public String getFabricFilter() {
        return mFabricFilter;
    }

    public boolean getPriceFilterApplied() {
        return mPriceFilterApplied;
    }

    public double getMinPricePerUnit() {
        return mMinPricePerUnit;
    }

    public double getMaxPricePerUnit() {
        return mMaxPricePerUnit;
    }
}
