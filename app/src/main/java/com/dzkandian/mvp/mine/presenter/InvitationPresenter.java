package com.dzkandian.mvp.mine.presenter;

import android.app.Application;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.dzkandian.app.http.utils.DownLoadManager;
import com.dzkandian.app.http.utils.RequestParamUtils;
import com.dzkandian.app.http.utils.RxUtils;
import com.dzkandian.common.uitls.Constant;
import com.dzkandian.mvp.mine.contract.InvitationContract;
import com.dzkandian.storage.bean.WeChatShareBean;
import com.dzkandian.storage.bean.mine.BannerBean;
import com.dzkandian.storage.bean.mine.InvitePageBean;
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


@ActivityScope
public class InvitationPresenter extends BasePresenter<InvitationContract.Model, InvitationContract.View> {
    @Inject
    RxErrorHandler mErrorHandler;
    @Inject
    Application mApplication;
    @Inject
    ImageLoader mImageLoader;
    @Inject
    AppManager mAppManager;

    @Inject
    public InvitationPresenter(@NonNull InvitationContract.Model model, @NonNull InvitationContract.View rootView) {
        super(model, rootView);
    }

    /**
     * 获取收徒页面展示的必需数据
     */
    public void invitePageData() {
        String token = DataHelper.getStringSF(mApplication, Constant.SP_KEY_TOKEN);

        mModel.invitePageData(token, RequestParamUtils.buildRequestBody(mApplication, new HashMap<>()))
                .compose(RxUtils.applySchedulers(mRootView))
                .compose(RxUtils.handleBaseResult(mApplication))
                .subscribe(new ErrorHandleSubscriber<InvitePageBean>(mErrorHandler) {
                    @Override
                    public void onNext(InvitePageBean invitePageBean) {
                        mRootView.updateView(invitePageBean);
                    }
                    @Override
                    public void onError(Throwable t) {
                        super.onError(t);
                        mRootView.setErrorLayout();
                    }
                });

    }

    /**
     * 获取轮播图图片
     */
    public void getBannerImgs() {
        String token = DataHelper.getStringSF(mApplication, Constant.SP_KEY_TOKEN);

        mModel.banner(token, RequestParamUtils.banner(mApplication, "invitePage"))
                .compose(RxUtils.applySchedulers(mRootView))
                .compose(RxUtils.handleBaseResult(mApplication))
                .subscribe(new ErrorHandleSubscriber<List<BannerBean>>(mErrorHandler) {
                    @Override
                    public void onNext(List<BannerBean> imgsBean) {
                        mRootView.banner(imgsBean);
                    }
                });
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
     * 请求权限
     *
     * @param shareImageUrl 分享图片地址
     */
    public void requestPermission(String shareImageUrl) {
        PermissionUtil.externalStorage(new PermissionUtil.RequestPermission() {
            @Override
            public void onRequestPermissionSuccess() {
//                Toast.makeText(getApplication(), "权限申请成功", Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(mApplication, UpdateService.class);
//                intent.putExtra("apk", apkUrl);
//                mAppManager.getTopActivity().startService(intent);
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
//        ProgressManager.getInstance().addResponseListener(shareImageUrl, new ProgressListener() {
//            @Override
//            public void onProgress(@NonNull ProgressInfo progressInfo) {
//                Timber.d("=======下载进度" + progressInfo.getPercent());
//            }
//
//            @Override
//            public void onError(long id, Exception e) {
//
//            }
//        });
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
                .subscribe(filePath -> mRootView.downloadCallBack(filePath));
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
