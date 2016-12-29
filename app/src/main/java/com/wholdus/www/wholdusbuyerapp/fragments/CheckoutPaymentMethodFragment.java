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

    public CheckoutPaymentMethodFragment(){
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (CartListenerInterface) context;
        } catch (ClassCastException cee) {
            cee.printStackTrace();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_checkout_payment_method, container, false);

        initReferences(rootView);
        mListener.disableProgressBar();
        mListener.fragmentCreated("Payment Method", true);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        Bundle args = getArguments();
        if (args != null) {
            mCODValue = args.getFloat("orderValue", 5000) / 50;
            mOrderValue = args.getFloat("finalValue", 5000);
        }
    }

    private void initReferences(ViewGroup rootView){
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

    private void radioButtonClicked(int option, boolean checked){

        switch (option){
            case 1:
                if(checked){
                    mNEFTRadioButton.setChecked(false);
                    mListener.setPaymentMethod(0);
                    mDescription.setText("2 % COD charges at Rs. " + String.format("%.0f", mCODValue));
                } else {
                    mListener.setPaymentMethod(-1);
                    mDescription.setText("");
                }
                break;
            case 2:
                if(checked){
                    mCODRadioButton.setChecked(false);
                    mListener.setPaymentMethod(1);
                    mDescription.setText("Please transfer the order amount Rs. " + String.format("%.0f", mOrderValue)
                            + " to Acc. No. 017105008405 with IFSC Code ICIC0000171 in the name of PROBZIP BUSINESS SOLUTIONS PVT LTD");
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
