package com.scorpio.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.scorpio.ui.util.DensityUtil;


public class KeyboardView extends View {

    private String[] keys = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "CE", "0", "X"};

    private Paint mPaint;

    private RectF rectF;

    private float strokeWidth;

    private Paint.FontMetrics fontMetrics = new Paint.FontMetrics();

    private OnKeyDownListener onKeyDownListener;
    private float ceilWidth;
    private float ceilHeight;

    public KeyboardView(Context context) {
        this(context, null);
    }

    public KeyboardView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public KeyboardView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        rectF = new RectF();
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        strokeWidth = DensityUtil.dip2px(context, 0.5f);
        mPaint.setStrokeWidth(strokeWidth);
        mPaint.setColor(Color.GRAY);
        mPaint.setAntiAlias(true);
        mPaint.getFontMetrics(fontMetrics);
    }

    public void setOnKeyDownListener(OnKeyDownListener listener) {
        if (listener != null)
            this.onKeyDownListener = listener;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawLine(canvas);

        drawKeys(canvas);
    }

    private void drawKeys(Canvas canvas) {
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setTextSize(DensityUtil.dip2px(getContext(), 20));
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.BLACK);

        for (int row = 0; row < 4; row++) {
            for (int cloum = 0; cloum < 3; cloum++) {
                String keyName = keys[row * 3 + cloum];
                canvas.drawText(keyName,
                        ceilWidth * (cloum + 1) - ceilWidth / 2 + rectF.left,
                        ceilHeight * (row + 1) - ceilHeight / 2 + rectF.top - (fontMetrics.descent + fontMetrics.ascent) / 2,
                        mPaint);
            }
        }
    }

    private void drawLine(Canvas canvas) {
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(strokeWidth);
        mPaint.setColor(Color.LTGRAY);
        for (int i = 0; i < 5; i++) {
            canvas.drawLine(rectF.left, i * ceilHeight + rectF.top, rectF.right, i * ceilHeight + rectF.top, mPaint);
        }

        for (int i = 0; i < 2; i++) {
            canvas.drawLine(
                    ceilWidth * (i + 1) + rectF.left, rectF.top,
                    ceilWidth * (i + 1) + rectF.left, rectF.bottom,
                    mPaint);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        rectF.set(strokeWidth / 2 + getPaddingLeft(),
                strokeWidth / 2 + getPaddingTop(),
                w - getPaddingRight() - strokeWidth / 2,
                h - getPaddingBottom() - strokeWidth / 2);
        ceilWidth = rectF.width() / 3;
        ceilHeight = rectF.height() / 4;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            int position = getPositionByTouchEvent(event);
            if (onKeyDownListener != null) {
                onKeyDownListener.onKeyDown(position, keys[position]);
            }
        }
        return true;
    }

    private int getPositionByTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        int row = (int) (y - rectF.top) / (int) ceilHeight;
        int cloum = (int) (x - rectF.left) / (int) ceilWidth;
        return row * 3 + cloum;
    }

    public interface OnKeyDownListener {
        void onKeyDown(int index, String keyName);
    }
}
