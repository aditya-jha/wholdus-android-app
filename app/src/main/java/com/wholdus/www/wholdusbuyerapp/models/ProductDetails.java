package com.wholdus.www.wholdusbuyerapp.models;

import android.database.Cursor;

import com.wholdus.www.wholdusbuyerapp.databaseContracts.ProductsContract;

/**
 * Created by kaustubh on 14/12/16.
 */

public class ProductDetails {

    private int mProductDetailsID;
    private String mUnit;
    private String mDisplayName;
    private boolean mDeleteStatus;
    private boolean mShowOnline;
    private String mWarranty;
    private String mSpecialFeature;
    private String mAvailability;
    private String mStyle;
    private String mManufacturedCity;
    private String mPattern;
    private String mLotDescription;
    private String mDescription;
    private String mWorkDecorationType;
    private String mNeckCollarType;
    private String mDispatchedIn;
    private String mRemarks;
    private String mSellerCatalogNumber;
    private String mSleeve;
    private String mGender;
    private float mWeightPerUnit;
    private String mPackagingDetails;
    private String mLength;
    private String mCreatedAt;
    private String mUpdatedAt;

    public ProductDetails(Cursor cursor){
        setDataFromCursor(cursor);
    }

    public void setDataFromCursor(Cursor cursor){
        mProductDetailsID = cursor.getInt(cursor.getColumnIndexOrThrow(ProductsContract.ProductsTable.COLUMN_PRODUCT_DETAILS_ID));
        mUnit = cursor.getString(cursor.getColumnIndexOrThrow(ProductsContract.ProductsTable.COLUMN_UNIT));
        mDisplayName = cursor.getString(cursor.getColumnIndexOrThrow(ProductsContract.ProductsTable.COLUMN_DISPLAY_NAME));
        mDeleteStatus = (cursor.getInt(cursor.getColumnIndexOrThrow(ProductsContract.ProductsTable.COLUMN_DELETE_STATUS))!=0);
        mShowOnline = (cursor.getInt(cursor.getColumnIndexOrThrow(ProductsContract.ProductsTable.COLUMN_SHOW_ONLINE))!=0);
        mWarranty = cursor.getString(cursor.getColumnIndexOrThrow(ProductsContract.ProductsTable.COLUMN_WARRANTY));
        mSpecialFeature = cursor.getString(cursor.getColumnIndexOrThrow(ProductsContract.ProductsTable.COLUMN_SPECIAL_FEATURE));
        mAvailability = cursor.getString(cursor.getColumnIndexOrThrow(ProductsContract.ProductsTable.COLUMN_AVAILABILITY));
        mStyle = cursor.getString(cursor.getColumnIndexOrThrow(ProductsContract.ProductsTable.COLUMN_STYLE));
        mManufacturedCity = cursor.getString(cursor.getColumnIndexOrThrow(ProductsContract.ProductsTable.COLUMN_MANUFACTURED_CITY));
        mPattern = cursor.getString(cursor.getColumnIndexOrThrow(ProductsContract.ProductsTable.COLUMN_PATTERN));
        mLotDescription = cursor.getString(cursor.getColumnIndexOrThrow(ProductsContract.ProductsTable.COLUMN_LOT_DESCRIPTION));
        mDescription = cursor.getString(cursor.getColumnIndexOrThrow(ProductsContract.ProductsTable.COLUMN_DESCRIPTION));
        mWorkDecorationType = cursor.getString(cursor.getColumnIndexOrThrow(ProductsContract.ProductsTable.WORK_DECORATION_TYPE));
        mNeckCollarType = cursor.getString(cursor.getColumnIndexOrThrow(ProductsContract.ProductsTable.COLUMN_NECK_COLLAR_TYPE));
        mDispatchedIn = cursor.getString(cursor.getColumnIndexOrThrow(ProductsContract.ProductsTable.COLUMN_DISPATCHED_IN));
        mRemarks = cursor.getString(cursor.getColumnIndexOrThrow(ProductsContract.ProductsTable.COLUMN_REMARKS));
        mSellerCatalogNumber = cursor.getString(cursor.getColumnIndexOrThrow(ProductsContract.ProductsTable.COLUMN_SELLER_CATALOG_NUMBER));
        mSleeve = cursor.getString(cursor.getColumnIndexOrThrow(ProductsContract.ProductsTable.COLUMN_SLEEVE));
        mGender = cursor.getString(cursor.getColumnIndexOrThrow(ProductsContract.ProductsTable.COLUMN_GENDER));
        mWeightPerUnit = cursor.getFloat(cursor.getColumnIndexOrThrow(ProductsContract.ProductsTable.COLUMN_WEIGHT_PER_UNIT));
        mPackagingDetails = cursor.getString(cursor.getColumnIndexOrThrow(ProductsContract.ProductsTable.COLUMN_PACKAGING_DETAILS));
        mLength = cursor.getString(cursor.getColumnIndexOrThrow(ProductsContract.ProductsTable.COLUMN_LENGTH));
        mCreatedAt = cursor.getString(cursor.getColumnIndexOrThrow(ProductsContract.ProductsTable.COLUMN_CREATED_AT));
        mUpdatedAt = cursor.getString(cursor.getColumnIndexOrThrow(ProductsContract.ProductsTable.COLUMN_UPDATED_AT));
    }

    public int getProductDetailsID(){return mProductDetailsID;}

    public String getmUnit(){return mUnit;}

    public String getmDisplayName(){return mDisplayName;}

    public boolean getDeleteStatus(){return mDeleteStatus;}

    public boolean getShowOnline(){return mShowOnline;}

    public String getWarranty(){return mWarranty;}

    public String getSpecialFeature(){return mSpecialFeature;}

    public String getAvailability(){return mAvailability;}

    public String getStyle(){return mStyle;}

    public String getManufacturedCity(){return mManufacturedCity;}

    public String getPattern(){return mPattern;}

    public String getLotDescription(){return mLotDescription;}

    public String getDescription(){return mDescription;}

    public String getWorkDecorationType(){return mWorkDecorationType;}

    public String getNeckCollarType(){return mNeckCollarType;}

    public String getDispatchedIn(){return mDispatchedIn;}

    public String getRemarks(){return mRemarks;}

    public String getSellerCatalogNumber(){return mSellerCatalogNumber;}

    public String getSleeve(){return mSleeve;}

    public String getGender(){return mGender;}

    public float getWeightPerUnit(){return mWeightPerUnit;}

    public String getPackagingDetails(){return mPackagingDetails;}

    public String getLength(){return mLength;}

    public String getCreatedAt(){return mCreatedAt;}

    public String getUpdatedAt(){return mUpdatedAt;}
}
