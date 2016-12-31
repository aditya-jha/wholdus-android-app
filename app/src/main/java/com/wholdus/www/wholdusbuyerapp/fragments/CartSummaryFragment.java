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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.activities.HomeActivity;
import com.wholdus.www.wholdusbuyerapp.adapters.SubCartAdapter;
import com.wholdus.www.wholdusbuyerapp.interfaces.CartListenerInterface;
import com.wholdus.www.wholdusbuyerapp.interfaces.ItemClickListener;
import com.wholdus.www.wholdusbuyerapp.loaders.CartLoader;
import com.wholdus.www.wholdusbuyerapp.models.Cart;
import com.wholdus.www.wholdusbuyerapp.models.SubCart;
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

    private TextView mOrderValueTextView;
    private TextView mShippingChargeTextView;
    private LinearLayout mTopSummary;
    private LinearLayout mNoProducts;
    private Button mContinueShopping;
    private ListView mSubOrderListView;

    private SubCartAdapter mSubCartAdapter;
    private ArrayList<SubCart> mSubCarts;


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
        mOrderValueTextView = (TextView) view.findViewById(R.id.cart_summary_order_value_text_view);
        mShippingChargeTextView = (TextView) view.findViewById(R.id.cart_summary_shipping_charge_text_view);
        mTopSummary = (LinearLayout) view.findViewById(R.id.top_summary);

        mContinueShopping = (Button) view.findViewById(R.id.continue_shopping_button);
        mContinueShopping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
        mNoProducts = (LinearLayout) view.findViewById(R.id.no_products);

        mSubOrderListView = (ListView) view.findViewById(R.id.cart_summary_suborder_list_view);
        mSubCarts = new ArrayList<>();
        mSubCartAdapter = new SubCartAdapter(getContext(), mSubCarts);
        mSubOrderListView.setAdapter(mSubCartAdapter);
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
        mNoProducts.setVisibility(View.GONE);
        mTopSummary.setVisibility(View.VISIBLE);

        mSubCarts.clear();
        mSubCarts.addAll(mCart.getSubCarts());
        mSubCartAdapter.notifyDataSetChanged();

        mListener.setCart(mCart);

        mShippingChargeTextView.setText("Rs. " +String.format("%.0f",mCart.getShippingCharge()));
        mOrderValueTextView.setText("Rs. " +String.format("%.0f",mCart.getCalculatedPrice()));

        mListener.disableProgressBar();
    }

    private void setViewForEmptyCart(){
        mTopSummary.setVisibility(View.GONE);
        mNoProducts.setVisibility(View.VISIBLE);
        mListener.disableProgressBar();
    }


    @Override
    public void onLoaderReset(Loader<Cart> loader) {

    }

    @Override
    public void onLoadFinished(Loader<Cart> loader, Cart data) {
        if (data!= null && data.getPieces() > 0){
            mCart = data;
            setViewForCart();
        }
        else {
            setViewForEmptyCart();
        }
    }

    @Override
    public Loader<Cart> onCreateLoader(int id, Bundle args) {
        return new  CartLoader(getContext(), -1, true, true,true,true);
    }
}
