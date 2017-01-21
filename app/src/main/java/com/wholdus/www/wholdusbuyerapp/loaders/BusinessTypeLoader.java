package com.wholdus.www.wholdusbuyerapp.loaders;

import android.content.Context;

import com.wholdus.www.wholdusbuyerapp.databaseHelpers.UserDBHelper;
import com.wholdus.www.wholdusbuyerapp.models.BusinessTypes;

import java.util.ArrayList;

/**
 * Created by kaustubh on 16/1/17.
 */

public class BusinessTypeLoader extends AbstractLoader<ArrayList<BusinessTypes>> {

    public BusinessTypeLoader(Context context){
        super(context);
    }

    @Override
    public ArrayList<BusinessTypes> loadInBackground() {
        UserDBHelper userDBHelper = new UserDBHelper(getContext());
        return BusinessTypes.initBusinessTypesList(userDBHelper.getBusinessTypes(null));
    }
}
