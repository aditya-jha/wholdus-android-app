package com.wholdus.www.wholdusbuyerapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.adapters.FAQAdapter;
import com.wholdus.www.wholdusbuyerapp.helperClasses.APIConstants;
import com.wholdus.www.wholdusbuyerapp.helperClasses.OkHttpHelper;
import com.wholdus.www.wholdusbuyerapp.helperClasses.TrackingHelper;
import com.wholdus.www.wholdusbuyerapp.interfaces.HelpSupportListenerInterface;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Response;

/**
 * Created by aditya on 9/1/17.
 */

public class FAQFragment extends Fragment {

    private HelpSupportListenerInterface mListener;
    private ExpandableListView mExpandableListView;
    private FAQAdapter mAdapter;
    private LinkedHashMap<String, List<Map<String, String>>> mFAQData;
    private ProgressBar mPageLoader;
    private boolean mShowData;

    public FAQFragment() {}

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
        return inflater.inflate(R.layout.fragment_faq, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mPageLoader = (ProgressBar) view.findViewById(R.id.page_loader);

        mFAQData = new LinkedHashMap<>();
        mExpandableListView = (ExpandableListView) view.findViewById(R.id.expandable_list_view);
        mExpandableListView.setChildDivider(getResources().getDrawable(android.R.color.white));
        fetchFAQFromServer();

        TrackingHelper.getInstance(getContext())
                .logEvent(FirebaseAnalytics.Event.VIEW_ITEM, this.getClass().getSimpleName(), "");
    }

    @Override
    public void onResume() {
        super.onResume();
        mListener.fragmentCreated(getString(R.string.faq_title));
        if (mShowData && mFAQData != null) {
            showData();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void fetchFAQFromServer() {
        mExpandableListView.setVisibility(View.INVISIBLE);
        mPageLoader.setVisibility(View.VISIBLE);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Response response = OkHttpHelper.makeGetRequest(getActivity().getApplicationContext(), OkHttpHelper.generateUrl(APIConstants.FAQ_URL));
                    if (response.isSuccessful()) {
                        JSONArray faqs = new JSONObject(response.body().string()).getJSONArray("faqs");
                        response.body().close();

                        for (int i=0; i<faqs.length(); i++) {
                            JSONObject faq = faqs.getJSONObject(i);
                            JSONArray entries = faq.getJSONArray("faqentries");

                            ArrayList<Map<String, String>> faqEntries = new ArrayList<>();
                            for (int j=0; j<entries.length(); j++) {
                                JSONObject qaEntry = entries.getJSONObject(j);
                                Map<String, String> qa = new HashMap<>();
                                qa.put("question", qaEntry.getString("question"));
                                qa.put("answer", qaEntry.getString("answer"));
                                faqEntries.add(qa);
                            }
                            mFAQData.put(faq.getString("topic"), faqEntries);
                        }
                        showData();
                    } else {
                        showError();
                    }
                } catch (Exception e) {
                }
            }
        }).start();
    }

    private void showData() {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mExpandableListView.setVisibility(View.VISIBLE);
                    mPageLoader.setVisibility(View.INVISIBLE);
                    mAdapter = new FAQAdapter(getContext(), mFAQData);
                    mExpandableListView.setAdapter(mAdapter);
                    mShowData = false;
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
