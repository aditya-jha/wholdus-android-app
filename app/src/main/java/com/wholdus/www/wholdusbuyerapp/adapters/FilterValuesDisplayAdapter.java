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

import java.util.ArrayList;

/**
 * Created by aditya on 14/12/16.
 */

public class FilterValuesDisplayAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<String> mData;
    private String mKeyValue;

    public FilterValuesDisplayAdapter(Context context, ArrayList<String> data, String keyValue) {
        mContext = context;
        mData = data;
        mKeyValue = keyValue;
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

        String item = (String) getItem(i);

        CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox);
        if (FilterClass.isItemSelected(mKeyValue, item)) {
            checkBox.setChecked(true);
        } else {
            checkBox.setChecked(false);
        }

        TextView textView = (TextView) view.findViewById(R.id.textView);
        textView.setText(item);

        return view;
    }

    public void resetData(ArrayList<String> data, String keyValue) {
        mData.clear();
        mData.addAll(data);
        mKeyValue = keyValue;
    }

    public void itemClicked(View view, int position) {

        CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox);
        checkBox.toggle();
        FilterClass.toggleFilterItem(mKeyValue, mData.get(position));
    }
}

