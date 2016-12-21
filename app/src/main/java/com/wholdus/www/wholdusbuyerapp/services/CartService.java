package com.wholdus.www.wholdusbuyerapp.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.wallet.Cart;
import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.databaseHelpers.CartDBHelper;
import com.wholdus.www.wholdusbuyerapp.helperClasses.GlobalAccessHelper;
import com.wholdus.www.wholdusbuyerapp.singletons.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
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
                case R.string.fetch_cart:
                    JSONArray carts = data.getJSONArray("carts");
                    if (carts.length() > 0) {
                        JSONObject cart = carts.getJSONObject(0);
                        saveCartToDB(cart);
                    }
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void saveCartToDB(JSONObject cart) throws JSONException{
        CartDBHelper cartDBHelper = new CartDBHelper(this);
        cartDBHelper.saveCartDataFromJSONObject(cart);
        //TODO : Send cart updated broadcast
    }

}
