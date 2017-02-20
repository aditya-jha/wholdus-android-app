package com.wholdus.www.wholdusbuyerapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.activities.CategoryProductActivity;
import com.wholdus.www.wholdusbuyerapp.databaseContracts.CatalogContract;
import com.wholdus.www.wholdusbuyerapp.decorators.RecyclerViewSpaceItemDecoration;
import com.wholdus.www.wholdusbuyerapp.helperClasses.Constants;
import com.wholdus.www.wholdusbuyerapp.interfaces.CartSummaryListenerInterface;
import com.wholdus.www.wholdusbuyerapp.models.SubCart;

import java.util.ArrayList;

/**
 * Created by kaustubh on 30/12/16.
 */

public class SubCartAdapter extends RecyclerView.Adapter<SubCartAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<SubCart> mData;
    private int mCartSellerMinPieces;
    private CartSummaryListenerInterface mListener;

    public SubCartAdapter(Context context, ArrayList<SubCart> data,int cartSellerMinPieces, CartSummaryListenerInterface listener) {
        mContext = context;
        mData = data;
        mCartSellerMinPieces = cartSellerMinPieces;
        mListener = listener;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.list_view_subcart, parent, false));
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final SubCart subCart = mData.get(position);
        if (subCart == null){
            return;
        }

        holder.sellerName.setText(subCart.getSeller().getCompanyName());
        holder.summary.setText(String.valueOf(subCart.getPieces()) + " pieces - Rs. " +
                String.format("%.0f", subCart.getFinalPrice() - subCart.getShippingCharge()));

        holder.sellerName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, CategoryProductActivity.class);
                intent.putExtra(CatalogContract.SellersTable.COLUMN_SELLER_ID, subCart.getSellerID());
                intent.putExtra(Constants.TYPE, Constants.MANUFACTURER_PRODUCTS);
                mContext.startActivity(intent);
            }
        });

        holder.moreProductsFromSellerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, CategoryProductActivity.class);
                intent.putExtra(CatalogContract.SellersTable.COLUMN_SELLER_ID, subCart.getSellerID());
                intent.putExtra(Constants.TYPE, Constants.MANUFACTURER_PRODUCTS);
                mContext.startActivity(intent);
            }
        });

        if (subCart.getPieces() < mCartSellerMinPieces){
            holder.cartSellerMinPiecesLayout.setVisibility(View.VISIBLE);
            holder.cartSellerMinPiecesTextView.setText(String.format(mContext.getString(R.string.cart_seller_min_pieces_message),
                    mCartSellerMinPieces- subCart.getPieces()));
        } else {
            holder.cartSellerMinPiecesLayout.setVisibility(View.GONE);
        }

        CartItemsAdapter cartItemsAdapter = new CartItemsAdapter(mContext, subCart.getCartItems(), mListener);
        holder.cartItems.setAdapter(cartItemsAdapter);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        int id;
        private TextView sellerName;
        private TextView summary;
        private RecyclerView cartItems;
        private LinearLayout cartSellerMinPiecesLayout;
        private TextView cartSellerMinPiecesTextView;
        private Button moreProductsFromSellerButton;

        private MyViewHolder(final View itemView) {
            super(itemView);
            sellerName = (TextView) itemView.findViewById(R.id.subcart_seller_name_text_view);
            summary = (TextView) itemView.findViewById(R.id.subcart_summary_text_view);
            cartItems = (RecyclerView) itemView.findViewById(R.id.sub_cart_items_list_view);
            cartItems.setItemAnimator(new DefaultItemAnimator());
            cartItems.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
            cartItems.addItemDecoration(new RecyclerViewSpaceItemDecoration(mContext.getResources().getDimensionPixelSize(R.dimen.text_divider_gap_small), 0));

            cartSellerMinPiecesLayout = (LinearLayout) itemView.findViewById(R.id.cart_seller_min_pieces_layout);
            cartSellerMinPiecesTextView = (TextView) itemView.findViewById(R.id.cart_seller_min_pieces_text_view);
            moreProductsFromSellerButton = (Button) itemView.findViewById(R.id.more_products_from_seller_button);
        }
    }
}
