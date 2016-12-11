package com.wholdus.www.wholdusbuyerapp.databaseHelpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.SparseArray;

import com.wholdus.www.wholdusbuyerapp.databaseContracts.ProductsContract.CategoriesTable;
import com.wholdus.www.wholdusbuyerapp.databaseContracts.ProductsContract.ProductsTable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

