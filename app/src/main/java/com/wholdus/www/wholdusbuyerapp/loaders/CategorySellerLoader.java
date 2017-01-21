package com.wholdus.www.wholdusbuyerapp.loaders;

import android.content.Context;

import com.wholdus.www.wholdusbuyerapp.databaseHelpers.CatalogDBHelper;
import com.wholdus.www.wholdusbuyerapp.helperClasses.FilterClass;
import com.wholdus.www.wholdusbuyerapp.models.CategorySeller;

import java.util.ArrayList;

/**
 * Created by aditya on 14/12/16.
 */

public class CategorySellerLoader extends AbstractLoader<ArrayList<CategorySeller>> {

    public CategorySellerLoader(Context context) {
        super(context);
    }

    @Override
    public ArrayList<CategorySeller> loadInBackground() {
        CatalogDBHelper catalogDBHelper = new CatalogDBHelper(getContext());
        return CategorySeller.getCategorySellers(catalogDBHelper.getCategorySellers(FilterClass.getCategoryID()));
    }

}
