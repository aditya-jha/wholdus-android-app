package com.wholdus.www.wholdusbuyerapp.activities;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.adapters.IntroViewPagerAdapter;

public class IntroActivity extends FragmentActivity {

    private LinearLayout mIntroPagerDots;
    private ViewPager mViewPager;
    private PagerAdapter mPagerAdapter;
    private final int TOTAL_DOTS = 3;
    private ImageView[] mDots;
    private int mLastIntroPagerPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        initIntroPagerDots();
        initViewPagerSettings();
    }

    public void finishIntro() {
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

    private int getPixelFromDPValue(int dpValue) {
        int pixelValue = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dpValue,
                getResources().getDisplayMetrics()
        );
        return pixelValue;
    }

    private void initIntroPagerDots() {
        mIntroPagerDots = (LinearLayout) findViewById(R.id.introPagerDots);
        mDots = new ImageView[TOTAL_DOTS];

        for(int i=0; i<TOTAL_DOTS; i++) {
            mDots[i] = new ImageView(this);
            mDots[i].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.nonselected_dot));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(getPixelFromDPValue(10), 0, getPixelFromDPValue(10), 0);

            mIntroPagerDots.addView(mDots[i], params);
        }
        mLastIntroPagerPosition = 0;
        mDots[mLastIntroPagerPosition].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.selected_dot));
    }

    private void setIntroPagerDots(int position) {
        mDots[mLastIntroPagerPosition].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.nonselected_dot));
        mDots[position].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.selected_dot));
        mLastIntroPagerPosition = position;
    }
}
