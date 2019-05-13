package com.dzkandian.mvp.common.presenter;

import android.app.Application;

import com.jess.arms.integration.AppManager;
import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.http.imageloader.ImageLoader;


import me.jessyan.rxerrorhandler.core.RxErrorHandler;

import javax.inject.Inject;

import com.dzkandian.mvp.common.contract.PlayWebViewContract;


@ActivityScope
public class PlayWebViewPresenter extends BasePresenter<PlayWebViewContract.Model, PlayWebViewContract.View> {
    @Inject
    RxErrorHandler mErrorHandler;
    @Inject
    Application mApplication;
    @Inject
    ImageLoader mImageLoader;
    @Inject
    AppManager mAppManager;

    private boolean isDownload = false; //是否正在下载

    @Inject
    public PlayWebViewPresenter(PlayWebViewContract.Model model, PlayWebViewContract.View rootView) {
        super(model, rootView);
    }


//    /**
//     * 申请外部存储权限
//     */
//    public void requestPermission(String apkUrl) {
//        PermissionUtil.externalStorage(new PermissionUtil.RequestPermission() {
//            @Override
//            public void onRequestPermissionSuccess() {
//                if (!isDownload) {  //false  则
//                    downLoadApk(apkUrl);
//                }
////                mRootView.startProgressDialog();
//            }
//
//            @Override
//            public void onRequestPermissionFailure(List<String> permissions) {
//                mRootView.showMessage("权限获取失败");
//            }
//
//            @Override
//            public void onRequestPermissionFailureWithAskNeverAgain(List<String> permissions) {
//                mRootView.showMessage("权限获取失败，将无法正常使用该应用！");
//            }
//
//        }, new RxPermissions(mAppManager.getTopActivity()), mErrorHandler);
//    }
//
//    private void downLoadApk(String apkUrl) {
//        ProgressManager.getInstance().addResponseListener(apkUrl, new ProgressListener() {
//            @Override
//            public void onProgress(@NonNull ProgressInfo progressInfo) {
//                isDownload = true;
//                Timber.d("=======下载进度" + progressInfo.getPercent());
////                mRootView.showProgressDialog(progressInfo);
//            }
//
//            @Override
//            public void onError(long id, Exception e) {
//
//            }
//        });
//
//        mModel.update(apkUrl).subscribeOn(Schedulers.io())
//                .observeOn(Schedulers.io())
//                .concatMap(responseBody -> {
//                    try {
//                        String file_path = DownLoadManager.writeResponseBodyToDisk(mApplication, "dzkd." + 2, responseBody);
//                        return RxUtils.createData(file_path);
//                    } catch (Exception e) {
//                        return Observable.error(e);
//                    }
//                })
//                .observeOn(AndroidSchedulers.mainThread())
//                .compose(RxLifecycleUtils.bindToLifecycle(mRootView))
//                .subscribeWith(new ErrorHandleSubscriber<String>(mErrorHandler) {
//                    @Override
//                    public void onNext(String s) {
//                        Timber.d("======下载的路径+" + s);
//                        isDownload = false;
//                        mRootView.installApk(s);
//                    }
//
//                    @Override
//                    public void onError(Throwable t) {
//                        super.onError(t);
//                        mRootView.hideProgressDialog();
//                    }
//                });
//    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mErrorHandler = null;
        this.mAppManager = null;
        this.mImageLoader = null;
        this.mApplication = null;
    }
}
