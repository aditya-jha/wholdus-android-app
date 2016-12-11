package com.wholdus.www.wholdusbuyerapp.helperClasses;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.WholdusApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;

/**
 * Created by aditya on 26/11/16.
 */

public class GlobalAccessHelper {

    public static String getAccessToken(Context context) {
        return "access_token=" + ((WholdusApplication)context).getAccessToken();
    }

    public static String getRefreshToken(Application context) {
        return "refresh_token=" + ((WholdusApplication)context).getRefreshToken();
    }

    public static String generateUrl(Context context, String endPoint, @Nullable HashMap<String,Object> params) {
        String url = context.getString(R.string.api_base) + endPoint;
        if (params!= null){
            url += "?";
            for (HashMap.Entry<String, Object> entry : params.entrySet()) {
                url += "&" + entry.getKey() + "=" + String.valueOf(entry.getValue());
            }
        }
        return url;
    }

    public static int getBuyerID(Context context) {
        return ((WholdusApplication)context).getBuyerID();
    }
}
