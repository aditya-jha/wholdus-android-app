package com.wholdus.www.wholdusbuyerapp.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.vision.text.Line;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.activities.HandPickedActivity;
import com.wholdus.www.wholdusbuyerapp.adapters.ProductHomePageAdapter;
import com.wholdus.www.wholdusbuyerapp.databaseContracts.CatalogContract;
import com.wholdus.www.wholdusbuyerapp.decorators.GridItemDecorator;
import com.wholdus.www.wholdusbuyerapp.decorators.RecyclerViewSpaceItemDecoration;
import com.wholdus.www.wholdusbuyerapp.interfaces.HomeListenerInterface;
import com.wholdus.www.wholdusbuyerapp.interfaces.ItemClickListener;
import com.wholdus.www.wholdusbuyerapp.loaders.ProductsLoader;
import com.wholdus.www.wholdusbuyerapp.models.Product;

import java.util.ArrayList;

/**
 * Created by aditya on 16/11/16.
 */

public class HomeFragment extends Fragment {

    private HomeListenerInterface mListener;

    private FirebaseAnalytics mFirebaseAnalytics;

    private RecyclerView mProductsRecyclerView;
    private ArrayList<Product> mProducts;
    private ProductHomePageAdapter mProductHomePageAdapter;
    private ProductItemClickListener mProductItemClickListener;
    private LinearLayoutManager mProductsLayoutManager;
    private ProductsLoaderManager mProductsLoader;
    private final int PRODUCTS_DB_LOADER = 901;

    public HomeFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (HomeListenerInterface) context;
        } catch (ClassCastException cee) {
            Log.e(this.getClass().getSimpleName(), " must implemet " + HomeListenerInterface.class.getSimpleName());
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_home, container, false);

        Button button = (Button) rootView.findViewById(R.id.categories);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Bundle bundle = new Bundle();
                        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "Button");
                        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "categories button");
                        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                    }
                }).start();
                mListener.openCategory(-1);
            }
        });
        mProductsRecyclerView = (RecyclerView) rootView.findViewById(R.id.products_recycler_view);
        //TODO : If necessary set horizontal layout manager
        mProductsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mProducts = new ArrayList<>();
        mProductItemClickListener = new ProductItemClickListener();
        mProductHomePageAdapter = new ProductHomePageAdapter(getContext(), mProducts,mProductItemClickListener);
        mProductsLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mProductsRecyclerView.setLayoutManager(mProductsLayoutManager);
        mProductsRecyclerView.addItemDecoration(new RecyclerViewSpaceItemDecoration(0, getResources().getDimensionPixelSize(R.dimen.card_margin_horizontal)));
        mProductsRecyclerView.setAdapter(mProductHomePageAdapter);
        return rootView;
    }

    private class ProductItemClickListener implements ItemClickListener{
        @Override
        public void itemClicked(View view, int position, int id) {
            Intent intent = new Intent(getContext(), HandPickedActivity.class);
            ArrayList<Integer> productIDs = new ArrayList<>();
            productIDs.add(mProducts.get(position).getProductID());
            intent.putExtra(CatalogContract.ProductsTable.TABLE_NAME,productIDs);
            startActivity(intent);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mListener.fragmentCreated(getString(R.string.app_name), false);
        mProductsLoader = new ProductsLoaderManager();
        getActivity().getSupportLoaderManager().restartLoader(PRODUCTS_DB_LOADER, null, mProductsLoader);
    }

    public void setViewForProducts(ArrayList<Product> products){
        mProducts.clear();
        mProducts.addAll(products);
        mProductHomePageAdapter.notifyDataSetChanged();
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
            ArrayList<Integer> responseCodes = new ArrayList<>();
            responseCodes.add(0);
            // TODO : ?? Also add condition so that buyer product Id is not 0
            return new ProductsLoader(getContext(), null, null, responseCodes, null, 10);
        }
    }
}
