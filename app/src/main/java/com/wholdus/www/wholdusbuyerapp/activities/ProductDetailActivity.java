package com.wholdus.www.wholdusbuyerapp.activities;

import android.content.Intent;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.adapters.ThumbImageAdapter;
import com.wholdus.www.wholdusbuyerapp.databaseContracts.CatalogContract;
import com.wholdus.www.wholdusbuyerapp.helperClasses.Constants;
import com.wholdus.www.wholdusbuyerapp.helperClasses.HelperFunctions;
import com.wholdus.www.wholdusbuyerapp.interfaces.ItemClickListener;
import com.wholdus.www.wholdusbuyerapp.loaders.ProductLoader;
import com.wholdus.www.wholdusbuyerapp.models.Product;
import com.wholdus.www.wholdusbuyerapp.singletons.VolleySingleton;

import java.util.ArrayList;

public class ProductDetailActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Product>, ItemClickListener {

    private int mProductID;
    private Toolbar mToolbar;
    private Product mProduct;
    private ImageLoader mImageLoader;
    private NetworkImageView mDisplayImage;
    private RecyclerView mThumbImagesRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private ThumbImageAdapter mThumbImageAdapter;

    private static final int PRODUCT_LOADER = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        setProductID();

        mImageLoader = VolleySingleton.getInstance(this).getImageLoader();
        mDisplayImage = (NetworkImageView) findViewById(R.id.display_image);
        mDisplayImage.requestFocus();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

//        mDisplayImage.setMinimumWidth((int)HelperFunctions.convertPixelsToDp(displayMetrics.widthPixels, this));
//        mDisplayImage.setMinimumHeight((int)HelperFunctions.convertPixelsToDp(displayMetrics.widthPixels, this));

        new Thread(new Runnable() {
            @Override
            public void run() {
                initToolbar();
                mThumbImagesRecyclerView = (RecyclerView) findViewById(R.id.thumb_images_recycler_view);
                mLinearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
                mThumbImagesRecyclerView.setLayoutManager(mLinearLayoutManager);
            }
        }).start();

        getSupportLoaderManager().initLoader(PRODUCT_LOADER, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.default_action_buttons, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_bar_checkout:
                startActivity(new Intent(this, CheckoutActivity.class));
                break;
            case R.id.action_bar_store_home:
                startActivity(new Intent(this, StoreActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public Loader<Product> onCreateLoader(int id, Bundle args) {
        return new ProductLoader(this, mProductID);
    }

    @Override
    public void onLoadFinished(Loader<Product> loader, Product data) {
        mProduct = data;
        setDataToView();
    }

    @Override
    public void onLoaderReset(Loader<Product> loader) {
        mProduct = null;
    }

    @Override
    public void itemClicked(int position, int id) {
        mDisplayImage.setImageUrl(mProduct.getImageUrl(Constants.LARGE_IMAGE,
                mProduct.getProductImageNumbers()[position]), mImageLoader);
    }

    private void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.default_toolbar);
        setSupportActionBar(mToolbar);

        mToolbar.setNavigationIcon(R.drawable.ic_keyboard_arrow_left_white_32dp);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void setProductID() {
        mProductID = getIntent().getIntExtra(CatalogContract.ProductsTable.TABLE_NAME, 0);
        if (mProductID == 0) {
            Log.d(this.getClass().getSimpleName(), mProductID + " - this is not a valid product ID");
            onBackPressed();
        }
    }

    private void setDataToView() {
        mToolbar.setTitle(mProduct.getName());

        ArrayList<String> imageUrls = mProduct.getAllImageUrls(Constants.EXTRA_SMALL_IMAGE);
        if (imageUrls.size() == 0) {
            // no Image is present, set dummy image
            mDisplayImage.setImageResource(R.drawable.slide_1); /* TODO: Save Dummy Image */

            // Remove Thumb Image Section from View
            mThumbImagesRecyclerView.setVisibility(View.GONE);
        } else {
            mDisplayImage.setImageUrl(mProduct.getImageUrl(Constants.LARGE_IMAGE,
                    mProduct.getProductImageNumbers()[0]), mImageLoader);

            if (imageUrls.size() == 1) {
                // Remove Thumb Image Section from View
                mThumbImagesRecyclerView.setVisibility(View.GONE);
            } else {
                mThumbImageAdapter = new ThumbImageAdapter(this, imageUrls, this);
                mThumbImagesRecyclerView.setAdapter(mThumbImageAdapter);
            }
        }
    }
}
