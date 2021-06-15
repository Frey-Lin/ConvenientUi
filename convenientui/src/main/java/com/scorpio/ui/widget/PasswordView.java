package com.scorpio.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;

import com.scorpio.ui.util.DensityUtil;

import java.util.Arrays;

public class PasswordView extends View {

    private Paint mPaint;

    private float strokeWidth;

    private RectF rectF;

    private int[] password;

    private int count;

    private int mode = 1;

    private Paint.FontMetrics fontMetrics = new Paint.FontMetrics();

    public PasswordView(Context context) {
        this(context, null);
    }

    public PasswordView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PasswordView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        password = new int[6];
        strokeWidth = DensityUtil.dip2px(context, 1);
        rectF = new RectF();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.GRAY);
        mPaint.setStrokeWidth(strokeWidth);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDraw(Canvas canvas) {

        int width = getWidth();
        int height = getHeight();

        rectF.set(strokeWidth / 2 + getPaddingLeft(),
                strokeWidth / 2 + getPaddingTop(),
                width - strokeWidth / 2 - getPaddingRight(),
                height - strokeWidth / 2 - getPaddingBottom());

        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.LTGRAY);
        mPaint.setStrokeWidth(strokeWidth);
        //画圆角外框
        canvas.drawRoundRect(rectF, 20, 20, mPaint);

        //画分割线
        float ceilWidth = rectF.width() / 6;
        for (int i = 0; i < 5; i++) {
            canvas.drawLine(ceilWidth * (i + 1) + rectF.left, rectF.top, ceilWidth * (i + 1) + rectF.left, rectF.bottom, mPaint);
        }

        //画点
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.BLACK);
        mPaint.setTextSize(DensityUtil.dip2px(getContext(), 20));
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.getFontMetrics(fontMetrics);
        for (int i = 0; i < count; i++) {
            if (mode == 0) {
                canvas.drawCircle(ceilWidth * (i + 1) - ceilWidth / 2 + rectF.left, (rectF.bottom + rectF.top) / 2, 20, mPaint);
            } else {
                canvas.drawText(String.valueOf(password[i]),
                        ceilWidth * (i + 1) - ceilWidth / 2 + rectF.left,
                        (rectF.bottom + rectF.top) / 2 - (fontMetrics.ascent + fontMetrics.descent) / 2,
                        mPaint);
            }
        }
    }

    public void add(int num) {
        if (count >= password.length)
            return;
        password[count++] = num;
        System.out.println(Arrays.toString(password));
        invalidate();
    }

    public void delete() {
        if (count <= 0)
            return;
        password[--count] = 0;
        System.out.println(Arrays.toString(password));
        invalidate();
    }

    public void clean() {
        count = 0;
        Arrays.fill(password, 0);
        invalidate();
    }

}
