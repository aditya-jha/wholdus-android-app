package com.wholdus.www.wholdusbuyerapp.helperClasses;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.WholdusApplication;

import org.json.JSONObject;

/**
 * Created by aditya on 26/11/16.
 */

public class GlobalAccessHelper {

    public static String getAccessToken(Application context) {
        WholdusApplication wholdusApplication = (WholdusApplication)context;
        String accessToken = wholdusApplication.getAccessToken();
        return "access_token=" + accessToken;
    }

    public static String getRefreshToken(Application context) {
        WholdusApplication wholdusApplication = (WholdusApplication)context;
        String refreshToken = wholdusApplication.getRefreshToken();
        return "refresh_token=" + refreshToken;
    }

    public static String generateUrl(Context context, String endPoint, @Nullable JSONObject params) {
        return context.getString(R.string.api_base) + endPoint;
    }

    public static String getBuyerID(Application context) {
        return ((WholdusApplication)context).getBuyerID();
    }
}
