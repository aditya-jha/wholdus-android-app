package com.wholdus.www.wholdusbuyerapp.services;

import android.app.IntentService;
import android.content.Intent;
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
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
public class UserService extends IntentService {

    public static final String REQUEST_TAG = "USER_API_REQUESTS";

    public UserService() {
        super("UserService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(UserService.class.getSimpleName(), "user service started");
        switch (intent.getIntExtra("TODO", 0)) {
            case R.string.fetch_user_profile:
                fetchUserProfile();
                break;
            default:
                return;
        }
    }

    private void fetchUserProfile() {
        String url = GlobalAccessHelper.generateUrl(getApplicationContext(), getString(R.string.buyer_details_url), null);
        volleyStringRequest(Request.Method.GET, url, null);
    }

    public void volleyStringRequest(int method, String endPoint, final String jsonData) {

        StringRequest stringRequest = new StringRequest(method, endPoint,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject data = new JSONObject(response);
                            JSONArray buyers = data.getJSONArray("buyers");
                            if (buyers.length() == 1) {
                                saveToDB(buyers.getJSONObject(0));
                            }
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
                params.put("Accept", "version=0");
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

    public void cancelRequests() {
        VolleySingleton.getInstance(getApplicationContext()).cancelPendingRequests(REQUEST_TAG);
    }

    private void saveToDB(JSONObject response) throws JSONException {
        UserDBHelper userDBHelper = UserDBHelper.getInstance(this);

        // update userData
        boolean saved = userDBHelper.updateUserData(response) > 0 ? true : false;

        if (saved) {
            Intent intent = new Intent(getString(R.string.user_data_updated));
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }
    }
}
