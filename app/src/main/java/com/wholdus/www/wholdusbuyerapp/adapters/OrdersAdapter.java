package com.wholdus.www.wholdusbuyerapp.adapters;

import android.content.Context;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.decorators.RecyclerViewSpaceItemDecoration;
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

    public OrdersAdapter(Context context, ArrayList<Order> listData, final ItemClickListener listener) {
        mContext = context;
        mListData = listData;
        mListener = listener;
    }

    @Override
    public int getItemCount() {
        return mListData.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.list_item_layout_orders, parent, false));
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        holder.mListener = mListener;
        Order order = mListData.get(position);
        holder.orderID.setText(String.format(mContext.getString(R.string.order_number_format),
                order.getDisplayNumber()));
        holder.orderStatus.setText(order.getOrderStatusDisplay());
        holder.orderDate.setText(HelperFunctions.getDateFromString(order.getCreatedAt()));
        holder.orderAmount.setText(String.format(mContext.getString(R.string.price_format),
                String.valueOf((int) Math.ceil(order.getFinalPrice()))));

        ArrayList<Suborder> suborders = order.getSuborders();
        ItemClickListener itemClickListener = new ItemClickListener() {
            @Override
            public void itemClicked(View view, int position, int id) {
                holder.itemView.callOnClick();
            }
        };
        SuborderListViewAdapter suborderAdapter = new SuborderListViewAdapter(mContext, suborders, itemClickListener);
        holder.suborderListView.setAdapter(suborderAdapter);
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView orderID;
        private TextView orderStatus;
        private TextView orderDate;
        private TextView orderAmount;
        private RecyclerView suborderListView;
        private ItemClickListener mListener;

        private MyViewHolder(final View itemView) {
            super(itemView);
            orderID = (TextView) itemView.findViewById(R.id.order_id_text_view);
            orderStatus = (TextView) itemView.findViewById(R.id.order_status_text_view);
            orderDate = (TextView) itemView.findViewById(R.id.order_date_text_view);
            orderAmount = (TextView) itemView.findViewById(R.id.order_amount_text_view);
            suborderListView = (RecyclerView) itemView.findViewById(R.id.suborder_list_view);
            itemView.setOnClickListener(this);
            suborderListView.setItemAnimator(new DefaultItemAnimator());
            suborderListView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
            suborderListView.addItemDecoration(new RecyclerViewSpaceItemDecoration(mContext.getResources().getDimensionPixelSize(R.dimen.text_divider_gap_small), 0));

        }



        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (position == RecyclerView.NO_POSITION) return;

            if (mListener != null) {
                mListener.itemClicked(view, position, -1);
            }
        }
    }
}
