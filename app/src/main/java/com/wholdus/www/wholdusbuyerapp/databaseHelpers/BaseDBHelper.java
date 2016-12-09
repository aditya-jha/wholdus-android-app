package com.wholdus.www.wholdusbuyerapp.databaseHelpers;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by aditya on 9/12/16.
 */

public class BaseDBHelper {

    protected DatabaseHelper mDatabaseHelper;
    protected Context mContext;

    public BaseDBHelper() {}

    public BaseDBHelper(Context context) {
        mDatabaseHelper = DatabaseHelper.getInstance(context);
        mContext = context;
    }

    /*
        Helper function to open & close database through DBHelper class and execute raw query
     */
    protected Cursor getCursor(String query) {
        SQLiteDatabase db = mDatabaseHelper.openDatabase();
        Cursor cursor = db.rawQuery(query, null);
        mDatabaseHelper.closeDatabase();
        return cursor;
    }
}
