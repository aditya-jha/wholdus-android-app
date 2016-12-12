package com.wholdus.www.wholdusbuyerapp.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.activities.CheckoutActivity;
import com.wholdus.www.wholdusbuyerapp.activities.StoreActivity;
import com.wholdus.www.wholdusbuyerapp.interfaces.CategoryProductListenerInterface;
import com.wholdus.www.wholdusbuyerapp.models.BuyerProduct;

/**
 * Created by aditya on 8/12/16.
 */

public class ProductsGridFragment extends Fragment implements LoaderManager.LoaderCallbacks<BuyerProduct>, View.OnClickListener {

    private CategoryProductListenerInterface mListener;

    public ProductsGridFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (CategoryProductListenerInterface) context;
        } catch (ClassCastException cee) {
            Log.e(getClass().getSimpleName(), " must implement " + CategoryProductListenerInterface.class.getSimpleName());
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_products_grid, container, false);
        setHasOptionsMenu(true);

        Button filteButton = (Button) rootView.findViewById(R.id.filter_button);
        filteButton.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
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
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.default_action_buttons, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_bar_checkout:
                startActivity(new Intent(getContext(), CheckoutActivity.class));
                break;
            case R.id.action_bar_store_home:
                startActivity(new Intent(getContext(), StoreActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<BuyerProduct> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<BuyerProduct> loader, BuyerProduct data) {
    }

    @Override
    public void onLoaderReset(Loader<BuyerProduct> loader) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.filter_button:
                mListener.openFilter();
        }
    }

    public void refreshData() {
        Log.d(getClass().getSimpleName(), "refresh data");
    }
}
