package com.wholdus.www.wholdusbuyerapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.interfaces.ItemClickListener;
import com.wholdus.www.wholdusbuyerapp.models.Suborder;

import java.util.ArrayList;

/**
 * Created by kaustubh on 13/12/16.
 */

public class SuborderListViewAdapter extends RecyclerView.Adapter<SuborderListViewAdapter.MyViewHolder> {

    private ArrayList<Suborder> mListData;
    private Context mContext;
    private ItemClickListener mListener;

    public SuborderListViewAdapter(Context context, ArrayList<Suborder> listData, final ItemClickListener listener) {
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
        return new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.list_item_layout_orders_suborders, parent, false));
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Suborder suborder = mListData.get(position);

        holder.sellerName.setText(suborder.getSeller().getCompanyName());
        holder.pieces.setText(String.valueOf(suborder.getPieces()));

        holder.mListener = mListener;
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        int id;
        TextView sellerName;
        TextView pieces;
        private ItemClickListener mListener;

        private MyViewHolder(final View itemView) {
            super(itemView);
            sellerName = (TextView) itemView.findViewById(R.id.suborder_seller_name_text_view);
            pieces = (TextView) itemView.findViewById(R.id.suborder_pieces_text_view);
            itemView.setOnClickListener(this);
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
