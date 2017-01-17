package com.wholdus.www.wholdusbuyerapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.models.BusinessTypes;

import java.util.ArrayList;

/**
 * Created by kaustubh on 16/1/17.
 */

public class BusinessTypeSelectAdapter extends BaseAdapter {

    private RadioButton mSelectedRadioButton;
    private int mSelectedPosition = -1;
    private ArrayList<BusinessTypes> mData;
    private Context mContext;

    public BusinessTypeSelectAdapter(Context context, ArrayList<BusinessTypes> data) {
        mContext = context;
        mData = data;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public Object getItem(int i) {
        return mData.get(i);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;


        if(convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_layout_business_type_select, parent, false);
            holder = new ViewHolder();

            holder.name = (TextView)convertView.findViewById(R.id.business_type_name);
            holder.radioButton = (RadioButton)convertView.findViewById(R.id.business_type_radio_button);

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder)convertView.getTag();
        }

        BusinessTypes businessType = mData.get(position);

        holder.radioButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(position != mSelectedPosition && mSelectedRadioButton != null){
                    mSelectedRadioButton.setChecked(false);
                }

                mSelectedPosition = position;
                mSelectedRadioButton = (RadioButton)v;
            }
        });

        if(mSelectedPosition != position){
            holder.radioButton.setChecked(false);
        }  else{
            holder.radioButton.setChecked(true);
            if(mSelectedRadioButton != null && holder.radioButton != mSelectedRadioButton){
                mSelectedRadioButton = holder.radioButton;
            }
        }

        holder.name.setText(businessType.getBusinessType());
        return convertView;
    }

    public int getSelectedPosition(){return mSelectedPosition;}

    public void clearSelectedPosition(){mSelectedPosition = -1;}

    private class ViewHolder{
        TextView name;
        RadioButton radioButton;
    }

}
