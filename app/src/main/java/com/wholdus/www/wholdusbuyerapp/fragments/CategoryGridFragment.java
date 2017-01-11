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
import com.wholdus.www.wholdusbuyerapp.databaseContracts.CatalogContract;
import com.wholdus.www.wholdusbuyerapp.decorators.GridItemDecorator;
import com.wholdus.www.wholdusbuyerapp.helperClasses.Constants;
import com.wholdus.www.wholdusbuyerapp.helperClasses.HelperFunctions;
import com.wholdus.www.wholdusbuyerapp.helperClasses.IntentFilters;
import com.wholdus.www.wholdusbuyerapp.helperClasses.TODO;
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

public class CategoryGridFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<ArrayList<Category>>,
        ItemClickListener {

    private static final int CATEGORIES_LOADER = 0;
    private BroadcastReceiver mReceiver;
    private RecyclerView mRecyclerView;
    private CategoriesGridAdapter mCategoriesGridAdapter;
    private HomeListenerInterface mListener;
    private List<Category> mCategoriesData;
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
        mListener = (HomeListenerInterface) context;
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
        return mRootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().getSupportLoaderManager().initLoader(CATEGORIES_LOADER, null, this);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mListener.fragmentCreated("All Categories", true);
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
        mListener = null;
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
            setDataToView(data);
            mPageLoader.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Category>> loader) {
        mLoaderDataLoaded = false;
    }

    @Override
    public void itemClicked(View view, int position, int id) {
        final int ID = view.getId();
        switch (ID) {
            case R.id.fav_icon_image_view:
                mPageLoader.setVisibility(View.GONE);
                Category category = mCategoriesData.get(position);
                Intent intent = new Intent(getContext(), CatalogService.class);
                intent.putExtra("TODO", TODO.UPDATE_BUYER_INTEREST);
                intent.putExtra(CatalogContract.CategoriesTable.COLUMN_CATEGORY_ID, category.getCategoryID());
                intent.putExtra(CatalogContract.CategoriesTable.COLUMN_BUYER_INTEREST_IS_ACTIVE, (category.getBuyerInterestIsActive()==1)?0:1);
                getContext().startService(intent);
                break;
            default:
                // open category clicked
                mListener.openCategory(mCategoriesData.get(position).getCategoryID());
        }
    }

    private void initReferences(ViewGroup rootView) {
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
            } else if (!mLoaderDataLoaded) {
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
                mCategoriesData.clear();
                mCategoriesData.addAll(data);
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mCategoriesGridAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        }).start();
    }

    private void showErrorMessage() {
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
