package com.wholdus.www.wholdusbuyerapp.databaseHelpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.wholdus.www.wholdusbuyerapp.databaseContracts.OrdersContract;
import com.wholdus.www.wholdusbuyerapp.databaseContracts.UserProfileContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kaustubh on 8/12/16.
 */

public class OrderDBHelper extends BaseDBHelper {

    public OrderDBHelper(Context context) {
        super(context);
    }

    public Cursor getOrdersData(@Nullable List<Integer> orderStatusValues,
                                @Nullable String orderID, @Nullable List<String> columns) {
        String columnNames;
        if (columns!= null && !columns.isEmpty()){
            columnNames = TextUtils.join(", ", columns);
        } else {columnNames = "*";}

        String query = "SELECT " + columnNames +" FROM " + OrdersContract.OrdersTable.TABLE_NAME;
        boolean whereApplied = false;
        if (orderID!= null && !TextUtils.isEmpty(orderID)){
            query += "WHERE " + OrdersContract.OrdersTable.COLUMN_ORDER_ID + " = " + orderID;
            whereApplied = true;
        }
        if (orderStatusValues!= null && !orderStatusValues.isEmpty()){
            if (whereApplied){query += " AND ";
            } else {query += " WHERE ";}
            query += OrdersContract.OrdersTable.COLUMN_ORDER_STATUS_VALUE + " IN " + TextUtils.join(", ", orderStatusValues);
        }

        return getCursor(query);
    }

    public ArrayList<String> getPresentOrderIDs(){
        ArrayList<String> columns = new ArrayList<>();
        columns.add(OrdersContract.OrdersTable.COLUMN_ORDER_ID);
        Cursor cursor = getOrdersData(null, null, columns);
        ArrayList<String> orderIDs = new ArrayList<>();
        while (cursor.moveToNext()){
            orderIDs.add(cursor.getString(cursor.getColumnIndexOrThrow(OrdersContract.OrdersTable.COLUMN_ORDER_ID)));
        }
        return orderIDs;
    }

    public void saveOrdersData(JSONArray ordersArray) throws JSONException {
        SQLiteDatabase db = mDatabaseHelper.openDatabase();

        try {

            UserDBHelper userDBHelper = new UserDBHelper(mContext);
            List<String> presentBuyerAddressIDs = userDBHelper.getPresentBuyerAddressIDs();
            List<String> presentOrderIDs = getPresentOrderIDs();

            db.beginTransaction();
            for (int i = 0; i < ordersArray.length(); i++) {
                JSONObject order = ordersArray.getJSONObject(i);
                ContentValues values = getOrderContentValues(order);
                JSONObject buyerAddress = order.getJSONObject("buyer_address");
                userDBHelper.updateUserAddressDataFromJSONObject(buyerAddress,
                        db, presentBuyerAddressIDs.contains(buyerAddress.getString(OrdersContract.OrdersTable.COLUMN_BUYER_ADDRESS_ID)));
                String orderID = order.getString(OrdersContract.OrdersTable.COLUMN_ORDER_ID);

                if (!presentOrderIDs.contains(orderID)) { // insert
                    db.insert(OrdersContract.OrdersTable.TABLE_NAME, null, values);
                } else {
                    String selection = OrdersContract.OrdersTable.COLUMN_ORDER_ID + " = " + orderID;
                    db.update(OrdersContract.OrdersTable.TABLE_NAME, values, selection, null);
                }

            }

            db.setTransactionSuccessful();
        }catch (Exception e) {
        }
        finally {
            db.endTransaction();
        }

        mDatabaseHelper.closeDatabase();
    }

    public ContentValues getOrderContentValues(JSONObject order) throws JSONException{
        ContentValues values = new ContentValues();
        values.put(OrdersContract.OrdersTable.COLUMN_ORDER_ID, order.getString(OrdersContract.OrdersTable.COLUMN_ORDER_ID));
        values.put(OrdersContract.OrdersTable.COLUMN_DISPLAY_NUMBER, order.getString(OrdersContract.OrdersTable.COLUMN_DISPLAY_NUMBER));
        JSONObject buyerAddress = order.getJSONObject("buyer_address");
        values.put(OrdersContract.OrdersTable.COLUMN_BUYER_ADDRESS_ID, buyerAddress.getString(OrdersContract.OrdersTable.COLUMN_BUYER_ADDRESS_ID));
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
        values.put(OrdersContract.OrdersTable.COLUMN_REMARKS, order.getString(OrdersContract.OrdersTable.COLUMN_REMARKS));
        return values;
    }

}
