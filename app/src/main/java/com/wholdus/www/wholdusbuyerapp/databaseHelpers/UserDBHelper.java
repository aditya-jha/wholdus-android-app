package com.wholdus.www.wholdusbuyerapp.databaseHelpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.wholdus.www.wholdusbuyerapp.databaseContracts.UserProfileContract.BusinessTypesTable;
import com.wholdus.www.wholdusbuyerapp.databaseContracts.UserProfileContract.UserAddressTable;
import com.wholdus.www.wholdusbuyerapp.databaseContracts.UserProfileContract.UserInterestsTable;
import com.wholdus.www.wholdusbuyerapp.databaseContracts.UserProfileContract.UserTable;
import com.wholdus.www.wholdusbuyerapp.databaseContracts.OrdersContract.OrdersTable;
import com.wholdus.www.wholdusbuyerapp.helperClasses.GlobalAccessHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aditya on 25/11/16.
 */

public class UserDBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "users";

    private static final String TEXT_TYPE = " TEXT";
    private static final String INT_TYPE = " INTEGER";
    private static final String REAL_TYPE = " REAL ";
    private static final String COMMA_SEP = ",";
    private static final String NOT_NULL = " NOT NULL";

    private static final String SQL_CREATE_USER_TABLE =
            "CREATE TABLE " + UserTable.TABLE_NAME + " (" +
                    UserTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + COMMA_SEP +
                    UserTable.COLUMN_BUYER_ID + TEXT_TYPE + NOT_NULL + COMMA_SEP +
                    UserTable.COLUMN_NAME + TEXT_TYPE + COMMA_SEP +
                    UserTable.COLUMN_EMAIL + TEXT_TYPE + COMMA_SEP +
                    UserTable.COLUMN_MOBILE_NUMBER + TEXT_TYPE + NOT_NULL + COMMA_SEP +
                    UserTable.COLUMN_WHATSAPP_NUMBER + TEXT_TYPE + COMMA_SEP +
                    UserTable.COLUMN_COMPANY_NAME + TEXT_TYPE + COMMA_SEP +
                    UserTable.COLUMN_BUSINESS_TYPE + TEXT_TYPE + COMMA_SEP +
                    UserTable.COLUMN_GENDER + TEXT_TYPE + " )";

    private static final String SQL_DROP_USER_TABLE =
            "DROP TABLE IF EXISTS " + UserTable.TABLE_NAME;

    private static final String SQL_CREATE_USER_ADDRESS_TABLE =
            "CREATE TABLE " + UserAddressTable.TABLE_NAME + " (" +
                    UserAddressTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + COMMA_SEP +
                    UserAddressTable.COLUMN_BUYER_ID + TEXT_TYPE + COMMA_SEP +
                    UserAddressTable.COLUMN_PINCODE_ID + TEXT_TYPE + COMMA_SEP +
                    UserAddressTable.COLUMN_ADDRESS_ID + TEXT_TYPE + COMMA_SEP +
                    UserAddressTable.COLUMN_PRIORITY + TEXT_TYPE + COMMA_SEP +
                    UserAddressTable.COLUMN_CITY + TEXT_TYPE + COMMA_SEP +
                    UserAddressTable.COLUMN_STATE + TEXT_TYPE + COMMA_SEP +
                    UserAddressTable.COLUMN_LANDMARK + TEXT_TYPE + COMMA_SEP +
                    UserAddressTable.COLUMN_ADDRESS + TEXT_TYPE + COMMA_SEP +
                    UserAddressTable.COLUMN_CONTACT_NUMBER + TEXT_TYPE + COMMA_SEP +
                    UserAddressTable.COLUMN_ADDRESS_ALIAS + TEXT_TYPE + COMMA_SEP +
                    UserAddressTable.COLUMN_PINCODE + TEXT_TYPE + " )";

    private static final String SQL_DROP_USER_ADDRESS_TABLE =
            "DROP TABLE IF EXISTS " + UserAddressTable.TABLE_NAME;

    private static final String SQL_CREATE_BUSINESS_TYPES_TABLE =
            "CREATE TABLE " + BusinessTypesTable.TABLE_NAME + " (" +
                    BusinessTypesTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + COMMA_SEP +
                    BusinessTypesTable.COLUMN_BUSINESS_TYPE + TEXT_TYPE + COMMA_SEP +
                    BusinessTypesTable.COLUMN_BUSINESS_TYPE_ID + TEXT_TYPE + COMMA_SEP +
                    BusinessTypesTable.COLUMN_DESCRIPTION + TEXT_TYPE + " )";

    private static final String SQL_DROP_BUSINESS_TYPES_TABLE =
            "DROP TABLE IF EXISTS " + BusinessTypesTable.TABLE_NAME;

    private static final String SQL_CREATE_USER_INTERESTS_TABLE =
            "CREATE TABLE " + UserInterestsTable.TABLE_NAME + " (" +
                    UserInterestsTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + COMMA_SEP +
                    UserInterestsTable.COLUMN_BUYER_ID + TEXT_TYPE + COMMA_SEP +
                    UserInterestsTable.COLUMN_BUYER_INTEREST_ID + TEXT_TYPE + COMMA_SEP +
                    UserInterestsTable.COLUMN_CATEGORY_ID + TEXT_TYPE + COMMA_SEP +
                    UserInterestsTable.COLUMN_CATEGORY_NAME + TEXT_TYPE + COMMA_SEP +
                    UserInterestsTable.COLUMN_MIN_PRICE_PER_UNIT + TEXT_TYPE + COMMA_SEP +
                    UserInterestsTable.COLUMN_MAX_PRICE_PER_UNIT + TEXT_TYPE + COMMA_SEP +
                    UserInterestsTable.COLUMN_FABRIC_FILTER_TEXT + TEXT_TYPE + COMMA_SEP +
                    UserInterestsTable.COLUMN_PRICE_FILTER_APPLIED + INT_TYPE + " )";

    private static final String SQL_DROP_USER_INTERESTS_TABLE =
            "DROP TABLE IF EXISTS " + BusinessTypesTable.TABLE_NAME;

    private static final String SQL_CREATE_ORDERS_TABLE =
            "CREATE TABLE " + OrdersTable.TABLE_NAME + " (" +
                    OrdersTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + COMMA_SEP +
                    OrdersTable.COLUMN_ORDER_ID + TEXT_TYPE + COMMA_SEP +
                    OrdersTable.COLUMN_DISPLAY_NUMBER + TEXT_TYPE + COMMA_SEP +
                    OrdersTable.COLUMN_BUYER_ADDRESS_ID + TEXT_TYPE + COMMA_SEP +
                    OrdersTable.COLUMN_PRODUCT_COUNT + INT_TYPE + COMMA_SEP +
                    OrdersTable.COLUMN_RETAIL_PRICE + REAL_TYPE + COMMA_SEP +
                    OrdersTable.COLUMN_CALCULATED_PRICE + REAL_TYPE + COMMA_SEP +
                    OrdersTable.COLUMN_EDITED_PRICE + REAL_TYPE + COMMA_SEP +
                    OrdersTable.COLUMN_SHIPPING_CHARGE + REAL_TYPE + COMMA_SEP +
                    OrdersTable.COLUMN_COD_CHARGE + REAL_TYPE + COMMA_SEP +
                    OrdersTable.COLUMN_FINAL_PRICE + REAL_TYPE + COMMA_SEP +
                    OrdersTable.COLUMN_ORDER_STATUS_VALUE + INT_TYPE + COMMA_SEP +
                    OrdersTable.COLUMN_ORDER_STATUS_DISPLAY + TEXT_TYPE + COMMA_SEP +
                    OrdersTable.COLUMN_PAYMENT_STATUS_VALUE + INT_TYPE + COMMA_SEP +
                    OrdersTable.COLUMN_PAYMENT_STATUS_DISPLAY + TEXT_TYPE + COMMA_SEP +
                    OrdersTable.CREATED_AT + TEXT_TYPE + COMMA_SEP +
                    OrdersTable.COLUMN_REMARKS + TEXT_TYPE + " )";

    private static final String SQL_DROP_ORDERS_TABLE =
            "DROP TABLE IF EXISTS " + OrdersTable.TABLE_NAME;

    public UserDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_USER_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_USER_ADDRESS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_BUSINESS_TYPES_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_USER_INTERESTS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_ORDERS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL(SQL_DROP_USER_TABLE);
        sqLiteDatabase.execSQL(SQL_DROP_USER_ADDRESS_TABLE);
        sqLiteDatabase.execSQL(SQL_DROP_BUSINESS_TYPES_TABLE);
        sqLiteDatabase.execSQL(SQL_DROP_USER_INTERESTS_TABLE);
        sqLiteDatabase.execSQL(SQL_DROP_ORDERS_TABLE);

        onCreate(sqLiteDatabase);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public Cursor getUserData(String buyerID) {
        String query = "SELECT * FROM " + UserTable.TABLE_NAME + " WHERE " + UserTable.COLUMN_BUYER_ID + "= " + buyerID + ";";

        return getReadableDatabase().rawQuery(query, null);
    }

    public Cursor getOrdersData(@Nullable List<Integer> orderStatusValues, @Nullable String orderID) {
        String query = "SELECT * FROM " + OrdersTable.TABLE_NAME;
        boolean whereApplied = false;
        if (orderID!= null && !TextUtils.isEmpty(orderID)){
            query += "WHERE " + OrdersTable.COLUMN_ORDER_ID + " = " + orderID;
            whereApplied = true;
        }
        if (!orderStatusValues.isEmpty()){
            if (whereApplied == true){
                query += " AND ";
            }
            else {
                query += " WHERE ";
            }
            query += OrdersTable.COLUMN_ORDER_STATUS_VALUE + " IN " + TextUtils.join(", ", orderStatusValues);
        }

        return getReadableDatabase().rawQuery(query, null);
    }

    public Cursor getUserAddress(String buyerID, @Nullable String addressID, int _ID) {
        String query = "SELECT * FROM " + UserAddressTable.TABLE_NAME + " WHERE " + UserAddressTable.COLUMN_BUYER_ID + " = " + buyerID;
        if (addressID != null && !TextUtils.isEmpty(addressID)) {
            query += " AND " + UserAddressTable.COLUMN_ADDRESS_ID + " = " + addressID;
        } else if (_ID != -1) {
            query += " AND " + UserAddressTable._ID + " = " + _ID;
        }
        return getReadableDatabase().rawQuery(query, null);
    }

    public Cursor getUserInterests(String buyerID, @Nullable String buyerInterestID, int _ID) {
        String query = "SELECT * FROM " + UserInterestsTable.TABLE_NAME + " WHERE " +
                UserInterestsTable.COLUMN_BUYER_ID + "=" + buyerID;
        if (buyerInterestID != null) {
            query += " AND " + UserInterestsTable.COLUMN_BUYER_INTEREST_ID + "=" + buyerInterestID;
        } else if (_ID != -1) {
            query += " AND " + UserInterestsTable._ID + "=" + _ID;
        }
        return getReadableDatabase().rawQuery(query, null);
    }

    public Cursor getBusinessTypes(@Nullable String businessTypeID) {
        String query = "SELECT * FROM " + BusinessTypesTable.TABLE_NAME;
        if (businessTypeID != null) {
            query += " WHERE " + BusinessTypesTable.COLUMN_BUSINESS_TYPE_ID + " = " + businessTypeID;
        }
        return getReadableDatabase().rawQuery(query, null);
    }

    public long updateUserData(JSONObject data) throws JSONException {

        ContentValues user = new ContentValues();
        user.put(UserTable.COLUMN_NAME, data.getString(UserTable.COLUMN_NAME));
        user.put(UserTable.COLUMN_COMPANY_NAME, data.getString(UserTable.COLUMN_COMPANY_NAME));
        user.put(UserTable.COLUMN_EMAIL, data.getString(UserTable.COLUMN_EMAIL));
        user.put(UserTable.COLUMN_MOBILE_NUMBER, data.getString(UserTable.COLUMN_MOBILE_NUMBER));
        user.put(UserTable.COLUMN_WHATSAPP_NUMBER, data.getString(UserTable.COLUMN_WHATSAPP_NUMBER));
        user.put(UserTable.COLUMN_GENDER, data.getString(UserTable.COLUMN_GENDER));

        String businessType = data.getJSONObject("details").getJSONObject("buyer_type").getString(UserTable.COLUMN_BUSINESS_TYPE);
        user.put(UserTable.COLUMN_BUSINESS_TYPE, businessType);

        String buyerID = data.getString(UserTable.COLUMN_BUYER_ID);
        long rows;
        SQLiteDatabase db = getWritableDatabase();

        if (getUserData(buyerID).getCount() == 0) {
            user.put(UserTable.COLUMN_BUYER_ID, buyerID);
            rows = db.insert(UserTable.TABLE_NAME, null, user);
        } else {
            rows = db.update(
                    UserTable.TABLE_NAME,
                    user,
                    UserTable.COLUMN_BUYER_ID + "=" + buyerID,
                    null
            );
        }
        db.close();
        return rows;
    }

    public long updateUserData(String buyerID, ContentValues values) {
        SQLiteDatabase db = getWritableDatabase();

        long rows = db.update(
                UserTable.TABLE_NAME,
                values,
                UserTable.COLUMN_BUYER_ID + "=" + buyerID,
                null
        );

        db.close();
        return rows;
    }

    public long updateUserAddressData(JSONObject data) throws JSONException {
        SQLiteDatabase db = getWritableDatabase();

        JSONArray address = new JSONArray();

        try {
            address = data.getJSONArray("address");
        } catch (Exception e) {
            address.put(0, data);
        }

        String buyerID = data.getString(UserAddressTable.COLUMN_BUYER_ID);

        for (int i = 0; i < address.length(); i++) {
            JSONObject currAddress = address.getJSONObject(i);
            ContentValues values = new ContentValues();

            String addressID = currAddress.getString(UserAddressTable.COLUMN_ADDRESS_ID);
            int _ID = -1;
            if (currAddress.has(UserAddressTable._ID)) {
                _ID = currAddress.getInt(UserAddressTable._ID);
            }

            values.put(UserAddressTable.COLUMN_BUYER_ID, buyerID);
            values.put(UserAddressTable.COLUMN_ADDRESS, currAddress.getString(UserAddressTable.COLUMN_ADDRESS));
            //values.put(UserAddressTable.COLUMN_ADDRESS_ALIAS, currAddress.getString(UserAddressTable.COLUMN_ADDRESS_ALIAS));
            values.put(UserAddressTable.COLUMN_ADDRESS_ALIAS, "Home");
            values.put(UserAddressTable.COLUMN_ADDRESS_ID, addressID);
            values.put(UserAddressTable.COLUMN_CITY, currAddress.getString(UserAddressTable.COLUMN_CITY));
            values.put(UserAddressTable.COLUMN_CONTACT_NUMBER, currAddress.getString(UserAddressTable.COLUMN_CONTACT_NUMBER));
            values.put(UserAddressTable.COLUMN_LANDMARK, currAddress.getString(UserAddressTable.COLUMN_LANDMARK));
            values.put(UserAddressTable.COLUMN_PINCODE_ID, currAddress.getString(UserAddressTable.COLUMN_PINCODE_ID));
            values.put(UserAddressTable.COLUMN_PINCODE, currAddress.getString(UserAddressTable.COLUMN_PINCODE));
            values.put(UserAddressTable.COLUMN_STATE, currAddress.getString(UserAddressTable.COLUMN_STATE));
            values.put(UserAddressTable.COLUMN_PRIORITY, currAddress.getString(UserAddressTable.COLUMN_PRIORITY));

            if ((TextUtils.isEmpty(addressID) && _ID == -1) || getUserAddress(buyerID, addressID, -1).getCount() == 0) {
                // insert
                db.insert(UserAddressTable.TABLE_NAME, null, values);
            } else {
                String selection = UserAddressTable.COLUMN_BUYER_ID + "=" + buyerID;
                if (_ID != -1) {
                    selection += " AND " + UserAddressTable._ID + "=" + _ID;
                } else {
                    selection += " AND " + UserAddressTable.COLUMN_ADDRESS_ID + "=" + addressID;
                }
                db.update(
                        UserAddressTable.TABLE_NAME,
                        values,
                        selection,
                        null
                );
            }
        }

        db.close();
        return 1;
    }

    public int updateBusinessTypesData(JSONObject data) throws JSONException {
        JSONArray businessTypes = data.getJSONArray(BusinessTypesTable.TABLE_NAME);
        SQLiteDatabase db = getWritableDatabase();

        for (int i = 0; i < businessTypes.length(); i++) {
            JSONObject bt = businessTypes.getJSONObject(i);
            ContentValues values = new ContentValues();
            String businessTypeID = bt.getString(BusinessTypesTable.COLUMN_BUSINESS_TYPE_ID);
            values.put(BusinessTypesTable.COLUMN_BUSINESS_TYPE_ID, businessTypeID);
            values.put(BusinessTypesTable.COLUMN_BUSINESS_TYPE, bt.getString(BusinessTypesTable.COLUMN_BUSINESS_TYPE));
            values.put(BusinessTypesTable.COLUMN_DESCRIPTION, bt.getString(BusinessTypesTable.COLUMN_DESCRIPTION));

            if (getBusinessTypes(businessTypeID).getCount() == 0) { // insert
                db.insert(BusinessTypesTable.TABLE_NAME, null, values);
            } else { // update
                db.update(BusinessTypesTable.TABLE_NAME,
                        values,
                        BusinessTypesTable.COLUMN_BUSINESS_TYPE_ID + "=" + businessTypeID,
                        null);
            }
        }

        db.close();
        return businessTypes.length();
    }

    public int updateUserInterestsData(JSONObject data) throws JSONException {
        JSONArray interests = data.getJSONArray(UserInterestsTable.TABLE_NAME);
        String buyerID = data.getString(UserInterestsTable.COLUMN_BUYER_ID);

        SQLiteDatabase db = getWritableDatabase();

        for (int i = 0; i < interests.length(); i++) {
            JSONObject currInterest = interests.getJSONObject(i);
            ContentValues values = new ContentValues();

            JSONObject category = currInterest.getJSONObject("category");
            String buyerInterestID = currInterest.getString(UserInterestsTable.COLUMN_BUYER_INTEREST_ID);

            int priceFilterApplied = GlobalAccessHelper.getIntFromBooleanString(currInterest.getString(
                    UserInterestsTable.COLUMN_PRICE_FILTER_APPLIED));
            values.put(UserInterestsTable.COLUMN_BUYER_ID, buyerID);
            values.put(UserInterestsTable.COLUMN_BUYER_INTEREST_ID, buyerInterestID);
            values.put(UserInterestsTable.COLUMN_CATEGORY_ID, category.getString(UserInterestsTable.COLUMN_CATEGORY_ID));
            values.put(UserInterestsTable.COLUMN_CATEGORY_NAME, category.getString(UserInterestsTable.COLUMN_CATEGORY_NAME));
            values.put(UserInterestsTable.COLUMN_MIN_PRICE_PER_UNIT, currInterest.getString(UserInterestsTable.COLUMN_MIN_PRICE_PER_UNIT));
            values.put(UserInterestsTable.COLUMN_MAX_PRICE_PER_UNIT, currInterest.getString(UserInterestsTable.COLUMN_MAX_PRICE_PER_UNIT));
            values.put(UserInterestsTable.COLUMN_PRICE_FILTER_APPLIED, priceFilterApplied);
            values.put(UserInterestsTable.COLUMN_FABRIC_FILTER_TEXT, currInterest.getString(UserInterestsTable.COLUMN_FABRIC_FILTER_TEXT));

            if (getUserInterests(buyerID, buyerInterestID, -1).getCount() == 0) { // insert
                db.insert(UserInterestsTable.TABLE_NAME, null, values);
            } else {
                db.update(UserInterestsTable.TABLE_NAME,
                        values,
                        UserInterestsTable.COLUMN_BUYER_ID + "=" + buyerID + " AND " + UserInterestsTable.COLUMN_BUYER_INTEREST_ID + "=" + buyerInterestID,
                        null);
            }
        }

        db.close();
        return 0;
    }

    public JSONObject getJSONDataFromCursor(String tableName, Cursor cursor, int position) {

        if (cursor != null) {
            try {
                switch (tableName) {
                    case UserTable.TABLE_NAME:
                        return getUserJSONDataFromCursor(cursor, position);
                    case UserAddressTable.TABLE_NAME:
                        return getUserAddressDataFromCursor(cursor, position);
                    case BusinessTypesTable.TABLE_NAME:
                        return getBusinessTypesDataFromCursor(cursor, position);
                    case UserInterestsTable.TABLE_NAME:
                        return getUserInterestsDataFromCursor(cursor, position);
                    default:
                        return null;
                }
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }

        } else {
            return null;
        }
    }

    private JSONObject getUserJSONDataFromCursor(Cursor cursor, int position) throws JSONException {
        JSONObject data = new JSONObject();

        cursor.moveToPosition(position);
        data.put(UserTable.COLUMN_NAME, cursor.getString(cursor.getColumnIndexOrThrow(UserTable.COLUMN_NAME)));
        data.put(UserTable.COLUMN_COMPANY_NAME, cursor.getString(cursor.getColumnIndexOrThrow(UserTable.COLUMN_COMPANY_NAME)));
        data.put(UserTable.COLUMN_MOBILE_NUMBER, cursor.getString(cursor.getColumnIndexOrThrow(UserTable.COLUMN_MOBILE_NUMBER)));
        data.put(UserTable.COLUMN_WHATSAPP_NUMBER, cursor.getString(cursor.getColumnIndexOrThrow(UserTable.COLUMN_WHATSAPP_NUMBER)));
        data.put(UserTable.COLUMN_EMAIL, cursor.getString(cursor.getColumnIndexOrThrow(UserTable.COLUMN_EMAIL)));
        data.put(UserTable.COLUMN_BUSINESS_TYPE, cursor.getString(cursor.getColumnIndexOrThrow(UserTable.COLUMN_BUSINESS_TYPE)));

        return data;
    }

    private JSONObject getUserAddressDataFromCursor(Cursor cursor, int position) throws JSONException {
        JSONObject data = new JSONObject();
        JSONArray address = new JSONArray();
        if (position == -1) {
            int count = 0;
            while (cursor.moveToNext()) {
                address.put(count++, getAddressJSONHelper(cursor));
            }
            data.put("address", address);
        } else {
            cursor.moveToPosition(position);
            //data.put("address", getAddressJSONHelper(cursor));
            return getAddressJSONHelper(cursor);
        }

        return data;
    }

    private JSONObject getAddressJSONHelper(Cursor cursor) throws JSONException {
        JSONObject currAddress = new JSONObject();

        currAddress.put(UserAddressTable.COLUMN_ADDRESS, cursor.getString(cursor.getColumnIndexOrThrow(UserAddressTable.COLUMN_ADDRESS)));
        currAddress.put(UserAddressTable.COLUMN_ADDRESS_ALIAS, cursor.getString(cursor.getColumnIndexOrThrow(UserAddressTable.COLUMN_ADDRESS_ALIAS)));
        currAddress.put(UserAddressTable.COLUMN_ADDRESS_ID, cursor.getString(cursor.getColumnIndexOrThrow(UserAddressTable.COLUMN_ADDRESS_ID)));
        currAddress.put(UserAddressTable.COLUMN_BUYER_ID, cursor.getString(cursor.getColumnIndexOrThrow(UserAddressTable.COLUMN_BUYER_ID)));
        currAddress.put(UserAddressTable.COLUMN_CITY, cursor.getString(cursor.getColumnIndexOrThrow(UserAddressTable.COLUMN_CITY)));
        currAddress.put(UserAddressTable.COLUMN_CONTACT_NUMBER, cursor.getString(cursor.getColumnIndexOrThrow(UserAddressTable.COLUMN_CONTACT_NUMBER)));
        currAddress.put(UserAddressTable.COLUMN_LANDMARK, cursor.getString(cursor.getColumnIndexOrThrow(UserAddressTable.COLUMN_LANDMARK)));
        currAddress.put(UserAddressTable.COLUMN_PINCODE, cursor.getString(cursor.getColumnIndexOrThrow(UserAddressTable.COLUMN_PINCODE)));
        currAddress.put(UserAddressTable.COLUMN_PINCODE_ID, cursor.getString(cursor.getColumnIndexOrThrow(UserAddressTable.COLUMN_PINCODE_ID)));
        currAddress.put(UserAddressTable.COLUMN_PRIORITY, cursor.getString(cursor.getColumnIndexOrThrow(UserAddressTable.COLUMN_PRIORITY)));
        currAddress.put(UserAddressTable.COLUMN_STATE, cursor.getString(cursor.getColumnIndexOrThrow(UserAddressTable.COLUMN_STATE)));
        currAddress.put(UserAddressTable._ID, cursor.getInt(cursor.getColumnIndexOrThrow(UserAddressTable._ID)));

        return currAddress;
    }

    private JSONObject getBusinessTypesDataFromCursor(Cursor cursor, int position) throws JSONException {
        JSONObject data = new JSONObject();
        JSONArray businessTypes = new JSONArray();

        if (position == -1) {
            int count = 0;
            while (cursor.moveToNext()) {
                JSONObject currBusinessType = new JSONObject();

                currBusinessType.put(BusinessTypesTable.COLUMN_BUSINESS_TYPE_ID, cursor.getString(cursor.getColumnIndexOrThrow(BusinessTypesTable.COLUMN_BUSINESS_TYPE_ID)));
                currBusinessType.put(BusinessTypesTable.COLUMN_BUSINESS_TYPE, cursor.getString(cursor.getColumnIndexOrThrow(BusinessTypesTable.COLUMN_BUSINESS_TYPE)));
                currBusinessType.put(BusinessTypesTable.COLUMN_DESCRIPTION, cursor.getString(cursor.getColumnIndexOrThrow(BusinessTypesTable.COLUMN_DESCRIPTION)));

                businessTypes.put(count++, currBusinessType);
            }
        }
        data.put(BusinessTypesTable.TABLE_NAME, businessTypes);
        return data;
    }

    private JSONObject getUserInterestsDataFromCursor(Cursor cursor, int position) throws JSONException {
        JSONObject data = new JSONObject();
        JSONArray interests = new JSONArray();

        if (position == -1) {
            int count = 0;
            while (cursor.moveToNext()) {
                JSONObject currInterest = new JSONObject();

                currInterest.put(UserInterestsTable._ID, cursor.getInt(cursor.getColumnIndexOrThrow(UserInterestsTable._ID)));
                currInterest.put(UserInterestsTable.COLUMN_BUYER_INTEREST_ID, cursor.getString(cursor.getColumnIndexOrThrow(UserInterestsTable.COLUMN_BUYER_INTEREST_ID)));
                currInterest.put(UserInterestsTable.COLUMN_CATEGORY_ID, cursor.getString(cursor.getColumnIndexOrThrow(UserInterestsTable.COLUMN_CATEGORY_ID)));
                currInterest.put(UserInterestsTable.COLUMN_CATEGORY_NAME, cursor.getString(cursor.getColumnIndexOrThrow(UserInterestsTable.COLUMN_CATEGORY_NAME)));
                currInterest.put(UserInterestsTable.COLUMN_MIN_PRICE_PER_UNIT, cursor.getString(cursor.getColumnIndexOrThrow(UserInterestsTable.COLUMN_MIN_PRICE_PER_UNIT)));
                currInterest.put(UserInterestsTable.COLUMN_MAX_PRICE_PER_UNIT, cursor.getString(cursor.getColumnIndexOrThrow(UserInterestsTable.COLUMN_MAX_PRICE_PER_UNIT)));
                currInterest.put(UserInterestsTable.COLUMN_PRICE_FILTER_APPLIED, cursor.getInt(cursor.getColumnIndexOrThrow(UserInterestsTable.COLUMN_PRICE_FILTER_APPLIED)));
                currInterest.put(UserInterestsTable.COLUMN_FABRIC_FILTER_TEXT, cursor.getString(cursor.getColumnIndexOrThrow(UserInterestsTable.COLUMN_FABRIC_FILTER_TEXT)));

                interests.put(count++, currInterest);
            }
        }
        data.put(UserInterestsTable.TABLE_NAME, interests);
        return data;
    }
}
