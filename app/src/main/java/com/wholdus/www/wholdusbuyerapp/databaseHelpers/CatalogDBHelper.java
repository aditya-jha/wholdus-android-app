package com.wholdus.www.wholdusbuyerapp.databaseHelpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;

import com.wholdus.www.wholdusbuyerapp.databaseContracts.CatalogContract.CategoriesTable;
import com.wholdus.www.wholdusbuyerapp.databaseContracts.CatalogContract.ProductsTable;
import com.wholdus.www.wholdusbuyerapp.databaseContracts.CatalogContract.SellerAddressTable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import static com.wholdus.www.wholdusbuyerapp.databaseContracts.CatalogContract.CategorySellersTable;
import static com.wholdus.www.wholdusbuyerapp.databaseContracts.CatalogContract.SellersTable;


/**
 * Created by aditya on 10/12/16.
 * For database interactions related to catalog data
 */

public class CatalogDBHelper extends BaseDBHelper {

    public CatalogDBHelper(Context context) {
        super(context);
    }

    private SparseArray<String> mPresentSellerIDs;
    private SparseArray<String> mPresentProductIDs;
    private SparseArray<String> mPresentBuyerProductIDs;
    private SparseArray<String> mPresentBuyerProductResponseIDs;
    private SparseArray<String> mPresentSellerAddressIDs;
    private SparseArray<String> mPresentSellerAddressHistoryIDs;
    private SparseArray<String> mPresentCategoryIDs;

    public static String[] BasicProductColumns = {ProductsTable._ID, ProductsTable.COLUMN_PRODUCT_ID, ProductsTable.COLUMN_SELLER_ID,
            ProductsTable.COLUMN_CATEGORY_ID, ProductsTable.COLUMN_PRICE_PER_UNIT, ProductsTable.COLUMN_LOT_SIZE, ProductsTable.COLUMN_PRICE_PER_LOT,
            ProductsTable.COLUMN_MIN_PRICE_PER_UNIT, ProductsTable.COLUMN_MARGIN, ProductsTable.COLUMN_URL, ProductsTable.COLUMN_IMAGE_NAME,
            ProductsTable.COLUMN_IMAGE_COUNT, ProductsTable.COLUMN_IMAGE_NUMBERS, ProductsTable.COLUMN_IMAGE_PATH, ProductsTable.COLUMN_NAME,
            ProductsTable.COLUMN_COLOURS, ProductsTable.COLUMN_FABRIC_GSM, ProductsTable.COLUMN_SIZES};

    public static String[] ExtraProductColumns = {ProductsTable.COLUMN_PRODUCT_DETAILS_ID, ProductsTable.COLUMN_UNIT, ProductsTable.COLUMN_DISPLAY_NAME,
            ProductsTable.COLUMN_DELETE_STATUS, ProductsTable.COLUMN_SHOW_ONLINE, ProductsTable.COLUMN_WARRANTY, ProductsTable.COLUMN_SPECIAL_FEATURE,
            ProductsTable.COLUMN_AVAILABILITY, ProductsTable.COLUMN_STYLE, ProductsTable.COLUMN_MANUFACTURED_CITY, ProductsTable.COLUMN_PATTERN,
            ProductsTable.COLUMN_LOT_DESCRIPTION, ProductsTable.COLUMN_DESCRIPTION, ProductsTable.WORK_DECORATION_TYPE, ProductsTable.COLUMN_NECK_COLLAR_TYPE,
            ProductsTable.COLUMN_DISPATCHED_IN, ProductsTable.COLUMN_REMARKS, ProductsTable.COLUMN_SELLER_CATALOG_NUMBER, ProductsTable.COLUMN_SLEEVE,
            ProductsTable.COLUMN_GENDER, ProductsTable.COLUMN_WEIGHT_PER_UNIT, ProductsTable.COLUMN_PACKAGING_DETAILS, ProductsTable.COLUMN_LENGTH,
            ProductsTable.COLUMN_PRODUCT_CREATED_AT, ProductsTable.COLUMN_PRODUCT_UPDATED_AT};


    public Cursor getAllCategories(boolean productsCount) {
        String countQuery = "";
        if (productsCount) {
            countQuery = ", (SELECT COUNT(*) FROM " + ProductsTable.TABLE_NAME + " AS P " +
                    " WHERE C." + CategoriesTable.COLUMN_CATEGORY_ID + " = P." + ProductsTable.COLUMN_CATEGORY_ID + ") AS " +
                    CategoriesTable.COLUMN_PRODUCTS_COUNT;
        }
        String query = "SELECT C.*" + countQuery + " FROM " + CategoriesTable.TABLE_NAME + " AS C";
        ;
        return getCursor(query);
    }

    public Cursor getCategoryData(int categoryID, int showOnline, @Nullable String[] columns){
        String columnNames = getColumnNamesString(columns);
        String query = "SELECT " + columnNames + " FROM " + CategoriesTable.TABLE_NAME;
        boolean whereApplied = false;
        if (categoryID != -1){
            query += " WHERE " + CategoriesTable.COLUMN_CATEGORY_ID + " = " + categoryID;
            whereApplied = true;
        }
        if (showOnline != -1){
            query += whereClauseHelper(whereApplied) + CategoriesTable.COLUMN_SHOW_ONLINE + " = " + showOnline;
        }
        return getCursor(query);
    }

    /**
     * @param orderBy use as "column_name" + "sort_order"(default ASC)
     *                to use descending, " DESC "
     * @return
     */

