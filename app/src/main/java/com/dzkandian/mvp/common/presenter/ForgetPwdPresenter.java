package com.dzkandian.mvp.common.presenter;

import android.app.Application;
import android.text.TextUtils;

import com.dzkandian.app.MyApplication;
import com.dzkandian.app.config.EventBusTags;
import com.dzkandian.app.http.utils.RequestParamUtils;
import com.dzkandian.app.http.utils.RxUtils;
import com.dzkandian.common.uitls.Constant;
import com.dzkandian.db.UserInfoBeanDao;
import com.dzkandian.mvp.common.contract.ForgetPwdContract;
import com.dzkandian.storage.bean.UserBean;
import com.dzkandian.storage.bean.UserInfoBean;
import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.integration.AppManager;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.utils.DataHelper;

import org.simple.eventbus.EventBus;

import java.util.List;

import javax.inject.Inject;

import me.jessyan.rxerrorhandler.core.RxErrorHandler;
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber;
import okhttp3.RequestBody;
import timber.log.Timber;


@ActivityScope
public class ForgetPwdPresenter extends BasePresenter<ForgetPwdContract.Model, ForgetPwdContract.View> {
    @Inject
    RxErrorHandler mErrorHandler;
    @Inject
    Application mApplication;
    @Inject
    ImageLoader mImageLoader;
    @Inject
    AppManager mAppManager;

    @Inject
    public ForgetPwdPresenter(@android.support.annotation.NonNull ForgetPwdContract.Model model, @android.support.annotation.NonNull ForgetPwdContract.View rootView) {
        super(model, rootView);
    }

    private String getToken() {
        return DataHelper.getStringSF(mApplication, Constant.SP_KEY_TOKEN);
    }

    /**
     * 发送验证码
     *
     * @param phone 手机号码
     */
    public void senSmsCode(String phone) {
        RequestBody body = RequestParamUtils.buildSendSmsCode(mApplication, phone, "MODIFY_OR_FIND_PASS");
        mModel.sendSmsCode("", body)
                .compose(RxUtils.applySchedulers(mRootView))
                .compose(RxUtils.handleBaseResult(mApplication))
                .subscribe(new ErrorHandleSubscriber<UserBean>(mErrorHandler) {
                    @Override
                    public void onNext(UserBean data) {
                        mRootView.showMessage("发送成功");
                        mRootView.startCountDown(60);
                    }
                });
    }

    /**
     * 忘记密码
     *
     * @param phone       手机号码
     * @param smsCode     手机验证码
     * @param md5Password MD5加密密码
     */
    public void forgetPassword(String phone, String smsCode, String md5Password) {
        RequestBody requestBody = RequestParamUtils.buildForgetPwd(mApplication, phone, smsCode, md5Password);
        mModel.forgetPassword(requestBody).compose(RxUtils.applySchedulers(mRootView))
                .compose(RxUtils.handleBaseResult(mApplication))
                .subscribe(new ErrorHandleSubscriber<UserBean>(mErrorHandler) {
                    @Override
                    public void onNext(UserBean userBean) {
                        mRootView.showMessage("修改密码成功");
                        mRootView.killMyself();
                    }
                });
    }


    /**
     * 修改密码
     *
     * @param smsCode     手机验证码
     * @param md5Password MD5加密密码
     */
    public void revisePassword(String phone, String smsCode, String md5Password) {
        if (!TextUtils.isEmpty(getToken())) {
            RequestBody requestBody = RequestParamUtils.buildRevisePwd(mApplication, phone, smsCode, md5Password);
            mModel.revisePassword(getToken(), requestBody).compose(RxUtils.applySchedulers(mRootView))
                    .compose(RxUtils.handleBaseResult(mApplication))
                    .subscribe(new ErrorHandleSubscriber<UserBean>(mErrorHandler) {
                        @Override
                        public void onNext(UserBean userBean) {
                            UserInfoBeanDao userInfoDao = ((MyApplication) mApplication).getDaoSession().getUserInfoBeanDao();
                            List<UserInfoBean> list = userInfoDao.loadAll();
                            if (list != null && list.size() > 0) {
                                Timber.d("=db=    ForgetPwdPresenter - UserInfo - query 成功");
                                UserInfoBean userInfoBean = (list.get(0));
                                userInfoBean.setPassword("Y");
                                userInfoDao.update(userInfoBean);
                                EventBus.getDefault().post(userInfoBean, EventBusTags.TAG_UPDATE_USER_INFO);
                                mRootView.killMyself();
                            } else {
                                Timber.d("=db=    ForgetPwdPresenter - UserInfo - query 失败");
                            }
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
