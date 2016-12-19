package com.wholdus.www.wholdusbuyerapp.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.helperClasses.Constants;
import com.wholdus.www.wholdusbuyerapp.interfaces.ItemClickListener;
import com.wholdus.www.wholdusbuyerapp.interfaces.ProductCardListenerInterface;
import com.wholdus.www.wholdusbuyerapp.models.Product;
import com.wholdus.www.wholdusbuyerapp.singletons.VolleySingleton;

import java.util.ArrayList;

/**
 * Created by kaustubh on 18/12/16.
 */

public class ProductSwipeDeckAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<Product> mProductArrayList;
    private ImageLoader mImageLoader;
    private ProductCardListenerInterface mListener;
    private ItemClickListener mItemClickListener;

    public ProductSwipeDeckAdapter(Context context, ArrayList<Product> productArrayList
            , ProductCardListenerInterface listenerInterface, ItemClickListener itemClickListener){
        mContext = context;
        mProductArrayList = productArrayList;
        mImageLoader = VolleySingleton.getInstance(context).getImageLoader();
        mListener = listenerInterface;
        mItemClickListener = itemClickListener;
    }

    @Override
    public int getCount() {
        return mProductArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return mProductArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.product_card_layout, parent, false);
            holder = new ViewHolder();
            holder.productImageView = (NetworkImageView) convertView.findViewById(R.id.product_card_image_view);
            holder.productFabric = (TextView) convertView.findViewById(R.id.product_card_fabric_text_view);
            holder.productPrice = (TextView) convertView.findViewById(R.id.product_card_price_text_view);
            holder.progressBar = (ProgressBar) convertView.findViewById(R.id.loading_indicator);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        Product product = mProductArrayList.get(position);
        holder.productFabric.setText(product.getFabricGSM());
        holder.productPrice.setText("Rs." + String.format("%.0f",product.getMinPricePerUnit()) + "/pc");
        holder.productImageView.setImageUrl(product.getImageUrl(Constants.LARGE_IMAGE, "1"), mImageLoader);
        holder.productImageView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
                if(holder.productImageView.getDrawable() != null) {
                    holder.progressBar.setVisibility(View.GONE);
                }
            }
        });

        mListener.cardCreated(product.getName());
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mItemClickListener.itemClicked(position, 0);
            }
        });

        return convertView;
    }

    static class ViewHolder {
        NetworkImageView productImageView;
        TextView productFabric;
        TextView productPrice;
        ProgressBar progressBar;
    }
}
