package com.dzkandian.mvp.mine.ui.adapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.TextView;

import com.dzkandian.R;
import com.dzkandian.storage.bean.mine.InviteProfitBean;
import com.jess.arms.base.BaseHolder;

import butterknife.BindView;

/**
 * Created by Administrator on 2018/4/28 0028.
 */

public class InviteEarningsHolder extends BaseHolder<InviteProfitBean>{
    @Nullable
    @BindView(R.id.tv_zongsy_title)
    TextView tvZongsyTitle;
    @Nullable
    @BindView(R.id.tv_zongsy_describe)
    TextView tvZongsyDescribe;
    @Nullable
    @BindView(R.id.tv_zongsy_createTime)
    TextView tvZongsyCreateTime;
    public InviteEarningsHolder(@NonNull View itemView) {
        super(itemView);
    }

    @Override
    public void setData(@NonNull InviteProfitBean data, int position) {
        tvZongsyTitle.setText(data.getTitle());
        tvZongsyDescribe.setText(text(data.getDescribe()));
        tvZongsyCreateTime.setText(data.getCreateTime().substring(0,10));
    }

    /*字体颜色转换*/
    private Spanned text(String str){
        return Html.fromHtml(str);
    }
}
