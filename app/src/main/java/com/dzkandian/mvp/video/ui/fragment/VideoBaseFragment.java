package com.dzkandian.mvp.video.ui.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
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
import com.dzkandian.app.MyApplication;
import com.dzkandian.app.config.EventBusTags;
import com.dzkandian.common.uitls.Constant;
import com.dzkandian.common.uitls.NetworkUtils;
import com.dzkandian.common.widget.laoding.LoadingProgressDialog;
import com.dzkandian.common.widget.recyclerview.LinearLayoutManagerWrapper;
import com.dzkandian.common.widget.recyclerview.StaggeredGridLayoutManagerWrapper;
import com.dzkandian.common.widget.recyclerview.StaggeredGridSpacesItemDecoration;
import com.dzkandian.mvp.video.contract.VideoBaseContract;
import com.dzkandian.mvp.video.di.component.DaggerVideoBaseComponent;
import com.dzkandian.mvp.video.di.module.VideoBaseModule;
import com.dzkandian.mvp.video.presenter.VideoBasePresenter;
import com.dzkandian.mvp.video.ui.adapter.VideoViewAdapter;
import com.dzkandian.mvp.video.ui.adapter.VideoWaterfallAdapter;
import com.dzkandian.storage.bean.video.VideoBean;
import com.dzkandian.storage.event.FloatingActionEvent;
import com.jess.arms.base.BaseFragment;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
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

/**
 * 视频子页面（列表）
 */
