package com.wholdus.www.wholdusbuyerapp.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.WholdusApplication;
import com.wholdus.www.wholdusbuyerapp.adapters.OrdersAdapter;
import com.wholdus.www.wholdusbuyerapp.databaseContracts.UserProfileContract;
import com.wholdus.www.wholdusbuyerapp.databaseHelpers.UserDBHelper;
import com.wholdus.www.wholdusbuyerapp.decorators.RecyclerViewVerticalSpaceItemDecoration;
import com.wholdus.www.wholdusbuyerapp.interfaces.ItemClickListener;
import com.wholdus.www.wholdusbuyerapp.interfaces.ProfileListenerInterface;
import com.wholdus.www.wholdusbuyerapp.loaders.OrdersLoader;
import com.wholdus.www.wholdusbuyerapp.models.Order;
import com.wholdus.www.wholdusbuyerapp.services.OrderService;
import com.wholdus.www.wholdusbuyerapp.services.UserService;

import org.json.JSONException;

import java.util.ArrayList;

import static android.support.v7.recyclerview.R.attr.layoutManager;

/**
 * Created by aditya on 19/11/16.
 */

public class OrdersFragment extends Fragment implements ItemClickListener {

    private ProfileListenerInterface mListener;
    private RecyclerView mOrdersListView;
    private final int ORDERS_DB_LOADER = 10;
    private BroadcastReceiver mOrderServiceResponseReceiver;
    private OrderLoaderManager mOrderLoader;
    private OrdersAdapter ordersAdapter;
    ArrayList<Order> mOrderArrayList;

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

        fetchDataFromServer();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        mOrderLoader = new OrderLoaderManager();
        getActivity().getSupportLoaderManager().restartLoader(ORDERS_DB_LOADER, null, mOrderLoader);

        IntentFilter intentFilter = new IntentFilter(getString(R.string.order_data_updated));
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mOrderServiceResponseReceiver, intentFilter);
        mListener.fragmentCreated("My Orders", true);
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

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void handleAPIResponse() {
        getActivity().getSupportLoaderManager().restartLoader(ORDERS_DB_LOADER, null, mOrderLoader);
    }

    private void initReferences(ViewGroup rootView){
        mOrdersListView = (RecyclerView) rootView.findViewById(R.id.orders_recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mOrdersListView.setLayoutManager(mLayoutManager);
        mOrdersListView.setItemAnimator(new DefaultItemAnimator());
        RecyclerViewVerticalSpaceItemDecoration dividerItemDecoration = new RecyclerViewVerticalSpaceItemDecoration(40);
        mOrdersListView.addItemDecoration(dividerItemDecoration);
        mOrderArrayList = new ArrayList<>();
        ordersAdapter = new OrdersAdapter(getContext(), mOrderArrayList, this);
        mOrdersListView.setAdapter(ordersAdapter);
    }

    private void setViewForOrders(ArrayList<Order> orders){
        //TODO: If empty list, handle case
        mOrderArrayList.clear();
        mOrderArrayList.addAll(orders);
        //TODO: More efficient way to implement clear and add
        ordersAdapter.notifyDataSetChanged();
    }

    private void fetchDataFromServer(){
        Intent intent = new Intent(getContext(), OrderService.class);
        intent.putExtra("TODO", R.string.fetch_orders);
        getContext().startService(intent);
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
            return new OrdersLoader(getContext(), -1, null, true, false, false, true);
        }
   }

    @Override
    public void itemClicked(int position, int id) {
        mListener.openOrderDetails(mOrderArrayList.get(position).getOrderID());
    }
}
