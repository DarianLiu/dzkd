package com.dzkandian.mvp.common.presenter;

import android.app.Application;
import android.text.TextUtils;

import com.dzkandian.app.MyApplication;
import com.dzkandian.app.config.EventBusTags;
import com.dzkandian.app.http.utils.RequestParamUtils;
import com.dzkandian.app.http.utils.RxUtils;
import com.dzkandian.common.uitls.Constant;
import com.dzkandian.db.DeviceInfoBeanDao;
import com.dzkandian.db.UserInfoBeanDao;
import com.dzkandian.mvp.common.contract.LoginContract;
import com.dzkandian.storage.bean.DeviceInfoBean;
import com.dzkandian.storage.bean.UserBean;
import com.dzkandian.storage.bean.UserInfoBean;
import com.dzkandian.storage.event.PushEvent;
import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.integration.AppManager;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.utils.DataHelper;
import com.jess.arms.utils.RxLifecycleUtils;

import org.simple.eventbus.EventBus;

import java.io.File;
import java.util.HashMap;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import me.jessyan.rxerrorhandler.core.RxErrorHandler;
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber;
import okhttp3.RequestBody;
import timber.log.Timber;


@ActivityScope
public class LoginPresenter extends BasePresenter<LoginContract.Model, LoginContract.View> {
    @Inject
    RxErrorHandler mErrorHandler;
    @Inject
    Application mApplication;
    @Inject
    ImageLoader mImageLoader;
    @Inject
    AppManager mAppManager;

    @Inject
    public LoginPresenter(LoginContract.Model model, LoginContract.View rootView) {
        super(model, rootView);
    }

    /**
     * 发送验证码
     *
     * @param phone 手机号码
     */
    public void senSmsCode(String phone) {
        RequestBody body = RequestParamUtils.buildSendSmsCode(mApplication, phone, "SMS_LOGIN");
        mModel.sendSmsCode("", body)
                .compose(RxUtils.applySchedulers(mRootView))
                .compose(RxUtils.handleBaseResult(mApplication))
                .subscribe(new ErrorHandleSubscriber<UserBean>(mErrorHandler) {
                    @Override
                    public void onNext(UserBean data) {
                        mRootView.showMessage("验证码发送成功");
                        mRootView.startCountDown(60);
                    }
                });
    }

    /**
     * 短信验证码登录
     *
     * @param phone   手机号码
     * @param smsCode 短信验证码
     */
    public void smsLogin(String phone, String smsCode, int touchHardware) {
        RequestBody requestBody = RequestParamUtils.buildSmsLogin(mApplication, phone, smsCode);
        mModel.smsLogin(requestBody).compose(RxUtils.applySchedulers(mRootView))
                .compose(RxUtils.handleBaseResult(mApplication))
                .subscribe(new ErrorHandleSubscriber<UserBean>(mErrorHandler) {
                    @Override
                    public void onNext(UserBean userBean) {
                        uploadDeviceInfo(userBean, touchHardware);
                    }
                });
    }

    /**
     * 手机号密码登录
     *
     * @param phone    手机号码
     * @param password 密码
     */
    public void login(String phone, String password, int touchHardware) {
        RequestBody requestBody = RequestParamUtils.buildLogin(mApplication, phone, password);
        mModel.login(requestBody).compose(RxUtils.applySchedulers(mRootView))
                .compose(RxUtils.handleBaseResult(mApplication))
                .subscribe(new ErrorHandleSubscriber<UserBean>(mErrorHandler) {
                    @Override
                    public void onNext(UserBean userBean) {
                        uploadDeviceInfo(userBean, touchHardware);
                    }
                });
    }

    public void wxLogin(String code, int touchHardware) {
        RequestBody requestBody = RequestParamUtils.buildWXLogin(mApplication, code);
        mModel.wxLogin(requestBody).compose(RxUtils.applySchedulers(mRootView))
                .compose(RxUtils.handleBaseResult(mApplication))
                .subscribe(new ErrorHandleSubscriber<UserBean>(mErrorHandler) {
                    @Override
                    public void onNext(UserBean userBean) {
                        uploadDeviceInfo(userBean, touchHardware);

                    }
                });
    }

