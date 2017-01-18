package com.wholdus.www.wholdusbuyerapp.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.adapters.BuyerInterestsAdapter;
import com.wholdus.www.wholdusbuyerapp.helperClasses.Constants;
import com.wholdus.www.wholdusbuyerapp.helperClasses.HelperFunctions;
import com.wholdus.www.wholdusbuyerapp.helperClasses.TODO;
import com.wholdus.www.wholdusbuyerapp.interfaces.ProfileListenerInterface;
import com.wholdus.www.wholdusbuyerapp.loaders.ProfileLoader;
import com.wholdus.www.wholdusbuyerapp.models.Buyer;
import com.wholdus.www.wholdusbuyerapp.services.UserService;

/**
 * Created by aditya on 3/12/16.
 */

public class BuyerInterestFragment extends Fragment implements LoaderManager.LoaderCallbacks<Buyer> {

    private ProfileListenerInterface mListener;
    private ViewGroup mPageLayout, mRootView;
    private ProgressBar mPageLoader;
    private ListView mInterestsListView;
    private BroadcastReceiver mReceiver;
    private Snackbar mSnackbar;

    private static final int BUYER_INTERESTS_DB_LOADER = 0;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (ProfileListenerInterface) context;
        } catch (ClassCastException cee) {
            Log.e(this.getClass().getSimpleName(), " must implement Listener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                handleAPIResponse(intent);
            }
        };
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = (ViewGroup) inflater.inflate(R.layout.fragment_buyer_interest, container, false);
        return mRootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initReferences(mRootView);
        getActivity().getSupportLoaderManager().restartLoader(BUYER_INTERESTS_DB_LOADER, null, this);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mListener.fragmentCreated(getString(R.string.buyer_interest_fragment_title), false);
        IntentFilter intentFilter = new IntentFilter(getString(R.string.user_data_updated));
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mReceiver, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mReceiver);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mSnackbar != null && mSnackbar.isShownOrQueued()) {
            mSnackbar.dismiss();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public Loader<Buyer> onCreateLoader(int id, Bundle args) {
        return new ProfileLoader(getContext(), true, false, true);
    }

    @Override
    public void onLoadFinished(Loader<Buyer> loader, Buyer data) {
        if (data != null && data.getBuyerInterest().size() > 0) {
            mPageLoader.setVisibility(View.INVISIBLE);
            mPageLayout.setVisibility(View.VISIBLE);
            BuyerInterestsAdapter adapter = new BuyerInterestsAdapter(getContext(), data.getBuyerInterest());
            mInterestsListView.setAdapter(adapter);
        } else {
            fetchBuyerDataFromServer();
        }
    }

    @Override
    public void onLoaderReset(Loader<Buyer> loader) {

    }

    private void initReferences(ViewGroup rootView) {
        mPageLoader = (ProgressBar) rootView.findViewById(R.id.page_loader);
        mPageLayout = (ViewGroup) rootView.findViewById(R.id.page_layout);
        mInterestsListView = (ListView) rootView.findViewById(R.id.interests_list_view);

//        Button button = (Button) rootView.findViewById(R.id.add_button);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                BuyerInterestDialogFragment dialogFragment = BuyerInterestDialogFragment.getInstance();
//                dialogFragment.show(getActivity().getSupportFragmentManager(), dialogFragment.getClass().getSimpleName());
//            }
//        });
        mPageLoader.setVisibility(View.VISIBLE);
        mPageLayout.setVisibility(View.INVISIBLE);

        mInterestsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getContext(), "Item at postion " + i + " clicked", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchBuyerDataFromServer() {
        Intent intent = new Intent(getContext(), UserService.class);
        intent.putExtra("TODO", TODO.FETCH_USER_PROFILE);
        getContext().startService(intent);
    }

    private void handleAPIResponse(Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras != null && extras.getString(Constants.ERROR_RESPONSE) != null) {
            if (mPageLayout.getVisibility() != View.VISIBLE) {
                showErrorMessage();
            }
        } else {
            getActivity().getSupportLoaderManager().restartLoader(BUYER_INTERESTS_DB_LOADER, null, this);
        }
    }

    private void showErrorMessage() {
        if (mSnackbar != null && mSnackbar.isShownOrQueued()) {
            mSnackbar.dismiss();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                String message;
                if (HelperFunctions.isNetworkAvailable(getContext())) {
                    message = getString(R.string.api_error_message);
                } else {
                    message = getString(R.string.no_internet_access);
                }
                mSnackbar = Snackbar.make(mRootView, message, Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.retry_text, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                fetchBuyerDataFromServer();
                            }
                        });

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((TextView) mSnackbar.getView().findViewById(android.support.design.R.id.snackbar_text)).setTextColor(Color.WHITE);
                        mSnackbar.show();
                    }
                });
            }
        }).start();
    }
}
