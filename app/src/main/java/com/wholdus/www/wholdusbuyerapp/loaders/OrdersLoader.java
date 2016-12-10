package com.wholdus.www.wholdusbuyerapp.loaders;

import android.content.Context;

import com.wholdus.www.wholdusbuyerapp.databaseHelpers.OrderDBHelper;
import com.wholdus.www.wholdusbuyerapp.models.Order;

import java.util.ArrayList;

/**
 * Created by kaustubh on 7/12/16.
 */

public class OrdersLoader extends AbstractLoader<ArrayList<Order>>{

    public OrdersLoader(Context context) {
        super(context);
    }

    @Override
    public ArrayList<Order> loadInBackground() {

        // fetch data from DB
        OrderDBHelper orderDBHelper = new OrderDBHelper(getContext());
        return Order.getOrdersFromCursor(orderDBHelper.getOrdersData(null, null, null));
    }
}
