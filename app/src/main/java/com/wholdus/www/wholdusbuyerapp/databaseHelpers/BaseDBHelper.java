package com.wholdus.www.wholdusbuyerapp.databaseHelpers;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;
import android.text.TextUtils;

/**
 * Created by aditya on 9/12/16.
 */

public class BaseDBHelper {

    protected DatabaseHelper mDatabaseHelper;
    protected Context mContext;

    public BaseDBHelper() {
    }

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

    protected String getColumnNamesString(@Nullable String[] columns){
        String columnNames;
        if (columns != null && columns.length != 0) {
            columnNames = TextUtils.join(", ", columns);
        } else {
            columnNames = "*";
        }

        return columnNames;
    }

    protected static String whereClauseHelper(boolean whereApplied){
        return whereApplied ? " AND " : " WHERE ";
    }

    /*
     * Cursor query (String table,
                String[] columns,
                String selection,
                String[] selectionArgs,
                String groupBy,
                String having,
                String orderBy,
                String limit)

    protected  Cursor getCursor(String table, @Nullable String[] cols, @Nullable String selection,
                                @Nullable String[] selectionArgs, @Nullable String groupBy,
                                @Nullable String having, @Nullable String orderBy,
                                int limit, int offset) {
        SQLiteDatabase db = mDatabaseHelper.openDatabase();
        String limitOffset = offset + "," + limit;
        Cursor cursor = db.query(table, cols, selection, selectionArgs, groupBy, having, orderBy, limitOffset);
        mDatabaseHelper.closeDatabase();

        return cursor;
    }
    */
}
