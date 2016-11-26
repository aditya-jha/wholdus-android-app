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
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.adapters.BuyerPersonalDetailsAdapter;
import com.wholdus.www.wholdusbuyerapp.databaseContracts.UserProfileContract.UserTable;
import com.wholdus.www.wholdusbuyerapp.databaseHelpers.UserDBHelper;
import com.wholdus.www.wholdusbuyerapp.loaders.UserDBLoader;
import com.wholdus.www.wholdusbuyerapp.models.BuyerPersonalDetails;
import com.wholdus.www.wholdusbuyerapp.services.UserService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by aditya on 19/11/16.
 */

public class ProfileFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private ListView mPersonalDetailsListView;
    private ListView mAddressListView;
    private BroadcastReceiver mUserServiceResponseReceiver;
    private static final int USER_DB_LOADER = 0;

    public ProfileFragment() {
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
        getActivity().getSupportLoaderManager().initLoader(USER_DB_LOADER, null, this);

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

    private void initReferences(ViewGroup rootView) {
        mPersonalDetailsListView = (ListView) rootView.findViewById(R.id.personal_details_list_view);
    }

    private void handleAPIResponse() {
        getActivity().getSupportLoaderManager().restartLoader(USER_DB_LOADER, null, this);
    }

    private void setViewForPersonalDetails(JSONObject buyer) {
        if (buyer == null) return;

        ArrayList<BuyerPersonalDetails> items = new ArrayList<>();
        try {
            items.add(new BuyerPersonalDetails("Name", buyer.getString(UserTable.COLUMN_NAME), R.drawable.ic_person_black_24dp));
            items.add(new BuyerPersonalDetails("Company Name", buyer.getString(UserTable.COLUMN_COMPANY_NAME), R.drawable.ic_store_mall_directory_black_24dp));
            items.add(new BuyerPersonalDetails("Mobile Number", buyer.getString(UserTable.COLUMN_MOBILE_NUMBER), R.drawable.ic_phone_black_24dp));
            items.add(new BuyerPersonalDetails("E-mail", buyer.getString(UserTable.COLUMN_EMAIL), R.drawable.ic_mail_outline_black_24dp));
            items.add(new BuyerPersonalDetails("Whatsapp Number", buyer.getString(UserTable.COLUMN_WHATSAPP_NUMBER), R.drawable.ic_perm_phone_msg_black_24dp));
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        BuyerPersonalDetailsAdapter adapter = new BuyerPersonalDetailsAdapter(getContext(), items);
        mPersonalDetailsListView.setAdapter(adapter);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new UserDBLoader(getContext());
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (data.getCount()) {
            case 0:
                // no data present.
                // keep showing loader
                break;
            case 1:
                // data present, update the UI
                // stop the loader
                // fetch latest data from server if present
                setViewForPersonalDetails(UserDBHelper.getJSONDataFromCursor(data, 0));
                break;
            default:
                // this should not happen
                data.close();
                return;
        }
        data.close();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
