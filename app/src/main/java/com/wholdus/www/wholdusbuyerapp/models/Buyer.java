package com.wholdus.www.wholdusbuyerapp.models;

import android.database.Cursor;
import android.util.Log;

import com.wholdus.www.wholdusbuyerapp.databaseContracts.UserProfileContract.UserTable;

import java.util.ArrayList;

/**
 * Created by aditya on 7/12/16.
 */

public class Buyer {
    private int m_ID;
    private String mBuyerID;
    private String mName;
    private String mEmail;
    private String mMobileNumber;
    private String mWhatsappNumber;
    private String mCompanyName;
    private String mBusinessType;
    private String mGender;

    private ArrayList<BuyerAddress> mBuyerAddress;

    private ArrayList<BuyerInterest> mBuyerInterest;

    public Buyer() {
    }

    public Buyer(Cursor cursor) {
        setBuyerData(cursor);
    }

    public void setBuyerData(Cursor cursor) {
        if (cursor.getCount() == 0) {
            return;
        } else if (cursor.getCount() > 1) {
            /* TODO: multiple buyer with same ID, should not exist, handle the case */
            Log.e(this.getClass().getSimpleName(), "multiple buyer with same ID");
            return;
        }
        cursor.moveToFirst();
        m_ID = cursor.getInt(cursor.getColumnIndexOrThrow(UserTable._ID));
        mBuyerID = cursor.getString(cursor.getColumnIndexOrThrow(UserTable.COLUMN_BUYER_ID));
        mName = cursor.getString(cursor.getColumnIndexOrThrow(UserTable.COLUMN_NAME));
        mEmail = cursor.getString(cursor.getColumnIndexOrThrow(UserTable.COLUMN_EMAIL));
        mMobileNumber = cursor.getString(cursor.getColumnIndexOrThrow(UserTable.COLUMN_MOBILE_NUMBER));
        mWhatsappNumber = cursor.getString(cursor.getColumnIndexOrThrow(UserTable.COLUMN_WHATSAPP_NUMBER));
        mCompanyName = cursor.getString(cursor.getColumnIndexOrThrow(UserTable.COLUMN_COMPANY_NAME));
        mBusinessType = cursor.getString(cursor.getColumnIndexOrThrow(UserTable.COLUMN_BUSINESS_TYPE));
        mGender = cursor.getString(cursor.getColumnIndexOrThrow(UserTable.COLUMN_GENDER));
    }

    public void setAddressData(Cursor cursor) {
        mBuyerAddress = new ArrayList<>();
        while (cursor.moveToNext()) {
            mBuyerAddress.add(new BuyerAddress(cursor));
        }
    }

    public void setInterestData(Cursor cursor) {
        mBuyerInterest = new ArrayList<>();
        while (cursor.moveToNext()) {
            mBuyerInterest.add(new BuyerInterest(cursor));
        }
    }

    public int get_ID() {
        return m_ID;
    }

    public String getBuyerID() {
        return mBuyerID;
    }

    public String getName() {
        return mName;
    }

    public String getEmail() {
        return mEmail;
    }

    public String getMobileNumber() {
        return mMobileNumber;
    }

    public String getWhatsappNumber() {
        return mWhatsappNumber;
    }

    public String getCompanyName() {
        return mCompanyName;
    }

    public String getBusinessType() {
        return mBusinessType;
    }

    public String getGender() {
        return mGender;
    }

    public ArrayList<BuyerAddress> getBuyerAddress() {
        return mBuyerAddress;
    }

    public ArrayList<BuyerInterest> getBuyerInterest() {
        return mBuyerInterest;
    }

    public boolean isEmpty() {
        return m_ID > 0;
    }
}
