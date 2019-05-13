package com.dzkandian.mvp.mine.presenter;

import android.app.Application;
import android.support.annotation.NonNull;

import com.dzkandian.app.http.utils.RequestParamUtils;
import com.dzkandian.app.http.utils.RxUtils;
import com.dzkandian.common.uitls.Constant;
import com.dzkandian.mvp.mine.contract.QuestionAllContract;
import com.dzkandian.storage.bean.mine.QuestionAllBean;
import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.integration.AppManager;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.utils.DataHelper;
import com.jess.arms.utils.RxLifecycleUtils;

import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import me.jessyan.rxerrorhandler.core.RxErrorHandler;
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber;
import okhttp3.RequestBody;


@ActivityScope
public class QuestionAllPresenter extends BasePresenter<QuestionAllContract.Model, QuestionAllContract.View> {
    @Inject
    RxErrorHandler mErrorHandler;
    @Inject
    Application mApplication;
    @Inject
    ImageLoader mImageLoader;
    @Inject
    AppManager mAppManager;

    @Inject
    public QuestionAllPresenter(@NonNull QuestionAllContract.Model model, @NonNull QuestionAllContract.View rootView) {
        super(model, rootView);
    }

    /**
     * 一次获取所有常见问题
     */
    public void questionAll() {
        RequestBody requestBody = RequestParamUtils.buildRequestBody(mApplication, new HashMap<>());
        String token = DataHelper.getStringSF(mApplication, Constant.SP_KEY_TOKEN);
        mModel.questionAll(token, requestBody)
                .subscribeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(() -> {
                    mRootView.finishRefresh();//隐藏刷新
                }).compose(RxLifecycleUtils.bindToLifecycle(mRootView))
                .compose(RxUtils.handleBaseResult(mAppManager.getTopActivity()))
                .subscribe(new ErrorHandleSubscriber<List<QuestionAllBean>>(mErrorHandler) {
                    @Override
                    public void onNext(List<QuestionAllBean> questionList) {
                        mRootView.updateListView(questionList);
                    }

                    @Override
                    public void onError(Throwable t) {
                        super.onError(t);
                        mRootView.refreshFailed();
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
