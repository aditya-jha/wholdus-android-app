package com.wholdus.www.wholdusbuyerapp.models;

import android.database.Cursor;

import com.wholdus.www.wholdusbuyerapp.databaseContracts.CartContract.CartItemsTable;

import java.util.ArrayList;

/**
 * Created by kaustubh on 23/12/16.
 */

public class CartItem {
    private int mCartItemID;
    private int mSubCartID;
    private int mProductID;
    private int mPieces;
    private int mLots;
    private int mLotSize;
    private float mRetailPricePerPiece;
    private float mCalculatedPricePerPiece;
    private float mShippingCharge;
    private float mFinalPrice;
    private String mCreatedAt;
    private String mUpdatedAt;
    private int mSynced;

    public CartItem(Cursor cursor){
        setDataFromCursor(cursor);
    }

    public void setDataFromCursor(Cursor cursor){
        mCartItemID = cursor.getInt(cursor.getColumnIndexOrThrow(CartItemsTable.COLUMN_CART_ITEM_ID));
        mSubCartID = cursor.getInt(cursor.getColumnIndexOrThrow(CartItemsTable.COLUMN_SUBCART_ID));
        mProductID = cursor.getInt(cursor.getColumnIndexOrThrow(CartItemsTable.COLUMN_PRODUCT_ID));
        mPieces = cursor.getInt(cursor.getColumnIndexOrThrow(CartItemsTable.COLUMN_PIECES));
        mLots = cursor.getInt(cursor.getColumnIndexOrThrow(CartItemsTable.COLUMN_LOTS));
        mLotSize = cursor.getInt(cursor.getColumnIndexOrThrow(CartItemsTable.COLUMN_LOT_SIZE));
        mRetailPricePerPiece = cursor.getFloat(cursor.getColumnIndexOrThrow(CartItemsTable.COLUMN_RETAIL_PRICE_PER_PIECE));
        mCalculatedPricePerPiece = cursor.getFloat(cursor.getColumnIndexOrThrow(CartItemsTable.COLUMN_CALCULATED_PRICE_PER_PIECE));
        mShippingCharge = cursor.getFloat(cursor.getColumnIndexOrThrow(CartItemsTable.COLUMN_SHIPPING_CHARGE));
        mFinalPrice = cursor.getFloat(cursor.getColumnIndexOrThrow(CartItemsTable.COLUMN_FINAL_PRICE));
        mCreatedAt = cursor.getString(cursor.getColumnIndexOrThrow(CartItemsTable.COLUMN_CREATED_AT));
        mUpdatedAt = cursor.getString(cursor.getColumnIndexOrThrow(CartItemsTable.COLUMN_UPDATED_AT));
        mSynced = cursor.getInt(cursor.getColumnIndexOrThrow(CartItemsTable.COLUMN_SYNCED));
    }

    public static ArrayList<CartItem> getCartItemsFromCursor(Cursor cursor){
        ArrayList<CartItem> cartItems = new ArrayList<>();
        while (cursor.moveToNext()){
            cartItems.add(new CartItem(cursor));
        }
        return cartItems;
    }

    public int getCartItemID(){return mCartItemID;}

    public int getSubCartID(){return mSubCartID;}

    public int getProductID(){return mProductID;}

    public int getPieces(){return mPieces;}

    public int getLots(){return mLots;}

    public int getLotSize(){return mLotSize;}

    public float getRetailPricePerPiece(){return mRetailPricePerPiece;}

    public float getCalculatedPricePerPiece(){return mCalculatedPricePerPiece;}

    public float getShippingCharge(){return mShippingCharge;}

    public float getFinalPrice(){return mFinalPrice;}

    public String getCreatedAt(){return mCreatedAt;}

    public String getUpdatedAt(){return mUpdatedAt;}

    public int getSynced(){return mSynced;}
}
