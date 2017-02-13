package com.wholdus.www.wholdusbuyerapp.fragments;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crash.FirebaseCrash;
import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.adapters.SubOrderAdapter;
import com.wholdus.www.wholdusbuyerapp.decorators.RecyclerViewSpaceItemDecoration;
import com.wholdus.www.wholdusbuyerapp.helperClasses.APIConstants;
import com.wholdus.www.wholdusbuyerapp.helperClasses.ContactsHelperClass;
import com.wholdus.www.wholdusbuyerapp.helperClasses.GlobalAccessHelper;
import com.wholdus.www.wholdusbuyerapp.helperClasses.HelperFunctions;
import com.wholdus.www.wholdusbuyerapp.helperClasses.OkHttpHelper;
import com.wholdus.www.wholdusbuyerapp.helperClasses.TODO;
import com.wholdus.www.wholdusbuyerapp.interfaces.OrderDetailsListenerInterface;
import com.wholdus.www.wholdusbuyerapp.loaders.OrdersLoader;
import com.wholdus.www.wholdusbuyerapp.models.Order;
import com.wholdus.www.wholdusbuyerapp.models.Suborder;
import com.wholdus.www.wholdusbuyerapp.services.BuyerContactsService;
import com.wholdus.www.wholdusbuyerapp.services.OrderService;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Response;

/**
 * Created by kaustubh on 13/12/16.
 */

public class OrderDetailsFragment extends Fragment implements LoaderManager.LoaderCallbacks<ArrayList<Order>> {

    private OrderDetailsListenerInterface mListener;
    private int mOrderID;
    private Order mOrder;
    private static final int ORDERS_DB_LOADER = 20;

    private TextView mOrderDate;
    private TextView mOrderStatus;
    private TextView mOrderPieces;
    private TextView mOrderProducts;
    private TextView mOrderValue;
    private TextView mCODCharge;
    private TextView mShippingCharge;
    private TextView mFinalPrice;
    private Button mCancelButton;
    private RecyclerView mSubOrdersListView;
    private ArrayList<Suborder> mSuborders;
    private SubOrderAdapter mSuborderAdapter;
    private ProgressBar mPageLoader;
    private ViewGroup mPageLayout;
    private boolean mCancelledRequest = false;

    private BroadcastReceiver mOrderServiceResponseReceiver;

    private static final int CONTACTS_PERMISSION = 0;

