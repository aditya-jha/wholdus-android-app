package com.wholdus.www.wholdusbuyerapp.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.databaseContracts.UserProfileContract;
import com.wholdus.www.wholdusbuyerapp.databaseHelpers.OrderDBHelper;
import com.wholdus.www.wholdusbuyerapp.fragments.BuyerAddressFragment;
import com.wholdus.www.wholdusbuyerapp.fragments.CartSummaryFragment;
import com.wholdus.www.wholdusbuyerapp.fragments.CheckoutAddressConfirmFragment;
import com.wholdus.www.wholdusbuyerapp.fragments.CheckoutPaymentMethodFragment;
import com.wholdus.www.wholdusbuyerapp.fragments.EditAddressFragment;
import com.wholdus.www.wholdusbuyerapp.helperClasses.Constants;
import com.wholdus.www.wholdusbuyerapp.helperClasses.GlobalAccessHelper;
import com.wholdus.www.wholdusbuyerapp.helperClasses.TODO;
import com.wholdus.www.wholdusbuyerapp.interfaces.CartListenerInterface;
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

import static java.security.AccessController.getContext;

public class CartActivity extends AppCompatActivity implements CartListenerInterface, UserAddressInterface {


    private int mStatus = 0;
    private int mBuyerAddressID;
    private int mPaymentMethod = -1;
    private Cart mCart;
    private Integer mCheckoutID;

    private Toolbar mToolbar;
    private TextView mTotalTextView;
    private TextView mProductsPiecesTextView;
    private Button mProceedButton;
    private LinearLayout mProceedButtonLayout;
    private int mOrderID = -1;

    private ProgressBar mProgressBar;

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

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initToolbar(){
        mToolbar = (Toolbar) findViewById(R.id.default_toolbar);
        setSupportActionBar(mToolbar);
    }

    private void initReferences(){
        mTotalTextView = (TextView) findViewById(R.id.cart_summary_total_price_text_view);
        mProductsPiecesTextView = (TextView) findViewById(R.id.cart_summary_total_products_text_view);
        mTotalTextView = (TextView) findViewById(R.id.cart_summary_total_price_text_view);
        mProceedButtonLayout = (LinearLayout) findViewById(R.id.proceed_button_layout);
        mProceedButton = (Button) findViewById(R.id.cart_summary_proceed_button);
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
        mToolbar.setTitle(title);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        //TODO : Do not let user cancel transaction
        if (mCheckoutID != null && mCheckoutID > 0) {
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            finish();
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            //No button clicked
                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Transaction will be cancelled. Are you sure?").setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();
        }
        else {
            finish();
        }

    }

    @Override
    public void editAddress(int addressID, int _ID) {
        Bundle bundle = new Bundle();
        bundle.putInt("addressID", addressID);
        bundle.putInt("_ID", _ID);
        openToFragment("editAddress", bundle);
    }

    @Override
    public void addressSaved() {
        openToFragment("select_address",null);
    }

    @Override
    public void addressSelected(int addressID) {
        mBuyerAddressID = addressID;
    }

    @Override
    public void openSelectAddress() {
        Bundle args = new Bundle();
        args.putString("fragment_title", "Select Address");
        openToFragment("select_address", args);
        mProgressBar.setVisibility(View.GONE);
        mProceedButtonLayout.setVisibility(View.GONE);
    }

    @Override
    public void addressClicked(int addressID, int _ID) {
        Bundle args = new Bundle();
        args.putInt(UserProfileContract.UserAddressTable.COLUMN_ADDRESS_ID, addressID);
        openToFragment("confirm_address", args);
        mProgressBar.setVisibility(View.VISIBLE);
        mProceedButtonLayout.setVisibility(View.VISIBLE);

    }


