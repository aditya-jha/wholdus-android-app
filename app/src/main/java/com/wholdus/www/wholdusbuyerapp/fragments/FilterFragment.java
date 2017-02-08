package com.wholdus.www.wholdusbuyerapp.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
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

import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarChangeListener;
import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarFinalValueListener;
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar;
import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.adapters.FilterBrandValuesDisplayAdapter;
import com.wholdus.www.wholdusbuyerapp.adapters.FilterCategoryValuesDisplayAdapter;
import com.wholdus.www.wholdusbuyerapp.adapters.FilterValuesDisplayAdapter;
import com.wholdus.www.wholdusbuyerapp.dataSource.FiltersData;
import com.wholdus.www.wholdusbuyerapp.helperClasses.FilterClass;
import com.wholdus.www.wholdusbuyerapp.interfaces.CategoryProductListenerInterface;
import com.wholdus.www.wholdusbuyerapp.loaders.CategoriesGridLoader;
import com.wholdus.www.wholdusbuyerapp.loaders.CategorySellerLoader;
import com.wholdus.www.wholdusbuyerapp.models.Category;
import com.wholdus.www.wholdusbuyerapp.models.CategorySeller;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Created by aditya on 12/12/16.
 * TODO: try to use same adapter for all filters, inside functionality is same except the data source
 **/

