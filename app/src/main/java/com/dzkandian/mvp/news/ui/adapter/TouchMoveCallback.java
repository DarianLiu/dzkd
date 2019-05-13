package com.dzkandian.mvp.news.ui.adapter;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.dzkandian.R;

import java.util.Collections;
import java.util.List;

import timber.log.Timber;


/**
 * RecyclerView拖拽滑动回调
 * Created by liuli on 2018/5/7.
 */
public class TouchMoveCallback extends ItemTouchHelper.Callback {


    private ColumnManageAdapter adapter;
    private List<String> datas;

    private ColumnAdapter mAdapter;

    private int dragFlags;
    private int swipeFlags;
    private boolean up;//手指抬起标记位

    public TouchMoveCallback(@NonNull ColumnManageAdapter adapter) {
        this.adapter = adapter;
        this.datas = adapter.getColumnList();
    }

    public TouchMoveCallback(@NonNull ColumnAdapter adapter) {
        this.mAdapter = adapter;
        this.datas = adapter.getColumnList();
    }

    /**
     * 设置item是否处理拖拽事件和滑动事件，以及拖拽和滑动操作的方向
     */
    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        //判断 recyclerView的布局管理器数据
        if (recyclerView.getLayoutManager() instanceof GridLayoutManager &&
                (viewHolder instanceof ColumnManageAdapter.AddedViewHolder
                        || viewHolder instanceof ColumnAdapter.ColumnViewHolder)) {//设置能拖拽的方向
            dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
            swipeFlags = 0;//0则不响应事件
        }
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    /**
     * 当用户从item原来的位置拖动可以拖动的item到新位置的过程中调用
     * 拖动某个item的回调，在return前要更新item位置，调用notifyItemMoved（draggedPosition，targetPosition）
     *
     * @param viewHolder:正在拖动item
     * @param target：要拖到的目标
     * @return true 表示消费事件
     */
    @Override
    public boolean onMove(RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        if (target instanceof ColumnManageAdapter.AddedViewHolder || target instanceof ColumnAdapter.ColumnViewHolder) {
            int fromPosition = viewHolder.getAdapterPosition();//得到item原来的position
            int toPosition = target.getAdapterPosition();//得到目标position

            if (toPosition >= datas.size()) {
                toPosition = datas.size() - 1;
            }
            if (fromPosition < toPosition) {
                for (int i = fromPosition; i < toPosition; i++) {
                    Collections.swap(datas, i, i + 1);
                    Timber.d("======fromPosition：" + fromPosition + " =====toPosition：" + toPosition);
//                    Timber.d("======移动：" + i + " 到：" + (i + 1));
                }
            } else {
                for (int i = fromPosition; i > toPosition; i--) {
                    Collections.swap(datas, i, i - 1);
                    Timber.d("======fromPosition：" + fromPosition + " =====toPosition：" + toPosition);
//                    Timber.d("======移动：" + i + " 到：" + (i - 1));
                }
            }

            if (adapter != null)
                adapter.notifyItemMoved(fromPosition, toPosition);

            if (mAdapter != null)
                mAdapter.notifyItemMoved(fromPosition, toPosition);
        }
        return true;
    }

    /**
     * 设置是否支持长按拖拽
     */
    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    /**
     * 滑动
     */
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

    }

    /**
     * 当用户与item的交互结束并且item也完成了动画时调用
     */
    @Override
    public void clearView(@NonNull RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        if (recyclerView.getScrollState() != RecyclerView.SCROLL_STATE_IDLE || !recyclerView.isComputingLayout()) {
            if (adapter != null)
                adapter.notifyDataSetChanged();
            if (mAdapter != null) {
                mAdapter.notifyDataSetChanged();
                Context context = ((ColumnAdapter.ColumnViewHolder) viewHolder).itemView.getContext();
                ((ColumnAdapter.ColumnViewHolder) viewHolder).tvName.setTextColor(context.getResources().getColor(R.color.color_text_title));
            }

        }

        initData();
        if (dragListener != null) {
            dragListener.clearView();
        }


    }

    /**
     * 重置
     */
    private void initData() {
        if (dragListener != null) {
//            dragListener.deleteState(false);
            dragListener.dragState(false);
        }
        up = false;
    }

    /**
     * 自定义拖动与滑动交互
     */
    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if (null == dragListener) {
            return;
        }

//        //RecyclerView高度
//        float rvHeight = recyclerView.getHeight();
////        Timber.d("======RecyclerView高度：" + rvHeight);
//        //item底部距离recyclerView顶部高度
//        float itemBottom = viewHolder.itemView.getBottom();
////        Timber.d("======item底部距离recyclerView顶部高度：" + itemBottom);
//        //删除控件的高度
//        float deleteHeight = viewHolder.itemView.getContext().getResources()
//                .getDimensionPixelSize(R.dimen.btn_delete_height);
////        Timber.d("======删除处的高度：" + deleteHeight);
//        //拖到删除处所需要拖拽的偏移量
//        float needDragHeight = rvHeight - itemBottom - deleteHeight;
////        Timber.d("=========拖到删除处需要拖拽的偏移量" + needDragHeight);
////        Timber.d("======拖拽Y轴偏移量：" + dY);
////        if (dY >= needDragHeight) {//拖到删除处
////            dragListener.deleteState(true);
////            if (up) {//在删除处放手，则删除item
//////                viewHolder.itemView.setVisibility(View.INVISIBLE);//先设置不可见，如果不设置的话，会看到viewHolder返回到原位置时才消失，因为remove会在viewHolder动画执行完成后才将viewHolder删除
////                adapter.deleteColumn(viewHolder.getAdapterPosition());
////                initData();
////                return;
////            }
////        } else {//没有到删除处
//////            if (View.INVISIBLE == viewHolder.itemView.getVisibility()) {//如果viewHolder不可见，则表示用户放手，重置删除区域状态
//////                dragListener.dragState(false);
//////            }
////            dragListener.deleteState(false);
////        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }

    /**
     * 当长按选中item的时候（拖拽开始的时候）调用
     */
    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        if (ItemTouchHelper.ACTION_STATE_DRAG == actionState && dragListener != null) {
            dragListener.dragState(true);
            viewHolder.itemView.setBackgroundResource(R.drawable.shape_column_pressed);
            if (mAdapter != null) {
                Context context = ((ColumnAdapter.ColumnViewHolder) viewHolder).itemView.getContext();
                ((ColumnAdapter.ColumnViewHolder) viewHolder).tvName.setTextColor(context.getResources().getColor(R.color.colorPrimary));
            }
        }
        super.onSelectedChanged(viewHolder, actionState);
    }


    /**
     * 设置手指离开后ViewHolder的动画时间，在用户手指离开后调用
     */
    @Override
    public long getAnimationDuration(RecyclerView recyclerView, int animationType, float animateDx, float animateDy) {
        //手指放开
        up = true;
        return super.getAnimationDuration(recyclerView, animationType, animateDx, animateDy);
    }

    public interface DragListener {
//        /**
//         * 用户是否将 item拖动到删除处，根据状态改变颜色
//         */
//        void deleteState(boolean delete);

        /**
         * 是否于拖拽状态
         */
        void dragState(boolean start);

        /**
         * 当用户与item的交互结束并且item也完成了动画时调用
         */
        void clearView();
    }

    private DragListener dragListener;

    public void setDragListener(DragListener dragListener) {
        this.dragListener = dragListener;
    }
}
