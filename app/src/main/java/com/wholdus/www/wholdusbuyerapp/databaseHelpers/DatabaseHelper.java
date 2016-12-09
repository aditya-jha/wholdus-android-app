package com.wholdus.www.wholdusbuyerapp.databaseHelpers;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.wholdus.www.wholdusbuyerapp.databaseContracts.BuyerProductsContract;
import com.wholdus.www.wholdusbuyerapp.databaseContracts.ProductsContract;
import com.wholdus.www.wholdusbuyerapp.databaseContracts.UserProfileContract;
import com.wholdus.www.wholdusbuyerapp.databaseContracts.OrdersContract;

/**
 * Created by aditya on 7/12/16.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "wholdus.db";

    private static final String TEXT_TYPE = " TEXT";
    private static final String INT_TYPE = " INTEGER";
    private static final String REAL_TYPE = " REAL ";
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
                    UserProfileContract.UserInterestsTable.COLUMN_BUYER_INTEREST_ID + TEXT_TYPE + COMMA_SEP +
                    UserProfileContract.UserInterestsTable.COLUMN_CATEGORY_ID + TEXT_TYPE + COMMA_SEP +
                    UserProfileContract.UserInterestsTable.COLUMN_CATEGORY_NAME + TEXT_TYPE + COMMA_SEP +
                    UserProfileContract.UserInterestsTable.COLUMN_MIN_PRICE_PER_UNIT + TEXT_TYPE + COMMA_SEP +
                    UserProfileContract.UserInterestsTable.COLUMN_MAX_PRICE_PER_UNIT + TEXT_TYPE + COMMA_SEP +
                    UserProfileContract.UserInterestsTable.COLUMN_FABRIC_FILTER_TEXT + TEXT_TYPE + COMMA_SEP +
                    UserProfileContract.UserInterestsTable.COLUMN_PRICE_FILTER_APPLIED + INT_TYPE + " )";

    private static final String SQL_DROP_USER_INTERESTS_TABLE =
            "DROP TABLE IF EXISTS " + UserProfileContract.BusinessTypesTable.TABLE_NAME;

    private static final String SQL_CREATE_ORDERS_TABLE =
            "CREATE TABLE " + OrdersContract.OrdersTable.TABLE_NAME + " (" +
                    OrdersContract.OrdersTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + COMMA_SEP +
                    OrdersContract.OrdersTable.COLUMN_ORDER_ID + TEXT_TYPE + COMMA_SEP +
                    OrdersContract.OrdersTable.COLUMN_DISPLAY_NUMBER + TEXT_TYPE + COMMA_SEP +
                    OrdersContract.OrdersTable.COLUMN_BUYER_ADDRESS_ID + TEXT_TYPE + COMMA_SEP +
                    OrdersContract.OrdersTable.COLUMN_PRODUCT_COUNT + INT_TYPE + COMMA_SEP +
                    OrdersContract.OrdersTable.COLUMN_PIECES + INT_TYPE + COMMA_SEP +
                    OrdersContract.OrdersTable.COLUMN_RETAIL_PRICE + REAL_TYPE + COMMA_SEP +
                    OrdersContract.OrdersTable.COLUMN_CALCULATED_PRICE + REAL_TYPE + COMMA_SEP +
                    OrdersContract.OrdersTable.COLUMN_EDITED_PRICE + REAL_TYPE + COMMA_SEP +
                    OrdersContract.OrdersTable.COLUMN_SHIPPING_CHARGE + REAL_TYPE + COMMA_SEP +
                    OrdersContract.OrdersTable.COLUMN_COD_CHARGE + REAL_TYPE + COMMA_SEP +
                    OrdersContract.OrdersTable.COLUMN_FINAL_PRICE + REAL_TYPE + COMMA_SEP +
                    OrdersContract.OrdersTable.COLUMN_ORDER_STATUS_VALUE + INT_TYPE + COMMA_SEP +
                    OrdersContract.OrdersTable.COLUMN_ORDER_STATUS_DISPLAY + TEXT_TYPE + COMMA_SEP +
                    OrdersContract.OrdersTable.COLUMN_PAYMENT_STATUS_VALUE + INT_TYPE + COMMA_SEP +
                    OrdersContract.OrdersTable.COLUMN_PAYMENT_STATUS_DISPLAY + TEXT_TYPE + COMMA_SEP +
                    OrdersContract.OrdersTable.COLUMN_CREATED_AT + TEXT_TYPE + COMMA_SEP +
                    OrdersContract.OrdersTable.COLUMN_REMARKS + TEXT_TYPE + " )";

    private static final String SQL_DROP_ORDERS_TABLE =
            "DROP TABLE IF EXISTS " + OrdersContract.OrdersTable.TABLE_NAME;
    
    private static final String SQL_CREATE_SUBORDERS_TABLE =
            "CREATE TABLE " + OrdersContract.SubordersTable.TABLE_NAME + " (" +
                    OrdersContract.SubordersTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + COMMA_SEP +
                    OrdersContract.SubordersTable.COLUMN_ORDER_ID + TEXT_TYPE + COMMA_SEP +
                    OrdersContract.SubordersTable.COLUMN_SUBORDER_ID + TEXT_TYPE + COMMA_SEP +
                    OrdersContract.SubordersTable.COLUMN_DISPLAY_NUMBER + TEXT_TYPE + COMMA_SEP +
                    OrdersContract.SubordersTable.COLUMN_SELLER_ID + TEXT_TYPE + COMMA_SEP +
                    OrdersContract.SubordersTable.COLUMN_SELLER_ADDRESS_ID + TEXT_TYPE + COMMA_SEP +
                    OrdersContract.SubordersTable.COLUMN_PRODUCT_COUNT + INT_TYPE + COMMA_SEP +
                    OrdersContract.SubordersTable.COLUMN_PIECES + INT_TYPE + COMMA_SEP +
                    OrdersContract.SubordersTable.COLUMN_RETAIL_PRICE + REAL_TYPE + COMMA_SEP +
                    OrdersContract.SubordersTable.COLUMN_CALCULATED_PRICE + REAL_TYPE + COMMA_SEP +
                    OrdersContract.SubordersTable.COLUMN_EDITED_PRICE + REAL_TYPE + COMMA_SEP +
                    OrdersContract.SubordersTable.COLUMN_SHIPPING_CHARGE + REAL_TYPE + COMMA_SEP +
                    OrdersContract.SubordersTable.COLUMN_COD_CHARGE + REAL_TYPE + COMMA_SEP +
                    OrdersContract.SubordersTable.COLUMN_FINAL_PRICE + REAL_TYPE + COMMA_SEP +
                    OrdersContract.SubordersTable.COLUMN_SUBORDER_STATUS_VALUE + INT_TYPE + COMMA_SEP +
                    OrdersContract.SubordersTable.COLUMN_SUBORDER_STATUS_DISPLAY + TEXT_TYPE + COMMA_SEP +
                    OrdersContract.SubordersTable.COLUMN_PAYMENT_STATUS_VALUE + INT_TYPE + COMMA_SEP +
                    OrdersContract.SubordersTable.COLUMN_PAYMENT_STATUS_DISPLAY + TEXT_TYPE + COMMA_SEP +
                    OrdersContract.SubordersTable.COLUMN_CREATED_AT + TEXT_TYPE + " )";

    private static final String SQL_DROP_SUBORDERS_TABLE =
            "DROP TABLE IF EXISTS " + OrdersContract.SubordersTable.TABLE_NAME;

    private static final String SQL_CREATE_ORDER_ITEMS_TABLE =
            "CREATE TABLE " + OrdersContract.OrderItemsTable.TABLE_NAME + " (" +
                    OrdersContract.OrderItemsTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + COMMA_SEP +
                    OrdersContract.OrderItemsTable.COLUMN_ORDER_ITEM_ID + TEXT_TYPE + COMMA_SEP +
                    OrdersContract.OrderItemsTable.COLUMN_SUBORDER_ID + TEXT_TYPE + COMMA_SEP +
                    OrdersContract.OrderItemsTable.COLUMN_PRODUCT_ID + TEXT_TYPE + COMMA_SEP +
                    OrdersContract.OrderItemsTable.COLUMN_ORDER_SHIPMENT_ID + TEXT_TYPE + COMMA_SEP +
                    OrdersContract.OrderItemsTable.COLUMN_LOTS + INT_TYPE + COMMA_SEP +
                    OrdersContract.OrderItemsTable.COLUMN_LOT_SIZE + INT_TYPE + COMMA_SEP +
                    OrdersContract.OrderItemsTable.COLUMN_PIECES + INT_TYPE + COMMA_SEP +
                    OrdersContract.OrderItemsTable.COLUMN_RETAIL_PRICE_PER_PIECE + REAL_TYPE + COMMA_SEP +
                    OrdersContract.OrderItemsTable.COLUMN_CALCULATED_PRICE_PER_PIECE + REAL_TYPE + COMMA_SEP +
                    OrdersContract.OrderItemsTable.COLUMN_EDITED_PRICE_PER_PIECE + REAL_TYPE + COMMA_SEP +
                    OrdersContract.OrderItemsTable.COLUMN_FINAL_PRICE + REAL_TYPE + COMMA_SEP +
                    OrdersContract.OrderItemsTable.COLUMN_ORDER_ITEM_STATUS_VALUE + INT_TYPE + COMMA_SEP +
                    OrdersContract.OrderItemsTable.COLUMN_ORDER_ITEM_STATUS_DISPLAY + TEXT_TYPE + COMMA_SEP +
                    OrdersContract.OrderItemsTable.COLUMN_TRACKING_URL + TEXT_TYPE + COMMA_SEP +
                    OrdersContract.OrderItemsTable.COLUMN_CREATED_AT + TEXT_TYPE + COMMA_SEP +
                    OrdersContract.OrderItemsTable.COLUMN_REMARKS + TEXT_TYPE + " )";

    private static final String SQL_DROP_ORDER_ITEMS_TABLE =
            "DROP TABLE IF EXISTS " + OrdersContract.OrderItemsTable.TABLE_NAME;

    private static final String SQL_CREATE_PRODUCTS_TABLE =
            "CREATE TABLE " + ProductsContract.ProductsTable.TABLE_NAME + " (" +
                    ProductsContract.ProductsTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + COMMA_SEP +
                    ProductsContract.ProductsTable.COLUMN_PRODUCT_ID + INT_TYPE + COMMA_SEP +
                    ProductsContract.ProductsTable.COLUMN_CATEGORY_ID + INT_TYPE + COMMA_SEP +
                    ProductsContract.ProductsTable.COLUMN_SELLER_ID + INT_TYPE + COMMA_SEP +
                    ProductsContract.ProductsTable.COLUMN_PRODUCT_DETAILS_ID + INT_TYPE + COMMA_SEP +
                    ProductsContract.ProductsTable.COLUMN_DISPLAY_NAME + TEXT_TYPE + COMMA_SEP +
                    ProductsContract.ProductsTable.COLUMN_NAME + TEXT_TYPE + COMMA_SEP +
                    ProductsContract.ProductsTable.COLUMN_LOT_SIZE + INT_TYPE + COMMA_SEP +
                    ProductsContract.ProductsTable.COLUMN_PRICE_PER_UNIT + REAL_TYPE + COMMA_SEP +
                    ProductsContract.ProductsTable.COLUMN_PRICE_PER_LOT + REAL_TYPE + COMMA_SEP +
                    ProductsContract.ProductsTable.COLUMN_MIN_PRICE_PER_UNIT + REAL_TYPE + COMMA_SEP +
                    ProductsContract.ProductsTable.COLUMN_MARGIN + REAL_TYPE + COMMA_SEP +
                    ProductsContract.ProductsTable.COLUMN_UNIT + TEXT_TYPE + COMMA_SEP +
                    ProductsContract.ProductsTable.COLUMN_URL + TEXT_TYPE + COMMA_SEP +
                    ProductsContract.ProductsTable.COLUMN_SHOW_ONLINE + INT_TYPE + COMMA_SEP +
                    ProductsContract.ProductsTable.COLUMN_DELETE_STATUS + INT_TYPE + COMMA_SEP +
                    ProductsContract.ProductsTable.COLUMN_IMAGE_NAME + TEXT_TYPE + COMMA_SEP +
                    ProductsContract.ProductsTable.COLUMN_IMAGE_COUNT + INT_TYPE + COMMA_SEP +
                    ProductsContract.ProductsTable.COLUMN_IMAGE_NUMBERS + TEXT_TYPE + COMMA_SEP +
                    ProductsContract.ProductsTable.COLUMN_IMAGE_PATH + TEXT_TYPE + COMMA_SEP +
                    ProductsContract.ProductsTable.COLUMN_WARRANTY + TEXT_TYPE + COMMA_SEP +
                    ProductsContract.ProductsTable.COLUMN_SPECIAL_FEATURE + TEXT_TYPE + COMMA_SEP +
                    ProductsContract.ProductsTable.COLUMN_AVAILABLITY + TEXT_TYPE + COMMA_SEP +
                    ProductsContract.ProductsTable.COLUMN_STYLE + TEXT_TYPE + COMMA_SEP +
                    ProductsContract.ProductsTable.COLUMN_MANUFACTURED_CITY + TEXT_TYPE + COMMA_SEP +
                    ProductsContract.ProductsTable.COLUMN_PATTERN + TEXT_TYPE + COMMA_SEP +
                    ProductsContract.ProductsTable.COLUMN_COLOURS + TEXT_TYPE + COMMA_SEP +
                    ProductsContract.ProductsTable.COLUMN_LOT_DESCRIPTION + TEXT_TYPE + COMMA_SEP +
                    ProductsContract.ProductsTable.COLUMN_DESCRIPTION + TEXT_TYPE + COMMA_SEP +
                    ProductsContract.ProductsTable.COLUMN_WORK_DESCRIPTION_TYPE + TEXT_TYPE + COMMA_SEP +
                    ProductsContract.ProductsTable.COLUMN_NECK_COLLAR_TYPE + TEXT_TYPE + COMMA_SEP +
                    ProductsContract.ProductsTable.COLUMN_FABRIC_GSM + TEXT_TYPE + COMMA_SEP +
                    ProductsContract.ProductsTable.COLUMN_DISPATCHED_IN + TEXT_TYPE + COMMA_SEP +
                    ProductsContract.ProductsTable.COLUMN_REMARKS + TEXT_TYPE + COMMA_SEP +
                    ProductsContract.ProductsTable.COLUMN_SELLER_CATALOG_NUMBER + TEXT_TYPE + COMMA_SEP +
                    ProductsContract.ProductsTable.COLUMN_SLEEVE + TEXT_TYPE + COMMA_SEP +
                    ProductsContract.ProductsTable.COLUMN_SIZES + TEXT_TYPE + COMMA_SEP +
                    ProductsContract.ProductsTable.COLUMN_GENDER + TEXT_TYPE + COMMA_SEP +
                    ProductsContract.ProductsTable.COLUMN_WEIGHT_PER_UNIT + REAL_TYPE + COMMA_SEP +
                    ProductsContract.ProductsTable.COLUMN_PACKAGING_DETAILS + TEXT_TYPE + COMMA_SEP +
                    ProductsContract.ProductsTable.COLUMN_LENGTH + TEXT_TYPE + COMMA_SEP +
                    ProductsContract.ProductsTable.COLUMN_CREATED_AT + TEXT_TYPE + COMMA_SEP +
                    ProductsContract.ProductsTable.COLUMN_UPDATED_AT + TEXT_TYPE + " )";

    private static final String SQL_DROP_PRODUCTS_TABLE =
            "DROP TABLE IF EXISTS " + ProductsContract.ProductsTable.TABLE_NAME;

    private static final String SQL_CREATE_CATEGORIES_TABLE =
            "CREATE TABLE " + ProductsContract.CategoriesTable.TABLE_NAME + " (" +
                    ProductsContract.CategoriesTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + COMMA_SEP +
                    ProductsContract.CategoriesTable.COLUMN_CATEGORY_ID + INT_TYPE + COMMA_SEP +
                    ProductsContract.CategoriesTable.COLUMN_DISPLAY_NAME + TEXT_TYPE + COMMA_SEP +
                    ProductsContract.CategoriesTable.COLUMN_URL + TEXT_TYPE + COMMA_SEP +
                    ProductsContract.CategoriesTable.COLUMN_SLUG + TEXT_TYPE + " )";

    private static final String SQL_DROP_CATEGORIES_TABLE =
            "DROP TABLE IF EXISTS " + ProductsContract.CategoriesTable.TABLE_NAME;

    private static final String SQL_CREATE_SELLERS_TABLE =
            "CREATE TABLE " + ProductsContract.SellersTable.TABLE_NAME + " (" +
                    ProductsContract.SellersTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + COMMA_SEP +
                    ProductsContract.SellersTable.COLUMN_SELLER_ID + TEXT_TYPE + COMMA_SEP +
                    ProductsContract.SellersTable.COLUMN_COMPANY_NAME + TEXT_TYPE + COMMA_SEP +
                    ProductsContract.SellersTable.COLUMN_NAME + TEXT_TYPE + COMMA_SEP +
                    ProductsContract.SellersTable.COLUMN_COMPANY_PROFILE + TEXT_TYPE + COMMA_SEP +
                    ProductsContract.SellersTable.COLUMN_SHOW_ONLINE + INT_TYPE + COMMA_SEP +
                    ProductsContract.SellersTable.COLUMN_DELETE_STATUS + INT_TYPE + " )";

    private static final String SQL_DROP_SELLERS_TABLE =
            "DROP TABLE IF EXISTS " + ProductsContract.SellersTable.TABLE_NAME;

    private static final String SQL_CREATE_SELLER_ADDRESS_TABLE =
            "CREATE TABLE " + ProductsContract.SellerAddressTable.TABLE_NAME + " (" +
                    ProductsContract.SellerAddressTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + COMMA_SEP +
                    ProductsContract.SellerAddressTable.COLUMN_SELLER_ID + TEXT_TYPE + COMMA_SEP +
                    ProductsContract.SellerAddressTable.COLUMN_ADDRESS_ID + TEXT_TYPE + COMMA_SEP +
                    ProductsContract.SellerAddressTable.COLUMN_CITY + TEXT_TYPE + COMMA_SEP +
                    ProductsContract.SellerAddressTable.COLUMN_STATE + TEXT_TYPE + COMMA_SEP +
                    ProductsContract.SellerAddressTable.COLUMN_LANDMARK + TEXT_TYPE + COMMA_SEP +
                    ProductsContract.SellerAddressTable.COLUMN_ADDRESS + TEXT_TYPE + COMMA_SEP +
                    ProductsContract.SellerAddressTable.COLUMN_CONTACT_NUMBER + TEXT_TYPE + COMMA_SEP +
                    ProductsContract.SellerAddressTable.COLUMN_PINCODE + TEXT_TYPE + " )";

    private static final String SQL_DROP_SELLER_ADDRESS_TABLE =
            "DROP TABLE IF EXISTS " + ProductsContract.SellerAddressTable.TABLE_NAME;

    private static final String SQL_CREATE_BUYER_PRODUCT_TABLE =
            "CREATE TABLE " + BuyerProductsContract.BuyerProductTable.TABLE_NAME + " (" +
                    BuyerProductsContract.BuyerProductTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + COMMA_SEP +
                    BuyerProductsContract.BuyerProductTable.COLUMN_BUYER_PRODUCT_ID + INT_TYPE + COMMA_SEP +
                    BuyerProductsContract.BuyerProductTable.COLUMN_BUYER_PRODUCT_RESPONSE_ID + INT_TYPE + COMMA_SEP +
                    BuyerProductsContract.BuyerProductTable.COLUMN_PRODUCT_ID + INT_TYPE + COMMA_SEP +
                    BuyerProductsContract.BuyerProductTable.COLUMN_STORE_DISCOUNT + REAL_TYPE + COMMA_SEP +
                    BuyerProductsContract.BuyerProductTable.COLUMN_HAS_SWIPED + INT_TYPE + COMMA_SEP +
                    BuyerProductsContract.BuyerProductTable.COLUMN_RESPONSE_CODE + INT_TYPE + COMMA_SEP +
                    BuyerProductsContract.BuyerProductTable.COLUMN_SYNCED + INT_TYPE + " )";

    private static final String SQL_DROP_BUYER_PRODUCT_TABLE =
            "DROP TABLE IF EXISTS " + BuyerProductsContract.BuyerProductTable.TABLE_NAME;

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
        /* TODO: count openings if error in logcat */
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

        sqLiteDatabase.execSQL(SQL_CREATE_ORDERS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_SUBORDERS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_ORDER_ITEMS_TABLE);

        sqLiteDatabase.execSQL(SQL_CREATE_PRODUCTS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_CATEGORIES_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_SELLERS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_SELLER_ADDRESS_TABLE);

        sqLiteDatabase.execSQL(SQL_CREATE_BUYER_PRODUCT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL(SQL_DROP_USER_TABLE);
        sqLiteDatabase.execSQL(SQL_DROP_USER_ADDRESS_TABLE);
        sqLiteDatabase.execSQL(SQL_DROP_BUSINESS_TYPES_TABLE);
        sqLiteDatabase.execSQL(SQL_DROP_USER_INTERESTS_TABLE);

        sqLiteDatabase.execSQL(SQL_DROP_ORDERS_TABLE);
        sqLiteDatabase.execSQL(SQL_DROP_SUBORDERS_TABLE);
        sqLiteDatabase.execSQL(SQL_DROP_ORDER_ITEMS_TABLE);

        sqLiteDatabase.execSQL(SQL_DROP_PRODUCTS_TABLE);
        sqLiteDatabase.execSQL(SQL_DROP_CATEGORIES_TABLE);
        sqLiteDatabase.execSQL(SQL_DROP_SELLERS_TABLE);
        sqLiteDatabase.execSQL(SQL_DROP_SELLER_ADDRESS_TABLE);

        sqLiteDatabase.execSQL(SQL_DROP_BUYER_PRODUCT_TABLE);

        onCreate(sqLiteDatabase);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
