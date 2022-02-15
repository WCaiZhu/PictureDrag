package com.example.picturedrag.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.cz.photopicker.preview.PreviewManager;
import com.example.common.util.ArrayUtils;
import com.example.common.util.DensityUtils;
import com.example.common.util.FileUtils;
import com.example.common.util.ScreenUtils;
import com.example.picturedrag.R;
import com.example.picturedrag.adapter.OnNineGridViewListener;
import com.example.picturedrag.adapter.PhotoPublishAdapter;
import com.example.picturedrag.databinding.ActivityDragBinding;
import com.example.picturedrag.dialog.PhotoHandleTypeDialog;
import com.example.picturedrag.util.ItemTouchHelperCallback;
import com.example.picturedrag.util.PhotoUtil;
import com.lxj.xpermission.PermissionConstants;
import com.lxj.xpermission.XPermission;

import java.util.ArrayList;
import java.util.List;

public class PicDragActivity extends AppCompatActivity {
    /**
     * 适配器
     */
    private PhotoPublishAdapter mAdapter;
    /**
     * 数据列表
     */
    private final ArrayList<String> mDataList = new ArrayList<>();

    /**
     * 拖拽排序删除
     */
    private ItemTouchHelperCallback myCallBack;

    private ItemTouchHelper mItemTouchHelper;
    /**
     * 拍照和选择图片的相关操作
     */
    private PhotoUtil mPhotoUtil;

