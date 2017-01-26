package com.wholdus.www.wholdusbuyerapp.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.activities.CartActivity;
import com.wholdus.www.wholdusbuyerapp.activities.CategoryProductActivity;
import com.wholdus.www.wholdusbuyerapp.activities.ProductDetailActivity;
import com.wholdus.www.wholdusbuyerapp.adapters.ProductsGridAdapter;
import com.wholdus.www.wholdusbuyerapp.databaseContracts.CatalogContract;
import com.wholdus.www.wholdusbuyerapp.decorators.GridDividerItemDecoration;
import com.wholdus.www.wholdusbuyerapp.helperClasses.APIConstants;
import com.wholdus.www.wholdusbuyerapp.helperClasses.CartMenuItemHelper;
import com.wholdus.www.wholdusbuyerapp.helperClasses.Constants;
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
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by aditya on 8/12/16.
 */

public class ProductsGridFragment extends Fragment implements LoaderManager.LoaderCallbacks<ArrayList<GridProductModel>>,
        View.OnClickListener, ItemClickListener, SwipeRefreshLayout.OnRefreshListener {

    private CategoryProductListenerInterface mListener;
    private ArrayList<GridProductModel> mProducts;
    private ProductsGridAdapter mAdapter;
    private BroadcastReceiver mReceiver;
    private ProgressBar mPageLoader;
    private LinearLayout mPageLayout;
    private CardView mNoProducts;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private GridLayoutManager mGridLayoutManager;
    private Snackbar mSnackbar;
    private Queue<Integer> mRequestQueue;
    private HashSet<Integer> mPagesLoaded;
    private LinearLayout mSortFilterLayout;

    private CartMenuItemHelper mCartMenuItemHelper;

    private String mFilters;
    private int mTotalPages, mRecyclerViewPosition, mActivePageCall, mTotalProductsOnServer;
    private boolean mSortFilterVisible = true;
    private boolean mLoaderLoading, mLoadMoreData;

    Animation mSlideInBottom, mSlideOutBottom;
    private int mScrollingCounter1 = 0, mScrollingCounter2 = 1;

    private ArrayList<Integer> mResponseCodes;

    private final int PRODUCTS_GRID_LOADER = 2;
    private final int mLIMIT = 50;

    public ProductsGridFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mListener = (CategoryProductListenerInterface) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initResponseCodes(getArguments());
        resetVariables();
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, final Intent intent) {
                handleOnBroadcastReceive(intent);
            }
        };

        if (mResponseCodes.size() == 1) {
            IntentFilter bpIntentFilter = new IntentFilter(getString(R.string.buyer_product_data_updated));
            LocalBroadcastManager.getInstance(getContext()).registerReceiver(mReceiver, bpIntentFilter);
        } else {
            IntentFilter intentFilter = new IntentFilter(IntentFilters.PRODUCT_DATA);
            LocalBroadcastManager.getInstance(getContext()).registerReceiver(mReceiver, intentFilter);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_products_grid, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mListener.showMenuButtonInToolbar();
        mListener.filterFragmentActive(false);

        setVisibility(View.VISIBLE, View.INVISIBLE, View.INVISIBLE);

        if (mAdapter == null) {
            mAdapter = new ProductsGridAdapter(getContext(), mProducts, this);
            if (mProducts.size() != 0) {
                resetVariables();
            }
        }

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int activePage = (int) Math.ceil((mGridLayoutManager.findLastVisibleItemPosition() + mLIMIT + 0.0) / mLIMIT);
                if (activePage > 0 && !mPagesLoaded.contains(activePage)) {
                    if (mRequestQueue.size() > 0 && mRequestQueue.element() == activePage) {
                        return;
                    }
                    mRequestQueue.add(activePage);
                    fetchProductsFromServer();
                }
                if (dy > 0) {
                    hideSortFilterLayout();
                    mScrollingCounter1 += 1;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (mScrollingCounter1 == mScrollingCounter2) {
                                showSortFilterLayout();
                            }
                            mScrollingCounter2 += 1;
                        }
                    }, 700);
                } else {
                    showSortFilterLayout();
                }
            }
        });


        if (mProducts.size() > 0) {
            setVisibility(View.INVISIBLE, View.VISIBLE, View.INVISIBLE);
        }
        loadData();
    }

    private void hideSortFilterLayout() {
        if (mSortFilterVisible) {
            mSortFilterLayout.startAnimation(mSlideOutBottom);
            mSortFilterLayout.setVisibility(View.GONE);
            mSortFilterVisible = false;
        }
    }

    private void showSortFilterLayout() {
        if (!mSortFilterVisible) {
            mSortFilterLayout.startAnimation(mSlideInBottom);
            mSortFilterVisible = true;
        }
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mPageLoader = (ProgressBar) view.findViewById(R.id.page_loader);
        mPageLayout = (LinearLayout) view.findViewById(R.id.page_layout);

        Button filterButton = (Button) view.findViewById(R.id.filter_button);
        filterButton.setOnClickListener(this);

        Button filterButtonNoProducts = (Button) view.findViewById(R.id.filter_button_no_products);
        filterButtonNoProducts.setOnClickListener(this);

        Button sortButton = (Button) view.findViewById(R.id.sort_button);
        sortButton.setOnClickListener(this);

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.products_recycler_view);
        mGridLayoutManager = new GridLayoutManager(getContext(), 2);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mRecyclerView.addItemDecoration(new GridDividerItemDecoration(
                ContextCompat.getDrawable(getContext(), R.drawable.divider),
                ContextCompat.getDrawable(getContext(), R.drawable.divider), 2));
        mRecyclerView.setHasFixedSize(true);

        mNoProducts = (CardView) view.findViewById(R.id.no_products);

        mSortFilterLayout = (LinearLayout) view.findViewById(R.id.sort_filter_sheet);
        mSlideInBottom = AnimationUtils.loadAnimation(getContext(), R.anim.bottom_sheet_in_bottom);
        mSlideInBottom.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mSortFilterLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mSlideOutBottom = AnimationUtils.loadAnimation(getContext(), R.anim.bottom_sheet_out_bottom);
    }

    @Override
    public void onResume() {
        super.onResume();

        // restore recycler view position
        mGridLayoutManager.scrollToPosition(mRecyclerViewPosition);

        dismissDialog();
    }

    @Override
    public void onPause() {
        super.onPause();
        // save recycler view position
        mRecyclerViewPosition = mGridLayoutManager.findFirstVisibleItemPosition();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mReceiver);
        } catch (Exception e){

        }
        mReceiver = null;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.default_action_buttons, menu);
        mCartMenuItemHelper = new CartMenuItemHelper(getContext(), menu.findItem(R.id.action_bar_checkout), getActivity().getSupportLoaderManager());
        mCartMenuItemHelper.restartLoader();
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_bar_checkout:
                startActivity(new Intent(getContext(), CartActivity.class));
                break;
            case R.id.action_bar_shortlist:
                if (mResponseCodes.size() != 1) {
                    Intent shortlistIntent = new Intent(getContext(), CategoryProductActivity.class);
                    shortlistIntent.putExtra(Constants.TYPE, Constants.FAV_PRODUCTS);
                    getContext().startActivity(shortlistIntent);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<ArrayList<GridProductModel>> onCreateLoader(int id, Bundle args) {
        mLoaderLoading = true;
        return new GridProductsLoader(getContext(), mResponseCodes);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<GridProductModel>> loader, final ArrayList<GridProductModel> data) {
        int firstVisible = -1, lastVisible = -1;
        if (mGridLayoutManager != null) {
            lastVisible = mGridLayoutManager.findLastCompletelyVisibleItemPosition();
            firstVisible = mGridLayoutManager.findFirstVisibleItemPosition();
        }

        if (data != null && data.size() > 0) {
            int diff = 0, runCount = mProducts.size();
            if (runCount > 0 && mProducts.get(runCount - 1) == null) runCount--;

            while (diff < runCount && diff < data.size()) {
                if (mProducts.get(diff).getProductID() != data.get(diff).getProductID()) {
                    break;
                }
                diff++;
            }

            final int diffIndex = diff;

            if (diffIndex >= firstVisible || (mProducts.size() >= lastVisible && lastVisible >= 0 && mProducts.get(lastVisible) == null)) {
                mAdapter.clear();
                mAdapter.add(data);

                if (mTotalProductsOnServer == -1 || mProducts.size() < mTotalProductsOnServer) {
                    mProducts.add(null);
                    mAdapter.notifyDataSetChanged();
                }

                if (mPageLoader.getVisibility() == View.VISIBLE || mNoProducts.getVisibility() == View.VISIBLE) {
                    setVisibility(View.INVISIBLE, View.VISIBLE, View.INVISIBLE);
                }

                if (firstVisible >= 0) {
                    View firstVisibleView = mGridLayoutManager.findViewByPosition(firstVisible);
                    mGridLayoutManager.scrollToPositionWithOffset(firstVisible, firstVisibleView.getTop());
                }

                mLoaderLoading = false;

                if (mLoadMoreData) {
                    getActivity().getSupportLoaderManager().restartLoader(PRODUCTS_GRID_LOADER, null, ProductsGridFragment.this);
                    mLoadMoreData = false;
                }
            } else {
                mLoaderLoading = false;
                showSnackbarToUpdateUI(mTotalPages);
            }
        } else {
            mLoaderLoading = false;
            if (mLoadMoreData) {
                getActivity().getSupportLoaderManager().restartLoader(PRODUCTS_GRID_LOADER, null, ProductsGridFragment.this);
                mLoadMoreData = false;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<GridProductModel>> loader) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.filter_button:
            case R.id.filter_button_no_products:
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
                CartDialogFragment cartDialog = new CartDialogFragment();
                Bundle args = new Bundle();
                args.putInt(CatalogContract.ProductsTable.COLUMN_PRODUCT_ID, mProducts.get(position).getProductID());
                cartDialog.setArguments(args);
                cartDialog.show(getFragmentManager(), cartDialog.getClass().getSimpleName());
                break;
            case R.id.fav_icon_image_view:
                GridProductModel product = mProducts.get(position);
                product.toggleLikeStatus();
                mAdapter.notifyItemChanged(position, product);

                intent = new Intent(getContext(), BuyerProductService.class);
                intent.putExtra("TODO", TODO.UPDATE_PRODUCT_RESPONSE);
                intent.putExtra(CatalogContract.ProductsTable.COLUMN_PRODUCT_ID, product.getProductID());
                intent.putExtra(CatalogContract.ProductsTable.COLUMN_RESPONDED_FROM, 1);
                intent.putExtra(CatalogContract.ProductsTable.COLUMN_HAS_SWIPED, false);
                intent.putExtra(CatalogContract.ProductsTable.COLUMN_RESPONSE_CODE, product.getLikeStatus() ? 1 : 2);

                getContext().startService(intent);
                break;
            default:
                intent = new Intent(getContext(), ProductDetailActivity.class);
                intent.putExtra(CatalogContract.ProductsTable.TABLE_NAME, mProducts.get(position).getProductID());
                startActivity(intent);
        }
    }

    @Override
    public void onRefresh() {
        final int oldTotalPages = mTotalPages;
        final int oldTotalProducts = mTotalProductsOnServer;
        resetVariables();
        mTotalPages = oldTotalPages;
        mTotalProductsOnServer = oldTotalProducts;
        getActivity().getSupportLoaderManager().restartLoader(PRODUCTS_GRID_LOADER, null, this);
        mRequestQueue.add(1);
        mSwipeRefreshLayout.setRefreshing(false);
    }

    public void dismissDialog() {
        if (mCartMenuItemHelper != null) {
            mCartMenuItemHelper.restartLoader();
        }
    }

    public void loadData() {
        String filter = FilterClass.getFilterString();
        if (mFilters == null || !FilterClass.getFilterString().equals(mFilters)) {
            setVisibility(View.VISIBLE, View.INVISIBLE, View.INVISIBLE);
            mFilters = filter;
            resetVariables();
            getActivity().getSupportLoaderManager().restartLoader(PRODUCTS_GRID_LOADER, null, ProductsGridFragment.this);
            mRequestQueue.add(1);
            fetchProductsFromServer();
        }
    }

    private void setVisibility(int loader, int layout, int error) {
        mPageLoader.setVisibility(loader);
        mPageLayout.setVisibility(layout);
        mNoProducts.setVisibility(error);
    }

    private void initResponseCodes(Bundle args) {
        final int type = args.getInt(Constants.TYPE);
        mResponseCodes = new ArrayList<>();
        switch (type) {
            case Constants.FAV_PRODUCTS:
                mResponseCodes.add(Constants.FAV_PRODUCTS);
                break;
            case Constants.REJECTED_PRODUCTS:
                mResponseCodes.add(Constants.REJECTED_PRODUCTS);
                break;
            default:
                mResponseCodes.add(Constants.FAV_PRODUCTS);
                mResponseCodes.add(Constants.REJECTED_PRODUCTS);
                mResponseCodes.add(Constants.ALL_PRODUCTS);
        }
    }

    public void resetVariables() {
        try {
            mTotalPages = -1;
            mRecyclerViewPosition = 0;
            mLoadMoreData = false;
            mActivePageCall = 0;
            mTotalProductsOnServer = -1;
            mLoaderLoading = false;

            if (mRequestQueue == null) {
                mRequestQueue = new LinkedList<>();
            } else {
                mRequestQueue.clear();
            }

            if (mPagesLoaded == null) {
                mPagesLoaded = new HashSet<>();
            } else {
                mPagesLoaded.clear();
            }

            if (mProducts == null) {
                mProducts = new ArrayList<>();
            } else {
                mAdapter.clear();
            }
        } catch (Exception e) {
        }
    }

    private void fetchProductsFromServer() {
        if (mRequestQueue.size() > 0) {
            int pageNumber = mRequestQueue.element();

            if (mPagesLoaded.contains(pageNumber) || (mTotalPages != -1 && pageNumber > mTotalPages)) {
                mRequestQueue.remove();
                fetchProductsFromServer();
                return;
            }
            if (pageNumber == mActivePageCall) {
                return;
            }

            mActivePageCall = pageNumber;

            if (mResponseCodes.size() == 1) {
                if (getActivity() != null) {
                    Intent bpResponse = new Intent(getActivity().getApplicationContext(), BuyerProductService.class);
                    bpResponse.putExtra("TODO", TODO.FETCH_BUYER_PRODUCTS_RESPONSE);
                    bpResponse.putExtra(APIConstants.API_ITEM_PER_PAGE_KEY, mLIMIT);
                    bpResponse.putExtra(APIConstants.API_PAGE_NUMBER_KEY, pageNumber);
                    bpResponse.putExtra(APIConstants.API_RESPONSE_CODE_KEY, TextUtils.join(",", mResponseCodes));
                    bpResponse.putExtra(CatalogContract.CategoriesTable.COLUMN_CATEGORY_ID, FilterClass.getCategoryID());
                    getActivity().startService(bpResponse);
                }
            } else {
                if (getActivity() != null) {
                    Intent intent = new Intent(getActivity().getApplicationContext(), CatalogService.class);
                    intent.putExtra("TODO", R.integer.fetch_products);
                    intent.putExtra(APIConstants.API_PAGE_NUMBER_KEY, pageNumber);
                    intent.putExtra(APIConstants.API_ITEM_PER_PAGE_KEY, mLIMIT);
                    getActivity().startService(intent);
                }
            }
        }
    }

    private void handleOnBroadcastReceive(final Intent intent) {
        if (mRequestQueue.size() > 0) {
            mPagesLoaded.add(mRequestQueue.remove());
        }

        Bundle extras = intent.getExtras();
        if (extras.getString(Constants.ERROR_RESPONSE) != null) {
            if (mPageLoader.getVisibility() == View.VISIBLE) {
                mPageLoader.setVisibility(View.INVISIBLE);
                showErrorMessage();
            }
            return;
        }

        mTotalProductsOnServer = intent.getIntExtra(APIConstants.TOTAL_ITEMS_KEY, -1);
        if (mTotalProductsOnServer == 0) {
            setVisibility(View.INVISIBLE, View.INVISIBLE, View.VISIBLE);
            return;
        }

        if (mProducts.size() >= mTotalProductsOnServer) {
            removeDummyObject();
        }

        final int pageNumber = intent.getIntExtra(APIConstants.API_PAGE_NUMBER_KEY, -1);
        final int totalPages = intent.getIntExtra(APIConstants.API_TOTAL_PAGES_KEY, 1);
        final int updatedInserted = intent.getIntExtra(Constants.INSERTED_UPDATED, 0);

        mTotalPages = totalPages;
        if (updatedInserted > 0 && pageNumber != -1) {
            if (!mLoaderLoading) {
                if (getActivity() != null) {
                    getActivity().getSupportLoaderManager().restartLoader(PRODUCTS_GRID_LOADER, null, this);
                }
            } else {
                mLoadMoreData = true;
            }
        } else if (updatedInserted == -1) {
            if (mNoProducts.getVisibility() == View.INVISIBLE) {
                setVisibility(View.INVISIBLE, View.INVISIBLE, View.VISIBLE);
            }
        } else if ((mPageLoader.getVisibility() == View.VISIBLE && mTotalProductsOnServer > 0) ||
                mNoProducts.getVisibility() == View.VISIBLE) {
            getActivity().getSupportLoaderManager().restartLoader(PRODUCTS_GRID_LOADER, null, this);
        } else if (mTotalPages == pageNumber) {
            removeDummyObject();
        }

        if (mRequestQueue.size() > 0) {
            fetchProductsFromServer();
        }
    }

    private void showErrorMessage() {
        if (mSnackbar != null && mSnackbar.isShownOrQueued()) {
            mSnackbar.dismiss();
        }

        final View view = getView();
        if (view == null) {
            return;
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
                mSnackbar = Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.retry_text, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                mPageLoader.setVisibility(View.VISIBLE);
                                resetVariables();
                                mRequestQueue.add(1);
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

        final View view = getView();
        if (view == null) {
            return;
        }

        mSnackbar = Snackbar.make(view, getString(R.string.new_products_message), Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.show_text, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        resetVariables();
                        mTotalPages = totalPages;
                        getActivity().getSupportLoaderManager().restartLoader(PRODUCTS_GRID_LOADER, null, ProductsGridFragment.this);
                    }
                });

        ((TextView) mSnackbar.getView().findViewById(android.support.design.R.id.snackbar_text)).setTextColor(Color.WHITE);
        mSnackbar.show();
    }

    private void removeDummyObject() {
        if (mProducts.size() > 0 && mProducts.get(mProducts.size() - 1) == null) {
            mProducts.remove(mProducts.size() - 1);
            mAdapter.notifyItemRemoved(mProducts.size());
        }
    }
}
