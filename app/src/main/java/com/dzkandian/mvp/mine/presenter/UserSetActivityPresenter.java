package com.dzkandian.mvp.mine.presenter;

import android.app.Application;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.dzkandian.app.MyApplication;
import com.dzkandian.app.config.EventBusTags;
import com.dzkandian.app.http.utils.RequestParamUtils;
import com.dzkandian.app.http.utils.RxUtils;
import com.dzkandian.common.uitls.Constant;
import com.dzkandian.db.UserInfoBeanDao;
import com.dzkandian.mvp.common.ui.activity.LoginActivity;
import com.dzkandian.mvp.mine.contract.UserSetActivityContract;
import com.dzkandian.storage.bean.UserInfoBean;
import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.integration.AppManager;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.utils.DataHelper;

import org.simple.eventbus.EventBus;

import java.io.File;
import java.util.List;

import javax.inject.Inject;

import me.jessyan.rxerrorhandler.core.RxErrorHandler;
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber;
import okhttp3.RequestBody;

/**
 * 账号设置
 */
@ActivityScope
public class UserSetActivityPresenter extends BasePresenter<UserSetActivityContract.Model, UserSetActivityContract.View> {
    @Inject
    RxErrorHandler mErrorHandler;
    @Inject
    Application mApplication;
    @Inject
    ImageLoader mImageLoader;
    @Inject
    AppManager mAppManager;

    @Inject
    public UserSetActivityPresenter(@NonNull UserSetActivityContract.Model model, @NonNull UserSetActivityContract.View rootView) {
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

    private String getToken() {
        return DataHelper.getStringSF(mApplication, Constant.SP_KEY_TOKEN);
    }

    /**
     * 上传头像
     */
    public void uploadAvatar(File file) {
        if (!TextUtils.isEmpty(getToken())) {
            RequestBody body = RequestParamUtils.buildUploadAvatar(mApplication, file);
            mModel.uploadAvatar(getToken(), body)
                    .compose(RxUtils.applySchedulers(mRootView))
                    .compose(RxUtils.handleBaseResult(mApplication))
                    .subscribe(new ErrorHandleSubscriber<String>(mErrorHandler) {
                        @Override
                        public void onNext(String data) {
                            UserInfoBeanDao userInfoDao = ((MyApplication) mApplication).getDaoSession().getUserInfoBeanDao();
                            List<UserInfoBean> list = userInfoDao.loadAll();
                            if (list != null && list.size() > 0) {
//                                Timber.d("=db=    UserSetActivityPresenter - UserInfo uploadAvatar - query 成功");
                                UserInfoBean userInfoBean = (list.get(0));
                                userInfoBean.setAvatar(data);
                                userInfoDao.update(userInfoBean);
                                if (!TextUtils.isEmpty(Constant.FIEL_SAVE_PATH + Constant.USER_HEADER_IMAGE_NAME)
                                        && new File(Constant.FIEL_SAVE_PATH + Constant.USER_HEADER_IMAGE_NAME).exists()) {
                                    new File(Constant.FIEL_SAVE_PATH + Constant.USER_HEADER_IMAGE_NAME).delete();
                                }
                                EventBus.getDefault().post(userInfoBean, EventBusTags.TAG_UPDATE_USER_INFO);
                            }
//                            else {
//                                Timber.d("=db=    UserSetActivityPresenter - UserInfo uploadAvatar - query 失败");
//                            }
                            mRootView.cachePicture();//头像上传成功清除缓存
                        }
                    });
        }
    }

    /**
     * 修改信息
     * gender(性别)
     * birthday(生日)
     * avatar(头像)
     */
    public void uploadInfo(String key, String value, String key2, String value2) {
        String token = DataHelper.getStringSF(mApplication, Constant.SP_KEY_TOKEN);
        if (TextUtils.isEmpty(token)) {
            mRootView.launchActivity(new Intent(mAppManager.getTopActivity(), LoginActivity.class));
        } else {
            RequestBody requestBody = RequestParamUtils.buildUpdateInfo(mApplication, key, value, key2, value2);
            mModel.uploadInfo(token, requestBody)
                    .compose(RxUtils.applySchedulers(mRootView))
                    .compose(RxUtils.handleBaseResult(mApplication))
                    .subscribe(new ErrorHandleSubscriber<UserInfoBean>(mErrorHandler) {
                        @Override
                        public void onNext(UserInfoBean muserInfoBean) {
                            UserInfoBeanDao userInfoDao = ((MyApplication) mApplication).getDaoSession().getUserInfoBeanDao();
                            List<UserInfoBean> list = userInfoDao.loadAll();
                            if (list != null && list.size() > 0) {
//                                Timber.d("=db=    UserSetActivityPresenter - UserInfo uploadInfo - query 成功");
                                UserInfoBean userInfoBean = (list.get(0));
                                if (TextUtils.equals(key, "gender")) {
                                    userInfoBean.setGender(Long.parseLong(value));
                                } else if (TextUtils.equals(key, "birthday")) {
                                    userInfoBean.setBirthday(value);
                                }
                                userInfoDao.update(userInfoBean);
                                EventBus.getDefault().post(userInfoBean, EventBusTags.TAG_UPDATE_USER_INFO);
                            }
//                            else {
//                                Timber.d("=db=    UserSetActivityPresenter - UserInfo uploadInfo - query 失败");
//                            }
                            mRootView.showMessage("修改完成");
                        }
                    });
        }
    }
}
