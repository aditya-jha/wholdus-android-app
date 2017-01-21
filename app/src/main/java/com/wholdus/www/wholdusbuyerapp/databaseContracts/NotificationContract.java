package com.wholdus.www.wholdusbuyerapp.databaseContracts;

import android.provider.BaseColumns;

/**
 * Created by kaustubh on 11/1/17.
 */

public class NotificationContract {

    private NotificationContract() {}

    public static final class NotificationTable implements BaseColumns {
        public static final String TABLE_NAME = "notification";
        public static final String COLUMN_NOTIFICATION_JSON = "notification_json";
        public static final String COLUMN_NOTIFICATION_TYPE = "notification_type";
        public static final String COLUMN_NOTIFICATION_TIME = "notification_time";
    }
}