public class FilterFragment extends Fragment implements View.OnClickListener,
        AdapterView.OnItemClickListener, LoaderManager.LoaderCallbacks<ArrayList<CategorySeller>> {

    private ListView mFilterValues, mFilterKeys;
    private TextView mMinPriceValue, mMaxPriceValue;
    private LinkedHashMap<String, ArrayList<String>> mFilterData;
    private ArrayAdapter mFilterKeysAdapter;
    private FilterValuesDisplayAdapter mFilterValuesAdapter;
    private FilterBrandValuesDisplayAdapter mBrandFilterValuesAdapter;
    private FilterCategoryValuesDisplayAdapter mCategoryFilterValuesAdapter;
    private String mSelectedFilter;
    private CategoryProductListenerInterface mListener;
    private boolean mCategoryDisplayed = false;

    private static final int CATEGORY_SELLERS_DB_LOADER =2001;
    private static final int CATEGORY_DB_LOADER =2002;

    public FilterFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mListener = (CategoryProductListenerInterface) context;
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
        Bundle args = getArguments();
        try {
            mCategoryDisplayed = args.getBoolean("CategoryDisplayed");
        } catch (Exception e) {

        }
        initReferences(rootView);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mListener.filterFragmentActive(true);
        // set default selected item in filter keys listview
        mFilterKeys.requestFocusFromTouch();
        mFilterKeys.setSelection(0);
        mFilterKeys.performItemClick(mFilterKeysAdapter.getView(0, null, mFilterKeys), 0, 0);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
                if (mCategoryDisplayed) {
                    FilterClass.resetCategoryFilter();
                }
                populateValuesListView(mSelectedFilter);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<ArrayList<CategorySeller>> onCreateLoader(int id, Bundle args) {
        return new CategorySellerLoader(getContext());
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<CategorySeller>> loader, ArrayList<CategorySeller> data) {
        if (data != null) {
            mBrandFilterValuesAdapter.resetData(data);
            mBrandFilterValuesAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<CategorySeller>> loader) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.filter_button:
                mListener.applyFilter();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        switch (adapterView.getId()) {
            case R.id.filter_keys_list_view:
                String filterKeySelected = (String) mFilterKeysAdapter.getItem(position);

                /* TODO: handle state of list view when click and presses */
                int childCount = adapterView.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    if (i == position) {
                        adapterView.getChildAt(i).setBackgroundColor(Color.LTGRAY);
                    } else {
                        adapterView.getChildAt(i).setBackgroundColor(Color.WHITE);
                    }
                }
                populateValuesListView(filterKeySelected);
                break;
            case R.id.filter_values_list_view:
                if (mSelectedFilter.equals(FilterClass.FILTER_BRAND_KEY)) {
                    mBrandFilterValuesAdapter.itemClicked(view, position);
                } else if (mSelectedFilter.equals(FilterClass.FILTER_CATEGORY_KEY)) {
                    mCategoryFilterValuesAdapter.itemClicked(view, position);
                } else {
                    mFilterValuesAdapter.itemClicked(view, position);
                }
                break;
        }
    }

    public void categoryIDChanged(int oldCategoryID) {
        if (FilterClass.getCategoryID() != oldCategoryID) {
            FilterClass.resetFilter(FilterClass.FILTER_BRAND_KEY);
            getActivity().getSupportLoaderManager().restartLoader(CATEGORY_SELLERS_DB_LOADER, null, this);
        }
    }

    private void initReferences(ViewGroup rootView) {
        Button filterButton = (Button) rootView.findViewById(R.id.filter_button);
        filterButton.setOnClickListener(this);

        mFilterValues = (ListView) rootView.findViewById(R.id.filter_values_list_view);
        mFilterKeys = (ListView) rootView.findViewById(R.id.filter_keys_list_view);

        mMinPriceValue = (TextView) rootView.findViewById(R.id.min_price_value);
        mMaxPriceValue = (TextView) rootView.findViewById(R.id.max_price_value);

        CrystalRangeSeekbar priceRangeSeekBar = (CrystalRangeSeekbar) rootView.findViewById(R.id.price_range);
        priceRangeSeekBar.setMinValue(FilterClass.MIN_PRICE_DEFAULT);
        priceRangeSeekBar.setMaxValue(FilterClass.MAX_PRICE_DEFAULT);
        priceRangeSeekBar.setMinStartValue(FilterClass.getMinPriceFilter()).setMaxStartValue(FilterClass.getMaxPriceFilter()).apply();

        priceRangeSeekBar.setOnRangeSeekbarChangeListener(new OnRangeSeekbarChangeListener() {
            @Override
            public void valueChanged(Number minValue, Number maxValue) {
                mMaxPriceValue.setText(maxValue.toString());
                mMinPriceValue.setText(minValue.toString());
            }
        });
        priceRangeSeekBar.setOnRangeSeekbarFinalValueListener(new OnRangeSeekbarFinalValueListener() {
            @Override
            public void finalValue(Number minValue, Number maxValue) {
                FilterClass.setPriceFilter(minValue.intValue(), maxValue.intValue());
            }
        });

        mFilterData = FiltersData.getData(mCategoryDisplayed);

        /* TODO: better to create separate class */
        mFilterKeysAdapter = new ArrayAdapter<String>(getContext(), R.layout.list_item_filter_key, new ArrayList<>(mFilterData.keySet())) {
            @NonNull
            @Override
            public View getView(int position, View convertView, @Nullable ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_filter_key, parent, false);
                }
                TextView textView = (TextView) convertView.findViewById(R.id.textView);
                textView.setText(getItem(position));

                return convertView;
            }
        };
        mFilterKeys.setAdapter(mFilterKeysAdapter);
        mFilterKeys.setOnItemClickListener(this);

        mFilterValuesAdapter = new FilterValuesDisplayAdapter(getContext(), new ArrayList<String>(), "");
        mBrandFilterValuesAdapter = new FilterBrandValuesDisplayAdapter(getContext(), new ArrayList<CategorySeller>());
        if (mCategoryDisplayed) {
            mCategoryFilterValuesAdapter = new FilterCategoryValuesDisplayAdapter(getContext(), new ArrayList<Category>());
        }
        mFilterValues.setOnItemClickListener(this);
    }

    private void populateValuesListView(String filterKey) {
        mSelectedFilter = filterKey;
        switch (filterKey) {
            case FilterClass.FILTER_BRAND_KEY:
                mFilterValues.setAdapter(mBrandFilterValuesAdapter);
                getActivity().getSupportLoaderManager().restartLoader(CATEGORY_SELLERS_DB_LOADER, null, this);
                break;
            case FilterClass.FILTER_CATEGORY_KEY:
                mFilterValues.setAdapter(mCategoryFilterValuesAdapter);
                getActivity().getSupportLoaderManager().restartLoader(CATEGORY_DB_LOADER, null, new CategoryLoaderManager());
                break;
            default:
                mFilterValues.setAdapter(mFilterValuesAdapter);
                mFilterValuesAdapter.resetData(mFilterData.get(mSelectedFilter), mSelectedFilter);
                mFilterValuesAdapter.notifyDataSetChanged();
        }
    }

    private class CategoryLoaderManager implements LoaderManager.LoaderCallbacks<ArrayList<Category>> {

        @Override
        public void onLoaderReset(Loader<ArrayList<Category>> loader) {
        }

        @Override
        public void onLoadFinished(Loader<ArrayList<Category>> loader, ArrayList<Category> data) {
            if (data != null && mListener != null) {
                mCategoryFilterValuesAdapter.resetData(data);
                mCategoryFilterValuesAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public Loader<ArrayList<Category>> onCreateLoader(final int id, Bundle args) {
            return new CategoriesGridLoader(getContext(), false, null);
        }
    }
}
