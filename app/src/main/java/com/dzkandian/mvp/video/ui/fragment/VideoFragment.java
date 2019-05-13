package com.dzkandian.mvp.video.ui.fragment;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidkun.xtablayout.XTabLayout;
import com.dzkandian.R;
import com.dzkandian.app.config.EventBusTags;
import com.dzkandian.common.uitls.Constant;
import com.dzkandian.common.uitls.NetworkUtils;
import com.dzkandian.common.uitls.StatusBarUtil;
import com.dzkandian.common.uitls.TimesUtils;
import com.dzkandian.common.widget.adapter.FragmentAdapter;
import com.dzkandian.common.widget.laoding.LoadingProgressDialog;
import com.dzkandian.mvp.news.ui.activity.ColumnManageActivity;
import com.dzkandian.mvp.news.ui.activity.SearchActivity;
import com.dzkandian.mvp.news.ui.fragment.ErrorNetworkFragment;
import com.dzkandian.mvp.video.contract.VideoContract;
import com.dzkandian.mvp.video.di.component.DaggerVideoComponent;
import com.dzkandian.mvp.video.di.module.VideoModule;
import com.dzkandian.mvp.video.presenter.VideoPresenter;
import com.dzkandian.storage.ColumnBean;
import com.dzkandian.storage.event.ColumnEvent;
import com.dzkandian.storage.event.FloatingActionEvent;
import com.dzkandian.storage.event.GoldAnimationEvent;
import com.dzkandian.storage.event.NetworkRetryEvent;
import com.dzkandian.storage.event.RewardMainEvent;
import com.dzkandian.storage.event.TimeRewardEvent;
import com.jess.arms.base.BaseFragment;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.jess.arms.utils.DataHelper;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

import static com.jess.arms.utils.Preconditions.checkNotNull;


public class VideoFragment extends BaseFragment<VideoPresenter> implements VideoContract.View {

