package com.wholdus.www.wholdusbuyerapp.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.iid.FirebaseInstanceId;
import com.wholdus.www.wholdusbuyerapp.helperClasses.APIConstants;
import com.wholdus.www.wholdusbuyerapp.helperClasses.OkHttpHelper;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FirebaseNotificationService extends IntentService {

    private static final String INSTANCE_ID_KEY = "instance_id";
    private static final String TOKEN_KEY = "token";

    public FirebaseNotificationService() {
        super("FirebaseNotificationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        sendTokenToServer();
    }

    private void sendTokenToServer() {
        JSONObject data = new JSONObject();
        try {
            data.put(INSTANCE_ID_KEY, FirebaseInstanceId.getInstance().getId());
            data.put(TOKEN_KEY, FirebaseInstanceId.getInstance().getToken());

            OkHttpClient okHttpClient = OkHttpHelper.getClient(getApplicationContext());

            RequestBody requestBody = RequestBody.create(OkHttpHelper.JSON, data.toString());
            Request request = new Request.Builder()
                    .url(OkHttpHelper.generateUrl(APIConstants.FIREBASE_TOKEN_REGISTRATION_URL))
                    .put(requestBody)
                    .build();

            Response response = okHttpClient.newCall(request).execute();
            Log.d(this.getClass().getSimpleName(), response.toString());

            if (!response.isSuccessful())
                throw new IOException("unable to push token to server " + response);
            else {
                response.body().close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            FirebaseCrash.report(e);
        }
    }
}
