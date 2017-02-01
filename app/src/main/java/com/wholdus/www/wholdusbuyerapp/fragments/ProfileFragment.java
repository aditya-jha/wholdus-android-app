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
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.adapters.BuyerPersonalDetailsAdapter;
import com.wholdus.www.wholdusbuyerapp.helperClasses.Constants;
import com.wholdus.www.wholdusbuyerapp.helperClasses.HelperFunctions;
import com.wholdus.www.wholdusbuyerapp.helperClasses.TODO;
import com.wholdus.www.wholdusbuyerapp.interfaces.ProfileListenerInterface;
import com.wholdus.www.wholdusbuyerapp.loaders.ProfileLoader;
import com.wholdus.www.wholdusbuyerapp.models.Buyer;
import com.wholdus.www.wholdusbuyerapp.models.BuyerPersonalDetails;
import com.wholdus.www.wholdusbuyerapp.services.UserService;

import java.util.ArrayList;

/**
 * Created by aditya on 19/11/16.
 * fragment for Displaying user profile
 */

public class ProfileFragment extends Fragment implements LoaderManager.LoaderCallbacks<Buyer> {


    private ListView mPersonalDetailsListView;
    private BroadcastReceiver mUserServiceResponseReceiver;
    private ProfileListenerInterface mListener;
    private ProgressBar mPageLoader;
    private CardView mProfileCard;

    private static final int USER_DB_LOADER = 0;

    public ProfileFragment() {
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
                handleAPIResponse(intent);
            }
        };
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mPageLoader = (ProgressBar) view.findViewById(R.id.page_loader);

        mProfileCard = (CardView) view.findViewById(R.id.profile_card);
        mProfileCard.setVisibility(View.INVISIBLE);

        mPersonalDetailsListView = (ListView) view.findViewById(R.id.personal_details_list_view);

        TextView mEditPersonalDetailsTextView = (TextView) view.findViewById(R.id.edit_personal_details_text_view);
        mEditPersonalDetailsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.editPersonalDetails();
            }
        });

        getActivity().getSupportLoaderManager().restartLoader(USER_DB_LOADER, null, this);

        fetchDataFromServer();
    }

    @Override
    public void onResume() {
        super.onResume();

        IntentFilter intentFilter = new IntentFilter(getString(R.string.user_data_updated));
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mUserServiceResponseReceiver, intentFilter);

        mListener.fragmentCreated("My Profile", true);
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mUserServiceResponseReceiver);
        }catch (Exception e){

        }
    }

    @Override
    public void onStop() {
        super.onStop();
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
    public Loader<Buyer> onCreateLoader(int id, Bundle args) {
        return new ProfileLoader(getContext(), true, false, false);
    }

    @Override
    public void onLoadFinished(Loader<Buyer> loader, Buyer buyer) {
        if (buyer == null || buyer.getMobileNumber() == null || mListener == null) return;

        mPageLoader.setVisibility(View.INVISIBLE);
        mProfileCard.setVisibility(View.VISIBLE);

        ArrayList<BuyerPersonalDetails> items = new ArrayList<>();
        items.add(new BuyerPersonalDetails(getString(R.string.name_key),
                buyer.getName().isEmpty() ? getString(R.string.value_not_provided) : buyer.getName(), R.drawable.ic_person_black_24dp));
        items.add(new BuyerPersonalDetails(getString(R.string.company_name_key),
                buyer.getCompanyName().isEmpty() ? getString(R.string.value_not_provided) : buyer.getCompanyName(), R.drawable.ic_store_mall_directory_black_28dp));
        items.add(new BuyerPersonalDetails(getString(R.string.mobile_number_key),
                buyer.getMobileNumber(), R.drawable.ic_phone_black_24dp));
        items.add(new BuyerPersonalDetails(getString(R.string.email_key),
                buyer.getEmail().isEmpty() ? getString(R.string.value_not_provided) : buyer.getEmail(), R.drawable.ic_mail_outline_black_24dp));
        items.add(new BuyerPersonalDetails(getString(R.string.whatsapp_number_key),
                buyer.getWhatsappNumber().isEmpty() ? getString(R.string.value_not_provided) : buyer.getWhatsappNumber(), R.drawable.ic_perm_phone_msg_black_24dp));

        BuyerPersonalDetailsAdapter adapter = new BuyerPersonalDetailsAdapter(getContext(), items);
        mPersonalDetailsListView.setAdapter(adapter);
        HelperFunctions.setListViewHeightBasedOnChildren(mPersonalDetailsListView);
    }

    @Override
    public void onLoaderReset(Loader<Buyer> loader) {

    }

    private void handleAPIResponse(final Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras.getString(Constants.ERROR_RESPONSE) != null) {
            Toast.makeText(getContext(), getString(R.string.no_internet_access), Toast.LENGTH_SHORT).show();
        } else {
            getActivity().getSupportLoaderManager().restartLoader(USER_DB_LOADER, null, this);
        }
    }

    private void fetchDataFromServer() {
        Intent intent = new Intent(getContext(), UserService.class);
        intent.putExtra("TODO", TODO.FETCH_USER_PROFILE);
        getContext().startService(intent);
    }
}
