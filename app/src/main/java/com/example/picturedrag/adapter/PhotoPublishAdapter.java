package com.example.picturedrag.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.common.util.DensityUtils;
import com.example.common.util.ScreenUtils;
import com.example.picturedrag.R;

/**
 * 图片选择适配器
 *
 * @author Wuczh
 * @date 2021/11/23
 */
public class PhotoPublishAdapter extends BaseRecyclerViewAdapter<String> {

    /**
     * 是否需要添加图标
     */
    private boolean isNeedAddBtn = true;

    /**
     * 最大图片数
     */
    private int maxPic = 1;

    /**
     * 监听器
     */
    private OnNineGridViewListener mListener;


    public PhotoPublishAdapter(@NonNull Context context) {
        super(context);
    }

    /**
     * 设置是否需要添加图标
     *
     * @param needAddBtn 是否需要添加图标
     */
    public void setNeedAddBtn(boolean needAddBtn) {
        isNeedAddBtn = needAddBtn;
    }

    @Override
    public int getItemCount() {
        if (!isNeedAddBtn) {
            return super.getItemCount();
        }
        if (super.getItemCount() == maxPic) {//照片数量和总数相等
            return super.getItemCount();
        }
        return super.getItemCount() + 1;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NineGridViewHolder(getLayoutView(parent, R.layout.item_nine_grid_layout));
    }

    @Override
    public void onBind(@NonNull RecyclerView.ViewHolder holder, int position) {
        String data = getItem(position);
        if (data == null) {
            return;
        }
        showItem((NineGridViewHolder) holder, data, position);
    }

    private void showItem(final NineGridViewHolder holder, String data, int position) {
        int width = ScreenUtils.getScreenWidth(getContext());
        LinearLayout.LayoutParams prm = (LinearLayout.LayoutParams) holder.img.getLayoutParams();//获取图片的布局
        prm.width = (width - DensityUtils.dp2px(getContext(), 40)) / 3;
        prm.height = (width - DensityUtils.dp2px(getContext(), 40)) / 3;
        holder.img.setLayoutParams(prm);
        if (position == getDataSize()) {
            holder.img.setImageBitmap(BitmapFactory.decodeResource(getContext().getResources(), R.drawable.ic_add_pic));
            holder.img.setOnClickListener(v -> {
                if (mListener != null) {
                    mListener.onAddPic(maxPic - getDataSize());
                }
            });
        } else {
            if (mListener != null) {
                mListener.onDisplayImg(getContext(), data, holder.img);
            }
            holder.img.setOnClickListener(v -> {
                if (mListener != null) {
                    mListener.onClickPic(data, holder.getAdapterPosition());
                }
            });

            holder.img.setOnLongClickListener(v -> {
                if (mListener != null) {
                    mListener.onLongClickPic(holder, data, holder.getAdapterPosition());
                }
                return true;
            });
        }
    }

    public static class NineGridViewHolder extends RecyclerView.ViewHolder {

        /**
         * 图片
         */
        private final ImageView img;

        private NineGridViewHolder(View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img);
        }
    }

    public void setOnNineGridViewListener(OnNineGridViewListener listener) {
        mListener = listener;
    }


    /**
     * 设置最大图片数[count]
     */
    public void setMaxPic(int count) {
        maxPic = count;
    }

    /**
     * 获取最大图片数
     */
    public int getMaxPic() {
        return maxPic;
    }

    @Override
    public String getItem(int position) {
        //必须重写 否则+号布局的position会报空指针
        if (position != getDataSize()) {
            return super.getItem(position);
        }
        return "";
    }
}
