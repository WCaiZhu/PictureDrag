package com.cz.photopicker.picker.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cz.photopicker.R;
import com.cz.photopicker.contract.PhotoLoader;
import com.cz.photopicker.databinding.DialogPopImgFolderBinding;
import com.cz.photopicker.picker.PickerUIConfig;
import com.example.common.util.ArrayUtils;

import java.util.List;

/**
 * 图片文件弹框
 * @author Wuczh
 * @date 2022/1/12
 */

public class ImageFolderDialog extends Dialog {

    private DialogPopImgFolderBinding mBinding;

    private RecyclerView mRecyclerView;

    private ImageFolderAdapter mAdapter;

    private Listener mListener;

    public ImageFolderDialog(Context context) {
        super(context, R.style.BaseDialog);
        mBinding = DialogPopImgFolderBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        setWindowAnimations();
        initRecyclerView();
        setListeners();
    }

    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        mAdapter = new ImageFolderAdapter(getContext());
        mBinding.recyclerView.setLayoutManager(layoutManager);
        mBinding.recyclerView.setHasFixedSize(true);
        mBinding.recyclerView.setAdapter(mAdapter);
    }

    public void setPhotoLoader(PhotoLoader<String> photoLoader) {
        mAdapter.setPhotoLoader(photoLoader);
    }

    public void setData(List<ImageFolderItemBean> list) {
        if (ArrayUtils.isEmpty(list)) {
            mBinding.recyclerView.setVisibility(View.GONE);
            return;
        }
        mBinding.recyclerView.setVisibility(View.VISIBLE);
        mAdapter.setData(list);
        mBinding.recyclerView.smoothScrollToPosition(0);
        mAdapter.notifyDataSetChanged();
    }

    public void setPickerUIConfig(PickerUIConfig config) {
        mAdapter.setPickerUIConfig(config);
    }

    protected void setListeners() {
        mAdapter.setOnItemClickListener((viewHolder, item, position) -> {
            if (mListener != null) {
                mListener.onSelected(ImageFolderDialog.this, item);
            }
        });
    }

    public void setListener(Listener listener) {
        mListener = listener;
    }

    public interface Listener {
        void onSelected(DialogInterface dialog, ImageFolderItemBean bean);
    }


    /**
     * 设置窗口弹出动画
     */
    private void setWindowAnimations() {
        if (getWindow() != null) {
            getWindow().setWindowAnimations(R.style.animation_top_in_top_out); //设置窗口弹出动画
        }
    }
}
