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
import android.widget.ListView;
import android.widget.Toast;

import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.adapters.BusinessTypeSelectAdapter;
import com.wholdus.www.wholdusbuyerapp.helperClasses.HelperFunctions;
import com.wholdus.www.wholdusbuyerapp.interfaces.OnBoardingListenerInterface;
import com.wholdus.www.wholdusbuyerapp.loaders.BusinessTypeLoader;
import com.wholdus.www.wholdusbuyerapp.models.BusinessTypes;
import com.wholdus.www.wholdusbuyerapp.services.UserService;

import java.util.ArrayList;

/**
 * Created by kaustubh on 16/1/17.
 */

public class GetBusinessTypeFragment extends Fragment implements LoaderManager.LoaderCallbacks<ArrayList<BusinessTypes>> {

    private OnBoardingListenerInterface mListener;
    private BroadcastReceiver mUserServiceResponseReceiver;

    private ListView mBusinessTypeListView;
    private ArrayList<BusinessTypes> mBusinessTypes;
    private BusinessTypeSelectAdapter mBusinessTypeSelectAdapter;

    private final int BUSINESS_TYPE_DB_LOADER = 1100;

    public GetBusinessTypeFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (OnBoardingListenerInterface) context;
        } catch (ClassCastException cee) {
            cee.printStackTrace();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserServiceResponseReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                handleAPIResponse(intent);
            }
        };
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_get_business_type, container, false);
        initReferences(rootView);

        fetchDataFromServer();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mListener.fragmentCreated("Business Type", false);

        getActivity().getSupportLoaderManager().restartLoader(BUSINESS_TYPE_DB_LOADER, null, this);

        IntentFilter intentFilter = new IntentFilter(getString(R.string.user_data_updated));
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mUserServiceResponseReceiver, intentFilter);

    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mUserServiceResponseReceiver);
        } catch (Exception e) {

        }
    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mUserServiceResponseReceiver= null;
    }

    private void initReferences(ViewGroup view) {
        mBusinessTypeListView = (ListView) view.findViewById(R.id.business_type_listview);
        mBusinessTypes = new ArrayList<>();
        mBusinessTypeSelectAdapter = new BusinessTypeSelectAdapter(getContext(), mBusinessTypes);
        mBusinessTypeListView.setAdapter(mBusinessTypeSelectAdapter);
    }

    public void setViewForData(ArrayList<BusinessTypes> data) {
        mBusinessTypes.clear();
        mBusinessTypes.addAll(data);
        mBusinessTypeSelectAdapter.notifyDataSetChanged();
        mBusinessTypeSelectAdapter.clearSelectedPosition();
        HelperFunctions.setListViewHeightBasedOnChildren(mBusinessTypeListView);
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<BusinessTypes>> loader) {

    }

    @Override
    public Loader<ArrayList<BusinessTypes>> onCreateLoader(int id, Bundle args) {
        return new BusinessTypeLoader(getContext());
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<BusinessTypes>> loader, ArrayList<BusinessTypes> data) {
        if (data != null) {
            setViewForData(data);
        }
    }

    private void handleAPIResponse(Intent intent) {
        if (mBusinessTypes.isEmpty()) {
            getActivity().getSupportLoaderManager().restartLoader(BUSINESS_TYPE_DB_LOADER, null, this);
        }
    }

    private void fetchDataFromServer() {
        Intent businessTypesIntent = new Intent(getContext(), UserService.class);
        businessTypesIntent.putExtra("TODO", R.string.fetch_business_types);
        getContext().startService(businessTypesIntent);
    }

    public void updateBusinessType() {
        int selectedPosition = mBusinessTypeSelectAdapter.getSelectedPosition();
        if (selectedPosition != -1) {
            Intent intent = new Intent(getContext(), UserService.class);
            intent.putExtra("TODO", R.string.update_buyer_type);
            intent.putExtra(getString(R.string.business_type_key), String.valueOf(mBusinessTypes.get(selectedPosition).getBusinessTypeID()));
            getContext().startService(intent);
            mListener.businessTypeSaved();
        } else {
            Toast.makeText(getContext(), "Please select a value", Toast.LENGTH_SHORT).show();
        }
    }
}
