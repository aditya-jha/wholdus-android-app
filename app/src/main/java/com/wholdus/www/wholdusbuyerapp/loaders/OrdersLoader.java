package com.wholdus.www.wholdusbuyerapp.loaders;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.Nullable;
import android.util.Log;

import com.wholdus.www.wholdusbuyerapp.databaseHelpers.CatalogDBHelper;
import com.wholdus.www.wholdusbuyerapp.databaseHelpers.OrderDBHelper;
import com.wholdus.www.wholdusbuyerapp.models.Order;
import com.wholdus.www.wholdusbuyerapp.models.OrderItem;
import com.wholdus.www.wholdusbuyerapp.models.Product;
import com.wholdus.www.wholdusbuyerapp.models.Seller;
import com.wholdus.www.wholdusbuyerapp.models.Suborder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kaustubh on 7/12/16.
 */

public class OrdersLoader extends AbstractLoader<ArrayList<Order>>{

    private int mOrderID;
    private List<Integer> mOrderStatusValues;
    private boolean mInitialiseSuborders;
    private boolean mInitialiseOrderItems;
    private boolean mInitialiseSeller;
    private boolean mInitialiseProduct;


    public OrdersLoader(Context context,
                        int orderID,
                        List<Integer> orderStatusValues,
                        boolean initialiseSuborders,
                        boolean initialiseOrderItems,
                        boolean initialiseProduct,
                        boolean initialiseSeller) {
        super(context);
        mOrderID = orderID;
        mOrderStatusValues = orderStatusValues;
        mInitialiseSuborders = initialiseSuborders;
        mInitialiseOrderItems = initialiseOrderItems;
        mInitialiseProduct = initialiseProduct;
        mInitialiseSeller = initialiseSeller;
    }

    @Override
    public ArrayList<Order> loadInBackground() {

        // fetch data from DB
        OrderDBHelper orderDBHelper = new OrderDBHelper(getContext());
        CatalogDBHelper catalogDBHelper = new CatalogDBHelper(getContext());
        ArrayList<Order> orders = Order.getOrdersFromCursor(orderDBHelper.getOrdersData(mOrderStatusValues, mOrderID != -1 ? mOrderID : null, null));
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
                if (mInitialiseOrderItems){
                    for (Suborder suborder:order.getSuborders()){
                        suborder.setOrderItems(orderDBHelper.getOrderItemsData(null, null, suborder.getSuborderID(), null));

                        if (mInitialiseProduct){
                            for (OrderItem orderItem:suborder.getOrderItems()){
                                //TODO: Initialise product
                            }
                        }
                    }
                }
            }
        }
        return orders;
    }
}
