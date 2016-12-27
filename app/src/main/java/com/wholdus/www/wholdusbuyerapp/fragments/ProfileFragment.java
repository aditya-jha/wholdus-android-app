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
import com.wholdus.www.wholdusbuyerapp.adapters.BuyerPersonalDetailsAdapter;
import com.wholdus.www.wholdusbuyerapp.helperClasses.HelperFunctions;
import com.wholdus.www.wholdusbuyerapp.interfaces.ProfileListenerInterface;
import com.wholdus.www.wholdusbuyerapp.loaders.ProfileLoader;
import com.wholdus.www.wholdusbuyerapp.models.Buyer;
import com.wholdus.www.wholdusbuyerapp.models.BuyerAddress;
import com.wholdus.www.wholdusbuyerapp.models.BuyerPersonalDetails;
import com.wholdus.www.wholdusbuyerapp.services.UserService;

import java.util.ArrayList;

/**
 * Created by aditya on 19/11/16.
 * fragment for Displaying user profile
 */

public class ProfileFragment extends Fragment implements LoaderManager.LoaderCallbacks<Buyer> {


    private ListView mPersonalDetailsListView;
    private BroadcastReceiver mUserServiceResponseReceiver;
    private ProfileListenerInterface mListener;
    private final int USER_DB_LOADER = 0;

    public ProfileFragment() {
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
        mUserServiceResponseReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                handleAPIResponse();
            }
        };
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_profile, container, false);

        initReferences(rootView);
        getActivity().getSupportLoaderManager().restartLoader(USER_DB_LOADER, null, this);

        fetchDataFromServer();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        IntentFilter intentFilter = new IntentFilter(getString(R.string.user_data_updated));
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mUserServiceResponseReceiver, intentFilter);

        mListener.fragmentCreated("My Profile", false);
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mUserServiceResponseReceiver);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mUserServiceResponseReceiver = null;
        //TODO : Do nullifying receivers for all fragments and activities
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public Loader<Buyer> onCreateLoader(int id, Bundle args) {
        return new ProfileLoader(getContext(), true, false, false);
    }

    @Override
    public void onLoadFinished(Loader<Buyer> loader, Buyer data) {
        setViewForPersonalDetails(data);

    }

    @Override
    public void onLoaderReset(Loader<Buyer> loader) {

    }

    private void initReferences(ViewGroup rootView) {
        mPersonalDetailsListView = (ListView) rootView.findViewById(R.id.personal_details_list_view);

        TextView mEditPersonalDetailsTextView = (TextView) rootView.findViewById(R.id.edit_personal_details_text_view);
        mEditPersonalDetailsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.editPersonalDetails();
            }
        });

    }

    private void handleAPIResponse() {
        getActivity().getSupportLoaderManager().restartLoader(USER_DB_LOADER, null, this);
    }

    private void setViewForPersonalDetails(Buyer buyer) {
        if (buyer == null) return;

        ArrayList<BuyerPersonalDetails> items = new ArrayList<>();
        items.add(new BuyerPersonalDetails(getString(R.string.name_key),
                buyer.getName(), R.drawable.ic_person_black_24dp));
        items.add(new BuyerPersonalDetails(getString(R.string.company_name_key),
                buyer.getCompanyName(), R.drawable.ic_store_mall_directory_black_28dp));
        items.add(new BuyerPersonalDetails(getString(R.string.mobile_number_key),
                buyer.getMobileNumber(), R.drawable.ic_phone_black_24dp));
        items.add(new BuyerPersonalDetails(getString(R.string.email_key),
                buyer.getEmail(), R.drawable.ic_mail_outline_black_24dp));
        items.add(new BuyerPersonalDetails(getString(R.string.whatsapp_number_key),
                buyer.getWhatsappNumber(), R.drawable.ic_perm_phone_msg_black_24dp));

        BuyerPersonalDetailsAdapter adapter = new BuyerPersonalDetailsAdapter(getContext(), items);
        mPersonalDetailsListView.setAdapter(adapter);
        HelperFunctions.setListViewHeightBasedOnChildren(mPersonalDetailsListView);
    }


    private void fetchDataFromServer() {
        Intent intent = new Intent(getContext(), UserService.class);
        intent.putExtra("TODO", R.string.fetch_user_profile);
        getContext().startService(intent);
    }
}
