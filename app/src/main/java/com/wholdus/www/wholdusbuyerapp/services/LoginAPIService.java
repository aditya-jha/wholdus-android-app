package com.wholdus.www.wholdusbuyerapp.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import com.google.firebase.crash.FirebaseCrash;
import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.databaseContracts.UserProfileContract;
import com.wholdus.www.wholdusbuyerapp.helperClasses.APIConstants;
import com.wholdus.www.wholdusbuyerapp.helperClasses.Constants;
import com.wholdus.www.wholdusbuyerapp.helperClasses.IntentFilters;
import com.wholdus.www.wholdusbuyerapp.helperClasses.LoginHelper;
import com.wholdus.www.wholdusbuyerapp.helperClasses.OkHttpHelper;
import com.wholdus.www.wholdusbuyerapp.helperClasses.TODO;

import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.R.attr.cacheColorHint;
import static android.R.attr.type;

/**
 * Created by aditya on 12/11/16.
 */

public class LoginAPIService extends IntentService {

    public LoginAPIService() {
        super("LoginAPIService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            final int todo = intent.getIntExtra("TODO", -1);
            switch (todo) {
                case TODO.LOGIN:
                    login(intent);
                    break;
                case TODO.SINGUP:
                    signup();
                    break;
                case TODO.FORGOT_PASSWORD:
                    forgotPassword();
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            FirebaseCrash.report(e);
            sendBroadcast(intent.getIntExtra("TODO", -1), e.toString(), 500);
        }
    }


    public void signup() {
//        String endPoint = super.generateUrl(mContext.getString(R.string.signup_url));
//
//        try {
//            JSONObject userData = new JSONObject();
//            userData.put(NAME_KEY, name);
//            userData.put(MOBILE_NUMBER_KEY, mobileNumber);
//            userData.put(PASSWORD_KEY, password);
//
//            super.volleyStringRequest(Request.Method.POST, endPoint, userData.toString());
//        } catch (Exception e) {
//            Toast.makeText(mContext, e.toString(), Toast.LENGTH_SHORT).show();
//        }
    }

    public void login(Intent intent) throws Exception {
        JSONObject loginData = new JSONObject();

        loginData.put(UserProfileContract.UserTable.COLUMN_MOBILE_NUMBER,
                intent.getStringExtra(UserProfileContract.UserTable.COLUMN_MOBILE_NUMBER));
        loginData.put(UserProfileContract.UserTable.COLUMN_PASSWORD,
                intent.getStringExtra(UserProfileContract.UserTable.COLUMN_PASSWORD));

        OkHttpClient okHttpClient = OkHttpHelper.getClient(getApplicationContext());
        RequestBody requestBody = RequestBody.create(OkHttpHelper.JSON, loginData.toString());

        Request request = new Request.Builder()
                .url(OkHttpHelper.generateUrl(APIConstants.LOGIN_URL))
                .post(requestBody)
                .build();

        try {
            Response response = okHttpClient.newCall(request).execute();
            int responseCode = response.code();

            switch (responseCode) {
                case 200:
                    JSONObject data = new JSONObject(response.body().string());
                    JSONObject buyerLogin = data.getJSONObject(APIConstants.BUYER_LOGIN_KEY);

                    LoginHelper loginHelper = new LoginHelper(this);
                    if (loginHelper.login(buyerLogin)) {
                        sendBroadcast(intent.getIntExtra("TODO", -1), "success", responseCode);
                    } else {
                        sendBroadcast(intent.getIntExtra("TODO", -1), null, responseCode);
                    }
                    break;
                case 401:
                    sendBroadcast(intent.getIntExtra("TODO", -1), getString(R.string.invalid_credentials), responseCode);
                    break;
                case 403:
                    sendBroadcast(intent.getIntExtra("TODO", -1), getString(R.string.unregistered_mobile_number), responseCode);
                    break;
            }
        } catch (Exception e) {
            sendBroadcast(intent.getIntExtra("TODO", -1), e.toString(), 500);
            FirebaseCrash.report(e);
        }
    }

    public void forgotPassword() {
    }

    private void sendBroadcast(int todo, @Nullable String data, int responseCode) {
        Intent intent = new Intent(IntentFilters.LOGIN_SIGNUP_DATA);
        intent.putExtra("TODO", todo);
        intent.putExtra(APIConstants.RESPONSE_CODE, responseCode);
        intent.putExtra(APIConstants.LOGIN_API_DATA, data);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
