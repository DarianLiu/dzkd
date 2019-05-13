package com.dzkandian.mvp.mine.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dzkandian.R;
import com.dzkandian.app.config.EventBusTags;
import com.dzkandian.common.uitls.Constant;
import com.dzkandian.common.uitls.NetworkUtils;
import com.dzkandian.common.widget.laoding.LoadingProgressDialog;
import com.dzkandian.common.widget.recyclerview.RecyclerItemDecoration;
import com.dzkandian.mvp.common.ui.activity.MainActivity;
import com.dzkandian.mvp.mine.contract.CollectionShortContract;
import com.dzkandian.mvp.mine.di.component.DaggerCollectionShortComponent;
import com.dzkandian.mvp.mine.di.module.CollectionShortModule;
import com.dzkandian.mvp.mine.presenter.CollectionShortPresenter;
import com.dzkandian.mvp.mine.ui.adapter.CollectionShortAdapter;
import com.dzkandian.mvp.video.ui.activity.ShortDetailActivity;
import com.dzkandian.storage.bean.CollectionVideoBean;
import com.dzkandian.storage.bean.video.VideoBean;
import com.dzkandian.storage.event.ChangeTabEvent;
import com.jess.arms.base.BaseFragment;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.jess.arms.utils.DataHelper;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

import static com.jess.arms.utils.Preconditions.checkNotNull;