    /**
     * 上传设备信息
     */
    public void uploadDeviceInfo(UserBean userBean, int touchHardware) {
        String uploasdInfors = DataHelper.getStringSF(mApplication, Constant.SP_KEY_MOBILE_INFO);
        RequestBody requestBody = RequestParamUtils.buildUploadDeviceInfo(mApplication, uploasdInfors, touchHardware);
        mModel.uploadDeviceInfo(userBean.getToken(), requestBody)
                .compose(RxUtils.applySchedulers(mRootView))
                .compose(RxUtils.handleBaseResult(mApplication))
                .subscribe(new ErrorHandleSubscriber<DeviceInfoBean>(mErrorHandler) {
                    @Override
                    public void onNext(DeviceInfoBean deviceInfoBean) {
                        Timber.d("===上传设备信息成功，不做任何处理");
                        getEssentialParameter(userBean);
                    }
                });
    }

    /**
     * 获取APP运行必备参数
     */
    public void getEssentialParameter(UserBean userBean) {
        int emulator = DataHelper.getIntergerSF(mApplication, Constant.SP_KEY_MOBILE_EMULATOR);
        int xposed = DataHelper.getIntergerSF(mApplication, Constant.SP_KEY_MOBILE_XPOSED);
        int root = DataHelper.getIntergerSF(mApplication, Constant.SP_KEY_MOBILE_ROOT);
        RequestBody requestBody = RequestParamUtils.buildRequestBody(mApplication, new HashMap<>());
        mModel.getEssentialParameter(userBean.getToken(), requestBody)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxLifecycleUtils.bindToLifecycle(mRootView))
                .compose(RxUtils.handleBaseResultSaveTimeStamp(mApplication))
                .subscribe(new ErrorHandleSubscriber<DeviceInfoBean>(mErrorHandler) {
                    @Override
                    public void onNext(DeviceInfoBean deviceInfoBean) {

                        Timber.d("===获取APP运行必备参数成功，判断是模拟器还是什么");
                        //是否开启限制（方便平时模拟器调试）
                        if (Constant.OPEN_TESTING) {
                            if (deviceInfoBean.getLevel() == 2 && emulator == 1) {
                                //后台开启限制模拟器
                                mRootView.showNormalDialog();
                            } else if (deviceInfoBean.getLevel() == 3 && (emulator == 1 || xposed == 2 || root == 3)) {
                                //后台开启限制模拟器、xposed、root限制
                                mRootView.showNormalDialog();
                            } else {
                                dbDeviceInfo(deviceInfoBean, userBean);
                            }
                        } else {
                            dbDeviceInfo(deviceInfoBean, userBean);
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
     * @param deviceInfoBean
     */
    private void dbDeviceInfo(DeviceInfoBean deviceInfoBean, UserBean userBean) {
        DeviceInfoBeanDao deviceInfoDao = ((MyApplication) mApplication).getDaoSession().getDeviceInfoBeanDao();
        deviceInfoDao.deleteAll();
        deviceInfoDao.insert(deviceInfoBean);
        Timber.d("=db=    LoginPresenter - DeviceInfo - insert 成功");
        userInfo(userBean);
    }

    /**
     * 获取用户信息
     */
    public void userInfo(UserBean userBean) {
        if (!TextUtils.isEmpty(userBean.getToken())) {
            RequestBody requestBody = RequestParamUtils.buildRequestBody(mApplication, new HashMap<>());
            mModel.userInfo(userBean.getToken(), requestBody)
                    .compose(RxUtils.applySchedulers(mRootView))
                    .compose(RxUtils.handleBaseResult(mApplication))
                    .subscribe(new ErrorHandleSubscriber<UserInfoBean>(mErrorHandler) {
                        @Override
                        public void onNext(UserInfoBean userInfoBean) {
                            mRootView.showMessage("登录成功");
                            UserInfoBeanDao userInfoDao = ((MyApplication) mApplication).getDaoSession().getUserInfoBeanDao();
                            userInfoDao.deleteAll();
                            userInfoDao.insert(userInfoBean);
                            Timber.d("=db=    LoginPresenter - UserInfo - insert 成功");

                            DataHelper.setStringSF(mApplication, Constant.SP_KEY_TOKEN, userBean.getToken());
                            DataHelper.setStringSF(mApplication, Constant.SP_KEY_EXPIRE, userBean.getExpire());

                            if (new File(Constant.FIEL_SAVE_PATH + Constant.USER_HEADER_IMAGE_NAME).exists()) {
                                new File(Constant.FIEL_SAVE_PATH + Constant.USER_HEADER_IMAGE_NAME).delete();
                            }
                            /*保存用户ID*/
                            DataHelper.setStringSF(mApplication, Constant.SP_KEY_USER_ID, String.valueOf(userInfoBean.getUserId()));
                            EventBus.getDefault().post(true, EventBusTags.TAG_LOGIN_STATE);
                            mRootView.killMyself();
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
