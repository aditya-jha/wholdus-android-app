package com.wholdus.www.wholdusbuyerapp.databaseContracts;

import android.provider.BaseColumns;

/**
 * Created by aditya on 9/12/16.
 */

public class BuyerProductsContract {

    private BuyerProductsContract() {}

    public static final class BuyerProductTable implements BaseColumns {
        public static final String TABLE_NAME = "buyer_products";
        public static final String COLUMN_BUYER_PRODUCT_ID = "buyerproductID";
        public static final String COLUMN_BUYER_PRODUCT_RESPONSE_ID = "buyerproductresponseID";
        public static final String COLUMN_PRODUCT_ID = "productID";
        public static final String COLUMN_STORE_DISCOUNT = "store_discount";
        public static final String COLUMN_HAS_SWIPED = "has_swiped";
        public static final String COLUMN_RESPONSE_CODE = "response_code";
        public static final String COLUMN_SYNCED = "synced";
    }
}
