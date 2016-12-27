package com.wholdus.www.wholdusbuyerapp.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.interfaces.ItemClickListener;
import com.wholdus.www.wholdusbuyerapp.models.Category;

import java.util.List;

/**
 * Created by aditya on 11/12/16.
 * RecyclerView Adapter to display products as grid
 */

public class CategoriesGridAdapter extends RecyclerView.Adapter<CategoriesGridAdapter.ViewHolder> {

    private List<Category> mCategories;
    private Context mContext;
    private ItemClickListener mListener;

    public CategoriesGridAdapter(Context context, List<Category> categories, final ItemClickListener listener) {
        mContext = context;
        mCategories = categories;
        mListener = listener;
    }

    @Override
    public int getItemCount() {
        return mCategories.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_category_grid, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CategoriesGridAdapter.ViewHolder holder, int position) {
        Category category = mCategories.get(position);

        holder.mNameTextView.setText(category.getCategoryName());

        if (category.getBuyerInterest() != null) {
            holder.mFavIconImageView.setImageResource(R.drawable.ic_favorite_red_24dp);
        } else {
            holder.mFavIconImageView.setImageResource(R.drawable.ic_favorite_border_black_24dp);
        }

        Glide.with(mContext)
                .load(category.getImageURL())
                .asBitmap()
                .into(new BitmapImageViewTarget(holder.mIconImageView) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        super.setResource(resource);
                        holder.mIconImageView.setImageBitmap(resource);
                        holder.mProgressBar.setVisibility(View.GONE);
                    }
                });
        holder.mListener = mListener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView mNameTextView;
        ImageView mIconImageView;
        ProgressBar mProgressBar;
        ImageView mFavIconImageView;

        private ItemClickListener mListener;

        ViewHolder(View itemView) {
            super(itemView);

            mNameTextView = (TextView) itemView.findViewById(R.id.name_textView);
            mIconImageView = (ImageView) itemView.findViewById(R.id.icon_imageView);
            mProgressBar = (ProgressBar) itemView.findViewById(R.id.loading_indicator);
            mFavIconImageView = (ImageView) itemView.findViewById(R.id.fav_icon_image_view);

            itemView.setOnClickListener(this);
            mFavIconImageView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (position == RecyclerView.NO_POSITION) return;

            if (mListener != null) {
                mListener.itemClicked(view, position, -1);
            }
        }
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        super.onViewRecycled(holder);
        Glide.clear(holder.mIconImageView);
    }
}
