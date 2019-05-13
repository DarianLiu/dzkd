package com.dzkandian.mvp.news.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dzkandian.R;
import com.dzkandian.common.uitls.AndroidUtil;

import java.util.Collections;
import java.util.List;


/**
 * 栏目管理
 * Created by liuli on 2018/5/7.
 */
public class ColumnManageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<String> mList;
    private int mAddedSize;

    private static final int TYPE_ADDED = 101;
    private static final int TYPE_NOTICE = 102;
    private static final int TYPE_SURPLUS = 103;
    private boolean enableDelete;//删除开关
    private long shanchuTimes;//点击删除栏目的上一次时间；

    public ColumnManageAdapter(@NonNull Context context, List<String> list, int addedSize) {
        this.mContext = context.getApplicationContext();
        this.mList = list;
        this.mAddedSize = addedSize;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if (manager instanceof GridLayoutManager) {
            final GridLayoutManager gridManager = ((GridLayoutManager) manager);
            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    int type = getItemViewType(position);
                    switch (type) {
                        case TYPE_ADDED:
                        case TYPE_SURPLUS:
                            return 1;
                        case TYPE_NOTICE:
                            return 4;
                        default:
                            return 1;
                    }
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position < mAddedSize) {
            return TYPE_ADDED;
        } else if (position == mAddedSize) {
            return TYPE_NOTICE;
        } else {
            return TYPE_SURPLUS;
        }
    }

    /**
     * 设置是否可删除
     *
     * @param enableDelete 删除开关
     */
    public void setEnableDelete(boolean enableDelete) {
        if (enableDelete != this.enableDelete) {
            this.enableDelete = enableDelete;
            notifyItemRangeChanged(0, mAddedSize);
        }

    }

    /**
     * 添加栏目
     *
     * @param position 添加项的位置
     */
    private void addColumn(int position) {
        int realPosition = position - 1;
        if (position != mAddedSize) {
            for (int i = realPosition; i > mAddedSize; i--) {
                Collections.swap(mList, i, i - 1);
            }
        }
        mAddedSize += 1;
        notifyItemMoved(position, mAddedSize - 1);
        notifyItemRangeChanged(mAddedSize - 1, mList.size() + 1 - position);

    }

    /**
     * 删除栏目
     *
     * @param position 添加项的位置
     */
    private void deleteColumn(int position) {
        if (mAddedSize == 1) {
            if (System.currentTimeMillis()-shanchuTimes>2000){
                Toast.makeText(mContext, "最后一项栏目不能删除", Toast.LENGTH_SHORT).show();
            }
            shanchuTimes=System.currentTimeMillis();
//            return false;
        } else {
//            Timber.d("=======点击的位置：" + position);
//            Timber.d("=======点击前展示栏目数：" + mAddedSize);
            int totalSize = mList.size();//数据总量
            int lastColumnPos = totalSize - 1;
            for (int i = position; i < lastColumnPos; i++) {
                Collections.swap(mList, i, i + 1);
            }

            mAddedSize -= 1;
//            Timber.d("=======点击后展示栏目数：" + mAddedSize);
            notifyItemMoved(position, mList.size());
            notifyItemRangeChanged(position, totalSize + 1 - position);
        }
    }

    public int getAddedSize() {
        return mAddedSize;
    }

    public List<String> getColumnList() {
        return mList;
    }

    @NonNull
    public List<String> getAddedColumnList() {
        return mList.subList(0, mAddedSize);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_ADDED) {
            return new AddedViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_column, parent, false));
        } else if (viewType == TYPE_NOTICE) {
            return new NoticeViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_notice, parent, false));
        } else {
            return new SurPlusViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_column, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int type = getItemViewType(position);
        switch (type) {
            case TYPE_ADDED:
                if (holder instanceof AddedViewHolder) {
                    ((AddedViewHolder) holder).llColumn.setBackgroundResource(R.drawable.selector_column_normal_bg);
                    ((AddedViewHolder) holder).tvName.setText(mList.get(position));
                    if (enableDelete) {
                        ((AddedViewHolder) holder).ivDelete.setVisibility(View.VISIBLE);
                    } else {
                        ((AddedViewHolder) holder).ivDelete.setVisibility(View.GONE);
                    }
                    ((AddedViewHolder) holder).llColumn.setOnClickListener(v -> {
                        if (enableDelete) {
                            deleteColumn(position);
                        } else {
                            //跳转
                        }
                    });
                }
                break;
            case TYPE_NOTICE:
                if (holder instanceof NoticeViewHolder) {

                }
                break;
            case TYPE_SURPLUS:
                if (holder instanceof SurPlusViewHolder) {
                    ((SurPlusViewHolder) holder).llColumn.setBackgroundResource(R.drawable.selector_column_normal_bg);
                    ((SurPlusViewHolder) holder).ivDelete.setVisibility(View.GONE);
                    AndroidUtil.setTextSize(((SurPlusViewHolder) holder).tvName,
                            mContext.getString(R.string.str_plus), 15, mList.get(position - 1), 14);

                    ((SurPlusViewHolder) holder).llColumn.setOnClickListener(v -> {
                        addColumn(position);
                    });
                }
                break;
        }

    }

    @Override
    public int getItemCount() {
        return mList.size() + 1;
    }

    public class AddedViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout llColumn;
        TextView tvName;
        ImageView ivDelete;

        public AddedViewHolder(@NonNull View itemView) {
            super(itemView);
            llColumn = itemView.findViewById(R.id.ll_column);
            tvName = itemView.findViewById(R.id.tv_name);
            ivDelete = itemView.findViewById(R.id.iv_delete);
        }
    }

    public class NoticeViewHolder extends RecyclerView.ViewHolder {
        public NoticeViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public class SurPlusViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout llColumn;
        TextView tvName;
        ImageView ivDelete;

        public SurPlusViewHolder(@NonNull View itemView) {
            super(itemView);
            llColumn = itemView.findViewById(R.id.ll_column);
            tvName = itemView.findViewById(R.id.tv_name);
            ivDelete = itemView.findViewById(R.id.iv_delete);
        }
    }
}
