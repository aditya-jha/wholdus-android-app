package com.wholdus.www.wholdusbuyerapp.services;

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.databaseContracts.CatalogContract.ProductsTable;
import com.wholdus.www.wholdusbuyerapp.databaseHelpers.CatalogDBHelper;
import com.wholdus.www.wholdusbuyerapp.helperClasses.GlobalAccessHelper;
import com.wholdus.www.wholdusbuyerapp.helperClasses.TODO;
import com.wholdus.www.wholdusbuyerapp.models.BuyerProductResponse;
import com.wholdus.www.wholdusbuyerapp.singletons.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
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
        int todo = intent.getIntExtra("TODO", -1);
        switch (todo) {
            case TODO.FETCH_BUYER_PRODUCTS:
                fetchBuyerProducts(todo, 1);
                break;
            case TODO.UPDATE_PRODUCT_RESPONSE:
                updateProductResponseInDB(intent);
                break;
        }
    }

    private void fetchBuyerProducts(int todo, int pageNumber) {
        HashMap<String, String> params = new HashMap<>();
        //TODO : Save categories and seller data separately so that it doesn't have to requested here
        //TODO : Also try that all products don't have to be requested every time
        params.put("product_details", "1");
        params.put("product_details_details", "1");
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
                case TODO.FETCH_BUYER_PRODUCTS:
                    saveBuyerProductsToDB(response);
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
        JSONObject data = new JSONObject(response);
        // TODO Handle pagination
        CatalogDBHelper dbHelper = new CatalogDBHelper(this);
        JSONArray buyerProducts = data.getJSONArray("buyer_products");
        dbHelper.saveBuyerProductsDataFromJSONArray(buyerProducts);
        sendBuyerProductDataUpdatedBroadCast(null);
        //TODO : Possibly fetch a minimum of 50 buyer products
    }

    private void sendBuyerProductDataUpdatedBroadCast(@Nullable String extra) {
        Intent intent = new Intent(getString(R.string.buyer_product_data_updated));
        if (extra != null) {
            intent.putExtra("extra", extra);
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
            buyerProductResponse.put(ProductsTable.COLUMN_PRODUCT_CREATED_AT, "");
            buyerProductResponse.put(ProductsTable.COLUMN_PRODUCT_UPDATED_AT, "");
            buyerProductResponse.put(ProductsTable.COLUMN_SYNCED, 0);

            CatalogDBHelper dbHelper = new CatalogDBHelper(this);
            dbHelper.saveBuyerProductResponseData(buyerProductResponse);

            updateAllUnsyncedBuyerProductResponses(dbHelper);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveBuyerProductResponseToDB(String response) throws JSONException {
        JSONObject data = new JSONObject(response);
        CatalogDBHelper dbHelper = new CatalogDBHelper(this);
        dbHelper.saveBuyerProductResponseData(data.getJSONObject("buyer_product_response"));
    }

    private void sendBuyerProductResponseToServer(BuyerProductResponse buyerProductResponse) {
        HashMap<String, String> params = new HashMap<>();
        String url = GlobalAccessHelper.generateUrl(getString(R.string.buyer_product_url), params);
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
        catalogDBHelper  = catalogDBHelper == null ? new CatalogDBHelper(this) : catalogDBHelper;
        String[] columns = {
                ProductsTable.COLUMN_PRODUCT_ID,
                ProductsTable.COLUMN_RESPONSE_CODE,
                ProductsTable.COLUMN_RESPONDED_FROM,
                ProductsTable.COLUMN_HAS_SWIPED,
                ProductsTable.COLUMN_STORE_MARGIN
        };
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
                null,
                -1,
                -1,
                columns);
        ArrayList<BuyerProductResponse> buyerProductResponses = BuyerProductResponse.getDataFromCursor(cursor);
        for (BuyerProductResponse buyerProductResponse : buyerProductResponses) {
            sendBuyerProductResponseToServer(buyerProductResponse);
        }
    }
}
