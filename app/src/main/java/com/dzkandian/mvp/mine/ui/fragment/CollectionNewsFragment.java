package com.dzkandian.mvp.mine.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dzkandian.R;
import com.dzkandian.app.config.EventBusTags;
import com.dzkandian.common.uitls.NetworkUtils;
import com.dzkandian.common.widget.laoding.LoadingProgressDialog;
import com.dzkandian.mvp.mine.contract.CollectionNewsContract;
import com.dzkandian.mvp.mine.di.component.DaggerCollectionNewsComponent;
import com.dzkandian.mvp.mine.di.module.CollectionNewsModule;
import com.dzkandian.mvp.mine.presenter.CollectionNewsPresenter;
import com.dzkandian.mvp.mine.ui.adapter.CollectionNewsAdatper;
import com.dzkandian.storage.bean.CollectionNewsBean;
import com.jess.arms.base.BaseFragment;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import org.simple.eventbus.Subscriber;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

import static com.jess.arms.utils.Preconditions.checkNotNull;


public class CollectionNewsFragment extends BaseFragment<CollectionNewsPresenter> implements CollectionNewsContract.View {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @Nullable
    private LoadingProgressDialog loadingProgressDialog;

    private CollectionNewsAdatper mAdapter;
    private List<CollectionNewsBean> mList;
    private long refreshNewsLastTimes;//资讯收藏 刷新 的上一次时间；
    private long loadMoreNewsLastTimes;//资讯收藏 加载 的上一次时间；
    private long cannelNewsLastTimes;//资讯收藏 删除 的上一次时间；

    @Override
    public void setupFragmentComponent(@NonNull AppComponent appComponent) {
        DaggerCollectionNewsComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .collectionNewsModule(new CollectionNewsModule(this))
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
                        mPresenter.getCollectionList(false);
                    }
                } else {
                    if (System.currentTimeMillis() - loadMoreNewsLastTimes > 2000) {
                        loadMoreNewsLastTimes = System.currentTimeMillis();
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
                        mPresenter.getCollectionList(true);
                    }
                } else {
                    if (System.currentTimeMillis() - refreshNewsLastTimes > 2000) {
                        refreshNewsLastTimes = System.currentTimeMillis();
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
     * 删除某一条资讯收藏
     */
    @Subscriber(tag = EventBusTags.TAG_COLLECTION_NEWS)
    public void cancelColletion(int position) {
        if (!TextUtils.equals(mList.get(position).getUrl(), "")) {
            if (isInternet()) {
                try {
                    String urlEncode = URLEncoder.encode(mList.get(position).getUrl(), "UTF-8");
                    mPresenter.removeNewsCollection(position, urlEncode);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            } else {
                if (System.currentTimeMillis() - cannelNewsLastTimes > 2000) {
                    cannelNewsLastTimes = System.currentTimeMillis();
                    showMessage("网络请求失败，请连网后重试");
                }
            }
        }
    }

    /**
     * 刷新资讯收藏
     */
    @Subscriber(tag = EventBusTags.TAG_COLLECTION_NEWS_REFRESH)
    public void refreshColletion(boolean value) {
        if (value && refreshLayout != null) {
            if (mList != null) {
                mList.clear();
            }
            refreshLayout.autoRefresh();
        }
    }

    @Override
    public void removeNewsCollection(int position) {
        mList.remove(position);
        if (mList != null && mList.size() == 0) {
            mAdapter.notifyItemChanged(0);
            refreshLayout.autoRefresh();
        } else {
//            mAdapter.cancelCollection();
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void setData(@Nullable Object data) {

    }

    /**
     * 初始化RecyclerView
     */
    private void initRecyclerView() {
        mList = new ArrayList<>();
        mAdapter = new CollectionNewsAdatper(mList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(mAdapter);
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

    @Override
    public void onDestroy() {
        if (loadingProgressDialog != null && loadingProgressDialog.isShowing()) {
            loadingProgressDialog.dismiss();
            loadingProgressDialog = null;
        }
        mList = null;
        mAdapter = null;
        super.onDestroy();
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
     * @param list
     */
    @Override
    public void refreshData(List<CollectionNewsBean> list) {
        mList.clear();
        mList.addAll(list);
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 加载更多添加数据
     *
     * @param list
     */
    @Override
    public void loadMoreData(List<CollectionNewsBean> list) {
        if (list.size() == 0) {
            refreshLayout.setNoMoreData(true);
        } else {
            int index = mList.size();
            mList.addAll(list);
            mAdapter.notifyItemRangeInserted(index, list.size());
        }
    }
}
