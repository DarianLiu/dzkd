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
import com.dzkandian.common.widget.FragmentTabHost;
import com.dzkandian.common.widget.laoding.LoadingProgressDialog;
import com.dzkandian.mvp.mine.contract.MyCollectionContract;
import com.dzkandian.mvp.mine.di.component.DaggerMyCollectionComponent;
import com.dzkandian.mvp.mine.di.module.MyCollectionModule;
import com.dzkandian.mvp.mine.presenter.MyCollectionPresenter;
import com.dzkandian.mvp.mine.ui.fragment.CollectionNewsFragment;
import com.dzkandian.mvp.mine.ui.fragment.CollectionShortFragment;
import com.dzkandian.mvp.mine.ui.fragment.CollectionVideoFragment;
import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;

import butterknife.BindView;

import static com.jess.arms.utils.Preconditions.checkNotNull;


public class MyCollectionActivity extends BaseActivity<MyCollectionPresenter> implements MyCollectionContract.View {

    @BindView(R.id.view_statue)
    View viewStatue;
    @BindView(R.id.tv_toolbar_title)
    TextView tvToolbarTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(android.R.id.tabs)
    TabWidget tabs;
    @BindView(android.R.id.tabcontent)
    FrameLayout tabcontent;
    @BindView(android.R.id.tabhost)
    FragmentTabHost tabhost;
    @Nullable
    private LoadingProgressDialog loadingProgressDialog;

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {
        DaggerMyCollectionComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .myCollectionModule(new MyCollectionModule(this))
                .build()
                .inject(this);
    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_my_collection; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        tvToolbarTitle.setText(R.string.mine_collection);
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

        tabhost.setup(MyCollectionActivity.this, getSupportFragmentManager(), android.R.id.tabcontent);
        tabhost.getTabWidget().setDividerDrawable(null); // 去掉分割线

        tabhost.addTab(tabhost.newTabSpec("news")
                .setIndicator(getTabView(0)), CollectionNewsFragment.class, null);
        tabs.getChildTabViewAt(0).setOnClickListener(view -> {
            tabhost.setCurrentTab(0);
        });

        tabhost.addTab(tabhost.newTabSpec("video")
                .setIndicator(getTabView(1)), CollectionVideoFragment.class, null);
        tabs.getChildTabViewAt(1).setOnClickListener(view -> {
            tabhost.setCurrentTab(1);
        });

        tabhost.addTab(tabhost.newTabSpec("short")
                .setIndicator(getTabView(2)), CollectionShortFragment.class, null);
        tabs.getChildTabViewAt(2).setOnClickListener(view -> {
            tabhost.setCurrentTab(2);
        });
    }

    private View getTabView(int tab) {
        View tabView = LayoutInflater.from(this).inflate(R.layout.view_tab, null);
        TextView tvTabName = tabView.findViewById(R.id.tv_tab_name);
        if (tab == 0) {
            tvTabName.setText(getResources().getString(R.string.mine_collection_news));
        } else if (tab == 1) {
            tvTabName.setText(getResources().getString(R.string.mine_collection_video));
        } else {
            tvTabName.setText(getResources().getString(R.string.mine_collection_short));
        }
        return tabView;
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
        if (loadingProgressDialog != null && loadingProgressDialog.isShowing()) {
            loadingProgressDialog.dismiss();
            loadingProgressDialog = null;
        }
        super.onDestroy();
    }
}
