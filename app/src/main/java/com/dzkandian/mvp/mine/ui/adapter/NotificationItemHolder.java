package com.dzkandian.mvp.mine.ui.adapter;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.dzkandian.R;
import com.dzkandian.storage.bean.mine.NotificationBean;
import com.jess.arms.base.BaseHolder;

import butterknife.BindView;

/**
 * Created by Administrator on 2018/12/3
 * 系统通知
 * MessageBean
 * uid	    long	用户id
 * hideTime	Date	隐藏时间
 * title	String	标题
 * viewTime	Date	获取列表的时间
 * showTime	long	展示时长
 * content	String	回复内容
 */

public class NotificationItemHolder extends BaseHolder<NotificationBean> {
    @BindView(R.id.notification_time)
    TextView notificationTime;
    @BindView(R.id.notification_prams)
    TextView notificationPrams;
    @BindView(R.id.notification_content)
    TextView notificationContent;

    public NotificationItemHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void setData(NotificationBean data, int position) {
        if (!TextUtils.isEmpty(data.getContent())) {
            notificationContent.setText(data.getContent());//回复内容
        }
        if (!TextUtils.isEmpty(data.getTitle())) {
            notificationPrams.setText(data.getTitle());//标题
        }
        if (!TextUtils.isEmpty(data.getCreateTime())) {//时间
            notificationTime.setText(data.getCreateTime().substring(0, data.getCreateTime().indexOf(" ")));
        }
    }
}
