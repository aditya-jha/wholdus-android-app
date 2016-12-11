package com.wholdus.www.wholdusbuyerapp.models;

import android.database.Cursor;

import com.wholdus.www.wholdusbuyerapp.databaseContracts.UserProfileContract.UserAddressTable;

/**
 * Created by aditya on 7/12/16.
 */

public class BuyerAddress {
    private int m_ID;
    private int mAddressID;
    private int mPincodeID;
    private String mAddress;
    private String mCity;
    private String mState;
    private String mLandmark;
    private String mContactNumber;
    private String mPincode;
    private String mAlias;
    private int mPriority;

    public BuyerAddress() {
    }

    public BuyerAddress(Cursor cursor) {
        m_ID = cursor.getInt(cursor.getColumnIndexOrThrow(UserAddressTable._ID));
        mAddressID = cursor.getInt(cursor.getColumnIndexOrThrow(UserAddressTable.COLUMN_ADDRESS_ID));
        mPincodeID = cursor.getInt(cursor.getColumnIndexOrThrow(UserAddressTable.COLUMN_PINCODE_ID));
        mAddress = cursor.getString(cursor.getColumnIndexOrThrow(UserAddressTable.COLUMN_ADDRESS));
        mCity = cursor.getString(cursor.getColumnIndexOrThrow(UserAddressTable.COLUMN_CITY));
        mState = cursor.getString(cursor.getColumnIndexOrThrow(UserAddressTable.COLUMN_STATE));
        mLandmark = cursor.getString(cursor.getColumnIndexOrThrow(UserAddressTable.COLUMN_LANDMARK));
        mContactNumber = cursor.getString(cursor.getColumnIndexOrThrow(UserAddressTable.COLUMN_CONTACT_NUMBER));
        mPincode = cursor.getString(cursor.getColumnIndexOrThrow(UserAddressTable.COLUMN_PINCODE));
        mAlias = cursor.getString(cursor.getColumnIndexOrThrow(UserAddressTable.COLUMN_ADDRESS_ALIAS));
        mPriority = cursor.getInt(cursor.getColumnIndexOrThrow(UserAddressTable.COLUMN_PRIORITY));
    }

    public int get_ID() {
        return m_ID;
    }

    public int getAddressID() {
        return mAddressID;
    }

    public int getPincodeID() {
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
