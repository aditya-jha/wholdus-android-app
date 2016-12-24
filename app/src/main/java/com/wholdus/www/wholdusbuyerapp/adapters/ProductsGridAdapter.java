package com.wholdus.www.wholdusbuyerapp.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
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

    private static final int PRODUCT_VIEW = 0;
    private static final int LOADER_VIEW = 1;

    public ProductsGridAdapter(Context context, ArrayList<GridProductModel> products, final ItemClickListener listener) {
        mContext = context;
        mData = products;
        mListener = listener;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public int getItemViewType(int position) {
        GridProductModel gridProductModel = mData.get(position);
        if (gridProductModel.getName() == null) {
            return LOADER_VIEW;
        } else {
            return PRODUCT_VIEW;
        }
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
                viewHolder =  new ProductViewHolder(inflater.inflate(R.layout.layout_product_grid, parent, false));
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
        holder.mProductPrice.setText(String.format(mContext.getString(R.string.price_per_pcs_format), product.getPrice().toString()));
        Glide.with(mContext)
                .load(product.getImageUrl(Constants.SMALL_IMAGE, "1"))
                .asBitmap()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        holder.mProductImage.setImageBitmap(resource);
                        holder.mProgressBar.setVisibility(View.GONE);
                    }
                });
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
                if (view.getId() == R.id.share_image_view) {
                    mListener.itemClicked(mProductImage, position);
                } else {
                    mListener.itemClicked(view, position);
                }
            }
        }
    }

    private static class ProgressViewHolder extends RecyclerView.ViewHolder {
        ProgressViewHolder(View itemView) {
            super(itemView);
        }
    }
}
