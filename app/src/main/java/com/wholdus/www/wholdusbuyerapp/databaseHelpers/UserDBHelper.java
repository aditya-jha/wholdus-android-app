package com.wholdus.www.wholdusbuyerapp.databaseHelpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.SparseArray;

import com.wholdus.www.wholdusbuyerapp.databaseContracts.UserProfileContract.BusinessTypesTable;
import com.wholdus.www.wholdusbuyerapp.databaseContracts.UserProfileContract.UserAddressTable;
import com.wholdus.www.wholdusbuyerapp.databaseContracts.UserProfileContract.UserInterestsTable;
import com.wholdus.www.wholdusbuyerapp.databaseContracts.UserProfileContract.UserTable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;

/**
 * Created by aditya on 25/11/16.
 */

public class UserDBHelper extends BaseDBHelper {

    public UserDBHelper(Context context) {
        super(context);
    }

    public Cursor getUserData(int buyerID) {
        String query = "SELECT * FROM " + UserTable.TABLE_NAME + " WHERE " + UserTable.COLUMN_BUYER_ID + "= " + buyerID + ";";
        return getCursor(query);
    }

    public Cursor getUserAddress(int addressID, int _ID, @Nullable String[] columns) {
        String columnNames;
        if (columns != null && !(columns.length == 0)) {
            columnNames = TextUtils.join(", ", columns);
        } else {
            columnNames = "*";
        }

        String query = "SELECT " + columnNames + " FROM " + UserAddressTable.TABLE_NAME;
        if (addressID != -1) {
            query += " WHERE " + UserAddressTable.COLUMN_ADDRESS_ID + " = " + addressID;
        } else if (_ID != -1) {
            query += " WHERE " + UserAddressTable._ID + " = " + _ID;
        }
        return getCursor(query);
    }

