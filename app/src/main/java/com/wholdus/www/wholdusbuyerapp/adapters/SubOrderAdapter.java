package com.wholdus.www.wholdusbuyerapp.adapters;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.helperClasses.HelperFunctions;
import com.wholdus.www.wholdusbuyerapp.models.Suborder;

import java.util.ArrayList;

/**
 * Created by kaustubh on 31/12/16.
 */

public class SubOrderAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<Suborder> mData;

    public SubOrderAdapter(Context context, ArrayList<Suborder> data) {
        mContext = context;
        mData = data;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int i) {
        return mData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;

        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.list_view_subcart, viewGroup, false);
            holder = new ViewHolder();
            holder.sellerName = (TextView) view.findViewById(R.id.subcart_seller_name_text_view);
            holder.summary = (TextView) view.findViewById(R.id.subcart_summary_text_view);
            holder.orderItems = (ListView) view.findViewById(R.id.sub_cart_items_list_view);
            holder.linearLayout = (LinearLayout) view.findViewById(R.id.linear_layout);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        Suborder subOrder = mData.get(i);

        holder.sellerName.setText(subOrder.getSeller().getCompanyName());
        holder.summary.setText(String.format(mContext.getString(R.string.pieces_price_format),
                String.valueOf((int) Math.ceil(subOrder.getPieces())),
                String.valueOf((int) Math.ceil(subOrder.getFinalPrice()))));

        OrderItemsAdapter orderItemsAdapter = new OrderItemsAdapter(mContext, subOrder.getOrderItems());
        holder.orderItems.setAdapter(orderItemsAdapter);
        //HelperFunctions.setListViewHeightBasedOnChildren(holder.orderItems);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 210 * subOrder.getProductCount(), mContext.getResources().getDisplayMetrics()));
        //HelperFunctions.setListViewHeightBasedOnChildren(holder.cartItems);
        holder.linearLayout.setLayoutParams(layoutParams);

        return view;
    }

    private class ViewHolder {
        int id;
        TextView sellerName;
        TextView summary;
        ListView orderItems;
        LinearLayout linearLayout;
    }
}
