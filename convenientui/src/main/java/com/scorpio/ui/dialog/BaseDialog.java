package com.scorpio.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.WindowManager;

import com.scorpio.ui.R;


public class BaseDialog extends Dialog {

    public BaseDialog(Context context) {
        this(context, R.style.MyDialog);
    }

    public BaseDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected BaseDialog(Context context, boolean cancelable, DialogInterface.OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public void setPosition(int x, int y) {
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.x = x;
        params.y = y;
        getWindow().setAttributes(params);
    }

    public void setGravity(int gravity) {
        getWindow().setGravity(gravity);
    }

    public void setSize(int width, int height) {
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = width;
        params.height = height;
        getWindow().setAttributes(params);
    }

}
