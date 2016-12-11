package com.wholdus.www.wholdusbuyerapp.loaders;

import android.content.Context;
import android.database.Cursor;

import com.wholdus.www.wholdusbuyerapp.databaseHelpers.CatalogDBHelper;
import com.wholdus.www.wholdusbuyerapp.databaseHelpers.UserDBHelper;
import com.wholdus.www.wholdusbuyerapp.models.Category;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by aditya on 10/12/16.
 */

public class CategoriesGridLoader extends AbstractLoader<ArrayList<Category>> {

    public CategoriesGridLoader(Context context) {
        super(context);
    }

    @Override
    public ArrayList<Category> loadInBackground() {
        CatalogDBHelper catalogDBHelper = new CatalogDBHelper(getContext());
        ArrayList<Category> categories = Category.getCategoryArrayList(catalogDBHelper.getAllCategories(false));

        UserDBHelper userDBHelper = new UserDBHelper(getContext());
        HashSet userInterestIDs = userDBHelper.getAllUserInterestID();

        for (Category category: categories) {
            if (userInterestIDs.contains(category.getCategoryID())) {
                category.setLikeStatus(true);
            }
        }

        return categories;
    }
}
