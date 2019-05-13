package com.dzkandian.mvp.news.ui.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.baidu.mobad.feeds.BaiduNative;
import com.baidu.mobad.feeds.NativeErrorCode;
import com.baidu.mobad.feeds.NativeResponse;
import com.baidu.mobad.feeds.RequestParameters;
import com.dzkandian.R;
import com.dzkandian.app.config.EventBusTags;
import com.dzkandian.common.uitls.Constant;
import com.dzkandian.common.uitls.NetworkUtils;
import com.dzkandian.common.widget.laoding.LoadingProgressDialog;
import com.dzkandian.common.widget.recyclerview.LinearLayoutManagerWrapper;
import com.dzkandian.mvp.news.contract.NewBaseContract;
import com.dzkandian.mvp.news.di.component.DaggerNewBaseComponent;
import com.dzkandian.mvp.news.di.module.NewBaseModule;
import com.dzkandian.mvp.news.presenter.NewBasePresenter;
import com.dzkandian.mvp.news.ui.adapter.NewsAdapter;
import com.dzkandian.storage.bean.news.NewsBean;
import com.dzkandian.storage.event.FloatingActionEvent;
import com.jess.arms.base.BaseFragment;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.jess.arms.utils.DataHelper;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import timber.log.Timber;

import static com.jess.arms.utils.Preconditions.checkNotNull;


public class NewBaseFragment extends BaseFragment<NewBasePresenter> implements NewBaseContract.View {

    @BindView(R.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    private String mType;//资讯分类
    private String mTextSize;//字体大小

    //    private List<Integer> mADViewPositions = new ArrayList<>();//记录广告实时位置列表
    List<NativeResponse> adNativeList;//百度广告数据列表
    private BaiduNative baiduNative;//百度广告
    private RequestParameters requestParameters;//百度广告参数设置
    private boolean isRefresh = true;//是否刷新
    private NewsAdapter mAdapter;
    private LinearLayoutManagerWrapper linearLayoutManager;
    private LoadingProgressDialog loadingProgressDialog;

    private int haveTouchHardware;//是否有触摸硬件（触摸面积不为0）
    private String first_touch_area; //上一次保存的触摸面积
    //    private NativeExpressAD mADManager;//原生广告
    private long refreshNewsBaseLastTimes;//新闻列表 刷新 的上一次时间；
    private long loadMoreNewsBaseLastTimes;//新闻列表 加载 的上一次时间；

    @Override
    public void setupFragmentComponent(@NonNull AppComponent appComponent) {
        DaggerNewBaseComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .newBaseModule(new NewBaseModule(this))
                .build()
                .inject(this);
    }

    @Override
    public View initView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_new_base, container, false);
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        try {
            mType = getArguments().getString("type");
        } catch (NullPointerException e) {
            Timber.d("NullPointerException: " + "没有传值");
        }
        mTextSize = getArguments().getString("textSize");

        //是否有触摸硬件（触摸面积不为0）
        haveTouchHardware = DataHelper.getIntergerSF(getActivity().getApplicationContext(), Constant.SP_KEY_HAVE_TOUCH_HARDWARE);

        first_touch_area = DataHelper.getStringSF(getActivity().getApplicationContext(), Constant.SP_KEY_TOUCH_AREA);

        adNativeList = new ArrayList<>();
        initRecyclerView();
        if (getActivity() != null)
            initBaiDuAd(getActivity());
        checkTouchHardware();

//        initNativeExpressAD();//初始化广告

