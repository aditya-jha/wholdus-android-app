package com.wholdus.www.wholdusbuyerapp.models;

import android.database.Cursor;
import android.renderscript.Double2;

import com.wholdus.www.wholdusbuyerapp.databaseContracts.CatalogContract;
import com.wholdus.www.wholdusbuyerapp.helperClasses.GlobalAccessHelper;
import com.wholdus.www.wholdusbuyerapp.helperClasses.HelperFunctions;

import java.util.ArrayList;

/**
 * Created by aditya on 15/12/16.
 * product model to hold limited data needed to display in product grids
 */

public class GridProductModel {

    private int mProductID;
    private String mImageUrl;
    private String mName;
    private Double mPrice;
    private String mFabric;
    private int mCartCount;

    public GridProductModel() {

    }

    public GridProductModel(Cursor cursor) {
        mProductID = cursor.getInt(cursor.getColumnIndexOrThrow(CatalogContract.ProductsTable.COLUMN_PRODUCT_ID));
        mImageUrl = getImageUrl(cursor);
        mName = cursor.getString(cursor.getColumnIndexOrThrow(CatalogContract.ProductsTable.COLUMN_NAME));
        mPrice = cursor.getDouble(cursor.getColumnIndexOrThrow(CatalogContract.ProductsTable.COLUMN_MIN_PRICE_PER_UNIT));
        mFabric = cursor.getString(cursor.getColumnIndexOrThrow(CatalogContract.ProductsTable.COLUMN_FABRIC_GSM));
        mImageUrl = cursor.getString(cursor.getColumnIndexOrThrow(CatalogContract.ProductsTable.COLUMN_IMAGE_PATH))
                + "%s/" + cursor.getString(cursor.getColumnIndexOrThrow(CatalogContract.ProductsTable.COLUMN_IMAGE_NAME))
                + "-%s.jpg";
        mCartCount = 0;
    }

    public static ArrayList<GridProductModel> getGridProducts(Cursor cursor) {
        ArrayList<GridProductModel> gridProducts = new ArrayList<>();
        while (cursor.moveToNext()) {
            gridProducts.add(new GridProductModel(cursor));
        }
        return gridProducts;
    }

    private String getImageUrl(Cursor cursor) {
        return "";
    }

    public int getProductID() {
        return mProductID;
    }

    public String getImageUrl(String imageSize, String imageNumber) {
        return HelperFunctions.generateUrl(String.format(mImageUrl, imageSize, imageNumber));
    }

    public String getName() {
        return mName;
    }

    public Double getPrice() {
        return mPrice;
    }

    public String getFabric() {
        return mFabric;
    }

    public int getCartCount() {
        return mCartCount;
    }
}
