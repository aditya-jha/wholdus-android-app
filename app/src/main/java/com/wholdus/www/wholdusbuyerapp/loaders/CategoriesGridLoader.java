package com.wholdus.www.wholdusbuyerapp.loaders;

import android.content.Context;
import android.database.Cursor;

import com.wholdus.www.wholdusbuyerapp.databaseHelpers.CatalogDBHelper;
import com.wholdus.www.wholdusbuyerapp.databaseHelpers.UserDBHelper;
import com.wholdus.www.wholdusbuyerapp.models.BuyerInterest;
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
        ArrayList<BuyerInterest> buyerInterests = BuyerInterest.getBuyerIntersetList(userDBHelper.getUserInterests(-1, -1, null));

        for (Category category: categories) {
            for (int i=0; i<buyerInterests.size(); i++) {
                if (buyerInterests.get(i).getCategoryID() == category.getCategoryID()) {
                    category.setBuyerInterest(buyerInterests.get(i));
                }
            }
        }

        return categories;
    }

    public void deleteData() {
        mResult = null;
    }
}
