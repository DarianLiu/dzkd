package com.dzkandian.mvp.mine.presenter;

import android.app.Application;
import android.support.annotation.NonNull;

import com.dzkandian.app.http.utils.RequestParamUtils;
import com.dzkandian.app.http.utils.RxUtils;
import com.dzkandian.common.uitls.Constant;
import com.dzkandian.mvp.mine.contract.ProfitDetailContract;
import com.dzkandian.storage.bean.mine.TaskRecordBean;
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

/**
 * 收益明细
 */
@ActivityScope
public class ProfitDetailPresenter extends BasePresenter<ProfitDetailContract.Model, ProfitDetailContract.View> {
    @Inject
    RxErrorHandler mErrorHandler;
    @Inject
    Application mApplication;
    @Inject
    ImageLoader mImageLoader;
    @Inject
    AppManager mAppManager;

    @Inject
    public ProfitDetailPresenter(@NonNull ProfitDetailContract.Model model, @NonNull ProfitDetailContract.View rootView) {
        super(model, rootView);

    }

    private String getToken() {
        return DataHelper.getStringSF(mApplication, Constant.SP_KEY_TOKEN);
    }

    private int page = 1;

    /**
     * 收益明显
     *
     * @param isRefresh 是否刷新
     */
    public void questTaskRecord(boolean isRefresh) {
        if (isRefresh)
            page = 1;
        RequestBody requestBody =
                RequestParamUtils.buildQuestionType(mApplication, String.valueOf(page), "15");
        mModel.getTaskRecord(getToken(), requestBody)
                .subscribeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(() -> {
                    if (isRefresh) {
                        mRootView.finishRefresh();//隐藏刷新
                    } else {
                        mRootView.finishLoadMore();//隐藏加载更多
                    }
                })
                .compose(RxLifecycleUtils.bindToLifecycle(mRootView))
                .compose(RxUtils.handleBaseResult(mAppManager.getTopActivity()))
                .subscribe(new ErrorHandleSubscriber<List<TaskRecordBean>>(mErrorHandler) {
                    @Override
                    public void onNext(@NonNull List<TaskRecordBean> questTaskRecord) {
                        if (questTaskRecord.size() != 0) {
                            page += 1;
                        } else {
                            mRootView.refreshFailed(false);
                        }
                        if (isRefresh) {
                            mRootView.refreshData(questTaskRecord);
                        } else {
                            mRootView.loadMoreData(questTaskRecord);
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


    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mErrorHandler = null;
        this.mAppManager = null;
        this.mImageLoader = null;
        this.mApplication = null;
    }
}
