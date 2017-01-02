package com.wholdus.www.wholdusbuyerapp.databaseContracts;

import android.provider.BaseColumns;

/**
 * Created by aditya on 25/11/16.
 */

public final class UserProfileContract {

    // this class should not be instantiated, hence constructor private
    private UserProfileContract() {
    }

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
        public static final String COLUMN_PASSWORD = "password";

        public static final String COLUMN_STORE_GLOBAL_MARGIN = "store_global_margin";

        public static final String COLUMN_CREATED_AT = "created_at";
        public static final String COLUMN_UPDATED_AT = "updated_at";
    }

    public static final class UserAddressTable implements BaseColumns {
        public static final String TABLE_NAME = "user_address";
        public static final String COLUMN_PINCODE_ID = "pincodeID";
        public static final String COLUMN_ADDRESS_ID = "addressID";
        public static final String COLUMN_ADDRESS_HISTORY_ID = "address_history_ID";
        public static final String COLUMN_PRIORITY = "priority";
        public static final String COLUMN_CITY = "city";
        public static final String COLUMN_STATE = "state";
        public static final String COLUMN_LANDMARK = "landmark";
        public static final String COLUMN_ADDRESS = "address";
        public static final String COLUMN_CONTACT_NUMBER = "contact_number";
        public static final String COLUMN_PINCODE = "pincode";
        public static final String COLUMN_ADDRESS_ALIAS = "alias";

        public static final String COLUMN_SYNCED = "synced";
        public static final String COLUMN_CLIENT_ID = "client_id";

        public static final String COLUMN_CREATED_AT = "created_at";
        public static final String COLUMN_UPDATED_AT = "updated_at";
    }

    public static final class UserInterestsTable implements BaseColumns {
        public static final String TABLE_NAME = "buyer_interests";
        public static final String COLUMN_BUYER_INTEREST_ID = "buyerinterestID";
        public static final String COLUMN_CATEGORY_ID = "categoryID";
        public static final String COLUMN_CATEGORY_NAME = "display_name";
        public static final String COLUMN_PRICE_FILTER_APPLIED = "price_filter_applied";
        public static final String COLUMN_MIN_PRICE_PER_UNIT = "min_price_per_unit";
        public static final String COLUMN_FABRIC_FILTER_TEXT = "fabric_filter_text";
        public static final String COLUMN_MAX_PRICE_PER_UNIT = "max_price_per_unit";

        public static final String COLUMN_CREATED_AT = "created_at";
        public static final String COLUMN_UPDATED_AT = "updated_at";
    }

    public static final class BusinessTypesTable implements BaseColumns {
        public static final String TABLE_NAME = "business_types";
        public static final String COLUMN_BUSINESS_TYPE = "business_type";
        public static final String COLUMN_BUSINESS_TYPE_ID = "businesstypeID";
        public static final String COLUMN_DESCRIPTION = "description";
    }
}
