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
        ViewHolder holder;

        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.address_display_list_view, viewGroup, false);
            holder = new ViewHolder();
            holder.aliasTextView = (TextView) view.findViewById(R.id.alias_text_view);
            holder.contactNumberTextView = (TextView) view.findViewById(R.id.contact_number_text_view);
            holder.addressTextView = (TextView) view.findViewById(R.id.address_text_view);
            holder.cityStatePincodeTextView = (TextView) view.findViewById(R.id.city_state_pincode_text_view);
            view.setTag(holder);
        }
        else {
            holder = (ViewHolder) view.getTag();
        }

        BuyerAddress address = mData.get(i);

        holder.aliasTextView.setText(address.getAlias());
        holder.contactNumberTextView.setText(address.getContactNumber());
        holder.addressTextView.setText(getAddressTextView(address));
        holder.cityStatePincodeTextView.setText(getCityStatePincodeTextView(address));

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

    class ViewHolder{
        int id;
        TextView aliasTextView;
        TextView contactNumberTextView;
        TextView addressTextView;
        TextView cityStatePincodeTextView;
    }
}
