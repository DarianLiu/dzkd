package com.dzkandian.mvp.news.presenter;

import android.app.Application;
import android.support.annotation.NonNull;

import com.dzkandian.app.http.utils.RxUtils;
import com.dzkandian.common.uitls.Constant;
import com.dzkandian.mvp.news.contract.NewBaseContract;
import com.dzkandian.storage.bean.news.NewsBean;
import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.integration.AppManager;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.utils.DataHelper;
import com.jess.arms.utils.DeviceUtils;
import com.jess.arms.utils.RxLifecycleUtils;
import com.trello.rxlifecycle2.android.FragmentEvent;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import me.jessyan.rxerrorhandler.core.RxErrorHandler;
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber;


@ActivityScope
public class NewBasePresenter extends BasePresenter<NewBaseContract.Model, NewBaseContract.View> {
    @Inject
    RxErrorHandler mErrorHandler;
    @Inject
    Application mApplication;
    @Inject
    ImageLoader mImageLoader;
    @Inject
    AppManager mAppManager;

    @Inject
    public NewBasePresenter(NewBaseContract.Model model, NewBaseContract.View rootView) {
        super(model, rootView);
    }


    private String beforeId = "";

    /**
     * 获取资讯列表
     *
     * @param isRefresh 是否
     * @param mType     资讯类型
     */
    public void getNewsList(boolean isRefresh, String mType) {
        if (isRefresh)
            beforeId = "";
        mModel.getNewsList(mType, 10, beforeId, DeviceUtils.getVersionName(mApplication),
                String.valueOf(DeviceUtils.getVersionCode(mApplication)),
                "Android",
                DataHelper.getStringSF(mApplication, Constant.PHONE_IMEI),
                String.valueOf(System.currentTimeMillis()))
                .subscribeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(() -> {
                    if (isRefresh) {
                        //结束刷新
                        mRootView.finishRefresh();
//                        EventBus.getDefault().post(new FloatingActionEvent.ShareBean().state(1).type(mType).build(),
//                                EventBusTags.TAG_REFRESH_STATE + Constant.TYPE_NEWS);
                    } else {
                        //结束加载更多
                        mRootView.finishLoadMore();
//                        Timber.d("==================presenter：结束加载更多");
//                        EventBus.getDefault().post(new FloatingActionEvent.ShareBean().state(3).type(mType).build(),
//                                EventBusTags.TAG_REFRESH_STATE + Constant.TYPE_NEWS);
                    }
                })
                .compose(RxLifecycleUtils.bindUntilEvent(mRootView, FragmentEvent.PAUSE))
                .compose(RxUtils.handleBaseResult(mAppManager.getTopActivity()))
                .subscribe(new ErrorHandleSubscriber<List<NewsBean>>(mErrorHandler) {
                    @Override
                    public void onNext(@NonNull List<NewsBean> data) {
                        if (data.size() != 0)
                            beforeId = data.get(data.size() - 1).getId();
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
//                            EventBus.getDefault().post(new FloatingActionEvent.ShareBean().state(5).build(), EventBusTags.TAG_REFRESH_STATE + Constant.TYPE_NEWS);
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
