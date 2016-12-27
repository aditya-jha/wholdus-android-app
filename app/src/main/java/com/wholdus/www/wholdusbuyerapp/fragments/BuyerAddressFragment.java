package com.wholdus.www.wholdusbuyerapp.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.adapters.AddressDisplayListViewAdapter;
import com.wholdus.www.wholdusbuyerapp.helperClasses.HelperFunctions;
import com.wholdus.www.wholdusbuyerapp.interfaces.ProfileListenerInterface;
import com.wholdus.www.wholdusbuyerapp.models.BuyerAddress;

import java.util.ArrayList;

/**
 * Created by kaustubh on 26/12/16.
 */

public class BuyerAddressFragment extends Fragment implements LoaderManager.LoaderCallbacks<ArrayList<BuyerAddress>> {

    private TextView mNoAddressTextView;
    private ListView mAddressListView;
    private ProfileListenerInterface mListener;
    private final int USER_ADDRESS_DB_LOADER = 50;
    //TODO Define all loaders at one place


    public BuyerAddressFragment(){

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (ProfileListenerInterface) context;
        } catch (ClassCastException cee) {
            cee.printStackTrace();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_profile, container, false);

        initReferences(rootView);
        getActivity().getSupportLoaderManager().restartLoader(USER_ADDRESS_DB_LOADER, null, this);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mListener.fragmentCreated("My Addresses", false);
    }


    private void setViewForAddressListView(ArrayList<BuyerAddress> address) {
        if (address.isEmpty()) {
            mNoAddressTextView.setVisibility(View.VISIBLE);
        } else {
            mNoAddressTextView.setVisibility(View.GONE);
            AddressDisplayListViewAdapter adapter = new AddressDisplayListViewAdapter(getActivity().getApplicationContext(), address);
            mAddressListView.setAdapter(adapter);
            HelperFunctions.setListViewHeightBasedOnChildren(mAddressListView);

            mAddressListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    int _ID = (Integer) view.getTag(R.integer._ID);
                    int addressID = (int) view.getTag(R.integer.addressID);

                    mListener.editAddress(addressID, _ID);
                }
            });
        }
    }

    private void initReferences(ViewGroup rootView) {
        mAddressListView = (ListView) rootView.findViewById(R.id.address_list_view);
        mNoAddressTextView = (TextView) rootView.findViewById(R.id.no_address_text_view);
        TextView mAddAddressTextView = (TextView) rootView.findViewById(R.id.add_address_text_view);
        mAddAddressTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.editAddress(-1, -1);
            }
        });
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<BuyerAddress>> loader) {

    }

    @Override
    public void onLoadFinished(Loader<ArrayList<BuyerAddress>> loader, ArrayList<BuyerAddress> data) {
        setViewForAddressListView(data);
    }

    @Override
    public Loader<ArrayList<BuyerAddress>> onCreateLoader(int id, Bundle args) {
        return null;
    }
}
