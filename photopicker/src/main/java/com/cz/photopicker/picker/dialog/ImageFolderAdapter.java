package com.cz.photopicker.picker.dialog;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.cz.photopicker.R;
import com.cz.photopicker.contract.PhotoLoader;
import com.cz.photopicker.picker.PickerUIConfig;
import com.example.common.adapter.BaseRecyclerViewAdapter;
import com.example.common.util.DensityUtils;

/**
 * 图片文件夹列表适配器
 * @author Wuczh
 * @date 2022/1/12
 */

public class ImageFolderAdapter extends BaseRecyclerViewAdapter<ImageFolderItemBean> {

    /** 图片加载接口 */
    private PhotoLoader<String> mPhotoLoader;

    /** 未选中图标 */
    private Bitmap mUnselectBitmap;
    /** 已选中图标 */
    private Bitmap mSelectedBitmap;

    public ImageFolderAdapter(Context context) {
        super(context);
    }

    /** 设置图片加载接口 */
    public void setPhotoLoader(PhotoLoader<String> photoLoader){
        mPhotoLoader = photoLoader;
    }

    /** 设置UI配置 */
    public void setPickerUIConfig(PickerUIConfig config){
        mUnselectBitmap = getUnselectBitmap(config.getFolderSelectColor());
        mSelectedBitmap = getSelectedBitmap(config.getFolderSelectColor());
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ImageFolderViewHolder(getLayoutView(parent, R.layout.item_img_folder_layout));
    }

    @Override
    public void onBind(RecyclerView.ViewHolder holder, int position) {
        ImageFolderItemBean bean = getItem(position);
        if (bean == null){
            return;
        }
        showItem((ImageFolderViewHolder) holder, bean, position);
    }

    private void showItem(ImageFolderViewHolder holder, ImageFolderItemBean bean, int position) {
        mPhotoLoader.displayImg(getContext(), bean.imageFolder.getFirstImagePath(), holder.folderImg);
        holder.floderName.setText(bean.imageFolder.getName());
        holder.count.setText(getContext().getString(R.string.component_picker_folder_num, String.valueOf(bean.imageFolder.getCount())));
        holder.selectIconImg.setImageBitmap(bean.isSelected ? mSelectedBitmap : mUnselectBitmap);
        holder.dirName.setText(bean.imageFolder.isAllPicture() ? bean.imageFolder.getName() : bean.imageFolder.getDir());
    }

    private Bitmap getUnselectBitmap(@ColorRes int color){
        int side = DensityUtils.dp2px(getContext(), 30);

        Bitmap bitmap = Bitmap.createBitmap(side, side, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(ContextCompat.getColor(getContext(), android.R.color.transparent));
        Paint paint = new Paint();
        paint.setColor(ContextCompat.getColor(getContext(), color));
        paint.setStrokeWidth(4);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(side / 2, side / 2, side / 2 - 10, paint);
        return bitmap;
    }

    private Bitmap getSelectedBitmap(@ColorRes int color){
        int side = DensityUtils.dp2px(getContext(), 30);

        Bitmap bitmap = Bitmap.createBitmap(side, side, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(ContextCompat.getColor(getContext(), android.R.color.transparent));
        Paint paint = new Paint();
        paint.setColor(ContextCompat.getColor(getContext(), color));
        paint.setStrokeWidth(4);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(side / 2, side / 2, side / 2 - 10, paint);

        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(side / 2, side / 2, side / 2 - 25, paint);
        return bitmap;
    }

    private class ImageFolderViewHolder extends RecyclerView.ViewHolder{

        /** 文件夹第一张图片 */
        private ImageView folderImg;
        /** 文件夹名称 */
        private TextView floderName;
        /** 文件夹路径 */
        private TextView dirName;
        /** 文件夹内图片数量 */
        private TextView count;
        /** 选中图标 */
        private ImageView selectIconImg;

        private ImageFolderViewHolder(View itemView) {
            super(itemView);
            folderImg = itemView.findViewById(R.id.folder_img);
            floderName = itemView.findViewById(R.id.floder_name);
            dirName = itemView.findViewById(R.id.dir_name);
            count = itemView.findViewById(R.id.count);
            selectIconImg = itemView.findViewById(R.id.select_icon);
        }
    }

}
