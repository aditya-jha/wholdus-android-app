package com.wholdus.www.wholdusbuyerapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.models.Category;
import com.wholdus.www.wholdusbuyerapp.singletons.VolleySingleton;

import java.util.List;

/**
 * Created by aditya on 11/12/16.
 */

public class CategoriesGridAdapter extends RecyclerView.Adapter<CategoriesGridAdapter.ViewHolder> {

    private List<Category> mCategories;
    private Context mContext;
    private ImageLoader mImageLoader;

    public CategoriesGridAdapter(Context context, List<Category> categories) {
        mContext = context;
        mCategories = categories;
        mImageLoader = VolleySingleton.getInstance(context).getImageLoader();
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
    public void onBindViewHolder(CategoriesGridAdapter.ViewHolder holder, int position) {
        Category category = mCategories.get(position);

        holder.mNameTextView.setText(category.getCategoryName());
        holder.mNameTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_favorite_border_black_24dp, 0);

        holder.mIconImageView.setImageUrl(category.getImageURL(), mImageLoader);
        holder.mProgressBar.setVisibility(View.GONE);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView mNameTextView;
        NetworkImageView mIconImageView;
        ProgressBar mProgressBar;

        ViewHolder(View itemView) {
            super(itemView);

            mNameTextView = (TextView) itemView.findViewById(R.id.name_textView);
            mIconImageView = (NetworkImageView) itemView.findViewById(R.id.icon_imageView);
            mProgressBar = (ProgressBar) itemView.findViewById(R.id.loading_indicator);
        }
    }
}
