package com.wholdus.www.wholdusbuyerapp.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.databaseContracts.CartContract;
import com.wholdus.www.wholdusbuyerapp.databaseContracts.CatalogContract.CategoriesTable;
import com.wholdus.www.wholdusbuyerapp.databaseContracts.CatalogContract.ProductsTable;
import com.wholdus.www.wholdusbuyerapp.databaseContracts.OrdersContract;
import com.wholdus.www.wholdusbuyerapp.databaseHelpers.CartDBHelper;
import com.wholdus.www.wholdusbuyerapp.databaseHelpers.CatalogDBHelper;
import com.wholdus.www.wholdusbuyerapp.databaseHelpers.OrderDBHelper;
import com.wholdus.www.wholdusbuyerapp.fragments.ProductsGridFragment;
import com.wholdus.www.wholdusbuyerapp.helperClasses.APIConstants;
import com.wholdus.www.wholdusbuyerapp.helperClasses.Constants;
import com.wholdus.www.wholdusbuyerapp.helperClasses.FilterClass;
import com.wholdus.www.wholdusbuyerapp.helperClasses.GlobalAccessHelper;
import com.wholdus.www.wholdusbuyerapp.helperClasses.HelperFunctions;
import com.wholdus.www.wholdusbuyerapp.helperClasses.IntentFilters;
import com.wholdus.www.wholdusbuyerapp.helperClasses.TODO;
import com.wholdus.www.wholdusbuyerapp.singletons.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by aditya on 10/12/16.
 * For API Calls related to catalog data
 */

public class CatalogService extends IntentService {

    private static final String REQUEST_TAG = "CATALOG_REQUEST";
    private static final String
            PRODUCT_SHARED_PREFERENCES = "ProductsSharedPreference",
            OFFLINE_DELETED_PRODUCTS_KEY = "OfflineDeletedProductsKey";

