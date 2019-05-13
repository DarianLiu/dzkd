package com.dzkandian.mvp.news.ui.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dzkandian.R;
import com.dzkandian.app.MyApplication;
import com.dzkandian.app.config.EventBusTags;
import com.dzkandian.storage.bean.SearchHistoryBean;

import org.simple.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 搜索历史列表
 * Created by LiuLi on 2019/1/9.
 */

public class SearchHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<SearchHistoryBean> mSearchList = new ArrayList<>();
    private final static int normalType = 0;  // 第一种ViewType，正常的item
    private final static int deleteType = 1;  //删除的布局


    public SearchHistoryAdapter(Context context) {
        this.mContext = context;
    }

    /**
     * 第一次进入搜索界面显示搜索历史
     *
     * @param searchList 搜索历史list
     */
    public void setSearchList(List<SearchHistoryBean> searchList) {
        Collections.reverse(searchList);
        this.mSearchList = searchList;
    }

    /**
     * 搜索历史改变之后  （目前不调用；）
     *
     * @param searchList 搜索历史list
     */
    public void changeSearchList(List<SearchHistoryBean> searchList) {
        if (mSearchList != null) {
            mSearchList.clear();
        }
        Collections.reverse(searchList);
        this.mSearchList = searchList;
        notifyDataSetChanged();
    }

    public void recycle() {
        if (mSearchList != null)
            mSearchList.clear();
        this.mContext = null;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case normalType:
                return new ItemHolder(LayoutInflater.from(mContext).inflate(R.layout.item_search_history, parent, false));
            case deleteType:
                return new DeleteHolder(LayoutInflater.from(mContext).inflate(R.layout.item_search_delete, parent, false));
            default:
                return new ItemHolder(LayoutInflater.from(mContext).inflate(R.layout.item_search_history, parent, false));
        }

    }

    @Override
    public int getItemViewType(int position) {
        if (position > mSearchList.size()) {
            return deleteType;
        } else {
            return normalType;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemHolder) {
            ItemHolder itemHolder = (ItemHolder) holder;
            if (position == 0) {
                itemHolder.tvText.setText("搜索历史");
                itemHolder.tvText.setTextSize(14);
                Drawable thumb = mContext.getResources().getDrawable(R.drawable.icon_search_history);
                thumb.setBounds(0, 0, thumb.getIntrinsicWidth(), thumb.getIntrinsicHeight());
                itemHolder.tvText.setCompoundDrawables(thumb, null, null, null);
                itemHolder.tvText.setCompoundDrawablePadding(6);
                itemHolder.ivDelete.setVisibility(View.GONE);
            } else if (position <= mSearchList.size()) {
                SearchHistoryBean searchHistoryBean = mSearchList.get(position - 1);
                itemHolder.tvText.setTextSize(16);
                itemHolder.tvText.setText(searchHistoryBean.getSearchKey());

                //删除点击事件
                if (itemHolder.ivDelete != null) {
                    itemHolder.ivDelete.setOnClickListener(v -> {
                        if (position <= mSearchList.size()) {
                            MyApplication.get().getDaoSession().getSearchHistoryBeanDao().delete(searchHistoryBean);
                            mSearchList.remove(position - 1);
                            notifyItemRemoved(position);//删除动画
                            notifyDataSetChanged();
                        }
                    });
                }

                //搜索历史点击事件
                if (itemHolder.tvText != null) {
                    itemHolder.tvText.setOnClickListener(v -> {
                        if (position <= mSearchList.size()) {
                            EventBus.getDefault().post(searchHistoryBean.getSearchKey(), EventBusTags.TAG_CLICK_SEARCH_HISTORY);
                        }
                    });
                }
            }
        } else if (holder instanceof DeleteHolder) {
            ((DeleteHolder) holder).tvSearchDeleteAll.setOnClickListener(v -> {
                MyApplication.get().getDaoSession().getSearchHistoryBeanDao().deleteAll();
                mSearchList.clear();
                notifyDataSetChanged();
            });
        }
    }

    @Override
    public int getItemCount() {
        if (mSearchList.size() > 0) {
            return mSearchList.size() + 2;
        } else {
            return 0;
        }
    }

    class ItemHolder extends RecyclerView.ViewHolder {

        TextView tvText;
        ImageView ivDelete;

        ItemHolder(View itemView) {
            super(itemView);
            tvText = itemView.findViewById(R.id.tv_text);
            ivDelete = itemView.findViewById(R.id.iv_delete);
        }
    }

    class DeleteHolder extends RecyclerView.ViewHolder {

        private TextView tvSearchDeleteAll;

        DeleteHolder(View itemView) {
            super(itemView);
            tvSearchDeleteAll = itemView.findViewById(R.id.tv_search_delete_all);
        }
    }
}
