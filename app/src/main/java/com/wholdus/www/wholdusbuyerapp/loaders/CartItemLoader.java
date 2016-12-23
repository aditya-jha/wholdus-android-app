package com.wholdus.www.wholdusbuyerapp.loaders;

import android.content.Context;
import android.support.annotation.Nullable;

import com.wholdus.www.wholdusbuyerapp.databaseHelpers.CartDBHelper;
import com.wholdus.www.wholdusbuyerapp.models.CartItem;

import java.util.ArrayList;

/**
 * Created by kaustubh on 23/12/16.
 */

public class CartItemLoader extends AbstractLoader<ArrayList<CartItem>> {

    private int mCartItemID;
    private ArrayList<Integer> mExcludeCartItemIDs;
    private int mSubCartID;
    private int mProductID;
    private int mSynced;
    private String[] mColumns;

    public CartItemLoader(Context context,
                          int cartItemID,
                          @Nullable ArrayList<Integer> excludeCartItemIDs,
                          int subCartID,
                          int productID,
                          int synced,
                          @Nullable String[] columns){
        super(context);
        mCartItemID = cartItemID;
        mExcludeCartItemIDs = excludeCartItemIDs;
        mSubCartID = subCartID;
        mProductID = productID;
        mSynced = synced;
        mColumns = columns;
    }

    @Override
    public ArrayList<CartItem> loadInBackground() {
        CartDBHelper cartDBHelper = new CartDBHelper(getContext());
        return CartItem.getCartItemsFromCursor(cartDBHelper.getCartItemsData(mCartItemID, mExcludeCartItemIDs, mSubCartID, mProductID, mSynced, mColumns));
    }
}
