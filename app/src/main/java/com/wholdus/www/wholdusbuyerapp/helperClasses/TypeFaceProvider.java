package com.wholdus.www.wholdusbuyerapp.helperClasses;

import android.content.Context;
import android.graphics.Typeface;

import java.util.Hashtable;

/**
 * Created by aditya on 15/1/17.
 */

public class TypeFaceProvider {

    private static Hashtable<String, Typeface> sTypeFaces = new Hashtable<String, Typeface>(4);

    public static Typeface getTypeFace(Context context, String fileName) {
        Typeface tempTypeface = sTypeFaces.get(fileName);

        if (tempTypeface == null) {
            tempTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/" + fileName + ".ttf");
            sTypeFaces.put(fileName, tempTypeface);
        }

        return tempTypeface;
    }
}
