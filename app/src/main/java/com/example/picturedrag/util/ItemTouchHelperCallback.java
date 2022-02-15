package com.example.picturedrag.util;

import android.graphics.Canvas;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.common.util.AnimUtils;
import com.example.common.util.ArrayUtils;
import com.example.common.util.DensityUtils;
import com.example.common.util.VibratorUtil;
import com.example.picturedrag.App;
import com.example.picturedrag.adapter.PhotoPublishAdapter;

import java.util.ArrayList;
import java.util.Collections;

/**
 * 拖拽排序删除
 *
 * @author Wuczh
 * @date 2021/11/18
 */
public class ItemTouchHelperCallback extends ItemTouchHelper.Callback {

    /**
     * 是否需要拖拽震动提醒
     */
    private boolean isNeedDragVibrate = true;
    /**
     * 适配器
     */
    private PhotoPublishAdapter mAdapter;

    /**
     * 手指抬起标记位
     */
    private boolean up;
    /**
     * 可滑动伸缩空间
     */
    private NestedScrollView mScrollView;
    /**
     * 数据列表
     */
    private ArrayList<String> mDataList;

    private int dragFlags;
    private int swipeFlags;

    /**
     * @param isNeedDragVibrate 是否需要拖拽震动提醒
     * @param imagesList        图片数据
     * @param scrollView        可滑动伸缩空间
     * @param adapter           适配器
     */
    public ItemTouchHelperCallback(boolean isNeedDragVibrate, ArrayList<String> imagesList, NestedScrollView scrollView, PhotoPublishAdapter adapter) {
        this.isNeedDragVibrate = isNeedDragVibrate;
        mAdapter = adapter;
        mScrollView = scrollView;
        mDataList = imagesList;
    }

    /**
     * 设置item是否处理拖拽事件和滑动事件，以及拖拽和滑动操作的方向
     */
    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        //判断 recyclerView的布局管理器数据,设置 item 只能处理拖拽事件，并能够向左、右、上、下拖拽
        if (recyclerView.getLayoutManager() instanceof StaggeredGridLayoutManager) {//设置能拖拽的方向
            dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
            swipeFlags = 0;//0则不响应滑动事件
        }
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    /**
     * 拖拽，交换位置(当用户从item原来的位置拖动可以拖动的item到新位置的过程中调用)
     *
     * @param viewHolder 拖动的 item
     * @param target     目标 item
     */
    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder
            viewHolder, @NonNull RecyclerView.ViewHolder target) {
        if (ArrayUtils.isEmpty(mDataList)) {
            return false;
        }
        // 得到拖动ViewHolder的Position
        int fromPosition = viewHolder.getAdapterPosition();
        // 得到目标ViewHolder的Position
        int toPosition = target.getAdapterPosition();

        //因为没有将 +号的图片 加入imageList,所以不用imageList.size-1 此处限制不能移动到recyclerView最后一位
        if (toPosition == mDataList.size() || mDataList.size() == fromPosition) {
            return false;
        }

        if (fromPosition < toPosition) {//顺序小到大
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(mDataList, i, i + 1);
            }
        } else {//顺序大到小
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(mDataList, i, i - 1);
            }
        }
        mAdapter.notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    /**
     * 滑动
     */
    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
    }

    /**
     * 当长按选中item时（拖拽开始时）调用
     * ItemTouchHelper.ACTION_STATE_IDLE  闲置状态
     * ItemTouchHelper.ACTION_STATE_DRAG  拖拽中状态
     */
    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        if (viewHolder == null) {
            return;
        }
        //设置拖拽震动提醒
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE && isNeedDragVibrate) {
            VibratorUtil.vibrate(App.get(), 100);
        }
        //设置拖拽动画
        if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {//开始拖拽
            AnimUtils.startScaleSelf(viewHolder.itemView, 1.0f, 1.05f, 1.0f, 1.05f, 50, true);
        }
        //设置拖拽状态为true
        if (ItemTouchHelper.ACTION_STATE_DRAG == actionState && dragListener != null) {
            dragListener.dragState(true);
        }
        super.onSelectedChanged(viewHolder, actionState);
    }

    /**
     * 当手指松开时（拖拽完成时）调用
     */
    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        AnimUtils.startScaleSelf(viewHolder.itemView, 1.05f, 1.0f, 1.05f, 1.0f, 50, true);
        super.clearView(recyclerView, viewHolder);
        initData();
        if (dragListener != null) {
            dragListener.clearView();
        }
    }

    /**
     * 自定义拖动与滑动交互
     *
     * @param c
     * @param recyclerView
     * @param viewHolder
     * @param dX                X轴移动的距离
     * @param dY                Y轴移动的距离
     * @param actionState       当前Item的状态
     * @param isCurrentlyActive 如果当前被用户操作为true，反之为false
     */
    @Override
    public void onChildDraw(Canvas c, @NonNull RecyclerView
            recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY,
                            int actionState, boolean isCurrentlyActive) {
        if (null == dragListener) {
            return;
        }
        int editTextHeight = DensityUtils.dp2px(App.get(), 130);//输入框的高度
        int deleteViewHeight = DensityUtils.dp2px(App.get(), 50);//删除区间的高度
        if (dY >= (mScrollView.getHeight() - editTextHeight)
                - viewHolder.itemView.getBottom()//item底部距离recyclerView顶部高度
                - deleteViewHeight
                + mScrollView.getScrollY()) {//拖到删除处
            dragListener.deleteState(true);
            if (up) {//在删除处放手，则删除item
                mDataList.remove(viewHolder.getAdapterPosition());
                dragListener.deleteOk();
                mAdapter.notifyDataSetChanged();
                initData();
                return;
            }
        } else {//没有到删除处
            dragListener.deleteState(false);
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }

    /**
     * 设置是否支持长按拖拽
     * 此处必须返回false
     * 需要在recyclerView长按事件里限制,否则最后+号长按后扔可拖拽
     */
    @Override
    public boolean isLongPressDragEnabled() {
        return false;
    }

    /**
     * 设置是否支持支持滑动
     *
     * @return true  支持滑动操作
     * false 不支持滑动操作
     */
    @Override
    public boolean isItemViewSwipeEnabled() {
        return false;
    }

    /**
     * 设置手指离开后ViewHolder的动画时间，在用户手指离开后调用
     *
     * @param recyclerView
     * @param animationType
     * @param animateDx
     * @param animateDy
     * @return
     */
    @Override
    public long getAnimationDuration(@NonNull RecyclerView recyclerView, int animationType,
                                     float animateDx, float animateDy) {
        //手指放开
        up = true;
        return super.getAnimationDuration(recyclerView, animationType, animateDx, animateDy);
    }

    public interface DragListener {
        /**
         * 用户是否将 item拖动到删除处，根据状态改变颜色
         *
         * @param delete
         */
        void deleteState(boolean delete);

        /**
         * 是否于拖拽状态
         *
         * @param start
         */
        void dragState(boolean start);

        /**
         * 当删除完成后调用
         */
        void deleteOk();

        /**
         * 当用户与item的交互结束并且item也完成了动画时调用
         */
        void clearView();
    }

    private DragListener dragListener;

    public void setDragListener(DragListener dragListener) {
        this.dragListener = dragListener;
    }

    /**
     * 重置状态（拖拽状态设置为false 删除状态为false）
     */
    private void initData() {
        if (dragListener != null) {
            dragListener.deleteState(false);
            dragListener.dragState(false);
        }
        up = false;
    }
}
