package com.wholdus.www.wholdusbuyerapp.databaseHelpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.SparseArray;

import com.wholdus.www.wholdusbuyerapp.databaseContracts.CatalogContract.CategoriesTable;
import com.wholdus.www.wholdusbuyerapp.databaseContracts.CatalogContract.ProductsTable;
import com.wholdus.www.wholdusbuyerapp.databaseContracts.CatalogContract.SellerAddressTable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    private SparseArray<String> mPresentSellerAddressIDs;
    private SparseArray<String> mPresentSellerAddressHistoryIDs;

    public static String[] BasicProductColumns = {ProductsTable._ID, ProductsTable.COLUMN_PRODUCT_ID, ProductsTable.COLUMN_SELLER_ID,
            ProductsTable.COLUMN_CATEGORY_ID, ProductsTable.COLUMN_PRICE_PER_UNIT, ProductsTable.COLUMN_LOT_SIZE, ProductsTable.COLUMN_PRICE_PER_LOT,
            ProductsTable.COLUMN_MIN_PRICE_PER_UNIT, ProductsTable.COLUMN_MARGIN, ProductsTable.COLUMN_URL, ProductsTable.COLUMN_IMAGE_NAME,
            ProductsTable.COLUMN_IMAGE_COUNT, ProductsTable.COLUMN_IMAGE_NUMBERS, ProductsTable.COLUMN_IMAGE_PATH, ProductsTable.COLUMN_COLOURS,
            ProductsTable.COLUMN_FABRIC_GSM, ProductsTable.COLUMN_SIZES};

    public static String[] ExtraProductColumns = {ProductsTable.COLUMN_PRODUCT_DETAILS_ID, ProductsTable.COLUMN_UNIT, ProductsTable.COLUMN_DISPLAY_NAME,
            ProductsTable.COLUMN_DELETE_STATUS, ProductsTable.COLUMN_SHOW_ONLINE, ProductsTable.COLUMN_WARRANTY, ProductsTable.COLUMN_SPECIAL_FEATURE,
            ProductsTable.COLUMN_AVAILABILITY, ProductsTable.COLUMN_STYLE, ProductsTable.COLUMN_MANUFACTURED_CITY, ProductsTable.COLUMN_PATTERN,
            ProductsTable.COLUMN_LOT_DESCRIPTION, ProductsTable.COLUMN_DESCRIPTION, ProductsTable.WORK_DECORATION_TYPE, ProductsTable.COLUMN_NECK_COLLAR_TYPE,
            ProductsTable.COLUMN_DISPATCHED_IN, ProductsTable.COLUMN_REMARKS, ProductsTable.COLUMN_SELLER_CATALOG_NUMBER, ProductsTable.COLUMN_SLEEVE,
            ProductsTable.COLUMN_GENDER, ProductsTable.COLUMN_WEIGHT_PER_UNIT, ProductsTable.COLUMN_PACKAGING_DETAILS, ProductsTable.COLUMN_LENGTH,
            ProductsTable.COLUMN_CREATED_AT, ProductsTable.COLUMN_UPDATED_AT};


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

    /**
     * @param orderBy use as "column_name" + "sort_order"(default ASC)
     *                to use descending, " DESC "
     * @return
     */

    public Cursor getProductData(int productID,
                                 @Nullable HashSet<String> sellerIDs,
                                 @Nullable HashSet<Integer> categoryIDs,
                                 int priceGreaterThan,
                                 int priceLowerThan,
                                 @Nullable HashSet<String> fabrics,
                                 @Nullable HashSet<String> colours,
                                 @Nullable HashSet<String> sizes,
                                 int deleteStatus,
                                 int showOnline,
                                 @Nullable String[] orderBy,
                                 int limit,
                                 int offset,
                                 @Nullable String[] columns) {
        String columnNames = getColumnNamesString(columns);
        String query = "SELECT " + columnNames + " FROM " + ProductsTable.TABLE_NAME;
        boolean whereApplied = false;

        if (productID != -1) {
            query += " WHERE " + ProductsTable.COLUMN_PRODUCT_ID + " = " + productID;
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
        if (deleteStatus != -1) {
            query += whereClauseHelper(whereApplied) + ProductsTable.COLUMN_DELETE_STATUS + " = " + deleteStatus;
            whereApplied = true;
        }
        if (showOnline != -1) {
            query += whereClauseHelper(whereApplied) + ProductsTable.COLUMN_SHOW_ONLINE + " = " + showOnline;
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
        String[] columns = {ProductsTable.COLUMN_PRODUCT_ID, ProductsTable.COLUMN_UPDATED_AT};
        Cursor cursor = getProductData(-1, null, null, -1, -1, null, null, null, -1, -1, null, -1, -1, columns);
        SparseArray<String> productIDs = new SparseArray<>();
        while (cursor.moveToNext()) {
            productIDs.put(cursor.getInt(cursor.getColumnIndexOrThrow(ProductsTable.COLUMN_PRODUCT_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(ProductsTable.COLUMN_UPDATED_AT)));
        }
        mPresentProductIDs = productIDs;
        return productIDs;
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

        // get map of catID and updated at
        SparseArray<String> categoriesPresent = getMapOfCategoriesPresent();

        try {
            JSONArray categories = data.getJSONArray(CategoriesTable.TABLE_NAME);

            db.beginTransaction();
            for (int i = 0; i < categories.length(); i++) {
                JSONObject category = categories.getJSONObject(i);

                int categoryID = category.getInt(CategoriesTable.COLUMN_CATEGORY_ID);
                ContentValues cv = getCategoryContentValueFromJSON(category);

                // update if categoryID present and updated_at is not same
                // insert if categoryID not present
                String updatedAt = categoriesPresent.get(categoryID);
                if (updatedAt == null) { // categoryID not present
                    db.insert(CategoriesTable.TABLE_NAME, null, cv);
                    insertedUpdatedCount++;
                } else if (!category.getString(CategoriesTable.COLUMN_UPDATED_AT).equals(updatedAt)) {
                    String selection = CategoriesTable.COLUMN_CATEGORY_ID + "=" + categoryID;
                    db.update(CategoriesTable.TABLE_NAME, cv, selection, null);
                    insertedUpdatedCount++;
                }

                // if seller categories key is present, add it
                if (category.has(CategorySellersTable.TABLE_NAME)) {
                    updateCategorySellers(category.getJSONArray(CategorySellersTable.TABLE_NAME), categoryID);
                }
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
        mDatabaseHelper.closeDatabase();
    }

    public int saveProductsFromJSONArray(JSONArray products) {
        SQLiteDatabase db = mDatabaseHelper.openDatabase();
        int insertedUpdated = 0;

        try {
            SparseArray<String> presentProducts = getPresentProductIDs();
            db.beginTransaction();

            for (int i = 0; i < products.length(); i++) {
                JSONObject product = products.getJSONObject(i);
                int productID = product.getInt(ProductsTable.COLUMN_PRODUCT_ID);

                ContentValues cv = getProductContentValues(product);
                if (presentProducts.get(productID) == null) { // insert
                    db.insert(ProductsTable.TABLE_NAME, null, cv);
                    insertedUpdated++;
                } else if (!presentProducts.get(productID).equals(product.getString(ProductsTable.COLUMN_UPDATED_AT))) { // update
                    db.update(ProductsTable.TABLE_NAME,
                            cv,
                            ProductsTable.COLUMN_PRODUCT_ID + " = " + productID,
                            null);
                    insertedUpdated++;
                }
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

    public void saveProductData(JSONObject product) throws JSONException {
        SQLiteDatabase db = mDatabaseHelper.openDatabase();
        int productID = product.getInt(ProductsTable.COLUMN_PRODUCT_ID);
        if (!product.has("details") || !product.has("image")) {
            return;
        }
        // TODO: Save category data
        String productUpdatedAtLocal = getPresentProductIDs().get(productID);
        String productUpdatedAtServer = product.getString(ProductsTable.COLUMN_UPDATED_AT);
        if (productUpdatedAtLocal == null) { // insert
            ContentValues values = getProductContentValues(product);
            db.insert(ProductsTable.TABLE_NAME, null, values);
            mPresentProductIDs.put(productID, productUpdatedAtServer);
        } else if (!productUpdatedAtLocal.equals(productUpdatedAtServer)) {
            ContentValues values = getProductContentValues(product);
            String selection = ProductsTable.COLUMN_PRODUCT_ID + " = " + productID;
            db.update(ProductsTable.TABLE_NAME, values, selection, null);
            mPresentProductIDs.put(productID, productUpdatedAtServer);
        }
        mDatabaseHelper.closeDatabase();
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
        values.put(ProductsTable.COLUMN_CREATED_AT, product.getString(ProductsTable.COLUMN_CREATED_AT));
        values.put(ProductsTable.COLUMN_UPDATED_AT, product.getString(ProductsTable.COLUMN_UPDATED_AT));

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

    private SparseArray<String> getMapOfCategoriesPresent() {
        SparseArray<String> categories = new SparseArray<>();

        String query = "SELECT " + CategoriesTable.COLUMN_CATEGORY_ID + ", " + CategoriesTable.COLUMN_UPDATED_AT + " FROM " + CategoriesTable.TABLE_NAME;
        Cursor cursor = getCursor(query);
        while (cursor.moveToNext()) {
            categories.put(cursor.getInt(0), cursor.getString(1));
        }

        return categories;
    }

    private ContentValues getCategoryContentValueFromJSON(JSONObject category) throws JSONException {
        ContentValues cv = new ContentValues();

        cv.put(CategoriesTable.COLUMN_CATEGORY_ID, category.getLong(CategoriesTable.COLUMN_CATEGORY_ID));
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

