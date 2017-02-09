package com.wholdus.www.wholdusbuyerapp.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.crash.FirebaseCrash;
import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.databaseContracts.UserProfileContract;
import com.wholdus.www.wholdusbuyerapp.fragments.BuyerAddressFragment;
import com.wholdus.www.wholdusbuyerapp.fragments.CartSummaryFragment;
import com.wholdus.www.wholdusbuyerapp.fragments.CheckoutAddressConfirmFragment;
import com.wholdus.www.wholdusbuyerapp.fragments.CheckoutPaymentMethodFragment;
import com.wholdus.www.wholdusbuyerapp.fragments.EditAddressFragment;
import com.wholdus.www.wholdusbuyerapp.fragments.HandPickedFragment;
import com.wholdus.www.wholdusbuyerapp.fragments.OrderDetailsFragment;
import com.wholdus.www.wholdusbuyerapp.helperClasses.APIConstants;
import com.wholdus.www.wholdusbuyerapp.helperClasses.Constants;
import com.wholdus.www.wholdusbuyerapp.helperClasses.GlobalAccessHelper;
import com.wholdus.www.wholdusbuyerapp.helperClasses.IntentFilters;
import com.wholdus.www.wholdusbuyerapp.helperClasses.TODO;
import com.wholdus.www.wholdusbuyerapp.interfaces.CartDialogListener;
import com.wholdus.www.wholdusbuyerapp.interfaces.CartListenerInterface;
import com.wholdus.www.wholdusbuyerapp.interfaces.OrderDetailsListenerInterface;
import com.wholdus.www.wholdusbuyerapp.interfaces.UserAddressInterface;
import com.wholdus.www.wholdusbuyerapp.models.Cart;
import com.wholdus.www.wholdusbuyerapp.services.CartService;
import com.wholdus.www.wholdusbuyerapp.services.OrderService;
import com.wholdus.www.wholdusbuyerapp.singletons.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import static com.wholdus.www.wholdusbuyerapp.fragments.CheckoutPaymentMethodFragment.COD_CHARGE_PERCENT;

