package com.wholdus.www.wholdusbuyerapp.loaders;

import android.content.Context;
import android.database.Cursor;

import com.wholdus.www.wholdusbuyerapp.databaseHelpers.CatalogDBHelper;
import com.wholdus.www.wholdusbuyerapp.helperClasses.FilterClass;
import com.wholdus.www.wholdusbuyerapp.models.GridProductModel;
import com.wholdus.www.wholdusbuyerapp.models.Product;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by kaustubh on 17/12/16.
 */

public class ProductsLoader extends AbstractLoader<ArrayList<Product>> {

    private ArrayList<Integer> mProductIDs;
    private ArrayList<Integer> mExcludeProductIDs;
    private ArrayList<Integer> mResponseCodes;
    private String[] mOrderBy;
    private int mLimit;

    public ProductsLoader(Context context,
                          ArrayList<Integer> productIDs,
                          ArrayList<Integer> excludeProductIDs,
                          ArrayList<Integer> responseCodes,
                          String[] orderBy,
                          int limit) {
        super(context);
        mProductIDs = productIDs;
        mExcludeProductIDs = excludeProductIDs;
        mResponseCodes = responseCodes;
        mOrderBy = orderBy;
        mLimit = limit;
    }
    @Override
    public ArrayList<Product> loadInBackground() {
        CatalogDBHelper catalogDBHelper = new CatalogDBHelper(getContext());

        Cursor cursor = catalogDBHelper.getProductData(mProductIDs,
                mExcludeProductIDs,
                null,
                null,
                null,
                null,
                FilterClass.getSelectedItems("Brands"),
                FilterClass.getCategoryIDs(),
                FilterClass.getMinPriceFilter(),
                FilterClass.getMaxPriceFilter(),
                FilterClass.getSelectedItems("Fabric"),
                FilterClass.getSelectedItems("Colors"),
                FilterClass.getSelectedItems("Sizes"),
                mResponseCodes,
                0,
                1,
                -1,
                -1,
                mOrderBy, // ORDER BY
                mLimit,
                0,
                CatalogDBHelper.BasicProductColumns);

        return Product.getProductsFromCursor(cursor);
    }
}
