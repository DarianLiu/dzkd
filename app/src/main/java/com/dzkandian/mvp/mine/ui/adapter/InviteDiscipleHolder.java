package com.dzkandian.mvp.mine.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.dzkandian.R;
import com.dzkandian.common.uitls.AndroidUtil;
import com.dzkandian.storage.bean.mine.ApprenticesBean;
import com.jess.arms.base.BaseHolder;

import butterknife.BindView;

/**
 * Created by Administrator on 2018/4/28 0028.
 */

public class InviteDiscipleHolder extends BaseHolder<ApprenticesBean>{
    @Nullable
    @BindView(R.id.tv_chenggong_title)
    TextView tvChenggongTitle;
    @Nullable
    @BindView(R.id.tv_chenggong_gave)
    TextView tvChenggongGave;
    @Nullable
    @BindView(R.id.tv_chenggong_waitGive)
    TextView tvChenggongWaitGive;
    @Nullable
    @BindView(R.id.tv_chenggong_createTime)
    TextView tvChenggongCreateTime;
    public InviteDiscipleHolder(@NonNull View itemView) {
        super(itemView);
    }

    @Override
    public void setData(@NonNull ApprenticesBean data, int position) {
        Context context = tvChenggongGave.getContext();
        tvChenggongTitle.setText(data.getTitle());
        AndroidUtil.setTextSizeColor(tvChenggongGave
                ,new String[]{"该徒弟已提供",data.getProfit()+"","金币"}
                ,new int[]{context.getResources().getColor(R.color.color_text_title),context.getResources().getColor(R.color.color_text_red),context.getResources().getColor(R.color.color_text_title)}
                ,new int[]{14,14,14});
        if (data.getWaitGive()>0){
            AndroidUtil.setTextSizeColor(tvChenggongWaitGive
                    ,new String[]{"等待发放奖励",data.getWaitGive()+"","金币"}
                    ,new int[]{context.getResources().getColor(R.color.color_text_title),context.getResources().getColor(R.color.color_text_red),context.getResources().getColor(R.color.color_text_title)}
                    ,new int[]{12,12,12});
        }else {
            tvChenggongWaitGive.setText("邀请收益发放完毕");
            tvChenggongWaitGive.setTextColor(context.getResources().getColor(R.color.color_text_title));
        }
        tvChenggongCreateTime.setText(data.getCreateTime().substring(0,16));
    }
}
