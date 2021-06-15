package com.scorpio.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.scorpio.ui.R;
import com.scorpio.ui.util.DensityUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by feng on 2018/5/17.
 */

public class HorizontalScrollTabLayout extends RelativeLayout {
    private LinearLayout mContainer;
    private ViewPager mViewPager;
    private HorizontalScrollView mScrollView;
    private final static String TAG = "tab";
    private ImageView indicator;
    private TabLayoutOnPageChangeListener mListener;
    private List<View> views = new ArrayList<>();
    private int margin;
    int curIndex = -1;
    boolean firstBoot = true;
    private boolean isIndicatorsShouldScroll = true;
    private OnTabSelectListener mTabSelectListener;
    private int mLayoutMode;
    private int mAverageMaxCount;

    private final int LAYOUT_MODE_AVERAGE = 1;
    private final int LAYOUT_MODE_NONE = 2;
    private final int LAYOUT_MODE_AVERAGE_MAX_COUNT = 3;

    int mWidthMeasureSpec, mHeightMeasureSpec;

    private boolean mSetItemOnce;

    public HorizontalScrollTabLayout(Context context) {
        this(context, null);
    }

    public HorizontalScrollTabLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HorizontalScrollTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidthMeasureSpec = widthMeasureSpec;
        mHeightMeasureSpec = heightMeasureSpec;
        int width = MeasureSpec.getSize(widthMeasureSpec);
        if (mLayoutMode == LAYOUT_MODE_AVERAGE && mAverageMaxCount > 0) {
            int perWidth = width / mAverageMaxCount;
            for (int i = 0; i < mContainer.getChildCount(); i++) {
                mContainer.getChildAt(i).getLayoutParams().width = perWidth;
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mLayoutMode == LAYOUT_MODE_AVERAGE && mAverageMaxCount > 0) {
            int perWidth = getMeasuredWidth() / mAverageMaxCount;
            for (int i = 0; i < mContainer.getChildCount(); i++) {
                mContainer.getChildAt(i).getLayoutParams().width = perWidth;
            }
        }
    }