public class CollectionShortFragment extends BaseFragment<CollectionShortPresenter> implements CollectionShortContract.View {
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.collection_short_recyclerView)
    RecyclerView collectionShortRecyclerView;
    @BindView(R.id.collection_short_tv_error)
    TextView collectionShortTvError;
    @BindView(R.id.collection_short_btn)
    Button collectionShortBtn;
    @BindView(R.id.collection_short_errorLayout)
    LinearLayout collectionShortErrorLayout;
    @Nullable
    private LoadingProgressDialog loadingProgressDialog;

    private CollectionShortAdapter mAdapter;
    private List<CollectionVideoBean> mList;
    private long refreshShortLastTimes;//小视频收藏 刷新 的上一次时间；
    private long loadMoreShortLastTimes;//小视频收藏 加载 的上一次时间；
    private long cannelShortLastTimes;//小视频收藏 删除 的上一次时间；
    private long clickShortVideoItemTimes;//小视频列表 点击item 的上一次时间

    @Override
    public void setupFragmentComponent(@NonNull AppComponent appComponent) {
        DaggerCollectionShortComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .collectionShortModule(new CollectionShortModule(this))
                .build()
                .inject(this);
    }

    @Override
    public View initView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_collection_short, container, false);
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        initRecyclerView();

        collectionShortBtn.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), MainActivity.class);
            startActivity(intent);
            EventBus.getDefault().post(new ChangeTabEvent.Builder().indexTab(1).build(), EventBusTags.TAG_CHANGE_TAB);
        });

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
                    if (System.currentTimeMillis() - loadMoreShortLastTimes > 2000) {
                        loadMoreShortLastTimes = System.currentTimeMillis();
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
                    if (System.currentTimeMillis() - refreshShortLastTimes > 2000) {
                        refreshShortLastTimes = System.currentTimeMillis();
                        showMessage("网络请求失败，请连网后重试");
                        refreshFailed(true);//无网刷新
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
     * 播放某一条小视频收藏
     */
    @Subscriber(tag = EventBusTags.TAG_COLLECTION_SHORT_PLAY)
    public void playColletion(int position) {
        if (System.currentTimeMillis() - clickShortVideoItemTimes > 1000) {
            clickShortVideoItemTimes = System.currentTimeMillis();
            String mTextSize = DataHelper.getStringSF(getContext().getApplicationContext(), Constant.SP_KEY_SET_TEXTSIZE);
            CollectionVideoBean data = mList.get(position);
            VideoBean videoBean = new VideoBean();
            videoBean.setVideoId(data.getId());
            videoBean.setTitle(data.getTitle());
            videoBean.setType("video");
            videoBean.setThumbUrl(data.getThumbUrl());
            videoBean.setUrl(data.getUrl());
            videoBean.setSource(data.getSource());
            videoBean.setUpdateTime(data.getUpdateTime());
            videoBean.setCanShare(data.getCanShare());
            videoBean.setWebUrl(data.getWebUrl());
            videoBean.setDescribe(data.getDescrible());
            Intent intent = new Intent(getContext(), ShortDetailActivity.class);
            intent.putExtra("type", data.getType());
            intent.putExtra("video", videoBean);
            intent.putExtra("textSize", mTextSize);
            intent.putExtra("shortCollection","shortCollection"); //如果是小视频收藏页进去则不发送通知
            ArmsUtils.startActivity(intent);
        }
    }

    /**
     * 删除某一条视频收藏
     */
    @Subscriber(tag = EventBusTags.TAG_COLLECTION_SHORT)
    public void cancelColletion(int position) {
        if (!TextUtils.equals(mList.get(position).getWebUrl(), "")) {
            if (isInternet()) {
                try {
                    String urlEncode = URLEncoder.encode(mList.get(position).getWebUrl(), "UTF-8");
                    mPresenter.removeNewsCollection(position, urlEncode);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            } else {
                if (System.currentTimeMillis() - cannelShortLastTimes > 2000) {
                    cannelShortLastTimes = System.currentTimeMillis();
                    showMessage("网络请求失败，请连网后重试");
                }
            }
        }
    }

    /**
     * 刷新小视频收藏
     */
    @Subscriber(tag = EventBusTags.TAG_COLLECTION_SHORT_REFRESH)
    public void refreshColletion(boolean value) {
        if (value && refreshLayout != null) {
            if (mList != null) {
                mList.clear();
            }
            refreshLayout.autoRefresh();
        }
    }

    @Override
    public void removeShortCollection(int position) {
        mList.remove(position);
        if (mList != null && mList.size() == 0) {
            mAdapter.notifyItemChanged(0);
            refreshLayout.autoRefresh();
        } else {
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
        mAdapter = new CollectionShortAdapter(mList);
        collectionShortRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        collectionShortRecyclerView.addItemDecoration(new RecyclerItemDecoration(2));
        collectionShortRecyclerView.setHasFixedSize(true);
        collectionShortRecyclerView.setAdapter(mAdapter);
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
     * true网络异常 ；false没有收藏 ；
     *
     * @param isShowError 是否显示网络异常布局（否则显示空布局）
     */
    @Override
    public void refreshFailed(boolean isShowError) {
        if (collectionShortRecyclerView != null) {
            collectionShortRecyclerView.setVisibility(View.GONE);
        }
        if (collectionShortErrorLayout != null) {
            collectionShortErrorLayout.setVisibility(View.VISIBLE);
        }
        if (isShowError) {//网络异常：
            collectionShortTvError.setText(R.string.error_network);
            collectionShortTvError.setCompoundDrawablesWithIntrinsicBounds(null,
                    collectionShortTvError.getContext().getResources().getDrawable(R.drawable.icon_error_network),
                    null, null);
            collectionShortBtn.setVisibility(View.GONE);
        } else {//没有收藏：
            collectionShortTvError.setText(R.string.error_empty_collection);
            collectionShortTvError.setCompoundDrawablesWithIntrinsicBounds(null,
                    collectionShortTvError.getContext().getResources().getDrawable(R.drawable.icon_error_empty_collection),
                    null, null);
            collectionShortBtn.setVisibility(View.VISIBLE);
        }
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
    public void refreshData(List<CollectionVideoBean> list) {
        if (collectionShortRecyclerView != null) {
            collectionShortRecyclerView.setVisibility(View.VISIBLE);
        }
        if (collectionShortErrorLayout != null) {
            collectionShortErrorLayout.setVisibility(View.GONE);
        }
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
    public void loadMoreData(List<CollectionVideoBean> list) {
        if (list.size() == 0) {
            refreshLayout.setNoMoreData(true);
        } else {
            int index = mList.size();
            mList.addAll(list);
            mAdapter.notifyItemRangeInserted(index, list.size());
        }
    }
}
