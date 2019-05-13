package com.dzkandian.mvp.common.ui.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import com.dzkandian.R;
import com.dzkandian.app.config.EventBusTags;
import com.dzkandian.common.uitls.Constant;
import com.dzkandian.common.uitls.StatusBarUtil;
import com.dzkandian.common.uitls.update.UpdateAppBean;
import com.dzkandian.common.uitls.update.UpdateDialogFragment;
import com.dzkandian.common.widget.FragmentTabHost;
import com.dzkandian.common.widget.SoundPoolManager;
import com.dzkandian.common.widget.explosion.ExplosionField;
import com.dzkandian.common.widget.laoding.LoadingProgressDialog;
import com.dzkandian.mvp.common.contract.MainContract;
import com.dzkandian.mvp.common.di.component.DaggerMainComponent;
import com.dzkandian.mvp.common.di.module.MainModule;
import com.dzkandian.mvp.common.presenter.MainPresenter;
import com.dzkandian.mvp.mine.ui.fragment.MineFragment;
import com.dzkandian.mvp.news.ui.fragment.NewsFragment;
import com.dzkandian.mvp.task_center.ui.fragment.TaskCenterFragment;
import com.dzkandian.mvp.video.ui.fragment.VideoFragment;
import com.dzkandian.storage.event.ChangeTabEvent;
import com.dzkandian.storage.event.PushEvent;
import com.dzkandian.storage.event.RewardMainEvent;
import com.dzkandian.storage.event.TimeRewardEvent;
import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.jess.arms.utils.DataHelper;
import com.jess.arms.utils.DeviceUtils;
import com.umeng.analytics.MobclickAgent;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import butterknife.BindDrawable;
import butterknife.BindString;
import butterknife.BindView;

import static com.jess.arms.utils.Preconditions.checkNotNull;

/**
 * 主页面
 */
public class MainActivity extends BaseActivity<MainPresenter> implements MainContract.View {

    @BindView(android.R.id.tabs)
    TabWidget tabs;
    @BindView(android.R.id.tabhost)
    FragmentTabHost tabHost;
    /*奖励效果图相关View*/
    @BindView(R.id.rewardIV)
    ImageView ivReward;
    @BindView(R.id.rewardTV)
    TextView tvReward;
    @BindView(R.id.rewardView)
    FrameLayout rewardView;

    @BindString(R.string.menu_news)
    String str_menu_news;
    @BindString(R.string.menu_video)
    String str_menu_video;
    @BindString(R.string.menu_task_center)
    String str_menu_task_center;
    @BindString(R.string.menu_mine)
    String str_menu_mine;

    @BindDrawable(R.drawable.selector_menu_news)
    Drawable drawableMenuNews;
    @BindDrawable(R.drawable.selector_menu_video)
    Drawable drawableMenuVideo;
    @BindDrawable(R.drawable.selector_menu_task)
    Drawable drawableMenuTask;
    @BindDrawable(R.drawable.selector_menu_mine)
    Drawable drawableMenuMine;



    private ExplosionField explosionField;//爆裂效果
    private boolean isOneExplode = true;//是否第一次爆裂
    private long firstRewardTime = 0;//上一次领取奖励的爆裂效果时间
    private int TAB_CURRENT = 0;//当前选中项
    @Nullable
    private LoadingProgressDialog loadingProgressDialog;

