package com.dzkandian.common.player.play;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;

import com.dzkandian.common.uitls.Constant;
import com.dzkandian.common.widget.recyclerview.MyLinearLayoutManager;
import com.dzkandian.mvp.video.ui.adapter.ShortDetailAdapter;
import com.dzkandian.storage.bean.video.VideoBean;
import com.jess.arms.utils.DataHelper;
import com.jess.arms.utils.DeviceUtils;
import com.kk.taurus.playerbase.entity.DataSource;
import com.kk.taurus.playerbase.receiver.ReceiverGroup;

/**
 * ListPlayLogic
 * Created by Taurus on 2018/4/15.
 */

public class ListPlayLogic {

    private RecyclerView mRecycler;
    private ShortDetailAdapter mAdapter;

    private int mScreenH;

    private int mPlayPosition = -1;
    private int mVerticalRecyclerStart;

    private ReceiverGroup mReceiverGroup;

    private ShortDetailAdapter.OnVideoSwitchListener mOnVideoSwitchListener;

    public ListPlayLogic(RecyclerView recycler, ShortDetailAdapter adapter, ShortDetailAdapter.OnVideoSwitchListener onVideoSwitchListener) {
        this.mRecycler = recycler;
        this.mAdapter = adapter;
        this.mOnVideoSwitchListener = onVideoSwitchListener;
        init();
    }

    private void init() {
        mScreenH = (int) DeviceUtils.getScreenHeight(mRecycler.getContext());
        mRecycler.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int[] location = new int[2];
                mRecycler.getLocationOnScreen(location);
                mVerticalRecyclerStart = location[1];
                mRecycler.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
        mRecycler.addOnScrollListener(onScrollListener);

        mRecycler.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(View view) {
                if (view != null && mRecycler != null && mRecycler.getChildCount() == 1 && mRecycler.getChildLayoutPosition(view) == 0) {
                    mPlayPosition = 0;
                    mOnVideoSwitchListener.showAd(false);
                    playPosition(mPlayPosition);
                    mRecycler.removeOnChildAttachStateChangeListener(this);
                }
            }

            @Override
            public void onChildViewDetachedFromWindow(View view) {

            }
        });
    }

    public void setReceiverGroup(ReceiverGroup receiverGroup) {
        this.mReceiverGroup = receiverGroup;
    }

    private RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                int itemVisibleRectHeight = getItemVisibleRectHeight(mPlayPosition);
                if (itemVisibleRectHeight <= mScreenH / 2) {
//                    Timber.d("==========ShortVideo - onScrollStateChanged stop：");
                    AssistPlayer.get().stop();
                    if (mAdapter != null)
                        mAdapter.notifyItemChanged(mPlayPosition);
                    mPlayPosition = -1;
                }

                MyLinearLayoutManager layoutManager = (MyLinearLayoutManager) recyclerView.getLayoutManager();
                int firstPosition = layoutManager.findFirstVisibleItemPosition();
                int lastPosition = layoutManager.findLastVisibleItemPosition();
//                Timber.d("==========ShortVideo - onScrollStateChanged start：" + firstPosition + " : " + lastPosition);
                if (mPlayPosition == getVisibleRectMaxPosition(firstPosition, lastPosition)) {
//                    Timber.d("==========ShortVideo - onScrollStateChanged getVisibleRectMaxPosition: " + getVisibleRectMaxPosition(firstPosition, lastPosition));
                    return;
                }

                mPlayPosition = getVisibleRectMaxPosition(firstPosition, lastPosition);
//                Timber.d("==========ShortVideo -onScrollStateChanged mPlayPosition：" + mPlayPosition + " indexIsAD: " + mAdapter.indexIsAD(mPlayPosition));
                mOnVideoSwitchListener.showAd(mAdapter.indexIsAD(mPlayPosition));
                playPosition(mPlayPosition);
                if (mAdapter != null && mAdapter.isGetAdBig()) {
                    mAdapter.fetchBaiDuAdBig();
                }
                if (mAdapter != null && mAdapter.isGetAdSmall()) {
                    mAdapter.fetchBaiDuAdSmall();
                }
                mOnVideoSwitchListener.showComment();
