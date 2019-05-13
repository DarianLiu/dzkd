package com.dzkandian.mvp.mine.presenter;

import android.app.Application;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.alipay.sdk.app.AuthTask;
import com.dzkandian.R;
import com.dzkandian.app.MyApplication;
import com.dzkandian.app.config.EventBusTags;
import com.dzkandian.app.exception.ApiException;
import com.dzkandian.app.http.utils.RequestParamUtils;
import com.dzkandian.app.http.utils.RxUtils;
import com.dzkandian.common.uitls.Constant;
import com.dzkandian.db.UserInfoBeanDao;
import com.dzkandian.mvp.common.ui.activity.LoginActivity;
import com.dzkandian.mvp.mine.contract.UpdateInfoContract;
import com.dzkandian.storage.bean.UserBindBean;
import com.dzkandian.storage.bean.UserInfoBean;
import com.dzkandian.storage.bean.mine.AlipayInfoBean;
import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.integration.AppManager;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.utils.DataHelper;
import com.jess.arms.utils.RxLifecycleUtils;
import com.trello.rxlifecycle2.android.ActivityEvent;

import org.simple.eventbus.EventBus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import me.jessyan.rxerrorhandler.core.RxErrorHandler;
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber;
import okhttp3.RequestBody;
import timber.log.Timber;


@ActivityScope
public class UpdateInfoPresenter extends BasePresenter<UpdateInfoContract.Model, UpdateInfoContract.View> {
    @Inject
    RxErrorHandler mErrorHandler;
    @Inject
    Application mApplication;
    @Inject
    ImageLoader mImageLoader;
    @Inject
    AppManager mAppManager;

    @Inject
    public UpdateInfoPresenter(@NonNull UpdateInfoContract.Model model, @NonNull UpdateInfoContract.View rootView) {
        super(model, rootView);
    }

    private String getToken() {
        return DataHelper.getStringSF(mApplication, Constant.SP_KEY_TOKEN);
    }

    /**
     * 要修改的信息
     * 可选：username(用户名)
     * alipayAccount(支付宝-账户)
     * alipayName(支付宝-姓名)
     * weixinPayName(微信钱包-姓名)
     * weixinPayPhone(微信钱包-手机号)
     */
    public void updateInfo(String key, String value, String key2, String value2) {
        if (TextUtils.isEmpty(getToken())) {
            mRootView.launchActivity(new Intent(mAppManager.getTopActivity(), LoginActivity.class));
        } else {
            RequestBody requestBody = RequestParamUtils.buildUpdateInfo(mApplication, key, value, key2, value2);
            mModel.updateInfo(getToken(), requestBody)
                    .compose(RxUtils.applySchedulers(mRootView))
                    .compose(RxUtils.handleBaseResult(mApplication))
                    .subscribe(new ErrorHandleSubscriber<UserInfoBean>(mErrorHandler) {
                        @Override
                        public void onNext(UserInfoBean muserInfoBean) {
                            mRootView.showMessage("修改成功");
                            UserInfoBeanDao userInfoDao = ((MyApplication) mApplication).getDaoSession().getUserInfoBeanDao();
                            List<UserInfoBean> list = userInfoDao.loadAll();
                            if (list != null && list.size() > 0) {
//                                Timber.d("=db=    UpdateInfoPresenter - UserInfo updateInfo - query 成功");
                                UserInfoBean userInfoBean = (list.get(0));
                                if (TextUtils.equals(key, "username")) {
                                    userInfoBean.setUsername(value);
                                } else if (TextUtils.equals(key, "weixinPayName")) {
                                    userInfoBean.setWeixinPayName(value);
                                    userInfoBean.setWeixinPayPhone(value2);
                                } else if (TextUtils.equals(key, "alipayAccount")) {
                                    userInfoBean.setAlipayAccount(value);
                                    userInfoBean.setAlipayName(value2);
                                }
                                userInfoDao.update(userInfoBean);
                                EventBus.getDefault().post(userInfoBean, EventBusTags.TAG_UPDATE_USER_INFO);
                                mRootView.killMyself();
                            } else {
//                                Timber.d("=db=    UpdateInfoPresenter - UserInfo updateInfo - query 失败");
                            }
                        }

                        @Override
                        public void onError(Throwable t) {
                            if (t instanceof ApiException) {
                                if (((ApiException) t).getCode() == 412) {
                                    mRootView.showMessage("发送失败，请修改输入的违规字符后再试");
                                } else {
                                    super.onError(t);
                                }
                            } else {
                                super.onError(t);
                            }
                        }
                    });
        }
    }

