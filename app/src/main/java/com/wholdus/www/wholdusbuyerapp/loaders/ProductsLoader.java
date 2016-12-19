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

    public ProductsLoader(Context context) {
        super(context);

    }
    @Override
    public ArrayList<Product> loadInBackground() {
        CatalogDBHelper catalogDBHelper = new CatalogDBHelper(getContext());

        Cursor cursor = catalogDBHelper.getProductData(-1,
                null,
                null,
                -1,
                -1,
                null,
                null,
                null,
                0,
                1,
                null, // ORDER BY
                10,
                0,
                CatalogDBHelper.BasicProductColumns);

        return Product.getProductsFromCursor(cursor);
    }
}
