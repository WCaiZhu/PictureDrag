package com.example.picturedrag.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.appcompat.widget.AppCompatEditText;

/**
 * 用于解决嵌套Scrollview的时候由于多行而产生的滑动冲突问题
 *
 * @author Wuczh
 * @date 2021/11/30
 */
public class EditTextWithScrollView extends AppCompatEditText {

    //滑动距离的最大边界
    private int mOffsetHeight;

    //是否到顶或者到底的标志
    private boolean mBottomFlag = false;
    private boolean mCanVerticalScroll;

    public EditTextWithScrollView(Context context) {
        super(context);
        init();
    }

    public EditTextWithScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public EditTextWithScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mCanVerticalScroll = canVerticalScroll();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN)
            //如果是新的按下事件，则对mBottomFlag重新初始化
            mBottomFlag = false;
        //如果已经不要这次事件，则传出取消的信号，这里的作用不大
        if (mBottomFlag)
            event.setAction(MotionEvent.ACTION_CANCEL);

        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean result = super.onTouchEvent(event);
        if (mCanVerticalScroll) {
            //如果是需要拦截，则再拦截，这个方法会在onScrollChanged方法之后再调用一次
            if (!mBottomFlag)
                getParent().requestDisallowInterceptTouchEvent(true);
        } else {
            getParent().requestDisallowInterceptTouchEvent(false);
        }
        return result;
    }

    @Override
    protected void onScrollChanged(int horiz, int vert, int oldHoriz, int oldVert) {
        super.onScrollChanged(horiz, vert, oldHoriz, oldVert);
        if (vert == mOffsetHeight || vert == 0) {
            //这里触发父布局或祖父布局的滑动事件
            getParent().requestDisallowInterceptTouchEvent(false);
            mBottomFlag = true;
        }
    }

    /**
     * EditText竖直方向是否可以滚动
     *
     * @return true：可以滚动   false：不可以滚动
     */
    private boolean canVerticalScroll() {
        //滚动的距离
        int scrollY = getScrollY();
        //控件内容的总高度
        int scrollRange = getLayout().getHeight();
        //控件实际显示的高度
        int scrollExtent = getHeight() - getCompoundPaddingTop() - getCompoundPaddingBottom();
        //控件内容总高度与实际显示高度的差值
        mOffsetHeight = scrollRange - scrollExtent;

        if (mOffsetHeight == 0) {

            return false;
        }

        return (scrollY > 0) || (scrollY < mOffsetHeight - 1);
    }
}
