package com.wholdus.www.wholdusbuyerapp.models;

import android.database.Cursor;

import com.wholdus.www.wholdusbuyerapp.databaseContracts.OrdersContract;

import java.util.ArrayList;

/**
 * Created by kaustubh on 8/12/16.
 */

public class Suborder {

    private int m_ID;
    private int mOrderID;
    private int mSuborderID;
    private String mDisplayNumber;
    private int mSellerID;
    private Seller mSeller;
    private int mSellerAddressID;
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
    private String mUpdatedAt;

    private ArrayList<OrderItem> mOrderItems;

    public Suborder() {
    }
    
    public Suborder(Cursor cursor){
        setDataFromCursor(cursor);
    }

    public void setDataFromCursor(Cursor cursor) {
        m_ID = cursor.getInt(cursor.getColumnIndexOrThrow(OrdersContract.SubordersTable._ID));
        mOrderID = cursor.getInt(cursor.getColumnIndexOrThrow(OrdersContract.SubordersTable.COLUMN_ORDER_ID));
        mSuborderID = cursor.getInt(cursor.getColumnIndexOrThrow(OrdersContract.SubordersTable.COLUMN_SUBORDER_ID));
        mDisplayNumber = cursor.getString(cursor.getColumnIndexOrThrow(OrdersContract.SubordersTable.COLUMN_DISPLAY_NUMBER));
        mSellerID = cursor.getInt(cursor.getColumnIndexOrThrow(OrdersContract.SubordersTable.COLUMN_SELLER_ID));
        mSellerAddressID = cursor.getInt(cursor.getColumnIndexOrThrow(OrdersContract.SubordersTable.COLUMN_SELLER_ADDRESS_ID));
        mProductCount = cursor.getInt(cursor.getColumnIndexOrThrow(OrdersContract.SubordersTable.COLUMN_PRODUCT_COUNT));
        mPieces = cursor.getInt(cursor.getColumnIndexOrThrow(OrdersContract.SubordersTable.COLUMN_PIECES));
        mRetailPrice = cursor.getFloat(cursor.getColumnIndexOrThrow(OrdersContract.SubordersTable.COLUMN_RETAIL_PRICE));
        mCalculatedPrice = cursor.getFloat(cursor.getColumnIndexOrThrow(OrdersContract.SubordersTable.COLUMN_CALCULATED_PRICE));
        mEditedPrice = cursor.getFloat(cursor.getColumnIndexOrThrow(OrdersContract.SubordersTable.COLUMN_EDITED_PRICE));
        mShippingCharge = cursor.getFloat(cursor.getColumnIndexOrThrow(OrdersContract.SubordersTable.COLUMN_SHIPPING_CHARGE));
        mCODCharge = cursor.getFloat(cursor.getColumnIndexOrThrow(OrdersContract.SubordersTable.COLUMN_COD_CHARGE));
        mFinalPrice = cursor.getFloat(cursor.getColumnIndexOrThrow(OrdersContract.SubordersTable.COLUMN_FINAL_PRICE));
        mSuborderStatusValue = cursor.getInt(cursor.getColumnIndexOrThrow(OrdersContract.SubordersTable.COLUMN_SUBORDER_STATUS_VALUE));
        mSuborderStatusDisplay = cursor.getString(cursor.getColumnIndexOrThrow(OrdersContract.SubordersTable.COLUMN_SUBORDER_STATUS_DISPLAY));
        mPaymentStatusValue = cursor.getInt(cursor.getColumnIndexOrThrow(OrdersContract.SubordersTable.COLUMN_PAYMENT_STATUS_VALUE));
        mPaymentStatusDisplay = cursor.getString(cursor.getColumnIndexOrThrow(OrdersContract.SubordersTable.COLUMN_PAYMENT_STATUS_DISPLAY));
        mCreatedAt = cursor.getString(cursor.getColumnIndexOrThrow(OrdersContract.SubordersTable.COLUMN_CREATED_AT));
        mUpdatedAt = cursor.getString(cursor.getColumnIndexOrThrow(OrdersContract.SubordersTable.COLUMN_UPDATED_AT));
    }

    public int getID() {
        return m_ID;
    }

    public int getOrderID() {
        return mOrderID;
    }

    public int getSuborderID() {
        return mSuborderID;
    }

    public String getDisplayNumber() {
        return mDisplayNumber;
    }

    public int getSellerID() {
        return mSellerID;
    }

    public Seller getSeller() {
        return mSeller;
    }

    public int getSellerAddressID() {
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

    public String getUpdatedAt() {
        return mUpdatedAt;
    }

    public ArrayList<OrderItem> getOrderItems() {
        return mOrderItems;
    }

}
