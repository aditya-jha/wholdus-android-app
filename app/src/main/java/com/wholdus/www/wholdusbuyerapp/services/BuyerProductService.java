package com.wholdus.www.wholdusbuyerapp.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.databaseHelpers.BuyerProductsDBHelper;
import com.wholdus.www.wholdusbuyerapp.helperClasses.GlobalAccessHelper;
import com.wholdus.www.wholdusbuyerapp.singletons.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by aditya on 9/12/16.
 */

public class BuyerProductService extends IntentService {

    public static final String REQUEST_TAG = "BUYER_PRODUCTS_API_REQUESTS";

    public BuyerProductService() {
        super("BuyerProductService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        int todo = intent.getIntExtra("TODO", 0);
        switch (todo) {
            case R.string.fetch_buyer_products:
                fetchBuyerProducts(todo);
        }
    }

    private void fetchBuyerProducts(int todo) {
        String endPoint = GlobalAccessHelper.generateUrl(getApplicationContext(), getString(R.string.fetch_buyer_products), null);
        volleyStringRequest(todo, Request.Method.GET, endPoint, null);
    }

    private void volleyStringRequest(final int todo, int method, String endPoint, final String jsonData) {

        StringRequest stringRequest = new StringRequest(method, endPoint,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        onResponseHandler(todo, response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Accept", "version=1");
                params.put("Authorization", GlobalAccessHelper.getAccessToken(getApplication()));
                return params;
            }

            @Nullable
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

        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest, REQUEST_TAG);
    }

    private void onResponseHandler(int todo, String response) {
        switch (todo) {
            case R.string.fetch_buyer_products:
                Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
                saveResponseToDB(response);

        }
    }

    private void saveResponseToDB(String response) {
        BuyerProductsDBHelper dbHelper = new BuyerProductsDBHelper(this);

        try {
            int insertedUpdated = dbHelper.updateBuyerProductsData(new JSONObject(response));
        } catch (JSONException e) {

        }
    }
}
