package com.wholdus.www.wholdusbuyerapp.models;

import java.util.ArrayList;

/**
 * Created by kaustubh on 8/12/16.
 */

public class OrderItem {

    private int ID;
    private String orderItemID;
    private String suborderID;
    private String productID;
    private Product product;
    private String orderShipmentID;
    private int lots;
    private int lotSize;
    private int pieces;
    private float retailPricePerPiece;
    private float calculatedPricePerPiece;
    private float editedPricePerPiece;
    private float finalPrice;
    private int orderItemStatusValue;
    private String orderItemStatusDisplay;
    private String trackingUrl;
    private String createdAt;
    private String remarks;

    public OrderItem(){}

    public int getID(){return ID;}

    public String getOrderItemID(){return orderItemID;}

    public String getSuborderID(){return suborderID;}

    public String getProductID(){return productID;}

    public Product getProduct(){return product;}

    public String getOrderShipmentID(){return orderShipmentID;}

    public int getLots(){return lots;}

    public int getLotSize(){return lotSize;}

    public int getPieces(){return pieces;}

    public float getRetailPricePerPiece(){return retailPricePerPiece;}

    public float getCalculatedPricePerPiece(){return calculatedPricePerPiece;}

    public float getEditedPricePerPiece(){return editedPricePerPiece;}

    public int getOrderItemStatusValue(){return orderItemStatusValue;}

    public String getOrderItemStatusDisplay(){return orderItemStatusDisplay;}

    public String getTrackingUrl(){return trackingUrl;}

    public String getCreatedAt(){return createdAt;}

    public String getRemarks(){return remarks;}
}
