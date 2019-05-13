package com.dzkandian.mvp.mine.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dzkandian.R;
import com.dzkandian.common.uitls.NetworkUtils;
import com.dzkandian.common.widget.laoding.LoadingProgressDialog;
import com.dzkandian.mvp.mine.contract.ActiveCenterContract;
import com.dzkandian.mvp.mine.di.component.DaggerActiveCenterComponent;
import com.dzkandian.mvp.mine.di.module.ActiveCenterModule;
import com.dzkandian.mvp.mine.presenter.ActiveCenterPresenter;
import com.dzkandian.mvp.mine.ui.adapter.ActiveCenterAdapter;
import com.dzkandian.storage.bean.ActiveBean;
import com.jess.arms.base.BaseFragment;
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
 * 活动中心
 */
public class ActiveCenterFragment extends BaseFragment<ActiveCenterPresenter> implements ActiveCenterContract.View {

    @Nullable
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @Nullable
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;

    @Nullable
    private ActiveCenterAdapter mAdapter;
    @Nullable
    private List<ActiveBean> mActiveList;
    private long refreshActiveCenterLastTimes;//活动公告 刷新 的上一次时间；
    private long loadMoreActiveCenterLastTimes;//活动公告 加载 的上一次时间；


    @Override
    public void setupFragmentComponent(@NonNull AppComponent appComponent) {
        DaggerActiveCenterComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .activeCenterModule(new ActiveCenterModule(this))
                .build()
                .inject(this);
    }

    @Override
    public View initView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.include_refresh_recycler, container, false);
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        initRecyclerView();

        refreshLayout.setEnableLoadMore(true);
        refreshLayout.setDisableContentWhenRefresh(true);
        refreshLayout.setEnableAutoLoadMore(true);
        refreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (isInternet()) {
                    if (mPresenter != null) {
                        mPresenter.activeCenter(false);
                    }
                } else {
                    if (System.currentTimeMillis() - loadMoreActiveCenterLastTimes > 2000) {
                        loadMoreActiveCenterLastTimes = System.currentTimeMillis();
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
                        mPresenter.activeCenter(true);
                    }
                } else {
                    if (System.currentTimeMillis() - refreshActiveCenterLastTimes > 2000) {
                        refreshActiveCenterLastTimes = System.currentTimeMillis();
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
        return NetworkUtils.checkNetwork(getContext().getApplicationContext());
    }

    @Override
    public void setData(@Nullable Object data) {

    }

    /**
     * 初始化RecyclerView
     */
    private void initRecyclerView() {
        mActiveList = new ArrayList<>();
        mAdapter = new ActiveCenterAdapter(mActiveList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(mAdapter);
    }


    @Nullable
    private LoadingProgressDialog loadingProgressDialog;


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
        getActivity().finish();
    }

    /**
     * 刷新失败
     *
     * @param isShowError 是否显示网络异常布局（否则显示空布局）
     */
    @Override
    public void refreshFailed(boolean isShowError) {
        mAdapter.showErrorView(isShowError);
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
     * 刷新数据
     *
     * @param activeList 活动列表
     */
    @Override
    public void refreshData(@NonNull List<ActiveBean> activeList) {
        mActiveList.clear();
        mActiveList.addAll(activeList);
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 加载更多添加数据
     *
     * @param activeList 活动列表
     */
    @Override
    public void loadMoreData(@NonNull List<ActiveBean> activeList) {
        if (activeList.size() == 0) {
            refreshLayout.setNoMoreData(true);
        } else {
            int index = mActiveList.size();
            mActiveList.addAll(activeList);
            mAdapter.notifyItemRangeInserted(index, activeList.size());
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (loadingProgressDialog != null && loadingProgressDialog.isShowing()) {
            loadingProgressDialog.dismiss();
            loadingProgressDialog = null;
        }
        mActiveList = null;
        mAdapter = null;
    }


}
