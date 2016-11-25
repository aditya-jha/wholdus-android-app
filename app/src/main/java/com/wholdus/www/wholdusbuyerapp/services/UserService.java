package com.wholdus.www.wholdusbuyerapp.services;

import android.content.Context;

import com.android.volley.Request;
import com.wholdus.www.wholdusbuyerapp.R;

/**
 * Created by aditya on 20/11/16.
 */

public class UserService extends BaseAPIService {
    private String REQUEST_TAG;

    private Context mContext;

    public UserService(Context context) {
        super(context, context.getString(R.string.user_api_request_tag));
        mContext = context;
    }

    public void getUserDetails() {
        String endPoint = super.generateUrl(mContext.getString(R.string.buyer_details_url));
        super.volleyStringRequest(Request.Method.GET, endPoint, null);
    }
}
