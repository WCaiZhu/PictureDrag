package com.example.common.util;

import android.content.Context;
import android.util.TypedValue;

/**
 * 单位转换类
 * @author Wuczh
 * @date 2022/1/22
 */
public class DensityUtils {

    /**
     * dp转px
     * @param context 上下文
     * @param dpValue dp值
     */
    public static int dp2px(Context context, float dpValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, context.getResources().getDisplayMetrics());
    }

    /**
     * dp转px
     * @param context 上下文
     * @param dpValue dp值
     */
    public static float dp2pxFloat(Context context, float dpValue) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, context.getResources().getDisplayMetrics());
    }

    /**
     * sp转px
     * @param context 上下文
     * @param spValue sp值
     */
    public static int sp2px(Context context, float spValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spValue, context.getResources().getDisplayMetrics());
    }

    /**
     * sp转px
     * @param context 上下文
     * @param spValue sp值
     */
    public static float sp2pxFloat(Context context, float spValue) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spValue, context.getResources().getDisplayMetrics());
    }

    /**
     * px转dp
     * @param context 上下文
     * @param pxValue px值
     */
    public static float px2dp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (pxValue / scale);
    }

    /**
     * px转sp
     * @param context 上下文
     * @param pxValue px值
     */
    public static float px2sp(Context context, float pxValue) {
        return (pxValue / context.getResources().getDisplayMetrics().scaledDensity);
    }

}
