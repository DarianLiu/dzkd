package com.dzkandian.mvp.news.presenter;

import android.app.Application;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.dzkandian.app.exception.ApiException;
import com.dzkandian.app.http.utils.RequestParamUtils;
import com.dzkandian.app.http.utils.RxUtils;
import com.dzkandian.common.uitls.Constant;
import com.dzkandian.mvp.news.contract.ReplyDetailContract;
import com.dzkandian.storage.bean.news.ReplyBean;
import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.integration.AppManager;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.utils.DataHelper;
import com.jess.arms.utils.RxLifecycleUtils;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import me.jessyan.rxerrorhandler.core.RxErrorHandler;
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber;
import okhttp3.RequestBody;


@ActivityScope
public class ReplyDetailPresenter extends BasePresenter<ReplyDetailContract.Model, ReplyDetailContract.View> {
    @Inject
    RxErrorHandler mErrorHandler;
    @Inject
    Application mApplication;
    @Inject
    ImageLoader mImageLoader;
    @Inject
    AppManager mAppManager;

    @Inject
    public ReplyDetailPresenter(ReplyDetailContract.Model model, ReplyDetailContract.View rootView) {
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

    private String getToken() {
        return DataHelper.getStringSF(mApplication, Constant.SP_KEY_TOKEN);
    }

    /**
     * 回复列表
     * isRefresh 是否刷新
     */
    public void getReplyList(boolean refresh, String commentId, String commitFrom, String lastId) {
        RequestBody requestBody =
                RequestParamUtils.getReplyList(mApplication, commentId, String.valueOf(refresh), commitFrom, "10", lastId);
        mModel.getReplyList(getToken(), requestBody)
                .subscribeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(() -> {
                    if (refresh) {
                        mRootView.finishRefresh();//隐藏刷新
                    } else {
                        mRootView.finishLoadMore();//隐藏加载更多
                    }
                })
                .compose(RxLifecycleUtils.bindToLifecycle(mRootView))
                .compose(RxUtils.handleBaseResult(mAppManager.getTopActivity()))
                .subscribe(new ErrorHandleSubscriber<List<ReplyBean>>(mErrorHandler) {
                    @Override
                    public void onNext(@NonNull List<ReplyBean> list) {
                        if (list.size() == 0) {
                            mRootView.refreshFailed(false);
                        }
                        if (refresh) {
                            mRootView.refreshData(list);
                        } else {
                            mRootView.loadMoreData(list);
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        super.onError(t);
                        if (refresh)
                            mRootView.refreshFailed(true);
                    }
                });
    }

    /**
     * 创建回复接口
     */
    public void foundReply(String inputString, String content, String commitFrom, String readTime, String aId, String aType, String aUrl, String aTitle, String reqId, String parentId, String replyId, String replyName) {
        RequestBody requestBody = RequestParamUtils.foundReply(mApplication, content, commitFrom, readTime, aId, aType, aUrl, aTitle, reqId, parentId, replyId, replyName);
        if (!TextUtils.isEmpty(getToken())) {
            mModel.foundReply(getToken(), requestBody)
                    .compose(RxUtils.applySchedulers(mRootView))
                    .compose(RxUtils.handleBaseResult(mApplication))
                    .subscribe(new ErrorHandleSubscriber<Object>(mErrorHandler) {
                        @Override
                        public void onNext(Object object) {
                            mRootView.replySuccess(inputString, content, parentId, replyId, replyName);
                        }

                        @Override
                        public void onError(Throwable t) {
                            super.onError(t);
                        }
                    });
        }
    }

    /**
     * 创建回复界面的点赞接口
     */
    public void foundPraise(int position, String commId, String type) {
        RequestBody requestBody = RequestParamUtils.foundPraise(mApplication, commId, type);
        if (!TextUtils.isEmpty(getToken())) {
            mModel.foundPraise(getToken(), requestBody)
                    .compose(RxUtils.applyhideSchedulers(mRootView))
                    .compose(RxUtils.handleBaseResult(mApplication))
                    .subscribe(new ErrorHandleSubscriber<Integer>(mErrorHandler) {
                        @Override
                        public void onNext(Integer integer) {
                            //请求成功了，integer为剩余点赞数
                            mRootView.praiseResult(true, position, integer);
                        }

                        @Override
                        public void onError(Throwable t) {
                            super.onError(t);
                            if (t instanceof ApiException && ((ApiException) t).getCode() == 600) {
                                //请求成功了，只是不可以点赞，满了
                                mRootView.praiseResult(true, position, -1);
                            } else {
                                //由于一些原因  请求失败了
                                mRootView.praiseResult(false, position, -1);
                            }
                        }
                    });
        }
    }
}
