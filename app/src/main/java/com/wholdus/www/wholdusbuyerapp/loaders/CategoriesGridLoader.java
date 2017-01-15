package com.wholdus.www.wholdusbuyerapp.loaders;

import android.content.Context;

import com.wholdus.www.wholdusbuyerapp.databaseHelpers.CatalogDBHelper;
import com.wholdus.www.wholdusbuyerapp.databaseHelpers.UserDBHelper;
import com.wholdus.www.wholdusbuyerapp.models.Category;
import com.wholdus.www.wholdusbuyerapp.models.Product;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by aditya on 10/12/16.
 */

public class CategoriesGridLoader extends AbstractLoader<ArrayList<Category>> {

    private boolean mInitialiseProducts;

    public CategoriesGridLoader(Context context, boolean initialiseProducts) {
        super(context);
        mInitialiseProducts = initialiseProducts;
    }

    @Override
    public ArrayList<Category> loadInBackground() {
        CatalogDBHelper catalogDBHelper = new CatalogDBHelper(getContext());
        ArrayList<Category> categories = Category.getCategoryArrayList(catalogDBHelper.getCategoryData(-1,-1,null,1,-1,-1,null));

        if (mInitialiseProducts){
            for (Category category:categories){
                category.setProducts(Product.getProductsFromCursor(catalogDBHelper.getProductData(null,null,null,null,null,null,null,new ArrayList<>(Arrays.asList(category.getCategoryID())),-1,-1,null,null,null,new ArrayList<>(Arrays.asList(0,1)),0,1,-1,-1,null,10,0,null)));
            }
        }
        return categories;
    }

    public void deleteData() {
        mResult = null;
    }
}
