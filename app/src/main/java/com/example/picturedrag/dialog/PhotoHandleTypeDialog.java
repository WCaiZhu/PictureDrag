package com.example.picturedrag.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.example.picturedrag.R;
import com.example.picturedrag.databinding.DialogTakePicBinding;


/**
 * 拍照类型选择弹窗
 *
 * @author Wuczh
 * @date 2021/8/23
 */
public class PhotoHandleTypeDialog extends Dialog {


    private DialogTakePicBinding mBinding;

    /**
     * 拍照
     */
    private DialogInterface.OnClickListener mTakePhotoListener = null;

    /**
     * 从相册选择图片
     */
    private DialogInterface.OnClickListener mTakeAlbumListener = null;

    /**
     * 取消
     */
    private DialogInterface.OnClickListener mCancelListener = null;

    public PhotoHandleTypeDialog(@NonNull Context context) {
        //重写dialog默认的主题(默认配置的属性背景宽度距离手机屏幕的左右两边各有16dp的间隔,要想去除边框距离，需重新定义主题)
        super(context,R.style.BaseDialog);
        mBinding = DialogTakePicBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        setWindowAnimations();
        setListeners();
    }

    /**
     * 设置窗口弹出动画
     */
    private void setWindowAnimations() {
        if (getWindow() != null) {
            getWindow().setWindowAnimations(R.style.animation_bottom_in_bottom_out); //设置窗口弹出动画
        }
    }

    protected void setListeners() {
        mBinding.takePhotoTv.setOnClickListener(v -> {
            if (mTakePhotoListener != null) {
                mTakePhotoListener.onClick(this, v.getId());
                dismiss();
            }
        });

        mBinding.cancelTv.setOnClickListener(v -> {
            dismiss();
            if (mCancelListener != null) {
                mCancelListener.onClick(this, v.getId());
            }
        });

        mBinding.chooseAlbumTv.setOnClickListener(v -> {
            if (mTakeAlbumListener != null) {
                mTakeAlbumListener.onClick(this, v.getId());
                dismiss();
            }
        });
    }

    /**
     * 设置拍照按钮点击
     *
     * @param listener 点击监听
     */
    public void setTakePhotoListener(DialogInterface.OnClickListener listener) {
        mTakePhotoListener = listener;
    }

    /**
     * 设置从相册选择图片按钮点击
     *
     * @param listener 点击监听
     */
    public void setTakeAlbumListener(DialogInterface.OnClickListener listener) {
        mTakeAlbumListener = listener;
    }

    /**
     * 设置取消按钮点击
     *
     * @param listener 点击监听
     */
    public void setCancelListener(DialogInterface.OnClickListener listener) {
        mCancelListener = listener;
    }

    @Override
    public void show() {
        Window window = getWindow();
        if (window != null) {
            window.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            window.setAttributes(layoutParams);
        }
        super.show();
    }

}
