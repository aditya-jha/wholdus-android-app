package com.wholdus.www.wholdusbuyerapp.helperClasses;

import android.app.Application;
import android.content.Context;
import android.support.annotation.Nullable;

import com.wholdus.www.wholdusbuyerapp.WholdusApplication;

import java.util.HashMap;

/**
 * Created by aditya on 26/11/16.
 */

public final class GlobalAccessHelper {

    public static String getAccessToken(Context context) {
        return "access_token=" + ((WholdusApplication) context).getAccessToken();
    }

    public static String getRefreshToken(Application context) {
        return "refresh_token=" + ((WholdusApplication) context).getRefreshToken();
    }

    public static String generateUrl(String endPoint, @Nullable HashMap<String, String> params) {
        return APIConstants.API_BASE + endPoint + getUrlStringFromHashMap(params);
    }

    public static int getBuyerID(Context context) {
        return ((WholdusApplication) context).getBuyerID();
    }

    public static String getMobileNumber(Context context) {
        return ((WholdusApplication) context).getMobileNumber();
    }

    public static String getUrlStringFromHashMap(@Nullable HashMap<String, String> params) {
        String url = "?";
        if (params != null) {
            for (HashMap.Entry<String, String> entry : params.entrySet()) {
                url += "&" + entry.getKey() + "=" + entry.getValue();
            }
        }
        return url;
    }
}
