package com.wholdus.www.wholdusbuyerapp.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.activities.HandPickedActivity;
import com.wholdus.www.wholdusbuyerapp.activities.HomeActivity;
import com.wholdus.www.wholdusbuyerapp.adapters.SubCartAdapter;
import com.wholdus.www.wholdusbuyerapp.decorators.GridDividerItemDecoration;
import com.wholdus.www.wholdusbuyerapp.decorators.RecyclerViewSpaceItemDecoration;
import com.wholdus.www.wholdusbuyerapp.helperClasses.FilterClass;
import com.wholdus.www.wholdusbuyerapp.helperClasses.HelperFunctions;
import com.wholdus.www.wholdusbuyerapp.interfaces.CartListenerInterface;
import com.wholdus.www.wholdusbuyerapp.interfaces.CartSummaryListenerInterface;
import com.wholdus.www.wholdusbuyerapp.loaders.CartLoader;
import com.wholdus.www.wholdusbuyerapp.models.Cart;
import com.wholdus.www.wholdusbuyerapp.models.SubCart;
import com.wholdus.www.wholdusbuyerapp.services.CartService;

import java.util.ArrayList;

/**
 * Created by kaustubh on 26/12/16.
 */

public class CartSummaryFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cart>, CartSummaryListenerInterface {

    private CartListenerInterface mListener;
    private BroadcastReceiver mCartServiceResponseReceiver;
    private Cart mCart;

    private TextView mOrderValueTextView, mShippingChargeTextView, mNoProductsLeftTextView;
    private CardView mTopSummary, mNoProducts;
    private Button mContinueShopping;
    private RecyclerView mSubCartListView;

    private SubCartAdapter mSubCartAdapter;
    private ArrayList<SubCart> mSubCarts;

    private static final int CART_DB_LOADER = 90;

    public CartSummaryFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mListener = (CartListenerInterface) context;
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
    public void onResume() {
        super.onResume();
        mListener.fragmentCreated("Cart Summary", true);
        getActivity().getSupportLoaderManager().restartLoader(CART_DB_LOADER, null, this);

        IntentFilter intentFilter = new IntentFilter(getString(R.string.cart_data_updated));
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mCartServiceResponseReceiver, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mCartServiceResponseReceiver);
        } catch (Exception e) {
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCartServiceResponseReceiver = null;
    }

    public void initReferences(ViewGroup view) {
        mOrderValueTextView = (TextView) view.findViewById(R.id.cart_summary_order_value_text_view);
        mShippingChargeTextView = (TextView) view.findViewById(R.id.cart_summary_shipping_charge_text_view);
        mTopSummary = (CardView) view.findViewById(R.id.top_summary);

        mContinueShopping = (Button) view.findViewById(R.id.continue_shopping_button);
        mContinueShopping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), HandPickedActivity.class);
                FilterClass.resetFilter();
                FilterClass.resetCategoryFilter();
                startActivity(intent);
                getActivity().finish();
            }
        });

        mNoProducts = (CardView) view.findViewById(R.id.no_products);
        mNoProductsLeftTextView = (TextView) view.findViewById(R.id.no_products_left_text_view);

        mSubCartListView = (RecyclerView) view.findViewById(R.id.cart_summary_suborder_list_view);
        mSubCartListView.setItemAnimator(new DefaultItemAnimator());
        mSubCartListView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mSubCartListView.addItemDecoration(new RecyclerViewSpaceItemDecoration(getResources().getDimensionPixelSize(R.dimen.card_margin_vertical), 0));
        mSubCarts = new ArrayList<>();
        mSubCartAdapter = new SubCartAdapter(getContext(), mSubCarts, this);
        mSubCartListView.setAdapter(mSubCartAdapter);
        mSubCartListView.setNestedScrollingEnabled(false);
    }

    @Override
    public void enableProgressBar() {
        mListener.enableProgressBar();
    }

    private void fetchDataFromServer() {
        Intent intent = new Intent(getContext(), CartService.class);
        intent.putExtra("TODO", R.string.post_cart_item);
        getContext().startService(intent);
        Intent cartServiceIntent = new Intent(getContext(), CartService.class);
        cartServiceIntent.putExtra("TODO", R.string.fetch_cart);
        getContext().startService(cartServiceIntent);
    }

    private void handleAPIResponse() {
        if (getActivity() != null) {
            getActivity().getSupportLoaderManager().restartLoader(CART_DB_LOADER, null, this);
        }
    }

    private void setViewForCart() {
        mNoProducts.setVisibility(View.GONE);
        mTopSummary.setVisibility(View.VISIBLE);

        mSubCarts.clear();
        mSubCarts.addAll(mCart.getSubCarts());
        setBasicCartView();

        mShippingChargeTextView.setText(String.format(getString(R.string.price_format), String.valueOf((int) Math.ceil(mCart.getShippingCharge()))));
        mOrderValueTextView.setText(String.format(getString(R.string.price_format), String.valueOf((int) Math.ceil(mCart.getCalculatedPrice()))));

    }

    private void setViewForEmptyCart() {
        mTopSummary.setVisibility(View.GONE);
        mNoProducts.setVisibility(View.VISIBLE);
        mListener.disableProgressBar();

        mSubCarts.clear();
        setBasicCartView();

        mShippingChargeTextView.setText("");
        mOrderValueTextView.setText("");

    }

    private void setViewForUnsyncedCart() {
        fetchDataFromServer();

        mTopSummary.setVisibility(View.GONE);
        mNoProducts.setVisibility(View.VISIBLE);
        mContinueShopping.setVisibility(View.GONE);
        mNoProductsLeftTextView.setText("You do not seem to have an active internet connection.\nPlease connect to the internet and try again");

        mSubCarts.clear();
        setBasicCartView();

        mShippingChargeTextView.setText("");
        mOrderValueTextView.setText("");

    }

    private void setBasicCartView() {
        mSubCartAdapter.notifyDataSetChanged();
        //HelperFunctions.setListViewHeightBasedOnChildren(mSubCartListView);

        mListener.setCart(mCart);

        mListener.disableProgressBar();
    }


    @Override
    public void onLoaderReset(Loader<Cart> loader) {
    }

    @Override
    public void onLoadFinished(Loader<Cart> loader, Cart data) {
        boolean cartChanged = (mCart == null || data == null || (mCart.getPieces() != data.getPieces()));
        mCart = data;
        if (data != null) {
            if (data.getSynced() == 1) {
                if (data.getPieces() > 0) {
                    if (cartChanged) {
                        setViewForCart();
                    }
                } else {
                    setViewForEmptyCart();
                }
            } else {
                setViewForUnsyncedCart();
            }
        } else {
            setViewForEmptyCart();
        }
    }

    @Override
    public Loader<Cart> onCreateLoader(int id, Bundle args) {
        return new CartLoader(getContext(), -1, true, true, true, true);
    }
}
