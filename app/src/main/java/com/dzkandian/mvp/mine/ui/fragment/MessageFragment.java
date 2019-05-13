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
import com.dzkandian.mvp.mine.contract.MessageContract;
import com.dzkandian.mvp.mine.di.component.DaggerMessageComponent;
import com.dzkandian.mvp.mine.di.module.MessageModule;
import com.dzkandian.mvp.mine.presenter.MessagePresenter;
import com.dzkandian.mvp.mine.ui.adapter.MessageAdapter;
import com.dzkandian.storage.bean.mine.MessageBean;
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


public class MessageFragment extends BaseFragment<MessagePresenter> implements MessageContract.View {
    @Nullable
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @Nullable
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    private List<MessageBean> mMessageList;
    private MessageAdapter mMessageAdapte;
    private long refreshMessageLastTimes;//我的消息 刷新 的上一次时间；
    private long loadMoreMessageLastTimes;//我的消息 加载 的上一次时间；

    @Override
    public void setupFragmentComponent(@NonNull AppComponent appComponent) {
        DaggerMessageComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .messageModule(new MessageModule(this))
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
                    if (System.currentTimeMillis() - loadMoreMessageLastTimes > 2000) {
                        loadMoreMessageLastTimes = System.currentTimeMillis();
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
                    if (System.currentTimeMillis() - refreshMessageLastTimes > 2000) {
                        refreshMessageLastTimes = System.currentTimeMillis();
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
        mMessageList = new ArrayList<>();
        mMessageAdapte = new MessageAdapter(mMessageList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(mMessageAdapte);
    }

    @Override
    public void setData(@Nullable Object data) {

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
     * @param messageBeans
     */
    @Override
    public void refreshData(List<MessageBean> messageBeans) {
        mMessageList.clear();
        mMessageList.addAll(messageBeans);
        mMessageAdapte.notifyDataSetChanged();

    }

    /**
     * 加载更多
     *
     * @param messageBeans
     */
    @Override
    public void loadMoreData(List<MessageBean> messageBeans) {
        if (messageBeans.size() == 0) {
            refreshLayout.setNoMoreData(true);
        } else {
            int index = mMessageList.size();
            mMessageList.addAll(messageBeans);
            mMessageAdapte.notifyItemRangeInserted(index, mMessageList.size());
        }
    }

    /**
     * 刷新失败 显示空布局
     *
     * @param isShowError
     */
    @Override
    public void refreshFailed(boolean isShowError) {
        mMessageAdapte.showErrorView(isShowError);
    }

    @Override
    public void onDestroy() {
        if (loadingProgressDialog != null && loadingProgressDialog.isShowing()) {
            loadingProgressDialog.dismiss();
            loadingProgressDialog = null;
        }
        mMessageAdapte = null;
        mMessageList = null;
        super.onDestroy();
    }
}