    @BindView(R.id.iv_rewardtime_light)
    ImageView ivRewardtimeLight;//时段奖励的背景光
    @BindView(R.id.rl_rewardtime_bag)
    RelativeLayout rlRewardtimeBag;//时段奖励的福袋
    @BindView(R.id.iv_rewardtime_receive)
    ImageView ivRewardtimeReceive;//时段奖励的领取按钮
    @BindView(R.id.tv_rewardtime)
    TextView tvRewardTime;//时段奖励的倒计时
    @BindView(R.id.tabLayout_video)
    XTabLayout tabLayoutVideo;
    @BindView(R.id.iv_video_more)
    ImageView ivMore;
    @BindView(R.id.video_view_pager)
    ViewPager viewPager;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.tv_title_search)
    TextView tvSearch;
    @BindView(R.id.v)
    View v;

    private List<String> mColumnList;
    private List<Class> mFragments;
    private int mAddedSize;
    private FragmentAdapter mFragmentAdapter;
    private String mTextSize;//字体大小
    private LoadingProgressDialog loadingProgressDialog;


    @Nullable
    private AlertDialog.Builder normalDialog;
    private long goldAnimationLastTimes;//点击金币箱按钮的上一次时间；
    private long clickColumnManageLastTimes;//点击视频栏目管理的上一次时间；
    private AnimatorSet mAnimatorSetTranslation, mAnimatorSetScaleBig, mAnimatorSetScaleSmall;

    @Override
    public void setupFragmentComponent(@NonNull AppComponent appComponent) {
        DaggerVideoComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .videoModule(new VideoModule(this))
                .build()
                .inject(this);
    }

    @Override
    public View initView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_video, container, false);
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        v.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, StatusBarUtil.getStatusBarHeight(getActivity())));
        mTextSize = getArguments().getString("textSize");
        mColumnList = new ArrayList<>();
        mFragments = new ArrayList<>();

        if (mPresenter != null) {
            int haveTouchHardware = DataHelper.getIntergerSF(getActivity().getApplicationContext(), Constant.SP_KEY_HAVE_TOUCH_HARDWARE);
            mPresenter.getVideoTitleList(haveTouchHardware != 1 ? 0 : 1);
        }

        fab.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFFFFF")));

        /* 滑动到顶部 */
        fab.setOnClickListener(v -> {
            //发送消息
            EventBus.getDefault().post(new FloatingActionEvent.Builder().state(0).build(), EventBusTags.TAG_SHOW_STATE);//0 点击回到顶部; 1:隐藏   2：显示
            fab.setVisibility(View.GONE);
        });


        initRewardTimeAnimation();
        /*金币箱点击事件*/
        ivRewardtimeReceive.setOnClickListener(view -> {
            if (System.currentTimeMillis() - goldAnimationLastTimes > 2000) {
                goldAnimationLastTimes = System.currentTimeMillis();
                if (isInternet()) {
                    assert mPresenter != null;
                    mPresenter.timeReward("");
                } else {
                    showMessage("连接网络可领取奖励");
                }
            }
        });

        tvSearch.setOnClickListener(v -> launchActivity(new Intent(getActivity(), SearchActivity.class)));
    }

    /**
     * 更新TabLayout + ViewPager
     *
     * @param allColumn  所有资讯分类列表
     * @param viewColumn 显示的资讯分类列表
     */
    @Override
    public void updateView(List<String> allColumn, @NonNull List<String> viewColumn) {
        timeStart();

        mColumnList.clear();
        mFragments.clear();
//        updateColumn();

        mAddedSize = viewColumn.size();
        mColumnList.addAll(viewColumn);

        for (int i = 0; i < mAddedSize; i++) {
            mFragments.add(VideoBaseFragment.class);
        }

        updateColumn();

        ivMore.setOnClickListener(v -> {
            if (System.currentTimeMillis() - clickColumnManageLastTimes > 1000) {
                clickColumnManageLastTimes = System.currentTimeMillis();
                ColumnBean columnBean = DataHelper.getDeviceData(getActivity(), Constant.SP_KEY_VIDEO_COLUMN);
                if (columnBean != null) {
                    Intent intent = new Intent(getActivity(), ColumnManageActivity.class);
                    intent.putStringArrayListExtra("column", (ArrayList<String>) columnBean.getAllColumn());
                    intent.putExtra("type", "video");
                    intent.putExtra("addedSize", mAddedSize);
                    startActivityForResult(intent, 300);
                } else if (allColumn != null) {
                    Intent intent = new Intent(getActivity(), ColumnManageActivity.class);
                    intent.putStringArrayListExtra("column", (ArrayList<String>) allColumn);
                    intent.putExtra("type", "video");
                    intent.putExtra("addedSize", mAddedSize);
                    startActivityForResult(intent, 300);
                }
            }
        });

    }

    private void updateColumn() {
        if (mFragmentAdapter == null) {
            mFragmentAdapter = new FragmentAdapter(getChildFragmentManager(), mFragments, mColumnList, 1, mTextSize);
            viewPager.setAdapter(mFragmentAdapter);
            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    EventBus.getDefault().post(true, EventBusTags.TAG_BOTTOM_MENU);
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
            tabLayoutVideo.setupWithViewPager(viewPager);
        } else {
            mFragmentAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @NonNull Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 300 && resultCode == 300) {
            ArrayList<String> titles = data.getStringArrayListExtra("returnData");
            mColumnList.clear();
            mFragments.clear();

            mColumnList.addAll(titles);
            mAddedSize = titles.size();

            if (mAddedSize != 0) {
                for (int i = 0; i < mAddedSize; i++) {
                    mFragments.add(VideoBaseFragment.class);
                }
            }
            updateColumn();
        }
    }


    /**
     * 接收栏目跳转
     */
    @Subscriber(tag = EventBusTags.TAG_COLUMN_MANAGE)
    public void columnVideoEvent(@NonNull ColumnEvent event) {
        String type = event.getType();
        int position = event.getPosition();
        if (type.equals("video") && viewPager != null) {
            viewPager.setCurrentItem(position);
        }
    }


    /**
     * 接收悬乎窗状态事件（显示/隐藏）
     * 0 点击回到顶部; 1:隐藏   2：显示
     *
     * @param floatingActionEvent 刷新状态事件
     */
    @Subscriber(tag = EventBusTags.TAG_SHOW_STATE)
    public void receiveRefreshEvent(FloatingActionEvent floatingActionEvent) {
        switch (floatingActionEvent.getState()) {
            case 1:
                fab.hide();
                break;
            case 2:
                fab.show();
                break;
        }
    }


    @Override
    public void setData(@Nullable Object data) {

    }

    @Override
    public void showLoading() {
        if (loadingProgressDialog == null)
            loadingProgressDialog = new LoadingProgressDialog.Builder(getActivity()).create();
        loadingProgressDialog.show();
    }

    @Override
    public void hideLoading() {
        if (loadingProgressDialog != null && loadingProgressDialog.isShowing())
            loadingProgressDialog.dismiss();
    }

    @Override
    public void showMessage(@NonNull String message) {
        checkNotNull(message);
        ArmsUtils.makeText(getActivity().getApplicationContext(), message);
    }

    @Override
    public void launchActivity(@NonNull Intent intent) {
        checkNotNull(intent);
        ArmsUtils.startActivity(intent);
    }

    @Override
    public void killMyself() {

    }

    /**
     * 初始化时段奖励动画
     */
    private void initRewardTimeAnimation() {
        /*时段奖励福袋变大动画*/
        ObjectAnimator scaleBigX = ObjectAnimator.ofFloat(rlRewardtimeBag, "scaleX", 0.8f, 1f);
        scaleBigX.setDuration(300);
        scaleBigX.setRepeatCount(0);
        ObjectAnimator scaleBigY = ObjectAnimator.ofFloat(rlRewardtimeBag, "scaleY", 0.8f, 1f);
        scaleBigY.setDuration(300);
        scaleBigY.setRepeatCount(0);
        mAnimatorSetScaleBig = new AnimatorSet();
        mAnimatorSetScaleBig.setInterpolator(new AccelerateInterpolator());
        mAnimatorSetScaleBig.playTogether(scaleBigX, scaleBigY);

        /*时段奖励福袋变小动画*/
        ObjectAnimator scaleSmallX = ObjectAnimator.ofFloat(rlRewardtimeBag, "scaleX", 1f, 0.8f);
        scaleSmallX.setDuration(300);
        scaleSmallX.setRepeatCount(0);
        ObjectAnimator scaleSmallY = ObjectAnimator.ofFloat(rlRewardtimeBag, "scaleY", 1f, 0.8f);
        scaleSmallY.setDuration(300);
        scaleSmallY.setRepeatCount(0);
        mAnimatorSetScaleSmall = new AnimatorSet();
        mAnimatorSetScaleSmall.setInterpolator(new AccelerateInterpolator());
        mAnimatorSetScaleSmall.playTogether(scaleSmallX, scaleSmallY);

        /*时段奖励福袋跳动动画*/
        ObjectAnimator translationY = ObjectAnimator.ofFloat(rlRewardtimeBag, "translationY", 0f, -6f, 12f, -10f, 5f, 0f);
        translationY.setDuration(2000);
        translationY.setRepeatCount(ValueAnimator.INFINITE);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(ivRewardtimeLight, "alpha", 0.5f, 0.2f, 1f, 0.0f, 1f, 0.5f);
        alpha.setDuration(2000);
        alpha.setRepeatCount(ValueAnimator.INFINITE);
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(rlRewardtimeBag, "scaleX", 0.8f, 0.8f, 1f, 0.8f, 0.95f, 0.8f);
        scaleX.setDuration(2000);
        scaleX.setRepeatCount(ValueAnimator.INFINITE);
        mAnimatorSetTranslation = new AnimatorSet();
        mAnimatorSetTranslation.setInterpolator(new AccelerateDecelerateInterpolator());
        mAnimatorSetTranslation.playTogether(translationY, alpha, scaleX);
    }

    /*开始金币箱摆动*/
    public void startGoldAnimation() {
        if (mAnimatorSetScaleBig != null && (mAnimatorSetScaleBig.isStarted() || mAnimatorSetScaleBig.isRunning())) {
            mAnimatorSetScaleBig.end();
        }

        if (mAnimatorSetScaleSmall != null && (!mAnimatorSetScaleSmall.isStarted() || !mAnimatorSetScaleSmall.isRunning())) {
            mAnimatorSetScaleSmall.start();
        }

        if (mAnimatorSetTranslation != null && (!mAnimatorSetTranslation.isStarted() || !mAnimatorSetTranslation.isRunning())) {
            mAnimatorSetTranslation.setStartDelay(250);
            mAnimatorSetTranslation.start();
        }
        tvRewardTime.setVisibility(View.GONE);
        ivRewardtimeReceive.setVisibility(View.VISIBLE);
        ivRewardtimeLight.setVisibility(View.VISIBLE);
    }

    /*关闭金币箱摆动*/
    public void closeGoldAnimation() {
        if (mAnimatorSetScaleSmall != null && (mAnimatorSetScaleSmall.isStarted() || mAnimatorSetScaleSmall.isRunning())) {
            mAnimatorSetScaleSmall.end();
        }
        if (mAnimatorSetTranslation != null) {
            mAnimatorSetTranslation.end();
        }
        if (mAnimatorSetScaleBig != null && (!mAnimatorSetScaleBig.isStarted() || !mAnimatorSetScaleBig.isRunning())) {
            mAnimatorSetScaleBig.start();
        }
        tvRewardTime.setVisibility(View.VISIBLE);
        ivRewardtimeReceive.setVisibility(View.GONE);
        ivRewardtimeLight.setVisibility(View.GONE);
    }

    /*判断当前是否有网络*/
    private boolean isInternet() {
        return NetworkUtils.checkNetwork(getContext().getApplicationContext());
    }

    @Override
    public void onDestroy() {
        if (loadingProgressDialog != null && loadingProgressDialog.isShowing()) {
            loadingProgressDialog.dismiss();
        }
        if (viewPager != null)
            viewPager.clearOnPageChangeListeners();
        super.onDestroy();
        loadingProgressDialog = null;
    }

    /**
     * 无网络进入APP，显示新建的无网络Fragment;
     */
    @Override
    public void showErrorNetwork() {
        mFragments.clear();
        mColumnList.clear();

        mColumnList.add("");
        mFragments.add(ErrorNetworkFragment.class);
        mAddedSize = mColumnList.size();
        updateColumn();

    }

    /**
     * 接收无网络Fragment的重新加载按钮的事件
     *
     * @param networkRetryEvent 1  视频
     */
    @Subscriber
    private void receiveNetWorkEvent(@NonNull NetworkRetryEvent networkRetryEvent) {
        if (networkRetryEvent.getEvent() == 1 && mPresenter != null) {
            int haveTouchHardware = DataHelper.getIntergerSF(getActivity().getApplicationContext(), Constant.SP_KEY_HAVE_TOUCH_HARDWARE);
            mPresenter.getVideoTitleList(haveTouchHardware != 1 ? 0 : 1);
        }
    }

    /**
     * 接收视频中点击金币箱领取时段奖励的事件  做刷新
     *
     * @param goldAnimationEvent 1  是看点  发给  视频的
     */
    @Subscriber
    private void receiveGoldAnimationEvent(@NonNull GoldAnimationEvent goldAnimationEvent) {
        if (goldAnimationEvent.getModule() == 1) {
            closeGoldAnimation();
        }
    }


    /**
     * 接收时段奖励计时事件
     *
     * @param event 时段奖励计时事件
     */
    @Subscriber(tag = EventBusTags.TAG_TIME_REWARD_TIME_SHOW)
    private void receiveTimeRewardEvent(@NonNull TimeRewardEvent event) {
        if (!event.isTimeEnd()) {
            int minute = TimesUtils.getMinute(event.getTimeDifference());
            int second = TimesUtils.getSecond(event.getTimeDifference());
            tvRewardTime.setText(String.format("%s:%s", minute < 10 ? "0" + minute : String.valueOf(minute),
                    second < 10 ? "0" + second : String.valueOf(second)));
        } else {
            startGoldAnimation();
        }
    }

    /**
     * 接收从登录界面，或者退出登录时的事件，只要登录，或者退出登录都要亮起金币箱动画
     *
     * @param isLogin 登录
     */
    @Subscriber(tag = EventBusTags.TAG_LOGIN_STATE)
    private void receiveLoginState(boolean isLogin) {
        startGoldAnimation();
    }

    /**
     * 时段奖励返回接口
     *
     * @param number 时段奖励金币数
     */
    @Override
    public void timeRewardInt(int number) {
        closeGoldAnimation();
        //点击了金币箱，给 视频 发送点击金币箱领取时段奖励的事件
        EventBus.getDefault().post(new GoldAnimationEvent.Builder().module(2).build());
        //时段奖励领取金币的爆裂效果  number为金币数
        EventBus.getDefault().post(
                new RewardMainEvent.Builder()
                        .newRewardType("timeReward")
                        .newRewardGoid(number)
                        .build(),
                EventBusTags.TAG_COIN_REWARD);

        //获取当前服务器时间戳（每次接口请求都会保存时间戳在本地）
        String nowTime = DataHelper.getStringSF(getContext(), Constant.SP_KEY_TIME_STAMP);
        long nowTimeStamp = TextUtils.isEmpty(nowTime) ? 0 : Long.parseLong(nowTime);

        //更新领取时段奖励的时间  SP_KEY_TIME_LAST
        DataHelper.setStringSF(getContext(), Constant.SP_KEY_TIME_LAST, nowTime);

        //下个整点奖励的时间戳
        long nextHourTimeStamp = ((long) TimesUtils.getHour(nowTimeStamp) + 1) * 60 * 60 * 1000;

        //距离下个整点的时差（毫秒）
        long timeDifference = nextHourTimeStamp - nowTimeStamp;
//        Timber.d("============TimeReward - 距离下个整点的时差：( timeRewardSuccess )" + timeDifference);
        if (timeDifference > 0) {
            EventBus.getDefault().post(new TimeRewardEvent.Builder()
                    .timeDifference(timeDifference)
                    .isTimeEnd(false)
                    .build(), EventBusTags.TAG_TIME_REWARD_TIME_START);
        }
    }

    /**
     * 领取时段奖励错误返回接口    错误
     */
    @Override
    public void timeRewardError() {
        closeGoldAnimation();
        //点击了金币箱，给 视频 发送点击金币箱领取时段奖励的事件
        EventBus.getDefault().post(new GoldAnimationEvent.Builder().module(2).build());
        showMessage(getResources().getString(R.string.reward_time_already));
        //获取当前服务器时间戳（每次接口请求都会保存时间戳在本地）
        String nowTime = DataHelper.getStringSF(getContext(), Constant.SP_KEY_TIME_STAMP);
        long nowTimeStamp = TextUtils.isEmpty(nowTime) ? 0 : Long.parseLong(nowTime);

        //更新领取时段奖励的时间  SP_KEY_TIME_LAST
        DataHelper.setStringSF(getContext(), Constant.SP_KEY_TIME_LAST, nowTime);

        //下个整点奖励的时间戳
        long nextHourTimeStamp = ((long) TimesUtils.getHour(nowTimeStamp) + 1) * 60 * 60 * 1000;

        //距离下个整点的时差（毫秒）
        long timeDifference = nextHourTimeStamp - nowTimeStamp;
//        Timber.d("============TimeReward - 距离下个整点的时差：( timeRewardError )" + timeDifference);
        if (timeDifference > 0) {
            EventBus.getDefault().post(new TimeRewardEvent.Builder()
                    .timeDifference(timeDifference)
                    .isTimeEnd(false)
                    .build(), EventBusTags.TAG_TIME_REWARD_TIME_START);
        }
    }


    /**
     * 设备异常dialog
     */
    @Override
    public void showNormalDialog() {
        normalDialog = new AlertDialog.Builder(getActivity());
        normalDialog.setTitle(getResources().getString(R.string.dialog_prompt));
        normalDialog.setMessage(getResources().getString(R.string.dialog_root_reminder));
        normalDialog.setNegativeButton(getResources().getString(R.string.dialog_i_know),
                (dialog, which) -> {
                    dialog.dismiss();
                    ArmsUtils.exitApp();
                });

        normalDialog.setCancelable(false);
        // 显示
        normalDialog.show();
    }


    /**
     * 开始计时
     */
    public void timeStart() {
        //刷新时间戳， 然后刷新倒计时
//        long nowTimeStamp = Long.valueOf(DataHelper.getStringSF(getContext(), Constant.SP_KEY_TIME_STAMP));

        //获取当前服务器时间戳（每次接口请求都会保存时间戳在本地）
        String nowTime = DataHelper.getStringSF(getContext(), Constant.SP_KEY_TIME_STAMP);
        long nowTimeStamp = TextUtils.isEmpty(nowTime) ? 0 : Long.parseLong(nowTime);

        //获取上次领取奖励的服务器时间戳
        String lastRewardTime = DataHelper.getStringSF(getContext(), Constant.SP_KEY_TIME_LAST);
        long lastTimeStamp = TextUtils.isEmpty(lastRewardTime) ? 0 : Long.parseLong(lastRewardTime);

        if (lastTimeStamp == 0) {
            startGoldAnimation();//上次未领取奖励，显示奖励动画
        } else if (TimesUtils.getHour(nowTimeStamp) > TimesUtils.getHour(lastTimeStamp)) {
            startGoldAnimation();//当前时间的总小时数大于上次领取奖励时间总小时数，显示奖励动画
        } else {
            closeGoldAnimation();//隐藏奖励动画，并发送计时事件
            long nextHourTimeStamp = ((long) TimesUtils.getHour(nowTimeStamp) + 1) * 60 * 60 * 1000;
            long timeDifference = nextHourTimeStamp - nowTimeStamp;
//            Timber.d("============TimeReward - 距离下个整点的时差：( timeStart )" + timeDifference);
            if (timeDifference > 0) {
                EventBus.getDefault().post(new TimeRewardEvent.Builder()
                        .timeDifference(timeDifference)
                        .isTimeEnd(false)
                        .build(), EventBusTags.TAG_TIME_REWARD_TIME_START);
            }

        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (normalDialog != null) {
            normalDialog = null;
        }
    }
}
