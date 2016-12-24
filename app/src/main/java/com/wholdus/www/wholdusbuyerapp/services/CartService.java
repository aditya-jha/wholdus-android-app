package com.wholdus.www.wholdusbuyerapp.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.wallet.Cart;
import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.databaseContracts.CartContract.CartItemsTable;
import com.wholdus.www.wholdusbuyerapp.databaseHelpers.CartDBHelper;
import com.wholdus.www.wholdusbuyerapp.helperClasses.GlobalAccessHelper;
import com.wholdus.www.wholdusbuyerapp.loaders.CartItemLoader;
import com.wholdus.www.wholdusbuyerapp.models.CartItem;
import com.wholdus.www.wholdusbuyerapp.singletons.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by kaustubh on 21/12/16.
 */

public class CartService extends IntentService implements CartItemLoader.OnLoadCompleteListener<ArrayList<CartItem>>{

    public static final String REQUEST_TAG = "CART_API_REQUESTS";
    private final int CART_ITEM_DB_LOADER = 90;
    private CartItemLoader cartItemLoader;
    // TODO : Set all LOADER values in constants

    public CartService() {
        super("CartService");
    }
    protected void onHandleIntent(Intent intent) {
        switch (intent.getIntExtra("TODO", 0)) {
            case R.string.fetch_cart:
                fetchCart(R.string.fetch_cart);
                break;
            case R.string.post_cart_item:
                // TODO : Also add added from column
                //String[] columns = {CartItemsTable.COLUMN_PRODUCT_ID, CartItemsTable.COLUMN_LOTS};
                cartItemLoader = new CartItemLoader(getApplicationContext(), -1, null, -1, -1, 0, null);
                cartItemLoader.registerListener(CART_ITEM_DB_LOADER, this);
                cartItemLoader.startLoading();
                break;
        }
    }
    public void fetchCart(int todo){
        HashMap<String,String> params = new HashMap<>();
        //TODO : Save categories and seller data separately so that it doesn't have to requested here
        params.put("sub_cart_details", "1");
        params.put("cart_item_details", "1");
        //TODO : Also try that all products don't have to be requested every time
        params.put("product_details", "1");
        params.put("product_details_details","1");
        params.put("product_image_details", "1");
        params.put("category_details", "1");
        params.put("seller_details", "1");
        String url = GlobalAccessHelper.generateUrl(getString(R.string.cart_url), params);
        volleyStringRequest(todo, Request.Method.GET, url, null);
    }

    public void volleyStringRequest(final int todo, int method, String endPoint, final String jsonData) {

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

    private void onResponseHandler(final int todo, String response) {
        try {
            JSONObject data = new JSONObject(response);
            switch (todo) {
                default:
                    JSONArray carts = data.getJSONArray("carts");
                    CartDBHelper cartDBHelper = new CartDBHelper(this);
                    if (carts.length() > 0) {
                        JSONObject cart = carts.getJSONObject(0);
                        cartDBHelper.saveCartDataFromJSONObject(cart);
                    } else {
                        cartDBHelper.deleteCart(-1);
                    }
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (cartItemLoader != null) {
            cartItemLoader.unregisterListener(this);
            cartItemLoader.cancelLoad();
            cartItemLoader.stopLoading();
        }
    }

    @Override
    public void onLoadComplete(Loader<ArrayList<CartItem>> loader, ArrayList<CartItem> data) {
        if (data.isEmpty()){
            return;
        }
        HashMap<String,String> params = new HashMap<>();
        params.put("sub_cart_details", "1");
        params.put("cart_item_details", "1");
        String url = GlobalAccessHelper.generateUrl(getString(R.string.cart_item_url), params);
        JSONObject jsonData = new JSONObject();
        JSONArray products = new JSONArray();
        JSONObject product = new JSONObject();
        try {
            for (CartItem cartItem : data) {
                product.put(CartItemsTable.COLUMN_PRODUCT_ID, cartItem.getProductID());
                product.put(CartItemsTable.COLUMN_LOTS, cartItem.getLots());
                products.put(product);
            }
            jsonData.put("products", products);
        } catch (JSONException e){
            e.printStackTrace();
            return;
        }

        volleyStringRequest(R.string.post_cart_item, Request.Method.POST, url, jsonData.toString());
    }
}
