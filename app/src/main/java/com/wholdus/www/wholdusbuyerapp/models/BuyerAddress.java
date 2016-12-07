package com.wholdus.www.wholdusbuyerapp.models;

import android.database.Cursor;

import com.wholdus.www.wholdusbuyerapp.databaseContracts.UserProfileContract;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by aditya on 7/12/16.
 */

public class BuyerAddress {
    private int m_ID;
    private String mBuyerID;
    private String mAddressID;
    private String mPincodeID;
    private String mAddress;
    private String mCity;
    private String mState;
    private String mLandmark;
    private String mContactNumber;
    private String mPincode;
    private String mAlias;
    private int mPriority;

    public BuyerAddress() {}

    public BuyerAddress(JSONObject json) throws JSONException {}

    public BuyerAddress(Cursor cursor) {
        m_ID = cursor.getInt(cursor.getColumnIndexOrThrow(UserProfileContract.UserAddressTable._ID));
        mBuyerID = cursor.getString(cursor.getColumnIndexOrThrow(UserProfileContract.UserAddressTable.COLUMN_BUYER_ID));
        mAddressID = cursor.getString(cursor.getColumnIndexOrThrow(UserProfileContract.UserAddressTable.COLUMN_ADDRESS_ID));
        mPincodeID = cursor.getString(cursor.getColumnIndexOrThrow(UserProfileContract.UserAddressTable.COLUMN_PINCODE_ID));
        mAddress = cursor.getString(cursor.getColumnIndexOrThrow(UserProfileContract.UserAddressTable.COLUMN_ADDRESS));
        mCity = cursor.getString(cursor.getColumnIndexOrThrow(UserProfileContract.UserAddressTable.COLUMN_CITY));
        mState = cursor.getString(cursor.getColumnIndexOrThrow(UserProfileContract.UserAddressTable.COLUMN_STATE));
        mLandmark = cursor.getString(cursor.getColumnIndexOrThrow(UserProfileContract.UserAddressTable.COLUMN_LANDMARK));
        mContactNumber = cursor.getString(cursor.getColumnIndexOrThrow(UserProfileContract.UserAddressTable.COLUMN_CONTACT_NUMBER));
        mPincode = cursor.getString(cursor.getColumnIndexOrThrow(UserProfileContract.UserAddressTable.COLUMN_PINCODE));
        mAlias = cursor.getString(cursor.getColumnIndexOrThrow(UserProfileContract.UserAddressTable.COLUMN_ADDRESS_ALIAS));
        mPriority = cursor.getInt(cursor.getColumnIndexOrThrow(UserProfileContract.UserAddressTable.COLUMN_PRIORITY));
    }

    public int get_ID() {
        return m_ID;
    }

    public String getBuyerID() {
        return mBuyerID;
    }

    public String getAddressID() {
        return mAddressID;
    }

    public String getPincodeID() {
        return mPincodeID;
    }

    public String getAddress() {
        return mAddress;
    }

    public String getCity() {
        return mCity;
    }

    public String getState() {
        return mState;
    }

    public String getLandmark() {
        return mLandmark;
    }

    public String getContactNumber() {
        return mContactNumber;
    }

    public String getPincode() {
        return mPincode;
    }

    public String getAlias() {
        return mAlias;
    }

    public int getPriority() {
        return mPriority;
    }
}
