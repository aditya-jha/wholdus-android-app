package com.wholdus.www.wholdusbuyerapp.interfaces;

import com.wholdus.www.wholdusbuyerapp.models.Cart;

/**
 * Created by kaustubh on 26/12/16.
 */

public interface CartListenerInterface {
    void fragmentCreated(String title, boolean backEnabled);

    void setCart(Cart cart);

    void disableProgressBar();

    void enableProgressBar();

    void addressSelected(int addressID);

    void openSelectAddress();

    void setPaymentMethod(int paymentMethod);
}
