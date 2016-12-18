package com.wholdus.www.wholdusbuyerapp.loaders;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.res.TypedArrayUtils;
import android.util.Log;

import com.wholdus.www.wholdusbuyerapp.databaseHelpers.CatalogDBHelper;
import com.wholdus.www.wholdusbuyerapp.models.Product;

import java.util.ArrayList;

/**
 * Created by aditya on 18/12/16.
 */

public class ProductLoader extends AbstractLoader<Product> {

    private Context mContext;
    private int mProductID;

    public ProductLoader(Context context, int productID) {
        super(context);
        mContext = context;
        mProductID = productID;
    }

    @Override
    public Product loadInBackground() {
        CatalogDBHelper catalogDBHelper = new CatalogDBHelper(mContext);
        Cursor cursor = catalogDBHelper.getProductData(
                mProductID,
                null,
                null,
                -1,
                -1,
                null,
                null,
                null,
                0,
                1,
                null,
                -1,
                -1,
                null
                );

        if (cursor.getCount() == 0) {
            return null;
        }

        cursor.moveToFirst();

        Product product = new Product(cursor);
        product.setProductDetails(cursor);

        return product;
    }

    private String[] getColumnsToFetch() {
        String[] columns = new String[CatalogDBHelper.BasicProductColumns.length + CatalogDBHelper.ExtraProductColumns.length];
        System.arraycopy(CatalogDBHelper.BasicProductColumns, 0, columns, 0, CatalogDBHelper.BasicProductColumns.length);
        System.arraycopy(CatalogDBHelper.ExtraProductColumns, 0, columns, CatalogDBHelper.BasicProductColumns.length, CatalogDBHelper.ExtraProductColumns.length);

        return columns;
    }
}
