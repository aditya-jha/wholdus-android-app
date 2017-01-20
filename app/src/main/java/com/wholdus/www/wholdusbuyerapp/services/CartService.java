package com.wholdus.www.wholdusbuyerapp.services;

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
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
import com.wholdus.www.wholdusbuyerapp.databaseContracts.CatalogContract;
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

public class CartService extends IntentService {

    public static final String REQUEST_TAG = "CART_API_REQUESTS";

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
                startLoaderForSendingCartItems();
                break;
            case R.string.write_cart_item:
                saveCartItem(intent);
                break;
            case R.string.delete_cart:
                deleteAllCarts();
                break;
        }
    }

    public void fetchCart(int todo) {
        HashMap<String, String> params = new HashMap<>();
        //TODO : Save categories and seller data separately so that it doesn't have to requested here
        params.put("sub_cart_details", "1");
        params.put("cart_item_details", "1");
        //TODO : Also try that all products don't have to be requested every time
        params.put("product_details", "1");
        params.put("product_details_details", "1");
        params.put("product_image_details", "1");
        params.put("category_details", "1");
        params.put("seller_details", "1");
        params.put("seller_address_details", "1");
        String url = GlobalAccessHelper.generateUrl(getString(R.string.cart_url), params);
        volleyStringRequest(todo, Request.Method.GET, url, null);
    }

    public void startLoaderForSendingCartItems() {
        CartDBHelper cartDBHelper = new CartDBHelper(getApplicationContext());
        Cursor cursor = cartDBHelper.getCartItemsData(-1, null, -1, -1, 0, null);
        ArrayList<CartItem> cartItems = CartItem.getCartItemsFromCursor(cursor);
        if (cartItems.isEmpty()) {
            return;
        }
        HashMap<String, String> params = new HashMap<>();
        params.put("product_details", "1");
        params.put("sub_cart_details", "1");
        params.put("cart_item_details", "1");
        String url = GlobalAccessHelper.generateUrl(getString(R.string.cart_item_url), params);
        JSONObject jsonData = new JSONObject();
        JSONArray products = new JSONArray();

        try {
            for (CartItem cartItem : cartItems) {
                JSONObject product = new JSONObject();
                product.put(CartItemsTable.COLUMN_PRODUCT_ID, cartItem.getProductID());
                product.put(CartItemsTable.COLUMN_LOTS, cartItem.getLots());
                products.put(product);
            }
            jsonData.put("products", products);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        volleyStringRequest(R.string.post_cart_item, Request.Method.POST, url, jsonData.toString());
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
                    sendCartDataUpdatedBroadCast(null);
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void deleteAllCarts() {
        try {
            CartDBHelper cartDBHelper = new CartDBHelper(this);
            cartDBHelper.deleteCart(-1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void saveCartItem(Intent intent) {
        CartDBHelper cartDBHelper = new CartDBHelper(getApplicationContext());
        JSONObject cartItem = new JSONObject();
        JSONObject product = new JSONObject();
        try {
            cartItem.put(CartItemsTable.COLUMN_CART_ITEM_ID, 0);
            cartItem.put(CartItemsTable.COLUMN_SUBCART_ID, -1);
            Integer productID = intent.getIntExtra(CatalogContract.ProductsTable.COLUMN_PRODUCT_ID, -1);
            if (productID == -1) {
                return;
            }
            product.put(CatalogContract.ProductsTable.COLUMN_PRODUCT_ID, productID);
            cartItem.put("product", product);
            cartItem.put(CartItemsTable.COLUMN_LOTS, intent.getIntExtra(CartItemsTable.COLUMN_LOTS, 1));
            cartItem.put(CartItemsTable.COLUMN_PIECES, intent.getIntExtra(CartItemsTable.COLUMN_PIECES, 1));
            cartItem.put(CartItemsTable.COLUMN_LOT_SIZE, intent.getIntExtra(CartItemsTable.COLUMN_LOT_SIZE, 1));
            cartItem.put(CartItemsTable.COLUMN_RETAIL_PRICE_PER_PIECE, intent.getFloatExtra(CartItemsTable.COLUMN_RETAIL_PRICE_PER_PIECE, 1));
            cartItem.put(CartItemsTable.COLUMN_CALCULATED_PRICE_PER_PIECE, intent.getFloatExtra(CartItemsTable.COLUMN_CALCULATED_PRICE_PER_PIECE, 1));
            cartItem.put(CartItemsTable.COLUMN_SHIPPING_CHARGE, 0);
            cartItem.put(CartItemsTable.COLUMN_FINAL_PRICE, intent.getFloatExtra(CartItemsTable.COLUMN_FINAL_PRICE, 1));
            cartItem.put(CartItemsTable.COLUMN_CREATED_AT, "");
            cartItem.put(CartItemsTable.COLUMN_UPDATED_AT, "");
            cartItem.put(CartItemsTable.COLUMN_SYNCED, 0);

            cartDBHelper.saveCartItemDataFromJSONObject(cartItem);
            Bundle bundle = new Bundle();
            bundle.putInt(CartItemsTable.COLUMN_PIECES, intent.getIntExtra(CartItemsTable.COLUMN_PIECES, 1));
            sendCartItemWriteBroadCast(bundle);
            startLoaderForSendingCartItems();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendCartDataUpdatedBroadCast(@Nullable String extra) {
        Intent intent = new Intent(getString(R.string.cart_data_updated));
        if (extra != null) {
            intent.putExtra("extra", extra);
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void sendCartItemWriteBroadCast(@Nullable Bundle extra) {
        Intent intent = new Intent(getString(R.string.cart_item_written));
        if (extra != null) {
            intent.putExtra("extra", extra);
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
