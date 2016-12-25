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
import com.wholdus.www.wholdusbuyerapp.interfaces.HandPickedListenerInterface;
import com.wholdus.www.wholdusbuyerapp.interfaces.ItemClickListener;
import com.wholdus.www.wholdusbuyerapp.interfaces.ProductCardListenerInterface;
import com.wholdus.www.wholdusbuyerapp.loaders.ProductsLoader;
import com.wholdus.www.wholdusbuyerapp.models.Product;
import com.wholdus.www.wholdusbuyerapp.services.BuyerProductService;
import com.wholdus.www.wholdusbuyerapp.services.CartService;

import java.util.ArrayList;

/**
 * Created by kaustubh on 17/12/16.
 */

public class HandPickedFragment extends Fragment implements ProductCardListenerInterface, ItemClickListener {

    private HandPickedListenerInterface mListener;
    private final int PRODUCTS_DB_LOADER = 30;
    private BroadcastReceiver mProductServiceResponseReceiver;
    private ProductsLoaderManager mProductsLoader;
    ArrayList<Product> mProductsArrayList;
    ArrayList<Integer> mProductIDs;
    //private int mCurrentProductID;
    private Float mStoreMargin;
    SwipeDeck mSwipeDeck;
    ImageButton mLikeButton;
    ImageButton mDislikeButton;
    ImageButton mAddToCartButton;
    ImageButton mSetStorePriceButton;
    ProductSwipeDeckAdapter mProductSwipeDeckAdapter;
    LinearLayout mNoProductsLeft;
    TextView mNoProductsLeftTextView;
    ProgressBar mNoProductsLeftProgressBar;
    private int mPosition = 0;
    private int mProductsLeft = 0;
    private int mProductBuffer = 8;
    private boolean mHasSwiped = true;
    private boolean mFirstLoad = true;

