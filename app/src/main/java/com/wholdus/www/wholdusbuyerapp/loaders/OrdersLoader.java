package com.wholdus.www.wholdusbuyerapp.loaders;

import android.content.Context;
import android.database.Cursor;

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
        UserDBHelper userDBHelper = new UserDBHelper(getContext());
        ArrayList<Order> orders = new ArrayList<>();

        Cursor cursor = userDBHelper.getOrdersData(null, null);

        if (cursor.getCount() > 0){
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToNext();
                Order order = new Order();
                order.setDataFromCursor(cursor);
                orders.add(order);
            }
        }

        return orders;
    }
}
