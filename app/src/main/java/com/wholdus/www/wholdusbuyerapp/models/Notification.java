package com.wholdus.www.wholdusbuyerapp.models;

import android.database.Cursor;

import com.wholdus.www.wholdusbuyerapp.databaseContracts.NotificationContract.NotificationTable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by kaustubh on 11/1/17.
 */

public class Notification {
    private int m_ID;
    private String mNotificationJSON;
    private String mNotificationTitle;
    private String mNotificationBody;
    private String mNotificationType;
    private String mNotificationTime;

    public Notification(Cursor cursor) {
        setDataFromCursor(cursor);
    }

    public static ArrayList<Notification> getNotificationsFromCursor(Cursor cursor) {
        ArrayList<Notification> notifications = new ArrayList<>();
        while (cursor.moveToNext()) {
            notifications.add(new Notification(cursor));
        }
        return notifications;
    }

    public void setDataFromCursor(Cursor cursor){
        m_ID = cursor.getInt(cursor.getColumnIndexOrThrow(NotificationTable._ID));
        mNotificationType = cursor.getString(cursor.getColumnIndexOrThrow(NotificationTable.COLUMN_NOTIFICATION_TYPE));
        mNotificationTime = cursor.getString(cursor.getColumnIndexOrThrow(NotificationTable.COLUMN_NOTIFICATION_TIME));
        mNotificationJSON = cursor.getString(cursor.getColumnIndexOrThrow(NotificationTable.COLUMN_NOTIFICATION_JSON));
        try {
            JSONObject jsonObject = new JSONObject(mNotificationJSON);
            mNotificationTitle = jsonObject.getString("notification_title");
            mNotificationBody = jsonObject.getString("notification_body");
        }catch (JSONException e){
            e.printStackTrace();
            mNotificationTitle = "Wholdus";
            mNotificationTitle = "New notification received";
        }
    }

    public int getID(){return m_ID;}

    public String getNotificationJSON(){return mNotificationJSON;}

    public String getNotificationTitle(){return mNotificationTitle;}

    public String getNotificationBody(){return mNotificationBody;}

    public String getNotificationType(){return mNotificationType;}

    public String getNotificationTime(){return mNotificationTime;}
}
