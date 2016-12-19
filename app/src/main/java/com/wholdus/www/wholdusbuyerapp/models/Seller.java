package com.wholdus.www.wholdusbuyerapp.models;

import android.database.Cursor;

import com.wholdus.www.wholdusbuyerapp.databaseContracts.CatalogContract;

import static com.wholdus.www.wholdusbuyerapp.databaseContracts.CatalogContract.*;

/**
 * Created by kaustubh on 8/12/16.
 */

public class Seller {

    private int m_ID;
    private int mSellerID;
    private String mCompanyName;
    private String mName;
    private String mCompanyProfile;
    private int mShowOnline;
    private String mCreatedAt;
    private String mUpdatedAt;

    public Seller() {
    }

    public Seller(Cursor cursor) {
        setDataFromCursor(cursor);
    }

    public void setDataFromCursor(Cursor cursor) {
        m_ID = cursor.getInt(cursor.getColumnIndexOrThrow(SellersTable._ID));
        mSellerID = cursor.getInt(cursor.getColumnIndexOrThrow(SellersTable.COLUMN_SELLER_ID));
        mCompanyName = cursor.getString(cursor.getColumnIndexOrThrow(SellersTable.COLUMN_COMPANY_NAME));
        mName = cursor.getString(cursor.getColumnIndexOrThrow(SellersTable.COLUMN_NAME));
        mCompanyProfile = cursor.getString(cursor.getColumnIndexOrThrow(SellersTable.COLUMN_COMPANY_PROFILE));
        mShowOnline = cursor.getInt(cursor.getColumnIndexOrThrow(SellersTable.COLUMN_SHOW_ONLINE));
        mCreatedAt = cursor.getString(cursor.getColumnIndexOrThrow(SellersTable.COLUMN_CREATED_AT));
        mUpdatedAt = cursor.getString(cursor.getColumnIndexOrThrow(SellersTable.COLUMN_UPDATED_AT));
    }

    public int getID() {
        return m_ID;
    }

    public int getSellerID() {
        return mSellerID;
    }

    public String getCompanyName() {
        return mCompanyName;
    }

    public String getName() {
        return mName;
    }

    public String getCompanyProfile() {
        return mCompanyProfile;
    }

    public int getShowOnline() {
        return mShowOnline;
    }

    public String getCreatedAt() {
        return mCreatedAt;
    }

    public String getUpdatedAt() {
        return mUpdatedAt;
    }

}
