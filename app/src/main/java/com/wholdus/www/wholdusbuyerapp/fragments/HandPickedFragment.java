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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.daprlabs.aaron.swipedeck.SwipeDeck;
import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.adapters.ProductSwipeDeckAdapter;
import com.wholdus.www.wholdusbuyerapp.interfaces.HandPickedListenerInterface;
import com.wholdus.www.wholdusbuyerapp.interfaces.ProductCardListenerInterface;
import com.wholdus.www.wholdusbuyerapp.loaders.ProductsLoader;
import com.wholdus.www.wholdusbuyerapp.models.Product;
import com.wholdus.www.wholdusbuyerapp.services.BuyerProductService;

import java.util.ArrayList;

/**
 * Created by kaustubh on 17/12/16.
 */

public class HandPickedFragment extends Fragment implements ProductCardListenerInterface {

    private HandPickedListenerInterface mListener;
    private final int PRODUCTS_DB_LOADER = 30;
    private BroadcastReceiver mProductServiceResponseReceiver;
    private ProductsLoaderManager mProductsLoader;
    ArrayList<Product> mProductsArrayList;
    SwipeDeck mSwipeDeck;
    ImageButton mLikeButton;
    ImageButton mDislikeButton;
    ImageButton mAddToCartButton;
    ImageButton mSetStorePriceButton;
    ProductSwipeDeckAdapter mProductSwipeDeckAdapter;
    private int mProductsLeft = 0;

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
        mProductServiceResponseReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                handleAPIResponse();
            }
        };
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_handpicked, container, false);
        initReferences(rootView);

        fetchDataFromServer();
        return rootView;
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
        getActivity().getSupportLoaderManager().restartLoader(PRODUCTS_DB_LOADER, null, mProductsLoader);
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
                mSwipeDeck.swipeTopCardRight(180);
            }
        });
        mDislikeButton = (ImageButton) rootView.findViewById(R.id.hand_picked_dislike_button);
        mDislikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSwipeDeck.swipeTopCardLeft(180);
            }
        });
        //TODO : Implement add to cart and set store price fragments
        mAddToCartButton = (ImageButton) rootView.findViewById(R.id.hand_picked_cart_button);
        mSetStorePriceButton = (ImageButton) rootView.findViewById(R.id.hand_picked_set_price_button);

        mProductsArrayList = new ArrayList<>();
        mProductSwipeDeckAdapter = new ProductSwipeDeckAdapter(getContext(), mProductsArrayList, this);
        mSwipeDeck.setAdapter(mProductSwipeDeckAdapter);
    }

    private void fetchDataFromServer(){
        Intent intent = new Intent(getContext(), BuyerProductService.class);
        intent.putExtra("TODO", R.string.fetch_buyer_products);
        getContext().startService(intent);
    }

    private void actionAfterSwipe(boolean liked){
        mProductsLeft -= 1;
        if (mProductsLeft < 5){
            getActivity().getSupportLoaderManager().restartLoader(PRODUCTS_DB_LOADER, null, mProductsLoader);
        }
    }

    @Override
    public void cardCreated(String productName) {
        mListener.fragmentCreated(productName);
    }

    private void setViewForProducts(ArrayList<Product> data){
        //TODO: Add products to adapter correctly(not ones already present)
        mProductsArrayList.addAll(data);
        mProductSwipeDeckAdapter.notifyDataSetChanged();
        mProductsLeft += data.size();

        //TODO: Handle case for 0 products
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
            //TODO : only request products which are not in list
            return new ProductsLoader(getContext());
        }
    }
}
