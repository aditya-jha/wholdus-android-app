package com.wholdus.www.wholdusbuyerapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.interfaces.HelpSupportListenerInterface;

/**
 * Created by aditya on 9/1/17.
 */

public class FAQFragment extends Fragment {

    private HelpSupportListenerInterface mListener;
    private RecyclerView mRecyclerView;

    public FAQFragment() {}

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mListener = (HelpSupportListenerInterface) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_faq, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.faq_recycler_view);

    }

    @Override
    public void onResume() {
        super.onResume();
        mListener.fragmentCreated(getString(R.string.faq_title));
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
