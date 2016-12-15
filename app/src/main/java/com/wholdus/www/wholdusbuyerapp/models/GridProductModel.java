package com.wholdus.www.wholdusbuyerapp.models;

import android.database.Cursor;
import android.renderscript.Double2;

import com.wholdus.www.wholdusbuyerapp.databaseContracts.CatalogContract;

import java.util.ArrayList;

/**
 * Created by aditya on 15/12/16.
 * product model to hold limited data needed to display in product grids
 */

public class GridProductModel {

    private int m_ID;
    private int mProductID;
    private String mImageUrl;
    private String mName;
    private Double mPrice;
    private String mFabric;
    private boolean mLikeStatus;
    private int mCartCount;

    public GridProductModel() {

    }

    public GridProductModel(Cursor cursor) {
        m_ID = cursor.getInt(cursor.getColumnIndexOrThrow(CatalogContract.ProductsTable._ID));
        mProductID = cursor.getInt(cursor.getColumnIndexOrThrow(CatalogContract.ProductsTable.COLUMN_PRODUCT_ID));
        mImageUrl = getImageUrl(cursor);
        mName = cursor.getString(cursor.getColumnIndexOrThrow(CatalogContract.ProductsTable.COLUMN_NAME));
        mPrice = cursor.getDouble(cursor.getColumnIndexOrThrow(CatalogContract.ProductsTable.COLUMN_MIN_PRICE_PER_UNIT));
        mFabric = cursor.getString(cursor.getColumnIndexOrThrow(CatalogContract.ProductsTable.COLUMN_FABRIC_GSM));
        mLikeStatus = cursor.getInt(cursor.getColumnIndexOrThrow(CatalogContract.ProductsTable.COLUMN_LIKE_STATUS)) > 0;
        mCartCount = 0;
    }

    public static ArrayList<GridProductModel> getGridProducts(Cursor cursor) {
        ArrayList<GridProductModel> gridProducts = new ArrayList<>();
        while (cursor.moveToNext()) {
            gridProducts.add(new GridProductModel(cursor));
        }
        return gridProducts;
    }

    private String getImageUrl(Cursor cursor) {
        return "";
    }
}