//                Timber.d("==========ShortVideo - onScrollStateChanged play：" + mPlayPosition);
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
        }
    };

    public int getPlayPosition() {
        return mPlayPosition;
    }

    public void attachPlay() {
        mRecycler.post(() -> {
            ShortDetailAdapter.VideoItemHolder itemHolder = getItemHolder(mPlayPosition);
            if (itemHolder != null) {
                if (mAdapter != null && !mAdapter.indexIsAD(mPlayPosition))
                    AssistPlayer.get().play(itemHolder.layoutContainer, null);
            }
        });
    }

    private void playPosition(final int position) {
        VideoBean item = getItem(position);
        final DataSource dataSource = new DataSource(item.getUrl());
        String token = DataHelper.getStringSF(mRecycler.getContext().getApplicationContext(), Constant.SP_KEY_TOKEN);
        if (!TextUtils.isEmpty(token)) {
            dataSource.setTitle(item.getCanShare());
        }

        final ShortDetailAdapter.VideoItemHolder holder = getItemHolder(position);
        if (holder != null) {
            mRecycler.post(() -> {
                if (mAdapter != null && !mAdapter.indexIsAD(position)) {
                    if (AssistPlayer.get().getReceiverGroup() == null && mReceiverGroup != null) {
                        AssistPlayer.get().setReceiverGroup(mReceiverGroup);
                    }
                    AssistPlayer.get().play(holder.layoutContainer, dataSource);
                }
            });
        }
    }

    private VideoBean getItem(int position) {
        return mAdapter.getItem(position);
    }

    private ShortDetailAdapter.VideoItemHolder getItemHolder(int position) {
        if (mRecycler != null) {
            RecyclerView.ViewHolder viewHolder = mRecycler.findViewHolderForLayoutPosition(position);
            if (viewHolder != null && viewHolder instanceof ShortDetailAdapter.VideoItemHolder) {
                return ((ShortDetailAdapter.VideoItemHolder) viewHolder);
            }
        }
        return null;
    }

    /**
     * 获取Item中渲染视图的可见高度
     *
     * @param position 位置
     */
    private int getItemVisibleRectHeight(int position) {
        RecyclerView.ViewHolder viewHolder = mRecycler.findViewHolderForLayoutPosition(position);
        if (viewHolder == null)
            return 0;
        int[] location = new int[2];
        viewHolder.itemView.getLocationOnScreen(location);
        int height = viewHolder.itemView.getHeight();

        int visibleRect;
        if (location[1] <= mVerticalRecyclerStart) {
            visibleRect = location[1] - mVerticalRecyclerStart + height;
        } else {
            if (location[1] + height >= mScreenH) {
                visibleRect = mScreenH - location[1];
            } else {
                visibleRect = height;
            }
        }
        return visibleRect;
    }

    /**
     * 获取两个索引条目中渲染视图可见高度最大的条目
     */
    private int getVisibleRectMaxPosition(int position1, int position2) {
        RecyclerView.ViewHolder itemHolder1 = mRecycler.findViewHolderForLayoutPosition(position1);
        RecyclerView.ViewHolder itemHolder2 = mRecycler.findViewHolderForLayoutPosition(position2);
        if (itemHolder1 == null && itemHolder2 == null) {
            return RecyclerView.NO_POSITION;
        }
        if (itemHolder1 == null) {
            return position2;
        }
        if (itemHolder2 == null) {
            return position1;
        }
        int visibleRect1 = getItemVisibleRectHeight(position1);
        int visibleRect2 = getItemVisibleRectHeight(position2);
        return visibleRect1 >= visibleRect2 ? position1 : position2;
    }

    /**
     * 判断给定的索引条目，渲染视图的可见高度是否满足播放条件.
     */
    private boolean isVisibleRectAvailablePlay(int position) {
        ShortDetailAdapter.VideoItemHolder itemHolder = getItemHolder(position);
        if (itemHolder == null)
            return false;
        int height = itemHolder.layoutContainer.getHeight();
        return getItemVisibleRectHeight(position) > (height / 2);
    }

    private boolean isCompleteVisibleRect(int position) {
        ShortDetailAdapter.VideoItemHolder itemHolder = getItemHolder(position);
        if (itemHolder == null)
            return false;
        int height = itemHolder.layoutContainer.getHeight();
        return getItemVisibleRectHeight(position) == height;
    }

    public void destroy() {
        mAdapter = null;
        if (mRecycler != null) {
            mRecycler.removeOnScrollListener(onScrollListener);
            mRecycler = null;
        }
    }

}
