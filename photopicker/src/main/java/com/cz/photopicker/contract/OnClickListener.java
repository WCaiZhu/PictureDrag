package com.cz.photopicker.contract;

import android.content.Context;

import com.cz.photopicker.contract.preview.PreviewController;


/**
 * 点击事件
 * @author Wuczh
 * @date 2022/1/12
 */

public interface OnClickListener<T> {
    void onClick(Context context, T source, int position, PreviewController controller);
}
