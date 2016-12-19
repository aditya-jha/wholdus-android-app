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
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.wholdus.www.wholdusbuyerapp.activities.AccountActivity;
import com.wholdus.www.wholdusbuyerapp.activities.CheckoutActivity;
import com.wholdus.www.wholdusbuyerapp.activities.ProductDetailActivity;
import com.wholdus.www.wholdusbuyerapp.activities.StoreActivity;
import com.wholdus.www.wholdusbuyerapp.adapters.ProductsGridAdapter;
import com.wholdus.www.wholdusbuyerapp.databaseContracts.CatalogContract;
import com.wholdus.www.wholdusbuyerapp.helperClasses.APIConstants;
import com.wholdus.www.wholdusbuyerapp.helperClasses.Constants;
import com.wholdus.www.wholdusbuyerapp.helperClasses.FilterClass;
import com.wholdus.www.wholdusbuyerapp.interfaces.CategoryProductListenerInterface;
import com.wholdus.www.wholdusbuyerapp.interfaces.EndlessRecyclerViewScrollListener;
import com.wholdus.www.wholdusbuyerapp.interfaces.ItemClickListener;
import com.wholdus.www.wholdusbuyerapp.loaders.GridProductsLoader;
import com.wholdus.www.wholdusbuyerapp.models.GridProductModel;
import com.wholdus.www.wholdusbuyerapp.services.CatalogService;

import java.util.ArrayList;

/**
 * Created by aditya on 8/12/16.
 */

