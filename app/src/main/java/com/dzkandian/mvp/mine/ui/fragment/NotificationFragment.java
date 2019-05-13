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
import com.dzkandian.mvp.mine.contract.NotificationContract;
import com.dzkandian.mvp.mine.di.component.DaggerNotificationComponent;
import com.dzkandian.mvp.mine.di.module.NotificationModule;
import com.dzkandian.mvp.mine.presenter.NotificationPresenter;
import com.dzkandian.mvp.mine.ui.adapter.NotificationAdapter;
import com.dzkandian.storage.bean.mine.NotificationBean;
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


public class NotificationFragment extends BaseFragment<NotificationPresenter> implements NotificationContract.View {
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    private LoadingProgressDialog loadingProgressDialog;
    private List<NotificationBean> mNotificationList;
    private NotificationAdapter mNotificationAdapter;
    private long refreshNotificationLastTimes;//系统通知 刷新 的上一次时间；
    private long loadMoreNotificationLastTimes;//系统通知 加载 的上一次时间；

    @Override
    public void setupFragmentComponent(@NonNull AppComponent appComponent) {
        DaggerNotificationComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .notificationModule(new NotificationModule(this))
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
                        mPresenter.replyList(false);
                    }
                } else {
                    if (System.currentTimeMillis() - loadMoreNotificationLastTimes > 2000) {
                        loadMoreNotificationLastTimes = System.currentTimeMillis();
                        showMessage("网络请求失败，请连网后重试");
                    }
                    finishLoadMore();//隐藏加载更多
                }
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                if (isInternet()) {
                    if (mPresenter != null) {
                        mPresenter.replyList(true);
                    }
                } else {
                    if (System.currentTimeMillis() - refreshNotificationLastTimes > 2000) {
                        refreshNotificationLastTimes = System.currentTimeMillis();
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

    /**
     * 初始化 recyclerview
     */
    private void initRecyclerView() {
        mNotificationList = new ArrayList<>();
        mNotificationAdapter = new NotificationAdapter(mNotificationList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(mNotificationAdapter);
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
        getActivity().finish();
    }

    /**
     * 结束加载更多
     */
    @Override
    public void finishRefresh() {
        refreshLayout.finishRefresh();
    }

    /**
     * 结束刷新
     */
    @Override
    public void finishLoadMore() {
        refreshLayout.finishLoadMore();
    }

    /**
     * 刷新数据
     *
     * @param notificationBean
     */
    @Override
    public void refreshData(List<NotificationBean> notificationBean) {
        mNotificationList.clear();
        mNotificationList.addAll(notificationBean);
        mNotificationAdapter.notifyDataSetChanged();
    }

    /**
     * 加载更多
     *
     * @param notificationBean
     */
    @Override
    public void loadMoreData(List<NotificationBean> notificationBean) {
        if (notificationBean.size() == 0) {
            refreshLayout.setNoMoreData(true);
        } else {
            int index = mNotificationList.size();
            mNotificationList.addAll(notificationBean);
            mNotificationAdapter.notifyItemRangeInserted(index, mNotificationList.size());
        }
    }

    /**
     * 刷新失败 显示空布局
     *
     * @param isShowError
     */
    @Override
    public void refreshFailed(boolean isShowError) {
        mNotificationAdapter.showErrorView(isShowError);
    }

    @Override
    public void onDestroy() {
        if (loadingProgressDialog != null && loadingProgressDialog.isShowing()) {
            loadingProgressDialog.dismiss();
            loadingProgressDialog = null;
        }
        mNotificationAdapter = null;
        mNotificationList = null;
        super.onDestroy();
    }
}