    public void updateCart(JSONObject requestBody, int requestMethod, int todo, HashMap<String,String> params){
        String url = GlobalAccessHelper.generateUrl(getString(R.string.checkout_url), params);
        volleyStringRequest(todo, requestMethod, url, requestBody.toString());
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void setCart(Cart cart) {
        mCart = cart;
        setViewForProceedButtonLayout();
    }

    @Override
    public void disableProgressBar() {
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void setPaymentMethod(int paymentMethod) {
        mPaymentMethod = paymentMethod;
    }

    private void setViewForProceedButtonLayout(){
        if (mCart != null){
            mTotalTextView.setText("Total: Rs. " + String.format("%.0f",mCart.getFinalPrice()));
            mProductsPiecesTextView.setText(String.valueOf(mCart.getProductCount()) + " products - "
                    + String.valueOf(mCart.getPieces()) + " pieces");
            mProceedButton.setEnabled(true);
        }
    }

    private String getFragmentToOpenName(Bundle savedInstanceState) {
        String openFragment;
        if (savedInstanceState == null) {
            openFragment = getIntent().getStringExtra(getString(R.string.open_fragment_key));
        } else {
            openFragment = (String) savedInstanceState.getSerializable(getString(R.string.open_fragment_key));
        }
        if (openFragment == null) {
            openFragment = "";
        }
        return openFragment;
    }

    private void openToFragment(String fragmentName, @Nullable Bundle bundle) {
        Fragment fragment;

        switch (fragmentName) {
            case "cart_summary":
                mProgressBar.setVisibility(View.VISIBLE);
                fragment = new CartSummaryFragment();
                break;
            case "select_address":
                fragment = new BuyerAddressFragment();
                break;
            case "confirm_address":
                fragment = new CheckoutAddressConfirmFragment();
                break;
            case "editAddress":
                fragment = new EditAddressFragment();
                break;
            case "paymentMethod":
                fragment = new CheckoutPaymentMethodFragment();
                break;
            default:
                fragment = new CartSummaryFragment();
        }

        fragment.setArguments(bundle);
        String backStateName = fragment.getClass().getName();
        FragmentManager fm = getSupportFragmentManager();

        boolean fragmentPopped = fm.popBackStackImmediate(backStateName, 0);

        if (!fragmentPopped) { // fragment not in backstack create it
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.cart_fragment_container, fragment, fragment.getClass().getSimpleName());
            ft.addToBackStack(backStateName);
            ft.commit();
        }

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
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
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
                    openToFragment("paymentMethod", args);
                    break;
                case TODO.UPDATE_CART_PAYMENT_METHOD:
                    mProceedButtonLayout.setVisibility(View.GONE);
                    //mOrderID = data.getJSONObject("order").getInt("orderID");
                    Toast.makeText(this, "Order successfully placed", Toast.LENGTH_SHORT).show();
                    Intent cartIntent = new Intent(this, CartService.class);
                    cartIntent.putExtra("TODO", R.string.delete_cart);
                    startService(cartIntent);
                    //OrderDBHelper orderDBHelper = new OrderDBHelper(this);
                    //orderDBHelper.saveOrderData(data.getJSONObject("order"));
                    //startOrderDetailsActivity();

                    runAsyncTaskOrder(data.getJSONObject("order"));


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void runAsyncTaskOrder(final JSONObject order){
        new AsyncTask<Void, Void, Void>() {
            protected void onPreExecute() {
            }

            protected Void doInBackground(Void... unused) {
                saveOrder(order);
                return null;
            }

            protected void onPostExecute(Void unused) {
                startOrderDetailsActivity();
            }
        }.execute();
    }

    private void saveOrder(JSONObject order){
        OrderDBHelper orderDBHelper = new OrderDBHelper(this);
        try {
            orderDBHelper.saveOrderData(order);
            mOrderID = order.getInt("orderID");
        } catch (JSONException e){

        }
    }

    private void proceedButtonClicked(){
        try {
            JSONObject requestBody = new JSONObject();
            HashMap<String,String> params = new HashMap<>();
            if (mStatus == 0 && mCheckoutID == null && mCart.getSynced() == 1){
                updateCart(requestBody, Request.Method.POST, TODO.CREATE_CART, params);
            } else if (mStatus == 0 && mCheckoutID > 0){
                requestBody.put("checkoutID", mCheckoutID);
                requestBody.put("status", mStatus + 1);
                requestBody.put("addressID", mBuyerAddressID);
                updateCart(requestBody, Request.Method.PUT, TODO.UPDATE_CART_ADDRESS, params);
            } else if (mStatus == 1 && mCheckoutID > 0){
                requestBody.put("checkoutID", mCheckoutID);
                requestBody.put("status", mStatus + 1);
                updateCart(requestBody, Request.Method.PUT, TODO.UPDATE_CART_SUMMARY_CONFIRM, params);
            } else if (mStatus == 2 && mCheckoutID > 0){
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
        } catch (JSONException e){
            e.printStackTrace();
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
        }
    }

    public void startOrderDetailsActivity(){
        if (mOrderID > 0) {
            Intent orderIntent = new Intent(this, AccountActivity.class);
            // todo set flags so that cart activity is not opened but app doesnt close
            orderIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            orderIntent.putExtra(Constants.OPEN_FRAGMENT_KEY, "orderDetails");
            Bundle bundle = new Bundle();
            bundle.putInt("orderID", mOrderID);
            startActivity(orderIntent);
            finish();
        }
    }

}
