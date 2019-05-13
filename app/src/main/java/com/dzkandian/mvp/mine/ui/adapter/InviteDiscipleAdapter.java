package com.dzkandian.mvp.mine.ui.adapter;

import android.support.annotation.NonNull;
import android.view.View;

import com.dzkandian.R;
import com.dzkandian.common.uitls.Constant;
import com.dzkandian.storage.bean.mine.ApprenticesBean;
import com.jess.arms.base.BaseHolder;
import com.jess.arms.base.DefaultAdapter;

import java.util.List;


/**
 * 邀请的徒弟列表
 * Created by Administrator on 2018/4/28 0028.
 */

public class InviteDiscipleAdapter extends DefaultAdapter<ApprenticesBean> {

    private static final int TYPE_DATA = 0;//数据类型
    private static final int TYPE_EMPTY = 1;//空数据类型（空数据，网络异常）

    private int errorState;//是否显示异常布局（默认为0，不填充异常数据；1：空列表；2：网络异常）

    public InviteDiscipleAdapter(List<ApprenticesBean> infos) {
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
    public BaseHolder<ApprenticesBean> getHolder(@NonNull View v, int viewType) {
        return viewType == TYPE_DATA ? new InviteDiscipleHolder(v) : new EmptyItemHolder(v);
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
    public void onBindViewHolder(BaseHolder<ApprenticesBean> holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case TYPE_DATA:
                holder.setData(mInfos.get(position), position);
                break;
            case TYPE_EMPTY:
                if (errorState == 2) {
                    holder.setData(null, Constant.ERROR_NETWORK);
                } else if (errorState == 1) {
                    holder.setData(null, Constant.ERROR_EMPTY_APPRENTICE);
                }
                break;
        }
    }

    @Override
    public int getLayoutId(int viewType) {
        return viewType == TYPE_DATA ? R.layout.item_invite_disciple : R.layout.view_empty;
    }
}
