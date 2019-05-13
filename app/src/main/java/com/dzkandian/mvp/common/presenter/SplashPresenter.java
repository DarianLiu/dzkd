package com.dzkandian.mvp.common.presenter;

import android.app.Application;

import com.dzkandian.app.MyApplication;
import com.dzkandian.app.config.EventBusTags;
import com.dzkandian.app.http.utils.RequestParamUtils;
import com.dzkandian.app.http.utils.RxUtils;
import com.dzkandian.common.uitls.Constant;
import com.dzkandian.db.DeviceInfoBeanDao;
import com.dzkandian.mvp.common.contract.SplashContract;
import com.dzkandian.storage.bean.DeviceInfoBean;
import com.dzkandian.storage.event.PushEvent;
import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.integration.AppManager;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.utils.DataHelper;
import com.jess.arms.utils.PermissionUtil;
import com.jess.arms.utils.RxLifecycleUtils;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.simple.eventbus.EventBus;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import me.jessyan.rxerrorhandler.core.RxErrorHandler;
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber;
import okhttp3.RequestBody;
import timber.log.Timber;


@ActivityScope
public class SplashPresenter extends BasePresenter<SplashContract.Model, SplashContract.View> {
    @Inject
    RxErrorHandler mErrorHandler;
    @Inject
    Application mApplication;
    @Inject
    ImageLoader mImageLoader;
    @Inject
    AppManager mAppManager;

    @Inject
    public SplashPresenter(@android.support.annotation.NonNull SplashContract.Model model, @android.support.annotation.NonNull SplashContract.View rootView) {
        super(model, rootView);
    }

    /**
     * 请求权限 获取信息
     *
     * @return
     */
    public void requestPermission(List<String> permissionList) {

        PermissionUtil.requestPermission(new PermissionUtil.RequestPermission() {
            @Override
            public void onRequestPermissionSuccess() {
                mRootView.adsNativeSplash();
                mRootView.getPhoneInfo();
            }

            @Override
            public void onRequestPermissionFailure(List<String> permissions) {
                countDown();
                mRootView.getPhoneInfo();
            }

            @Override
            public void onRequestPermissionFailureWithAskNeverAgain(List<String> permissions) {
                countDown();
                mRootView.getPhoneInfo();
            }
        }, new RxPermissions(mAppManager.getTopActivity()), mErrorHandler, permissionList.get(0), permissionList.get(1), permissionList.get(2));

    }

