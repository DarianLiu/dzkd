package com.dzkandian.mvp.mine.ui.adapter;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dzkandian.R;
import com.dzkandian.app.config.EventBusTags;
import com.dzkandian.common.uitls.Constant;
import com.dzkandian.mvp.common.ui.activity.MainActivity;
import com.dzkandian.storage.event.ChangeTabEvent;
import com.jess.arms.base.BaseHolder;

import org.simple.eventbus.EventBus;

import butterknife.BindView;

/**
 * RecyclerView空布局（通用：无重试按钮）
 * Created by liuli on 2018/5/3.
 */
public class EmptyItemHolder extends BaseHolder {
    @BindView(R.id.tv_error)
    TextView tvError;
    @BindView(R.id.ll_error_view)
    LinearLayout llErrorView;
    @BindView(R.id.btn_goto_collection)
    Button button;

    public EmptyItemHolder(@NonNull View itemView) {
        super(itemView);
    }

    @Override
    public void setData(Object data, int position) {
        if (position == Constant.ERROR_NETWORK) {
            //网络异常
            tvError.setText(R.string.error_network);
            tvError.setCompoundDrawablesWithIntrinsicBounds(null,
                    tvError.getContext().getResources().getDrawable(R.drawable.icon_error_network),
                    null, null);
        } else if (position == Constant.ERROR_EMPTY_ORDER) {
            //暂无订单
            tvError.setText(R.string.error_empty_order);
            tvError.setCompoundDrawablesWithIntrinsicBounds(null,
                    tvError.getContext().getResources().getDrawable(R.drawable.icon_error_empty_order),
                    null, null);
        } else if (position == Constant.ERROR_EMPTY_PROFIT) {
            //暂无收益
            tvError.setText(R.string.error_empty_earning);
            tvError.setCompoundDrawablesWithIntrinsicBounds(null,
                    tvError.getContext().getResources().getDrawable(R.drawable.icon_error_empty_earning),
                    null, null);
        } else if (position == Constant.ERROR_EMPTY_PROFIT_APPRENTICE) {
            //暂无徒弟提供收益
            tvError.setText(R.string.error_empty_disciple_earning);
            tvError.setCompoundDrawablesWithIntrinsicBounds(null,
                    tvError.getContext().getResources().getDrawable(R.drawable.icon_error_empty_disciple_earning),
                    null, null);
        } else if (position == Constant.ERROR_EMPTY_APPRENTICE) {
            //暂无徒弟
            tvError.setText(R.string.error_empty_disciple);
            tvError.setCompoundDrawablesWithIntrinsicBounds(null,
                    tvError.getContext().getResources().getDrawable(R.drawable.icon_errorr_empty_disciple),
                    null, null);
        } else if (position == Constant.ERROR_EMPTY_NOTICE) {
            //暂无公告
            tvError.setText(R.string.error_empty_notice);
            tvError.setCompoundDrawablesWithIntrinsicBounds(null,
                    tvError.getContext().getResources().getDrawable(R.drawable.icon_errorr_empty_notice),
                    null, null);
        } else if (position == Constant.ERROR_EMPTY_MESSAGE) {
            //暂无消息
            tvError.setText(R.string.error_empty_message);
            tvError.setCompoundDrawablesWithIntrinsicBounds(null,
                    tvError.getContext().getResources().getDrawable(R.drawable.icon_errorr_empty_message),
                    null, null);
        } else if (position == Constant.ERROR_EMPTY_COMMENT) {
            //暂无评论
            tvError.setText(R.string.error_empty_comment);
            tvError.setCompoundDrawablesWithIntrinsicBounds(null,
                    tvError.getContext().getResources().getDrawable(R.drawable.icon_not_comment),
                    null, null);
        } else if (position == Constant.ERROR_NETWORK_COMMENT) {
            //评论页面网络异常
            tvError.setText(R.string.error_network_comment);
            tvError.setCompoundDrawablesWithIntrinsicBounds(null,
                    tvError.getContext().getResources().getDrawable(R.drawable.icon_netword_comment),
                    null, null);
        } else if (position == Constant.ERROR_EMPTY_COLLECTION_NEWS) {
            //暂无资讯收藏
            if (tvError != null) {
                tvError.setText(R.string.error_empty_collection);
                tvError.setCompoundDrawablesWithIntrinsicBounds(null,
                        tvError.getContext().getResources().getDrawable(R.drawable.icon_error_empty_collection),
                        null, null);
            }
            if (button != null) {
                button.setVisibility(View.VISIBLE);
                button.setOnClickListener(view -> {
                    Intent intent = new Intent(button.getContext(), MainActivity.class);
                    button.getContext().startActivity(intent);
                    EventBus.getDefault().post(new ChangeTabEvent.Builder().indexTab(0).build(), EventBusTags.TAG_CHANGE_TAB);
                });
            }
        }else if (position == Constant.ERROR_EMPTY_COLLECTION_VIDEO) {
            //暂无视频收藏
            if (tvError != null) {
                tvError.setText(R.string.error_empty_collection);
                tvError.setCompoundDrawablesWithIntrinsicBounds(null,
                        tvError.getContext().getResources().getDrawable(R.drawable.icon_error_empty_collection),
                        null, null);
            }
            if (button != null) {
                button.setVisibility(View.VISIBLE);
                button.setOnClickListener(view -> {
                    Intent intent = new Intent(button.getContext(), MainActivity.class);
                    button.getContext().startActivity(intent);
                    EventBus.getDefault().post(new ChangeTabEvent.Builder().indexTab(1).build(), EventBusTags.TAG_CHANGE_TAB);
                });
            }
        }
    }
}
