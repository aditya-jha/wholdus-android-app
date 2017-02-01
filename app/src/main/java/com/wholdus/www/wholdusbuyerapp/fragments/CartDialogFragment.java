package com.wholdus.www.wholdusbuyerapp.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.databaseContracts.CartContract;
import com.wholdus.www.wholdusbuyerapp.databaseContracts.CatalogContract;
import com.wholdus.www.wholdusbuyerapp.interfaces.CartDialogListener;
import com.wholdus.www.wholdusbuyerapp.loaders.CartItemLoader;
import com.wholdus.www.wholdusbuyerapp.loaders.ProductLoader;
import com.wholdus.www.wholdusbuyerapp.models.CartItem;
import com.wholdus.www.wholdusbuyerapp.models.Product;
import com.wholdus.www.wholdusbuyerapp.services.CartService;

import java.util.ArrayList;

/**
 * Created by kaustubh on 19/12/16.
 */

public class CartDialogFragment extends DialogFragment implements View.OnClickListener {

    private CartDialogListener mListener;

    private TextView mLotSize;
    private TextView mProductName;
    private TextView mPricePerPiece;
    private Spinner mPiecesSpinner;
    private TextView mTotalPrice;
    private int mProductID;
    private Product mProduct;
    private int mLots;
    private int mOldLots = -1;
    private ArrayAdapter<Integer> mPiecesAdapter;
    private Button mAddtoCartButton;

    private static final int PRODUCTS_DB_LOADER = 50;
    private static final int CART_DB_LOADER = 51;

    public CartDialogFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mListener = (CartDialogListener) context;
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            Window window = getDialog().getWindow();
            Point size = new Point();

            Display display = window.getWindowManager().getDefaultDisplay();
            display.getSize(size);

            int width = size.x;

