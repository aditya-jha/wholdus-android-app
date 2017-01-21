package com.wholdus.www.wholdusbuyerapp.interfaces;

/**
 * Created by aditya on 10/12/16.
 */

public interface HomeListenerInterface {

    void openCategory(int categoryID);

    void fragmentCreated(String fragmentName, boolean backEnabled);

    void helpButtonClicked();
}
