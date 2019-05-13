package com.dzkandian.mvp.common.di.module;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Module;
import dagger.Provides;

import com.dzkandian.mvp.common.contract.PlayWebViewContract;
import com.dzkandian.mvp.common.model.PlayWebViewModel;


@Module
public class PlayWebViewModule {
    private PlayWebViewContract.View view;

    /**
     * 构建PlayWebViewActivityModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public PlayWebViewModule(PlayWebViewContract.View view) {
        this.view = view;
    }

    @ActivityScope
    @Provides
    PlayWebViewContract.View providePlayWebViewView() {
        return this.view;
    }

    @ActivityScope
    @Provides
    PlayWebViewContract.Model providePlayWebViewModel(PlayWebViewModel model) {
        return model;
    }
}