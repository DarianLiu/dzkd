package com.dzkandian.mvp.mine.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TabWidget;
import android.widget.TextView;

import com.dzkandian.R;
import com.dzkandian.app.config.EventBusTags;
import com.dzkandian.common.widget.FragmentTabHost;
import com.dzkandian.common.widget.laoding.LoadingProgressDialog;
import com.dzkandian.mvp.mine.contract.MessageCenterContract;
import com.dzkandian.mvp.mine.di.component.DaggerMessageCenterComponent;
import com.dzkandian.mvp.mine.di.module.MessageCenterModule;
import com.dzkandian.mvp.mine.presenter.MessageCenterPresenter;
import com.dzkandian.mvp.mine.ui.fragment.ActiveCenterFragment;
import com.dzkandian.mvp.mine.ui.fragment.MessageFragment;
import com.dzkandian.mvp.mine.ui.fragment.NotificationFragment;
import com.dzkandian.storage.event.PushEvent;
import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import butterknife.BindView;
import timber.log.Timber;

import static com.jess.arms.utils.Preconditions.checkNotNull;


public class MessageCenterActivity extends BaseActivity<MessageCenterPresenter> implements MessageCenterContract.View {


    @BindView(R.id.view_statue)
    View viewStatue;
    @BindView(R.id.tv_toolbar_title)
    TextView tvToolbarTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(android.R.id.tabs)
    TabWidget tabs;
    @BindView(android.R.id.tabcontent)
    FrameLayout tabContent;
    @BindView(android.R.id.tabhost)
    FragmentTabHost tabHost;
    @Nullable
    private LoadingProgressDialog loadingProgressDialog;

    private boolean activePush;//有活动公告推送
    private boolean notificationPush;//有系统通知推送
    private boolean messagePush;//有我的消息推送

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {
        DaggerMessageCenterComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .messageCenterModule(new MessageCenterModule(this))
                .build()
                .inject(this);
    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_message_center; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        tvToolbarTitle.setText(R.string.message);
        toolbar.setNavigationOnClickListener(view -> finish());
        //得到当前界面的装饰视图
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            //设置让应用主题内容占据状态栏和导航栏
            int option = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            //        SYSTEM_UI_FLAG_FULLSCREEN表示全屏的意思，也就是会将状态栏隐藏
            //设置系统UI元素的可见性
            decorView.setSystemUiVisibility(option);

            //设置状态栏颜色
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
            //设置导航栏颜色
            getWindow().setNavigationBarColor(Color.BLACK);
        }

        int tab = getIntent().getIntExtra("tab", 0);

        tabHost.setup(MessageCenterActivity.this, getSupportFragmentManager(), android.R.id.tabcontent);
        tabHost.getTabWidget().setDividerDrawable(null); // 去掉分割线

        tabHost.addTab(tabHost.newTabSpec("active")
                .setIndicator(getTabView(0)), ActiveCenterFragment.class, null);
        tabs.getChildTabViewAt(0).setOnClickListener(view -> {
            tabHost.setCurrentTab(0);
        });

        tabHost.addTab(tabHost.newTabSpec("notification")
                .setIndicator(getTabView(1)), NotificationFragment.class, null);
        tabs.getChildTabViewAt(1).setOnClickListener(view -> {
            tabHost.setCurrentTab(1);
        });

        tabHost.addTab(tabHost.newTabSpec("message")
                .setIndicator(getTabView(2)), MessageFragment.class, null);
        tabs.getChildTabViewAt(2).setOnClickListener(view -> {
            tabHost.setCurrentTab(2);
        });

        activePush = getIntent().getBooleanExtra("active_push", false);
        notificationPush = getIntent().getBooleanExtra("notification_push", false);
        messagePush = getIntent().getBooleanExtra("message_push", false);

        tabs.setCurrentTab(tab);

