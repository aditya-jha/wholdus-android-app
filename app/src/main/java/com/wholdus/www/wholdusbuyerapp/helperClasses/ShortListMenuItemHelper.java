package com.wholdus.www.wholdusbuyerapp.helperClasses;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.activities.CategoryProductActivity;

import org.json.JSONObject;

import java.util.HashMap;

import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by kaustubh on 8/2/17.
 */

public class ShortListMenuItemHelper {
    private MenuItem mMenuItem;
    private Context mContext;

    private RelativeLayout mShortlistItemCountLayout;
    private TextView mShortlistItemCountTextView;

    private int mShortListCount;
    public static final String
            SHORTLIST_SHARED_PREFERENCES = "ShortlistSharedPreference",
            SHORTLIST_COUNT_KEY = "ShortlistCountKey";

    public ShortListMenuItemHelper(Context context, MenuItem menuItem){
        mContext = context;
        mMenuItem = menuItem;
        MenuItemCompat.setActionView(mMenuItem, R.layout.shortlist_icon_item_count);
        mShortlistItemCountLayout = (RelativeLayout) MenuItemCompat.getActionView(mMenuItem);
        mShortlistItemCountLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startShortListActivity();
            }
        });
        mShortlistItemCountTextView = (TextView) mShortlistItemCountLayout.findViewById(R.id.actionbar_shortlist_item_count_text_view);
        new ShortlistCountRequest().execute();
        refreshShortListCount();
    }

    private void startShortListActivity(){
        Intent shortlistIntent = new Intent(mContext, CategoryProductActivity.class);
        shortlistIntent.putExtra(Constants.TYPE, Constants.FAV_PRODUCTS);
        mContext.startActivity(shortlistIntent);
    }

    public void refreshShortListCount(){
        SharedPreferences shortlistPreferences = mContext.getSharedPreferences(SHORTLIST_SHARED_PREFERENCES, MODE_PRIVATE);
        mShortListCount = shortlistPreferences.getInt(SHORTLIST_COUNT_KEY, 0);
        setShortListItemCount();
    }

    private void writeShortListCount(){
        SharedPreferences shortlistPreferences = mContext.getSharedPreferences(SHORTLIST_SHARED_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = shortlistPreferences.edit();
        editor.putInt(SHORTLIST_COUNT_KEY, mShortListCount);
        editor.apply();
        setShortListItemCount();
    }

    private void updateShortlistCount(int shortlistCount){
        mShortListCount = shortlistCount;
        writeShortListCount();
    }

    public void incrementShortListCount(){
        refreshShortListCount();
        mShortListCount += 1;
        writeShortListCount();
    }

    public void decrementShortListCount(){
        refreshShortListCount();
        mShortListCount -= 1;
        writeShortListCount();
    }

    private void setShortListItemCount(){
        if (mShortlistItemCountTextView != null && mContext != null) {
            if (mShortListCount > 0) {
                mShortlistItemCountTextView.setVisibility(View.VISIBLE);
                if (mShortListCount < 100) {
                    mShortlistItemCountTextView.setText(String.valueOf(mShortListCount));
                } else {
                    mShortlistItemCountTextView.setText(String.valueOf("99+"));
                }
            } else {
                mShortlistItemCountTextView.setVisibility(View.INVISIBLE);
            }
        }
    }

    private class ShortlistCountRequest extends AsyncTask<Void, Void, Integer> {

        protected Integer doInBackground(Void... par) {
            HashMap<String, String> params = new HashMap<>();
            String url = GlobalAccessHelper.generateUrl(APIConstants.BUYER_PRODUCT_RESPONSE_COUNT_URL, params);
            try {
                Response response = OkHttpHelper.makeGetRequest(mContext.getApplicationContext(), url);
                if (response.isSuccessful()) {
                    JSONObject responseJSON = new JSONObject(response.body().string());
                    Integer result = responseJSON.getInt("like_count");
                    response.body().close();
                    return result;
                } else {
                    return -1;
                }
            }catch (Exception e) {
                return -1;
            }
        }

        protected void onPostExecute(Integer result) {
            if (result != -1){
                updateShortlistCount(result);
            }
        }


    }

    @Nullable
    public Rect getBounds(){
        if (mShortlistItemCountLayout == null){
            return null;
        }
        Rect rect = new Rect();

        if (mShortlistItemCountLayout.getGlobalVisibleRect(rect)){
            return rect;
        } else {
            return null;
        }

    }
}
