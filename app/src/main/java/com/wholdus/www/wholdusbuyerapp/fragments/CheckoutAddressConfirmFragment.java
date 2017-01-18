package com.wholdus.www.wholdusbuyerapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.databaseContracts.UserProfileContract;
import com.wholdus.www.wholdusbuyerapp.interfaces.CartListenerInterface;
import com.wholdus.www.wholdusbuyerapp.loaders.BuyerAddressLoader;
import com.wholdus.www.wholdusbuyerapp.models.BuyerAddress;

import java.util.ArrayList;

/**
 * Created by kaustubh on 28/12/16.
 */

public class CheckoutAddressConfirmFragment extends Fragment implements LoaderManager.LoaderCallbacks<ArrayList<BuyerAddress>>{

    private int mAddressID;
    private BuyerAddress mAddress;
    private CartListenerInterface mListener;
    private final int USER_ADDRESS_DB_LOADER = 51;

    private TextView mAliasTextView;
    private TextView mContactNumberTextView;
    private TextView mAddressTextView;
    private TextView mCityStatePincodeTextView;
    private TextView mSelectAnother;

    public CheckoutAddressConfirmFragment(){

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (CartListenerInterface) context;
        } catch (ClassCastException cee) {
            cee.printStackTrace();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_checkout_confirm_address, container, false);

        initReferences(rootView);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        Bundle args = getArguments();
        mAddressID = args.getInt(UserProfileContract.UserAddressTable.COLUMN_ADDRESS_ID);
        getActivity().getSupportLoaderManager().restartLoader(USER_ADDRESS_DB_LOADER, null, this);
        mListener.fragmentCreated("Confirm Address", true);
    }

    private void initReferences(ViewGroup rootView){
        mAliasTextView = (TextView) rootView.findViewById(R.id.alias_text_view);
        mContactNumberTextView = (TextView) rootView.findViewById(R.id.contact_number_text_view);
        mAddressTextView = (TextView) rootView.findViewById(R.id.address_text_view);
        mCityStatePincodeTextView = (TextView) rootView.findViewById(R.id.city_state_pincode_text_view);
        mSelectAnother  = (TextView) rootView.findViewById(R.id.checkout_confirm_address_select_another_text_view);
    }

    private void setViewForAddress(){
        mListener.disableProgressBar();
        mListener.addressSelected(mAddressID);
        mAliasTextView.setText(mAddress.getAlias());
        mContactNumberTextView.setText(mAddress.getContactNumber());
        mAddressTextView.setText(getAddressTextView(mAddress));
        mCityStatePincodeTextView.setText(getCityStatePincodeTextView(mAddress));

        mSelectAnother.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.openSelectAddress();
            }
        });

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

    @Override
    public void onLoaderReset(Loader<ArrayList<BuyerAddress>> loader) {

    }

    @Override
    public void onLoadFinished(Loader<ArrayList<BuyerAddress>> loader, ArrayList<BuyerAddress> data) {
        if (data != null && data.size() == 1) {
            mAddress = data.get(0);
            setViewForAddress();
        }
    }

    @Override
    public Loader<ArrayList<BuyerAddress>> onCreateLoader(int id, Bundle args) {
        return new BuyerAddressLoader(getContext(), mAddressID, -1);
    }

}
