package com.dzkandian.mvp.mine.ui.adapter;

import android.view.View;

import com.dzkandian.R;
import com.dzkandian.common.uitls.Constant;
import com.dzkandian.storage.bean.mine.NotificationBean;
import com.jess.arms.base.BaseHolder;
import com.jess.arms.base.DefaultAdapter;

import java.util.List;

/**
 * Created by Administrator on 2018/12/3.
 */

public class NotificationAdapter extends DefaultAdapter<NotificationBean> {
    private static final int TYPE_DATA = 0;//数据类型
    private static final int TYPE_EMPTY = 1;//空数据类型（空数据，网络异常）
    private int errorState;//是否显示异常布局（默认为0，不填充异常数据；1：空列表；2：网络异常）

    public NotificationAdapter(List<NotificationBean> infos) {
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

    @Override
    public BaseHolder<NotificationBean> getHolder(View v, int viewType) {
        return viewType == TYPE_DATA ? new NotificationItemHolder(v) : new EmptyItemHolder(v);
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
    public void onBindViewHolder(BaseHolder<NotificationBean> holder, int position) {
        if (holder instanceof NotificationItemHolder) {
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
        return viewType == TYPE_DATA ? R.layout.item_notification : R.layout.view_empty;
    }
}
