package com.wholdus.www.wholdusbuyerapp.loaders;

import android.content.Context;
import android.database.Cursor;

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
            CatalogContract.ProductsTable.COLUMN_IMAGE_PATH,
            CatalogContract.ProductsTable.COLUMN_RESPONSE_CODE,
    };

    private int mOffset, mLimit;
    private ArrayList<Integer> mResponseCodes;

    public GridProductsLoader(Context context, int pageNumber, int limit, ArrayList<Integer> responseCodes) {
        super(context);
        mLimit = limit;
        mOffset = pageNumber > 0 ? (pageNumber - 1) * mLimit : 0;
        mResponseCodes = responseCodes;
    }

    @Override
    public ArrayList<GridProductModel> loadInBackground() {

        CatalogDBHelper catalogDBHelper = new CatalogDBHelper(getContext());

        ArrayList<Integer> categoryID = new ArrayList<>();
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
                mResponseCodes,
                0,
                1,
                1,
                1,
                new String[] {FilterClass.getSortString()}, // ORDER BY
                mLimit,
                mOffset,
                columns);
        return GridProductModel.getGridProducts(cursor);
    }
}