public class VideoBaseFragment extends BaseFragment<VideoBasePresenter> implements VideoBaseContract.View
//        NativeExpressAD.NativeExpressADListener
{

    @BindView(R.id.video_refreshLayout)
    SmartRefreshLayout mRefreshLayout;
    @BindView(R.id.video_recyclerView)
    RecyclerView mRecyclerView;

    private View error_view;

    private String mType;//资讯分类
    private boolean isWaterfall;//是否瀑布流
    private String mTextSize;//字体大小

    //    private List<Integer> mADViewPositions = new ArrayList<>();//记录广告实时位置列表
    private List<NativeResponse> mNrAdList;//百度广告数据列表
    private BaiduNative baiduNative;//百度广告
    private RequestParameters requestParameters;//百度广告参数设置
    private boolean isRefresh = true;//是否刷新
    private VideoViewAdapter mAdapter;
    private VideoWaterfallAdapter mWaterfallAdapter;
    private LinearLayoutManagerWrapper linearLayoutManager;
    private StaggeredGridLayoutManagerWrapper staggeredGridLayoutManager;
    private LoadingProgressDialog loadingProgressDialog;
    //    private NativeExpressAD mADManager;
    private long refreshVideoBaseLastTimes;//视频列表 刷新 的上一次时间；
    private long loadMoreVideoBaseLastTimes;//视频列表 加载 的上一次时间；
    private String beforeId = ""; //
    private int mBeforeDBPosition;  //数据库前面数据
    private int mLastDBPosition;    //数据库后面数据
    private List<VideoBean> mDBAllVideoBeans;   //数据库的全部数据

    @Override
    public void setupFragmentComponent(@NonNull AppComponent appComponent) {
        DaggerVideoBaseComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .videoBaseModule(new VideoBaseModule(this))
                .build()
                .inject(this);
    }

    @Override
    public View initView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_video_base, container, false);
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        try {
            mType = getArguments().getString("type");
            isWaterfall = getArguments().getBoolean("listType", false);
//            Timber.d("=========栏目类型: " + mType + " isWaterfall：" + isWaterfall);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        mTextSize = getArguments().getString("textSize");
        error_view = getLayoutInflater().inflate(R.layout.view_error_shortnetwork, null);

        mNrAdList = new ArrayList<>();

        initRecyclerView();
        if (getActivity() != null)
            initBaiDuAd(getActivity());
//        initNativeExpressAD();

        mRefreshLayout.setFooterHeight(25);
        mRefreshLayout.setDisableContentWhenRefresh(true);
        mRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (isInternet()) {
                    isRefresh = false;
                    mNrAdList.clear();
//                    Timber.d(" =============小视频   onLoadMore       beforeId" + beforeId);
                    fetchBaiDuAd();
                } else {
                    if (System.currentTimeMillis() - loadMoreVideoBaseLastTimes > 2000) {
                        loadMoreVideoBaseLastTimes = System.currentTimeMillis();
                        showMessage("网络请求失败，请连网后重试");
                    }
                    finishLoadMore();//隐藏加载更多
                }
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                if (isInternet()) {
                    isRefresh = true;
                    mNrAdList.clear();
                    refreshLayout.setNoMoreData(false);
                    fetchBaiDuAd();
                } else {
                    if (System.currentTimeMillis() - refreshVideoBaseLastTimes > 2000) {
                        refreshVideoBaseLastTimes = System.currentTimeMillis();
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
                int firstPosition = -1;
                if (linearLayoutManager != null) {
                    firstPosition = linearLayoutManager.findFirstVisibleItemPosition();
                } else if (staggeredGridLayoutManager != null) {
                    int[] firstPositions = staggeredGridLayoutManager.findFirstCompletelyVisibleItemPositions(new int[2]);
                    firstPosition = firstPositions[0];
                }

                if (newState == RecyclerView.SCROLL_STATE_IDLE && firstPosition == 0) {
                    EventBus.getDefault().post(new FloatingActionEvent.Builder().state(1).build(), EventBusTags.TAG_SHOW_STATE);
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
                    EventBus.getDefault().post(new FloatingActionEvent.Builder().state(2).build(), EventBusTags.TAG_SHOW_STATE);//0 点击回到顶部; 1:隐藏   2：显示
                } else if (dy > 0 && Math.abs(dy) > Math.abs(dx)) {
                    EventBus.getDefault().post(false, EventBusTags.TAG_BOTTOM_MENU);
                    EventBus.getDefault().post(new FloatingActionEvent.Builder().state(1).build(), EventBusTags.TAG_SHOW_STATE);//0 点击回到顶部; 1:隐藏   2：显示
                }

                if (mAdapter != null && mAdapter.getNewsSize() > 0) {
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
                } else if (mWaterfallAdapter != null && mWaterfallAdapter.getVideoSize() > 0) {
                    int[] lastChildPositions = ((StaggeredGridLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPositions(new int[2]);
//                    Timber.d("=======lastChildPositions: " + Arrays.toString(lastChildPositions));
                    for (int lastChildPosition : lastChildPositions) {
                        if (lastChildPosition == mWaterfallAdapter.getVideoSize() - 1) {
                            recyclerView.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(),
                                    SystemClock.uptimeMillis(), MotionEvent.ACTION_CANCEL, 0, 0, 0));
                        }
                    }
                }
            }
        });

    }

    @Subscriber(tag = EventBusTags.TAG_TEXT_SIZE)
    public void textSize(boolean size) {
        if (size) {
            if (staggeredGridLayoutManager != null && mWaterfallAdapter != null) {
                int[] firstArray = new int[2];//创建数组保存数据；
                int[] lastArray = new int[2];
                firstArray = staggeredGridLayoutManager.findFirstVisibleItemPositions(firstArray);
                lastArray = staggeredGridLayoutManager.findLastVisibleItemPositions(lastArray);

                int firstPosition = 0;//屏幕可见最上面的item 的 position
                int lastPosition = 0;//屏幕可见最下面的item 的 position
                if (firstArray != null && firstArray.length == 2) {
                    firstPosition = firstArray[0] < firstArray[1] ? firstArray[0] : firstArray[1];
                }
                if (lastArray != null && lastArray.length == 2) {
                    lastPosition = lastArray[0] < lastArray[1] ? lastArray[1] : lastArray[0];
                }

                int start = firstPosition - 6 < 0 ? 0 : firstPosition - 6;//用于adapter刷新局部布局的第一个 position
                int count = (lastPosition - firstPosition < 0 ? 0 : lastPosition - firstPosition) + 12;//用于adapter刷新局部布局的总计 item 数

                mWaterfallAdapter.notifyItemRangeChanged(start, count);
            }
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
     * 初始化RecyclerView
     */
    private void initRecyclerView() {
        if (isWaterfall) {
            mWaterfallAdapter = new VideoWaterfallAdapter(this, mType, mTextSize);
            staggeredGridLayoutManager = new StaggeredGridLayoutManagerWrapper(2, StaggeredGridLayoutManager.VERTICAL);
            mRecyclerView.setLayoutManager(staggeredGridLayoutManager);
            mRecyclerView.addItemDecoration(new StaggeredGridSpacesItemDecoration(2));
            mRecyclerView.setAdapter(mWaterfallAdapter);
        } else {
            mAdapter = new VideoViewAdapter(this, mType, mTextSize);
            linearLayoutManager = new LinearLayoutManagerWrapper(getContext());
            mRecyclerView.setLayoutManager(linearLayoutManager);
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setAdapter(mAdapter);
        }

    }

    /**
     * 接收悬乎窗状态事件（显示/隐藏）
     * 0 点击回到顶部; 1:隐藏   2：显示
     *
     * @param floatingActionEvent 刷新状态事件
     */
    @Subscriber(tag = EventBusTags.TAG_SHOW_STATE)
    public void receiveRefreshEvent(FloatingActionEvent floatingActionEvent) {
        if (floatingActionEvent.getState() == 0 && isWaterfall) {
            staggeredGridLayoutManager.scrollToPositionWithOffset(0, 0);
        } else if (floatingActionEvent.getState() == 0 && !isWaterfall) {
            linearLayoutManager.scrollToPositionWithOffset(0, 0);
//            linearLayoutManager.setStackFromEnd(false);
        }
    }

    /**
     * 接收到小视频详情页返回到列表事件
     */
    @Subscriber(tag = EventBusTags.TAG_SHORT_FINISH)
    public void receiveRefreshEvent(String currPlayId) {
        mDBAllVideoBeans = MyApplication.get().getDaoSession().getVideoBeanDao().loadAll();
        List<VideoBean> showVideoBeans = new ArrayList<>();
        int befoPositon = 0;
        int currPlayPosition = 0;

        for (int i = 0; i < mDBAllVideoBeans.size(); i++) {
            if (mDBAllVideoBeans.get(i).getVideoId().equals(currPlayId)) { //这里是判断播放页 播放视频ID在数据是否存在  在哪个 位置
                currPlayPosition = i;
            }
        }
        if (isWaterfall && currPlayPosition > 0) {
            if (mWaterfallAdapter != null && staggeredGridLayoutManager != null) {
                Timber.d("=======小视频接收到小视频详情页返回到列表事件 currPlayPosition   " + currPlayPosition + "          全部视频多少    " + mDBAllVideoBeans.size());

                if (mDBAllVideoBeans.size() > 1 && currPlayPosition < mDBAllVideoBeans.size()) {
                    if (mDBAllVideoBeans.size() > currPlayPosition) {

                        if (currPlayPosition > 10) { //如果大于10取前面10条数据
                            mBeforeDBPosition = currPlayPosition - 10;
                            Timber.d("====小视频  返回列表   mBeforeDBPosition  " + mBeforeDBPosition);
                            for (int i = mBeforeDBPosition; i < currPlayPosition; i++) { //取当前视频之前的10条数据
                                showVideoBeans.add(mDBAllVideoBeans.get(i));
                            }
                        } else {
                            mBeforeDBPosition = 0;
                            for (int i = 0; i < currPlayPosition; i++) { //小于10则有多少取多少条
                                showVideoBeans.add(mDBAllVideoBeans.get(i));
                            }
                            befoPositon = showVideoBeans.size();
                        }
                    }

                    mLastDBPosition = currPlayPosition + 10;

                    Timber.d("====小视频  返回列表   mLastDBPosition  " + mLastDBPosition);
                    if (mLastDBPosition < mDBAllVideoBeans.size()) { //如果数据大于10条则取10条
                        for (int i = currPlayPosition; i < mLastDBPosition; i++) { //取当前视频之后的10条数据
                            showVideoBeans.add(mDBAllVideoBeans.get(i));
                        }
                    } else { //小于10条则把所有数据全添加进去
                        for (int i = currPlayPosition; i < mDBAllVideoBeans.size(); i++) {
                            showVideoBeans.add(mDBAllVideoBeans.get(i));
                        }
                    }


                    mWaterfallAdapter.refreshNews(showVideoBeans); //这里必须刷新数据库中的数据  这样才能自增ID对应
//                    mRecyclerView.smoothScrollToPosition(currPlayPosition);

                    try {
                        if (currPlayPosition > 10) {
                            staggeredGridLayoutManager.scrollToPositionWithOffset(10, 0);
                        } else {
                            staggeredGridLayoutManager.scrollToPositionWithOffset(befoPositon, 0);
                        }
                    } catch (Exception ignored) {

                    }
                    if (!TextUtils.isEmpty(mDBAllVideoBeans.get(mDBAllVideoBeans.size() - 1).getVideoId()))
                        beforeId = mDBAllVideoBeans.get(mDBAllVideoBeans.size() - 1).getVideoId();
//                    for (int i = 0; i < videoBeans.size(); i++) {
//                        Timber.d("=============小视频  回到列表  " + videoBeans.get(i).getVideoId() + "         第  " + i);
//                    }
//                    Timber.d("=============小视频  回到列表  " + String.valueOf(videoBeans.get(videoBeans.size() - 1).getVideoId()));
                }
            }
        }
    }


    /**
     * 是否请求权限
     */
    public boolean isQuestPermission() {
        return ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
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
            mRefreshLayout.finishRefresh(0);
        }
    }

    @Override
    public void finishLoadMore() {
        mRefreshLayout.finishLoadMore(0);
    }

    /**
     * 刷新数据
     *
     * @param data 资讯列表
     */
    @Override
    public void refreshData(List<VideoBean> data) {
        if (data.size() == 0) {
            mRefreshLayout.setRefreshContent(error_view);
        } else {
            if (isWaterfall) { // 小视频
                MyApplication.get().getDaoSession().getVideoBeanDao().deleteAll();
                MyApplication.get().getDaoSession().getVideoBeanDao().saveInTx(data); //每次刷新先清空数据库内小视频数据再保存
//                Timber.d("=============小视频数据库  刷新" + MyApplication.get().getDaoSession().getVideoBeanDao().loadAll().size());
            }
            mRefreshLayout.setRefreshContent(mRecyclerView);

            if (data.size() > 1 && !TextUtils.isEmpty(data.get(data.size() - 1).getVideoId()))
                beforeId = data.get(data.size() - 1).getVideoId();

            questAd(data);  //广告位去显示广告

            if (isWaterfall) {
                if (mWaterfallAdapter != null) {
                    mWaterfallAdapter.refreshNews(data);
                }
            } else {
                if (mAdapter != null) {
                    mAdapter.refreshNews(data);
                }
            }

            mRefreshLayout.setEnableLoadMore(true);
        }

    }

    @Override
    public void loadMoreData(List<VideoBean> data) {
        if (data.size() == 0) {
            if (mRefreshLayout != null)
                mRefreshLayout.setNoMoreData(true);
            return;
        }

        if (data.size() > 1 && !TextUtils.isEmpty(data.get(data.size() - 1).getVideoId()))
            beforeId = data.get(data.size() - 1).getVideoId();

        if (isWaterfall) {
            List<VideoBean> videoDBBeans = MyApplication.get().getDaoSession().getVideoBeanDao().loadAll();
            String videoId = data.get(0).getVideoId();
            for (int i = 0; i < videoDBBeans.size(); i++) {
                if (videoDBBeans.get(i).getVideoId().equals(videoId)) { //如果数据库和后台返回的视频有相同ID 则移除后面全部
                    for (int j = i; j < videoDBBeans.size(); j++) {
                        MyApplication.get().getDaoSession().getVideoBeanDao().delete(videoDBBeans.get(j));
                    }
                }
            }
            mLastDBPosition = mLastDBPosition + data.size();  //加载了数据 下标也要跟随增加   不然会取重复数据
            MyApplication.get().getDaoSession().getVideoBeanDao().saveInTx(data); //把加载的小视频数据存入数据库
//            Timber.d("=============小视频数据库  加载" + MyApplication.get().getDaoSession().getVideoBeanDao().loadAll().size());
//            for (int i = 0; i < data.size(); i++) {
//                Timber.d("=============小视频  后台返回数据  " + data.get(i).getVideoId() + "         第  " + i);
//            }
        }

        questAd(data);  //广告位去显示广告

        if (isWaterfall) {
            if (mWaterfallAdapter != null)
                mWaterfallAdapter.loadMoreNews(data);
        } else {
            if (mAdapter != null)
                mAdapter.loadMoreNews(data);
        }

    }

    @Override
    public void refreshFailed() {
        if (isWaterfall) {
            if (mRefreshLayout != null)
                mRefreshLayout.setRefreshContent(error_view);
        } else {
            if (mAdapter != null)
                mAdapter.showEmptyView(true);
        }

        if (mRefreshLayout != null) {
            mRefreshLayout.setEnableLoadMore(false);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
//        MobclickAgent.onPageStart(this.getClass().getSimpleName());
    }

    @Override
    public void onPause() {
        if (mRefreshLayout != null) {
            mRefreshLayout.finishRefresh(0);
            mRefreshLayout.finishLoadMore(0);
        }
        super.onPause();

//        MobclickAgent.onPageEnd(this.getClass().getSimpleName());
    }

    @Override
    public void onDestroy() {
        if (loadingProgressDialog != null && loadingProgressDialog.isShowing()) {
            loadingProgressDialog.dismiss();
        }

        if (mAdapter != null) {
            mAdapter.recycle();
            mAdapter = null;
        }

        if (mWaterfallAdapter != null) {
            mWaterfallAdapter.recycle();
            mWaterfallAdapter = null;
        }

        if (baiduNative != null) {
            baiduNative.destroy();
            baiduNative = null;
        }

        if (requestParameters != null) {
            requestParameters = null;
        }

        if (mNrAdList != null) {
            mNrAdList.clear();
        }

        linearLayoutManager = null;
        staggeredGridLayoutManager = null;
        loadingProgressDialog = null;
        if (isWaterfall) //如果是小视频列表才清空
            MyApplication.get().getDaoSession().getVideoBeanDao().deleteAll(); //退出时清空

        super.onDestroy();

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

    @Override
    public void killMyself() {

    }

    /**
     * 请求广告
     */
    public void initBaiDuAd(Activity activity) {
        baiduNative = new BaiduNative(activity, isWaterfall ?
                Constant.BAIDU_AD_ID_VIDEO_WATEFALL : Constant.BAIDU_AD_ID_VIDEO,
                new BaiduNative.BaiduNativeNetworkListener() {
                    @Override
                    public void onNativeLoad(List<NativeResponse> list) {

                        if (list != null && list.size() > 0) {
                            if (isRefresh) {
                                mNrAdList = list;
                                if (!refreshDBVideoBean()) {
                                    if (mPresenter != null) {
                                        mPresenter.getVideoList(isRefresh, mType, beforeId);
                                    }
                                    Timber.d("====小视频  刷新数据请求后台接口   mBeforeDBPosition  " + mBeforeDBPosition);
                                }
                            } else {
                                mNrAdList = list;
                                if (!loadDBVideoBean()) {
                                    if (mPresenter != null) {
                                        mPresenter.getVideoList(isRefresh, mType, beforeId);
                                    }
                                    Timber.d("====小视频  加载数据请求后台接口   mLastDBPosition  " + mLastDBPosition);
                                }
                            }
                        }
                    }

                    @Override
                    public void onNativeFail(NativeErrorCode nativeErrorCode) {
                        if (isRefresh) {
                            if (!refreshDBVideoBean()) {
                                if (mPresenter != null) {
                                    mPresenter.getVideoList(isRefresh, mType, beforeId);
                                    Timber.d("====小视频  广告加载失败 刷新数据请求后台接口");
                                }
                            }
                        } else {
                            if (!loadDBVideoBean()) {
                                if (mPresenter != null) {
                                    mPresenter.getVideoList(isRefresh, mType, beforeId);
                                    Timber.d("====小视频  广告加载失败 加载数据请求后台接口");
                                }
                            }
                        }
                    }
                });
        requestParameters = new RequestParameters.Builder()
                .downloadAppConfirmPolicy(RequestParameters.DOWNLOAD_APP_CONFIRM_ONLY_MOBILE).build();
    }

    private void fetchBaiDuAd() {
        if (baiduNative != null)
            baiduNative.makeRequest(requestParameters);
    }

    //加载判断数据库有没有数据  有则取数据库 没有去请求接口
    private boolean loadDBVideoBean() {
        List<VideoBean> showVideoBeans = new ArrayList<>();
        if (isWaterfall && mDBAllVideoBeans != null && mLastDBPosition + 10 < mDBAllVideoBeans.size()) { //数据库还有多于十条数据
            for (int i = mLastDBPosition; i < mLastDBPosition + 10; i++) {
                showVideoBeans.add(mDBAllVideoBeans.get(i));
            }
            mLastDBPosition += 10;
            questAd(showVideoBeans);  //广告位去显示广告
            mWaterfallAdapter.loadMoreNews(showVideoBeans);
            Timber.d("====小视频  加载数据大于10   mLastDBPosition  " + mLastDBPosition);
            mRefreshLayout.finishLoadMore(true);//结束加载
            return true;
        } else if (isWaterfall && mDBAllVideoBeans != null && mLastDBPosition < mDBAllVideoBeans.size()) {
            for (int i = mLastDBPosition; i < mDBAllVideoBeans.size(); i++) {
                showVideoBeans.add(mDBAllVideoBeans.get(i));
            }
            mWaterfallAdapter.loadMoreNews(showVideoBeans);
            questAd(showVideoBeans);  //广告位去显示广告
            mLastDBPosition = mLastDBPosition + showVideoBeans.size();
            mRefreshLayout.finishLoadMore(true);//结束加载
            Timber.d("====小视频  加载数据小于10  mLastDBPosition   " + mLastDBPosition);
            return true;
        }
        return false;
    }

    //刷新时先判断数据库有没有数据
    private boolean refreshDBVideoBean() {
        List<VideoBean> showVideoBeans = new ArrayList<>();
        if (isWaterfall && mDBAllVideoBeans != null && mDBAllVideoBeans.size() > mBeforeDBPosition && mBeforeDBPosition > 10) { //如果数据还有大于10条数据取前十条
            for (int i = mBeforeDBPosition - 10; i < mBeforeDBPosition; i++) {
                showVideoBeans.add(mDBAllVideoBeans.get(i));
            }
            questAd(showVideoBeans);  //广告位去显示广告
            mBeforeDBPosition = mBeforeDBPosition - 10;
            mWaterfallAdapter.refreshDBNews(showVideoBeans);
            mRefreshLayout.finishRefresh(true);//结束加载
            Timber.d("====小视频  刷新数据大于10  mBeforeDBPosition   " + mBeforeDBPosition);
            return true;
        } else if (isWaterfall && mDBAllVideoBeans != null && mDBAllVideoBeans.size() > mBeforeDBPosition && mBeforeDBPosition > 0 && mBeforeDBPosition < 10) { //小于10条并有数据 全取出来
            for (int i = 0; i < mBeforeDBPosition; i++) {
                showVideoBeans.add(mDBAllVideoBeans.get(i));
            }
            questAd(showVideoBeans);  //广告位去显示广告
            mWaterfallAdapter.refreshDBNews(showVideoBeans);
            mBeforeDBPosition = 0;
            mRefreshLayout.finishRefresh(true);//结束加载
            Timber.d("====小视频  刷新数据小于10  mBeforeDBPosition   " + mBeforeDBPosition);
            return true;
        }
        return false;
    }

    //广告位显示第三方广告
    private void questAd(List<VideoBean> videoBeanList) {
        //广告数量
        int ad_size = 0;

        //获取的百度广告列表大于0才去遍历视频列表
        if (mNrAdList.size() > 0) {
            for (int i = 0; i < videoBeanList.size(); i++) {
                VideoBean video = videoBeanList.get(i);
                if (TextUtils.equals(video.getType(), "ad")) {

                    if (ad_size < mNrAdList.size()) {
                        NativeResponse nrAd = mNrAdList.get(ad_size);
                        video.setAdType(2);
                        video.setNativeResponse(nrAd);
                        videoBeanList.remove(i);
                        videoBeanList.add(i, video);
                        ad_size++;
                    }
                }
            }
            mNrAdList.clear();
        }
        Timber.d("======VideoBaseFragment -- 后台设置的广告数量: " + ad_size);
    }


}
