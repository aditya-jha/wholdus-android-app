package com.wholdus.www.wholdusbuyerapp.interfaces;

import android.support.annotation.Nullable;

/**
 * Created by kaustubh on 27/12/16.
 */

public interface UserAddressInterface {

    void editAddress(int addressID, int _ID);

    void fragmentCreated(String title, boolean backEnabled);

    void addressClicked(int addressID, int _ID);

    void addressSaved(int addressID);
}
