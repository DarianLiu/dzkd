package com.dzkandian.mvp.mine.presenter;

import android.app.Application;
import android.support.annotation.NonNull;

import com.dzkandian.app.http.utils.RequestParamUtils;
import com.dzkandian.app.http.utils.RxUtils;
import com.dzkandian.common.uitls.Constant;
import com.dzkandian.storage.bean.CollectionVideoBean;
import com.jess.arms.integration.AppManager;
import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.http.imageloader.ImageLoader;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import me.jessyan.rxerrorhandler.core.RxErrorHandler;
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber;
import okhttp3.RequestBody;

import javax.inject.Inject;

import com.dzkandian.mvp.mine.contract.CollectionShortContract;
import com.jess.arms.utils.DataHelper;
import com.jess.arms.utils.RxLifecycleUtils;

import java.util.List;


@ActivityScope
public class CollectionShortPresenter extends BasePresenter<CollectionShortContract.Model, CollectionShortContract.View> {
    @Inject
    RxErrorHandler mErrorHandler;
    @Inject
    Application mApplication;
    @Inject
    ImageLoader mImageLoader;
    @Inject
    AppManager mAppManager;

    @Inject
    public CollectionShortPresenter(CollectionShortContract.Model model, CollectionShortContract.View rootView) {
        super(model, rootView);
    }

    private String getToken() {
        return DataHelper.getStringSF(mApplication, Constant.SP_KEY_TOKEN);
    }

    private int page = 1;


    /**
     * //@param page    	int	页码
     * //@param limit   	int	数量
     */
    public void getCollectionList(boolean isRefresh) {
        if (isRefresh)
            page = 1;
        RequestBody requestBody = RequestParamUtils.videoCollectionList(mApplication, String.valueOf(page), String.valueOf(10), "");
        mModel.collectionShortList(getToken(), requestBody)
                .subscribeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(() -> {
                    if (isRefresh) {
                        mRootView.finishRefresh();//隐藏刷新
                    } else {
                        mRootView.finishLoadMore();//隐藏加载更多
                    }
                }).compose(RxLifecycleUtils.bindToLifecycle(mRootView))
                .compose(RxUtils.handleBaseResult(mAppManager.getTopActivity()))
                .subscribe(new ErrorHandleSubscriber<List<CollectionVideoBean>>(mErrorHandler) {
                    @Override
                    public void onNext(@NonNull List<CollectionVideoBean> list) {
                        if (list.size() != 0) {
                            page += 1;
                        }
                        if (isRefresh) {
                            if (list.size() != 0) {
                                mRootView.refreshData(list);
                            } else {
                                mRootView.refreshFailed(false);
                            }
                        } else {
                            mRootView.loadMoreData(list);
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        super.onError(t);
                        if (isRefresh)
                            mRootView.refreshFailed(true);
                    }
                });
    }

    public void removeNewsCollection(int position, String url) {
        RequestBody requestBody = RequestParamUtils.collectionVideo(mApplication, url);
        mModel.removeShortCollection(getToken(), requestBody)
                .compose(RxUtils.applySchedulers(mRootView))
                .compose(RxUtils.handleBaseResult(mApplication))
                .subscribe(new ErrorHandleSubscriber<Integer>(mErrorHandler) {
                    @Override
                    public void onNext(Integer integer) {
                        if (integer == 2) {
                            mRootView.removeShortCollection(position);
                        }
                    }
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mErrorHandler = null;
        this.mAppManager = null;
        this.mImageLoader = null;
        this.mApplication = null;
    }
}
