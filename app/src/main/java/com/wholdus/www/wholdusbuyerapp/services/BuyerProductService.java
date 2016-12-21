package com.wholdus.www.wholdusbuyerapp.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.databaseHelpers.CatalogDBHelper;
import com.wholdus.www.wholdusbuyerapp.helperClasses.GlobalAccessHelper;
import com.wholdus.www.wholdusbuyerapp.singletons.VolleySingleton;

import org.json.JSONArray;
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
                fetchBuyerProducts(todo, 1);
        }
    }

    private void fetchBuyerProducts(int todo, int pageNumber) {
        HashMap<String,String> params = new HashMap<>();
        //TODO : Save categories and seller data separately so that it doesn't have to requested here
        //TODO : Also try that all products don't have to be requested every time
        params.put("product_details", "1");
        params.put("product_details_details","1");
        params.put("product_image_details", "1");
        params.put("category_details", "1");
        params.put("seller_details", "1");
        params.put("items_per_page", "20");
        params.put("page_number", String.valueOf(pageNumber));
        String url = GlobalAccessHelper.generateUrl(getString(R.string.buyer_product_url), params);
        volleyStringRequest(todo, Request.Method.GET, url, null);
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
        try {
            switch (todo) {
                case R.string.fetch_buyer_products:
                    saveBuyerProductsToDB(response);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void saveBuyerProductsToDB(String response) throws JSONException {
        JSONObject data = new JSONObject(response);
        // TODO Handle pagination
        CatalogDBHelper dbHelper = new CatalogDBHelper(this);
        JSONArray buyerProducts = data.getJSONArray("buyer_products");
        dbHelper.saveBuyerProductsDataFromJSONArray(buyerProducts);
        sendBuyerProductDataUpdatedBroadCast(null);
    }

    private void sendBuyerProductDataUpdatedBroadCast(@Nullable String extra) {
        Intent intent = new Intent(getString(R.string.buyer_product_data_updated));
        if (extra != null) {
            intent.putExtra("extra", extra);
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
