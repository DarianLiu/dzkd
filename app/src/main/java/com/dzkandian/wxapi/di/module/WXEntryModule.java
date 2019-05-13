package com.dzkandian.wxapi.di.module;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Module;
import dagger.Provides;

import com.dzkandian.wxapi.contract.WXEntryContract;
import com.dzkandian.wxapi.model.WXEntryModel;


@Module
public class WXEntryModule {
    private WXEntryContract.View view;

    /**
     * 构建WXEntryModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public WXEntryModule(WXEntryContract.View view) {
        this.view = view;
    }

    @ActivityScope
    @Provides
    WXEntryContract.View provideWXEntryView() {
        return this.view;
    }

    @ActivityScope
    @Provides
    WXEntryContract.Model provideWXEntryModel(WXEntryModel model) {
        return model;
    }
}