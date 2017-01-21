package com.wholdus.www.wholdusbuyerapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.wholdus.www.wholdusbuyerapp.databaseContracts.UserProfileContract;
import com.wholdus.www.wholdusbuyerapp.models.BusinessTypes;
import com.wholdus.www.wholdusbuyerapp.models.Category;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by aditya on 28/11/16.
 */

public class BusinessTypesAdapter extends BaseAdapter implements SpinnerAdapter {

    private Context mContext;
    private ArrayList<BusinessTypes> mData;

    public BusinessTypesAdapter(Context context, ArrayList<BusinessTypes> data) {
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
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(android.R.layout.simple_spinner_dropdown_item, viewGroup, false);
        }

        BusinessTypes businessType = mData.get(i);

        TextView dataTextView = (TextView) view.findViewById(android.R.id.text1);
        dataTextView.setText(businessType.getBusinessType());

        view.setTag(businessType.getBusinessTypeID());

        return view;
    }

    public int getSelectedItemIndex(String businessType) {
        for (int i=0; i<mData.size(); i++) {
            if (mData.get(i).getBusinessType().equals(businessType)) {
                return i;
            }
        }
        return 0;
    }
}
