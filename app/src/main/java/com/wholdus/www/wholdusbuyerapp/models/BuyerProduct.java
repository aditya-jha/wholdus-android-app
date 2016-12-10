package com.wholdus.www.wholdusbuyerapp.models;

import android.database.Cursor;

import com.wholdus.www.wholdusbuyerapp.databaseContracts.BuyerProductsContract;
import com.wholdus.www.wholdusbuyerapp.databaseContracts.BuyerProductsContract.BuyerProductTable;

import java.util.ArrayList;

/**
 * Created by aditya on 9/12/16.
 */

public class BuyerProduct {

    private int m_ID;
    private long mBuyerProductID;
    private long mBuyerProductResponseID;
    private Product mProduct;
    private boolean mHasSwiped;
    private int mResponseCode;
    private double mStoreDiscount;
    private boolean mSynced;

    public BuyerProduct() {
    }

    public BuyerProduct(Cursor cursor) {
        m_ID = cursor.getInt(cursor.getColumnIndex(BuyerProductTable._ID));
        mBuyerProductID = cursor.getInt(cursor.getColumnIndex(BuyerProductTable.COLUMN_BUYER_PRODUCT_ID));
        mBuyerProductResponseID = cursor.getInt(cursor.getColumnIndex(BuyerProductTable.COLUMN_BUYER_PRODUCT_RESPONSE_ID));
        mHasSwiped = cursor.getInt(cursor.getColumnIndex(BuyerProductTable.COLUMN_HAS_SWIPED)) > 0;
        mResponseCode = cursor.getInt(cursor.getColumnIndex(BuyerProductTable.COLUMN_RESPONSE_CODE));
        mStoreDiscount = cursor.getDouble(cursor.getColumnIndex(BuyerProductTable.COLUMN_STORE_DISCOUNT));
        mSynced = cursor.getInt(cursor.getColumnIndex(BuyerProductTable.COLUMN_SYNCED)) > 0;
        setProduct(cursor);
    }

    private void setProduct(Cursor cursor) {
        mProduct = new Product(cursor);
    }

    public int get_ID() {
        return m_ID;
    }

    public double getStoreDiscount() {
        return mStoreDiscount;
    }

    public long getBuyerProductID() {
        return mBuyerProductID;
    }

    public Product getProduct() {
        return mProduct;
    }

    public boolean getHasSwiped() {
        return mHasSwiped;
    }

    public int getResponseCode() {
        return mResponseCode;
    }

    public long getBuyerProductResponseID() {
        return mBuyerProductResponseID;
    }

    public boolean getSynced() {
        return mSynced;
    }

    public static ArrayList<BuyerProduct> getDataFromCursor(Cursor cursor) {
        ArrayList<BuyerProduct> buyerProducts = new ArrayList<>();
        while (cursor.moveToNext()) {
            buyerProducts.add(new BuyerProduct(cursor));
        }
        return buyerProducts;
    }
}