    /**
     * 微信钱包绑定
     *
     * @param code 微信授权返回数据
     */
    public void weChatPayBind(String code, String weixinPayAppid) {
        if (TextUtils.isEmpty(getToken())) {
            mRootView.launchActivity(new Intent(mAppManager.getTopActivity(), LoginActivity.class));
        } else {
            RequestBody requestBody = RequestParamUtils.buildWXPay(mApplication, code, weixinPayAppid);
            mModel.weixinPayBind(getToken(), requestBody)
                    .compose(RxUtils.applySchedulers(mRootView))
                    .compose(RxUtils.handleBaseResult(mApplication))
                    .subscribe(new ErrorHandleSubscriber<UserBindBean>(mErrorHandler) {
                        @Override
                        public void onNext(@NonNull UserBindBean userBindBean) {
                            UserInfoBeanDao userInfoDao = ((MyApplication) mApplication).getDaoSession().getUserInfoBeanDao();
                            List<UserInfoBean> list = userInfoDao.loadAll();
                            if (list != null && list.size() > 0) {
//                                Timber.d("=db=    UpdateInfoPresenter - UserInfo weChatPayBind - query 成功");
                                UserInfoBean userInfoBean = (list.get(0));
                                userInfoBean.setWeixinPayNickname(userBindBean.getNickname());
                                userInfoBean.setWeixinPayAvatar(userBindBean.getHeadimgurl());
                                userInfoBean.setWeixinPayAppid(weixinPayAppid);
                                userInfoDao.update(userInfoBean);
                            } else {
//                                Timber.d("=db=    UpdateInfoPresenter - UserInfo weChatPayBind - query 失败");
                            }
                            mRootView.showMessage(mApplication.getResources().getString(R.string.toast_authorize));
                            mRootView.updateWeChatPayBindInfo(userBindBean.getNickname(), userBindBean.getHeadimgurl());
                        }
                    });
        }
    }

    /**
     * 支付宝登录取签
     */
    public void aLiPayLoginParam() {
        if (TextUtils.isEmpty(getToken())) {
            mRootView.launchActivity(new Intent(mAppManager.getTopActivity(), LoginActivity.class));
        } else {
            RequestBody requestBody = RequestParamUtils.buildRequestBody(mApplication, new HashMap<>());
            mModel.aLiPayLoginParam(getToken(), requestBody)
                    .compose(RxUtils.applySchedulers(mRootView))
                    .compose(RxUtils.handleBaseResult(mApplication))
                    .subscribe(new ErrorHandleSubscriber<AlipayInfoBean>(mErrorHandler) {
                        @Override
                        public void onNext(AlipayInfoBean alipayInfoBean) {
//                            Timber.d("====================alipayInfoBean.getTargetId()" + alipayInfoBean.getTargetId() + "\n"
//                                    + alipayInfoBean.getRsasign());
                            String info =
                                    "apiname=com.alipay.account.auth" +
                                            "&app_id=2017121200628903" +
                                            "&app_name=mc" +
                                            "&auth_type=AUTHACCOUNT" +
                                            "&biz_type=openservice" +
                                            "&method=alipay.open.auth.sdk.code.get" +
                                            "&pid=2088821952575743" +
                                            "&product_id=APP_FAST_LOGIN" +
                                            "&scope=kuaijie" +
                                            "&sign_type=RSA2" +
                                            "&target_id=" + alipayInfoBean.getTargetId() +
                                            "&sign=" + alipayInfoBean.getRsasign();
//                            AuthTask authTask = new AuthTask(mAppManager.getTopActivity());
//                            new Thread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    Map<String, String> resultMap = authTask.authV2(info, true);
//                                    String resultStatus = resultMap.get("resultStatus");
//                                    String result = resultMap.get("result");
//                                    if (TextUtils.equals(resultStatus, "9000")) {
//                                        Timber.d("================result" + result);
//                                        //截取#之前的字符串
//                                        int position = result.indexOf("auth_code=");
//                                        String auth_code = result.substring(position + 10, result.indexOf("&", position));
//
//                                        /**调用支付宝登录*/
//                                        aLiPayLogin(auth_code);
//                                        Timber.d("================authCode" + auth_code);
//                                    }
//
//                                }
//                            }).start();

                            getAlipayCode(info);
                        }
                    });
        }
    }

