package com.wholdus.www.wholdusbuyerapp.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
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
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.daprlabs.aaron.swipedeck.SwipeDeck;
import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.activities.CartActivity;
import com.wholdus.www.wholdusbuyerapp.activities.CategoryProductActivity;
import com.wholdus.www.wholdusbuyerapp.adapters.ProductSwipeDeckAdapter;
import com.wholdus.www.wholdusbuyerapp.databaseContracts.CatalogContract;
import com.wholdus.www.wholdusbuyerapp.helperClasses.APIConstants;
import com.wholdus.www.wholdusbuyerapp.helperClasses.CartMenuItemHelper;
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
    private BroadcastReceiver mBuyerProductServiceResponseReceiver, mProductServiceResponseReceiver, mSpecificProductServiceResponseReceiver;
    private ProductsLoaderManager mProductsLoader;
    private ArrayList<Product> mProductsArrayList;
    private ArrayList<Integer> mExcludeProductIDs, mProductIDs;
    private SwipeDeck mSwipeDeck;
    private ImageButton mLikeButton, mDislikeButton, mAddToCartButton, mFilterButton;
    private ProductSwipeDeckAdapter mProductSwipeDeckAdapter;
    private LinearLayout mNoProductsLeft;
    private TextView mNoProductsLeftTextView;
    private ProgressBar mNoProductsLeftProgressBar;
    private int mPosition = 0, mProductsLeft = 0, mProductBuffer = 8, mProductsPageNumber, mTotalProductPages;
    private boolean mHasSwiped = true, mFirstLoad = true, mButtonEnabled;
    private final int mProductsLimit = 20;

    private static final int ANIMATION_DURATION = 250;
    private CartMenuItemHelper mCartMenuItemHelper;

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
        } catch (Exception e) {

        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
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
        if (mProductIDs != null) {
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

        if (mProductIDs != null) {
            IntentFilter specificProductIntentFilter = new IntentFilter(IntentFilters.SPECIFIC_PRODUCT_DATA);
            LocalBroadcastManager.getInstance(getContext()).registerReceiver(mSpecificProductServiceResponseReceiver, specificProductIntentFilter);
        }

        if (mProductsArrayList.isEmpty()) {
            updateProducts();
        }

        if (mCartMenuItemHelper != null) {
            mCartMenuItemHelper.restartLoader();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBuyerProductServiceResponseReceiver != null) {
            try {
                LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mBuyerProductServiceResponseReceiver);
            } catch (Exception e) {

            }
            mBuyerProductServiceResponseReceiver = null;
        }
        if (mProductServiceResponseReceiver != null) {
            try {
                LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mProductServiceResponseReceiver);
            } catch (Exception e) {

            }
            mProductServiceResponseReceiver = null;
        }
        if (mSpecificProductServiceResponseReceiver != null) {
            try {
                LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mSpecificProductServiceResponseReceiver);
            } catch (Exception e) {

            }
            mSpecificProductServiceResponseReceiver = null;
        }
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
                Intent shortlistIntent = new Intent(getContext(), CategoryProductActivity.class);
                shortlistIntent.putExtra(Constants.TYPE, Constants.FAV_PRODUCTS);
                getContext().startActivity(shortlistIntent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void handleBuyerProductAPIResponse() {
        if (getActivity() != null && mProductsLeft < mProductBuffer) {
            getActivity().getSupportLoaderManager().restartLoader(PRODUCTS_DB_LOADER, null, mProductsLoader);
        }
    }

    private void initReferences(ViewGroup rootView) {
        SwipeDeck.ANIMATION_DURATION = ANIMATION_DURATION;
        mSwipeDeck = (SwipeDeck) rootView.findViewById(R.id.product_swipe_deck);

        mSwipeDeck.setLeftImage(R.id.left_image);
        mSwipeDeck.setRightImage(R.id.right_image);
        mSwipeDeck.setCallback(new SwipeDeck.SwipeDeckCallback() {
            @Override
            public void cardSwipedLeft(long l) {
                actionAfterSwipe(false);
            }

            @Override
            public void cardSwipedRight(long l) {
                actionAfterSwipe(true);
            }
        });

        final Animation animButtonScale = AnimationUtils.loadAnimation(getContext(), R.anim.button_scale_down_up);
        final FrameLayout likeButtonLayout = (FrameLayout) rootView.findViewById(R.id.hand_picked_like_button_layout);
        mLikeButton = (ImageButton) rootView.findViewById(R.id.hand_picked_like_button);
        mLikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                likeButtonLayout.startAnimation(animButtonScale);
                if (mProductsLeft > 0 && mButtonEnabled) {
                    bringFeedbackImageToFront(true);
                    mHasSwiped = false;
                    mButtonEnabled = false;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mSwipeDeck.swipeTopCardRight(ANIMATION_DURATION);
                        }
                    }, 300);
                }
            }
        });
        final FrameLayout dislikeButtonLayout = (FrameLayout) rootView.findViewById(R.id.hand_picked_dislike_button_layout);
        mDislikeButton = (ImageButton) rootView.findViewById(R.id.hand_picked_dislike_button);
        mDislikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dislikeButtonLayout.startAnimation(animButtonScale);
                if (mProductsLeft > 0 && mButtonEnabled) {
                    bringFeedbackImageToFront(false);
                    mHasSwiped = false;
                    mButtonEnabled = false;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mSwipeDeck.swipeTopCardLeft(ANIMATION_DURATION);
                        }
                    }, 300);
                }
            }
        });
        final FrameLayout addToCartButtonLayout = (FrameLayout) rootView.findViewById(R.id.hand_picked_cart_button_layout);
        mAddToCartButton = (ImageButton) rootView.findViewById(R.id.hand_picked_cart_button);
        mAddToCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addToCartButtonLayout.startAnimation(animButtonScale);
                if (mProductsLeft > 0 && mButtonEnabled) {
                    FragmentManager fragmentManager = getFragmentManager();
                    CartDialogFragment dialogFragment = new CartDialogFragment();
                    Bundle args = new Bundle();
                    args.putInt(CatalogContract.ProductsTable.COLUMN_PRODUCT_ID, mProductsArrayList.get(mPosition).getProductID());
                    dialogFragment.setArguments(args);
                    dialogFragment.show(fragmentManager, dialogFragment.getClass().getSimpleName());
                }
            }
        });
        final FrameLayout filterButtonLayout = (FrameLayout) rootView.findViewById(R.id.hand_picked_filter_button_layout);
        mFilterButton = (ImageButton) rootView.findViewById(R.id.hand_picked_filter_button);
        mFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterButtonLayout.startAnimation(animButtonScale);
                //resetProducts();
                //FilterClass.setCategoryID(-1);
                if (mButtonEnabled) {
                    mListener.openFilter(true);
                    mListener.fragmentCreated("Filter");
                }
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

    private void bringFeedbackImageToFront(boolean liked) {
        try {
            int adapterIndex;
            if (mProductsLeft > 2) {
                adapterIndex = 2;
            } else if (mProductsLeft == 2) {
                adapterIndex = 1;
            } else if (mProductsLeft == 1) {
                adapterIndex = 0;
            } else {
                return;
            }
            int resourceID;
            if (liked) {
                resourceID = R.id.right_image;
            } else {
                resourceID = R.id.left_image;
            }
            View convertView = mSwipeDeck.getChildAt(adapterIndex);
            if (convertView == null) {
                return;
            }
            final Animation animFeedbackImage = AnimationUtils.loadAnimation(getContext(), R.anim.feedback_image_fade_in);
            final ImageView imageView = (ImageView) convertView.findViewById(resourceID);
            imageView.setVisibility(View.INVISIBLE);
            imageView.setAlpha((float) 1);
            animFeedbackImage.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    imageView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animation animation) {

                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            imageView.startAnimation(animFeedbackImage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendBuyerProductResponse(boolean liked) {
        Intent intent = new Intent(getContext(), BuyerProductService.class);
        intent.putExtra("TODO", TODO.UPDATE_PRODUCT_RESPONSE);
        intent.putExtra(CatalogContract.ProductsTable.COLUMN_PRODUCT_ID, mProductsArrayList.get(mPosition).getProductID());
        intent.putExtra(CatalogContract.ProductsTable.COLUMN_RESPONDED_FROM, 0);
        intent.putExtra(CatalogContract.ProductsTable.COLUMN_HAS_SWIPED, mHasSwiped);
        intent.putExtra(CatalogContract.ProductsTable.COLUMN_RESPONSE_CODE, liked ? 1 : 2);
        getContext().startService(intent);
    }

    private void actionAfterSwipe(boolean liked) {

        sendBuyerProductResponse(liked);

        if (getActivity() == null) {
            return;
        }

        mProductsLeft -= 1;
        if (mProductsLeft < 1) {
            setNoProductsLeftView();
            return;
        }

        mPosition += 1;
        try {
            mListener.fragmentCreated(mProductsArrayList.get(mPosition).getName());
        } catch (Exception e) {
            e.printStackTrace();
            setNoProductsLeftView();
        }

        if (mProductsLeft < mProductBuffer) {
            getActivity().getSupportLoaderManager().restartLoader(PRODUCTS_DB_LOADER, null, mProductsLoader);
            updateProducts();
        }

        mHasSwiped = true;
        mButtonEnabled = true;

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
            mListener.fragmentCreated(data.get(0).getName());
            mButtonEnabled = true;
            //setButtonsState(true);
        }
        mProductsArrayList.addAll(data);
        for (Product product : data) {
            mExcludeProductIDs.add(product.getProductID());
            if (mProductIDs != null) {
                mProductIDs.remove(Integer.valueOf(product.getProductID()));
            }
        }

        if (mProductIDs != null && mProductIDs.size() == 0) {
            mProductIDs = null;
        }
        mProductSwipeDeckAdapter.notifyDataSetChanged();
        mProductsLeft += data.size();

        if (mProductsLeft < mProductBuffer) {
            updateProducts();
        }
    }

    private void setNoProductsLeftView() {
        //setButtonsState(false);
        mListener.fragmentCreated("Hand Picked For You");
        mSwipeDeck.setVisibility(View.GONE);
        mNoProductsLeft.setVisibility(View.VISIBLE);
        mNoProductsLeftProgressBar.setVisibility(View.GONE);
        mNoProductsLeftTextView.setVisibility(View.VISIBLE);
        if (!FilterClass.isFilterApplied()) {
            mNoProductsLeftTextView.setText(R.string.no_products_left);
        } else {
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
            if (data != null) {
                setViewForProducts(data);
            }
        }


        @Override
        public Loader<ArrayList<Product>> onCreateLoader(final int id, Bundle args) {
            int productLimit;
            ArrayList<Integer> responseCodes = new ArrayList<>();
            responseCodes.add(0);
            // TODO : ?? Also add condition so that buyer product Id is not 0
            if (mProductIDs != null) {
                responseCodes.add(1);
                responseCodes.add(2);
                productLimit = mProductIDs.size();
            } else {
                productLimit = 15;
            }
            return new ProductsLoader(getContext(), mProductIDs, mExcludeProductIDs, responseCodes, null, productLimit);
        }
    }

    @Override
    public void itemClicked(View view, int position, int id) {
        mListener.openProductDetails(mProductsArrayList.get(position).getProductID());
    }

    public void resetProducts() {

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
        //setButtonsState(false);
        mButtonEnabled = false;
    }

    public void updateProducts() {
        fetchBuyerProducts();
        getActivity().getSupportLoaderManager().restartLoader(PRODUCTS_DB_LOADER, null, mProductsLoader);
        if (FilterClass.isFilterApplied()) {
            fetchProducts();
        }
        if (mProductIDs != null) {
            fetchSpecificProducts();
        }
    }

    private void fetchSpecificProducts() {
        Intent intent = new Intent(getContext(), CatalogService.class);
        intent.putExtra("TODO", R.integer.fetch_specific_products);
        intent.putExtra("productIDs", TextUtils.join(",", mProductIDs));
        intent.putExtra("items_per_page", String.valueOf(mProductIDs.size()));
        getActivity().startService(intent);
    }

    private void fetchProducts() {
        if (mTotalProductPages == -1 || mTotalProductPages >= mProductsPageNumber) {
            Intent intent = new Intent(getContext(), CatalogService.class);
            intent.putExtra("TODO", R.integer.fetch_products);
            intent.putExtra("page_number", mProductsPageNumber);
            intent.putExtra("items_per_page", mProductsLimit);
            getActivity().startService(intent);
        }
    }

    private void handleProductAPIResponse(final Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras.getString(Constants.ERROR_RESPONSE) != null) {
            return;
        }
        final int pageNumber = intent.getIntExtra(APIConstants.API_PAGE_NUMBER_KEY, -1);
        final int totalPages = intent.getIntExtra(APIConstants.API_TOTAL_PAGES_KEY, 1);
        final int updatedInserted = intent.getIntExtra(Constants.INSERTED_UPDATED, 0);
        if (updatedInserted > 0) {
            if (mProductsPageNumber == 1 && pageNumber == 1) {
                handleBuyerProductAPIResponse();
            } else if (mProductsPageNumber > pageNumber) {
                handleBuyerProductAPIResponse();
            } else if (mProductsPageNumber == pageNumber) {
                handleBuyerProductAPIResponse();
                mProductsPageNumber++;
            }
        }
        mTotalProductPages = totalPages;
    }
}
