package com.wholdus.www.wholdusbuyerapp.loaders;

import android.content.Context;
import android.database.Cursor;

import com.wholdus.www.wholdusbuyerapp.databaseHelpers.CartDBHelper;
import com.wholdus.www.wholdusbuyerapp.databaseHelpers.CatalogDBHelper;
import com.wholdus.www.wholdusbuyerapp.models.Cart;
import com.wholdus.www.wholdusbuyerapp.models.CartItem;
import com.wholdus.www.wholdusbuyerapp.models.Product;
import com.wholdus.www.wholdusbuyerapp.models.Seller;
import com.wholdus.www.wholdusbuyerapp.models.SubCart;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kaustubh on 26/12/16.
 */

public class CartLoader extends AbstractLoader<Cart> {

    private int mCartID;
    private boolean mInitialiseSubCarts;
    private boolean mInitialiseCartItems;
    private boolean mInitialiseSeller;
    private boolean mInitialiseProduct;

    public CartLoader(Context context,
                      int cartID,
                      boolean initialiseSubCarts,
                      boolean initialiseCartItems,
                      boolean initialiseProduct,
                      boolean initialiseSeller) {
        super(context);
        mCartID = cartID;
        mInitialiseSubCarts = initialiseSubCarts;
        mInitialiseCartItems = initialiseCartItems;
        mInitialiseProduct = initialiseProduct;
        mInitialiseSeller = initialiseSeller;
    }

    @Override
    public Cart loadInBackground() {

        // fetch data from DB
        CartDBHelper cartDBHelper = new CartDBHelper(getContext());
        CatalogDBHelper catalogDBHelper = new CatalogDBHelper(getContext());
        Cursor cursor = cartDBHelper.getCartData(-1, -1, null);
        if (cursor.getCount() == 0) {
            return null;
        }
        cursor.moveToNext();
        Cart cart = new Cart(cursor);
        if (mInitialiseSubCarts) {
            cart.setSubCarts(cartDBHelper.getSubCartData(cart.getCartID(), -1, -1, -1,null));
            if (mInitialiseSeller) {
                for (SubCart subCart : cart.getSubCarts()) {
                    Cursor sellerCursor = catalogDBHelper.getSellerData(subCart.getSellerID(), null);
                    if (sellerCursor.getCount() > 0) {
                        sellerCursor.moveToNext();
                        subCart.setSeller(new Seller(sellerCursor));
                    }
                }
            }
            if (mInitialiseCartItems) {
                for (SubCart subCart : cart.getSubCarts()) {
                    subCart.setCartItems(cartDBHelper.getCartItemsData(-1, null, subCart.getSubCartID(),-1,-1,null));

                    if (mInitialiseProduct) {
                        for (CartItem cartItem : subCart.getCartItems()) {
                            ArrayList<Integer> productIDs = new ArrayList<>();
                            productIDs.add(cartItem.getProductID());
                            Cursor productCursor = catalogDBHelper.getProductData(productIDs, null, null, null, null, null, null, null, -1, -1, null, null, null, null, -1, -1, -1, -1, null, -1, -1, catalogDBHelper.BasicProductColumns);
                            if (productCursor.getCount() > 0) {
                                productCursor.moveToNext();
                                cartItem.setProduct(new Product(productCursor));
                            }
                        }
                    }
                }
            }
        }
        return cart;
    }
}
