package com.dzkandian.mvp.mine.model;

import android.app.Application;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;

import com.jess.arms.di.scope.ActivityScope;

import javax.inject.Inject;

import com.dzkandian.mvp.mine.contract.AboutContract;


@ActivityScope
public class AboutModel extends BaseModel implements AboutContract.Model {
    @Nullable
    @Inject
    Gson mGson;
    @Nullable
    @Inject
    Application mApplication;

    @Inject
    public AboutModel(IRepositoryManager repositoryManager) {
        super(repositoryManager);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mGson = null;
        this.mApplication = null;
    }
}