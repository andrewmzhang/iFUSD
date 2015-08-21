package com.fremontunified.ifusd.app;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class FremontHomePager extends FragmentActivity {

    private ViewPager mViewPager;
    private String[] mActivityArray = new String[]{
            "News",
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewPager = new ViewPager(this);
        mViewPager.setId(R.id.viewPager);
        setContentView(mViewPager);

        FragmentManager fm = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentPagerAdapter(fm) {
            @Override
            public Fragment getItem(int pos) {
                String decider = mActivityArray[pos];
                if (decider == "News")
                    return PageGenerator.generateNews();
                else
                    return null;
            }

            @Override
            public int getCount() {
                return mActivityArray.length;
            }
        });

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {

            }

            @Override
            public void onPageScrollStateChanged(int i) {
                String title = mActivityArray[i];
                if (title != null)
                    setTitle(title);
            }
        });

        mViewPager.setCurrentItem(0);

    }



}

