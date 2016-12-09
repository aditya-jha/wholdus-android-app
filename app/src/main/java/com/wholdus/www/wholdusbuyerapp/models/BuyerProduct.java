package com.wholdus.www.wholdusbuyerapp.models;

import android.database.Cursor;

/**
 * Created by aditya on 9/12/16.
 */

public class BuyerProduct {

    private int m_ID;
    private String mBuyerProductID;
    private String mBuyerProductResponseID;
    private Product mProduct;
    private Boolean mHasSwiped;
    private int mResponseCode;
    private double mStoreDiscount;

    public BuyerProduct() {
    }

    public BuyerProduct(Cursor cursor) {
    }

    public int get_ID() {
        return m_ID;
    }

    public double getStoreDiscount() {
        return mStoreDiscount;
    }

    public String getBuyerProductID() {
        return mBuyerProductID;
    }

    public Product getProduct() {
        return mProduct;
    }

    public Boolean getHasSwiped() {
        return mHasSwiped;
    }

    public int getResponseCode() {
        return mResponseCode;
    }

    public String getBuyerProductResponseID() {
        return mBuyerProductResponseID;
    }
}
