package com.wholdus.www.wholdusbuyerapp.databaseHelpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

import com.wholdus.www.wholdusbuyerapp.databaseContracts.UserProfileContract.BusinessTypesTable;
import com.wholdus.www.wholdusbuyerapp.databaseContracts.UserProfileContract.UserAddressTable;
import com.wholdus.www.wholdusbuyerapp.databaseContracts.UserProfileContract.UserTable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by aditya on 25/11/16.
 */

public class UserDBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "users";

    private static final String TEXT_TYPE = " TEXT";
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

    public UserDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_USER_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_USER_ADDRESS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_BUSINESS_TYPES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL(SQL_DROP_USER_TABLE);
        sqLiteDatabase.execSQL(SQL_DROP_USER_ADDRESS_TABLE);
        sqLiteDatabase.execSQL(SQL_DROP_BUSINESS_TYPES_TABLE);

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

    public Cursor getUserAddress(String buyerID, @Nullable String ID) {
        String query = "SELECT * FROM " + UserAddressTable.TABLE_NAME + " WHERE " + UserAddressTable.COLUMN_BUYER_ID + " = " + buyerID;
        if (ID != null) {
            query += " AND " + UserAddressTable.COLUMN_ADDRESS_ID + " = " + ID + ";";
        } else {
            query += ";";
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

    public long updateUserAddressData(JSONObject data) throws JSONException {
        SQLiteDatabase db = getWritableDatabase();
        JSONArray address = data.getJSONArray("address");
        String buyerID = data.getString(UserAddressTable.COLUMN_BUYER_ID);

        for (int i = 0; i < address.length(); i++) {
            JSONObject currAddress = address.getJSONObject(i);
            ContentValues values = new ContentValues();

            String addressID = currAddress.getString(UserAddressTable.COLUMN_ADDRESS_ID);

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

            if (getUserAddress(buyerID, addressID).getCount() == 0) {
                // insert
                db.insert(UserAddressTable.TABLE_NAME, null, values);
            } else {
                db.update(
                        UserAddressTable.TABLE_NAME,
                        values,
                        UserAddressTable.COLUMN_BUYER_ID + "=" + buyerID + " AND " + UserAddressTable.COLUMN_ADDRESS_ID + "=" + addressID,
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
        return businessTypes.length();
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
                currAddress.put(UserAddressTable._ID, cursor.getString(cursor.getColumnIndexOrThrow(UserAddressTable._ID)));

                address.put(count++, currAddress);
            }
        }
        data.put("address", address);
        return data;
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
}
