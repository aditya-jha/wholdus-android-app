package com.wholdus.www.wholdusbuyerapp.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.models.BuyerPersonalDetails;

import java.util.ArrayList;

/**
 * Created by aditya on 19/11/16.
 */

public class BuyerPersonalDetailsAdapter extends ArrayAdapter<BuyerPersonalDetails> {

    public BuyerPersonalDetailsAdapter(Context context, ArrayList<BuyerPersonalDetails> buyerPersonalDetails) {
        super(context, 0, buyerPersonalDetails);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.personal_details_list_view, parent, false);
        }

        BuyerPersonalDetails currentBuyerPersonalDetails = getItem(position);

        TextView keyTextView = (TextView) convertView.findViewById(R.id.key_text_view);
        keyTextView.setText(currentBuyerPersonalDetails.getKey());

        TextView valueTextView = (TextView) convertView.findViewById(R.id.value_text_view);
        valueTextView.setText(currentBuyerPersonalDetails.getValue());

        ImageView iconImageView = (ImageView) convertView.findViewById(R.id.icon_iamge_view);
        iconImageView.setImageResource(currentBuyerPersonalDetails.getIconResource());

        return convertView;
    }
}
