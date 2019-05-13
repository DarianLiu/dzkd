package com.dzkandian.mvp.mine.ui.adapter;

import android.view.View;

import com.dzkandian.R;
import com.dzkandian.storage.bean.CollectionVideoBean;
import com.jess.arms.base.BaseHolder;
import com.jess.arms.base.DefaultAdapter;

import java.util.List;

/**
 * Created by Administrator on 2018/10/30.
 */

public class CollectionShortAdapter extends DefaultAdapter<CollectionVideoBean> {

    public CollectionShortAdapter(List<CollectionVideoBean> infos) {
        super(infos);
    }

    @Override
    public BaseHolder<CollectionVideoBean> getHolder(View v, int viewType) {
        return new CollectionShortHolder(v);
    }

    @Override
    public void onBindViewHolder(BaseHolder<CollectionVideoBean> holder, int position) {
        if (holder instanceof CollectionShortHolder) {
            holder.setData(mInfos.get(position), position);
        }
    }

    @Override
    public int getLayoutId(int viewType) {
        return R.layout.item_collection_short;
    }
}
