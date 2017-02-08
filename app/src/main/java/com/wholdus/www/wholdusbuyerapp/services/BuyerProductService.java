package com.wholdus.www.wholdusbuyerapp.services;

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.databaseContracts.CatalogContract;
import com.wholdus.www.wholdusbuyerapp.databaseContracts.CatalogContract.ProductsTable;
import com.wholdus.www.wholdusbuyerapp.databaseHelpers.CatalogDBHelper;
import com.wholdus.www.wholdusbuyerapp.helperClasses.APIConstants;
import com.wholdus.www.wholdusbuyerapp.helperClasses.Constants;
import com.wholdus.www.wholdusbuyerapp.helperClasses.FilterClass;
import com.wholdus.www.wholdusbuyerapp.helperClasses.GlobalAccessHelper;
import com.wholdus.www.wholdusbuyerapp.helperClasses.ShortListMenuItemHelper;
import com.wholdus.www.wholdusbuyerapp.helperClasses.TODO;
import com.wholdus.www.wholdusbuyerapp.models.BuyerProductResponse;
import com.wholdus.www.wholdusbuyerapp.singletons.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

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
        int todo = intent.getIntExtra("TODO", -1);
        switch (todo) {
            case TODO.FETCH_BUYER_PRODUCTS:
                fetchBuyerProducts(todo, 1);
                break;
            case TODO.UPDATE_PRODUCT_RESPONSE:
                updateProductResponseInDB(intent);
                break;
            case TODO.FETCH_BUYER_PRODUCTS_RESPONSE:
                fetchBuyerProductResponse(TODO.FETCH_BUYER_PRODUCTS_RESPONSE, intent);
                break;
            case TODO.UPDATE_UNSYNCED_BUYER_RESPONSES:
                updateAllUnsyncedBuyerProductResponses(null);
                break;
        }
    }

    private void fetchBuyerProducts(int todo, int pageNumber) {
        HashMap<String, String> params = FilterClass.getFilterHashMap();
        //TODO : Save categories and seller data separately so that it doesn't have to requested here
        //TODO : Also try that all products don't have to be requested every time
        params.put("product_details", "1");
        params.put("product_show_online", "1");
        params.putAll(CatalogService.getProductDetailsParams());
        params.put("category_details", "1");
        params.put("items_per_page", "20");
        params.put("page_number", String.valueOf(pageNumber));
        String url = GlobalAccessHelper.generateUrl(APIConstants.BUYER_PRODUCT_URL, params);
        volleyStringRequest(todo, Request.Method.GET, url, null);
    }

    private void fetchBuyerProductResponse(int todo, Intent intent) {
        HashMap<String, String> params = new HashMap<>();
        params.put(APIConstants.API_ITEM_PER_PAGE_KEY,
                String.valueOf(intent.getIntExtra(APIConstants.API_ITEM_PER_PAGE_KEY, 20)));
        params.put(APIConstants.API_PAGE_NUMBER_KEY,
                String.valueOf(intent.getIntExtra(APIConstants.API_PAGE_NUMBER_KEY, 1)));
        params.put(APIConstants.API_RESPONSE_CODE_KEY, intent.getStringExtra(APIConstants.API_RESPONSE_CODE_KEY));
        if (intent.getIntExtra(CatalogContract.CategoriesTable.COLUMN_CATEGORY_ID, -1) != -1) {
            params.put("categoryID", intent.getIntExtra(CatalogContract.CategoriesTable.COLUMN_CATEGORY_ID, -1) + "");
        }
        params.put("product_details", "1");
        params.put("product_show_online", "1");
        params.putAll(CatalogService.getProductDetailsParams());
        params.put("category_details", "1");
        String url = GlobalAccessHelper.generateUrl(APIConstants.BUYER_PRODUCT_RESPONSE_URL, params);
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
                case TODO.FETCH_BUYER_PRODUCTS:
                    saveBuyerProductsToDB(response);
                    break;
                case TODO.FETCH_BUYER_PRODUCTS_RESPONSE:
                    saveBuyerProductResponseToDB(response);
                    break;
                case TODO.UPDATE_PRODUCT_RESPONSE:
                    saveBuyerProductResponseToDB(response);
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void saveBuyerProductsToDB(String response) throws JSONException {
        Intent intent = new Intent(getString(R.string.buyer_product_data_updated));
        if (response == null) {
            // error response
            intent.putExtra(Constants.ERROR_RESPONSE, "Error");
        } else {
            JSONObject data = new JSONObject(response);
            JSONArray buyerProducts = data.getJSONArray("buyer_products");

            if (buyerProducts.length() == 0) {
                intent.putExtra(Constants.INSERTED_UPDATED, -1);
            } else {
                CatalogDBHelper dbHelper = new CatalogDBHelper(this);
                int updatedInserted = dbHelper.saveBuyerProductsDataFromJSONArray(buyerProducts);
                intent.putExtra(Constants.INSERTED_UPDATED, updatedInserted);
            }
            intent.putExtra(APIConstants.API_PAGE_NUMBER_KEY, data.getInt(APIConstants.API_PAGE_NUMBER_KEY));
            intent.putExtra(APIConstants.API_TOTAL_PAGES_KEY, data.getInt(APIConstants.API_TOTAL_PAGES_KEY));
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void updateProductResponseInDB(Intent intent) {
        try {
            JSONObject buyerProductResponse = new JSONObject();
            JSONObject product = new JSONObject();

            int productID = intent.getIntExtra(ProductsTable.COLUMN_PRODUCT_ID, -1);
            int responseCode = intent.getIntExtra(ProductsTable.COLUMN_RESPONSE_CODE, -1);

            if (productID == -1 || responseCode == -1) {
                return;
            }

            product.put(ProductsTable.COLUMN_PRODUCT_ID, productID);
            buyerProductResponse.put("product", product);
            buyerProductResponse.put(ProductsTable.COLUMN_RESPONSE_CODE, responseCode);
            buyerProductResponse.put(ProductsTable.COLUMN_BUYER_PRODUCT_RESPONSE_ID, -1);

            float storeMargin = intent.getFloatExtra(ProductsTable.COLUMN_STORE_MARGIN, -1);
            if (storeMargin > 0) {
                buyerProductResponse.put(ProductsTable.COLUMN_STORE_MARGIN, storeMargin);
            } else {
                buyerProductResponse.put(ProductsTable.COLUMN_STORE_MARGIN, -1);
            }

            buyerProductResponse.put(ProductsTable.COLUMN_HAS_SWIPED, intent.getBooleanExtra(ProductsTable.COLUMN_HAS_SWIPED, true) ? 1 : 0);
            buyerProductResponse.put(ProductsTable.COLUMN_RESPONDED_FROM, intent.getIntExtra(ProductsTable.COLUMN_RESPONDED_FROM, 0));
            SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
            String currentTime = dateFormatGmt.format(new Date());
            buyerProductResponse.put(ProductsTable.COLUMN_PRODUCT_CREATED_AT, currentTime);
            buyerProductResponse.put(ProductsTable.COLUMN_PRODUCT_UPDATED_AT, currentTime);
            buyerProductResponse.put(ProductsTable.COLUMN_SYNCED, 0);

            CatalogDBHelper dbHelper = new CatalogDBHelper(this);
            dbHelper.saveBuyerProductResponseData(buyerProductResponse);

            updateAllUnsyncedBuyerProductResponses(dbHelper);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendBuyerProductResponseUpdateBroadcast(@Nullable Bundle extra){
        Intent intent = new Intent(getString(R.string.buyer_product_response_data_updated));
        if (extra != null) {
            intent.putExtra("extra", extra);
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void saveBuyerProductResponseToDB(String response) throws JSONException {
        Intent intent = new Intent(getString(R.string.buyer_product_data_updated));
        if (response == null) {
            // error response
            intent.putExtra(Constants.ERROR_RESPONSE, "Error");
        } else {
            int updatedInserted = 0;
            JSONObject data = new JSONObject(response);
            CatalogDBHelper dbHelper = new CatalogDBHelper(this);

            if (data.has("buyer_product_response")) {
                JSONObject bpResponse = data.getJSONObject("buyer_product_response");
                updatedInserted = dbHelper.saveBuyerProductResponseData(bpResponse);
                intent.putExtra(Constants.INSERTED_UPDATED, updatedInserted);
            } else {
                JSONArray buyerProducts = data.getJSONArray("buyer_products");

                if (buyerProducts.length() == 0) {
                    intent.putExtra(Constants.INSERTED_UPDATED, -1);
                } else {
                    updatedInserted = dbHelper.saveBPResponseDataFromJSONArray(buyerProducts);
                    intent.putExtra(Constants.INSERTED_UPDATED, updatedInserted);
                }
                intent.putExtra(APIConstants.API_PAGE_NUMBER_KEY, data.getInt(APIConstants.API_PAGE_NUMBER_KEY));
                intent.putExtra(APIConstants.API_TOTAL_PAGES_KEY, data.getInt(APIConstants.API_TOTAL_PAGES_KEY));
                intent.putExtra(APIConstants.TOTAL_ITEMS_KEY, data.getInt(APIConstants.TOTAL_ITEMS_KEY));
            }
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void sendBuyerProductResponseToServer(BuyerProductResponse buyerProductResponse) {
        HashMap<String, String> params = new HashMap<>();
        String url = GlobalAccessHelper.generateUrl(APIConstants.BUYER_PRODUCT_URL, params);
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put(ProductsTable.COLUMN_PRODUCT_ID, buyerProductResponse.getProductID());
            requestBody.put("responded", buyerProductResponse.getResponseCode());
            requestBody.put(ProductsTable.COLUMN_HAS_SWIPED, buyerProductResponse.getHasSwiped());
            requestBody.put(ProductsTable.COLUMN_RESPONDED_FROM, buyerProductResponse.getRespondedFrom());
            if (buyerProductResponse.getStoreMargin() > 0) {
                requestBody.put(ProductsTable.COLUMN_STORE_MARGIN, buyerProductResponse.getStoreMargin());
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        volleyStringRequest(TODO.UPDATE_PRODUCT_RESPONSE, Request.Method.PUT, url, requestBody.toString());
    }

    private void updateAllUnsyncedBuyerProductResponses(@Nullable CatalogDBHelper catalogDBHelper) {
        catalogDBHelper = catalogDBHelper == null ? new CatalogDBHelper(this) : catalogDBHelper;
        String[] columns = {
                ProductsTable.COLUMN_PRODUCT_ID,
                ProductsTable.COLUMN_RESPONSE_CODE,
                ProductsTable.COLUMN_RESPONDED_FROM,
                ProductsTable.COLUMN_HAS_SWIPED,
                ProductsTable.COLUMN_STORE_MARGIN
        };
        String[] sortString = new String[] {ProductsTable.COLUMN_BUYER_PRODUCT_RESPONSE_UPDATED_AT + " ASC "};

        Cursor cursor = catalogDBHelper.getProductData(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                -1,
                -1,
                null,
                null,
                null,
                null,
                -1,
                -1,
                -1,
                0,
                sortString,
                -1,
                -1,
                columns);
        ArrayList<BuyerProductResponse> buyerProductResponses = BuyerProductResponse.getDataFromCursor(cursor);
        for (BuyerProductResponse buyerProductResponse : buyerProductResponses) {
            sendBuyerProductResponseToServer(buyerProductResponse);
        }
    }
}
