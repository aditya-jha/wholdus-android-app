package com.wholdus.www.wholdusbuyerapp.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.adapters.BusinessTypesAdapter;
import com.wholdus.www.wholdusbuyerapp.helperClasses.InputValidationHelper;
import com.wholdus.www.wholdusbuyerapp.interfaces.ProfileListenerInterface;
import com.wholdus.www.wholdusbuyerapp.loaders.EditProfileFragmentLoader;
import com.wholdus.www.wholdusbuyerapp.models.BusinessTypes;
import com.wholdus.www.wholdusbuyerapp.models.Buyer;
import com.wholdus.www.wholdusbuyerapp.models.EditProfileData;
import com.wholdus.www.wholdusbuyerapp.services.UserService;

import java.util.ArrayList;

/**
 * Created by aditya on 28/11/16.
 */

public class EditProfileDetailsFragment extends Fragment implements LoaderManager.LoaderCallbacks<EditProfileData> {

    private ProfileListenerInterface mListener;

    private TextInputLayout mNameWrapper, mWhatsappNumberWrapper;
    private TextInputEditText mNameEditText, mCompanyNameEditText, mWhatsappNumberEditText;
    private Spinner mBusinessTypeSpinner;
    private BusinessTypesAdapter mBusinessTypeAdapter;
    private BroadcastReceiver mUserServiceResponseReceiver;
    private String mSelectedBusinessType;

    private static final int EDIT_PROFILE_FRAGMENT_LOADER = 0;

    public EditProfileDetailsFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mListener = (ProfileListenerInterface) context;
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
        return inflater.inflate(R.layout.fragment_edit_profile_details, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mNameWrapper = (TextInputLayout) view.findViewById(R.id.name_wrapper);
        mWhatsappNumberWrapper = (TextInputLayout) view.findViewById(R.id.whatsapp_number_wrapper);

        mNameEditText = (TextInputEditText) view.findViewById(R.id.name_edit_text);
        mCompanyNameEditText = (TextInputEditText) view.findViewById(R.id.company_name_edit_text);
        mWhatsappNumberEditText = (TextInputEditText) view.findViewById(R.id.whatsapp_number_edit_text);

        mBusinessTypeSpinner = (Spinner) view.findViewById(R.id.business_type_spinner);

        TextView mSaveButton = (TextView) view.findViewById(R.id.submit_button);
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveDetails();
            }
        });

        fetchBusinessTypesDataFromServer();
        getActivity().getSupportLoaderManager().restartLoader(EDIT_PROFILE_FRAGMENT_LOADER, null, this);
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
        try {
            LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mUserServiceResponseReceiver);
        } catch (Exception e){

        }
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
    public Loader<EditProfileData> onCreateLoader(int id, Bundle args) {
        return new EditProfileFragmentLoader(getContext());
    }

    @Override
    public void onLoadFinished(Loader<EditProfileData> loader, EditProfileData data) {
        if (data != null && mListener != null) {
            setViewForPersonalDetails(data.getBuyer());
            setViewForBusinessTypes(data.getBusinessType());
        }
    }

    @Override
    public void onLoaderReset(Loader<EditProfileData> loader) {

    }

    private void saveDetails() {
        // update local and send to server through userservice
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
    }

    private String getStringFromView(int key) {
        switch (key) {
            case R.string.name_key:
                return mNameEditText.getText().toString();
            case R.string.whatsapp_number_key:
                return mWhatsappNumberEditText.getText().toString();
            case R.string.company_name_key:
                return mCompanyNameEditText.getText().toString();
            case R.string.business_type_key:
                BusinessTypes businessTypes = (BusinessTypes) mBusinessTypeSpinner.getSelectedItem();
                return String.valueOf(businessTypes.getBusinessTypeID());
        }
        return "";
    }

    private void setViewForPersonalDetails(Buyer buyer) {
        mNameEditText.setText(buyer.getName());
        mCompanyNameEditText.setText(buyer.getCompanyName());
        mWhatsappNumberEditText.setText(buyer.getWhatsappNumber());
        mSelectedBusinessType = buyer.getBusinessType();
        setSelectionForBusinessType();
    }

    private void setViewForBusinessTypes(ArrayList<BusinessTypes> data) {
        mBusinessTypeAdapter = new BusinessTypesAdapter(getContext(), data);
        mBusinessTypeSpinner.setAdapter(mBusinessTypeAdapter);
        setSelectionForBusinessType();
    }

    public void setSelectionForBusinessType() {
        if (mSelectedBusinessType != null && mBusinessTypeAdapter != null) {
            mBusinessTypeSpinner.setSelection(mBusinessTypeAdapter.getSelectedItemIndex(mSelectedBusinessType));
        }
    }

    private void handleReceiverResponse(Intent intent) {
        int todo = intent.getIntExtra("TODO", -1);
        switch (todo) {
            case R.string.user_data_updated:
                mListener.openProfileFragment();
                break;
            case R.string.fetch_business_types:
                getActivity().getSupportLoaderManager().restartLoader(EDIT_PROFILE_FRAGMENT_LOADER, null, this);
                break;
        }
    }

    private void fetchBusinessTypesDataFromServer() {
        Intent intent = new Intent(getContext(), UserService.class);
        intent.putExtra("TODO", R.string.fetch_business_types);
        getContext().startService(intent);
    }
}
