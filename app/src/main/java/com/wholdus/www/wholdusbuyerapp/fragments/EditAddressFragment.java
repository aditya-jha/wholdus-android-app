package com.wholdus.www.wholdusbuyerapp.fragments;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.databaseHelpers.UserDBHelper;
import com.wholdus.www.wholdusbuyerapp.interfaces.ProfileListenerInterface;

/**
 * Created by aditya on 29/11/16.
 */

public class EditAddressFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private ProfileListenerInterface mListener;
    private String mAddressID;
    private static final int USER_ADDRESS_LOADER = 0;
    private UserDBHelper mUserAddressDBHelper;

    public EditAddressFragment() {}

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (ProfileListenerInterface) context;
        } catch (ClassCastException cee) {
            Log.d(EditAddressFragment.class.getSimpleName(), " must implement " + ProfileListenerInterface.class.getSimpleName());
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        mAddressID = arguments.getString("addressID", null);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_edit_address, container, false);

        initReferences(rootView);
        getActivity().getSupportLoaderManager().restartLoader(USER_ADDRESS_LOADER, null, this);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mAddressID != null) {
            mListener.fragmentCreated("Edit Address", true);
        } else {
            mListener.fragmentCreated("Add New Address", true);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public Loader<Cursor> onCreateLoader(final int id, Bundle args) {
        return new CursorLoader(getContext()) {
            @Override
            public Cursor loadInBackground() {
                mUserAddressDBHelper = new UserDBHelper(getContext());
                return mUserAddressDBHelper.getUserAddress(mAddressID);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
            case USER_ADDRESS_LOADER:
                if(mUserAddressDBHelper != null) {
                    mUserAddressDBHelper.close;
                    mUserAddressDBHelper = null;
                }
                break;
        }
    }

    private void initReferences(ViewGroup rootView) {

    }
}
