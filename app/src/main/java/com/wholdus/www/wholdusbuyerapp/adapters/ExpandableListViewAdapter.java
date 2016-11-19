package com.wholdus.www.wholdusbuyerapp.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wholdus.www.wholdusbuyerapp.R;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static com.wholdus.www.wholdusbuyerapp.R.drawable.ic_expand_less_black_24dp;

/**
 * Created by aditya on 16/11/16.
 */

public class ExpandableListViewAdapter extends BaseExpandableListAdapter {

    private int mGroupView;
    private int mListView;
    private ArrayList<String> mListTitles;
    private LinkedHashMap<String, List<String>> mListData;
    private Context mContext;

    public ExpandableListViewAdapter(Context context, ArrayList<String> titles,
                                     LinkedHashMap<String, List<String>> data, int groupView, int listView) {
        mContext = context;
        mListView = listView;
        mGroupView = groupView;
        mListTitles = titles;
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
            convertView = inflater.inflate(mListView, null);
        }

        TextView textView = (TextView) convertView.findViewById(R.id.textView);
        textView.setText((String) getChild(groupPosition, childPosition));

        return convertView;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        final String listTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(mGroupView, null);
        }

        TextView textView = (TextView) convertView.findViewById(R.id.textView);
        textView.setTypeface(null, Typeface.BOLD);
        textView.setText(listTitle);

        ImageView imageView = (ImageView) convertView.findViewById(R.id.imageView);
        if (getChildrenCount(groupPosition) > 0) {
            if (isExpanded) {
                imageView.setImageResource(R.drawable.ic_expand_less_black_24dp);
            } else {
                imageView.setImageResource(R.drawable.ic_expand_more_black_24dp);
            }
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
        return mListData.get(mListTitles.get(groupPosition)).get(childPosition);
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mListTitles.get(groupPosition);
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mListData.get(mListTitles.get(groupPosition)).size();
    }

    @Override
    public int getGroupCount() {
        return mListData.size();
    }
}
