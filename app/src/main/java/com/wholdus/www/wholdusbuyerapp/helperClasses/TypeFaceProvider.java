package com.wholdus.www.wholdusbuyerapp.helperClasses;

import android.content.Context;
import android.graphics.Typeface;

import java.util.Hashtable;

/**
 * Created by aditya on 15/1/17.
 */

public class TypeFaceProvider {

    public static final String FONT_THIN = "Raleway-Thin";
    public static final String FONT_LIGHT = "Raleway-Light";
    public static final String FONT_MEDIUM = "Raleway-Medium";
    public static final String FONT_REGULAR = "Raleway-Regular";
    public static final String FONT_BOLD = "Raleway-Bold";

    private static Hashtable<String, Typeface> sTypeFaces = new Hashtable<String, Typeface>(5);

    public static Typeface getTypeFace(Context context, String fileName) {
        Typeface tempTypeface = sTypeFaces.get(fileName);

        if (tempTypeface == null) {
            tempTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/" + fileName + ".ttf");
            sTypeFaces.put(fileName, tempTypeface);
        }

        return tempTypeface;
    }
}