    /**
     * 上传设备信息
     *
     * @param uploasdInfors 设备信息
     */
    public void uploadDeviceInfo(String uploasdInfors, int touchHardware) {
        String token = DataHelper.getStringSF(mApplication, Constant.SP_KEY_TOKEN);
        RequestBody requestBody = RequestParamUtils.buildUploadDeviceInfo(mApplication, uploasdInfors, touchHardware);
        mModel.uploadDeviceInfo(token, requestBody)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxLifecycleUtils.bindToLifecycle(mRootView))
                .compose(RxUtils.handleBaseResult(mApplication))
                .subscribe(new ErrorHandleSubscriber<DeviceInfoBean>(mErrorHandler) {
                    @Override
                    public void onNext(DeviceInfoBean deviceInfoBean) {

                    }

                    @Override
                    public void onError(Throwable t) {

                    }
                });
    }

    /**
     * 获取APP运行必备参数
     */
    public void getEssentialParameter(boolean root, boolean xposed, boolean emulator) {
        String token = DataHelper.getStringSF(mApplication, Constant.SP_KEY_TOKEN);
        RequestBody requestBody = RequestParamUtils.buildRequestBody(mApplication, new HashMap<>());
        mModel.getEssentialParameter(token, requestBody)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxLifecycleUtils.bindToLifecycle(mRootView))
                .compose(RxUtils.handleBaseResultSaveTimeStamp(mApplication))
                .subscribe(new ErrorHandleSubscriber<DeviceInfoBean>(mErrorHandler) {
                    @Override
                    public void onNext(DeviceInfoBean deviceInfoBean) {
                        Timber.d("===获取APP运行必备参数成功，判断是模拟器还是什么");
                        //getLevel()  移动端安全级别，为1时放行虚拟机和ROOT用户、为2时不放行虚拟机用户、为3时两者都不放行
                        if (Constant.OPEN_TESTING) {
                            if (deviceInfoBean.getLevel() == 2 && emulator) {
                                mRootView.showNormalDialog();
                            } else if (deviceInfoBean.getLevel() == 3) {
                                if (root || xposed || emulator) {
                                    mRootView.showNormalDialog();
                                }
                            } else {
                                dbDeviceInfo(deviceInfoBean);
                            }
                        } else {
                            dbDeviceInfo(deviceInfoBean);
                        }

                        Timber.d("=========Push      deviceInfoBean.getNewsActivity() " + deviceInfoBean.getNewsActivity() + "            deviceInfoBean.getNewsMessage()" + deviceInfoBean.getNewsMessage());
                        //如果有未读活动和反馈消息发生通知
                        if (deviceInfoBean.getNewsActivity() != 0 || deviceInfoBean.getNewsMessage() != 0 || deviceInfoBean.getNewsArticle() != 0) {
                            PushEvent pushEvent = new PushEvent.Builder()
                                    .newActive(deviceInfoBean.getNewsActivity())
                                    .newNotification(deviceInfoBean.getNewsMessage())
                                    .newMessage(deviceInfoBean.getNewsArticle())
                                    .build();
                            EventBus.getDefault().postSticky(pushEvent, EventBusTags.TAG_PUSH_MESSAGE);
                        }
                    }

                    @Override
                    public void onError(Throwable t) {

                    }
                });
    }

    /**
     * 将设备信息保存在数据库；
     *
     * @param deviceInfoBean
     */
    private void dbDeviceInfo(DeviceInfoBean deviceInfoBean) {
        deviceInfoBean.setId(Long.parseLong(Constant.DEVICE_INFO_ID));
        DeviceInfoBeanDao deviceInfoDao = ((MyApplication) mApplication).getDaoSession().getDeviceInfoBeanDao();
        deviceInfoDao.deleteAll();
        deviceInfoDao.insert(deviceInfoBean);
        Timber.d("=db=    SplashPresenter - DeviceInfo - insert 成功");
    }

    /**
     * 第一次安装APP 检查剪切板是否有交易猫ID  有则上传
     *
     * @param rand
     */
    public void appActivate(String rand) {
        RequestBody requestBody = RequestParamUtils.buildisRealizationId(mApplication, rand);
        mModel.isRealization(requestBody).compose(RxUtils.applySchedulers(mRootView))
                .compose(RxUtils.handleBaseResult(mApplication))
                .subscribe(new ErrorHandleSubscriber<String>(mErrorHandler) {
                    @Override
                    public void onNext(String s) {

                    }

                    @Override
                    public void onError(Throwable t) {

                    }
                });
    }

    /**
     * 第一次安装APP 检查是否是 有米渠道
     */
    public void appYouMi() {
        RequestBody requestBody = RequestParamUtils.buildRequestBody(mApplication, new HashMap<>());
        mModel.isRealizationYouMi(requestBody).compose(RxUtils.applySchedulers(mRootView))
                .compose(RxUtils.handleBaseResult(mApplication))
                .subscribe(new ErrorHandleSubscriber<Object>(mErrorHandler) {
                    @Override
                    public void onNext(Object o) {
//                        DataHelper.setStringSF(mApplication, Constant.SP_YOU_MI_KEY_FIRST, "no");//设置打开标识
                    }

                    @Override
                    public void onError(Throwable t) {

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
     * 倒计时
     */
    public void countDown() {
        Observable.interval(10, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                .take(360)
                .map(aLong -> aLong + 10)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxLifecycleUtils.bindToLifecycle(mRootView))
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull Long aLong) {
                        mRootView.updateCountDown(aLong);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        mRootView.updateCountDown(Long.parseLong("0"));
                    }
                });
    }
}
