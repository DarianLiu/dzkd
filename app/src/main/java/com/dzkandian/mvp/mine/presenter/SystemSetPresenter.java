package com.dzkandian.mvp.mine.presenter;

import android.app.Application;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.dzkandian.app.http.utils.RequestParamUtils;
import com.dzkandian.app.http.utils.RxUtils;
import com.dzkandian.common.uitls.Constant;
import com.dzkandian.common.uitls.update.UpdateAppBean;
import com.dzkandian.common.uitls.update.UpdateAppHttpUtil;
import com.dzkandian.mvp.mine.contract.SystemSetContract;
import com.dzkandian.storage.bean.VersionBean;
import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.integration.AppManager;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.utils.DataHelper;
import com.jess.arms.utils.DeviceUtils;
import com.jess.arms.utils.PermissionUtil;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;
import java.util.List;

import javax.inject.Inject;

import me.jessyan.rxerrorhandler.core.RxErrorHandler;
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber;
import okhttp3.RequestBody;


@ActivityScope
public class SystemSetPresenter extends BasePresenter<SystemSetContract.Model, SystemSetContract.View> {
    @Inject
    RxErrorHandler mErrorHandler;
    @Inject
    Application mApplication;
    @Inject
    ImageLoader mImageLoader;
    @Inject
    AppManager mAppManager;

    @Inject
    public SystemSetPresenter(@NonNull SystemSetContract.Model model, @NonNull SystemSetContract.View rootView) {
        super(model, rootView);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    /**
     * 检查更新
     *
     * @param channel 手机相关信息
     */
    public void checkUpdate(String channel) {
        RequestBody requestBody = RequestParamUtils.buildCheckUpdate(mApplication, channel);
        String token = DataHelper.getStringSF(mApplication, Constant.SP_KEY_TOKEN);
        mModel.checkUpdate(TextUtils.isEmpty(token) ? "" : token, requestBody).compose(RxUtils.applySchedulers(mRootView))
                .compose(RxUtils.handleBaseResult(mApplication))
                .subscribe(new ErrorHandleSubscriber<VersionBean>(mErrorHandler) {
                    @Override
                    public void onNext(@NonNull VersionBean versionBean) {
                        if (DeviceUtils.getVersionCode(mApplication) < versionBean.getVersionCode()) {
                            //当前版本号小于服务器上APK上的版本号，表示需要更新
                            DataHelper.setStringSF(mApplication, Constant.SP_KEY_UPDATE_APP, "ture");
                            requestPermissionUpdate(versionBean);
                        } else {
                            DataHelper.setStringSF(mApplication, Constant.SP_KEY_UPDATE_APP, "false");
                            mRootView.showMessage("恭喜，当前已是最新版本");
                        }
                    }
                });
    }

    /**
     * 更新版本：申请外部存储权限
     */
    public void requestPermissionUpdate(VersionBean versionBean) {
        PermissionUtil.externalStorage(new PermissionUtil.RequestPermission() {
            @Override
            public void onRequestPermissionSuccess() {
                updateAppDialog(versionBean);
            }

            @Override
            public void onRequestPermissionFailure(List<String> permissions) {
                mRootView.showMessage("授权失败，请允许存储权限后再尝试更新");
                updateAppDialog(versionBean);
            }

            @Override
            public void onRequestPermissionFailureWithAskNeverAgain(List<String> permissions) {
                mRootView.showMessage("授权失败，请到设置中允许存储权限后再尝试更新");
                updateAppDialog(versionBean);
            }

        }, new RxPermissions(mAppManager.getTopActivity()), mErrorHandler);
    }

    /**
     * @param versionBean
     */
    public void updateAppDialog(VersionBean versionBean) {
        String path = Environment.getExternalStorageDirectory() + File.separator;
        UpdateAppBean updateAppBean = new UpdateAppBean();
        updateAppBean
                .setUpdate("Yes")//是否更新，Yes,No
                .setNewVersion(versionBean.getVersion())//新版本号，
                .setApkFileUrl(versionBean.getApkUrl())//apk下载地址
                .setUpdateDefDialogTitle("发现新版本")//更新头部
                .setUpdateLog(versionBean.getDescribe())//更新内容
                .setConstraint(versionBean.getForce() == 1)//是否强制更新，可以不设置
                .setTargetPath(path);//设置apk下载路径
        updateAppBean.setHttpManager(new UpdateAppHttpUtil());
        mRootView.showUpdateDialog(updateAppBean);
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
