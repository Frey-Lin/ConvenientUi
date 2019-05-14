package com.scorpio.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.scorpio.ui.R;
import com.scorpio.ui.util.DensityUtil;


/**
 * Created by feng on 2018/5/2.
 */

public class CountView extends RelativeLayout implements View.OnClickListener {

    private ImageButton mIncreaseBtn;
    private ImageButton mDecreaseBtn;
    private TextView mCountTv;
    private int direction;
    private Drawable increaseDrawable;
    private Drawable decreaseDrawable;
    private float countTextSize;
    private int countTextColor;
    private float countMargin;
    private Drawable countDrawableBottom;
    private OnCountChangeListener mCountChangeListener;
    //当前数量
    private int mCurCount = 0;

    public CountView(Context context) {
        this(context, null);
    }

    public CountView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CountView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CountView);
        direction = typedArray.getInt(R.styleable.CountView_direction, 0);
        increaseDrawable = typedArray.getDrawable(R.styleable.CountView_increase_drawable);
        if (increaseDrawable == null) {
            increaseDrawable = getResources().getDrawable(R.drawable.bg_count_increase);
        }
        decreaseDrawable = typedArray.getDrawable(R.styleable.CountView_decrease_drawable);
        if (decreaseDrawable == null) {
            decreaseDrawable = getResources().getDrawable(R.drawable.bg_count_decrease);
        }
        countTextSize = typedArray.getDimension(R.styleable.CountView_count_textSize, DensityUtil.sp2px(context, 14));
        countTextColor = typedArray.getColor(R.styleable.CountView_count_textColor, 0);
        countMargin = typedArray.getDimension(R.styleable.CountView_count_margin, 0);
        countDrawableBottom = typedArray.getDrawable(R.styleable.CountView_count_drawable_bottom);
        typedArray.recycle();

        mIncreaseBtn = new ImageButton(context);
        //mIncreaseBtn.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        mIncreaseBtn.setBackground(increaseDrawable);
        //mIncreaseBtn.setImageDrawable(increaseDrawable);
        mIncreaseBtn.setOnClickListener(this);

        mDecreaseBtn = new ImageButton(context);
        // mDecreaseBtn.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        //mDecreaseBtn.setImageDrawable(decreaseDrawable);
        mDecreaseBtn.setBackground(decreaseDrawable);
        mDecreaseBtn.setOnClickListener(this);

        mCountTv = new TextView(context);
        mCountTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, countTextSize);
        mCountTv.setTextColor(countTextColor);
        mCountTv.setGravity(Gravity.CENTER);
        //mCountTv.setCompoundDrawables(null, null, null, countDrawableBottom);
        mCountTv.setCompoundDrawablesWithIntrinsicBounds(null, null, null, countDrawableBottom);

        addChild(context);

    }

    private void addChild(Context context) {
        int dp20 = DensityUtil.dip2px(context, 20);
        LayoutParams increaseBtnLp = new LayoutParams(dp20, dp20);
        LayoutParams decreaseBtnLp = new LayoutParams(dp20, dp20);
        LayoutParams countTvLp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        if (direction == 1) {
            increaseBtnLp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            increaseBtnLp.addRule(RelativeLayout.CENTER_HORIZONTAL);

            decreaseBtnLp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            decreaseBtnLp.addRule(RelativeLayout.CENTER_HORIZONTAL);

            countTvLp.addRule(RelativeLayout.CENTER_IN_PARENT);
        } else if (direction == 2) {
            increaseBtnLp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            increaseBtnLp.addRule(RelativeLayout.CENTER_HORIZONTAL);

            decreaseBtnLp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            decreaseBtnLp.addRule(RelativeLayout.CENTER_HORIZONTAL);

            countTvLp.addRule(RelativeLayout.CENTER_IN_PARENT);
        } else if (direction == 3) {
            increaseBtnLp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            increaseBtnLp.addRule(RelativeLayout.CENTER_VERTICAL);

            decreaseBtnLp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            decreaseBtnLp.addRule(RelativeLayout.CENTER_VERTICAL);

            countTvLp.addRule(RelativeLayout.CENTER_IN_PARENT);
        } else if (direction == 4) {
            increaseBtnLp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            increaseBtnLp.addRule(RelativeLayout.CENTER_VERTICAL);

            decreaseBtnLp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            decreaseBtnLp.addRule(RelativeLayout.CENTER_VERTICAL);

            countTvLp.addRule(RelativeLayout.CENTER_IN_PARENT);
        } else {
//            throw new IllegalArgumentException("布局中缺少direction属性或direction属性取值有误，确保direction取值为" +
//                    "vertical_top_increation，vertical_bottom_increation，horizontal_left_increation， horizontal_right_increation中之一");
            increaseBtnLp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            increaseBtnLp.addRule(RelativeLayout.CENTER_VERTICAL);

            decreaseBtnLp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            decreaseBtnLp.addRule(RelativeLayout.CENTER_VERTICAL);

            countTvLp.addRule(RelativeLayout.CENTER_IN_PARENT);
        }

        addView(mIncreaseBtn, increaseBtnLp);
        addView(mDecreaseBtn, decreaseBtnLp);
        addView(mCountTv, countTvLp);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int resWidth = 0;
        int resHeight = 0;
        if (widthMode == MeasureSpec.EXACTLY) {
            resWidth = widthSize;
        } else {
            if ("vertical_top_increation".equals(direction) || "vertical_bottom_increation".equals(direction)) {
                resWidth = getMaxChildWidth() + getPaddingLeft() + getPaddingRight();
            } else {
                resWidth = mDecreaseBtn.getMeasuredWidth() + mIncreaseBtn.getMeasuredWidth()
                        + mCountTv.getMeasuredWidth() + (int) (countMargin * 2) + getPaddingLeft() + getPaddingRight();
            }
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            resHeight = heightSize;
        } else {
            if ("vertical_top_increation".equals(direction) || "vertical_bottom_increation".equals(direction)) {
                resHeight = mIncreaseBtn.getMeasuredHeight() + mDecreaseBtn.getMeasuredHeight()
                        + mCountTv.getMeasuredHeight() + (int) (2 * countMargin) + getPaddingTop() + getPaddingBottom();
            } else {
                resHeight = getMaxChildHeight() + getPaddingTop() + getPaddingBottom();
            }
        }
        setMeasuredDimension(resWidth, resHeight);
    }

    private int getMaxChildHeight() {
        int maxHeight = 0;
        for (int i = 0; i < getChildCount(); i++) {
            int height = getChildAt(i).getMeasuredHeight();
            if (height > maxHeight) {
                maxHeight = height;
            }
        }
        return maxHeight;
    }

    private int getMaxChildWidth() {
        int maxWidth = 0;
        for (int i = 0; i < getChildCount(); i++) {
            int width = getChildAt(i).getMeasuredWidth();
            if (width > maxWidth) {
                maxWidth = width;
            }
        }
        return maxWidth;
    }

    public int getCount() {
        return mCurCount;
    }

    public void setOnCountChangeListener(OnCountChangeListener listener) {
        mCountChangeListener = listener;
    }

    @Override
    public void onClick(View v) {
        if (v == mIncreaseBtn) {
            mCurCount++;
            mCountTv.setText(mCurCount + "");
            if (mCountChangeListener != null) {
                mCountChangeListener.onCountChange(mCurCount);
            }
        } else if (v == mDecreaseBtn) {
            if (mCurCount <= 0) {
                return;
            } else {
                mCurCount--;
                mCountTv.setText(mCurCount + "");
                if (mCountChangeListener != null) {
                    mCountChangeListener.onCountChange(mCurCount);
                }
            }
        }
    }

    interface OnCountChangeListener {
        void onCountChange(int count);
    }
}
