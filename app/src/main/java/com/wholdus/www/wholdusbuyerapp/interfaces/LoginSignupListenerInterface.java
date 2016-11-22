package com.wholdus.www.wholdusbuyerapp.interfaces;

/**
 * Created by aditya on 17/11/16.
 */

public interface LoginSignupListenerInterface {
    void singupClicked();

    void loginClicked();

    void forgotPasswordClicked(String mobileNumber);

    void permissionsBottomSheet(boolean show);

    void requestReceiveSMSPermission();

    void loginSuccess();

    void hideSoftKeyboard();
}
