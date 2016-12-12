package com.wholdus.www.wholdusbuyerapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.vision.text.Text;
import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.helperClasses.HelperFunctions;
import com.wholdus.www.wholdusbuyerapp.models.Order;
import com.wholdus.www.wholdusbuyerapp.models.Suborder;

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
        holder.orderDate.setText(HelperFunctions.getDateFromString(order.getCreatedAt()));
        holder.orderAmount.setText(String.format("%.00f", order.getFinalPrice()));

        ArrayList<Suborder> suborders = order.getSuborders();
        SuborderListViewAdapter suborderAdapter = new SuborderListViewAdapter(mContext, suborders);
        holder.suborderListView.setAdapter(suborderAdapter);
        HelperFunctions.setListViewHeightBasedOnChildren(holder.suborderListView);
        /*
        final int adapterCount = suborderAdapter.getCount();
        LinearLayout layout = new LinearLayout(mContext);
        for (int i = 0; i < adapterCount; i++) {
            View item = suborderAdapter.getView(i, null, null);
            layout.addView(item);
        }*/

        //holder.suborderListView.addView(layout);

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_layout_orders, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView orderID;
        public TextView orderDate;
        public TextView orderAmount;
        public ListView suborderListView;
        public MyViewHolder(View itemView) {
            super(itemView);
            orderID = (TextView) itemView.findViewById(R.id.order_id_text_view);
            orderDate = (TextView) itemView.findViewById(R.id.order_date_text_view);
            orderAmount = (TextView) itemView.findViewById(R.id.order_amount_text_view);
            suborderListView = (ListView) itemView.findViewById(R.id.suborder_list_view);
        }
    }

    private class SuborderListViewAdapter extends BaseAdapter{

        private ArrayList<Suborder> mListData;
        private Context mContext;
        private LayoutInflater layoutInflater;

        public SuborderListViewAdapter(Context context, ArrayList<Suborder> listData){
            mContext = context;
            mListData = listData;
            layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return mListData.size();
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            ViewHolder holder;

            if(convertView == null){
                convertView = layoutInflater.inflate(R.layout.list_item_layout_orders_suborders, viewGroup, false);
                holder = new ViewHolder();
                holder.sellerName = (TextView) convertView.findViewById(R.id.suborder_seller_name_text_view);
                holder.pieces = (TextView) convertView.findViewById(R.id.suborder_pieces_text_view);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            final Suborder suborder = this.mListData.get(i);

            holder.sellerName.setText(suborder.getSeller().getCompanyName());
            holder.pieces.setText(String.valueOf(suborder.getPieces()));
            return convertView;
        }

        @Override
        public Object getItem(int i) {
            return mListData.get(i);
        }

        class ViewHolder{
            int id;
            TextView sellerName;
            TextView pieces;
        }
    }

}
