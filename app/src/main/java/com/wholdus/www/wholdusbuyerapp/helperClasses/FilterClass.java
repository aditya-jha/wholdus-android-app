package com.wholdus.www.wholdusbuyerapp.helperClasses;

import android.util.Log;

/**
 * Created by aditya on 12/12/16.
 */

public final class FilterClass {
    private FilterClass() {
    }

    private static int mCategoryID;

    public static int getCategoryID() {
        return mCategoryID;
    }

    public static void setCategoryID(int id) {
        mCategoryID = id;
    }

    public static void resetFilter() {
        Log.d("FILTER CLASS", "resetting filters");
    }
}
