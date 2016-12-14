package com.wholdus.www.wholdusbuyerapp.helperClasses;

import android.util.Log;

import java.util.HashSet;

/**
 * Created by aditya on 12/12/16.
 */

public final class FilterClass {
    private FilterClass() {
    }

    private static int mCategoryID;
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
}
