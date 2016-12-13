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
import com.wholdus.www.wholdusbuyerapp.databaseContracts.UserProfileContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

/**
 * Created by kaustubh on 8/12/16.
 */

public class OrderDBHelper extends BaseDBHelper {

    public OrderDBHelper(Context context) {
        super(context);
    }

    public Cursor getOrdersData(@Nullable List<Integer> orderStatusValues,
                                @Nullable Integer orderID, @Nullable String[] columns) {
        String columnNames = getColumnNamesString(columns);

        String query = "SELECT " + columnNames + " FROM " + OrdersContract.OrdersTable.TABLE_NAME;
        boolean whereApplied = false;
        if (orderID != null && orderID != -1) {
            query += "WHERE " + OrdersContract.OrdersTable.COLUMN_ORDER_ID + " = " + orderID;
            whereApplied = true;
        }
        if (orderStatusValues != null && !orderStatusValues.isEmpty()) {
            if (whereApplied) {
                query += " AND ";
            } else {
                query += " WHERE ";
            }
            query += OrdersContract.OrdersTable.COLUMN_ORDER_STATUS_VALUE + " IN " + TextUtils.join(", ", orderStatusValues);
        }

        return getCursor(query);
    }

    public Cursor getSubordersData(@Nullable List<Integer> subOrderStatusValues,
                                @Nullable Integer suborderID, @Nullable Integer orderID, @Nullable String[] columns) {
        String columnNames = getColumnNamesString(columns);

        String query = "SELECT " + columnNames + " FROM " + OrdersContract.SubordersTable.TABLE_NAME;
        boolean whereApplied = false;
        if (suborderID != null && suborderID != -1) {
            query += "WHERE " + OrdersContract.SubordersTable.COLUMN_SUBORDER_ID + " = " + suborderID;
            whereApplied = true;
        }
        if (orderID != null && orderID != -1) {
            if (whereApplied) {
                query += " AND ";
            } else {
                query += " WHERE ";
            }
            query += OrdersContract.SubordersTable.COLUMN_ORDER_ID + " = " + orderID;
            whereApplied = true;
        }
        if (subOrderStatusValues != null && !subOrderStatusValues.isEmpty()) {
            if (whereApplied) {
                query += " AND ";
            } else {
                query += " WHERE ";
            }
            query += OrdersContract.SubordersTable.COLUMN_SUBORDER_STATUS_VALUE + " IN " + TextUtils.join(", ", subOrderStatusValues);
        }

        return getCursor(query);
    }

