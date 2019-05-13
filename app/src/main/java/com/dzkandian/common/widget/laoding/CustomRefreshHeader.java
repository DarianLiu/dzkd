package com.dzkandian.common.widget.laoding;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.dzkandian.R;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshKernel;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.util.DensityUtil;


/**
 * 自定义个性化刷新头部
 * Created by liuli on 2018/5/4.
 */
public class CustomRefreshHeader extends LinearLayout implements RefreshHeader {

    private ImageView mProgressView;//刷新动画视图
    private AnimationDrawable anim;//刷新动画


    public CustomRefreshHeader(@NonNull Context context) {
        super(context);
        initView(context);
    }

    public CustomRefreshHeader(@NonNull Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public CustomRefreshHeader(@NonNull Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public void initView(@NonNull Context context) {
        setGravity(Gravity.CENTER);
        mProgressView = new ImageView(context);
        anim = (AnimationDrawable) context.getResources().getDrawable(R.drawable.animation_refresh);
        mProgressView.setImageDrawable(anim);
        addView(mProgressView);
        setMinimumHeight(DensityUtil.dp2px(60));
    }


    /**
     * 获取实体视图
     *
     * @return 实体视图
     */
    @NonNull
    @Override
    public View getView() {
        return this;//真实的视图就是自己，不能返回null
    }

    /**
     * 获取变换方式 {@link SpinnerStyle} 必须返回 非空
     *
     * @return 变换方式
     */
    @NonNull
    @Override
    public SpinnerStyle getSpinnerStyle() {
        return SpinnerStyle.Translate;//指定为平移，不能null
    }

    /**
     * 设置主题颜色
     *
     * @param colors 对应Xml中配置的 srlPrimaryColor srlAccentColor
     */
    @Override
    public void setPrimaryColors(int... colors) {

    }

    /**
     * 尺寸定义完成 （如果高度不改变（代码修改：setHeader），只调用一次, 在RefreshLayout#onMeasure中调用）
     *
     * @param kernel        RefreshKernel
     * @param height        HeaderHeight or FooterHeight
     * @param maxDragHeight 最大拖动高度
     */
    @Override
    public void onInitialized(@NonNull RefreshKernel kernel, int height, int maxDragHeight) {

    }

    /**
     * 手指拖动下拉（会连续多次调用，添加isDragging并取代之前的onPulling、onReleasing）
     *
     * @param isDragging    true 手指正在拖动 false 回弹动画
     * @param percent       下拉的百分比 值 = offset/footerHeight (0 - percent - (footerHeight+maxDragHeight) / footerHeight )
     * @param offset        下拉的像素偏移量  0 - offset - (footerHeight+maxDragHeight)
     * @param height        高度 HeaderHeight or FooterHeight
     * @param maxDragHeight 最大拖动高度
     */
    @Override
    public void onMoving(boolean isDragging, float percent, int offset, int height, int maxDragHeight) {

    }

    /**
     * 释放时刻（调用一次，将会触发加载）
     *
     * @param refreshLayout RefreshLayout
     * @param height        高度 HeaderHeight or FooterHeight
     * @param maxDragHeight 最大拖动高度
     */
    @Override
    public void onReleased(@NonNull RefreshLayout refreshLayout, int height, int maxDragHeight) {

    }

    /**
     * 开始动画
     *
     * @param refreshLayout RefreshLayout
     * @param height        HeaderHeight or FooterHeight
     * @param maxDragHeight 最大拖动高度
     */
    @Override
    public void onStartAnimator(@NonNull RefreshLayout refreshLayout, int height, int maxDragHeight) {
        if (!anim.isRunning()) {
            anim.start();
        }
    }

    /**
     * 动画结束
     *
     * @param refreshLayout RefreshLayout
     * @param success       数据是否成功刷新或加载
     * @return 完成动画所需时间 如果返回 Integer.MAX_VALUE 将取消本次完成事件，继续保持原有状态
     */
    @Override
    public int onFinish(@NonNull RefreshLayout refreshLayout, boolean success) {
        if (anim.isRunning()) {
            anim.stop();
        }
        return 100;//延迟500毫秒之后再弹回
    }

    /**
     * 水平方向的拖动
     *
     * @param percentX  下拉时，手指水平坐标对屏幕的占比（0 - percentX - 1）
     * @param offsetX   下拉时，手指水平坐标对屏幕的偏移（0 - offsetX - LayoutWidth）
     * @param offsetMax 最大的偏移量
     */
    @Override
    public void onHorizontalDrag(float percentX, int offsetX, int offsetMax) {

    }

    /**
     * 是否支持水平方向的拖动（将会影响到onHorizontalDrag的调用）
     *
     * @return 水平拖动需要消耗更多的时间和资源，所以如果不支持请返回false
     */
    @Override
    public boolean isSupportHorizontalDrag() {
        return false;
    }

    /**
     * 状态变化
     *
     * @param refreshLayout RefreshLayout
     * @param oldState      原本状态
     * @param newState      现在状态
     */
    @Override
    public void onStateChanged(@NonNull RefreshLayout refreshLayout, @NonNull RefreshState oldState, @NonNull RefreshState newState) {
        switch (newState) {
            case None:
            case PullDownToRefresh://下拉刷新
                break;
            case Refreshing://正在刷新
                break;
            case ReleaseToRefresh://释放刷新
                break;
        }
    }
}
