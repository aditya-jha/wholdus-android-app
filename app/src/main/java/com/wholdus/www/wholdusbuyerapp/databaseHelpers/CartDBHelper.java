package com.wholdus.www.wholdusbuyerapp.databaseHelpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.SparseArray;

import com.wholdus.www.wholdusbuyerapp.databaseContracts.CartContract.CartTable;
import com.wholdus.www.wholdusbuyerapp.databaseContracts.CartContract.SubCartsTable;
import com.wholdus.www.wholdusbuyerapp.databaseContracts.CartContract.CartItemsTable;
import com.wholdus.www.wholdusbuyerapp.databaseContracts.CatalogContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by kaustubh on 21/12/16.
 */

public class CartDBHelper extends BaseDBHelper {

    private SparseArray<String> mPresentSubCartIds;
    private SparseArray<String> mPresentCartItemProductIds;

    public CartDBHelper(Context context) {
        super(context);
    }

    public Cursor getCartData(int cartID, int synced, @Nullable String[] columns){

        String columnNames = getColumnNamesString(columns);

        String query = "SELECT " + columnNames + " FROM " + CartTable.TABLE_NAME;

        boolean whereApplied = false;
        if (cartID != -1) {
            query += " WHERE " + CartTable.COLUMN_CART_ID + " = " + cartID;
            whereApplied = true;
        }
        if (synced != -1) {
            query += whereClauseHelper(whereApplied) + CartTable.COLUMN_SYNCED + " = " + synced;
        }

        return getCursor(query);

    }

    public Cursor getSubCartData(int cartID, int subCartID, int sellerID, int synced, @Nullable String[] columns){

        String columnNames = getColumnNamesString(columns);

        String query = "SELECT " + columnNames + " FROM " + SubCartsTable.TABLE_NAME;

        boolean whereApplied = false;
        if (cartID != -1) {
            query += " WHERE " + SubCartsTable.COLUMN_CART_ID + " = " + cartID;
            whereApplied = true;
        }
        if (subCartID != -1) {
            query += whereClauseHelper(whereApplied) + SubCartsTable.COLUMN_SUBCART_ID + " = " + subCartID;
            whereApplied = true;
        }
        if (sellerID != -1) {
            query += whereClauseHelper(whereApplied) + SubCartsTable.COLUMN_SELLER_ID + " = " + sellerID;
            whereApplied = true;
        }
        if (synced != -1) {
            query += whereClauseHelper(whereApplied) + SubCartsTable.COLUMN_SYNCED + " = " + synced;
        }

        return getCursor(query);

    }

    public Cursor getCartItemsData(int cartItemID, @Nullable ArrayList<Integer> excludeCartItemIDs, int subCartID, int productID, int synced, @Nullable String[] columns){

        String columnNames = getColumnNamesString(columns);

        String query = "SELECT " + columnNames + " FROM " + CartItemsTable.TABLE_NAME;

        boolean whereApplied = false;
        if (cartItemID != -1) {
            query += " WHERE " + CartItemsTable.COLUMN_CART_ITEM_ID + " = " + cartItemID;
            whereApplied = true;
        }
        if (excludeCartItemIDs!= null && !excludeCartItemIDs.isEmpty()){
            query += whereClauseHelper(whereApplied) + CartItemsTable.COLUMN_CART_ITEM_ID + " NOT IN (" + TextUtils.join(", ", excludeCartItemIDs) + ") ";
            whereApplied = true;
        }
        if (subCartID != -1) {
            query += whereClauseHelper(whereApplied) + CartItemsTable.COLUMN_SUBCART_ID + " = " + subCartID;
            whereApplied = true;
        }
        if (productID != -1) {
            query += whereClauseHelper(whereApplied) + CartItemsTable.COLUMN_PRODUCT_ID + " = " + productID;
            whereApplied = true;
        }
        if (synced != -1) {
            query += whereClauseHelper(whereApplied) + CartItemsTable.COLUMN_SYNCED + " = " + synced;
        }

        return getCursor(query);

    }

