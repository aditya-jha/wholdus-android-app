package com.wholdus.www.wholdusbuyerapp.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.models.NavDrawerData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import static android.R.attr.data;
import static android.media.CamcorderProfile.get;
import static com.wholdus.www.wholdusbuyerapp.R.drawable.ic_expand_more_black_24dp;
import static com.wholdus.www.wholdusbuyerapp.R.id.imageView;
import static com.wholdus.www.wholdusbuyerapp.R.id.textView;

/**
 * Created by aditya on 16/11/16.
 */

public class NavigationDrawerAdapter extends BaseExpandableListAdapter {

    private List<NavDrawerData> mListData;
    private Context mContext;

    public NavigationDrawerAdapter(Context context, List<NavDrawerData> data) {
        mContext = context;
        mListData = data;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.navigation_drawer_list_item, parent, false);
        }

        NavDrawerData data = (NavDrawerData) getChild(groupPosition, childPosition);
        TextView textView = (TextView) convertView.findViewById(R.id.textView);
        textView.setText(data.getName());

        textView.setCompoundDrawablesWithIntrinsicBounds(data.getIcon(), 0, 0, 0);

        return convertView;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        NavDrawerData data = (NavDrawerData) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.navigation_drawer_list_group, parent, false);
        }

        TextView textView = (TextView) convertView.findViewById(R.id.textView);
        textView.setTypeface(null, Typeface.NORMAL);
        textView.setText(data.getName());

        if (getChildrenCount(groupPosition) > 0) {
            if (isExpanded) {
                textView.setCompoundDrawablesWithIntrinsicBounds(data.getIcon(), 0, R.drawable.ic_expand_less_black_24dp, 0);
            } else {
                textView.setCompoundDrawablesWithIntrinsicBounds(data.getIcon(), 0, ic_expand_more_black_24dp, 0);
            }
        } else {
            textView.setCompoundDrawablesWithIntrinsicBounds(data.getIcon(), 0, 0, 0);
        }

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mListData.get(groupPosition).getChilds().get(childPosition);
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mListData.get(groupPosition);
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mListData.get(groupPosition).getChilds().size();
    }

    @Override
    public int getGroupCount() {
        return mListData.size();
    }
}
