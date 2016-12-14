package com.wholdus.www.wholdusbuyerapp.databaseContracts;

import android.provider.BaseColumns;

/**
 * Created by kaustubh on 7/12/16.
 */

public class ProductsContract {

    private ProductsContract() {
    }

    public static final class ProductsTable implements BaseColumns {
        public static final String TABLE_NAME = "products";
        public static final String COLUMN_PRODUCT_ID = "productID";
        public static final String COLUMN_CATEGORY_ID = "categoryID";
        public static final String COLUMN_SELLER_ID = "sellerID";
        public static final String COLUMN_PRODUCT_DETAILS_ID = "detailsID";

        public static final String COLUMN_PRICE_PER_UNIT = "price_per_unit";
        public static final String COLUMN_LOT_SIZE = "lot_size";
        public static final String COLUMN_PRICE_PER_LOT = "price_per_lot";
        public static final String COLUMN_MIN_PRICE_PER_UNIT = "min_price_per_unit";
        public static final String COLUMN_MARGIN = "margin";

        public static final String COLUMN_UNIT = "unit";
        public static final String COLUMN_DISPLAY_NAME = "display_name";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_URL = "url";
        public static final String COLUMN_SHOW_ONLINE = "show_online";
        public static final String COLUMN_DELETE_STATUS = "delete_status";

        public static final String COLUMN_IMAGE_NAME = "image_name";
        public static final String COLUMN_IMAGE_COUNT = "image_count";
        public static final String COLUMN_IMAGE_NUMBERS = "image_numbers";
        public static final String COLUMN_IMAGE_PATH = "image_path";

        public static final String COLUMN_WARRANTY = "warranty";
        public static final String COLUMN_SPECIAL_FEATURE = "special_feature";
        public static final String COLUMN_AVAILABILITY = "availability";
        public static final String COLUMN_STYLE = "style";
        public static final String COLUMN_MANUFACTURED_CITY = "manufactured_city";
        public static final String COLUMN_PATTERN = "pattern";
        public static final String COLUMN_COLOURS = "colours";
        public static final String COLUMN_LOT_DESCRIPTION = "lot_description";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String WORK_DECORATION_TYPE = "work_decoration_type";
        public static final String COLUMN_NECK_COLLAR_TYPE = "neck_collar_type";
        public static final String COLUMN_FABRIC_GSM = "fabric_gsm";
        public static final String COLUMN_DISPATCHED_IN = "dispatched_in";
        public static final String COLUMN_REMARKS = "remarks";
        public static final String COLUMN_SELLER_CATALOG_NUMBER = "seller_catalog_number";
        public static final String COLUMN_SLEEVE = "sleeve";
        public static final String COLUMN_SIZES = "sizes";
        public static final String COLUMN_GENDER = "gender";
        public static final String COLUMN_WEIGHT_PER_UNIT = "weight_per_unit";
        public static final String COLUMN_PACKAGING_DETAILS = "packaging_details";
        public static final String COLUMN_LENGTH = "length";

        public static final String COLUMN_CREATED_AT = "created_at";
        public static final String COLUMN_UPDATED_AT = "updated_at";
    }

    public static final class CategoriesTable implements BaseColumns {
        public static final String TABLE_NAME = "categories";
        public static final String COLUMN_CATEGORY_ID = "categoryID";

        public static final String COLUMN_CATEGORY_NAME = "display_name";
        public static final String COLUMN_URL = "url";
        public static final String COLUMN_SLUG = "slug";
        public static final String COLUMN_IMAGE_URL = "image_url";
        public static final String COLUMN_PRODUCTS_COUNT = "product_count";
        public static final String COLUMN_CREATED_AT = "created_at";
        public static final String COLUMN_UPDATED_AT = "updated_at";
    }

    public static final class SellersTable implements BaseColumns {
        public static final String TABLE_NAME = "sellers";
        public static final String COLUMN_SELLER_ID = "sellerID";

        public static final String COLUMN_COMPANY_NAME = "company_name";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_COMPANY_PROFILE = "company_profile";

        public static final String COLUMN_SHOW_ONLINE = "show_online";

        public static final String COLUMN_CREATED_AT = "created_at";
        public static final String COLUMN_UPDATED_AT = "updated_at";
    }

    public static final class SellerAddressTable implements BaseColumns {
        public static final String TABLE_NAME = "seller_address";
        public static final String COLUMN_ADDRESS_ID = "addressID";
        public static final String COLUMN_SELLER_ID = "sellerID";

        public static final String COLUMN_CITY = "city";
        public static final String COLUMN_STATE = "state";
        public static final String COLUMN_LANDMARK = "landmark";
        public static final String COLUMN_ADDRESS = "address";
        public static final String COLUMN_CONTACT_NUMBER = "contact_number";
        public static final String COLUMN_PINCODE = "pincode";

        public static final String COLUMN_CREATED_AT = "created_at";
        public static final String COLUMN_UPDATED_AT = "updated_at";
    }
}