package com.wholdus.www.wholdusbuyerapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.interfaces.ItemClickListener;
import com.wholdus.www.wholdusbuyerapp.models.GridProductModel;
import com.wholdus.www.wholdusbuyerapp.singletons.VolleySingleton;

import java.util.ArrayList;

/**
 * Created by aditya on 15/12/16.
 */

public class ProductsGridAdapter extends RecyclerView.Adapter<ProductsGridAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<GridProductModel> mProducts;
    private ImageLoader mImageLoader;
    private ItemClickListener mListener;

    public ProductsGridAdapter(Context context, ArrayList<GridProductModel> products, final ItemClickListener listener) {
        mContext = context;
        mProducts = products;
        mImageLoader = VolleySingleton.getInstance(context).getImageLoader();
        mListener = listener;
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_category_grid, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ProductsGridAdapter.ViewHolder holder, int position) {
        GridProductModel product = mProducts.get(position);

        holder.mProuctName.setText(product.getName());
        holder.mProductFabric.setText(product.getFabric());
        holder.mProductPrice.setText("Rs. " + product.getPrice().toString() + "/pcs");
        holder.mProductImage.setImageUrl(product.getImageUrl(), mImageLoader);
        holder.mProductImage.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
                if(holder.mProductImage.getDrawable() != null) {
                    holder.mProgressBar.setVisibility(View.GONE);
                }
            }
        });

        holder.mListener = mListener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private NetworkImageView mProductImage;
        private TextView mProuctName, mProductFabric, mProductPrice;
        private ImageButton mFabButton, mShareButton, mCartButton;
        private ItemClickListener mListener;
        private ProgressBar mProgressBar;

        ViewHolder(View itemView) {
            super(itemView);

            mProductImage = (NetworkImageView) itemView.findViewById(R.id.product_image);
            mProuctName = (TextView) itemView.findViewById(R.id.product_name);
            mProductFabric = (TextView) itemView.findViewById(R.id.product_fabric);
            mProductPrice = (TextView) itemView.findViewById(R.id.product_price);
            mFabButton = (ImageButton) itemView.findViewById(R.id.fav_icon_image_view);
            mShareButton = (ImageButton) itemView.findViewById(R.id.share_image_view);
            mCartButton = (ImageButton) itemView.findViewById(R.id.cart_image_view);
            mProgressBar = (ProgressBar) itemView.findViewById(R.id.loading_indicator);

            mCartButton.setOnClickListener(this);
            mFabButton.setOnClickListener(this);
            mShareButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (position == RecyclerView.NO_POSITION) return;

            if (mListener != null) {
                mListener.itemClicked(position, view.getId());
            }
        }
    }
}
