package com.wholdus.www.wholdusbuyerapp.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.daprlabs.aaron.swipedeck.SwipeDeck;
import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.adapters.ProductSwipeDeckAdapter;
import com.wholdus.www.wholdusbuyerapp.databaseContracts.CatalogContract;
import com.wholdus.www.wholdusbuyerapp.helperClasses.APIConstants;
import com.wholdus.www.wholdusbuyerapp.helperClasses.Constants;
import com.wholdus.www.wholdusbuyerapp.helperClasses.FilterClass;
import com.wholdus.www.wholdusbuyerapp.helperClasses.IntentFilters;
import com.wholdus.www.wholdusbuyerapp.helperClasses.TODO;
import com.wholdus.www.wholdusbuyerapp.interfaces.HandPickedListenerInterface;
import com.wholdus.www.wholdusbuyerapp.interfaces.ItemClickListener;
import com.wholdus.www.wholdusbuyerapp.interfaces.ProductCardListenerInterface;
import com.wholdus.www.wholdusbuyerapp.loaders.ProductsLoader;
import com.wholdus.www.wholdusbuyerapp.models.Product;
import com.wholdus.www.wholdusbuyerapp.services.BuyerProductService;
import com.wholdus.www.wholdusbuyerapp.services.CatalogService;

import java.util.ArrayList;

/**
 * Created by kaustubh on 17/12/16.
 */

public class HandPickedFragment extends Fragment implements ProductCardListenerInterface, ItemClickListener {

    private HandPickedListenerInterface mListener;
    private final int PRODUCTS_DB_LOADER = 30;
    private BroadcastReceiver mBuyerProductServiceResponseReceiver;
    private BroadcastReceiver mProductServiceResponseReceiver;
    private BroadcastReceiver mSpecificProductServiceResponseReceiver;
    private ProductsLoaderManager mProductsLoader;
    ArrayList<Product> mProductsArrayList;
    ArrayList<Integer> mExcludeProductIDs;
    ArrayList<Integer> mProductIDs;
    SwipeDeck mSwipeDeck;
    ImageButton mLikeButton;
    ImageButton mDislikeButton;
    ImageButton mAddToCartButton;
    ImageButton mFilterButton;
    ProductSwipeDeckAdapter mProductSwipeDeckAdapter;
    LinearLayout mNoProductsLeft;
    TextView mNoProductsLeftTextView;
    ProgressBar mNoProductsLeftProgressBar;
    private int mPosition = 0;
    private int mProductsLeft = 0;
    private int mProductBuffer = 8;
    private boolean mHasSwiped = true;
    private boolean mFirstLoad = true;
    private int mProductsPageNumber, mTotalProductPages;
    private final int mProductsLimit = 20;

