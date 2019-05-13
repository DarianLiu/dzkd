package com.dzkandian.mvp.news.presenter;

import android.app.Application;
import android.support.annotation.NonNull;

import com.dzkandian.app.http.utils.RxUtils;
import com.dzkandian.mvp.news.contract.SearchContract;
import com.dzkandian.storage.bean.SearchBean;
import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.integration.AppManager;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.utils.RxLifecycleUtils;

import java.util.List;

import javax.inject.Inject;

import me.jessyan.rxerrorhandler.core.RxErrorHandler;
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber;


@ActivityScope
public class SearchPresenter extends BasePresenter<SearchContract.Model, SearchContract.View> {
    @Inject
    RxErrorHandler mErrorHandler;
    @Inject
    Application mApplication;
    @Inject
    ImageLoader mImageLoader;
    @Inject
    AppManager mAppManager;

    @Inject
    public SearchPresenter(SearchContract.Model model, SearchContract.View rootView) {
        super(model, rootView);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mErrorHandler = null;
        this.mAppManager = null;
        this.mImageLoader = null;
        this.mApplication = null;
    }


    /**
     * 获取搜索列表
     *
     * @param isRefresh 是否刷新
     * @param keyword   搜索关键词
     * @param num       分页参数：最后一条的时间戳
     * @param beforeId  分页参数：最后一条的ID
     */
    public void getSearchList(boolean isRefresh, String keyword, long num, String beforeId) {
        mModel.getSearchList(keyword, num, beforeId)
                .doFinally(() -> {
                    if (isRefresh) {
                        mRootView.finishRefresh(); //结束刷新
                    } else {
                        mRootView.finishLoadMore();//结束加载更多
                    }
                })
                .compose(RxLifecycleUtils.bindToLifecycle(mRootView))
                .compose(RxUtils.applyhideSchedulers(mRootView))
                .compose(RxUtils.handleBaseResult(mApplication))
                .subscribe(new ErrorHandleSubscriber<List<SearchBean>>(mErrorHandler) {
                    @Override
                    public void onNext(@NonNull List<SearchBean> data) {
                        if (isRefresh) {
                            mRootView.refreshData(data);
                        } else {
                            mRootView.loadMoreData(data);
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        super.onError(t);
                        if (isRefresh) {
                            mRootView.refreshFailed();
                        }
                    }
                });
    }
}
