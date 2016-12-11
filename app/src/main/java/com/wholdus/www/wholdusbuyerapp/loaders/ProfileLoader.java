package com.wholdus.www.wholdusbuyerapp.loaders;

import android.content.Context;

import com.wholdus.www.wholdusbuyerapp.databaseHelpers.UserDBHelper;
import com.wholdus.www.wholdusbuyerapp.helperClasses.GlobalAccessHelper;
import com.wholdus.www.wholdusbuyerapp.models.Buyer;

/**
 * Created by aditya on 7/12/16.
 */

public class ProfileLoader extends AbstractLoader<Buyer> {

    private boolean mLoadAddress, mLoadInterest, mLoadProfile;

    public ProfileLoader(Context context, boolean profile, boolean address, boolean interest) {
        super(context);
        mLoadProfile = profile;
        mLoadAddress = address;
        mLoadInterest = interest;
    }

    @Override
    public Buyer loadInBackground() {
        String buyerID = GlobalAccessHelper.getBuyerID(getContext());

        // fetch data from DB
        UserDBHelper userDBHelper = new UserDBHelper(getContext());
        Buyer buyer = new Buyer();

        if (mLoadProfile) {
            buyer.setBuyerData(userDBHelper.getUserData(buyerID));
        }
        if (mLoadAddress) {
            buyer.setAddressData(userDBHelper.getUserAddress(null, -1, null));
        }
        if (mLoadInterest) {
            buyer.setInterestData(userDBHelper.getUserInterests(null, -1));
        }

        return buyer;
    }
}
