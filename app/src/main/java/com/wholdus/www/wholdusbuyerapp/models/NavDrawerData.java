package com.wholdus.www.wholdusbuyerapp.models;

import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aditya on 15/1/17.
 */

public class NavDrawerData {

    private String mName;
    private Integer mIcon;
    private List<NavDrawerData> mChilds;

    public NavDrawerData(String name, Integer icon, @Nullable List<NavDrawerData> child) {
        mName = name;
        mIcon = icon;
        mChilds = child;
    }

    public String getName() {
        return mName;
    }

    public Integer getIcon() {
        return mIcon;
    }

    public List<NavDrawerData> getChilds() {
        return mChilds;
    }
}
