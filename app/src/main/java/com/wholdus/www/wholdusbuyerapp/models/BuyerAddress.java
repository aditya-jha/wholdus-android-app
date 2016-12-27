package com.wholdus.www.wholdusbuyerapp.models;

import android.database.Cursor;

import com.wholdus.www.wholdusbuyerapp.databaseContracts.UserProfileContract.UserAddressTable;

import java.util.ArrayList;
import java.util.UUID;

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
    private int mSynced;
    private String mCreatedAt;
    private String mUpdatedAt;
    private String mClientID;

    public BuyerAddress() {
        m_ID = -1;
        mAddressID = -1;
        mPincodeID = -1;
        mPriority = 0;
        mSynced = 0;
        mCreatedAt = "";
        mUpdatedAt = "";
        mClientID = UUID.randomUUID().toString();
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
        mSynced = cursor.getInt(cursor.getColumnIndexOrThrow(UserAddressTable.COLUMN_SYNCED));
        mCreatedAt = cursor.getString(cursor.getColumnIndexOrThrow(UserAddressTable.COLUMN_CREATED_AT));
        mUpdatedAt = cursor.getString(cursor.getColumnIndexOrThrow(UserAddressTable.COLUMN_UPDATED_AT));
        mClientID = cursor.getString(cursor.getColumnIndexOrThrow(UserAddressTable.COLUMN_CLIENT_ID));
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

    public int getSynced() {return mSynced;}

    public String getCreatedAt(){return mCreatedAt;}

    public String getUpdatedAt(){return mUpdatedAt;}

    public String getClientID(){return mClientID;}

    public static ArrayList<BuyerAddress> getBuyerAddressesFromCursor(Cursor cursor) {
        ArrayList<BuyerAddress> buyerAddresses= new ArrayList<>();
        while (cursor.moveToNext()) {
            buyerAddresses.add(new BuyerAddress(cursor));
        }
        return buyerAddresses;
    }
}
