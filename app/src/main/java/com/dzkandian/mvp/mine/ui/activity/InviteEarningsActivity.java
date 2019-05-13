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
import com.dzkandian.mvp.mine.contract.InviteEarningsContract;
import com.dzkandian.mvp.mine.di.component.DaggerInviteEarningsComponent;
import com.dzkandian.mvp.mine.di.module.InviteEarningsModule;
import com.dzkandian.mvp.mine.presenter.InviteEarningsPresenter;
import com.dzkandian.mvp.mine.ui.adapter.InviteEarningsAdapter;
import com.dzkandian.storage.bean.mine.InviteProfitBean;
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
 * 好友邀请的总收益
 */
public class InviteEarningsActivity extends BaseActivity<InviteEarningsPresenter> implements InviteEarningsContract.View {

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

    private List<InviteProfitBean> inviteProfitBeans;
    private InviteEarningsAdapter inviteEarningsAdapter;
    @Nullable
    private LoadingProgressDialog loadingProgressDialog;
    private long refreshEarningsLastTimes;//徒弟提供的总收益 刷新 的上一次时间；
    private long loadMoreEarningsLastTimes;//徒弟提供的总收益 加载 的上一次时间；

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {
        DaggerInviteEarningsComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .inviteEarningsModule(new InviteEarningsModule(this))
                .build()
                .inject(this);
    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_invite_earnings; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        tvToolbarTitle.setText(R.string.invite_earnings);
        toolbar.setNavigationOnClickListener(v -> killMyself());

        initRecyclerView();
        refreshLayout.setEnableLoadMore(true);
        refreshLayout.setDisableContentWhenRefresh(true);
        refreshLayout.setOnRefreshListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (isInternet()) {
                    if (mPresenter != null) {
                        mPresenter.apprenticesList(false);
                    }
                } else {
                    if (System.currentTimeMillis() - loadMoreEarningsLastTimes > 2000) {
                        loadMoreEarningsLastTimes = System.currentTimeMillis();
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
                    if (System.currentTimeMillis() - refreshEarningsLastTimes > 2000) {
                        refreshEarningsLastTimes = System.currentTimeMillis();
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
        inviteProfitBeans = new ArrayList<>();
        inviteEarningsAdapter = new InviteEarningsAdapter(inviteProfitBeans);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(InviteEarningsActivity.this);
        mrecyclerView.setLayoutManager(linearLayoutManager);
        mrecyclerView.setHasFixedSize(true);
        mrecyclerView.setAdapter(inviteEarningsAdapter);
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
    public void refreshData(@NonNull List<InviteProfitBean> questionList) {
        inviteProfitBeans.clear();
        inviteProfitBeans.addAll(questionList);
        inviteEarningsAdapter.notifyDataSetChanged();
    }

    /**
     * 加载更多数据
     */
    @Override
    public void loadMoreData(@NonNull List<InviteProfitBean> questionList) {
        if (questionList.size() == 0) {
            refreshLayout.setNoMoreData(true);
        } else {
            int index = inviteProfitBeans.size();
            inviteProfitBeans.addAll(questionList);
            inviteEarningsAdapter.notifyItemRangeInserted(index, questionList.size());
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
     * @param isNetError 是否显示异常布局
     */
    @Override
    public void refreshFailed(boolean isNetError) {
        inviteEarningsAdapter.showErrorView(isNetError);
    }
}
