package com.wholdus.www.wholdusbuyerapp.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.adapters.ThumbImageAdapter;
import com.wholdus.www.wholdusbuyerapp.databaseContracts.CartContract;
import com.wholdus.www.wholdusbuyerapp.databaseContracts.CatalogContract;
import com.wholdus.www.wholdusbuyerapp.fragments.CartDialogFragment;
import com.wholdus.www.wholdusbuyerapp.helperClasses.APIConstants;
import com.wholdus.www.wholdusbuyerapp.helperClasses.CartMenuItemHelper;
import com.wholdus.www.wholdusbuyerapp.helperClasses.Constants;
import com.wholdus.www.wholdusbuyerapp.helperClasses.GlobalAccessHelper;
import com.wholdus.www.wholdusbuyerapp.helperClasses.InputValidationHelper;
import com.wholdus.www.wholdusbuyerapp.helperClasses.IntentFilters;
import com.wholdus.www.wholdusbuyerapp.helperClasses.OkHttpHelper;
import com.wholdus.www.wholdusbuyerapp.helperClasses.ShareIntentClass;
import com.wholdus.www.wholdusbuyerapp.helperClasses.ShortListMenuItemHelper;
import com.wholdus.www.wholdusbuyerapp.helperClasses.TODO;
import com.wholdus.www.wholdusbuyerapp.helperClasses.TrackingHelper;
import com.wholdus.www.wholdusbuyerapp.interfaces.CartDialogListener;
import com.wholdus.www.wholdusbuyerapp.interfaces.ItemClickListener;
import com.wholdus.www.wholdusbuyerapp.loaders.CartItemLoader;
import com.wholdus.www.wholdusbuyerapp.loaders.ProductLoader;
import com.wholdus.www.wholdusbuyerapp.models.CartItem;
import com.wholdus.www.wholdusbuyerapp.models.Product;
import com.wholdus.www.wholdusbuyerapp.services.BuyerProductService;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Response;

import static com.wholdus.www.wholdusbuyerapp.helperClasses.APIConstants.API_TOTAL_ITEMS_KEY;

