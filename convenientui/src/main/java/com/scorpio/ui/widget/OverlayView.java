package com.scorpio.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import com.scorpio.ui.R;

/**
 * 创建日期：2021/12/14 15:00
 *
 * @author linxuefeng
 * @version 1.0
 * 类说明：
 */
public class OverlayView extends View {

    private RectF mCropRect;

    private float[] mGridPoints;

    private int mGridRowCount = 3, mGridColumnCount = 3;

    private final Paint mCropGridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    /**
     * 触摸位置离裁剪框四个角的距离阈值，小于等于该值认为触摸了其中一个角
     */
    private final int mCornerTouchThreshold;

    /**
     * 裁剪框四个角的点坐标
     */
    private PointF[] mCornerPoints;

    /**
     * 当前触摸裁剪框的区域，左上、左下、右上、右下、内部
     */
    private TouchArea mTouchArea;

    private float mPreTouchX = -1, mPreTouchY = -1;

    {
        mCornerTouchThreshold = getResources().getDimensionPixelSize(R.dimen.dp20);
    }

    public OverlayView(Context context) {
        this(context, null);
    }

    public OverlayView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OverlayView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mCropRect = new RectF(100, 100, 100 + 800, 100 + 800);
        mCropGridPaint.setStrokeWidth(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1f, getResources().getDisplayMetrics()));
        mCropGridPaint.setColor(Color.GREEN);
        mCornerPoints = getCornerPoints();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawCropGrid(canvas);
    }

    /**
     * 画网格线
     *
     * @param canvas
     */
    private void drawCropGrid(Canvas canvas) {
        if (mGridPoints == null && !mCropRect.isEmpty()) {
            // 两点确定一条直线，每个点需要两个float值表示x,y坐标，一条直线需要四个float，需要mGridRowCount + 1条横线和mGridColumnCount + 1条竖线
            mGridPoints = new float[(mGridRowCount + 1) * 4 + (mGridColumnCount + 1) * 4];
        }
        int index = 0;
        for (int i = 0; i < mGridRowCount + 1; i++) {
            mGridPoints[index++] = mCropRect.left;
            mGridPoints[index++] = mCropRect.height() * (((float) i) / mGridRowCount) + mCropRect.top;
            mGridPoints[index++] = mCropRect.right;
            mGridPoints[index++] = mCropRect.height() * (((float) i) / mGridRowCount) + mCropRect.top;
        }

        for (int i = 0; i < mGridColumnCount + 1; i++) {
            mGridPoints[index++] = mCropRect.width() * (((float) i) / mGridColumnCount) + mCropRect.left;
            mGridPoints[index++] = mCropRect.top;
            mGridPoints[index++] = mCropRect.width() * (((float) i) / mGridColumnCount) + mCropRect.left;
            mGridPoints[index++] = mCropRect.bottom;
        }

        if (mGridPoints != null) {
            canvas.drawLines(mGridPoints, mCropGridPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mCropRect.isEmpty()) {
            return false;
        }
        float x = event.getX();
        float y = event.getY();
        if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_DOWN) {
            mTouchArea = getCurrentTouchArea(event);
            boolean shouldHandle = mTouchArea != null /*&& mTouchArea != TouchArea.INSIDE*/;
            if (shouldHandle) {
                mPreTouchX = x;
                mPreTouchY = y;
            } else {
                mPreTouchX = -1;
                mPreTouchY = -1;
            }
            return shouldHandle;
        } else if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_MOVE) {
            if (mTouchArea != null) {
                // 确保x, y的值在合法的范围区域内
                x = Math.min(Math.max(x, getPaddingLeft()), getWidth() - getPaddingRight());
                y = Math.min(Math.max(y, getPaddingTop()), getHeight() - getPaddingBottom());
                updateCropRect(x, y);

                mPreTouchX = x;
                mPreTouchY = y;
                return true;
            }
        } else if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
            resetTouch();
        }
        return super.onTouchEvent(event);
    }

    private void resetTouch() {
        mPreTouchX = -1;
        mPreTouchY = -1;
        mTouchArea = null;
    }

    private enum TouchArea {
        LEFT_TOP,
        RIGHT_TOP,
        LEFT_BOTTOM,
        RIGHT_BOTTOM,
        INSIDE
    }

    /**
     * 获取当前触摸裁剪框的哪个区域
     * @param event
     * @return
     */
    private TouchArea getCurrentTouchArea(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        double closestCornerDistance = mCornerTouchThreshold;
        TouchArea[] touchAreas = new TouchArea[]{TouchArea.LEFT_TOP, TouchArea.RIGHT_TOP, TouchArea.LEFT_BOTTOM, TouchArea.RIGHT_BOTTOM};
        TouchArea touchArea = null;
        for (int i = 0; i < 4; i++) {
            double distanceToCorner = Math.sqrt(Math.pow(x - mCornerPoints[i].x, 2) + Math.pow(y - mCornerPoints[i].y, 2));
            if (distanceToCorner < closestCornerDistance) {
                closestCornerDistance = distanceToCorner;
                touchArea = touchAreas[i];
            }
        }

        if (touchArea == null && mCropRect.contains(x, y)) {
            touchArea = TouchArea.INSIDE;
        }

        return touchArea;
    }

    /**
     * 四个顶点的坐标
     *
     * @return
     */
    private PointF[] getCornerPoints() {
        return new PointF[]{
                new PointF(mCropRect.left, mCropRect.top),
                new PointF(mCropRect.right, mCropRect.top),
                new PointF(mCropRect.left, mCropRect.bottom),
                new PointF(mCropRect.right, mCropRect.bottom)
        };
    }

    /**
     * 更新裁剪框位置和大小
     *
     * @param touchX
     * @param touchY
     */
    private void updateCropRect(float touchX, float touchY) {
        switch (mTouchArea) {
            case LEFT_TOP:
                mCropRect.set(touchX, touchY, mCropRect.right, mCropRect.bottom);
                break;
            case LEFT_BOTTOM:
                mCropRect.set(touchX, mCropRect.top, mCropRect.right, touchY);
                break;
            case RIGHT_TOP:
                mCropRect.set(mCropRect.left, touchY, touchX, mCropRect.bottom);
                break;
            case RIGHT_BOTTOM:
                mCropRect.set(mCropRect.left, mCropRect.top, touchX, touchY);
                break;
            case INSIDE:
                mCropRect.offset(touchX - mPreTouchX, touchY - mPreTouchY);
                break;
        }
        mCornerPoints = getCornerPoints();
        invalidate();
    }
}
