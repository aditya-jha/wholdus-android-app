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
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.activities.CategoryProductActivity;
import com.wholdus.www.wholdusbuyerapp.activities.HandPickedActivity;
import com.wholdus.www.wholdusbuyerapp.activities.NotificationActivity;
import com.wholdus.www.wholdusbuyerapp.adapters.CategoryHomePageAdapter;
import com.wholdus.www.wholdusbuyerapp.adapters.ProductHomePageAdapter;
import com.wholdus.www.wholdusbuyerapp.databaseContracts.CatalogContract;
import com.wholdus.www.wholdusbuyerapp.databaseHelpers.NotificationDBHelper;
import com.wholdus.www.wholdusbuyerapp.decorators.GridDividerItemDecoration;
import com.wholdus.www.wholdusbuyerapp.decorators.RecyclerViewSpaceItemDecoration;
import com.wholdus.www.wholdusbuyerapp.helperClasses.Constants;
import com.wholdus.www.wholdusbuyerapp.helperClasses.FilterClass;
import com.wholdus.www.wholdusbuyerapp.helperClasses.IntentFilters;
import com.wholdus.www.wholdusbuyerapp.helperClasses.NavDrawerHelper;
import com.wholdus.www.wholdusbuyerapp.interfaces.HomeListenerInterface;
import com.wholdus.www.wholdusbuyerapp.interfaces.ItemClickListener;
import com.wholdus.www.wholdusbuyerapp.loaders.CategoriesGridLoader;
import com.wholdus.www.wholdusbuyerapp.loaders.ProductsLoader;
import com.wholdus.www.wholdusbuyerapp.models.Category;
import com.wholdus.www.wholdusbuyerapp.models.Product;

import java.util.ArrayList;

import static com.wholdus.www.wholdusbuyerapp.R.id.help;
import static com.wholdus.www.wholdusbuyerapp.R.id.neft_radio_button;

/**
 * Created by aditya on 16/11/16.
 */

public class HomeFragment extends Fragment implements View.OnClickListener, ItemClickListener {

    private HomeListenerInterface mListener;

    private RecyclerView mProductsRecyclerView;
    private ArrayList<Product> mProducts;
    private ProductHomePageAdapter mProductHomePageAdapter;
    private ProductsLoaderManager mProductsLoader;

    private RecyclerView mCategoriesRecyclerView;
    private ArrayList<Category> mCategories;
    private CategoryHomePageAdapter mCategoryHomePageAdapter;
    private CategoryLoaderManager mCategoriesLoader;

    private static final int PRODUCTS_DB_LOADER = 901;
    private static final int CATEGORIES_DB_LOADER = 902;

    private BroadcastReceiver mCategoryServiceResponseReceiver;
    private ProgressBar mProgressBar;
    private TextView mNotificationCount;

