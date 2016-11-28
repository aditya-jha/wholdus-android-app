package com.wholdus.www.wholdusbuyerapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.wholdus.www.wholdusbuyerapp.databaseContracts.UserProfileContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by aditya on 28/11/16.
 */

public class BusinessTypesAdapter extends BaseAdapter implements SpinnerAdapter {

    private Context mContext;
    private JSONArray mData;

    public BusinessTypesAdapter(Context context, JSONArray data) {
        mContext = context;
        mData = data;
    }

    @Override
    public int getCount() {
        return mData.length();
    }

    @Override
    public Object getItem(int i) {
        try {
            return mData.getJSONObject(i);
        } catch (JSONException e) {
        }
        return null;
    }

    @Override
    public long getItemId(int i) {
        try {
            JSONObject currItem = mData.getJSONObject(i);
            return (long) Integer.parseInt(currItem.getString(UserProfileContract.BusinessTypesTable.COLUMN_BUSINESS_TYPE_ID));
        } catch (JSONException e) {
        }
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(android.R.layout.simple_spinner_item, viewGroup, false);
        }
        try {
            JSONObject currentData = mData.getJSONObject(i);

            TextView dataTextView = (TextView) view.findViewById(android.R.id.text1);
            dataTextView.setText(currentData.getString(UserProfileContract.BusinessTypesTable.COLUMN_BUSINESS_TYPE));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return view;
    }

    public int getSelectedItemIndex(String businessType) {
        try {
            for (int i = 0; i < mData.length(); i++) {
                if (mData.getJSONObject(i).getString(UserProfileContract.BusinessTypesTable.COLUMN_BUSINESS_TYPE).equals(businessType)) {
                    return i;
                }
            }
        } catch (JSONException e) {
        }
        return 0;
    }
}
