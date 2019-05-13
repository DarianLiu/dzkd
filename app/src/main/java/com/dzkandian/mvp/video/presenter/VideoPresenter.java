package com.dzkandian.mvp.video.presenter;

import android.app.Application;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.dzkandian.app.config.EventBusTags;
import com.dzkandian.app.http.utils.RequestParamUtils;
import com.dzkandian.app.http.utils.RxUtils;
import com.dzkandian.common.uitls.Constant;
import com.dzkandian.common.uitls.ListCompareUtils;
import com.dzkandian.mvp.common.ui.activity.LoginActivity;
import com.dzkandian.mvp.video.contract.VideoContract;
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
public class VideoPresenter extends BasePresenter<VideoContract.Model, VideoContract.View> {
    @Inject
    RxErrorHandler mErrorHandler;
    @Inject
    Application mApplication;
    @Inject
    ImageLoader mImageLoader;
    @Inject
    AppManager mAppManager;

    @Inject
    public VideoPresenter(VideoContract.Model model, VideoContract.View rootView) {
        super(model, rootView);
    }


    /**
     * 获取视频栏目列表
     */
    public void getVideoTitleList(int touchHardware) {
        mModel.getVideoTitleList(DeviceUtils.getVersionName(mApplication),
                String.valueOf(DeviceUtils.getVersionCode(mApplication)),
                "Android",
                DataHelper.getStringSF(mApplication, Constant.PHONE_IMEI),
                String.valueOf(System.currentTimeMillis()))
                .compose(RxUtils.applySchedulers(mRootView))
                .compose(RxUtils.handleBaseResultSaveTimeStamp(mApplication))
                .subscribe(new ErrorHandleSubscriber<List<String>>(mErrorHandler) {
                    @Override
                    public void onNext(@NonNull List<String> data) {
                        ColumnBean videoBean = DataHelper.getDeviceData(mApplication, Constant.SP_KEY_VIDEO_COLUMN);
                        if (videoBean != null) {
                            ColumnBean columnBean = ListCompareUtils.getServerAddList(videoBean, data);
                            DataHelper.saveDeviceData(mApplication, Constant.SP_KEY_VIDEO_COLUMN, columnBean);
//                            Timber.d("=================" + columnBean);
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
                        public void onNext(@NonNull Integer integer) {
                            mRootView.timeRewardInt(integer);
                        }

                        @Override
                        public void onError(Throwable t) {
                            mRootView.timeRewardError();
                        }
                    });
        }
    }
}
