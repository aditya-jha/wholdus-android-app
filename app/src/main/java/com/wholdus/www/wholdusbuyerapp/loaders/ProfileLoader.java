package com.wholdus.www.wholdusbuyerapp.loaders;

import android.content.Context;
import android.database.Cursor;

import com.wholdus.www.wholdusbuyerapp.databaseHelpers.UserDBHelper;
import com.wholdus.www.wholdusbuyerapp.helperClasses.GlobalAccessHelper;
import com.wholdus.www.wholdusbuyerapp.models.Buyer;

/**
 * Created by aditya on 7/12/16.
 */

public class ProfileLoader extends AbstractLoader<Buyer> {

    public ProfileLoader(Context context) {
        super(context);
    }

    @Override
    public Buyer loadInBackground() {
        String buyerID = GlobalAccessHelper.getBuyerID(getContext());

        // fetch data from DB
        UserDBHelper userDBHelper = new UserDBHelper(getContext());
        Buyer buyer = new Buyer();

        buyer.setBuyerData(userDBHelper.getUserData(buyerID));
        buyer.setAddressData(userDBHelper.getUserAddress(buyerID, null, -1));

        return buyer;
    }
}
