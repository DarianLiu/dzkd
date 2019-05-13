package com.dzkandian.mvp.mine.presenter;

import android.app.Application;
import android.support.annotation.NonNull;

import com.dzkandian.app.http.utils.RequestParamUtils;
import com.dzkandian.app.http.utils.RxUtils;
import com.dzkandian.common.uitls.Constant;
import com.dzkandian.storage.bean.mine.MyOrderBean;
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

import com.dzkandian.mvp.mine.contract.MineOrderContract;
import com.jess.arms.utils.DataHelper;
import com.jess.arms.utils.RxLifecycleUtils;

import java.util.List;


@ActivityScope
public class MineOrderPresenter extends BasePresenter<MineOrderContract.Model, MineOrderContract.View> {
    @Inject
    RxErrorHandler mErrorHandler;
    @Inject
    Application mApplication;
    @Inject
    ImageLoader mImageLoader;
    @Inject
    AppManager mAppManager;

    @Inject
    public MineOrderPresenter(MineOrderContract.Model model, MineOrderContract.View rootView) {
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

    private int page = 1;

    /**
     * 请求所以订单
     *
     * @param isRefresh
     */
    public void questAllOrder(boolean isRefresh) {
        if (isRefresh) {
            page = 1;
        }
        RequestBody requestBody = RequestParamUtils.buildMyOrderType(mApplication, String.valueOf(page), String.valueOf(20), "");
        String token = DataHelper.getStringSF(mApplication, Constant.SP_KEY_TOKEN);
        mModel.questAllOrderList(token, requestBody)
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
                .subscribe(new ErrorHandleSubscriber<List<MyOrderBean>>(mErrorHandler) {
                    @Override
                    public void onNext(@NonNull List<MyOrderBean> myOrderBeans) {

                        if (myOrderBeans.size() != 0) {
                            page += 1;
                        } else {
                            mRootView.refreshFailed(false);
                        }
                        if (isRefresh) {
                            mRootView.refreshData(myOrderBeans);
                        } else {
                            mRootView.loadMoreData(myOrderBeans);
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
}
