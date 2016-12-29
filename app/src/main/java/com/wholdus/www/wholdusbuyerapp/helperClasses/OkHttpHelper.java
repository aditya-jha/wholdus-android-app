package com.wholdus.www.wholdusbuyerapp.helperClasses;

import android.content.Context;
import android.support.annotation.Nullable;

import com.facebook.stetho.okhttp3.StethoInterceptor;

import java.util.HashMap;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by aditya on 28/12/16.
 */

public class OkHttpHelper {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private static OkHttpClient mOkHttpClient;

    public static synchronized OkHttpClient getClient(Context context) {
        if (mOkHttpClient == null) {
            mOkHttpClient = new OkHttpClient.Builder()
                    .addNetworkInterceptor(new StethoInterceptor())
                    .addInterceptor(new RequestTokenInterceptor(context))
                    .build();
        }
        return mOkHttpClient;
    }

    public static String generateUrl(String endpoint) {
        return APIConstants.TEMP_API_BASE + endpoint;
    }
}
