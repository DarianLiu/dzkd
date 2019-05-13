package com.dzkandian.mvp.mine.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.dzkandian.R;
import com.dzkandian.common.uitls.NetworkUtils;
import com.dzkandian.common.widget.laoding.LoadingProgressDialog;
import com.dzkandian.mvp.mine.contract.InviteDiscipleContract;
import com.dzkandian.mvp.mine.di.component.DaggerInviteDiscipleComponent;
import com.dzkandian.mvp.mine.di.module.InviteDiscipleModule;
import com.dzkandian.mvp.mine.presenter.InviteDisciplePresenter;
import com.dzkandian.mvp.mine.ui.adapter.InviteDiscipleAdapter;
import com.dzkandian.storage.bean.mine.ApprenticesBean;
import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

import static com.jess.arms.utils.Preconditions.checkNotNull;

/**
 * 好友邀请的徒弟
 */
public class InviteDiscipleActivity extends BaseActivity<InviteDisciplePresenter> implements InviteDiscipleContract.View {

    @Nullable
    @BindView(R.id.tv_toolbar_title)
    TextView tvToolbarTitle;
    @Nullable
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @Nullable
    @BindView(R.id.recyclerView)
    RecyclerView mrecyclerView;
    @Nullable
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    private List<ApprenticesBean> apprenticesBeans;
    private InviteDiscipleAdapter inviteDiscipleAdapter;
    @Nullable
    private LoadingProgressDialog loadingProgressDialog;
    private long refreshDiscipleLastTimes;//成功邀请的徒弟 刷新 的上一次时间；
    private long loadMoreDiscipleLastTimes;//成功邀请的徒弟 加载 的上一次时间；

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {
        DaggerInviteDiscipleComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .inviteDiscipleModule(new InviteDiscipleModule(this))
                .build()
                .inject(this);
    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_invite_disciple; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        tvToolbarTitle.setText(R.string.invite_disciple);
        toolbar.setNavigationOnClickListener(v -> killMyself());
        initRecyclerView();

        refreshLayout.setEnableLoadMore(true);
        refreshLayout.setDisableContentWhenRefresh(true);
        refreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (isInternet()) {
                    if (mPresenter != null) {
                        mPresenter.apprenticesList(false);
                    }
                } else {
                    if (System.currentTimeMillis() - loadMoreDiscipleLastTimes > 2000) {
                        loadMoreDiscipleLastTimes = System.currentTimeMillis();
                        showMessage("网络请求失败，请连网后重试");
                    }
                    finishLoadMore();//隐藏加载更多
                }
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                if (isInternet()) {
                    if (mPresenter != null) {
                        refreshLayout.setNoMoreData(false);
                        mPresenter.apprenticesList(true);
                    }
                } else {
                    if (System.currentTimeMillis() - refreshDiscipleLastTimes > 2000) {
                        refreshDiscipleLastTimes = System.currentTimeMillis();
                        showMessage("网络请求失败，请连网后重试");
                        refreshFailed(true);
                    }
                    finishRefresh();//隐藏刷新
                }
            }
        });
        refreshLayout.autoRefresh();
    }

    /*判断当前是否有网络*/
    private boolean isInternet() {
        return NetworkUtils.checkNetwork(getApplicationContext());
    }

    /**
     * 初始化RecyclerView
     */
    private void initRecyclerView() {
        apprenticesBeans = new ArrayList<>();
        inviteDiscipleAdapter = new InviteDiscipleAdapter(apprenticesBeans);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(InviteDiscipleActivity.this);
        mrecyclerView.setLayoutManager(linearLayoutManager);
        mrecyclerView.setHasFixedSize(true);
        mrecyclerView.setAdapter(inviteDiscipleAdapter);
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
        super.onDestroy();
        if (loadingProgressDialog != null && loadingProgressDialog.isShowing()) {
            loadingProgressDialog.dismiss();
            loadingProgressDialog = null;
        }
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

    /**
     * 刷新数据
     */
    @Override
    public void refreshData(@NonNull List<ApprenticesBean> questionList) {
        apprenticesBeans.clear();
        apprenticesBeans.addAll(questionList);
        inviteDiscipleAdapter.notifyDataSetChanged();
    }

    /**
     * 加载更多数据
     */
    @Override
    public void loadMoreData(@NonNull List<ApprenticesBean> questionList) {
        if (questionList.size() == 0) {
            refreshLayout.setNoMoreData(true);
        } else {
            int index = apprenticesBeans.size();
            apprenticesBeans.addAll(questionList);
            inviteDiscipleAdapter.notifyItemRangeInserted(index, questionList.size());
        }
    }

    /**
     * 结束刷新状态
     */
    @Override
    public void finishRefresh() {
        refreshLayout.finishRefresh();
    }

    /**
     * 结束加载更多状态
     */
    @Override
    public void finishLoadMore() {
        refreshLayout.finishLoadMore(0);
    }

    /**
     * 刷新失败（网络异常）
     *
     * @param isNetError 是否显示网络异常布局（否则显示空布局）
     */
    @Override
    public void refreshFailed(boolean isNetError) {
        inviteDiscipleAdapter.showErrorView(isNetError);
    }
}
