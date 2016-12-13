package com.wholdus.www.wholdusbuyerapp.interfaces;

import android.support.annotation.Nullable;

/**
 * Created by aditya on 28/11/16.
 */

public interface ProfileListenerInterface {
    void editPersonalDetails();

    void fragmentCreated(String fragmentName, boolean backEnabled);

    void openProfileFragment();

    void editAddress(@Nullable int addressID, int _ID);

    void openOrderDetails(int orderID);
}
