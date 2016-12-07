package com.wholdus.www.wholdusbuyerapp.databaseContracts;

import android.provider.BaseColumns;

/**
 * Created by kaustubh on 6/12/16.
 */

public final class OrdersContract {

    private OrdersContract() {}

    public static final class OrdersTable implements BaseColumns {
        public static final String TABLE_NAME = "orders";
        public static final String COLUMN_ORDER_ID = "orderID";
        public static final String COLUMN_DISPLAY_NUMBER = "display_number";
        public static final String COLUMN_BUYER_ADDRESS_ID = "buyeraddressID";

        public static final String COLUMN_PRODUCT_COUNT = "product_count";
        public static final String COLUMN_PIECES = "pieces";
        public static final String COLUMN_RETAIL_PRICE = "retail_price";
        public static final String COLUMN_CALCULATED_PRICE = "calculated_price";
        public static final String COLUMN_EDITED_PRICE = "edited_price";
        public static final String COLUMN_SHIPPING_CHARGE = "shipping_charge";
        public static final String COLUMN_COD_CHARGE = "cod_charge";
        public static final String COLUMN_FINAL_PRICE = "final_price";

        public static final String COLUMN_ORDER_STATUS_VALUE = "order_status_value";
        public static final String COLUMN_ORDER_STATUS_DISPLAY = "order_status_display";
        public static final String COLUMN_PAYMENT_STATUS_VALUE = "payment_status_value";
        public static final String COLUMN_PAYMENT_STATUS_DISPLAY = "payment_status_display";

        public static final String CREATED_AT = "created_at";

        public static final String COLUMN_REMARKS = "remarks";
    }

    public static final class SubordersTable implements BaseColumns {
        public static final String TABLE_NAME = "suborders";
        public static final String COLUMN_ORDER_ID = "orderID";
        public static final String COLUMN_SUBORDER_ID = "suborderID";
        public static final String COLUMN_DISPLAY_NUMBER = "display_number";
        public static final String COLUMN_SELLER_ID = "sellerID";
        public static final String COLUMN_SELLER_ADDRESS_ID = "selleraddressID";

        public static final String COLUMN_PRODUCT_COUNT = "product_count";
        public static final String COLUMN_PIECES = "pieces";
        public static final String COLUMN_RETAIL_PRICE = "retail_price";
        public static final String COLUMN_CALCULATED_PRICE = "calculated_price";
        public static final String COLUMN_EDITED_PRICE = "edited_price";
        public static final String COLUMN_SHIPPING_CHARGE = "shipping_charge";
        public static final String COLUMN_COD_CHARGE = "cod_charge";
        public static final String COLUMN_FINAL_PRICE = "final_price";

        public static final String COLUMN_SUBORDER_STATUS_VALUE = "suborder_status_value";
        public static final String COLUMN_SUBORDER_STATUS_DISPLAY = "suborder_status_display";
        public static final String COLUMN_PAYMENT_STATUS_VALUE = "payment_status_value";
        public static final String COLUMN_PAYMENT_STATUS_DISPLAY = "payment_status_display";

        public static final String CREATED_AT = "created_at";
    }

    public static final class OrderItemsTable implements BaseColumns {
        public static final String TABLE_NAME = "order_items";
        public static final String COLUMN_ORDER_ITEM_ID = "orderitemID";
        public static final String COLUMN_SUBORDER_ID = "suborderID";
        public static final String COLUMN_PRODUCT_ID = "productID";
        public static final String COLUMN_ORDER_SHIPMENT_ID = "ordershipmentID";
        public static final String COLUMN_DISPLAY_NUMBER = "display_number";
        public static final String COLUMN_SELLER_ID = "sellerID";
        public static final String COLUMN_SELLER_ADDRESS_ID = "selleraddressID";

        public static final String COLUMN_LOTS = "lots";
        public static final String COLUMN_LOT_SIZE = "lot_size";
        public static final String COLUMN_PIECES = "pieces";
        public static final String COLUMN_RETAIL_PRICE_PER_PIECE = "retail_price_per_piece";
        public static final String COLUMN_CALCULATED_PRICE_PER_PIECE = "calculated_price_per_piece";
        public static final String COLUMN_EDITED_PRICE_PER_PIECE = "edited_price_per_piece";
        public static final String COLUMN_FINAL_PRICE = "final_price";

        public static final String COLUMN_ORDER_ITEM_STATUS_VALUE = "order_item_status_value";
        public static final String COLUMN_ORDER_ITEM_STATUS_DISPLAY = "order_item_status_display";
        public static final String COLUMN_TRACKING_URL = "tracking_url";

        public static final String CREATED_AT = "created_at";

        public static final String COLUMN_REMARKS = "remarks";
    }
}
