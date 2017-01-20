package com.wholdus.www.wholdusbuyerapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.adapters.SubOrderAdapter;
import com.wholdus.www.wholdusbuyerapp.helperClasses.HelperFunctions;
import com.wholdus.www.wholdusbuyerapp.interfaces.OrderDetailsListenerInterface;
import com.wholdus.www.wholdusbuyerapp.loaders.OrdersLoader;
import com.wholdus.www.wholdusbuyerapp.models.Order;
import com.wholdus.www.wholdusbuyerapp.models.Suborder;

import java.util.ArrayList;

/**
 * Created by kaustubh on 13/12/16.
 */

public class OrderDetailsFragment extends Fragment implements LoaderManager.LoaderCallbacks<ArrayList<Order>> {

    private OrderDetailsListenerInterface mListener;
    private int mOrderID;
    private Order mOrder;
    private static final int ORDERS_DB_LOADER = 20;

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
    private ProgressBar mPageLoader;
    private ViewGroup mPageLayout;

    public OrderDetailsFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mListener = (OrderDetailsListenerInterface) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mOrderID = getArguments().getInt("orderID", -1);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_order_details, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mPageLayout = (ViewGroup) view.findViewById(R.id.page_layout);
        mPageLoader = (ProgressBar) view.findViewById(R.id.page_loader);

        mOrderDate = (TextView) view.findViewById(R.id.order_date_text_view);
        mOrderStatus = (TextView) view.findViewById(R.id.order_status_text_view);
        mOrderProducts = (TextView) view.findViewById(R.id.order_details_products_text_view);
        mOrderPieces = (TextView) view.findViewById(R.id.order_details_pieces_text_view);
        mOrderValue = (TextView) view.findViewById(R.id.order_details_order_value_text_view);
        mCODCharge = (TextView) view.findViewById(R.id.order_details_cod_charge_text_view);
        mShippingCharge = (TextView) view.findViewById(R.id.order_details_shipping_charge_text_view);
        mFinalPrice = (TextView) view.findViewById(R.id.order_details_total_amount_text_view);
        mSubOrdersListView = (ListView) view.findViewById(R.id.suborder_list_view);
        mSuborders = new ArrayList<>();
        mSuborderAdapter = new SubOrderAdapter(getContext(), mSuborders);
        mSubOrdersListView.setAdapter(mSuborderAdapter);

        mPageLayout.setVisibility(View.INVISIBLE);

        getActivity().getSupportLoaderManager().restartLoader(ORDERS_DB_LOADER, null, this);
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

    @Override
    public Loader<ArrayList<Order>> onCreateLoader(int id, Bundle args) {
        return new OrdersLoader(getContext(), mOrderID, null, true, true, true, true);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Order>> loader, ArrayList<Order> data) {
        if (data != null && data.size() == 1) {
            mPageLoader.setVisibility(View.INVISIBLE);
            mPageLayout.setVisibility(View.VISIBLE);
            mOrder = data.get(0);
            setViewForOrders();
        }
        //TODO Handle case for no order ID
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Order>> loader) {

    }

    public void setViewForOrders() {
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
        HelperFunctions.setListViewHeightBasedOnChildren(mSubOrdersListView);
    }
}
