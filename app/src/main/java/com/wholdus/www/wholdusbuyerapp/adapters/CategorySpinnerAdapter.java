package com.wholdus.www.wholdusbuyerapp.adapters;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.helperClasses.HelperFunctions;
import com.wholdus.www.wholdusbuyerapp.models.Category;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aditya on 11/12/16.
 */

public class CategorySpinnerAdapter extends BaseAdapter implements SpinnerAdapter {

    private Context mContext;
    private List<Category> mCategories;

    public CategorySpinnerAdapter(Context context, ArrayList<Category> categories) {
        mContext = context;
        mCategories = categories;
    }

    @Override
    public int getCount() {
        return mCategories.size();
    }

    @Override
    public Object getItem(int i) {
        return mCategories.get(i);
    }

    @Override
    public long getItemId(int i) {
        return mCategories.get(i).getCategoryID();
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        int selectedItemPosition = position;
        if (parent instanceof AdapterView) {
            selectedItemPosition = ((AdapterView) parent)
                    .getSelectedItemPosition();
        }
        return makeLayout(selectedItemPosition, convertView, parent, R.layout.layout_spinner_item, -1);
    }

    @Override
    public View getDropDownView(final int position, final View convertView, final ViewGroup parent) {
        return makeLayout(position, convertView, parent, android.R.layout.simple_spinner_dropdown_item, android.R.color.white);
    }

    private View makeLayout(final int position, final View convertView, final ViewGroup parent, final int layout, int color) {
        TextView dataTextView;
        if (convertView != null) {
            dataTextView = (TextView) convertView;
        } else {
            dataTextView = (TextView) LayoutInflater.from(mContext).inflate(layout,
                    parent, false);
        }
        if (color == -1) {
            dataTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        } else {
            dataTextView.setBackgroundColor(mContext.getResources().getColor(color));
        }
        dataTextView.setText(mCategories.get(position).getCategoryName());

        return dataTextView;
    }

    public int getPositionFromID(int categoryID) {
        for (int i = 0; i < mCategories.size(); i++) {
            if (mCategories.get(i).getCategoryID() == categoryID) {
                return i;
            }
        }
        return 0;
    }
}
