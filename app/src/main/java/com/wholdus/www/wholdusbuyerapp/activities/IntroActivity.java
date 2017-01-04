package com.wholdus.www.wholdusbuyerapp.activities;

import android.animation.ArgbEvaluator;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.adapters.IntroViewPagerAdapter;
import com.wholdus.www.wholdusbuyerapp.helperClasses.HelperFunctions;

import static com.wholdus.www.wholdusbuyerapp.R.color.slide_2_color;
import static com.wholdus.www.wholdusbuyerapp.R.color.slide_3_color;
import static com.wholdus.www.wholdusbuyerapp.R.id.colors;

public class IntroActivity extends FragmentActivity {

    private static final int TOTAL_DOTS = 3;
    private ImageView[] mDots;
    private int mLastIntroPagerPosition;
    private Button mNextButton;
    private ViewPager mViewPager;
    private Integer[] mColors;
    private ArgbEvaluator mArgbEvaluator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        mArgbEvaluator = new ArgbEvaluator();
        setColors();
        initIntroPagerDots();
        initViewPagerSettings();
        mNextButton = (Button) findViewById(R.id.start_button);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = mViewPager.getCurrentItem();
                if (position == TOTAL_DOTS - 1) {
                    Intent intent = new Intent(getApplicationContext(), LoginSignupActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    mViewPager.setCurrentItem(position + 1);
                }
            }
        });
    }

    private void initViewPagerSettings() {
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        PagerAdapter mPagerAdapter = new IntroViewPagerAdapter(getSupportFragmentManager(), TOTAL_DOTS, this);
        mViewPager.setAdapter(mPagerAdapter);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if(position < (TOTAL_DOTS -1) && position < (mColors.length - 1)) {
                    mViewPager.setBackgroundColor((Integer) mArgbEvaluator.evaluate(positionOffset, mColors[position], mColors[position + 1]));
                } else {
                    // the last page color
                    mViewPager.setBackgroundColor(mColors[mColors.length - 1]);
                }
            }

            @Override
            public void onPageSelected(int position) {
                setIntroPagerDots(position);
                if (position == TOTAL_DOTS - 1) {
                    mNextButton.setText(getApplicationContext().getString(R.string.start_button));
                } else {
                    mNextButton.setText(getApplicationContext().getString(R.string.next_button));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initIntroPagerDots() {
        LinearLayout mIntroPagerDots = (LinearLayout) findViewById(R.id.intro_pager_dots);
        mDots = new ImageView[TOTAL_DOTS];

        for (int i = 0; i < TOTAL_DOTS; i++) {
            mDots[i] = new ImageView(this);
            mDots[i].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.non_selected_dot_white));

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
        mDots[mLastIntroPagerPosition].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.selected_dot_white));
    }

    private void setIntroPagerDots(int position) {
        mDots[mLastIntroPagerPosition].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.non_selected_dot_white));
        mDots[position].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.selected_dot_white));
        mLastIntroPagerPosition = position;
    }

    private void setColors() {
        mColors = new Integer[TOTAL_DOTS];
        mColors[0] = getResources().getColor(R.color.slide_1_color);
        mColors[1] = getResources().getColor(R.color.slide_2_color);
        mColors[2] = getResources().getColor(R.color.slide_3_color);
    }
}
