package com.wholdus.www.wholdusbuyerapp.databaseContracts;

import android.provider.BaseColumns;

/**
 * Created by aditya on 25/11/16.
 */

public final class UserProfileContract {

    // this class should not be instantiated, hence constructor private
    private UserProfileContract() {}

    public static final class UserTable implements BaseColumns {
        public static final String TABLE_NAME = "user";
        public static final String COLUMN_BUYER_ID = "buyerID";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_EMAIL = "email";
        public static final String COLUMN_MOBILE_NUMBER = "mobile_number";
        public static final String COLUMN_WHATSAPP_NUMBER = "whatsapp_number";
        public static final String COLUMN_COMPANY_NAME = "company_name";
        public static final String COLUMN_BUSINESS_TYPE = "business_type";
        public static final String COLUMN_GENDER = "gender";
    }

    public static final class UserAddressTable implements BaseColumns {
        public static final String TABLE_NAME = "user_address";
        public static final String COLUMN_BUYER_ID = "buyerID";
        public static final String COLUMN_PINCODE_ID = "pincodeID";
        public static final String COLUMN_ADDRESS_ID = "addressID";
        public static final String COLUMN_PRIORITY = "priority";
        public static final String COLUMN_CITY = "city";
        public static final String COLUMN_STATE = "state";
        public static final String COLUMN_LANDMARK = "landmark";
        public static final String COLUMN_ADDRESS = "address";
        public static final String COLUMN_CONTACT_NUMBER = "contact_number";
        public static final String COLUMN_PINCODE = "pincode";
    }
}
