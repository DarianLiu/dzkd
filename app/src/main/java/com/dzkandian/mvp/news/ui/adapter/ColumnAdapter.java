package com.dzkandian.mvp.news.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dzkandian.R;
import com.dzkandian.app.config.EventBusTags;
import com.dzkandian.common.uitls.AndroidUtil;
import com.dzkandian.storage.event.ColumnEvent;
import com.jess.arms.utils.ArmsUtils;

import org.simple.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;


/**
 * 栏目管理
 * Created by liuli on 2018/5/7.
 */
public class ColumnAdapter extends RecyclerView.Adapter<ColumnAdapter.ColumnViewHolder> {

    private Context mContext;
    private List<String> mList;
    private int type;//栏目类型（我的栏目、可添加栏目）

    private boolean enableDelete;//删除开关
    private ColumnItemClickCallback callback;
    //    private onClickItemListener listener;
    private String title_type;
    private long shanchuTimes;//点击删除栏目的上一次时间；

    public ColumnAdapter(@NonNull Context context, int type, ColumnItemClickCallback callback, String title) {
        this.mContext = context;
        this.mList = new ArrayList<>();
        this.type = type;
        this.callback = callback;
        this.title_type = title;
    }

    public void setData(@NonNull List<String> list) {
        mList.addAll(list);
        notifyDataSetChanged();
    }

    /**
     * 设置是否可删除
     *
     * @param enableDelete 删除开关
     */
    public void setEnableDelete(boolean enableDelete) {
        if (enableDelete != this.enableDelete) {
            this.enableDelete = enableDelete;
            notifyItemRangeChanged(0, mList.size());
        }
    }

    /**
     * 添加栏目
     *
     * @param columnName 栏目名
     */
    public void addColumn(String columnName) {
        mList.add(mList.size(), columnName);
        notifyDataSetChanged();
    }

    /**
     * 删除栏目
     *
     * @param position 添加项的位置
     */
    private void deleteColumn(int position) {
        mList.remove(position);
        notifyDataSetChanged();
    }

    public List<String> getColumnList() {
        return mList;
    }


    @NonNull
    public List<String> getAddedColumnList() {
        return mList.subList(0, mList.size());
    }


    @NonNull
    @Override
    public ColumnViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ColumnViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_column, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ColumnViewHolder holder, int position) {
        String title = mList.get(position);
        switch (type) {
            case 101:
                holder.itemView.setBackgroundResource(R.drawable.shape_column_normal);
                holder.llColumn.setBackgroundResource(R.drawable.selector_column_normal_bg);
                holder.tvName.setText(title.contains("#") ? title.replace("#", "") : title);
                if (enableDelete) {
                    holder.ivDelete.setVisibility(View.VISIBLE);
                    holder.llColumn.setOnClickListener(v -> {
                        if (position == 0 && getAddedColumnList().size() <= 1) {
                            if (System.currentTimeMillis()-shanchuTimes>2000){
                                Toast.makeText(mContext, "最后一项栏目不能删除", Toast.LENGTH_SHORT).show();
                            }
                            shanchuTimes=System.currentTimeMillis();
                        } else {
                            if (position < mList.size()) {
                                callback.onClickColumn(mList.get(position));
                                deleteColumn(position);
                            }
                        }
                    });

                } else {
                    holder.ivDelete.setVisibility(View.GONE);
                    holder.llColumn.setOnClickListener(v -> {
                        ColumnEvent columnBean = new ColumnEvent(position, title_type);
                        EventBus.getDefault().post(columnBean, EventBusTags.TAG_COLUMN_MANAGE);

                        ArmsUtils.obtainAppComponentFromContext(mContext).appManager().getTopActivity().finish();

                    });
                }
//                holder.llColumn.setOnLongClickListener(v -> false);
                break;
            case 102:
                holder.llColumn.setBackgroundResource(R.drawable.selector_column_normal_bg);
                AndroidUtil.setTextSize(holder.tvName, mContext.getString(R.string.str_plus), 16,
                        title.contains("#") ? title.replace("#", "") : title, 14);

                holder.llColumn.setOnClickListener(v -> {
                    if (position < mList.size()) {
                        callback.onClickColumn(mList.get(position));
                        deleteColumn(position);
                    }
                });
                break;
        }
//        holder.tvName.setOnLongClickListener(v -> false);
    }


//    public interface onClickItemListener{  //点击当前Item的回调监听
//        void newsItem(int position);
//    }


    @Override
    public int getItemCount() {
        return mList.size();
    }


    public interface ColumnItemClickCallback {
        void onClickColumn(String column);
    }


    public class ColumnViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout llColumn;
        public TextView tvName;
        public ImageView ivDelete;

        public ColumnViewHolder(@NonNull View itemView) {
            super(itemView);
            llColumn = itemView.findViewById(R.id.ll_column);
            tvName = itemView.findViewById(R.id.tv_name);
            ivDelete = itemView.findViewById(R.id.iv_delete);
        }
    }
}
