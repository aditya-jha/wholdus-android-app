package com.wholdus.www.wholdusbuyerapp.databaseHelpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.SparseArray;


import com.wholdus.www.wholdusbuyerapp.databaseContracts.OrdersContract;
import com.wholdus.www.wholdusbuyerapp.databaseContracts.CatalogContract;
import com.wholdus.www.wholdusbuyerapp.databaseContracts.UserProfileContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by kaustubh on 8/12/16.
 */

public class OrderDBHelper extends BaseDBHelper {

    public OrderDBHelper(Context context) {
        super(context);
    }

    private SparseArray<String> mPresentOrderIDs;
    private SparseArray<String> mPresentSuborderIDs;
    private SparseArray<String> mPresentOrderItemIDs;

    public Cursor getOrdersData(@Nullable List<Integer> orderStatusValues,
                                @Nullable Integer orderID, @Nullable String[] columns) {
        String columnNames = getColumnNamesString(columns);

        String query = "SELECT " + columnNames + " FROM " + OrdersContract.OrdersTable.TABLE_NAME;
        boolean whereApplied = false;
        if (orderID != null && orderID != -1) {
            query += " WHERE " + OrdersContract.OrdersTable.COLUMN_ORDER_ID + " = " + orderID;
            whereApplied = true;
        }
        if (orderStatusValues != null && !orderStatusValues.isEmpty()) {
            query += whereClauseHelper(whereApplied);
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
            query += " WHERE " + OrdersContract.SubordersTable.COLUMN_SUBORDER_ID + " = " + suborderID;
            whereApplied = true;
        }
        if (orderID != null && orderID != -1) {
            query += whereClauseHelper(whereApplied);
            query += OrdersContract.SubordersTable.COLUMN_ORDER_ID + " = " + orderID;
            whereApplied = true;
        }
        if (subOrderStatusValues != null && !subOrderStatusValues.isEmpty()) {
            query += whereClauseHelper(whereApplied);
            query += OrdersContract.SubordersTable.COLUMN_SUBORDER_STATUS_VALUE + " IN " + TextUtils.join(", ", subOrderStatusValues);
        }

        return getCursor(query);
    }

    public Cursor getOrderItemsData(@Nullable List<Integer> orderItemStatusValues,
                                   @Nullable Integer orderItemID, @Nullable Integer suborderID, @Nullable String[] columns) {
        String columnNames = getColumnNamesString(columns);

        String query = "SELECT " + columnNames + " FROM " + OrdersContract.OrderItemsTable.TABLE_NAME;
        boolean whereApplied = false;
        if (orderItemID != null && orderItemID != -1) {
            query += " WHERE " + OrdersContract.OrderItemsTable.COLUMN_ORDER_ITEM_ID + " = " + orderItemID;
            whereApplied = true;
        }
        if (suborderID != null && suborderID != -1) {
            query += whereClauseHelper(whereApplied);
            query += OrdersContract.OrderItemsTable.COLUMN_SUBORDER_ID + " = " + suborderID;
            whereApplied = true;
        }
        if (orderItemStatusValues != null && !orderItemStatusValues.isEmpty()) {
            query += whereClauseHelper(whereApplied);
            query += OrdersContract.OrderItemsTable.COLUMN_ORDER_ITEM_STATUS_VALUE + " IN " + TextUtils.join(", ", orderItemStatusValues);
        }

        return getCursor(query);
    }

