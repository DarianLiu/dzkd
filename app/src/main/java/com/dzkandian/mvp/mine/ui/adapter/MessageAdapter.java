package com.dzkandian.mvp.mine.ui.adapter;

import android.support.annotation.NonNull;
import android.view.View;

import com.dzkandian.R;
import com.dzkandian.common.uitls.Constant;
import com.dzkandian.storage.bean.mine.MessageBean;
import com.jess.arms.base.BaseHolder;
import com.jess.arms.base.DefaultAdapter;

import java.util.List;

/**
 * Created by Administrator on 2018/6/7 0007.
 * 我的消息adapter
 */

public class MessageAdapter extends DefaultAdapter<MessageBean> {
    private static final int TYPE_EMPTY = 0;//空数据类型（空数据，网络异常）
    private static final int TYPE_DATA = 1;//数据类型
    private int errorState;//是否显示异常布局（默认为0，不填充异常数据；1：空列表；2：网络异常）

    public MessageAdapter(List<MessageBean> infos) {
        super(infos);
    }

    @Override
    public int getItemCount() {
        return mInfos.size() == 0 ? 1 : mInfos.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mInfos.size() == 0 ? TYPE_EMPTY : TYPE_DATA;
    }

    @NonNull
    @Override
    public BaseHolder<MessageBean> getHolder(@NonNull View v, int viewType) {
        return viewType == TYPE_DATA ? new MessageItemHolder(v) : new EmptyItemHolder(v);
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
    public void onBindViewHolder(BaseHolder<MessageBean> holder, int position) {
        if (holder instanceof MessageItemHolder) {
            holder.setData(mInfos.get(position), position);
        } else if (holder instanceof EmptyItemHolder) {
            if (errorState == 2) {
                holder.setData(null, Constant.ERROR_NETWORK);
            } else if (errorState == 1) {
                holder.setData(null, Constant.ERROR_EMPTY_MESSAGE);
            }
        }
    }

    @Override
    public int getLayoutId(int viewType) {
        return viewType == TYPE_DATA ? R.layout.item_message_reply_praise : R.layout.view_empty;
    }

}
