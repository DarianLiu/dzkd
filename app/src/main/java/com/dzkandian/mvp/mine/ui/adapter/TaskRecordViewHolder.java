package com.dzkandian.mvp.mine.ui.adapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.dzkandian.R;
import com.dzkandian.storage.bean.mine.TaskRecordBean;
import com.jess.arms.base.BaseHolder;

import butterknife.BindView;

/**
 * 收益明显ItemHolder
 * Created by Administrator on 2018/4/30.
 */

class TaskRecordViewHolder extends BaseHolder<TaskRecordBean> {


    @Nullable
    @BindView(R.id.mine_task_record_num)
    TextView mineTaskRecordNum;
    @Nullable
    @BindView(R.id.mine_task_record_num1)
    TextView mineTaskRecordNum1;
    @Nullable
    @BindView(R.id.mine_task_record_date)
    TextView mineTaskRecordDate;

    public TaskRecordViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    @Override
    public void setData(@NonNull TaskRecordBean data, int position) {

        mineTaskRecordNum.setText(data.getReward() + "");
        mineTaskRecordNum1.setText(data.getDescribe());
//        mineTaskRecordDate.setText(data.getId().substring(0, 10));//截取年月日时间
        mineTaskRecordDate.setText(data.getId());//不截取

    }
}
