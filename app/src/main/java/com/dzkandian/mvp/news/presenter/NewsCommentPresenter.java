package com.dzkandian.mvp.news.presenter;

import android.app.Application;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.dzkandian.app.exception.ApiException;
import com.dzkandian.app.http.utils.RequestParamUtils;
import com.dzkandian.app.http.utils.RxUtils;
import com.dzkandian.common.uitls.Constant;
import com.dzkandian.mvp.news.contract.NewsCommentContract;
import com.dzkandian.storage.bean.news.CommentRecordBean;
import com.dzkandian.storage.bean.news.NewBarrageBean;
import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.integration.AppManager;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.utils.DataHelper;
import com.jess.arms.utils.RxLifecycleUtils;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import me.jessyan.rxerrorhandler.core.RxErrorHandler;
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber;
import okhttp3.RequestBody;


@ActivityScope
public class NewsCommentPresenter extends BasePresenter<NewsCommentContract.Model, NewsCommentContract.View> {
    @Inject
    RxErrorHandler mErrorHandler;
    @Inject
    Application mApplication;
    @Inject
    ImageLoader mImageLoader;
    @Inject
    AppManager mAppManager;

    @Inject
    public NewsCommentPresenter(NewsCommentContract.Model model, NewsCommentContract.View rootView) {
        super(model, rootView);
    }

    private String getToken() {
        return DataHelper.getStringSF(mApplication, Constant.SP_KEY_TOKEN);
    }

    /**
     * 评论列表
     * isRefresh 是否刷新
     */
    public void newsCommentRecord(boolean isRefresh, String aId, String aType, String commitFrom, int size, String lastId) {
        RequestBody requestBody =
                RequestParamUtils.commentRecord(mApplication, aId, aType, commitFrom, size, lastId);
        mModel.commentRecord(getToken(), requestBody)
                .doFinally(() -> {
                    if (isRefresh) {
                        mRootView.finishRefresh();//隐藏刷新
                    } else {
                        mRootView.finishLoadMore();//隐藏加载更多
                    }
                })
                .compose(RxUtils.applyhideSchedulers(mRootView))
                .compose(RxUtils.handleBaseResult(mAppManager.getTopActivity()))
                .subscribe(new ErrorHandleSubscriber<CommentRecordBean>(mErrorHandler) {
                    @Override
                    public void onNext(@NonNull CommentRecordBean commentBeans) {
                        if (commentBeans.getCommentList().size() == 0) {
                            mRootView.refreshFailed(false);
                        }
                        if (isRefresh) {
                            mRootView.refreshData(commentBeans);
                        } else {
                            mRootView.loadMoreData(commentBeans);
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

    /*
     *评论点赞
     */
    public void commentThumbsUp(int position, Long commid, String type) {
        RequestBody requestBody = RequestParamUtils.commentThumbsUp(mApplication, commid, type);
        if (!TextUtils.isEmpty(getToken())) {
            mModel.commentThumbsUp(getToken(), requestBody)
                    .compose(RxUtils.applyhideSchedulers(mRootView))
                    .compose(RxUtils.handleBaseResult(mApplication))
                    .subscribe(new ErrorHandleSubscriber<Integer>(mErrorHandler) {
                        @Override
                        public void onNext(Integer integer) {
                            mRootView.receiveThumbsUp(integer, position);
                        }

                        @Override
                        public void onError(Throwable t) {
                            if (t instanceof ApiException && ((ApiException) t).getCode() == 600) {
                                //请求成功了，只是不可以点赞，满了
                                mRootView.receiveThumbsUp(-1, position);
                            }
                        }
                    });
        }
    }

    /**
     * 创建评论接口
     */
    public void foundComment(String content, String commitFrom, int readTime, String aId, String aType, String aUrl, String aTitle, String reqId, long parentId, long replyId, String replyName) {
        RequestBody requestBody = RequestParamUtils.foundComment(mApplication, content, commitFrom, readTime, aId, aType, aUrl, aTitle, reqId, parentId, replyId, replyName);
        if (!TextUtils.isEmpty(getToken())) {
            mModel.foundComment(getToken(), requestBody)
                    .compose(RxUtils.applySchedulers(mRootView))
                    .compose(RxUtils.handleBaseResult(mApplication))
                    .subscribe(new ErrorHandleSubscriber<NewBarrageBean>(mErrorHandler) {
                        @Override
                        public void onNext(NewBarrageBean String) {
//                            Timber.d("=============" + "评论成功");
                            mRootView.commentSuccess();
                        }

                        @Override
                        public void onError(Throwable t) {
                            mRootView.commitFail();
                        }
                    });
        }
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
