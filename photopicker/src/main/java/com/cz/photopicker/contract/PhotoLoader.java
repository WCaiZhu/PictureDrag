package com.cz.photopicker.contract;

import android.content.Context;
import android.widget.ImageView;

/**
 * 预览图片加载接口
 * @author Wuczh
 * @date 2022/1/12
 */

public interface PhotoLoader<T> {
    void displayImg(Context context, T source, ImageView imageView);
}
