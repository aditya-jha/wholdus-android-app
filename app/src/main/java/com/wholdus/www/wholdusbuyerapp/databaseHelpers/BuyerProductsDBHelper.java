package com.wholdus.www.wholdusbuyerapp.databaseHelpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.wholdus.www.wholdusbuyerapp.databaseContracts.BuyerProductsContract.BuyerProductTable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by aditya on 9/12/16.
 */

public class BuyerProductsDBHelper extends BaseDBHelper {

    public BuyerProductsDBHelper(Context context) {
        super(context);
    }

    public int updateBuyerProductsData(JSONObject data) throws JSONException {
        JSONArray products = data.getJSONArray(BuyerProductTable.TABLE_NAME);
        SQLiteDatabase db = mDatabaseHelper.openDatabase();

        HashMap<Long, Boolean> map = getIDSyncMap();
        int totalUpdates = 0;

        for (int i = 0; i < products.length(); i++) {
            ContentValues cv = getBuyerProductContentValuesFromJSONObject(products.getJSONObject(i));
            cv.put(BuyerProductTable.COLUMN_SYNCED, 1);


            long buyerProductID = cv.getAsLong(BuyerProductTable.COLUMN_BUYER_PRODUCT_ID);
            Boolean synced = map.get(buyerProductID);

            if (synced != null) { // this product is already in db
                if (!synced) { // and not up to date, update
                    totalUpdates++;
                    db.update(
                            BuyerProductTable.TABLE_NAME,
                            cv,
                            BuyerProductTable.COLUMN_BUYER_PRODUCT_ID + "=" + buyerProductID,
                            null);
                }
            } else { // add to db
                totalUpdates++;
                db.insert(BuyerProductTable.TABLE_NAME, null, cv);
            }
        }

        return totalUpdates;
    }

    /* TODO: check implementation of sparsearray for better performance */
    private HashMap<Long, Boolean> getIDSyncMap() {
        String query = "SELECT " + BuyerProductTable.COLUMN_BUYER_PRODUCT_ID + ", " + BuyerProductTable.COLUMN_SYNCED +
                " FROM " + BuyerProductTable.TABLE_NAME;
        Cursor cursor = getCursor(query);

        HashMap<Long, Boolean> map = new HashMap<>();
        while (cursor.moveToNext()) {
            map.put(cursor.getLong(0), cursor.getInt(1) > 0);
        }
        return map;
    }

    public Cursor getBuyerProducts() {
        String query = "SELECT " + BuyerProductTable.COLUMN_BUYER_PRODUCT_ID + ", " + BuyerProductTable.COLUMN_SYNCED +
                " FROM " + BuyerProductTable.TABLE_NAME;
        return getCursor(query);
    }

    private ContentValues getBuyerProductContentValuesFromJSONObject(JSONObject data) throws JSONException {
        ContentValues cv = new ContentValues();

        cv.put(BuyerProductTable.COLUMN_BUYER_PRODUCT_ID, data.getLong(BuyerProductTable.COLUMN_BUYER_PRODUCT_ID));
        cv.put(BuyerProductTable.COLUMN_BUYER_PRODUCT_RESPONSE_ID, data.getLong(BuyerProductTable.COLUMN_BUYER_PRODUCT_RESPONSE_ID));
        cv.put(BuyerProductTable.COLUMN_PRODUCT_ID, data.getLong(BuyerProductTable.COLUMN_PRODUCT_ID));
        cv.put(BuyerProductTable.COLUMN_STORE_DISCOUNT, data.getDouble(BuyerProductTable.COLUMN_STORE_DISCOUNT));
        cv.put(BuyerProductTable.COLUMN_HAS_SWIPED, data.getInt(BuyerProductTable.COLUMN_HAS_SWIPED));
        cv.put(BuyerProductTable.COLUMN_RESPONSE_CODE, data.getInt(BuyerProductTable.COLUMN_RESPONSE_CODE));

        return cv;
    }
}
