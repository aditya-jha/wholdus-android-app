package com.wholdus.www.wholdusbuyerapp.interfaces;

/**
 * Created by kaustubh on 16/1/17.
 */

public interface OnBoardingListenerInterface {
    void fragmentCreated(String title, boolean backEnabled);

    void businessTypeSaved();

    void whatsappNumberSaved();
}
