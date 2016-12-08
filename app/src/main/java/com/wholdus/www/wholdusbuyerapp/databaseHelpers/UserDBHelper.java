package com.wholdus.www.wholdusbuyerapp.databaseHelpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.wholdus.www.wholdusbuyerapp.databaseContracts.UserProfileContract.BusinessTypesTable;
import com.wholdus.www.wholdusbuyerapp.databaseContracts.UserProfileContract.UserAddressTable;
import com.wholdus.www.wholdusbuyerapp.databaseContracts.UserProfileContract.UserInterestsTable;
import com.wholdus.www.wholdusbuyerapp.databaseContracts.UserProfileContract.UserTable;
import com.wholdus.www.wholdusbuyerapp.databaseContracts.OrdersContract.OrdersTable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aditya on 25/11/16.
 */

public class UserDBHelper {

    private DatabaseHelper mDatabaseHelper;

    public UserDBHelper(Context context) {
        mDatabaseHelper = DatabaseHelper.getInstance(context);
    }

    /*
        Helper function to open & close database through DBHelper class and execute raw query
     */
    private Cursor getCursor(String query) {
        SQLiteDatabase db = mDatabaseHelper.openDatabase();
        Cursor cursor = db.rawQuery(query, null);
        mDatabaseHelper.closeDatabase();
        return cursor;
    }

    public Cursor getUserData(String buyerID) {
        String query = "SELECT * FROM " + UserTable.TABLE_NAME + " WHERE " + UserTable.COLUMN_BUYER_ID + "= " + buyerID + ";";
        return getCursor(query);
    }

    public Cursor getOrdersData(@Nullable List<Integer> orderStatusValues, @Nullable String orderID) {
        String query = "SELECT * FROM " + OrdersTable.TABLE_NAME;
        boolean whereApplied = false;
        if (orderID!= null && !TextUtils.isEmpty(orderID)){
            query += "WHERE " + OrdersTable.COLUMN_ORDER_ID + " = " + orderID;
            whereApplied = true;
        }
        if (orderStatusValues!= null && !orderStatusValues.isEmpty()){
            if (whereApplied == true){
                query += " AND ";
            }
            else {
                query += " WHERE ";
            }
            query += OrdersTable.COLUMN_ORDER_STATUS_VALUE + " IN " + TextUtils.join(", ", orderStatusValues);
        }

        return getCursor(query);
    }

    public Cursor getUserAddress(String buyerID, @Nullable String addressID, int _ID) {
        String query = "SELECT * FROM " + UserAddressTable.TABLE_NAME + " WHERE " + UserAddressTable.COLUMN_BUYER_ID + " = " + buyerID;
        if (addressID != null && !TextUtils.isEmpty(addressID)) {
            query += " AND " + UserAddressTable.COLUMN_ADDRESS_ID + " = " + addressID;
        } else if (_ID != -1) {
            query += " AND " + UserAddressTable._ID + " = " + _ID;
        }
        return getCursor(query);
    }

    public Cursor getUserInterests(String buyerID, @Nullable String buyerInterestID, int _ID) {
        String query = "SELECT * FROM " + UserInterestsTable.TABLE_NAME + " WHERE " +
                UserInterestsTable.COLUMN_BUYER_ID + "=" + buyerID;
        if (buyerInterestID != null) {
            query += " AND " + UserInterestsTable.COLUMN_BUYER_INTEREST_ID + "=" + buyerInterestID;
        } else if (_ID != -1) {
            query += " AND " + UserInterestsTable._ID + "=" + _ID;
        }
        return getCursor(query);
    }

    public Cursor getBusinessTypes(@Nullable String businessTypeID) {
        String query = "SELECT * FROM " + BusinessTypesTable.TABLE_NAME;
        if (businessTypeID != null) {
            query += " WHERE " + BusinessTypesTable.COLUMN_BUSINESS_TYPE_ID + " = " + businessTypeID;
        }
        return getCursor(query);
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
        SQLiteDatabase db = mDatabaseHelper.openDatabase();

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
        mDatabaseHelper.closeDatabase();
        return rows;
    }

    public long updateUserData(String buyerID, ContentValues values) {
        SQLiteDatabase db = mDatabaseHelper.openDatabase();

        long rows = db.update(
                UserTable.TABLE_NAME,
                values,
                UserTable.COLUMN_BUYER_ID + "=" + buyerID,
                null
        );

        mDatabaseHelper.closeDatabase();
        return rows;
    }

