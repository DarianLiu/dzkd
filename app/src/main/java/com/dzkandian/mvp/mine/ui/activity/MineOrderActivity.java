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
import com.dzkandian.mvp.mine.contract.MineOrderContract;
import com.dzkandian.mvp.mine.di.component.DaggerMineOrderComponent;
import com.dzkandian.mvp.mine.di.module.MineOrderModule;
import com.dzkandian.mvp.mine.presenter.MineOrderPresenter;
import com.dzkandian.mvp.mine.ui.adapter.MyOrderAdapter;
import com.dzkandian.storage.bean.mine.MyOrderBean;
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


public class MineOrderActivity extends BaseActivity<MineOrderPresenter> implements MineOrderContract.View {
    @BindView(R.id.tv_toolbar_title)
    TextView tvToolbarTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @Nullable
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @Nullable
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    private List<MyOrderBean> mAllOrder;
    private MyOrderAdapter myOrderAdapter;
    private long refreshAllOrderLastTimes;//全部订单 刷新 的上一次时间；
    private long loadMoreAllOrderLastTimes;//全部订单 加载 的上一次时间；

    @Nullable
    private LoadingProgressDialog loadingProgressDialog;

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {
        DaggerMineOrderComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .mineOrderModule(new MineOrderModule(this))
                .build()
                .inject(this);
    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_my_order; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        tvToolbarTitle.setText(R.string.mine_my_order);
        toolbar.setNavigationOnClickListener(v -> finish());

        initRecyclerView();
        refreshLayout.setEnableLoadMore(true);//开启加载更多
        refreshLayout.setDisableContentWhenRefresh(true);//刷新时禁止滑动
        refreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (isInternet()) {
                    if (mPresenter != null) {
                        mPresenter.questAllOrder(false);
                    }
                } else {
                    if (System.currentTimeMillis() - loadMoreAllOrderLastTimes > 2000) {
                        loadMoreAllOrderLastTimes = System.currentTimeMillis();
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
                        mPresenter.questAllOrder(true);
                    }
                } else {
                    if (System.currentTimeMillis() - refreshAllOrderLastTimes > 2000) {
                        refreshAllOrderLastTimes = System.currentTimeMillis();
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

    private void initRecyclerView() {
        mAllOrder = new ArrayList<>();
        myOrderAdapter = new MyOrderAdapter(mAllOrder);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(myOrderAdapter);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (loadingProgressDialog != null && loadingProgressDialog.isShowing()) {
            loadingProgressDialog.dismiss();
            loadingProgressDialog = null;
        }
        mAllOrder = null;
        myOrderAdapter = null;
    }

    /**
     * 结束刷新
     */
    @Override
    public void finishRefresh() {
        refreshLayout.finishRefresh();
    }

    /**
     * 结束加载更多
     */
    @Override
    public void finishLoadMore() {
        refreshLayout.finishLoadMore();
    }

    /**
     * 刷新失败
     *
     * @param isRefresh
     */
    @Override
    public void refreshFailed(boolean isRefresh) {
        myOrderAdapter.showErrorView(isRefresh);
    }

    /**
     * 刷新数据
     *
     * @param myOrderBeans
     */
    @Override
    public void refreshData(List<MyOrderBean> myOrderBeans) {
        mAllOrder.clear();
        mAllOrder.addAll(myOrderBeans);
        myOrderAdapter.notifyDataSetChanged();
    }

    /**
     * 加载更多
     *
     * @param myOrderBeans
     */
    @Override
    public void loadMoreData(List<MyOrderBean> myOrderBeans) {
        if (myOrderBeans.size() == 0) {
            //无更多数据
            refreshLayout.setNoMoreData(true);
        } else {
            int index = mAllOrder.size();
            mAllOrder.addAll(myOrderBeans);
            myOrderAdapter.notifyItemRangeInserted(index, mAllOrder.size());
        }
    }
}
