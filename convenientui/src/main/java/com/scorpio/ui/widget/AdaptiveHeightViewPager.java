package com.scorpio.ui.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

/**
 * A viewpager which could adjust its height by the highest child
 * @author Frey-Lin
 */

public class AdaptiveHeightViewPager extends ViewPager {

    private SimpleOnPageChangeListener simpleOnPageChangeListener = new SimpleOnPageChangeListener() {
        @Override
        public void onPageSelected(int position) {
            setHeight(position);
        }
    };

    public AdaptiveHeightViewPager(Context context) {
        super(context);
        addOnPageChangeListener(simpleOnPageChangeListener);
    }

    public AdaptiveHeightViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        addOnPageChangeListener(simpleOnPageChangeListener);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = 0;
        //下面遍历所有child的高度
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            child.measure(widthMeasureSpec,
                    MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            int h = child.getMeasuredHeight();
            if (h > height) //采用最大的view的高度。
                height = h;
        }
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void setHeight(int position) {
        if (position < 0 || position >= getChildCount())
            return;
        View child = getChildAt(position);
        getLayoutParams().height = child.getMeasuredHeight();
    }

}
