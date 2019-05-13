package com.dzkandian.mvp.news.ui.adapter;

import android.support.annotation.NonNull;
import android.view.View;

import com.dzkandian.R;
import com.dzkandian.common.uitls.Constant;
import com.dzkandian.mvp.mine.ui.adapter.EmptyItemHolder;
import com.dzkandian.storage.bean.news.ReplyBean;
import com.jess.arms.base.BaseHolder;
import com.jess.arms.base.DefaultAdapter;

import java.util.List;

/**
 * Created by Administrator on 2018/12/5.
 */

public class ReplyDetailAdapter extends DefaultAdapter<ReplyBean> {
    private static final int TYPE_DATA = 0;//数据类型
    private static final int TYPE_EMPTY = 1;//空数据类型（空数据，网络异常）
    private static final int REPLY_EMPTY = 2;//回复数据为空类型
    private int errorState;//是否显示异常布局（默认为0，不填充异常数据；1：空列表；2：网络异常）

    @Override
    public int getItemCount() {
        if (mInfos.size() == 0) {
            return 1;
        } else if (mInfos.size() == 1) {
            return 2;
        } else {
            return mInfos.size();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mInfos.size() == 0) {
            return TYPE_EMPTY;
        } else if (mInfos.size() == 1) {
            if (position == 0) {
                return TYPE_DATA;
            } else {
                return REPLY_EMPTY;
            }
        } else {
            return TYPE_DATA;
        }
    }

    public ReplyDetailAdapter(List<ReplyBean> infos) {
        super(infos);
    }

    @Override
    public BaseHolder<ReplyBean> getHolder(View v, int viewType) {
        if (viewType == TYPE_DATA) {
            return new ReplyDetailHolder(v);
        } else if (viewType == REPLY_EMPTY) {
            return new EmptyReplyHolder(v);
        } else {
            return new EmptyItemHolder(v);
        }
    }

    /**
     * 显示异常布局（isShowError true：异常布局；false：空布局）
     */
    public void showErrorView(boolean isShowError) {
        if (isShowError) {
            errorState = 2;
        } else {
            errorState = 1;
        }
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(BaseHolder<ReplyBean> holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case TYPE_DATA:
                holder.setData(mInfos.get(position), position);
                break;
            case TYPE_EMPTY:
                if (errorState == 2) {
                    holder.setData(null, Constant.ERROR_NETWORK_COMMENT);
                } else if (errorState == 1) {
                    holder.setData(null, Constant.ERROR_EMPTY_COMMENT);
                }
                break;
            case REPLY_EMPTY:
                break;
        }
    }

    //防止滑动时因为点赞过的itemview复用导致出现动画
    //size>holder.getLayoutPosition:防止用户刷新刚发表的假数据时由于info.size小于holder.getLayoutPosition造成数组越界异常
    @Override
    public void onViewRecycled(@NonNull BaseHolder<ReplyBean> holder) {
        super.onViewRecycled(holder);
        if (holder instanceof ReplyDetailHolder)
            if (mInfos != null && mInfos.size() > holder.getLayoutPosition())
                mInfos.get(holder.getLayoutPosition()).setAnimated(false);
    }

    @Override
    public int getLayoutId(int viewType) {
        if (viewType == TYPE_DATA) {
            return R.layout.item_reply_details;
        } else if (viewType == REPLY_EMPTY) {
            return R.layout.item_reply_empty;
        } else {
            return R.layout.view_empty;
        }
    }
}
