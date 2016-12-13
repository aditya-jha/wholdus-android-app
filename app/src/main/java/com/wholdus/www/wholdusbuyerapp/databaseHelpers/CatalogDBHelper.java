package com.wholdus.www.wholdusbuyerapp.databaseHelpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;
import android.util.SparseArray;

import com.wholdus.www.wholdusbuyerapp.databaseContracts.OrdersContract;
import com.wholdus.www.wholdusbuyerapp.databaseContracts.ProductsContract;
import com.wholdus.www.wholdusbuyerapp.databaseContracts.ProductsContract.CategoriesTable;
import com.wholdus.www.wholdusbuyerapp.databaseContracts.ProductsContract.ProductsTable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.wholdus.www.wholdusbuyerapp.helperClasses.HelperFunctions.booleanToInteger;

/**
 * Created by aditya on 10/12/16.
 * For database interactions related to catalog data
 */

public class CatalogDBHelper extends BaseDBHelper {

    public CatalogDBHelper(Context context) {
        super(context);
    }

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

    public Cursor getSellerData(@Nullable Integer sellerID, @Nullable String[] columns){
        String columnNames = getColumnNamesString(columns);

        String query = "SELECT " + columnNames + " FROM " + ProductsContract.SellersTable.TABLE_NAME;

        if (sellerID != null && sellerID != -1) {
            query += " WHERE " + ProductsContract.SellersTable.COLUMN_SELLER_ID + " = " + sellerID;
        }

        return getCursor(query);
    }

    public SparseArray<String> getPresentSellerIDs() {
        String[] columns = new String[]{ProductsContract.SellersTable.COLUMN_SELLER_ID, ProductsContract.SellersTable.COLUMN_UPDATED_AT};
        Cursor cursor = getSellerData(null, columns);
        SparseArray<String> sellerIDs = new SparseArray<>();
        while (cursor.moveToNext()) {
            sellerIDs.put(cursor.getInt(cursor.getColumnIndexOrThrow(ProductsContract.SellersTable.COLUMN_SELLER_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(ProductsContract.SellersTable.COLUMN_UPDATED_AT)));
        }
        return sellerIDs;
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

    public void saveSellerData(JSONObject seller, @Nullable String sellerUpdatedAtLocal) throws JSONException{
        SQLiteDatabase db = mDatabaseHelper.openDatabase();
        int sellerID = seller.getInt(ProductsContract.SellersTable.COLUMN_SELLER_ID);
        if (!seller.has(ProductsContract.SellersTable.COLUMN_UPDATED_AT)){
            return;
        }
        String sellerUpdatedAtServer = seller.getString(ProductsContract.SellersTable.COLUMN_UPDATED_AT);
        if (sellerUpdatedAtLocal == null) { // insert
            ContentValues values = getSellerContentValues(seller);
            db.insert(ProductsContract.SellersTable.TABLE_NAME, null, values);
        } else if (!sellerUpdatedAtLocal.equals(sellerUpdatedAtServer)) {
            ContentValues values = getSellerContentValues(seller);
            String selection = ProductsContract.SellersTable.COLUMN_SELLER_ID + " = " + sellerID;
            db.update(ProductsContract.SellersTable.TABLE_NAME, values, selection, null);
        }
        mDatabaseHelper.closeDatabase();
    }

    public ContentValues getSellerContentValues(JSONObject seller) throws JSONException{
        ContentValues values = new ContentValues();
        values.put(ProductsContract.SellersTable.COLUMN_SELLER_ID, seller.getInt(ProductsContract.SellersTable.COLUMN_SELLER_ID));
        values.put(ProductsContract.SellersTable.COLUMN_COMPANY_NAME, seller.getString(ProductsContract.SellersTable.COLUMN_COMPANY_NAME));
        values.put(ProductsContract.SellersTable.COLUMN_NAME, seller.getString(ProductsContract.SellersTable.COLUMN_NAME));
        values.put(ProductsContract.SellersTable.COLUMN_COMPANY_PROFILE, seller.getString(ProductsContract.SellersTable.COLUMN_COMPANY_PROFILE));
        values.put(ProductsContract.SellersTable.COLUMN_SHOW_ONLINE, booleanToInteger(seller.getBoolean(ProductsContract.SellersTable.COLUMN_SHOW_ONLINE)));
        values.put(ProductsContract.SellersTable.COLUMN_CREATED_AT, seller.getString(ProductsContract.SellersTable.COLUMN_CREATED_AT));
        values.put(ProductsContract.SellersTable.COLUMN_UPDATED_AT, seller.getString(ProductsContract.SellersTable.COLUMN_UPDATED_AT));
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

