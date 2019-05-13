package com.dzkandian.mvp.common.di.module;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Module;
import dagger.Provides;

import com.dzkandian.mvp.common.contract.AdWebContract;
import com.dzkandian.mvp.common.model.AdWebModel;


@Module
public class AdWebModule {
    private AdWebContract.View view;

    /**
     * 构建AdWebModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public AdWebModule(AdWebContract.View view) {
        this.view = view;
    }

    @ActivityScope
    @Provides
    AdWebContract.View provideAdWebView() {
        return this.view;
    }

    @ActivityScope
    @Provides
    AdWebContract.Model provideAdWebModel(AdWebModel model) {
        return model;
    }
}