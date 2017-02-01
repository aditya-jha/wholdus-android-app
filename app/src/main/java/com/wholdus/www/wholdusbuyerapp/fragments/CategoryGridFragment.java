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
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.activities.OnBoardingActivity;
import com.wholdus.www.wholdusbuyerapp.adapters.CategoriesGridAdapter;
import com.wholdus.www.wholdusbuyerapp.databaseContracts.CatalogContract;
import com.wholdus.www.wholdusbuyerapp.decorators.GridDividerItemDecoration;
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
    private Snackbar mSnackbar;

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
        mCategoriesData = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_categories_grid, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mPageLoader = (ProgressBar) view.findViewById(R.id.page_loader);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.categories_recycler_view);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.addItemDecoration(new GridDividerItemDecoration(
                ContextCompat.getDrawable(getContext(), R.drawable.divider),
                ContextCompat.getDrawable(getContext(), R.drawable.divider),
                2));

        mCategoriesGridAdapter = new CategoriesGridAdapter(getContext(), mCategoriesData, this);
        mRecyclerView.setAdapter(mCategoriesGridAdapter);

        getActivity().getSupportLoaderManager().initLoader(CATEGORIES_LOADER, null, this);
        fetchDataFromServer();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        updateUnsyncedInterests();
    }

    private void updateUnsyncedInterests(){
        Intent intent = new Intent(getContext(), CatalogService.class);
        intent.putExtra("TODO", TODO.UPDATE_UNSYNCED_BUYER_INTERESTS);
        getContext().startService(intent);
    }


    @Override
    public void onResume() {
        super.onResume();
        mListener.fragmentCreated("Categories", true);

        IntentFilter intentFilter = new IntentFilter(IntentFilters.CATEGORY_DATA);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mReceiver, intentFilter);

        if (mLoaderDataLoaded && mCategoriesData.size() == 0) {
            getActivity().getSupportLoaderManager().restartLoader(CATEGORIES_LOADER, null, this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mReceiver);
        }catch (Exception e){

        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mSnackbar != null && mSnackbar.isShownOrQueued()) {
            mSnackbar.dismiss();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mReceiver = null;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public Loader<ArrayList<Category>> onCreateLoader(int id, Bundle args) {
        mLoaderDataLoaded = false;
        return new CategoriesGridLoader(getContext(), false);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Category>> loader, final ArrayList<Category> data) {
        mLoaderDataLoaded = true;
        if (data != null && data.size() > 0 && mListener != null) {
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
                updateCategoryLikeStatus(position);
                break;
            default:
                // open category clicked
                if (getActivity() != null && getActivity() instanceof OnBoardingActivity){
                    updateCategoryLikeStatus(position);
                }
                mListener.openCategory(mCategoriesData.get(position).getCategoryID());
        }
    }

    private void updateCategoryLikeStatus(int position){
        mPageLoader.setVisibility(View.GONE);
        Category category = mCategoriesData.get(position);
        category.setBuyerInterestIsActive((category.getBuyerInterestIsActive()==1)?0:1);
        Intent intent = new Intent(getContext(), CatalogService.class);
        intent.putExtra("TODO", TODO.UPDATE_BUYER_INTEREST);
        intent.putExtra(CatalogContract.CategoriesTable.COLUMN_CATEGORY_ID, category.getCategoryID());
        intent.putExtra(CatalogContract.CategoriesTable.COLUMN_BUYER_INTEREST_IS_ACTIVE, category.getBuyerInterestIsActive());
        getContext().startService(intent);
        mCategoriesGridAdapter.notifyItemChanged(position);
    }

    private void fetchDataFromServer() {
        if (!mFetchedDataFromServer) {
            Intent intent = new Intent(getContext(), CatalogService.class);
            intent.putExtra("TODO", TODO.FETCH_CATEGORIES);
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
        final View view = getView();
        if (view == null) return;

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (HelperFunctions.isNetworkAvailable(getContext())) {
                    mSnackbar = Snackbar.make(view, getString(R.string.api_error_message), Snackbar.LENGTH_INDEFINITE);
                    mSnackbar.getView();
                } else {
                    mSnackbar = Snackbar.make(view, getString(R.string.no_internet_access), Snackbar.LENGTH_INDEFINITE);
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
