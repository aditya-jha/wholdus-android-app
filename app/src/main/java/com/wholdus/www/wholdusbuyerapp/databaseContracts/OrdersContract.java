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
        public static final String COLUMN_BUYER_ADDRESS_ID = "buyeraddressId";

        public static final String COLUMN_PRODUCT_COUNT = "product_count";
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
}