    public OrderDetailsFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mListener = (OrderDetailsListenerInterface) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mOrderID = getArguments().getInt("orderID", -1);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.help_action_buttons, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_bar_call:
                callPhone(getString(R.string.phone1));
                break;
            case R.id.action_bar_chat:
                chatButtonClicked();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void callPhone(String number) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + number));
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CONTACTS_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    chatButtonClicked();
                } else {
                    Toast.makeText(getContext(), "Permission needed to chat with us", Toast.LENGTH_SHORT).show();
                }
        }
    }

    private void chatButtonClicked() {

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        ContactsHelperClass contactsHelperClass = new ContactsHelperClass(getActivity().getApplicationContext());
                        String savedNumber = contactsHelperClass.getSavedNumber();
                        if (savedNumber != null) {
                            openWhatsapp(savedNumber);
                        } else {
                            contactsHelperClass.saveWholdusContacts();
                            savedNumber = contactsHelperClass.getSavedNumber();
                            if (savedNumber != null) openWhatsapp(savedNumber);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        FirebaseCrash.report(e);
                    }
                }
            }).start();
            startBuyerContactsService();
        } else {
            requestPermissions(new String[]{Manifest.permission.WRITE_CONTACTS}, CONTACTS_PERMISSION);
        }
    }

    public void startBuyerContactsService(){
        Intent intent = new Intent(getContext(), BuyerContactsService.class);
        intent.putExtra("TODO", TODO.SEND_BUYER_CONTACTS);
        getContext().startService(intent);
    }

    private void openWhatsapp(final String number) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    final Uri uri = Uri.parse("smsto:" + number);
                    Intent i = new Intent(Intent.ACTION_SENDTO, uri);
                    i.putExtra("sms_body", "I need some help");
                    i.setPackage("com.whatsapp");
                    getContext().startActivity(i);
                } catch (Exception e) {
                    Toast.makeText(getContext(), "Whatsapp not installed", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_order_details, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mPageLayout = (ViewGroup) view.findViewById(R.id.page_layout);
        mPageLoader = (ProgressBar) view.findViewById(R.id.page_loader);

        mOrderDate = (TextView) view.findViewById(R.id.order_date_text_view);
        mOrderStatus = (TextView) view.findViewById(R.id.order_status_text_view);
        mOrderProducts = (TextView) view.findViewById(R.id.order_details_products_text_view);
        mOrderPieces = (TextView) view.findViewById(R.id.order_details_pieces_text_view);
        mOrderValue = (TextView) view.findViewById(R.id.order_details_order_value_text_view);
        mCODCharge = (TextView) view.findViewById(R.id.order_details_cod_charge_text_view);
        mShippingCharge = (TextView) view.findViewById(R.id.order_details_shipping_charge_text_view);
        mFinalPrice = (TextView) view.findViewById(R.id.order_details_total_amount_text_view);
        mCancelButton = (Button) view.findViewById(R.id.order_details_cancel_button);

        mSubOrdersListView = (RecyclerView) view.findViewById(R.id.suborder_list_view);
        mSubOrdersListView.setItemAnimator(new DefaultItemAnimator());
        mSubOrdersListView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mSubOrdersListView.addItemDecoration(new RecyclerViewSpaceItemDecoration(getResources().getDimensionPixelSize(R.dimen.card_margin_vertical), 0));
        mSuborders = new ArrayList<>();
        mSuborderAdapter = new SubOrderAdapter(getContext(), mSuborders);
        mSubOrdersListView.setAdapter(mSuborderAdapter);
        mSubOrdersListView.setNestedScrollingEnabled(false);

        mPageLayout.setVisibility(View.INVISIBLE);

        getActivity().getSupportLoaderManager().restartLoader(ORDERS_DB_LOADER, null, this);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mOrderServiceResponseReceiver);
        } catch (Exception e) {

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mOrderServiceResponseReceiver = null;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public Loader<ArrayList<Order>> onCreateLoader(int id, Bundle args) {
        return new OrdersLoader(getContext(), mOrderID, null, true, true, true, true);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Order>> loader, ArrayList<Order> data) {
        if (data != null && data.size() == 1 && mListener != null) {
            mPageLoader.setVisibility(View.INVISIBLE);
            mPageLayout.setVisibility(View.VISIBLE);
            mOrder = data.get(0);
            setViewForOrders();
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Order>> loader) {

    }

    public void setViewForOrders() {
        mListener.fragmentCreated(getString(R.string.order_card_order_number) + mOrder.getDisplayNumber(), true);

        mOrderDate.setText(HelperFunctions.getDateFromString(mOrder.getCreatedAt()));
        mOrderStatus.setText(mOrder.getOrderStatusDisplay());
        mOrderProducts.setText(String.valueOf(mOrder.getProductCount()));
        mOrderPieces.setText(String.valueOf(mOrder.getPieces()));
        mOrderValue.setText(String.format(getString(R.string.price_format), String.valueOf((int) Math.ceil(mOrder.getCalculatedPrice()))));
        mCODCharge.setText(String.format(getString(R.string.price_format), String.valueOf((int) Math.ceil(mOrder.getCODCharge()))));
        mShippingCharge.setText(String.format(getString(R.string.price_format), String.valueOf((int) Math.ceil(mOrder.getShippingCharge()))));
        mFinalPrice.setText(String.format(getString(R.string.price_format), String.valueOf((int) Math.ceil(mOrder.getFinalPrice()))));
        if (mOrder.getOrderStatusValue() == 0){
            mCancelButton.setVisibility(View.VISIBLE);
            mCancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mPageLoader.setVisibility(View.VISIBLE);
                    if (!mCancelledRequest) {
                        new CancelRequest().execute();
                    }
                    mCancelledRequest = true;
                }
            });
        } else {
            mCancelButton.setVisibility(View.GONE);
        }

        mSuborders.clear();
        mSuborders.addAll(mOrder.getSuborders());
        mSuborderAdapter.notifyDataSetChanged();
        //HelperFunctions.setListViewHeightBasedOnChildren(mSubOrdersListView);
    }

    private class CancelRequest extends AsyncTask<Void, Void, Boolean> {

        protected Boolean doInBackground(Void... par) {
            HashMap<String, String> params = new HashMap<>();
            String url = GlobalAccessHelper.generateUrl(APIConstants.ORDERS_URL, params);
            try {
                JSONObject data = new JSONObject();
                data.put("orderID", mOrder.getOrderID());
                Response response = OkHttpHelper.makeDeleteRequest(getContext().getApplicationContext(), url, data.toString());
                return response.isSuccessful();
            }catch (Exception e) {
                return false;
            }
        }

        protected void onPostExecute(Boolean result) {
            if (result){
                runOrderService();
            }else {
                Toast.makeText(getContext(), getString(R.string.api_error_message),Toast.LENGTH_SHORT).show();
                mPageLoader.setVisibility(View.GONE);
                mCancelledRequest =false;
            }
        }


    }

    private void runOrderService() {
        mOrderServiceResponseReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                cancellationSuccess();
            }
        };
        IntentFilter intentFilter = new IntentFilter(getString(R.string.order_data_updated));
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mOrderServiceResponseReceiver, intentFilter);
        Intent intent = new Intent(getContext(), OrderService.class);
        intent.putExtra("TODO", R.string.fetch_orders);
        getContext().startService(intent);
    }

    private void cancellationSuccess(){
        getActivity().getSupportLoaderManager().restartLoader(ORDERS_DB_LOADER, null, this);
        Toast.makeText(getContext(), getString(R.string.cancel_order_success_message),Toast.LENGTH_SHORT).show();
    }
}
