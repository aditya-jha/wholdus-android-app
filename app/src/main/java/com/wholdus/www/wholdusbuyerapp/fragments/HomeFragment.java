package com.wholdus.www.wholdusbuyerapp.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crash.FirebaseCrash;
import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.activities.HandPickedActivity;
import com.wholdus.www.wholdusbuyerapp.activities.HelpSupportActivity;
import com.wholdus.www.wholdusbuyerapp.adapters.ProductHomePageAdapter;
import com.wholdus.www.wholdusbuyerapp.databaseContracts.CatalogContract;
import com.wholdus.www.wholdusbuyerapp.decorators.RecyclerViewSpaceItemDecoration;
import com.wholdus.www.wholdusbuyerapp.helperClasses.Constants;
import com.wholdus.www.wholdusbuyerapp.helperClasses.ContactsHelperClass;
import com.wholdus.www.wholdusbuyerapp.interfaces.HomeListenerInterface;
import com.wholdus.www.wholdusbuyerapp.interfaces.ItemClickListener;
import com.wholdus.www.wholdusbuyerapp.loaders.ProductsLoader;
import com.wholdus.www.wholdusbuyerapp.models.Product;

import java.util.ArrayList;

import static com.wholdus.www.wholdusbuyerapp.R.id.help;
import static com.wholdus.www.wholdusbuyerapp.R.id.transition_current_scene;

/**
 * Created by aditya on 16/11/16.
 */

public class HomeFragment extends Fragment implements View.OnClickListener {

    private HomeListenerInterface mListener;

    private FirebaseAnalytics mFirebaseAnalytics;

    private RecyclerView mProductsRecyclerView;
    private ArrayList<Product> mProducts;
    private ProductHomePageAdapter mProductHomePageAdapter;
    private ProductItemClickListener mProductItemClickListener;
    private LinearLayoutManager mProductsLayoutManager;
    private ProductsLoaderManager mProductsLoader;

    private final int PRODUCTS_DB_LOADER = 901;
    private static final int CONTACTS_PERMISSION = 0;

    public HomeFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mListener = (HomeListenerInterface) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button categoriesButton = (Button) view.findViewById(R.id.categories);
        categoriesButton.setOnClickListener(this);

        Button helpButton = (Button) view.findViewById(help);
        helpButton.setOnClickListener(this);

        Button notificationButton = (Button) view.findViewById(R.id.notification);
        notificationButton.setOnClickListener(this);

        mProductsRecyclerView = (RecyclerView) view.findViewById(R.id.products_recycler_view);
        mProductsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mProducts = new ArrayList<>();
        mProductItemClickListener = new ProductItemClickListener();
        mProductHomePageAdapter = new ProductHomePageAdapter(getContext(), mProducts, mProductItemClickListener);
        mProductsLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mProductsRecyclerView.setLayoutManager(mProductsLayoutManager);
        mProductsRecyclerView.addItemDecoration(new RecyclerViewSpaceItemDecoration(0, getResources().getDimensionPixelSize(R.dimen.card_margin_horizontal)));
        mProductsRecyclerView.setAdapter(mProductHomePageAdapter);
    }

    private class ProductItemClickListener implements ItemClickListener {
        @Override
        public void itemClicked(View view, int position, int id) {
            Intent intent = new Intent(getContext(), HandPickedActivity.class);
            ArrayList<Integer> productIDs = new ArrayList<>();
            productIDs.add(mProducts.get(position).getProductID());
            intent.putExtra(CatalogContract.ProductsTable.TABLE_NAME, productIDs);
            startActivity(intent);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mListener.fragmentCreated(getString(R.string.app_name), false);
        mProductsLoader = new ProductsLoaderManager();
        getActivity().getSupportLoaderManager().restartLoader(PRODUCTS_DB_LOADER, null, mProductsLoader);
    }

    @Override
    public void onClick(View view) {
        final int ID = view.getId();
        switch (ID) {
            case R.id.categories:
                mListener.openCategory(-1);
                break;
            case help:
                helpButtonClicked();
                break;
            case R.id.notification:
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CONTACTS_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    helpButtonClicked();
                } else {
                    Toast.makeText(getContext(), "Permission needed to chat with us", Toast.LENGTH_SHORT).show();
                }
        }
    }

    private void helpButtonClicked() {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "help");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "help on home screen clicked");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

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
        } else {
            requestPermissions(new String[]{Manifest.permission.WRITE_CONTACTS}, CONTACTS_PERMISSION);
        }
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
                    Intent intent = new Intent(getContext(), HelpSupportActivity.class);
                    intent.putExtra(Constants.OPEN_FRAGMENT_KEY, ContactUsFragment.class.getSimpleName());
                    intent.putExtra(Constants.OPEN_ACTIVITY_KEY, HelpSupportActivity.class.getSimpleName());
                    getContext().startActivity(intent);
                }
            }
        });
    }

    public void setViewForProducts(ArrayList<Product> products) {
        mProducts.clear();
        mProducts.addAll(products);
        mProductHomePageAdapter.notifyDataSetChanged();
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
            return new ProductsLoader(getContext(), null, null, responseCodes, null, 10);
        }
    }
}
