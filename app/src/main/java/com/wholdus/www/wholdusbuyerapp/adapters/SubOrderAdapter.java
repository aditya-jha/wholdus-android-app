package com.wholdus.www.wholdusbuyerapp.adapters;

import android.content.Context;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.decorators.RecyclerViewSpaceItemDecoration;
import com.wholdus.www.wholdusbuyerapp.models.Suborder;

import java.util.ArrayList;

/**
 * Created by kaustubh on 31/12/16.
 */

public class SubOrderAdapter extends RecyclerView.Adapter<SubOrderAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<Suborder> mData;

    public SubOrderAdapter(Context context, ArrayList<Suborder> data) {
        mContext = context;
        mData = data;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.list_view_subcart, parent, false));
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Suborder subOrder = mData.get(position);

        holder.sellerName.setText(subOrder.getSeller().getCompanyName());
        holder.summary.setText(String.format(mContext.getString(R.string.pieces_price_format),
                String.valueOf((int) Math.ceil(subOrder.getPieces())),
                String.valueOf((int) Math.ceil(subOrder.getFinalPrice()))));

        OrderItemsAdapter orderItemsAdapter = new OrderItemsAdapter(mContext, subOrder.getOrderItems());
        holder.orderItems.setAdapter(orderItemsAdapter);
    }


    class MyViewHolder extends RecyclerView.ViewHolder{
        int id;
        TextView sellerName;
        TextView summary;
        RecyclerView orderItems;

        private MyViewHolder(final View itemView) {
            super(itemView);
            sellerName = (TextView) itemView.findViewById(R.id.subcart_seller_name_text_view);
            summary = (TextView) itemView.findViewById(R.id.subcart_summary_text_view);
            orderItems = (RecyclerView) itemView.findViewById(R.id.sub_cart_items_list_view);
            orderItems = (RecyclerView) itemView.findViewById(R.id.sub_cart_items_list_view);
            orderItems.setItemAnimator(new DefaultItemAnimator());
            orderItems.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
            orderItems.addItemDecoration(new RecyclerViewSpaceItemDecoration(mContext.getResources().getDimensionPixelSize(R.dimen.text_divider_gap_small), 0));
        }
    }
}