    private ActivityDragBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityDragBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        mPhotoUtil = new PhotoUtil();
        initRecyclerView();
        fixBottom();
        setListeners();
    }

    /**
     * 初始化RecyclerView
     */
    private void initRecyclerView() {
        mAdapter = new PhotoPublishAdapter(PicDragActivity.this);
        mAdapter.setMaxPic(15);
        myCallBack = new ItemTouchHelperCallback(false, mDataList, mBinding.scrollView, mAdapter);
        mItemTouchHelper = new ItemTouchHelper(myCallBack);//实现化ItemTouchHelper(拖拽和滑动删除的过程中会回调ItemTouchHelper.Callback的相关方法)
        mItemTouchHelper.attachToRecyclerView(mBinding.picRv);//关联对应的 RecyclerView
        mBinding.picRv.setAdapter(mAdapter);
        mAdapter.setData(mDataList);
    }

    /**
     * 处理recyclerView下面的布局
     */
    private void fixBottom() {
        int row = mAdapter.getItemCount() / 3;
        row = (0 == mAdapter.getItemCount() % 3) ? row : row + 1;//少于3为1行
        int screenWidth = ScreenUtils.getScreenWidth(PicDragActivity.this);
        int itemHeight = (screenWidth - DensityUtils.dp2px(PicDragActivity.this, 40)) / 3;
        int editHeight = mBinding.contentEdit.getHeight();
        editHeight = editHeight == 0 ? DensityUtils.dp2px(PicDragActivity.this, 130) : mBinding.contentEdit.getHeight();
        int layoutMargin = DensityUtils.dp2px(PicDragActivity.this, 20);//距离上部应用的间隔
        int marginTop = itemHeight * row + editHeight + layoutMargin;//+ itemSpace * (row - 1)
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mBinding.bottomLl.getLayoutParams();
        params.setMargins(0, marginTop, 0, 0);
        mBinding.bottomLl.setLayoutParams(params);
    }


    /**
     * 点击
     */
    protected void setListeners() {
        mAdapter.setOnNineGridViewListener(new OnNineGridViewListener() {

            @Override
            public void onDisplayImg(@NonNull Context context, @NonNull String
                    source, @NonNull ImageView imageView) {
                Glide.with(context)
                        .load(FileUtils.filePathToUri(context, source))
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .centerCrop()
                        .into(imageView);
            }

            //删除图片
            @Override
            public void onDeletePic(@NonNull String picInfo, int position) {
            }

            //点击图片后看大图
            @Override
            public void onClickPic(@NonNull String picInfo, int position) {
                PreviewManager.<String>create()
                        .setPosition(position)
                        .setBackgroundColor(R.color.black)
                        .setStatusBarColor(R.color.black)
                        .setNavigationBarColor(R.color.black)
                        .setPagerTextColor(R.color.white)
                        .setPagerTextSize(14)
                        .setShowPagerText(true)
                        .setImgLoader((context, source, imageView) -> Glide.with(context)
                                .load(source)
                                .centerCrop()
                                .into(imageView))
                        .build(getPicData())
                        .open(PicDragActivity.this);
            }

            @Override
            public void onLongClickPic(RecyclerView.ViewHolder viewHolder, String data, int position) {
                if (viewHolder.getLayoutPosition() != mDataList.size()) {
                    mItemTouchHelper.startDrag(viewHolder);//viewHolder开始拖动
                }
            }

            //点击+号按钮
            @Override
            public void onAddPic(int addCount) {
                addBut();
            }
        });
        //滑动状态回调处理
        myCallBack.setDragListener(new ItemTouchHelperCallback.DragListener() {
            @Override
            public void deleteState(boolean delete) {
                //根据是否是删除状态，显示对应的删除区域的文字和背景色
                if (delete) {
                    mBinding.deleteAreaView.setAlpha(0.9f);
                    mBinding.deleteAreaTv.setText(getString(R.string.loosen_delete_pic));
                } else {
                    mBinding.deleteAreaView.setAlpha(0.6f);
                    mBinding.deleteAreaTv.setText(R.string.drag_delete_pic);
                }
            }

            @Override
            public void dragState(boolean start) {
                //根据是否是开始滑动状态，来设置是否显示删除区域
                if (start) {
                    mBinding.deleteAreaView.setVisibility(View.VISIBLE);
                } else {
                    mBinding.deleteAreaView.setVisibility(View.GONE);
                }
            }

            @Override
            public void deleteOk() {
                //删除后重新计算图片选择数量

            }

            @Override
            public void clearView() {
                //item删除后需要重新计算底部区域的显示位置，否则会造成底部区域显示混乱
                fixBottom();
            }
        });
        //发表完反，清空对应的内容和图片
        mBinding.publishTv.setOnClickListener(v -> {
            mBinding.contentEdit.setText("");
            clearData();
            Toast.makeText(PicDragActivity.this, R.string.publish_success, Toast.LENGTH_SHORT).show();
        });
        //所在位置
        mBinding.locationTv.setOnClickListener(v -> Toast.makeText(PicDragActivity.this, R.string.location, Toast.LENGTH_SHORT).show());
        //提醒谁看
        mBinding.remindWhoToSeeTv.setOnClickListener(v -> Toast.makeText(PicDragActivity.this, R.string.remind_who_to_see, Toast.LENGTH_SHORT).show());
        //谁可以看
        mBinding.whoCanSeeTv.setOnClickListener(v -> Toast.makeText(PicDragActivity.this, R.string.who_can_see, Toast.LENGTH_SHORT).show());
    }


    /**
     * 显示选择照片类型弹框
     *
     * @param addCount 还可添加的图片数量
     */
    private void showHandleTypeDialog(int addCount) {
        PhotoHandleTypeDialog dialog = new PhotoHandleTypeDialog(PicDragActivity.this);
        dialog.setTakePhotoListener((dialog1, which) -> {
            mPhotoUtil.takePhoto(PicDragActivity.this);
            dialog1.dismiss();
        });
        dialog.setTakeAlbumListener((dialog12, which) -> {
            getAlbun();
            dialog12.dismiss();
        });
        dialog.setTakeAlbumListener((dialog12, which) -> mPhotoUtil.takeAlbum(PicDragActivity.this, addCount));
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        //拍照和选择图片的回调
        mPhotoUtil.setListener(new PhotoUtil.OnPhotoListener() {
            @Override
            public void takePhoto(List<String> lis) {
                addData(lis);
            }

            @Override
            public void takeAlbum(List<String> lis) {
                addData(lis);
            }
        });
    }

    /**
     * 从本地相册获取图片
     */
    private void getAlbun() {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, 2);
    }


    /**
     * 添加数据
     */
    public void addData(@NonNull List<String> data) {
        if (mAdapter == null) {
            return;
        }
        // 判断添加的数据长度和已有数据长度之和是否超过总长度
        int length = (data.size() + mDataList.size()) > mAdapter.getMaxPic() ? (mAdapter.getMaxPic() - mDataList.size()) : data.size();
        for (int i = 0; i < length; i++) {
            mDataList.add(data.get(i));
        }
        mAdapter.setData(mDataList);
        mAdapter.notifyDataSetChanged();
        fixBottom();
    }

    /**
     * 删除数据
     */
    public void clearData() {
        if (mAdapter == null) {
            return;
        }
        if (ArrayUtils.isEmpty(mDataList)) {
            return;
        }
        mDataList.clear();
        mAdapter.notifyDataSetChanged();
        fixBottom();
    }

    /**
     * 获取图片数据
     */
    public List<String> getPicData() {
        if (mDataList == null) {
            return new ArrayList<>();
        }
        return mDataList;
    }

    /**
     * 加号按钮点击时先判断是否有开启对应的权限
     */
    @SuppressLint("WrongConstant")
    public void addBut() {
        String[] permissions = new String[]{PermissionConstants.CAMERA, PermissionConstants.STORAGE};

        XPermission.create(this, permissions).callback(new XPermission.SimpleCallback() {
            @Override
            public void onGranted() {
                int count = mAdapter.getMaxPic() - getPicData().size();
                showHandleTypeDialog(count);
            }

            @Override
            public void onDenied() {
                Toast.makeText(PicDragActivity.this, "没有权限，无法使用该功能", Toast.LENGTH_SHORT).show();
            }
        }).request();
    }
}