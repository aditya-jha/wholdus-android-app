package com.wholdus.www.wholdusbuyerapp.models;

import android.database.Cursor;

import com.wholdus.www.wholdusbuyerapp.WholdusApplication;
import com.wholdus.www.wholdusbuyerapp.databaseContracts.OrdersContract;
import com.wholdus.www.wholdusbuyerapp.databaseHelpers.OrderDBHelper;

import java.util.ArrayList;

/**
 * Created by kaustubh on 7/12/16.
 */

public class Order {
    private int m_ID;
    private int mOrderID;
    private String mDisplayNumber;
    private int mBuyerAddressID;
    private BuyerAddress mBuyerAddress;
    private int mProductCount;
    private int mPieces;
    private float mRetailPrice;
    private float mCalculatedPrice;
    private float mEditedPrice;
    private float mShippingCharge;
    private float mCODCharge;
    private float mFinalPrice;
    private int mOrderStatusValue;
    private String mOrderStatusDisplay;
    private int mPaymentStatusValue;
    private String mPaymentStatusDisplay;
    private String mCreatedAt;
    private String mUpdatedAt;
    private String mRemarks;

    private ArrayList<Suborder> mSuborders;

    public Order() {
    }

    public Order(Cursor cursor) {
        setDataFromCursor(cursor);
    }

    public static ArrayList<Order> getOrdersFromCursor(Cursor cursor) {
        ArrayList<Order> orders = new ArrayList<>();
        while (cursor.moveToNext()) {
            orders.add(new Order(cursor));
        }
        return orders;
    }

    public void setDataFromCursor(Cursor cursor) {
        m_ID = cursor.getInt(cursor.getColumnIndexOrThrow(OrdersContract.OrdersTable._ID));
        mOrderID = cursor.getInt(cursor.getColumnIndexOrThrow(OrdersContract.OrdersTable.COLUMN_ORDER_ID));
        mDisplayNumber = cursor.getString(cursor.getColumnIndexOrThrow(OrdersContract.OrdersTable.COLUMN_DISPLAY_NUMBER));
        mBuyerAddressID = cursor.getInt(cursor.getColumnIndexOrThrow(OrdersContract.OrdersTable.COLUMN_BUYER_ADDRESS_ID));
        mProductCount = cursor.getInt(cursor.getColumnIndexOrThrow(OrdersContract.OrdersTable.COLUMN_PRODUCT_COUNT));
        mPieces = cursor.getInt(cursor.getColumnIndexOrThrow(OrdersContract.OrdersTable.COLUMN_PIECES));
        mRetailPrice = cursor.getFloat(cursor.getColumnIndexOrThrow(OrdersContract.OrdersTable.COLUMN_RETAIL_PRICE));
        mCalculatedPrice = cursor.getFloat(cursor.getColumnIndexOrThrow(OrdersContract.OrdersTable.COLUMN_CALCULATED_PRICE));
        mEditedPrice = cursor.getFloat(cursor.getColumnIndexOrThrow(OrdersContract.OrdersTable.COLUMN_EDITED_PRICE));
        mShippingCharge = cursor.getFloat(cursor.getColumnIndexOrThrow(OrdersContract.OrdersTable.COLUMN_SHIPPING_CHARGE));
        mCODCharge = cursor.getFloat(cursor.getColumnIndexOrThrow(OrdersContract.OrdersTable.COLUMN_COD_CHARGE));
        mFinalPrice = cursor.getFloat(cursor.getColumnIndexOrThrow(OrdersContract.OrdersTable.COLUMN_FINAL_PRICE));
        mOrderStatusValue = cursor.getInt(cursor.getColumnIndexOrThrow(OrdersContract.OrdersTable.COLUMN_ORDER_STATUS_VALUE));
        mOrderStatusDisplay = cursor.getString(cursor.getColumnIndexOrThrow(OrdersContract.OrdersTable.COLUMN_ORDER_STATUS_DISPLAY));
        mPaymentStatusValue = cursor.getInt(cursor.getColumnIndexOrThrow(OrdersContract.OrdersTable.COLUMN_PAYMENT_STATUS_VALUE));
        mPaymentStatusDisplay = cursor.getString(cursor.getColumnIndexOrThrow(OrdersContract.OrdersTable.COLUMN_PAYMENT_STATUS_DISPLAY));
        mCreatedAt = cursor.getString(cursor.getColumnIndexOrThrow(OrdersContract.OrdersTable.COLUMN_CREATED_AT));
        mUpdatedAt = cursor.getString(cursor.getColumnIndexOrThrow(OrdersContract.OrdersTable.COLUMN_UPDATED_AT));
        mRemarks = cursor.getString(cursor.getColumnIndexOrThrow(OrdersContract.OrdersTable.COLUMN_REMARKS));
    }

    public int get_ID() {
        return m_ID;
    }

    public int getOrderID() {
        return mOrderID;
    }

    public String getDisplayNumber() {
        return mDisplayNumber;
    }

    public int getBuyerAddressID() {
        return mBuyerAddressID;
    }

    public BuyerAddress getBuyerAddress() {
        return mBuyerAddress;
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

    public float getFinalPrice() {
        return mFinalPrice;
    }

    public int getOrderStatusValue() {
        return mOrderStatusValue;
    }

    public String getOrderStatusDisplay() {
        return mOrderStatusDisplay;
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

    public String getRemarks() {
        return mRemarks;
    }

    public ArrayList<Suborder> getSuborders() {
        return mSuborders;
    }

    public void setSuborders(Cursor cursor){
        mSuborders = Suborder.getSubordersFromCursor(cursor);
    }
}
