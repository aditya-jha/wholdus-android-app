package com.wholdus.www.wholdusbuyerapp.helperClasses;

import android.text.TextUtils;
import android.util.Log;

import com.wholdus.www.wholdusbuyerapp.R;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by aditya on 12/12/16.
 */

public final class FilterClass {
    private FilterClass() {
    }

    private static int mCategoryID;
    private static int mMinPrice = 0;
    private static int mMaxPrice = 5000;
    private static int mSelectedSort = -1;
    private static HashSet<String> mFabrics = new HashSet<>();
    private static HashSet<String> mColors = new HashSet<>();
    private static HashSet<String> mSizes = new HashSet<>();
    private static HashSet<String> mBrands = new HashSet<>();

    public static int getCategoryID() {
        return mCategoryID;
    }

    public static void setCategoryID(int id) {
        mCategoryID = id;
    }

    public static void resetFilter() {
        Log.d("FILTER CLASS", "resetting filters");
        mFabrics.clear();
        mColors.clear();
        mSizes.clear();
        mBrands.clear();
    }

    public static void toggleFilterItem(String type, String value) {
        HashSet<String> object = getSelectedItems(type);
        if (object.contains(value)) {
            object.remove(value);
        } else {
            object.add(value);
        }
    }

    public static HashSet<String> getSelectedItems(String type) {
        HashSet<String> returnValue;
        switch (type) {
            case "Fabric":
                returnValue = mFabrics;
                break;
            case "Colors":
                returnValue = mColors;
                break;
            case "Sizes":
                returnValue = mSizes;
                break;
            case "Brands":
                returnValue = mBrands;
                break;
            default:
                returnValue = mSizes;
        }
        return returnValue;
    }

    public static boolean isItemSelected(String type, String value) {
        return getSelectedItems(type).contains(value);
    }

    public static void setPriceFilter(int min, int max) {
        mMaxPrice = max;
        mMinPrice = min;
    }

    public static int getMinPriceFilter() {
        return mMinPrice;
    }

    public static int getMaxPriceFilter() {
        return mMaxPrice;
    }

    public static int getSelectSort() {
        return mSelectedSort;
    }

    public static void setSelectedSort(int sort) {
        mSelectedSort = sort;
    }

    public static String getFilterString() {
        HashMap<String, String> params = new HashMap<>();
        params.put("categoryID", String.valueOf(mCategoryID));
        params.put("sellerID", TextUtils.join(",", mBrands));
        params.put("fabric", TextUtils.join(",", mFabrics));
        params.put("colour", TextUtils.join(",", mColors));
        if (mMaxPrice == 5000) {
            params.put("max_price_per_unit", "-1");
        } else {
            params.put("max_price_per_unit", String.valueOf(mMaxPrice));
        }
        params.put("min_price_per_unit", String.valueOf(mMinPrice));

        return GlobalAccessHelper.getUrlStringFromHashMap(params);
    }
}
