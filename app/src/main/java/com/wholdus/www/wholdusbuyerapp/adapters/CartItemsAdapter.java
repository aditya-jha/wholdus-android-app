package com.wholdus.www.wholdusbuyerapp.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
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

public class CartItemsAdapter extends RecyclerView.Adapter<CartItemsAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<CartItem> mData;
    private CartSummaryListenerInterface mListener;

    public CartItemsAdapter(Context context, ArrayList<CartItem> data, CartSummaryListenerInterface listener) {
        mContext = context;
        mData = data;
        mListener = listener;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.list_item_layout_cart_items, parent, false));
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        CartItem cartItem = mData.get(position);
        Product product = cartItem.getProduct();
        if (cartItem == null || product == null){
            return;
        }

        holder.productName.setText(product.getName());
        holder.pricePerPiece.setText(String.format(mContext.getString(R.string.price_per_pcs_format), String.valueOf((int) Math.ceil(product.getMinPricePerUnit()))));
        holder.total.setText(String.format(mContext.getString(R.string.price_format), String.valueOf((int) Math.ceil(cartItem.getFinalPrice()))));
        holder.selectedPieces.setText(String.valueOf(cartItem.getPieces()));

        Glide.with(mContext)
                .load(product.getImageUrl(Constants.SMALL_IMAGE, "1"))
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(new BitmapImageViewTarget(holder.productImage));

        holder.productImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openProductDetails(position);
            }
        });

        holder.productName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openProductDetails(position);
            }
        });

        holder.minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Product product1 = mData.get(position).getProduct();
                int pieces = Integer.parseInt(holder.selectedPieces.getText().toString());
                pieces -= product1.getLotSize();
                if (pieces < product1.getLotSize()) {
                    return;
                }

                addProductToCart(pieces, position);
            }
        });

        holder.plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Product product1 = mData.get(position).getProduct();
                int pieces = Integer.parseInt(holder.selectedPieces.getText().toString());
                pieces += product1.getLotSize();

                addProductToCart(pieces, position);
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
                                addProductToCart(0, position);
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
    }

    private void addProductToCart(int pieces, int position) {
        Product product = mData.get(position).getProduct();
        mListener.enableProgressBar();
        int lots = (pieces/product.getLotSize());
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

    private void openProductDetails(int position){
        Product product = mData.get(position).getProduct();
        Intent intent = new Intent(mContext, ProductDetailActivity.class);
        intent.putExtra(CatalogContract.ProductsTable.TABLE_NAME, product.getProductID());
        mContext.startActivity(intent);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        int id;
        TextView productName;
        TextView pricePerPiece;
        TextView selectedPieces;
        TextView total;
        ImageView productImage;
        ProgressBar progressBar;
        ImageButton removeButton;
        ImageButton minusButton;
        ImageButton plusButton;

        private MyViewHolder(final View itemView) {
            super(itemView);
            productName = (TextView) itemView.findViewById(R.id.cart_item_product_name_text_view);
            pricePerPiece = (TextView) itemView.findViewById(R.id.cart_item_product_price_per_piece_text_view);
            total = (TextView) itemView.findViewById(R.id.cart_item_final_price_text_view);
            progressBar = (ProgressBar) itemView.findViewById(R.id.loading_indicator);
            productImage = (ImageView) itemView.findViewById(R.id.product_image);
            removeButton = (ImageButton) itemView.findViewById(R.id.cart_item_remove_button);
            minusButton = (ImageButton) itemView.findViewById(R.id.minus_button);
            plusButton = (ImageButton) itemView.findViewById(R.id.plus_button);
            selectedPieces = (TextView) itemView.findViewById(R.id.selected_pieces);
        }
    }
}