public class ProductsGridFragment extends Fragment implements LoaderManager.LoaderCallbacks<ArrayList<GridProductModel>>,
        View.OnClickListener, ItemClickListener {

    private CategoryProductListenerInterface mListener;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private String mFilters;
    private ArrayList<GridProductModel> mProducts;
    private ProductsGridAdapter mProductsGridAdapter;
    private RecyclerView mProductsRecyclerView;
    private EndlessRecyclerViewScrollListener mOnScrollListener;
    private BroadcastReceiver mReceiver;
    private GridLayoutManager mGridLayoutManager;

    private int mPageNumber, mTotalPages, mRecyclerViewPosition;

    private final int mLimit = 20;
    public static final int PRODUCTS_GRID_LOADER = 2;

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
        Log.d(this.getClass().getSimpleName(), "onattach");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mProducts == null) {
            mProducts = new ArrayList<>();
        } else {
            Toast.makeText(getContext(), "products already when created - " + mProducts.size(), Toast.LENGTH_SHORT).show();
        }

        mRecyclerViewPosition = 0;
        mPageNumber = 1;
        mTotalPages = -1;

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                handleOnBroadcastReceive(intent);
            }
        };

        Log.d(this.getClass().getSimpleName(), "oncreate");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_products_grid, container, false);
        setHasOptionsMenu(true);

        Button filterButton = (Button) rootView.findViewById(R.id.filter_button);
        filterButton.setOnClickListener(this);

        Button sortButton = (Button) rootView.findViewById(R.id.sort_button);
        sortButton.setOnClickListener(this);

        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(false);
                updateData();
            }
        });

        mProductsRecyclerView = (RecyclerView) rootView.findViewById(R.id.products_recycler_view);

        mGridLayoutManager = new GridLayoutManager(getContext(), 2);
        mGridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (mProductsGridAdapter.getItemViewType(position)) {
                    case 1:
                        return 2;
                    default:
                        return 1;
                }
            }
        });

        mProductsRecyclerView.setLayoutManager(mGridLayoutManager);

        mOnScrollListener = new EndlessRecyclerViewScrollListener(mGridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Toast.makeText(getContext(), "load more - " + page, Toast.LENGTH_SHORT).show();
                updateData();
            }
        };
        mProductsRecyclerView.addOnScrollListener(mOnScrollListener);

        Log.d(this.getClass().getSimpleName(), "onCreateview");
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (mProductsGridAdapter == null) {
            mProductsGridAdapter = new ProductsGridAdapter(getContext(), mProducts, this);
            if (mProducts.size() == 0) {
                updateData();
            } else {
                refreshData();
            }
        }
        mProductsRecyclerView.setAdapter(mProductsGridAdapter);

        Log.d(this.getClass().getSimpleName(), "onactivitycreated");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(this.getClass().getSimpleName(), "onstart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(this.getClass().getSimpleName(), "onresume");
        IntentFilter intentFilter = new IntentFilter(getString(R.string.category_data_updated));
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mReceiver, intentFilter);
        restoreRecyclerViewPosition();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(this.getClass().getSimpleName(), "onpause");
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mReceiver);
        saveRecyclerViewPosition();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(this.getClass().getSimpleName(), "onstop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(this.getClass().getSimpleName(), "onDestryview");
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
    public Loader<ArrayList<GridProductModel>> onCreateLoader(int id, Bundle args) {
        return new GridProductsLoader(getContext(), mPageNumber, mLimit);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<GridProductModel>> loader, final ArrayList<GridProductModel> data) {
        int oldPosition = mProducts.size();

        if (oldPosition != 0) {
            oldPosition--;
            mProducts.remove(oldPosition);
            mProductsGridAdapter.notifyItemRemoved(oldPosition);
        }

        if (data.size() != 0) {
            mProducts.addAll(data);
            mProducts.add(new GridProductModel());
            mProductsGridAdapter.notifyItemRangeInserted(oldPosition, data.size() + 1);
        } else {
            mTotalPages = 0;
        }

        Log.d("on load", mProducts.size() + "");
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<GridProductModel>> loader) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.filter_button:
                mListener.openFilter(true);
                break;
            case R.id.sort_button:
                SortBottomSheetFragment.newInstance().show(getChildFragmentManager(), "Sort");
                break;
        }
    }

    @Override
    public void itemClicked(int position, int id) {
        switch (id) {
            case R.id.share_image_view:
                /* TODO: handle share button click */
                break;
            case R.id.cart_image_view:
                /* TODO: handle cart button click */
                break;
            case R.id.fav_icon_image_view:
                /* TODO: handle fav button click */
                break;
            default:
                Intent intent = new Intent(getContext(), ProductDetailActivity.class);
                intent.putExtra(CatalogContract.ProductsTable.TABLE_NAME, mProducts.get(position).getProductID());
                startActivity(intent);
        }
    }

    public void updateData() {
        String filters = FilterClass.getFilterString();

        // if the filter is not same then set page number to 1 and clean current products
        // else increment pageNumber
        if (!filters.equals(mFilters)) {
            mFilters = filters;
            mPageNumber = 1;

            resetAdapterState();
        } else {
            mPageNumber++;
        }

        if (hasNextPage()) {
            fetchProductsFromServer();
            getActivity().getSupportLoaderManager().restartLoader(PRODUCTS_GRID_LOADER, null, this);
        }
    }

    private void fetchProductsFromServer() {
        Intent intent = new Intent(getContext(), CatalogService.class);
        intent.putExtra("TODO", R.integer.fetch_products);
        intent.putExtra("page_number", mPageNumber);
        intent.putExtra("items_per_page", mLimit);
        getActivity().startService(intent);
    }

    private void handleOnBroadcastReceive(final Intent intent) {
        String type = intent.getStringExtra("type");
        if (type.equals("ProductResponse")) {
            int pageNumber = intent.getIntExtra(APIConstants.API_PAGE_NUMBER_KEY, -1);
            int totalPages = intent.getIntExtra(APIConstants.API_TOTAL_PAGES_KEY, 1);
            int updatedInserted = intent.getIntExtra(Constants.INSERTED_UPDATED, 0);

            if (updatedInserted > 0) {
                if (mPageNumber > pageNumber && mPageNumber != 1) {
                            /* TODO: show button to refresh data */
                } else {
                    refreshData();
                }
            }
            mTotalPages = totalPages;
        }
    }

    private boolean hasNextPage() {
        return mTotalPages == -1 || mTotalPages > mPageNumber;
    }

    private void refreshData() {
        mPageNumber = 1;
        resetAdapterState();
        getActivity().getSupportLoaderManager().restartLoader(PRODUCTS_GRID_LOADER, null, this);
        Toast.makeText(getContext(), "Products updated", Toast.LENGTH_SHORT).show();
    }

    public void resetAdapterState() {
        int totalItems = mProducts.size();
        if (totalItems > 0) {
            mProducts.clear();
            mProductsGridAdapter.notifyItemRangeRemoved(0, totalItems);
        }
    }

    private void saveRecyclerViewPosition() {
        mRecyclerViewPosition = mGridLayoutManager.findFirstCompletelyVisibleItemPosition();
    }

    private void restoreRecyclerViewPosition() {
        mProductsRecyclerView.scrollToPosition(mRecyclerViewPosition);
    }
}
