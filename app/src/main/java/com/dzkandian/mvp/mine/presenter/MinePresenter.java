package com.dzkandian.mvp.mine.presenter;

import android.app.Application;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.dzkandian.app.MyApplication;
import com.dzkandian.app.config.EventBusTags;
import com.dzkandian.app.http.utils.DownLoadManager;
import com.dzkandian.app.http.utils.RequestParamUtils;
import com.dzkandian.app.http.utils.RxUtils;
import com.dzkandian.common.uitls.Constant;
import com.dzkandian.db.DeviceInfoBeanDao;
import com.dzkandian.db.UserInfoBeanDao;
import com.dzkandian.mvp.mine.contract.MineContract;
import com.dzkandian.storage.bean.CoinBean;
import com.dzkandian.storage.bean.DeviceInfoBean;
import com.dzkandian.storage.bean.MarqueeBean;
import com.dzkandian.storage.bean.RandomAdBean;
import com.dzkandian.storage.bean.UserInfoBean;
import com.dzkandian.storage.event.PushEvent;
import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.integration.AppManager;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.utils.DataHelper;
import com.jess.arms.utils.DeviceUtils;
import com.jess.arms.utils.PermissionUtil;
import com.jess.arms.utils.RxLifecycleUtils;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.simple.eventbus.EventBus;

import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import me.jessyan.rxerrorhandler.core.RxErrorHandler;
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber;
import okhttp3.RequestBody;
import timber.log.Timber;


@ActivityScope
public class MinePresenter extends BasePresenter<MineContract.Model, MineContract.View> {
    @Inject
    RxErrorHandler mErrorHandler;
    @Inject
    Application mApplication;
    @Inject
    ImageLoader mImageLoader;
    @Inject
    AppManager mAppManager;

    @Inject
    public MinePresenter(@NonNull MineContract.Model model, @NonNull MineContract.View rootView) {
        super(model, rootView);
    }

    private String getToken() {
        return DataHelper.getStringSF(mApplication, Constant.SP_KEY_TOKEN);
    }

    /**
     * 获取金币数量
     */
    public void getCoin() {
        if (!TextUtils.isEmpty(getToken())) {
            RequestBody requestBody = RequestParamUtils.buildRequestBody(mApplication, new HashMap<>());
            mModel.getCoin(getToken(), requestBody)
                    .subscribeOn(Schedulers.io())
                    .doOnSubscribe(disposable -> {
//                                disposable.dispose();
                    })
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doFinally(new Action() {
                        @Override
                        public void run() {
                            mRootView.finishRefresh();//隐藏进度条
                        }
                    }).compose(RxLifecycleUtils.bindToLifecycle(mRootView))
                    .compose(RxUtils.handleBaseResult(mApplication))
                    .subscribe(new ErrorHandleSubscriber<CoinBean>(mErrorHandler) {
                        @Override
                        public void onNext(CoinBean coinBean) {
                            mRootView.updateCoin(coinBean);
                        }
                    });
        }
    }

    /**
     * 更新金币数量
     */
    public void updateCoin() {
        if (!TextUtils.isEmpty(getToken())) {
            RequestBody requestBody = RequestParamUtils.buildRequestBody(mApplication, new HashMap<>());
            mModel.getCoin(getToken(), requestBody)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .compose(RxLifecycleUtils.bindToLifecycle(mRootView))
                    .compose(RxUtils.handleBaseResult(mApplication))
                    .subscribe(new ErrorHandleSubscriber<CoinBean>(mErrorHandler) {
                        @Override
                        public void onNext(CoinBean coinBean) {
                            mRootView.updateCoin(coinBean);
                        }

                        @Override
                        public void onError(Throwable t) {
                        }
                    });
        }
    }

    /**
     * 获取跑马灯数据
     */
    public void getMarquee() {
        if (!TextUtils.isEmpty(getToken())) {
            mModel.getMarquee()
                    .compose(RxUtils.applySchedulers(mRootView))
                    .compose(RxUtils.handleBaseResultSaveTimeStamp(mApplication))
                    .subscribe(new ErrorHandleSubscriber<MarqueeBean>(mErrorHandler) {
                                   @Override
                                   public void onNext(MarqueeBean marqueeBean) {
                                       mRootView.SetMarquee(marqueeBean);
                                   }
                               }
                    );
        }
    }

    /**
     * 请求权限
     *
     * @param shareImageUrl 分享图片地址
     */
    public void requestPermission(String shareImageUrl) {
        PermissionUtil.externalStorage(new PermissionUtil.RequestPermission() {
            @Override
            public void onRequestPermissionSuccess() {
                downLoad(shareImageUrl);
            }

            @Override
            public void onRequestPermissionFailure(List<String> permissions) {
                mRootView.showMessage("权限获取失败");
            }

            @Override
            public void onRequestPermissionFailureWithAskNeverAgain(List<String> permissions) {
                mRootView.showMessage("权限获取失败，将无法正常使用该应用！");
            }

        }, new RxPermissions(mAppManager.getTopActivity()), mErrorHandler);
    }

