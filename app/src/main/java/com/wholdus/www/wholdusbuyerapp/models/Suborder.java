package com.wholdus.www.wholdusbuyerapp.models;

import java.util.ArrayList;

/**
 * Created by kaustubh on 8/12/16.
 */

public class Suborder {

    private int ID;
    private String orderID;
    private String suborderID;
    private String displayNumber;
    private String sellerID;
    private Seller seller;
    private String sellerAddressID;
    private SellerAddress sellerAddress;
    private int productCount;
    private int pieces;
    private float retailPrice;
    private float calculatedPrice;
    private float editedPrice;
    private float shippingCharge;
    private float CODCharge;
    private float finalPrice;
    private int suborderStatusValue;
    private String suborderStatusDisplay;
    private int paymentStatusValue;
    private String paymentStatusDisplay;
    private String createdAt;

    private ArrayList<OrderItem> orderItems;

    public Suborder(){}

    public int getID(){return ID;}

    public String getOrderID(){return orderID;}

    public String getSuborderID(){return suborderID;}

    public String getDisplayNumber(){return displayNumber;}

    public String getSellerID(){return sellerID;}

    public Seller getSeller(){return seller;}

    public String getSellerAddressID(){return sellerAddressID;}

    public SellerAddress getSellerAddress(){return sellerAddress;}

    public int getProductCount(){return productCount;}

    public int getPieces(){return pieces;}

    public float getRetailPrice(){return retailPrice;}

    public float getCalculatedPrice(){return calculatedPrice;}

    public float getEditedPrice(){return editedPrice;}

    public float getShippingCharge(){return shippingCharge;}

    public float getCODCharge(){return CODCharge;}

    public int getSuborderStatusValue(){return suborderStatusValue;}

    public String getSuborderStatusDisplay(){return suborderStatusDisplay;}

    public int getPaymentStatusValue(){return paymentStatusValue;}

    public String getPaymentStatusDisplay(){return paymentStatusDisplay;}

    public String getCreatedAt(){return createdAt;}

    public ArrayList<OrderItem> getOrderItems(){return orderItems;}

}
