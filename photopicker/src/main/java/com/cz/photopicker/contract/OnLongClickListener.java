package com.cz.photopicker.contract;

import android.content.Context;

import com.cz.photopicker.contract.preview.PreviewController;


/**
 * 长按事件
 * @author Wuczh
 * @date 2022/1/12
 */

public interface OnLongClickListener<T> {
    void onLongClick(Context context, T source, int position, PreviewController controller);
}
