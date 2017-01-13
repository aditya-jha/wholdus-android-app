package com.wholdus.www.wholdusbuyerapp.databaseHelpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;

import com.wholdus.www.wholdusbuyerapp.databaseContracts.CartContract;
import com.wholdus.www.wholdusbuyerapp.databaseContracts.NotificationContract.NotificationTable;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Date;
import java.util.jar.JarException;


/**
 * Created by kaustubh on 11/1/17.
 */

public class NotificationDBHelper extends BaseDBHelper {

    public NotificationDBHelper(Context context) {
        super(context);
    }

    public Cursor getNotificationData(int id, @Nullable String type, @Nullable String[] columns){
        String columnNames = getColumnNamesString(columns);
        String query = "SELECT " + columnNames + " FROM " + NotificationTable.TABLE_NAME;
        boolean whereApplied = false;
        if (id != -1){
            query += " WHERE " + NotificationTable._ID + " = " + id;
            whereApplied = true;
        }
        if (type != null && !type.equals("")){
            query += whereClauseHelper(whereApplied) + NotificationTable.COLUMN_NOTIFICATION_TYPE + " = " + type;
        }
        query += " ORDER BY " + NotificationTable._ID + " DESC ";
        return getCursor(query);
    }

    public void saveNotificationData(JSONObject notification) throws JSONException{
        SQLiteDatabase db = mDatabaseHelper.openDatabase();
        ContentValues values = getNotificationContentValues(notification);
        db.insert(NotificationTable.TABLE_NAME, null, values);
        mDatabaseHelper.closeDatabase();
    }

    private ContentValues getNotificationContentValues(JSONObject notification) throws JSONException{
        ContentValues contentValues = new ContentValues();
        contentValues.put(NotificationTable.COLUMN_NOTIFICATION_JSON, notification.getString(NotificationTable.COLUMN_NOTIFICATION_JSON));
        contentValues.put(NotificationTable.COLUMN_NOTIFICATION_TYPE, notification.getString(NotificationTable.COLUMN_NOTIFICATION_TYPE));
        contentValues.put(NotificationTable.COLUMN_NOTIFICATION_TIME, DateFormat.getDateTimeInstance().format(new Date()));
        return contentValues;
    }

    private void deleteNotification(@Nullable String type, int id){
        SQLiteDatabase db = mDatabaseHelper.openDatabase();
        String selection = null;
        boolean whereApplied = false;
        if (id != -1){
            selection = NotificationTable._ID + " = " + id;
            whereApplied = true;
        }
        if (type != null && !type.equals("")){
            selection += whereApplied ? " AND " : " ";
            selection += NotificationTable.COLUMN_NOTIFICATION_TYPE + " = " + type;
        }
        db.delete(NotificationTable.TABLE_NAME,selection,null);
    }
}
