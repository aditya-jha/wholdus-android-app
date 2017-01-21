package com.wholdus.www.wholdusbuyerapp.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.activities.ProductDetailActivity;
import com.wholdus.www.wholdusbuyerapp.databaseContracts.CartContract;
import com.wholdus.www.wholdusbuyerapp.databaseContracts.CatalogContract;
import com.wholdus.www.wholdusbuyerapp.helperClasses.Constants;
import com.wholdus.www.wholdusbuyerapp.interfaces.CartSummaryListenerInterface;
import com.wholdus.www.wholdusbuyerapp.models.CartItem;
import com.wholdus.www.wholdusbuyerapp.models.Product;
import com.wholdus.www.wholdusbuyerapp.services.CartService;

import java.util.ArrayList;

/**
 * Created by kaustubh on 30/12/16.
 */

public class CartItemsAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<CartItem> mData;
    private CartSummaryListenerInterface mListener;

    public CartItemsAdapter(Context context, ArrayList<CartItem> data, CartSummaryListenerInterface listener) {
        mContext = context;
        mData = data;
        mListener = listener;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public Object getItem(int i) {
        return mData.get(i);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        final ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_layout_cart_items, viewGroup, false);
            holder = new ViewHolder();
            holder.productName = (TextView) convertView.findViewById(R.id.cart_item_product_name_text_view);
            holder.pricePerPiece = (TextView) convertView.findViewById(R.id.cart_item_product_price_per_piece_text_view);
            holder.total = (TextView) convertView.findViewById(R.id.cart_item_final_price_text_view);
            holder.pieces = (Spinner) convertView.findViewById(R.id.cart_item_pieces_spinner);
            holder.progressBar = (ProgressBar) convertView.findViewById(R.id.loading_indicator);
            holder.productImage = (ImageView) convertView.findViewById(R.id.product_image);
            holder.removeButton = (ImageButton) convertView.findViewById(R.id.cart_item_remove_button);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        CartItem cartItem = mData.get(position);
        final Product product = cartItem.getProduct();

        holder.productName.setText(product.getName());
        holder.pricePerPiece.setText(String.format(mContext.getString(R.string.price_per_pcs_format), String.valueOf((int) Math.ceil(product.getMinPricePerUnit()))));
        holder.total.setText(String.format(mContext.getString(R.string.price_format), String.valueOf((int) Math.ceil(cartItem.getFinalPrice()))));

        ArrayAdapter<Integer> piecesAdapter = new ArrayAdapter<>(mContext, R.layout.cart_dialog_spinner_text_view);
        for (int j = 1; j < 11; j++) {
            piecesAdapter.add(product.getLotSize() * j);
        }
        piecesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.pieces.setAdapter(piecesAdapter);
        final int oldSelectionPosition = piecesAdapter.getPosition(cartItem.getLots() * product.getLotSize());
        holder.pieces.setSelection(oldSelectionPosition);

        Glide.with(mContext)
                .load(product.getImageUrl(Constants.SMALL_IMAGE, "1"))
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .into(holder.productImage);

        holder.productImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, ProductDetailActivity.class);
                intent.putExtra(CatalogContract.ProductsTable.TABLE_NAME, product.getProductID());
                mContext.startActivity(intent);
            }
        });

        holder.pieces.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (oldSelectionPosition != i) {
                    holder.pieces.setSelection(i);
                    addProductToCart(((int) holder.pieces.getSelectedItem()) / product.getLotSize(), product);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        holder.removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                addProductToCart(0, product);
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage("Do you want to remove this product from cart?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();

            }
        });

        return convertView;
    }

    private void addProductToCart(int lots, Product product) {
        mListener.enableProgressBar();
        Intent intent = new Intent(mContext, CartService.class);
        intent.putExtra("TODO", R.string.write_cart_item);
        intent.putExtra(CatalogContract.ProductsTable.COLUMN_PRODUCT_ID, product.getProductID());
        intent.putExtra(CartContract.CartItemsTable.COLUMN_LOTS, lots);
        intent.putExtra(CartContract.CartItemsTable.COLUMN_PIECES, product.getLotSize() * lots);
        intent.putExtra(CartContract.CartItemsTable.COLUMN_LOT_SIZE, product.getLotSize());
        intent.putExtra(CartContract.CartItemsTable.COLUMN_RETAIL_PRICE_PER_PIECE, product.getPricePerUnit());
        intent.putExtra(CartContract.CartItemsTable.COLUMN_CALCULATED_PRICE_PER_PIECE, product.getMinPricePerUnit());
        intent.putExtra(CartContract.CartItemsTable.COLUMN_FINAL_PRICE, product.getMinPricePerUnit() * lots * product.getLotSize());
        mContext.startService(intent);
    }

    class ViewHolder {
        int id;
        TextView productName;
        TextView pricePerPiece;
        Spinner pieces;
        TextView total;
        ImageView productImage;
        ProgressBar progressBar;
        ImageButton removeButton;
    }
}
