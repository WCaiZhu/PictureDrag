package com.example.picturedrag.util;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.cz.photopicker.picker.PickerManager;
import com.cz.photopicker.picker.PickerUIConfig;
import com.example.common.util.ArrayUtils;
import com.example.common.util.FileUtils;
import com.example.picturedrag.config.Constant;

import java.util.List;

/**
 * 拍照和选择图片的相关操作
 *
 * @author Wuczh
 * @date 2021/11/29
 */
public class PhotoUtil {
    /**
     * 图片保存地址
     */
    private String mCameraSavePath = FileManager.getCacheFolderPath();

    /**
     * FileProvider名字
     */
    private String mAuthority = Constant.FileProvider.AUTHORITIES;

    /**
     * 从相册中选择
     *
     * @param addCount
     */
    public void takeAlbum(Context context, int addCount) {
        PickerManager
                .create()
                .setImgLoader((context1, source, imageView) -> Glide.with(context1)
                        .load(FileUtils.filePathToUri(context1, source))
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .centerCrop()
                        .into(imageView))
                .setPreviewImgLoader((context12, source, imageView) -> Glide.with(context12)
                        .load(FileUtils.filePathToUri(context12, source))
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .centerCrop()
                        .into(imageView))
                .setOnPhotoPickerListener(photos -> {
                    if (ArrayUtils.isEmpty(photos)) {
                        return;
                    }
                    if (mListener != null) {
                        mListener.takeAlbum(photos);
                    }
                })
                .setMaxCount(addCount)
                .setNeedCamera(false)
                .setNeedItemPreview(true)
                .setPickerUIConfig(PickerUIConfig.createDefault())
                .setCameraSavePath(mCameraSavePath)
                .setAuthority(mAuthority)
                .build()
                .open(context);
    }

    /**
     * 拍照
     */
    public void takePhoto(Context context) {
        PickerManager
                .create()
                .setPreviewImgLoader((context1, source, imageView) -> Glide.with(context1)
                        .load(FileUtils.filePathToUri(context1, source))
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .centerCrop()
                        .into(imageView))
                .setOnPhotoPickerListener(photos -> {
                    if (!ArrayUtils.isEmpty(photos)) {
//                        PicInfo picInfo = new PicInfo(photos.get(0), FileUtils.filePathToUri(context, photos.get(0)));
//                        ArrayList<PicInfo> lis = new ArrayList<>();
//                        lis.add(picInfo);
                        if (mListener != null) {
                            mListener.takePhoto(photos);
                        }
                    }
                })
                .setCameraSavePath(mCameraSavePath)
                .setAuthority(mAuthority)
                .build()
                .takePhoto(context, true);
    }

    private OnPhotoListener mListener;

    /**
     * 设置监听
     *
     * @param listener
     */
    public void setListener(OnPhotoListener listener) {
        mListener = listener;
    }

    public interface OnPhotoListener {
        /**
         * 拍照返回的图片信息
         */
        void takePhoto(List<String> lis);

        /**
         * 选中返回的图片信息
         */
        void takeAlbum(List<String> lis);

    }
}
