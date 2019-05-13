package com.dzkandian.mvp.mine.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.dzkandian.R;
import com.dzkandian.common.uitls.AndroidUtil;
import com.dzkandian.storage.bean.mine.CoinExchangeBean;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 提现记录数据的适配器
 * Created by Administrator on 2018/4/28.
 */

public class QuickCashAdapter extends RecyclerView.Adapter<QuickCashAdapter.QuickCashItemHolder> {
    private Context context;// 上下文Context
    private List<CoinExchangeBean.ListBean> info;// 数据源
    private int selectedPos = 0;
    private OnItemClickListener onItemClickListener;

    public QuickCashAdapter(@NonNull Context context, List<CoinExchangeBean.ListBean> infos) {
        this.context = context.getApplicationContext();
        this.info = infos;
    }


    @NonNull
    @Override
    public QuickCashItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new QuickCashItemHolder(LayoutInflater.from(context).inflate(R.layout.item_cash, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull QuickCashItemHolder holder, int position) {
        //设置数据
        AndroidUtil.setTextSize(holder.tvCashItemrmb1, info.get(position).getRmb() / 100 + "", 18, " 元", 16);
        holder.tvCashItemsurplus.setText(MessageFormat.format("剩余{0}件", info.get(position).getSurplus()));

        if (position == selectedPos) {
            holder.tvCashItemrmb1.setSelected(true);
            holder.tvCashItemrmb1.setBackgroundColor(context.getResources().getColor(R.color.cash_background));
        } else {
            holder.tvCashItemrmb1.setSelected(false);
            holder.tvCashItemrmb1.setBackgroundColor(context.getResources().getColor(R.color.default_background));
        }

        //点击当前Item 切换颜色
        holder.tvCashItemrmb1.setOnClickListener(v -> {
            if (selectedPos != position) {
                selectedPos = position;
                notifyDataSetChanged();
            }
        });

         /*回调方法*/
        onItemClickListener.onClick(selectedPos);

    }

    /**
     * 获取选中项
     *
     * @return 选中项
     */
    public int getSelectedPosition() {
        return selectedPos;
    }

    @Override
    public int getItemCount() {
        return info.size();
    }

    public class QuickCashItemHolder extends RecyclerView.ViewHolder {

        TextView tvCashItemrmb1;
        TextView tvCashItemsurplus;

        public QuickCashItemHolder(@NonNull View itemView) {
            super(itemView);
            tvCashItemrmb1 = (TextView) itemView.findViewById(R.id.tv_cash_itemrmb1);
            tvCashItemsurplus = (TextView) itemView.findViewById(R.id.tv_cash_itemsurplus);
        }

    }


    /* 自定义Adapter Item的点击事件*/
    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener=onItemClickListener;
    }

    /*点击接口   把当前的position传过去 */
    public interface OnItemClickListener{
        void onClick(int position);
    }

}
