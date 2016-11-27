package com.wholdus.www.wholdusbuyerapp.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.WholdusApplication;
import com.wholdus.www.wholdusbuyerapp.adapters.AddressDisplayListViewAdapter;
import com.wholdus.www.wholdusbuyerapp.adapters.BuyerPersonalDetailsAdapter;
import com.wholdus.www.wholdusbuyerapp.databaseContracts.UserProfileContract.UserAddressTable;
import com.wholdus.www.wholdusbuyerapp.databaseContracts.UserProfileContract.UserTable;
import com.wholdus.www.wholdusbuyerapp.databaseHelpers.UserDBHelper;
import com.wholdus.www.wholdusbuyerapp.interfaces.ProfileListenerInterface;
import com.wholdus.www.wholdusbuyerapp.models.BuyerPersonalDetails;
import com.wholdus.www.wholdusbuyerapp.services.UserService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by aditya on 19/11/16.
 * fragment for CRUD operations on user profile
 */

public class ProfileFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private TextView mNoAddressTextView;
    private ListView mPersonalDetailsListView;
    private ListView mAddressListView;
    private TextView mEditPersonalDetailsTextView;
    private BroadcastReceiver mUserServiceResponseReceiver;
    private UserDBHelper mUserDBHelper, mUserAddressDBHelper;
    private ProfileListenerInterface listener;

    public ProfileFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (ProfileListenerInterface) context;
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

        mUserServiceResponseReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                handleAPIResponse();
            }
        };

        getActivity().getSupportLoaderManager().initLoader(R.integer.user_details_db_loader, null, this);
        getActivity().getSupportLoaderManager().initLoader(R.integer.user_address_db_loader, null, this);

        initReferences(rootView);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        IntentFilter intentFilter = new IntentFilter(getString(R.string.user_data_updated));
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mUserServiceResponseReceiver, intentFilter);

        Intent intent = new Intent(getContext(), UserService.class);
        intent.putExtra("TODO", R.string.fetch_user_profile);
        getContext().startService(intent);
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
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public Loader<Cursor> onCreateLoader(final int id, Bundle args) {
        return new CursorLoader(getContext()) {
            @Override
            public Cursor loadInBackground() {
                WholdusApplication wholdusApplication = (WholdusApplication) getActivity().getApplication();

                switch (id) {
                    case R.integer.user_details_db_loader:
                        mUserDBHelper = new UserDBHelper(getContext());
                        return mUserDBHelper.getUserData(wholdusApplication.getBuyerID());
                    case R.integer.user_address_db_loader:
                        mUserAddressDBHelper = new UserDBHelper(getContext());
                        return mUserAddressDBHelper.getUserAddress(wholdusApplication.getBuyerID(), null);
                    default:
                        return null;
                }
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        try {
            if (loader.getId() == R.integer.user_details_db_loader) {
                if(data.getCount() == 1) {
                    setViewForPersonalDetails(mUserDBHelper.getJSONDataFromCursor(UserTable.TABLE_NAME, data, 0));
                }
            } else if(loader.getId() == R.integer.user_address_db_loader) {
                JSONObject address = mUserDBHelper.getJSONDataFromCursor(UserAddressTable.TABLE_NAME, data, -1);
                setViewForAddressListView(address.getJSONArray("address"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        try {
            if(loader.getId() == R.integer.user_address_db_loader) {
                mUserAddressDBHelper.close();
                mUserAddressDBHelper = null;
            } else if(loader.getId() == R.integer.user_details_db_loader) {
                mUserDBHelper.close();
                mUserDBHelper = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("db close error", "eeeeeeeeeeeeee");
        }
    }

    private void initReferences(ViewGroup rootView) {
        mPersonalDetailsListView = (ListView) rootView.findViewById(R.id.personal_details_list_view);
        mAddressListView = (ListView) rootView.findViewById(R.id.address_list_view);
        mNoAddressTextView = (TextView) rootView.findViewById(R.id.no_address_text_view);

        mEditPersonalDetailsTextView = (TextView) rootView.findViewById(R.id.edit_personal_details_text_view);
        mEditPersonalDetailsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.editPersonalDetails();
            }
        });
    }

    private void handleAPIResponse() {
        getActivity().getSupportLoaderManager().restartLoader(R.integer.user_details_db_loader, null, this);
        getActivity().getSupportLoaderManager().restartLoader(R.integer.user_address_db_loader, null, this);
    }

    private void setViewForPersonalDetails(JSONObject buyer) throws JSONException {
        if (buyer == null) return;

        ArrayList<BuyerPersonalDetails> items = new ArrayList<>();
        items.add(new BuyerPersonalDetails("Name", buyer.getString(UserTable.COLUMN_NAME), R.drawable.ic_person_black_24dp));
        items.add(new BuyerPersonalDetails("Company Name", buyer.getString(UserTable.COLUMN_COMPANY_NAME), R.drawable.ic_store_mall_directory_black_24dp));
        items.add(new BuyerPersonalDetails("Mobile Number", buyer.getString(UserTable.COLUMN_MOBILE_NUMBER), R.drawable.ic_phone_black_24dp));
        items.add(new BuyerPersonalDetails("E-mail", buyer.getString(UserTable.COLUMN_EMAIL), R.drawable.ic_mail_outline_black_24dp));
        items.add(new BuyerPersonalDetails("Whatsapp Number", buyer.getString(UserTable.COLUMN_WHATSAPP_NUMBER), R.drawable.ic_perm_phone_msg_black_24dp));

        BuyerPersonalDetailsAdapter adapter = new BuyerPersonalDetailsAdapter(getContext(), items);
        mPersonalDetailsListView.setAdapter(adapter);
    }

    private void setViewForAddressListView(JSONArray address) {
        if(address.length() == 0) {
            mNoAddressTextView.setVisibility(View.VISIBLE);
            return;
        }
        mNoAddressTextView.setVisibility(View.GONE);
        AddressDisplayListViewAdapter adapter = new AddressDisplayListViewAdapter(getActivity().getApplicationContext(), address);
        mAddressListView.setAdapter(adapter);
    }
}
