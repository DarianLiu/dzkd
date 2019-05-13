package com.dzkandian.mvp.common.presenter;

import android.app.Application;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.dzkandian.app.http.utils.DownLoadManager;
import com.dzkandian.app.http.utils.RequestParamUtils;
import com.dzkandian.app.http.utils.RxUtils;
import com.dzkandian.common.uitls.Constant;
import com.dzkandian.mvp.common.contract.WebViewContract;
import com.dzkandian.storage.bean.WeChatShareBean;
import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.integration.AppManager;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.utils.DataHelper;
import com.jess.arms.utils.PermissionUtil;
import com.jess.arms.utils.RxLifecycleUtils;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import me.jessyan.rxerrorhandler.core.RxErrorHandler;
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber;
import okhttp3.RequestBody;


@ActivityScope
public class WebViewPresenter extends BasePresenter<WebViewContract.Model, WebViewContract.View> {
    @Inject
    RxErrorHandler mErrorHandler;
    @Inject
    Application mApplication;
    @Inject
    ImageLoader mImageLoader;
    @Inject
    AppManager mAppManager;

    @Inject
    public WebViewPresenter(@NonNull WebViewContract.Model model, @NonNull WebViewContract.View rootView) {
        super(model, rootView);
    }

    private String getToken() {
        return DataHelper.getStringSF(mApplication, Constant.SP_KEY_TOKEN);
    }

    /**
     * 获取红包列表所有数据给前端
     */
    public void invitationRedPacket(String userId) {
        if (!TextUtils.isEmpty(getToken())) {
            RequestBody requestBody = RequestParamUtils.invitationRedPacket(mApplication, userId);
            mModel.invitationRedPacket(getToken(), requestBody).compose(RxUtils.applySchedulers(mRootView))
                    .compose(RxUtils.handleBaseResult(mApplication))
                    .subscribe(new ErrorHandleSubscriber<String>(mErrorHandler) {
                        @Override
                        public void onNext(String data) {
                            mRootView.redPacketList(data);
                        }
                    });
        }
    }

    /**
     * 发消息给徒弟，提醒徒弟提现或者赚金币
     */
    public void messageToApprentice(String userId, String apprenticeId, String msgType) {
        if (!TextUtils.isEmpty(getToken())) {
            RequestBody requestBody = RequestParamUtils.messageToApprentice(mApplication, userId, apprenticeId, msgType);
            mModel.messageToApprentice(getToken(), requestBody).compose(RxUtils.applySchedulers(mRootView))
                    .compose(RxUtils.handleBaseResult(mApplication))
                    .subscribe(new ErrorHandleSubscriber<String>(mErrorHandler) {
                        @Override
                        public void onNext(String apprenticeMessageBean) {
                            mRootView.messageToApprentice(apprenticeMessageBean);
                        }

                        @Override
                        public void onError(Throwable t) {
                            super.onError(t);
                            mRootView.messageToError();
                        }
                    });
        }
    }


    /**
     * 拆红包
     */
    public void openRedPacket(String userId, String redPacketId) {
        if (!TextUtils.isEmpty(getToken())) {
            RequestBody requestBody = RequestParamUtils.openRedPacket(mApplication, userId, redPacketId);
            mModel.openRedPacket(getToken(), requestBody).compose(RxUtils.applySchedulers(mRootView))
                    .compose(RxUtils.handleBaseResult(mApplication))
                    .subscribe(new ErrorHandleSubscriber<String>(mErrorHandler) {
                        @Override
                        public void onNext(String redPacketBean) {
                            mRootView.openRedPacket(redPacketBean);
                        }
                    });
        }
    }


    /**
     * 获取收徒分享的必需数据
     */
    public void inviteShare() {
        String token = DataHelper.getStringSF(mApplication, Constant.SP_KEY_TOKEN);

        mModel.inviteShare(token, RequestParamUtils.buildRequestBody(mApplication, new HashMap<>()))
                .compose(RxUtils.applySchedulers(mRootView))
                .compose(RxUtils.handleBaseResult(mApplication))
                .subscribe(new ErrorHandleSubscriber<WeChatShareBean>(mErrorHandler) {
                    @Override
                    public void onNext(WeChatShareBean weChatShareBean) {
                        mRootView.updateShareData(weChatShareBean);
                    }
                });

    }


    /**
     * 关注公众号列表接口
     */
    public void isFoucs(String userId) {
        if (!TextUtils.isEmpty(getToken())) {
            RequestBody requestBody = RequestParamUtils.isFoucs(mApplication, userId);
            mModel.isFoucs(getToken(), requestBody).compose(RxUtils.applySchedulers(mRootView))
                    .compose(RxUtils.handleBaseResult(mApplication))
                    .subscribe(new ErrorHandleSubscriber<String>(mErrorHandler) {
                        @Override
                        public void onNext(String redPacketBean) {
                            mRootView.isfoucs(redPacketBean);
//                            Timber.d("===========关注公众号列表接口成功" + redPacketBean.toString());
                        }
                    });
        }
    }


    /**
     * 关注公众号列表接口
     */
    public void binding(String userId, String code) {
        if (!TextUtils.isEmpty(getToken())) {
            RequestBody requestBody = RequestParamUtils.binding(mApplication, userId, code);
            mModel.binding(getToken(), requestBody).compose(RxUtils.applySchedulers(mRootView))
                    .compose(RxUtils.handleBaseResult(mApplication))
                    .subscribe(new ErrorHandleSubscriber<String>(mErrorHandler) {
                        @Override
                        public void onNext(String redPacketBean) {
                            mRootView.binding(redPacketBean);
//                            Timber.d("===========关注公众号绑定关系接口" + redPacketBean.toString());
                        }
                    });
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


    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mErrorHandler = null;
        this.mAppManager = null;
        this.mImageLoader = null;
        this.mApplication = null;
    }
}
