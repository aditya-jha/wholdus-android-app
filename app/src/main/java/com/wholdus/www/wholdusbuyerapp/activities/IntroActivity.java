package com.wholdus.www.wholdusbuyerapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.adapters.IntroViewPagerAdapter;
import com.wholdus.www.wholdusbuyerapp.fragments.IntroSlideFragment;
import com.wholdus.www.wholdusbuyerapp.helperClasses.HelperFunctions;

public class IntroActivity extends FragmentActivity implements IntroSlideFragment.OnIntroSlideListener {

    private LinearLayout mIntroPagerDots;
    private ViewPager mViewPager;
    private PagerAdapter mPagerAdapter;
    private static final int TOTAL_DOTS = 3;
    private ImageView[] mDots;
    private int mLastIntroPagerPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        if(savedInstanceState != null) {
            Log.v("bundle ", savedInstanceState.toString());
            return;
        }
        initIntroPagerDots();
        initViewPagerSettings();
    }

    @Override
    public void onStartButtonClicked() {
        Intent intent = new Intent(this, LoginSignupActivity.class);
        startActivity(intent);
        finish();
    }

    private void initViewPagerSettings() {
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mPagerAdapter = new IntroViewPagerAdapter(getSupportFragmentManager(), TOTAL_DOTS, this);
        mViewPager.setAdapter(mPagerAdapter);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setIntroPagerDots(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initIntroPagerDots() {
        mIntroPagerDots = (LinearLayout) findViewById(R.id.introPagerDots);
        mDots = new ImageView[TOTAL_DOTS];

        for(int i=0; i<TOTAL_DOTS; i++) {
            mDots[i] = new ImageView(this);
            mDots[i].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.nonselected_dot_6dp));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(
                    (int) HelperFunctions.convertPixelsToDp(10, this),
                    0,
                    (int) HelperFunctions.convertDpToPixel(10, this),
                    0);

            mIntroPagerDots.addView(mDots[i], params);
        }
        mLastIntroPagerPosition = 0;
        mDots[mLastIntroPagerPosition].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.selected_dot));
    }

    private void setIntroPagerDots(int position) {
        mDots[mLastIntroPagerPosition].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.nonselected_dot_6dp));
        mDots[position].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.selected_dot));
        mLastIntroPagerPosition = position;
    }
}
