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
import com.wholdus.www.wholdusbuyerapp.databaseHelpers.CatalogDBHelper;
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
                fetchCategories(todo);
                break;

        }
    }

    public void fetchCategories(int todo) {
        String endPoint = GlobalAccessHelper.generateUrl(getApplicationContext(), getString(R.string.category_url), null);
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
        switch (todo) {
            case R.integer.fetch_categories:
                // save to db
                CatalogDBHelper dbHelper = new CatalogDBHelper(getApplicationContext());
                int updatedInserted = dbHelper.updateCategories(response);

                if (updatedInserted > 0) {
                    sendUpdatedBroadCast(getString(R.string.categories_data_updated), null);
                }
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
