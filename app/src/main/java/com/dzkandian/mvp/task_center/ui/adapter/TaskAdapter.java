package com.dzkandian.mvp.task_center.ui.adapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.dzkandian.R;
import com.dzkandian.storage.bean.task.TaskBean;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;


/**
 * 任务列表适配器
 * Created by liuli on 2018/4/24.
 */

public class TaskAdapter extends BaseAdapter {

    private int noviceTaskSize;
    private List<TaskBean> mList;
    private SoftReference<Fragment> mContext;
    private OnItemViewClickListener onItemViewClickListener;

    private static final int TYPE_TASK_HEAD = 0;
    private static final int TYPE_TASK = 1;

    public TaskAdapter(@NonNull Fragment context) {
        this.mContext = new SoftReference<>(context);
        this.mList = new ArrayList<>();
    }

    @Override
    public int getCount() {
        if (noviceTaskSize != 0) {
            return mList.size() + 2;
        } else {
            return mList.size() + 1;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        //给日常任务、新手任务添加头部
        if (position == 0) {
            return TYPE_TASK_HEAD;
        } else if (noviceTaskSize != 0 && position == noviceTaskSize + 1) {
            return TYPE_TASK_HEAD;
        } else {
            return TYPE_TASK;
        }
    }

    /**
     * 添加数据
     *
     * @param dailyTaskList  日常任务列表
     * @param noviceTaskList 新手任务列表
     */
    public void addData(@NonNull List<TaskBean> dailyTaskList, @NonNull List<TaskBean> noviceTaskList) {
        mList.clear();
        this.noviceTaskSize = noviceTaskList.size();
        mList.addAll(noviceTaskList);
        mList.addAll(dailyTaskList);
        notifyDataSetChanged();
    }

    /**
     * 移除指定项任务
     *
     * @param position 数据的指向位置（注意：不是Adapter的position）
     */
    private void removeNoviceTask(int position) {
        mList.remove(position);
        noviceTaskSize--;
    }

    /**
     * 更新任务状态
     *
     * @param position Adapter的position（注意：不是数据的position）
     * @param state    (1：待领取,2:已领取)
     */
    public void updateTaskState(int position, int state) {
        int realPosition = getRealPosition(position);
//        Timber.d("==========任务—" + realPosition + "—" + getItem(position).getName());
        mList.get(realPosition).setReceive(state);
        if (0 < position && position <= noviceTaskSize && state == 2) {
            removeNoviceTask(realPosition);
        }
        notifyDataSetChanged();
    }

    @Nullable
    @Override
    public TaskBean getItem(int position) {
//        int realPosition = getRealPosition(position);
//        if (realPosition == -1) {
//            return null;
//        } else {
//            return mList.get(realPosition);
//        }
        if (position == 0) {
            return null;
        } else if (noviceTaskSize == 0) {
            return mList.get(position - 1);
        } else if (position <= noviceTaskSize) {
            return mList.get(position - 1);
        } else if (position == noviceTaskSize + 1) {
            return null;
        } else {
            return mList.get(position - 2);
        }
    }

    /**
     * @param position item的position
     * @return 数据的position
     */
    private int getRealPosition(int position) {

        if (position < noviceTaskSize) {
            return position - 1;
        } else if (position == noviceTaskSize && noviceTaskSize != 0) {
            return position - 1;
        } else if (position > noviceTaskSize && noviceTaskSize != 0) {
            return position - 2;
        } else if (position > noviceTaskSize && noviceTaskSize == 0) {
            return position - 1;
        } else {
            return -1;
        }

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Nullable
    @Override
    public View getView(int position, @Nullable View convertView, ViewGroup parent) {
        int viewType = getItemViewType(position);
        if (viewType == TYPE_TASK_HEAD) {
            TypeViewHolder holder;
            if (convertView == null) {
                holder = new TypeViewHolder();
                convertView = LayoutInflater.from(mContext.get().getContext().getApplicationContext()).inflate(R.layout.include_task_type, null);
                holder.tvTaskType = convertView.findViewById(R.id.tv_task_type);
                convertView.setTag(holder);
            } else {
                holder = (TypeViewHolder) convertView.getTag();
            }

            if (noviceTaskSize == 0) {
                holder.tvTaskType.setText(R.string.task_daily);
            } else if (position == 0) {
                holder.tvTaskType.setText(R.string.task_novice);
            } else {
                holder.tvTaskType.setText(R.string.task_daily);
            }
            return convertView;
        } else {
            TaskViewHolder holder;
            if (convertView == null) {
                holder = new TaskViewHolder();
                convertView = LayoutInflater.from(mContext.get().getContext().getApplicationContext()).inflate(R.layout.item_task, null);
                holder.rlTaskTop = convertView.findViewById(R.id.rl_task_top);
                holder.tvTaskName = convertView.findViewById(R.id.tv_task_name);
                holder.tvTaskCoin = convertView.findViewById(R.id.tv_task_coin);
                holder.ivTaskArrow = convertView.findViewById(R.id.iv_task_arrow);
                holder.tvTaskDescribe = convertView.findViewById(R.id.tv_task_describe);
                holder.btnTaskFinish = convertView.findViewById(R.id.btn_task_finish);
                holder.ivTaskImage = convertView.findViewById(R.id.iv_task_image);
                convertView.setTag(holder);
            } else {
                holder = (TaskViewHolder) convertView.getTag();
            }

            TaskBean taskBean = getItem(position);
            Glide.with(mContext.get()).asDrawable()
                    .load(taskBean.getIcon())
                    .apply(new RequestOptions().centerCrop()
                            .diskCacheStrategy(DiskCacheStrategy.RESOURCE))
                    .into(holder.ivTaskImage);
            holder.tvTaskName.setText(taskBean.getName());
            holder.tvTaskCoin.setText(taskBean.getProfit());
            if (taskBean.isExpend()) {
                holder.tvTaskDescribe.setVisibility(View.VISIBLE);
                holder.tvTaskName.setGravity(Gravity.TOP);
                holder.ivTaskArrow.setSelected(true);
            } else {
                holder.tvTaskDescribe.setVisibility(View.GONE);
                holder.ivTaskArrow.setSelected(false);
                holder.tvTaskName.setGravity(Gravity.CENTER);
            }

            holder.tvTaskDescribe.setText(taskBean.getDescribe());
            holder.btnTaskFinish.setText(taskBean.getBtnText());

            //收缩展开点击事件处理
            holder.rlTaskTop.setOnClickListener(v -> {
                if (taskBean.isExpend()) {
                    taskBean.setExpend(false);
                    holder.ivTaskArrow.setSelected(false);
                    holder.tvTaskDescribe.setVisibility(View.GONE);
                    holder.tvTaskName.setGravity(Gravity.CENTER);
                } else {
                    taskBean.setExpend(true);
                    holder.ivTaskArrow.setSelected(true);
                    holder.tvTaskDescribe.setVisibility(View.VISIBLE);
                    holder.tvTaskName.setGravity(Gravity.TOP);
                }
            });

            if (taskBean.getReceive() == 2) {
                holder.btnTaskFinish.setEnabled(true);
                holder.btnTaskFinish.setText(R.string.btn_receive_already);
//                Timber.d("==========任务状态更新—" + getRealPosition(position) + "—" + taskBean.getName());
                holder.btnTaskFinish.setBackground(mContext.get().getResources().getDrawable(R.drawable.shape_btn_unenable));
            } else if (taskBean.getReceive() == 1) {
                holder.btnTaskFinish.setEnabled(true);
                holder.btnTaskFinish.setText(R.string.btn_receive_reward);
                holder.btnTaskFinish.setBackground(mContext.get().getResources().getDrawable(R.drawable.shape_btn_receive));
            } else {
                holder.btnTaskFinish.setEnabled(true);
                holder.btnTaskFinish.setBackground(mContext.get().getResources().getDrawable(R.drawable.shape_btn_normal));
            }

            holder.btnTaskFinish.setOnClickListener(v ->
                    onItemViewClickListener.OnClick(position, taskBean));
            return convertView;
        }

    }

    public void setOnItemViewClickListener(OnItemViewClickListener onItemViewClickListener) {
        this.onItemViewClickListener = onItemViewClickListener;
    }

    public interface OnItemViewClickListener {
        void OnClick(int position, TaskBean taskBean);
    }

    private class TaskViewHolder {
        LinearLayout rlTaskTop;
        TextView tvTaskName;
        TextView tvTaskCoin;
        ImageView ivTaskArrow;
        TextView tvTaskDescribe;
        Button btnTaskFinish;
        ImageView ivTaskImage;

    }

    private class TypeViewHolder {
        TextView tvTaskType;
    }
}
