package com.wholdus.www.wholdusbuyerapp.models;

import android.database.Cursor;

import java.util.ArrayList;

import static com.wholdus.www.wholdusbuyerapp.databaseContracts.ProductsContract.CategoriesTable;

/**
 * Created by aditya on 10/12/16.
 */

public class Category {

    private int m_ID;
    private int mCategoryID;
    private String mCategoryName;
    private String mImageURL;
    private int mProductsCount;
    private boolean mLikeStatus;

    public Category() {
    }

    public Category(Cursor cursor) {
        setCategoryFromCursor(cursor);
    }

    private void setCategoryFromCursor(Cursor cursor) {
        m_ID = cursor.getInt(cursor.getColumnIndexOrThrow(CategoriesTable._ID));
        mCategoryID = cursor.getInt(cursor.getColumnIndexOrThrow(CategoriesTable.COLUMN_CATEGORY_ID));
        mCategoryName = cursor.getString(cursor.getColumnIndexOrThrow(CategoriesTable.COLUMN_CATEGORY_NAME));
        mImageURL = cursor.getString(cursor.getColumnIndexOrThrow(CategoriesTable.COLUMN_IMAGE_URL));
        mProductsCount = 0;
        mLikeStatus = false;
    }

    public void setProductsCount(Cursor cursor) {
        mProductsCount = cursor.getInt(cursor.getColumnIndexOrThrow(CategoriesTable.COLUMN_PRODUCTS_COUNT));
    }

    public void setLikeStatus(boolean status) {
        mLikeStatus = status;
    }

    public int get_ID() {
        return m_ID;
    }

    public int getCategoryID() {
        return mCategoryID;
    }

    public String getCategoryName() {
        return mCategoryName;
    }

    public String getImageURL() {
        return mImageURL;
    }

    public int getProductsCount() {
        return mProductsCount;
    }

    public boolean getLikeStatus() {
        return mLikeStatus;
    }

    public static ArrayList<Category> getCategoryArrayList(Cursor cursor) {
        ArrayList<Category> categories = new ArrayList<>();

        while (cursor.moveToNext()) {
            categories.add(new Category(cursor));
        }
        return categories;
    }
}