public class CartActivity extends AppCompatActivity implements CartListenerInterface,
        UserAddressInterface, OrderDetailsListenerInterface, CartDialogListener {

    private int mStatus = 0;
    private int mBuyerAddressID = 0;
    private int mPaymentMethod = -1;
    private Cart mCart;
    private Integer mCheckoutID;
    private int mOrderID = -1;

    private Toolbar mToolbar;
    private TextView mTotalTextView, mProductsPiecesTextView, mProceedButton;
    private LinearLayout mProceedButtonLayout;
    private ProgressBar mProgressBar;

    private BroadcastReceiver mOrderServiceResponseReceiver, mUserAddressServiceResponseReceiver, mCartServiceBroadcastReceiver;

    public static final String REQUEST_TAG = "CHECKOUT_API_REQUESTS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        mProgressBar = (ProgressBar) findViewById(R.id.loading_indicator);
        initToolbar();
        initReferences();
        openToFragment(getFragmentToOpenName(savedInstanceState), null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setViewForProceedButtonLayout();
        mCartServiceBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String intentAction = intent.getAction();
                if (intentAction == null){
                    return;
                }
                switch (intentAction){
                    case IntentFilters.CART_ITEM_WRITTEN:
                        enableProgressBar();
                        break;
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(IntentFilters.CART_ITEM_WRITTEN);
        LocalBroadcastManager.getInstance(this).registerReceiver(mCartServiceBroadcastReceiver, intentFilter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == EditAddressFragment.REQUEST_CHECK_SETTINGS) {
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.cart_fragment_container);
            if (fragment != null && fragment instanceof EditAddressFragment) {
                EditAddressFragment activeFragment = (EditAddressFragment) fragment;
                activeFragment.onActivityResult(requestCode, resultCode, data);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mOrderServiceResponseReceiver);
        } catch (Exception e) {

        }
        try {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mUserAddressServiceResponseReceiver);
        } catch (Exception e) {

        }
        try {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mCartServiceBroadcastReceiver);
        } catch (Exception e) {

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mOrderServiceResponseReceiver = null;
        mUserAddressServiceResponseReceiver = null;
        mCartServiceBroadcastReceiver = null;
    }

    private void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.default_toolbar);
        setSupportActionBar(mToolbar);
    }

    private void initReferences() {
        mTotalTextView = (TextView) findViewById(R.id.cart_summary_total_price_text_view);
        mProductsPiecesTextView = (TextView) findViewById(R.id.cart_summary_total_products_text_view);
        mTotalTextView = (TextView) findViewById(R.id.cart_summary_total_price_text_view);
        mProceedButtonLayout = (LinearLayout) findViewById(R.id.proceed_button_layout);
        mProceedButton = (TextView) findViewById(R.id.cart_summary_proceed_button);
        mProceedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                proceedButtonClicked();
            }
        });
        mProceedButton.setEnabled(false);
    }

    @Override
    public void fragmentCreated(String title, boolean backEnabled) {
        try {
            mToolbar.setTitle(title);
            mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
        } catch (Exception e) {
            FirebaseCrash.report(e);
        }
    }

    @Override
    public void onBackPressed() {
        //TODO : Do not let user cancel transaction
        if (mStatus >= 1 && mStatus < 3) {
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            openToFragment(CartSummaryFragment.class.getSimpleName(), null);
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Transaction will be cancelled. Are you sure?").setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();
        } else if (mStatus == 0 && mCheckoutID != null && mCheckoutID > 0) {
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.cart_fragment_container);
            if (fragment != null && (fragment instanceof EditAddressFragment || fragment instanceof BuyerAddressFragment)) {
                openToFragment(CartSummaryFragment.class.getSimpleName(), null);
            } else if (fragment != null && fragment instanceof CheckoutAddressConfirmFragment){
                openToFragment(BuyerAddressFragment.class.getSimpleName(), null);
            }
            else {
                super.onBackPressed();
            }
        } else {
            finish();
        }
    }

    @Override
    public void editAddress(int addressID, int _ID) {
        Bundle bundle = new Bundle();
        bundle.putInt("addressID", addressID);
        bundle.putInt("_ID", _ID);
        openToFragment(EditAddressFragment.class.getSimpleName(), bundle);
    }

    @Override
    public void addressSaved(int addressID) {
        if (addressID > 0){
            addressClicked(addressID, -1);
        } else {
            mProgressBar.setVisibility(View.VISIBLE);
            waitForAddressResponse();
        }
    }

    public void waitForAddressResponse(){
        mUserAddressServiceResponseReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                startAddressConfirmFragment(intent);
            }
        };
        IntentFilter intentFilter = new IntentFilter(getString(R.string.user_address_data_updated));
        LocalBroadcastManager.getInstance(this).registerReceiver(mUserAddressServiceResponseReceiver, intentFilter);
    }

    public void startAddressConfirmFragment(Intent intent){
        Bundle args = intent.getBundleExtra("extra");
        if (args != null) {
            int addressID = args.getInt(UserProfileContract.UserAddressTable.COLUMN_ADDRESS_ID, -1);
            if (addressID != -1) {
                addressClicked(addressID, -1);
            } else {
                openToFragment(BuyerAddressFragment.class.getSimpleName(), null);
            }
        } else {
            openToFragment(BuyerAddressFragment.class.getSimpleName(), null);
        }
    }

    @Override
    public void addressSelected(int addressID) {
        mBuyerAddressID = addressID;
    }

    @Override
    public void openSelectAddress() {
        Bundle args = new Bundle();
        args.putString("fragment_title", "Select Address");
        openToFragment(BuyerAddressFragment.class.getSimpleName(), args);
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void addressClicked(int addressID, int _ID) {
        Bundle args = new Bundle();
        args.putInt(UserProfileContract.UserAddressTable.COLUMN_ADDRESS_ID, addressID);
        openToFragment(CheckoutAddressConfirmFragment.class.getSimpleName(), args);
        mProgressBar.setVisibility(View.VISIBLE);
    }



    public void updateCart(JSONObject requestBody, int requestMethod, int todo, HashMap<String, String> params) {
        String url = GlobalAccessHelper.generateUrl(APIConstants.CHECKOUT_URL, params);
        volleyStringRequest(todo, requestMethod, url, requestBody.toString());
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void setCart(Cart cart) {
        mCart = cart;
        setViewForProceedButtonLayout();
    }

    @Override
    public void CODApplied(boolean applied){
        int orderValue = (int) Math.ceil(mCart.getFinalPrice());
        if (applied) {
            orderValue = (int) Math.ceil(mCart.getFinalPrice() + (mCart.getCalculatedPrice()*COD_CHARGE_PERCENT));
        }
        mTotalTextView.setText("Total " + String.format(getString(R.string.price_format), String.valueOf(orderValue)));
    }

    @Override
    public void disableProgressBar() {
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void enableProgressBar() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void setPaymentMethod(int paymentMethod) {
        mPaymentMethod = paymentMethod;
    }

    private void setViewForProceedButtonLayout() {
        if (mCart == null || mCart.getSynced() == 0 || mCart.getPieces() == 0){
            mProceedButton.setEnabled(false);
            mProceedButtonLayout.setVisibility(View.GONE);

        } else {
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.cart_fragment_container);
            if (fragment != null && (fragment instanceof EditAddressFragment || fragment instanceof BuyerAddressFragment ||
                    fragment instanceof OrderDetailsFragment)) {
                mProceedButton.setEnabled(false);
                mProceedButtonLayout.setVisibility(View.GONE);
            } else {
                mProceedButtonLayout.setVisibility(View.VISIBLE);
                CODApplied(mPaymentMethod == 0);
                mProductsPiecesTextView.setText(String.valueOf(mCart.getProductCount()) + " products - "
                        + String.valueOf(mCart.getPieces()) + " pieces");
                mProceedButton.setEnabled(true);
            }
        }
    }

    private String getFragmentToOpenName(Bundle savedInstanceState) {
        String openFragment;
        if (savedInstanceState == null) {
            openFragment = getIntent().getStringExtra(Constants.OPEN_FRAGMENT_KEY);
        } else {
            openFragment = (String) savedInstanceState.getSerializable(Constants.OPEN_FRAGMENT_KEY);
        }
        if (openFragment == null) {
            openFragment = "";
        }
        return openFragment;
    }

    private void openToFragment(String fragmentName, @Nullable Bundle bundle) {
        Fragment fragment;

        if (fragmentName == null){
            fragmentName = CartSummaryFragment.class.getSimpleName();
        }

        if (fragmentName.equals(CartSummaryFragment.class.getSimpleName())) {
            mProgressBar.setVisibility(View.VISIBLE);
            resetAllIDs();
            mProceedButtonLayout.setVisibility(View.VISIBLE);
            fragment = new CartSummaryFragment();
        } else if (fragmentName.equals(BuyerAddressFragment.class.getSimpleName())) {
            mProceedButtonLayout.setVisibility(View.GONE);
            if (bundle == null){
                bundle = new Bundle();
            }
            bundle.putBoolean("showSelectAddressButton", true);
            fragment = new BuyerAddressFragment();
        } else if (fragmentName.equals(CheckoutAddressConfirmFragment.class.getSimpleName())) {
            mProceedButtonLayout.setVisibility(View.VISIBLE);
            fragment = new CheckoutAddressConfirmFragment();
        } else if (fragmentName.equals(EditAddressFragment.class.getSimpleName())) {
            mProceedButtonLayout.setVisibility(View.GONE);
            fragment = new EditAddressFragment();
        } else if (fragmentName.equals(CheckoutPaymentMethodFragment.class.getSimpleName())) {
            mProceedButtonLayout.setVisibility(View.VISIBLE);
            fragment = new CheckoutPaymentMethodFragment();
        } else if (fragmentName.equals(OrderDetailsFragment.class.getSimpleName())) {
            mProceedButtonLayout.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.GONE);
            fragment = new OrderDetailsFragment();
        } else {
            resetAllIDs();
            mProgressBar.setVisibility(View.VISIBLE);
            mProceedButtonLayout.setVisibility(View.VISIBLE);
            fragment = new CartSummaryFragment();
        }

        fragment.setArguments(bundle);
        String backStateName = fragment.getClass().getName();
        FragmentManager fm = getSupportFragmentManager();

        boolean fragmentPopped = fm.popBackStackImmediate(backStateName, 0);

        if (!fragmentPopped) {
            // fragment not in backstack create it
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.cart_fragment_container, fragment, fragment.getClass().getSimpleName());
            ft.addToBackStack(backStateName);
            ft.commit();
        }
    }

    private void resetAllIDs() {
        mCheckoutID = null;
        mStatus = 0;
        mBuyerAddressID = 0;
        mPaymentMethod = -1;
        mCart = null;
        mOrderID = -1;
    }

    private void volleyStringRequest(final int todo, int method, String endPoint, final String jsonData) {

        StringRequest stringRequest = new StringRequest(method, endPoint,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        onResponseHandler(todo, response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String err = error.toString();
                if (err.contains("NoConnectionError")) {
                    Toast.makeText(getApplicationContext(), getString(R.string.no_internet_access), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.api_error_message), Toast.LENGTH_LONG).show();
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Accept", "version=1");
                params.put("Authorization", GlobalAccessHelper.getAccessToken(getApplication()));
                return params;
            }

            @Nullable
            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return jsonData == null ? null : jsonData.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    uee.printStackTrace();
                }
                return null;
            }
        };

        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest, REQUEST_TAG);
    }

    private void onResponseHandler(int todo, String response) {
        try {
            JSONObject data = new JSONObject(response);
            JSONObject checkout = data.getJSONObject("checkout");
            mStatus = checkout.getJSONObject("status").getInt("value");
            switch (todo) {
                case TODO.CREATE_CART:
                    mCheckoutID = checkout.getInt("checkoutID");
                    openSelectAddress();
                    break;
                case TODO.UPDATE_CART_ADDRESS:
                    proceedButtonClicked();
                    break;
                case TODO.UPDATE_CART_SUMMARY_CONFIRM:
                    mToolbar.setTitle("Payment Method");
                    Bundle args = new Bundle();
                    args.putFloat("orderValue", mCart.getCalculatedPrice());
                    args.putFloat("finalValue", mCart.getFinalPrice());
                    openToFragment(CheckoutPaymentMethodFragment.class.getSimpleName(), args);
                    break;
                case TODO.UPDATE_CART_PAYMENT_METHOD:
                    View parentLayout = findViewById(R.id.activity_cart);
                    Snackbar.make(parentLayout, "Order successfully placed", Snackbar.LENGTH_LONG).setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    }).show();
                    //Toast.makeText(this, "Order successfully placed", Toast.LENGTH_SHORT).show();
                    Intent cartIntent = new Intent(this, CartService.class);
                    cartIntent.putExtra("TODO", R.string.delete_cart);
                    startService(cartIntent);
                    mOrderID = data.getJSONObject("order").getInt("orderID");
                    runOrderService();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void runOrderService() {
        mOrderServiceResponseReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                startOrderDetailsActivity();
            }
        };
        IntentFilter intentFilter = new IntentFilter(getString(R.string.order_data_updated));
        LocalBroadcastManager.getInstance(this).registerReceiver(mOrderServiceResponseReceiver, intentFilter);
        Intent intent = new Intent(this, OrderService.class);
        intent.putExtra("TODO", R.string.fetch_orders);
        startService(intent);
    }
    private void syncCartItems(){
        Intent intent = new Intent(this, CartService.class);
        intent.putExtra("TODO", R.string.post_cart_item);
        startService(intent);
    }

    private void proceedButtonClicked() {
        try {
            JSONObject requestBody = new JSONObject();
            HashMap<String, String> params = new HashMap<>();
            if (mCart == null){
                return;
            } else if (mCart.getSynced()==0){
                syncCartItems();
            } else if (mStatus == 0 && mCheckoutID == null && mCart.getSynced() == 1) {
                updateCart(requestBody, Request.Method.POST, TODO.CREATE_CART, params);
            } else if (mStatus == 0 && mCheckoutID != null && mCheckoutID > 0 && mBuyerAddressID > 0) {
                requestBody.put("checkoutID", mCheckoutID);
                requestBody.put("status", mStatus + 1);
                requestBody.put("addressID", mBuyerAddressID);
                updateCart(requestBody, Request.Method.PUT, TODO.UPDATE_CART_ADDRESS, params);
            } else if (mStatus == 1 && mCheckoutID != null && mCheckoutID > 0) {
                requestBody.put("checkoutID", mCheckoutID);
                requestBody.put("status", mStatus + 1);
                updateCart(requestBody, Request.Method.PUT, TODO.UPDATE_CART_SUMMARY_CONFIRM, params);
            } else if (mStatus == 2 && mCheckoutID != null && mCheckoutID > 0) {
                if (mPaymentMethod == 0 || mPaymentMethod == 1) {
                    requestBody.put("checkoutID", mCheckoutID);
                    requestBody.put("status", mStatus + 1);
                    requestBody.put("payment_method", mPaymentMethod);
                    params.put("sub_order_details", "1");
                    params.put("order_item_details", "1");
                    params.put("product_details", "1");
                    updateCart(requestBody, Request.Method.PUT, TODO.UPDATE_CART_PAYMENT_METHOD, params);
                } else {
                    Toast.makeText(this, "Select payment method", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, getString(R.string.api_error_message), Toast.LENGTH_SHORT).show();
        }
    }

    public void startOrderDetailsActivity() {
        Bundle bundle = new Bundle();
        bundle.putInt("orderID", mOrderID);
        openToFragment(OrderDetailsFragment.class.getSimpleName(), bundle);
    }

    @Override
    public void dismissDialog() {

    }
}
