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
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.scorpio.ui.R;
import com.scorpio.ui.util.DensityUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by feng on 2018/5/17.
 */

public class HorizontalScrollTabLayout extends RelativeLayout {
    /**
     * 所有tab的容器
     */
    private LinearLayout mContainer;
    /**
     * 和HorizontalScrollTabLayout绑定的ViewPager，可能为null
     */
    private ViewPager mViewPager;
    /**
     * 水平滑动的ScrollView
     */
    private HorizontalScrollView mScrollView;
    private final static String TAG = "tab";
    /**
     * tab的指示器
     */
    private ImageView indicator;
    /**
     * 监听ViewPager页面的变化
     */
    private TabLayoutOnPageChangeListener mListener;
    private List<View> views = new ArrayList<>();
    private int margin;
    int curIndex;
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
        setItem(curIndex);
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


    private void init(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.tablayout, this);
        mContainer = findViewById(R.id.container);
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            // 这个方法会多次调用，当ViewPager滑动的时候，会修改指示器的位置，这个方法会被触发，导致指示器位置跳跃
            @Override
            public void onGlobalLayout() {
                Log.e(TAG, "onGlobalLayout curIndex = " + curIndex);
//                if (!mSetItemOnce) {
//                    setItem(curIndex);
//                    mSetItemOnce = true;
//                }
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
        ((MarginLayoutParams) indicator.getLayoutParams()).topMargin = indicatorPadding;

        mLayoutMode = typedArray.getInt(R.styleable.HorizontalScrollTabLayout_layoutMode, 2);
        mAverageMaxCount = typedArray.getInt(R.styleable.HorizontalScrollTabLayout_averageMaxCount, 3);
        typedArray.recycle();
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
                if (mViewPager != null && index >= 0 && index < mViewPager.getAdapter().getCount()) {
                    isIndicatorsShouldScroll = false;
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
    }


    public void setupViewPager(ViewPager viewPager) {
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

        /**
         * @param position             当前页面
         * @param positionOffset       当前页面偏移的百分比，向下一页滑动时，positionOffset从[0-1)变化, 向上一页滑动时从(1-0]变化
         * @param positionOffsetPixels 当前页面偏移的像素 向下一页滑动时，positionOffsetPixels从[0-pageWidth)变化， 向上一页滑动时
         *                             从(pageWidth-0]变化
         */
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
            // 往下一页滑动
            if (position == tempCurIndex) {
                Log.e(TAG, "向右滑");
                if (tempCurIndex == mContainer.getChildCount() - 1) {
                    return;
                }
                // 当前选中的tab的宽度
                int v1Width = mContainer.getChildAt(tempCurIndex).getWidth();
                // 下一个tab的宽度
                int v2Width = mContainer.getChildAt(tempCurIndex + 1).getWidth();
                // 指示器到下个tab之间移动的最大距离
                int maxDistanceToNextTab = (v1Width + v2Width) / 2;
                if (isIndicatorsShouldScroll) {
                    moveIndicatorTo(tempMargin + (int) (maxDistanceToNextTab * positionOffset));
                }
                int nextRight = mContainer.getChildAt(tempCurIndex + 1).getRight() - scrollX;
                // 如果下个tab的右边显示不全
                if (nextRight > mScrollView.getRight()) {
                    // positionOffset不会等于1，需手动设置
                    if (positionOffset > 0.9) {
                        positionOffset = 1;
                    }
                    //mScrollView.smoothScrollTo(scrollX + (int) (maxDistanceToNextTab * positionOffset), 0);
                }
                Log.e(TAG, "mScrollView.getScrollX=" + mScrollView.getScrollX());
                if (positionOffset <= 0.5) {

                } else {

                }
            } else if (position < tempCurIndex) {// 往上一页滑动
                Log.e(TAG, "向左滑");
                if (tempCurIndex == 0) {
                    return;
                }
                int lineWidth = indicator.getWidth();
                int v1Width = mContainer.getChildAt(tempCurIndex).getWidth();
                int v2Width = mContainer.getChildAt(tempCurIndex - 1).getWidth();
                // 指示器到上一个tab的最大移动距离
                int maxDistanceToLastTab = (v1Width + v2Width) / 2;
                Log.e(TAG, "向左滑margin:" + margin);
                int offset = (int) (maxDistanceToLastTab * (1 - positionOffset));
                if (isIndicatorsShouldScroll) {
                    moveIndicatorTo(tempMargin - offset);
                }
                int lastLeft = mContainer.getChildAt(position).getLeft() - scrollX;
                if (lastLeft < 0) {
                    //mScrollView.smoothScrollTo(scrollX - (int) (maxDistanceToLastTab * (1 - positionOffset)), 0);
                }
            }
            makeIndicatorCenter();
            lastOffsetPixels = positionOffsetPixels;
        }

        @Override
        public void onPageSelected(int position) {
            Log.e(TAG, "onPageSelected position = " + position);
            curIndex = position;
            Log.e(TAG, "margin:" + margin);
            if (mTabSelectListener != null) {
                mTabSelectListener.onTabSelected(mContainer.getChildAt(position), position);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            // 一个常规的state顺序为1-2-0
            Log.e(TAG, "onPageScrollStateChanged state=" + state);
            if (mContainer.getChildCount() <= 0)
                return;
            if (state == 1) {//开始滑动
                // 快速滑动时，onPageScrollStateChanged会被调用多次，状态为1-2-1-2-0，如果对tempMargin和tempCurIndex重新赋值会使计算错误
                //tempMargin = ((MarginLayoutParams) indicator.getLayoutParams()).leftMargin;
                //tempCurIndex = curIndex;
                scrollX = mScrollView.getScrollX();
            } else if (state == 0) {//nothing
                tempCurIndex = curIndex;
                scrollX = mScrollView.getScrollX();
                isIndicatorsShouldScroll = true;
                setItem(curIndex);
                margin = mContainer.getChildAt(curIndex).getLeft() + (mContainer.getChildAt(curIndex).getWidth() - indicator.getWidth()) / 2;
                if (curIndex == 0) {
                    // 滑动结束后选中第一个tab，则滑动到最左边
                    mScrollView.fullScroll(ScrollView.FOCUS_LEFT);
                } else if (curIndex == mContainer.getChildCount() - 1) {
                    // 滑动结束后选中最后一个tab，则滑动到最右边
                    mScrollView.fullScroll(ScrollView.FOCUS_RIGHT);
                } else {
                    makeIndicatorCenter();
                }
            }
        }
    }

    public void setItem(int position) {
        if (position < 0 || position > mContainer.getChildCount() - 1) {
            return;
        }
        reset();
        View child = mContainer.getChildAt(position);
        child.setSelected(true);
        int left = getIndicatorLeft(position);
        moveIndicatorTo(left);
        tempMargin = left;
        curIndex = position;
        tempCurIndex = position;
        switchViewPager(position);
    }

    private void makeIndicatorCenter() {
        int indicatorLeft = indicator.getLeft();
        View lastView = mContainer.getChildAt(mContainer.getChildCount() - 1);
        if ((lastView.getRight() < getWidth()
                || indicatorLeft < (getWidth() - indicator.getWidth()) / 2
                || lastView.getRight() - indicator.getRight() < (getWidth() - indicator.getWidth()) / 2)
                && (indicatorLeft - getWidth() / 2 > mScrollView.getScrollX()
                && indicatorLeft - mScrollView.getScrollX() <= (getWidth() - indicator.getWidth()) / 2)) {
            return;
        }
        int scrollTo = (indicatorLeft - getWidth() / 2) + indicator.getWidth() / 2;
        mScrollView.smoothScrollTo(scrollTo, 0);
    }

    private void switchViewPager(int position) {
        if (mViewPager != null && position >= 0 && position < mViewPager.getAdapter().getCount()) {
            mViewPager.setCurrentItem(position);
        }
    }

    public void reset() {
        mSetItemOnce = false;
        for (int i = 0; i < mContainer.getChildCount(); i++) {
            cleanSelected(mContainer.getChildAt(i));
        }
    }

    private void cleanSelected(View view) {
        view.setSelected(false);
    }

    private int getIndicatorLeft(int position) {
        View child = mContainer.getChildAt(position);
        int childLeft = child.getLeft();
        int childWidth = child.getMeasuredWidth();
        int indicatorsWidth = indicator.getMeasuredWidth();
        int left = childLeft + (childWidth - indicatorsWidth) / 2;
        return left;
    }

    /**
     * 移动指示器
     *
     * @param dx x方向的增量
     */
    private void moveIndicatorBy(int dx) {
        if (indicator == null || !(indicator.getLayoutParams() instanceof MarginLayoutParams)) {
            return;
        }
        MarginLayoutParams params = ((MarginLayoutParams) indicator.getLayoutParams());
        params.leftMargin += dx;
        indicator.setLayoutParams(params);
    }

    /**
     * 移动指示器
     *
     * @param leftMargin x方向的偏移量
     */
    private void moveIndicatorTo(int leftMargin) {
        if (indicator == null || !(indicator.getLayoutParams() instanceof MarginLayoutParams)) {
            return;
        }
        MarginLayoutParams params = ((MarginLayoutParams) indicator.getLayoutParams());
        params.leftMargin = leftMargin;
        indicator.setLayoutParams(params);
    }

    public void setOnTabSelectListener(OnTabSelectListener listener) {
        if (listener != null) {
            mTabSelectListener = listener;
        }
    }


    public LinearLayout getContainer() {
        return mContainer;
    }

    public interface OnTabSelectListener {
        void onTabSelected(View tabView, int position);
    }

}
