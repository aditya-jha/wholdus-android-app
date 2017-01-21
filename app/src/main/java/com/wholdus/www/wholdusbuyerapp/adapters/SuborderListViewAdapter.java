package com.wholdus.www.wholdusbuyerapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.interfaces.ItemClickListener;
import com.wholdus.www.wholdusbuyerapp.models.Suborder;

import java.util.ArrayList;

/**
 * Created by kaustubh on 13/12/16.
 */

public class SuborderListViewAdapter extends BaseAdapter {

    private ArrayList<Suborder> mListData;
    private Context mContext;

    public SuborderListViewAdapter(Context context, ArrayList<Suborder> listData) {
        mContext = context;
        mListData = listData;
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
    public Object getItem(int i) {
        return mListData.get(i);
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_layout_orders_suborders, viewGroup, false);
            holder = new ViewHolder();
            holder.sellerName = (TextView) convertView.findViewById(R.id.suborder_seller_name_text_view);
            holder.pieces = (TextView) convertView.findViewById(R.id.suborder_pieces_text_view);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final Suborder suborder = mListData.get(i);

        holder.sellerName.setText(suborder.getSeller().getCompanyName());
        holder.pieces.setText(String.valueOf(suborder.getPieces()));

        return convertView;
    }

    class ViewHolder {
        int id;
        TextView sellerName;
        TextView pieces;
    }
}
