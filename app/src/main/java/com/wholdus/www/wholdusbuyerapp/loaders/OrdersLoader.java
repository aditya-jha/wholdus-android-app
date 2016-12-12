package com.wholdus.www.wholdusbuyerapp.loaders;

import android.content.Context;
import android.database.Cursor;

import com.wholdus.www.wholdusbuyerapp.databaseHelpers.CatalogDBHelper;
import com.wholdus.www.wholdusbuyerapp.databaseHelpers.OrderDBHelper;
import com.wholdus.www.wholdusbuyerapp.models.Order;
import com.wholdus.www.wholdusbuyerapp.models.Product;
import com.wholdus.www.wholdusbuyerapp.models.Seller;
import com.wholdus.www.wholdusbuyerapp.models.Suborder;

import java.util.ArrayList;

/**
 * Created by kaustubh on 7/12/16.
 */

public class OrdersLoader extends AbstractLoader<ArrayList<Order>>{

    boolean mInitialiseSuborders;
    boolean mInitialiseOrderItems;
    boolean mInitialiseSeller;

    public OrdersLoader(Context context,
                        boolean initialiseSuborders,
                        boolean initialiseOrderItems,
                        boolean initialiseSeller) {
        super(context);
        mInitialiseSuborders = initialiseSuborders;
        mInitialiseOrderItems = initialiseOrderItems;
        mInitialiseSeller = initialiseSeller;
    }

    @Override
    public ArrayList<Order> loadInBackground() {

        // fetch data from DB
        OrderDBHelper orderDBHelper = new OrderDBHelper(getContext());
        CatalogDBHelper catalogDBHelper = new CatalogDBHelper(getContext());
        ArrayList<Order> orders = Order.getOrdersFromCursor(orderDBHelper.getOrdersData(null, null, null));
        if (mInitialiseSuborders) {
            for (Order order : orders) {
                order.setSuborders(orderDBHelper.getSubordersData(null, null, order.getOrderID(), null));
                if (mInitialiseSeller){
                    for (Suborder suborder:order.getSuborders()){
                        Cursor cursor = catalogDBHelper.getSellerData(suborder.getSellerID(),null);
                        if (cursor.getCount() >0){
                            cursor.moveToNext();
                            suborder.setSeller(new Seller(cursor));
                        }
                    }
                }
            }
        }
        return orders;
    }
}
