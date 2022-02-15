package com.cz.photopicker.picker;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.annotation.ColorRes;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cz.photopicker.R;
import com.cz.photopicker.databinding.ActivityPickerLayoutBinding;
import com.cz.photopicker.picker.dialog.ImageFolderDialog;
import com.cz.photopicker.picker.dialog.ImageFolderItemBean;
import com.cz.photopicker.preview.PreviewManager;
import com.cz.photopicker.util.album.AlbumUtils;
import com.cz.photopicker.util.album.ImageFolder;
import com.example.common.util.AnimUtils;
import com.example.common.util.ArrayUtils;
import com.example.common.util.BitmapUtils;
import com.example.common.util.DateUtils;
import com.example.common.util.DensityUtils;
import com.example.common.util.DeviceUtils;
import com.example.common.util.DrawableUtils;
import com.example.common.util.FileUtils;
import com.example.common.util.SelectorUtils;
import com.example.common.util.ToastUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 照片选择页面
 * Created by wuczh on 2022/1/22
 */

public class PhotoPickerActivity extends Activity {

    private ActivityPickerLayoutBinding mBinding;

    /**
     * 启动页面
     *
     * @param context 上下文
     */
    public static void start(Context context) {
        Intent starter = new Intent(context, PhotoPickerActivity.class);
        context.startActivity(starter);
    }

    /**
     * 照相请求码
     */
    private static final int REQUEST_CAMERA = 777;
    /**
     * 列表适配器
     */
    private PhotoPickerAdapter mAdapter;

    /**
     * 选择数据
     */
    private PickerBean mPickerBean;
    /**
     * 当前照片
     */
    private final List<PickerItemBean> mCurrentPhotoList = new ArrayList<>();
    /**
     * 已选中的照片
     */
    private final List<PickerItemBean> mSelectedList = new ArrayList<>();

