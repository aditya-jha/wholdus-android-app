package com.wholdus.www.wholdusbuyerapp.loaders;

import android.content.Context;

import com.wholdus.www.wholdusbuyerapp.databaseHelpers.CatalogDBHelper;
import com.wholdus.www.wholdusbuyerapp.models.Category;

import java.util.ArrayList;

/**
 * Created by aditya on 10/12/16.
 */

public class CategoriesGridLoader extends AbstractLoader<ArrayList<Category>> {

    public CategoriesGridLoader(Context context) {
        super(context);
    }

    @Override
    public ArrayList<Category> loadInBackground() {
        CatalogDBHelper dbHelper = new CatalogDBHelper(getContext());
        return Category.getCategoryArrayList(dbHelper.getAllCategories());
    }
}
