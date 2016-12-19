package com.wholdus.www.wholdusbuyerapp.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.databaseContracts.CatalogContract.CategoriesTable;
import com.wholdus.www.wholdusbuyerapp.databaseContracts.CatalogContract.ProductsTable;
import com.wholdus.www.wholdusbuyerapp.databaseHelpers.CatalogDBHelper;
import com.wholdus.www.wholdusbuyerapp.helperClasses.APIConstants;
import com.wholdus.www.wholdusbuyerapp.helperClasses.Constants;
import com.wholdus.www.wholdusbuyerapp.helperClasses.FilterClass;
import com.wholdus.www.wholdusbuyerapp.helperClasses.GlobalAccessHelper;
import com.wholdus.www.wholdusbuyerapp.singletons.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by aditya on 10/12/16.
 * For API Calls related to catalog data
 */

public class CatalogService extends IntentService {

    private static final String REQUEST_TAG = "CATALOG_REQUEST";

    public CatalogService() {
        super("CatalogService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        int todo = intent.getIntExtra("TODO", -1);
        switch (todo) {
            case R.integer.fetch_categories:
                fetchCategories(todo, intent.getBooleanExtra(getString(R.string.seller_category_details), false));
                break;
            case R.integer.fetch_products:
                int pageNumber = intent.getIntExtra("page_number", 0);
                int itemsPerPage = intent.getIntExtra("items_per_page", 20);
                fetchProducts(todo, pageNumber, itemsPerPage);
        }
    }


    private void fetchProducts(int todo, int pageNumber, int itemsPerPage) {
        HashMap<String, String> params = FilterClass.getFilterHashMap();
        params.put("items_per_page", String.valueOf(itemsPerPage));
        params.put("page_number", String.valueOf(pageNumber));
        params.put("product_image_details", "1");
        params.put("product_details_details", "1");
        params.put("product_show_online", "1");
        params.put("seller_details", "1");
        String endPoint = GlobalAccessHelper.generateUrl(getString(R.string.product_url), params);
        volleyStringRequest(todo, Request.Method.GET, endPoint, null);
    }

    private void fetchCategories(int todo, boolean categoryDetails) {
        HashMap<String, String> params = new HashMap<>();;
        if (categoryDetails) {
            params.put("seller_category_details", "1");
        }
        params.put("category_show_online", "1");
        String endPoint = GlobalAccessHelper.generateUrl(getString(R.string.category_url), params);
        volleyStringRequest(todo, Request.Method.GET, endPoint, null);
    }

    public void volleyStringRequest(final int todo, int method, String endPoint, final String jsonData) {

        StringRequest stringRequest = new StringRequest(method, endPoint,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject data = new JSONObject(response);
                            onResponseHandler(todo, data);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
            }
        }
        ) {
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

    private void onResponseHandler(int todo, JSONObject response) {
        CatalogDBHelper catalogDBHelper = new CatalogDBHelper(getApplicationContext());
        int updatedInserted = 0;

        switch (todo) {
            case R.integer.fetch_categories:
                // save to db
                updatedInserted = catalogDBHelper.updateCategories(response);

                if (updatedInserted > 0) {
                    sendUpdatedBroadCast(getString(R.string.category_data_updated), CategoriesTable.TABLE_NAME);
                }
                break;
            case R.integer.fetch_products:
                // save to db
                try {
                    updatedInserted = catalogDBHelper.saveProductsFromJSONArray(response.getJSONArray(ProductsTable.TABLE_NAME));

                    Intent intent = new Intent(getString(R.string.category_data_updated));
                    intent.putExtra("type", Constants.PRODUCT_RESPONSE);
                    intent.putExtra(Constants.INSERTED_UPDATED, updatedInserted);
                    intent.putExtra(APIConstants.API_PAGE_NUMBER_KEY, response.getInt(APIConstants.API_PAGE_NUMBER_KEY));
                    intent.putExtra(APIConstants.API_TOTAL_PAGES_KEY, response.getInt(APIConstants.API_TOTAL_PAGES_KEY));

                    LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private void sendUpdatedBroadCast(String intentFilter, @Nullable String extra) {
        Intent intent = new Intent(intentFilter);
        if (extra != null) {
            intent.putExtra("extra", extra);
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
