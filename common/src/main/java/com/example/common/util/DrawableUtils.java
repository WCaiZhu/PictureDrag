package com.example.common.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;

import androidx.annotation.ColorRes;
import androidx.core.content.ContextCompat;

/**
 * Drawable帮助类
 * @author Wuczh
 * @date 2022/1/12
 */

public class DrawableUtils {

    /**
     * 用颜色创建Drawable
     * @param context 上下文
     * @param color 颜色
     */
    public static ColorDrawable createColorDrawable(Context context, @ColorRes int color){
        return new ColorDrawable(ContextCompat.getColor(context, color));
    }

    /**
     * 用Bitmap创建Drawable
     * @param context 上下文
     * @param bitmap Bitmap
     */
    public static BitmapDrawable createBitmapDrawable(Context context, Bitmap bitmap){
        return new BitmapDrawable(context.getResources(), bitmap);
    }
}
