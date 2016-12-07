package com.wholdus.www.wholdusbuyerapp.databaseHelpers;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.wholdus.www.wholdusbuyerapp.databaseContracts.UserProfileContract;

/**
 * Created by aditya on 7/12/16.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "wholdus.db";

    private static final String TEXT_TYPE = " TEXT";
    private static final String INT_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";
    private static final String NOT_NULL = " NOT NULL";

    private static final String SQL_CREATE_USER_TABLE =
            "CREATE TABLE " + UserProfileContract.UserTable.TABLE_NAME + " (" +
                    UserProfileContract.UserTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + COMMA_SEP +
                    UserProfileContract.UserTable.COLUMN_BUYER_ID + TEXT_TYPE + NOT_NULL + COMMA_SEP +
                    UserProfileContract.UserTable.COLUMN_NAME + TEXT_TYPE + COMMA_SEP +
                    UserProfileContract.UserTable.COLUMN_EMAIL + TEXT_TYPE + COMMA_SEP +
                    UserProfileContract.UserTable.COLUMN_MOBILE_NUMBER + TEXT_TYPE + NOT_NULL + COMMA_SEP +
                    UserProfileContract.UserTable.COLUMN_WHATSAPP_NUMBER + TEXT_TYPE + COMMA_SEP +
                    UserProfileContract.UserTable.COLUMN_COMPANY_NAME + TEXT_TYPE + COMMA_SEP +
                    UserProfileContract.UserTable.COLUMN_BUSINESS_TYPE + TEXT_TYPE + COMMA_SEP +
                    UserProfileContract.UserTable.COLUMN_GENDER + TEXT_TYPE + " )";

    private static final String SQL_DROP_USER_TABLE =
            "DROP TABLE IF EXISTS " + UserProfileContract.UserTable.TABLE_NAME;

    private static final String SQL_CREATE_USER_ADDRESS_TABLE =
            "CREATE TABLE " + UserProfileContract.UserAddressTable.TABLE_NAME + " (" +
                    UserProfileContract.UserAddressTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + COMMA_SEP +
                    UserProfileContract.UserAddressTable.COLUMN_BUYER_ID + TEXT_TYPE + COMMA_SEP +
                    UserProfileContract.UserAddressTable.COLUMN_PINCODE_ID + TEXT_TYPE + COMMA_SEP +
                    UserProfileContract.UserAddressTable.COLUMN_ADDRESS_ID + TEXT_TYPE + COMMA_SEP +
                    UserProfileContract.UserAddressTable.COLUMN_PRIORITY + TEXT_TYPE + COMMA_SEP +
                    UserProfileContract.UserAddressTable.COLUMN_CITY + TEXT_TYPE + COMMA_SEP +
                    UserProfileContract.UserAddressTable.COLUMN_STATE + TEXT_TYPE + COMMA_SEP +
                    UserProfileContract.UserAddressTable.COLUMN_LANDMARK + TEXT_TYPE + COMMA_SEP +
                    UserProfileContract.UserAddressTable.COLUMN_ADDRESS + TEXT_TYPE + COMMA_SEP +
                    UserProfileContract.UserAddressTable.COLUMN_CONTACT_NUMBER + TEXT_TYPE + COMMA_SEP +
                    UserProfileContract.UserAddressTable.COLUMN_ADDRESS_ALIAS + TEXT_TYPE + COMMA_SEP +
                    UserProfileContract.UserAddressTable.COLUMN_PINCODE + TEXT_TYPE + " )";

    private static final String SQL_DROP_USER_ADDRESS_TABLE =
            "DROP TABLE IF EXISTS " + UserProfileContract.UserAddressTable.TABLE_NAME;

    private static final String SQL_CREATE_BUSINESS_TYPES_TABLE =
            "CREATE TABLE " + UserProfileContract.BusinessTypesTable.TABLE_NAME + " (" +
                    UserProfileContract.BusinessTypesTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + COMMA_SEP +
                    UserProfileContract.BusinessTypesTable.COLUMN_BUSINESS_TYPE + TEXT_TYPE + COMMA_SEP +
                    UserProfileContract.BusinessTypesTable.COLUMN_BUSINESS_TYPE_ID + TEXT_TYPE + COMMA_SEP +
                    UserProfileContract.BusinessTypesTable.COLUMN_DESCRIPTION + TEXT_TYPE + " )";

    private static final String SQL_DROP_BUSINESS_TYPES_TABLE =
            "DROP TABLE IF EXISTS " + UserProfileContract.BusinessTypesTable.TABLE_NAME;

    private static final String SQL_CREATE_USER_INTERESTS_TABLE =
            "CREATE TABLE " + UserProfileContract.UserInterestsTable.TABLE_NAME + " (" +
                    UserProfileContract.UserInterestsTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + COMMA_SEP +
                    UserProfileContract.UserInterestsTable.COLUMN_BUYER_ID + TEXT_TYPE + COMMA_SEP +
                    UserProfileContract.UserInterestsTable.COLUMN_BUYER_INTEREST_ID + TEXT_TYPE + COMMA_SEP +
                    UserProfileContract.UserInterestsTable.COLUMN_CATEGORY_ID + TEXT_TYPE + COMMA_SEP +
                    UserProfileContract.UserInterestsTable.COLUMN_CATEGORY_NAME + TEXT_TYPE + COMMA_SEP +
                    UserProfileContract.UserInterestsTable.COLUMN_MIN_PRICE_PER_UNIT + TEXT_TYPE + COMMA_SEP +
                    UserProfileContract.UserInterestsTable.COLUMN_MAX_PRICE_PER_UNIT + TEXT_TYPE + COMMA_SEP +
                    UserProfileContract.UserInterestsTable.COLUMN_FABRIC_FILTER_TEXT + TEXT_TYPE + COMMA_SEP +
                    UserProfileContract.UserInterestsTable.COLUMN_PRICE_FILTER_APPLIED + INT_TYPE + " )";

    private static final String SQL_DROP_USER_INTERESTS_TABLE =
            "DROP TABLE IF EXISTS " + UserProfileContract.BusinessTypesTable.TABLE_NAME;

    private static DatabaseHelper instance;

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context);
        }
        return instance;
    }

    public synchronized SQLiteDatabase openDatabase() {
        /* TODO: counting openings if error in logcat */
        return instance.getWritableDatabase();
        /*
         *   writeable and readable are same, so does not matter
         *   https://nfrolov.wordpress.com/2014/08/16/android-sqlitedatabase-locking-and-multi-threading/
         */
    }

    public synchronized void closeDatabase() {
        /* TODO: implement closeDatabase function if logcat shows open connection error */
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_USER_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_USER_ADDRESS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_BUSINESS_TYPES_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_USER_INTERESTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL(SQL_DROP_USER_TABLE);
        sqLiteDatabase.execSQL(SQL_DROP_USER_ADDRESS_TABLE);
        sqLiteDatabase.execSQL(SQL_DROP_BUSINESS_TYPES_TABLE);
        sqLiteDatabase.execSQL(SQL_DROP_USER_INTERESTS_TABLE);

        onCreate(sqLiteDatabase);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
