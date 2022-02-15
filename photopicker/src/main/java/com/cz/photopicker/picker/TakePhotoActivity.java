package com.cz.photopicker.picker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.cz.photopicker.R;
import com.cz.photopicker.databinding.ActivityTakePhotoLayoutBinding;
import com.cz.photopicker.util.album.AlbumUtils;
import com.example.common.util.DateUtils;
import com.example.common.util.DeviceUtils;
import com.example.common.util.FileUtils;
import com.example.common.util.ToastUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 拍照页面
 * @author Wuczh
 * @date 2022/1/12
 */

public class TakePhotoActivity extends AppCompatActivity {

    /**
     * 照相请求码
     */
    private static final int REQUEST_CAMERA = 777;
    /**
     * 选择数据
     */
    private PickerBean mPickerBean;
    /**
     * 临时文件路径
     */
    private String mTempFilePath = "";

    private ActivityTakePhotoLayoutBinding mBinding;

    /**
     * 启动页面
     *
     * @param context 上下文
     */
    public static void start(Context context) {
        Intent starter = new Intent(context, TakePhotoActivity.class);
        context.startActivity(starter);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityTakePhotoLayoutBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        mPickerBean = PickerManager.sPickerBean;
        DeviceUtils.setStatusBarColor(this, getWindow(), android.R.color.black);
        DeviceUtils.setNavigationBarColorRes(this, getWindow(), android.R.color.black);
        setListeners();
        takePhoto();
    }

    protected void setListeners() {

        // 取消按钮
        mBinding.cancelBtn.setOnClickListener(v -> handleCameraCancel());

        // 确定按钮
        mBinding.confirmBtn.setOnClickListener(v -> handleConfirm());
    }

    /**
     * 拍照
     */
    private void takePhoto() {
        if (!FileUtils.isFileExists(mPickerBean.cameraSavePath) && !FileUtils.createFolder(mPickerBean.cameraSavePath)) {
            ToastUtils.showShort(this, R.string.component_photo_folder_fail);
            return;
        }

        mTempFilePath = mPickerBean.cameraSavePath + "P_" + DateUtils.getCurrentFormatString(DateUtils.TYPE_4) + ".jpg";
        if (!FileUtils.createNewFile(mTempFilePath)) {
            ToastUtils.showShort(this, R.string.component_photo_temp_file_fail);
            return;
        }

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) == null) {
            ToastUtils.showShort(this, R.string.component_no_camera);
            return;
        }
        // 设置系统相机拍照后的输出路径
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(this, mPickerBean.authority, FileUtils.createFile(mTempFilePath)));
            } catch (Exception e) {
                e.printStackTrace();
                ToastUtils.showShort(this, R.string.component_photo_temp_file_fail);
                return;
            }
        } else {
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(FileUtils.createFile(mTempFilePath)));
        }
        startActivityForResult(cameraIntent, REQUEST_CAMERA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CAMERA) {
            if (resultCode == Activity.RESULT_OK) {
                AlbumUtils.notifyScanImageCompat(this, mTempFilePath);// 更新相册
                if (mPickerBean.isImmediately) {
                    handleConfirm();
                } else {
                    new Handler().postDelayed(() -> handleCameraSuccess(), 300);
                }
                return;
            }
            handleCameraCancel();
        }
    }

    /**
     * 处理确认照片
     */
    private void handleConfirm() {
        List<String> list = new ArrayList<>();
        list.add(mTempFilePath);
        if (mPickerBean.photoPickerListener != null) {
            mPickerBean.photoPickerListener.onPickerSelected(list);
        }
        finish();
    }

    /**
     * 处理拍照成功
     */
    private void handleCameraSuccess() {
        if (mPickerBean.previewLoader != null) {
            mPickerBean.previewLoader.displayImg(this, mTempFilePath, mBinding.photoImg);
        }
    }

    /**
     * 处理拍照取消
     */
    private void handleCameraCancel() {
        if (!TextUtils.isEmpty(mTempFilePath)) {
            FileUtils.deleteFile(mTempFilePath);// 删除临时文件
        }
        if (mPickerBean.photoPickerListener != null) {
            mPickerBean.photoPickerListener.onPickerSelected(new ArrayList<String>());
        }
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        mTempFilePath = "";
        PickerManager.sPickerBean.clear();
        mPickerBean.clear();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        handleCameraCancel();
    }
}
