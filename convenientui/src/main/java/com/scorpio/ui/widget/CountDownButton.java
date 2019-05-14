package com.scorpio.ui.widget;


import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.AppCompatButton;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.scorpio.ui.R;


/**
 * @author Frey-Lin
 */

public class CountDownButton extends AppCompatButton {

    private final int STATUS_BEFORE_COUNTDOWN = 1;
    private final int STATUS_COUNTDOWNING = 2;
    private final int STATUS_END_COUNTDOWN = 3;

    private int currentStatus = STATUS_BEFORE_COUNTDOWN;
    private int currentNum;
    private int startNum;
    private int endNum;
    private String startText, endText, unit;
    private onCountDownEndListener listener;

    private Handler handler = new Handler(Looper.getMainLooper());

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (currentNum <= endNum) {
                endCountDown();
                return;
            }
            setText(--currentNum + unit);
            handler.postDelayed(this, 1000);
        }
    };

    private void endCountDown() {
        currentStatus = STATUS_END_COUNTDOWN;
        setText(endText);
        if (listener != null) {
            listener.onCountDownEnd(startNum, endNum);
        }
        setClickable(true);
    }

    public CountDownButton(Context context) {
        this(context, null);
    }

    public CountDownButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CountDownButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CountDownButton);
        startNum = typedArray.getInt(R.styleable.CountDownButton_startNum, 60);
        endNum = typedArray.getInt(R.styleable.CountDownButton_endNum, 0);
        unit = typedArray.getString(R.styleable.CountDownButton_unit);
        if(TextUtils.isEmpty(unit)){
            unit = "s";
        }
        currentNum = startNum;
        if (startNum < endNum) {
            throw new IllegalArgumentException("startnum must not small then endnum");
        }
        startText = typedArray.getString(R.styleable.CountDownButton_startText);
        endText = typedArray.getString(R.styleable.CountDownButton_endText);
        setText(startText);
        typedArray.recycle();
    }

    public void startCountDown() {
        if (currentStatus == STATUS_BEFORE_COUNTDOWN || currentStatus == STATUS_END_COUNTDOWN) {
            setClickable(false);
            currentNum = startNum;
            handler.post(runnable);
            currentStatus = STATUS_COUNTDOWNING;
        }
    }

    public void stop() {
        handler.removeCallbacks(runnable);
        endCountDown();
    }

    public void setCountDownEndListener(onCountDownEndListener listener) {
        this.listener = listener;
    }

    public interface onCountDownEndListener {
        void onCountDownEnd(int startNum, int endNum);
    }

}
