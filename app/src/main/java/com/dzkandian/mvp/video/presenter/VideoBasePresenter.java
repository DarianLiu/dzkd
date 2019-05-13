package com.dzkandian.mvp.video.presenter;

import android.app.Application;
import android.support.annotation.NonNull;

import com.dzkandian.app.http.utils.RxUtils;
import com.dzkandian.common.uitls.Constant;
import com.dzkandian.mvp.video.contract.VideoBaseContract;
import com.dzkandian.storage.bean.video.VideoBean;
import com.jess.arms.integration.AppManager;
import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.http.imageloader.ImageLoader;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import me.jessyan.rxerrorhandler.core.RxErrorHandler;
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber;
import timber.log.Timber;

import javax.inject.Inject;

import com.jess.arms.utils.DataHelper;
import com.jess.arms.utils.DeviceUtils;
import com.jess.arms.utils.RxLifecycleUtils;
import com.trello.rxlifecycle2.android.FragmentEvent;

import java.util.List;


@ActivityScope
public class VideoBasePresenter extends BasePresenter<VideoBaseContract.Model, VideoBaseContract.View> {
    @Inject
    RxErrorHandler mErrorHandler;
    @Inject
    Application mApplication;
    @Inject
    ImageLoader mImageLoader;
    @Inject
    AppManager mAppManager;

    @Inject
    public VideoBasePresenter(VideoBaseContract.Model model, VideoBaseContract.View rootView) {
        super(model, rootView);
    }


    /**
     * 获取视频列表
     *
     * @param isRefresh 是否刷新（刷新：true；加载更多：false）
     * @param type      类型
     *                  //@param num       每页数量
     *                  //@param beforeId  最后一项ID
     */
    public void getVideoList(boolean isRefresh, String type, String beforeId) {
        if (isRefresh)
            beforeId = "";
        mModel.getVideoList(type, 10, beforeId, DeviceUtils.getVersionName(mApplication),
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
                    } else {
                        //结束加载更多
                        mRootView.finishLoadMore();
//                        Timber.d("==================presenter：结束加载更多");
                    }
                }).compose(RxLifecycleUtils.bindUntilEvent(mRootView, FragmentEvent.PAUSE))
                .compose(RxUtils.handleBaseResult(mAppManager.getTopActivity()))
                .subscribe(new ErrorHandleSubscriber<List<VideoBean>>(mErrorHandler) {
                    @Override
                    public void onNext(@NonNull List<VideoBean> videoBeans) {
                        if (isRefresh) {
                            mRootView.refreshData(videoBeans);
                        } else {
                            mRootView.loadMoreData(videoBeans);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mErrorHandler = null;
        this.mAppManager = null;
        this.mImageLoader = null;
        this.mApplication = null;
    }
}