    @Override
    public void addView(View child, ViewGroup.LayoutParams params) {
        if (mContainer != null) {
            addTab(child, params);
        } else {
            super.addView(child, params);
        }
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return super.generateLayoutParams(attrs);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mContainer.getChildCount() > 0 && indicator != null) {
            indicator.setVisibility(View.VISIBLE);
        } else {
            indicator.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    public void addTab(View view) {
        addTab(view, view.getLayoutParams());
    }

    public void addTab(View view, ViewGroup.LayoutParams layoutParams) {
        if (mContainer.getChildCount() == 0) {
            view.setSelected(true);
        }
        if (layoutParams != null) {
            mContainer.addView(view, layoutParams);
        } else {
            mContainer.addView(view);
            //view.measure(mWidthMeasureSpec, mHeightMeasureSpec);
        }
        mContainer.measure(mWidthMeasureSpec, mHeightMeasureSpec);
        view.setClickable(true);
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = mContainer.indexOfChild(v);
                setItem(index);
                if (mViewPager != null && index < mViewPager.getAdapter().getCount()) {
                    isIndicatorsShouldScroll = false;
                    mViewPager.setCurrentItem(index);
                }
                if (mTabSelectListener != null) {
                    mTabSelectListener.onTabSelected(v, index);
                }
            }
        });

        if (mLayoutMode == LAYOUT_MODE_AVERAGE && mAverageMaxCount > 0) {
            int perWidth = getMeasuredWidth() / mAverageMaxCount;
            for (int i = 0; i < mContainer.getChildCount(); i++) {
                mContainer.getChildAt(i).getLayoutParams().width = perWidth;
            }
        }
        if (indicator != null && indicator.getVisibility() != View.VISIBLE) {
            indicator.setVisibility(View.VISIBLE);
        }
        //setItem(0);
    }


    public void setupViewPager(ViewPager viewPager) {
        setItem(0);
        if (mListener == null) {
            mListener = new TabLayoutOnPageChangeListener();
        }
        mViewPager = viewPager;
        viewPager.addOnPageChangeListener(mListener);
    }

    int lastOffsetPixels = 0;
    int tempMargin;
    int tempCurIndex;
    int scrollX;

    class TabLayoutOnPageChangeListener implements ViewPager.OnPageChangeListener {


        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            Log.e(TAG, "=============onPageScrolled=" + positionOffset);
            Log.e(TAG, "positionOffset=" + positionOffset);
            Log.e(TAG, "positionOffsetPixels=" + positionOffsetPixels);
            Log.e(TAG, "position=" + position);
            Log.e(TAG, "curIndex=" + tempCurIndex);
            Log.e(TAG, "===========================" + positionOffset);
            if (mContainer.getChildCount() <= 0) {
                return;
            }
//                if (positionOffset == 0) {
//                    return;
//                }
            if (position == tempCurIndex) {
                Log.e(TAG, "向右滑");
                if (tempCurIndex == mContainer.getChildCount() - 1) {
                    return;
                }
                int lineWidth = indicator.getWidth();
                int v1Width = mContainer.getChildAt(tempCurIndex).getWidth();
                int v2Width = mContainer.getChildAt(tempCurIndex + 1).getWidth();
                int len = lineWidth + (v1Width - lineWidth) / 2 + (v2Width - lineWidth) / 2;
                if (isIndicatorsShouldScroll) {
                    ((FrameLayout.LayoutParams) indicator.getLayoutParams()).leftMargin = tempMargin + (int) (len * positionOffset);
                    indicator.requestLayout();
                }
                int nextRight = mContainer.getChildAt(tempCurIndex + 1).getRight() - scrollX;
                if (nextRight > mScrollView.getRight()) {
                    mScrollView.smoothScrollTo(scrollX + (int) (len * positionOffset), 0);
                }
                Log.e(TAG, "mScrollView.getScrollX=" + mScrollView.getScrollX());
                if (positionOffset <= 0.5) {

                } else {

                }
            } else if (position < tempCurIndex) {
                Log.e(TAG, "向左滑");
                if (tempCurIndex == 0) {
                    return;
                }
                int lineWidth = indicator.getWidth();
                int v1Width = mContainer.getChildAt(tempCurIndex).getWidth();
                int v2Width = mContainer.getChildAt(tempCurIndex - 1).getWidth();
                int len = lineWidth + (v1Width - lineWidth) / 2 + (v2Width - lineWidth) / 2;
                Log.e(TAG, "向左滑margin:" + margin);
                int offest = (int) (len * (1 - positionOffset));
                if (isIndicatorsShouldScroll) {
                    ((FrameLayout.LayoutParams) indicator.getLayoutParams()).leftMargin = tempMargin - offest;
                    indicator.requestLayout();
                }
                int lastLeft = mContainer.getChildAt(position).getLeft() - scrollX;
                if (lastLeft < 0) {
                    mScrollView.smoothScrollTo(scrollX - (int) (len * (1 - positionOffset)), 0);
                }
            }
            lastOffsetPixels = positionOffsetPixels;
        }

        @Override
        public void onPageSelected(int position) {
            Log.e(TAG, "onPageSelected position = " + position);
            curIndex = position;
            Log.e(TAG, "margin:" + margin);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if (mContainer.getChildCount() <= 0)
                return;
            if (state == 1) {//开始滑动
                tempMargin = ((FrameLayout.LayoutParams) indicator.getLayoutParams()).leftMargin;
                tempCurIndex = curIndex;
                scrollX = mScrollView.getScrollX();
            } else if (state == 0) {//nothing
                tempMargin = margin;
                tempCurIndex = curIndex;
                scrollX = mScrollView.getScrollX();
                isIndicatorsShouldScroll = true;
                setItem(curIndex);
                margin = mContainer.getChildAt(curIndex).getLeft() + (mContainer.getChildAt(curIndex).getWidth() - indicator.getWidth()) / 2;
            }
            Log.e(TAG, "onPageScrollStateChanged state=" + state);
        }
    }

    public void setItem(int i) {
        if (i < 0 || i > mContainer.getChildCount() - 1) {
            return;
        }
        reset();
        View child = mContainer.getChildAt(i);
        child.setSelected(true);
        int left = child.getLeft();
        Log.e(TAG, "left=" + left);
        // int childWidth = child.getLayoutParams().width;
        int childWidth = child.getMeasuredWidth();
        int indicatorsWidth = indicator.getMeasuredWidth();
        Log.e(TAG, "child.getWidth()=" + childWidth);
        int lineLeft = left + (childWidth - indicatorsWidth) / 2;
//        line.setLeft(lineLeft);
        FrameLayout.LayoutParams params = ((FrameLayout.LayoutParams) indicator.getLayoutParams());
        params.leftMargin = lineLeft;
        tempMargin = lineLeft;
        indicator.setLayoutParams(params);
        curIndex = i;
    }

    public void reset() {
        mSetItemOnce = false;
        for (int i = 0; i < mContainer.getChildCount(); i++) {
            cleanSelected(mContainer.getChildAt(i));
        }
    }

    private void cleanSelected(View view) {
//        if (view instanceof ViewGroup) {
//            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
//                cleanSelected(((ViewGroup) view).getChildAt(i));
//            }
//        }
        view.setSelected(false);
    }

    public void setOnTabSelectListener(OnTabSelectListener listener) {
        if (listener != null) {
            mTabSelectListener = listener;
        }
    }

    private void init(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.tablayout, this);
        mContainer = findViewById(R.id.container);
        mContainer.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (!mSetItemOnce) {
                    setItem(curIndex);
                    mSetItemOnce = true;
                }
            }
        });
        mScrollView = findViewById(R.id.scrollView);
        indicator = findViewById(R.id.line);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.HorizontalScrollTabLayout);
        Drawable drawable = typedArray.getDrawable(R.styleable.HorizontalScrollTabLayout_indicatorDrawable);
        indicator.setImageDrawable(drawable);

        int indicatorWidth = typedArray.getDimensionPixelSize(R.styleable.HorizontalScrollTabLayout_indicatorWidth, (int) DensityUtil.dip2px(context, 20));
        indicator.getLayoutParams().width = indicatorWidth;
        int indicatorHeight = typedArray.getDimensionPixelSize(R.styleable.HorizontalScrollTabLayout_indicatorHeight, (int) DensityUtil.dip2px(context, 20));
        indicator.getLayoutParams().height = indicatorHeight;
        int indicatorPadding = typedArray.getDimensionPixelSize(R.styleable.HorizontalScrollTabLayout_indicatorPadding, 0);
        ((FrameLayout.LayoutParams) indicator.getLayoutParams()).topMargin = indicatorPadding;

        mLayoutMode = typedArray.getInt(R.styleable.HorizontalScrollTabLayout_layoutMode, 2);
        mAverageMaxCount = typedArray.getInt(R.styleable.HorizontalScrollTabLayout_averageMaxCount, 3);
        typedArray.recycle();
    }

    public LinearLayout getContainer() {
        return mContainer;
    }

    public interface OnTabSelectListener {
        void onTabSelected(View tabView, int position);
    }

}
