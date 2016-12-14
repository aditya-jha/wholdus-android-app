package com.wholdus.www.wholdusbuyerapp.dataSource;

import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;

/**
 * Created by aditya on 13/12/16.
 */

public class FiltersData {
    public static LinkedHashMap<String, ArrayList<String>> getData() {
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

        data.put("Fabric", new ArrayList<>(Arrays.asList(fabric)));
        data.put("Brand", new ArrayList<String>());
        data.put("Colors", new ArrayList<>(Arrays.asList(colors)));
        data.put("Sizes", new ArrayList<>(Arrays.asList(sizes)));

        return data;
    }
}
