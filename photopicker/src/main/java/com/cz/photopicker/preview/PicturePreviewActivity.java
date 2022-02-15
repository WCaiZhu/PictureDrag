package com.cz.photopicker.preview;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cz.photopicker.contract.preview.PreviewController;
import com.cz.photopicker.databinding.ActivityPreviewLayoutBinding;
import com.cz.photopicker.util.ViewPagerSnapHelper;
import com.example.common.util.ArrayUtils;
import com.example.common.util.DeviceUtils;

/**
 * 图片预览页面
 * @author Wuczh
 * @date 2022/1/12
 */

public class PicturePreviewActivity extends AppCompatActivity {

    private ActivityPreviewLayoutBinding mBinding;

    /**
     * 启动页面
     *
     * @param context 上下文
     */
    public static void start(Context context) {
        Intent starter = new Intent(context, PicturePreviewActivity.class);
        context.startActivity(starter);
    }

    /**
     * 图片数据
     */
    private PreviewBean mPreviewBean;
    /**
     * 适配器
     */
    private PicturePagerAdapter mAdapter;
    /**
     * 滑动帮助类
     */
    private ViewPagerSnapHelper mSnapHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityPreviewLayoutBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        mPreviewBean = PreviewManager.sPreviewBean;
        initRecyclerView();
        initData();
        setListeners();
    }

    @SuppressWarnings("unchecked")
    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        mAdapter = new PicturePagerAdapter(this, mPreviewBean.isScale, mPreviewBean.photoLoader);
        mBinding.recyclerView.setLayoutManager(layoutManager);
        mBinding.recyclerView.setHasFixedSize(true);
        mBinding.recyclerView.setAdapter(mAdapter);
        mSnapHelper = new ViewPagerSnapHelper(mPreviewBean.showPosition);
        mSnapHelper.attachToRecyclerView(mBinding.recyclerView);
    }

    protected void initData() {
        mBinding.rootView.setBackgroundColor(ContextCompat.getColor(this, mPreviewBean.backgroundColor));
        DeviceUtils.setStatusBarColor(this, getWindow(), mPreviewBean.statusBarColor);
        DeviceUtils.setNavigationBarColorRes(this, getWindow(), mPreviewBean.navigationBarColor);
        setPagerNum(mPreviewBean.showPosition);
        mBinding.pagerTips.setVisibility(mPreviewBean.isShowPagerText ? View.VISIBLE : View.GONE);
        mBinding.pagerTips.setTextColor(ContextCompat.getColor(this, mPreviewBean.pagerTextColor));
        mBinding.pagerTips.setTextSize(TypedValue.COMPLEX_UNIT_SP, mPreviewBean.pagerTextSize);

        mAdapter.setData(mPreviewBean.sourceList);
        mAdapter.notifyDataSetChanged();
        mBinding.recyclerView.scrollToPosition(mPreviewBean.showPosition);
    }

    protected void setListeners() {

        mSnapHelper.setOnPageChangeListener(position -> setPagerNum(position));

        mAdapter.setOnItemClickListener((viewHolder, item, position) -> {
            if (mPreviewBean.clickListener != null) {
                mPreviewBean.clickListener.onClick(viewHolder.itemView.getContext(), item, position, mPreviewController);
            }
        });

        mAdapter.setOnItemLongClickListener((viewHolder, item, position) -> {
            if (mPreviewBean.longClickListener != null) {
                mPreviewBean.longClickListener.onLongClick(viewHolder.itemView.getContext(), item, position, mPreviewController);
            }
        });
    }

    private PreviewController mPreviewController = () -> finish();

    /**
     * 设置页码
     */
    private void setPagerNum(int position) {
        mBinding.pagerTips.setText(new StringBuffer().append(position + 1).append(" / ").append(ArrayUtils.getSize(mPreviewBean.sourceList)));
    }

    @Override
    public void finish() {
        super.finish();
        mAdapter.release();
        PreviewManager.sPreviewBean.clear();
        mPreviewBean.clear();
    }
}
