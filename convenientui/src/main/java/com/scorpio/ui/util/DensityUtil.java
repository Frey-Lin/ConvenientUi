package com.scorpio.ui.util;

import android.content.Context;

/**
 * 像素单位转换工具类
 *
 * @author feng
 */
public class DensityUtil {

    /**
     * dp转换成px
     *
     * @param context
     * @param dpValue
     * @return
     */
    public static float dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return dpValue * scale + 0.5f;
    }

    /**
     * px转换成dp
     *
     * @param context
     * @param pxValue
     * @return
     */
    public static float px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return pxValue / scale + 0.5f;
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue
     * @return
     */
    public static float sp2px(Context context, float spValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return spValue * scale + 0.5f;
    }

    // 将px值转换为sp值，保证文字大小不变
    public static float px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return pxValue / fontScale + 0.5f;
    }
}
