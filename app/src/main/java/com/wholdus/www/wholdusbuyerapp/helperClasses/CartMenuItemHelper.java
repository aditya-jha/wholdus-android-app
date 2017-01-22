package com.wholdus.www.wholdusbuyerapp.helperClasses;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.activities.CartActivity;
import com.wholdus.www.wholdusbuyerapp.databaseContracts.CartContract;
import com.wholdus.www.wholdusbuyerapp.loaders.CartItemLoader;
import com.wholdus.www.wholdusbuyerapp.loaders.CartLoader;
import com.wholdus.www.wholdusbuyerapp.models.Cart;
import com.wholdus.www.wholdusbuyerapp.models.CartItem;

import java.util.ArrayList;

/**
 * Created by kaustubh on 22/1/17.
 */

public class CartMenuItemHelper {

    private MenuItem mCartMenuItem;
    private int mCartProducts = -1;
    private static final int CART_DB_LOADER = 1500;
    private RelativeLayout mCartItemCountLayout;
    private TextView mCartItemCountTextView;
    private Context mContext;
    private LoaderManager mLoaderManager;

    public CartMenuItemHelper(Context context, MenuItem menuItem, LoaderManager loaderManager){
        mContext = context;
        mCartMenuItem = menuItem;
        mLoaderManager = loaderManager;
        MenuItemCompat.setActionView(mCartMenuItem, R.layout.cart_icon_item_count);
        mCartItemCountLayout = (RelativeLayout) MenuItemCompat.getActionView(mCartMenuItem);
        mCartItemCountLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startCartActivity();
            }
        });
        mCartItemCountTextView = (TextView) mCartItemCountLayout.findViewById(R.id.actionbar_cart_item_count_text_view);
        setCartItemCount();
    }

    public void restartLoader(){
        if (mLoaderManager != null) {
            CartLoaderManager cartLoaderManager = new CartLoaderManager(mContext);
            mLoaderManager.restartLoader(CART_DB_LOADER, null, cartLoaderManager);
        }
    }

    private void startCartActivity(){
        mContext.startActivity(new Intent(mContext, CartActivity.class));
    }

    private class CartLoaderManager implements LoaderManager.LoaderCallbacks<ArrayList<CartItem>>{

        private Context mContext;

        CartLoaderManager(Context context){
            mContext = context;
        }
        @Override
        public void onLoaderReset(Loader<ArrayList<CartItem>> loader) {
        }

        @Override
        public void onLoadFinished(Loader<ArrayList<CartItem>> loader, ArrayList<CartItem> data) {
            if (data != null) {
                int productCount = 0;
                for (CartItem cartItem:data){
                    if (cartItem.getPieces()>0){
                        productCount += 1;
                    }
                }
                mCartProducts = productCount;
            } else {
                mCartProducts = 0;
            }
            setCartItemCount();
        }

        @Override
        public Loader<ArrayList<CartItem>> onCreateLoader(int id, Bundle args) {
            if (mContext != null) {
                return new CartItemLoader(mContext, -1,null,-1, -1, -1, false, null);
            }else {
                return null;
            }
        }
    }

    private void setCartItemCount(){
        if (mCartItemCountTextView != null && mContext != null) {
            if (mCartProducts > 0) {
                mCartItemCountTextView.setVisibility(View.VISIBLE);
                mCartItemCountTextView.setText(String.valueOf(mCartProducts));
            } else {
                mCartItemCountTextView.setVisibility(View.INVISIBLE);
            }
        }
    }
}
