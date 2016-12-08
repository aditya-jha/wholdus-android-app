package com.wholdus.www.wholdusbuyerapp.loaders;

import android.content.Context;

import com.wholdus.www.wholdusbuyerapp.databaseHelpers.UserDBHelper;
import com.wholdus.www.wholdusbuyerapp.helperClasses.GlobalAccessHelper;
import com.wholdus.www.wholdusbuyerapp.models.BusinessTypes;
import com.wholdus.www.wholdusbuyerapp.models.Buyer;
import com.wholdus.www.wholdusbuyerapp.models.EditProfileData;

/**
 * Created by aditya on 8/12/16.
 */

public class EditProfileFragmentLoader extends AbstractLoader<EditProfileData> {

    public EditProfileFragmentLoader(Context context) {
        super(context);
    }

    @Override
    public EditProfileData loadInBackground() {
        String buyerID = GlobalAccessHelper.getBuyerID(getContext());

        // fetch data from DB
        UserDBHelper userDBHelper = new UserDBHelper(getContext());

        return new EditProfileData(new Buyer(userDBHelper.getUserData(buyerID)),
                BusinessTypes.initBusinessTypesList(userDBHelper.getBusinessTypes(null)));
    }
}
