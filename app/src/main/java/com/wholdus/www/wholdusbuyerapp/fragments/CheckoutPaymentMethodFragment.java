package com.wholdus.www.wholdusbuyerapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.interfaces.CartListenerInterface;

/**
 * Created by kaustubh on 29/12/16.
 */

public class CheckoutPaymentMethodFragment extends Fragment {
    // TODO Populate view from get request for payment methods

    private CartListenerInterface mListener;
    private RadioButton mCODRadioButton;
    private RadioButton mNEFTRadioButton;
    private RadioButton mCreditRadioButton;
    private TextView mDescription;

    private float mCODValue;
    private float mOrderValue;

    public static final float COD_CHARGE_PERCENT = (float) 0.02;

    public CheckoutPaymentMethodFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mListener = (CartListenerInterface) context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_checkout_payment_method, container, false);

        initReferences(rootView);
        mListener.disableProgressBar();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        Bundle args = getArguments();
        mListener.fragmentCreated("Payment Method", true);
        if (args != null) {
            mCODValue = args.getFloat("orderValue", 5000)*COD_CHARGE_PERCENT;
            mOrderValue = args.getFloat("finalValue", 5000);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void initReferences(ViewGroup rootView) {
        mCODRadioButton = (RadioButton) rootView.findViewById(R.id.cod_radio_button);
        mNEFTRadioButton = (RadioButton) rootView.findViewById(R.id.neft_radio_button);
        mCreditRadioButton = (RadioButton) rootView.findViewById(R.id.credit_radio_button);
        mDescription = (TextView) rootView.findViewById(R.id.payment_method_description);

        mCODRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                radioButtonClicked(1, mCODRadioButton.isChecked());
            }
        });

        mNEFTRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                radioButtonClicked(2, mNEFTRadioButton.isChecked());
            }
        });

        mCreditRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                radioButtonClicked(3, mCreditRadioButton.isChecked());
            }
        });
    }

    private void radioButtonClicked(int option, boolean checked) {
        mListener.CODApplied(false);
        switch (option) {
            case 1:
                if (checked) {
                    mNEFTRadioButton.setChecked(false);
                    mListener.setPaymentMethod(0);
                    String descriptionText = "2 % COD charges at " + String.format(getString(R.string.price_format), String.valueOf((int) Math.ceil(mCODValue)));
                    int orderValue = (int) Math.ceil(mCODValue +mOrderValue);
                    descriptionText += "\nOrder value will be " + String.format(getString(R.string.price_format), String.valueOf(orderValue));
                    mListener.CODApplied(true);
                    mDescription.setText(descriptionText);
                } else {
                    mListener.setPaymentMethod(-1);
                    mDescription.setText("");
                }
                break;
            case 2:
                if (checked) {
                    mCODRadioButton.setChecked(false);
                    mListener.setPaymentMethod(1);
                    mDescription.setText(String.format(getString(R.string.neft_payment), String.valueOf((int) Math.ceil(mOrderValue))));
                } else {
                    mListener.setPaymentMethod(-1);
                    mDescription.setText("");
                }
                break;
            case 3:
                mCreditRadioButton.setChecked(false);
                mCODRadioButton.setChecked(false);
                mNEFTRadioButton.setChecked(false);
                if (checked) {
                    mDescription.setText("15 more transactions required to avail credit");
                } else {
                    mDescription.setText("");
                }
                mListener.setPaymentMethod(-1);
                break;
        }
    }
}