        setRedVisible();
    }

    private View getTabView(int tab) {
        View tabView = LayoutInflater.from(this).inflate(R.layout.view_tab, null);
        TextView tvTabName = tabView.findViewById(R.id.tv_tab_name);
        if (tab == 0) {
            tvTabName.setText(getResources().getString(R.string.active_center));
        } else if (tab == 1) {
            tvTabName.setText(getResources().getString(R.string.active_notification));
        } else if (tab == 2) {
            tvTabName.setText(getResources().getString(R.string.my_message));
        }
        return tabView;
    }

    /**
     * 设置红点显示隐藏
     */
    public void setRedVisible() {
        if (activePush) {
            tabs.getChildTabViewAt(0).findViewById(R.id.iv_tab_red_point).setVisibility(View.VISIBLE);
        } else {
            tabs.getChildTabViewAt(0).findViewById(R.id.iv_tab_red_point).setVisibility(View.GONE);
        }

        if (notificationPush) {
            tabs.getChildTabViewAt(1).findViewById(R.id.iv_tab_red_point).setVisibility(View.VISIBLE);
        } else {
            tabs.getChildTabViewAt(1).findViewById(R.id.iv_tab_red_point).setVisibility(View.GONE);
        }

        if (messagePush) {
            tabs.getChildTabViewAt(2).findViewById(R.id.iv_tab_red_point).setVisibility(View.VISIBLE);
        } else {
            tabs.getChildTabViewAt(2).findViewById(R.id.iv_tab_red_point).setVisibility(View.GONE);
        }

        EventBus.getDefault().removeStickyEvent(PushEvent.class, EventBusTags.TAG_PUSH_MESSAGE);
    }

    /**
     * 活动公告已读
     *
     * @param isRead 已读
     */
    @Subscriber(tag = EventBusTags.TAG_READ_ACTIVE)
    public void recieveReadActiveEvent(boolean isRead) {
        Timber.d("=========Push -MessageCenter : 接受到【活动公告】已读推送");
        //如果活动公告已读且目前是红点未读状态处于显示状态，若我的消息处于已读状态则通知前面所有消息已阅读
        tabs.getChildTabViewAt(0).findViewById(R.id.iv_tab_red_point).setVisibility(View.GONE);
        activePush = false;
        EventBus.getDefault().post(new PushEvent.Builder()
                .newActive(0)
                .newNotification(notificationPush ? 1 : 0)
                .newMessage(messagePush ? 1 : 0)
                .build(), EventBusTags.TAG_PUSH_MESSAGE);
    }

    /**
     * 系统通知已读
     *
     * @param isRead 已读
     */
    @Subscriber(tag = EventBusTags.TAG_READ_NOTIFICATION)
    public void recieveReadNotificationEvent(boolean isRead) {
        tabs.getChildTabViewAt(1).findViewById(R.id.iv_tab_red_point).setVisibility(View.GONE);
        notificationPush = false;
        EventBus.getDefault().post(new PushEvent.Builder()
                .newActive(activePush ? 1 : 0)
                .newNotification(0)
                .newMessage(messagePush ? 1 : 0)
                .build(), EventBusTags.TAG_PUSH_MESSAGE);
    }

    /**
     * 我的消息已读
     *
     * @param isRead 已读
     */
    @Subscriber(tag = EventBusTags.TAG_READ_MESSAGE)
    public void recieveReadMessageEvent(boolean isRead) {
        Timber.d("=========Push -MessageCenter : 接受到【我的消息】已读推送");
        //如果我的消息已读且目前是红点未读状态处于显示状态，若活动公告处于已读状态则通知前面所有消息已阅读
        tabs.getChildTabViewAt(2).findViewById(R.id.iv_tab_red_point).setVisibility(View.GONE);
        messagePush = false;
        EventBus.getDefault().post(new PushEvent.Builder()
                .newActive(activePush ? 1 : 0)
                .newNotification(notificationPush ? 1 : 0)
                .newMessage(0)
                .build(), EventBusTags.TAG_PUSH_MESSAGE);
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
    public void showMessage(@NonNull String message) {
        checkNotNull(message);
        ArmsUtils.snackbarText(message);
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

    @Override
    protected void onDestroy() {
        EventBus.getDefault().removeStickyEvent(MessageCenterActivity.class);
        if (loadingProgressDialog != null && loadingProgressDialog.isShowing()) {
            loadingProgressDialog.dismiss();
            loadingProgressDialog = null;
        }
        super.onDestroy();
    }
}