    public Cursor getUserInterests(int buyerInterestID, int _ID, @Nullable String[] columns) {
        String columnsToSelect;
        if (columns != null) columnsToSelect = "*";
        else columnsToSelect = TextUtils.join(",", columns);

        String query = "SELECT " + columnsToSelect + " FROM " + UserInterestsTable.TABLE_NAME;
        if (buyerInterestID != -1) {
            query += " WHERE " + UserInterestsTable.COLUMN_BUYER_INTEREST_ID + "=" + buyerInterestID;
        } else if (_ID != -1) {
            query += " WHERE " + UserInterestsTable._ID + "=" + _ID;
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

    public SparseArray<String> getPresentBuyerAddressIDs() {
        String[] columns = new String[]{UserAddressTable.COLUMN_ADDRESS_ID, UserAddressTable.COLUMN_UPDATED_AT};
        Cursor cursor = getUserAddress(-1, -1, columns);
        SparseArray<String> buyerAddressIDs = new SparseArray<>();

        while (cursor.moveToNext()) {
            buyerAddressIDs.put(cursor.getInt(cursor.getColumnIndexOrThrow(UserAddressTable.COLUMN_ADDRESS_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(UserAddressTable.COLUMN_UPDATED_AT)));
        }

        return buyerAddressIDs;
    }

    public HashSet getAllUserInterestID() {
        Cursor cursor = getUserInterests(-1, -1, new String[]{UserInterestsTable.COLUMN_BUYER_INTEREST_ID});
        HashSet ids = new HashSet();
        while (cursor.moveToNext()) {
            ids.add(cursor.getInt(0));
        }
        return ids;
    }

    public int updateUserData(JSONObject data) throws JSONException {

        ContentValues user = new ContentValues();
        user.put(UserTable.COLUMN_NAME, data.getString(UserTable.COLUMN_NAME));
        user.put(UserTable.COLUMN_COMPANY_NAME, data.getString(UserTable.COLUMN_COMPANY_NAME));
        user.put(UserTable.COLUMN_EMAIL, data.getString(UserTable.COLUMN_EMAIL));
        user.put(UserTable.COLUMN_MOBILE_NUMBER, data.getString(UserTable.COLUMN_MOBILE_NUMBER));
        user.put(UserTable.COLUMN_WHATSAPP_NUMBER, data.getString(UserTable.COLUMN_WHATSAPP_NUMBER));
        user.put(UserTable.COLUMN_GENDER, data.getString(UserTable.COLUMN_GENDER));

        String businessType = data.getJSONObject("details").getJSONObject("buyer_type").getString(UserTable.COLUMN_BUSINESS_TYPE);
        user.put(UserTable.COLUMN_BUSINESS_TYPE, businessType);

        int buyerID = data.getInt(UserTable.COLUMN_BUYER_ID);
        int insertedUpdated = 0;
        SQLiteDatabase db = mDatabaseHelper.openDatabase();

        if (getUserData(buyerID).getCount() == 0) {
            user.put(UserTable.COLUMN_BUYER_ID, buyerID);
            db.insert(UserTable.TABLE_NAME, null, user);
            insertedUpdated++;
        } else {
            db.update(
                    UserTable.TABLE_NAME,
                    user,
                    UserTable.COLUMN_BUYER_ID + "=" + buyerID,
                    null
            );
            insertedUpdated++;
        }
        mDatabaseHelper.closeDatabase();
        return insertedUpdated;
    }

    public long updateUserData(int buyerID, ContentValues values) {
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
        JSONArray address = new JSONArray();

        try {
            address = data.getJSONArray("address");
        } catch (Exception e) {
            address.put(0, data);
        }

        for (int i = 0; i < address.length(); i++) {
            JSONObject currAddress = address.getJSONObject(i);
            updateUserAddressDataFromJSONObject(currAddress);
        }

        mDatabaseHelper.closeDatabase();
        return 1;
    }

    public void updateUserAddressDataFromJSONObject(JSONObject currAddress) throws JSONException {
        SQLiteDatabase db = mDatabaseHelper.openDatabase();

        ContentValues values = getBuyerAddressContentValues(currAddress);
        int addressID = values.getAsInteger(UserAddressTable.COLUMN_ADDRESS_ID);

        int _ID = -1;
        if (currAddress.has(UserAddressTable._ID)) {
            _ID = currAddress.getInt(UserAddressTable._ID);
        }

        if ((addressID == 0 && _ID == -1) || getUserAddress(addressID, -1, null).getCount() == 0) {
            // insert
            db.insert(UserAddressTable.TABLE_NAME, null, values);
        } else {
            String selection;
            if (_ID != -1) {
                selection = UserAddressTable._ID + "=" + _ID;
            } else {
                selection = UserAddressTable.COLUMN_ADDRESS_ID + "=" + addressID;
            }
            db.update(UserAddressTable.TABLE_NAME, values, selection, null);
        }
        mDatabaseHelper.closeDatabase();
    }

    public void updateUserAddressDataFromJSONObject(JSONObject currAddress, @Nullable Boolean addressPresent) throws JSONException {
        SQLiteDatabase db = mDatabaseHelper.openDatabase();

        ContentValues values = getBuyerAddressContentValues(currAddress);
        int addressID = values.getAsInteger(UserAddressTable.COLUMN_ADDRESS_ID);

        if (addressPresent == null) {
            addressPresent = (getUserAddress(addressID, -1, null).getCount() != 0);
        }

        if (!addressPresent) {
            db.insert(UserAddressTable.TABLE_NAME, null, values);
        } else {
            String selection = UserAddressTable.COLUMN_ADDRESS_ID + "=" + addressID;
            db.update(UserAddressTable.TABLE_NAME, values, selection, null);
        }
        mDatabaseHelper.closeDatabase();
    }

    private ContentValues getBuyerAddressContentValues(JSONObject currAddress) throws JSONException {
        ContentValues values = new ContentValues();
        if (currAddress.has(UserAddressTable.COLUMN_ADDRESS_ID)) {
            values.put(UserAddressTable.COLUMN_ADDRESS_ID, currAddress.getInt(UserAddressTable.COLUMN_ADDRESS_ID));
        } else {
            values.put(UserAddressTable.COLUMN_ADDRESS_ID, 0);
        }
        values.put(UserAddressTable.COLUMN_ADDRESS, currAddress.getString(UserAddressTable.COLUMN_ADDRESS));
        values.put(UserAddressTable.COLUMN_ADDRESS_ALIAS, currAddress.getString(UserAddressTable.COLUMN_ADDRESS_ALIAS));
        values.put(UserAddressTable.COLUMN_CITY, currAddress.getString(UserAddressTable.COLUMN_CITY));
        values.put(UserAddressTable.COLUMN_CONTACT_NUMBER, currAddress.getString(UserAddressTable.COLUMN_CONTACT_NUMBER));
        values.put(UserAddressTable.COLUMN_LANDMARK, currAddress.getString(UserAddressTable.COLUMN_LANDMARK));
        values.put(UserAddressTable.COLUMN_PINCODE_ID, currAddress.getInt(UserAddressTable.COLUMN_PINCODE_ID));
        values.put(UserAddressTable.COLUMN_PINCODE, currAddress.getString(UserAddressTable.COLUMN_PINCODE));
        values.put(UserAddressTable.COLUMN_STATE, currAddress.getString(UserAddressTable.COLUMN_STATE));
        values.put(UserAddressTable.COLUMN_PRIORITY, currAddress.getString(UserAddressTable.COLUMN_PRIORITY));
        //values.put(UserAddressTable.COLUMN_CREATED_AT, currAddress.getString(UserAddressTable.COLUMN_CREATED_AT));
        //values.put(UserAddressTable.COLUMN_UPDATED_AT, currAddress.getString(UserAddressTable.COLUMN_UPDATED_AT));
        return values;
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

        SQLiteDatabase db = mDatabaseHelper.openDatabase();

        for (int i = 0; i < interests.length(); i++) {
            JSONObject currInterest = interests.getJSONObject(i);
            ContentValues values = new ContentValues();

            JSONObject category = currInterest.getJSONObject("category");
            int buyerInterestID = currInterest.getInt(UserInterestsTable.COLUMN_BUYER_INTEREST_ID);

            int priceFilterApplied = currInterest.getBoolean(UserInterestsTable.COLUMN_PRICE_FILTER_APPLIED) ? 1 : 0;
            values.put(UserInterestsTable.COLUMN_BUYER_INTEREST_ID, buyerInterestID);
            values.put(UserInterestsTable.COLUMN_CATEGORY_ID, category.getInt(UserInterestsTable.COLUMN_CATEGORY_ID));
            values.put(UserInterestsTable.COLUMN_CATEGORY_NAME, category.getString(UserInterestsTable.COLUMN_CATEGORY_NAME));
            values.put(UserInterestsTable.COLUMN_MIN_PRICE_PER_UNIT, currInterest.getString(UserInterestsTable.COLUMN_MIN_PRICE_PER_UNIT));
            values.put(UserInterestsTable.COLUMN_MAX_PRICE_PER_UNIT, currInterest.getString(UserInterestsTable.COLUMN_MAX_PRICE_PER_UNIT));
            values.put(UserInterestsTable.COLUMN_PRICE_FILTER_APPLIED, priceFilterApplied);
            values.put(UserInterestsTable.COLUMN_FABRIC_FILTER_TEXT, currInterest.getString(UserInterestsTable.COLUMN_FABRIC_FILTER_TEXT));

            if (getUserInterests(buyerInterestID, -1, null).getCount() == 0) { // insert
                db.insert(UserInterestsTable.TABLE_NAME, null, values);
            } else {
                String selection = UserInterestsTable.COLUMN_BUYER_INTEREST_ID + "=" + buyerInterestID;
                db.update(UserInterestsTable.TABLE_NAME, values, selection, null);
            }
        }

        mDatabaseHelper.closeDatabase();
        return 0;
    }
}
