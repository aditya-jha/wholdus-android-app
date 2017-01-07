package com.wholdus.www.wholdusbuyerapp.services;

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
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
import com.wholdus.www.wholdusbuyerapp.databaseContracts.CatalogContract.CategoriesTable;
import com.wholdus.www.wholdusbuyerapp.databaseContracts.CatalogContract.ProductsTable;
import com.wholdus.www.wholdusbuyerapp.databaseHelpers.CatalogDBHelper;
import com.wholdus.www.wholdusbuyerapp.helperClasses.APIConstants;
import com.wholdus.www.wholdusbuyerapp.helperClasses.Constants;
import com.wholdus.www.wholdusbuyerapp.helperClasses.FilterClass;
import com.wholdus.www.wholdusbuyerapp.helperClasses.GlobalAccessHelper;
import com.wholdus.www.wholdusbuyerapp.helperClasses.HelperFunctions;
import com.wholdus.www.wholdusbuyerapp.helperClasses.IntentFilters;
import com.wholdus.www.wholdusbuyerapp.helperClasses.TODO;
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
                break;
            case TODO.UPDATE_BUYER_INTEREST:
                updateBuyerInterest(todo, intent);
                break;
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
        Log.d(this.getClass().getSimpleName(), "making call for page: " + pageNumber);
        volleyStringRequest(todo, Request.Method.GET, endPoint, null);
    }

    private void fetchCategories(int todo, boolean categoryDetails) {
        HashMap<String, String> params = new HashMap<>();;
        if (categoryDetails) {
            params.put("seller_category_details", "1");
        }
        params.put("category_show_online", "1");
        String endPoint = GlobalAccessHelper.generateUrl(APIConstants.CATEGORY_URL, params);
        volleyStringRequest(todo, Request.Method.GET, endPoint, null);
    }

    public void volleyStringRequest(final int todo, int method, String endPoint, final String jsonData) {

        StringRequest stringRequest = new StringRequest(method, endPoint,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject data = new JSONObject(response);
                            switch (todo) {
                                case R.integer.fetch_categories:
                                    updateCategories(data);
                                    break;
                                case R.integer.fetch_products:
                                    updateProducts(data);
                                    break;
                                case TODO.UPDATE_BUYER_INTEREST:
                                    updateBuyerInterestFromJSON(data);
                                    break;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                try {
                    switch (todo) {
                        case R.integer.fetch_categories:
                            updateCategories(null);
                            break;
                        case R.integer.fetch_products:
                            updateProducts(null);
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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

    private void updateCategories(JSONObject response) {
        Intent intent = new Intent(IntentFilters.CATEGORY_DATA);
        if (response == null) {
            // error response
            intent.putExtra(Constants.ERROR_RESPONSE, "Error");
        } else {
            // save to db
            CatalogDBHelper catalogDBHelper = new CatalogDBHelper(this);
            int updatedInserted = catalogDBHelper.updateCategories(response);
            intent.putExtra(Constants.INSERTED_UPDATED, updatedInserted);
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void updateProducts(JSONObject response) throws JSONException {
        Intent intent = new Intent(IntentFilters.PRODUCT_DATA);
        if (response == null) {
            // error response
            intent.putExtra(Constants.ERROR_RESPONSE, "Error");
        } else {
            CatalogDBHelper catalogDBHelper = new CatalogDBHelper(this);
            int updatedInserted = catalogDBHelper.saveProductsFromJSONArray(response.getJSONArray(ProductsTable.TABLE_NAME));

            intent.putExtra(Constants.INSERTED_UPDATED, updatedInserted);
            intent.putExtra(APIConstants.API_PAGE_NUMBER_KEY, response.getInt(APIConstants.API_PAGE_NUMBER_KEY));
            intent.putExtra(APIConstants.API_TOTAL_PAGES_KEY, response.getInt(APIConstants.API_TOTAL_PAGES_KEY));
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void updateBuyerInterest(int todo, Intent intent){
        try {
            JSONObject buyerInterest = new JSONObject();
            int categoryID = intent.getIntExtra(CategoriesTable.COLUMN_CATEGORY_ID, -1);
            int isActive = intent.getIntExtra(CategoriesTable.COLUMN_BUYER_INTEREST_IS_ACTIVE, -1);
            if (categoryID == -1 || isActive == -1) {
                return;
            }
            buyerInterest.put(CategoriesTable.COLUMN_CATEGORY_ID, categoryID);
            buyerInterest.put(CategoriesTable.COLUMN_BUYER_INTEREST_IS_ACTIVE, isActive==1);
            buyerInterest.put(CategoriesTable.COLUMN_CREATED_AT, "");
            buyerInterest.put(CategoriesTable.COLUMN_UPDATED_AT, "");
            buyerInterest.put(CategoriesTable.COLUMN_SYNCED, 0);
            buyerInterest.put(CategoriesTable.COLUMN_BUYER_INTEREST_ID, -1);

            CatalogDBHelper catalogDBHelper = new CatalogDBHelper(this);
            catalogDBHelper.updateBuyerInterestData(buyerInterest);
            Intent broadcastIntent = new Intent(IntentFilters.CATEGORY_DATA);
            broadcastIntent.putExtra(Constants.INSERTED_UPDATED, 1);
            LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);
            updateAllUnsyncedBuyerInterests();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void updateBuyerInterestFromJSON(JSONObject data){
        try {
            CatalogDBHelper catalogDBHelper = new CatalogDBHelper(this);
            catalogDBHelper.updateBuyerInterestData(data.getJSONObject("buyer_interest"));
            //Intent intent = new Intent(IntentFilters.CATEGORY_DATA);
            //intent.putExtra(Constants.INSERTED_UPDATED, 1);
            //LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    private void updateAllUnsyncedBuyerInterests(){
        CatalogDBHelper catalogDBHelper = new CatalogDBHelper(this);
        String[] columns = {CategoriesTable.COLUMN_CATEGORY_ID, CategoriesTable.COLUMN_BUYER_INTEREST_IS_ACTIVE};
        Cursor cursor = catalogDBHelper.getCategoryData(-1,-1,null,-1,-1,0,columns);
        while (cursor.moveToNext()){
            try {
                JSONObject requestBody = new JSONObject();
                requestBody.put(CategoriesTable.COLUMN_CATEGORY_ID,
                        cursor.getInt(cursor.getColumnIndexOrThrow(CategoriesTable.COLUMN_CATEGORY_ID)));
                requestBody.put(CategoriesTable.COLUMN_BUYER_INTEREST_IS_ACTIVE,
                        cursor.getInt(cursor.getColumnIndexOrThrow(CategoriesTable.COLUMN_BUYER_INTEREST_IS_ACTIVE)));
                sendBuyerInterestDataToServer(requestBody);
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
    }

    private void sendBuyerInterestDataToServer(JSONObject requestBody){
        HashMap<String, String> params = new HashMap<>();
        String url = GlobalAccessHelper.generateUrl(APIConstants.BUYER_INTEREST_URL, params);
        volleyStringRequest(TODO.UPDATE_BUYER_INTEREST, Request.Method.POST, url, requestBody.toString());
    }
}
