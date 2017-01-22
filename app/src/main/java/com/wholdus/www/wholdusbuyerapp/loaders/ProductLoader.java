package com.wholdus.www.wholdusbuyerapp.loaders;

import android.content.Context;
import android.database.Cursor;

import com.wholdus.www.wholdusbuyerapp.databaseHelpers.CatalogDBHelper;
import com.wholdus.www.wholdusbuyerapp.models.Product;
import com.wholdus.www.wholdusbuyerapp.models.SellerAddress;

import java.util.ArrayList;

/**
 * Created by aditya on 18/12/16.
 */

public class ProductLoader extends AbstractLoader<Product> {

    private Context mContext;
    private int mProductID;

    public ProductLoader(Context context, int productID) {
        super(context);
        mContext = context;
        mProductID = productID;
    }

    @Override
    public Product loadInBackground() {
        ArrayList<Integer> productIDs = new ArrayList<>();
        productIDs.add(mProductID);
        CatalogDBHelper catalogDBHelper = new CatalogDBHelper(mContext);
        Cursor cursor = catalogDBHelper.getProductData(
                productIDs,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                -1,
                -1,
                null,
                null,
                null,
                null,
                0,
                1,
                -1,
                -1,
                null,
                -1,
                -1,
                null
        );

        if (cursor.getCount() == 0) {
            return null;
        }

        cursor.moveToFirst();

        Product product = new Product(cursor);
        product.setProductDetails(cursor);

        cursor = catalogDBHelper.getSellerData(product.getSellerID(), null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            product.setSeller(cursor);

            cursor = catalogDBHelper.getSellerAddressData(-1, 0, product.getSellerID(), null);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                product.getSeller().setSellerAddress(new SellerAddress(cursor));
            }
        }

        cursor = catalogDBHelper.getCategoryData(product.getCategoryID(), -1, null, 1, -1, -1, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            product.setCategory(cursor);
        }


        return product;
    }
}
