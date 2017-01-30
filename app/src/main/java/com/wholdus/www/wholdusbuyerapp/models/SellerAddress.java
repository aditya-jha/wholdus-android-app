package com.wholdus.www.wholdusbuyerapp.models;

import android.database.Cursor;

import com.wholdus.www.wholdusbuyerapp.databaseContracts.CatalogContract.SellerAddressTable;

/**
 * Created by kaustubh on 8/12/16.
 */

public class SellerAddress {
    private int mAddressID;
    private String mCity;
    private String mLandmark;
    private String mState;
    private String mAddress;
    private String mPincode;

    public SellerAddress(Cursor cursor){setDataFromCursor(cursor);}

    public void setDataFromCursor(Cursor cursor){
        mAddressID = cursor.getInt(cursor.getColumnIndexOrThrow(SellerAddressTable.COLUMN_ADDRESS_ID));
        mCity = cursor.getString(cursor.getColumnIndexOrThrow(SellerAddressTable.COLUMN_CITY));
        mLandmark = cursor.getString(cursor.getColumnIndexOrThrow(SellerAddressTable.COLUMN_LANDMARK));
        mState = cursor.getString(cursor.getColumnIndexOrThrow(SellerAddressTable.COLUMN_STATE));
        mAddress = cursor.getString(cursor.getColumnIndexOrThrow(SellerAddressTable.COLUMN_ADDRESS));
        mPincode = cursor.getString(cursor.getColumnIndexOrThrow(SellerAddressTable.COLUMN_PINCODE));
    }

    public int getAddressID(){return mAddressID;}

    public String getCity(){return mCity;}

    public String getLandmark(){return mLandmark;}

    public String getState(){return mState;}

    public String getAddress(){return mAddress;}

    public String getPincode(){return mPincode;}
}
