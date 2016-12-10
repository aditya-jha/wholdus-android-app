package com.wholdus.www.wholdusbuyerapp.loaders;

import android.content.Context;
import android.database.Cursor;

import com.wholdus.www.wholdusbuyerapp.databaseHelpers.OrderDBHelper;
import com.wholdus.www.wholdusbuyerapp.databaseHelpers.UserDBHelper;
import com.wholdus.www.wholdusbuyerapp.helperClasses.GlobalAccessHelper;
import com.wholdus.www.wholdusbuyerapp.models.Buyer;
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
        Cursor cursor = orderDBHelper.getOrdersData(null, null, null);
        ArrayList<Order> orders = Order.getOrdersFromCursor(cursor);

        return orders;
    }
}
