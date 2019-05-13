package com.dzkandian.mvp.news.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;

import com.dzkandian.R;
import com.dzkandian.common.uitls.Constant;
import com.dzkandian.mvp.mine.ui.adapter.EmptyItemHolder;
import com.dzkandian.storage.bean.UserInfoBean;
import com.dzkandian.storage.bean.news.CommentBean;
import com.dzkandian.storage.bean.news.ReplyBean;
import com.jess.arms.base.BaseHolder;
import com.jess.arms.base.DefaultAdapter;
import com.jess.arms.utils.ArmsUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 评论列表adapter
 * Created by Administrator on 2018/8/28.
 */
public class CommentAdapter extends DefaultAdapter<CommentBean> {

    private static final int TYPE_DATA = 0;//数据类型
    private static final int TYPE_EMPTY = 1;//空数据类型（空数据，网络异常）

    private int errorState;//是否显示异常布局（默认为0，不填充异常数据；1：空列表；2：网络异常）

    @Override
    public int getItemCount() {
        return mInfos.size() == 0 ? 1 : mInfos.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mInfos.size() == 0 ? TYPE_EMPTY : TYPE_DATA;
    }

    public CommentAdapter(List<CommentBean> infos, Context context) {
        super(infos);
    }

    @Override
    public BaseHolder<CommentBean> getHolder(View v, int viewType) {
        return viewType == TYPE_DATA ? new CommentViewHolder(v) : new EmptyItemHolder(v);
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
    public void onBindViewHolder(BaseHolder<CommentBean> holder, int position) {
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
        }
    }

    @Override
    public int getLayoutId(int viewType) {
        return viewType == TYPE_DATA ? R.layout.item_news_comment_view : R.layout.view_empty;
    }



    //回复更新楼层
    public void updateReplyStatus(int position, String content, UserInfoBean userInfoBean) {
        if (userInfoBean != null) {
            ReplyBean replyBean = new ReplyBean();
            if (!TextUtils.isEmpty(userInfoBean.getUsername())) {
                replyBean.setUserName(userInfoBean.getUsername());
            } else {
                replyBean.setUserName("我");
            }
            replyBean.setContent(content);
            List<ReplyBean> replyList = mInfos.get(position).getReplyList();
            if (null == replyList) {
                replyList = new ArrayList<>();
            }
            replyList.add(0, replyBean);
        }
        notifyItemChanged(position);
    }

    //防止滑动时因为点赞过的itemview复用导致出现动画
    //size>holder.getLayoutPosition:防止用户刷新刚发表的假数据后刷新由于info.size小于holder.getLayoutPosition造成数组越界异常
    //注：调用notifyItemChange,removed等会触发此方法
    @Override
    public void onViewRecycled(@NonNull BaseHolder<CommentBean> holder) {
        super.onViewRecycled(holder);
        if (holder instanceof CommentViewHolder) {
            if (mInfos != null && mInfos.size() > holder.getLayoutPosition()) {
                mInfos.get(holder.getLayoutPosition()).setHasAnimate(false);
            }
        }
    }
}