    public HandPickedFragment(){
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
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_handpicked, container, false);
        initReferences(rootView);
        mProductServiceResponseReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                handleAPIResponse();
            }
        };
        fetchDataFromServer();
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mListener.fragmentCreated("Wholdus");
    }

    @Override
    public void onResume(){
        super.onResume();

        mProductsLoader = new ProductsLoaderManager();
        getActivity().getSupportLoaderManager().restartLoader(PRODUCTS_DB_LOADER, null, mProductsLoader);

        IntentFilter intentFilter = new IntentFilter(getString(R.string.buyer_product_data_updated));
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mProductServiceResponseReceiver, intentFilter);

    }

    private void handleAPIResponse() {
        if (getActivity()!= null && mProductsLeft < mProductBuffer) {
            getActivity().getSupportLoaderManager().restartLoader(PRODUCTS_DB_LOADER, null, mProductsLoader);
        }
    }

    private void initReferences(ViewGroup rootView){

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
                //args.putInt("LayoutWidth", );
                //args.putInt("LayoutHeight", );
                dialogFragment.setArguments(args);
                dialogFragment.show(fragmentManager, dialogFragment.getClass().getSimpleName());
            }
        });

        mSetStorePriceButton = (ImageButton) rootView.findViewById(R.id.hand_picked_set_price_button);
        setButtonsState(false);

        mProductsArrayList = new ArrayList<>();
        mProductIDs = new ArrayList<>();
        mProductSwipeDeckAdapter = new ProductSwipeDeckAdapter(getContext(), mProductsArrayList, this, this);
        mSwipeDeck.setAdapter(mProductSwipeDeckAdapter);

        mNoProductsLeft = (LinearLayout) rootView.findViewById(R.id.no_products_left);
        mNoProductsLeftTextView = (TextView) rootView.findViewById(R.id.no_products_left_text_view);
        mNoProductsLeftProgressBar = (ProgressBar) rootView.findViewById(R.id.loading_indicator);
        mNoProductsLeft.setVisibility(View.VISIBLE);
        mNoProductsLeftTextView.setVisibility(View.GONE);
    }

    private void fetchDataFromServer(){

        fetchBuyerProducts();

        Intent cartServiceIntent = new Intent(getContext(), CartService.class);
        cartServiceIntent.putExtra("TODO", R.string.fetch_cart);
        getContext().startService(cartServiceIntent);
    }

    private void fetchBuyerProducts(){
        Intent intent = new Intent(getContext(), BuyerProductService.class);
        intent.putExtra("TODO", R.string.fetch_buyer_products);
        getContext().startService(intent);
    }

    private void actionAfterSwipe(boolean liked){
        Intent intent = new Intent(getContext(), BuyerProductService.class);
        intent.putExtra("TODO", R.string.update_product_response);
        intent.putExtra(CatalogContract.ProductsTable.COLUMN_PRODUCT_ID, mProductsArrayList.get(mPosition).getProductID());
        if (mStoreMargin != null) {
            intent.putExtra(CatalogContract.ProductsTable.COLUMN_STORE_MARGIN, mStoreMargin);
        }
        intent.putExtra(CatalogContract.ProductsTable.COLUMN_RESPONDED_FROM, 0);
        intent.putExtra(CatalogContract.ProductsTable.COLUMN_HAS_SWIPED, mHasSwiped);
        intent.putExtra(CatalogContract.ProductsTable.COLUMN_RESPONSE_CODE, liked?1:2);
        getContext().startService(intent);

        mHasSwiped = true;
        mStoreMargin = null;

        mProductsLeft -= 1;
        if (mProductsLeft == 0){
            setNoProductsLeftView();
            return;
        }

        mPosition += 1;
        mListener.fragmentCreated(mProductsArrayList.get(mPosition).getName());

        if (mProductsLeft < mProductBuffer){
            getActivity().getSupportLoaderManager().restartLoader(PRODUCTS_DB_LOADER, null, mProductsLoader);
            fetchBuyerProducts();
        }
    }

    @Override
    public void cardCreated() {
        //mCurrentProductID = mProductsArrayList.get(mPosition).getProductID();
        //setButtonsState(true);
    }

    private void setViewForProducts(ArrayList<Product> data){
        //TODO: Add products to adapter correctly(not ones already present)

        if(data.size() == 0){
            if (!mFirstLoad && mProductsArrayList.size() == 0){
                setNoProductsLeftView();
            }
            if (mFirstLoad){
                mFirstLoad = false;
            }
            return;
        }

        if (mFirstLoad || mProductsArrayList.size() == 0){
            mNoProductsLeft.setVisibility(View.GONE);
            mFirstLoad = false;
            mListener.fragmentCreated(data.get(mPosition).getName());
            setButtonsState(true);
        }
        mProductsArrayList.addAll(data);
        for(Product product:data){
            mProductIDs.add(product.getProductID());
        }
        mProductSwipeDeckAdapter.notifyDataSetChanged();
        mProductsLeft += data.size();

        //TODO: Handle case for 0 products
    }

    private void setNoProductsLeftView(){
        setButtonsState(false);
        mListener.fragmentCreated("Hand Picked For You");
        mSwipeDeck.setVisibility(View.GONE);
        mNoProductsLeft.setVisibility(View.VISIBLE);
        mNoProductsLeftProgressBar.setVisibility(View.GONE);
        mNoProductsLeftTextView.setVisibility(View.VISIBLE);
        mNoProductsLeftTextView.setText(R.string.no_products_left);
    }

    private void setButtonsState(boolean state){
        mLikeButton.setEnabled(state);
        mDislikeButton.setEnabled(state);
        mAddToCartButton.setEnabled(state);
        mSetStorePriceButton.setEnabled(state);
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
            ArrayList<Integer> responseCodes = new ArrayList<>();
            responseCodes.add(0);
            // TODO : ?? Also add condition so that buyer product Id is not 0
            return new ProductsLoader(getContext(), null, mProductIDs, responseCodes, null, 15);
        }
    }

    @Override
    public void itemClicked(View view, int position, int id) {
        mListener.openProductDetails(mProductsArrayList.get(position).getProductID());
    }
}
