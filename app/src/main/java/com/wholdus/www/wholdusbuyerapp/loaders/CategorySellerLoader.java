package com.wholdus.www.wholdusbuyerapp.loaders;

import android.content.Context;
import android.text.TextUtils;

import com.wholdus.www.wholdusbuyerapp.databaseHelpers.CatalogDBHelper;
import com.wholdus.www.wholdusbuyerapp.helperClasses.FilterClass;
import com.wholdus.www.wholdusbuyerapp.models.CategorySeller;

import java.util.ArrayList;
import java.util.HashSet;

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
        int sellerID = -1;
        HashSet<String> sellers = FilterClass.getSelectedItems(FilterClass.FILTER_BRAND_KEY);
        if (sellers != null && sellers.size() == 1){
            try {
                sellerID = Integer.parseInt(TextUtils.join(",", sellers));
            } catch (Exception e){
            }

        }
        return CategorySeller.getCategorySellers(catalogDBHelper.getCategorySellers(FilterClass.getCategoryID(), sellerID));
    }

}
