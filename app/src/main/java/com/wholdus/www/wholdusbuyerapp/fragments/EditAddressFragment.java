package com.wholdus.www.wholdusbuyerapp.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.databaseContracts.UserProfileContract.UserAddressTable;
import com.wholdus.www.wholdusbuyerapp.helperClasses.InputValidationHelper;
import com.wholdus.www.wholdusbuyerapp.interfaces.UserAddressInterface;
import com.wholdus.www.wholdusbuyerapp.loaders.BuyerAddressLoader;
import com.wholdus.www.wholdusbuyerapp.models.BuyerAddress;
import com.wholdus.www.wholdusbuyerapp.services.UserService;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by aditya on 29/11/16.
 */

public class EditAddressFragment extends Fragment implements LoaderManager.LoaderCallbacks<ArrayList<BuyerAddress>>{

    private UserAddressInterface mListener;
    private int mAddressID;
    private int m_ID;

    private TextInputLayout mPincodeWrapper, mMobileNumberWrapper, mAddressWrapper;
    private TextInputEditText mPincodeEditText, mMobileNumberEditText,
            mAddressEditText, mCityEditText, mStateEditText, mLandmarkEditText, mAliasEditText;

    private Button mSaveButton;
    private Button mCurrentLocationButton;
    private GoogleApiClient mGoogleApiClient;
    private BuyerAddress mBuyerAddress;

    private static final int PERMISSION_ACCESS_COARSE_LOCATION = 0;
    private int USER_ADDRESS_LOADER = 80;