    private SparseArray<String> getPresentOrderIDs() {
        String[] columns = new String[]{OrdersContract.OrdersTable.COLUMN_ORDER_ID, OrdersContract.OrdersTable.COLUMN_UPDATED_AT};
        Cursor cursor = getOrdersData(null, null, columns);
        SparseArray<String> orderIDs = new SparseArray<>();
        while (cursor.moveToNext()) {
            orderIDs.put(cursor.getInt(cursor.getColumnIndexOrThrow(OrdersContract.OrdersTable.COLUMN_ORDER_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(OrdersContract.OrdersTable.COLUMN_UPDATED_AT)));
        }
        return orderIDs;
    }

    private SparseArray<String> getPresentSuborderIDs() {
        String[] columns = new String[]{OrdersContract.SubordersTable.COLUMN_SUBORDER_ID, OrdersContract.SubordersTable.COLUMN_UPDATED_AT};
        Cursor cursor = getSubordersData(null, null, null,columns);
        SparseArray<String> suborderIDs = new SparseArray<>();
        while (cursor.moveToNext()) {
            suborderIDs.put(cursor.getInt(cursor.getColumnIndexOrThrow(OrdersContract.SubordersTable.COLUMN_SUBORDER_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(OrdersContract.SubordersTable.COLUMN_UPDATED_AT)));
        }
        return suborderIDs;
    }

    public void saveOrdersData(JSONArray ordersArray) throws JSONException {
        SQLiteDatabase db = mDatabaseHelper.openDatabase();
        UserDBHelper userDBHelper = new UserDBHelper(mContext);
        CatalogDBHelper catalogDBHelper = new CatalogDBHelper(mContext);
        SparseArray<String> presentBuyerAddressIDs = userDBHelper.getPresentBuyerAddressIDs();
        SparseArray<String> presentOrderIDs = getPresentOrderIDs();
        SparseArray<String> presentSuborderIDs = getPresentSuborderIDs();
        SparseArray<String> presentSellerIDs = catalogDBHelper.getPresentSellerIDs();

        try {
            db.beginTransaction();
            for (int i = 0; i < ordersArray.length(); i++) {
                JSONObject order = ordersArray.getJSONObject(i);
                int orderID = order.getInt(OrdersContract.OrdersTable.COLUMN_ORDER_ID);
                saveOrderData(order, presentOrderIDs.get(orderID));

                JSONObject buyerAddress = order.getJSONObject("buyer_address");
                int buyerAddressID = buyerAddress.getInt(UserProfileContract.UserAddressTable.COLUMN_ADDRESS_ID);
                String buyerAddressUpdatedAtLocal = presentBuyerAddressIDs.get(buyerAddressID);
                String buyerAddressUpdatedAtServer = buyerAddress.getString(UserProfileContract.UserAddressTable.COLUMN_UPDATED_AT);

                if (buyerAddressUpdatedAtLocal == null) {
                    userDBHelper.updateUserAddressDataFromJSONObject(buyerAddress, false);
                } else if (!buyerAddressUpdatedAtLocal.equals(buyerAddressUpdatedAtServer)) {
                    userDBHelper.updateUserAddressDataFromJSONObject(buyerAddress, true);
                }

                if (order.has("sub_orders")){
                    JSONArray subordersArray = order.getJSONArray("sub_orders");
                    saveSubordersData(subordersArray, presentSuborderIDs, presentSellerIDs);
                }
            }

            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }

        mDatabaseHelper.closeDatabase();
    }

    public void saveSubordersData(JSONArray subordersArray,
                                  @Nullable SparseArray<String> presentSuborderIDs, @Nullable SparseArray<String> presentSellerIDs) throws JSONException{
        if (presentSuborderIDs == null){
            presentSuborderIDs = getPresentSuborderIDs();
        }
        CatalogDBHelper catalogDBHelper = new CatalogDBHelper(mContext);
        for (int i = 0; i < subordersArray.length(); i++) {
            JSONObject suborder = subordersArray.getJSONObject(i);
            int suborderID = suborder.getInt(OrdersContract.SubordersTable.COLUMN_SUBORDER_ID);
            saveSuborderData(suborder, presentSuborderIDs.get(suborderID));

            if (suborder.has("seller")){
                JSONObject seller = suborder.getJSONObject("seller");
                int sellerID = seller.getInt(ProductsContract.SellersTable.COLUMN_SELLER_ID);
                catalogDBHelper.saveSellerData(seller, presentSellerIDs.get(sellerID));
            }
        }
    }

    public void saveOrderData(JSONObject order, @Nullable String orderUpdatedAtLocal) throws JSONException{
        SQLiteDatabase db = mDatabaseHelper.openDatabase();
        int orderID = order.getInt(OrdersContract.OrdersTable.COLUMN_ORDER_ID);
        String orderUpdatedAtServer = order.getString(OrdersContract.OrdersTable.COLUMN_UPDATED_AT);
        if (orderUpdatedAtLocal == null) { // insert
            ContentValues values = getOrderContentValues(order);
            db.insert(OrdersContract.OrdersTable.TABLE_NAME, null, values);
        } else if (!orderUpdatedAtLocal.equals(orderUpdatedAtServer)) {
            ContentValues values = getOrderContentValues(order);
            String selection = OrdersContract.OrdersTable.COLUMN_ORDER_ID + " = " + orderID;
            db.update(OrdersContract.OrdersTable.TABLE_NAME, values, selection, null);
        }
        mDatabaseHelper.closeDatabase();
    }

    public void saveSuborderData(JSONObject suborder, @Nullable String suborderUpdatedAtLocal) throws JSONException{
        SQLiteDatabase db = mDatabaseHelper.openDatabase();
        int suborderID = suborder.getInt(OrdersContract.SubordersTable.COLUMN_SUBORDER_ID);
        String suborderUpdatedAtServer = suborder.getString(OrdersContract.SubordersTable.COLUMN_UPDATED_AT);
        if (suborderUpdatedAtLocal == null) { // insert
            ContentValues values = getSubOrderContentValues(suborder);
            db.insert(OrdersContract.SubordersTable.TABLE_NAME, null, values);
        } else if (!suborderUpdatedAtLocal.equals(suborderUpdatedAtServer)) {
            ContentValues values = getOrderContentValues(suborder);
            String selection = OrdersContract.SubordersTable.COLUMN_SUBORDER_ID + " = " + suborderID;
            db.update(OrdersContract.SubordersTable.TABLE_NAME, values, selection, null);
        }
        mDatabaseHelper.closeDatabase();
    }

    private ContentValues getOrderContentValues(JSONObject order) throws JSONException {
        ContentValues values = new ContentValues();
        values.put(OrdersContract.OrdersTable.COLUMN_ORDER_ID, order.getInt(OrdersContract.OrdersTable.COLUMN_ORDER_ID));
        values.put(OrdersContract.OrdersTable.COLUMN_DISPLAY_NUMBER, order.getString(OrdersContract.OrdersTable.COLUMN_DISPLAY_NUMBER));
        JSONObject buyerAddress = order.getJSONObject("buyer_address");
        values.put(OrdersContract.OrdersTable.COLUMN_BUYER_ADDRESS_ID, buyerAddress.getInt(UserProfileContract.UserAddressTable.COLUMN_ADDRESS_ID));
        values.put(OrdersContract.OrdersTable.COLUMN_PRODUCT_COUNT, order.getInt(OrdersContract.OrdersTable.COLUMN_PRODUCT_COUNT));
        values.put(OrdersContract.OrdersTable.COLUMN_PIECES, order.getInt(OrdersContract.OrdersTable.COLUMN_PIECES));
        values.put(OrdersContract.OrdersTable.COLUMN_RETAIL_PRICE, order.getDouble(OrdersContract.OrdersTable.COLUMN_RETAIL_PRICE));
        values.put(OrdersContract.OrdersTable.COLUMN_CALCULATED_PRICE, order.getDouble(OrdersContract.OrdersTable.COLUMN_CALCULATED_PRICE));
        values.put(OrdersContract.OrdersTable.COLUMN_EDITED_PRICE, order.getDouble(OrdersContract.OrdersTable.COLUMN_EDITED_PRICE));
        values.put(OrdersContract.OrdersTable.COLUMN_SHIPPING_CHARGE, order.getDouble(OrdersContract.OrdersTable.COLUMN_SHIPPING_CHARGE));
        values.put(OrdersContract.OrdersTable.COLUMN_COD_CHARGE, order.getDouble(OrdersContract.OrdersTable.COLUMN_COD_CHARGE));
        values.put(OrdersContract.OrdersTable.COLUMN_FINAL_PRICE, order.getDouble(OrdersContract.OrdersTable.COLUMN_FINAL_PRICE));
        JSONObject orderStatus = order.getJSONObject("order_status");
        values.put(OrdersContract.OrdersTable.COLUMN_ORDER_STATUS_VALUE, orderStatus.getInt("value"));
        values.put(OrdersContract.OrdersTable.COLUMN_ORDER_STATUS_DISPLAY, orderStatus.getString("display_value"));
        JSONObject paymentStatus = order.getJSONObject("order_payment_status");
        values.put(OrdersContract.OrdersTable.COLUMN_PAYMENT_STATUS_VALUE, paymentStatus.getInt("value"));
        values.put(OrdersContract.OrdersTable.COLUMN_PAYMENT_STATUS_DISPLAY, paymentStatus.getString("display_value"));
        values.put(OrdersContract.OrdersTable.COLUMN_CREATED_AT, order.getString(OrdersContract.OrdersTable.COLUMN_CREATED_AT));
        values.put(OrdersContract.OrdersTable.COLUMN_UPDATED_AT, order.getString(OrdersContract.OrdersTable.COLUMN_UPDATED_AT));
        values.put(OrdersContract.OrdersTable.COLUMN_REMARKS, order.getString(OrdersContract.OrdersTable.COLUMN_REMARKS));
        return values;
    }

    private ContentValues getSubOrderContentValues(JSONObject suborder) throws JSONException {
        ContentValues values = new ContentValues();
        values.put(OrdersContract.SubordersTable.COLUMN_ORDER_ID, suborder.getInt(OrdersContract.SubordersTable.COLUMN_ORDER_ID));
        values.put(OrdersContract.SubordersTable.COLUMN_SUBORDER_ID, suborder.getInt(OrdersContract.SubordersTable.COLUMN_SUBORDER_ID));
        values.put(OrdersContract.SubordersTable.COLUMN_DISPLAY_NUMBER, suborder.getString(OrdersContract.SubordersTable.COLUMN_DISPLAY_NUMBER));
        JSONObject seller = suborder.getJSONObject("seller");
        values.put(OrdersContract.SubordersTable.COLUMN_SELLER_ID, seller.getInt(ProductsContract.SellersTable.COLUMN_SELLER_ID));
        JSONObject sellerAddress = suborder.getJSONObject("seller_address");
        values.put(OrdersContract.SubordersTable.COLUMN_SELLER_ADDRESS_ID, sellerAddress.getInt(ProductsContract.SellerAddressTable.COLUMN_ADDRESS_ID));
        values.put(OrdersContract.SubordersTable.COLUMN_PRODUCT_COUNT, suborder.getInt(OrdersContract.SubordersTable.COLUMN_PRODUCT_COUNT));
        values.put(OrdersContract.SubordersTable.COLUMN_PIECES, suborder.getInt(OrdersContract.SubordersTable.COLUMN_PIECES));
        values.put(OrdersContract.SubordersTable.COLUMN_RETAIL_PRICE, suborder.getDouble(OrdersContract.SubordersTable.COLUMN_RETAIL_PRICE));
        values.put(OrdersContract.SubordersTable.COLUMN_CALCULATED_PRICE, suborder.getDouble(OrdersContract.SubordersTable.COLUMN_CALCULATED_PRICE));
        values.put(OrdersContract.SubordersTable.COLUMN_EDITED_PRICE, suborder.getDouble(OrdersContract.SubordersTable.COLUMN_EDITED_PRICE));
        values.put(OrdersContract.SubordersTable.COLUMN_SHIPPING_CHARGE, suborder.getDouble(OrdersContract.SubordersTable.COLUMN_SHIPPING_CHARGE));
        values.put(OrdersContract.SubordersTable.COLUMN_COD_CHARGE, suborder.getDouble(OrdersContract.SubordersTable.COLUMN_COD_CHARGE));
        values.put(OrdersContract.SubordersTable.COLUMN_FINAL_PRICE, suborder.getDouble(OrdersContract.SubordersTable.COLUMN_FINAL_PRICE));
        JSONObject suborderStatus = suborder.getJSONObject("sub_order_status");
        values.put(OrdersContract.SubordersTable.COLUMN_SUBORDER_STATUS_VALUE, suborderStatus.getInt("value"));
        values.put(OrdersContract.SubordersTable.COLUMN_SUBORDER_STATUS_DISPLAY, suborderStatus.getString("display_value"));
        JSONObject paymentStatus = suborder.getJSONObject("sub_order_payment_status");
        values.put(OrdersContract.SubordersTable.COLUMN_PAYMENT_STATUS_VALUE, paymentStatus.getInt("value"));
        values.put(OrdersContract.SubordersTable.COLUMN_PAYMENT_STATUS_DISPLAY, paymentStatus.getString("display_value"));
        values.put(OrdersContract.SubordersTable.COLUMN_CREATED_AT, suborder.getString(OrdersContract.SubordersTable.COLUMN_CREATED_AT));
        values.put(OrdersContract.SubordersTable.COLUMN_UPDATED_AT, suborder.getString(OrdersContract.SubordersTable.COLUMN_UPDATED_AT));
        return values;
    }
}
