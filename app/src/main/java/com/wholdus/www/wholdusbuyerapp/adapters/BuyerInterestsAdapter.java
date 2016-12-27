package com.wholdus.www.wholdusbuyerapp.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.models.BuyerInterest;

import java.util.ArrayList;

/**
 * Created by aditya on 27/12/16.
 */

public class BuyerInterestsAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<BuyerInterest> mData;

    public BuyerInterestsAdapter(Context context, ArrayList<BuyerInterest> interests) {
        mContext = context;
        mData = interests;
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
        return mData.get(i).getBuyerInterestID();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.list_item_buyer_interest, viewGroup, false);
        }
        TextView categoryName = (TextView) view.findViewById(R.id.category_name);
        TextView priceRange = (TextView) view.findViewById(R.id.price_range);
        TextView fabric = (TextView) view.findViewById(R.id.fabric);

        BuyerInterest interest = mData.get(i);

        categoryName.setText(interest.getCategoryName());
        String priceText = String.valueOf((int)Math.ceil(interest.getMinPricePerUnit())) + "-" + String.valueOf((int)Math.ceil(interest.getMinPricePerUnit()));
        priceRange.setText(priceText);

        if (TextUtils.isEmpty(interest.getFabricFilter())) {
            fabric.setText(mContext.getString(R.string.no_fabric_filter_text));
        } else {
            fabric.setText(interest.getFabricFilter());
        }

        return view;
    }
}
