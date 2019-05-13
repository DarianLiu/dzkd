package com.dzkandian.common.widget.recyclerview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Created by Administrator on 2018/6/3.
 */

public class MyRecyclerView extends RecyclerView {

    private boolean isCancelFling;

    public MyRecyclerView(Context context) {
        super(context);
    }

    public MyRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MyRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * 设置屏蔽惯性滑动
     * @param cancelFling 是否屏蔽惯性滑动
     */
    public void setCancelFling(boolean cancelFling) {
        isCancelFling = cancelFling;
    }

    @Override
    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        return super.dispatchNestedFling(velocityX, velocityY, consumed);
    }

    @Override
    public boolean fling(int velocityX, int velocityY) {
        getLayoutManager().getChildCount();
        return super.fling(velocityX, velocityY);
    }

    //    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
//        return getScrollingChildHelper().dispatchNestedFling(velocityX, velocityY, consumed);
//    }
//
//    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
//        return getScrollingChildHelper().dispatchNestedPreFling(velocityX, velocityY);
//    }

}