    public EditAddressFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (UserAddressInterface) context;
        } catch (ClassCastException cee) {
            cee.printStackTrace();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        mAddressID = arguments.getInt("addressID", -1);
        m_ID = arguments.getInt("_ID", -1);
        startLocationListener();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_edit_address, container, false);

        initReferences(rootView);

        if (mAddressID == -1 && m_ID == -1) {
            // Create new buyer model
            mBuyerAddress = new BuyerAddress();
        } else {
            getActivity().getSupportLoaderManager().restartLoader(USER_ADDRESS_LOADER, null, this);
        }

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAddressID == -1 && m_ID == -1) {
            mListener.fragmentCreated("Add New Address", true);
        } else {
            mListener.fragmentCreated("Edit Address", true);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_ACCESS_COARSE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (mGoogleApiClient != null) {
                        mGoogleApiClient.connect();
                    }
                } else {
                    Toast.makeText(getContext(), "Need your location!", Toast.LENGTH_SHORT).show();
                }
        }
    }

    private void startLocationListener(){
        mGoogleApiClient = new GoogleApiClient.Builder(getContext(), new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(@Nullable Bundle bundle) {
                if (ContextCompat.checkSelfPermission(getContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                    double lat = location.getLatitude(),
                            lng = location.getLongitude();
                    Toast.makeText(getContext(), lat + " , " + lng, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onConnectionSuspended(int i) {

            }
        }, new GoogleApiClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                Toast.makeText(getContext(), "Error google", Toast.LENGTH_SHORT).show();
            }
        }).addApi(LocationServices.API).build();
    }

    private void initReferences(ViewGroup rootView) {
        mPincodeWrapper = (TextInputLayout) rootView.findViewById(R.id.pincode_wrapper);
        mMobileNumberWrapper = (TextInputLayout) rootView.findViewById(R.id.mobile_number_wrapper);
        mAddressWrapper = (TextInputLayout) rootView.findViewById(R.id.address_wrapper);

        mPincodeEditText = (TextInputEditText) rootView.findViewById(R.id.pincode_edit_text);
        mMobileNumberEditText = (TextInputEditText) rootView.findViewById(R.id.mobile_number_edit_text);
        mCityEditText = (TextInputEditText) rootView.findViewById(R.id.city_edit_text);
        mStateEditText = (TextInputEditText) rootView.findViewById(R.id.state_edit_text);
        mAddressEditText = (TextInputEditText) rootView.findViewById(R.id.address_edit_text);
        mLandmarkEditText = (TextInputEditText) rootView.findViewById(R.id.landmark_edit_text);
        mAliasEditText = (TextInputEditText) rootView.findViewById(R.id.alias_edit_text);

        mSaveButton = (Button) rootView.findViewById(R.id.save_button);
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveAddress();
                mListener.addressSaved();
            }
        });
        if (!(mAddressID == -1 && m_ID == -1)) {
            mSaveButton.setEnabled(false);
        }

        mCurrentLocationButton = (Button) rootView.findViewById(R.id.current_location_button);
        mCurrentLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(getContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                            PERMISSION_ACCESS_COARSE_LOCATION);
                } else {
                    if (mGoogleApiClient != null) {
                        mGoogleApiClient.connect();
                    }
                }
            }
        });
    }

    private void saveAddress() {
        try {
            JSONObject address = new JSONObject();
            address.put(UserAddressTable.COLUMN_ADDRESS, getDataFromView(UserAddressTable.COLUMN_ADDRESS));
            address.put(UserAddressTable.COLUMN_ADDRESS_ALIAS, getDataFromView(UserAddressTable.COLUMN_ADDRESS_ALIAS));
            address.put(UserAddressTable.COLUMN_STATE, getDataFromView(UserAddressTable.COLUMN_STATE));
            address.put(UserAddressTable.COLUMN_PINCODE, getDataFromView(UserAddressTable.COLUMN_PINCODE));
            address.put(UserAddressTable.COLUMN_CITY, getDataFromView(UserAddressTable.COLUMN_CITY));
            address.put(UserAddressTable.COLUMN_LANDMARK, getDataFromView(UserAddressTable.COLUMN_LANDMARK));
            address.put(UserAddressTable.COLUMN_CONTACT_NUMBER, getDataFromView(UserAddressTable.COLUMN_CONTACT_NUMBER));
            address.put(UserAddressTable.COLUMN_SYNCED, 0);
            address.put(UserAddressTable.COLUMN_CLIENT_ID, mBuyerAddress.getClientID());

            //TODO : Update loader parameters from address id to pincode if response received from server
            address.put(UserAddressTable.COLUMN_ADDRESS_ID, mAddressID);
            address.put(UserAddressTable.COLUMN_CREATED_AT, mBuyerAddress.getCreatedAt());
            address.put(UserAddressTable.COLUMN_UPDATED_AT, mBuyerAddress.getUpdatedAt());
            address.put(UserAddressTable.COLUMN_CLIENT_ID, mBuyerAddress.getClientID());
            address.put(UserAddressTable.COLUMN_PRIORITY, mBuyerAddress.getPriority());
            address.put(UserAddressTable.COLUMN_PINCODE_ID, mBuyerAddress.getPincodeID());

            address.put(UserAddressTable._ID, mBuyerAddress.get_ID());

            Intent intent = new Intent(getContext(), UserService.class);
            intent.putExtra(UserAddressTable.TABLE_NAME, address.toString());
            intent.putExtra("TODO", R.string.update_user_address);

            getContext().startService(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getDataFromView(String key) throws Exception {
        switch (key) {
            case UserAddressTable.COLUMN_CONTACT_NUMBER:
                String mobileNumber = mMobileNumberEditText.getText().toString();
                if (InputValidationHelper.isValidMobileNumber(mMobileNumberWrapper, mobileNumber)) {
                    return mobileNumber;
                } else throw new Exception();
            case UserAddressTable.COLUMN_PINCODE:
                String pincode = mPincodeEditText.getText().toString();
                if (InputValidationHelper.isValidPincode(mPincodeWrapper, pincode)) {
                    return pincode;
                } else throw new Exception();
            case UserAddressTable.COLUMN_ADDRESS:
                String address = mAddressEditText.getText().toString();
                if (InputValidationHelper.isNameValid(mAddressWrapper, address)) {
                    return address;
                } else throw new Exception();
            case UserAddressTable.COLUMN_LANDMARK:
                return mLandmarkEditText.getText().toString();
            case UserAddressTable.COLUMN_ADDRESS_ALIAS:
                return mAliasEditText.getText().toString();
            case UserAddressTable.COLUMN_CITY:
                return mCityEditText.getText().toString();
            case UserAddressTable.COLUMN_STATE:
                return mStateEditText.getText().toString();
            default:
                return "";
        }
    }

    private void setViewFromData(BuyerAddress address){
        mAliasEditText.setText(address.getAlias());
        mMobileNumberEditText.setText(address.getContactNumber());
        mLandmarkEditText.setText(address.getLandmark());
        mCityEditText.setText(address.getCity());
        mStateEditText.setText(address.getState());
        mPincodeEditText.setText(address.getPincode());
        mAddressEditText.setText(address.getAddress());
    }
    @Override
    public void onLoaderReset(Loader<ArrayList<BuyerAddress>> loader) {

    }

    @Override
    public void onLoadFinished(Loader<ArrayList<BuyerAddress>> loader, ArrayList<BuyerAddress> data) {
        if (data.size() > 0){
            mBuyerAddress = data.get(0);
            setViewFromData(mBuyerAddress);
            mSaveButton.setEnabled(true);
        }
    }

    @Override
    public Loader<ArrayList<BuyerAddress>> onCreateLoader(int id, Bundle args) {
        return new BuyerAddressLoader(getContext(), mAddressID, m_ID);
    }

}
