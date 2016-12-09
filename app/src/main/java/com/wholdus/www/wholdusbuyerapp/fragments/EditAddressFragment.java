package com.wholdus.www.wholdusbuyerapp.fragments;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
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
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
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
import com.wholdus.www.wholdusbuyerapp.databaseHelpers.UserDBHelper;
import com.wholdus.www.wholdusbuyerapp.helperClasses.GlobalAccessHelper;
import com.wholdus.www.wholdusbuyerapp.helperClasses.InputValidationHelper;
import com.wholdus.www.wholdusbuyerapp.interfaces.ProfileListenerInterface;
import com.wholdus.www.wholdusbuyerapp.services.UserService;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by aditya on 29/11/16.
 */

public class EditAddressFragment extends Fragment {

    private ProfileListenerInterface mListener;
    private String mAddressID;
    private int m_ID;
    private static final int USER_ADDRESS_LOADER = 0;
    private UserDBHelper mUserAddressDBHelper;
    private BroadcastReceiver mUserServiceResponseReceiver;

    private TextInputLayout mPincodeWrapper, mMobileNumberWrapper, mAddressWrapper;
    private TextInputEditText mPincodeEditText, mMobileNumberEditText,
            mAddressEditText, mCityEditText, mStateEditText, mLandmarkEditText, mAliasEditText;
    private GoogleApiClient mGoogleApiClient;

    private static final int PERMISSION_ACCESS_COARSE_LOCATION = 0;

    public EditAddressFragment() {
    }

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
        mAddressID = arguments.getString("addressID", "");
        m_ID = arguments.getInt("_ID", -1);
        mUserServiceResponseReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                handleReceiverResponse(intent);
            }
        };
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_edit_address, container, false);

        initReferences(rootView);

        if (!TextUtils.isEmpty(mAddressID) || m_ID != -1) {
            // getActivity().getSupportLoaderManager().restartLoader(USER_ADDRESS_LOADER, null, this);
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
        if (!TextUtils.isEmpty(mAddressID) || m_ID != -1) {
            mListener.fragmentCreated("Edit Address", true);
        } else {
            mListener.fragmentCreated("Add New Address", true);
        }
        IntentFilter intentFilter = new IntentFilter(getString(R.string.user_data_updated));
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mUserServiceResponseReceiver, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mUserServiceResponseReceiver);
    }

    @Override
    public void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
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

        Button mCancelButton = (Button) rootView.findViewById(R.id.cancel_button);
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.openProfileFragment();
            }
        });

        Button mSaveButton = (Button) rootView.findViewById(R.id.save_button);
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveAddress();
            }
        });

        Button mCurrentLocationButton = (Button) rootView.findViewById(R.id.current_location_button);
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

            address.put(UserAddressTable.COLUMN_ADDRESS_ID, mAddressID);
            address.put(UserAddressTable.COLUMN_PINCODE_ID, "");
            address.put(UserAddressTable.COLUMN_PRIORITY, "");

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

    private void setViewFromData(JSONObject address) throws JSONException {
        mMobileNumberEditText.setText(address.getString(UserAddressTable.COLUMN_CONTACT_NUMBER));
        mLandmarkEditText.setText(address.getString(UserAddressTable.COLUMN_LANDMARK));
        mCityEditText.setText(address.getString(UserAddressTable.COLUMN_CITY));
        mStateEditText.setText(address.getString(UserAddressTable.COLUMN_STATE));
        mPincodeEditText.setText(address.getString(UserAddressTable.COLUMN_PINCODE));
        mAddressEditText.setText(address.getString(UserAddressTable.COLUMN_ADDRESS));
    }

    private void handleReceiverResponse(Intent intent) {
        String extra = intent.getStringExtra("extra");
        if (extra != null && extra.equals(getString(R.string.user_data_modified))) {
            // close fragment
            mListener.openProfileFragment();
        }
    }
}
