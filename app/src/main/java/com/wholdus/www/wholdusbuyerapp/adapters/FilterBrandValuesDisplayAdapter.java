package com.wholdus.www.wholdusbuyerapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.helperClasses.FilterClass;
import com.wholdus.www.wholdusbuyerapp.models.CategorySeller;

import java.util.ArrayList;

/**
 * Created by aditya on 14/12/16.
 */

public class FilterBrandValuesDisplayAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<CategorySeller> mData;

    public FilterBrandValuesDisplayAdapter(Context context, ArrayList<CategorySeller> data) {
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
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.list_item_filter_values, viewGroup, false);
        }

        CategorySeller categorySeller = mData.get(i);

        CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox);
        if (FilterClass.isItemSelected(FilterClass.FILTER_BRAND_KEY, String.valueOf(categorySeller.getSellerID()))) {
            checkBox.setChecked(true);
        } else {
            checkBox.setChecked(false);
        }

        TextView textView = (TextView) view.findViewById(R.id.textView);
        textView.setText(categorySeller.getCompanyName());

        return view;
    }

    public void resetData(ArrayList<CategorySeller> data) {
        mData.clear();
        mData.addAll(data);
    }

    public void itemClicked(View view, int position) {
        CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox);
        checkBox.toggle();
        FilterClass.toggleFilterItem(FilterClass.FILTER_BRAND_KEY, String.valueOf(mData.get(position).getSellerID()));
    }
}
