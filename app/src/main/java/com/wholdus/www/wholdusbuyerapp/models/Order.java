package com.wholdus.www.wholdusbuyerapp.models;

import android.database.Cursor;

import com.wholdus.www.wholdusbuyerapp.databaseContracts.OrdersContract;

import java.util.ArrayList;

/**
 * Created by kaustubh on 7/12/16.
 */

public class Order {
    private int ID;
    private String orderID;
    private String displayNumber;
    private String buyerAddressID;
    private BuyerAddress buyerAddress;
    private int productCount;
    private int pieces;
    private float retailPrice;
    private float calculatedPrice;
    private float editedPrice;
    private float shippingCharge;
    private float CODCharge;
    private float finalPrice;
    private int orderStatusValue;
    private String orderStatusDisplay;
    private int paymentStatusValue;
    private String paymentStatusDisplay;
    private String createdAt;
    private String remarks;

    private ArrayList<Suborder> suborders;

    public Order(){}

    public Order(Cursor cursor){
    }

    public void setDataFromCursor(Cursor cursor){
        ID = cursor.getInt(cursor.getColumnIndexOrThrow(OrdersContract.OrdersTable._ID));
        orderID = cursor.getString(cursor.getColumnIndexOrThrow(OrdersContract.OrdersTable.COLUMN_ORDER_ID));
        displayNumber = cursor.getString(cursor.getColumnIndexOrThrow(OrdersContract.OrdersTable.COLUMN_DISPLAY_NUMBER));
        buyerAddressID = cursor.getString(cursor.getColumnIndexOrThrow(OrdersContract.OrdersTable.COLUMN_BUYER_ADDRESS_ID));
        productCount = cursor.getInt(cursor.getColumnIndexOrThrow(OrdersContract.OrdersTable.COLUMN_PRODUCT_COUNT));
        pieces = cursor.getInt(cursor.getColumnIndexOrThrow(OrdersContract.OrdersTable.COLUMN_PIECES));
        retailPrice = cursor.getFloat(cursor.getColumnIndexOrThrow(OrdersContract.OrdersTable.COLUMN_RETAIL_PRICE));
        calculatedPrice = cursor.getFloat(cursor.getColumnIndexOrThrow(OrdersContract.OrdersTable.COLUMN_CALCULATED_PRICE));
        editedPrice = cursor.getFloat(cursor.getColumnIndexOrThrow(OrdersContract.OrdersTable.COLUMN_EDITED_PRICE));
        shippingCharge = cursor.getFloat(cursor.getColumnIndexOrThrow(OrdersContract.OrdersTable.COLUMN_SHIPPING_CHARGE));
        CODCharge = cursor.getFloat(cursor.getColumnIndexOrThrow(OrdersContract.OrdersTable.COLUMN_COD_CHARGE));
        finalPrice = cursor.getFloat(cursor.getColumnIndexOrThrow(OrdersContract.OrdersTable.COLUMN_FINAL_PRICE));
        orderStatusValue = cursor.getInt(cursor.getColumnIndexOrThrow(OrdersContract.OrdersTable.COLUMN_ORDER_STATUS_VALUE));
        orderStatusDisplay = cursor.getString(cursor.getColumnIndexOrThrow(OrdersContract.OrdersTable.COLUMN_ORDER_STATUS_DISPLAY));
        paymentStatusValue = cursor.getInt(cursor.getColumnIndexOrThrow(OrdersContract.OrdersTable.COLUMN_PAYMENT_STATUS_VALUE));
        paymentStatusDisplay = cursor.getString(cursor.getColumnIndexOrThrow(OrdersContract.OrdersTable.COLUMN_PAYMENT_STATUS_DISPLAY));
        createdAt = cursor.getString(cursor.getColumnIndexOrThrow(OrdersContract.OrdersTable.COLUMN_CREATED_AT));
        remarks = cursor.getString(cursor.getColumnIndexOrThrow(OrdersContract.OrdersTable.COLUMN_REMARKS));
    }

    public int getID(){return ID;}

    public String getOrderID(){return orderID;}

    public String getDisplayNumber(){return displayNumber;}

    public String getBuyerAddressID(){return buyerAddressID;}

    public BuyerAddress getBuyerAddress(){return buyerAddress;}

    public int getProductCount(){return productCount;}

    public int getPieces(){return pieces;}

    public float getRetailPrice(){return retailPrice;}

    public float getCalculatedPrice(){return calculatedPrice;}

    public float getEditedPrice(){return editedPrice;}

    public float getShippingCharge(){return shippingCharge;}

    public float getCODCharge(){return CODCharge;}

    public float getFinalPrice(){return finalPrice;}

    public int getOrderStatusValue(){return orderStatusValue;}

    public String getOrderStatusDisplay(){return orderStatusDisplay;}

    public int getPaymentStatusValue(){return paymentStatusValue;}

    public String getPaymentStatusDisplay(){return paymentStatusDisplay;}

    public String getCreatedAt(){return createdAt;}

    public String getRemarks(){return remarks;}

    public ArrayList<Suborder> getSuborders(){return suborders;}
}
