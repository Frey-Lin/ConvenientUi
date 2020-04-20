package com.scorpio.ui.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Scroller;

import androidx.annotation.Nullable;


import java.io.IOException;
import java.io.InputStream;

public class BigImageView extends View {

    /**
     * 图片加载区域
     */
    private Rect rect;

    /**
     * 处理滑动，计算滑动距离
     */
    private Scroller scroller;

    /**
     * 手势检测
     */
    private GestureDetector gestureDetector;

    /**
     * 手势监听
     */
    private GestureDetector.OnGestureListener gestureListener;

    private int viewWidth;

    private int viewHeight;

    private int imageWidth;

    private int imageHeight;
    /**
     * 图片解析矩形区域的高度
     */
    private int rectHeight;

    private BitmapFactory.Options options;

    private BitmapRegionDecoder bitmapRegionDecoder;
    /**
     * 缩放比，view的宽度比图片宽度
     */
    private float scale;

    private Bitmap bitmap;
    /**
     * 图片缩放矩阵
     */
    private Matrix matrix;

    public BigImageView(Context context) {
        this(context, null);
    }

    public BigImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BigImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        rect = new Rect();
        matrix = new Matrix();
        scroller = new Scroller(context);
        gestureListener = new GestureListener();
        gestureDetector = new GestureDetector(context, gestureListener);
    }

    public void setInput(InputStream input) {
        options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(input, null, options);
        imageWidth = options.outWidth;
        imageHeight = options.outHeight;
        options.inJustDecodeBounds = false;
        //开启复用功能
        options.inMutable = true;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        try {
            //创建区域解码器
            bitmapRegionDecoder = BitmapRegionDecoder.newInstance(input, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //触发布局重绘
        requestLayout();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        options.inBitmap = bitmap;
        bitmap = bitmapRegionDecoder.decodeRegion(rect, options);
        matrix.reset();
        matrix.setScale(scale, scale);
        canvas.drawBitmap(bitmap, matrix, null);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        viewWidth = w;
        viewHeight = h;
        rect.left = 0;
        rect.top = 0;
        rect.right = imageWidth;
        scale = (float) viewWidth / imageWidth;
        rectHeight = (int) ((float) viewHeight / scale);
        rect.bottom = rectHeight;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    class GestureListener extends GestureDetector.SimpleOnGestureListener {
        //手指按下
        @Override
        public boolean onDown(MotionEvent e) {
            //手指按下时，如果滑动没结束，强制结束滑动
            if (!scroller.isFinished()) {
                scroller.forceFinished(true);
            }
            return true;
        }

        //手指滑动
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            rect.offset(0, (int) distanceY);
            if (rect.top < 0) {
                rect.top = 0;
                rect.bottom = rectHeight;
            }

            if (rect.bottom > imageHeight) {
                rect.bottom = imageHeight;
                rect.top = imageHeight - rectHeight;
            }
            invalidate();
            return false;
        }

        //手指松开后惯性滑动
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            scroller.fling(0, rect.top,
                    (int) velocityX, -(int) velocityY,
                    0, 0,
                    0, imageHeight - rectHeight);
            return false;
        }
    }

    @Override
    public void computeScroll() {
        if (scroller.isFinished()) {
            return;
        }

        if (scroller.computeScrollOffset()) {
            rect.top = scroller.getCurrY();
            rect.bottom = rect.top + rectHeight;
            invalidate();
        }
    }
}