    public Cursor getProductData(@Nullable ArrayList<Integer> productIDs,
                                 @Nullable ArrayList<Integer> excludeProductIDs,
                                 @Nullable ArrayList<Integer> buyerProductIDs,
                                 @Nullable ArrayList<Integer> excludeBuyerProductIDs,
                                 @Nullable ArrayList<Integer> buyerProductResponseIDs,
                                 @Nullable ArrayList<Integer> excludeBuyerProductResponseIDs,
                                 @Nullable HashSet<String> sellerIDs,
                                 @Nullable HashSet<Integer> categoryIDs,
                                 int priceGreaterThan,
                                 int priceLowerThan,
                                 @Nullable HashSet<String> fabrics,
                                 @Nullable HashSet<String> colours,
                                 @Nullable HashSet<String> sizes,
                                 @Nullable ArrayList<Integer> responseCodes,
                                 int deleteStatus,
                                 int showOnline,
                                 int buyerProductActive,
                                 int synced,
                                 @Nullable String[] orderBy,
                                 int limit,
                                 int offset,
                                 @Nullable String[] columns) {
        String columnNames = getColumnNamesString(columns);
        String query = "SELECT " + columnNames + " FROM " + ProductsTable.TABLE_NAME;
        boolean whereApplied = false;

        if (productIDs != null && !productIDs.isEmpty()) {
            query += " WHERE " + ProductsTable.COLUMN_PRODUCT_ID + " IN (" + TextUtils.join(", ", productIDs) + ") ";
            whereApplied = true;
        }
        if (excludeProductIDs != null && !excludeProductIDs.isEmpty()) {
            query += whereClauseHelper(whereApplied) + ProductsTable.COLUMN_PRODUCT_ID + " NOT IN (" + TextUtils.join(", ", excludeProductIDs) + ") ";
            whereApplied = true;
        }
        if (buyerProductIDs != null && !buyerProductIDs.isEmpty()) {
            query += " WHERE " + ProductsTable.COLUMN_BUYER_PRODUCT_ID + " IN (" + TextUtils.join(", ", buyerProductIDs) + ") ";
            whereApplied = true;
        }
        if (excludeBuyerProductIDs != null && !excludeBuyerProductIDs.isEmpty()) {
            query += whereClauseHelper(whereApplied) + ProductsTable.COLUMN_BUYER_PRODUCT_ID + " NOT IN (" + TextUtils.join(", ", excludeBuyerProductIDs) + ") ";
            whereApplied = true;
        }
        if (buyerProductResponseIDs != null && !buyerProductResponseIDs.isEmpty()) {
            query += " WHERE " + ProductsTable.COLUMN_BUYER_PRODUCT_RESPONSE_ID + " IN (" + TextUtils.join(", ", buyerProductResponseIDs) + ") ";
            whereApplied = true;
        }
        if (excludeBuyerProductResponseIDs != null && !excludeBuyerProductResponseIDs.isEmpty()) {
            query += whereClauseHelper(whereApplied) + ProductsTable.COLUMN_BUYER_PRODUCT_RESPONSE_ID + " NOT IN (" + TextUtils.join(", ", excludeBuyerProductResponseIDs) + ") ";
            whereApplied = true;
        }

        if (sellerIDs != null && !sellerIDs.isEmpty()) {
            query += whereClauseHelper(whereApplied) + ProductsTable.COLUMN_SELLER_ID + " IN (" + TextUtils.join(", ", sellerIDs) + ") ";
            whereApplied = true;
        }
        if (categoryIDs != null && !categoryIDs.isEmpty()) {
            query += whereClauseHelper(whereApplied) + ProductsTable.COLUMN_CATEGORY_ID + " IN (" + TextUtils.join(", ", categoryIDs) + ") ";
            whereApplied = true;
        }
        if (priceGreaterThan != -1) {
            query += whereClauseHelper(whereApplied) + ProductsTable.COLUMN_MIN_PRICE_PER_UNIT + " >= " + priceGreaterThan;
            whereApplied = true;
        }
        if (priceLowerThan != -1) {
            query += whereClauseHelper(whereApplied) + ProductsTable.COLUMN_MIN_PRICE_PER_UNIT + " <= " + priceLowerThan;
            whereApplied = true;
        }
        if (fabrics != null && !fabrics.isEmpty()) {
            query += whereClauseHelper(whereApplied) + " ( LOWER(" + ProductsTable.COLUMN_FABRIC_GSM + ") LIKE LOWER('%" +
                    TextUtils.join("%') OR LOWER(" + ProductsTable.COLUMN_FABRIC_GSM + ") LIKE LOWER('%", fabrics) + "%') ) ";
            whereApplied = true;
        }
        if (colours != null && !colours.isEmpty()) {
            query += whereClauseHelper(whereApplied) + " ( LOWER(" + ProductsTable.COLUMN_COLOURS + ") LIKE LOWER('%" +
                    TextUtils.join("%') OR LOWER(" + ProductsTable.COLUMN_COLOURS + ") LIKE LOWER('%", colours) + "%') ) ";
            whereApplied = true;
        }
        if (sizes != null && !sizes.isEmpty()) {
            query += whereClauseHelper(whereApplied) + " ( LOWER(" + ProductsTable.COLUMN_SIZES + ") LIKE LOWER('%" +
                    TextUtils.join("%') OR LOWER(" + ProductsTable.COLUMN_SIZES + ") LIKE LOWER('%", sizes) + "%') ) ";
            whereApplied = true;
        }
        if (responseCodes != null && !responseCodes.isEmpty()) {
            query += whereClauseHelper(whereApplied) + ProductsTable.COLUMN_RESPONSE_CODE + " IN (" + TextUtils.join(", ", responseCodes) + ") ";
            whereApplied = true;
        }
        if (deleteStatus != -1) {
            query += whereClauseHelper(whereApplied) + ProductsTable.COLUMN_DELETE_STATUS + " = " + deleteStatus;
            whereApplied = true;
        }
        if (showOnline != -1) {
            query += whereClauseHelper(whereApplied) + ProductsTable.COLUMN_SHOW_ONLINE + " = " + showOnline;
            whereApplied = true;
        }
        if (buyerProductActive != -1) {
            query += whereClauseHelper(whereApplied) + ProductsTable.COLUMN_BUYER_PRODUCT_IS_ACTIVE + " = " + buyerProductActive;
            whereApplied = true;
        }
        if (synced != -1) {
            query += whereClauseHelper(whereApplied) + ProductsTable.COLUMN_SYNCED + " = " + synced;
            whereApplied = true;
        }
        if (orderBy == null || orderBy.length == 0) {
            orderBy = new String[]{ProductsTable.COLUMN_PRODUCT_ID + " DESC "};
        }
        query += " ORDER BY " + TextUtils.join(", ", orderBy);
        if (limit != -1) {
            query += " LIMIT " + limit;
        }
        if (offset != -1) {
            query += " OFFSET " + offset;
        }

        return getCursor(query);
    }

