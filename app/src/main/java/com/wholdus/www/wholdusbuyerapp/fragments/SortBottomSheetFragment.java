package com.wholdus.www.wholdusbuyerapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.helperClasses.FilterClass;
import com.wholdus.www.wholdusbuyerapp.interfaces.CategoryProductListenerInterface;

/**
 * Created by aditya on 15/12/16.
 */

public class SortBottomSheetFragment extends BottomSheetDialogFragment {

    private int mSelectedSort;
    private CategoryProductListenerInterface mListener;

    public static synchronized SortBottomSheetFragment newInstance() {
        SortBottomSheetFragment sortBottomSheetFragment = new SortBottomSheetFragment();
        return sortBottomSheetFragment;
    }

    public SortBottomSheetFragment() {
        super();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (CategoryProductListenerInterface) context;
        } catch (ClassCastException cee) {
            Log.e(this.getClass().getSimpleName(), " must implement " + CategoryProductListenerInterface.class.getSimpleName());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_sort_bottomsheet, container, false);

        RadioGroup radioGroup = (RadioGroup) rootView.findViewById(R.id.sort_radio_group);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                FilterClass.setSelectedSort(i);
            }
        });

        mSelectedSort = FilterClass.getSelectSort();
        if (mSelectedSort == -1) {
            mSelectedSort = R.id.newest_first;
            FilterClass.setSelectedSort(R.id.newest_first);
        }
        RadioButton radioButton = (RadioButton) rootView.findViewById(mSelectedSort);
        radioButton.setChecked(true);

        Button sortButton = (Button) rootView.findViewById(R.id.sort_button);
        sortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSelectedSort != FilterClass.getSelectSort()) {
                    mListener.sortClicked();
                }
                dismiss();
            }
        });
        return rootView;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}