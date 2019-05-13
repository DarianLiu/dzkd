package com.dzkandian.wxapi.presenter;

import android.app.Application;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.jess.arms.integration.AppManager;
import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.http.imageloader.ImageLoader;

import me.jessyan.rxerrorhandler.core.RxErrorHandler;

import javax.inject.Inject;

import com.dzkandian.wxapi.contract.WXEntryContract;


@ActivityScope
public class WXEntryPresenter extends BasePresenter<WXEntryContract.Model, WXEntryContract.View> {
    @Nullable
    @Inject
    RxErrorHandler mErrorHandler;
    @Nullable
    @Inject
    Application mApplication;
    @Nullable
    @Inject
    ImageLoader mImageLoader;
    @Nullable
    @Inject
    AppManager mAppManager;

    @Inject
    public WXEntryPresenter(@NonNull WXEntryContract.Model model, @NonNull WXEntryContract.View rootView) {
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
}
