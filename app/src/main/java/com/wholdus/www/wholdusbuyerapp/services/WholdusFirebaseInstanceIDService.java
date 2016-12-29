package com.wholdus.www.wholdusbuyerapp.services;

import android.content.Intent;

import com.google.android.gms.iid.InstanceID;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by aditya on 28/12/16.
 */

public class WholdusFirebaseInstanceIDService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        startService(new Intent(this, FirebaseNotificationService.class));
    }
}
