package com.wholdus.www.wholdusbuyerapp.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.wholdus.www.wholdusbuyerapp.R;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.wholdus.www.wholdusbuyerapp.R.drawable.ic_expand_more_black_24dp;

/**
 * Created by aditya on 10/1/17.
 */

public class FAQAdapter extends BaseExpandableListAdapter {

    private Context mContext;
    private ArrayList<String> mListTitles;
    private LinkedHashMap<String, List<Map<String, String>>> mListData;

    public FAQAdapter(Context context, LinkedHashMap<String, List<Map<String, String>>> data) {
        mContext = context;
        mListData = data;
        mListTitles = new ArrayList<>(data.keySet());
    }

    @Override
    public int getGroupCount() {
        return mListData.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mListData.get(mListTitles.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mListTitles.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mListData.get(mListTitles.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        final String listTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.layout_faq_group, parent, false);
        }

        TextView textView = (TextView) convertView.findViewById(R.id.faq_title);
        textView.setTypeface(null, Typeface.BOLD);
        textView.setText(listTitle);

        if (getChildrenCount(groupPosition) > 0) {
            if (isExpanded) {
                textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_expand_less_black_24dp, 0);
            } else {
                textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, ic_expand_more_black_24dp, 0);
            }
        } else {
            textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.layout_faq_child, parent, false);
        }

        TextView question = (TextView) convertView.findViewById(R.id.question);
        TextView answer = (TextView) convertView.findViewById(R.id.answer);

        Map<String, String> faqEntry = mListData.get(mListTitles.get(groupPosition)).get(childPosition);
        question.setText(faqEntry.get("question"));
        answer.setText(faqEntry.get("answer"));

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}

