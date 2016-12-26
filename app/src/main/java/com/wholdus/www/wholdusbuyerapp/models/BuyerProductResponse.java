package com.wholdus.www.wholdusbuyerapp.models;

import android.database.Cursor;

import com.wholdus.www.wholdusbuyerapp.databaseContracts.CatalogContract.ProductsTable;

import java.util.ArrayList;

/**
 * Created by aditya on 9/12/16.
 */

public class BuyerProductResponse {

    private int m_ID;
    private int mBuyerProductResponseID;
    private int mProductID;
    private Product mProduct;
    private int mHasSwiped;
    private int mRespondedFrom;
    private String mCreatedAt;
    private String mUpdatedAt;
    private int mResponseCode;
    private Float mStoreMargin;
    private int mSynced;

    public BuyerProductResponse() {
    }

    public BuyerProductResponse(Cursor cursor) {
        setDataFromCursor(cursor);
    }

    public void setDataFromCursor(Cursor cursor) {
        if (cursor.getColumnIndex(ProductsTable._ID) != -1) {
            m_ID = cursor.getInt(cursor.getColumnIndex(ProductsTable._ID));
        }

        if (cursor.getColumnIndex(ProductsTable.COLUMN_PRODUCT_ID) != -1) {
            mProductID = cursor.getInt(cursor.getColumnIndex(ProductsTable.COLUMN_PRODUCT_ID));
        }

        if (cursor.getColumnIndex(ProductsTable.COLUMN_BUYER_PRODUCT_RESPONSE_ID) != -1) {
            mBuyerProductResponseID = cursor.getInt(cursor.getColumnIndex(ProductsTable.COLUMN_BUYER_PRODUCT_RESPONSE_ID));
        }

        if (cursor.getColumnIndex(ProductsTable.COLUMN_HAS_SWIPED) != -1) {
            mHasSwiped = cursor.getInt(cursor.getColumnIndex(ProductsTable.COLUMN_HAS_SWIPED));
        }

        if (cursor.getColumnIndex(ProductsTable.COLUMN_RESPONDED_FROM) != -1) {
            mRespondedFrom = cursor.getInt(cursor.getColumnIndex(ProductsTable.COLUMN_RESPONDED_FROM));
        }

        if (cursor.getColumnIndex(ProductsTable.COLUMN_BUYER_PRODUCT_RESPONSE_CREATED_AT) != -1) {
            mCreatedAt = cursor.getString(cursor.getColumnIndex(ProductsTable.COLUMN_BUYER_PRODUCT_RESPONSE_CREATED_AT));
        }

        if (cursor.getColumnIndex(ProductsTable.COLUMN_BUYER_PRODUCT_RESPONSE_UPDATED_AT) != -1) {
            mUpdatedAt = cursor.getString(cursor.getColumnIndex(ProductsTable.COLUMN_BUYER_PRODUCT_RESPONSE_UPDATED_AT));
        }

        if (cursor.getColumnIndex(ProductsTable.COLUMN_RESPONSE_CODE) != -1) {
            mResponseCode = cursor.getInt(cursor.getColumnIndex(ProductsTable.COLUMN_RESPONSE_CODE));
        }

        if (cursor.getColumnIndex(ProductsTable.COLUMN_STORE_MARGIN) != -1) {
            mStoreMargin = cursor.getFloat(cursor.getColumnIndex(ProductsTable.COLUMN_STORE_MARGIN));
        }

        if (cursor.getColumnIndex(ProductsTable.COLUMN_SYNCED) != -1) {
            mSynced = cursor.getInt(cursor.getColumnIndex(ProductsTable.COLUMN_SYNCED));
        }
    }

    private void setProduct(Cursor cursor) {
        mProduct = new Product(cursor);
    }

    public int get_ID() {
        return m_ID;
    }

    public float getStoreMargin() {
        return mStoreMargin;
    }

    public int getProductID() {
        return mProductID;
    }

    public Product getProduct() {
        return mProduct;
    }

    public int getHasSwiped() {
        return mHasSwiped;
    }

    public int getRespondedFrom() {
        return mRespondedFrom;
    }

    public String getCreatedAt() {
        return mCreatedAt;
    }

    public String getUpdatedAt() {
        return mUpdatedAt;
    }

    public int getResponseCode() {
        return mResponseCode;
    }

    public int getBuyerProductResponseID() {
        return mBuyerProductResponseID;
    }

    public int getSynced() {
        return mSynced;
    }

    public static ArrayList<BuyerProductResponse> getDataFromCursor(Cursor cursor) {
        ArrayList<BuyerProductResponse> buyerProducts = new ArrayList<>();
        while (cursor.moveToNext()) {
            buyerProducts.add(new BuyerProductResponse(cursor));
        }
        return buyerProducts;
    }
}
