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

import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.interfaces.HandPickedListenerInterface;
import com.wholdus.www.wholdusbuyerapp.loaders.ProductsLoader;
import com.wholdus.www.wholdusbuyerapp.models.Buyer;
import com.wholdus.www.wholdusbuyerapp.models.Product;
import com.wholdus.www.wholdusbuyerapp.services.BuyerProductService;

import java.util.ArrayList;

/**
 * Created by kaustubh on 17/12/16.
 */

public class HandPickedFragment extends Fragment {

    private HandPickedListenerInterface mListener;
    private final int PRODUCTS_DB_LOADER = 30;
    private BroadcastReceiver mProductServiceResponseReceiver;
    private ProductsLoaderManager mProductsLoader;
    ArrayList<Product> mProductsArrayList;

    public HandPickedFragment(){
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (HandPickedListenerInterface) context;
        } catch (ClassCastException cee) {
            cee.printStackTrace();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mProductServiceResponseReceiver = new BroadcastReceiver() {
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
    public void onResume(){
        super.onResume();

        mProductsLoader = new ProductsLoaderManager();
        getActivity().getSupportLoaderManager().restartLoader(PRODUCTS_DB_LOADER, null, mProductsLoader);

        IntentFilter intentFilter = new IntentFilter(getString(R.string.buyer_product_data_updated));
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mProductServiceResponseReceiver, intentFilter);

        mListener.fragmentCreated("Hand Picked Fragment");
    }

    private void handleAPIResponse() {
        getActivity().getSupportLoaderManager().restartLoader(PRODUCTS_DB_LOADER, null, mProductsLoader);
    }

    private void initReferences(ViewGroup rootView){

    }

    private void fetchDataFromServer(){
        Intent intent = new Intent(getContext(), BuyerProductService.class);
        intent.putExtra("TODO", R.string.fetch_buyer_products);
        getContext().startService(intent);
    }

    private void setViewForProducts(ArrayList<Product> data){

    }

    private class ProductsLoaderManager implements LoaderManager.LoaderCallbacks<ArrayList<Product>> {

        @Override
        public void onLoaderReset(Loader<ArrayList<Product>> loader) {
        }

        @Override
        public void onLoadFinished(Loader<ArrayList<Product>> loader, ArrayList<Product> data) {
            setViewForProducts(data);
        }


        @Override
        public Loader<ArrayList<Product>> onCreateLoader(final int id, Bundle args) {
            return new ProductsLoader(getContext());
        }
    }
}
