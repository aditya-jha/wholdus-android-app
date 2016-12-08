package com.wholdus.www.wholdusbuyerapp.models;

import java.util.ArrayList;

/**
 * Created by kaustubh on 8/12/16.
 */

public class Suborder {

    private int m_ID;
    private String mOrderID;
    private String mSuborderID;
    private String mDisplayNumber;
    private String mSellerID;
    private Seller mSeller;
    private String mSellerAddressID;
    private SellerAddress mSellerAddress;
    private int mProductCount;
    private int mPieces;
    private float mRetailPrice;
    private float mCalculatedPrice;
    private float mEditedPrice;
    private float mShippingCharge;
    private float mCODCharge;
    private float mFinalPrice;
    private int mSuborderStatusValue;
    private String mSuborderStatusDisplay;
    private int mPaymentStatusValue;
    private String mPaymentStatusDisplay;
    private String mCreatedAt;

    private ArrayList<OrderItem> mOrderItems;

    public Suborder() {
    }

    public int getID() {
        return m_ID;
    }

    public String getOrderID() {
        return mOrderID;
    }

    public String getSuborderID() {
        return mSuborderID;
    }

    public String getDisplayNumber() {
        return mDisplayNumber;
    }

    public String getSellerID() {
        return mSellerID;
    }

    public Seller getSeller() {
        return mSeller;
    }

    public String getSellerAddressID() {
        return mSellerAddressID;
    }

    public SellerAddress getSellerAddress() {
        return mSellerAddress;
    }

    public int getProductCount() {
        return mProductCount;
    }

    public int getPieces() {
        return mPieces;
    }

    public float getRetailPrice() {
        return mRetailPrice;
    }

    public float getCalculatedPrice() {
        return mCalculatedPrice;
    }

    public float getEditedPrice() {
        return mEditedPrice;
    }

    public float getShippingCharge() {
        return mShippingCharge;
    }

    public float getCODCharge() {
        return mCODCharge;
    }

    public int getSuborderStatusValue() {
        return mSuborderStatusValue;
    }

    public String getSuborderStatusDisplay() {
        return mSuborderStatusDisplay;
    }

    public int getPaymentStatusValue() {
        return mPaymentStatusValue;
    }

    public String getPaymentStatusDisplay() {
        return mPaymentStatusDisplay;
    }

    public String getCreatedAt() {
        return mCreatedAt;
    }

    public ArrayList<OrderItem> getOrderItems() {
        return mOrderItems;
    }

}
