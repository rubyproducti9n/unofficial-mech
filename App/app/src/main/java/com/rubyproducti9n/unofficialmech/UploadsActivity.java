package com.rubyproducti9n.unofficialmech;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import androidx.viewpager.widget.ViewPager;


import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

public class UploadsActivity extends BaseActivity {

    long backPressedTime;
    boolean backPressDisabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uploads);

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        ViewPager viewPager = findViewById(R.id.view_pager);

        UploadsActivity.MyPagerAdapter pagerAdapter = new UploadsActivity.MyPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        tabLayout.setupWithViewPager(viewPager);

        String fragmentIdentifier = getIntent().getStringExtra("fragmentIdentifier");
        Fragment fragment;
        if ("FragmentCreatePost".equals(fragmentIdentifier)) {
            fragment = new FragmentCreatePost();
        } else if ("FragmentNotice".equals(fragmentIdentifier)) {
            fragment = new FragmentNotice();
        } else if ("FragmentMemories".equals(fragmentIdentifier)) {
            fragment = new FragmentMemories();
        } else {
            fragment = new FragmentNotice();
        }
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.view_pager, fragment)
                .commit();
    }

    private static class MyPagerAdapter extends FragmentPagerAdapter {

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Memories";
                default:
                    return null;
            }
        }

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new FragmentMemories();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 1;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}