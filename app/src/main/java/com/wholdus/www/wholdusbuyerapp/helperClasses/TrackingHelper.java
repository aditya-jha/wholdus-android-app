package com.wholdus.www.wholdusbuyerapp.helperClasses;

import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

/**
 * Created by aditya on 2/2/17.
 */

public class TrackingHelper {

    private static TrackingHelper mInstance;
    private FirebaseAnalytics mFirebaseAnalytics;

    private TrackingHelper(Context context) {
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
    }

    public static synchronized TrackingHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new TrackingHelper(context);
        }
        return mInstance;
    }

    public void logEvent(String event, String ID, String name) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, ID);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, name);
        mFirebaseAnalytics.logEvent(event, bundle);
    }
}
