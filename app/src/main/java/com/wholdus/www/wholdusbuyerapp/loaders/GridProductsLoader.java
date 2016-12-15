package com.wholdus.www.wholdusbuyerapp.loaders;

import android.content.Context;
import android.database.Cursor;

import com.wholdus.www.wholdusbuyerapp.databaseHelpers.CatalogDBHelper;
import com.wholdus.www.wholdusbuyerapp.helperClasses.FilterClass;
import com.wholdus.www.wholdusbuyerapp.models.GridProductModel;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by aditya on 15/12/16.
 */

public class GridProductsLoader extends AbstractLoader<ArrayList<GridProductModel>> {

    private static final String[] columns = {};

    public GridProductsLoader(Context context, String filerParams) {
        super(context);
    }

    @Override
    public ArrayList<GridProductModel> loadInBackground() {
        CatalogDBHelper catalogDBHelper = new CatalogDBHelper(getContext());
        Cursor cursor = catalogDBHelper.getProductData(-1,
                FilterClass.getSelectedItems("Brand"),
                new HashSet<Integer>(FilterClass.getCategoryID()),
                FilterClass.getMinPriceFilter(),
                FilterClass.getMaxPriceFilter(),
                FilterClass.getSelectedItems("Fabric"),
                FilterClass.getSelectedItems("Colors"),
                FilterClass.getSelectedItems("Sizes"),
                0,
                1,
                null,
                -1,
                -1,
                columns);
        return GridProductModel.getGridProducts(cursor);
    }
}
