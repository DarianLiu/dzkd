package com.dzkandian.mvp.mine.ui.adapter;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dzkandian.R;
import com.dzkandian.storage.bean.mine.MyOrderBean;
import com.jess.arms.base.BaseHolder;

import butterknife.BindView;

/**
 * 我的订单ItemViewHolder
 * Created by Administrator on 2018/5/2.
 */

class MyorderItemViewHolder extends BaseHolder<MyOrderBean> {
    @BindView(R.id.tv_order_type)
    TextView tvOrderType;
    @BindView(R.id.tv_order_rmb)
    TextView tvOrderRmb;
    @BindView(R.id.tv_order_date)
    TextView tvOrderDate;
    @BindView(R.id.tv_order_num)
    TextView tvOrderNum;
    @BindView(R.id.tv_order_image)
    ImageView tvOrderImage;
    @BindView(R.id.tv_order_line)
    View tvOrderLine;
    @BindView(R.id.tv_order_closemsg)
    TextView tvOrderCloseMsg;

    public MyorderItemViewHolder(@NonNull View v) {
        super(v);
    }

    @Override
    public void setData(@NonNull MyOrderBean data, int position) {
        int rmb = data.getRmb() / 100;
        String type = "";
        if (data.getChannel().equals("alipay")) {
            type = "支付宝";
        } else if (data.getChannel().equals("weixinPay")) {
            type = "微信钱包";
        }
        switch (data.getStatus()) {
            case -1:
                tvOrderType.setText("提现至" + type);
                tvOrderRmb.setText(rmb + "元");

                String time1 = data.getCloseTime();
                if (time1 != null && time1.length() >= 10) {
                    time1 = time1.substring(0, 10);
                }
                tvOrderDate.setText(time1);
                tvOrderImage.setBackgroundResource(R.drawable.icon_mine_order_fail);
                tvOrderLine.setVisibility(View.VISIBLE);
                tvOrderCloseMsg.setVisibility(View.VISIBLE);
                tvOrderCloseMsg.setText("失败原因：" + data.getCloseMsg());
                break;
            case 1:
                tvOrderType.setText("提现至" + type);
                tvOrderRmb.setText(rmb + "元");

                String time2 = data.getCreateTime();
                if (time2 != null && time2.length() >= 10) {
                    time2 = time2.substring(0, 10);
                }
                tvOrderDate.setText(time2);
                tvOrderImage.setBackgroundResource(R.drawable.icon_mine_order_inhand);
                tvOrderLine.setVisibility(View.GONE);
                tvOrderCloseMsg.setVisibility(View.GONE);
                break;
            case 2:
                tvOrderType.setText("提现至" + type);
                tvOrderRmb.setText(rmb + "元");

                String time3 = data.getFinishTime();
                if (time3 != null && time3.length() >= 10) {
                    time3 = time3.substring(0, 10);
                }
                tvOrderDate.setText(time3);
                tvOrderImage.setBackgroundResource(R.drawable.icon_mine_order_success);
                tvOrderLine.setVisibility(View.GONE);
                tvOrderCloseMsg.setVisibility(View.GONE);
                break;
            case 3:
                tvOrderType.setText("提现至" + type);
                tvOrderRmb.setText(rmb + "元");

                String time4 = data.getCreateTime();
                if (time4 != null && time4.length() >= 10) {
                    time4 = time4.substring(0, 10);
                }
                tvOrderDate.setText(time4);
                tvOrderImage.setBackgroundResource(R.drawable.icon_mine_order_money);
                tvOrderLine.setVisibility(View.GONE);
                tvOrderCloseMsg.setVisibility(View.GONE);
                break;
            default:
                break;
        }
        tvOrderNum.setText(data.getId());
    }
}
