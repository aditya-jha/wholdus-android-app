package com.wholdus.www.wholdusbuyerapp.helperClasses;

import com.wholdus.www.wholdusbuyerapp.activities.HomeActivity;
import com.wholdus.www.wholdusbuyerapp.fragments.HomeFragment;

/**
 * Created by kaustubh on 19/1/17.
 */

public class NavDrawerHelper {

    private static NavDrawerHelper mInstance = null;

    private String mOpenActivity;
    private String mOpenFragment;
    private int mType;

    private NavDrawerHelper(){
        mOpenActivity = HomeActivity.class.getSimpleName();
        mOpenFragment = HomeFragment.class.getSimpleName();
        mType = Constants.ALL_PRODUCTS;
    }

    public static NavDrawerHelper getInstance(){
        if(mInstance == null)
        {
            mInstance = new NavDrawerHelper();
        }
        return mInstance;
    }

    public String getOpenActivity(){return mOpenActivity;}

    public void setOpenActivity(String openActivity){mOpenActivity = openActivity;}

    public String getOpenFragment(){return mOpenFragment;}

    public void setOpenFragment(String openFragment){mOpenFragment = openFragment;}

    public int getType(){return mType;}

    public void setType(int type){mType = type;}

}