        mRefreshLayout.setFooterHeight(25);
        mRefreshLayout.setDisableContentWhenRefresh(true);
        mRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (isInternet()) {
                    isRefresh = false;
                    adNativeList.clear();
                    fetchBaiduAd();
                } else {
                    if (System.currentTimeMillis() - loadMoreNewsBaseLastTimes > 2000) {
                        loadMoreNewsBaseLastTimes = System.currentTimeMillis();
                        showMessage("网络请求失败，请连网后重试");
                    }
                    finishLoadMore();//隐藏加载更多
                }
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                if (isInternet()) {
                    isRefresh = true;
                    adNativeList.clear();
                    refreshLayout.setNoMoreData(false);
                    fetchBaiduAd();
                } else {
                    if (System.currentTimeMillis() - refreshNewsBaseLastTimes > 2000) {
                        refreshNewsBaseLastTimes = System.currentTimeMillis();
                        showMessage("网络请求失败，请连网后重试");
                        refreshFailed();
                    }
                    finishRefresh();//隐藏刷新
                }
            }
        });
        mRefreshLayout.autoRefresh();

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                //如果两个条件都满足则说明是真正的滑动到了顶部
                int firstPosition = linearLayoutManager.findFirstVisibleItemPosition();
                if (newState == RecyclerView.SCROLL_STATE_IDLE && firstPosition == 0) {
//                    EventBus.getDefault().post(new FloatingActionEvent.Builder().state(1).build(), EventBusTags.TAG_SHOW_STATE);
//                    EventBus.getDefault().post(true, EventBusTags.TAG_BOTTOM_MENU);
                }

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy < 0 && Math.abs(dy) > Math.abs(dx)) {
                    if (mRefreshLayout.getState() == RefreshState.Loading) {
                        mRefreshLayout.setNestedScrollingEnabled(false);
                    }
                    EventBus.getDefault().post(true, EventBusTags.TAG_BOTTOM_MENU);
//                    EventBus.getDefault().post(new FloatingActionEvent.Builder().state(2).build(), EventBusTags.TAG_SHOW_STATE);//0 点击回到顶部; 1:隐藏   2：显示
//                    fab.show();//向上滑动
                } else if (dy > 0 && Math.abs(dy) > Math.abs(dx)) {
                    EventBus.getDefault().post(false, EventBusTags.TAG_BOTTOM_MENU);
//                    EventBus.getDefault().post(new FloatingActionEvent.Builder().state(1).build(), EventBusTags.TAG_SHOW_STATE);//0 点击回到顶部; 1:隐藏   2：显示
                }

                if (mAdapter.getNewsSize() > 0) {
                    int lastChildPosition = recyclerView.getLayoutManager().getChildCount() - 1;
                    //得到当前显示的最后一个item的view
                    View lastChildView = recyclerView.getLayoutManager().getChildAt(lastChildPosition);
                    //得到lastChildView的bottom坐标值
                    int lastChildBottom = lastChildView.getBottom();
                    //得到Recyclerview的底部坐标减去底部padding值，也就是显示内容最底部的坐标
                    int recyclerBottom = recyclerView.getBottom() - recyclerView.getPaddingBottom();
                    //通过这个lastChildView得到这个view当前的position值
                    int lastPosition = recyclerView.getLayoutManager().getPosition(lastChildView);

                    //判断lastChildView的bottom值跟recyclerBottom
                    //判断lastPosition是不是最后一个position
                    //如果两个条件都满足则说明是真正的滑动到了底部
                    if (lastChildBottom == recyclerBottom && lastPosition == recyclerView.getLayoutManager().getItemCount() - 1) {
                        recyclerView.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(),
                                SystemClock.uptimeMillis(), MotionEvent.ACTION_CANCEL, 0, 0, 0));

                    }

                }
            }
        });

    }

    @Subscriber(tag = EventBusTags.TAG_TEXT_SIZE)
    public void textSize(boolean size) {
        if (size) {
            if (linearLayoutManager != null && mAdapter != null) {
                int firstPosition = linearLayoutManager.findFirstVisibleItemPosition();//屏幕可见最上面的item 的 position
                int lastPosition = linearLayoutManager.findLastVisibleItemPosition();//屏幕可见最下面的item 的 position
                int start = firstPosition - 6 < 0 ? 0 : firstPosition - 6;//用于adapter刷新局部布局的第一个 position
                int count = (lastPosition - firstPosition < 0 ? 0 : lastPosition - firstPosition) + 12;//用于adapter刷新局部布局的总计 item 数
                mAdapter.notifyItemRangeChanged(start, count);
            }
        }
    }

    /*判断当前是否有网络*/
    private boolean isInternet() {
        if (getContext() != null) {
            return NetworkUtils.checkNetwork(getContext().getApplicationContext());
        } else {
            return true;
        }
    }

    /**
     * 检测触摸硬件
     */
    @SuppressLint("ClickableViewAccessibility")
    private void checkTouchHardware() {
        if (haveTouchHardware != 1) {
            mRecyclerView.setOnTouchListener((v, event) -> {
                float touch_area = event.getSize();
//                Timber.d("========touch area：" + touch_area);
                if (touch_area > 0 && touch_area != 1 && haveTouchHardware != 1) {
                    if (TextUtils.isEmpty(first_touch_area)) {
                        DataHelper.setStringSF(getActivity().getApplicationContext(), Constant.SP_KEY_TOUCH_AREA, String.valueOf(touch_area));
                    } else if (!TextUtils.equals(first_touch_area, String.valueOf(touch_area))) {
                        haveTouchHardware = 1;
                        DataHelper.setIntergerSF(getActivity().getApplicationContext(), Constant.SP_KEY_HAVE_TOUCH_HARDWARE, haveTouchHardware);
                    }
                }
                return false;
            });
        }
    }


    /**
     * 初始化RecyclerView
     */
    private void initRecyclerView() {
        mAdapter = new NewsAdapter(mType, this, mTextSize);
        linearLayoutManager = new LinearLayoutManagerWrapper(this.getContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);
//        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mAdapter);
    }

    /**
     * 接收悬乎窗状态事件（显示/隐藏）
     * 0 点击回到顶部; 1:隐藏   2：显示
     *
     * @param floatingActionEvent 刷新状态事件
     */
    @Subscriber(tag = EventBusTags.TAG_SHOW_STATE)
    public void receiveRefreshEvent(FloatingActionEvent floatingActionEvent) {
        if (floatingActionEvent.getState() == 0) {
            linearLayoutManager.scrollToPositionWithOffset(0, 0);
            linearLayoutManager.setStackFromEnd(false);
        }
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
    public void finishRefresh() {
        if (mRefreshLayout != null) {
            mRefreshLayout.finishRefresh();
        }
    }

    @Override
    public void finishLoadMore() {
        mRefreshLayout.finishLoadMore();
    }

    public boolean isQuestPermission() {
        return ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * 刷新数据
     *
     * @param data 资讯列表
     */
    @Override
    public void refreshData(List<NewsBean> data) {
        int ad_size = 0;//广告数量

        //获取的百度广告列表大于0才去遍历视频列表
        if (adNativeList.size() > 0) {
            for (int i = 0; i < data.size(); i++) {
                NewsBean news = data.get(i);
                if (TextUtils.equals(news.getType(), "ad")) {
                    if (ad_size < adNativeList.size()) {
                        news.setAdType(2);
                        news.setNativeResponse(adNativeList.get(ad_size));
                        data.remove(i);
                        data.add(i, news);
                        ad_size++;
                    }
                }
            }
            adNativeList.clear();
        }
        mAdapter.refreshNews(data);
        if (mRefreshLayout != null)
            mRefreshLayout.setEnableLoadMore(true); //刷新成功 启用上拉加载
    }

    /**
     * 加载更多数据
     *
     * @param data 资讯列表
     */
    @Override
    public void loadMoreData(List<NewsBean> data) {
        if (data.size() == 0) {
            mRefreshLayout.setNoMoreData(true);
        } else {
            int ad_size = 0;//广告数量

            //获取的百度广告列表大于0才去遍历视频列表
            if (adNativeList.size() > 0) {
                for (int i = 0; i < data.size(); i++) {
                    NewsBean news = data.get(i);
                    if (TextUtils.equals(news.getType(), "ad")) {
                        if (ad_size < adNativeList.size()) {
                            news.setAdType(2);
                            news.setNativeResponse(adNativeList.get(ad_size));
                            data.remove(i);
                            data.add(i, news);
                            ad_size++;
                        }
                    }
                }
                adNativeList.clear();
            }
            mAdapter.loadMoreNews(data);
        }

    }

    @Override
    public void refreshFailed() {
        mAdapter.showEmptyView(true);
        if (mRefreshLayout != null)
            mRefreshLayout.setEnableLoadMore(false);  //显示异常布局 禁用上拉加载
    }

    @Override
    public void onResume() {
        super.onResume();
//        MobclickAgent.onPageStart("news-" + mType);
    }

    @Override
    public void onPause() {
        super.onPause();
//        MobclickAgent.onPageEnd("news-" + mType);
    }

    @Override
    public void onDestroy() {
        if (loadingProgressDialog != null && loadingProgressDialog.isShowing()) {
            loadingProgressDialog.dismiss();
        }
        if (mAdapter != null) {
//            mAdapter.destroyADView();
            mAdapter.recycle();
            mAdapter = null;
        }

        if (linearLayoutManager != null) {
            linearLayoutManager = null;
        }
        if (baiduNative != null) {
            baiduNative.destroy();
            baiduNative = null;
        }
        if (requestParameters != null) {
            requestParameters = null;
        }
        if (adNativeList != null) {
            adNativeList.clear();
        }
        super.onDestroy();

        loadingProgressDialog = null;

    }

    @Override
    public void killMyself() {

    }

    private void initBaiDuAd(Activity activity) {
        baiduNative = new BaiduNative(
                activity,
                Constant.BAIDU_AD_ID_NEWS,
                new BaiduNative.BaiduNativeNetworkListener() {
                    @Override
                    public void onNativeLoad(List<NativeResponse> list) {
                        if (list != null && list.size() > 0) {
                            if (mPresenter != null) {
                                mPresenter.getNewsList(isRefresh, mType);
                            }
                            adNativeList = list;
//                            for (int i = 0; i < list.size(); i++) {
//                                Timber.d("==========广告标题：" + i + ":   " + list.get(i).getTitle());
//                            }
                        }
                    }

                    @Override
                    public void onNativeFail(NativeErrorCode nativeErrorCode) {
                        if (mPresenter != null) {
                            mPresenter.getNewsList(isRefresh, mType);
                        }
                    }
                });
        requestParameters = new RequestParameters.Builder()
                .downloadAppConfirmPolicy(RequestParameters.DOWNLOAD_APP_CONFIRM_ONLY_MOBILE).build();
    }

    private void fetchBaiduAd() {
        if (baiduNative != null)
            baiduNative.makeRequest(requestParameters);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbindDrawables(getView());
    }

    private void unbindDrawables(View view) {
        if (view != null && view.getBackground() != null) {
            view.getBackground().setCallback(null);
        }
        if (view != null && view instanceof ViewGroup && !(view instanceof AdapterView)) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                unbindDrawables(((ViewGroup) view).getChildAt(i));
            }
            ((ViewGroup) view).removeAllViews();
        }
    }
}
