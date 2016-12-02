package com.wholdus.www.wholdusbuyerapp.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.databaseContracts.UserProfileContract.UserAddressTable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by aditya on 27/11/16.
 */

public class AddressDisplayListViewAdapter extends BaseAdapter {

    private Context mContext;
    private JSONArray mData;

    public AddressDisplayListViewAdapter(Context context, JSONArray data) {
        mContext = context;
        mData = data;
    }

    @Override
    public int getCount() {
        return mData.length();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.address_display_list_view, viewGroup, false);
        }

        try {
            JSONObject currentData = mData.getJSONObject(i);

            TextView aliasTextView = (TextView) view.findViewById(R.id.alias_text_view);
            aliasTextView.setText(currentData.getString(UserAddressTable.COLUMN_ADDRESS_ALIAS));

            TextView contactNumberTextView = (TextView) view.findViewById(R.id.contact_number_text_view);
            contactNumberTextView.setText(currentData.getString(UserAddressTable.COLUMN_CONTACT_NUMBER));

            TextView addressTextView = (TextView) view.findViewById(R.id.address_text_view);
            addressTextView.setText(getAddressTextView(currentData));

            TextView cityStatePincodeTextView = (TextView) view.findViewById(R.id.city_state_pincode_text_view);
            cityStatePincodeTextView.setText(getCityStatePincodeTextView(currentData));

            view.setTag(R.integer.addressID, currentData.getString(UserAddressTable.COLUMN_ADDRESS_ID));
            view.setTag(R.integer._ID, currentData.getInt(UserAddressTable._ID));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return view;
    }

    private String getAddressTextView(JSONObject data) throws JSONException {
        String address = data.getString(UserAddressTable.COLUMN_ADDRESS);
        String landmark = data.getString(UserAddressTable.COLUMN_LANDMARK);

        if (landmark.isEmpty()) return address;
        else if (address.isEmpty()) return landmark;
        else return address + " " + landmark;
    }

    private String getCityStatePincodeTextView(JSONObject data) throws JSONException {
        String city = data.getString(UserAddressTable.COLUMN_CITY);
        String state = data.getString(UserAddressTable.COLUMN_STATE);
        String pincode = data.getString(UserAddressTable.COLUMN_PINCODE);

        ArrayList<String> value = new ArrayList<>();

        if (!city.isEmpty()) value.add(city);
        if (!state.isEmpty()) value.add(state);
        if (!pincode.isEmpty()) value.add(pincode);

        return TextUtils.join(", ", value);
    }
}