            window.setLayout((int) (width), WindowManager.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.CENTER);
        } catch (Exception e) {
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_cart_dialog, container, false);
        Bundle mArgs = getArguments();
        mProductID = mArgs.getInt(CatalogContract.ProductsTable.COLUMN_PRODUCT_ID);
        mLots = 1;
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initReferences((ViewGroup) getView());
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().restartLoader(CART_DB_LOADER, null, new CartItemLoaderManager());
        getLoaderManager().restartLoader(PRODUCTS_DB_LOADER, null, new ProductLoaderManager());
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        mListener.dismissDialog();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View view) {
        final int ID = view.getId();
        switch (ID) {
            case R.id.cart_dialog_add_to_cart_button:
                addProductToCart();
                break;
        }
    }

    public void initReferences(ViewGroup rootView) {
        mLotSize = (TextView) rootView.findViewById(R.id.cart_dialog_lot_size_text_view);
        mProductName = (TextView) rootView.findViewById(R.id.cart_dialog_product_name_text_view);
        mPricePerPiece = (TextView) rootView.findViewById(R.id.cart_dialog_price_per_piece_text_view);
        mTotalPrice = (TextView) rootView.findViewById(R.id.cart_dialog_total_price_text_view);
        mPiecesSpinner = (Spinner) rootView.findViewById(R.id.cart_dialog_pieces_spinner);

        mAddtoCartButton = (Button) rootView.findViewById(R.id.cart_dialog_add_to_cart_button);
        mAddtoCartButton.setOnClickListener(this);
        mAddtoCartButton.setEnabled(false);
    }

    public void setViewOnLoad() {

        mPiecesAdapter = new ArrayAdapter<>(getContext(), R.layout.cart_dialog_spinner_text_view);
        for (int i = 1; i < 11; i++) {
            mPiecesAdapter.add(mProduct.getLotSize() * i);
        }
        //mPiecesAdapter.setDropDownViewResource(R.layout.cart_dialog_spinner_text_view);
        mPiecesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mPiecesSpinner.setAdapter(mPiecesAdapter);
        if (mLots != -1) {
            mPiecesSpinner.setSelection(mPiecesAdapter.getPosition(mLots * mProduct.getLotSize()));
        } else {
            mPiecesSpinner.setSelection(mPiecesAdapter.getPosition(mProduct.getLotSize()));
        }

        mLotSize.setText(String.valueOf(mProduct.getLotSize()));
        mProductName.setText(mProduct.getName());
        mPricePerPiece.setText(String.format(getString(R.string.price_format), String.valueOf((int) Math.ceil(mProduct.getMinPricePerUnit()))));

        mPiecesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mLots = (((int) (adapterView.getItemAtPosition(i))) / mProduct.getLotSize());
                float finalPrice = ((int) adapterView.getItemAtPosition(i)) * mProduct.getMinPricePerUnit();
                mTotalPrice.setText(String.format(getString(R.string.price_format), String.valueOf((int) Math.ceil(finalPrice))));
                mPiecesSpinner.setSelection(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        if (mProduct.getDeleteStatus() || !mProduct.getShowOnline()){
            mAddtoCartButton.setEnabled(false);
            mAddtoCartButton.setText(R.string.out_of_stock_key);
        } else {
            mAddtoCartButton.setEnabled(true);
        }

    }

    public void addProductToCart() {
        if (mOldLots != mLots && mProduct != null) {
            Intent intent = new Intent(getContext(), CartService.class);
            intent.putExtra("TODO", R.string.write_cart_item);
            intent.putExtra(CatalogContract.ProductsTable.COLUMN_PRODUCT_ID, mProduct.getProductID());
            intent.putExtra(CartContract.CartItemsTable.COLUMN_LOTS, mLots);
            intent.putExtra(CartContract.CartItemsTable.COLUMN_PIECES, mProduct.getLotSize() * mLots);
            intent.putExtra(CartContract.CartItemsTable.COLUMN_LOT_SIZE, mProduct.getLotSize());
            intent.putExtra(CartContract.CartItemsTable.COLUMN_RETAIL_PRICE_PER_PIECE, mProduct.getPricePerUnit());
            intent.putExtra(CartContract.CartItemsTable.COLUMN_CALCULATED_PRICE_PER_PIECE, mProduct.getMinPricePerUnit());
            intent.putExtra(CartContract.CartItemsTable.COLUMN_FINAL_PRICE, mProduct.getMinPricePerUnit() * mLots * mProduct.getLotSize());
            getContext().startService(intent);
            if (mOldLots == -1) {
                Toast.makeText(getContext(), "Added to Cart", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Cart updated", Toast.LENGTH_SHORT).show();
            }
        }
        dismiss();
    }

    private class CartItemLoaderManager implements LoaderManager.LoaderCallbacks<ArrayList<CartItem>> {
        @Override
        public void onLoaderReset(Loader<ArrayList<CartItem>> loader) {
        }

        @Override
        public void onLoadFinished(Loader<ArrayList<CartItem>> loader, ArrayList<CartItem> data) {
            if (data != null && !data.isEmpty()) {
                mAddtoCartButton.setText("UPDATE CART");
                mLots = data.get(0).getLots();
                mOldLots = data.get(0).getLots();
                if (mProduct != null && mPiecesAdapter != null) {
                    float finalPrice = mLots * mProduct.getLotSize() * mProduct.getMinPricePerUnit();
                    mTotalPrice.setText(String.format(getString(R.string.price_format), String.valueOf((int) Math.ceil(finalPrice))));
                    mPiecesSpinner.setSelection(mPiecesAdapter.getPosition(mLots * mProduct.getLotSize()));
                }
            } else {
                if (mProduct != null && mPiecesAdapter != null) {
                    float finalPrice = mProduct.getLotSize() * mProduct.getMinPricePerUnit();
                    mTotalPrice.setText(String.format(getString(R.string.price_format), String.valueOf((int) Math.ceil(finalPrice))));
                    mPiecesSpinner.setSelection(mPiecesAdapter.getPosition(mProduct.getLotSize()));
                }
            }
        }

        @Override
        public Loader<ArrayList<CartItem>> onCreateLoader(int id, Bundle args) {
            return new CartItemLoader(getContext(), -1, null, -1, mProductID, -1, false, null);
        }
    }

    private class ProductLoaderManager implements LoaderManager.LoaderCallbacks<Product> {
        @Override
        public Loader<Product> onCreateLoader(int id, Bundle args) {
            return new ProductLoader(getContext(), mProductID);
        }

        @Override
        public void onLoadFinished(Loader<Product> loader, Product data) {
            if (data != null) {
                mProduct = data;
                setViewOnLoad();
            } else {
                dismiss();
                Toast.makeText(getContext(), R.string.api_error_message,Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onLoaderReset(Loader<Product> loader) {
        }
    }
}
