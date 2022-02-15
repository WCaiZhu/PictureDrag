package com.example.picturedrag.adapter;

import android.content.Context;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;


/**
 * 九宫格回调接口
 *
 * @author Wuczh
 * @date 2022/1/12
 */
public interface OnNineGridViewListener {


    /**
     * 添加图片，可添加的数量[addCount]
     */
    void onAddPic(int addCount);

    /**
     * 展示图片，上下文[context]，数据[data]，控件[imageView]
     */
    void onDisplayImg(Context context, String data, ImageView imageView);

    /**
     * 删除图片，数据[data]，位置[position]
     */
    void onDeletePic(String data, int position);

    /**
     * 点击图片，数据[data]，位置[position]
     */
    void onClickPic(String data, int position);


    /**
     * 长按图片，数据[data]，位置[position]
     */
    void onLongClickPic(RecyclerView.ViewHolder viewHolder, String data, int position);
}
