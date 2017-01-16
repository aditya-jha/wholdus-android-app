package com.wholdus.www.wholdusbuyerapp.databaseHelpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.wholdus.www.wholdusbuyerapp.databaseContracts.CartContract.CartTable;
import com.wholdus.www.wholdusbuyerapp.databaseContracts.CartContract.SubCartsTable;
import com.wholdus.www.wholdusbuyerapp.databaseContracts.CartContract.CartItemsTable;
import com.wholdus.www.wholdusbuyerapp.databaseContracts.CatalogContract.CategorySellersTable;
import com.wholdus.www.wholdusbuyerapp.databaseContracts.CatalogContract.SellerAddressTable;
import com.wholdus.www.wholdusbuyerapp.databaseContracts.NotificationContract.NotificationTable;

import static com.wholdus.www.wholdusbuyerapp.databaseContracts.OrdersContract.OrderItemsTable;
import static com.wholdus.www.wholdusbuyerapp.databaseContracts.OrdersContract.OrdersTable;
import static com.wholdus.www.wholdusbuyerapp.databaseContracts.OrdersContract.SubordersTable;
import static com.wholdus.www.wholdusbuyerapp.databaseContracts.CatalogContract.CategoriesTable;
import static com.wholdus.www.wholdusbuyerapp.databaseContracts.CatalogContract.ProductsTable;
import static com.wholdus.www.wholdusbuyerapp.databaseContracts.CatalogContract.SellersTable;
import static com.wholdus.www.wholdusbuyerapp.databaseContracts.UserProfileContract.BusinessTypesTable;
import static com.wholdus.www.wholdusbuyerapp.databaseContracts.UserProfileContract.UserAddressTable;
import static com.wholdus.www.wholdusbuyerapp.databaseContracts.UserProfileContract.UserTable;

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
            "CREATE TABLE " + UserTable.TABLE_NAME + " (" +
                    UserTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + COMMA_SEP +
                    UserTable.COLUMN_BUYER_ID + INT_TYPE + NOT_NULL + COMMA_SEP +
                    UserTable.COLUMN_NAME + TEXT_TYPE + COMMA_SEP +
                    UserTable.COLUMN_EMAIL + TEXT_TYPE + COMMA_SEP +
                    UserTable.COLUMN_MOBILE_NUMBER + TEXT_TYPE + NOT_NULL + COMMA_SEP +
                    UserTable.COLUMN_WHATSAPP_NUMBER + TEXT_TYPE + COMMA_SEP +
                    UserTable.COLUMN_COMPANY_NAME + TEXT_TYPE + COMMA_SEP +
                    UserTable.COLUMN_BUSINESS_TYPE + TEXT_TYPE + COMMA_SEP +
                    UserTable.COLUMN_CREATED_AT + TEXT_TYPE + COMMA_SEP +
                    UserTable.COLUMN_UPDATED_AT + TEXT_TYPE + COMMA_SEP +
                    UserTable.COLUMN_GENDER + TEXT_TYPE + " )";

    private static final String SQL_DROP_USER_TABLE =
            "DROP TABLE IF EXISTS " + UserTable.TABLE_NAME;

    private static final String SQL_CREATE_USER_ADDRESS_TABLE =
            "CREATE TABLE " + UserAddressTable.TABLE_NAME + " (" +
                    UserAddressTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + COMMA_SEP +
                    UserAddressTable.COLUMN_ADDRESS_ID + INT_TYPE + COMMA_SEP +
                    UserAddressTable.COLUMN_ADDRESS_HISTORY_ID + INT_TYPE + COMMA_SEP +
                    UserAddressTable.COLUMN_PRIORITY + INT_TYPE + COMMA_SEP +
                    UserAddressTable.COLUMN_CITY + TEXT_TYPE + COMMA_SEP +
                    UserAddressTable.COLUMN_STATE + TEXT_TYPE + COMMA_SEP +
                    UserAddressTable.COLUMN_LANDMARK + TEXT_TYPE + COMMA_SEP +
                    UserAddressTable.COLUMN_ADDRESS + TEXT_TYPE + COMMA_SEP +
                    UserAddressTable.COLUMN_CONTACT_NUMBER + TEXT_TYPE + COMMA_SEP +
                    UserAddressTable.COLUMN_ADDRESS_ALIAS + TEXT_TYPE + COMMA_SEP +
                    UserAddressTable.COLUMN_CREATED_AT + TEXT_TYPE + COMMA_SEP +
                    UserAddressTable.COLUMN_SYNCED + INT_TYPE + COMMA_SEP +
                    UserAddressTable.COLUMN_CLIENT_ID + TEXT_TYPE + COMMA_SEP +
                    UserAddressTable.COLUMN_UPDATED_AT + TEXT_TYPE + COMMA_SEP +
                    UserAddressTable.COLUMN_PINCODE + TEXT_TYPE + " )";

    private static final String SQL_DROP_USER_ADDRESS_TABLE =
            "DROP TABLE IF EXISTS " + UserAddressTable.TABLE_NAME;

    private static final String SQL_CREATE_BUSINESS_TYPES_TABLE =
            "CREATE TABLE " + BusinessTypesTable.TABLE_NAME + " (" +
                    BusinessTypesTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + COMMA_SEP +
                    BusinessTypesTable.COLUMN_BUSINESS_TYPE + TEXT_TYPE + COMMA_SEP +
                    BusinessTypesTable.COLUMN_BUSINESS_TYPE_ID + INT_TYPE + COMMA_SEP +
                    BusinessTypesTable.COLUMN_DESCRIPTION + TEXT_TYPE + " )";

    private static final String SQL_DROP_BUSINESS_TYPES_TABLE =
            "DROP TABLE IF EXISTS " + BusinessTypesTable.TABLE_NAME;

    private static final String SQL_CREATE_ORDERS_TABLE =
            "CREATE TABLE " + OrdersTable.TABLE_NAME + " (" +
                    OrdersTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + COMMA_SEP +
                    OrdersTable.COLUMN_ORDER_ID + INT_TYPE + COMMA_SEP +
                    OrdersTable.COLUMN_DISPLAY_NUMBER + TEXT_TYPE + COMMA_SEP +
                    OrdersTable.COLUMN_BUYER_ADDRESS_ID + INT_TYPE + COMMA_SEP +
                    OrdersTable.COLUMN_PRODUCT_COUNT + INT_TYPE + COMMA_SEP +
                    OrdersTable.COLUMN_PIECES + INT_TYPE + COMMA_SEP +
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
                    OrdersTable.COLUMN_CREATED_AT + TEXT_TYPE + COMMA_SEP +
                    OrdersTable.COLUMN_UPDATED_AT + TEXT_TYPE + COMMA_SEP +
                    OrdersTable.COLUMN_REMARKS + TEXT_TYPE + " )";

    private static final String SQL_DROP_ORDERS_TABLE =
            "DROP TABLE IF EXISTS " + OrdersTable.TABLE_NAME;

    private static final String SQL_CREATE_SUBORDERS_TABLE =
            "CREATE TABLE " + SubordersTable.TABLE_NAME + " (" +
                    SubordersTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + COMMA_SEP +
                    SubordersTable.COLUMN_ORDER_ID + INT_TYPE + COMMA_SEP +
                    SubordersTable.COLUMN_SUBORDER_ID + INT_TYPE + COMMA_SEP +
                    SubordersTable.COLUMN_DISPLAY_NUMBER + TEXT_TYPE + COMMA_SEP +
                    SubordersTable.COLUMN_SELLER_ID + INT_TYPE + COMMA_SEP +
                    SubordersTable.COLUMN_SELLER_ADDRESS_ID + INT_TYPE + COMMA_SEP +
                    SubordersTable.COLUMN_PRODUCT_COUNT + INT_TYPE + COMMA_SEP +
                    SubordersTable.COLUMN_PIECES + INT_TYPE + COMMA_SEP +
                    SubordersTable.COLUMN_RETAIL_PRICE + REAL_TYPE + COMMA_SEP +
                    SubordersTable.COLUMN_CALCULATED_PRICE + REAL_TYPE + COMMA_SEP +
                    SubordersTable.COLUMN_EDITED_PRICE + REAL_TYPE + COMMA_SEP +
                    SubordersTable.COLUMN_SHIPPING_CHARGE + REAL_TYPE + COMMA_SEP +
                    SubordersTable.COLUMN_COD_CHARGE + REAL_TYPE + COMMA_SEP +
                    SubordersTable.COLUMN_FINAL_PRICE + REAL_TYPE + COMMA_SEP +
                    SubordersTable.COLUMN_SUBORDER_STATUS_VALUE + INT_TYPE + COMMA_SEP +
                    SubordersTable.COLUMN_SUBORDER_STATUS_DISPLAY + TEXT_TYPE + COMMA_SEP +
                    SubordersTable.COLUMN_PAYMENT_STATUS_VALUE + INT_TYPE + COMMA_SEP +
                    SubordersTable.COLUMN_PAYMENT_STATUS_DISPLAY + TEXT_TYPE + COMMA_SEP +
                    SubordersTable.COLUMN_UPDATED_AT + TEXT_TYPE + COMMA_SEP +
                    SubordersTable.COLUMN_CREATED_AT + TEXT_TYPE + " )";

    private static final String SQL_DROP_SUBORDERS_TABLE =
            "DROP TABLE IF EXISTS " + SubordersTable.TABLE_NAME;

    private static final String SQL_CREATE_ORDER_ITEMS_TABLE =

            "CREATE TABLE " + OrderItemsTable.TABLE_NAME + " (" +
                    OrderItemsTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + COMMA_SEP +
                    OrderItemsTable.COLUMN_ORDER_ITEM_ID + INT_TYPE + COMMA_SEP +
                    OrderItemsTable.COLUMN_SUBORDER_ID + INT_TYPE + COMMA_SEP +
                    OrderItemsTable.COLUMN_PRODUCT_ID + INT_TYPE + COMMA_SEP +
                    OrderItemsTable.COLUMN_ORDER_SHIPMENT_ID + INT_TYPE + COMMA_SEP +
                    OrderItemsTable.COLUMN_LOTS + INT_TYPE + COMMA_SEP +
                    OrderItemsTable.COLUMN_LOT_SIZE + INT_TYPE + COMMA_SEP +
                    OrderItemsTable.COLUMN_PIECES + INT_TYPE + COMMA_SEP +
                    OrderItemsTable.COLUMN_RETAIL_PRICE_PER_PIECE + REAL_TYPE + COMMA_SEP +
                    OrderItemsTable.COLUMN_CALCULATED_PRICE_PER_PIECE + REAL_TYPE + COMMA_SEP +
                    OrderItemsTable.COLUMN_EDITED_PRICE_PER_PIECE + REAL_TYPE + COMMA_SEP +
                    OrderItemsTable.COLUMN_FINAL_PRICE + REAL_TYPE + COMMA_SEP +
                    OrderItemsTable.COLUMN_ORDER_ITEM_STATUS_VALUE + INT_TYPE + COMMA_SEP +
                    OrderItemsTable.COLUMN_ORDER_ITEM_STATUS_DISPLAY + TEXT_TYPE + COMMA_SEP +
                    OrderItemsTable.COLUMN_TRACKING_URL + TEXT_TYPE + COMMA_SEP +
                    OrderItemsTable.COLUMN_CREATED_AT + TEXT_TYPE + COMMA_SEP +
                    OrderItemsTable.COLUMN_UPDATED_AT + TEXT_TYPE + COMMA_SEP +
                    OrderItemsTable.COLUMN_REMARKS + TEXT_TYPE + " )";

    private static final String SQL_DROP_ORDER_ITEMS_TABLE =
            "DROP TABLE IF EXISTS " + OrderItemsTable.TABLE_NAME;

    private static final String SQL_CREATE_PRODUCTS_TABLE =

            "CREATE TABLE " + ProductsTable.TABLE_NAME + " (" +
                    ProductsTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + COMMA_SEP +
                    ProductsTable.COLUMN_PRODUCT_ID + INT_TYPE + COMMA_SEP +
                    ProductsTable.COLUMN_CATEGORY_ID + INT_TYPE + COMMA_SEP +
                    ProductsTable.COLUMN_SELLER_ID + INT_TYPE + COMMA_SEP +
                    ProductsTable.COLUMN_PRODUCT_DETAILS_ID + INT_TYPE + COMMA_SEP +
                    ProductsTable.COLUMN_PRODUCT_SCORE + REAL_TYPE + COMMA_SEP +
                    ProductsTable.COLUMN_DISPLAY_NAME + TEXT_TYPE + COMMA_SEP +
                    ProductsTable.COLUMN_NAME + TEXT_TYPE + COMMA_SEP +
                    ProductsTable.COLUMN_LOT_SIZE + INT_TYPE + COMMA_SEP +
                    ProductsTable.COLUMN_PRICE_PER_UNIT + REAL_TYPE + COMMA_SEP +
                    ProductsTable.COLUMN_PRICE_PER_LOT + REAL_TYPE + COMMA_SEP +
                    ProductsTable.COLUMN_MIN_PRICE_PER_UNIT + REAL_TYPE + COMMA_SEP +
                    ProductsTable.COLUMN_MARGIN + REAL_TYPE + COMMA_SEP +
                    ProductsTable.COLUMN_UNIT + TEXT_TYPE + COMMA_SEP +
                    ProductsTable.COLUMN_URL + TEXT_TYPE + COMMA_SEP +
                    ProductsTable.COLUMN_SHOW_ONLINE + INT_TYPE + COMMA_SEP +
                    ProductsTable.COLUMN_DELETE_STATUS + INT_TYPE + COMMA_SEP +
                    ProductsTable.COLUMN_IMAGE_NAME + TEXT_TYPE + COMMA_SEP +
                    ProductsTable.COLUMN_IMAGE_COUNT + INT_TYPE + COMMA_SEP +
                    ProductsTable.COLUMN_IMAGE_NUMBERS + TEXT_TYPE + COMMA_SEP +
                    ProductsTable.COLUMN_IMAGE_PATH + TEXT_TYPE + COMMA_SEP +
                    ProductsTable.COLUMN_WARRANTY + TEXT_TYPE + COMMA_SEP +
                    ProductsTable.COLUMN_SPECIAL_FEATURE + TEXT_TYPE + COMMA_SEP +
                    ProductsTable.COLUMN_AVAILABILITY + TEXT_TYPE + COMMA_SEP +
                    ProductsTable.COLUMN_STYLE + TEXT_TYPE + COMMA_SEP +
                    ProductsTable.COLUMN_MANUFACTURED_CITY + TEXT_TYPE + COMMA_SEP +
                    ProductsTable.COLUMN_PATTERN + TEXT_TYPE + COMMA_SEP +
                    ProductsTable.COLUMN_COLOURS + TEXT_TYPE + COMMA_SEP +
                    ProductsTable.COLUMN_LOT_DESCRIPTION + TEXT_TYPE + COMMA_SEP +
                    ProductsTable.COLUMN_DESCRIPTION + TEXT_TYPE + COMMA_SEP +
                    ProductsTable.WORK_DECORATION_TYPE + TEXT_TYPE + COMMA_SEP +
                    ProductsTable.COLUMN_NECK_COLLAR_TYPE + TEXT_TYPE + COMMA_SEP +
                    ProductsTable.COLUMN_FABRIC_GSM + TEXT_TYPE + COMMA_SEP +
                    ProductsTable.COLUMN_DISPATCHED_IN + TEXT_TYPE + COMMA_SEP +
                    ProductsTable.COLUMN_REMARKS + TEXT_TYPE + COMMA_SEP +
                    ProductsTable.COLUMN_SELLER_CATALOG_NUMBER + TEXT_TYPE + COMMA_SEP +
                    ProductsTable.COLUMN_SLEEVE + TEXT_TYPE + COMMA_SEP +
                    ProductsTable.COLUMN_SIZES + TEXT_TYPE + COMMA_SEP +
                    ProductsTable.COLUMN_GENDER + TEXT_TYPE + COMMA_SEP +
                    ProductsTable.COLUMN_WEIGHT_PER_UNIT + REAL_TYPE + COMMA_SEP +
                    ProductsTable.COLUMN_PACKAGING_DETAILS + TEXT_TYPE + COMMA_SEP +
                    ProductsTable.COLUMN_LENGTH + TEXT_TYPE + COMMA_SEP +
                    ProductsTable.COLUMN_PRODUCT_CREATED_AT + TEXT_TYPE + COMMA_SEP +
                    ProductsTable.COLUMN_PRODUCT_UPDATED_AT + TEXT_TYPE + COMMA_SEP +
                    ProductsTable.COLUMN_BUYER_PRODUCT_ID + INT_TYPE + COMMA_SEP +
                    ProductsTable.COLUMN_BUYER_PRODUCT_CREATED_AT + TEXT_TYPE + COMMA_SEP +
                    ProductsTable.COLUMN_BUYER_PRODUCT_UPDATED_AT + TEXT_TYPE + COMMA_SEP +
                    ProductsTable.COLUMN_BUYER_PRODUCT_IS_ACTIVE + INT_TYPE + COMMA_SEP +
                    ProductsTable.COLUMN_BUYER_PRODUCT_RESPONSE_ID + INT_TYPE + COMMA_SEP +
                    ProductsTable.COLUMN_STORE_MARGIN + REAL_TYPE + COMMA_SEP +
                    ProductsTable.COLUMN_HAS_SWIPED + INT_TYPE + COMMA_SEP +
                    ProductsTable.COLUMN_RESPONDED_FROM + INT_TYPE + COMMA_SEP +
                    ProductsTable.COLUMN_RESPONSE_CODE + INT_TYPE + COMMA_SEP +
                    ProductsTable.COLUMN_BUYER_PRODUCT_RESPONSE_CREATED_AT + TEXT_TYPE + COMMA_SEP +
                    ProductsTable.COLUMN_BUYER_PRODUCT_RESPONSE_UPDATED_AT + TEXT_TYPE + COMMA_SEP +
                    ProductsTable.COLUMN_SYNCED + INT_TYPE +
                    " )";

    private static final String SQL_DROP_PRODUCTS_TABLE =
            "DROP TABLE IF EXISTS " + ProductsTable.TABLE_NAME;

    private static final String SQL_CREATE_CATEGORIES_TABLE =

            "CREATE TABLE " + CategoriesTable.TABLE_NAME + " (" +
                    CategoriesTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + COMMA_SEP +
                    CategoriesTable.COLUMN_CATEGORY_ID + INT_TYPE + COMMA_SEP +
                    CategoriesTable.COLUMN_SHOW_ONLINE + INT_TYPE + COMMA_SEP +
                    CategoriesTable.COLUMN_CATEGORY_NAME + TEXT_TYPE + COMMA_SEP +
                    CategoriesTable.COLUMN_URL + TEXT_TYPE + COMMA_SEP +
                    CategoriesTable.COLUMN_IMAGE_URL + TEXT_TYPE + COMMA_SEP +
                    CategoriesTable.COLUMN_CREATED_AT + TEXT_TYPE + COMMA_SEP +
                    CategoriesTable.COLUMN_UPDATED_AT + TEXT_TYPE + COMMA_SEP +
                    CategoriesTable.COLUMN_BUYER_INTEREST_ID + INT_TYPE + COMMA_SEP +
                    CategoriesTable.COLUMN_BUYER_INTEREST_IS_ACTIVE + INT_TYPE + COMMA_SEP +
                    CategoriesTable.COLUMN_BUYER_INTEREST_CREATED_AT + TEXT_TYPE + COMMA_SEP +
                    CategoriesTable.COLUMN_BUYER_INTEREST_UPDATED_AT + TEXT_TYPE + COMMA_SEP +
                    CategoriesTable.COLUMN_SYNCED + INT_TYPE + COMMA_SEP +
                    CategoriesTable.COLUMN_SLUG + TEXT_TYPE + " )";


    private static final String SQL_DROP_CATEGORIES_TABLE =
            "DROP TABLE IF EXISTS " + CategoriesTable.TABLE_NAME;

    private static final String SQL_CREATE_SELLERS_TABLE =
            "CREATE TABLE " + SellersTable.TABLE_NAME + " (" +
                    SellersTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + COMMA_SEP +
                    SellersTable.COLUMN_SELLER_ID + INT_TYPE + COMMA_SEP +
                    SellersTable.COLUMN_COMPANY_NAME + TEXT_TYPE + COMMA_SEP +
                    SellersTable.COLUMN_NAME + TEXT_TYPE + COMMA_SEP +
                    SellersTable.COLUMN_COMPANY_PROFILE + TEXT_TYPE + COMMA_SEP +
                    SellersTable.COLUMN_SHOW_ONLINE + INT_TYPE + COMMA_SEP +
                    SellersTable.COLUMN_CREATED_AT + TEXT_TYPE + COMMA_SEP +
                    SellersTable.COLUMN_UPDATED_AT + TEXT_TYPE + " )";

    private static final String SQL_DROP_SELLERS_TABLE =
            "DROP TABLE IF EXISTS " + SellersTable.TABLE_NAME;

    private static final String SQL_CREATE_SELLER_ADDRESS_TABLE =

            "CREATE TABLE " + SellerAddressTable.TABLE_NAME + " (" +
                    SellerAddressTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + COMMA_SEP +
                    SellerAddressTable.COLUMN_SELLER_ID + INT_TYPE + COMMA_SEP +
                    SellerAddressTable.COLUMN_ADDRESS_ID + INT_TYPE + COMMA_SEP +
                    SellerAddressTable.COLUMN_ADDRESS_HISTORY_ID + INT_TYPE + COMMA_SEP +
                    SellerAddressTable.COLUMN_CITY + TEXT_TYPE + COMMA_SEP +
                    SellerAddressTable.COLUMN_STATE + TEXT_TYPE + COMMA_SEP +
                    SellerAddressTable.COLUMN_LANDMARK + TEXT_TYPE + COMMA_SEP +
                    SellerAddressTable.COLUMN_ADDRESS + TEXT_TYPE + COMMA_SEP +
                    SellerAddressTable.COLUMN_CONTACT_NUMBER + TEXT_TYPE + COMMA_SEP +
                    SellerAddressTable.COLUMN_CREATED_AT + TEXT_TYPE + COMMA_SEP +
                    SellerAddressTable.COLUMN_UPDATED_AT + TEXT_TYPE + COMMA_SEP +
                    SellerAddressTable.COLUMN_PINCODE + TEXT_TYPE + " )";

    private static final String SQL_DROP_SELLER_ADDRESS_TABLE =
            "DROP TABLE IF EXISTS " + SellerAddressTable.TABLE_NAME;

    private static final String SQL_CREATE_CATEGORY_SELLER_TABLE =
            "CREATE TABLE " + CategorySellersTable.TABLE_NAME + " (" +
                    CategorySellersTable.COLUMN_CATEGORY_ID + INT_TYPE + COMMA_SEP +
                    CategorySellersTable.COLUMN_SELLER_ID + INT_TYPE + COMMA_SEP +
                    CategorySellersTable.COLUMN_COMPANY_NAME + TEXT_TYPE + " )";

    private static final String SQL_DROP_CATEGORY_SELLER_TABLE =
            "DROP TABLE IF EXISTS " + CategorySellersTable.TABLE_NAME;

    private static final String SQL_CREATE_CART_TABLE =
            "CREATE TABLE " + CartTable.TABLE_NAME + " (" +
                    CartTable.COLUMN_CART_ID + INT_TYPE + COMMA_SEP +
                    CartTable.COLUMN_PRODUCT_COUNT + INT_TYPE + COMMA_SEP +
                    CartTable.COLUMN_PIECES + INT_TYPE + COMMA_SEP +
                    CartTable.COLUMN_RETAIL_PRICE + REAL_TYPE + COMMA_SEP +
                    CartTable.COLUMN_CALCULATED_PRICE + REAL_TYPE + COMMA_SEP +
                    CartTable.COLUMN_SHIPPING_CHARGE + REAL_TYPE + COMMA_SEP +
                    CartTable.COLUMN_FINAL_PRICE + REAL_TYPE + COMMA_SEP +
                    CartTable.COLUMN_CREATED_AT + TEXT_TYPE + COMMA_SEP +
                    CartTable.COLUMN_UPDATED_AT + TEXT_TYPE + COMMA_SEP +
                    CartTable.COLUMN_SYNCED + TEXT_TYPE + " )";

    private static final String SQL_DROP_CART_TABLE =
            "DROP TABLE IF EXISTS " + CartTable.TABLE_NAME;

    private static final String SQL_CREATE_SUBCART_TABLE =
            "CREATE TABLE " + SubCartsTable.TABLE_NAME + " (" +
                    SubCartsTable.COLUMN_CART_ID + INT_TYPE + COMMA_SEP +
                    SubCartsTable.COLUMN_SELLER_ID + INT_TYPE + COMMA_SEP +
                    SubCartsTable.COLUMN_SUBCART_ID + INT_TYPE + COMMA_SEP +
                    SubCartsTable.COLUMN_PRODUCT_COUNT + INT_TYPE + COMMA_SEP +
                    SubCartsTable.COLUMN_PIECES + INT_TYPE + COMMA_SEP +
                    SubCartsTable.COLUMN_RETAIL_PRICE + REAL_TYPE + COMMA_SEP +
                    SubCartsTable.COLUMN_CALCULATED_PRICE + REAL_TYPE + COMMA_SEP +
                    SubCartsTable.COLUMN_SHIPPING_CHARGE + REAL_TYPE + COMMA_SEP +
                    SubCartsTable.COLUMN_FINAL_PRICE + REAL_TYPE + COMMA_SEP +
                    SubCartsTable.COLUMN_CREATED_AT + TEXT_TYPE + COMMA_SEP +
                    SubCartsTable.COLUMN_UPDATED_AT + TEXT_TYPE + COMMA_SEP +
                    SubCartsTable.COLUMN_SYNCED + TEXT_TYPE + " )";

    private static final String SQL_DROP_SUBCART_TABLE =
            "DROP TABLE IF EXISTS " + SubCartsTable.TABLE_NAME;

    private static final String SQL_CREATE_CART_ITEMS_TABLE =
            "CREATE TABLE " + CartItemsTable.TABLE_NAME + " (" +
                    CartItemsTable.COLUMN_CART_ITEM_ID + INT_TYPE + COMMA_SEP +
                    CartItemsTable.COLUMN_PRODUCT_ID + INT_TYPE + COMMA_SEP +
                    CartItemsTable.COLUMN_SUBCART_ID + INT_TYPE + COMMA_SEP +
                    CartItemsTable.COLUMN_LOTS + INT_TYPE + COMMA_SEP +
                    CartItemsTable.COLUMN_PIECES + INT_TYPE + COMMA_SEP +
                    CartItemsTable.COLUMN_LOT_SIZE + INT_TYPE + COMMA_SEP +
                    CartItemsTable.COLUMN_RETAIL_PRICE_PER_PIECE + REAL_TYPE + COMMA_SEP +
                    CartItemsTable.COLUMN_CALCULATED_PRICE_PER_PIECE + REAL_TYPE + COMMA_SEP +
                    CartItemsTable.COLUMN_SHIPPING_CHARGE + REAL_TYPE + COMMA_SEP +
                    CartItemsTable.COLUMN_FINAL_PRICE + REAL_TYPE + COMMA_SEP +
                    CartItemsTable.COLUMN_CREATED_AT + TEXT_TYPE + COMMA_SEP +
                    CartItemsTable.COLUMN_UPDATED_AT + TEXT_TYPE + COMMA_SEP +
                    CartItemsTable.COLUMN_SYNCED + TEXT_TYPE + " )";

    private static final String SQL_DROP_CART_ITEMS_TABLE =
            "DROP TABLE IF EXISTS " + CartItemsTable.TABLE_NAME;

    private static final String SQL_CREATE_NOTIFICATION_TABLE =
            "CREATE TABLE " + NotificationTable.TABLE_NAME + " (" +
                    NotificationTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + COMMA_SEP +
                    NotificationTable.COLUMN_NOTIFICATION_TYPE + TEXT_TYPE + COMMA_SEP +
                    NotificationTable.COLUMN_NOTIFICATION_TIME + TEXT_TYPE + COMMA_SEP +
                    NotificationTable.COLUMN_NOTIFICATION_JSON + TEXT_TYPE + " )";

    private static final String SQL_DROP_NOTIFICATION_TABLE =
            "DROP TABLE IF EXISTS " + NotificationTable.TABLE_NAME;

    private static DatabaseHelper instance;

    //TODO : CREATE indexes on certain columns if necessary like product_id

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

        sqLiteDatabase.execSQL(SQL_CREATE_ORDERS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_SUBORDERS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_ORDER_ITEMS_TABLE);

        sqLiteDatabase.execSQL(SQL_CREATE_PRODUCTS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_CATEGORIES_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_SELLERS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_SELLER_ADDRESS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_CATEGORY_SELLER_TABLE);

        sqLiteDatabase.execSQL(SQL_CREATE_CART_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_SUBCART_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_CART_ITEMS_TABLE);

        sqLiteDatabase.execSQL(SQL_CREATE_NOTIFICATION_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL(SQL_DROP_USER_TABLE);
        sqLiteDatabase.execSQL(SQL_DROP_USER_ADDRESS_TABLE);
        sqLiteDatabase.execSQL(SQL_DROP_BUSINESS_TYPES_TABLE);

        sqLiteDatabase.execSQL(SQL_DROP_ORDERS_TABLE);
        sqLiteDatabase.execSQL(SQL_DROP_SUBORDERS_TABLE);
        sqLiteDatabase.execSQL(SQL_DROP_ORDER_ITEMS_TABLE);

        sqLiteDatabase.execSQL(SQL_DROP_PRODUCTS_TABLE);
        sqLiteDatabase.execSQL(SQL_DROP_CATEGORIES_TABLE);
        sqLiteDatabase.execSQL(SQL_DROP_SELLERS_TABLE);
        sqLiteDatabase.execSQL(SQL_DROP_SELLER_ADDRESS_TABLE);
        sqLiteDatabase.execSQL(SQL_DROP_CATEGORY_SELLER_TABLE);

        sqLiteDatabase.execSQL(SQL_DROP_CART_TABLE);
        sqLiteDatabase.execSQL(SQL_DROP_SUBCART_TABLE);
        sqLiteDatabase.execSQL(SQL_DROP_CART_ITEMS_TABLE);

        sqLiteDatabase.execSQL(SQL_DROP_NOTIFICATION_TABLE);

        onCreate(sqLiteDatabase);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
