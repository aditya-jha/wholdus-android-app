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

    private ArrayList<Integer> mResponseCodes;

    public ProductsLoader(Context context, ArrayList<Integer> responseCodes) {
        super(context);
        mResponseCodes = responseCodes;
    }
    @Override
    public ArrayList<Product> loadInBackground() {
        CatalogDBHelper catalogDBHelper = new CatalogDBHelper(getContext());

        Cursor cursor = catalogDBHelper.getProductData(null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                -1,
                -1,
                null,
                null,
                null,
                mResponseCodes,
                0,
                1,
                -1,
                -1,
                null, // ORDER BY
                10,
                0,
                CatalogDBHelper.BasicProductColumns);

        return Product.getProductsFromCursor(cursor);
    }
}