    public CatalogService() {
        super("CatalogService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        int todo = intent.getIntExtra("TODO", -1);
        switch (todo) {
            case TODO.FETCH_CATEGORIES:
                fetchCategories(todo, intent.getBooleanExtra(getString(R.string.seller_category_details), false),
                        intent.getBooleanExtra(getString(R.string.category_product_details), false));
                break;
            case R.integer.fetch_products:
                int pageNumber = intent.getIntExtra("page_number", 0);
                int itemsPerPage = intent.getIntExtra("items_per_page", 20);
                fetchProducts(todo, pageNumber, itemsPerPage);
                break;
            case TODO.FETCH_DELETED_OFFLINE_PRODUCTS:
                fetchDeletedOfflineProducts(todo, intent);
                break;
            case TODO.UPDATE_BUYER_INTEREST:
                updateBuyerInterest(todo, intent);
                break;
            case R.integer.fetch_specific_products:
                fetchSpecificProducts(todo, intent);
                break;
            case TODO.UPDATE_UNSYNCED_BUYER_INTERESTS:
                updateAllUnsyncedBuyerInterests();
                break;
        }
    }


    private void fetchProducts(int todo, int pageNumber, int itemsPerPage) {
        HashMap<String, String> params = FilterClass.getFilterHashMap();
        params.putAll(getProductDetailsParams());
        params.put("items_per_page", String.valueOf(itemsPerPage));
        params.put("page_number", String.valueOf(pageNumber));
        params.put("product_show_online", "1");
        String endPoint = GlobalAccessHelper.generateUrl(getString(R.string.product_url), params);
        volleyStringRequest(todo, Request.Method.GET, endPoint, null);
    }

    private void fetchSpecificProducts(int todo, Intent intent) {
        HashMap<String, String> params = getProductDetailsParams();
        params.put(ProductsTable.COLUMN_PRODUCT_ID, intent.getStringExtra("productIDs"));
        params.put("items_per_page", intent.getStringExtra("items_per_page"));
        params.put("page_number", "1");
        params.put("product_show_online", "1");
        String endPoint = GlobalAccessHelper.generateUrl(getString(R.string.product_url), params);
        volleyStringRequest(todo, Request.Method.GET, endPoint, null);
    }

    private void fetchDeletedOfflineProducts(int todo, Intent intent){
        HashMap<String, String> params = new HashMap<>();
        SharedPreferences preferences = getSharedPreferences(PRODUCT_SHARED_PREFERENCES, MODE_PRIVATE);
        String updatedAt = preferences.getString(OFFLINE_DELETED_PRODUCTS_KEY, "2016-01-01T00:00:00.000Z");
        params.put("product_updated_at", updatedAt);
        String endPoint = GlobalAccessHelper.generateUrl(getString(R.string.product_deleted_offline_url), params);
        volleyStringRequest(todo, Request.Method.GET, endPoint, null);
    }

    public static HashMap<String, String> getProductDetailsParams(){
        HashMap<String, String> params = new HashMap<>();
        params.put("product_image_details", "1");
        params.put("product_details_details", "1");
        params.put("seller_details", "1");
        params.put("seller_address_details", "1");
        return params;
    }

    private void fetchCategories(int todo, boolean categoryDetails, boolean productDetails) {
        HashMap<String, String> params = new HashMap<>();
        if (categoryDetails) {
            params.put("seller_category_details", "1");
        }
        if (productDetails) {
            params.put("category_product_details", "1");
            params.putAll(getProductDetailsParams());
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
                                case TODO.FETCH_CATEGORIES:
                                    updateCategories(data);
                                    break;
                                case R.integer.fetch_products:
                                    updateProducts(data);
                                    break;
                                case R.integer.fetch_specific_products:
                                    updateSpecificProducts(data);
                                    break;
                                case TODO.FETCH_DELETED_OFFLINE_PRODUCTS:
                                    updateOfflineDeletedProducts(data);
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
                try {
                    switch (todo) {
                        case TODO.FETCH_CATEGORIES:
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
            JSONArray products = response.getJSONArray(ProductsTable.TABLE_NAME);
            if (products.length() == 0) {
                intent.putExtra(Constants.INSERTED_UPDATED, -1);
            } else {
                int updatedInserted = catalogDBHelper.saveProductsFromJSONArray(products);
                intent.putExtra(Constants.INSERTED_UPDATED, updatedInserted);
            }
            intent.putExtra(APIConstants.API_PAGE_NUMBER_KEY, response.getInt(APIConstants.API_PAGE_NUMBER_KEY));
            intent.putExtra(APIConstants.API_TOTAL_PAGES_KEY, response.getInt(APIConstants.API_TOTAL_PAGES_KEY));
            intent.putExtra(APIConstants.TOTAL_ITEMS_KEY, response.getInt(APIConstants.TOTAL_ITEMS_KEY));
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void updateSpecificProducts(JSONObject response) throws JSONException {

        if (response != null) {
            Intent intent = new Intent(IntentFilters.SPECIFIC_PRODUCT_DATA);
            CatalogDBHelper catalogDBHelper = new CatalogDBHelper(this);
            catalogDBHelper.saveProductsFromJSONArray(response.getJSONArray(ProductsTable.TABLE_NAME));
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }

    }

    private void updateBuyerInterest(int todo, Intent intent) {
        try {
            JSONObject buyerInterest = new JSONObject();
            int categoryID = intent.getIntExtra(CategoriesTable.COLUMN_CATEGORY_ID, -1);
            int isActive = intent.getIntExtra(CategoriesTable.COLUMN_BUYER_INTEREST_IS_ACTIVE, -1);
            if (categoryID == -1 || isActive == -1) {
                return;
            }
            buyerInterest.put(CategoriesTable.COLUMN_CATEGORY_ID, categoryID);
            buyerInterest.put(CategoriesTable.COLUMN_BUYER_INTEREST_IS_ACTIVE, isActive == 1);
            buyerInterest.put(CategoriesTable.COLUMN_CREATED_AT, "");
            buyerInterest.put(CategoriesTable.COLUMN_UPDATED_AT, "");
            buyerInterest.put(CategoriesTable.COLUMN_SYNCED, 0);
            buyerInterest.put(CategoriesTable.COLUMN_BUYER_INTEREST_ID, -1);

            CatalogDBHelper catalogDBHelper = new CatalogDBHelper(this);
            catalogDBHelper.updateBuyerInterestData(buyerInterest);
            //Intent broadcastIntent = new Intent(IntentFilters.CATEGORY_DATA);
            //broadcastIntent.putExtra(Constants.INSERTED_UPDATED, 1);
            //LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);
            updateAllUnsyncedBuyerInterests();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateBuyerInterestFromJSON(JSONObject data) {
        try {
            CatalogDBHelper catalogDBHelper = new CatalogDBHelper(this);
            catalogDBHelper.updateBuyerInterestData(data.getJSONObject("buyer_interest"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void updateOfflineDeletedProducts(JSONObject data){
        try {
            CatalogDBHelper catalogDBHelper = new CatalogDBHelper(this);
            CartDBHelper cartDBHelper = new CartDBHelper(this);
            OrderDBHelper orderDBHelper = new OrderDBHelper(this);
            Cursor cartItemsCursor = cartDBHelper.getCartItemsData(-1,null,-1,-1,-1,new String[] {CartContract.CartItemsTable.COLUMN_PRODUCT_ID});
            ArrayList<Integer> cartItemsArray = new ArrayList<>();
            while (cartItemsCursor.moveToNext()){
                cartItemsArray.add(cartItemsCursor.getInt(cartItemsCursor.getColumnIndexOrThrow(CartContract.CartItemsTable.COLUMN_PRODUCT_ID)));
            }
            String cartItemsString = TextUtils.join(",", cartItemsArray);
            Cursor orderItemsCursor = orderDBHelper.getOrderItemsData(null,null,null,new String[] {OrdersContract.OrderItemsTable.COLUMN_PRODUCT_ID});
            ArrayList<Integer> orderItemsArray = new ArrayList<>();
            while (orderItemsCursor.moveToNext()){
                orderItemsArray.add(orderItemsCursor.getInt(orderItemsCursor.getColumnIndexOrThrow(OrdersContract.OrderItemsTable.COLUMN_PRODUCT_ID)));
            }
            String orderItemsString = TextUtils.join(",", orderItemsArray);
            catalogDBHelper.updateOfflineProducts(data.getString("offline_products"), cartItemsString, orderItemsString);
            catalogDBHelper.updateDeletedProducts(data.getString("deleted_products"));
            SharedPreferences preferences = getSharedPreferences(PRODUCT_SHARED_PREFERENCES, MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(OFFLINE_DELETED_PRODUCTS_KEY, data.getJSONObject("meta").getString("timestamp"));
            editor.apply();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void updateAllUnsyncedBuyerInterests() {
        CatalogDBHelper catalogDBHelper = new CatalogDBHelper(this);
        String[] columns = {CategoriesTable.COLUMN_CATEGORY_ID, CategoriesTable.COLUMN_BUYER_INTEREST_IS_ACTIVE};
        Cursor cursor = catalogDBHelper.getCategoryData(-1, -1, null, -1, -1, 0, columns);
        while (cursor.moveToNext()) {
            try {
                JSONObject requestBody = new JSONObject();
                requestBody.put(CategoriesTable.COLUMN_CATEGORY_ID,
                        cursor.getInt(cursor.getColumnIndexOrThrow(CategoriesTable.COLUMN_CATEGORY_ID)));
                requestBody.put(CategoriesTable.COLUMN_BUYER_INTEREST_IS_ACTIVE,
                        cursor.getInt(cursor.getColumnIndexOrThrow(CategoriesTable.COLUMN_BUYER_INTEREST_IS_ACTIVE)));
                sendBuyerInterestDataToServer(requestBody);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendBuyerInterestDataToServer(JSONObject requestBody) {
        HashMap<String, String> params = new HashMap<>();
        String url = GlobalAccessHelper.generateUrl(APIConstants.BUYER_INTEREST_URL, params);
        volleyStringRequest(TODO.UPDATE_BUYER_INTEREST, Request.Method.POST, url, requestBody.toString());
    }
}
