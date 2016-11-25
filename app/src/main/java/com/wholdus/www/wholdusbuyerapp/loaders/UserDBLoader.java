package com.wholdus.www.wholdusbuyerapp.loaders;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.CursorLoader;

import com.wholdus.www.wholdusbuyerapp.databaseHelpers.UserDBHelper;

/**
 * Created by aditya on 25/11/16.
 */

public class UserDBLoader extends CursorLoader {

    private Context mContext;

    public UserDBLoader(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public Cursor loadInBackground() {
        UserDBHelper userDBHelper = new UserDBHelper(mContext);
        return userDBHelper.getUserData();
    }
}
