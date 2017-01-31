package com.wholdus.www.wholdusbuyerapp.interfaces;

import android.view.View;

/**
 * Created by kaustubh on 17/12/16.
 */

public interface HandPickedListenerInterface {

    void fragmentCreated(String title);

    void openProductDetails(int productID);

    void openFilter(boolean open);
}
