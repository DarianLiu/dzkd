package com.dzkandian.mvp.news.presenter;

import android.app.Application;
import android.content.Intent;
import android.text.TextUtils;

import com.dzkandian.app.config.EventBusTags;
import com.dzkandian.app.http.utils.RequestParamUtils;
import com.dzkandian.app.http.utils.RxUtils;
import com.dzkandian.common.uitls.Constant;
import com.dzkandian.common.uitls.ListCompareUtils;
import com.dzkandian.mvp.common.ui.activity.LoginActivity;
import com.dzkandian.mvp.news.contract.NewsContract;
import com.dzkandian.storage.ColumnBean;
import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.integration.AppManager;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.utils.DataHelper;
import com.jess.arms.utils.DeviceUtils;

import org.simple.eventbus.EventBus;

import java.util.List;

import javax.inject.Inject;

import me.jessyan.rxerrorhandler.core.RxErrorHandler;
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber;
import okhttp3.RequestBody;


@ActivityScope
public class NewsPresenter extends BasePresenter<NewsContract.Model, NewsContract.View> {
    @Inject
    RxErrorHandler mErrorHandler;
    @Inject
    Application mApplication;
    @Inject
    ImageLoader mImageLoader;
    @Inject
    AppManager mAppManager;

    @Inject
    public NewsPresenter(NewsContract.Model model, NewsContract.View rootView) {
        super(model, rootView);
    }

    /**
     * 获取资讯分类列表
     */
    public void getNewsTitleList(int touchHardware) {
        mModel.getNewsTitleList(DeviceUtils.getVersionName(mApplication),
                String.valueOf(DeviceUtils.getVersionCode(mApplication)),
                "Android",
                DataHelper.getStringSF(mApplication, Constant.PHONE_IMEI),
                String.valueOf(System.currentTimeMillis()))
                .compose(RxUtils.applySchedulers(mRootView))
                .compose(RxUtils.handleBaseResultSaveTimeStamp(mApplication))
                .subscribe(new ErrorHandleSubscriber<List<String>>(mErrorHandler) {
                               @Override
                               public void onNext(@android.support.annotation.NonNull List<String> data) {
                                   ColumnBean bean = DataHelper.getDeviceData(mApplication, Constant.SP_KEY_NEWS_COLUMN);
                                   if (bean != null) {
                                       ColumnBean columnBean = ListCompareUtils.getServerAddList(bean, data);
                                       DataHelper.saveDeviceData(mApplication, Constant.SP_KEY_NEWS_COLUMN, columnBean);
//                                           Timber.d("=================" + columnBean);
                                       List<String> allColumn = columnBean.getAllColumn();
                                       int viewSize = columnBean.getViewSize();
                                       if (allColumn.size() != 0 && viewSize != 0) {
                                           mRootView.updateView(allColumn, allColumn.subList(0, viewSize));
                                       } else {
                                           if (data.size() < 8) {
                                               mRootView.updateView(data, data);
                                           } else {
                                               mRootView.updateView(data, data.subList(0, data.size() - 5));
                                           }
                                       }
                                   } else {
                                       if (data.size() < 8) {
                                           mRootView.updateView(data, data);
                                       } else {
                                           mRootView.updateView(data, data.subList(0, data.size() - 5));
                                       }
                                   }
                               }

                               @Override
                               public void onError(Throwable t) {
                                   super.onError(t);
                                   mRootView.showErrorNetwork();
                               }
                           }
                );
    }

    /**
     * 时段获得收益
     *
     * @param value1 当前时间戳
     */
    public void timeReward(String value1) {
        String token = DataHelper.getStringSF(mApplication, Constant.SP_KEY_TOKEN);
        if (TextUtils.isEmpty(token)) {
            mRootView.launchActivity(new Intent(mAppManager.getTopActivity(), LoginActivity.class));
        } else {
            RequestBody requestBody = RequestParamUtils.timeReward(mApplication, value1);
            mModel.timeReward(token, requestBody)
                    .compose(RxUtils.applySchedulers(mRootView))
                    .compose(RxUtils.handleBaseResultSaveTimeStamp(mApplication))
                    .subscribe(new ErrorHandleSubscriber<Integer>(mErrorHandler) {
                        @Override
                        public void onNext(@android.support.annotation.NonNull Integer integer) {
                            mRootView.timeRewardInt(integer);
                        }

                        @Override
                        public void onError(Throwable t) {
//                            super.onError(t);
                            mRootView.timeRewardError();
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
