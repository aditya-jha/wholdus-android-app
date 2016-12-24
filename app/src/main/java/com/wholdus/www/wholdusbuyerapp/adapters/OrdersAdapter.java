package com.wholdus.www.wholdusbuyerapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.helperClasses.HelperFunctions;
import com.wholdus.www.wholdusbuyerapp.interfaces.ItemClickListener;
import com.wholdus.www.wholdusbuyerapp.models.Order;
import com.wholdus.www.wholdusbuyerapp.models.Suborder;

import java.util.ArrayList;

/**
 * Created by kaustubh on 8/12/16.
 */

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.MyViewHolder> {

    private ArrayList<Order> mListData;
    private Context mContext;
    private ItemClickListener mListener;

    public OrdersAdapter(Context context, ArrayList<Order> listData,final ItemClickListener listener){
        mContext = context;
        mListData = listData;
        mListener = listener;
    }

    @Override
    public int getItemCount() {
        return mListData.size();
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Order order = mListData.get(position);
        holder.orderID.setText(order.getDisplayNumber());
        holder.orderStatus.setText(order.getOrderStatusDisplay());
        holder.orderDate.setText(HelperFunctions.getDateFromString(order.getCreatedAt()));
        holder.orderAmount.setText(String.format("%.00f", order.getFinalPrice()));

        ArrayList<Suborder> suborders = order.getSuborders();
        SuborderListViewAdapter suborderAdapter = new SuborderListViewAdapter(mContext, suborders);
        holder.suborderListView.setAdapter(suborderAdapter);
        HelperFunctions.setListViewHeightBasedOnChildren(holder.suborderListView);
        //TODO:Handle on click behavior for the list view
        /*
        final int adapterCount = suborderAdapter.getCount();
        LinearLayout layout = new LinearLayout(mContext);
        for (int i = 0; i < adapterCount; i++) {
            View item = suborderAdapter.getView(i, null, null);
            layout.addView(item);
        }*/

        //holder.suborderListView.addView(layout);
        holder.mListener = mListener;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_layout_orders, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView orderID;
        private TextView orderStatus;
        private TextView orderDate;
        private TextView orderAmount;
        private ListView suborderListView;
        private ItemClickListener mListener;

        private MyViewHolder(View itemView) {
            super(itemView);
            orderID = (TextView) itemView.findViewById(R.id.order_id_text_view);
            orderStatus = (TextView) itemView.findViewById(R.id.order_status_text_view);
            orderDate = (TextView) itemView.findViewById(R.id.order_date_text_view);
            orderAmount = (TextView) itemView.findViewById(R.id.order_amount_text_view);
            suborderListView = (ListView) itemView.findViewById(R.id.suborder_list_view);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            int position = getAdapterPosition();
            if (position == RecyclerView.NO_POSITION) return;

            if (mListener != null) {
                mListener.itemClicked(view, position);
            }

        }
    }
}