    public SparseArray<String> getPresentSubCartIds(){
        if (mPresentSubCartIds != null){
            return mPresentSubCartIds;
        }
        String[] columns = {SubCartsTable.COLUMN_SUBCART_ID, SubCartsTable.COLUMN_UPDATED_AT};
        Cursor cursor = getSubCartData(-1, -1, -1, -1, columns);
        SparseArray<String> subCartIDs = new SparseArray<>();
        while (cursor.moveToNext()){
            subCartIDs.put(cursor.getInt(cursor.getColumnIndexOrThrow(SubCartsTable.COLUMN_SUBCART_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(SubCartsTable.COLUMN_UPDATED_AT)));
        }
        mPresentSubCartIds = subCartIDs;
        return subCartIDs;
    }

    public SparseArray<String> getPresentCartItemProductIds(){
        if (mPresentCartItemProductIds != null){
            return mPresentCartItemProductIds;
        }
        String[] columns = {CartItemsTable.COLUMN_PRODUCT_ID, CartItemsTable.COLUMN_UPDATED_AT};
        Cursor cursor = getCartItemsData(-1, null, -1, -1, -1, columns);
        SparseArray<String> cartItemProductIDs = new SparseArray<>();
        while (cursor.moveToNext()){
            cartItemProductIDs.put(cursor.getInt(cursor.getColumnIndexOrThrow(CartItemsTable.COLUMN_PRODUCT_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(CartItemsTable.COLUMN_UPDATED_AT)));
        }
        mPresentCartItemProductIds = cartItemProductIDs;
        return cartItemProductIDs;
    }

    public void deleteCart(int cartID) throws JSONException{
        SQLiteDatabase db = mDatabaseHelper.openDatabase();
        String selection = null;
        if (cartID != - 1){
            selection = CartTable.COLUMN_CART_ID + " = " + cartID;
        }
        db.delete(CartTable.TABLE_NAME, selection, null);
        deleteSubCarts(null);
        mDatabaseHelper.closeDatabase();
    }

    public void deleteSubCarts(@Nullable ArrayList<Integer> excludeSubCartIDs) throws JSONException{
        SQLiteDatabase db = mDatabaseHelper.openDatabase();
        String selection = null;
        if (excludeSubCartIDs != null && !excludeSubCartIDs.isEmpty()) {
            selection = SubCartsTable.COLUMN_SUBCART_ID + " NOT IN (" + TextUtils.join(", ", excludeSubCartIDs) + ")";
        }
        db.delete(SubCartsTable.TABLE_NAME, selection, null);
        mPresentSubCartIds = null;
        deleteCartItems(-1, excludeSubCartIDs, null);
        mDatabaseHelper.closeDatabase();
    }

    public void deleteCartItems(int subCartID, @Nullable ArrayList<Integer> excludeSubCartIDs, @Nullable ArrayList<Integer> excludeCartItemIDs) throws JSONException{
        SQLiteDatabase db = mDatabaseHelper.openDatabase();
        if (excludeCartItemIDs == null){
            excludeCartItemIDs = new ArrayList<>();
        }
        excludeCartItemIDs.add(0);
        String selection = CartItemsTable.COLUMN_CART_ITEM_ID + " NOT IN (" + TextUtils.join(", ", excludeCartItemIDs) + ")";
        if (subCartID != -1){
            selection += " AND " + CartItemsTable.COLUMN_SUBCART_ID + " = " + subCartID;
        }
        if(excludeSubCartIDs != null && !excludeSubCartIDs.isEmpty()){
            selection += " AND " + CartItemsTable.COLUMN_SUBCART_ID + " NOT IN (" + TextUtils.join(", ", excludeSubCartIDs) + ")";
        }
        db.delete(CartItemsTable.TABLE_NAME,selection,null);
        mPresentCartItemProductIds = null;
        mDatabaseHelper.closeDatabase();
    }

    public void saveCartDataFromJSONObject(JSONObject cart) throws JSONException{
        SQLiteDatabase db = mDatabaseHelper.openDatabase();
        String[] columns = {CartTable.COLUMN_UPDATED_AT};
        Cursor cursor = getCartData(-1, -1, columns);
        try {
            db.beginTransaction();
            boolean cartInserted = false;
            if (cursor.getCount() == 0) {
                ContentValues values = getCartContentValues(cart);
                db.insert(CartTable.TABLE_NAME, null, values);
                cartInserted = true;
            }
            if (!cartInserted) {
                cursor.moveToNext();
                if (!cart.getString(CartTable.COLUMN_UPDATED_AT).equals(cursor.getString(cursor.getColumnIndexOrThrow(CartTable.COLUMN_UPDATED_AT)))) {
                    ContentValues values = getCartContentValues(cart);
                    db.update(CartTable.TABLE_NAME, values, null, null);
                    cartInserted = true;
                }
            }
            if (cartInserted && cart.has("sub_carts")) {
                JSONArray subCarts = cart.getJSONArray("sub_carts");
                ArrayList<Integer> subCartIDs = new ArrayList<>();
                for (int i = 0; i < subCarts.length(); i++) {
                    JSONObject subCart = subCarts.getJSONObject(i);
                    saveSubCartDataFromJSONObject(subCart);
                    subCartIDs.add(subCart.getInt(SubCartsTable.COLUMN_SUBCART_ID));
                }
                deleteSubCarts(subCartIDs);
            }
            db.setTransactionSuccessful();
        }catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
        mDatabaseHelper.closeDatabase();
    }

    public void saveSubCartDataFromJSONObject(JSONObject subCart)throws JSONException{
        SQLiteDatabase db = mDatabaseHelper.openDatabase();
        int subCartID = subCart.getInt(SubCartsTable.COLUMN_SUBCART_ID);
        String subCartUpdatedAtLocal = getPresentSubCartIds().get(subCartID);
        String subCartUpdatedAtServer = subCart.getString(SubCartsTable.COLUMN_UPDATED_AT);
        boolean subCartInserted = false;
        if (subCartUpdatedAtLocal == null){
            ContentValues values = getSubCartContentValues(subCart);
            db.insert(SubCartsTable.TABLE_NAME, null, values);
            mPresentSubCartIds.put(subCartID, subCartUpdatedAtServer);
            subCartInserted = true;
        } else if (!subCartUpdatedAtLocal.equals(subCartUpdatedAtServer)){
            ContentValues values = getSubCartContentValues(subCart);
            String selection = SubCartsTable.COLUMN_SUBCART_ID + " = " + subCartID;
            db.update(SubCartsTable.TABLE_NAME, values, selection, null);
            mPresentSubCartIds.put(subCartID, subCartUpdatedAtServer);
            subCartInserted = true;
        }
        if (subCartInserted && subCart.has("cart_items")){
            JSONArray cartItems = subCart.getJSONArray("cart_items");
            ArrayList<Integer> cartItemIDs = new ArrayList<>();
            for (int i = 0; i < cartItems.length(); i++) {
                JSONObject cartItem = cartItems.getJSONObject(i);
                saveCartItemDataFromJSONObject(cartItem);
                cartItemIDs.add(cartItem.getInt(CartItemsTable.COLUMN_CART_ITEM_ID));
            }
            deleteCartItems(subCartID, null, cartItemIDs);
        }

        mDatabaseHelper.closeDatabase();
    }

    public void saveCartItemDataFromJSONObject(JSONObject cartItem) throws JSONException{
        SQLiteDatabase db = mDatabaseHelper.openDatabase();
        int cartItemID = cartItem.getInt(CartItemsTable.COLUMN_CART_ITEM_ID);
        if (!cartItem.has("product")){
            return;
        }
        CatalogDBHelper catalogDBHelper = new CatalogDBHelper(mContext);
        JSONObject product = cartItem.getJSONObject("product");
        catalogDBHelper.saveProductData(product);
        int productID = product.getInt(CatalogContract.ProductsTable.COLUMN_PRODUCT_ID);
        String cartItemUpdatedAtLocal = getPresentCartItemProductIds().get(productID);
        String cartItemUpdatedAtServer = cartItem.getString(CartItemsTable.COLUMN_UPDATED_AT);
        // Local save cart item ID = 0 and updated at is blank string
        if (cartItemID == 0){
            ContentValues values = getCartItemContentValues(cartItem);
            if (cartItemUpdatedAtLocal == null) {
                db.insert(CartItemsTable.TABLE_NAME, null, values);
            } else {
                String selection = CartItemsTable.COLUMN_PRODUCT_ID + " = " + productID;
                db.update(CartItemsTable.TABLE_NAME, values, selection, null);
            }
            ContentValues subCartValues = new ContentValues();
            subCartValues.put(SubCartsTable.COLUMN_SYNCED, 0);
            String selection = SubCartsTable.COLUMN_SUBCART_ID + " = " + cartItem.getInt(CartItemsTable.COLUMN_SUBCART_ID);
            db.update(SubCartsTable.TABLE_NAME, subCartValues, selection, null);
            db.update(CartTable.TABLE_NAME, subCartValues, null, null);
        } else {
            if (cartItemUpdatedAtLocal == null){
                ContentValues values = getCartItemContentValues(cartItem);
                db.insert(CartItemsTable.TABLE_NAME, null, values);
            } else if (!cartItemUpdatedAtLocal.equals(cartItemUpdatedAtServer)) {
                ContentValues values = getCartItemContentValues(cartItem);
                String selection = CartItemsTable.COLUMN_CART_ITEM_ID + " = " + cartItemID + " AND (( " +
                        CartItemsTable.COLUMN_SYNCED + " = 1) OR ( " +
                        CartItemsTable.COLUMN_SYNCED + " = 0 AND " +
                        CartItemsTable.COLUMN_LOTS + " = " + cartItem.getInt(CartItemsTable.COLUMN_LOTS) + ")) ";
                db.update(CartItemsTable.TABLE_NAME, values, selection, null);
            }
        }
        mPresentCartItemProductIds.put(productID, cartItemUpdatedAtServer);
        mDatabaseHelper.closeDatabase();
    }

    public ContentValues getCartContentValues(JSONObject cart) throws JSONException{
        ContentValues values = new ContentValues();
        values.put(CartTable.COLUMN_CART_ID, cart.getInt(CartTable.COLUMN_CART_ID));
        values.put(CartTable.COLUMN_PRODUCT_COUNT, cart.getInt(CartTable.COLUMN_PRODUCT_COUNT));
        values.put(CartTable.COLUMN_PIECES, cart.getInt(CartTable.COLUMN_PIECES));
        values.put(CartTable.COLUMN_RETAIL_PRICE, cart.getInt(CartTable.COLUMN_RETAIL_PRICE));
        values.put(CartTable.COLUMN_CALCULATED_PRICE, cart.getDouble(CartTable.COLUMN_CALCULATED_PRICE));
        values.put(CartTable.COLUMN_SHIPPING_CHARGE, cart.getDouble(CartTable.COLUMN_SHIPPING_CHARGE));
        values.put(CartTable.COLUMN_FINAL_PRICE, cart.getDouble(CartTable.COLUMN_FINAL_PRICE));
        values.put(CartTable.COLUMN_CREATED_AT, cart.getString(CartTable.COLUMN_CREATED_AT));
        values.put(CartTable.COLUMN_UPDATED_AT, cart.getString(CartTable.COLUMN_UPDATED_AT));
        values.put(CartTable.COLUMN_SYNCED, 1);
        return values;
    }

    public ContentValues getSubCartContentValues(JSONObject cart) throws JSONException{
        ContentValues values = new ContentValues();
        values.put(SubCartsTable.COLUMN_CART_ID, cart.getInt(SubCartsTable.COLUMN_CART_ID));
        values.put(SubCartsTable.COLUMN_SUBCART_ID, cart.getInt(SubCartsTable.COLUMN_SUBCART_ID));
        JSONObject seller = cart.getJSONObject("seller");
        values.put(SubCartsTable.COLUMN_SELLER_ID, seller.getInt(SubCartsTable.COLUMN_SELLER_ID));
        values.put(SubCartsTable.COLUMN_PRODUCT_COUNT, cart.getInt(SubCartsTable.COLUMN_PRODUCT_COUNT));
        values.put(SubCartsTable.COLUMN_PIECES, cart.getInt(SubCartsTable.COLUMN_PIECES));
        values.put(SubCartsTable.COLUMN_RETAIL_PRICE, cart.getInt(SubCartsTable.COLUMN_RETAIL_PRICE));
        values.put(SubCartsTable.COLUMN_CALCULATED_PRICE, cart.getDouble(SubCartsTable.COLUMN_CALCULATED_PRICE));
        values.put(SubCartsTable.COLUMN_SHIPPING_CHARGE, cart.getDouble(SubCartsTable.COLUMN_SHIPPING_CHARGE));
        values.put(SubCartsTable.COLUMN_FINAL_PRICE, cart.getDouble(SubCartsTable.COLUMN_FINAL_PRICE));
        values.put(SubCartsTable.COLUMN_CREATED_AT, cart.getString(SubCartsTable.COLUMN_CREATED_AT));
        values.put(SubCartsTable.COLUMN_UPDATED_AT, cart.getString(SubCartsTable.COLUMN_UPDATED_AT));
        values.put(SubCartsTable.COLUMN_SYNCED, 1);
        return values;
    }

    public ContentValues getCartItemContentValues(JSONObject cart) throws JSONException{
        ContentValues values = new ContentValues();
        values.put(CartItemsTable.COLUMN_CART_ITEM_ID, cart.getInt(CartItemsTable.COLUMN_CART_ITEM_ID));
        values.put(CartItemsTable.COLUMN_SUBCART_ID, cart.getInt(CartItemsTable.COLUMN_SUBCART_ID));
        JSONObject product = cart.getJSONObject("product");
        values.put(CartItemsTable.COLUMN_PRODUCT_ID, product.getInt(CartItemsTable.COLUMN_PRODUCT_ID));
        values.put(CartItemsTable.COLUMN_LOTS, cart.getInt(CartItemsTable.COLUMN_LOTS));
        values.put(CartItemsTable.COLUMN_PIECES, cart.getInt(CartItemsTable.COLUMN_PIECES));
        values.put(CartItemsTable.COLUMN_LOT_SIZE, cart.getInt(CartItemsTable.COLUMN_LOT_SIZE));
        values.put(CartItemsTable.COLUMN_RETAIL_PRICE_PER_PIECE, cart.getInt(CartItemsTable.COLUMN_RETAIL_PRICE_PER_PIECE));
        values.put(CartItemsTable.COLUMN_CALCULATED_PRICE_PER_PIECE, cart.getDouble(CartItemsTable.COLUMN_CALCULATED_PRICE_PER_PIECE));
        values.put(CartItemsTable.COLUMN_SHIPPING_CHARGE, cart.getDouble(CartItemsTable.COLUMN_SHIPPING_CHARGE));
        values.put(CartItemsTable.COLUMN_FINAL_PRICE, cart.getDouble(CartItemsTable.COLUMN_FINAL_PRICE));
        values.put(CartItemsTable.COLUMN_CREATED_AT, cart.getString(CartItemsTable.COLUMN_CREATED_AT));
        values.put(CartItemsTable.COLUMN_UPDATED_AT, cart.getString(CartItemsTable.COLUMN_UPDATED_AT));
        if (cart.has(CartItemsTable.COLUMN_SYNCED)){
            values.put(CartItemsTable.COLUMN_SYNCED, cart.getInt(CartItemsTable.COLUMN_SYNCED));
        } else {
            values.put(CartItemsTable.COLUMN_SYNCED, 1);
        }
        return values;
    }
    
}
