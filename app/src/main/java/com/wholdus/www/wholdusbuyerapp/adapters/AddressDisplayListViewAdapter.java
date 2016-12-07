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
import com.wholdus.www.wholdusbuyerapp.models.BuyerAddress;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.R.attr.data;

/**
 * Created by aditya on 27/11/16.
 */

public class AddressDisplayListViewAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<BuyerAddress> mData;

    public AddressDisplayListViewAdapter(Context context, ArrayList<BuyerAddress> data) {
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
            view = LayoutInflater.from(mContext).inflate(R.layout.address_display_list_view, viewGroup, false);
        }

        BuyerAddress address = (BuyerAddress) getItem(i);

        TextView aliasTextView = (TextView) view.findViewById(R.id.alias_text_view);
        aliasTextView.setText(address.getAlias());

        TextView contactNumberTextView = (TextView) view.findViewById(R.id.contact_number_text_view);
        contactNumberTextView.setText(address.getContactNumber());

        TextView addressTextView = (TextView) view.findViewById(R.id.address_text_view);
        addressTextView.setText(getAddressTextView(address));

        TextView cityStatePincodeTextView = (TextView) view.findViewById(R.id.city_state_pincode_text_view);
        cityStatePincodeTextView.setText(getCityStatePincodeTextView(address));

        view.setTag(R.integer.addressID, address.getAddressID());
        view.setTag(R.integer._ID, address.get_ID());

        return view;
    }

    private String getAddressTextView(BuyerAddress data) {
        String address = data.getAddress();
        String landmark = data.getLandmark();

        if (landmark.isEmpty()) return address;
        else if (address.isEmpty()) return landmark;
        else return address + " " + landmark;
    }

    private String getCityStatePincodeTextView(BuyerAddress data) {
        String city = data.getCity();
        String state = data.getState();
        String pincode = data.getPincode();

        ArrayList<String> value = new ArrayList<>();

        if (!city.isEmpty()) value.add(city);
        if (!state.isEmpty()) value.add(state);
        if (!pincode.isEmpty()) value.add(pincode);

        return TextUtils.join(", ", value);
    }
}