    /**
     * 临时文件路径
     */
    private String mTempFilePath = "";
    /**
     * 当前展示的图片文件夹
     */
    private ImageFolder mCurrentImageFolder;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityPickerLayoutBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        mPickerBean = PickerManager.sPickerBean;
        findViews();
        setListeners();
        initData();
    }

    protected void findViews() {
        initRecyclerView();
        mBinding.topLayout.setBackgroundColor(ContextCompat.getColor(this, mPickerBean.pickerUIConfig.getTopLayoutColor()));
        mBinding.bottomLayout.setBackgroundColor(ContextCompat.getColor(this, mPickerBean.pickerUIConfig.getBottomLayoutColor()));
        mBinding.title.setTextColor(ContextCompat.getColor(this, mPickerBean.pickerUIConfig.getMainTextColor()));
        mBinding.folderText.setTextColor(ContextCompat.getColor(this, mPickerBean.pickerUIConfig.getMainTextColor()));
        drawBackBtn(mPickerBean.pickerUIConfig.getBackBtnColor());
        if (mPickerBean.pickerUIConfig.getMoreFolderImg() != 0) {
            mBinding.moreImg.setImageResource(mPickerBean.pickerUIConfig.getMoreFolderImg());
        } else {
            drawMoreImg(mPickerBean.pickerUIConfig.getMainTextColor());
        }
        drawConfirmBtn();
        mBinding.previewBtn.setTextColor(SelectorUtils.createTxPressedUnableColor(this,
                mPickerBean.pickerUIConfig.getPreviewBtnNormal(),
                mPickerBean.pickerUIConfig.getPreviewBtnUnable(),
                mPickerBean.pickerUIConfig.getPreviewBtnUnable()));
        mBinding.confirmBtn.setEnabled(false);
        mBinding.previewBtn.setEnabled(false);
        DeviceUtils.setStatusBarColor(this, getWindow(), mPickerBean.pickerUIConfig.getStatusBarColor());
        DeviceUtils.setNavigationBarColorRes(this, getWindow(), mPickerBean.pickerUIConfig.getNavigationBarColor());
    }

    /**
     * 初始化RecyclerView
     */
    private void initRecyclerView() {
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        mAdapter = new PhotoPickerAdapter(this, mPickerBean.photoLoader, mPickerBean.isNeedCamera, mPickerBean.pickerUIConfig);
        mBinding.recyclerView.setLayoutManager(layoutManager);
        mBinding.recyclerView.setHasFixedSize(true);
        mBinding.recyclerView.setAdapter(mAdapter);
    }

    /**
     * 绘制返回按钮
     *
     * @param color 颜色
     */
    private void drawBackBtn(@ColorRes int color) {
        //        Observable.just(color)
        //                .map(colorRes -> BitmapUtils.rotateBitmap(getArrowBitmap(colorRes), 90))
        //                .subscribeOn(Schedulers.io())
        //                .observeOn(AndroidSchedulers.mainThread())
        //                .subscribe(new BaseObserver<Bitmap>() {
        //                    @Override
        //                    public void onBaseNext(Bitmap bitmap) {
        //                        mBinding.backBtn.setImageBitmap(bitmap);
        //                    }
        //
        //                    @Override
        //                    public void onBaseError(Throwable e) {
        //
        //                    }
        //                });
//        Bitmap bitmap = BitmapUtils.rotateBitmap(getArrowBitmap(color), 90);
//        mBinding.backBtn.setImageBitmap(bitmap);
    }

    /**
     * 绘制更多图标
     *
     * @param color 颜色
     */
    private void drawMoreImg(@ColorRes int color) {
//        Bitmap bitmap = getArrowBitmap(color);
//        mBinding.moreImg.setImageBitmap(bitmap);
    }

    /**
     * 绘制确定按钮
     */
    private void drawConfirmBtn() {
        mBinding.confirmBtn.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mBinding.confirmBtn.getViewTreeObserver().removeOnPreDrawListener(this);
                int width = mBinding.confirmBtn.getMeasuredWidth();
                int height = mBinding.confirmBtn.getMeasuredHeight();

                StateListDrawable drawable = SelectorUtils.createBgPressedUnableDrawable(
                        getCornerDrawable(mPickerBean.pickerUIConfig.getConfirmBtnNormal(), width, height),
                        getCornerDrawable(mPickerBean.pickerUIConfig.getConfirmBtnPressed(), width, height),
                        getCornerDrawable(mPickerBean.pickerUIConfig.getConfirmBtnUnable(), width, height));
                mBinding.confirmBtn.setBackground(drawable);
                mBinding.confirmBtn.setTextColor(SelectorUtils.createTxPressedUnableColor(PhotoPickerActivity.this,
                        mPickerBean.pickerUIConfig.getConfirmTextNormal(),
                        mPickerBean.pickerUIConfig.getConfirmTextPressed(),
                        mPickerBean.pickerUIConfig.getConfirmTextUnable()));
                return true;
            }
        });
    }

    protected void setListeners() {
        // 返回按钮
        mBinding.backBtn.setOnClickListener(v -> finish());

        // 文件夹按钮
        mBinding.folderBtn.setOnClickListener(v -> {
            List<ImageFolderItemBean> folders = new ArrayList<>();
            List<ImageFolder> ifs = AlbumUtils.getAllImageFolders(PhotoPickerActivity.this);
            // 组装数据
            for (ImageFolder folder : ifs) {
                ImageFolderItemBean iteamBean = new ImageFolderItemBean();
                iteamBean.imageFolder = folder;
                iteamBean.isSelected = mCurrentImageFolder.getDir().equals(folder.getDir());
                folders.add(iteamBean);
            }

            ImageFolderDialog dialog = new ImageFolderDialog(PhotoPickerActivity.this);
            dialog.setPhotoLoader(mPickerBean.photoLoader);
            dialog.setPickerUIConfig(mPickerBean.pickerUIConfig);
            dialog.setData(folders);
            dialog.setOnCancelListener(dialog1 -> {
                dialog1.dismiss();
                AnimUtils.startRotateSelf(mBinding.moreImg, -180, 0, 500, true);
            });
            dialog.setListener((dialog12, bean) -> {
                dialog12.dismiss();
                if (mCurrentImageFolder.getDir().equals(bean.imageFolder.getDir())) {// 选择了同一个文件夹
                    return;
                }
                mCurrentImageFolder = bean.imageFolder;
                mBinding.folderText.setText(bean.imageFolder.getName());
                configAdapterData(AlbumUtils.getImageListOfFolder(PhotoPickerActivity.this, bean.imageFolder));
                AnimUtils.startRotateSelf(mBinding.moreImg, -180, 0, 500, true);
            });
            dialog.show();
            AnimUtils.startRotateSelf(mBinding.moreImg, 0, -180, 500, true);
        });

        // 确定按钮
        mBinding.confirmBtn.setOnClickListener(v -> {
            List<String> list = new ArrayList<>();
            for (PickerItemBean itemBean : mSelectedList) {
                list.add(itemBean.photoPath);
            }
            if (mPickerBean != null && mPickerBean.photoPickerListener != null) {
                mPickerBean.photoPickerListener.onPickerSelected(list);
            }
            finish();
        });

        // 预览按钮
        mBinding.previewBtn.setOnClickListener(v -> {
            List<String> list = new ArrayList<>();
            for (PickerItemBean itemBean : mSelectedList) {
                list.add(itemBean.photoPath);// 组装数据
            }

            PreviewManager
                    .<String>create()
                    .setBackgroundColor(mPickerBean.pickerUIConfig.getItemBgColor())
                    .setStatusBarColor(mPickerBean.pickerUIConfig.getStatusBarColor())
                    .setNavigationBarColor(mPickerBean.pickerUIConfig.getNavigationBarColor())
                    .setPagerTextColor(mPickerBean.pickerUIConfig.getMainTextColor())
                    .setOnClickListener((context, source, position, controller) -> controller.close())
                    .setImgLoader((context, source, imageView) -> mPickerBean.previewLoader.displayImg(context, source, imageView))
                    .build(list)
                    .open(PhotoPickerActivity.this);
        });

        // 图片选中回调
        mAdapter.setListener(new PhotoPickerAdapter.Listener() {
            @Override
            public void onSelected(PickerItemBean bean, int position) {
                if (mSelectedList.size() == mPickerBean.maxCount && !bean.isSelected) {// 已经选满图片
                    ToastUtils.showShort(PhotoPickerActivity.this,
                            PhotoPickerActivity.this.getString(R.string.component_picker_photo_count_tips, String.valueOf(mPickerBean.maxCount)));
                    return;
                }

                for (int i = 0; i < mCurrentPhotoList.size(); i++) {
                    if (bean.photoPath.equals(mCurrentPhotoList.get(i).photoPath)) {
                        mCurrentPhotoList.get(i).isSelected = !bean.isSelected;
                        mAdapter.setData(mCurrentPhotoList);
                        mAdapter.notifyItemChanged(position);
                        if (mCurrentPhotoList.get(i).isSelected) {// 点击后是选中状态
                            mSelectedList.add(bean);
                        } else {
                            for (Iterator<PickerItemBean> ite = mSelectedList.iterator(); ite.hasNext(); ) {
                                PickerItemBean itemBean = ite.next();
                                if (itemBean.photoPath.equals(bean.photoPath)) {
                                    ite.remove();
                                    break;
                                }
                            }
                        }
                        // 设置按钮状态
                        mBinding.confirmBtn.setEnabled(mSelectedList.size() > 0);
                        mBinding.previewBtn.setEnabled(mSelectedList.size() > 0);
                        mBinding.confirmBtn.setText(mSelectedList.size() > 0 ? getString(R.string.component_picker_confirm_num,
                                String.valueOf(mSelectedList.size()), String.valueOf(mPickerBean.maxCount))
                                : getString(R.string.component_picker_confirm));
                        mBinding.previewBtn.setText(mSelectedList.size() > 0 ? getString(R.string.component_picker_preview_num,
                                String.valueOf(mSelectedList.size()))
                                : getString(R.string.component_picker_preview));
                        return;
                    }
                }
            }

            @Override
            public void onClickCamera() {
                takePhoto();
            }
        });

        // 图片点击回调
        mAdapter.setOnItemClickListener((viewHolder, item, position) -> {
            if (!mPickerBean.isNeedItemPreview) {
                return;
            }
            PreviewManager
                    .<String>create()
                    .setBackgroundColor(mPickerBean.pickerUIConfig.getItemBgColor())
                    .setStatusBarColor(mPickerBean.pickerUIConfig.getStatusBarColor())
                    .setNavigationBarColor(mPickerBean.pickerUIConfig.getNavigationBarColor())
                    .setPagerTextColor(mPickerBean.pickerUIConfig.getMainTextColor())
                    .setShowPagerText(false)
                    .setOnClickListener((context, source, position1, controller) -> controller.close())
                    .setImgLoader((context, source, imageView) -> mPickerBean.previewLoader.displayImg(context, source, imageView))
                    .build(item.photoPath)
                    .open(PhotoPickerActivity.this);
        });
    }

    protected void initData() {
        if (ArrayUtils.isEmpty(mPickerBean.sourceList)) {
            mCurrentImageFolder = AlbumUtils.getAllImageFolder(this);
            configAdapterData(AlbumUtils.getAllImages(this));
        } else {
            mCurrentImageFolder = null;
            configAdapterData(mPickerBean.sourceList);//让用户选择指定的图片
            mBinding.folderBtn.setEnabled(false);
            mBinding.folderText.setText(R.string.component_picker_custom_photo);
            mBinding.moreImg.setVisibility(View.GONE);
        }

    }

    /**
     * 配置适配器数据
     */
    private void configAdapterData(List<String> source) {
        mCurrentPhotoList.clear();
        for (String path : source) {
            PickerItemBean itemBean = new PickerItemBean();
            itemBean.photoPath = path;
            for (PickerItemBean selectedBean : mSelectedList) {
                if (selectedBean.photoPath.equals(path)) {
                    itemBean.isSelected = true;
                    break;
                }
            }
            mCurrentPhotoList.add(itemBean);
        }
        mAdapter.setData(mCurrentPhotoList);
        mBinding.recyclerView.smoothScrollToPosition(0);
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 根据颜色获取圆角Drawable
     *
     * @param color  颜色
     * @param width  宽度
     * @param height 高度
     */
    private Drawable getCornerDrawable(@ColorRes int color, int width, int height) {
        Bitmap bitmap = BitmapUtils.drawableToBitmap(DrawableUtils.createColorDrawable(this, color), width, height);
        if (bitmap == null) {
            return null;
        }
        Bitmap cornerBitmap = BitmapUtils.createRoundedCornerBitmap(bitmap, 8);
        return DrawableUtils.createBitmapDrawable(this, cornerBitmap);
    }

    /**
     * 获取箭头图片
     *
     * @param color 颜色
     */
    private Bitmap getArrowBitmap(@ColorRes int color) {
        int side = DensityUtils.dp2px(this, 20);
        int centerPoint = side / 2;

        Bitmap bitmap = Bitmap.createBitmap(side, side, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(ContextCompat.getColor(this, android.R.color.transparent));
        Paint paint = new Paint();
        paint.setColor(ContextCompat.getColor(this, color));
        paint.setStrokeWidth(7);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        Path path = new Path();
        path.moveTo(centerPoint, centerPoint + DensityUtils.dp2px(this, 3));
        path.lineTo(centerPoint - DensityUtils.dp2px(this, 7), centerPoint - DensityUtils.dp2px(this, 3));
        path.moveTo(centerPoint, centerPoint + DensityUtils.dp2px(this, 3));
        path.lineTo(centerPoint + DensityUtils.dp2px(this, 7), centerPoint - DensityUtils.dp2px(this, 3));
        canvas.drawPath(path, paint);

        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(centerPoint, centerPoint + DensityUtils.dp2px(this, 3), 3, paint);
        return bitmap;
    }

    /**
     * 拍照
     */
    @SuppressLint("QueryPermissionsNeeded")
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
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(this, mPickerBean.authority, new File(mTempFilePath)));
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
                AlbumUtils.notifyScanImage(this, mTempFilePath);// 更新相册
                new Handler().postDelayed(this::handleCameraSuccess, 300);
                return;
            }
            FileUtils.deleteFile(mTempFilePath);// 删除临时文件
            mTempFilePath = "";
        }
    }

    /**
     * 处理拍照成功
     */
    private void handleCameraSuccess() {
        mTempFilePath = "";
        List<ImageFolder> ifs = AlbumUtils.getAllImageFolders(this);
        for (ImageFolder folder : ifs) {
            if (folder.getDir().equals(mCurrentImageFolder.getDir())) {
                configAdapterData(AlbumUtils.getImageListOfFolder(this, folder));
                break;
            }
        }
    }

    @Override
    public void finish() {
        super.finish();
        mAdapter.release();
        PickerManager.sPickerBean.clear();
        mPickerBean.clear();
    }
}
