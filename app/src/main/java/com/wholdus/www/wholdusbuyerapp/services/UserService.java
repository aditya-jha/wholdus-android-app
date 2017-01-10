package com.wholdus.www.wholdusbuyerapp.services;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.databaseContracts.UserProfileContract.UserAddressTable;
import com.wholdus.www.wholdusbuyerapp.databaseContracts.UserProfileContract.UserTable;
import com.wholdus.www.wholdusbuyerapp.databaseHelpers.CatalogDBHelper;
import com.wholdus.www.wholdusbuyerapp.databaseHelpers.UserDBHelper;
import com.wholdus.www.wholdusbuyerapp.helperClasses.APIConstants;
import com.wholdus.www.wholdusbuyerapp.helperClasses.Constants;
import com.wholdus.www.wholdusbuyerapp.helperClasses.FilterClass;
import com.wholdus.www.wholdusbuyerapp.helperClasses.GlobalAccessHelper;
import com.wholdus.www.wholdusbuyerapp.helperClasses.HelperFunctions;
import com.wholdus.www.wholdusbuyerapp.helperClasses.TODO;
import com.wholdus.www.wholdusbuyerapp.models.BuyerAddress;
import com.wholdus.www.wholdusbuyerapp.singletons.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.R.attr.breadCrumbShortTitle;
import static android.R.attr.cacheColorHint;
import static android.R.attr.data;
import static com.wholdus.www.wholdusbuyerapp.databaseContracts.UserProfileContract.*;


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
        int todo = intent.getIntExtra("TODO", 0);
        try {
            switch (todo) {
                case TODO.FETCH_USER_PROFILE:
                    fetchUserProfile(todo);
                    break;
                case R.string.update_user_profile:
                    // update user profile
                    JSONObject data = new JSONObject();
                    data.put(UserTable.COLUMN_COMPANY_NAME, intent.getStringExtra(getString(R.string.company_name_key)));
                    data.put(UserTable.COLUMN_WHATSAPP_NUMBER, intent.getStringExtra(getString(R.string.whatsapp_number_key)));
                    data.put(UserTable.COLUMN_NAME, intent.getStringExtra(getString(R.string.name_key)));

                    JSONObject details = new JSONObject();
                    details.put("buyertypeID", intent.getStringExtra(getString(R.string.business_type_key)));
                    data.put("details", details);

                    updateUserProfile(todo, data);
                    break;
                case R.string.fetch_business_types:
                    // fetch business types and update db
                    fetchBusinessTypes(todo);
                    break;
                case R.string.update_user_address:
                    JSONObject address = new JSONObject(intent.getStringExtra(UserAddressTable.TABLE_NAME));
                    updateUserAddress(todo, address);
                    break;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void updateUserAddress(int todo, JSONObject address) throws JSONException {
        UserDBHelper userDBHelper = new UserDBHelper(this);
        userDBHelper.updateUserAddressData(address);
        updateAllUnSyncedAddresses();
        sendUserDataUpdatedBroadCast(getString(R.string.user_data_modified));
    }

    private void fetchUserProfile(int todo) {
        HashMap<String,String> params = new HashMap<>();
        params.put("buyer_address_details", "1");
        params.put("buyer_interest_details", "1");
        params.put("buyer_details_details", "1");
        params.put("category_details", "1");
        String url = GlobalAccessHelper.generateUrl(getString(R.string.buyer_details_url), params);
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

        HashMap<String,String> params = new HashMap<>();
        params.put("buyer_address_details", "1");
        params.put("buyer_interest_details", "1");
        params.put("buyer_details_details", "1");
        params.put("category_details", "1");
        // send to server
        String url = GlobalAccessHelper.generateUrl(getString(R.string.buyer_details_url), params);
        volleyStringRequest(todo, Request.Method.PUT, url, data.toString());
    }

    private void fetchBusinessTypes(int todo) {
        String url = GlobalAccessHelper.generateUrl(getString(R.string.business_types_url), null);
        volleyStringRequest(todo, Request.Method.GET, url, null);
    }


    private void onResponseHandler(final int todo, String response) {
        try {
            JSONObject data = new JSONObject(response);
            switch (todo) {
                case TODO.FETCH_USER_PROFILE:
                    JSONArray buyers = data.getJSONArray("buyers");
                    if (buyers.length() == 1) {
                        saveResponseToDB(buyers.getJSONObject(0));
                    }
                    break;
                case R.string.update_user_profile:
                    saveResponseToDB(data.getJSONObject("buyer"));
                    break;
                case R.string.fetch_business_types:
                    handleBusinessTypesResponse(data);
                    break;
                case R.string.update_user_address:
                    saveBuyerAddressUpdateResponseToDb(data);
                    break;

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void saveResponseToDB(JSONObject response) throws JSONException {
        UserDBHelper userDBHelper = new UserDBHelper(this);
        CatalogDBHelper catalogDBHelper = new CatalogDBHelper(this);

        // update userData
        boolean savedUserData = userDBHelper.updateUserData(response) > 0;
        boolean savedUserAddress = userDBHelper.updateUserAddressData(response) > 0;
        catalogDBHelper.updateBuyerInterestsDataFromJSONArray(response.getJSONArray("buyer_interests"));

        sendUserDataUpdatedBroadCast(null);
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
                Intent intent = new Intent(getString(R.string.user_data_updated));
                intent.putExtra(Constants.ERROR_RESPONSE, "Error");
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
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

    private void updateAllUnSyncedAddresses(){
        UserDBHelper userDBHelper = new UserDBHelper(getApplicationContext());
        ArrayList<BuyerAddress> buyerAddresses = BuyerAddress.getBuyerAddressesFromCursor(userDBHelper.getUserAddress(-1,-1,0,0,null));
        for (BuyerAddress buyerAddress: buyerAddresses){
            sendBuyerAddressToServer(buyerAddress);
        }

    }

    private void sendBuyerAddressToServer(BuyerAddress buyerAddress){
        HashMap<String, String> params = new HashMap<>();
        String url = GlobalAccessHelper.generateUrl(getString(R.string.buyer_address_url), params);
        JSONObject requestBody = new JSONObject();

        int requestMethod;
        try {
            requestBody.put(UserAddressTable.COLUMN_PINCODE, buyerAddress.getPincode());
            requestBody.put(UserAddressTable.COLUMN_ADDRESS, buyerAddress.getAddress());
            requestBody.put(UserAddressTable.COLUMN_LANDMARK, buyerAddress.getLandmark());
            requestBody.put(UserAddressTable.COLUMN_CITY, buyerAddress.getCity());
            requestBody.put(UserAddressTable.COLUMN_STATE, buyerAddress.getState());
            requestBody.put(UserAddressTable.COLUMN_CONTACT_NUMBER, buyerAddress.getContactNumber());
            requestBody.put(UserAddressTable.COLUMN_ADDRESS_ALIAS, buyerAddress.getAlias());

            if (buyerAddress.getAddressID() > 0){
                requestBody.put(UserAddressTable.COLUMN_ADDRESS_ID, buyerAddress.getAddressID());
                requestMethod = Request.Method.PUT;
            } else {
                requestBody.put(UserAddressTable.COLUMN_CLIENT_ID, buyerAddress.getClientID());
                requestMethod = Request.Method.POST;
            }

        } catch (JSONException e){
            e.printStackTrace();
            return;
        }

        volleyStringRequest(R.string.update_user_address, requestMethod, url, requestBody.toString());
    }

    public void saveBuyerAddressUpdateResponseToDb(JSONObject data) throws JSONException{
        UserDBHelper userDBHelper = new UserDBHelper(getApplicationContext());
        userDBHelper.updateUserAddressDataFromJSONObject(data.getJSONObject("address"), false);
        sendUserDataUpdatedBroadCast(null);
    }
}
