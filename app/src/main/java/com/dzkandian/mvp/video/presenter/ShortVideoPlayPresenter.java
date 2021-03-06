package com.dzkandian.mvp.video.presenter;

import android.app.Application;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.dzkandian.app.config.EventBusTags;
import com.dzkandian.app.exception.ApiException;
import com.dzkandian.app.http.utils.DownLoadManager;
import com.dzkandian.app.http.utils.RequestParamUtils;
import com.dzkandian.app.http.utils.RxUtils;
import com.dzkandian.common.uitls.Constant;
import com.dzkandian.mvp.common.ui.activity.LoginActivity;
import com.dzkandian.mvp.video.contract.ShortVideoPlayContract;
import com.dzkandian.storage.bean.RandomAdBean;
import com.dzkandian.storage.bean.news.NewBarrageBean;
import com.dzkandian.storage.bean.news.NewsOrVideoShareBean;
import com.dzkandian.storage.bean.video.VideoBean;
import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.integration.AppManager;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.utils.DataHelper;
import com.jess.arms.utils.DeviceUtils;
import com.jess.arms.utils.PermissionUtil;
import com.jess.arms.utils.RxLifecycleUtils;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.trello.rxlifecycle2.android.ActivityEvent;

import org.simple.eventbus.EventBus;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import me.jessyan.rxerrorhandler.core.RxErrorHandler;
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber;
import okhttp3.RequestBody;


@ActivityScope
public class ShortVideoPlayPresenter extends BasePresenter<ShortVideoPlayContract.Model, ShortVideoPlayContract.View> {
    @Inject
    RxErrorHandler mErrorHandler;
    @Inject
    Application mApplication;
    @Inject
    ImageLoader mImageLoader;
    @Inject
    AppManager mAppManager;
    private boolean shortCollectionRefresh = false;

    @Inject
    public ShortVideoPlayPresenter(ShortVideoPlayContract.Model model, ShortVideoPlayContract.View rootView) {
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
    public void getVideoList(boolean isRefresh, String beforeId, String type) {
        mModel.getVideoList(type, 10, isRefresh ? "" : beforeId, DeviceUtils.getVersionName(mApplication),
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
                    }
                }).compose(RxLifecycleUtils.bindToLifecycle(mRootView))
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

    //    private Disposable disposable;
    private long progress = 0;//当前进度

