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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.interfaces.CartListenerInterface;
import com.wholdus.www.wholdusbuyerapp.loaders.CartLoader;
import com.wholdus.www.wholdusbuyerapp.models.Cart;
import com.wholdus.www.wholdusbuyerapp.services.CartService;

import java.util.ArrayList;

/**
 * Created by kaustubh on 26/12/16.
 */

public class CartSummaryFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cart> {

    private CartListenerInterface mListener;
    private final int CART_DB_LOADER = 90;
    private BroadcastReceiver mCartServiceResponseReceiver;
    private Cart mCart;

    private TextView mTotalTextView;
    private TextView mProductsPiecesTextView;
    private TextView mOrderValueTextView;
    private TextView mShippingChargeTextView;
    private Button mProceedButton;

    public CartSummaryFragment(){

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
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_cart_summary, container, false);
        initReferences(rootView);
        mCartServiceResponseReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                handleAPIResponse();
            }
        };
        fetchDataFromServer();
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume(){
        super.onResume();
        mListener.fragmentCreated("Cart Summary", true);
        getActivity().getSupportLoaderManager().restartLoader(CART_DB_LOADER, null, this);

        IntentFilter intentFilter = new IntentFilter(getString(R.string.cart_data_updated));
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mCartServiceResponseReceiver, intentFilter);

    }

    public void initReferences(ViewGroup view){

        mTotalTextView = (TextView) view.findViewById(R.id.cart_summary_total_price_text_view);
        mOrderValueTextView = (TextView) view.findViewById(R.id.cart_summary_order_value_text_view);
        mShippingChargeTextView = (TextView) view.findViewById(R.id.cart_summary_shipping_charge_text_view);
        mProductsPiecesTextView = (TextView) view.findViewById(R.id.cart_summary_total_products_text_view);
        mTotalTextView = (TextView) view.findViewById(R.id.cart_summary_total_price_text_view);
        mProceedButton = (Button) view.findViewById(R.id.cart_summary_proceed_button);
        mProceedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Proceed button clicked", Toast.LENGTH_SHORT).show();
            }
        });
        mProceedButton.setEnabled(false);
    }

    private void fetchDataFromServer(){
        Intent cartServiceIntent = new Intent(getContext(), CartService.class);
        cartServiceIntent.putExtra("TODO", R.string.fetch_cart);
        getContext().startService(cartServiceIntent);
    }

    private void handleAPIResponse() {
        if (getActivity()!= null) {
            getActivity().getSupportLoaderManager().restartLoader(CART_DB_LOADER, null, this);
        }
    }

    private void setViewForCart(){
        mTotalTextView.setText("Total: Rs. " + String.format("%.0f",mCart.getFinalPrice()));
        mProductsPiecesTextView.setText(String.valueOf(mCart.getProductCount()) + " products - "
                + String.valueOf(mCart.getPieces()) + " pieces");
        mShippingChargeTextView.setText("Rs. " +String.format("%.0f",mCart.getShippingCharge()));
        mOrderValueTextView.setText("Rs. " +String.format("%.0f",mCart.getCalculatedPrice()));
        mProceedButton.setEnabled(true);
    }


    @Override
    public void onLoaderReset(Loader<Cart> loader) {

    }

    @Override
    public void onLoadFinished(Loader<Cart> loader, Cart data) {
        if (data!= null){
            mCart = data;
            setViewForCart();
        }
    }

    @Override
    public Loader<Cart> onCreateLoader(int id, Bundle args) {
        return new  CartLoader(getContext(), -1, true, true,true,true);
    }
}
