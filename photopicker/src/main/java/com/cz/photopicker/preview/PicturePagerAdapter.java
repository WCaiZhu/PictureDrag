package com.cz.photopicker.preview;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cz.photopicker.contract.PhotoLoader;
import com.cz.photopicker.photoview.PhotoView;
import com.cz.photopicker.photoview.PhotoViewAttacher;
import com.example.common.adapter.BaseRecyclerViewAdapter;

/**
 * 图片翻页适配器
 * @author Wuczh
 * @date 2022/1/12
 */

class PicturePagerAdapter extends BaseRecyclerViewAdapter<Object> {

    /** 是否缩放 */
    private boolean isScale;
    /** 图片加载器 */
    private PhotoLoader<Object> mPhotoLoader;

    /**
     * 图片翻页适配器
     * @param context 上下文
     * @param isScale 是否缩放
     * @param photoLoader 图片加载器
     */
     PicturePagerAdapter(Context context, boolean isScale, PhotoLoader<Object> photoLoader) {
        super(context);
        this.isScale = isScale;
        mPhotoLoader = photoLoader;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        FrameLayout frameLayout = new FrameLayout(parent.getContext());
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        frameLayout.setLayoutParams(layoutParams);
        return new DataViewHolder(frameLayout);
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull RecyclerView.ViewHolder viewHolder) {
        super.onViewDetachedFromWindow(viewHolder);
        if (isScale && viewHolder instanceof DataViewHolder) {
            DataViewHolder holder = (DataViewHolder) viewHolder;
            if (holder.photoImg instanceof PhotoView) {
                PhotoView photoView = (PhotoView) holder.photoImg;
                PhotoViewAttacher attacher = photoView.getAttacher();
                attacher.update();//离开屏幕后还原缩放
            }
        }
    }

    @Override
    public void onBind(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof DataViewHolder){
            showItem((DataViewHolder) holder, position);
        }
    }

    private void showItem(DataViewHolder holder, int position) {
        if (mPhotoLoader != null){
            mPhotoLoader.displayImg(getContext(), getItem(position), holder.photoImg);
        }
    }

    class DataViewHolder extends RecyclerView.ViewHolder{

        ImageView photoImg;

        private DataViewHolder(ViewGroup itemView) {
            super(itemView);
            photoImg = isScale ? new PhotoView(itemView.getContext()) : new ImageView(itemView.getContext());
            itemView.addView(photoImg, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            photoImg.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        }
    }

    /** 释放资源 */
    void release(){
        mPhotoLoader = null;
    }
}