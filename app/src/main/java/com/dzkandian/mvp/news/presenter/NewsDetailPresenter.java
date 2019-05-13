package com.dzkandian.mvp.news.presenter;

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
import com.dzkandian.mvp.common.ui.activity.AgreementWebActivity;
import com.dzkandian.mvp.common.ui.activity.LoginActivity;
import com.dzkandian.mvp.news.contract.NewsDetailContract;
import com.dzkandian.storage.bean.news.NewBarrageBean;
import com.dzkandian.storage.bean.news.NewsOrVideoShareBean;
import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.integration.AppManager;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.utils.DataHelper;
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
import io.reactivex.schedulers.Schedulers;
import me.jessyan.rxerrorhandler.core.RxErrorHandler;
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber;
import okhttp3.RequestBody;


@ActivityScope
public class NewsDetailPresenter extends BasePresenter<NewsDetailContract.Model, NewsDetailContract.View> {
    @Inject
    RxErrorHandler mErrorHandler;
    @Inject
    Application mApplication;
    @Inject
    ImageLoader mImageLoader;
    @Inject
    AppManager mAppManager;
    private boolean newsCollectionRefresh = false;

    @Inject
    public NewsDetailPresenter(@NonNull NewsDetailContract.Model model, @NonNull NewsDetailContract.View rootView) {
        super(model, rootView);
    }

    @Override
    public void onDestroy() {
        if (newsCollectionRefresh) {
            EventBus.getDefault().post(true, EventBusTags.TAG_COLLECTION_NEWS_REFRESH);
        }
        super.onDestroy();
        this.mErrorHandler = null;
        this.mAppManager = null;
        this.mImageLoader = null;
        this.mApplication = null;
    }


    public String getToken() {
        return DataHelper.getStringSF(mApplication, Constant.SP_KEY_TOKEN);
    }

    /**
     * 计时任务
     */
    public void timing() {
        Observable.intervalRange(0, 20, 0, 333, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxLifecycleUtils.bindUntilEvent(mRootView, ActivityEvent.PAUSE))
                .subscribe(new ErrorHandleSubscriber<Long>(mErrorHandler) {
                    @Override
                    public void onNext(Long aLong) {
                        if (mRootView != null)
                            mRootView.updateArcProgress(false);
                    }

                    @Override
                    public void onComplete() {
                        if (mRootView != null)
                            mRootView.updateArcProgress(true);
                    }
                });
    }

    /**
     * 阅读获得收益
     *
     * @param value1    当前时间戳
     * @param value2    文章ID
     * @param value3    文章类型
     * @param record    违规记录	   1、表示异常 0、表示正常
     * @param model     手机型号
     * @param debug     调试	       1、表示开启 0、表示未开启
     * @param debugInfo 调试信息
     * @param progress  进程	       1、表示异常 0、表示正常
     * @param pgk       手机运行进程的包名
     * @param aList     该金币领取时所浏览器过的文章记录列表(json字符串)
     */
    public void readingReward(String value1, String value2, String value3, int record, String model,
                              int debug, String debugInfo, int progress, String pgk, String aList) {
//        Timber.d("========作弊：" + record);
        if (TextUtils.isEmpty(getToken())) {
            mRootView.showMessage("登录可领取奖励");
            mRootView.launchActivity(new Intent(mAppManager.getTopActivity(), LoginActivity.class));
        } else {
            RequestBody requestBody = RequestParamUtils.readingReward(mApplication, value1, value2,
                    value3, record, model, debug, debugInfo, progress, pgk, aList);
            mModel.readingReward(getToken(), requestBody)
                    .compose(RxUtils.applySchedulers(mRootView))
                    .compose(RxUtils.handleBaseResult(mApplication))
                    .subscribe(new ErrorHandleSubscriber<Integer>(mErrorHandler) {
                        @Override
                        public void onNext(@NonNull Integer integer) {
                            if (mRootView != null)
                                mRootView.readingRewardInt(integer);
                        }
                    });
        }
    }

