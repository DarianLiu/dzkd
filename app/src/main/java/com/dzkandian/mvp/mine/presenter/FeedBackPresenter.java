package com.dzkandian.mvp.mine.presenter;

import android.app.Application;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.dzkandian.app.exception.ApiException;
import com.dzkandian.app.http.utils.RequestParamUtils;
import com.dzkandian.app.http.utils.RxUtils;
import com.dzkandian.common.uitls.Constant;
import com.dzkandian.mvp.common.ui.activity.LoginActivity;
import com.dzkandian.mvp.mine.contract.FeedBackContract;
import com.dzkandian.storage.bean.UserBean;
import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.integration.AppManager;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.utils.DataHelper;

import javax.inject.Inject;

import me.jessyan.rxerrorhandler.core.RxErrorHandler;
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber;
import okhttp3.RequestBody;


@ActivityScope
public class FeedBackPresenter extends BasePresenter<FeedBackContract.Model, FeedBackContract.View> {
    @Inject
    RxErrorHandler mErrorHandler;
    @Inject
    Application mApplication;
    @Inject
    ImageLoader mImageLoader;
    @Inject
    AppManager mAppManager;

    @Inject
    public FeedBackPresenter(@NonNull FeedBackContract.Model model, @NonNull FeedBackContract.View rootView) {
        super(model, rootView);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mErrorHandler = null;
        this.mAppManager = null;
        this.mImageLoader = null;
        this.mApplication = null;
    }

    public void feedBack(String value1, String value2,  String value3){
        String token = DataHelper.getStringSF(mApplication, Constant.SP_KEY_TOKEN);
        if (TextUtils.isEmpty(token)) {
            mRootView.launchActivity(new Intent(mAppManager.getTopActivity(), LoginActivity.class));
        } else {
            RequestBody requestBody = RequestParamUtils.feedBack(mApplication, value1, value2, value3);
            mModel.feedBack(token,requestBody)
                    .compose(RxUtils.applySchedulers(mRootView))
                    .compose(RxUtils.handleBaseResult(mApplication))
                    .subscribe(new ErrorHandleSubscriber<UserBean>(mErrorHandler){
                        @Override
                        public void onNext(UserBean userBean) {
                            mRootView.showMessage("反馈成功，感谢你的宝贵建议");
                            mRootView.killMyself();
                        }

                        @Override
                        public void onError(Throwable t) {
                            if (t instanceof ApiException) {
                                if (((ApiException) t).getCode() == 412) {
                                    mRootView.showMessage("发送失败，请修改输入的违规字符后再试");
                                }else {
                                    super.onError(t);
                                }
                            }else {
                                super.onError(t);
                            }
                        }
                    });
        }
    }
}
