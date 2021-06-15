package com.scorpio.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.scorpio.ui.R;


/**
 * Created by feng on 2017/6/12.
 */

public class SurperRadioGroup extends LinearLayout {
    private int selectedId = -1;
    private OnCheckedChangeListener checkedChangeListener;

    public SurperRadioGroup(Context context) {
        this(context, null);
    }

    public SurperRadioGroup(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SurperRadioGroup(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray attributes = context.obtainStyledAttributes(
                attrs, R.styleable.SurperRadioGroup);
        int value = attributes.getResourceId(R.styleable.SurperRadioGroup_selectedId, View.NO_ID);
        if (value != -1) {
            selectedId = value;
        }
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (selectedId != -1) {
            View selectedView = findViewById(selectedId);
            if (selectedView != null) {
                selectedView.setSelected(true);
            }
        }
        addClickListener(child);
        super.addView(child, index, params);
    }

    private void addClickListener(final View child) {
        if (child != null) {
            child.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
//                    if (v.isSelected()) {
//                        return;
//                    }
                    if (checkedChangeListener != null) {
                        checkedChangeListener.onCheckedChanged(SurperRadioGroup.this, child.getId());
                    }
                    int count = getChildCount();
                    for (int i = 0; i < count; i++) {
                        View child = getChildAt(i);
                        if (child.getId() != v.getId()) {  // 排除摄像头
                            cleanSelectStatus(child);
                        }
                    }
                    setSelected(v, !v.isSelected());
                }
            });
        }
    }

    private void cleanSelectStatus(View v) {
        setSelected(v, false);
    }

    private void cleanAllSelect() {
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            cleanSelectStatus(child);
        }
    }

    private void setSelected(View view, boolean selected) {
        selectedId = view.getId();
        view.setSelected(selected);
        if (view instanceof ViewGroup) {
            int count = ((ViewGroup) view).getChildCount();
            for (int i = 0; i < count; i++) {
                View child = ((ViewGroup) view).getChildAt(i);
                if (child instanceof ViewGroup) {
                    setSelected(child, selected);
                } else {
                    child.setSelected(selected);
                }
            }
        }
    }

    public void setSelectedId(int selectedId) {
        if (findViewById(selectedId) != null) {
            this.selectedId = selectedId;
        }
        cleanAllSelect();
        setSelected(findViewById(selectedId), true);
    }

    public int getSelectedId() {
        return selectedId;
    }

    public void setCheckedChangeListener(OnCheckedChangeListener checkedChangeListener) {
        this.checkedChangeListener = checkedChangeListener;
    }

    public interface OnCheckedChangeListener {
        void onCheckedChanged(SurperRadioGroup group, @IdRes int checkedId);
    }
}
