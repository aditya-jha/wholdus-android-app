package com.wholdus.www.wholdusbuyerapp.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarChangeListener;
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar;
import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.databaseContracts.UserProfileContract;
import com.wholdus.www.wholdusbuyerapp.helperClasses.FilterClass;
import com.wholdus.www.wholdusbuyerapp.helperClasses.TODO;
import com.wholdus.www.wholdusbuyerapp.services.UserService;

/**
 * Created by aditya on 27/12/16.
 */

public class BuyerInterestDialogFragment extends DialogFragment implements View.OnClickListener {

    private static BuyerInterestDialogFragment mInstance;

    public BuyerInterestDialogFragment() {

    }

    public static synchronized BuyerInterestDialogFragment getInstance(int categoryID, String categoryName, int minPrice, int maxPrice) {
        if (mInstance == null) {
            mInstance = new BuyerInterestDialogFragment();
        }
        Bundle args = new Bundle();
        args.putInt(UserProfileContract.UserInterestsTable.COLUMN_CATEGORY_ID, categoryID);
        args.putString(UserProfileContract.UserInterestsTable.COLUMN_CATEGORY_NAME, categoryName);
        args.putInt(UserProfileContract.UserInterestsTable.COLUMN_MIN_PRICE_PER_UNIT, minPrice);
        args.putInt(UserProfileContract.UserInterestsTable.COLUMN_MAX_PRICE_PER_UNIT, maxPrice);

        mInstance.setArguments(args);
        return mInstance;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return (ViewGroup) inflater.inflate(R.layout.fragment_buyer_interest_dialog, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();

        final TextView minPriceValue = (TextView) view.findViewById(R.id.min_price_value);
        final TextView maxPriceValue = (TextView) view.findViewById(R.id.max_price_value);

        CrystalRangeSeekbar priceRangeSeekBar = (CrystalRangeSeekbar) view.findViewById(R.id.price_range);
        priceRangeSeekBar.setMinValue(FilterClass.MIN_PRICE_DEFAULT);
        priceRangeSeekBar.setMaxValue(FilterClass.MAX_PRICE_DEFAULT);
        priceRangeSeekBar
                .setMinStartValue(args.getInt(UserProfileContract.UserInterestsTable.COLUMN_MAX_PRICE_PER_UNIT, FilterClass.MIN_PRICE_DEFAULT))
                .setMaxStartValue(args.getInt(UserProfileContract.UserInterestsTable.COLUMN_MIN_PRICE_PER_UNIT, FilterClass.MAX_PRICE_DEFAULT))
                .apply();

        priceRangeSeekBar.setOnRangeSeekbarChangeListener(new OnRangeSeekbarChangeListener() {
            @Override
            public void valueChanged(Number minValue, Number maxValue) {
                maxPriceValue.setText(maxValue.toString());
                minPriceValue.setText(minValue.toString());
            }
        });

        Button saveButton = (Button) view.findViewById(R.id.submit_button);
        saveButton.setOnClickListener(this);

        Button cancelButton = (Button) view.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        Point size = new Point();

        try {
            Display display = window.getWindowManager().getDefaultDisplay();
            display.getSize(size);

            int width = size.x;

            window.setLayout((int) (width * 1), WindowManager.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.CENTER);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(View view) {
        final int ID = view.getId();
        switch (ID) {
            case R.id.cancel_button:
                dismiss();
                break;
            case R.id.submit_button:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        CrystalRangeSeekbar rangeSeekbar = (CrystalRangeSeekbar) getDialog().findViewById(R.id.price_range);
                        final int minPrice = rangeSeekbar.getSelectedMinValue().intValue();
                        final int maxPrice = rangeSeekbar.getSelectedMaxValue().intValue();

                        final int categoryID = getArguments().getInt(UserProfileContract.UserInterestsTable.COLUMN_CATEGORY_ID);
                        final String categoryName = getArguments().getString(UserProfileContract.UserInterestsTable.COLUMN_CATEGORY_NAME);

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(getContext(), UserService.class);
                                intent.putExtra("TODO", TODO.UPDATE_BUYER_INTEREST);
                                intent.putExtra(UserProfileContract.UserInterestsTable.COLUMN_FABRIC_FILTER_TEXT, "");
                                intent.putExtra(UserProfileContract.UserInterestsTable.COLUMN_CATEGORY_ID, categoryID);
                                intent.putExtra(UserProfileContract.UserInterestsTable.COLUMN_CATEGORY_NAME, categoryName);
                                intent.putExtra(UserProfileContract.UserInterestsTable.COLUMN_MIN_PRICE_PER_UNIT, minPrice);
                                intent.putExtra(UserProfileContract.UserInterestsTable.COLUMN_MAX_PRICE_PER_UNIT, maxPrice);

                                if (maxPrice != FilterClass.MAX_PRICE_DEFAULT && minPrice != FilterClass.MIN_PRICE_DEFAULT) {
                                    intent.putExtra(UserProfileContract.UserInterestsTable.COLUMN_PRICE_FILTER_APPLIED, 1);
                                } else {
                                    intent.putExtra(UserProfileContract.UserInterestsTable.COLUMN_PRICE_FILTER_APPLIED, 0);
                                }
                                getContext().startService(intent);
                            }
                        });
                        dismiss();
                    }
                }).start();
                break;
        }
    }
}