public class ProductDetailActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Product>, ItemClickListener,
        View.OnClickListener, CartDialogListener {

    private int mProductID;
    private Toolbar mToolbar;
    private Product mProduct;
    private ImageView mDisplayImage;
    private RecyclerView mThumbImagesRecyclerView;
    private TextView mProductName, mProductPrice, mLotSize, mLotDescription,
            mProductFabric, mProductColor, mProductSizes, mProductBrand,
            mProductPattern, mProductStyle, mProductWork, mSellerName, mSellerLocation,
            mSellerSpeciality, mCartButton;
    private ImageView mFavButton;
    private Button mCheckPincodeButton, mMoreProductsFromSellerButton;
    private TextInputEditText mPincodeEditText;
    private TextInputLayout mPincodeWrapper;
    private String mPincodeText = "";
    private BroadcastReceiver mCartServiceResponseReceiver;
    private CartItemLoaderManager mCartItemLoaderManager;

    private static final int PRODUCT_LOADER = 10;
    private static final int CART_ITEM_LOADER = 11;

    public static final String SHIPPING_SHARED_PREFERENCES = "ShippingSharedPreference";
    public static final String PINCODE_KEY = "PincodeKey";
    public static final String SHIPPING_AVAILABLE_KEY = "ShippingAvailableKey";

    private CartMenuItemHelper mCartMenuItemHelper;
    private ShortListMenuItemHelper mShortListMenuItemHelper;

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
        mLotSize = (TextView) findViewById(R.id.lot_size);
        mLotDescription = (TextView) findViewById(R.id.lot_description);
        mProductFabric = (TextView) findViewById(R.id.fabric);
        mProductColor = (TextView) findViewById(R.id.colors);
        mProductSizes = (TextView) findViewById(R.id.sizes);
        mProductBrand = (TextView) findViewById(R.id.brand);

        mProductPattern = (TextView) findViewById(R.id.pattern);
        mProductStyle = (TextView) findViewById(R.id.style);
        mProductWork = (TextView) findViewById(R.id.work);

        mSellerName = (TextView) findViewById(R.id.seller_name);
        mSellerLocation = (TextView) findViewById(R.id.seller_location);
        mSellerSpeciality = (TextView) findViewById(R.id.seller_speciality);

        ImageView shareButton = (ImageView) findViewById(R.id.share_button);
        shareButton.setOnClickListener(this);

        mFavButton = (ImageView) findViewById(R.id.fav_icon);
        mFavButton.setOnClickListener(this);

        mCartButton = (TextView) findViewById(R.id.cart_button);
        mCartButton.setOnClickListener(this);

        mMoreProductsFromSellerButton = (Button) findViewById(R.id.more_products_from_seller_button);
        mMoreProductsFromSellerButton.setOnClickListener(this);

        mPincodeEditText = (TextInputEditText) findViewById(R.id.pincode_edit_text);
        mPincodeWrapper = (TextInputLayout) findViewById(R.id.pincode_wrapper);
        mCheckPincodeButton = (Button) findViewById(R.id.check_pincode);
        mCheckPincodeButton.setOnClickListener(this);

        SharedPreferences shippingPreferences = getSharedPreferences(SHIPPING_SHARED_PREFERENCES, MODE_PRIVATE);
        String pincode = shippingPreferences.getString(PINCODE_KEY, null);
        if (pincode != null) {
            mPincodeText = pincode;
            setPincodeShipping(shippingPreferences.getBoolean(SHIPPING_AVAILABLE_KEY, true));
        }

        getSupportLoaderManager().initLoader(PRODUCT_LOADER, null, this);
        mCartItemLoaderManager = new CartItemLoaderManager(this);


        mCartServiceResponseReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                onReceiveCartIntent(intent);
                refreshCartMenuItemHelper();
            }
        };

        // tracking
        TrackingHelper.getInstance(this).logEvent(
                FirebaseAnalytics.Event.VIEW_ITEM,
                "product_detail_view",
                String.valueOf(mProductID)
        );
        //end tracking
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.default_action_buttons, menu);
        mCartMenuItemHelper = new CartMenuItemHelper(this, menu.findItem(R.id.action_bar_checkout), getSupportLoaderManager());
        refreshCartMenuItemHelper();
        mShortListMenuItemHelper = new ShortListMenuItemHelper(this, menu.findItem(R.id.action_bar_shortlist));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_bar_checkout:
                startActivity(new Intent(this, CartActivity.class));
                break;
            case R.id.action_bar_shortlist:
                Intent shortlistIntent = new Intent(this, CategoryProductActivity.class);
                shortlistIntent.putExtra(Constants.TYPE, Constants.FAV_PRODUCTS);
                startActivity(shortlistIntent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter(IntentFilters.CART_ITEM_WRITTEN);
        LocalBroadcastManager.getInstance(this).registerReceiver(mCartServiceResponseReceiver, intentFilter);
        loadCartCount();
        refreshCartMenuItemHelper();
        if (mShortListMenuItemHelper != null){
            mShortListMenuItemHelper.refreshShortListCount();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mCartServiceResponseReceiver);
        } catch (Exception e) {}
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCartServiceResponseReceiver = null;
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
        if (data != null) {
            mProduct = data;
            setDataToView();
        }
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
                if (mShortListMenuItemHelper != null){
                    if (mProduct.getLikeStatus()){
                        mShortListMenuItemHelper.incrementShortListCount();
                    } else {
                        mShortListMenuItemHelper.decrementShortListCount();
                    }
                }

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
            case R.id.check_pincode:
                if (mCheckPincodeButton.getText().equals(getString(R.string.check))) {
                    mPincodeText = mPincodeEditText.getText().toString();
                    if (InputValidationHelper.isValidPincode(mPincodeWrapper, mPincodeText)) {
                        startPincodeCheckRequest();
                        mCheckPincodeButton.setEnabled(false);
                    }
                } else {
                    SharedPreferences shippingPreferences = getSharedPreferences(SHIPPING_SHARED_PREFERENCES, MODE_PRIVATE);
                    SharedPreferences.Editor editor = shippingPreferences.edit();
                    editor.clear();
                    editor.apply();
                    clearPincodeCheck();
                }
                break;
            case R.id.more_products_from_seller_button:
                if (mProduct!= null){
                    Intent sellerProductsIntent = new Intent(this, CategoryProductActivity.class);
                    sellerProductsIntent.putExtra(CatalogContract.SellersTable.COLUMN_SELLER_ID, mProduct.getSellerID());
                    sellerProductsIntent.putExtra(Constants.TYPE, Constants.MANUFACTURER_PRODUCTS);
                    startActivity(sellerProductsIntent);
                }
        }
    }

    @Override
    public void dismissDialog() {
        loadCartCount();
    }

    private void loadCartCount() {
        refreshCartMenuItemHelper();
        getSupportLoaderManager().restartLoader(CART_ITEM_LOADER, null, mCartItemLoaderManager);
    }

    private void clearPincodeCheck() {
        mPincodeWrapper.setHintEnabled(true);
        mPincodeEditText.setHint("Pincode");
        mCheckPincodeButton.setText(getString(R.string.check));
        mPincodeEditText.setText("");
        mPincodeEditText.setEnabled(true);
    }

    private void setPincodeShipping(boolean shippingAvailable) {
        mPincodeWrapper.setHintEnabled(false);
        mCheckPincodeButton.setText("Change");
        mCheckPincodeButton.setEnabled(true);
        if (shippingAvailable) {
            mPincodeEditText.setText("Delivery at " + mPincodeText);
        } else {
            mPincodeEditText.setText("No delivery at " + mPincodeText);
        }
        mPincodeEditText.setEnabled(false);
    }

    private void startPincodeCheckRequest() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HashMap<String, String> params = new HashMap<>();
                    params.put("pincode_code", mPincodeText);
                    params.put("cod_available", "1");
                    params.put("regular_delivery_available", "1");
                    String url = GlobalAccessHelper.generateUrl(APIConstants.PINCODE_SERVICEABILITY_URL, params);
                    Response response = OkHttpHelper.makeGetRequest(getApplicationContext(), url);
                    if (response.isSuccessful()) {
                        JSONObject responseJSON = new JSONObject(response.body().string());
                        final int totalItems = responseJSON.getInt(API_TOTAL_ITEMS_KEY);
                        response.body().close();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                SharedPreferences shippingPreferences = getSharedPreferences(SHIPPING_SHARED_PREFERENCES, MODE_PRIVATE);
                                SharedPreferences.Editor editor = shippingPreferences.edit();
                                editor.putString(PINCODE_KEY, mPincodeText);
                                editor.putBoolean(SHIPPING_AVAILABLE_KEY, totalItems > 0);
                                editor.apply();
                                setPincodeShipping(totalItems > 0);
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                clearPincodeCheck();
                            }
                        });
                    }
                } catch (Exception e) {
                    try {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                clearPincodeCheck();
                            }
                        });
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void loadDisplayImage(int position) {
        Glide.with(this)
                .load(mProduct.getImageUrl(Constants.LARGE_IMAGE, mProduct.getProductImageNumbers()[position]))
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .skipMemoryCache(true)
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
            onBackPressed();
        }
    }

    private void setDataToView() {
        mToolbar.setTitle(Html.fromHtml("<small>" + mProduct.getName() + "</small>"));

        ArrayList<String> imageUrls = mProduct.getAllImageUrls(Constants.EXTRA_SMALL_IMAGE);
        if (imageUrls.size() == 0) {
            // no Image is present, set dummy image
            mDisplayImage.setImageResource(R.drawable.image_not_available);

            // Remove Thumb Image Section from View
            mThumbImagesRecyclerView.setVisibility(View.GONE);
        } else {
            loadDisplayImage(0); // load image
            mThumbImagesRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.HORIZONTAL));
            ThumbImageAdapter mThumbImageAdapter = new ThumbImageAdapter(this, imageUrls, this);
            mThumbImagesRecyclerView.setAdapter(mThumbImageAdapter);
            if (imageUrls.size() == 1) { // Remove Thumb Image Section from View
                mThumbImagesRecyclerView.setVisibility(View.GONE);
            }
        }

        mProductName.setText(mProduct.getProductDetails().getDisplayName());
        mProductPrice.setText(String.format(getString(R.string.price_per_pcs_format), String.valueOf((int) Math.ceil(mProduct.getMinPricePerUnit()))));
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

        if (mProduct.getSeller() != null) {
            mSellerName.setText(mProduct.getSeller().getCompanyName());
            mSellerSpeciality.setText(mProduct.getSeller().getCompanyProfile());
            if (mProduct.getSeller().getSellerAddress() != null) {
                mSellerLocation.setText(mProduct.getSeller().getSellerAddress().getCity());
            }
        }

        setFavButtonImage();

        if (mProduct.getDeleteStatus() || !mProduct.getShowOnline()){
            mCartButton.setEnabled(false);
            mCartButton.setText(R.string.out_of_stock_key);
        }
    }

    private void setFavButtonImage() {
        mFavButton.setImageResource(mProduct.getLikeStatus() ? R.drawable.ic_favorite_red_24dp : R.drawable.ic_favorite_border_black_24dp);
    }

    private class CartItemLoaderManager implements LoaderManager.LoaderCallbacks<ArrayList<CartItem>> {
        private Context mContext;

        CartItemLoaderManager(Context context) {
            mContext = context;
        }

        @Override
        public void onLoaderReset(Loader<ArrayList<CartItem>> loader) {

        }

        @Override
        public void onLoadFinished(Loader<ArrayList<CartItem>> loader, ArrayList<CartItem> data) {
            if (data != null && !data.isEmpty()) {
                setCartButtonText(data.get(0).getPieces());
            } else {
                setCartButtonText(0);
            }
        }

        @Override
        public Loader<ArrayList<CartItem>> onCreateLoader(int id, Bundle args) {
            return new CartItemLoader(mContext, -1, null, -1, mProductID, -1, false, null);
        }
    }

    private void setCartButtonText(int pieces) {
        if (mProduct!= null && (mProduct.getDeleteStatus() || !mProduct.getShowOnline())){
            mCartButton.setEnabled(false);
            mCartButton.setText(R.string.out_of_stock_key);
            return;
        }
        if (pieces == 0) {
            mCartButton.setText(getString(R.string.add_to_cart));
            return;
        }

        String buttonText = String.valueOf(pieces);
        if (pieces == 1) {
            buttonText += " pc in cart";
        } else {
            buttonText += " pcs in cart";
        }
        mCartButton.setText(buttonText);
    }

    private void onReceiveCartIntent(Intent intent) {
        if (intent != null) {
            try {
                Bundle bundle = intent.getBundleExtra("extra");
                int pieces = bundle.getInt(CartContract.CartItemsTable.COLUMN_PIECES);
                setCartButtonText(pieces);
            } catch (Exception e) {

            }
        }
    }

    private void refreshCartMenuItemHelper(){
        if (mCartMenuItemHelper != null) {
            mCartMenuItemHelper.restartLoader();
        }
    }

}
