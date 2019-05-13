package com.dzkandian.mvp.common.presenter;

import android.app.Application;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.dzkandian.app.config.EventBusTags;
import com.dzkandian.app.http.utils.RequestParamUtils;
import com.dzkandian.app.http.utils.RxUtils;
import com.dzkandian.common.uitls.Constant;
import com.dzkandian.common.uitls.update.UpdateAppBean;
import com.dzkandian.common.uitls.update.UpdateAppHttpUtil;
import com.dzkandian.mvp.common.contract.MainContract;
import com.dzkandian.storage.bean.VersionBean;
import com.dzkandian.storage.event.TimeRewardEvent;
import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.integration.AppManager;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.utils.DataHelper;
import com.jess.arms.utils.DeviceUtils;
import com.jess.arms.utils.PermissionUtil;
import com.jess.arms.utils.RxLifecycleUtils;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.trello.rxlifecycle2.android.ActivityEvent;

import org.simple.eventbus.EventBus;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Observable;
import me.jessyan.rxerrorhandler.core.RxErrorHandler;
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber;
import okhttp3.RequestBody;


@ActivityScope
public class MainPresenter extends BasePresenter<MainContract.Model, MainContract.View> {
    @Inject
    RxErrorHandler mErrorHandler;
    @Inject
    Application mApplication;
    @Inject
    ImageLoader mImageLoader;
    @Inject
    AppManager mAppManager;

    @Inject
    public MainPresenter(@NonNull MainContract.Model model, @NonNull MainContract.View rootView) {
        super(model, rootView);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    /**
     * 检查更新
     *
     * @param channel 手机相关信息
     */
    public void checkUpdate(String channel) {
        String token = DataHelper.getStringSF(mApplication, Constant.SP_KEY_TOKEN);
        RequestBody requestBody = RequestParamUtils.buildCheckUpdate(mApplication, channel);
        mModel.checkUpdate(TextUtils.isEmpty(token) ? "" : token, requestBody).compose(RxUtils.applySchedulers(mRootView))
                .compose(RxUtils.handleBaseResult(mApplication))
                .subscribe(new ErrorHandleSubscriber<VersionBean>(mErrorHandler) {
                    @Override
                    public void onNext(@NonNull VersionBean versionBean) {
                        if (DeviceUtils.getVersionCode(mApplication) < versionBean.getVersionCode()) {
                            //当前版本号小于服务器上APK上的版本号，表示需要更新
                            DataHelper.setStringSF(mApplication, Constant.SP_KEY_UPDATE_APP, "ture");
                            requestPermissionUpdate(versionBean);
                        } else {
                            DataHelper.setStringSF(mApplication, Constant.SP_KEY_UPDATE_APP, "false");
                        }

                    }
                });
    }

    /**
     * 更新版本：申请外部存储权限
     */
    public void requestPermissionUpdate(VersionBean versionBean) {
        PermissionUtil.externalStorage(new PermissionUtil.RequestPermission() {
            @Override
            public void onRequestPermissionSuccess() {
                updateAppDialog(versionBean);
            }

            @Override
            public void onRequestPermissionFailure(List<String> permissions) {
                mRootView.showMessage("授权失败，请允许存储权限后再尝试更新");
                updateAppDialog(versionBean);
            }

            @Override
            public void onRequestPermissionFailureWithAskNeverAgain(List<String> permissions) {
                mRootView.showMessage("授权失败，请到设置中允许存储权限后再尝试更新");
                updateAppDialog(versionBean);
            }

        }, new RxPermissions(mAppManager.getTopActivity()), mErrorHandler);
    }

    /**
     * @param versionBean
     */
    public void updateAppDialog(VersionBean versionBean) {
        String path = Environment.getExternalStorageDirectory() + File.separator;
        UpdateAppBean updateAppBean = new UpdateAppBean();
        updateAppBean
                .setUpdate("Yes")//是否更新，Yes,No
                .setNewVersion(versionBean.getVersion())//新版本号，
                .setApkFileUrl(versionBean.getApkUrl())//apk下载地址
                .setUpdateDefDialogTitle("发现新版本")//更新头部
                .setUpdateLog(versionBean.getDescribe())//更新内容
                .setConstraint(versionBean.getForce() == 1)//是否强制更新，可以不设置
                .setTargetPath(path);//设置apk下载路径
        updateAppBean.setHttpManager(new UpdateAppHttpUtil());
        mRootView.showUpdateDialog(updateAppBean);
    }


    public long duration = 0;
    public boolean isStart = false;

    /**
     * @return 获取当前有没有时段奖励倒计时在跑；
     */
    public boolean getTimeStart(){
        return isStart;
    }

    /**
     * 开始计时
     *
     * @param timeDifference 时差（毫秒）
     */
    public void timeStart(long timeDifference) {
        if (!isStart) {
            isStart = true;
            duration = timeDifference;
            countdown(duration);
        }
//        Timber.d("============TimeReward - timeStart: " + duration);
    }

    /**
     * 重新开始计时（注意之前有过暂停）
     *
     * @param pauseTime 暂停时长
     */
    public void timeRestart(long pauseTime) {
        if (!isStart) {
            isStart = true;
            duration = duration - pauseTime;
            if (duration > 0) {
                countdown(duration);
            } else {
                EventBus.getDefault().post(new TimeRewardEvent.Builder()
                        .isTimeEnd(true)
                        .build(), EventBusTags.TAG_TIME_REWARD_TIME_SHOW);
            }
        }
//        Timber.d("============TimeReward - timeRestart" + duration);
    }

    /**
     * 倒计时任务
     *
     * @param timeDifference 时差（毫秒）
     */
    private void countdown(long timeDifference) {
//        Timber.d("============TimeReward - countdown");
        Observable.interval(1, TimeUnit.MILLISECONDS)
                .take(timeDifference)
                .map(aLong -> {
                    duration = timeDifference - aLong - 1;
                    return duration;
                })
                .filter(aLong -> aLong % 1000 == 0)
                .compose(RxLifecycleUtils.bindUntilEvent(mRootView, ActivityEvent.PAUSE))
                .subscribe(new ErrorHandleSubscriber<Long>(mErrorHandler) {
                    @Override
                    public void onNext(Long aLong) {
//                        Timber.d("============TimeReward - onNext：" + aLong);
                        if (mRootView != null)
                            mRootView.timeRewardCountdown(aLong);
                    }

                    @Override
                    public void onComplete() {
//                        Timber.d("============TimeReward - onComplete：");
                        isStart = false;
                        super.onComplete();
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
