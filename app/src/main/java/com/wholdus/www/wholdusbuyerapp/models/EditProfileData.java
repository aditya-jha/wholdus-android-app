package com.wholdus.www.wholdusbuyerapp.models;

import java.util.ArrayList;

/**
 * Created by aditya on 8/12/16.
 */

public class EditProfileData {
    private Buyer mBuyer;
    private ArrayList<BusinessTypes> mBusinessType;

    public EditProfileData(Buyer buyer, ArrayList<BusinessTypes> businessTypes) {
        mBusinessType = businessTypes;
        mBuyer = buyer;
    }

    public Buyer getBuyer() {
        return mBuyer;
    }

    public ArrayList<BusinessTypes> getBusinessType() {
        return mBusinessType;
    }
}
