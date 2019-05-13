package com.dzkandian.mvp.news.ui.fragment;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.dzkandian.R;
import com.dzkandian.app.MyApplication;
import com.dzkandian.app.config.EventBusTags;
import com.dzkandian.common.uitls.Constant;
import com.dzkandian.common.uitls.NetworkUtils;
import com.dzkandian.common.uitls.StatusBarUtil;
import com.dzkandian.common.uitls.TimesUtils;
import com.dzkandian.common.widget.adapter.FragmentAdapter;
import com.dzkandian.common.widget.laoding.LoadingProgressDialog;
import com.dzkandian.mvp.common.ui.activity.LoginActivity;
import com.dzkandian.mvp.common.ui.activity.RedRainActivity;
import com.dzkandian.mvp.common.ui.activity.WebViewActivity;
import com.dzkandian.mvp.news.contract.NewsContract;
import com.dzkandian.mvp.news.di.component.DaggerNewsComponent;
import com.dzkandian.mvp.news.di.module.NewsModule;
import com.dzkandian.mvp.news.presenter.NewsPresenter;
import com.dzkandian.mvp.news.ui.activity.ColumnManageActivity;
import com.dzkandian.mvp.news.ui.activity.SearchActivity;
import com.dzkandian.storage.ColumnBean;
import com.dzkandian.storage.bean.DeviceInfoBean;
import com.dzkandian.storage.event.ColumnEvent;
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


public class NewsFragment extends BaseFragment<NewsPresenter> implements NewsContract.View {

