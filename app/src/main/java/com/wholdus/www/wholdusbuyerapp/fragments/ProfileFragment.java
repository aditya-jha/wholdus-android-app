package com.wholdus.www.wholdusbuyerapp.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.adapters.BuyerPersonalDetailsAdapter;
import com.wholdus.www.wholdusbuyerapp.models.BuyerPersonalDetails;
import com.wholdus.www.wholdusbuyerapp.services.UserService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by aditya on 19/11/16.
 */

public class ProfileFragment extends Fragment {

    private ListView mPersonalDetailsListView;
    private ListView mAddressListView;
    private BroadcastReceiver mUserServiceResponseReceiver;
    private UserService mUserService;

    public ProfileFragment() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_profile, container, false);

        mUserServiceResponseReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String response = intent.getStringExtra(getString(R.string.api_response_data_key));
                handleAPIResponse(response);
            }
        };

        initReferences(rootView);

        return  rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        IntentFilter intentFilter = new IntentFilter(getString(R.string.api_response));
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mUserServiceResponseReceiver, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mUserServiceResponseReceiver);
    }

    private void initReferences(ViewGroup rootView) {
        mPersonalDetailsListView = (ListView) rootView.findViewById(R.id.personal_details_list_view);
    }

    private void fetchUserData() {
        mUserService = new UserService(getContext());
        mUserService.getUserDetails();
    }

    private void handleAPIResponse(String response) {
        try {
            JSONObject data = new JSONObject(response);
            JSONArray buyers = data.getJSONArray("buyers");

            if(buyers.length() != 1) {
                // this should not happen
            }

            setViewForPersonalDetails(buyers.getJSONObject(0));


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setViewForPersonalDetails(JSONObject buyer) {
        ArrayList<BuyerPersonalDetails> items = new ArrayList<>();
        try {
            items.add(new BuyerPersonalDetails("Name", buyer.getString("name"), R.drawable.ic_person_black_24dp));
            items.add(new BuyerPersonalDetails("Company Name", buyer.getString("company_name"), R.drawable.ic_store_mall_directory_black_24dp));
            items.add(new BuyerPersonalDetails("Mobile Number", buyer.getString("mobile_number"), R.drawable.ic_phone_black_24dp));
            items.add(new BuyerPersonalDetails("E-mail", buyer.getString("email"), R.drawable.ic_mail_outline_black_24dp));
            items.add(new BuyerPersonalDetails("Whatsapp Number", buyer.getString("whatsapp_number"), R.drawable.ic_perm_phone_msg_black_24dp));
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        BuyerPersonalDetailsAdapter adapter = new BuyerPersonalDetailsAdapter(getContext(), items);
        mPersonalDetailsListView.setAdapter(adapter);
    }
}
