package com.wholdus.www.wholdusbuyerapp.loaders;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.Nullable;

import com.wholdus.www.wholdusbuyerapp.databaseContracts.CatalogContract;
import com.wholdus.www.wholdusbuyerapp.databaseHelpers.CatalogDBHelper;
import com.wholdus.www.wholdusbuyerapp.helperClasses.FilterClass;
import com.wholdus.www.wholdusbuyerapp.models.GridProductModel;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by aditya on 15/12/16.
 */

public class GridProductsLoader extends AbstractLoader<ArrayList<GridProductModel>> {

    private static final String[] columns = {
            CatalogContract.ProductsTable._ID,
            CatalogContract.ProductsTable.COLUMN_NAME,
            CatalogContract.ProductsTable.COLUMN_PRODUCT_ID,
            CatalogContract.ProductsTable.COLUMN_MIN_PRICE_PER_UNIT,
            CatalogContract.ProductsTable.COLUMN_FABRIC_GSM,
            CatalogContract.ProductsTable.COLUMN_IMAGE_COUNT,
            CatalogContract.ProductsTable.COLUMN_IMAGE_NAME,
            CatalogContract.ProductsTable.COLUMN_IMAGE_NUMBERS,
            CatalogContract.ProductsTable.COLUMN_IMAGE_PATH
    };

    private int mOffset, mLimit;
    private ArrayList<Integer> mResponseCodes;

    public GridProductsLoader(Context context, int pageNumber, int limit, @Nullable ArrayList<Integer> responseCodes) {
        super(context);
        mLimit = limit;
        mResponseCodes = responseCodes;
        if (pageNumber > 0) {
            mOffset = (pageNumber - 1) * mLimit;
        } else {
            mOffset = 0;
        }
    }

    @Override
    public ArrayList<GridProductModel> loadInBackground() {
        CatalogDBHelper catalogDBHelper = new CatalogDBHelper(getContext());

        HashSet<Integer> categoryID = new HashSet<>();
        categoryID.add(FilterClass.getCategoryID());

        Cursor cursor = catalogDBHelper.getProductData(null,
                null,
                null,
                null,
                null,
                null,
                FilterClass.getSelectedItems("Brand"),
                categoryID,
                FilterClass.getMinPriceFilter(),
                FilterClass.getMaxPriceFilter(),
                FilterClass.getSelectedItems("Fabric"),
                FilterClass.getSelectedItems("Colors"),
                FilterClass.getSelectedItems("Sizes"),
                mResponseCodes, //TODO : Put appropriate response codes
                0,
                1,
                1,
                1,
                null, // ORDER BY
                mLimit,
                mOffset,
                columns);
        return GridProductModel.getGridProducts(cursor);
    }
}
