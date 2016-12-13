package com.wholdus.www.wholdusbuyerapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarChangeListener;
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar;
import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.dataSource.FiltersData;
import com.wholdus.www.wholdusbuyerapp.helperClasses.FilterClass;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Created by aditya on 12/12/16.
 */

public class FilterFragment extends Fragment implements View.OnClickListener {

    private ListView mFilterValues, mFilterKeys;
    private CrystalRangeSeekbar mPriceRangeSeekBar;
    private TextView mMinPriceValue, mMaxPriceValue;
    private LinkedHashMap<String, ArrayList<String>> mFilterData;
    private ArrayAdapter mFilterKeysAdapter;

    public FilterFragment() {}

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_filter, container, false);
        setHasOptionsMenu(true);

        initReferences(rootView);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.filter_action_buttons, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_bar_clear:
                FilterClass.resetFilter();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.filter_button:
                /* TODO: what happens when filter button is clicked */
                break;
        }
    }

    private void initReferences(ViewGroup rootView) {
        Button filterButton = (Button) rootView.findViewById(R.id.filter_button);
        filterButton.setOnClickListener(this);

        mFilterValues = (ListView) rootView.findViewById(R.id.filter_values_list_view);
        mFilterKeys = (ListView) rootView.findViewById(R.id.filter_keys_list_view);

        mMinPriceValue = (TextView) rootView.findViewById(R.id.min_price_value);
        mMaxPriceValue = (TextView) rootView.findViewById(R.id.max_price_value);

        mPriceRangeSeekBar = (CrystalRangeSeekbar) rootView.findViewById(R.id.price_range);
        mPriceRangeSeekBar.setMinValue(getResources().getInteger(R.integer.price_filter_min));
        mPriceRangeSeekBar.setMaxValue(getResources().getInteger(R.integer.price_filter_max));

        mPriceRangeSeekBar.setOnRangeSeekbarChangeListener(new OnRangeSeekbarChangeListener() {
            @Override
            public void valueChanged(Number minValue, Number maxValue) {
                mMaxPriceValue.setText(maxValue.toString());
                mMinPriceValue.setText(minValue.toString());
            }
        });

        mFilterData = FiltersData.getData();
        mFilterKeysAdapter = new ArrayAdapter<String>(getContext(), R.layout.list_item_filter_key, new ArrayList<>(mFilterData.keySet())) {
            @NonNull
            @Override
            public View getView(int position, View convertView, @Nullable ViewGroup parent) {
                if(convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_filter_key, parent, false);
                }
                TextView textView = (TextView) convertView.findViewById(R.id.textView);
                textView.setText(getItem(position));

                return convertView;
            }
        };

        mFilterKeys.setAdapter(mFilterKeysAdapter);

        mFilterKeys.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String filterKeySelected = (String) mFilterKeysAdapter.getItem(i);
                Toast.makeText(getContext(), filterKeySelected, Toast.LENGTH_SHORT).show();
                view.setSelected(true);
            }
        });
    }
}