    @BindView(R.id.iv_rewardtime_light)
    ImageView ivRewardtimeLight;//时段奖励的背景光
    @BindView(R.id.rl_rewardtime_bag)
    RelativeLayout rlRewardtimeBag;//时段奖励的福袋
    @BindView(R.id.iv_rewardtime_receive)
    ImageView ivRewardtimeReceive;//时段奖励的领取按钮
    @BindView(R.id.tv_rewardtime)
    TextView tvRewardTime;//时段奖励的倒计时
    @BindView(R.id.tabLayout_news)
    XTabLayout tabLayoutNews;
    @BindView(R.id.iv_more)
    ImageView ivMore;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.red_packet)
    ImageView redPacket;
    @BindView(R.id.tv_title_search)
    TextView tvSearch;
    @BindView(R.id.v)
    View v;

    private List<String> mColumnList;//显示的栏目类别
    private List<Class> mFragments;

    private int mAddedSize;//已经添加的栏目数量
    private FragmentAdapter mFragmentAdapter;
    private String mTextSize;//字体大小

    @Nullable
    private AlertDialog.Builder normalDialog;
    @Nullable
    private LoadingProgressDialog loadingProgressDialog;
    private long goldAnimationLastTimes;//点击金币箱按钮的上一次时间；
    private long clickColumnManageLastTimes;//点击新闻栏目管理的上一次时间；
    private AnimatorSet mAnimatorSetTranslation, mAnimatorSetScaleBig, mAnimatorSetScaleSmall;

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (normalDialog != null) {
            normalDialog = null;
        }
    }

    @Override
    public void onDestroy() {
        if (loadingProgressDialog != null && loadingProgressDialog.isShowing()) {
            loadingProgressDialog.dismiss();
        }
        handler.removeCallbacksAndMessages(null);

        if (viewPager != null)
            viewPager.clearOnPageChangeListeners();
        super.onDestroy();
        loadingProgressDialog = null;
    }

    @Override
    public void setupFragmentComponent(@NonNull AppComponent appComponent) {
        DaggerNewsComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .newsModule(new NewsModule(this))
                .build()
                .inject(this);
    }

    @Override
    public View initView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_news, container, false);
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        v.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, StatusBarUtil.getStatusBarHeight(getActivity())));
        mTextSize = getArguments().getString("textSize");
        mColumnList = new ArrayList<>();//显示的栏目类别
        mFragments = new ArrayList<>();
        if (mPresenter != null && getActivity() != null) {
            int haveTouchHardware = DataHelper.getIntergerSF(getActivity().getApplicationContext(), Constant.SP_KEY_HAVE_TOUCH_HARDWARE);
            mPresenter.getNewsTitleList(haveTouchHardware != 1 ? 0 : 1);
        }

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

        mAddedSize = viewColumn.size();
        mColumnList.addAll(viewColumn);
        if (mAddedSize != 0) {
            for (int i = 0; i < mAddedSize; i++) {
                mFragments.add(NewBaseFragment.class);
            }
        }
        //更新栏目
        updateColumn();

        handler.postDelayed(this::queryDeviceInfo, 1200);//有获取下新闻栏目后

        ivMore.setOnClickListener(v -> {
            if (System.currentTimeMillis() - clickColumnManageLastTimes > 2000) {
                clickColumnManageLastTimes = System.currentTimeMillis();
                ColumnBean columnBean = DataHelper.getDeviceData(getActivity(), Constant.SP_KEY_NEWS_COLUMN);
                if (columnBean != null) {
                    Intent intent = new Intent(getActivity(), ColumnManageActivity.class);
                    intent.putStringArrayListExtra("column", (ArrayList<String>) columnBean.getAllColumn());
                    intent.putExtra("type", "news");
                    intent.putExtra("addedSize", mAddedSize);
                    startActivityForResult(intent, 200);
                } else if (allColumn != null) {
                    Intent intent = new Intent(getActivity(), ColumnManageActivity.class);
                    intent.putStringArrayListExtra("column", (ArrayList<String>) allColumn);
                    intent.putExtra("type", "news");
                    intent.putExtra("addedSize", mAddedSize);
                    startActivityForResult(intent, 200);
                }
            }
        });

    }

    /*Handler更新UI*/
    Handler handler = new Handler();

    /**
     * 刷新栏目
     */
    private void updateColumn() {
        if (mFragmentAdapter == null) {
            mFragmentAdapter = new FragmentAdapter(getChildFragmentManager(), mFragments, mColumnList, 0, mTextSize);
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
            tabLayoutNews.setupWithViewPager(viewPager);
        } else {
            mFragmentAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @NonNull Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200 && resultCode == 300) {
            ArrayList<String> titles = data.getStringArrayListExtra("returnData");

            mColumnList.clear();
            mFragments.clear();

            mColumnList.addAll(titles);
            mAddedSize = titles.size();
            if (mAddedSize != 0) {
                for (int i = 0; i < mAddedSize; i++) {
                    mFragments.add(NewBaseFragment.class);
                }
            }
            updateColumn();
        }
    }

    /**
     * 接收栏目跳转
     */
    @Subscriber(tag = EventBusTags.TAG_COLUMN_MANAGE)
    public void columnEvent(@NonNull ColumnEvent event) {
        String type = event.getType();
        int position = event.getPosition();
        if (type.equals("news")) {
            viewPager.setCurrentItem(position);
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
        ArmsUtils.makeText(getContext(), message);
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
        scaleBigX.setDuration(280);
        scaleBigX.setRepeatCount(0);
        ObjectAnimator scaleBigY = ObjectAnimator.ofFloat(rlRewardtimeBag, "scaleY", 0.8f, 1f);
        scaleBigY.setDuration(280);
        scaleBigY.setRepeatCount(0);
        mAnimatorSetScaleBig = new AnimatorSet();
        mAnimatorSetScaleBig.setInterpolator(new AccelerateInterpolator());
        mAnimatorSetScaleBig.playTogether(scaleBigX, scaleBigY);

        /*时段奖励福袋变小动画*/
        ObjectAnimator scaleSmallX = ObjectAnimator.ofFloat(rlRewardtimeBag, "scaleX", 1f, 0.8f);
        scaleSmallX.setDuration(280);
        scaleSmallX.setRepeatCount(0);
        ObjectAnimator scaleSmallY = ObjectAnimator.ofFloat(rlRewardtimeBag, "scaleY", 1f, 0.8f);
        scaleSmallY.setDuration(280);
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
        return getContext() != null && NetworkUtils.checkNetwork(getContext().getApplicationContext());
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
     * @param networkRetryEvent 0  看点
     */
    @Subscriber
    private void receiveNetWorkEvent(@NonNull NetworkRetryEvent networkRetryEvent) {
        if (networkRetryEvent.getEvent() == 0 && mPresenter != null && getActivity() != null) {
            int haveTouchHardware = DataHelper.getIntergerSF(getActivity().getApplicationContext(), Constant.SP_KEY_HAVE_TOUCH_HARDWARE);
            mPresenter.getNewsTitleList(haveTouchHardware != 1 ? 0 : 1);
        }
    }

    /**
     * 接收视频中点击金币箱领取时段奖励的事件 做刷新
     *
     * @param goldAnimationEvent 2  是视频  发给  看点的
     */
    @Subscriber
    private void receiveGoldAnimationEvent(@NonNull GoldAnimationEvent goldAnimationEvent) {
        if (goldAnimationEvent.getModule() == 2) {
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
     * @param isLogin 是否登录
     */
    @Subscriber(tag = EventBusTags.TAG_LOGIN_STATE)
    private void receiveLoginState(boolean isLogin) {
        if (isLogin) {
            startGoldAnimation();
            queryDeviceInfo();//登录回来显示；
        }
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
        EventBus.getDefault().post(new GoldAnimationEvent.Builder().module(1).build());
        //时段奖励领取金币的爆裂效果  number为金币数
        EventBus.getDefault().post(
                new RewardMainEvent.Builder()
                        .newRewardType("timeReward")
                        .newRewardGoid(number)
                        .build(), EventBusTags.TAG_COIN_REWARD);

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
        EventBus.getDefault().post(new GoldAnimationEvent.Builder().module(1).build());
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
        if (normalDialog == null) {
            normalDialog = new AlertDialog.Builder(getActivity());
            normalDialog.setTitle(getResources().getString(R.string.dialog_prompt));
            normalDialog.setMessage(getResources().getString(R.string.dialog_root_reminder));
            normalDialog.setNegativeButton(getResources().getString(R.string.dialog_i_know),
                    (dialog, which) -> {
                        dialog.dismiss();
                        ArmsUtils.exitApp();
                    });

            normalDialog.setCancelable(false);
        }
        // 显示
        normalDialog.show();
    }

    /**
     * 开始计时
     */
    public void timeStart() {

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

    /**
     * 首页红包雨逻辑
     *
     * @param deviceInfoBean
     */
    public void redRain(DeviceInfoBean deviceInfoBean) {
        int indexPop = deviceInfoBean.getIndexPop();//首页弹窗类型：
        String indexPopAttachData = deviceInfoBean.getIndexPopAttachData();//弹窗附加数据：
        String indexPopActivityPic = deviceInfoBean.getIndexPopActivityPic();//首页弹窗活动图片地址
        String indexPopActivityEvent = deviceInfoBean.getIndexPopActivityEvent();//首页弹窗活动事件：
        int indexPopActivityHour = deviceInfoBean.getIndexPopActivityHour();//首页弹窗活动时频小时
        int hasApprentice = deviceInfoBean.getHasApprentice();////是否有收徒；
        int finishNoviceTask = deviceInfoBean.getFinishNoviceTask();//是否完成新手任务;
        int todaySign = deviceInfoBean.getTodaySign();//今日是否签到;
        String gotoInvitationPic = deviceInfoBean.getGotoInvitationPic();//收徒引导弹窗图片URL：
        String gotoTaskCenterPic = deviceInfoBean.getGotoTaskCenterPic();//新手任务引导弹窗图片URL：
        String gotoSignPic = deviceInfoBean.getGotoSignPic();//签到引导弹窗图片URL：

        int presentTime = TimesUtils.getHour(System.currentTimeMillis());//当前时间
        int presentDay = TimesUtils.getDay(System.currentTimeMillis());//当前天数

        int lastActivityTime = DataHelper.getIntergerSF(getContext(), Constant.SP_KEY_INDEX_POP_CLOSE_ACTIVITY_HOUR);//上一次出现 首页弹窗活动 的时间
        int lastHasApprenticeTime = DataHelper.getIntergerSF(getContext(), Constant.SP_KEY_HAS_APPRENTICE_CLOSE_DAY);//上一次出现 是否有收徒 的时间
        int lastFinishNoviceTaskTime = DataHelper.getIntergerSF(getContext(), Constant.SP_KEY_FINISH_NOVICE_TASK_CLOSE_DAY);//上一次出现 是否完成新手任务 的时间
        int lastTodaySignTime = DataHelper.getIntergerSF(getContext(), Constant.SP_KEY_TODAY_SIGN_CLOSE_DAY);//上一次出现 今日是否签到 的时间

        if (indexPop > 0) {
            if (indexPop == 3) {
                if (presentTime - lastActivityTime >= indexPopActivityHour) {
                    DataHelper.setIntergerSF(getContext(), Constant.SP_KEY_INDEX_POP_CLOSE_ACTIVITY_HOUR, TimesUtils.getHour(System.currentTimeMillis()));
                } else if (hasApprentice == 0 && presentDay - lastHasApprenticeTime > 0) {
                    indexPopActivityEvent = "invitation";
                    indexPopActivityPic = gotoInvitationPic;
                    DataHelper.setIntergerSF(getContext(), Constant.SP_KEY_HAS_APPRENTICE_CLOSE_DAY, TimesUtils.getDay(System.currentTimeMillis()));
                } else if (finishNoviceTask == 0 && presentDay - lastFinishNoviceTaskTime > 0) {
                    indexPopActivityEvent = "taskCenter";
                    indexPopActivityPic = gotoTaskCenterPic;
                    DataHelper.setIntergerSF(getContext(), Constant.SP_KEY_FINISH_NOVICE_TASK_CLOSE_DAY, TimesUtils.getDay(System.currentTimeMillis()));
                } else if (todaySign == 0 && presentDay - lastTodaySignTime > 0) {
                    indexPopActivityEvent = "taskCenter";
                    indexPopActivityPic = gotoSignPic;
                    DataHelper.setIntergerSF(getContext(), Constant.SP_KEY_TODAY_SIGN_CLOSE_DAY, TimesUtils.getDay(System.currentTimeMillis()));
                } else {
                    indexPopActivityPic = "";
                }
            }

            if (!TextUtils.isEmpty(indexPopActivityPic)) {
                Intent intent = new Intent(getActivity(), RedRainActivity.class);
                intent.putExtra("indexPop", indexPop);
                intent.putExtra("indexPopAttachData", indexPopAttachData);
                intent.putExtra("indexPopActivityPic", indexPopActivityPic);
                intent.putExtra("indexPopActivityEvent", indexPopActivityEvent);
                startActivity(intent);
            }
        }
    }

    /**
     * 首页右下角红包浮窗；
     *
     * @param deviceInfoBean
     */
    public void redPacketFloating(DeviceInfoBean deviceInfoBean) {
        String indexActivityIconUrl = deviceInfoBean.getIndexActivityIconUrl();
        String indexActivityIconPic = deviceInfoBean.getIndexActivityIconPic();
        if (!TextUtils.isEmpty(indexActivityIconUrl) && !TextUtils.isEmpty(indexActivityIconPic)) {
            redPacket.setVisibility(View.VISIBLE);
            Glide.with(this).asDrawable()
                    .load(indexActivityIconPic)
                    .apply(new RequestOptions().centerCrop()
                            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                            .error(R.drawable.icon_activity_transparent)
                            .placeholder(R.drawable.icon_activity_transparent))
                    .into(redPacket);
            ObjectAnimator objectAnimator1 = ObjectAnimator.ofFloat(redPacket, "scaleY", 1, 0.9f, 1);
            objectAnimator1.setDuration(1000);
            objectAnimator1.setRepeatCount(ValueAnimator.INFINITE);
            ObjectAnimator objectAnimator2 = ObjectAnimator.ofFloat(redPacket, "scaleX", 1, 0.9f, 1);
            objectAnimator2.setDuration(1000);
            objectAnimator2.setRepeatCount(ValueAnimator.INFINITE);
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
            animatorSet.playTogether(objectAnimator1, objectAnimator2);
            animatorSet.start();
        }

        redPacket.setOnClickListener(view -> {
            if (isLogin()) {
                if (isInternet()) {
                    Intent innerIntent = new Intent(getContext(), WebViewActivity.class);
                    innerIntent.putExtra("URL", indexActivityIconUrl);
                    launchActivity(innerIntent);
                }
            } else {
                launchActivity(new Intent(getActivity(), LoginActivity.class));
            }
        });
    }

    /**
     * @return 是否登录
     */
    private boolean isLogin() {
        String userId = "";
        if (getContext() != null) {
            userId = DataHelper.getStringSF(getContext().getApplicationContext(), Constant.SP_KEY_USER_ID);
        }
        return !TextUtils.isEmpty(userId);
    }

    /**
     * 获取数据库的设备信息
     */
    private void queryDeviceInfo() {
        List<DeviceInfoBean> list = MyApplication.get().getDaoSession().getDeviceInfoBeanDao().loadAll();
        if (list != null && list.size() > 0) {
            DeviceInfoBean deviceInfoBean = list.get(0);
            dbUpdateDeviceInfo(deviceInfoBean);
        }
    }

    /**
     * @param deviceInfoBean 获取到数据库的设备信息后，更新界面
     */
    public void dbUpdateDeviceInfo(DeviceInfoBean deviceInfoBean) {
        if (deviceInfoBean != null && getActivity() != null) {
            String isUpdateApp = DataHelper.getStringSF(getActivity().getApplicationContext(), Constant.SP_KEY_UPDATE_APP);
            if (TextUtils.isEmpty(isUpdateApp) || !isUpdateApp.equals("ture")) {//判断是否与更新弹窗有冲突
                if (isLogin()) {
                    redRain(deviceInfoBean);//红包雨效果
                } else {
                    DataHelper.removeSF(getContext().getApplicationContext(), Constant.SP_KEY_TOKEN);
                    Intent intent = new Intent(getActivity(), RedRainActivity.class);
                    intent.putExtra("indexPop", 1);
                    intent.putExtra("indexPopAttachData", "");
                    intent.putExtra("indexPopActivityPic", "");
                    intent.putExtra("indexPopActivityEvent", "");
                    startActivity(intent);
                }
            }
            redPacketFloating(deviceInfoBean);//首页右下角红包浮窗；
        }
    }

}
