package com.example.common.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.common.util.ArrayUtils;

import java.util.List;

/**
 * RecyclerView基类适配器
 * @author Wuczh
 * @date 2022/1/12
 */
public abstract class BaseRecyclerViewAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    /** 数据列表 */
    private List<T> mData;
    /** item点击 */
    protected OnItemClickListener<T> mOnItemClickListener;
    /** item长按 */
    protected OnItemLongClickListener<T> mOnItemLongClickListener;

    /** 上下文 */
    private Context mContext;


    public BaseRecyclerViewAdapter(Context context) {
        this.mContext = context;
    }

    public Context getContext(){
        return mContext;
    }

    /**
     * 获取数据
     * @param position 位置
     */
    public T getItem(int position) {
        if (mData == null || mData.size() == 0){
            return null;
        }
        try {
            return mData.get(position);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        setItemClick(holder, position);
        setItemLongClick(holder, position);
        onBind(holder, position);
    }


    /** 设置点击事件 */
    protected void setItemClick(final RecyclerView.ViewHolder holder, final int position) {
        holder.itemView.setOnClickListener(v -> {
            if(position >= 0 && mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(holder, getItem(position), position);
            }
        });
    }

    /** 设置长按事件 */
    protected void setItemLongClick(final RecyclerView.ViewHolder holder, final int position) {
        holder.itemView.setOnLongClickListener(v -> {
            if (position >= 0 && mOnItemLongClickListener != null){
                mOnItemLongClickListener.onItemLongClick(holder, getItem(position), position);
            }
            return true;
        });
    }

    public abstract void onBind(RecyclerView.ViewHolder holder, int position);

    @Override
    public int getItemCount() {
        return getDataSize();
    }

    /** 获取数据长度 */
    protected int getDataSize(){
        return ArrayUtils.getSize(mData);
    }

    /**
     * 设置数据
     * @param data 数据列表
     */
    public void setData(List<T> data){
        this.mData = data;
    }

    /** 获取数据 */
    public List<T> getData() {
        return mData;
    }

    /** 在onCreateViewHolder方法中根据layoutId获取View */
    protected View getLayoutView(ViewGroup parent, int layoutId){
        return LayoutInflater.from(mContext).inflate(layoutId, parent, false);
    }

    /**
     * 设置itemview的宽度
     * @param itemView holder的itemview
     * @param width 宽度
     */
    protected void setItemViewWidth(View itemView, int width){
        ViewGroup.LayoutParams layoutParams = itemView.getLayoutParams();
        layoutParams.width = width;
        itemView.setLayoutParams(layoutParams);
    }

    /**
     * 设置itemview的高度
     * @param itemView holder的itemview
     * @param height 高度
     */
    protected void setItemViewHeight(View itemView, int height){
        ViewGroup.LayoutParams layoutParams = itemView.getLayoutParams();
        layoutParams.height = height;
        itemView.setLayoutParams(layoutParams);
    }

    /**
     * 带动画的删除item并刷新数据
     * @param position 位置
     */
    public void notifyItemRemovedChanged(int position){
        if (getDataSize() == 0){
            return;
        }
        mData.remove(position);
        notifyItemRemoved(position);
        if (position != mData.size()) { // 如果移除的是最后一个，忽略
            notifyItemRangeChanged(position, mData.size() - position);
        }
    }

    /** 设置点击事件监听器 */
    public void setOnItemClickListener(OnItemClickListener<T> listener){
        mOnItemClickListener = listener;
    }
    /** 设置长按事件监听器 */
    public void setOnItemLongClickListener(OnItemLongClickListener<T> listener){
        mOnItemLongClickListener = listener;
    }

    public interface OnItemClickListener<T> {
        void onItemClick(RecyclerView.ViewHolder viewHolder, T item, int position);
    }

    public interface OnItemLongClickListener<T> {
        void onItemLongClick(RecyclerView.ViewHolder viewHolder, T item, int position);
    }
}
