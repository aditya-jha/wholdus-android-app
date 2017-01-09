package com.wholdus.www.wholdusbuyerapp.loaders;

import android.content.Context;

import com.wholdus.www.wholdusbuyerapp.databaseHelpers.UserDBHelper;
import com.wholdus.www.wholdusbuyerapp.models.BuyerAddress;

import java.util.ArrayList;

/**
 * Created by kaustubh on 27/12/16.
 */

public class BuyerAddressLoader extends AbstractLoader<ArrayList<BuyerAddress>> {

    private int mAddressID;
    private int m_ID;

    public BuyerAddressLoader(Context context, int addressID, int _ID){
        super(context);
        mAddressID = addressID;
        m_ID = _ID;
    }

    @Override
    public ArrayList<BuyerAddress> loadInBackground() {
        UserDBHelper userDBHelper = new UserDBHelper(getContext());
        return BuyerAddress.getBuyerAddressesFromCursor(userDBHelper.getUserAddress(mAddressID, m_ID, 0,-1, null));
    }
}
