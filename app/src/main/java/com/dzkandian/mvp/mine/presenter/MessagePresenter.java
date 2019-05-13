package com.dzkandian.mvp.mine.presenter;

import android.app.Application;

import com.dzkandian.app.config.EventBusTags;
import com.dzkandian.app.http.utils.RequestParamUtils;
import com.dzkandian.app.http.utils.RxUtils;
import com.dzkandian.common.uitls.Constant;
import com.dzkandian.storage.bean.mine.MessageBean;
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

import com.dzkandian.mvp.mine.contract.MessageContract;
import com.jess.arms.utils.DataHelper;
import com.jess.arms.utils.RxLifecycleUtils;

import org.simple.eventbus.EventBus;

import java.util.List;


@ActivityScope
public class MessagePresenter extends BasePresenter<MessageContract.Model, MessageContract.View> {
    @Inject
    RxErrorHandler mErrorHandler;
    @Inject
    Application mApplication;
    @Inject
    ImageLoader mImageLoader;
    @Inject
    AppManager mAppManager;

    @Inject
    public MessagePresenter(MessageContract.Model model, MessageContract.View rootView) {
        super(model, rootView);
    }


    private int page = 1;

    /**
     * 用户反馈回复列表
     *
     * @param isRefresh
     */
    public void replyList(boolean isRefresh) {
        if (isRefresh)
            page = 1;
        RequestBody body = RequestParamUtils.buildQuestionType(mApplication, String.valueOf(page), String.valueOf(10));
        String token = DataHelper.getStringSF(mApplication, Constant.SP_KEY_TOKEN);
        mModel.replyPraiseList(token, body)
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
                .subscribe(new ErrorHandleSubscriber<List<MessageBean>>(mErrorHandler) {
                    @Override
                    public void onNext(List<MessageBean> messageBeans) {
                        if (messageBeans.size() != 0) {
                            page += 1;
                        } else {
                            mRootView.refreshFailed(false);
                        }
                        if (isRefresh) {
                            mRootView.refreshData(messageBeans);
                        } else {
                            mRootView.loadMoreData(messageBeans);
                        }

                        EventBus.getDefault().post(true, EventBusTags.TAG_READ_MESSAGE);
                    }

                    @Override
                    public void onError(Throwable t) {
                        super.onError(t);
                        if (isRefresh)
                            mRootView.refreshFailed(true);
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