    public void newsShare(String url, String ua) {
        RequestBody requestBody = RequestParamUtils.newsShare(mApplication, url, ua);
        if (TextUtils.isEmpty(getToken())) {
            mRootView.upView();
        } else {
            mModel.newsShare(getToken(), requestBody)
                    .compose(RxUtils.applySchedulers(mRootView))
                    .compose(RxUtils.handleBaseResult(mApplication))
                    .subscribe(new ErrorHandleSubscriber<NewsOrVideoShareBean>(mErrorHandler) {
                        @Override
                        public void onNext(NewsOrVideoShareBean newsOrVideoShareBean) {
                            if (mRootView != null)
                                mRootView.newsShare(newsOrVideoShareBean);
                        }
                    });
        }
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
                            }else {
                                mRootView.thumbsUpError(true);
                            }
                        }
                    });
        }
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
                        if (mRootView != null)
                            mRootView.downloadCallBack(s);
                    }
                });
//        mModel.update(shareImageUrl).subscribeOn(Schedulers.io())
//                .observeOn(Schedulers.io())
//                .map(body -> {
//                    try {
//                        return DownLoadManager.writeImageToDisk(mApplication, "shareImageUser", body);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        return "";
//                    }
//                })
//                .observeOn(AndroidSchedulers.mainThread())
//                .compose(RxLifecycleUtils.bindToLifecycle(mRootView))
//                .subscribe(filePath -> mRootView.downloadCallBack(filePath));
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
                        if (mRootView != null)
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
    public void foundComment(String content, String commitFrom, int readTime, String aId, String aType, String aUrl, String aTitle, String reqId,long parentId,long replyId,String replyName) {
        RequestBody requestBody = RequestParamUtils.foundComment(mApplication, content, commitFrom, readTime, aId, aType, aUrl, aTitle, reqId,parentId,replyId,replyName);
//        Timber.d("发送评论" + requestBody.toString());
        if (!TextUtils.isEmpty(getToken())) {
            mModel.foundComment(getToken(), requestBody)
                    .compose(RxUtils.applySchedulers(mRootView))
                    .compose(RxUtils.handleBaseResult(mApplication))
                    .subscribe(new ErrorHandleSubscriber<NewBarrageBean>(mErrorHandler) {
                        @Override
                        public void onNext(NewBarrageBean String) {
                            if (mRootView != null)
                                mRootView.commentSuccess();
//                            Timber.d("=============" + "评论成功");
                        }

                    });
        }
    }

    /**
     * 新闻添加收藏
     *
     * @param url
     */
    public void saveNewsCollection(String url) {
        if (TextUtils.isEmpty(getToken())) {
            mRootView.showMessage("登录可收藏");
            mRootView.launchActivity(new Intent(mAppManager.getTopActivity(), LoginActivity.class));
        } else {
            RequestBody requestBody = RequestParamUtils.collectionNews(mApplication, url);
            mModel.newsCollection(getToken(), requestBody)
                    .compose(RxUtils.applySchedulers(mRootView))
                    .compose(RxUtils.handleBaseResult(mApplication))
                    .subscribe(new ErrorHandleSubscriber<Integer>(mErrorHandler) {
                        @Override
                        public void onNext(Integer integer) {
                            newsCollectionRefresh = true;
                            if (mRootView != null) {
                                if (integer == 1) {
                                    mRootView.collectionValue(true);
                                } else if (integer == 2) {
                                    mRootView.collectionValue(false);
                                }
                            }
                        }
                    });
        }
    }

//    public void x5Web(String id, String time) {
//        RequestBody requestBody = RequestParamUtils.x5Web(mApplication, id,time);
//        mModel.x5web(getToken(), requestBody)
//                .compose(RxUtils.applySchedulers(mRootView))
//                .compose(RxUtils.handleBaseResult(mApplication))
//                .subscribeWith(new ErrorHandleSubscriber<Integer>(mErrorHandler) {
//                    @Override
//                    public void onNext(@NonNull Integer integer) {
//
//                    }
//                });
//    }

}
