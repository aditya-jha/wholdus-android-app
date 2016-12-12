package com.wholdus.www.wholdusbuyerapp.models;

/**
 * Created by kaustubh on 8/12/16.
 */

public class Seller {

    private int m_ID;
    private int mSellerID;
    private String mCompanyName;
    private String mName;
    private String mCompanyProfile;
    private int mShowOnline;
    private String mCreatedAt;
    private String mUpdatedAt;
    private int mDeleteStatus;

    public Seller(){

    }

    public int getID(){return m_ID;}

    public int getSellerID(){return mSellerID;}

    public String getCompanyName(){return mCompanyName;}

    public String getName(){return mName;}

    public String getCompanyProfile(){return mCompanyProfile;}

    public int getShowOnline(){return mShowOnline;}

    public String getCreatedAt(){return mCreatedAt;}

    public String getUpdatedAt(){return mUpdatedAt;}

    public int getDeleteStatus(){return mDeleteStatus;}
}