    public HandPickedFragment() {
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
        Bundle mArgs = getArguments();
        try {
            mProductIDs = mArgs.getIntegerArrayList(CatalogContract.ProductsTable.TABLE_NAME);
        } catch (Exception e){

        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_handpicked, container, false);
        initReferences(rootView);
        mBuyerProductServiceResponseReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                handleBuyerProductAPIResponse();
            }
        };
        mProductServiceResponseReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                handleProductAPIResponse(intent);
            }
        };
        if (mProductIDs!= null){
            mSpecificProductServiceResponseReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    handleBuyerProductAPIResponse();
                }
            };
        }
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mListener.fragmentCreated("Wholdus");
    }

    @Override
    public void onResume() {
        super.onResume();

        mProductsLoader = new ProductsLoaderManager();

        IntentFilter intentFilter = new IntentFilter(getString(R.string.buyer_product_data_updated));
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mBuyerProductServiceResponseReceiver, intentFilter);

        IntentFilter productIntentFilter = new IntentFilter(IntentFilters.PRODUCT_DATA);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mProductServiceResponseReceiver, productIntentFilter);

        if (mProductIDs != null){
            IntentFilter specificProductIntentFilter = new IntentFilter(IntentFilters.SPECIFIC_PRODUCT_DATA);
            LocalBroadcastManager.getInstance(getContext()).registerReceiver(mSpecificProductServiceResponseReceiver, specificProductIntentFilter);
        }

        if (mProductsArrayList.isEmpty()) {
            updateProducts();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBuyerProductServiceResponseReceiver != null) {
            try {
                LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mBuyerProductServiceResponseReceiver);
            } catch (Exception e){

            }
            mBuyerProductServiceResponseReceiver = null;
        }
        if (mProductServiceResponseReceiver != null) {
            try {
                LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mProductServiceResponseReceiver);
            } catch (Exception e){

            }
            mProductServiceResponseReceiver = null;
        }
        if (mSpecificProductServiceResponseReceiver != null) {
            try {
                LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mSpecificProductServiceResponseReceiver);
            } catch (Exception e){

            }
            mSpecificProductServiceResponseReceiver = null;
        }
    }

    private void handleBuyerProductAPIResponse() {
        if (getActivity() != null && mProductsLeft < mProductBuffer) {
            getActivity().getSupportLoaderManager().restartLoader(PRODUCTS_DB_LOADER, null, mProductsLoader);
        }
    }

    private void initReferences(ViewGroup rootView) {

        mSwipeDeck = (SwipeDeck) rootView.findViewById(R.id.product_swipe_deck);
        mSwipeDeck.setLeftImage(R.id.left_image);
        mSwipeDeck.setRightImage(R.id.right_image);
        mSwipeDeck.setCallback(new SwipeDeck.SwipeDeckCallback() {
            @Override
            public void cardSwipedLeft(long l) {
                //TODO : Write functions to like and dislike
                actionAfterSwipe(false);
            }

            @Override
            public void cardSwipedRight(long l) {
                actionAfterSwipe(true);
            }
        });
        mLikeButton = (ImageButton) rootView.findViewById(R.id.hand_picked_like_button);
        mLikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mHasSwiped = false;
                mSwipeDeck.swipeTopCardRight(2000);
            }
        });
        mDislikeButton = (ImageButton) rootView.findViewById(R.id.hand_picked_dislike_button);
        mDislikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mHasSwiped = false;
                mSwipeDeck.swipeTopCardLeft(2000);
            }
        });
        //TODO : Implement add to cart and set store price fragments
        mAddToCartButton = (ImageButton) rootView.findViewById(R.id.hand_picked_cart_button);
        mAddToCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getFragmentManager();
                CartDialogFragment dialogFragment = new CartDialogFragment();
                Bundle args = new Bundle();
                args.putInt(CatalogContract.ProductsTable.COLUMN_PRODUCT_ID, mProductsArrayList.get(mPosition).getProductID());
                dialogFragment.setArguments(args);
                dialogFragment.show(fragmentManager, dialogFragment.getClass().getSimpleName());
            }
        });

        mFilterButton = (ImageButton) rootView.findViewById(R.id.hand_picked_filter_button);
        mFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //resetProducts();
                //FilterClass.setCategoryID(-1);
                mListener.openFilter(true);
                mListener.fragmentCreated("Filter");
            }
        });

        mProductsArrayList = new ArrayList<>();
        mExcludeProductIDs = new ArrayList<>();
        mProductSwipeDeckAdapter = new ProductSwipeDeckAdapter(getContext(), mProductsArrayList, this, this);
        mSwipeDeck.setAdapter(mProductSwipeDeckAdapter);

        mNoProductsLeft = (LinearLayout) rootView.findViewById(R.id.no_products_left);
        mNoProductsLeftTextView = (TextView) rootView.findViewById(R.id.no_products_left_text_view);
        mNoProductsLeftProgressBar = (ProgressBar) rootView.findViewById(R.id.loading_indicator);

        resetProducts();
    }

    private void fetchBuyerProducts() {
        Intent intent = new Intent(getContext(), BuyerProductService.class);
        intent.putExtra("TODO", TODO.FETCH_BUYER_PRODUCTS);
        getContext().startService(intent);
    }

    private void actionAfterSwipe(boolean liked) {
        Intent intent = new Intent(getContext(), BuyerProductService.class);
        intent.putExtra("TODO", TODO.UPDATE_PRODUCT_RESPONSE);
        intent.putExtra(CatalogContract.ProductsTable.COLUMN_PRODUCT_ID, mProductsArrayList.get(mPosition).getProductID());
        intent.putExtra(CatalogContract.ProductsTable.COLUMN_RESPONDED_FROM, 0);
        intent.putExtra(CatalogContract.ProductsTable.COLUMN_HAS_SWIPED, mHasSwiped);
        intent.putExtra(CatalogContract.ProductsTable.COLUMN_RESPONSE_CODE, liked ? 1 : 2);
        getContext().startService(intent);

        mHasSwiped = true;

        mProductsLeft -= 1;
        if (mProductsLeft == 0) {
            setNoProductsLeftView();
            return;
        }

        mPosition += 1;
        mListener.fragmentCreated(mProductsArrayList.get(mPosition).getName());

        if (mProductsLeft < mProductBuffer) {
            getActivity().getSupportLoaderManager().restartLoader(PRODUCTS_DB_LOADER, null, mProductsLoader);
            updateProducts();
        }
    }

    @Override
    public void cardCreated() {
        mNoProductsLeft.setVisibility(View.GONE);
    }

    private void setViewForProducts(ArrayList<Product> data) {
        //TODO: Add products to adapter correctly(not ones already present)

        if (data.size() == 0) {
            if (!mFirstLoad && mProductsArrayList.size() == 0 && mProductIDs == null) {
                setNoProductsLeftView();
            }
            if (mFirstLoad) {
                mFirstLoad = false;
            }
            return;
        }

        if (mFirstLoad || mProductsArrayList.size() == 0) {
            mFirstLoad = false;
            mListener.fragmentCreated(data.get(mPosition).getName());
            setButtonsState(true);
        }
        mProductsArrayList.addAll(data);
        for (Product product : data) {
            mExcludeProductIDs.add(product.getProductID());
            if (mProductIDs!=null){
                mProductIDs.remove(Integer.valueOf(product.getProductID()));
            }
        }

        if (mProductIDs!= null && mProductIDs.size() == 0){
            mProductIDs = null;
        }
        mProductSwipeDeckAdapter.notifyDataSetChanged();
        mProductsLeft += data.size();

        if (mProductsLeft < mProductBuffer) {
            updateProducts();
        }


    }

    private void setNoProductsLeftView() {
        setButtonsState(false);
        mListener.fragmentCreated("Hand Picked For You");
        mSwipeDeck.setVisibility(View.GONE);
        mNoProductsLeft.setVisibility(View.VISIBLE);
        mNoProductsLeftProgressBar.setVisibility(View.GONE);
        mNoProductsLeftTextView.setVisibility(View.VISIBLE);
        if (!FilterClass.isFilterApplied()) {
            mNoProductsLeftTextView.setText(R.string.no_products_left);
        }else {
            mNoProductsLeftTextView.setText(R.string.filter_no_products);
        }
    }

    private void setButtonsState(boolean state) {
        mLikeButton.setEnabled(state);
        mDislikeButton.setEnabled(state);
        mAddToCartButton.setEnabled(state);
        mFilterButton.setEnabled(true);
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
            int productLimit;
            ArrayList<Integer> responseCodes = new ArrayList<>();
            responseCodes.add(0);
            // TODO : ?? Also add condition so that buyer product Id is not 0
            if (mProductIDs != null){
                responseCodes.add(1);
                responseCodes.add(2);
                productLimit = mProductIDs.size();
            }else {
                productLimit = 15;
            }
            return new ProductsLoader(getContext(), mProductIDs, mExcludeProductIDs, responseCodes, null, productLimit);
        }
    }

    @Override
    public void itemClicked(View view, int position, int id) {
        mListener.openProductDetails(mProductsArrayList.get(position).getProductID());
    }

    public void resetProducts(){

        mNoProductsLeft.setVisibility(View.VISIBLE);
        mNoProductsLeftTextView.setVisibility(View.GONE);
        mNoProductsLeftProgressBar.setVisibility(View.VISIBLE);
        mProductsArrayList.clear();
        mExcludeProductIDs.clear();
        //mProductSwipeDeckAdapter.notifyDataSetChanged();
        mFirstLoad = true;
        mHasSwiped = true;
        mPosition = 0;
        mProductsLeft = 0;
        mProductsPageNumber = 1;
        mTotalProductPages = -1;
        setButtonsState(false);
    }

    public void updateProducts(){
        fetchBuyerProducts();
        getActivity().getSupportLoaderManager().restartLoader(PRODUCTS_DB_LOADER, null, mProductsLoader);
        if (FilterClass.isFilterApplied()){
            fetchProducts();
        }
        if (mProductIDs!= null){
            fetchSpecificProducts();
        }
    }

    private void fetchSpecificProducts(){
        Intent intent = new Intent(getContext(), CatalogService.class);
        intent.putExtra("TODO", R.integer.fetch_specific_products);
        intent.putExtra("productIDs", TextUtils.join(",", mProductIDs));
        intent.putExtra("items_per_page", String.valueOf(mProductIDs.size()));
        getActivity().startService(intent);
    }

    private void fetchProducts(){
        if (mTotalProductPages == -1 || mTotalProductPages >= mProductsPageNumber) {
            Intent intent = new Intent(getContext(), CatalogService.class);
            intent.putExtra("TODO", R.integer.fetch_products);
            intent.putExtra("page_number", mProductsPageNumber);
            intent.putExtra("items_per_page", mProductsLimit);
            getActivity().startService(intent);
        }
    }

    private void handleProductAPIResponse(final Intent intent){
        Bundle extras = intent.getExtras();
        if (extras.getString(Constants.ERROR_RESPONSE) != null) {
            return;
        }
        final int pageNumber = intent.getIntExtra(APIConstants.API_PAGE_NUMBER_KEY, -1);
        final int totalPages = intent.getIntExtra(APIConstants.API_TOTAL_PAGES_KEY, 1);
        final int updatedInserted = intent.getIntExtra(Constants.INSERTED_UPDATED, 0);
        Log.d(this.getClass().getSimpleName(), "page number from api: " + pageNumber);
        Log.d(this.getClass().getSimpleName(), "total pages: " + totalPages);
        if (updatedInserted > 0) {
            if (mProductsPageNumber == 1 && pageNumber == 1) {
                handleBuyerProductAPIResponse();
            } else if (mProductsPageNumber > pageNumber){
                handleBuyerProductAPIResponse();
            } else if (mProductsPageNumber == pageNumber) {
                handleBuyerProductAPIResponse();
                mProductsPageNumber++;
            }
        }
        mTotalProductPages = totalPages;
    }
}
