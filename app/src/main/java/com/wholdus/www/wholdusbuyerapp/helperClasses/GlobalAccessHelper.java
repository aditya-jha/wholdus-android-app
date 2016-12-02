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

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;

        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);

            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth,
                        ViewGroup.LayoutParams.MATCH_PARENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + ((listView.getDividerHeight()) * (listAdapter.getCount()));

        listView.setLayoutParams(params);
        listView.requestLayout();
    }
}
