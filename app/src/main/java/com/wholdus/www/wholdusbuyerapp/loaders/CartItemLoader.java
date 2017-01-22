package com.wholdus.www.wholdusbuyerapp.loaders;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.Nullable;

import com.wholdus.www.wholdusbuyerapp.databaseHelpers.CartDBHelper;
import com.wholdus.www.wholdusbuyerapp.databaseHelpers.CatalogDBHelper;
import com.wholdus.www.wholdusbuyerapp.models.CartItem;
import com.wholdus.www.wholdusbuyerapp.models.Product;

import java.util.ArrayList;

/**
 * Created by kaustubh on 23/12/16.
 */

public class CartItemLoader extends AbstractLoader<ArrayList<CartItem>> {

    private Context mContext;
    private int mCartItemID;
    private ArrayList<Integer> mExcludeCartItemIDs;
    private int mSubCartID;
    private int mProductID;
    private int mSynced;
    private String[] mColumns;
    private boolean mInitialiseProduct;

    public CartItemLoader(Context context,
                          int cartItemID,
                          @Nullable ArrayList<Integer> excludeCartItemIDs,
                          int subCartID,
                          int productID,
                          int synced,
                          boolean initialiseProduct,
                          @Nullable String[] columns) {
        super(context);
        mContext = context;
        mCartItemID = cartItemID;
        mExcludeCartItemIDs = excludeCartItemIDs;
        mSubCartID = subCartID;
        mProductID = productID;
        mSynced = synced;
        mInitialiseProduct = initialiseProduct;
        mColumns = columns;
    }

    @Override
    public ArrayList<CartItem> loadInBackground() {
        CartDBHelper cartDBHelper = new CartDBHelper(mContext);
        CatalogDBHelper catalogDBHelper = new CatalogDBHelper(mContext);
        ArrayList<CartItem> cartItems = CartItem.getCartItemsFromCursor(cartDBHelper.getCartItemsData(mCartItemID, mExcludeCartItemIDs, mSubCartID, mProductID, mSynced, mColumns));
        if (mInitialiseProduct) {
            for (CartItem cartItem : cartItems) {
                ArrayList<Integer> productIDs = new ArrayList<>();
                productIDs.add(cartItem.getProductID());
                Cursor productCursor = catalogDBHelper.getProductData(productIDs, null, null, null, null, null, null, null, -1, -1, null, null, null, null, -1, -1, -1, -1, null, -1, -1, catalogDBHelper.BasicProductColumns);
                if (productCursor.getCount() > 0) {
                    productCursor.moveToNext();
                    cartItem.setProduct(new Product(productCursor));
                } else {
                    return null;
                }
            }
        }
        return cartItems;
    }
}
