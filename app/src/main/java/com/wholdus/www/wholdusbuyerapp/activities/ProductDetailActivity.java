package com.wholdus.www.wholdusbuyerapp.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.adapters.ThumbImageAdapter;
import com.wholdus.www.wholdusbuyerapp.databaseContracts.CartContract;
import com.wholdus.www.wholdusbuyerapp.databaseContracts.CatalogContract;
import com.wholdus.www.wholdusbuyerapp.fragments.CartDialogFragment;
import com.wholdus.www.wholdusbuyerapp.helperClasses.Constants;
import com.wholdus.www.wholdusbuyerapp.helperClasses.ShareIntentClass;
import com.wholdus.www.wholdusbuyerapp.helperClasses.TODO;
import com.wholdus.www.wholdusbuyerapp.interfaces.ItemClickListener;
import com.wholdus.www.wholdusbuyerapp.loaders.CartItemLoader;
import com.wholdus.www.wholdusbuyerapp.loaders.ProductLoader;
import com.wholdus.www.wholdusbuyerapp.models.CartItem;
import com.wholdus.www.wholdusbuyerapp.models.Product;
import com.wholdus.www.wholdusbuyerapp.services.BuyerProductService;

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
    private ImageButton mShareButton, mFavButton;
    private Button mCartButton;
    private BroadcastReceiver mCartServiceResponseReceiver;

    private static final int PRODUCT_LOADER = 10;
    private static final int CART_ITEM_LOADER = 11;

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

        mShareButton = (ImageButton) findViewById(R.id.share_button);
        mShareButton.setOnClickListener(this);

        mFavButton = (ImageButton) findViewById(R.id.fav_icon);
        mFavButton.setOnClickListener(this);

        mCartButton = (Button) findViewById(R.id.cart_button);
        mCartButton.setOnClickListener(this);

        getSupportLoaderManager().initLoader(PRODUCT_LOADER, null, this);

        getSupportLoaderManager().initLoader(CART_ITEM_LOADER, null, new CartItemLoaderManager(this));

        mCartServiceResponseReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                onReceiveCartIntent(intent);
            }
        };

        IntentFilter intentFilter = new IntentFilter(getString(R.string.cart_item_written));
        LocalBroadcastManager.getInstance(this).registerReceiver(mCartServiceResponseReceiver, intentFilter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.default_action_buttons, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mCartServiceResponseReceiver != null) {
            try {
                LocalBroadcastManager.getInstance(this).unregisterReceiver(mCartServiceResponseReceiver);
            } catch (Exception e){

            }
            mCartServiceResponseReceiver = null;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_bar_checkout:
                startActivity(new Intent(this, CartActivity.class));
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
        if (data != null){
            mProduct = data;
            setDataToView();
        }
        // TODO Handle case for product ID not found
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
                mProduct.toggleLikeStatus();
                setFavButtonImage();

                intent = new Intent(this, BuyerProductService.class);
                intent.putExtra("TODO", TODO.UPDATE_PRODUCT_RESPONSE);
                intent.putExtra(CatalogContract.ProductsTable.COLUMN_PRODUCT_ID, mProduct.getProductID());
                intent.putExtra(CatalogContract.ProductsTable.COLUMN_RESPONDED_FROM, 2);
                intent.putExtra(CatalogContract.ProductsTable.COLUMN_HAS_SWIPED, false);
                intent.putExtra(CatalogContract.ProductsTable.COLUMN_RESPONSE_CODE, mProduct.getLikeStatus() ? 1 : 2);

                startService(intent);
                break;
            case R.id.cart_button:
                FragmentManager fragmentManager = getSupportFragmentManager();
                CartDialogFragment dialogFragment = new CartDialogFragment();
                Bundle args = new Bundle();
                args.putInt(CatalogContract.ProductsTable.COLUMN_PRODUCT_ID, mProductID);
                dialogFragment.setArguments(args);
                dialogFragment.show(fragmentManager, dialogFragment.getClass().getSimpleName());
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

        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
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
        if (mProduct.getProductDetails().getLotDescription().equals("")) {
            mLotDescription.setText(String.valueOf(mProduct.getLotSize()) + " pieces per lot");
        } else {
            mLotDescription.setText(mProduct.getProductDetails().getLotDescription());
        }

        mProductFabric.setText(mProduct.getFabricGSM());
        mProductColor.setText(mProduct.getColours());
        mProductSizes.setText(mProduct.getSizes());
        mProductBrand.setText(mProduct.getSeller().getCompanyName());

        mProductPattern.setText(mProduct.getProductDetails().getPackagingDetails());
        mProductStyle.setText(mProduct.getProductDetails().getStyle());
        mProductWork.setText(mProduct.getProductDetails().getWorkDecorationType());

        setFavButtonImage();
    }

    private void setFavButtonImage(){
        mFavButton.setImageResource(mProduct.getLikeStatus() ? R.drawable.ic_favorite_red_24dp : R.drawable.ic_favorite_border_black_24dp);
    }

    private class CartItemLoaderManager implements LoaderManager.LoaderCallbacks<ArrayList<CartItem>> {
        private Context mContext;
        CartItemLoaderManager(Context context){
            mContext = context;
        }
        @Override
        public void onLoaderReset(Loader<ArrayList<CartItem>> loader) {

        }

        @Override
        public void onLoadFinished(Loader<ArrayList<CartItem>> loader, ArrayList<CartItem> data) {
            if (!data.isEmpty()) {
                setCartButtonText(data.get(0).getPieces());
            }
        }

        @Override
        public Loader<ArrayList<CartItem>> onCreateLoader(int id, Bundle args) {
            return new CartItemLoader(mContext, -1, null, -1, mProductID, -1, false, null);
        }
    }

    private void setCartButtonText(int pieces){
        String buttonText = String.valueOf(pieces);
        if (pieces == 1){
            buttonText += " pc in cart";
        } else {
            buttonText += " pcs in cart";
        }
        mCartButton.setText(buttonText);
    }

    private void onReceiveCartIntent(Intent intent){
        if (intent != null){
            try {
                Bundle bundle = intent.getBundleExtra("extra");
                int pieces = bundle.getInt(CartContract.CartItemsTable.COLUMN_PIECES);
                setCartButtonText(pieces);
            }catch (Exception e){

            }
        }
    }

}