    /**
     * 开始计时，计时进度
     */
    public void startTime() {
//        Timber.d("================startTime: " + progress);
        progress = progress == 100 ? 0 : progress;
//        Timber.d("================startTime onNext progress: " + progress);
        Observable.intervalRange(progress + 1, 100 - progress, 0, 333, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.newThread())
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxLifecycleUtils.bindUntilEvent(mRootView, ActivityEvent.PAUSE))
                .subscribe(new ErrorHandleSubscriber<Long>(mErrorHandler) {
                    @Override
                    public void onNext(Long aLong) {
                        progress = aLong;
//                        Timber.d("================startTime onNext: " + progress);
                        mRootView.videoRewardProgress(aLong);
                    }

                    @Override
                    public void onSubscribe(Disposable d) {
                        super.onSubscribe(d);
                        addDispose(d);
                    }
                });
//        addDispose(disposable);
    }

    /**
     * 停止计时
     */
    public void stopTime() {
        unDispose();
//        if (disposable != null) {
//            disposable.dispose();
//            disposable = null;
//        }
    }


    /**
     * 点赞评论接口
     *
     * @param commId
     * @param type
     */
    public void commentThumbsUp(String commId, String type) {
        RequestBody requestBody = RequestParamUtils.commentThumbsUp(mApplication, commId, type);
        if (TextUtils.isEmpty(getToken())) {
            mRootView.launchActivity(new Intent(mAppManager.getTopActivity(), LoginActivity.class));
        } else {
            mModel.commentThumbsUp(getToken(), requestBody)
                    .compose(RxUtils.applyhideSchedulers(mRootView))
                    .compose(RxUtils.handleBaseResult(mApplication))
                    .subscribe(new ErrorHandleSubscriber<Integer>(mErrorHandler) {
                        @Override
                        public void onNext(Integer count) {
                            mRootView.thumbsUpSuccess(count);
                        }

                        @Override
                        public void onError(Throwable t) {
                            super.onError(t);
                            if (t instanceof ApiException && ((ApiException) t).getCode() == 600) {
                                mRootView.thumbsUpError(false);
                            } else {
                                mRootView.thumbsUpError(true);
                            }
                        }
                    });
        }
    }


    /**
     * 视频获得收益
     *
     * @param value1 当前时间戳
     * @param value2 视频ID
     * @param value3 视频类型
     * @param aList  该金币领取时所浏览器过的文章记录列表(json字符串)
     */
    public void videoReward(String value1, String value2, String value3, String aList) {
        if (TextUtils.isEmpty(getToken())) {
            mRootView.showMessage("登录可领取奖励");
            mRootView.launchActivity(new Intent(mAppManager.getTopActivity(), LoginActivity.class));
        } else {
            RequestBody requestBody = RequestParamUtils.videoReward(mApplication, value1, value2, value3, aList);
            mModel.videoReward(getToken(), requestBody)
                    .subscribeOn(Schedulers.io())
                    .doOnSubscribe(disposable -> {
                        mRootView.showLoading();//显示进度条
//                                disposable.dispose();
                    })
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doFinally(() -> {
                        mRootView.hideLoading();//隐藏进度条
                    }).compose(RxLifecycleUtils.bindUntilEvent(mRootView, ActivityEvent.DESTROY))
                    .compose(RxUtils.handleBaseResult(mApplication))
                    .subscribe(new ErrorHandleSubscriber<Integer>(mErrorHandler) {
                        @Override
                        public void onNext(@NonNull Integer integer) {
                            mRootView.videoRewardSuccess(integer);
                        }

                        @Override
                        public void onError(Throwable t) {
                            super.onError(t);
                            mRootView.videoRewardFail();
                        }
                    });
        }
    }

    /**
     * 视频分享数据
     *
     * @param url 视频地址
     * @param ua  ua
     */
    public void videoShare(String url, String ua) {
        RequestBody requestBody = RequestParamUtils.newsShare(mApplication, url, ua);
        mModel.videoShare(getToken(), requestBody)
                .compose(RxUtils.applySchedulers(mRootView))
                .compose(RxUtils.handleBaseResult(mApplication))
                .subscribe(new ErrorHandleSubscriber<NewsOrVideoShareBean>(mErrorHandler) {
                    @Override
                    public void onNext(NewsOrVideoShareBean newsOrVideoShareBean) {
                        mRootView.setVideoShareContent(newsOrVideoShareBean);
                    }

                    @Override
                    public void onError(Throwable t) {
                        super.onError(t);
                    }
                });
    }

    public String getToken() {
        return DataHelper.getStringSF(mApplication, Constant.SP_KEY_TOKEN);
    }

    /**
     * 请求权限
     *
     * @param shareImageUrl 分享图片地址
     */
    public void requestPermission(String shareImageUrl) {
        PermissionUtil.externalStorage(new PermissionUtil.RequestPermission() {
            @Override
            public void onRequestPermissionSuccess() {
                downLoad(shareImageUrl);
            }

            @Override
            public void onRequestPermissionFailure(List<String> permissions) {
                mRootView.showMessage("权限获取失败");
            }

            @Override
            public void onRequestPermissionFailureWithAskNeverAgain(List<String> permissions) {
                mRootView.showMessage("权限获取失败，将无法正常使用该应用！");
            }

        }, new RxPermissions(mAppManager.getTopActivity()), mErrorHandler);
    }

    /**
     * 下载图片
     *
     * @param shareImageUrl 分享图片地址
     */
    private void downLoad(String shareImageUrl) {
        mModel.update(shareImageUrl).subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .concatMap(responseBody -> {
                    try {
                        String file_path = DownLoadManager.writeResponseBodyToDisk(responseBody);
                        return RxUtils.createData(file_path);
                    } catch (Exception e) {
                        return Observable.error(e);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxLifecycleUtils.bindToLifecycle(mRootView))
                .subscribe(new ErrorHandleSubscriber<String>(mErrorHandler) {
                    @Override
                    public void onNext(String s) {
                        mRootView.downloadCallBack(s);
                    }
                });
    }

    /**
     * 获取弹幕
     */
    public void getBarrage(String aId, String aType, String commitFrom, int size, String lastId) {
        RequestBody requestBody = RequestParamUtils.getBarrage(mApplication, aId, commitFrom, aType, size, lastId);
        mModel.getBarrage(getToken(), requestBody)
                .subscribeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxLifecycleUtils.bindToLifecycle(mRootView))
                .compose(RxUtils.handleBaseResult(mApplication))
                .subscribe(new ErrorHandleSubscriber<NewBarrageBean>(mErrorHandler) {
                    @Override
                    public void onNext(NewBarrageBean barrageBean) {
//                        Timber.d("===========弹幕获取成功     数量：" + barrageBean.getCmtCount());
                        mRootView.loadBarrage(barrageBean);
                    }

                    @Override
                    public void onError(Throwable t) {

                    }
                });
    }


    /**
     * 创建评论接口
     */
    public void foundComment(String content, String commitFrom, int readTime, String aId, String aType, String aUrl, String aTitle, String reqId, long parentId, long replyId, String replyName) {
        RequestBody requestBody = RequestParamUtils.foundComment(mApplication, content, commitFrom, readTime, aId, aType, aUrl, aTitle, reqId, parentId, replyId, replyName);
//        Timber.d("发送评论" + requestBody.toString());
        if (!TextUtils.isEmpty(getToken())) {
            mModel.foundComment(getToken(), requestBody)
                    .compose(RxUtils.applySchedulers(mRootView))
                    .compose(RxUtils.handleBaseResult(mApplication))
                    .subscribe(new ErrorHandleSubscriber<NewBarrageBean>(mErrorHandler) {
                        @Override
                        public void onNext(NewBarrageBean String) {
                            mRootView.commentSuccess();
//                            Timber.d("=============" + "评论成功");
                        }

                        @Override
                        public void onError(Throwable t) {
                            super.onError(t);
                        }
                    });
        }
    }

    /**
     * 视频添加收藏
     *
     * @param url
     */
    public void saveShortCollection(String url) {
        if (TextUtils.isEmpty(getToken())) {
            mRootView.showMessage("登录可收藏");
            mRootView.launchActivity(new Intent(mAppManager.getTopActivity(), LoginActivity.class));
        } else {
            RequestBody requestBody = RequestParamUtils.collectionVideo(mApplication, url);
            mModel.shortCollection(getToken(), requestBody)
                    .compose(RxUtils.applySchedulers(mRootView))
                    .compose(RxUtils.handleBaseResult(mApplication))
                    .subscribe(new ErrorHandleSubscriber<Integer>(mErrorHandler) {
                        @Override
                        public void onNext(Integer integer) {
                            shortCollectionRefresh = true;
                            if (integer == 1) {
                                mRootView.collectionValue(true);
                            } else if (integer == 2) {
                                mRootView.collectionValue(false);
                            }
                        }
                    });
        }
    }

    /**
     * 获取DSP广告配置参数；
     */
    public void getRandomAd() {
        mModel.getRandomAd(
                DeviceUtils.getVersionName(mApplication),
                DataHelper.getStringSF(mApplication, Constant.PHONE_IMEI),
                Constant.AD_SUPPORT_SDK_ALL,
                Constant.AD_SMALL_VIDEO_DETAIL_BOTTOM_BANNER)
                .compose(RxUtils.applyhideSchedulers(mRootView))
                .compose(RxUtils.handleBaseResult(mApplication))
                .subscribe(new ErrorHandleSubscriber<RandomAdBean>(mErrorHandler) {
                    @Override
                    public void onNext(RandomAdBean randomAdBean) {
                        if (randomAdBean != null)
                            mRootView.randomDSPAd(randomAdBean);
                    }
                });
    }

    @Override
    public void onDestroy() {
        if (shortCollectionRefresh) {
            EventBus.getDefault().post(true, EventBusTags.TAG_COLLECTION_SHORT_REFRESH);
        }
        super.onDestroy();
        this.mErrorHandler = null;
        this.mAppManager = null;
        this.mImageLoader = null;
        this.mApplication = null;
    }
}
