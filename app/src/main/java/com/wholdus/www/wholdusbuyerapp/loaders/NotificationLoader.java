package com.wholdus.www.wholdusbuyerapp.loaders;

import android.content.Context;
import android.support.annotation.Nullable;

import com.wholdus.www.wholdusbuyerapp.databaseHelpers.NotificationDBHelper;
import com.wholdus.www.wholdusbuyerapp.models.Notification;

import java.util.ArrayList;

/**
 * Created by kaustubh on 11/1/17.
 */

public class NotificationLoader extends AbstractLoader<ArrayList<Notification>> {

    private Context mContext;
    private int m_ID;
    private String mType;

    public NotificationLoader(Context context, int id, @Nullable String type){
        super(context);
        mContext = context;
        m_ID = id;
        mType = type;
    }
    @Override
    public ArrayList<Notification>loadInBackground() {
        NotificationDBHelper notificationDBHelper = new NotificationDBHelper(getContext());
        return Notification.getNotificationsFromCursor(notificationDBHelper.getNotificationData(m_ID, mType, null));
    }
}
