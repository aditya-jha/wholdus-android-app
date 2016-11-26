package com.wholdus.www.wholdusbuyerapp.databaseHelpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.wholdus.www.wholdusbuyerapp.databaseContracts.UserProfileContract.UserAddressTable;
import com.wholdus.www.wholdusbuyerapp.databaseContracts.UserProfileContract.UserTable;

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
                    UserAddressTable.COLUMN_PINCODE + TEXT_TYPE + " )";

    private static final String SQL_DROP_USER_ADDRESS_TABLE =
            "DROP TABLE IF EXISTS " + UserAddressTable.TABLE_NAME;

    public UserDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_USER_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_USER_ADDRESS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL(SQL_DROP_USER_TABLE);
        sqLiteDatabase.execSQL(SQL_DROP_USER_ADDRESS_TABLE);

        onCreate(sqLiteDatabase);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public Cursor getUserData(String buyerID) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + UserTable.TABLE_NAME + " WHERE " + UserTable.COLUMN_BUYER_ID + "= " + buyerID + ";";

        return db.rawQuery(query, null);
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

        if (getUserData(buyerID).getCount() == 0) {
            user.put(UserTable.COLUMN_BUYER_ID, buyerID);
            SQLiteDatabase db = getWritableDatabase();
            long id = db.insert(UserTable.TABLE_NAME, null, user);
            db.close();
            return id;
        } else {
            SQLiteDatabase db = getWritableDatabase();
            String selection = UserTable.COLUMN_BUYER_ID + "=" + buyerID;
            long rows = db.update(
                    UserTable.TABLE_NAME,
                    user,
                    selection,
                    null
            );
            db.close();
            return rows;
        }
    }

    public static JSONObject getJSONDataFromCursor(Cursor cursor, int position) {

        if (cursor == null) {
            return null;
        }

        JSONObject data = new JSONObject();
        try {
            cursor.moveToPosition(position);
            data.put(UserTable.COLUMN_NAME, cursor.getString(cursor.getColumnIndexOrThrow(UserTable.COLUMN_NAME)));
            data.put(UserTable.COLUMN_COMPANY_NAME, cursor.getString(cursor.getColumnIndexOrThrow(UserTable.COLUMN_COMPANY_NAME)));
            data.put(UserTable.COLUMN_MOBILE_NUMBER, cursor.getString(cursor.getColumnIndexOrThrow(UserTable.COLUMN_MOBILE_NUMBER)));
            data.put(UserTable.COLUMN_WHATSAPP_NUMBER, cursor.getString(cursor.getColumnIndexOrThrow(UserTable.COLUMN_WHATSAPP_NUMBER)));
            data.put(UserTable.COLUMN_EMAIL, cursor.getString(cursor.getColumnIndexOrThrow(UserTable.COLUMN_EMAIL)));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return data;
    }
}
