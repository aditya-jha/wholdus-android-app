package com.wholdus.www.wholdusbuyerapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.activities.ProductDetailActivity;
import com.wholdus.www.wholdusbuyerapp.databaseContracts.CatalogContract;
import com.wholdus.www.wholdusbuyerapp.helperClasses.Constants;
import com.wholdus.www.wholdusbuyerapp.models.OrderItem;
import com.wholdus.www.wholdusbuyerapp.models.Product;

import java.util.ArrayList;

/**
 * Created by kaustubh on 31/12/16.
 */

public class OrderItemsAdapter extends RecyclerView.Adapter<OrderItemsAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<OrderItem> mData;

    public OrderItemsAdapter(Context context, ArrayList<OrderItem> data) {
        mContext = context;
        mData = data;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.list_item_layout_order_items, parent, false));
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        OrderItem orderItem = mData.get(position);
        Product product = orderItem.getProduct();

        holder.productName.setText(product.getName());
        holder.pieces.setText(String.valueOf(orderItem.getPieces()));
        holder.pricePerPiece.setText(String.format(mContext.getString(R.string.price_format), String.valueOf((int) Math.ceil(product.getMinPricePerUnit()))));
        holder.total.setText(String.format(mContext.getString(R.string.price_format), String.valueOf((int) Math.ceil(orderItem.getFinalPrice()))));
        holder.status.setText(orderItem.getOrderItemStatusDisplay());

        Glide.with(mContext)
                .load(product.getImageUrl(Constants.SMALL_IMAGE, "1"))
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
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

        if (orderItem.getTrackingUrl() != null && URLUtil.isValidUrl(orderItem.getTrackingUrl())) {
            //holder.tracking.setVisibility(View.VISIBLE);
            holder.tracking.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openTrackingUrl(position);
                }
            });
        } else {
            holder.tracking.setVisibility(View.GONE);
        }
    }

    private void openProductDetails(int position){
        Product product = mData.get(position).getProduct();
        Intent intent = new Intent(mContext, ProductDetailActivity.class);
        intent.putExtra(CatalogContract.ProductsTable.TABLE_NAME, product.getProductID());
        mContext.startActivity(intent);
    }
    private void openTrackingUrl(int position){
        OrderItem orderItem = mData.get(position);
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(orderItem.getTrackingUrl()));
            mContext.startActivity(intent);
        } catch (Exception e) {

        }
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        int id;
        TextView productName;
        TextView pricePerPiece;
        TextView pieces;
        TextView total;
        TextView status;
        ImageView productImage;
        ProgressBar progressBar;
        Button tracking;

        private MyViewHolder(final View itemView) {
            super(itemView);
            productName = (TextView) itemView.findViewById(R.id.order_item_product_name_text_view);
            pricePerPiece = (TextView) itemView.findViewById(R.id.order_item_product_price_per_piece_text_view);
            total = (TextView) itemView.findViewById(R.id.order_item_final_price_text_view);
            pieces = (TextView) itemView.findViewById(R.id.order_item_pieces_text_view);
            status = (TextView) itemView.findViewById(R.id.order_item_status_text_view);
            progressBar = (ProgressBar) itemView.findViewById(R.id.loading_indicator);
            productImage = (ImageView) itemView.findViewById(R.id.product_image);
            tracking = (Button) itemView.findViewById(R.id.order_item_track_button);
        }
    }
}
