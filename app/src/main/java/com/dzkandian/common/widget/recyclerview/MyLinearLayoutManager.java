package com.dzkandian.common.widget.recyclerview;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import timber.log.Timber;

/**
 * 类似抖音小视频样式布局管理器
 * Created by liuli on 2018/7/27.
 */

public class MyLinearLayoutManager extends LinearLayoutManager {

    private PagerSnapHelper mPagerSnapHelper;
//    private OnViewPagerListener mOnViewPagerListener;
//    private int mDrift = 0;//位移，用来判断移动方向

    public MyLinearLayoutManager(Context context) {
        super(context);
        mPagerSnapHelper = new PagerSnapHelper();
    }

    public MyLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
        mPagerSnapHelper = new PagerSnapHelper();
    }

    public MyLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mPagerSnapHelper = new PagerSnapHelper();
    }

    @Override
    public void onAttachedToWindow(RecyclerView view) {
        super.onAttachedToWindow(view);
        if (mPagerSnapHelper != null) {
            mPagerSnapHelper.attachToRecyclerView(view);//设置RecyclerView每次滚动一页
        }
//        view.addOnChildAttachStateChangeListener(mChildAttachStateChangeListener);
    }

//    /**
//     * 滑动状态的改变
//     * 缓慢拖拽-> SCROLL_STATE_DRAGGING
//     * 快速滚动-> SCROLL_STATE_SETTLING
//     * 空闲状态-> SCROLL_STATE_IDLE
//     *
//     * @param state 滑动状态
//     */
//    @Override
//    public void onScrollStateChanged(int state) {
//        switch (state) {
//            case RecyclerView.SCROLL_STATE_IDLE:
//                int firstPosition = this.findFirstCompletelyVisibleItemPosition();
//                int lastPosition = this.findLastCompletelyVisibleItemPosition();
//                Timber.d("==========ShortVideo - findLastCompletelyVisibleItemPosition：" + firstPosition + " " + lastPosition);
//                View viewIdle = this.findViewByPosition(lastPosition);
//                if (mOnViewPagerListener != null && firstPosition >= 0) {
//                    Timber.d("==========ShortVideo - findLastCompletelyVisibleItemPosition：" + (getChildCount() == 1));
//                    mOnViewPagerListener.onPageSelected(viewIdle, lastPosition, lastPosition == getItemCount() - 1);
//                }
//                break;
//        }
//    }

    /**
     * 布局完成后调用
     *
     * @param state
     */
    @Override
    public void onLayoutCompleted(RecyclerView.State state) {
        super.onLayoutCompleted(state);
    }

    /**
     * 监听竖直方向的相对偏移量
     */
    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
//        this.mDrift = dy;
        return super.scrollVerticallyBy(dy, recycler, state);
    }

    /**
     * 监听水平方向的相对偏移量
     */
    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
//        this.mDrift = dx;
        return super.scrollHorizontallyBy(dx, recycler, state);
    }
//
//    /**
//     * 设置监听
//     *
//     * @param listener 监听事件
//     */
//    public void setOnViewPagerListener(OnViewPagerListener listener) {
//        this.mOnViewPagerListener = listener;
//    }

//    private RecyclerView.OnChildAttachStateChangeListener mChildAttachStateChangeListener = new RecyclerView.OnChildAttachStateChangeListener() {
//        @Override
//        public void onChildViewAttachedToWindow(View view) {
//            if (mOnViewPagerListener != null && getChildCount() == 1 && view != null) {
//                mOnViewPagerListener.onInitComplete(view, getPosition(view));
//            }
//        }
//
//        @Override
//        public void onChildViewDetachedFromWindow(View view) {
//            if (mDrift >= 0) {
//                if (mOnViewPagerListener != null)
//                    mOnViewPagerListener.onPageRelease(view, true, getPosition(view));
//            } else {
//                if (mOnViewPagerListener != null)
//                    mOnViewPagerListener.onPageRelease(view, false, getPosition(view));
//            }
//        }
//    };

//    public interface OnViewPagerListener {
//
//        /*释放的监听*/
//        void onPageRelease(View view, boolean isNext, int position);
//
//        /*选中的监听以及判断是否滑动到底部*/
//        void onPageSelected(View view, int position, boolean isBottom);
//
//        /*布局完成的监听*/
//        void onInitComplete(View view, int position);
//
//    }

}
