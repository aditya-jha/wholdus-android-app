package com.wholdus.www.wholdusbuyerapp.models;

import android.database.Cursor;

import com.wholdus.www.wholdusbuyerapp.databaseContracts.CatalogContract;

import java.util.ArrayList;

/**
 * Created by aditya on 14/12/16.
 */

public class CategorySeller {
    private int mSellerID;
    private String mCompanyName;

    public CategorySeller() {}

    public CategorySeller(Cursor cursor) {
        mSellerID = cursor.getInt(cursor.getColumnIndexOrThrow(CatalogContract.CategorySellersTable.COLUMN_SELLER_ID));
        mCompanyName = cursor.getString(cursor.getColumnIndexOrThrow(CatalogContract.CategorySellersTable.COLUMN_COMPANY_NAME));
    }

    public int getSellerID() {
        return mSellerID;
    }

    public String getCompanyName() {
        return mCompanyName;
    }

    public static ArrayList<CategorySeller> getCategorySellers(Cursor cursor) {
        ArrayList<CategorySeller> sellers = new ArrayList<>();
        while (cursor.moveToNext()) {
            sellers.add(new CategorySeller(cursor));
        }
        return sellers;
    }
}