    public Cursor getSellerData(@Nullable Integer sellerID, @Nullable String[] columns) {
        String columnNames = getColumnNamesString(columns);

        String query = "SELECT " + columnNames + " FROM " + SellersTable.TABLE_NAME;

        if (sellerID != null && sellerID != -1) {
            query += " WHERE " + SellersTable.COLUMN_SELLER_ID + " = " + sellerID;
        }

        return getCursor(query);
    }

    /**
     * Seller Address IDs and Seller Address History IDs stored separately
     * Pass -1 for integer to not get the corresponding values
     * Seller Address IDs and Seller Address History IDs are stored by 0 as default
     * Pass address Id or address history ID as 0 when they are not wanted
     */

    public Cursor getSellerAddressData(int sellerAddressID,
                                       int sellerAddressHistoryID, int sellerID, @Nullable String[] columns) {
        String columnNames = getColumnNamesString(columns);

        String query = "SELECT " + columnNames + " FROM " + SellerAddressTable.TABLE_NAME;
        boolean whereApplied = false;
        if (sellerAddressID != -1) {
            query += " WHERE " + SellerAddressTable.COLUMN_ADDRESS_ID + " = " + sellerAddressID;
            whereApplied = true;
        }

        if (sellerAddressHistoryID != -1) {
            query += whereClauseHelper(whereApplied) + SellerAddressTable.COLUMN_ADDRESS_HISTORY_ID + " = " + sellerAddressHistoryID;
            whereApplied = true;
        }

        if (sellerID != -1) {
            query += whereClauseHelper(whereApplied) + SellerAddressTable.COLUMN_SELLER_ID + " = " + sellerID;
        }

        return getCursor(query);
    }


