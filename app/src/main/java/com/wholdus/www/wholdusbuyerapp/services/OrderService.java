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
import com.wholdus.www.wholdusbuyerapp.databaseHelpers.OrderDBHelper;
import com.wholdus.www.wholdusbuyerapp.databaseHelpers.UserDBHelper;
import com.wholdus.www.wholdusbuyerapp.helperClasses.GlobalAccessHelper;
import com.wholdus.www.wholdusbuyerapp.singletons.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by kaustubh on 8/12/16.
 */

public class OrderService extends IntentService {

    public static final String REQUEST_TAG = "ORDER_API_REQUESTS";

    public OrderService() {
        super("OrderService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        switch (intent.getIntExtra("TODO", 0)) {
            case R.string.fetch_orders:
                fetchOrders(R.string.fetch_orders, 1);
                break;
            default:
                return;
        }
    }

    public void fetchOrders(int todo, int pageNumber){
        HashMap<String,Object> params = new HashMap<>();
        params.put("items_per_page", 10);
        params.put("page_number", pageNumber);
        String url = GlobalAccessHelper.generateUrl(getApplicationContext(), getString(R.string.orders_url), params);
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
                case R.string.fetch_orders:
                    JSONArray orders = data.getJSONArray("orders");
                    saveOrderstoDB(orders);
                    handlePagination(data, todo);
                    break;
                default:
                    return;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void saveOrderstoDB(JSONArray ordersArray) throws JSONException{
        OrderDBHelper orderDBHelper = new OrderDBHelper(this);
        orderDBHelper.saveOrdersData(ordersArray);
    }

    private void handlePagination(JSONObject data, int todo){
        try {
            int pageNumber = data.getInt("page_number");
            int totalPages = data.getInt("total_pages");
            if (pageNumber < totalPages) {
                switch (todo) {
                    case R.string.fetch_orders:
                        fetchOrders(R.string.fetch_orders, pageNumber + 1);
                }
            }
        } catch (JSONException e){
            e.printStackTrace();
        }
    }
}