    private int activePush = 0;//活动公告推送数量
    private int notificationPush = 0;//活动公告推送数量
    private int messagePush = 0;//我的消息推送数量
    private long pauseTimeStamp; //Activity处于后台时的时间戳
    private UpdateAppBean mUpdateAppBean;
    private static final int MSG_COIN_EXPLOSION_TAG = 1;
    @NonNull
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_COIN_EXPLOSION_TAG:
                    if (explosionField != null && ivReward != null && tvReward != null) {
                        explosionField.expandExplosionBound(
                                (int) DeviceUtils.getScreenWidth(getApplicationContext()),
                                (int) (DeviceUtils.getScreenHeight(getApplicationContext()) * 0.6));
                        explosionField.explode(ivReward);
                        explosionField.explode(tvReward);
                        if (TextUtils.isEmpty(DataHelper.getStringSF(getApplicationContext(), Constant.SP_KEY_SET_SOUND))
                                || DataHelper.getStringSF(getApplicationContext(), Constant.SP_KEY_SET_SOUND).equals("true")) {
                            SoundPoolManager.getInstance(getApplicationContext()).playRinging();
                        }
                    }
            }
        }
    };/*Handler更新UI*/


    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {
        DaggerMainComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .mainModule(new MainModule(this))
                .build()
                .inject(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        int app_status = DataHelper.getIntergerSF(getApplicationContext(), Constant.SP_KEY_APP_STATUS);
//        Timber.d("--------app_status：" + app_status);
        if (app_status == Constant.APP_STATUS_RESTART) {
            super.onCreate(null);
        } else if (app_status == Constant.APP_STATUS_FORCE_KILLED) {
            super.onCreate(null);
            DataHelper.setIntergerSF(getApplicationContext(), Constant.SP_KEY_APP_STATUS, Constant.APP_STATUS_RESTART);
        } else {
            super.onCreate(savedInstanceState);
        }
    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_main; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    /**
     * 接收未读消息推送
     *
     * @param pushEvent 推送相关信息
     */
    @Subscriber(tag = EventBusTags.TAG_PUSH_MESSAGE)
    public void receivePushEvent(PushEvent pushEvent) {
        if (!pushEvent.isPush()) {
            tabs.getChildTabViewAt(3).findViewById(R.id.tv_red_point).setVisibility(View.GONE);
        } else {
            tabs.getChildTabViewAt(3).findViewById(R.id.tv_red_point).setVisibility(View.VISIBLE);
        }

        EventBus.getDefault().removeStickyEvent(PushEvent.class, EventBusTags.TAG_PUSH_MESSAGE);
        if (pushEvent.getNewActive() >= 0)
            activePush = pushEvent.getNewActive();
        if (pushEvent.getNewNotification() >= 0)
            notificationPush = pushEvent.getNewNotification();
        if (pushEvent.getNewMessage() >= 0)
            messagePush = pushEvent.getNewMessage();
        EventBus.getDefault().postSticky(
                new PushEvent.Builder()
                        .newActive(activePush)
                        .newNotification(notificationPush)
                        .newMessage(messagePush).build(),
                EventBusTags.TAG_PUSH_MESSAGE);
    }

    /**
     * 接收到切换界面的通知
     */
    @Subscriber(tag = EventBusTags.TAG_CHANGE_TAB)
    public void onReceiveNews(@NonNull ChangeTabEvent event) {
//        Timber.d("==TAG_CHANGE_TAB" + event.getIndexTab());
        TAB_CURRENT = event.getIndexTab();
        tabHost.setCurrentTab(TAB_CURRENT);
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        StatusBarUtil.setStatusBarTranslucent(this);
        assert mPresenter != null;
        mPresenter.checkUpdate("");
        initTabHost();
        explosionField = ExplosionField.attach2Window(this);
        //推送打开APP进入新闻详情页
        String mPushUpApp = getIntent().getStringExtra("mPushUpApp");
        if (!TextUtils.isEmpty(mPushUpApp)) {
            EventBus.getDefault().post(new ChangeTabEvent.Builder().indexTab(2).build(), EventBusTags.TAG_CHANGE_TAB);
        }

        EventBus.getDefault().registerSticky(this);
    }

    /**
     * 初始化FragmentTabHost(底部菜单点击事件处理)
     */
    public void initTabHost() {
        tabHost.setup(MainActivity.this, getSupportFragmentManager(), android.R.id.tabcontent);
        tabHost.getTabWidget().setDividerDrawable(null); // 去掉分割线

        String textSize = DataHelper.getStringSF(getApplicationContext(), Constant.SP_KEY_SET_TEXTSIZE);
        Bundle bundle = new Bundle();
        bundle.putString("textSize", textSize);

        for (int i = 0; i < 4; i++) {
            TabHost.TabSpec tabSpec;
            switch (i) {
                case 0:
                    tabSpec = tabHost.newTabSpec(str_menu_news)
                            .setIndicator(getTabView(str_menu_news, drawableMenuNews));
                    tabHost.addTab(tabSpec, NewsFragment.class, bundle);
//                    tabs.getChildTabViewAt(0).setOnClickListener(view -> {
//                        TAB_CURRENT = 0;
//                        tabHost.setCurrentTab(TAB_CURRENT);
//                    });
                    break;
                case 1:
                    tabSpec = tabHost.newTabSpec(str_menu_video)
                            .setIndicator(getTabView(str_menu_video, drawableMenuVideo));
                    tabHost.addTab(tabSpec, VideoFragment.class, bundle);
//                    tabs.getChildTabViewAt(1).setOnClickListener(view -> {
//                        TAB_CURRENT = 1;
//                        tabHost.setCurrentTab(TAB_CURRENT);
//                    });
                    break;
                case 2:
                    tabSpec = tabHost.newTabSpec(str_menu_task_center)
                            .setIndicator(getTabView(str_menu_task_center, drawableMenuTask));
                    tabHost.addTab(tabSpec, TaskCenterFragment.class, null);
//                    tabs.getChildTabViewAt(2).setOnClickListener(view -> {
//                        if (!TextUtils.isEmpty(DataHelper.getStringSF(MainActivity.this, Constant.SP_KEY_TOKEN))) {
//                            TAB_CURRENT = 2;
//                        } else {
//                            launchActivity(new Intent(MainActivity.this, TextLoginActivity.class));
//                        }
//                        tabHost.setCurrentTab(TAB_CURRENT);
//                    });
                    break;
                case 3:
                    tabSpec = tabHost.newTabSpec(str_menu_mine)
                            .setIndicator(getTabView(str_menu_mine, drawableMenuMine));
                    tabHost.addTab(tabSpec, MineFragment.class, null);
                    tabs.getChildTabViewAt(3).setOnClickListener(view -> {
                        if (!TextUtils.isEmpty(DataHelper.getStringSF(MainActivity.this, Constant.SP_KEY_TOKEN))) {
                            TAB_CURRENT = 3;
                        } else {
                            launchActivity(new Intent(MainActivity.this, LoginActivity.class));
                        }
                        tabHost.setCurrentTab(TAB_CURRENT);
                    });
                    break;
                default:
                    break;

            }
        }

        tabHost.setOnTabChangedListener(s -> {
            if (s.equals(str_menu_news)) {
                TAB_CURRENT = 0;
            } else if (s.equals(str_menu_video)) {
                TAB_CURRENT = 1;
            } else if (s.equals(str_menu_task_center)) {
                if (isLogin()) {
                    TAB_CURRENT = 2;
                    EventBus.getDefault().post(true, EventBusTags.TAG_BOTTOM_MENU);
                } else {
                    launchActivity(new Intent(MainActivity.this, LoginActivity.class));
                    tabHost.setCurrentTab(TAB_CURRENT);
                }


            } else if (s.equals(str_menu_mine)) {
                if (isLogin()) {
                    TAB_CURRENT = 3;
                    EventBus.getDefault().post(true, EventBusTags.TAG_BOTTOM_MENU);
                } else {
                    launchActivity(new Intent(MainActivity.this, LoginActivity.class));
                    tabHost.setCurrentTab(TAB_CURRENT);
                }

            }
            startAniam();
        });
        //默认选中
        tabs.setCurrentTab(TAB_CURRENT);
    }

    /**
     * @return 是否登录
     */
    private boolean isLogin() {
        String userId = DataHelper.getStringSF(getApplicationContext(), Constant.SP_KEY_USER_ID);
        return !TextUtils.isEmpty(userId);
    }

    /*底部图标动画*/
    private AnimatorSet mAnimSetNews = new AnimatorSet();
    private AnimatorSet mAnimSetVideo = new AnimatorSet();
    private AnimatorSet mAnimSetTask = new AnimatorSet();
    private AnimatorSet mAnimSetMine = new AnimatorSet();

    private void startAniam() {
        mAnimSetNews.end();
        mAnimSetVideo.end();
        mAnimSetTask.end();
        mAnimSetMine.end();
        switch (TAB_CURRENT) {
            case 0:
                mAnimSetNews.start();
                break;
            case 1:
                mAnimSetVideo.start();
                break;
            case 2:
                mAnimSetTask.start();
                break;
            case 3:
                mAnimSetMine.start();
                break;
        }
    }

    /**
     * 获取当前tab视图
     */
    private View getTabView(String tabName, Drawable tabResource) {
        View view = getLayoutInflater().inflate(R.layout.view_menu, null);
        ImageView imageView = view.findViewById(R.id.iv_menu);
        TextView textView = view.findViewById(R.id.tv_menu);
        imageView.setImageDrawable(tabResource);
        textView.setText(tabName);
        ObjectAnimator objectAnimator1 = ObjectAnimator.ofFloat(imageView, "scaleY", 0.85f, 1.15f, 0.95f, 1.0f);
        objectAnimator1.setDuration(400);
        ObjectAnimator objectAnimator2 = ObjectAnimator.ofFloat(imageView, "scaleX", 1.15f, 0.85f, 1.05f, 1.0f);
        objectAnimator2.setDuration(400);
        if (TextUtils.equals(tabName, str_menu_news)) {
            mAnimSetNews.setInterpolator(new AccelerateInterpolator());
            mAnimSetNews.playTogether(objectAnimator1, objectAnimator2);
        } else if (TextUtils.equals(tabName, str_menu_video)) {
            mAnimSetVideo.setInterpolator(new AccelerateInterpolator());
            mAnimSetVideo.playTogether(objectAnimator1, objectAnimator2);
        } else if (TextUtils.equals(tabName, str_menu_task_center)) {
            mAnimSetTask.setInterpolator(new AccelerateInterpolator());
            mAnimSetTask.playTogether(objectAnimator1, objectAnimator2);
        } else {
            mAnimSetMine.setInterpolator(new AccelerateInterpolator());
            mAnimSetMine.playTogether(objectAnimator1, objectAnimator2);
        }
        return view;
    }


    @Override
    public void showLoading() {
        if (loadingProgressDialog == null)
            loadingProgressDialog = new LoadingProgressDialog.Builder(this).create();
        loadingProgressDialog.show();
    }

    @Override
    public void hideLoading() {
        if (loadingProgressDialog != null && loadingProgressDialog.isShowing())
            loadingProgressDialog.dismiss();
    }

    @Override
    protected void onDestroy() {
        explosionField = null;
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
        SoundPoolManager.getInstance(getApplicationContext()).release();
        super.onDestroy();
        if (loadingProgressDialog != null && loadingProgressDialog.isShowing()) {
            loadingProgressDialog.dismiss();
            loadingProgressDialog = null;
        }
        DataHelper.setIntergerSF(getApplicationContext(), Constant.SP_KEY_APP_STATUS, Constant.APP_STATUS_FORCE_KILLED);

        EventBus.getDefault().removeStickyEvent(PushEvent.class);
    }

    @Override
    public void showMessage(@NonNull String message) {
        checkNotNull(message);
        ArmsUtils.makeText(getApplicationContext(), message);
    }

    @Override
    public void launchActivity(@NonNull Intent intent) {
        checkNotNull(intent);
        ArmsUtils.startActivity(intent);
    }

    @Override
    public void killMyself() {
        finish();
    }

    //返回键按钮的点击事件：
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private long exitTime = 0;

    public void exit() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            try {
                showMessage("再按一次退出程序");
            } catch (Exception e) {
                e.printStackTrace();
            }
            exitTime = System.currentTimeMillis();
            //MyApplication.getInstance().exit();
        } else {
            MobclickAgent.onKillProcess(this);
            ArmsUtils.exitApp();
        }
    }

    /**
     * rewardMainEvent.getRewardType() 类型 string
     * rewardMainEvent.getRewardGold() 金币数量 int
     *
     * @param rewardMainEvent 时段奖励，任务中心奖励发送到 MainActivity 进行金币爆裂；
     */
    @Subscriber(tag = EventBusTags.TAG_COIN_REWARD)
    private void getCionReward(RewardMainEvent rewardMainEvent) {
        if (rewardMainEvent != null) {
            rewardView.setVisibility(View.VISIBLE);
            if (rewardMainEvent.getRewardType().equals("timeReward")) {
                ivReward.setSelected(true);
            } else {
                ivReward.setSelected(false);
            }
            tvReward.setText(String.valueOf(rewardMainEvent.getRewardGold()));
            if (System.currentTimeMillis() - firstRewardTime < 2500) {
                handler.postDelayed(() -> {
                    if (isOneExplode) {
                        isOneExplode = false;
                        explosionAnim();
                    } else {
                        setReward();
                    }
                }, 2500);
            } else {
                firstRewardTime = System.currentTimeMillis();
                if (isOneExplode) {
                    isOneExplode = false;
                    explosionAnim();
                } else {
                    setReward();
                }
            }
        }
    }

    /*金币领取爆裂效果*/
    private void explosionAnim() {
        handler.sendEmptyMessageDelayed(MSG_COIN_EXPLOSION_TAG, 2000);
    }


    /*金币领取图恢复加爆裂*/
    private void setReward() {
        View root = findViewById(R.id.rewardView);
        resetReward(root);
        explosionAnim();
        explosionField.clear();
    }

    /*金币领取图恢复*/
    private void resetReward(View root) {
        if (root instanceof ViewGroup) {
            ViewGroup parent = (ViewGroup) root;
            for (int i = 0; i < parent.getChildCount(); i++) {
                resetReward(parent.getChildAt(i));
            }
        } else {
            root.setScaleX(1);
            root.setScaleY(1);
            root.setAlpha(1);
        }
    }

    /**
     * 接收时段奖励计时事件（开始计时）
     *
     * @param event 时段奖励计时事件
     */
    @Subscriber(tag = EventBusTags.TAG_TIME_REWARD_TIME_START)
    private void receiveTimeRewardTime(TimeRewardEvent event) {
//        Timber.d("============TimeReward - 距离下个整点的时差：(receiveTimeRewardTime)" + event.getTimeDifference());
        if (mPresenter != null) {
            mPresenter.timeStart(event.getTimeDifference());
        }
    }

    @Override
    protected void onPause() {
        if (mPresenter != null && mPresenter.getTimeStart()) {
            pauseTimeStamp = System.currentTimeMillis();
        }
        SoundPoolManager.getInstance(getApplicationContext()).stopRinging();
        super.onPause();
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (pauseTimeStamp > 0) {
            long pauseTime = System.currentTimeMillis() - pauseTimeStamp;
            pauseTimeStamp = 0;
            if (mPresenter != null && pauseTime > 0) {
                mPresenter.timeRestart(pauseTime);
            }
        }
    }

    /**
     * 时段奖励倒计时操作
     */
    @Override
    public void timeRewardCountdown(Long along) {
//        Timber.d("============TimeReward - 距离下个整点的时差(timeRewardCountdown)："
//                + along + " " + TimesUtils.getMinute(along) + ":" + TimesUtils.getSecond(along));
        EventBus.getDefault().post(new TimeRewardEvent.Builder()
                .isTimeEnd(along <= 0)
                .timeDifference(along)
                .build(), EventBusTags.TAG_TIME_REWARD_TIME_SHOW);

    }

    @Override
    public void showUpdateDialog(UpdateAppBean updateAppBean) {
        mUpdateAppBean = updateAppBean;
        Bundle bundle = new Bundle();
        bundle.putSerializable("UpdateAppBean", mUpdateAppBean);
        UpdateDialogFragment
                .newInstance(bundle)
                .show(this.getSupportFragmentManager(), "dialog");
    }

    /**
     * 更新框架出错后重新拉起更新
     *
     * @param isError 是否更新异常
     */
    @Subscriber(tag = EventBusTags.TAG_UPDATE_DIALOG)
    private void errorUpdateDialog(boolean isError) {
//        Timber.d("==updatedialog  errorUpdateDialog");
        if (isError && mUpdateAppBean != null) {
            Bundle bundle = new Bundle();
            bundle.putSerializable("UpdateAppBean", mUpdateAppBean);
            UpdateDialogFragment
                    .newInstance(bundle)
                    .show(this.getSupportFragmentManager(), "dialog");
        }
    }

    /**
     * 接收滑动状态事件（显示/隐藏）
     *
     * @param isShowBottomMenu 是否显示底部菜单
     */
    @Subscriber(tag = EventBusTags.TAG_BOTTOM_MENU)
    public void receiveScrollEvent(boolean isShowBottomMenu) {
        if (!isShowBottomMenu) {
            if (tabs != null && tabs.getVisibility() == View.VISIBLE && (TAB_CURRENT == 0 || TAB_CURRENT == 1)) {
                tabs.clearAnimation();
                TranslateAnimation translateHidden = new TranslateAnimation(
                        Animation.RELATIVE_TO_SELF, 0.0f,
                        Animation.RELATIVE_TO_SELF, 0.0f,
                        Animation.RELATIVE_TO_SELF, 0.0f,
                        Animation.RELATIVE_TO_SELF, 1.0f);
                translateHidden.setDuration(300);
                tabs.setAnimation(translateHidden);
                tabs.setVisibility(View.GONE);
            }
        } else {
            if (tabs != null && tabs.getVisibility() == View.GONE) {
                tabs.clearAnimation();
                TranslateAnimation translateShow = new TranslateAnimation(
                        Animation.RELATIVE_TO_SELF, 0.0f,
                        Animation.RELATIVE_TO_SELF, 0.0f,
                        Animation.RELATIVE_TO_SELF, 1.0f,
                        Animation.RELATIVE_TO_SELF, 0.0f);
                translateShow.setDuration(300);
                tabs.setAnimation(translateShow);
                tabs.setVisibility(View.VISIBLE);
            }
        }
    }
}