    public SparseArray<String> getPresentSellerIDs() {
        if (mPresentSellerIDs != null) {
            return mPresentSellerIDs;
        }
        String[] columns = new String[]{SellersTable.COLUMN_SELLER_ID, SellersTable.COLUMN_UPDATED_AT};
        Cursor cursor = getSellerData(null, columns);
        SparseArray<String> sellerIDs = new SparseArray<>();
        while (cursor.moveToNext()) {
            sellerIDs.put(cursor.getInt(cursor.getColumnIndexOrThrow(SellersTable.COLUMN_SELLER_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(SellersTable.COLUMN_UPDATED_AT)));
        }
        mPresentSellerIDs = sellerIDs;
        return sellerIDs;
    }

    public SparseArray<String> getPresentProductIDs() {
        if (mPresentProductIDs != null) {
            return mPresentProductIDs;
        }
        String[] columns = {ProductsTable.COLUMN_PRODUCT_ID, ProductsTable.COLUMN_PRODUCT_UPDATED_AT};
        Cursor cursor = getProductData(null, null, null, null, null, null, null, null, -1, -1, null, null, null, null, -1, -1, -1, -1, null, -1, -1, columns);
        SparseArray<String> productIDs = new SparseArray<>();
        while (cursor.moveToNext()) {
            productIDs.put(cursor.getInt(cursor.getColumnIndexOrThrow(ProductsTable.COLUMN_PRODUCT_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(ProductsTable.COLUMN_PRODUCT_UPDATED_AT)));
        }
        mPresentProductIDs = productIDs;
        return productIDs;
    }

    public SparseArray<String> getPresentBuyerProductIDs(){
        if (mPresentBuyerProductIDs!= null){
            return mPresentBuyerProductIDs;
        }
        String[] columns = {ProductsTable.COLUMN_BUYER_PRODUCT_ID, ProductsTable.COLUMN_BUYER_PRODUCT_UPDATED_AT};
        ArrayList<Integer> excludeBuyerProductIDs = new ArrayList<>();
        excludeBuyerProductIDs.add(0);
        Cursor cursor = getProductData(null, null, null, excludeBuyerProductIDs,null, null, null, null, -1, -1, null, null, null, null, -1, -1, -1, -1, null, -1, -1, columns);
        SparseArray<String> buyerProductsIDs = new SparseArray<>();
        while (cursor.moveToNext()) {
            buyerProductsIDs.put(cursor.getInt(cursor.getColumnIndexOrThrow(ProductsTable.COLUMN_BUYER_PRODUCT_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(ProductsTable.COLUMN_BUYER_PRODUCT_UPDATED_AT)));
        }
        mPresentBuyerProductIDs = buyerProductsIDs;
        return buyerProductsIDs;
    }

    public SparseArray<String> getPresentBuyerResponseProductIDs(){
        if (mPresentBuyerProductResponseIDs!= null){
            return mPresentBuyerProductResponseIDs;
        }
        String[] columns = {ProductsTable.COLUMN_BUYER_PRODUCT_RESPONSE_ID, ProductsTable.COLUMN_BUYER_PRODUCT_RESPONSE_UPDATED_AT};
        ArrayList<Integer> excludeBuyerProductResponseIDs = new ArrayList<>();
        excludeBuyerProductResponseIDs.add(0);
        excludeBuyerProductResponseIDs.add(-1);
        Cursor cursor = getProductData(null, null, null, null,null, excludeBuyerProductResponseIDs, null, null, -1, -1, null, null, null, null, -1, -1, -1, -1, null, -1, -1, columns);
        SparseArray<String> buyerProductResponseIDs = new SparseArray<>();
        while (cursor.moveToNext()) {
            buyerProductResponseIDs.put(cursor.getInt(cursor.getColumnIndexOrThrow(ProductsTable.COLUMN_BUYER_PRODUCT_RESPONSE_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(ProductsTable.COLUMN_BUYER_PRODUCT_RESPONSE_UPDATED_AT)));
        }
        mPresentBuyerProductResponseIDs = buyerProductResponseIDs;
        return buyerProductResponseIDs;
    }

    private SparseArray<String> getPresentCategoryIDs() {
        if (mPresentCategoryIDs != null){
            return mPresentCategoryIDs;
        }
        String[] columns = {CategoriesTable.COLUMN_CATEGORY_ID, CategoriesTable.COLUMN_UPDATED_AT};
        Cursor cursor = getCategoryData(-1, -1, columns);
        SparseArray<String> categories = new SparseArray<>();
        while (cursor.moveToNext()) {
            categories.put(cursor.getInt(cursor.getColumnIndexOrThrow(CategoriesTable.COLUMN_CATEGORY_ID))
                    , cursor.getString(cursor.getColumnIndexOrThrow(CategoriesTable.COLUMN_UPDATED_AT)));
        }
        mPresentCategoryIDs = categories;
        return categories;
    }

    public SparseArray<String> getPresentSellerAddressIDs() {
        if (mPresentSellerAddressIDs != null) {
            return mPresentSellerAddressIDs;
        }
        String[] columns = new String[]{SellerAddressTable.COLUMN_ADDRESS_ID, SellerAddressTable.COLUMN_UPDATED_AT};
        Cursor cursor = getSellerAddressData(-1, 0, -1, columns);
        SparseArray<String> sellerAddressIDs = new SparseArray<>();
        while (cursor.moveToNext()) {
            sellerAddressIDs.put(cursor.getInt(cursor.getColumnIndexOrThrow(SellerAddressTable.COLUMN_ADDRESS_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(SellerAddressTable.COLUMN_UPDATED_AT)));
        }
        mPresentSellerAddressIDs = sellerAddressIDs;
        return sellerAddressIDs;
    }

    public SparseArray<String> getPresentSellerAddressHistoryIDs() {
        if (mPresentSellerAddressHistoryIDs != null) {
            return mPresentSellerAddressHistoryIDs;
        }
        String[] columns = new String[]{SellerAddressTable.COLUMN_ADDRESS_HISTORY_ID, SellerAddressTable.COLUMN_UPDATED_AT};
        Cursor cursor = getSellerAddressData(0, -1, -1, columns);
        SparseArray<String> sellerAddressHistoryIDs = new SparseArray<>();
        while (cursor.moveToNext()) {
            sellerAddressHistoryIDs.put(cursor.getInt(cursor.getColumnIndexOrThrow(SellerAddressTable.COLUMN_ADDRESS_HISTORY_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(SellerAddressTable.COLUMN_UPDATED_AT)));
        }
        mPresentSellerAddressHistoryIDs = sellerAddressHistoryIDs;
        return sellerAddressHistoryIDs;
    }

    public int updateCategories(JSONObject data) {
        int insertedUpdatedCount = 0;
        SQLiteDatabase db = mDatabaseHelper.openDatabase();

        try {
            JSONArray categories = data.getJSONArray(CategoriesTable.TABLE_NAME);
            db.beginTransaction();
            for (int i = 0; i < categories.length(); i++) {
                JSONObject category = categories.getJSONObject(i);
                insertedUpdatedCount += saveCategoryData(category);
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            insertedUpdatedCount = 0;
        } finally {
            db.endTransaction();
        }

        mDatabaseHelper.closeDatabase();
        return insertedUpdatedCount;
    }

    public void saveSellerData(JSONObject seller) throws JSONException {
        SQLiteDatabase db = mDatabaseHelper.openDatabase();
        int sellerID = seller.getInt(SellersTable.COLUMN_SELLER_ID);
        if (!seller.has(SellersTable.COLUMN_UPDATED_AT)) {
            return;
        }
        String sellerUpdatedAtLocal = getPresentSellerIDs().get(sellerID);
        String sellerUpdatedAtServer = seller.getString(SellersTable.COLUMN_UPDATED_AT);
        if (sellerUpdatedAtLocal == null) { // insert
            ContentValues values = getSellerContentValues(seller);
            db.insert(SellersTable.TABLE_NAME, null, values);
            mPresentSellerIDs.put(sellerID, sellerUpdatedAtServer);
        } else if (!sellerUpdatedAtLocal.equals(sellerUpdatedAtServer)) {
            ContentValues values = getSellerContentValues(seller);
            String selection = SellersTable.COLUMN_SELLER_ID + " = " + sellerID;
            db.update(SellersTable.TABLE_NAME, values, selection, null);
            mPresentSellerIDs.put(sellerID, sellerUpdatedAtServer);
        }
        if (seller.has("address")) {
            saveSellerAddressFromJsonArray(seller.getJSONArray("address"));
        }
        mDatabaseHelper.closeDatabase();
    }

    public int saveProductsFromJSONArray(JSONArray products) {
        SQLiteDatabase db = mDatabaseHelper.openDatabase();
        int insertedUpdated = 0;

        try {
            db.beginTransaction();
            for (int i = 0; i < products.length(); i++) {
                JSONObject product = products.getJSONObject(i);
                insertedUpdated += saveProductData(product);
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            insertedUpdated = 0;
        } finally {
            db.endTransaction();
        }
        return insertedUpdated;
    }

    public int saveCategoryData(JSONObject category) throws JSONException{
        SQLiteDatabase db = mDatabaseHelper.openDatabase();
        int insertUpdated = 0;
        int categoryID = category.getInt(CategoriesTable.COLUMN_CATEGORY_ID);
        String categoryUpdatedAtLocal = getPresentCategoryIDs().get(categoryID);
        String categoryUpdatedAtServer = category.getString(CategoriesTable.COLUMN_UPDATED_AT);
        if (categoryUpdatedAtLocal == null) { // categoryID not present
            ContentValues cv = getCategoryContentValueFromJSON(category);
            db.insert(CategoriesTable.TABLE_NAME, null, cv);
            insertUpdated = 1;
        } else if (!categoryUpdatedAtLocal.equals(categoryUpdatedAtServer)) {
            ContentValues cv = getCategoryContentValueFromJSON(category);
            String selection = CategoriesTable.COLUMN_CATEGORY_ID + " = " + categoryID;
            db.update(CategoriesTable.TABLE_NAME, cv, selection, null);
            mPresentCategoryIDs.put(categoryID, categoryUpdatedAtServer);
            insertUpdated = 1;
        }

        // if seller categories key is present, add it
        if (category.has(CategorySellersTable.TABLE_NAME)) {
            updateCategorySellers(category.getJSONArray(CategorySellersTable.TABLE_NAME), categoryID);
        }
        mDatabaseHelper.closeDatabase();
        return insertUpdated;
    }

    public int saveProductData(JSONObject product) throws JSONException {
        SQLiteDatabase db = mDatabaseHelper.openDatabase();
        int productID = product.getInt(ProductsTable.COLUMN_PRODUCT_ID);
        if (product.has("seller")) {
            saveSellerData(product.getJSONObject("seller"));
        }
        if (!product.has("details") || !product.has("image")) {
            return 0;
        }
        int insertUpdated = 0;
        // TODO: Save category data and product data
        String productUpdatedAtLocal = getPresentProductIDs().get(productID);
        String productUpdatedAtServer = product.getString(ProductsTable.COLUMN_PRODUCT_UPDATED_AT);
        if (productUpdatedAtLocal == null) { // insert
            ContentValues values = getProductContentValues(product);
            values.putAll(getEmptyBuyerProductContentValues());
            values.putAll(getEmptyBuyerProductResponseContentValues());
            db.insert(ProductsTable.TABLE_NAME, null, values);
            mPresentProductIDs.put(productID, productUpdatedAtServer);
            insertUpdated = 1;
        } else if (!productUpdatedAtLocal.equals(productUpdatedAtServer)) {
            ContentValues values = getProductContentValues(product);
            String selection = ProductsTable.COLUMN_PRODUCT_ID + " = " + productID;
            db.update(ProductsTable.TABLE_NAME, values, selection, null);
            mPresentProductIDs.put(productID, productUpdatedAtServer);
            insertUpdated = 1;
        }
        mDatabaseHelper.closeDatabase();
        return insertUpdated;
    }

    public void saveBuyerProductData(JSONObject buyerProduct) throws JSONException{
        SQLiteDatabase db = mDatabaseHelper.openDatabase();
        if (!buyerProduct.has("product")){
            return;
        }
        JSONObject product = buyerProduct.getJSONObject("product");
        saveProductData(product);
        int productID = product.getInt(ProductsTable.COLUMN_PRODUCT_ID);
        int buyerProductID = buyerProduct.getInt(ProductsTable.COLUMN_BUYER_PRODUCT_ID);
        String buyerProductUpdatedAtLocal = getPresentBuyerProductIDs().get(buyerProductID);
        String buyerProductUpdatedAtServer = buyerProduct.getString(ProductsTable.COLUMN_PRODUCT_UPDATED_AT);
        if (buyerProductUpdatedAtLocal == null || !buyerProductUpdatedAtLocal.equals(buyerProductUpdatedAtServer)) {
            ContentValues values = getBuyerProductContentValuesFromJSONObject(buyerProduct);
            String selection = ProductsTable.COLUMN_PRODUCT_ID + " = " + productID;
            db.update(ProductsTable.TABLE_NAME, values, selection, null);
            mPresentBuyerProductIDs.put(buyerProductID, buyerProductUpdatedAtServer);
        }
        mDatabaseHelper.closeDatabase();
    }

    public void saveBuyerProductResponseData(JSONObject buyerProductResponse) throws JSONException{
        SQLiteDatabase db = mDatabaseHelper.openDatabase();
        if (!buyerProductResponse.has("product")){
            return;
        }
        JSONObject product = buyerProductResponse.getJSONObject("product");
        saveProductData(product);
        int productID = product.getInt(ProductsTable.COLUMN_PRODUCT_ID);
        int buyerProductResponseID = buyerProductResponse.getInt(ProductsTable.COLUMN_BUYER_PRODUCT_RESPONSE_ID);
        // Send buyer product response Id as -1 if being created locally
        if (buyerProductResponseID == -1){
            ContentValues values = getBuyerProductResponseContentValuesFromJSONObject(buyerProductResponse);
            String selection = ProductsTable.COLUMN_PRODUCT_ID + " = " + productID;
            db.update(ProductsTable.TABLE_NAME, values, selection, null);
        } else {
            String buyerProductResponseUpdatedAtLocal = getPresentBuyerResponseProductIDs().get(buyerProductResponseID);
            // Put buyerproductresponse updated at server as previous value for local update
            String buyerProductResponseUpdatedAtServer = buyerProductResponse.getString(ProductsTable.COLUMN_PRODUCT_UPDATED_AT);
            if (buyerProductResponseUpdatedAtLocal == null || !buyerProductResponseUpdatedAtLocal.equals(buyerProductResponseUpdatedAtServer)) {
                ContentValues values = getBuyerProductContentValuesFromJSONObject(product);
                String selection = ProductsTable.COLUMN_PRODUCT_ID + " = " + productID;
                db.update(ProductsTable.TABLE_NAME, values, selection, null);
                mPresentBuyerProductResponseIDs.put(buyerProductResponseID, buyerProductResponseUpdatedAtServer);
            }
        }
        mDatabaseHelper.closeDatabase();
    }

    public void saveSellerAddressFromJsonArray(JSONArray sellerAddress) throws JSONException {
        for (int i=0; i<sellerAddress.length(); i++) {
            saveSellerAddressData(sellerAddress.getJSONObject(i), false);
        }
    }

    public void saveSellerAddressData(JSONObject sellerAddress, boolean addressHistory) throws JSONException {
        SQLiteDatabase db = mDatabaseHelper.openDatabase();
        int addressID = sellerAddress.getInt(SellerAddressTable.COLUMN_ADDRESS_ID);
        String sellerAddressUpdatedAtLocal;
        if (addressHistory) {
            sellerAddressUpdatedAtLocal = getPresentSellerAddressHistoryIDs().get(addressID);
        } else {
            sellerAddressUpdatedAtLocal = getPresentSellerAddressIDs().get(addressID);
        }
        String sellerAddressUpdatedAtServer = sellerAddress.getString(SellerAddressTable.COLUMN_UPDATED_AT);
        if (sellerAddressUpdatedAtLocal == null) {
            ContentValues values = getSellerAddressContentValues(sellerAddress, addressHistory);
            db.insert(SellerAddressTable.TABLE_NAME, null, values);
        } else if (!sellerAddressUpdatedAtLocal.equals(sellerAddressUpdatedAtServer)) {
            ContentValues values = getSellerAddressContentValues(sellerAddress, addressHistory);
            String selection = addressHistory ? SellerAddressTable.COLUMN_ADDRESS_ID : SellerAddressTable.COLUMN_ADDRESS_HISTORY_ID
                    + " = " + addressID;
            db.update(SellerAddressTable.TABLE_NAME, values, selection, null);
        }
        if (addressHistory) {
            mPresentSellerAddressHistoryIDs.put(addressID, sellerAddressUpdatedAtServer);
        } else {
            mPresentSellerAddressIDs.put(addressID, sellerAddressUpdatedAtServer);
        }

        mDatabaseHelper.closeDatabase();
    }

    private ContentValues getSellerContentValues(JSONObject seller) throws JSONException {
        ContentValues values = new ContentValues();
        values.put(SellersTable.COLUMN_SELLER_ID, seller.getInt(SellersTable.COLUMN_SELLER_ID));
        values.put(SellersTable.COLUMN_COMPANY_NAME, seller.getString(SellersTable.COLUMN_COMPANY_NAME));
        values.put(SellersTable.COLUMN_NAME, seller.getString(SellersTable.COLUMN_NAME));
        values.put(SellersTable.COLUMN_COMPANY_PROFILE, seller.getString(SellersTable.COLUMN_COMPANY_PROFILE));
        values.put(SellersTable.COLUMN_SHOW_ONLINE, seller.getBoolean(SellersTable.COLUMN_SHOW_ONLINE) ? 1 : 0);
        values.put(SellersTable.COLUMN_CREATED_AT, seller.getString(SellersTable.COLUMN_CREATED_AT));
        values.put(SellersTable.COLUMN_UPDATED_AT, seller.getString(SellersTable.COLUMN_UPDATED_AT));
        return values;
    }

    private ContentValues getProductContentValues(JSONObject product) throws JSONException {
        ContentValues values = new ContentValues();
        values.put(ProductsTable.COLUMN_PRODUCT_ID, product.getInt(ProductsTable.COLUMN_PRODUCT_ID));
        JSONObject category = product.getJSONObject("category");
        values.put(ProductsTable.COLUMN_CATEGORY_ID, category.getInt(CategoriesTable.COLUMN_CATEGORY_ID));
        JSONObject seller = product.getJSONObject("seller");
        values.put(ProductsTable.COLUMN_SELLER_ID, seller.getInt(SellersTable.COLUMN_SELLER_ID));
        JSONObject productDetails = product.getJSONObject("details");
        values.put(ProductsTable.COLUMN_PRODUCT_DETAILS_ID, productDetails.getInt(ProductsTable.COLUMN_PRODUCT_DETAILS_ID));
        values.put(ProductsTable.COLUMN_PRICE_PER_UNIT, product.getDouble(ProductsTable.COLUMN_PRICE_PER_UNIT));
        values.put(ProductsTable.COLUMN_LOT_SIZE, product.getInt(ProductsTable.COLUMN_LOT_SIZE));
        values.put(ProductsTable.COLUMN_PRICE_PER_LOT, product.getDouble(ProductsTable.COLUMN_PRICE_PER_LOT));
        values.put(ProductsTable.COLUMN_MIN_PRICE_PER_UNIT, product.getDouble(ProductsTable.COLUMN_MIN_PRICE_PER_UNIT));
        values.put(ProductsTable.COLUMN_MARGIN, product.getDouble(ProductsTable.COLUMN_MARGIN));
        values.put(ProductsTable.COLUMN_UNIT, product.getString(ProductsTable.COLUMN_UNIT));
        values.put(ProductsTable.COLUMN_DISPLAY_NAME, product.getString(ProductsTable.COLUMN_DISPLAY_NAME));
        values.put(ProductsTable.COLUMN_NAME, product.getString(ProductsTable.COLUMN_NAME));
        values.put(ProductsTable.COLUMN_URL, product.getString(ProductsTable.COLUMN_URL));
        values.put(ProductsTable.COLUMN_SHOW_ONLINE, product.getBoolean(ProductsTable.COLUMN_SHOW_ONLINE) ? 1 : 0);
        values.put(ProductsTable.COLUMN_DELETE_STATUS, product.getBoolean(ProductsTable.COLUMN_DELETE_STATUS) ? 1 : 0);
        JSONObject image = product.getJSONObject("image");
        values.put(ProductsTable.COLUMN_IMAGE_NAME, image.getString(ProductsTable.COLUMN_IMAGE_NAME));
        values.put(ProductsTable.COLUMN_IMAGE_COUNT, image.getInt(ProductsTable.COLUMN_IMAGE_COUNT));
        JSONArray imageNumbers = image.getJSONArray(ProductsTable.COLUMN_IMAGE_NUMBERS);
        String imageNumbersString = imageNumbers.getString(0);
        for (int i = 1; i < imageNumbers.length(); i++) {
            imageNumbersString += "," + imageNumbers.getString(i);
        }
        values.put(ProductsTable.COLUMN_IMAGE_NUMBERS, imageNumbersString);
        values.put(ProductsTable.COLUMN_IMAGE_PATH, image.getString(ProductsTable.COLUMN_IMAGE_PATH));
        values.put(ProductsTable.COLUMN_WARRANTY, productDetails.getString(ProductsTable.COLUMN_WARRANTY));
        values.put(ProductsTable.COLUMN_SPECIAL_FEATURE, productDetails.getString(ProductsTable.COLUMN_SPECIAL_FEATURE));
        values.put(ProductsTable.COLUMN_AVAILABILITY, productDetails.getString(ProductsTable.COLUMN_AVAILABILITY));
        values.put(ProductsTable.COLUMN_STYLE, productDetails.getString(ProductsTable.COLUMN_STYLE));
        values.put(ProductsTable.COLUMN_MANUFACTURED_CITY, productDetails.getString(ProductsTable.COLUMN_MANUFACTURED_CITY));
        values.put(ProductsTable.COLUMN_PATTERN, productDetails.getString(ProductsTable.COLUMN_PATTERN));
        values.put(ProductsTable.COLUMN_COLOURS, productDetails.getString(ProductsTable.COLUMN_COLOURS));
        values.put(ProductsTable.COLUMN_LOT_DESCRIPTION, productDetails.getString(ProductsTable.COLUMN_LOT_DESCRIPTION));
        values.put(ProductsTable.COLUMN_DESCRIPTION, productDetails.getString(ProductsTable.COLUMN_DESCRIPTION));
        values.put(ProductsTable.WORK_DECORATION_TYPE, productDetails.getString(ProductsTable.WORK_DECORATION_TYPE));
        values.put(ProductsTable.COLUMN_NECK_COLLAR_TYPE, productDetails.getString(ProductsTable.COLUMN_NECK_COLLAR_TYPE));
        values.put(ProductsTable.COLUMN_FABRIC_GSM, productDetails.getString(ProductsTable.COLUMN_FABRIC_GSM));
        values.put(ProductsTable.COLUMN_DISPATCHED_IN, productDetails.getString(ProductsTable.COLUMN_DISPATCHED_IN));
        values.put(ProductsTable.COLUMN_REMARKS, productDetails.getString(ProductsTable.COLUMN_REMARKS));
        values.put(ProductsTable.COLUMN_SELLER_CATALOG_NUMBER, productDetails.getString(ProductsTable.COLUMN_SELLER_CATALOG_NUMBER));
        values.put(ProductsTable.COLUMN_SLEEVE, productDetails.getString(ProductsTable.COLUMN_SLEEVE));
        values.put(ProductsTable.COLUMN_SIZES, productDetails.getString(ProductsTable.COLUMN_SIZES));
        values.put(ProductsTable.COLUMN_GENDER, productDetails.getString(ProductsTable.COLUMN_GENDER));
        values.put(ProductsTable.COLUMN_WEIGHT_PER_UNIT, productDetails.getDouble(ProductsTable.COLUMN_WEIGHT_PER_UNIT));
        values.put(ProductsTable.COLUMN_PACKAGING_DETAILS, productDetails.getString(ProductsTable.COLUMN_PACKAGING_DETAILS));
        values.put(ProductsTable.COLUMN_LENGTH, productDetails.getString(ProductsTable.COLUMN_LENGTH));
        values.put(ProductsTable.COLUMN_PRODUCT_CREATED_AT, product.getString(ProductsTable.COLUMN_PRODUCT_CREATED_AT));
        values.put(ProductsTable.COLUMN_PRODUCT_UPDATED_AT, product.getString(ProductsTable.COLUMN_PRODUCT_UPDATED_AT));

        return values;
    }

    private ContentValues getBuyerProductContentValuesFromJSONObject(JSONObject data) throws JSONException {
        ContentValues cv = new ContentValues();

        cv.put(ProductsTable.COLUMN_BUYER_PRODUCT_ID, data.getInt(ProductsTable.COLUMN_BUYER_PRODUCT_ID));
        cv.put(ProductsTable.COLUMN_BUYER_PRODUCT_CREATED_AT, data.getString(ProductsTable.COLUMN_PRODUCT_CREATED_AT));
        cv.put(ProductsTable.COLUMN_BUYER_PRODUCT_UPDATED_AT, data.getString(ProductsTable.COLUMN_PRODUCT_UPDATED_AT));
        cv.put(ProductsTable.COLUMN_BUYER_PRODUCT_IS_ACTIVE, data.getBoolean(ProductsTable.COLUMN_BUYER_PRODUCT_IS_ACTIVE)?1:0);

        return cv;
    }

    private ContentValues getEmptyBuyerProductContentValues(){
        ContentValues values = new ContentValues();
        values.put(ProductsTable.COLUMN_BUYER_PRODUCT_ID, 0);
        values.put(ProductsTable.COLUMN_BUYER_PRODUCT_CREATED_AT, "");
        values.put(ProductsTable.COLUMN_BUYER_PRODUCT_UPDATED_AT, "");
        values.put(ProductsTable.COLUMN_BUYER_PRODUCT_IS_ACTIVE, 1);

        return values;
    }

    private ContentValues getBuyerProductResponseContentValuesFromJSONObject(JSONObject data) throws JSONException {
        ContentValues cv = new ContentValues();

        cv.put(ProductsTable.COLUMN_BUYER_PRODUCT_RESPONSE_ID, data.getInt(ProductsTable.COLUMN_BUYER_PRODUCT_RESPONSE_ID));
        cv.put(ProductsTable.COLUMN_STORE_MARGIN, data.getDouble(ProductsTable.COLUMN_STORE_MARGIN));
        cv.put(ProductsTable.COLUMN_HAS_SWIPED, data.getBoolean(ProductsTable.COLUMN_HAS_SWIPED)?1:0);
        cv.put(ProductsTable.COLUMN_RESPONDED_FROM, data.getInt(ProductsTable.COLUMN_RESPONDED_FROM));
        cv.put(ProductsTable.COLUMN_RESPONSE_CODE, data.getInt(ProductsTable.COLUMN_RESPONSE_CODE));
        cv.put(ProductsTable.COLUMN_BUYER_PRODUCT_RESPONSE_CREATED_AT, data.getString(ProductsTable.COLUMN_PRODUCT_CREATED_AT));
        cv.put(ProductsTable.COLUMN_BUYER_PRODUCT_RESPONSE_UPDATED_AT, data.getString(ProductsTable.COLUMN_PRODUCT_UPDATED_AT));
        if (data.has(ProductsTable.COLUMN_SYNCED)) {
            cv.put(ProductsTable.COLUMN_SYNCED, data.getInt(ProductsTable.COLUMN_SYNCED));
        }else {
            cv.put(ProductsTable.COLUMN_SYNCED, 1);
        }

        return cv;
    }

    private ContentValues getEmptyBuyerProductResponseContentValues(){
        ContentValues values = new ContentValues();
        values.put(ProductsTable.COLUMN_BUYER_PRODUCT_RESPONSE_ID, 0);
        //values.put(ProductsTable.COLUMN_STORE_MARGIN, -1);
        values.put(ProductsTable.COLUMN_HAS_SWIPED, 0);
        values.put(ProductsTable.COLUMN_RESPONDED_FROM, -1);
        values.put(ProductsTable.COLUMN_RESPONSE_CODE, 0);
        values.put(ProductsTable.COLUMN_BUYER_PRODUCT_RESPONSE_CREATED_AT, "");
        values.put(ProductsTable.COLUMN_BUYER_PRODUCT_RESPONSE_UPDATED_AT, "");
        values.put(ProductsTable.COLUMN_SYNCED,1);

        return values;
    }

    public ContentValues getSellerAddressContentValues(JSONObject sellerAddress, boolean addressHistory) throws JSONException {
        ContentValues values = new ContentValues();
        values.put(SellerAddressTable.COLUMN_ADDRESS_ID, sellerAddress.getInt(SellerAddressTable.COLUMN_ADDRESS_ID));
        values.put(SellerAddressTable.COLUMN_ADDRESS_HISTORY_ID, sellerAddress.getInt(SellerAddressTable.COLUMN_ADDRESS_ID));
        if (addressHistory) {
            values.put(SellerAddressTable.COLUMN_ADDRESS_ID, 0);
        } else {
            values.put(SellerAddressTable.COLUMN_ADDRESS_HISTORY_ID, 0);
        }
        values.put(SellerAddressTable.COLUMN_SELLER_ID, sellerAddress.getInt(SellerAddressTable.COLUMN_SELLER_ID));
        values.put(SellerAddressTable.COLUMN_ADDRESS, sellerAddress.getString(SellerAddressTable.COLUMN_ADDRESS));
        values.put(SellerAddressTable.COLUMN_CITY, sellerAddress.getString(SellerAddressTable.COLUMN_CITY));
        values.put(SellerAddressTable.COLUMN_CONTACT_NUMBER, sellerAddress.getString(SellerAddressTable.COLUMN_CONTACT_NUMBER));
        values.put(SellerAddressTable.COLUMN_LANDMARK, sellerAddress.getString(SellerAddressTable.COLUMN_LANDMARK));
        values.put(SellerAddressTable.COLUMN_PINCODE, sellerAddress.getString(SellerAddressTable.COLUMN_PINCODE));
        values.put(SellerAddressTable.COLUMN_STATE, sellerAddress.getString(SellerAddressTable.COLUMN_STATE));
        values.put(SellerAddressTable.COLUMN_CREATED_AT, sellerAddress.getString(SellerAddressTable.COLUMN_CREATED_AT));
        values.put(SellerAddressTable.COLUMN_UPDATED_AT, sellerAddress.getString(SellerAddressTable.COLUMN_UPDATED_AT));
        return values;
    }

    private ContentValues getCategoryContentValueFromJSON(JSONObject category) throws JSONException {
        ContentValues cv = new ContentValues();

        cv.put(CategoriesTable.COLUMN_CATEGORY_ID, category.getLong(CategoriesTable.COLUMN_CATEGORY_ID));
        cv.put(CategoriesTable.COLUMN_SHOW_ONLINE, category.getBoolean(CategoriesTable.COLUMN_SHOW_ONLINE)?1:0);
        cv.put(CategoriesTable.COLUMN_CATEGORY_NAME, category.getString(CategoriesTable.COLUMN_CATEGORY_NAME));
        cv.put(CategoriesTable.COLUMN_IMAGE_URL, "https://dummyimage.com/300x300/98f082/000000.png");
        cv.put(CategoriesTable.COLUMN_CREATED_AT, category.getString(CategoriesTable.COLUMN_CREATED_AT));
        cv.put(CategoriesTable.COLUMN_UPDATED_AT, category.getString(CategoriesTable.COLUMN_UPDATED_AT));
        cv.put(CategoriesTable.COLUMN_SLUG, category.getString(CategoriesTable.COLUMN_SLUG));
        cv.put(CategoriesTable.COLUMN_URL, category.getString(CategoriesTable.COLUMN_URL));

        return cv;
    }

    private SparseArray<String> getCategorySellersID(int categoryID) {
        String query = "SELECT C." + CategorySellersTable.COLUMN_SELLER_ID + ", C." +
                CategorySellersTable.COLUMN_COMPANY_NAME + " FROM " + CategorySellersTable.TABLE_NAME + " AS C WHERE C."
                + CategorySellersTable.COLUMN_CATEGORY_ID + "=" + categoryID;
        Cursor cursor = getCursor(query);
        SparseArray<String> sellers = new SparseArray<>();
        while (cursor.moveToNext()) {
            sellers.put(cursor.getInt(0), cursor.getString(1));
        }
        return sellers;
    }

    public Cursor getCategorySellers(int categoryID) {
        String query = "SELECT * FROM " + CategorySellersTable.TABLE_NAME +
                " WHERE " + CategorySellersTable.COLUMN_CATEGORY_ID + "=" + categoryID;
        return getCursor(query);
    }

    private void updateCategorySellers(JSONArray sellers, int categoryID) throws JSONException {
        SQLiteDatabase db = mDatabaseHelper.openDatabase();
        ContentValues cv = new ContentValues();
        cv.put(CategorySellersTable.COLUMN_CATEGORY_ID, categoryID);
        SparseArray<String> catSellersInDB = getCategorySellersID(categoryID);

        for (int i = 0; i < sellers.length(); i++) {
            JSONObject seller = sellers.getJSONObject(i).getJSONObject("seller");
            int sellerID = seller.getInt(CategorySellersTable.COLUMN_SELLER_ID);
            String companyName = seller.getString(CategorySellersTable.COLUMN_COMPANY_NAME);
            cv.put(CategorySellersTable.COLUMN_SELLER_ID, sellerID);
            cv.put(CategorySellersTable.COLUMN_COMPANY_NAME, companyName);

            String companyNameInDB = catSellersInDB.get(sellerID);
            if (companyNameInDB == null) { // insert
                db.insert(CategorySellersTable.TABLE_NAME, null, cv);
            } else if (!companyName.equals(companyNameInDB)) {
                db.update(CategorySellersTable.TABLE_NAME, cv, null, null);
            }
        }
        mDatabaseHelper.closeDatabase();
    }
}

