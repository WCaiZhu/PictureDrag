package com.cz.photopicker.bean;

import android.net.Uri;

/**
 * 图片信息实体
 *
 * @author Wuczh
 * @date 2022/1/22
 */
public class PicInfo {

    public String path;
    public Uri uri;

    public PicInfo(String path, Uri uri) {
        this.path = path;
        this.uri = uri;
    }
}