    private SparseArray<String> getPresentOrderIDs() {
        if (mPresentOrderIDs != null){
            return mPresentOrderIDs;
        }
        String[] columns = new String[]{OrdersContract.OrdersTable.COLUMN_ORDER_ID, OrdersContract.OrdersTable.COLUMN_UPDATED_AT};
        Cursor cursor = getOrdersData(null, null, columns);
        SparseArray<String> orderIDs = new SparseArray<>();
        while (cursor.moveToNext()) {
            orderIDs.put(cursor.getInt(cursor.getColumnIndexOrThrow(OrdersContract.OrdersTable.COLUMN_ORDER_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(OrdersContract.OrdersTable.COLUMN_UPDATED_AT)));
        }
        mPresentOrderIDs = orderIDs;
        return orderIDs;
    }

    private SparseArray<String> getPresentSuborderIDs() {
        if (mPresentSuborderIDs!= null){
            return mPresentSuborderIDs;
        }
        String[] columns = new String[]{OrdersContract.SubordersTable.COLUMN_SUBORDER_ID, OrdersContract.SubordersTable.COLUMN_UPDATED_AT};
        Cursor cursor = getSubordersData(null, null, null,columns);
        SparseArray<String> suborderIDs = new SparseArray<>();
        while (cursor.moveToNext()) {
            suborderIDs.put(cursor.getInt(cursor.getColumnIndexOrThrow(OrdersContract.SubordersTable.COLUMN_SUBORDER_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(OrdersContract.SubordersTable.COLUMN_UPDATED_AT)));
        }
        mPresentSuborderIDs = suborderIDs;
        return suborderIDs;
    }

    private SparseArray<String> getPresentOrderItemIDs() {
        if (mPresentOrderItemIDs!=null){
            return mPresentOrderItemIDs;
        }
        String[] columns = new String[]{OrdersContract.OrderItemsTable.COLUMN_ORDER_ITEM_ID, OrdersContract.OrderItemsTable.COLUMN_UPDATED_AT};
        Cursor cursor = getOrderItemsData(null, null, null,columns);
        SparseArray<String> orderItemIDs = new SparseArray<>();
        while (cursor.moveToNext()) {
            orderItemIDs.put(cursor.getInt(cursor.getColumnIndexOrThrow(OrdersContract.OrderItemsTable.COLUMN_ORDER_ITEM_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(OrdersContract.OrderItemsTable.COLUMN_UPDATED_AT)));
        }
        mPresentOrderItemIDs = orderItemIDs;
        return orderItemIDs;
    }

    public void saveOrdersData(JSONArray ordersArray) throws JSONException {
        SQLiteDatabase db = mDatabaseHelper.openDatabase();
        UserDBHelper userDBHelper = new UserDBHelper(mContext);

        try {
            db.beginTransaction();
            for (int i = 0; i < ordersArray.length(); i++) {
                JSONObject order = ordersArray.getJSONObject(i);
                saveOrderData(ordersArray.getJSONObject(i));

                userDBHelper.updateUserAddressDataFromJSONObject(order.getJSONObject("buyer_address"));

                if (order.has("sub_orders")){
                    saveSubordersData(order.getJSONArray("sub_orders"));
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

    public void saveSubordersData(JSONArray subordersArray) throws JSONException{

        CatalogDBHelper catalogDBHelper = new CatalogDBHelper(mContext);
        for (int i = 0; i < subordersArray.length(); i++) {
            JSONObject suborder = subordersArray.getJSONObject(i);
            saveSuborderData(suborder);

            if (suborder.has("seller")){
                catalogDBHelper.saveSellerData(suborder.getJSONObject("seller"));
            }

            if (suborder.has("order_items")){
                saveOrderItemsData(suborder.getJSONArray("order_items"));
            }
        }
    }

    public void saveOrderItemsData(JSONArray orderItemsArray) throws JSONException{

        CatalogDBHelper catalogDBHelper = new CatalogDBHelper(mContext);
        for (int i = 0; i < orderItemsArray.length(); i++) {
            JSONObject orderItem = orderItemsArray.getJSONObject(i);
            saveOrderItemData(orderItem);

            if (orderItem.has("product")){
                catalogDBHelper.saveProductData(orderItem.getJSONObject("product"));
            }

        }
    }

    public void saveOrderData(JSONObject order) throws JSONException{
        SQLiteDatabase db = mDatabaseHelper.openDatabase();
        int orderID = order.getInt(OrdersContract.OrdersTable.COLUMN_ORDER_ID);
        String orderUpdatedAtLocal = getPresentOrderIDs().get(orderID);
        String orderUpdatedAtServer = order.getString(OrdersContract.OrdersTable.COLUMN_UPDATED_AT);
        if (orderUpdatedAtLocal == null) { // insert
            ContentValues values = getOrderContentValues(order);
            db.insert(OrdersContract.OrdersTable.TABLE_NAME, null, values);
            mPresentOrderIDs.put(orderID, orderUpdatedAtServer);
        } else if (!orderUpdatedAtLocal.equals(orderUpdatedAtServer)) {
            ContentValues values = getOrderContentValues(order);
            String selection = OrdersContract.OrdersTable.COLUMN_ORDER_ID + " = " + orderID;
            db.update(OrdersContract.OrdersTable.TABLE_NAME, values, selection, null);
            mPresentOrderIDs.put(orderID, orderUpdatedAtServer);
        }
        mDatabaseHelper.closeDatabase();
    }

    public void saveSuborderData(JSONObject suborder) throws JSONException{
        SQLiteDatabase db = mDatabaseHelper.openDatabase();
        int suborderID = suborder.getInt(OrdersContract.SubordersTable.COLUMN_SUBORDER_ID);
        String suborderUpdatedAtLocal = getPresentSuborderIDs().get(suborderID);
        String suborderUpdatedAtServer = suborder.getString(OrdersContract.SubordersTable.COLUMN_UPDATED_AT);
        if (suborderUpdatedAtLocal == null) { // insert
            ContentValues values = getSubOrderContentValues(suborder);
            db.insert(OrdersContract.SubordersTable.TABLE_NAME, null, values);
            mPresentSuborderIDs.put(suborderID, suborderUpdatedAtServer);
        } else if (!suborderUpdatedAtLocal.equals(suborderUpdatedAtServer)) {
            ContentValues values = getSubOrderContentValues(suborder);
            String selection = OrdersContract.SubordersTable.COLUMN_SUBORDER_ID + " = " + suborderID;
            db.update(OrdersContract.SubordersTable.TABLE_NAME, values, selection, null);
            mPresentSuborderIDs.put(suborderID, suborderUpdatedAtServer);
        }

        mDatabaseHelper.closeDatabase();
    }

    public void saveOrderItemData(JSONObject orderItem) throws JSONException{
        SQLiteDatabase db = mDatabaseHelper.openDatabase();
        int orderItemID = orderItem.getInt(OrdersContract.OrderItemsTable.COLUMN_ORDER_ITEM_ID);
        String orderItemUpdatedAtLocal = getPresentOrderItemIDs().get(orderItemID);
        String orderItemUpdatedAtServer = orderItem.getString(OrdersContract.OrderItemsTable.COLUMN_UPDATED_AT);
        if (orderItemUpdatedAtLocal == null) { // insert
            ContentValues values = getOrderItemContentValues(orderItem);
            db.insert(OrdersContract.OrderItemsTable.TABLE_NAME, null, values);
            mPresentOrderItemIDs.put(orderItemID, orderItemUpdatedAtServer);
        } else if (!orderItemUpdatedAtLocal.equals(orderItemUpdatedAtServer)) {
            ContentValues values = getOrderItemContentValues(orderItem);
            String selection = OrdersContract.OrderItemsTable.COLUMN_ORDER_ITEM_ID + " = " + orderItemID;
            db.update(OrdersContract.OrderItemsTable.TABLE_NAME, values, selection, null);
            mPresentOrderItemIDs.put(orderItemID, orderItemUpdatedAtServer);
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
        values.put(OrdersContract.SubordersTable.COLUMN_SELLER_ID, seller.getInt(CatalogContract.SellersTable.COLUMN_SELLER_ID));
        JSONObject sellerAddress = suborder.getJSONObject("seller_address");
        values.put(OrdersContract.SubordersTable.COLUMN_SELLER_ADDRESS_ID, sellerAddress.getInt(CatalogContract.SellerAddressTable.COLUMN_ADDRESS_ID));
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
    
    private ContentValues getOrderItemContentValues(JSONObject orderitem) throws JSONException{
        ContentValues values = new ContentValues();
        values.put(OrdersContract.OrderItemsTable.COLUMN_ORDER_ITEM_ID, orderitem.getInt(OrdersContract.OrderItemsTable.COLUMN_ORDER_ITEM_ID));
        values.put(OrdersContract.OrderItemsTable.COLUMN_SUBORDER_ID, orderitem.getInt(OrdersContract.OrderItemsTable.COLUMN_SUBORDER_ID));
        JSONObject product = orderitem.getJSONObject("product");
        values.put(OrdersContract.OrderItemsTable.COLUMN_PRODUCT_ID, product.getInt(CatalogContract.ProductsTable.COLUMN_PRODUCT_ID));
        //TODO: What to do with null order shipment ID
        int orderShipmentID = 0;
        String trackingUrl = "";
        if (orderitem.has(OrdersContract.OrderItemsTable.COLUMN_ORDER_SHIPMENT_ID) && !orderitem.isNull(OrdersContract.OrderItemsTable.COLUMN_ORDER_SHIPMENT_ID)){
            orderShipmentID = orderitem.getInt(OrdersContract.OrderItemsTable.COLUMN_ORDER_SHIPMENT_ID);
            trackingUrl = orderitem.getString(OrdersContract.OrderItemsTable.COLUMN_TRACKING_URL);
        }
        values.put(OrdersContract.OrderItemsTable.COLUMN_ORDER_SHIPMENT_ID, orderShipmentID);
        values.put(OrdersContract.OrderItemsTable.COLUMN_LOTS, orderitem.getInt(OrdersContract.OrderItemsTable.COLUMN_LOTS));
        values.put(OrdersContract.OrderItemsTable.COLUMN_LOT_SIZE, orderitem.getInt(OrdersContract.OrderItemsTable.COLUMN_LOT_SIZE));
        values.put(OrdersContract.OrderItemsTable.COLUMN_PIECES, orderitem.getInt(OrdersContract.OrderItemsTable.COLUMN_PIECES));
        values.put(OrdersContract.OrderItemsTable.COLUMN_RETAIL_PRICE_PER_PIECE, orderitem.getDouble(OrdersContract.OrderItemsTable.COLUMN_RETAIL_PRICE_PER_PIECE));
        values.put(OrdersContract.OrderItemsTable.COLUMN_CALCULATED_PRICE_PER_PIECE, orderitem.getDouble(OrdersContract.OrderItemsTable.COLUMN_CALCULATED_PRICE_PER_PIECE));
        values.put(OrdersContract.OrderItemsTable.COLUMN_EDITED_PRICE_PER_PIECE, orderitem.getDouble(OrdersContract.OrderItemsTable.COLUMN_EDITED_PRICE_PER_PIECE));
        values.put(OrdersContract.OrderItemsTable.COLUMN_FINAL_PRICE, orderitem.getDouble(OrdersContract.OrderItemsTable.COLUMN_FINAL_PRICE));
        JSONObject orderItemStatus = orderitem.getJSONObject("order_item_status");
        values.put(OrdersContract.OrderItemsTable.COLUMN_ORDER_ITEM_STATUS_VALUE, orderItemStatus.getInt("value"));
        values.put(OrdersContract.OrderItemsTable.COLUMN_ORDER_ITEM_STATUS_DISPLAY, orderItemStatus.getString("display_value"));
        values.put(OrdersContract.OrderItemsTable.COLUMN_TRACKING_URL, trackingUrl);
        values.put(OrdersContract.OrderItemsTable.COLUMN_CREATED_AT, orderitem.getString(OrdersContract.OrderItemsTable.COLUMN_CREATED_AT));
        values.put(OrdersContract.OrderItemsTable.COLUMN_UPDATED_AT, orderitem.getString(OrdersContract.OrderItemsTable.COLUMN_UPDATED_AT));
        values.put(OrdersContract.OrderItemsTable.COLUMN_REMARKS, orderitem.getString(OrdersContract.OrderItemsTable.COLUMN_REMARKS));
        return values;
    }
}
