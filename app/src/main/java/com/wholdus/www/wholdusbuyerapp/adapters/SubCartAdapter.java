package com.wholdus.www.wholdusbuyerapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.helperClasses.HelperFunctions;
import com.wholdus.www.wholdusbuyerapp.interfaces.CartSummaryListenerInterface;
import com.wholdus.www.wholdusbuyerapp.interfaces.ItemClickListener;
import com.wholdus.www.wholdusbuyerapp.models.BuyerAddress;
import com.wholdus.www.wholdusbuyerapp.models.SubCart;

import java.util.ArrayList;

/**
 * Created by kaustubh on 30/12/16.
 */

public class SubCartAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<SubCart> mData;
    private CartSummaryListenerInterface mListener;

    public SubCartAdapter(Context context, ArrayList<SubCart> data, CartSummaryListenerInterface listener) {
        mContext = context;
        mData = data;
        mListener = listener;
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
    public View getView(final int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;

        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.list_view_subcart, viewGroup, false);
            holder = new ViewHolder();
            holder.sellerName = (TextView) view.findViewById(R.id.subcart_seller_name_text_view);
            holder.summary = (TextView) view.findViewById(R.id.subcart_summary_text_view);
            holder.cartItems = (ListView) view.findViewById(R.id.sub_cart_items_list_view);
            view.setTag(holder);
        }
        else {
            holder = (ViewHolder) view.getTag();
        }

        SubCart subCart = mData.get(i);

        holder.sellerName.setText(subCart.getSeller().getCompanyName());
        holder.summary.setText( String.valueOf(subCart.getPieces()) + " pieces - Rs. " +
                String.format("%.0f",subCart.getFinalPrice()));

        CartItemsAdapter cartItemsAdapter = new CartItemsAdapter(mContext, subCart.getCartItems(), mListener);
        holder.cartItems.setAdapter(cartItemsAdapter);
        HelperFunctions.setListViewHeightBasedOnChildren(holder.cartItems);

        return view;
    }

    private class ViewHolder{
        int id;
        TextView sellerName;
        TextView summary;
        ListView cartItems;
    }
}
