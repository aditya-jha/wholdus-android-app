package com.wholdus.www.wholdusbuyerapp.interfaces;

import android.support.annotation.Nullable;
import android.view.View;

/**
 * Created by aditya on 17/11/16.
 */

public interface LoginSignupListenerInterface {
    void singupClicked(@Nullable String mobileNumber);

    void loginClicked();

    void forgotPasswordClicked(String mobileNumber);

    void permissionsBottomSheet(boolean show);

    void requestReceiveSMSPermission();

    void loginSuccess();

    void hideSoftKeyboard(View view);
}
