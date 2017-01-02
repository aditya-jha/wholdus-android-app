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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.vision.text.Text;
import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.adapters.SubOrderAdapter;
import com.wholdus.www.wholdusbuyerapp.helperClasses.HelperFunctions;
import com.wholdus.www.wholdusbuyerapp.interfaces.OrderDetailsListenerInterface;
import com.wholdus.www.wholdusbuyerapp.interfaces.ProfileListenerInterface;
import com.wholdus.www.wholdusbuyerapp.loaders.OrdersLoader;
import com.wholdus.www.wholdusbuyerapp.models.Order;
import com.wholdus.www.wholdusbuyerapp.models.Suborder;

import java.util.ArrayList;

/**
 * Created by kaustubh on 13/12/16.
 */

public class OrderDetailsFragment extends Fragment {

    private OrderDetailsListenerInterface mListener;
    private int mOrderID;
    private Order mOrder;
    private final int ORDERS_DB_LOADER = 20;
    private OrderLoaderManager mOrderLoader;

    private TextView mOrderDate;
    private TextView mOrderStatus;
    private TextView mOrderPieces;
    private TextView mOrderProducts;
    private TextView mOrderValue;
    private TextView mCODCharge;
    private TextView mShippingCharge;
    private TextView mFinalPrice;
    private ListView mSubOrdersListView;
    private ArrayList<Suborder> mSuborders;
    private SubOrderAdapter mSuborderAdapter;

    public OrderDetailsFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (OrderDetailsListenerInterface) context;
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
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_order_details, container, false);
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
        mOrderDate = (TextView) rootView.findViewById(R.id.order_date_text_view);
        mOrderStatus = (TextView) rootView.findViewById(R.id.order_status_text_view);
        mOrderProducts = (TextView) rootView.findViewById(R.id.order_details_products_text_view);
        mOrderPieces = (TextView) rootView.findViewById(R.id.order_details_pieces_text_view);
        mOrderValue = (TextView) rootView.findViewById(R.id.order_details_order_value_text_view);
        mCODCharge = (TextView) rootView.findViewById(R.id.order_details_cod_charge_text_view);
        mShippingCharge = (TextView) rootView.findViewById(R.id.order_details_shipping_charge_text_view);
        mFinalPrice = (TextView) rootView.findViewById(R.id.order_details_total_amount_text_view);
        mSubOrdersListView = (ListView) rootView.findViewById(R.id.suborder_list_view);
        mSuborders = new ArrayList<>();
        mSuborderAdapter = new SubOrderAdapter(getContext(), mSuborders);
        mSubOrdersListView.setAdapter(mSuborderAdapter);
        //mSubOrdersListView.setVerticalScrollBarEnabled(false);
        //mSubOrdersListView.setScrollContainer(false);
        //mSubOrdersListView.setClickable(false);
        //HelperFunctions.setListViewHeightBasedOnChildren(mSubOrdersListView);
    }

    public void setViewForOrders(){
        mListener.fragmentCreated(getString(R.string.order_card_order_number) + mOrder.getDisplayNumber(), true);

        mOrderDate.setText(HelperFunctions.getDateFromString(mOrder.getCreatedAt()));
        mOrderStatus.setText(mOrder.getOrderStatusDisplay());
        mOrderProducts.setText(String.valueOf(mOrder.getProductCount()));
        mOrderPieces.setText(String.valueOf(mOrder.getPieces()));
        mOrderValue.setText("Rs. " + String.format("%.0f", mOrder.getCalculatedPrice()));
        mCODCharge.setText("Rs. " + String.format("%.0f", mOrder.getCODCharge()));
        mShippingCharge.setText("Rs. " + String.format("%.0f", mOrder.getShippingCharge()));
        mFinalPrice.setText("Rs. " + String.format("%.0f", mOrder.getFinalPrice()));

        mSuborders.clear();
        mSuborders.addAll(mOrder.getSuborders());
        mSuborderAdapter.notifyDataSetChanged();
    }

    private class OrderLoaderManager implements LoaderManager.LoaderCallbacks<ArrayList<Order>> {

        @Override
        public void onLoaderReset(Loader<ArrayList<Order>> loader) {
        }

        @Override
        public void onLoadFinished(Loader<ArrayList<Order>> loader, ArrayList<Order> data) {
            if (data.size() == 1) {
                mOrder= data.get(0);
                setViewForOrders();
            }
        }


        @Override
        public Loader<ArrayList<Order>> onCreateLoader(final int id, Bundle args) {
            return new OrdersLoader(getContext(), mOrderID, null, true, true, true, true);
        }
    }
}
