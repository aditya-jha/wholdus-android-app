package com.wholdus.www.wholdusbuyerapp.models;

import android.database.Cursor;
import android.text.TextUtils;

import com.wholdus.www.wholdusbuyerapp.databaseContracts.CatalogContract;

import java.util.ArrayList;

/**
 * Created by kaustubh on 8/12/16.
 */

public class Product {

    private int m_ID;
    private int mProductID;
    private int mSellerID;
    private Seller mSeller;
    private int mCategoryID;
    private Category mCategory;
    private float mPricePerUnit;
    private int mLotSize;
    private float mPricePerLot;
    private float mMinPricePerUnit;
    private float mMargin;
    private String mUrl;
    //TODO: What to do about images
    private String[] mProductImageNumbers;
    private int mImageCount;
    private String mImagePathString;
    private String mColours;
    private String mFabricGSM;
    private String mSizes;
    private ProductDetails mProductDetails;

    public Product(){}

    public Product(Cursor cursor) {
        setDataFromCursor(cursor);
    }

    public void setDataFromCursor(Cursor cursor){
        m_ID = cursor.getInt(cursor.getColumnIndexOrThrow(CatalogContract.ProductsTable._ID));
        mProductID = cursor.getInt(cursor.getColumnIndexOrThrow(CatalogContract.ProductsTable.COLUMN_PRODUCT_ID));
        mSellerID = cursor.getInt(cursor.getColumnIndexOrThrow(CatalogContract.ProductsTable.COLUMN_SELLER_ID));
        mCategoryID = cursor.getInt(cursor.getColumnIndexOrThrow(CatalogContract.ProductsTable.COLUMN_CATEGORY_ID));
        mPricePerUnit = cursor.getFloat(cursor.getColumnIndexOrThrow(CatalogContract.ProductsTable.COLUMN_PRICE_PER_UNIT));
        mLotSize = cursor.getInt(cursor.getColumnIndexOrThrow(CatalogContract.ProductsTable.COLUMN_LOT_SIZE));
        mPricePerLot = cursor.getFloat(cursor.getColumnIndexOrThrow(CatalogContract.ProductsTable.COLUMN_PRICE_PER_LOT));
        mMinPricePerUnit = cursor.getFloat(cursor.getColumnIndexOrThrow(CatalogContract.ProductsTable.COLUMN_MIN_PRICE_PER_UNIT));
        mMargin = cursor.getFloat(cursor.getColumnIndexOrThrow(CatalogContract.ProductsTable.COLUMN_MARGIN));
        mUrl = cursor.getString(cursor.getColumnIndexOrThrow(CatalogContract.ProductsTable.COLUMN_URL));
        mImageCount = cursor.getInt(cursor.getColumnIndexOrThrow(CatalogContract.ProductsTable.COLUMN_IMAGE_COUNT));
        mProductImageNumbers = TextUtils.split(cursor.getString(cursor.getColumnIndexOrThrow(CatalogContract.ProductsTable.COLUMN_IMAGE_NUMBERS)), ",");
        mColours = cursor.getString(cursor.getColumnIndexOrThrow(CatalogContract.ProductsTable.COLUMN_COLOURS));
        mFabricGSM = cursor.getString(cursor.getColumnIndexOrThrow(CatalogContract.ProductsTable.COLUMN_FABRIC_GSM));
        mSizes = cursor.getString(cursor.getColumnIndexOrThrow(CatalogContract.ProductsTable.COLUMN_SIZES));

        mImagePathString = cursor.getString(cursor.getColumnIndexOrThrow(CatalogContract.ProductsTable.COLUMN_IMAGE_PATH))
                + "%s/" + cursor.getString(cursor.getColumnIndexOrThrow(CatalogContract.ProductsTable.COLUMN_IMAGE_NAME))
                + "-%s.jpg";
    }

    public ArrayList<String> getAllImageUrls(String imageSize){
        ArrayList<String> allImageUrls = new ArrayList<>();
        for (String imageNumber:mProductImageNumbers){
            allImageUrls.add(getImageUrl(imageSize, imageNumber));
        }
        return allImageUrls;
    }
    public String getImageUrl(String imageSize, String imageNumber){
        return String.format(mImagePathString, imageSize, imageNumber);
    }

    public int get_ID(){return m_ID;}

    public int getProductID(){return  mProductID;}

    public int getSellerID(){return mSellerID;}

    public Seller getSeller(){return mSeller;}

    public int getCategoryID(){return mCategoryID;}

    public Category getCategory(){return mCategory;}

    public float getPricePerUnit(){return mPricePerUnit;}

    public int getLotSize(){return mLotSize;}

    public float getPricePerLot(){return mPricePerLot;}

    public float getMinPricePerUnit(){return mMinPricePerUnit;}

    public float getMargin(){return mMargin;}

    public String getUrl(){return mUrl;}

    public String[] getProductImageNumbers(){return mProductImageNumbers;}

    public int getImageCount(){return mImageCount;}

    public String getColours(){return mColours;}

    public String getFabricGSM(){return mFabricGSM;}

    public String getSizes(){return mSizes;}

    private ProductDetails getProductDetails(){return mProductDetails;}

    public void setProductDetails(Cursor cursor){
        mProductDetails = new ProductDetails(cursor);
    }

}
