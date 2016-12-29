package com.wholdus.www.wholdusbuyerapp.activities;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.integration.okhttp3.OkHttpGlideModule;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.adapters.ThumbImageAdapter;
import com.wholdus.www.wholdusbuyerapp.databaseContracts.CatalogContract;
import com.wholdus.www.wholdusbuyerapp.helperClasses.Constants;
import com.wholdus.www.wholdusbuyerapp.helperClasses.ShareIntentClass;
import com.wholdus.www.wholdusbuyerapp.interfaces.ItemClickListener;
import com.wholdus.www.wholdusbuyerapp.loaders.ProductLoader;
import com.wholdus.www.wholdusbuyerapp.models.Product;

import java.util.ArrayList;

public class ProductDetailActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Product>, ItemClickListener,
        View.OnClickListener {

    private int mProductID;
    private Toolbar mToolbar;
    private Product mProduct;
    private ImageView mDisplayImage;
    private RecyclerView mThumbImagesRecyclerView;
    private TextView mProductName, mProductPrice, mProductMrp, mLotSize, mLotDescription,
            mProductFabric, mProductColor, mProductSizes, mProductBrand,
            mProductPattern, mProductStyle, mProductWork, mSellerLocation, mSellerSpeciality;

    private static final int PRODUCT_LOADER = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        setProductID();

        initToolbar();

        mDisplayImage = (ImageView) findViewById(R.id.display_image);
        mDisplayImage.setOnClickListener(this);
        mThumbImagesRecyclerView = (RecyclerView) findViewById(R.id.thumb_images_recycler_view);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        mThumbImagesRecyclerView.setLayoutManager(mLinearLayoutManager);

        mProductName = (TextView) findViewById(R.id.product_name);
        mProductPrice = (TextView) findViewById(R.id.product_price);
        mProductMrp = (TextView) findViewById(R.id.product_mrp);
        mLotSize = (TextView) findViewById(R.id.lot_size);
        mLotDescription = (TextView) findViewById(R.id.lot_description);
        mProductFabric = (TextView) findViewById(R.id.fabric);
        mProductColor = (TextView) findViewById(R.id.colors);
        mProductSizes = (TextView) findViewById(R.id.sizes);
        mProductBrand = (TextView) findViewById(R.id.brand);

        mProductPattern = (TextView) findViewById(R.id.pattern);
        mProductStyle = (TextView) findViewById(R.id.style);
        mProductWork = (TextView) findViewById(R.id.work);

        mSellerLocation = (TextView) findViewById(R.id.seller_location);
        mSellerSpeciality = (TextView) findViewById(R.id.seller_speciality);

        ImageButton shareButton = (ImageButton) findViewById(R.id.share_button);
        shareButton.setOnClickListener(this);

        ImageButton favButton = (ImageButton) findViewById(R.id.fav_icon);
        favButton.setOnClickListener(this);

        Button cartButton = (Button) findViewById(R.id.cart_button);
        cartButton.setOnClickListener(this);

        getSupportLoaderManager().initLoader(PRODUCT_LOADER, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.default_action_buttons, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onStop() {
        super.onStop();
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
        finish();
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
    public void itemClicked(View view, int position, int id) {
        loadDisplayImage(position);
    }

    @Override
    public void onClick(View view) {
        final int ID = view.getId();
        switch (ID) {
            case R.id.display_image:
                Intent intent = new Intent(this, ProductGalleryActivity.class);
                intent.putExtra(CatalogContract.ProductsTable.TABLE_NAME, mProductID);
                startActivity(intent);
                break;
            case R.id.fav_icon:
                Toast.makeText(this, "fav button clicked", Toast.LENGTH_SHORT).show();
                break;
            case R.id.cart_button:
                Toast.makeText(this, "cart button clicked", Toast.LENGTH_SHORT).show();
                break;
            case R.id.share_button:
                ShareIntentClass.shareImage(this, mDisplayImage, mProduct.getProductDetails().getDisplayName());
                break;
        }
    }

    private void loadDisplayImage(int position) {
        Glide.with(this)
                .load(mProduct.getImageUrl(Constants.LARGE_IMAGE, mProduct.getProductImageNumbers()[position]))
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .skipMemoryCache(true)
                .thumbnail(0.05f)
                .into(mDisplayImage);
    }

    private void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.default_toolbar);
        setSupportActionBar(mToolbar);

        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
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
            mDisplayImage.setImageResource(R.drawable.image_not_available);

            // Remove Thumb Image Section from View
            mThumbImagesRecyclerView.setVisibility(View.GONE);
        } else {
            loadDisplayImage(0); // load image
            mThumbImagesRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
                @Override
                public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                    outRect.left = 15;
                }
            });
            ThumbImageAdapter mThumbImageAdapter = new ThumbImageAdapter(this, imageUrls, this);
            mThumbImagesRecyclerView.setAdapter(mThumbImageAdapter);
            if (imageUrls.size() == 1) { // Remove Thumb Image Section from View
                mThumbImagesRecyclerView.setVisibility(View.GONE);
            }
        }

        mProductName.setText(mProduct.getProductDetails().getDisplayName());
        mProductPrice.setText(String.format(getString(R.string.price_per_pcs_format), String.valueOf(mProduct.getMinPricePerUnit())));
        mProductMrp.setText(String.format(getString(R.string.price_format), String.valueOf(mProduct.getPricePerUnit())));
        mLotSize.setText(String.valueOf(mProduct.getLotSize()));
        mLotDescription.setText(mProduct.getProductDetails().getLotDescription());

        mProductFabric.setText(mProduct.getFabricGSM());
        mProductColor.setText(mProduct.getColours());
        mProductSizes.setText(mProduct.getSizes());
        mProductBrand.setText(mProduct.getSeller().getCompanyName());

        mProductPattern.setText(mProduct.getProductDetails().getPackagingDetails());
        mProductStyle.setText(mProduct.getProductDetails().getStyle());
        mProductWork.setText(mProduct.getProductDetails().getWorkDecorationType());
    }
}
