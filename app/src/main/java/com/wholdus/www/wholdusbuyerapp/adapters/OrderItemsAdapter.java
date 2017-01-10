package com.wholdus.www.wholdusbuyerapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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

public class OrderItemsAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<OrderItem> mData;

    public OrderItemsAdapter(Context context, ArrayList<OrderItem> data) {
        mContext = context;
        mData = data;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public Object getItem(int i) {
        return mData.get(i);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        final ViewHolder holder;

        //TODO Listview set height problems - Look into it :IMP

        if(convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_layout_order_items, viewGroup, false);
            holder = new ViewHolder();
            holder.productName = (TextView) convertView.findViewById(R.id.order_item_product_name_text_view);
            holder.pricePerPiece = (TextView) convertView.findViewById(R.id.order_item_product_price_per_piece_text_view);
            holder.total = (TextView) convertView.findViewById(R.id.order_item_final_price_text_view);
            holder.pieces = (TextView) convertView.findViewById(R.id.order_item_pieces_text_view);
            holder.status = (TextView) convertView.findViewById(R.id.order_item_status_text_view);
            holder.progressBar = (ProgressBar) convertView.findViewById(R.id.loading_indicator);
            holder.productImage = (ImageView) convertView.findViewById(R.id.product_image);
            holder.tracking = (Button) convertView.findViewById(R.id.order_item_track_button);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final OrderItem orderItem = mData.get(position);
        final Product product = orderItem.getProduct();

        holder.productName.setText(product.getName());
        holder.pieces.setText(String.valueOf(orderItem.getPieces()));
        holder.pricePerPiece.setText("Rs. " + String.format("%.0f", product.getMinPricePerUnit()));
        holder.total.setText("Rs. " + String.format("%.0f", orderItem.getFinalPrice()));
        holder.status.setText(orderItem.getOrderItemStatusDisplay());

        Glide.with(mContext)
                .load(product.getImageUrl(Constants.SMALL_IMAGE, "1"))
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .into(holder.productImage);

        holder.productImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, ProductDetailActivity.class);
                intent.putExtra(CatalogContract.ProductsTable.TABLE_NAME, product.getProductID());
                mContext.startActivity(intent);
            }
        });

        if (orderItem.getTrackingUrl() != null && URLUtil.isValidUrl(orderItem.getTrackingUrl())){
            //holder.tracking.setVisibility(View.VISIBLE);
            holder.tracking.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(orderItem.getTrackingUrl()));
                        mContext.startActivity(intent);
                    } catch (Exception e){

                    }
                }
            });
        } else {
            holder.tracking.setVisibility(View.GONE);
        }

        return convertView;
    }


    class ViewHolder{
        int id;
        TextView productName;
        TextView pricePerPiece;
        TextView pieces;
        TextView total;
        TextView status;
        ImageView productImage;
        ProgressBar progressBar;
        Button tracking;
    }
}
