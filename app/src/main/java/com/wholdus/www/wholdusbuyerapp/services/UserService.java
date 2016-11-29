package com.wholdus.www.wholdusbuyerapp.services;

import android.app.IntentService;
import android.content.ContentValues;
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
import com.wholdus.www.wholdusbuyerapp.databaseContracts.UserProfileContract.UserTable;
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
        switch (intent.getIntExtra("TODO", 0)) {
            case R.string.fetch_user_profile:
                fetchUserProfile(R.string.fetch_user_profile);
                break;
            case R.string.update_user_profile:
                // update user profile
                try {
                    JSONObject data = new JSONObject();
                    data.put(UserTable.COLUMN_COMPANY_NAME, intent.getStringExtra(getString(R.string.company_name_key)));
                    data.put(UserTable.COLUMN_WHATSAPP_NUMBER, intent.getStringExtra(getString(R.string.whatsapp_number_key)));
                    data.put(UserTable.COLUMN_NAME, intent.getStringExtra(getString(R.string.name_key)));

                    JSONObject details = new JSONObject();
                    details.put("buyertypeID", intent.getStringExtra(getString(R.string.business_type_key)));
                    data.put("details", details);

                    updateUserProfile(R.string.update_user_profile, data);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case R.string.fetch_business_types:
                // fetch business types and update db
                fetchBusinessTypes(R.string.fetch_business_types);
                break;
            default:
                return;
        }
    }

    private void fetchUserProfile(int todo) {
        String url = GlobalAccessHelper.generateUrl(getApplicationContext(), getString(R.string.buyer_details_url), null);
        volleyStringRequest(todo, Request.Method.GET, url, null);
    }

    private void updateUserProfile(int todo, JSONObject data) throws JSONException {
        ContentValues cv = new ContentValues();
        cv.put(UserTable.COLUMN_WHATSAPP_NUMBER, data.getString(UserTable.COLUMN_WHATSAPP_NUMBER));
        cv.put(UserTable.COLUMN_COMPANY_NAME, data.getString(UserTable.COLUMN_COMPANY_NAME));
        cv.put(UserTable.COLUMN_NAME, data.getString(UserTable.COLUMN_NAME));
        cv.put(UserTable.COLUMN_BUSINESS_TYPE, data.getJSONObject("details").getString("buyertypeID"));

        UserDBHelper userDBHelper = new UserDBHelper(this);
        userDBHelper.updateUserData(GlobalAccessHelper.getBuyerID(getApplication()), cv);

        sendUserDataUpdatedBroadCast(getString(R.string.user_data_modified));

        // send to server
        String url = GlobalAccessHelper.generateUrl(this, getString(R.string.buyer_details_url), null);
        volleyStringRequest(todo, Request.Method.PUT, url, data.toString());
    }

    private void fetchBusinessTypes(int todo) {
        String url = GlobalAccessHelper.generateUrl(this, getString(R.string.business_types_url), null);
        volleyStringRequest(todo, Request.Method.GET, url, null);
    }

    private void onResponseHandler(final int todo, String response) {
        try {
            JSONObject data = new JSONObject(response);
            switch (todo) {
                case R.string.fetch_user_profile:
                    JSONArray buyers = data.getJSONArray("buyers");
                    if (buyers.length() == 1) {
                        saveResponseToDB(buyers.getJSONObject(0));
                    }
                    break;
                case R.string.update_user_profile:
                    //saveResponseToDB(data.getJSONObject("buyers"));
                    break;
                case R.string.fetch_business_types:
                    handleBusinessTypesResponse(data);
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
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

    private void saveResponseToDB(JSONObject response) throws JSONException {
        UserDBHelper userDBHelper = new UserDBHelper(this);

        // update userData
        boolean savedUserData = userDBHelper.updateUserData(response) > 0;
        boolean savedUserAddress = userDBHelper.updateUserAddressData(response) > 0;

        if (savedUserData) {
            sendUserDataUpdatedBroadCast(null);
        }
    }

    private void handleBusinessTypesResponse(JSONObject data) throws JSONException {
        UserDBHelper userDBHelper = new UserDBHelper(this);

        boolean savedBusinessTypesData = userDBHelper.updateBusinessTypesData(data) > 0;
        if (savedBusinessTypesData) {
            sendUserDataUpdatedBroadCast(getString(R.string.business_types_data_updated));
        }
    }

    private void sendUserDataUpdatedBroadCast(@Nullable String extra) {
        Intent intent = new Intent(getString(R.string.user_data_updated));
        if (extra != null) {
            intent.putExtra("extra", extra);
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
