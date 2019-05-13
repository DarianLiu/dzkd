package com.dzkandian.mvp.mine.presenter;

import android.app.Application;
import android.text.TextUtils;

import com.dzkandian.app.MyApplication;
import com.dzkandian.app.config.EventBusTags;
import com.dzkandian.app.http.utils.RequestParamUtils;
import com.dzkandian.app.http.utils.RxUtils;
import com.dzkandian.common.uitls.Constant;
import com.dzkandian.db.UserInfoBeanDao;
import com.dzkandian.mvp.mine.contract.UpdatePhoneContract;
import com.dzkandian.storage.bean.UserBean;
import com.dzkandian.storage.bean.UserInfoBean;
import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.integration.AppManager;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.utils.DataHelper;
import com.jess.arms.utils.RxLifecycleUtils;
import com.trello.rxlifecycle2.android.ActivityEvent;

import org.simple.eventbus.EventBus;

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
public class UpdatePhonePresenter extends BasePresenter<UpdatePhoneContract.Model, UpdatePhoneContract.View> {
    @Inject
    RxErrorHandler mErrorHandler;
    @Inject
    Application mApplication;
    @Inject
    ImageLoader mImageLoader;
    @Inject
    AppManager mAppManager;

    @Inject
    public UpdatePhonePresenter(@android.support.annotation.NonNull UpdatePhoneContract.Model model, @android.support.annotation.NonNull UpdatePhoneContract.View rootView) {
        super(model, rootView);
    }

    private String getToken() {
        return DataHelper.getStringSF(mApplication, Constant.SP_KEY_TOKEN);
    }

    private void updatePhone(String phone) {
        UserInfoBeanDao userInfoDao = ((MyApplication) mApplication).getDaoSession().getUserInfoBeanDao();
        List<UserInfoBean> list = userInfoDao.loadAll();
        if (list != null && list.size() > 0) {
            Timber.d("=db=    UpdatePhonePresenter - UserInfo - query 成功");
            UserInfoBean userInfoBean = (list.get(0));
            userInfoBean.setPhone(phone);
            userInfoDao.update(userInfoBean);
            mRootView.killMyself();
            EventBus.getDefault().post(userInfoBean, EventBusTags.TAG_UPDATE_USER_INFO);
        } else {
            Timber.d("=db=    UpdatePhonePresenter - UserInfo - query 失败");
        }
    }

    /**
     * 绑定手机号码，同时设置登录密码，当用户以授权登录的方式注册APP后，用户可以通过此接口绑定手机号码以及同时设置登录密码
     */
    public void bindPhone(String phone, String code, String password) {
        if (!TextUtils.isEmpty(getToken())) {
            RequestBody requestBody = RequestParamUtils.bindPhone(mApplication, phone, code, password);
            mModel.bindPhone(getToken(), requestBody)
                    .compose(RxUtils.applySchedulers(mRootView))
                    .compose(RxUtils.handleBaseResult(mApplication))
                    .subscribe(new ErrorHandleSubscriber<UserBean>(mErrorHandler) {
                        @Override
                        public void onNext(UserBean userBean) {
                            mRootView.showMessage("修改成功");
                            StringBuilder sPhone = new StringBuilder(phone);
                            sPhone.replace(3, 7, "****");
                            updatePhone(sPhone.toString());
                        }
                    });
        }
    }


    /**
     * 已经绑定过手机号码后，更换绑定手机号(使用密码更换绑定手机号)
     */
    public void passwordUpdatePhone(String phone, String code, String password) {
        if (!TextUtils.isEmpty(getToken())) {
            RequestBody requestBody = RequestParamUtils.buildPasswordUpdatePhone(mApplication, phone, code, password);
            mModel.changePhone(getToken(), requestBody)
                    .compose(RxUtils.applySchedulers(mRootView))
                    .compose(RxUtils.handleBaseResult(mApplication))
                    .subscribe(new ErrorHandleSubscriber<UserBean>(mErrorHandler) {
                        @Override
                        public void onNext(UserBean userBean) {
                            mRootView.showMessage("修改成功");
                            StringBuilder sPhone = new StringBuilder(phone);
                            sPhone.replace(3, 7, "****");
                            updatePhone(sPhone.toString());
                        }
                    });
        }
    }


    /**
     * 已经绑定过手机号码后，更换绑定手机号(使用原号码短信验证更换绑定手机号)
     */
    public void smsCodeUpdatePhone(String phone, String newCode, String oldCode) {
        if (!TextUtils.isEmpty(getToken())) {
            RequestBody requestBody = RequestParamUtils.buildSmsCodeUpdatePhone(mApplication, phone, newCode, oldCode);
            mModel.changePhone(getToken(), requestBody)
                    .compose(RxUtils.applySchedulers(mRootView))
                    .compose(RxUtils.handleBaseResult(mApplication))
                    .subscribe(new ErrorHandleSubscriber<UserBean>(mErrorHandler) {
                        @Override
                        public void onNext(UserBean userBean) {
//                            UserInfoBean userInfoBean = DataHelper.getDeviceData(mApplication, Constant.SP_KEY_USER_INFO);
//                            userInfoBean.setPhone(phone);
//                            DataHelper.saveDeviceData(mApplication, Constant.SP_KEY_USER_INFO, userInfoBean);
//                            EventBus.getDefault().post(true, EventBusTags.TAG_UPDATE_USER_INFO);
                            mRootView.killMyself();
                        }
                    });
        }
    }

    /**
     * 发送短信
     */
    public void sendSmsCode(String phone) {
        if (!TextUtils.isEmpty(getToken())) {
            RequestBody requestBody = RequestParamUtils.buildSendSmsCode(mApplication, phone, "MODIFY_PHONE");
            mModel.sendSmsCode(getToken(), requestBody)
                    .compose(RxUtils.applySchedulers(mRootView))
                    .compose(RxUtils.handleBaseResult(mApplication))
                    .subscribe(new ErrorHandleSubscriber<UserBean>(mErrorHandler) {
                        @Override
                        public void onNext(UserBean userInfoBean) {
                            mRootView.showMessage("短信发送成功");
                            mRootView.showNotice(userInfoBean.getMessage());
                            countDown(60);
                        }
                    });
        }
    }

    private int lastPauseSurplusTime;//上一次暂停的剩余秒数；

    public void resumeTime(int resumeTime) {
        int time = lastPauseSurplusTime - (resumeTime / 1000) - 1;
        if (time > 0) {
            countDown(time);
        } else {
            mRootView.SmsCountDown(Long.parseLong("0"));
        }
    }

    /**
     * 倒计时
     */
    private void countDown(int totalTime) {
        Observable.interval(1, TimeUnit.SECONDS, Schedulers.io())
                .take(totalTime)
                .map(aLong -> totalTime - (aLong + 1))
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxLifecycleUtils.bindUntilEvent(mRootView, ActivityEvent.PAUSE))
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull Long aLong) {
                        mRootView.SmsCountDown(aLong);
                        lastPauseSurplusTime = aLong.intValue();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        mRootView.SmsCountDown(Long.parseLong("0"));
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
