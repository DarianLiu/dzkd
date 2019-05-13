package com.dzkandian.mvp.mine.presenter;

import android.app.Application;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.dzkandian.R;
import com.dzkandian.app.MyApplication;
import com.dzkandian.app.http.utils.RequestParamUtils;
import com.dzkandian.app.http.utils.RxUtils;
import com.dzkandian.common.uitls.Constant;
import com.dzkandian.db.DeviceInfoBeanDao;
import com.dzkandian.mvp.common.ui.activity.LoginActivity;
import com.dzkandian.mvp.mine.contract.QuickCashContract;
import com.dzkandian.storage.bean.DeviceInfoBean;
import com.dzkandian.storage.bean.mine.CoinExchangeBean;
import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.integration.AppManager;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.utils.DataHelper;

import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import me.jessyan.rxerrorhandler.core.RxErrorHandler;
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber;
import okhttp3.RequestBody;
import timber.log.Timber;


@ActivityScope
public class QuickCashPresenter extends BasePresenter<QuickCashContract.Model, QuickCashContract.View> {
    @Inject
    RxErrorHandler mErrorHandler;
    @Inject
    Application mApplication;
    @Inject
    ImageLoader mImageLoader;
    @Inject
    AppManager mAppManager;

    @Inject
    public QuickCashPresenter(@NonNull QuickCashContract.Model model, @NonNull QuickCashContract.View rootView) {
        super(model, rootView);
    }

    private String getToken() {
        return DataHelper.getStringSF(mApplication, Constant.SP_KEY_TOKEN);
    }

    /**
     * 获取提现数据
     */
    public void getCoinExchange() {
        if (TextUtils.isEmpty(getToken())) {
            mRootView.launchActivity(new Intent(mAppManager.getTopActivity(), LoginActivity.class));
        } else {
            RequestBody requestBody = RequestParamUtils.buildRequestBody(mApplication, new HashMap<>());
            mModel.getCoinExchange(getToken(), requestBody)
                    .compose(RxUtils.applySchedulers(mRootView))
                    .compose(RxUtils.handleBaseResult(mApplication))
                    .subscribe(new ErrorHandleSubscriber<CoinExchangeBean>(mErrorHandler) {
                        @Override
                        public void onNext(CoinExchangeBean userBean) {
                            DeviceInfoBeanDao deviceInfoDao = ((MyApplication) mApplication).getDaoSession().getDeviceInfoBeanDao();
                            List<DeviceInfoBean> list = deviceInfoDao.loadAll();
                            if (list != null && list.size() > 0) {
                                Timber.d("=db=    QuickCashPresenter - DeviceInfo - query 成功");
                                DeviceInfoBean deviceInfoBean = (list.get(0));
                                deviceInfoBean.setWeixinPayAppid(userBean.getWeixinPayAppid());//将新的WeixinPayAppid保存到设备信息数据库
                                deviceInfoDao.update(deviceInfoBean);
                                mRootView.setCoinExchangeBean(userBean);
                            } else {
                                Timber.d("=db=    QuickCashPresenter - DeviceInfo - query 失败");
                            }
                        }

                        @Override
                        public void onError(Throwable t) {
                            super.onError(t);
                            mRootView.setErrorLayout();
                        }
                    });
        }
    }


    /**
     * 发现提现
     */
    public void redeemNow(String rmb, String alipay) {
        if (!TextUtils.isEmpty(getToken())) {
            RequestBody requestBody = RequestParamUtils.buildRedeemNow(mApplication, rmb, alipay);
            mModel.redeemNow(getToken(), requestBody)
                    .compose(RxUtils.applySchedulers(mRootView))
                    .compose(RxUtils.handleBaseResult(mApplication))
                    .subscribe(new ErrorHandleSubscriber<CoinExchangeBean>(mErrorHandler) {
                        @Override
                        public void onNext(CoinExchangeBean userBean) {
                            mRootView.setCurrCoinBean(userBean);
                            mRootView.showMessage(mApplication.getResources().getString(R.string.toast_put_forward));
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
