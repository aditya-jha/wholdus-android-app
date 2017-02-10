package com.wholdus.www.wholdusbuyerapp.loaders;

import android.content.Context;
import android.support.annotation.Nullable;

import com.wholdus.www.wholdusbuyerapp.databaseHelpers.CatalogDBHelper;
import com.wholdus.www.wholdusbuyerapp.models.Category;
import com.wholdus.www.wholdusbuyerapp.models.Product;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by aditya on 10/12/16.
 */

public class CategoriesGridLoader extends AbstractLoader<ArrayList<Category>> {

    private boolean mInitialiseProducts;
    private ArrayList<Integer> mResponseCodes;

    public CategoriesGridLoader(Context context, boolean initialiseProducts, @Nullable ArrayList<Integer> responseCodes) {
        super(context);
        mInitialiseProducts = initialiseProducts;
        if (responseCodes != null){
            mResponseCodes = responseCodes;
        } else {
            mResponseCodes = new ArrayList<>(Arrays.asList(0, 1));
        }
    }

    @Override
    public ArrayList<Category> loadInBackground() {
        CatalogDBHelper catalogDBHelper = new CatalogDBHelper(getContext());
        ArrayList<Category> categories = Category.getCategoryArrayList(catalogDBHelper.getCategoryData(-1, -1, null, 1, -1, -1, null));

        if (mInitialiseProducts) {
            for (Category category : categories) {
                category.setProducts(Product.getProductsFromCursor(catalogDBHelper.getProductData(null, null, null, null, null, null, null, new ArrayList<>(Arrays.asList(category.getCategoryID())), -1, -1, null, null, null, mResponseCodes, 0, 1, -1, -1, null, 10, 0, null)));
            }
        }
        return categories;
    }

    public void deleteData() {
        mResult = null;
    }
}
