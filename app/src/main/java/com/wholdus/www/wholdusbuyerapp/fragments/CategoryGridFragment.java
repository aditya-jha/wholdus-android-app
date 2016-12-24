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
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.adapters.CategoriesGridAdapter;
import com.wholdus.www.wholdusbuyerapp.decorators.GridItemDecorator;
import com.wholdus.www.wholdusbuyerapp.helperClasses.Constants;
import com.wholdus.www.wholdusbuyerapp.helperClasses.HelperFunctions;
import com.wholdus.www.wholdusbuyerapp.helperClasses.IntentFilters;
import com.wholdus.www.wholdusbuyerapp.interfaces.HomeListenerInterface;
import com.wholdus.www.wholdusbuyerapp.interfaces.ItemClickListener;
import com.wholdus.www.wholdusbuyerapp.loaders.CategoriesGridLoader;
import com.wholdus.www.wholdusbuyerapp.models.Category;
import com.wholdus.www.wholdusbuyerapp.services.CatalogService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aditya on 10/12/16.
 * Fragment to Display Categories present on wholdus
 */

public class CategoryGridFragment extends Fragment implements LoaderManager.LoaderCallbacks<ArrayList<Category>>, ItemClickListener {

    private static int CATEGORIES_LOADER = 0;
    private BroadcastReceiver mReceiver;
    private RecyclerView mRecyclerView;
    private CategoriesGridAdapter mCategoriesGridAdapter;
    private HomeListenerInterface mListener;
    private List<Category> mCategoriesData;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ProgressBar mPageLoader;
    private boolean mFetchedDataFromServer, mLoaderDataLoaded;
    private ViewGroup mRootView;
    private Snackbar mSnackbar;
    private CategoriesGridLoader mLoader;

    public CategoryGridFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (HomeListenerInterface) context;
        } catch (ClassCastException cee) {
            Log.e(this.getClass().getSimpleName(), " must implement " + HomeListenerInterface.class.getSimpleName());
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                handleBroadcastOnReceive(intent);
            }
        };
        mFetchedDataFromServer = false;
        mLoaderDataLoaded = false;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = (ViewGroup) inflater.inflate(R.layout.fragment_categories_grid, container, false);
        initReferences(mRootView);
        getActivity().getSupportLoaderManager().initLoader(CATEGORIES_LOADER, null, this);
        return mRootView;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mListener.fragmentCreated("All Categories", false);
        IntentFilter intentFilter = new IntentFilter(IntentFilters.CATEGORY_DATA);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mReceiver, intentFilter);
        fetchDataFromServer();
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mReceiver);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mSnackbar != null && mSnackbar.isShownOrQueued()) {
            mSnackbar.dismiss();
        }
        if (mLoader != null) {
            mLoader.deleteData();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public Loader<ArrayList<Category>> onCreateLoader(int id, Bundle args) {
        mLoaderDataLoaded = false;
        mLoader = new CategoriesGridLoader(getContext());
        return mLoader;
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Category>> loader, final ArrayList<Category> data) {
        mLoaderDataLoaded = true;
        if (data.size() > 0) {
            mPageLoader.setVisibility(View.GONE);
            mSwipeRefreshLayout.setVisibility(View.VISIBLE);
            setDataToView(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Category>> loader) {
        mLoaderDataLoaded = false;
    }

    @Override
    public void itemClicked(int position, int id) {
        switch (id) {
            case R.id.fav_icon_image_view:
                /* TODO: add the category at position to buyer interests */
                break;
            default:
                // open category clicked
                mListener.openCategory(mCategoriesData.get(position).getCategoryID());
        }
    }

    private void initReferences(ViewGroup rootView) {
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getActivity().getSupportLoaderManager().initLoader(CATEGORIES_LOADER, null, CategoryGridFragment.this);
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
        mPageLoader = (ProgressBar) rootView.findViewById(R.id.page_loader);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.categories_recycler_view);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.addItemDecoration(new GridItemDecorator(2, getResources().getDimensionPixelSize(R.dimen.card_margin_horizontal), true, 0));
        mCategoriesData = new ArrayList<>();
        mCategoriesGridAdapter = new CategoriesGridAdapter(getContext(), mCategoriesData, this);
        mRecyclerView.setAdapter(mCategoriesGridAdapter);
    }

    private void fetchDataFromServer() {
        if (!mFetchedDataFromServer) {
            Intent intent = new Intent(getContext(), CatalogService.class);
            intent.putExtra("TODO", R.integer.fetch_categories);
            intent.putExtra(getString(R.string.seller_category_details), true);
            getActivity().startService(intent);
        }
    }

    private void handleBroadcastOnReceive(final Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras.getString(Constants.ERROR_RESPONSE) != null) {
            if (mLoaderDataLoaded && mPageLoader.getVisibility() != View.GONE) {
                // no data in local and error from server
                // show error message
                showErrorMessage();
                mPageLoader.setVisibility(View.INVISIBLE);
            } else if (!mLoaderDataLoaded){
                // retry download
                fetchDataFromServer();
            }
        } else {
            mFetchedDataFromServer = true;
            int insertedUpdated = intent.getIntExtra(Constants.INSERTED_UPDATED, 0);
            if (insertedUpdated > 0) {
                // new data available, restart the loader
                getActivity().getSupportLoaderManager().restartLoader(CATEGORIES_LOADER, null, this);
            }
        }
    }

    private void setDataToView(final List<Category> data) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (mCategoriesData.size() == 0 && data.size() != 0) {
                    mCategoriesData.addAll(data);
                    mCategoriesGridAdapter.notifyItemRangeInserted(0, mCategoriesData.size());
                }
            }
        }).start();
    }

    private void showErrorMessage() {
        final View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPageLoader.getVisibility() != View.VISIBLE) {
                    mPageLoader.setVisibility(View.VISIBLE);
                }
                fetchDataFromServer();
            }
        };
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (HelperFunctions.isNetworkAvailable(getContext())) {
                    mSnackbar = Snackbar.make(mRootView, getString(R.string.api_error_message), Snackbar.LENGTH_INDEFINITE);
                    mSnackbar.getView();
                } else {
                    mSnackbar = Snackbar.make(mRootView, getString(R.string.no_internet_access), Snackbar.LENGTH_INDEFINITE);
                }
                mSnackbar.setAction(R.string.retry_text, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        fetchDataFromServer();
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
}