    public HomeFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mListener = (HomeListenerInterface) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mCategoryServiceResponseReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                handleCategoryAPIResponse(intent);
            }
        };
        return inflater.inflate(R.layout.fragment_home, container, false);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mProgressBar = (ProgressBar) view.findViewById(R.id.loading_indicator);
        mProgressBar.setVisibility(View.VISIBLE);

        Button categoriesButton = (Button) view.findViewById(R.id.shortlist);
        categoriesButton.setOnClickListener(this);

        Button helpButton = (Button) view.findViewById(help);
        helpButton.setOnClickListener(this);

        Button notificationButton = (Button) view.findViewById(R.id.notification);
        notificationButton.setOnClickListener(this);

        Button handPickedViewAll = (Button) view.findViewById(R.id.hand_picked_view_all);
        handPickedViewAll.setOnClickListener(this);

        mProductsRecyclerView = (RecyclerView) view.findViewById(R.id.products_recycler_view);
        mProductsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mProducts = new ArrayList<>();
        mProductHomePageAdapter = new ProductHomePageAdapter(getContext(), mProducts, this);
        mProductsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        mProductsRecyclerView.addItemDecoration(new RecyclerViewSpaceItemDecoration(0, getResources().getDimensionPixelSize(R.dimen.text_divider_gap_small)));
        mProductsRecyclerView.setAdapter(mProductHomePageAdapter);

        mCategoriesRecyclerView = (RecyclerView) view.findViewById(R.id.categories_recycler_view);
        mCategoriesRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mCategories = new ArrayList<>();
        mCategoryHomePageAdapter = new CategoryHomePageAdapter(getContext(), mCategories, this);
        mCategoriesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mCategoriesRecyclerView.addItemDecoration(new GridDividerItemDecoration(
                ContextCompat.getDrawable(getContext(), R.drawable.divider),
                ContextCompat.getDrawable(getContext(), R.drawable.divider), 1));
        mCategoriesRecyclerView.setAdapter(mCategoryHomePageAdapter);
        //mProductsRecyclerView.setVerticalScrollBarEnabled(false);
        mCategoriesRecyclerView.setNestedScrollingEnabled(false);

        mNotificationCount = (TextView) view.findViewById(R.id.notification_count_text_view);
    }

    @Override
    public void itemClicked(View view, int position, int id) {
        final int ID = view.getId();
        Intent intent = new Intent(getContext(), HandPickedActivity.class);
        switch (ID) {
            case R.id.layout_product_home_page:
                ArrayList<Integer> productIDs = new ArrayList<>();
                productIDs.add(mProducts.get(position).getProductID());
                intent.putExtra(CatalogContract.ProductsTable.TABLE_NAME, productIDs);
                startActivity(intent);
                break;
            case R.id.category_name:
            case R.id.view_all_button:
                FilterClass.setCategoryID(mCategories.get(position).getCategoryID());
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mListener.fragmentCreated(getString(R.string.app_name), false);

        IntentFilter intentFilter = new IntentFilter(IntentFilters.CATEGORY_DATA);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mCategoryServiceResponseReceiver, intentFilter);

        mProductsLoader = new ProductsLoaderManager();
        getActivity().getSupportLoaderManager().restartLoader(PRODUCTS_DB_LOADER, null, mProductsLoader);

        mCategoriesLoader = new CategoryLoaderManager();
        getActivity().getSupportLoaderManager().restartLoader(CATEGORIES_DB_LOADER, null, mCategoriesLoader);

        NavDrawerHelper.getInstance().setOpenActivity(getActivity().getClass().getSimpleName());
        NavDrawerHelper.getInstance().setOpenFragment(this.getClass().getSimpleName());

        getNotificationCount();
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mCategoryServiceResponseReceiver);
        } catch (Exception e) {
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCategoryServiceResponseReceiver = null;
    }

    @Override
    public void onClick(View view) {
        final int ID = view.getId();
        switch (ID) {
            case R.id.shortlist:
                Intent shortlistIntent = new Intent(getContext(), CategoryProductActivity.class);
                shortlistIntent.putExtra(Constants.TYPE, Constants.FAV_PRODUCTS);
                startActivity(shortlistIntent);
                break;
            case help:
                mListener.helpButtonClicked();
                break;
            case R.id.notification:
                Intent intent = new Intent(getContext(), NotificationActivity.class);
                getContext().startActivity(intent);
                break;
            case R.id.hand_picked_view_all:
                FilterClass.resetFilter();
                FilterClass.resetCategoryFilter();
                startActivity(new Intent(getContext(), HandPickedActivity.class));
                break;
        }
    }

    public void setViewForProducts(final ArrayList<Product> products) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mProducts.clear();
                mProducts.addAll(products);
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mProductHomePageAdapter.notifyDataSetChanged();
                            mProgressBar.setVisibility(View.GONE);
                        }
                    });

                }
            }
        }).start();
    }

    public void setViewForCategories(final ArrayList<Category> categories) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<Category> categoriesToRemove = new ArrayList<>();
                for (Category category : categories) {
                    if (category.getProducts().size() < 10) {
                        categoriesToRemove.add(category);
                    }
                }
                categories.removeAll(categoriesToRemove);
                mCategories.clear();
                mCategories.addAll(categories);

                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mCategoryHomePageAdapter.notifyDataSetChanged();
                            mProgressBar.setVisibility(View.GONE);
                        }
                    });
                }
            }
        }).start();
    }

    private class ProductsLoaderManager implements LoaderManager.LoaderCallbacks<ArrayList<Product>> {

        @Override
        public void onLoaderReset(Loader<ArrayList<Product>> loader) {
        }

        @Override
        public void onLoadFinished(Loader<ArrayList<Product>> loader, ArrayList<Product> data) {
            if (data != null && data.size() > 0 && mProducts.isEmpty()) {
                setViewForProducts(data);
            }
        }


        @Override
        public Loader<ArrayList<Product>> onCreateLoader(final int id, Bundle args) {
            ArrayList<Integer> responseCodes = new ArrayList<>();
            responseCodes.add(0);
            // TODO : ?? Also add condition so that buyer product Id is not 0
            return new ProductsLoader(getContext(), null, null, responseCodes, null, 10);
        }
    }

    private class CategoryLoaderManager implements LoaderManager.LoaderCallbacks<ArrayList<Category>> {

        @Override
        public void onLoaderReset(Loader<ArrayList<Category>> loader) {
        }

        @Override
        public void onLoadFinished(Loader<ArrayList<Category>> loader, ArrayList<Category> data) {
            if (data != null && data.size() > 0 && mCategories.isEmpty()) {
                setViewForCategories(data);
                mProgressBar.setVisibility(View.GONE);
            }
        }

        @Override
        public Loader<ArrayList<Category>> onCreateLoader(final int id, Bundle args) {
            return new CategoriesGridLoader(getContext(), true);
        }
    }

    private void handleCategoryAPIResponse(Intent intent){
        Bundle extras = intent.getExtras();
        if (extras.getString(Constants.ERROR_RESPONSE) != null) {
        } else {
            int insertedUpdated = intent.getIntExtra(Constants.INSERTED_UPDATED, 0);
            if (insertedUpdated > 0 && mCategories.isEmpty()) {
                // new data available, restart the loader
                if (mCategoriesLoader == null){
                    mCategoriesLoader = new CategoryLoaderManager();
                }
                getActivity().getSupportLoaderManager().restartLoader(CATEGORIES_DB_LOADER, null, mCategoriesLoader);
            }
            if (insertedUpdated > 0 && mProducts.isEmpty()) {
                // new data available, restart the loader
                if (mProductsLoader == null){
                    mProductsLoader = new ProductsLoaderManager();
                }
                getActivity().getSupportLoaderManager().restartLoader(PRODUCTS_DB_LOADER, null, mProductsLoader);
            }
        }
    }

    private void getNotificationCount(){
        final NotificationDBHelper notificationDBHelper = new NotificationDBHelper(getContext());
        new Thread(new Runnable() {
            @Override
            public void run() {
                final int count = notificationDBHelper.getUnreadNotificationCount();
                try{
                    if (getActivity() != null){
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (count > 0) {
                                    mNotificationCount.setText(String.valueOf(count));
                                    mNotificationCount.setVisibility(View.VISIBLE);
                                } else {
                                    mNotificationCount.setVisibility(View.GONE);
                                }
                            }
                        });
                    }

                } catch (Exception e){

                }
            }
        }).start();
    }
}
