package com.wholdus.www.wholdusbuyerapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crash.FirebaseCrash;
import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.helperClasses.APIConstants;
import com.wholdus.www.wholdusbuyerapp.helperClasses.OkHttpHelper;
import com.wholdus.www.wholdusbuyerapp.helperClasses.TrackingHelper;
import com.wholdus.www.wholdusbuyerapp.interfaces.HelpSupportListenerInterface;

import org.json.JSONObject;

import okhttp3.Response;

/**
 * Created by aditya on 11/1/17.
 */

public class HelpSupportFragment extends Fragment {

    private HelpSupportListenerInterface mListener;
    private String mTitle, mURL, mAPIResponseKey, mContent;
    private ProgressBar mPageLoader;
    private ScrollView mPageLayout;
    private TextView mContentTextView;
    private boolean mShowData;

    public HelpSupportFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mListener = (HelpSupportListenerInterface) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mShowData = false;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setFragmentUsage();
        return inflater.inflate(R.layout.fragment_helpsupport, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mPageLoader = (ProgressBar) view.findViewById(R.id.page_loader);
        mPageLayout = (ScrollView) view.findViewById(R.id.page_layout);
        mContentTextView = (TextView) view.findViewById(R.id.content);

        fetchContentFromServer();

        TrackingHelper.getInstance(getContext())
                .logEvent(FirebaseAnalytics.Event.VIEW_ITEM, this.getClass().getSimpleName(), "");
    }

    @Override
    public void onResume() {
        super.onResume();
        mListener.fragmentCreated(mTitle);
        if (mShowData) showData();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void setFragmentUsage() {
        Bundle args = getArguments();
        String todo = args.getString("TODO", APIConstants.ABOUT_US_URL);

        switch (todo) {
            case APIConstants.ABOUT_US_URL:
                mTitle = getString(R.string.aboutus_title);
                mURL = OkHttpHelper.generateUrl(APIConstants.ABOUT_US_URL);
                mAPIResponseKey = "about_us";
                break;
            case APIConstants.PRIVACY_POLICY_URL:
                mTitle = getString(R.string.privacy_policy_title);
                mURL = OkHttpHelper.generateUrl(APIConstants.PRIVACY_POLICY_URL);
                mAPIResponseKey = "privacy_policy";
                break;
            case APIConstants.RETURN_REFUND_POLICY_URL:
                mTitle = getString(R.string.return_refund_title);
                mURL = OkHttpHelper.generateUrl(APIConstants.RETURN_REFUND_POLICY_URL);
                mAPIResponseKey = "terms_and_conditions";
                break;
        }
    }

    private void fetchContentFromServer() {
        mPageLayout.setVisibility(View.INVISIBLE);
        mPageLoader.setVisibility(View.VISIBLE);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Response response = OkHttpHelper.makeGetRequest(getActivity().getApplicationContext(), mURL);
                    if (response.isSuccessful()) {
                        mContent = new JSONObject(response.body().string()).getString(mAPIResponseKey);
                        response.body().close();
                        showData();
                    } else {
                        showError();
                    }
                } catch (Exception e) {
                    showError();
                }
            }
        }).start();
    }

    private void showData() {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mContent != null) {
                        mPageLoader.setVisibility(View.INVISIBLE);
                        mPageLayout.setVisibility(View.VISIBLE);
                        mContentTextView.setText(Html.fromHtml(mContent));
                        mShowData = false;
                    }
                }
            });
        } else {
            mShowData = true;
        }
    }

    private void showError() {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getContext(), getString(R.string.api_error_message), Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}