    private AuthTask authTask;
    private Map<String, String> resultMap;

    public void getAlipayCode(String info) {
        Observable.create((ObservableOnSubscribe<Map<String, String>>) emitter -> {
            authTask = new AuthTask(mAppManager.getTopActivity());
            resultMap = authTask.authV2(info, true);
            emitter.onNext(resultMap);
            emitter.onComplete();
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxLifecycleUtils.bindUntilEvent(mRootView, ActivityEvent.DESTROY))
                .subscribe(new ErrorHandleSubscriber<Map<String, String>>(mErrorHandler) {
                    @Override
                    public void onNext(Map<String, String> stringStringMap) {
                        String resultStatus = stringStringMap.get("resultStatus");
//                        String result = stringStringMap.get("result");
                        if (TextUtils.equals(resultStatus, "9000")) {
                            mRootView.showMessage(mApplication.getResources().getString(R.string.toast_authorize));
                            mRootView.updateWeChatPayBindInfo("", "");
//                            Timber.d("================result" + result);
//                            //截取#之前的字符串
//                            int position = result.indexOf("auth_code=");
//                            String auth_code = result.substring(position + 10, result.indexOf("&", position));
//
//                            /*调用支付宝登录*/
//                            Timber.d("================authCode" + auth_code);
//                            aLiPayLogin(auth_code);

                        }
                    }
                });

    }

    /**
     * 支付宝登录
     */
    public void aLiPayLogin(String code) {
        if (TextUtils.isEmpty(getToken())) {
            mRootView.launchActivity(new Intent(mAppManager.getTopActivity(), LoginActivity.class));
        } else {
            RequestBody requestBody = RequestParamUtils.buildWXLogin(mApplication, code);
            mModel.aLiPayBind(getToken(), requestBody)
                    .compose(RxUtils.applySchedulers(mRootView))
                    .compose(RxUtils.handleBaseResult(mApplication))
                    .subscribe(new ErrorHandleSubscriber<String>(mErrorHandler) {
                        @Override
                        public void onNext(@NonNull String string) {
                            mRootView.showMessage(mApplication.getResources().getString(R.string.toast_authorize));
//                            UserInfoBean userInfo = DataHelper.getDeviceData(mApplication, Constant.SP_KEY_USER_INFO);
//                            userInfo.setAlipayBindOpenid("已授权");
//                            DataHelper.saveDeviceData(mApplication, Constant.SP_KEY_USER_INFO, userInfo);
//                            mRootView.updateWeChatPayBindInfo("", "");
                        }
                    });
        }
    }

    @Override
    public void onDestroy() {
        if (resultMap != null)
            resultMap.clear();
        authTask = null;
        super.onDestroy();
        this.mErrorHandler = null;
        this.mAppManager = null;
        this.mImageLoader = null;
        this.mApplication = null;
    }
}
