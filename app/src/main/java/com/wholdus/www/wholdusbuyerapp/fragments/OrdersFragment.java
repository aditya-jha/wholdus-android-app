package com.wholdus.www.wholdusbuyerapp.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.activities.HandPickedActivity;
import com.wholdus.www.wholdusbuyerapp.adapters.OrdersAdapter;
import com.wholdus.www.wholdusbuyerapp.decorators.RecyclerViewSpaceItemDecoration;
import com.wholdus.www.wholdusbuyerapp.helperClasses.FilterClass;
import com.wholdus.www.wholdusbuyerapp.helperClasses.HelperFunctions;
import com.wholdus.www.wholdusbuyerapp.interfaces.ItemClickListener;
import com.wholdus.www.wholdusbuyerapp.interfaces.ProfileListenerInterface;
import com.wholdus.www.wholdusbuyerapp.loaders.OrdersLoader;
import com.wholdus.www.wholdusbuyerapp.models.Order;
import com.wholdus.www.wholdusbuyerapp.services.OrderService;

import java.util.ArrayList;

/**
 * Created by aditya on 19/11/16.
 */

public class OrdersFragment extends Fragment implements ItemClickListener, LoaderManager.LoaderCallbacks<ArrayList<Order>> {

    private ProfileListenerInterface mListener;
    private RecyclerView mOrdersRecyclerView;

    private BroadcastReceiver mOrderServiceResponseReceiver;
    private OrdersAdapter ordersAdapter;
    private ArrayList<Order> mOrderArrayList;
    private Parcelable mOrderListViewState;
    private RecyclerView.LayoutManager mLayoutManager;
    private ProgressBar mPageLoader;
    private boolean mLoadedFromServer;
    private CardView mNoOrders;

    private static final int ORDERS_DB_LOADER = 10;

    public OrdersFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mListener = (ProfileListenerInterface) context;
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
        return inflater.inflate(R.layout.fragment_orders, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mPageLoader = (ProgressBar) view.findViewById(R.id.page_loader);
        mOrdersRecyclerView = (RecyclerView) view.findViewById(R.id.orders_recycler_view);
        mLayoutManager = new LinearLayoutManager(getContext());
        mOrdersRecyclerView.setLayoutManager(mLayoutManager);
        mOrdersRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mOrdersRecyclerView.addItemDecoration(new RecyclerViewSpaceItemDecoration(40, 0));
        mOrderArrayList = new ArrayList<>();
        ordersAdapter = new OrdersAdapter(getContext(), mOrderArrayList, this);
        mOrdersRecyclerView.setAdapter(ordersAdapter);
        mOrdersRecyclerView.setNestedScrollingEnabled(false);

        mNoOrders = (CardView) view.findViewById(R.id.no_orders);

        Button discoverProducts = (Button) view.findViewById(R.id.discover_products);
        discoverProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), HandPickedActivity.class);
                FilterClass.resetFilter();
                FilterClass.resetCategoryFilter();
                startActivity(intent);
                getActivity().finish();
            }
        });

        mNoOrders.setVisibility(View.INVISIBLE);
        mPageLoader.setVisibility(View.VISIBLE);
        mOrdersRecyclerView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mOrderArrayList.isEmpty()) {
            mLoadedFromServer = false;
            getActivity().getSupportLoaderManager().restartLoader(ORDERS_DB_LOADER, null, this);
            fetchDataFromServer();
        }

        IntentFilter intentFilter = new IntentFilter(getString(R.string.order_data_updated));
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mOrderServiceResponseReceiver, intentFilter);
        mListener.fragmentCreated("My Orders", true);
    }

    @Override
    public void onPause() {
        super.onPause();
        mOrderListViewState = mLayoutManager.onSaveInstanceState();
        try {
            LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mOrderServiceResponseReceiver);
        } catch (Exception e){

        }
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
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

    @Override
    public Loader<ArrayList<Order>> onCreateLoader(int id, Bundle args) {
        return new OrdersLoader(getContext(), -1, null, true, false, false, true);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Order>> loader, ArrayList<Order> data) {
        if (data != null && data.size() > 0 && mListener != null) {
            if (data.size() != mOrderArrayList.size()) {
                mPageLoader.setVisibility(View.INVISIBLE);
                mNoOrders.setVisibility(View.INVISIBLE);
                mOrdersRecyclerView.setVisibility(View.VISIBLE);
                setViewForOrders(data);
            }
        } else if (mLoadedFromServer && mListener != null) {
            mPageLoader.setVisibility(View.INVISIBLE);
            mOrdersRecyclerView.setVisibility(View.VISIBLE);
            mNoOrders.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Order>> loader) {

    }

    private void handleAPIResponse() {
        mLoadedFromServer = true;
        getActivity().getSupportLoaderManager().restartLoader(ORDERS_DB_LOADER, null, this);
    }

    private void setViewForOrders(ArrayList<Order> orders) {
        mOrderArrayList.clear();
        mOrderArrayList.addAll(orders);
        ordersAdapter.notifyDataSetChanged();

        if (mOrderListViewState != null) {
            mLayoutManager.onRestoreInstanceState(mOrderListViewState);
        }
    }

    private void fetchDataFromServer() {
        mLoadedFromServer = false;
        Intent intent = new Intent(getContext(), OrderService.class);
        intent.putExtra("TODO", R.string.fetch_orders);
        getContext().startService(intent);
    }

    @Override
    public void itemClicked(View view, int position, int id) {
        mListener.openOrderDetails(mOrderArrayList.get(position).getOrderID());
    }
}
