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
import android.widget.TextView;

import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.databaseContracts.UserProfileContract.UserInterestsTable;
import com.wholdus.www.wholdusbuyerapp.databaseHelpers.UserDBHelper;
import com.wholdus.www.wholdusbuyerapp.helperClasses.GlobalAccessHelper;
import com.wholdus.www.wholdusbuyerapp.interfaces.ProfileListenerInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by aditya on 3/12/16.
 */

public class BuyerInterestFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private ProfileListenerInterface mListener;
    private UserDBHelper mUserDBHelper;
    private TextView textView;

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
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_buyer_interest, container, false);

        initReferences(rootView);

        getActivity().getSupportLoaderManager().restartLoader(BUYER_INTERESTS_DB_LOADER, null, this);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mListener.fragmentCreated(getString(R.string.buyer_interest_fragment_title), false);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
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
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getContext()) {
            @Override
            public Cursor loadInBackground() {
                mUserDBHelper = new UserDBHelper(getContext());
                return mUserDBHelper.getUserInterests(GlobalAccessHelper.getBuyerID(getActivity().getApplication()), null, -1);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        try {
            JSONObject interests = mUserDBHelper.getJSONDataFromCursor(UserInterestsTable.TABLE_NAME, data, -1);
            setViewFromData(interests.getJSONArray(UserInterestsTable.TABLE_NAME));
        } catch (JSONException e) {
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (mUserDBHelper != null) {
            mUserDBHelper.close();
            mUserDBHelper = null;
        }
    }

    private void initReferences(ViewGroup rootView) {
        textView = (TextView) rootView.findViewById(R.id.temp);
    }

    private void setViewFromData(JSONArray interests) throws JSONException {
        Log.d(this.getClass().getSimpleName(), interests.toString());
        textView.setText(interests.toString());
    }
}
