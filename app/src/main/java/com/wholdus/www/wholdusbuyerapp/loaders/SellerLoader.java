package com.wholdus.www.wholdusbuyerapp.loaders;

import android.content.Context;
import android.database.Cursor;

import com.wholdus.www.wholdusbuyerapp.databaseHelpers.CatalogDBHelper;
import com.wholdus.www.wholdusbuyerapp.models.Seller;

/**
 * Created by kaustubh on 18/2/17.
 */

public class SellerLoader extends AbstractLoader<Seller> {

    private int mSellerID;

    public SellerLoader(Context context,
                        int sellerID){
        super(context);
        mSellerID = sellerID;
    }

    @Override
    public Seller loadInBackground(){
        CatalogDBHelper catalogDBHelper = new CatalogDBHelper(getContext());
        Cursor cursor = catalogDBHelper.getSellerData(mSellerID, null);
        Seller seller = null;
        if (cursor.getCount() > 0){
            cursor.moveToNext();
            seller = new Seller(cursor);
        }
        return seller;
    }
}
