package com.wholdus.www.wholdusbuyerapp.databaseContracts;

import android.provider.BaseColumns;

/**
 * Created by kaustubh on 7/12/16.
 */

public class CartContract {

    private CartContract() {}

    public static final class CartTable implements BaseColumns {
        public static final String TABLE_NAME = "cart";
        public static final String COLUMN_CART_ID = "cartID";

        public static final String COLUMN_PRODUCT_COUNT = "product_count";
        public static final String COLUMN_PIECES = "pieces";
        public static final String COLUMN_RETAIL_PRICE = "retail_price";
        public static final String COLUMN_CALCULATED_PRICE = "calculated_price";
        public static final String COLUMN_SHIPPING_CHARGE = "shipping_charge";
        public static final String COLUMN_FINAL_PRICE = "final_price";

        public static final String COLUMN_CREATED_AT = "created_at";
        public static final String COLUMN_UPDATED_AT = "updated_at";
    }

    public static final class SubCartsTable implements BaseColumns {
        public static final String TABLE_NAME = "subcarts";
        public static final String COLUMN_CART_ID = "cartID";
        public static final String COLUMN_SUBCART_ID = "subcartID";
        public static final String COLUMN_SELLER_ID = "sellerID";

        public static final String COLUMN_PRODUCT_COUNT = "product_count";
        public static final String COLUMN_PIECES = "pieces";
        public static final String COLUMN_RETAIL_PRICE = "retail_price";
        public static final String COLUMN_CALCULATED_PRICE = "calculated_price";
        public static final String COLUMN_SHIPPING_CHARGE = "shipping_charge";
        public static final String COLUMN_FINAL_PRICE = "final_price";

        public static final String COLUMN_CREATED_AT = "created_at";
        public static final String COLUMN_UPDATED_AT = "updated_at";
    }

    public static final class CartItemsTable implements BaseColumns {
        public static final String TABLE_NAME = "cart_items";
        public static final String COLUMN_CART_ITEM_ID = "cartitemID";
        public static final String COLUMN_SUBCART_ID = "subcartID";
        public static final String COLUMN_PRODUCT_ID = "productID";

        public static final String COLUMN_PIECES = "pieces";
        public static final String COLUMN_LOTS = "lots";
        public static final String COLUMN_LOT_SIZE = "lot_size";
        public static final String COLUMN_RETAIL_PRICE_PER_PIECE = "retail_price_piece";
        public static final String COLUMN_CALCULATED_PRICE_PER_PIECE = "calculated_price_per_piece";
        public static final String COLUMN_SHIPPING_CHARGE = "shipping_charge";
        public static final String COLUMN_FINAL_PRICE = "final_price";

        public static final String COLUMN_STATUS = "status";

        public static final String COLUMN_CREATED_AT = "created_at";
        public static final String COLUMN_UPDATED_AT = "updated_at";
    }
}
