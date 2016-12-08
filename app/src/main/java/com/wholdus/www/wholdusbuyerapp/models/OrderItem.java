package com.wholdus.www.wholdusbuyerapp.models;

/**
 * Created by kaustubh on 8/12/16.
 */

public class OrderItem {

    private int m_ID;
    private String mOrderItemID;
    private String mSuborderID;
    private String mProductID;
    private Product mProduct;
    private String mOrderShipmentID;
    private int mLots;
    private int mLotSize;
    private int mPieces;
    private float mRetailPricePerPiece;
    private float mCalculatedPricePerPiece;
    private float mEditedPricePerPiece;
    private float mFinalPrice;
    private int mOrderItemStatusValue;
    private String mOrderItemStatusDisplay;
    private String mTrackingUrl;
    private String mCreatedAt;
    private String mRemarks;

    public OrderItem() {
    }

    public int get_ID() {
        return m_ID;
    }

    public String getOrderItemID() {
        return mOrderItemID;
    }

    public String getSuborderID() {
        return mSuborderID;
    }

    public String getProductID() {
        return mProductID;
    }

    public Product getProduct() {
        return mProduct;
    }

    public String getOrderShipmentID() {
        return mOrderShipmentID;
    }

    public int getLots() {
        return mLots;
    }

    public int getLotSize() {
        return mLotSize;
    }

    public int getPieces() {
        return mPieces;
    }

    public float getRetailPricePerPiece() {
        return mRetailPricePerPiece;
    }

    public float getCalculatedPricePerPiece() {
        return mCalculatedPricePerPiece;
    }

    public float getEditedPricePerPiece() {
        return mEditedPricePerPiece;
    }

    public int getOrderItemStatusValue() {
        return mOrderItemStatusValue;
    }

    public String getOrderItemStatusDisplay() {
        return mOrderItemStatusDisplay;
    }

    public String getTrackingUrl() {
        return mTrackingUrl;
    }

    public String getCreatedAt() {
        return mCreatedAt;
    }

    public String getRemarks() {
        return mRemarks;
    }
}
