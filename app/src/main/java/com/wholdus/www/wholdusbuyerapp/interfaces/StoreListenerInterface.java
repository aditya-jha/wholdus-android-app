package com.wholdus.www.wholdusbuyerapp.interfaces;

/**
 * Created by aditya on 23/12/16.
 */

public interface StoreListenerInterface {

    void fragmentCreated(String title, boolean backEnabled);

    void openEditStoreFragment();
}
