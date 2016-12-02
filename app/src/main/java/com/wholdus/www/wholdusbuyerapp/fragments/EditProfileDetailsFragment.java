package com.wholdus.www.wholdusbuyerapp.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;

import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.WholdusApplication;
import com.wholdus.www.wholdusbuyerapp.adapters.BusinessTypesAdapter;
import com.wholdus.www.wholdusbuyerapp.databaseContracts.UserProfileContract;
import com.wholdus.www.wholdusbuyerapp.databaseContracts.UserProfileContract.BusinessTypesTable;
import com.wholdus.www.wholdusbuyerapp.databaseContracts.UserProfileContract.UserTable;
import com.wholdus.www.wholdusbuyerapp.databaseHelpers.UserDBHelper;
import com.wholdus.www.wholdusbuyerapp.helperClasses.InputValidationHelper;
import com.wholdus.www.wholdusbuyerapp.interfaces.ProfileListenerInterface;
import com.wholdus.www.wholdusbuyerapp.services.UserService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by aditya on 28/11/16.
 */

public class EditProfileDetailsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private ProfileListenerInterface mListener;
    private UserDBHelper mUserDBHelper, mBusinessTypesDBHelper;
    private TextInputLayout mNameWrapper, mWhatsappNumberWrapper;
    private TextInputEditText mNameEditText, mCompanyNameEditText, mWhatsappNumberEditText;
    private Spinner mBusinessTypeSpinner;
    private BusinessTypesAdapter mBusinessTypeAdapter;
    private BroadcastReceiver mUserServiceResponseReceiver;
    private static final int USER_DETAILS_DB_LOADER = 0;
    private static final int BUSINESS_TYPES_DB_LOADER = 1;
    private String mSelectedBusinessType;

    public EditProfileDetailsFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (ProfileListenerInterface) context;
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
                handleReceiverResponse(intent);
            }
        };
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_edit_profile_details, container, false);

        initReferences(rootView);
        getActivity().getSupportLoaderManager().restartLoader(USER_DETAILS_DB_LOADER, null, this);
        getActivity().getSupportLoaderManager().restartLoader(BUSINESS_TYPES_DB_LOADER, null, this);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mListener.fragmentCreated("Edit Profile", true);
        IntentFilter intentFilter = new IntentFilter(getString(R.string.user_data_updated));
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mUserServiceResponseReceiver, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mUserServiceResponseReceiver);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mUserServiceResponseReceiver = null;
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
                if (id == USER_DETAILS_DB_LOADER) {
                    WholdusApplication wholdusApplication = (WholdusApplication) getActivity().getApplication();
                    mUserDBHelper = new UserDBHelper(getContext());
                    return mUserDBHelper.getUserData(wholdusApplication.getBuyerID());
                } else if (id == BUSINESS_TYPES_DB_LOADER) {
                    mBusinessTypesDBHelper = new UserDBHelper(getContext());
                    return mBusinessTypesDBHelper.getBusinessTypes(null);
                }
                return super.loadInBackground();
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        try {
            switch (loader.getId()) {
                case USER_DETAILS_DB_LOADER:
                    setViewForPersonalDetails(mUserDBHelper.getJSONDataFromCursor(UserProfileContract.UserTable.TABLE_NAME, data, 0));
                    break;
                case BUSINESS_TYPES_DB_LOADER:
                    if (data.getCount() == 0) {
                        // fetch from server
                        Intent intent = new Intent(getContext(), UserService.class);
                        intent.putExtra("TODO", R.string.fetch_business_types);
                        getContext().startService(intent);
                    } else {
                        // update UI
                        setViewForBusinessTypes(mBusinessTypesDBHelper.getJSONDataFromCursor(BusinessTypesTable.TABLE_NAME, data, -1));
                    }
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mSelectedBusinessType = null;
        try {
            switch (loader.getId()) {
                case USER_DETAILS_DB_LOADER:
                    if (mUserDBHelper != null) {
                        mUserDBHelper.close();
                        mUserDBHelper = null;
                    }
                    break;
                case BUSINESS_TYPES_DB_LOADER:
                    if (mBusinessTypesDBHelper != null) {
                        mBusinessTypesDBHelper.close();
                        mBusinessTypesDBHelper = null;
                    }
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initReferences(ViewGroup rootView) {
        mNameWrapper = (TextInputLayout) rootView.findViewById(R.id.name_wrapper);
        mWhatsappNumberWrapper = (TextInputLayout) rootView.findViewById(R.id.whatsapp_number_wrapper);

        mNameEditText = (TextInputEditText) rootView.findViewById(R.id.name_edit_text);
        mCompanyNameEditText = (TextInputEditText) rootView.findViewById(R.id.company_name_edit_text);
        mWhatsappNumberEditText = (TextInputEditText) rootView.findViewById(R.id.whatsapp_number_edit_text);

        mBusinessTypeSpinner = (Spinner) rootView.findViewById(R.id.business_type_spinner);

        Button mSaveButton = (Button) rootView.findViewById(R.id.submit_button);
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveDetails();
            }
        });
    }

    private void saveDetails() {
        // update local and send to server through userservice
        try {
            String name = getStringFromView(R.string.name_key);
            String companyName = getStringFromView(R.string.company_name_key);
            String whatsappNumber = getStringFromView(R.string.whatsapp_number_key);
            String businessTypeID = getStringFromView(R.string.business_type_key);

            if (InputValidationHelper.isValidMobileNumber(mWhatsappNumberWrapper, whatsappNumber) &&
                    InputValidationHelper.isNameValid(mNameWrapper, name)) {
                Intent intent = new Intent(getContext(), UserService.class);
                intent.putExtra("TODO", R.string.update_user_profile);
                intent.putExtra(getString(R.string.name_key), name);
                intent.putExtra(getString(R.string.whatsapp_number_key), whatsappNumber);
                intent.putExtra(getString(R.string.company_name_key), companyName);
                intent.putExtra(getString(R.string.business_type_key), businessTypeID);

                getContext().startService(intent);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String getStringFromView(int key) throws JSONException {
        switch (key) {
            case R.string.name_key:
                return mNameEditText.getText().toString();
            case R.string.whatsapp_number_key:
                return mWhatsappNumberEditText.getText().toString();
            case R.string.company_name_key:
                return mCompanyNameEditText.getText().toString();
            case R.string.business_type_key:
                return ((JSONObject) mBusinessTypeSpinner.getSelectedItem()).getString(BusinessTypesTable.COLUMN_BUSINESS_TYPE_ID);
        }
        return "";
    }

    private void setViewForPersonalDetails(JSONObject data) throws JSONException {
        mNameEditText.setText(data.getString(UserTable.COLUMN_NAME));
        mCompanyNameEditText.setText(data.getString(UserTable.COLUMN_COMPANY_NAME));
        mWhatsappNumberEditText.setText(data.getString(UserTable.COLUMN_WHATSAPP_NUMBER));

        mSelectedBusinessType = data.getString(UserTable.COLUMN_BUSINESS_TYPE);
        setSelectionForBusinessType();
    }

    private void setViewForBusinessTypes(JSONObject data) throws JSONException {
        JSONArray businessTypes = data.getJSONArray(BusinessTypesTable.TABLE_NAME);
        mBusinessTypeAdapter = new BusinessTypesAdapter(getContext(), businessTypes);
        mBusinessTypeSpinner.setAdapter(mBusinessTypeAdapter);
        setSelectionForBusinessType();
    }

    public void setSelectionForBusinessType() {
        if (mSelectedBusinessType != null && mBusinessTypeAdapter != null) {
            mBusinessTypeSpinner.setSelection(mBusinessTypeAdapter.getSelectedItemIndex(mSelectedBusinessType));
        }
    }

    private void handleReceiverResponse(Intent intent) {
        String extra = intent.getStringExtra("extra");
        if (extra != null) {
            if (extra.equals(getString(R.string.business_types_data_updated))) {
                getActivity().getSupportLoaderManager().restartLoader(USER_DETAILS_DB_LOADER, null, this);
            } else if (extra.equals(getString(R.string.user_data_modified))) {
                // close fragment
                mListener.openProfileFragment();
            }
        } else {
            mListener.openProfileFragment();
        }
    }
}
