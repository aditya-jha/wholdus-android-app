package com.wholdus.www.wholdusbuyerapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.activities.HandPickedActivity;
import com.wholdus.www.wholdusbuyerapp.databaseContracts.CatalogContract;
import com.wholdus.www.wholdusbuyerapp.decorators.RecyclerViewSpaceItemDecoration;
import com.wholdus.www.wholdusbuyerapp.interfaces.ItemClickListener;
import com.wholdus.www.wholdusbuyerapp.models.Category;
import com.wholdus.www.wholdusbuyerapp.models.Product;

import java.util.ArrayList;

/**
 * Created by kaustubh on 15/1/17.
 */

public class CategoryHomePageAdapter extends RecyclerView.Adapter<CategoryHomePageAdapter.MyViewHolder> {

    private ArrayList<Category> mListData;
    private Context mContext;
    private ItemClickListener mListener;

    public CategoryHomePageAdapter(Context context, ArrayList<Category> listData,final ItemClickListener listener){
        mContext = context;
        mListData = listData;
        mListener = listener;
    }

    @Override
    public int getItemCount() {
        return mListData.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_categories_home_page, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Category category= mListData.get(position);
        holder.mCategoryName.setText(category.getCategoryName());
        holder.mProducts.clear();
        holder.mProducts.addAll(category.getProducts());
        holder.mProductHomePageAdapter.notifyDataSetChanged();

        holder.mListener = mListener;
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, ItemClickListener{
        TextView mCategoryName;
        RecyclerView mProductsRecyclerView;
        private ProductHomePageAdapter mProductHomePageAdapter;
        private ArrayList<Product> mProducts;
        private ItemClickListener mListener;

        private MyViewHolder(final View itemView) {
            super(itemView);

            mCategoryName = (TextView) itemView.findViewById(R.id.category_name);
            mCategoryName.setOnClickListener(this);

            mProductsRecyclerView = (RecyclerView) itemView.findViewById(R.id.products_recycler_view);
            mProductsRecyclerView.setItemAnimator(new DefaultItemAnimator());
            mProducts = new ArrayList<>();
            mProductHomePageAdapter = new ProductHomePageAdapter(mContext, mProducts, this);
            mProductsRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
            mProductsRecyclerView.addItemDecoration(new RecyclerViewSpaceItemDecoration(0, mContext.getResources().getDimensionPixelSize(R.dimen.card_margin_horizontal)));
            mProductsRecyclerView.setAdapter(mProductHomePageAdapter);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            int position = getAdapterPosition();
            if (position == RecyclerView.NO_POSITION) return;

            if (mListener != null) {
                mListener.itemClicked(view, position, -1);
            }

        }

        @Override
        public void itemClicked(View view, int position, int id) {
            Intent intent = new Intent(mContext, HandPickedActivity.class);
            ArrayList<Integer> productIDs = new ArrayList<>();
            productIDs.add(mProducts.get(position).getProductID());
            intent.putExtra(CatalogContract.ProductsTable.TABLE_NAME, productIDs);
            mContext.startActivity(intent);
        }

    }
}
