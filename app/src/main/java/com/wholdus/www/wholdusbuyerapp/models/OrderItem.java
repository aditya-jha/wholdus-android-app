package com.wholdus.www.wholdusbuyerapp.models;

import android.database.Cursor;

import com.wholdus.www.wholdusbuyerapp.databaseContracts.OrdersContract;

import java.util.ArrayList;

/**
 * Created by kaustubh on 8/12/16.
 */

public class OrderItem {

    private int m_ID;
    private int mOrderItemID;
    private int mSuborderID;
    private int mProductID;
    private Product mProduct;
    private int mOrderShipmentID;
    private int mLots;
    private int mLotSize;
    private int mPieces;
    private float mRetailPricePerPiece;
    private float mCalculatedPricePerPiece;
    private float mEditedPricePerPiece;
    private float mFinalPrice;
    private int mOrderItemStatusValue;
    private String mOrderItemStatusDisplay;
    private String mTrackingUrl;
    private String mCreatedAt;
    private String mUpdatedAt;
    private String mRemarks;

    public OrderItem() {
    }
    
    public OrderItem(Cursor cursor){
        setDataFromCursor(cursor);
    }

    public static ArrayList<OrderItem> getOrderItemsFromCursor(Cursor cursor) {
        ArrayList<OrderItem> orderItems = new ArrayList<>();
        while (cursor.moveToNext()) {
            orderItems.add(new OrderItem(cursor));
        }
        return orderItems;
    }

    public void setDataFromCursor(Cursor cursor) {
        m_ID = cursor.getInt(cursor.getColumnIndexOrThrow(OrdersContract.OrderItemsTable._ID));
        mOrderItemID = cursor.getInt(cursor.getColumnIndexOrThrow(OrdersContract.OrderItemsTable.COLUMN_ORDER_ITEM_ID));
        mSuborderID = cursor.getInt(cursor.getColumnIndexOrThrow(OrdersContract.OrderItemsTable.COLUMN_SUBORDER_ID));
        mProductID = cursor.getInt(cursor.getColumnIndexOrThrow(OrdersContract.OrderItemsTable.COLUMN_PRODUCT_ID));
        mOrderShipmentID = cursor.getInt(cursor.getColumnIndexOrThrow(OrdersContract.OrderItemsTable.COLUMN_ORDER_SHIPMENT_ID));
        mLots = cursor.getInt(cursor.getColumnIndexOrThrow(OrdersContract.OrderItemsTable.COLUMN_LOTS));
        mLotSize = cursor.getInt(cursor.getColumnIndexOrThrow(OrdersContract.OrderItemsTable.COLUMN_LOT_SIZE));
        mPieces = cursor.getInt(cursor.getColumnIndexOrThrow(OrdersContract.OrderItemsTable.COLUMN_PIECES));
        mRetailPricePerPiece = cursor.getFloat(cursor.getColumnIndexOrThrow(OrdersContract.OrderItemsTable.COLUMN_RETAIL_PRICE_PER_PIECE));
        mCalculatedPricePerPiece = cursor.getFloat(cursor.getColumnIndexOrThrow(OrdersContract.OrderItemsTable.COLUMN_CALCULATED_PRICE_PER_PIECE));
        mEditedPricePerPiece = cursor.getFloat(cursor.getColumnIndexOrThrow(OrdersContract.OrderItemsTable.COLUMN_EDITED_PRICE_PER_PIECE));
        mFinalPrice = cursor.getFloat(cursor.getColumnIndexOrThrow(OrdersContract.OrderItemsTable.COLUMN_FINAL_PRICE));
        mOrderItemStatusValue = cursor.getInt(cursor.getColumnIndexOrThrow(OrdersContract.OrderItemsTable.COLUMN_ORDER_ITEM_STATUS_VALUE));
        mOrderItemStatusDisplay = cursor.getString(cursor.getColumnIndexOrThrow(OrdersContract.OrderItemsTable.COLUMN_ORDER_ITEM_STATUS_DISPLAY));
        mTrackingUrl = cursor.getString(cursor.getColumnIndexOrThrow(OrdersContract.OrderItemsTable.COLUMN_TRACKING_URL));
        mCreatedAt = cursor.getString(cursor.getColumnIndexOrThrow(OrdersContract.OrderItemsTable.COLUMN_CREATED_AT));
        mUpdatedAt = cursor.getString(cursor.getColumnIndexOrThrow(OrdersContract.OrderItemsTable.COLUMN_UPDATED_AT));
        mRemarks = cursor.getString(cursor.getColumnIndexOrThrow(OrdersContract.OrderItemsTable.COLUMN_REMARKS));
    }

    public int get_ID() {
        return m_ID;
    }

    public int getOrderItemID() {
        return mOrderItemID;
    }

    public int getSuborderID() {
        return mSuborderID;
    }

    public int getProductID() {
        return mProductID;
    }

    public Product getProduct() {
        return mProduct;
    }

    public void setProduct(Product product){mProduct = product;}

    public int getOrderShipmentID() {
        return mOrderShipmentID;
    }

    public int getLots() {
        return mLots;
    }

    public int getLotSize() {
        return mLotSize;
    }

    public int getPieces() {
        return mPieces;
    }

    public float getRetailPricePerPiece() {
        return mRetailPricePerPiece;
    }

    public float getCalculatedPricePerPiece() {
        return mCalculatedPricePerPiece;
    }

    public float getEditedPricePerPiece() {
        return mEditedPricePerPiece;
    }

    public float getFinalPrice() {
        return mFinalPrice;
    }

    public int getOrderItemStatusValue() {
        return mOrderItemStatusValue;
    }

    public String getOrderItemStatusDisplay() {
        return mOrderItemStatusDisplay;
    }

    public String getTrackingUrl() {
        return mTrackingUrl;
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
}
