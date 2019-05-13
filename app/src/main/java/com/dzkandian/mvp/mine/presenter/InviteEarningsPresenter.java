package com.dzkandian.mvp.mine.presenter;

import android.app.Application;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.dzkandian.app.http.utils.RequestParamUtils;
import com.dzkandian.app.http.utils.RxUtils;
import com.dzkandian.common.uitls.Constant;
import com.dzkandian.mvp.mine.contract.InviteEarningsContract;
import com.dzkandian.storage.bean.mine.InviteProfitBean;
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
public class InviteEarningsPresenter extends BasePresenter<InviteEarningsContract.Model, InviteEarningsContract.View> {
    @Inject
    RxErrorHandler mErrorHandler;
    @Inject
    Application mApplication;
    @Inject
    ImageLoader mImageLoader;
    @Inject
    AppManager mAppManager;

    @Inject
    public InviteEarningsPresenter(@NonNull InviteEarningsContract.Model model, @NonNull InviteEarningsContract.View rootView) {
        super(model, rootView);
    }

    private String getToken() {
        return DataHelper.getStringSF(mApplication, Constant.SP_KEY_TOKEN);
    }

    private int page;

    /**
     * 徒弟总收益列表
     * //@param page    	int	页码
     * //@param limit   	int	数量
     *
     * @param isRefresh 是否刷新
     */
    public void apprenticesList(boolean isRefresh) {
        if (isRefresh)
            page = 1;
        RequestBody requestBody = RequestParamUtils.buildQuestionType(mApplication,
                String.valueOf(page), String.valueOf(20));
        mModel.inviteProfitList(getToken(), requestBody)
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
                .subscribe(new ErrorHandleSubscriber<List<InviteProfitBean>>(mErrorHandler) {
                    @Override
                    public void onNext(@Nullable List<InviteProfitBean> beanList) {
                        if (beanList == null || beanList.size() == 0) {
                            mRootView.refreshFailed(false);
                        } else {
                            page += 1;
                            if (isRefresh) {
                                mRootView.refreshData(beanList);
                            } else {
                                mRootView.loadMoreData(beanList);
                            }
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
