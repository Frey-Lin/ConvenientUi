package com.sample;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.scorpio.ui.widget.HorizontalScrollTabLayout;

/**
 * 创建日期：2021/12/17 10:47
 *
 * @author linxuefeng
 * @version 1.0
 * 类说明：
 */
public class TabLayoutActivity extends AppCompatActivity {

    private HorizontalScrollTabLayout tabLayout;

    private ViewPager viewPager;

    private MyPageAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_layout);
        initView();
    }

    private void initView() {
        viewPager = findViewById(R.id.viewpager);
        adapter = new MyPageAdapter();
        viewPager.setAdapter(adapter);
        tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupViewPager(viewPager);
        for (int i = 0; i < adapter.getCount(); i++) {
            View view = LayoutInflater.from(this).inflate(R.layout.tab_item, tabLayout.getContainer(), false);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
            layoutParams.weight = 1;
            view.setLayoutParams(layoutParams);
            TextView item = view.findViewById(R.id.item);
            item.setText(i + 1 + "");
            tabLayout.addTab(view);
        }
        tabLayout.setItem(1);
    }

    private class MyPageAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return 10;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
            return view == o;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            TextView view = new TextView(TabLayoutActivity.this);
            view.setGravity(Gravity.CENTER);
            view.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 50);
            view.setText(position + 1 + "");
            view.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
            view.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
            view.setTextColor(getResources().getColor(R.color.pink_light4));
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
           // container.removeViewAt(position);
        }
    }

}
