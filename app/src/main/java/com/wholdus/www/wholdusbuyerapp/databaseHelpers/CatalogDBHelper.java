package com.wholdus.www.wholdusbuyerapp.databaseHelpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.SparseArray;

import com.wholdus.www.wholdusbuyerapp.databaseContracts.OrdersContract;
import com.wholdus.www.wholdusbuyerapp.databaseContracts.ProductsContract;
import com.wholdus.www.wholdusbuyerapp.databaseContracts.ProductsContract.CategoriesTable;
import com.wholdus.www.wholdusbuyerapp.databaseContracts.ProductsContract.ProductsTable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


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

    public Cursor getAllCategories(boolean productsCount) {
        String countQuery = "";
        if (productsCount) {
            countQuery = ", (SELECT COUNT(*) FROM " + ProductsTable.TABLE_NAME + " AS P " +
                    " WHERE C." + CategoriesTable.COLUMN_CATEGORY_ID + " = P." + ProductsTable.COLUMN_CATEGORY_ID + ") AS " +
                    CategoriesTable.COLUMN_PRODUCTS_COUNT;
        }
        String query = "SELECT C.*" + countQuery + " FROM " + CategoriesTable.TABLE_NAME + " AS C";;
        return getCursor(query);
    }

    public Cursor getProductData(int productID,
                                 @Nullable ArrayList<Integer> sellerIDs,
                                 @Nullable ArrayList<Integer> categoryIDs,
                                 int priceGreaterThan,
                                 int priceLowerThan,
                                 @Nullable ArrayList<String> fabrics,
                                 @Nullable ArrayList<String> colours,
                                 @Nullable ArrayList<String> sizes,
                                 int deleteStatus,
                                 int showOnline,
                                 @Nullable String[] columns){
        String columnNames = getColumnNamesString(columns);
        String query = "SELECT " + columnNames + " FROM " + ProductsTable.TABLE_NAME;
        boolean whereApplied = false;
        if (productID != -1){
            query += " AND " + ProductsTable.COLUMN_PRODUCT_ID + " = " + productID;
            whereApplied = true;
        }
        if (sellerIDs != null && !sellerIDs.isEmpty()){
            query = whereClauseHelper(query, whereApplied);
            query += ProductsTable.COLUMN_SELLER_ID + " IN " + TextUtils.join(", ", sellerIDs);
            whereApplied = true;
        }
        if (categoryIDs != null && !categoryIDs.isEmpty()){
            query = whereClauseHelper(query, whereApplied);
            query += ProductsTable.COLUMN_CATEGORY_ID + " IN " + TextUtils.join(", ", categoryIDs);
            whereApplied = true;
        }
        if (priceGreaterThan != -1){
            query = whereClauseHelper(query, whereApplied);
            query += ProductsTable.COLUMN_MIN_PRICE_PER_UNIT + " >= " + priceGreaterThan;
            whereApplied = true;
        }
        if (priceLowerThan != -1){
            query = whereClauseHelper(query, whereApplied);
            query += ProductsTable.COLUMN_MIN_PRICE_PER_UNIT + " <= " + priceLowerThan;
            whereApplied = true;
        }
        if (fabrics != null && !fabrics.isEmpty()){
            query = whereClauseHelper(query, whereApplied);
            query += " ( LOWER(" + ProductsTable.COLUMN_FABRIC_GSM + ") LIKE LOWER('%" +
                    TextUtils.join("%') OR LOWER(" + ProductsTable.COLUMN_FABRIC_GSM + ") LIKE LOWER('%", fabrics) + "%') ) ";
            whereApplied = true;
        }
        if (colours != null && !colours.isEmpty()){
            query = whereClauseHelper(query, whereApplied);
            query += " ( LOWER(" + ProductsTable.COLUMN_COLOURS + ") LIKE LOWER('%" +
                    TextUtils.join("%') OR LOWER(" + ProductsTable.COLUMN_COLOURS + ") LIKE LOWER('%", colours) + "%') ) ";
            whereApplied = true;
        }
        if (sizes != null && !sizes.isEmpty()){
            query = whereClauseHelper(query, whereApplied);
            query += " ( LOWER(" + ProductsTable.COLUMN_SIZES + ") LIKE LOWER('%" +
                    TextUtils.join("%') OR LOWER(" + ProductsTable.COLUMN_SIZES + ") LIKE LOWER('%", sizes) + "%') ) ";
            whereApplied = true;
        }
        if (deleteStatus != -1){
            query = whereClauseHelper(query, whereApplied);
            query += ProductsTable.COLUMN_DELETE_STATUS + " = " + deleteStatus;
            whereApplied = true;
        }
        if (showOnline != -1){
            query = whereClauseHelper(query, whereApplied);
            query += ProductsTable.COLUMN_SHOW_ONLINE + " = " + showOnline;
        }

        return getCursor(query);
    }

    public Cursor getSellerData(@Nullable Integer sellerID, @Nullable String[] columns){
        String columnNames = getColumnNamesString(columns);

        String query = "SELECT " + columnNames + " FROM " + ProductsContract.SellersTable.TABLE_NAME;

        if (sellerID != null && sellerID != -1) {
            query += " WHERE " + ProductsContract.SellersTable.COLUMN_SELLER_ID + " = " + sellerID;
        }

        return getCursor(query);
    }

    public SparseArray<String> getPresentSellerIDs() {
        if (mPresentSellerIDs!= null){
            return mPresentSellerIDs;
        }
        String[] columns = new String[]{ProductsContract.SellersTable.COLUMN_SELLER_ID, ProductsContract.SellersTable.COLUMN_UPDATED_AT};
        Cursor cursor = getSellerData(null, columns);
        SparseArray<String> sellerIDs = new SparseArray<>();
        while (cursor.moveToNext()) {
            sellerIDs.put(cursor.getInt(cursor.getColumnIndexOrThrow(ProductsContract.SellersTable.COLUMN_SELLER_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(ProductsContract.SellersTable.COLUMN_UPDATED_AT)));
        }
        mPresentSellerIDs = sellerIDs;
        return sellerIDs;
    }

    public SparseArray<String> getPresentProductIDs() {
        if (mPresentProductIDs!= null){
            return mPresentProductIDs;
        }
        String[] columns = new String[]{ProductsContract.ProductsTable.COLUMN_PRODUCT_ID, ProductsContract.ProductsTable.COLUMN_UPDATED_AT};
        Cursor cursor = getProductData(-1, null,null,-1,-1,null,null,null,-1,-1,columns);
        SparseArray<String> productIDs = new SparseArray<>();
        while (cursor.moveToNext()) {
            productIDs.put(cursor.getInt(cursor.getColumnIndexOrThrow(ProductsContract.ProductsTable.COLUMN_PRODUCT_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(ProductsContract.ProductsTable.COLUMN_UPDATED_AT)));
        }
        mPresentProductIDs = productIDs;
        return productIDs;
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

    public void saveSellerData(JSONObject seller) throws JSONException{
        SQLiteDatabase db = mDatabaseHelper.openDatabase();
        int sellerID = seller.getInt(ProductsContract.SellersTable.COLUMN_SELLER_ID);
        if (!seller.has(ProductsContract.SellersTable.COLUMN_UPDATED_AT)){
            return;
        }
        String sellerUpdatedAtLocal = getPresentSellerIDs().get(sellerID);
        String sellerUpdatedAtServer = seller.getString(ProductsContract.SellersTable.COLUMN_UPDATED_AT);
        if (sellerUpdatedAtLocal == null) { // insert
            ContentValues values = getSellerContentValues(seller);
            db.insert(ProductsContract.SellersTable.TABLE_NAME, null, values);
            mPresentSellerIDs.put(sellerID, sellerUpdatedAtServer);
        } else if (!sellerUpdatedAtLocal.equals(sellerUpdatedAtServer)) {
            ContentValues values = getSellerContentValues(seller);
            String selection = ProductsContract.SellersTable.COLUMN_SELLER_ID + " = " + sellerID;
            db.update(ProductsContract.SellersTable.TABLE_NAME, values, selection, null);
            mPresentSellerIDs.put(sellerID, sellerUpdatedAtServer);
        }
        mDatabaseHelper.closeDatabase();
    }

    public void saveProductData(JSONObject product) throws JSONException{
        SQLiteDatabase db = mDatabaseHelper.openDatabase();
        int productID = product.getInt(ProductsTable.COLUMN_PRODUCT_ID);
        if (!product.has("details") || !product.has("image")){
            return;
        }
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

    public ContentValues getSellerContentValues(JSONObject seller) throws JSONException{
        ContentValues values = new ContentValues();
        values.put(ProductsContract.SellersTable.COLUMN_SELLER_ID, seller.getInt(ProductsContract.SellersTable.COLUMN_SELLER_ID));
        values.put(ProductsContract.SellersTable.COLUMN_COMPANY_NAME, seller.getString(ProductsContract.SellersTable.COLUMN_COMPANY_NAME));
        values.put(ProductsContract.SellersTable.COLUMN_NAME, seller.getString(ProductsContract.SellersTable.COLUMN_NAME));
        values.put(ProductsContract.SellersTable.COLUMN_COMPANY_PROFILE, seller.getString(ProductsContract.SellersTable.COLUMN_COMPANY_PROFILE));
        values.put(ProductsContract.SellersTable.COLUMN_SHOW_ONLINE, seller.getBoolean(ProductsContract.SellersTable.COLUMN_SHOW_ONLINE)? 1 : 0);
        values.put(ProductsContract.SellersTable.COLUMN_CREATED_AT, seller.getString(ProductsContract.SellersTable.COLUMN_CREATED_AT));
        values.put(ProductsContract.SellersTable.COLUMN_UPDATED_AT, seller.getString(ProductsContract.SellersTable.COLUMN_UPDATED_AT));
        return values;
    }

    public ContentValues getProductContentValues(JSONObject product) throws JSONException{
        ContentValues values = new ContentValues();
        values.put(ProductsTable.COLUMN_PRODUCT_ID, product.getInt(ProductsTable.COLUMN_PRODUCT_ID));
        JSONObject category = product.getJSONObject("category");
        values.put(ProductsTable.COLUMN_CATEGORY_ID, category.getInt(CategoriesTable.COLUMN_CATEGORY_ID));
        JSONObject seller = product.getJSONObject("seller");
        values.put(ProductsTable.COLUMN_SELLER_ID, seller.getInt(ProductsContract.SellersTable.COLUMN_SELLER_ID));
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
        values.put(ProductsTable.COLUMN_URL, product.getString(ProductsTable.COLUMN_PRODUCT_ID));
        values.put(ProductsTable.COLUMN_SHOW_ONLINE, product.getBoolean(ProductsTable.COLUMN_SHOW_ONLINE) ? 1:0);
        values.put(ProductsTable.COLUMN_DELETE_STATUS, product.getBoolean(ProductsTable.COLUMN_PRODUCT_ID)?1:0);
        JSONObject image = product.getJSONObject("image");
        values.put(ProductsTable.COLUMN_IMAGE_NAME, image.getString(ProductsTable.COLUMN_IMAGE_NAME));
        values.put(ProductsTable.COLUMN_IMAGE_COUNT, image.getInt(ProductsTable.COLUMN_IMAGE_COUNT));
        JSONArray imageNumbers = image.getJSONArray(ProductsTable.COLUMN_IMAGE_NUMBERS);
        String imageNumbersString = imageNumbers.getString(0);
        for (int i=1;i<imageNumbers.length(); i++){
            imageNumbersString += ", " + imageNumbers.getString(i);
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
        values.put(ProductsTable.COLUMN_WORK_DESCRIPTION_TYPE, productDetails.getString(ProductsTable.COLUMN_WORK_DESCRIPTION_TYPE));
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
}

