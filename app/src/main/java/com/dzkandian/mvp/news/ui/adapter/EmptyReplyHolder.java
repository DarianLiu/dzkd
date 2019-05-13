package com.dzkandian.mvp.news.ui.adapter;

import android.view.View;
import android.widget.LinearLayout;

import com.dzkandian.R;
import com.jess.arms.base.BaseHolder;

import butterknife.BindView;

/**
 * 回复详情页里面的 当 有根评论 没有回复时；
 * Created by Administrator on 2018/12/11.
 */

public class EmptyReplyHolder extends BaseHolder {
    @BindView(R.id.layout_error_view)
    LinearLayout linearLayout;

    public EmptyReplyHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void setData(Object data, int position) {

    }
}
