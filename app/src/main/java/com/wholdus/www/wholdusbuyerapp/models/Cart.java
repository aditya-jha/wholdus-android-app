package com.wholdus.www.wholdusbuyerapp.models;

import android.database.Cursor;

import com.wholdus.www.wholdusbuyerapp.databaseContracts.CartContract.CartTable;

import java.util.ArrayList;

/**
 * Created by kaustubh on 26/12/16.
 */

public class Cart {
    private int mCartID;
    private int mProductCount;
    private int mPieces;
    private float mRetailPrice;
    private float mCalculatedPrice;
    private float mShippingCharge;
    private float mFinalPrice;
    private int mSynced;
    private ArrayList<SubCart> mSubCarts;

    public Cart(){}

    public Cart(Cursor cursor){setDataFromCursor(cursor);}

    public void setDataFromCursor(Cursor cursor){
        mCartID = cursor.getInt(cursor.getColumnIndexOrThrow(CartTable.COLUMN_CART_ID));
        mProductCount = cursor.getInt(cursor.getColumnIndexOrThrow(CartTable.COLUMN_PRODUCT_COUNT));
        mPieces = cursor.getInt(cursor.getColumnIndexOrThrow(CartTable.COLUMN_PIECES));
        mRetailPrice = cursor.getFloat(cursor.getColumnIndexOrThrow(CartTable.COLUMN_RETAIL_PRICE));
        mCalculatedPrice = cursor.getFloat(cursor.getColumnIndexOrThrow(CartTable.COLUMN_CALCULATED_PRICE));
        mShippingCharge = cursor.getFloat(cursor.getColumnIndexOrThrow(CartTable.COLUMN_SHIPPING_CHARGE));
        mFinalPrice = cursor.getFloat(cursor.getColumnIndexOrThrow(CartTable.COLUMN_FINAL_PRICE));
        mSynced = cursor.getInt(cursor.getColumnIndexOrThrow(CartTable.COLUMN_SYNCED));
    }

    public int getCartID(){return mCartID;}

    public int getProductCount(){return mProductCount;}

    public int getPieces(){return mPieces;}

    public float getRetailPrice(){return mRetailPrice;}

    public float getCalculatedPrice(){return mCalculatedPrice;}

    public float getShippingCharge(){return mShippingCharge;}

    public float getFinalPrice(){return mFinalPrice;}

    public int getSynced(){return mSynced;}

    public ArrayList<SubCart> getSubCarts(){return mSubCarts;}

    public void setSubCarts(Cursor cursor){
        mSubCarts = SubCart.getSubCartsFromCursor(cursor);
    }
}
