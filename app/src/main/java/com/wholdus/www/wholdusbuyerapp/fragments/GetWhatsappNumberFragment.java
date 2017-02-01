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
import android.widget.Toast;

import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.helperClasses.InputValidationHelper;
import com.wholdus.www.wholdusbuyerapp.helperClasses.TODO;
import com.wholdus.www.wholdusbuyerapp.interfaces.OnBoardingListenerInterface;
import com.wholdus.www.wholdusbuyerapp.loaders.ProfileLoader;
import com.wholdus.www.wholdusbuyerapp.models.Buyer;
import com.wholdus.www.wholdusbuyerapp.services.UserService;

/**
 * Created by kaustubh on 16/1/17.
 */

public class GetWhatsappNumberFragment extends Fragment implements LoaderManager.LoaderCallbacks<Buyer> {

    private OnBoardingListenerInterface mListener;
    private TextInputLayout mMobileNumberWrapper;
    private TextInputEditText mMobileNumberEditText;
    private BroadcastReceiver mUserServiceResponseReceiver;

    private final int USER_DB_LOADER = 1200;

    private Buyer mBuyer;

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
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_get_whatsapp_number, container, false);
        initReferences(rootView);

        fetchDataFromServer();
        return rootView;
    }

    @Override
    public void onResume(){
        super.onResume();
        mListener.fragmentCreated("Whatsapp Number", true);

        getActivity().getSupportLoaderManager().restartLoader(USER_DB_LOADER, null, this);

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
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mUserServiceResponseReceiver = null;
    }

    private void initReferences(ViewGroup rootView){
        mMobileNumberEditText = (TextInputEditText) rootView.findViewById(R.id.mobile_number_edit_text);
        mMobileNumberEditText.setHint("Whatsapp Number");
        mMobileNumberWrapper = (TextInputLayout) rootView.findViewById(R.id.mobile_number_wrapper);
        mMobileNumberWrapper.setHint("Whatsapp Number");
    }

    private void fetchDataFromServer(){
        Intent userDataIntent = new Intent(getContext(), UserService.class);
        userDataIntent.putExtra("TODO", TODO.FETCH_USER_PROFILE);
        getContext().startService(userDataIntent);
    }

    private void handleAPIResponse(Intent intent){
        if (mBuyer == null){
            getActivity().getSupportLoaderManager().restartLoader(USER_DB_LOADER, null, this);
        }
    }

    public void updateWhatsappNumber(){
        String mobileNumber = getValueFromEditText(mMobileNumberEditText);
        if (InputValidationHelper.isValidMobileNumber(mMobileNumberWrapper, mobileNumber)){
            Intent intent = new Intent(getContext(), UserService.class);
            intent.putExtra("TODO", R.string.update_buyer_whatsapp_number);
            intent.putExtra(getString(R.string.whatsapp_number_key), mobileNumber);
            getContext().startService(intent);
            mListener.whatsappNumberSaved();
        }
    }

    private String getValueFromEditText(TextInputEditText editText) {
        try {
            return editText.getText().toString();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void onLoaderReset(Loader<Buyer> loader) {

    }

    @Override
    public void onLoadFinished(Loader<Buyer> loader, Buyer data) {
        if (data != null && mListener != null){
            mBuyer = data;
            mMobileNumberEditText.setText(mBuyer.getWhatsappNumber());
        }
    }

    @Override
    public Loader<Buyer> onCreateLoader(int id, Bundle args) {
        return new ProfileLoader(getContext(), true, false, false);
    }
}
