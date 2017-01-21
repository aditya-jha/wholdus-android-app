package com.wholdus.www.wholdusbuyerapp.models;

import android.database.Cursor;
import android.text.TextUtils;

import com.wholdus.www.wholdusbuyerapp.databaseContracts.CatalogContract;
import com.wholdus.www.wholdusbuyerapp.helperClasses.Constants;
import com.wholdus.www.wholdusbuyerapp.helperClasses.HelperFunctions;

import java.util.ArrayList;

/**
 * Created by kaustubh on 8/12/16.
 */

public class Product {

    private int m_ID;
    private int mProductID;
    private String mName;
    private float mPricePerUnit;
    private int mLotSize;
    private float mPricePerLot;
    private float mMinPricePerUnit;
    private float mMargin;
    private String mUrl;
    private String[] mProductImageNumbers; //TODO: What to do about images
    private int mImageCount;
    private String mImagePathString;
    private String mColours;
    private String mFabricGSM;
    private String mSizes;
    private boolean mLiked;

    private int mSellerID;
    private int mCategoryID;

    private Seller mSeller;
    private Category mCategory;
    private ProductDetails mProductDetails;

    public Product() {
    }

    public Product(Cursor cursor) {
        setDataFromCursor(cursor);
    }

    public void setDataFromCursor(Cursor cursor) {
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
        mName = cursor.getString(cursor.getColumnIndexOrThrow(CatalogContract.ProductsTable.COLUMN_NAME));
        mColours = cursor.getString(cursor.getColumnIndexOrThrow(CatalogContract.ProductsTable.COLUMN_COLOURS));
        mFabricGSM = cursor.getString(cursor.getColumnIndexOrThrow(CatalogContract.ProductsTable.COLUMN_FABRIC_GSM));
        mSizes = cursor.getString(cursor.getColumnIndexOrThrow(CatalogContract.ProductsTable.COLUMN_SIZES));
        mName = cursor.getString(cursor.getColumnIndexOrThrow(CatalogContract.ProductsTable.COLUMN_NAME));
        mImagePathString = cursor.getString(cursor.getColumnIndexOrThrow(CatalogContract.ProductsTable.COLUMN_IMAGE_PATH))
                + "%s/" + cursor.getString(cursor.getColumnIndexOrThrow(CatalogContract.ProductsTable.COLUMN_IMAGE_NAME))
                + "-%s.jpg";
        mLiked = cursor.getInt(cursor.getColumnIndexOrThrow(CatalogContract.ProductsTable.COLUMN_RESPONSE_CODE)) == 1;
    }

    public static ArrayList<Product> getProductsFromCursor(Cursor cursor) {
        ArrayList<Product> products = new ArrayList<>();
        while (cursor.moveToNext()) {
            products.add(new Product(cursor));
        }
        return products;
    }

    public ArrayList<String> getAllImageUrls(String imageSize) {
        ArrayList<String> allImageUrls = new ArrayList<>();
        for (String imageNumber : mProductImageNumbers) {
            allImageUrls.add(getImageUrl(imageSize, imageNumber));
        }
        return allImageUrls;
    }

    public String getImageUrl(String imageSize, String imageNumber) {
        return HelperFunctions.generateUrl(String.format(mImagePathString, imageSize, imageNumber));
    }

    public int get_ID() {
        return m_ID;
    }

    public int getProductID() {
        return mProductID;
    }

    public float getPricePerUnit() {
        return mPricePerUnit;
    }

    public int getLotSize() {
        return mLotSize;
    }

    public float getPricePerLot() {
        return mPricePerLot;
    }

    public float getMinPricePerUnit() {
        return mMinPricePerUnit;
    }

    public float getMargin() {
        return mMargin;
    }

    public String getUrl() {
        return Constants.WEBSITE_URL + mUrl;
    }

    public String[] getProductImageNumbers() {
        return mProductImageNumbers;
    }

    public int getImageCount() {
        return mImageCount;
    }

    public String getColours() {
        return mColours;
    }

    public String getFabricGSM() {
        return mFabricGSM;
    }

    public String getSizes() {
        return mSizes;
    }

    public int getSellerID() {
        return mSellerID;
    }

    public int getCategoryID() {
        return mCategoryID;
    }

    public Seller getSeller() {
        return mSeller;
    }

    public void setSeller(Seller seller) {
        mSeller = seller;
    }

    public String getName(){return mName;}

    public Category getCategory() {
        return mCategory;
    }

    public void setCategory(Category category) {
        mCategory = category;
    }

    public ProductDetails getProductDetails() {
        return mProductDetails;
    }

    public void setProductDetails(Cursor cursor) {
        mProductDetails = new ProductDetails(cursor);
    }

    public void setSeller(Cursor cursor) {
        mSeller = new Seller(cursor);
    }

    public void setCategory(Cursor cursor) {
        mCategory = new Category(cursor);
    }

    public boolean getLikeStatus() {return mLiked;}

    public void toggleLikeStatus() {mLiked = !mLiked;}
}
