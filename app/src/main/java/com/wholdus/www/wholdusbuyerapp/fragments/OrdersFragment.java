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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.WholdusApplication;
import com.wholdus.www.wholdusbuyerapp.databaseContracts.UserProfileContract;
import com.wholdus.www.wholdusbuyerapp.databaseHelpers.UserDBHelper;
import com.wholdus.www.wholdusbuyerapp.interfaces.ProfileListenerInterface;
import com.wholdus.www.wholdusbuyerapp.services.UserService;

import org.json.JSONException;

/**
 * Created by aditya on 19/11/16.
 */

public class OrdersFragment extends Fragment {

    private ProfileListenerInterface mListener;
    private ListView mOrdersListView;
    private UserDBHelper mUserDBHelper;
    private final int ORDERS_DB_LOADER = 10;
    private BroadcastReceiver mOrderServiceResponseReceiver;
    private OrderLoader mOrderLoader;

    public OrdersFragment() {
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
        mOrderServiceResponseReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                handleAPIResponse();
            }
        };
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_orders, container, false);
        initReferences(rootView);
        mOrderLoader = new OrderLoader();
        getActivity().getSupportLoaderManager().restartLoader(ORDERS_DB_LOADER, null, mOrderLoader);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        IntentFilter intentFilter = new IntentFilter(getString(R.string.order_data_updated));
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mOrderServiceResponseReceiver, intentFilter);

        mListener.fragmentCreated("My Orders", true);

        Intent intent = new Intent(getContext(), OrderService.class);
        intent.putExtra("TODO", R.string.fetch_orders);
        getContext().startService(intent);

    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mOrderServiceResponseReceiver);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mOrderServiceResponseReceiver = null;
    }

    private void handleAPIResponse() {
        getActivity().getSupportLoaderManager().restartLoader(ORDERS_DB_LOADER, null, mOrderLoader);
    }

    private void initReferences(ViewGroup rootView){
        mOrdersListView = (ListView) rootView.findViewById(R.id.personal_details_list_view);
    }

    private class OrderLoader implements LoaderManager.LoaderCallbacks<Cursor> {

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            try {
                if (loader.getId() == ORDERS_DB_LOADER) {
                    if (data.getCount() == 1) {
                        setViewForPersonalDetails(mUserDBHelper.getJSONDataFromCursor(UserProfileContract.UserTable.TABLE_NAME, data, 0));
                    }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        public Loader<Cursor> onCreateLoader(final int id, Bundle args) {
            return new CursorLoader(getContext()) {
                @Override
                public Cursor loadInBackground() {

                    switch (id) {
                        case ORDERS_DB_LOADER:
                            mUserDBHelper = new UserDBHelper(getContext());
                            return mUserDBHelper.getOrdersData(null, null);
                        default:
                            return null;
                    }
                }
            };
        }
}
}
