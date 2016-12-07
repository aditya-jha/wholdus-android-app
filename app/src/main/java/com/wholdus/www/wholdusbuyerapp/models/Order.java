package com.wholdus.www.wholdusbuyerapp.models;

import java.util.ArrayList;

/**
 * Created by kaustubh on 7/12/16.
 */

public class Order {
    private int ID;
    private String orderID;
    private String displayNumber;
    private String buyerAddressID;
    private BuyerAddress buyerAddress;
    private int productCount;
    private int pieces;
    private float retailPrice;
    private float calculatedPrice;
    private float editedPrice;
    private float shippingCharge;
    private float CODCharge;
    private float finalPrice;
    private int orderStatusValue;
    private String orderStatusDisplay;
    private int paymentStatusValue;
    private String paymentStatusDisplay;
    private String createdAt;
    private String remarks;

    private ArrayList<Suborder> suborders;

    public Order(){}

    public int getID(){return ID;}

    public String getOrderID(){return orderID;}

    public String getDisplayNumber(){return displayNumber;}

    public String getBuyerAddressID(){return buyerAddressID;}

    public BuyerAddress getBuyerAddress(){return buyerAddress;}

    public int getProductCount(){return productCount;}

    public int getPieces(){return pieces;}

    public float getRetailPrice(){return retailPrice;}

    public float getCalculatedPrice(){return calculatedPrice;}

    public float getEditedPrice(){return editedPrice;}

    public float getShippingCharge(){return shippingCharge;}

    public float getCODCharge(){return CODCharge;}

    public int getOrderStatusValue(){return orderStatusValue;}

    public String getOrderStatusDisplay(){return orderStatusDisplay;}

    public int getPaymentStatusValue(){return paymentStatusValue;}

    public String getPaymentStatusDisplay(){return paymentStatusDisplay;}

    public String getCreatedAt(){return createdAt;}

    public String getRemarks(){return remarks;}

    public ArrayList<Suborder> getSuborders(){return suborders;}
}
