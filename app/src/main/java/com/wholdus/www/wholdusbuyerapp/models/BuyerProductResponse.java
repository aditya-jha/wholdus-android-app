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

    public BuyerProductResponse(Cursor cursor, boolean allData) {
        if (allData) {
            setDataAllDataFromCursor(cursor);
        }else {
            setBasicDataFromCursor(cursor);
        }
    }

    public void setDataAllDataFromCursor(Cursor cursor){
        m_ID = cursor.getInt(cursor.getColumnIndexOrThrow(ProductsTable._ID));
        mBuyerProductResponseID = cursor.getInt(cursor.getColumnIndexOrThrow(ProductsTable.COLUMN_BUYER_PRODUCT_RESPONSE_ID));
        mHasSwiped = cursor.getInt(cursor.getColumnIndexOrThrow(ProductsTable.COLUMN_HAS_SWIPED));
        mRespondedFrom = cursor.getInt(cursor.getColumnIndexOrThrow(ProductsTable.COLUMN_RESPONDED_FROM));
        mCreatedAt = cursor.getString(cursor.getColumnIndexOrThrow(ProductsTable.COLUMN_BUYER_PRODUCT_RESPONSE_CREATED_AT));
        mUpdatedAt = cursor.getString(cursor.getColumnIndexOrThrow(ProductsTable.COLUMN_BUYER_PRODUCT_RESPONSE_UPDATED_AT));
        mResponseCode = cursor.getInt(cursor.getColumnIndexOrThrow(ProductsTable.COLUMN_RESPONSE_CODE));
        mStoreMargin = cursor.getFloat(cursor.getColumnIndexOrThrow(ProductsTable.COLUMN_MARGIN));
        mSynced = cursor.getInt(cursor.getColumnIndexOrThrow(ProductsTable.COLUMN_SYNCED));
    }

    public void setBasicDataFromCursor(Cursor cursor){
        mProductID = cursor.getInt(cursor.getColumnIndexOrThrow(ProductsTable.COLUMN_PRODUCT_ID));
        mHasSwiped = cursor.getInt(cursor.getColumnIndexOrThrow(ProductsTable.COLUMN_HAS_SWIPED));
        mRespondedFrom = cursor.getInt(cursor.getColumnIndexOrThrow(ProductsTable.COLUMN_RESPONDED_FROM));
        mResponseCode = cursor.getInt(cursor.getColumnIndexOrThrow(ProductsTable.COLUMN_RESPONSE_CODE));
        mStoreMargin = cursor.getFloat(cursor.getColumnIndexOrThrow(ProductsTable.COLUMN_STORE_MARGIN));
    }

    private void setProduct(Cursor cursor) {
        mProduct = new Product(cursor);
    }

    public int get_ID() {
        return m_ID;
    }

    public double getStoreMargin() {
        return mStoreMargin;
    }

    public int getProductID(){return mProductID;}

    public Product getProduct() {
        return mProduct;
    }

    public int getHasSwiped() {return mHasSwiped;}

    public int getRespondedFrom(){return mRespondedFrom;}

    public String getCreatedAt(){return mCreatedAt;}

    public String getUpdatedAt(){return mUpdatedAt;}

    public int getResponseCode() {return mResponseCode;}

    public int getBuyerProductResponseID() {
        return mBuyerProductResponseID;
    }

    public int getSynced() {
        return mSynced;
    }

    public static ArrayList<BuyerProductResponse> getDataFromCursor(Cursor cursor) {
        ArrayList<BuyerProductResponse> buyerProducts = new ArrayList<>();
        while (cursor.moveToNext()) {
            buyerProducts.add(new BuyerProductResponse(cursor, true));
        }
        return buyerProducts;
    }

    public static ArrayList<BuyerProductResponse> getDataFromCursorForSendingToServer(Cursor cursor){
        ArrayList<BuyerProductResponse> buyerProducts = new ArrayList<>();
        while (cursor.moveToNext()) {
            buyerProducts.add(new BuyerProductResponse(cursor, false));
        }
        return buyerProducts;
    }
}
