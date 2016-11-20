package com.wholdus.www.wholdusbuyerapp.services;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.WholdusApplication;
import com.wholdus.www.wholdusbuyerapp.singletons.TokenSingleton;
import com.wholdus.www.wholdusbuyerapp.singletons.VolleySingleton;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by aditya on 12/11/16.
 */

public class BaseAPIService {

    private Context mContext;

    public BaseAPIService() {
    }

    public void setActivityCompat(Context context) {
        mContext = context;
    }

    public String generateUrl(String endPoint) {
        return mContext.getString(R.string.api_base) + endPoint;
    }

    public void volleyStringRequest(int method, String endPoint, final String jsonData, final String REQUEST_TAG) {

        StringRequest stringRequest = new StringRequest(method, endPoint,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Intent intent = new Intent(mContext.getString(R.string.api_response));
                        intent.putExtra(mContext.getString(R.string.api_response_data_key), response);
                        mContext.sendBroadcast(intent);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                if (networkResponse != null && networkResponse.data != null) {
                    switch (networkResponse.statusCode) {
                        case 401:

                    }
                }
                Toast.makeText(mContext, error.toString(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Accept", "version=1");
                params.put("Authorization", getAccessToken());
                return params;
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return jsonData == null ? null : jsonData.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    uee.printStackTrace();
                }
                return null;
            }
        };

        VolleySingleton.getInstance(mContext).addToRequestQueue(stringRequest, REQUEST_TAG);
    }

    private String getAccessToken() {
        WholdusApplication wholdusApplication = (WholdusApplication)((Activity) mContext).getApplication();
        String accessToken = wholdusApplication.getAccessToken();
        return "access_token=" + accessToken;
    }

    private String getRefreshToken() {
        WholdusApplication wholdusApplication = (WholdusApplication)((Activity) mContext).getApplication();
        String accessToken = wholdusApplication.getRefreshToken();
        return "refresh_token=" + accessToken;
    }
}