    public long updateUserAddressData(JSONObject data) throws JSONException {
        SQLiteDatabase db = mDatabaseHelper.openDatabase();

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

        mDatabaseHelper.closeDatabase();
        return 1;
    }

    public int updateBusinessTypesData(JSONObject data) throws JSONException {
        JSONArray businessTypes = data.getJSONArray(BusinessTypesTable.TABLE_NAME);
        SQLiteDatabase db = mDatabaseHelper.openDatabase();

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

        mDatabaseHelper.closeDatabase();
        return businessTypes.length();
    }

    public int updateUserInterestsData(JSONObject data) throws JSONException {
        JSONArray interests = data.getJSONArray(UserInterestsTable.TABLE_NAME);
        String buyerID = data.getString(UserInterestsTable.COLUMN_BUYER_ID);

        SQLiteDatabase db = mDatabaseHelper.openDatabase();

        for (int i = 0; i < interests.length(); i++) {
            JSONObject currInterest = interests.getJSONObject(i);
            ContentValues values = new ContentValues();

            JSONObject category = currInterest.getJSONObject("category");
            String buyerInterestID = currInterest.getString(UserInterestsTable.COLUMN_BUYER_INTEREST_ID);

            int priceFilterApplied = currInterest.getBoolean(UserInterestsTable.COLUMN_PRICE_FILTER_APPLIED) ? 1 : 0;
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

        mDatabaseHelper.closeDatabase();
        return 0;
    }

    public void saveOrdersData(JSONArray ordersArray) throws JSONException{
        for (int i = 0; i < ordersArray.length(); i++){
            JSONObject order = ordersArray.getJSONObject(i);
            ContentValues values = new ContentValues();
            values.put(OrdersTable.COLUMN_ORDER_ID, order.getString(OrdersTable.COLUMN_ORDER_ID));
            values.put(OrdersTable.COLUMN_DISPLAY_NUMBER, order.getString(OrdersTable.COLUMN_DISPLAY_NUMBER));
            JSONObject buyerAddress = order.getJSONObject("buyer_address");
            //TODO:Save buyer address if not exists
            values.put(OrdersTable.COLUMN_BUYER_ADDRESS_ID, buyerAddress.getString(OrdersTable.COLUMN_BUYER_ADDRESS_ID));
            values.put(OrdersTable.COLUMN_PRODUCT_COUNT, order.getInt(OrdersTable.COLUMN_PRODUCT_COUNT));
            values.put(OrdersTable.COLUMN_PIECES, order.getInt(OrdersTable.COLUMN_PIECES));
            values.put(OrdersTable.COLUMN_RETAIL_PRICE, order.getDouble(OrdersTable.COLUMN_RETAIL_PRICE));
            values.put(OrdersTable.COLUMN_CALCULATED_PRICE, order.getDouble(OrdersTable.COLUMN_CALCULATED_PRICE));
            values.put(OrdersTable.COLUMN_EDITED_PRICE, order.getDouble(OrdersTable.COLUMN_EDITED_PRICE));
            values.put(OrdersTable.COLUMN_SHIPPING_CHARGE, order.getDouble(OrdersTable.COLUMN_SHIPPING_CHARGE));
            values.put(OrdersTable.COLUMN_COD_CHARGE, order.getDouble(OrdersTable.COLUMN_COD_CHARGE));
            values.put(OrdersTable.COLUMN_FINAL_PRICE, order.getDouble(OrdersTable.COLUMN_FINAL_PRICE));
            JSONObject orderStatus = order.getJSONObject("order_status");
            values.put(OrdersTable.COLUMN_ORDER_STATUS_VALUE, orderStatus.getInt("value"));
            values.put(OrdersTable.COLUMN_ORDER_STATUS_DISPLAY, orderStatus.getString("display_value"));
            JSONObject paymentStatus = order.getJSONObject("order_payment_status");
            values.put(OrdersTable.COLUMN_PAYMENT_STATUS_VALUE, paymentStatus.getInt("value"));
            values.put(OrdersTable.COLUMN_PAYMENT_STATUS_DISPLAY, paymentStatus.getString("display_value"));
            values.put(OrdersTable.COLUMN_CREATED_AT, order.getString(OrdersTable.COLUMN_CREATED_AT));
            values.put(OrdersTable.COLUMN_REMARKS, order.getString(OrdersTable.COLUMN_REMARKS));

        }
    }
}
