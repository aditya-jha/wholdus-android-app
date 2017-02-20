package com.wholdus.www.wholdusbuyerapp.dataSource;

import android.support.annotation.Nullable;

import com.wholdus.www.wholdusbuyerapp.helperClasses.FilterClass;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.logging.Filter;

/**
 * Created by aditya on 13/12/16.
 */

public class FiltersData {
    public static LinkedHashMap<String, ArrayList<String>> getData(boolean categoryDisplayed, boolean brandDisplayed) {
        LinkedHashMap<String, ArrayList<String>> data = new LinkedHashMap<>();

        /* TODO: validate moving these values dynamic */
        String[] fabric = new String[] {
                "Cotton",
                "Silk",
                "Rayon",
                "Georgette",
                "Lycra",
                "Velvet",
                "Net",
                "Brasso",
                "Chiffon",
                "Crepe",
                "Chanderi",
                "Jacquard"
        };

        String[] colors = new String[] {
                "Red",
                "Blue",
                "Green",
                "Yellow",
                "Black",
                "White",
                "Pink",
                "Beige",
                "Purple",
                "Orange",
                "Multi"
        };

        String[] sizes = new String[] {
                "S",
                "M",
                "L",
                "XL",
                "XXL",
                "XXXL"
        };
        if (categoryDisplayed){
            data.put(FilterClass.FILTER_CATEGORY_KEY, new ArrayList<String>());
        }

        data.put(FilterClass.FILTER_FABRIC_KEY, new ArrayList<>(Arrays.asList(fabric)));
        if (brandDisplayed) {
            data.put(FilterClass.FILTER_BRAND_KEY, new ArrayList<String>());
        }
        data.put(FilterClass.FILTER_COLOUR_KEY, new ArrayList<>(Arrays.asList(colors)));
        data.put(FilterClass.FILTER_SIZE_KEY, new ArrayList<>(Arrays.asList(sizes)));



        return data;
    }
}
