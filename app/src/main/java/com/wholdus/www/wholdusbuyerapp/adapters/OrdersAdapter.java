package com.wholdus.www.wholdusbuyerapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.vision.text.Text;
import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.models.Order;

import java.util.ArrayList;

/**
 * Created by kaustubh on 8/12/16.
 */

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.MyViewHolder> {

    private ArrayList<Order> mListData;
    private Context mContext;

    public OrdersAdapter(Context context, ArrayList<Order> listData){
        mContext = context;
        mListData = listData;
    }

    @Override
    public int getItemCount() {
        return mListData.size();
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Order order = mListData.get(position);
        holder.orderID.setText(order.getDisplayNumber());
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_layout_orders, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView orderID;
        public MyViewHolder(View itemView) {
            super(itemView);
            orderID = (TextView) itemView.findViewById(R.id.order_id_textview);
        }
    }

}
