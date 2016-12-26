package com.wholdus.www.wholdusbuyerapp.models;

import android.database.Cursor;

import com.wholdus.www.wholdusbuyerapp.databaseContracts.CartContract.SubCartsTable;

import java.util.ArrayList;

/**
 * Created by kaustubh on 26/12/16.
 */

public class SubCart {

    private int mCartID;
    private int mSubCartID;
    private int mSellerID;
    private Seller mSeller;
    private int mProductCount;
    private int mPieces;
    private float mRetailPrice;
    private float mShippingCharge;
    private float mFinalPrice;
    private int mSynced;
    private ArrayList<CartItem> mCartItems;

    public SubCart(){}

    public SubCart(Cursor cursor){setDataFromCursor(cursor);}

    public void setDataFromCursor(Cursor cursor){
        mCartID = cursor.getInt(cursor.getColumnIndexOrThrow(SubCartsTable.COLUMN_CART_ID));
        mSubCartID = cursor.getInt(cursor.getColumnIndexOrThrow(SubCartsTable.COLUMN_SUBCART_ID));
        mSellerID = cursor.getInt(cursor.getColumnIndexOrThrow(SubCartsTable.COLUMN_SELLER_ID));
        mProductCount = cursor.getInt(cursor.getColumnIndexOrThrow(SubCartsTable.COLUMN_PRODUCT_COUNT));
        mPieces = cursor.getInt(cursor.getColumnIndexOrThrow(SubCartsTable.COLUMN_PIECES));
        mRetailPrice = cursor.getFloat(cursor.getColumnIndexOrThrow(SubCartsTable.COLUMN_RETAIL_PRICE));
        mShippingCharge = cursor.getFloat(cursor.getColumnIndexOrThrow(SubCartsTable.COLUMN_SHIPPING_CHARGE));
        mFinalPrice = cursor.getFloat(cursor.getColumnIndexOrThrow(SubCartsTable.COLUMN_FINAL_PRICE));
        mSynced = cursor.getInt(cursor.getColumnIndexOrThrow(SubCartsTable.COLUMN_SYNCED));
    }

    public int getCartID(){return mCartID;}

    public int getSubCartID(){return mSubCartID;}

    public int getSellerID(){return mSellerID;}

    public Seller getSeller(){return mSeller;}

    public void setSeller(Seller seller){mSeller = seller;}

    public int getProductCount(){return mProductCount;}

    private int getPieces(){return mPieces;}

    public float getRetailPrice(){return mRetailPrice;}

    public float getShippingCharge(){return mShippingCharge;}

    public float getFinalPrice(){return mFinalPrice;}

    public int getSynced(){return mSynced;}

    public ArrayList<CartItem> getCartItems(){return mCartItems;}

    public void setCartItems(Cursor cursor){
        mCartItems = CartItem.getCartItemsFromCursor(cursor);
    }

    public static ArrayList<SubCart> getSubCartsFromCursor(Cursor cursor){
        ArrayList<SubCart> subCarts = new ArrayList<>();
        while (cursor.moveToNext()){
            subCarts.add(new SubCart(cursor));
        }
        return subCarts;
    }
}
