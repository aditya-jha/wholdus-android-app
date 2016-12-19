package com.wholdus.www.wholdusbuyerapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.interfaces.ItemClickListener;
import com.wholdus.www.wholdusbuyerapp.singletons.VolleySingleton;

import java.util.ArrayList;

/**
 * Created by aditya on 18/12/16.
 */

public class ThumbImageAdapter extends RecyclerView.Adapter<ThumbImageAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<String> mImages;
    private ImageLoader mImageLoader;
    private ItemClickListener mListener;

    public ThumbImageAdapter(Context context, ArrayList<String> images, final ItemClickListener listener) {
        mImages = images;
        mContext = context;
        mImageLoader = VolleySingleton.getInstance(context).getImageLoader();
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_thumb_image, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mNetworkImageView.setImageUrl(mImages.get(position), mImageLoader);
        holder.mListener = mListener;
    }

    @Override
    public int getItemCount() {
        return mImages.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private NetworkImageView mNetworkImageView;
        private ItemClickListener mListener;

        public ViewHolder(View itemView) {
            super(itemView);
            mNetworkImageView = (NetworkImageView) itemView.findViewById(R.id.thumb_image);
            mNetworkImageView.setOnClickListener(this);
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
