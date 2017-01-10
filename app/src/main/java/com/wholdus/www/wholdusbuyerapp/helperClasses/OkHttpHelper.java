package com.wholdus.www.wholdusbuyerapp.helperClasses;

import android.content.Context;
import android.support.annotation.Nullable;

import com.facebook.stetho.okhttp3.StethoInterceptor;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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

    public static Response makeGetRequest(Context context, String url) throws IOException {
        OkHttpClient okHttpClient = OkHttpHelper.getClient(context);

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        return okHttpClient.newCall(request).execute();
    }

    public static Response makePostRequest(Context context, String url, String data) throws IOException {
        RequestBody requestBody = RequestBody.create(JSON, data);
        OkHttpClient okHttpClient = OkHttpHelper.getClient(context);

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        return okHttpClient.newCall(request).execute();
    }
}
