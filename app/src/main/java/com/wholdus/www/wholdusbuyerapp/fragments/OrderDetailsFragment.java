package com.wholdus.www.wholdusbuyerapp.fragments;

import android.content.Context;
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

import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.interfaces.ProfileListenerInterface;
import com.wholdus.www.wholdusbuyerapp.loaders.OrdersLoader;
import com.wholdus.www.wholdusbuyerapp.models.Order;

import java.util.ArrayList;

/**
 * Created by kaustubh on 13/12/16.
 */

public class OrderDetailsFragment extends Fragment {

    private ProfileListenerInterface mListener;
    private int mOrderID;
    private Order mOrder;
    private final int ORDERS_DB_LOADER = 20;
    private OrderLoaderManager mOrderLoader;

    public OrderDetailsFragment() {
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
        Bundle arguments = getArguments();
        mOrderID = arguments.getInt("orderID", -1);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_orders, container, false);
        initReferences(rootView);

        mOrderLoader = new OrderLoaderManager();
        getActivity().getSupportLoaderManager().restartLoader(ORDERS_DB_LOADER, null, mOrderLoader);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void initReferences(ViewGroup rootView){

    }

    public void setViewForOrders(ArrayList<Order> orders){
        if (orders.size() ==1){
            mOrder= orders.get(0);
            mListener.fragmentCreated(getString(R.string.order_card_order_number) + mOrder.getDisplayNumber(), true);
        }
    }

    private class OrderLoaderManager implements LoaderManager.LoaderCallbacks<ArrayList<Order>> {

        @Override
        public void onLoaderReset(Loader<ArrayList<Order>> loader) {
        }

        @Override
        public void onLoadFinished(Loader<ArrayList<Order>> loader, ArrayList<Order> data) {
            setViewForOrders(data);
        }


        @Override
        public Loader<ArrayList<Order>> onCreateLoader(final int id, Bundle args) {
            return new OrdersLoader(getContext(), mOrderID, null, true, true, true, true);
        }
    }
}
