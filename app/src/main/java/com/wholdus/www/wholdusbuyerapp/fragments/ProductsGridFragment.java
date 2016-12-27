package com.wholdus.www.wholdusbuyerapp.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.activities.CheckoutActivity;
import com.wholdus.www.wholdusbuyerapp.activities.ProductDetailActivity;
import com.wholdus.www.wholdusbuyerapp.activities.StoreActivity;
import com.wholdus.www.wholdusbuyerapp.adapters.ProductsGridAdapter;
import com.wholdus.www.wholdusbuyerapp.databaseContracts.CatalogContract;
import com.wholdus.www.wholdusbuyerapp.decorators.GridItemDecorator;
import com.wholdus.www.wholdusbuyerapp.helperClasses.APIConstants;
import com.wholdus.www.wholdusbuyerapp.helperClasses.Constants;
import com.wholdus.www.wholdusbuyerapp.helperClasses.EndlessRecyclerViewScrollListener;
import com.wholdus.www.wholdusbuyerapp.helperClasses.FilterClass;
import com.wholdus.www.wholdusbuyerapp.helperClasses.HelperFunctions;
import com.wholdus.www.wholdusbuyerapp.helperClasses.IntentFilters;
import com.wholdus.www.wholdusbuyerapp.helperClasses.ShareIntentClass;
import com.wholdus.www.wholdusbuyerapp.helperClasses.TODO;
import com.wholdus.www.wholdusbuyerapp.interfaces.CategoryProductListenerInterface;
import com.wholdus.www.wholdusbuyerapp.interfaces.ItemClickListener;
import com.wholdus.www.wholdusbuyerapp.loaders.GridProductsLoader;
import com.wholdus.www.wholdusbuyerapp.models.GridProductModel;
import com.wholdus.www.wholdusbuyerapp.services.BuyerProductService;
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
    private ProgressBar mPageLoader;
    private LinearLayout mPageLayout;
    private ViewGroup mRootView;
    private Snackbar mSnackbar;
    private boolean mLoaderLoading, mLoadMore;

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
        mProducts = new ArrayList<>();
        mRecyclerViewPosition = 0;
        mPageNumber = 1;
        mTotalPages = -1;
        mLoaderLoading = false;
        mLoadMore = false;

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                handleOnBroadcastReceive(intent);
            }
        };
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = (ViewGroup) inflater.inflate(R.layout.fragment_products_grid, container, false);
        setHasOptionsMenu(true);

        mPageLoader = (ProgressBar) mRootView.findViewById(R.id.page_loader);
        mPageLayout = (LinearLayout) mRootView.findViewById(R.id.page_layout);

        Button filterButton = (Button) mRootView.findViewById(R.id.filter_button);
        filterButton.setOnClickListener(this);

        Button sortButton = (Button) mRootView.findViewById(R.id.sort_button);
        sortButton.setOnClickListener(this);

        mSwipeRefreshLayout = (SwipeRefreshLayout) mRootView.findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                final int oldTotalPages = mTotalPages;
                refreshData();
                mTotalPages = oldTotalPages;
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        mProductsRecyclerView = (RecyclerView) mRootView.findViewById(R.id.products_recycler_view);
        mProductsRecyclerView.addItemDecoration(new GridItemDecorator(2, getResources().getDimensionPixelSize(R.dimen.card_margin_horizontal), true, 0));
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
                if (mLoaderLoading) {
                    mLoadMore = true;
                } else {
                    mLoadMore = false;
                    updateData();
                }
            }
        };
        mProductsRecyclerView.addOnScrollListener(mOnScrollListener);

        return mRootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (mProducts.size() > 0) {
            mPageLoader.setVisibility(View.INVISIBLE);
            mPageLayout.setVisibility(View.VISIBLE);
        } else {
            mPageLayout.setVisibility(View.INVISIBLE);
            mPageLoader.setVisibility(View.VISIBLE);
        }
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
        IntentFilter intentFilter = new IntentFilter(IntentFilters.PRODUCT_DATA);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mReceiver, intentFilter);

        restoreRecyclerViewPosition();
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mReceiver);
        saveRecyclerViewPosition();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        Log.d(this.getClass().getSimpleName(), "onstart");
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
        mLoaderLoading = true;
        return new GridProductsLoader(getContext(), mPageNumber, mLimit);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<GridProductModel>> loader, final ArrayList<GridProductModel> data) {
        mLoaderLoading = false;
        if (data.size() != 0) {
            if (mPageLoader.getVisibility() == View.VISIBLE) {
                mPageLoader.setVisibility(View.INVISIBLE);
                mPageLayout.setVisibility(View.VISIBLE);
            }
            final int oldPosition = mProducts.size();
            removeDummyObject();
            mProducts.addAll(data);
            if (mTotalPages == 1 || (mTotalPages == mPageNumber)) {
                mProductsGridAdapter.notifyItemRangeInserted(oldPosition, data.size());
            } else {
                mProducts.add(new GridProductModel());
                mProductsGridAdapter.notifyItemRangeInserted(oldPosition, data.size() + 1);
            }
        } else {
            removeDummyObject();
        }
        if (mLoadMore) {
            mLoadMore = false;
            updateData();
        }
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
    public void itemClicked(View view, int position, int id) {
        final int ID = id == -1 ? view.getId() : id;
        Intent intent;

        switch (ID) {
            case R.id.share_image_view:
                ShareIntentClass.shareImage(getContext(), (ImageView) view, mProducts.get(position).getName());
                break;
            case R.id.cart_image_view:
                if (mProducts.get(position).getCartCount() > 0) {
                    /* TODO: go to checkout */

                } else {
                    CartDialogFragment cartDialog = new CartDialogFragment();
                    Bundle args = new Bundle();
                    args.putInt(CatalogContract.ProductsTable.COLUMN_PRODUCT_ID, mProducts.get(position).getProductID());
                    cartDialog.setArguments(args);
                    cartDialog.show(getFragmentManager(), cartDialog.getClass().getSimpleName());
                }
                break;
            case R.id.fav_icon_image_view:
                GridProductModel product = mProducts.get(position);
                product.toggleLikeStatus();
                mProductsGridAdapter.notifyItemChanged(position, product);

                intent = new Intent(getContext(), BuyerProductService.class);
                intent.putExtra("TODO", TODO.UPDATE_PRODUCT_RESPONSE);
                intent.putExtra(CatalogContract.ProductsTable.COLUMN_PRODUCT_ID, product.getProductID());
                intent.putExtra(CatalogContract.ProductsTable.COLUMN_RESPONDED_FROM, 0);
                intent.putExtra(CatalogContract.ProductsTable.COLUMN_HAS_SWIPED, false);
                intent.putExtra(CatalogContract.ProductsTable.COLUMN_RESPONSE_CODE, product.getLikeStatus() ? 1 : 0);

                getContext().startService(intent);
                break;
            default:
                intent = new Intent(getContext(), ProductDetailActivity.class);
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
        Bundle extras = intent.getExtras();
        if (extras.getString(Constants.ERROR_RESPONSE) != null && mPageLoader.getVisibility() == View.VISIBLE) {
            mPageLoader.setVisibility(View.INVISIBLE);
            showErrorMessage();
            return;
        }
        final int pageNumber = intent.getIntExtra(APIConstants.API_PAGE_NUMBER_KEY, -1);
        final int totalPages = intent.getIntExtra(APIConstants.API_TOTAL_PAGES_KEY, 1);
        final int updatedInserted = intent.getIntExtra(Constants.INSERTED_UPDATED, 0);
        Log.d(this.getClass().getSimpleName(), "page number from api: " + pageNumber);
        Log.d(this.getClass().getSimpleName(), "total pages: " + totalPages);
        if (updatedInserted > 0) {
            if (mPageNumber == 1 && pageNumber == 1) {
                refreshData();
            } else if (mPageNumber > pageNumber){
                showSnackbarToUpdateUI(totalPages);
            } else if (mPageNumber == pageNumber) {
                getActivity().getSupportLoaderManager().restartLoader(PRODUCTS_GRID_LOADER, null, this);
                mPageNumber++;
            }
        }
        mTotalPages = totalPages;
        if (mTotalPages == 1) {
            removeDummyObject();
        }
    }

    private boolean hasNextPage() {
        return mTotalPages == -1 || mPageNumber <= mTotalPages;
    }

    /*
        Reset page number and total pages
        Remove all Items from Adapter and notify it
        Restart Loader
     */
    public void refreshData() {
        mPageNumber = 1;
        mTotalPages = -1;
        resetAdapterState();
        getActivity().getSupportLoaderManager().restartLoader(PRODUCTS_GRID_LOADER, null, this);
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

    private void showErrorMessage() {
        if (mSnackbar != null && mSnackbar.isShownOrQueued()) {
            mSnackbar.dismiss();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                String message;
                if (HelperFunctions.isNetworkAvailable(getContext())) {
                    message = getString(R.string.api_error_message);
                } else {
                    message = getString(R.string.no_internet_access);
                }
                mSnackbar = Snackbar.make(mRootView, message, Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.retry_text, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (mPageNumber == 1) {
                                    mPageLoader.setVisibility(View.VISIBLE);
                                }
                                fetchProductsFromServer();
                            }
                        });

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((TextView) mSnackbar.getView().findViewById(android.support.design.R.id.snackbar_text)).setTextColor(Color.WHITE);
                        mSnackbar.show();
                    }
                });
            }
        }).start();
    }

    private void showSnackbarToUpdateUI(final int totalPages) {
        if (mSnackbar != null && mSnackbar.isShownOrQueued()) {
            mSnackbar.dismiss();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {

                mSnackbar = Snackbar.make(mRootView, getString(R.string.new_products_message), Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.show_text, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                refreshData();
                                mTotalPages = totalPages;
                                getActivity().getSupportLoaderManager().restartLoader(PRODUCTS_GRID_LOADER, null, ProductsGridFragment.this);
                            }
                        });

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((TextView) mSnackbar.getView().findViewById(android.support.design.R.id.snackbar_text)).setTextColor(Color.WHITE);
                        mSnackbar.show();
                    }
                });
            }
        }).start();
    }

    private void removeDummyObject() {
        int oldPosition = mProducts.size();
        if (oldPosition != 0) {
            oldPosition--;
            mProducts.remove(oldPosition);
            mProductsGridAdapter.notifyItemRemoved(oldPosition);
        }
    }
}
