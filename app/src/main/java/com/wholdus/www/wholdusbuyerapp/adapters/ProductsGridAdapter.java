package com.wholdus.www.wholdusbuyerapp.adapters;

import android.content.Context;
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
import com.wholdus.www.wholdusbuyerapp.helperClasses.Constants;
import com.wholdus.www.wholdusbuyerapp.interfaces.ItemClickListener;
import com.wholdus.www.wholdusbuyerapp.models.GridProductModel;

import java.util.ArrayList;

/**
 * Created by aditya on 15/12/16.
 */

public class ProductsGridAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private ArrayList<GridProductModel> mData;
    private ItemClickListener mListener;

    public static final int PRODUCT_VIEW = 0;
    public static final int LOADER_VIEW = 1;

    public ProductsGridAdapter(Context context, ArrayList<GridProductModel> products, final ItemClickListener listener) {
        mContext = context;
        mData = products;
        mListener = listener;
    }

    public void clear() {
        mData.clear();
        notifyDataSetChanged();
    }

    public void add(ArrayList<GridProductModel> products) {
        mData.addAll(products);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mData.get(position) == null ? LOADER_VIEW : PRODUCT_VIEW;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(mContext);

        switch (viewType) {
            case LOADER_VIEW:
                viewHolder = new ProgressViewHolder(inflater.inflate(R.layout.layout_progress_bar, parent, false));
                break;
            default:
                viewHolder = new ProductViewHolder(inflater.inflate(R.layout.layout_product_grid, parent, false));
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case PRODUCT_VIEW:
                onBindProductViewHolder((ProductsGridAdapter.ProductViewHolder) holder, position);
                break;
            case LOADER_VIEW:
                break;
        }
    }

    private void onBindProductViewHolder(final ProductsGridAdapter.ProductViewHolder holder, int position) {
        GridProductModel product = mData.get(position);

        holder.mProductName.setText(product.getName());
        holder.mProductFabric.setText(product.getFabric());
        holder.mProductPrice.setText(String.format(mContext.getString(R.string.price_per_pcs_format), String.valueOf((int) Math.ceil(product.getPrice()))));

        holder.mFabButton.setImageResource(product.getLikeStatus() ? R.drawable.ic_favorite_red_24dp : R.drawable.ic_favorite_border_black_24dp);
        holder.mCartButton.setImageResource(product.getCartCount() > 0 ? R.drawable.ic_local_grocery_store_black_24dp : R.drawable.ic_add_shopping_cart_black_24dp);

        Glide.with(mContext)
                .load(product.getImageUrl(Constants.SMALL_IMAGE, "1"))
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(new BitmapImageViewTarget(holder.mProductImage));
        holder.mListener = mListener;
    }

    private static class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView mProductImage;
        TextView mProductName, mProductFabric, mProductPrice;
        ImageButton mFabButton, mShareButton, mCartButton;
        ProgressBar mProgressBar;

        private ItemClickListener mListener;

        ProductViewHolder(View itemView) {
            super(itemView);

            mProductImage = (ImageView) itemView.findViewById(R.id.product_image);
            mProductName = (TextView) itemView.findViewById(R.id.product_name);
            mProductFabric = (TextView) itemView.findViewById(R.id.product_fabric);
            mProductPrice = (TextView) itemView.findViewById(R.id.product_price);
            mFabButton = (ImageButton) itemView.findViewById(R.id.fav_icon_image_view);
            mShareButton = (ImageButton) itemView.findViewById(R.id.share_image_view);
            mCartButton = (ImageButton) itemView.findViewById(R.id.cart_image_view);
            mProgressBar = (ProgressBar) itemView.findViewById(R.id.loading_indicator);

            mCartButton.setOnClickListener(this);
            mFabButton.setOnClickListener(this);
            mShareButton.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (position == RecyclerView.NO_POSITION) return;

            if (mListener != null) {
                final int id = view.getId();
                if (id == R.id.share_image_view) {
                    mListener.itemClicked(mProductImage, position, id);
                } else {
                    mListener.itemClicked(view, position, id);
                }
            }
        }
    }

    private static class ProgressViewHolder extends RecyclerView.ViewHolder {
        ProgressViewHolder(View itemView) {
            super(itemView);
        }
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
        if (holder.getItemViewType() == PRODUCT_VIEW) {
            ProductsGridAdapter.ProductViewHolder productViewHolder = (ProductsGridAdapter.ProductViewHolder) holder;
            Glide.clear(productViewHolder.mProductImage);
        }
    }
}
