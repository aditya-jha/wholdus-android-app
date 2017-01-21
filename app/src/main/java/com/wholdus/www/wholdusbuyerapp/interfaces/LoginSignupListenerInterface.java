package com.wholdus.www.wholdusbuyerapp.interfaces;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

/**
 * Created by aditya on 17/11/16.
 */

public interface LoginSignupListenerInterface {
    void singupClicked(@Nullable String mobileNumber);

    void loginClicked(@Nullable String mobileNumber);

    void forgotPasswordClicked(@Nullable String mobileNumber);

    void resetPassword(@NonNull Bundle args);

    void openOTPFragment(@NonNull Bundle bundle);

    void loginSuccess(boolean registered);

    void hideSoftKeyboard(View view);
}
