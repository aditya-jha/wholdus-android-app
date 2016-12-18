package com.wholdus.www.wholdusbuyerapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.wholdus.www.wholdusbuyerapp.R;

/**
 * Created by kaustubh on 19/12/16.
 */

public class CartDialogFragment extends DialogFragment {

    private TextView mLotSize;
    private TextView mProductName;
    private TextView mLotDescription;
    private TextView mPricePerPiece;
    private Spinner mPiecesSpinner;
    private TextView mTotalPrice;
    private Button mAddtoCart;
    private Context mContext;

    private final int PRODUCTS_DB_LOADER = 50;
    private final int CART_DB_LOADER = 51;

    public CartDialogFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_cart_dialog, container, false);
        initReferences(rootView);
        return rootView;
    }

    public void initReferences(ViewGroup rootView){
        mLotSize = (TextView) rootView.findViewById(R.id.cart_dialog_lot_size_text_view);
        mProductName = (TextView) rootView.findViewById(R.id.cart_dialog_product_name_text_view);
        mLotDescription = (TextView) rootView.findViewById(R.id.cart_dialog_lot_description_text_view);
        mPricePerPiece = (TextView) rootView.findViewById(R.id.cart_dialog_price_per_piece_text_view);
        mTotalPrice = (TextView) rootView.findViewById(R.id.cart_dialog_total_price_text_view);
        mPiecesSpinner = (Spinner) rootView.findViewById(R.id.cart_dialog_pieces_spinner);
        mAddtoCart = (Button) rootView.findViewById(R.id.cart_dialog_add_to_cart_button);

        mAddtoCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: Add to cart
            }
        });

        ArrayAdapter<Integer> piecesAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1);
        piecesAdapter.addAll(new Integer[] {1, 2, 3});
        piecesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mPiecesSpinner.setAdapter(piecesAdapter);

        mPiecesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mTotalPrice.setText(String.valueOf(adapterView.getItemAtPosition(i)));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

    }

}
