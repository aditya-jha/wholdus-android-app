package com.wholdus.www.wholdusbuyerapp.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.interfaces.ItemClickListener;

import java.util.ArrayList;

/**
 * Created by aditya on 18/12/16.
 */

public class ThumbImageAdapter extends RecyclerView.Adapter<ThumbImageAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<String> mImages;
    private ItemClickListener mListener;

    public ThumbImageAdapter(Context context, ArrayList<String> images, final ItemClickListener listener) {
        mImages = images;
        mContext = context;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_thumb_image, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Glide.with(mContext)
                .load(mImages.get(position))
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.mImageView);
        holder.mListener = mListener;
    }

    @Override
    public int getItemCount() {
        return mImages.size();
    }


    @Override
    public void onViewRecycled(ViewHolder holder) {
        super.onViewRecycled(holder);
        Glide.clear(holder.mImageView);
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView mImageView;
        private ItemClickListener mListener;

        public ViewHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.thumb_image);
            mImageView.setOnClickListener(this);
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
}
