package com.wholdus.www.wholdusbuyerapp.models;

import android.database.Cursor;

import com.wholdus.www.wholdusbuyerapp.databaseContracts.UserProfileContract.BusinessTypesTable;

import java.util.ArrayList;

/**
 * Created by aditya on 8/12/16.
 */

public class BusinessTypes {
    private int m_ID;
    private String mBusinessType;
    private String mBusinessTypeID;
    private String mDescription;

    public BusinessTypes() {
    }

    public BusinessTypes(int id, String bt, String btID, String desc) {
        m_ID = id;
        mBusinessType = bt;
        mBusinessTypeID = btID;
        mDescription = desc;
    }

    public BusinessTypes(Cursor cursor) {
        setBusinessTypesData(cursor);
    }

    public void setBusinessTypesData(Cursor cursor) {
        m_ID = cursor.getInt(cursor.getColumnIndexOrThrow(BusinessTypesTable._ID));
        mBusinessType = cursor.getString(cursor.getColumnIndexOrThrow(BusinessTypesTable.COLUMN_BUSINESS_TYPE));
        mBusinessTypeID = cursor.getString(cursor.getColumnIndexOrThrow(BusinessTypesTable.COLUMN_BUSINESS_TYPE_ID));
        mDescription = cursor.getString(cursor.getColumnIndexOrThrow(BusinessTypesTable.COLUMN_DESCRIPTION));
    }

    public static ArrayList<BusinessTypes> initBusinessTypesList(Cursor cursor) {
        ArrayList<BusinessTypes> businessTypes = new ArrayList<>();
        while (cursor.moveToNext()) {
            businessTypes.add(new BusinessTypes(cursor));
        }
        return businessTypes;
    }

    public int get_ID() {
        return m_ID;
    }

    public String getBusinessType() {
        return mBusinessType;
    }

    public String getBusinessTypeID() {
        return mBusinessTypeID;
    }

    public String getDescription() {
        return mDescription;
    }
}
