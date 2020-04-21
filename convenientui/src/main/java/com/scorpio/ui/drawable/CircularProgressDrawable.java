package com.scorpio.ui.drawable;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.concurrent.Delayed;

public class CircularProgressDrawable extends Drawable implements Animatable {

    private Paint mPaint;

    private Context mContext;

    private int mCricleColor;

    private float mStrokeWidth;

    private ValueAnimator angleAnimator;

    private ValueAnimator sweepAngleAnimator;

    private float mAngle;

    private float mSweepAngle;

    private boolean isRunning;

    private RectF mBounds;

    public CircularProgressDrawable(Context context, int color, float strokeWidth) {
        this.mContext = context;
        this.mCricleColor = color;
        this.mStrokeWidth = strokeWidth;
        init();
    }

    private void init() {
        mBounds = new RectF();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(mCricleColor);
        mPaint.setStrokeWidth(mStrokeWidth);
        setupAnimator();
    }

    private void setupAnimator() {
        angleAnimator = ValueAnimator.ofFloat(0, 360);
        angleAnimator.setDuration(1000);
        angleAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        angleAnimator.setRepeatCount(ValueAnimator.INFINITE);
        angleAnimator.setRepeatMode(ValueAnimator.RESTART);
        angleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mAngle = (float) animation.getAnimatedValue();
                invalidateSelf();
            }
        });

        sweepAngleAnimator = ValueAnimator.ofFloat(0, 300);
        sweepAngleAnimator.setDuration(1000);
        sweepAngleAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        sweepAngleAnimator.setRepeatCount(ValueAnimator.INFINITE);
        sweepAngleAnimator.setRepeatMode(ValueAnimator.REVERSE);
        sweepAngleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mSweepAngle = (float) animation.getAnimatedValue();
            }
        });
    }


    @Override
    public void draw(@NonNull Canvas canvas) {
        if (!mBounds.isEmpty()) {
            canvas.drawArc(mBounds, mAngle, mSweepAngle, false, mPaint);
        }
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        if (bounds != null) {
            mBounds.left = bounds.left + mStrokeWidth / 2 + 0.5f;
            mBounds.top = bounds.top + mStrokeWidth / 2 + 0.5f;
            mBounds.right = bounds.right - mStrokeWidth / 2 - 0.5f;
            mBounds.bottom = bounds.bottom - mStrokeWidth / 2 - 0.5f;
        }
    }

    @Override
    public void start() {
        if (isRunning)
            return;
        angleAnimator.start();
        sweepAngleAnimator.start();
        isRunning = true;
    }

    @Override
    public void stop() {
        if (!isRunning)
            return;
        angleAnimator.cancel();
        sweepAngleAnimator.cancel();
        isRunning = false;
    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }

    @Override
    public void setAlpha(int alpha) {
        if (mPaint != null) {
            mPaint.setAlpha(alpha);
            invalidateSelf();
        }
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        if (mPaint != null) {
            mPaint.setColorFilter(colorFilter);
            invalidateSelf();
        }
    }

    @Override
    public int getOpacity() {
        if (mPaint != null) {
            if (mPaint.getXfermode() == null) {
                final int alpha = mPaint.getAlpha();
                if (alpha == 0) {
                    return PixelFormat.TRANSPARENT;
                }
                if (alpha == 255) {
                    return PixelFormat.OPAQUE;
                }
            }
        }
        return PixelFormat.TRANSLUCENT;
    }
}