    /**
     * 下载图片
     *
     * @param shareImageUrl 分享图片地址
     */
    private void downLoad(String shareImageUrl) {
        mModel.update(shareImageUrl).subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .concatMap(responseBody -> {
                    try {
                        String file_path = DownLoadManager.writeResponseBodyToDisk(responseBody);
                        return RxUtils.createData(file_path);
                    } catch (Exception e) {
                        return Observable.error(e);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxLifecycleUtils.bindToLifecycle(mRootView))
                .subscribe(new ErrorHandleSubscriber<String>(mErrorHandler) {
                    @Override
                    public void onNext(String s) {
                        mRootView.downloadCallBack(s);
                    }
                });
    }


    /**
     * 获取用户信息
     */
    public void userInfo() {
        if (!TextUtils.isEmpty(getToken())) {
            RequestBody requestBody = RequestParamUtils.buildRequestBody(mApplication, new HashMap<>());
            mModel.userInfo(getToken(), requestBody)
                    .compose(RxUtils.applySchedulers(mRootView))
                    .compose(RxUtils.handleBaseResult(mApplication))
                    .subscribe(new ErrorHandleSubscriber<UserInfoBean>(mErrorHandler) {
                        @Override
                        public void onNext(UserInfoBean userInfoBean) {
                            UserInfoBeanDao userInfoDao = ((MyApplication) mApplication).getDaoSession().getUserInfoBeanDao();
                            userInfoDao.deleteAll();
                            userInfoDao.insert(userInfoBean);
//                            Timber.d("=db=    LoginPresenter - UserInfo - insert 成功");

                            /*保存用户ID*/
                            DataHelper.setStringSF(mApplication, Constant.SP_KEY_USER_ID, String.valueOf(userInfoBean.getUserId()));
                            mRootView.updateUserInfo(userInfoBean);
                        }
                    });
        }
    }

    /**
     * 获取APP运行必备参数
     */
    public void getEssentialParameter() {
        RequestBody requestBody = RequestParamUtils.buildRequestBody(mApplication, new HashMap<>());
        mModel.getEssentialParameter(getToken(), requestBody)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxLifecycleUtils.bindToLifecycle(mRootView))
                .compose(RxUtils.handleBaseResultSaveTimeStamp(mApplication))
                .subscribe(new ErrorHandleSubscriber<DeviceInfoBean>(mErrorHandler) {
                    @Override
                    public void onNext(DeviceInfoBean deviceInfoBean) {
//                        Timber.d("===获取APP运行必备参数成功，判断是模拟器还是什么");
                        //是否开启限制（方便平时模拟器调试）
                        if (Constant.OPEN_TESTING) {
//                            if (deviceInfoBean.getLevel() == 2 && emulator == 1) {
//                                //后台开启限制模拟器
//                                mRootView.showNormalDialog();
//                            } else if (deviceInfoBean.getLevel() == 3 && (emulator == 1 || xposed == 2 || root == 3)) {
//                                //后台开启限制模拟器、xposed、root限制
//                                mRootView.showNormalDialog();
//                            } else {
                            dbDeviceInfo(deviceInfoBean);
//                            }
                        } else {
                            dbDeviceInfo(deviceInfoBean);
                        }

                        EventBus.getDefault().post(new PushEvent.Builder()
                                        .newActive(deviceInfoBean.getNewsActivity())
                                        .newNotification(deviceInfoBean.getNewsMessage())
                                        .newMessage(deviceInfoBean.getNewsArticle())
                                        .build(),
                                EventBusTags.TAG_PUSH_MESSAGE);

                    }
                });
    }

    /**
     * 将设备信息保存在数据库；
     *
     * @param deviceInfoBean 设备信息
     */
    private void dbDeviceInfo(DeviceInfoBean deviceInfoBean) {
        DeviceInfoBeanDao deviceInfoDao = ((MyApplication) mApplication).getDaoSession().getDeviceInfoBeanDao();
        deviceInfoDao.deleteAll();
        deviceInfoDao.insert(deviceInfoBean);
//        Timber.d("=db=    LoginPresenter - DeviceInfo - insert 成功");
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
     * 请求广告接口
     */
    public void requestAd() {
        mModel.getRandomAd(
                DeviceUtils.getVersionName(mApplication),
                DataHelper.getStringSF(mApplication, Constant.PHONE_IMEI),
                Constant.AD_SUPPORT_SDK_ALL,
                Constant.AD_MINE_BOTTOM_BIG_PIC)
                .compose(RxUtils.applyhideSchedulers(mRootView))
                .compose(RxUtils.handleBaseResult(mApplication))
                .subscribe(new ErrorHandleSubscriber<RandomAdBean>(mErrorHandler) {
                    @Override
                    public void onNext(RandomAdBean randomAdBean) {
                        mRootView.successSelfAd(randomAdBean);
                    }
                });
    }

}
